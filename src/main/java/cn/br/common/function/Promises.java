package cn.br.common.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 并发 异步
 * 
 <pre>
 Promise p = Promise.waitAll(p1, p2, p3) 
 Promise p = Promise.waitAny(p1, p2, p3) 
 Promise p = Promise.waitEither(p1, p2, p3)
 </pre>
 * @author ZJL
 *
 */
public class Promises {
	/**
	 * A Function with no arguments.
	 */
	public static interface Function0<R> {
		public R apply() throws Throwable;
	}

	public static class Promise<V> implements Future<V>, Action<V> {

		protected final CountDownLatch taskLock = new CountDownLatch(1);

		protected boolean cancelled = false;

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return invoked;
		}

		public V getOrNull() {
			return result;
		}

		@Override
		public V get() throws InterruptedException, ExecutionException {
			taskLock.await();
			if (exception != null) {
				// The result of the promise is an exception - throw it
				throw new ExecutionException(exception);
			}
			return result;
		}

		@Override
		public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			if (!taskLock.await(timeout, unit)) {
				throw new TimeoutException(String.format("Promise didn't redeem in %s %s", timeout, unit));
			}

			if (exception != null) {
				// The result of the promise is an exception - throw it
				throw new ExecutionException(exception);
			}
			return result;
		}

		protected List<Action<Promise<V>>> callbacks = new ArrayList<Action<Promise<V>>>();
		protected boolean invoked = false;
		protected V result = null;
		protected Throwable exception = null;

		@Override
		public void invoke(V result) {
			invokeWithResultOrException(result, null);
		}

		public void invokeWithException(Throwable t) {
			invokeWithResultOrException(null, t);
		}

		protected void invokeWithResultOrException(V result, Throwable t) {
			synchronized (this) {
				if (!invoked) {
					invoked = true;
					this.result = result;
					this.exception = t;
					taskLock.countDown();
				} else {
					return;
				}
			}
			for (Action<Promise<V>> callback : callbacks) {
				callback.invoke(this);
			}
		}

		/**
		 * 
		 * @param callback
		 */
		public void onRedeem(Action<Promise<V>> callback) {
			synchronized (this) {
				if (!invoked) {
					callbacks.add(callback);
				}
			}
			if (invoked) {
				callback.invoke(this);
			}
		}

		/**
		 * den
		 * @param <T>
		 * @param promises
		 * @return
		 */
		public static <T> Promise<List<T>> waitAll(final Promise<T>... promises) {
			return waitAll(Arrays.asList(promises));
		}

		public static <T> Promise<List<T>> waitAll(final Collection<Promise<T>> promises) {
			final CountDownLatch waitAllLock = new CountDownLatch(promises.size());
			final Promise<List<T>> result = new Promise<List<T>>() {

				@Override
				public boolean cancel(boolean mayInterruptIfRunning) {
					boolean r = true;
					for (Promise<T> f : promises) {
						r = r & f.cancel(mayInterruptIfRunning);
					}
					return r;
				}

				@Override
				public boolean isCancelled() {
					boolean r = true;
					for (Promise<T> f : promises) {
						r = r & f.isCancelled();
					}
					return r;
				}

				@Override
				public boolean isDone() {
					boolean r = true;
					for (Promise<T> f : promises) {
						r = r & f.isDone();
					}
					return r;
				}

				@Override
				public List<T> get() throws InterruptedException, ExecutionException {
					waitAllLock.await();
					List<T> r = new ArrayList<T>();
					for (Promise<T> f : promises) {
						r.add(f.get());
					}
					return r;
				}

				@Override
				public List<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
					if (!waitAllLock.await(timeout, unit)) {
						throw new TimeoutException(String.format("Promises didn't redeem in %s %s", timeout, unit));
					}

					return get();
				}
			};
			final Action<Promise<T>> action = new Action<Promise<T>>() {

				@Override
				public void invoke(Promise<T> completed) {
					waitAllLock.countDown();
					if (waitAllLock.getCount() == 0) {
						try {
							result.invoke(result.get());
						} catch (Exception e) {
							result.invokeWithException(e);
						}
					}
				}
			};
			for (Promise<T> f : promises) {
				f.onRedeem(action);
			}
			if (promises.isEmpty()) {
				result.invoke(Collections.<T> emptyList());
			}
			return result;
		}

		public static <A, B> Promise<Tuples.Tuple<A, B>> wait2(Promise<A> tA, Promise<B> tB) {
			final Promise<Tuples.Tuple<A, B>> result = new Promise<Tuples.Tuple<A, B>>();
			final Promise<List<Object>> t = waitAll(new Promise[] { tA, tB });
			t.onRedeem(new Action<Promise<List<Object>>>() {

				@Override
				public void invoke(Promise<List<Object>> completed) {
					List<Object> values = completed.getOrNull();
					if (values != null) {
						result.invoke(new Tuples.Tuple((A) values.get(0), (B) values.get(1)));
					} else {
						result.invokeWithException(completed.exception);
					}
				}
			});
			return result;
		}

		public static <A, B, C> Promise<Tuples.T3<A, B, C>> wait3(Promise<A> tA, Promise<B> tB, Promise<C> tC) {
			final Promise<Tuples.T3<A, B, C>> result = new Promise<Tuples.T3<A, B, C>>();
			final Promise<List<Object>> t = waitAll(new Promise[] { tA, tB, tC });
			t.onRedeem(new Action<Promise<List<Object>>>() {

				@Override
				public void invoke(Promise<List<Object>> completed) {
					List<Object> values = completed.getOrNull();
					if (values != null) {
						result.invoke(new Tuples.T3((A) values.get(0), (B) values.get(1), (C) values.get(2)));
					} else {
						result.invokeWithException(completed.exception);
					}
				}
			});
			return result;
		}

		public static <A, B, C, D> Promise<Tuples.T4<A, B, C, D>> wait4(Promise<A> tA, Promise<B> tB, Promise<C> tC, Promise<D> tD) {
			final Promise<Tuples.T4<A, B, C, D>> result = new Promise<Tuples.T4<A, B, C, D>>();
			final Promise<List<Object>> t = waitAll(new Promise[] { tA, tB, tC, tD });
			t.onRedeem(new Action<Promise<List<Object>>>() {

				@Override
				public void invoke(Promise<List<Object>> completed) {
					List<Object> values = completed.getOrNull();
					if (values != null) {
						result.invoke(new Tuples.T4((A) values.get(0), (B) values.get(1), (C) values.get(2), (D) values.get(3)));
					} else {
						result.invokeWithException(completed.exception);
					}
				}
			});
			return result;
		}

		public static <A, B, C, D, E> Promise<Tuples.T5<A, B, C, D, E>> wait5(Promise<A> tA, Promise<B> tB, Promise<C> tC, Promise<D> tD, Promise<E> tE) {
			final Promise<Tuples.T5<A, B, C, D, E>> result = new Promise<Tuples.T5<A, B, C, D, E>>();
			final Promise<List<Object>> t = waitAll(new Promise[] { tA, tB, tC, tD, tE });
			t.onRedeem(new Action<Promise<List<Object>>>() {

				@Override
				public void invoke(Promise<List<Object>> completed) {
					List<Object> values = completed.getOrNull();
					if (values != null) {
						result.invoke(new Tuples.T5((A) values.get(0), (B) values.get(1), (C) values.get(2), (D) values.get(3), (E) values.get(4)));
					} else {
						result.invokeWithException(completed.exception);
					}
				}
			});
			return result;
		}

		private static Promise<Tuples.Tuple<Integer, Promise<Object>>> waitEitherInternal(final Promise<?>... futures) {
			final Promise<Tuples.Tuple<Integer, Promise<Object>>> result = new Promise<Tuples.Tuple<Integer, Promise<Object>>>();
			for (int i = 0; i < futures.length; i++) {
				final int index = i + 1;
				((Promise<Object>) futures[i]).onRedeem(new Action<Promise<Object>>() {

					@Override
					public void invoke(Promise<Object> completed) {
						result.invoke(new Tuples.Tuple(index, completed));
					}
				});
			}
			return result;
		}

		public static <A, B> Promise<Eithers.Either<A, B>> waitEither(final Promise<A> tA, final Promise<B> tB) {
			final Promise<Eithers.Either<A, B>> result = new Promise<Eithers.Either<A, B>>();
			final Promise<Tuples.Tuple<Integer, Promise<Object>>> t = waitEitherInternal(tA, tB);

			t.onRedeem(new Action<Promise<Tuples.Tuple<Integer, Promise<Object>>>>() {

				@Override
				public void invoke(Promise<Tuples.Tuple<Integer, Promise<Object>>> completed) {
					Tuples.Tuple<Integer, Promise<Object>> value = completed.getOrNull();
					switch (value._1) {
					case 1:
						result.invoke(Eithers.Either.<A, B> _1((A) value._2.getOrNull()));
						break;
					case 2:
						result.invoke(Eithers.Either.<A, B> _2((B) value._2.getOrNull()));
						break;
					}

				}
			});

			return result;
		}

		public static <A, B, C> Promise<Eithers.E3<A, B, C>> waitEither(final Promise<A> tA, final Promise<B> tB, final Promise<C> tC) {
			final Promise<Eithers.E3<A, B, C>> result = new Promise<Eithers.E3<A, B, C>>();
			final Promise<Tuples.Tuple<Integer, Promise<Object>>> t = waitEitherInternal(tA, tB, tC);

			t.onRedeem(new Action<Promise<Tuples.Tuple<Integer, Promise<Object>>>>() {

				@Override
				public void invoke(Promise<Tuples.Tuple<Integer, Promise<Object>>> completed) {
					Tuples.Tuple<Integer, Promise<Object>> value = completed.getOrNull();
					switch (value._1) {
					case 1:
						result.invoke(Eithers.E3.<A, B, C> _1((A) value._2.getOrNull()));
						break;
					case 2:
						result.invoke(Eithers.E3.<A, B, C> _2((B) value._2.getOrNull()));
						break;
					case 3:
						result.invoke(Eithers.E3.<A, B, C> _3((C) value._2.getOrNull()));
						break;
					}

				}
			});

			return result;
		}

		public static <A, B, C, D> Promise<Eithers.E4<A, B, C, D>> waitEither(final Promise<A> tA, final Promise<B> tB, final Promise<C> tC, final Promise<D> tD) {
			final Promise<Eithers.E4<A, B, C, D>> result = new Promise<Eithers.E4<A, B, C, D>>();
			final Promise<Tuples.Tuple<Integer, Promise<Object>>> t = waitEitherInternal(tA, tB, tC, tD);

			t.onRedeem(new Action<Promise<Tuples.Tuple<Integer, Promise<Object>>>>() {

				@Override
				public void invoke(Promise<Tuples.Tuple<Integer, Promise<Object>>> completed) {
					Tuples.Tuple<Integer, Promise<Object>> value = completed.getOrNull();
					switch (value._1) {
					case 1:
						result.invoke(Eithers.E4.<A, B, C, D> _1((A) value._2.getOrNull()));
						break;
					case 2:
						result.invoke(Eithers.E4.<A, B, C, D> _2((B) value._2.getOrNull()));
						break;
					case 3:
						result.invoke(Eithers.E4.<A, B, C, D> _3((C) value._2.getOrNull()));
						break;
					case 4:
						result.invoke(Eithers.E4.<A, B, C, D> _4((D) value._2.getOrNull()));
						break;
					}

				}
			});

			return result;
		}

		public static <A, B, C, D, E> Promise<Eithers.E5<A, B, C, D, E>> waitEither(final Promise<A> tA, final Promise<B> tB, final Promise<C> tC, final Promise<D> tD,
				final Promise<E> tE) {
			final Promise<Eithers.E5<A, B, C, D, E>> result = new Promise<Eithers.E5<A, B, C, D, E>>();
			final Promise<Tuples.Tuple<Integer, Promise<Object>>> t = waitEitherInternal(tA, tB, tC, tD, tE);

			t.onRedeem(new Action<Promise<Tuples.Tuple<Integer, Promise<Object>>>>() {

				@Override
				public void invoke(Promise<Tuples.Tuple<Integer, Promise<Object>>> completed) {
					Tuples.Tuple<Integer, Promise<Object>> value = completed.getOrNull();
					switch (value._1) {
					case 1:
						result.invoke(Eithers.E5.<A, B, C, D, E> _1((A) value._2.getOrNull()));
						break;
					case 2:
						result.invoke(Eithers.E5.<A, B, C, D, E> _2((B) value._2.getOrNull()));
						break;
					case 3:
						result.invoke(Eithers.E5.<A, B, C, D, E> _3((C) value._2.getOrNull()));
						break;
					case 4:
						result.invoke(Eithers.E5.<A, B, C, D, E> _4((D) value._2.getOrNull()));
						break;
					case 5:
						result.invoke(Eithers.E5.<A, B, C, D, E> _5((E) value._2.getOrNull()));
						break;

					}

				}
			});

			return result;
		}

		public static <T> Promise<T> waitAny(final Promise<T>... futures) {
			final Promise<T> result = new Promise<T>();

			/**
			 * 定义执行器
			 */
			final Action<Promise<T>> action = new Action<Promise<T>>() {

				@Override
				public void invoke(Promise<T> completed) {
					synchronized (this) {
						if (result.isDone()) {
							return;
						}
					}
					T resultOrNull = completed.getOrNull();
					if (resultOrNull != null) {
						result.invoke(resultOrNull);
					} else {
						result.invokeWithException(completed.exception);
					}
				}
			};

			/**
			 * 注册执行器
			 */
			for (Promise<T> f : futures) {
				f.onRedeem(action);
			}

			return result;
		}
	}

	/**
	 * 延迟执行
	 * @author ZJL
	 *
	 */
	public static class Timeout extends Promise<Timeout> {

		static Timer timer = new Timer("F.Timeout", true);
		final public String token;
		final public long delay;

		public Timeout(String delay) {
			this(Time.parseDuration(delay) * 1000);
		}

		public Timeout(String token, String delay) {
			this(token, Time.parseDuration(delay) * 1000);
		}

		public Timeout(long delay) {
			this("timeout", delay);
		}

		public Timeout(String token, long delay) {
			this.delay = delay;
			this.token = token;
			final Timeout timeout = this;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					timeout.invoke(timeout);
				}
			}, delay);
		}

		@Override
		public String toString() {
			return "Timeout(" + delay + ")";
		}

	}

	public static Timeout Timeout(String delay) {
		return new Timeout(delay);
	}

	public static Timeout Timeout(String token, String delay) {
		return new Timeout(token, delay);
	}

	public static Timeout Timeout(long delay) {
		return new Timeout(delay);
	}

	public static Timeout Timeout(String token, long delay) {
		return new Timeout(token, delay);
	}

	public static interface Action0 {

		void invoke();
	}

	/**
	 * 执行器
	 * @author ZJL
	 *
	 * @param <T>
	 */
	public static interface Action<T> {

		/**
		 * 执行
		 * @param result
		 */
		void invoke(T result);
	}

	public static void main(String[] args) {
	}

}

package cn.br.common.job;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.br.common.exception.EfException;
import cn.br.common.exception.UnexpectedException;
import cn.br.common.function.Promises;
import cn.br.common.function.Promises.Promise;
import cn.br.common.function.Time;
import lombok.extern.slf4j.Slf4j;

/**
 * A job is an asynchronously executed unit of work
 * @param <V> The job result type (if any)
 */
@Slf4j
public class Job<V> implements Callable<V>, Runnable {

	public static final String invocationType = "Job";

	protected ExecutorService executor;

	/**
	 * 最近一次执行时间
	 */
	protected long lastRun = 0;

	/**
	 * 是否有错误
	 */
	protected boolean wasError = false;

	/**
	 * 错误异常
	 */
	protected Throwable lastException = null;

	/**
	 * 下一次执行时间
	 */
	Date nextPlannedExecution = null;

	public boolean init() {
		return true;
	}

	/**
	 * Things to do before an Invocation
	 */
	public void before() {

	}

	/**
	 * Things to do after an Invocation.
	 * (if the Invocation code has not thrown any exception)
	 */
	public void after() {

	}

	/**
	 * Here you do the job
	 */
	public void doJob() throws Exception {
	}

	/**
	 * Here you do the job and return a result
	 */
	public V doJobWithResult() throws Exception {
		doJob();
		return null;
	}

	public void execute() throws Exception {

	}

	/**
	 * 现在马上执行任务
	 * Start this job now (well ASAP)
	 * @return the job completion
	 */
	public Promise<V> now() {
		final Promise<V> smartFuture = new Promise<V>();
		JobsPlugin.executor.submit(getJobCallingCallable(smartFuture));
		return smartFuture;
	}

	/**
	 * If is called in a 'HttpRequest' invocation context, waits until request
	 * is served and schedules job then.
	 *
	 * Otherwise is the same as now();
	 *
	 * If you want to schedule a job to run after some other job completes, wait till a promise redeems
	 * of just override first Job's call() to schedule the second one.
	 *
	 * @return the job completion
	 */
	public Promise<V> afterRequest() {
		final Promise<V> smartFuture = new Promise<V>();
		Callable<V> callable = getJobCallingCallable(smartFuture);
		JobsPlugin.addAfterRequestAction(callable);
		return smartFuture;
	}

	/**
	 * 在指定秒后执行任务
	 * Start this job in several seconds
	 * @return the job completion
	 */
	public Promise<V> in(String delay) {
		return in(Time.parseDuration(delay));
	}

	/**
	 * 在指定秒后执行任务
	 * Start this job in several seconds
	 * @return the job completion
	 */
	public Promise<V> in(int seconds) {
		final Promise<V> smartFuture = new Promise<V>();
		JobsPlugin.executor.schedule(getJobCallingCallable(smartFuture), seconds, TimeUnit.SECONDS);
		return smartFuture;
	}

	/**
	 * 任务执行
	 * @param smartFuture
	 * @return
	 */
	private Callable<V> getJobCallingCallable(final Promise<V> smartFuture) {
		return new Callable<V>() {
			@Override
			public V call() throws Exception {
				try {
					V result = Job.this.call();
					if (smartFuture != null) {
						smartFuture.invoke(result);
					}
					return result;
				} catch (Exception e) {
					if (smartFuture != null) {
						smartFuture.invokeWithException(e);
					}
					return null;
				}
			}
		};
	}

	/**
	 * 每多少秒执行任务
	 * Run this job every n seconds
	 */
	public void every(String delay) {
		every(Time.parseDuration(delay));
	}

	/**
	 * Run this job every n seconds
	 */
	public void every(int seconds) {
		JobsPlugin.executor.scheduleWithFixedDelay(this, seconds, seconds, TimeUnit.SECONDS);
		JobsPlugin.scheduledJobs.add(this);
	}

	// Customize Invocation
	public void onException(Throwable e) {
		wasError = true;
		lastException = e;
		log.error("Error during job execution (%s)", e);
		throw new UnexpectedException(unwrap(e));
	}

	private Throwable unwrap(Throwable e) {
		while ((e instanceof UnexpectedException || e instanceof EfException) && e.getCause() != null) {
			e = e.getCause();
		}
		return e;
	}

	public void run() {
		call();
	}

	/**
	 * 任务执行
	 */
	public V call() {
		try {
			if (init()) {//初始化
				before();//执行前
				V result = null;

				try {
					lastException = null;
					lastRun = System.currentTimeMillis();//最近一次执行时间

					// If we have a plugin, get him to execute the job within the filter. 
					final AtomicBoolean executed = new AtomicBoolean(false);
					Promises.Function0<V> f = new Promises.Function0<V>() {
						@Override
						public V apply() throws Throwable {
							executed.set(true);
							return doJobWithResult();
						}
					};

					result = f.apply();//执行任务

					// No filter function found => we need to execute anyway( as before the use of withinFilter )
					if (!executed.get()) {
						result = doJobWithResult();
					}
					wasError = false;
				} catch (EfException e) {
					throw e;
				} catch (Exception e) {
					throw e;
				}
				after();//执行完后
				return result;
			}
		} catch (Throwable e) {
			onException(e);
		} finally {
			_finally();//执行完 把下一次任务 放进去
		}
		return null;
	}

	public void _finally() {
		if (executor == JobsPlugin.executor) {
			JobsPlugin.scheduleForCRON(this);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

}

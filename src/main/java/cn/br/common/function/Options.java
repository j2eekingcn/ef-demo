package cn.br.common.function;

import java.util.Collections;
import java.util.Iterator;

/**
 * Option<T> None<T> Some<T>
 * 
 * 可以设置或不设置的T值 <br/>
 * 
 * 
 * Safe division (will never throw a runtime ArithmeticException) 
 * 
	public Option<Double> div(double a, double b) {
	    if (b == 0)
	        return None();
	    else
	        return Some(a / b);
	}
	这是使用此功能的一种方法：
	
	Option<Double> q = div(42, 5);
	if (q.isDefined()) {
	    Logger.info("q = %s", q.get()); // "q = 8.4"
	}
	
	
	但是，利用Option<T>实现的事实，可以使用一种更方便的语法Iterable<T>：
	
	for (double q : div(42, 5)) {
	    Logger.info("q = %s", q); // "q = 8.4"
	}
 * @author ZJL
 *
 */
public class Options {

	/**
	 * 可以设置或不设置的T值
	 * @author ZJL
	 *
	 * @param <T>
	 */
	public static abstract class Option<T> implements Iterable<T> {

		public abstract boolean isDefined();

		public abstract T get();

		public static <T> None<T> None() {
			return (None<T>) (Object) None;
		}

		public static <T> Some<T> Some(T value) {
			return new Some<T>(value);
		}
	}

	public static <A> Some<A> Some(A a) {
		return new Some(a);
	}

	/**
	 * 没有值
	 * @author ZJL
	 *
	 * @param <T>
	 */
	public static class None<T> extends Option<T> {

		@Override
		public boolean isDefined() {
			return false;
		}

		@Override
		public T get() {
			throw new IllegalStateException("No value");
		}

		@Override
		public Iterator<T> iterator() {
			return Collections.<T> emptyList().iterator();
		}

		@Override
		public String toString() {
			return "None";
		}
	}

	public static None<Object> None = new None<Object>();

	/**
	 * 有值
	 * @author ZJL
	 *
	 * @param <T>
	 */
	public static class Some<T> extends Option<T> {

		final T value;

		public Some(T value) {
			this.value = value;
		}

		@Override
		public boolean isDefined() {
			return true;
		}

		@Override
		public T get() {
			return value;
		}

		@Override
		public Iterator<T> iterator() {
			return Collections.singletonList(value).iterator();
		}

		@Override
		public String toString() {
			return "Some(" + value + ")";
		}
	}
}

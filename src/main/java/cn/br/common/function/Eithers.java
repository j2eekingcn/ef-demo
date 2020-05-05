package cn.br.common.function;

import static cn.br.common.function.Options.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.br.common.function.Options.Option;

/**
 * Eithers	<br/>
 * 包含A值或B值
 * @author ZJL
 *
 */
public class Eithers {

	private static Logger logger = LoggerFactory.getLogger(Eithers.class);

	/**
	 * 包含A值或B值
	 * @author ZJL
	 * @param <A>
	 * @param <B>
	 */
	public static class Either<A, B> {

		final public Option<A> _1;
		final public Option<B> _2;

		private Either(Option<A> _1, Option<B> _2) {
			this._1 = _1;
			this._2 = _2;
		}

		public static <A, B> Either<A, B> _1(A value) {
			return new Either(Some(value), None);
		}

		public static <A, B> Either<A, B> _2(B value) {
			return new Either(None, Some(value));
		}

		@Override
		public String toString() {
			return "E2(_1: " + _1 + ", _2: " + _2 + ")";
		}
	}

	/**
	 * 包含A值或B值
	 * @author ZJL
	 *
	 * @param <A>
	 * @param <B>
	 */
	public static class E2<A, B> extends Either<A, B> {

		private E2(Option<A> _1, Option<B> _2) {
			super(_1, _2);
		}
	}

	/**
	 * 包含A值或B值或C值
	 * @author ZJL
	 *
	 * @param <A>
	 * @param <B>
	 * @param <C>
	 */
	public static class E3<A, B, C> {

		final public Option<A> _1;
		final public Option<B> _2;
		final public Option<C> _3;

		private E3(Option<A> _1, Option<B> _2, Option<C> _3) {
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
		}

		public static <A, B, C> E3<A, B, C> _1(A value) {
			return new E3(Some(value), None, None);
		}

		public static <A, B, C> E3<A, B, C> _2(B value) {
			return new E3(None, Some(value), None);
		}

		public static <A, B, C> E3<A, B, C> _3(C value) {
			return new E3(None, None, Some(value));
		}

		@Override
		public String toString() {
			return "E3(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ")";
		}
	}

	/**
	 * 包含A值或B值或C值或D值
	 * @author ZJL
	 *
	 * @param <A>
	 * @param <B>
	 * @param <C>
	 * @param <D>
	 */
	public static class E4<A, B, C, D> {

		final public Option<A> _1;
		final public Option<B> _2;
		final public Option<C> _3;
		final public Option<D> _4;

		private E4(Option<A> _1, Option<B> _2, Option<C> _3, Option<D> _4) {
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
			this._4 = _4;
		}

		public static <A, B, C, D> E4<A, B, C, D> _1(A value) {
			return new E4(Option.Some(value), None, None, None);
		}

		public static <A, B, C, D> E4<A, B, C, D> _2(B value) {
			return new E4(None, Some(value), None, None);
		}

		public static <A, B, C, D> E4<A, B, C, D> _3(C value) {
			return new E4(None, None, Some(value), None);
		}

		public static <A, B, C, D> E4<A, B, C, D> _4(D value) {
			return new E4(None, None, None, Some(value));
		}

		@Override
		public String toString() {
			return "E4(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ")";
		}
	}

	/**
	 * 包含A值或B值或C值或D值或E值
	 * @author ZJL
	 *
	 * @param <A>
	 * @param <B>
	 * @param <C>
	 * @param <D>
	 * @param <E>
	 */
	public static class E5<A, B, C, D, E> {

		final public Option<A> _1;
		final public Option<B> _2;
		final public Option<C> _3;
		final public Option<D> _4;
		final public Option<E> _5;

		private E5(Option<A> _1, Option<B> _2, Option<C> _3, Option<D> _4, Option<E> _5) {
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
			this._4 = _4;
			this._5 = _5;
		}

		public static <A, B, C, D, E> E5<A, B, C, D, E> _1(A value) {
			return new E5(Option.Some(value), None, None, None, None);
		}

		public static <A, B, C, D, E> E5<A, B, C, D, E> _2(B value) {
			return new E5(None, Option.Some(value), None, None, None);
		}

		public static <A, B, C, D, E> E5<A, B, C, D, E> _3(C value) {
			return new E5(None, None, Option.Some(value), None, None);
		}

		public static <A, B, C, D, E> E5<A, B, C, D, E> _4(D value) {
			return new E5(None, None, None, Option.Some(value), None);
		}

		public static <A, B, C, D, E> E5<A, B, C, D, E> _5(E value) {
			return new E5(None, None, None, None, Option.Some(value));
		}

		@Override
		public String toString() {
			return "E5(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ", _5:" + _5 + ")";
		}
	}

}
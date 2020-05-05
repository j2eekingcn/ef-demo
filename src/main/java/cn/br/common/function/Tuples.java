package cn.br.common.function;

/**
 *  元祖 Tuple
 *  
<pre>
public Option<Tuple<String, String>> parseEmail(String email) {
    final Matcher matcher = Pattern.compile("(\\w+)@(\\w+)").matcher(email);
    if (matcher.matches()) {
        return Some(Tuple(matcher.group(1), matcher.group(2)));
    }
    return None();
}
然后：

for (Tuple<String, String> email : parseEmail("foo@bar.com")) {
    Logger.info("name = %s", email._1); // "name = foo"
    Logger.info("server = %s", email._2); // "server = bar.com"
}
</pre>
 * @author ZJL
 * 
 * 
 *
 */
public class Tuples {

	public static class Tuple<A, B> {

		final public A _1;
		final public B _2;

		public Tuple(A _1, B _2) {
			this._1 = _1;
			this._2 = _2;
		}

		@Override
		public String toString() {
			return "T2(_1: " + _1 + ", _2: " + _2 + ")";
		}
	}

	public static <A, B> Tuple<A, B> Tuple(A a, B b) {
		return new Tuple<A, B>(a, b);
	}

	public static class T2<A, B> extends Tuple<A, B> {

		public T2(A _1, B _2) {
			super(_1, _2);
		}
	}

	public static <A, B> T2<A, B> T2(A a, B b) {
		return new T2<A, B>(a, b);
	}

	public static class T3<A, B, C> {

		final public A _1;
		final public B _2;
		final public C _3;

		public T3(A _1, B _2, C _3) {
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
		}

		@Override
		public String toString() {
			return "T3(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ")";
		}
	}

	public static <A, B, C> T3<A, B, C> T3(A a, B b, C c) {
		return new T3<A, B, C>(a, b, c);
	}

	public static class T4<A, B, C, D> {

		final public A _1;
		final public B _2;
		final public C _3;
		final public D _4;

		public T4(A _1, B _2, C _3, D _4) {
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
			this._4 = _4;
		}

		@Override
		public String toString() {
			return "T4(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ")";
		}
	}

	public static <A, B, C, D> T4<A, B, C, D> T4(A a, B b, C c, D d) {
		return new T4<A, B, C, D>(a, b, c, d);
	}

	public static class T5<A, B, C, D, E> {

		final public A _1;
		final public B _2;
		final public C _3;
		final public D _4;
		final public E _5;

		public T5(A _1, B _2, C _3, D _4, E _5) {
			this._1 = _1;
			this._2 = _2;
			this._3 = _3;
			this._4 = _4;
			this._5 = _5;
		}

		@Override
		public String toString() {
			return "T5(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ", _5:" + _5 + ")";
		}
	}

	public static <A, B, C, D, E> T5<A, B, C, D, E> T5(A a, B b, C c, D d, E e) {
		return new T5<A, B, C, D, E>(a, b, c, d, e);
	}
}

package cn.br.common.function;

import cn.br.common.function.Options.Option;

/**
 *  模式匹配
 * @author ZJL
 <pre>
 标准方法是：

Object o = anything();
if(o instanceof String && ((String)o).startsWith("command:")) {
    String s = (String)o;
    System.out.println(s.toUpperCase());
}


使用模式匹配库，您可以将其编写为：
for(String s: String.and(StartsWith("command:")).match(o)) {
    System.out.println(s.toUpperCase());
}
 </pre>
 */
public class Matchers {

	public static abstract class Matcher<T, R> {

		/**
		 * 创建一个 匹配器 (String)
		 */
		public static Matcher<Object, String> String = new Matcher<Object, String>() {

			@Override
			public Option<String> match(Object o) {
				if (o instanceof String) {
					return Option.Some((String) o);
				}
				return Option.None();
			}
		};

		public abstract Option<R> match(T o);

		/**
		 * 匹配的对象
		 */

		public Option<R> match(Option<T> o) {
			if (o.isDefined()) {
				return match(o.get());
			}
			return Option.None();
		}

		/**
		 * 匹配的规则
		 */
		public <NR> Matcher<T, NR> and(final Matcher<R, NR> nextMatcher) {
			final Matcher<T, R> firstMatcher = this;
			return new Matcher<T, NR>() {

				@Override
				public Option<NR> match(T o) {
					for (R r : firstMatcher.match(o)) {
						return nextMatcher.match(r);
					}
					return Option.None();
				}
			};
		}

		/**
		 * 匹配 是否 某个类
		 * @param clazz
		 * @return
		 */
		public static <K> Matcher<Object, K> ClassOf(final Class<K> clazz) {
			return new Matcher<Object, K>() {

				@Override
				public Option<K> match(Object o) {
					if (o instanceof Option && ((Option) o).isDefined()) {
						o = ((Option) o).get();
					}
					if (clazz.isInstance(o)) {
						return Option.Some((K) o);
					}
					return Option.None();
				}
			};
		}

		/**
		 * 匹配以什么开始
		 * @param prefix
		 * @return
		 */
		public static Matcher<String, String> StartsWith(final String prefix) {
			return new Matcher<String, String>() {

				@Override
				public Option<String> match(String o) {
					if (o.startsWith(prefix)) {
						return Option.Some(o);
					}
					return Option.None();
				}
			};
		}

		/**
		 * 正则匹配
		 */
		public static Matcher<String, String> Re(final String pattern) {
			return new Matcher<String, String>() {

				@Override
				public Option<String> match(String o) {
					if (o.matches(pattern)) {
						return Option.Some(o);
					}
					return Option.None();
				}
			};
		}

		/**
		 * 匹配是否相等 
		 */
		public static <X> Matcher<X, X> Equals(final X other) {
			return new Matcher<X, X>() {

				@Override
				public Option<X> match(X o) {
					if (o.equals(other)) {
						return Option.Some(o);
					}
					return Option.None();
				}
			};
		}
	}

}

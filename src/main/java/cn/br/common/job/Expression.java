package cn.br.common.job;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expression {

	/**
	 * 判断是否变量  ${cron.login} 格式
	 */
	static Pattern expression = Pattern.compile("^\\$\\{(.*)\\}$");

	public static Object evaluate(String value, String defaultValue) {
		Matcher matcher = expression.matcher(value);
		if (matcher.matches()) {
			System.err.println(value);
			//TODO
			//			return Play.configuration.getProperty(matcher.group(1), defaultValue);
		}
		return value;
	}

//	public static void main(String[] args) {
//		System.out.println(Expression.evaluate("1s", "1s"));
//		System.out.println(Expression.evaluate("0 28 11 ? * *", "0 28 11 ? * *"));
//		System.out.println(Expression.evaluate("${cron.login}", "cron.aaa"));
//	}

}

package cn.br.common.sql;

public class Singleton {
	/**
	 * 禁止指令重排序优化
	 */
	private static volatile Singleton instance = null;

	private Singleton() {
	}

	public static Singleton getInstance() {
		if (instance == null) {
			synchronized (Singleton.class) {
				if (instance == null) {
					instance = new Singleton();
				}
			}
		}
		return instance;
	}
}

package cn.br.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.LongAdder;

/**
 * 线程命名工厂
 * @author ZJL
 *
 */
public class NamedThreadFactory implements ThreadFactory {

	private final String prefix;
	private final LongAdder threadNumber = new LongAdder();

	public NamedThreadFactory(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		threadNumber.add(1);
		return new Thread(runnable, prefix + "thread-" + threadNumber.intValue());
	}
}
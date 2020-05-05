package cn.br.common.task;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import cn.br.common.task.cron.CronExecutorService;

/**
 * Task Manager
 * <p>
 * Manages all tasks, including task thread pools and stops, adds, and gets a task.
 *
 * @author ZJL
 * @date 2018/4/9
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskManager {

	private final static Map<String, Task> TASK_MAP = new HashMap<>(8);

	private final static ReentrantReadWriteLock rrw = new ReentrantReadWriteLock();

	private final static Lock readLock = rrw.readLock();

	private final static Lock writeLock = rrw.writeLock();

	private static CronExecutorService cronExecutorService;

	public static void init(CronExecutorService cronExecutorService) {
		if (null != TaskManager.cronExecutorService) {
			throw new RuntimeException("Don't re-initialize the task thread pool.");
		}
		TaskManager.cronExecutorService = cronExecutorService;
		Runtime.getRuntime().addShutdownHook(new Thread(cronExecutorService::shutdown));
	}

	public static CronExecutorService getExecutorService() {
		return cronExecutorService;
	}

	public static void addTask(Task task) {
		writeLock.lock();
		try {
			TASK_MAP.put(task.getName(), task);
		} finally {
			writeLock.unlock();
		}
		log.info(" Add task [{}]", task.getName());
	}

	public static List<Task> getTasks() {
		Collection<Task> values;
		readLock.lock();
		try {
			values = Optional.ofNullable(TASK_MAP.values()).orElse(Collections.EMPTY_LIST);
		} finally {
			readLock.unlock();
		}
		return new ArrayList<>(values);
	}

	public static Task getTask(String name) {
		readLock.lock();
		try {
			return TASK_MAP.get(name);
		} finally {
			readLock.unlock();
		}
	}

	public static boolean stopTask(String name) {
		Task task;
		readLock.lock();
		try {
			task = TASK_MAP.get(name);
		} finally {
			readLock.unlock();
		}
		return task == null ? Boolean.FALSE : task.stop();
	}

}

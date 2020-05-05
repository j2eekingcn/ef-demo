package cn.br.common.job;

import java.lang.reflect.Field;
import java.util.concurrent.FutureTask;

public class Java {
	/**
	 * Try to discover what is hidden under a FutureTask (hack)
	 */
	public static Object extractUnderlyingCallable(FutureTask<?> futureTask) {
		try {
			Object callable = null;
			// Try to search for the Filed sync first, if not present will try filed callable
			try {
				Field syncField = FutureTask.class.getDeclaredField("sync");
				syncField.setAccessible(true);
				Object sync = syncField.get(futureTask);
				if (sync != null) {
					Field callableField = sync.getClass().getDeclaredField("callable");
					callableField.setAccessible(true);
					callable = callableField.get(sync);
				}
			} catch (NoSuchFieldException ex) {
				Field callableField = FutureTask.class.getDeclaredField("callable");
				callableField.setAccessible(true);
				callable = callableField.get(futureTask);
			}
			if (callable != null && callable.getClass().getSimpleName().equals("RunnableAdapter")) {
				Field taskField = callable.getClass().getDeclaredField("task");
				taskField.setAccessible(true);
				return taskField.get(callable);
			}
			return callable;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

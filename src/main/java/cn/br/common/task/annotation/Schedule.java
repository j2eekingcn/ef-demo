package cn.br.common.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Schedule
 *
 * @author biezhi
 * @date 2018/4/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Schedule {

	/**
	 * cron expression
	 *
	 * @return
	 */
	String cron();

	/**
	 * The name of this task, when you don't specify it, will use the "task-0" index to start with 0,
	 * and the order can be messy.
	 * <p>
	 * If you want to manually manipulate a task, suggest specifying the name.
	 *
	 * @return
	 */
	String name() default "";

	/**
	 * Delay execution, unit millisecond, start the task by default.
	 *
	 * @return returns the number of milliseconds to delay execution.
	 */
	long delay() default 0;

}

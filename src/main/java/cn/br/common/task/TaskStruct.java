package cn.br.common.task;

import lombok.Data;

import java.lang.reflect.Method;

import cn.br.common.task.annotation.Schedule;

/**
 * Task struct
 * <p>
 * Used to save task meta information on a method.
 *
 * @author ZJL
 * @date 2018/4/9
 */
@Data
public class TaskStruct {

	private Schedule schedule;
	private String cron;
	private Method method;
	private Class<?> type;

}

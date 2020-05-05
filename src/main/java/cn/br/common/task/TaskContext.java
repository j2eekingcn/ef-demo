package cn.br.common.task;

import lombok.Getter;

/**
 * Task Context
 * <p>
 * Used to save task context, mainly to stop a task.
 *
 * @author ZJL
 * @date 2018/4/9
 */
public class TaskContext {

	@Getter
	private Task task;

	public TaskContext(Task task) {
		this.task = task;
	}

	public void stop() {
		task.stop();
	}

}

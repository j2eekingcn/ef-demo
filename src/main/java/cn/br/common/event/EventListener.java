package cn.br.common.event;

/**
 * EventListener 时间监听
 *
 * @author ZJL
 * @date 2017/9/18
 */
@FunctionalInterface
public interface EventListener {

	/**
	 * Start event <br/>
	 * 事件触发
	 *
	 * @param e Event instance
	 */
	void trigger(Event e);

}
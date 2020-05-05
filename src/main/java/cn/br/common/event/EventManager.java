package cn.br.common.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Event manager <br/>
 * 事件管理者
 *
 * @author ZJL
 * @date 2017/9/18
 */
public class EventManager {

	/**
	 * 事件类型--事件监听 关联关系
	 */
	private Map<EventType, List<EventListener>> listenerMap;

	/**
	 * 优先级
	 */
	private OrderComparator<EventListener> comparator = new OrderComparator<>();

	public EventManager() {
		this.listenerMap = Stream.of(EventType.values()).collect(Collectors.toMap(v -> v, v -> new LinkedList<>()));
	}

	/**
	 * 添加事件监听
	 * @param <T>
	 * @param type
	 * @param listener
	 */
	public <T> void addEventListener(EventType type, EventListener listener) {
		listenerMap.get(type).add(listener);
	}

	/**
	 * 事件触发通知
	 * @param <T>
	 * @param type
	 * @param event
	 */
	public <T> void fireEvent(EventType type, Event event) {
		listenerMap.get(type).stream().sorted(comparator).forEach(listener -> listener.trigger(event));
	}

}
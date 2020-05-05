package cn.br.common.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Event 事件
 *
 * @author ZJL
 * @date 2017/9/18
 */
public class Event {

	private Map<String, Object> attribute = new HashMap<>(4);

	public Map<String, Object> attribute() {
		return attribute;
	}

	public Object attribute(String key) {
		return attribute.get(key);
	}

	public Event attribute(String key, Object value) {
		attribute.put(key, value);
		return this;
	}

}
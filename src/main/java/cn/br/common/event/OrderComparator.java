package cn.br.common.event;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Bean order by <br/>
 * 
 * 优先级排序
 *
 * @author ZJL
 *         2017/6/2
 */
@SuppressWarnings("serial")
public class OrderComparator<T> implements Serializable, Comparator<T> {

	/**
	 * 返回
	 * 正数 代表大于
	 * 零 等于
	 * 负数 小于
	 */
	@Override
	public int compare(T e1, T e2) {
		Order o1 = e1.getClass().getAnnotation(Order.class);
		Order o2 = e2.getClass().getAnnotation(Order.class);
		Integer order1 = null != o1 ? o1.value() : Integer.MAX_VALUE;
		Integer order2 = null != o2 ? o2.value() : Integer.MAX_VALUE;
		return order1.compareTo(order2);
	}

}

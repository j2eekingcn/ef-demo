package cn.br.common.hander;

import cn.br.common.annotations.Filter;
import cn.br.common.annotations.Order;
import io.jooby.Context;
import io.jooby.Route.Before;

/**
 * 前置过滤器
 * 
 * @author ZJL
 *
 */
@Filter("/mvc/*")
@Order(1)
public class LoginHander implements Before {

	@Override
	public void apply(Context ctx) throws Exception {
		System.err.println("前置过滤器");
	}

}

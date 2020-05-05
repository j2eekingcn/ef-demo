package cn.br.common.hander;

import cn.br.common.annotations.Filter;
import io.jooby.Context;
import io.jooby.Route.After;

/**
 * 后置过滤器
 * 
 * @author ZJL
 *
 */
@Filter("/mvc/*")
public class LogHander implements After {

	@Override
	public void apply(Context ctx, Object obj, Throwable throwable) throws Exception {
		System.err.println("后置过滤器");
	}

}

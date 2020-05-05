package cn.br.ef.modules.sys.action;

import javax.inject.Inject;

import cn.br.ef.modules.sys.service.UserBiz;
import io.jooby.Context;
import io.jooby.MediaType;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;

@Path("/mvc")
public class SimpleAct {

	@Inject
	UserBiz userBiz;

	@GET
	public void get(Context ctx) {
		userBiz.test();
		ctx.setResponseType(MediaType.TEXT).send("hello World 232423433333344444!");
	}

}

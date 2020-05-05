package cn.br.common.hander;

import cn.br.common.annotations.Order;
import cn.br.common.annotations.SSE;
import io.jooby.ServerSentEmitter;
import io.jooby.ServerSentEmitter.Handler;

@SSE("/sse")
@Order(1)
public class ServerSendHander implements Handler {

	@Override
	public void handle(ServerSentEmitter sse) throws Exception {
	}

}

package cn.br;

import cn.br.ef.MvcApiMain;
import io.jooby.ExecutionMode;
import io.jooby.Jooby;

public class Boot {

	public static void main(String... args) {
		// Supplier<MvcApiMain> boot = MvcApiMain::new;
		// MvcApiMain mvcApiMain = boot.get();
		// Jooby.run(boot, args);

//		Jooby.run(MvcApiMain.class, args);

		Jooby.runApp(args, ExecutionMode.DEFAULT, MvcApiMain.class);
//		Jooby.runApp(args, ExecutionMode.WORKER, MvcApiMain.class);
//		Jooby.runApp(args, ExecutionMode.EVENT_LOOP, MvcApiMain.class);

	}
}

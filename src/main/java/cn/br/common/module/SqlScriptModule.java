package cn.br.common.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import cn.br.common.sql.SqlLoader;
import cn.br.template.ext.sql.SqlKit;
import io.jooby.Environment;
import io.jooby.Extension;
import io.jooby.Jooby;
import io.jooby.ServiceRegistry;
import io.jooby.di.JoobyModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlScriptModule implements Extension {

	public Config config() {
		return ConfigFactory.parseResources(getClass(), "m1.properties");
	}

	@Override
	public void install(Jooby application) throws Exception {
		Environment env = application.getEnvironment();
		Config config = application.getConfig();
		ServiceRegistry registry = application.getServices();

		SqlKit sqlKit = SqlLoader.getSingleton().loadSql(env);
		sqlKit.reloadModifiedSqlTemplate();
//		binder.bind(SqlKit.class).toInstance(sqlKit);
		registry.putIfAbsent(SqlKit.class, sqlKit);
		registry.putIfAbsent(Jooby.class, application);
		log.debug("模块 {} 初始化成功", this.getClass().getName());

	}
}

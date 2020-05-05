package cn.br.common.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import cn.br.common.job.JobsPlugin;
import io.jooby.Environment;
import io.jooby.Extension;
import io.jooby.Jooby;
import io.jooby.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobModule implements Extension {

	public Config config() {
		return ConfigFactory.parseResources(getClass(), "m1.properties");
	}

	@Override
	public void install(Jooby application) throws Exception {
		Environment env = application.getEnvironment();
		Config config = application.getConfig();
		ServiceRegistry registry = application.getServices();

		// binder.bindInterceptor(classMatcher, methodMatcher, interceptors);
		// SqlKit sqlKit = SqlLoader.getSingleton().loadSql(env);
		// binder.bind(SqlKit.class).toInstance(sqlKit);
		// log.debug("模块 {} 初始化成功", this.getClass().getName());
		JobsPlugin jobsPlugin = new JobsPlugin();
//		binder.bind(JobsPlugin.class).toInstance(jobsPlugin);
		registry.putIfAbsent(JobsPlugin.class, jobsPlugin);

		jobsPlugin.onApplicationStart();
		jobsPlugin.afterApplicationStart();

	}
}

package cn.br.common.view;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cn.br.kit.StrKit;
import cn.br.kit.core.util.ClassUtil;
import cn.br.kit.core.util.StrUtil;
import cn.br.template.Directive;
import cn.br.template.Engine;
import cn.br.template.source.ClassPathSourceFactory;
import cn.br.template.source.ISourceFactory;
import io.jooby.Environment;
import io.jooby.Extension;
import io.jooby.Jooby;
import io.jooby.ServiceRegistry;

public class Etl implements Extension {

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 后缀
	 */
	private final String suffix;

	private String path = "template";

	public static final Engine engine = new Engine();

	static List<String> sharedFunctionFiles = new ArrayList<String>();

	private BiConsumer<Engine, Config> configurer;

	static boolean sessionInView = false;
	static boolean createSession = true;

	public Etl(final String prefix, final String suffix) {
		this.suffix = requireNonNull(suffix, "Template suffix is required.");
	}

	public Etl(final String prefix) {
		this(prefix, ".html");
	}

	public Etl() {
		this("/");
	}

	public Etl doWith(final BiConsumer<Engine, Config> configurer) {
		this.configurer = requireNonNull(configurer, "Configurer is required.");
		return this;
	}

	/**
	 * 初始化 baseTemplatePath 值，启用 ClassPathSourceFactory 时 无需设置 baseTemplatePath 为
	 * web 根路径
	 */
	private void initBaseTemplatePath() {
		if (engine.getSourceFactory() instanceof ClassPathSourceFactory) {
			// do nothing
		} else {
			if (StrKit.isBlank(engine.getBaseTemplatePath())) {
				StringBuilder basePath = StrUtil.builder(getClass().getResource("/").getPath(), path);
				engine.setBaseTemplatePath(basePath.toString());
			}
		}

	}

	public Config config() {
		// ClassUtil.getContextClassLoader()
		// return ConfigFactory.parseResources(getClass(), "ftl.conf");
		return ConfigFactory.parseResources(ClassUtil.getContextClassLoader(), "ftl.conf");
	}

	private Properties properties(final Config config) {
		Properties props = new Properties();
		System.err.println(config.getConfig("ftl"));
		// dump
		config.getConfig("ftl").entrySet().forEach(e -> {
			// String name = e.getKey();
			// String value = e.getValue().unwrapped().toString();
			// log.debug(" ftl.{} = {}", name, value);
			// props.setProperty(name, value);
		});
		// this is a jooby option
		// props.remove("cache");
		return props;
	}

	/**
	 * 设置开发模式，值为 true 时支持模板文件热加载
	 */
	public void setDevMode(boolean devMode) {
		engine.setDevMode(devMode);
	}

	/**
	 * 设置 shared function 文件，多个文件用逗号分隔
	 */
	public void setSharedFunction(String sharedFunctionFiles) {
		if (StrKit.isBlank(sharedFunctionFiles)) {
			throw new IllegalArgumentException("sharedFunctionFiles can not be blank");
		}

		String[] fileArray = sharedFunctionFiles.split(",");
		for (String fileName : fileArray) {
			Etl.sharedFunctionFiles.add(fileName);
		}
	}

	/**
	 * 通过 List 配置多个 shared function file
	 * 
	 * <pre>
	 * 配置示例：
	 * 	<property name="sharedFunctionList">
	 *     	<list>
	 *     		<value>_layout.html</value>
	 *     		<value>_paginate.html</value>
	 *     	</list>
	 * 	</property>
	 * </pre>
	 */
	public void setSharedFunctionList(List<String> sharedFunctionList) {
		if (sharedFunctionList != null) {
			Etl.sharedFunctionFiles.addAll(sharedFunctionList);
		}
	}

	/**
	 * 添加 shared function 文件，可调用多次添加多个文件
	 */
	public void addSharedFunction(String fileName) {
		// 等待 SourceFactory、baseTemplatePath 配置到位，利用 sharedFunctionFiles 实现延迟加载
		sharedFunctionFiles.add(fileName);
	}

	/**
	 * 添加自定义指令
	 */
	public void addDirective(String directiveName, Class<? extends Directive> directiveClass) {
		engine.addDirective(directiveName, directiveClass);
	}

	/**
	 * 添加自定义指令，已被 addDirective(String, Class<? extends Directive>) 方法取代
	 */
	@Deprecated
	public void addDirective(String directiveName, Directive directive) {
		addDirective(directiveName, directive.getClass());
	}

	/**
	 * 添加共享对象
	 */
	public void addSharedObject(String name, Object object) {
		engine.addSharedObject(name, object);
	}

	/**
	 * 添加共享方法
	 */
	public void addSharedMethod(Object sharedMethodFromObject) {
		engine.addSharedMethod(sharedMethodFromObject);
	}

	/**
	 * 添加共享方法
	 */
	public void addSharedMethod(Class<?> sharedMethodFromClass) {
		engine.addSharedMethod(sharedMethodFromClass);
	}

	/**
	 * 添加扩展方法
	 */
	public static void addExtensionMethod(Class<?> targetClass, Object objectOfExtensionClass) {
		Engine.addExtensionMethod(targetClass, objectOfExtensionClass);
	}

	/**
	 * 添加扩展方法
	 */
	public static void addExtensionMethod(Class<?> targetClass, Class<?> extensionClass) {
		Engine.addExtensionMethod(targetClass, extensionClass);
	}

	/**
	 * 设置 ISourceFactory 用于为 engine 切换不同的 ISource 实现类
	 * 
	 * <pre>
	 * 配置为 ClassPathSourceFactory 时特别注意：
	 *    由于在 initServletContext() 通过如下方法中已设置了 baseTemplatePath 值：
	 *        setBaseTemplatePath(servletContext.getRealPath("/"))
	 *    
	 *    而 ClassPathSourceFactory 在 initServletContext() 方法中设置的
	 *    值之下不能工作，所以在本方法中通过如下方法清掉了该值：
	 *         setBaseTemplatePath(null)
	 *    
	 *    这种处理方式适用于绝大部分场景，如果在使用 ClassPathSourceFactory 的同时
	 *    仍然需要设置 baseTemplatePath，则在调用该方法 “之后” 通过如下代码再次配置：
	 *         setBaseTemplatePath(value)
	 * </pre>
	 */
	public void setSourceFactory(ISourceFactory sourceFactory) {
		if (sourceFactory instanceof ClassPathSourceFactory) {
			engine.setBaseTemplatePath(null);
		}
		engine.setSourceFactory(sourceFactory);
	}

	/**
	 * 设置模板基础路径
	 */
	public void setBaseTemplatePath(String baseTemplatePath) {
		engine.setBaseTemplatePath(baseTemplatePath);
	}

	/**
	 * 设置为 true 时支持在模板中使用 #(session.value) 形式访问 session 中的数据
	 */
	public void setSessionInView(boolean sessionInView) {
		Etl.sessionInView = sessionInView;
	}

	/**
	 * 在使用 request.getSession(createSession) 时传入 用来指示 session 不存在时是否立即创建
	 */
	public void setCreateSession(boolean createSession) {
		Etl.createSession = createSession;
	}

	/**
	 * 设置 encoding
	 */
	public void setEncoding(String encoding) {
		engine.setEncoding(encoding);
	}

	/**
	 * 设置 #date(...) 指令，对于 Date、Timestamp、Time 的输出格式
	 */
	public void setDatePattern(String datePattern) {
		engine.setDatePattern(datePattern);
	}

	/**
	 * 利用 sharedFunctionFiles 延迟调用 addSharedFunction 因为需要等待 baseTemplatePath 以及
	 * ISourceFactory 设置完毕以后 才能正常工作
	 */
	private void initSharedFunction() {
		for (String file : sharedFunctionFiles) {
			engine.addSharedFunction(file.trim());
		}
	}

	@Override
	public void install(Jooby application) throws Exception {
		initBaseTemplatePath();
		initSharedFunction();

		Config config = application.getConfig();
		Environment env = application.getEnvironment();
		ServiceRegistry serviceRegistry = application.getServices();

		log.error("Etl: {}", engine.getEngineConfig().getBaseTemplatePath());
		Properties properties = properties(config);

		// cache
		if (env.isActive("dev", "test") || config.getString("ftl.cache").isEmpty()) {
			engine.setDevMode(true); // noop cache
		} else {
			engine.setDevMode(false);
		}

		if (configurer != null) {
			configurer.accept(engine, config);
		}

		serviceRegistry.put(Engine.class, engine);
//		binder.bind(Engine.class).toInstance(engine);
		EtlView etlEngine = new EtlView(engine, suffix);
//		Multibinder.newSetBinder(binder, Renderer.class).addBinding().toInstance(etlEngine);
		application.encoder(etlEngine);

	}

}

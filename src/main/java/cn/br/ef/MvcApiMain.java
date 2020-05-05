package cn.br.ef;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import cn.br.common.annotations.Order;
import io.jooby.Jooby;
import io.jooby.annotations.Path;
import io.jooby.di.GuiceModule;
import io.jooby.hikari.HikariModule;
import io.jooby.json.GsonModule;
import lombok.extern.slf4j.Slf4j;

/**
 * 5679
 * 
 * @author ZJL
 *
 */
@Slf4j
public final class MvcApiMain extends Jooby {

	public String basePackage = "cn.br";
	public Reflections reflections;

	public void init() {
		getServices().putIfAbsent(Jooby.class, this);

		reflections = new Reflections(basePackage);
		install(new GuiceModule());
		
		addRoutes();
		
//		install(new HikariModule());
		install(new GsonModule());
		log.debug("初始化成功");
	}

	public MvcApiMain() {
		init();
	}

	public MvcApiMain(String basePackage) {
		this.basePackage = basePackage;
	}

	public int controllerCompare(Class<?> a, Class<?> b) {
		Order order1 = a.getAnnotation(Order.class);
		Order order2 = b.getAnnotation(Order.class);
		if (Objects.nonNull(order1) && Objects.nonNull(order2)) {
			return Integer.compare(order1.value(), order2.value());
		} else {
			Path f1 = a.getAnnotation(Path.class);
			Path f2 = b.getAnnotation(Path.class);
			return Integer.compare(f1.value().length, f2.value().length);
		}
	}

	private void addRoutes() {
		Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Path.class);
		List<Class<?>> controllerLists = controllers.stream().sorted((a, b) -> {
			return controllerCompare(a, b);
		}).collect(Collectors.toList());
		for (Class<?> controller : controllerLists) {
			mvc(controller);
		}
	}

}

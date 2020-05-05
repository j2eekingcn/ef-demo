package cn.br.common.sql;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;

import cn.br.kit.core.io.FileUtil;
import cn.br.kit.core.io.resource.ResourceUtil;
import cn.br.kit.core.util.ClassLoaderUtil;
import cn.br.template.ext.sql.SqlKit;
import cn.br.template.source.FileSourceFactory;
import io.jooby.Environment;

/**
 * SQL加载器
 * 
 * @author ZJL
 *
 */
public class SqlLoader {

	/**
	 * 内部类实现单例
	 */
	private static class SingletonHolder {
		public static SqlLoader instance = new SqlLoader();
	}

	private SqlLoader() {
	}

	/**
	 * 获取单例
	 */
	public static SqlLoader getSingleton() {
		return SingletonHolder.instance;
	}

	/**
	 * 加载SQL文件
	 */
	public SqlKit loadSql1(Environment env) {
		List<URL> sqlList = ResourceUtil.getResources("/sqls");

		SqlKit sqlKit = new SqlKit();
		sqlKit.getEngine().setSourceFactory(new FileSourceFactory());

//		Optional<Boolean> isDev = env.ifMode("dev", () -> Boolean.TRUE);
//		if (isDev.isPresent()) {
//			if (BooleanUtils.isTrue(isDev.get())) {// 设置模版开发状态
//				sqlKit.setDevMode(true);
//			}
//		}

		boolean isDev = env.isActive("dev", "test");
		if (isDev) {
			sqlKit.setDevMode(true);
		}

		for (URL item : sqlList) {
			FileUtil.loopFiles(new File(item.getFile()), new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.isFile() && pathname.getName().endsWith(".sql")) {
						sqlKit.addSqlTemplate(pathname.getPath());
						return true;
					}
					return false;
				}
			});
		}
		return sqlKit;
	}

	/**
	 * 加载SQL文件
	 */
	public SqlKit loadSql(Environment env) {
		URL sqlList = ResourceUtil.getResource("/sqls",this.getClass());
//		URL sqlList =ClassLoaderUtil.getClassLoader().getResource("/sqls");
		System.err.println(sqlList);
//		
//		URL sqlList2 =getClass().getResource("/sqls");
//		System.err.println(sqlList2);
		
		SqlKit sqlKit = new SqlKit();
		sqlKit.getEngine().setSourceFactory(new FileSourceFactory());
//		Optional<Boolean> isDev = env.ifMode("dev", () -> Boolean.TRUE);
//		if (isDev.isPresent()) {
//			if (BooleanUtils.isTrue(isDev.get())) {// 设置模版开发状态
//				sqlKit.setDevMode(true);
//			}
//		}

		boolean isDev = env.isActive("dev", "test");
		if (isDev) {
			sqlKit.setDevMode(true);
		}

		FileUtil.loopFiles(new File(sqlList.getFile()), new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isFile() && pathname.getName().endsWith(".sql")) {
					sqlKit.addSqlTemplate(pathname.getPath());
					return true;
				}
				return false;
			}
		});

		return sqlKit;
	}

}

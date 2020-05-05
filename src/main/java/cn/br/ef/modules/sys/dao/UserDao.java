package cn.br.ef.modules.sys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.sql.DataSource;
import com.google.gson.Gson;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import io.jooby.Environment;

@Singleton
public class UserDao {

	@Inject
	Config conf;

	@Inject
	Environment env;

	@Inject
	Injector injector;

	@Inject
	Gson gson;

	public void aa() {
		DataSource ds = injector.getInstance(DataSource.class);
		try (Connection conn = ds.getConnection()) {
			String sql = " select version()";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			int colCount = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= colCount; i++) {
					System.err.println(rs.getObject(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}

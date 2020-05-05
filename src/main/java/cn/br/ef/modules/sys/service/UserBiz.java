package cn.br.ef.modules.sys.service;

import javax.inject.Inject;
import com.google.inject.Singleton;
import cn.br.ef.modules.sys.dao.UserDao;

@Singleton
public class UserBiz {

	@Inject
	UserDao aa;

	public void test() {
		aa.aa();
	}

}

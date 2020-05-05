package cn.br.ef.modules.sys.model;

import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Table(name = "t_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

//	@Id
//	@DbComment("主键")
	private Long id;
	private String email; //员工号

	//@Temporal(TemporalType.TIMESTAMP)
	//@Column(name = "time", nullable = false, length = 12)
//	@Column(columnDefinition = "datetime")
	//@CreatedTimestamp
	//@UpdatedTimestamp
	private Date time;

	private String mobile;
	private String password;
	private String photo;
	private String name;
	private Long recommend_id;

//	@Column(columnDefinition = "datetime")
	private Date recommend_time;
	private int type; // 1 会员
	private boolean push_message;
	private int recommend_numbers = 0;//推荐次数
	private String recommend_code;//邀请码

//	@Column(columnDefinition = "datetime")
	private Date last_login_time;
	
	private String last_login_ip;
	private boolean disable;
	private String push_client_id;
	private String push_device_token;
	
//	@Transient
	private String sign;

}

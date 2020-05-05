package cn.br.common.util;

import io.jooby.Context;

/**
 * Web kit
 *
 * @author biezhi 2017/6/2
 */
public class WebKit {

	public static final String UNKNOWN_MAGIC = "unknown";

	/**
	 * Get the client IP address by request
	 *
	 * @param request Request instance
	 * @return return ip address
	 */
	public static String ipAddress(Context request) {
		String ipAddress = request.header("x-forwarded-for").value();
		if (StringKit.isBlank(ipAddress) || UNKNOWN_MAGIC.equalsIgnoreCase(ipAddress)) {
//			ipAddress = request.header("Proxy-Client-IP");
			ipAddress = request.header("Proxy-Client-IP").value();
		}
		if (StringKit.isBlank(ipAddress) || UNKNOWN_MAGIC.equalsIgnoreCase(ipAddress)) {
//			ipAddress = request.header("WL-Proxy-Client-IP");
			ipAddress = request.header("WL-Proxy-Client-IP").value();
		}
		if (StringKit.isBlank(ipAddress) || UNKNOWN_MAGIC.equalsIgnoreCase(ipAddress)) {
//			ipAddress = request.header("X-Real-IP");
			ipAddress = request.header("X-Real-IP").value();
		}
		if (StringKit.isBlank(ipAddress) || UNKNOWN_MAGIC.equalsIgnoreCase(ipAddress)) {
//			ipAddress = request.header("HTTP_CLIENT_IP");
			ipAddress = request.header("HTTP_CLIENT_IP").value();
		}
		if (StringKit.isBlank(ipAddress) || UNKNOWN_MAGIC.equalsIgnoreCase(ipAddress)) {
//			ipAddress = request.header("HTTP_X_FORWARDED_FOR");
			ipAddress = request.header("HTTP_X_FORWARDED_FOR").value();
		}
		return ipAddress;
	}

}

package cn.br.common.util;

import lombok.experimental.UtilityClass;

/**
 * 加解密类
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
@UtilityClass
public class PasswordKit {

	/**
	 * Define the BCrypt workload to use when generating password hashes. 10-31 is a valid value.
	 */
	private static final int workload = 12;

	/**
	 * This method can be used to generate a string representing an account password
	 * suitable for storing in a database. It will be an OpenBSD-style crypt(3) formatted
	 * hash string of length=60
	 * The bcrypt workload is specified in the above static variable, a value from 10 to 31.
	 * A workload of 12 is a very reasonable safe default as of 2013.
	 * This automatically handles secure 128-bit salt generation and storage within the hash.
	 *
	 * @param plaintext The account's plaintext password as provided during account creation,
	 *                  or when changing an account's password.
	 * @return String - a string of length 60 that is the bcrypt hashed password in crypt(3) format.
	 */
	public static String hashPassword(String plaintext) {
		String salt = BCrypt.gensalt(workload);
		return BCrypt.hashpw(plaintext, salt);
	}

	/**
	 * This method can be used to verify a computed hash from a plaintext (e.g. during a login
	 * request) with that of a stored hash from a database. The password hash from the database
	 * must be passed as the second variable.
	 *
	 * @param plaintext  The account's plaintext password, as provided during a login request
	 * @param storedHash The account's stored password hash, retrieved from the authorization database
	 * @return boolean - true if the password matches the password of the stored hash, false otherwise
	 */
	public static boolean checkPassword(String plaintext, String storedHash) {
		boolean password_verified;
		if (null == storedHash || !storedHash.startsWith("$2a$"))
			throw new IllegalArgumentException("Invalid hash provided for comparison");
		password_verified = BCrypt.checkpw(plaintext, storedHash);
		return (password_verified);
	}

}
package cn.br.common.validator;

import cn.br.common.exception.ValidatorException;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResult {

	private boolean valid;
	private String message;
	private String code;

	public static ValidationResult ok() {
		return new ValidationResult(true, null, null);
	}

	public static ValidationResult ok(String code) {
		return new ValidationResult(true, null, code);
	}

	public static ValidationResult fail(String message) {
		return new ValidationResult(false, message, null);
	}

	public static ValidationResult fail(String code, String message) {
		return new ValidationResult(false, message, code);
	}

	public void throwIfInvalid() {
		this.throwMessage(getMessage());
	}

	public void throwIfInvalid(String fieldName) {
		if (!isValid())
			throw new ValidatorException(fieldName + " " + getMessage());
	}

	public void throwMessage(String msg) {
		if (!isValid())
			throw new ValidatorException(msg);
	}

}
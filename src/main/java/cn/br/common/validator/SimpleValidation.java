package cn.br.common.validator;

import lombok.AllArgsConstructor;

import java.util.function.Predicate;

@AllArgsConstructor
public class SimpleValidation<T> implements Validation<T> {

	private Predicate<T> predicate;
	private String onErrorMessage;

	public static <T> SimpleValidation<T> from(Predicate<T> predicate, String onErrorMessage) {
		return new SimpleValidation<>(predicate, onErrorMessage);
	}

	@Override
	public ValidationResult test(T param) {
		return predicate.test(param) ? ValidationResult.ok() : ValidationResult.fail(onErrorMessage);
	}

}
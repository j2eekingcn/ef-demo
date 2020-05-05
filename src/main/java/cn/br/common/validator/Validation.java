package cn.br.common.validator;

@FunctionalInterface
public interface Validation<T> {

    ValidationResult test(T param);

    default Validation<T> and(Validation<T> other) {
        return param -> {
            ValidationResult firstResult = this.test(param);
            return !firstResult.isValid() ? firstResult : other.test(param);
        };
    }

    default Validation<T> or(Validation<T> other) {
        return (param) -> {
            ValidationResult firstResult = this.test(param);
            return firstResult.isValid() ? firstResult : other.test(param);
        };
    }

}
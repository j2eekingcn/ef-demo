package cn.br.common.exception;

import lombok.Getter;

/**
 * Java Bean Validator Exception
 *
 * @author biezhi
 * @date 2018/4/21
 */
@SuppressWarnings("serial")
public class ValidatorException extends RuntimeException {

    @Getter
    private Integer code;

    public ValidatorException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ValidatorException(String message) {
        super(message);
    }

}

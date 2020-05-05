package cn.br.common.event;

import java.lang.annotation.*;

/**
 * Order By <br/>
 * 
 * 优先级
 * @author ZJL
 * @since 1.6.6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {

    int value() default Integer.MAX_VALUE;

}
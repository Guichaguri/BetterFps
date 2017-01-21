package guichaguri.betterfps.transformers.patcher.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines which methods and fields will be copied into the target class
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Copy {

    boolean replace() default false;

}

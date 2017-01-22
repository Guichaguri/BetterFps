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

    Mode value() default Mode.COPY;

    enum Mode {
        COPY, // Copies if not exists
        REPLACE,
        APPEND, // Only for methods
        PREPEND // Only for methods
    }

}

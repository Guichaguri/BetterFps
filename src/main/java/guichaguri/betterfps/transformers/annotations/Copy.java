package guichaguri.betterfps.transformers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines which methods and fields must be copied into the target class
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Copy {

    /**
     * The mode which the field or method should be copied
     */
    Mode value() default Mode.COPY;

    enum Mode {

        /**
         * Copies if not exists
         */
        COPY,

        /**
         * Replaces if exists, copies otherwise
         * If the method calls itself with `super`, the call will be replaced with the code of the original method
         */
        REPLACE,

        /**
         * Appends the code to the overridden method
         * Only for methods
         */
        APPEND,

        /**
         * Prepends the code to the overridden method
         * Only for methods
         */
        PREPEND

    }

}

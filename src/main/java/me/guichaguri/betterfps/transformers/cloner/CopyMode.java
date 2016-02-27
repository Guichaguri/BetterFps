package me.guichaguri.betterfps.transformers.cloner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CopyMode {

    Mode value(); // Mode that the object will be copied, not needed if you'll use REPLACE

    enum Mode {
        REPLACE, ADD_IF_NOT_EXISTS, IGNORE,
        PREPEND, APPEND // APPEND & PREPEND can only be used in methods
    }
}

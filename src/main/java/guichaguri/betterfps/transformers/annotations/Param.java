package guichaguri.betterfps.transformers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines which local variable this parameter will receive from the patched method
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     * The local variable index
     */
    int value();

}

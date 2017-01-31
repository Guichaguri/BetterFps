package guichaguri.betterfps.transformers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a config condition
 * @see guichaguri.betterfps.transformers.Conditions
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface Condition {

    /**
     * Condition identifier
     */
    String value();

}

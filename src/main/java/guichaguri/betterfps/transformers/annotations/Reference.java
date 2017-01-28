package guichaguri.betterfps.transformers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a reference name to the method or field, so it can be referred by the custom patcher
 * @see Patcher
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Reference {

    /**
     * The reference name
     */
    String value();

}

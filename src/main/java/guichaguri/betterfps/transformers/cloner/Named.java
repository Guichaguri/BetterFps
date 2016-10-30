package guichaguri.betterfps.transformers.cloner;

import guichaguri.betterfps.tweaker.Mappings;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Named {

    Mappings value(); // Original name of the method/field

}

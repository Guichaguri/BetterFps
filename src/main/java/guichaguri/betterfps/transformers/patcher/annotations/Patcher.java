package guichaguri.betterfps.transformers.patcher.annotations;

import guichaguri.betterfps.transformers.patcher.IClassPatcher;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a custom class patcher.
 * @see IClassPatcher
 * @author Guilherme Chaguri
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Patcher {

    Class<? extends IClassPatcher> value();

}

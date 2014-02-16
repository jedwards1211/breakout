package org.andork.codegen.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells {@link BuilderGenerator} to lazy-initialize a list type field.
 * 
 * @author james.a.edwards
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BuilderLazyInitialize {

}

package org.andork.codegen.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells {@link BuilderGenerator} to ignore a field. The generated builder will
 * not contain a setter for that field.
 * 
 * @author james.a.edwards
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BuilderIgnore {

}

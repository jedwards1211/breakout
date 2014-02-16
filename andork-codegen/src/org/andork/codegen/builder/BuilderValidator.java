package org.andork.codegen.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells {@link BuilderGenerator} that the annotated method is a validator -- it
 * should be called by the generated builder's {@code create()} method to
 * validate the values of the fields.
 * 
 * @author james.a.edwards
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface BuilderValidator {

}

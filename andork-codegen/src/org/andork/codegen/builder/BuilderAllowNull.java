package org.andork.codegen.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells {@link BuilderGenerator} that an annotated field or all fields
 * of an annotated class may be null, and the generated builder's
 * {@code create()} method should not throw an exception if they are null.
 * 
 * @author james.a.edwards
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface BuilderAllowNull {

}

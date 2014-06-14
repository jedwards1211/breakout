package org.andork.codegen.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Tells {@link BuilderGenerator} what to call an element of the annotated
 * {@link List} type field in comments and variable names. Without this
 * annotation, {@link BuilderGenerator} will try to singularize the field name,
 * but this often causes problems. For example:
 * 
 * <pre>
 * private List&lt;ExpirationType&gt;	expirationTypes;
 * </pre>
 * 
 * will produce:
 * 
 * <pre>
 * public Builder addExpirationTyp(ExpirationType expirationTyp) {
 * 	result.expirationTypes.add(expirationTyp);
 * 	return this;
 * }
 * </pre>
 * 
 * Whereas<br>
 * <br>
 * 
 * <pre>
 * &#064;BuilderElementName(&quot;expirationType&quot;)
 * private List&lt;ExpirationType&gt;	expirationTypes;
 * </pre>
 * 
 * will produce:
 * 
 * <pre>
 * public Builder addExpirationType(ExpirationType expirationType) {
 * 	result.expirationTypes.add(expirationType);
 * 	return this;
 * }
 * </pre>
 * 
 * @author andy.edwards
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface BuilderElementName {
	String singular();

	String plural();
}

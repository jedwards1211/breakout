package org.andork.spec;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.andork.spec.SpecObject.Attribute;


/**
 * Base class for {@link SpecObject} format specifications. To create a
 * specification for a new type, simply create a subclass that calls one of the
 * constructors with the appropriate {@link Attribute}s and child specs.
 * 
 * @author james.a.edwards
 */
public abstract class Spec {
	private final String					name;
	private final Map<String, Attribute<?>>	attributes	= new HashMap<String, Attribute<?>>();
	private final Attribute<?>				contentAttribute;
	private final Map<String, Spec>			childSpecs	= new HashMap<String, Spec>();

	/**
	 * Creates a Spec the given name and attributes.
	 * 
	 * @param name
	 *            the name of the spec.
	 * @param attributes
	 *            the attributes.
	 */
	protected Spec(String name, Attribute<?>[] attributes) {
		this(name, attributes, (Attribute<?>) null);
	}

	/**
	 * Creates a Spec with the given name and attributes.
	 * 
	 * @param name
	 *            the name of the spec.
	 * @param attributes
	 *            the attributes.
	 * @param contentAttribute
	 *            if not {@code null}, the XML node content will be stored for
	 *            this attribute in the {@link SpecObject}.
	 */
	protected Spec(String name, Attribute<?>[] attributes, Attribute<?> contentAttribute) {
		this.name = name;

		if (attributes != null) {
			for (Attribute<?> Attribute : attributes) {
				this.attributes.put(Attribute.getName(), Attribute);
			}
		}
		this.contentAttribute = contentAttribute;
	}

	/**
	 * Creates a Spec with the given name, attributes, and child specs.
	 * 
	 * @param name
	 *            the name of the spec.
	 * @param attributes
	 *            the attributes.
	 * @param childSpecs
	 *            the Specs for child {@link SpecObject}s.
	 */
	protected Spec(String name, Attribute<?>[] attributes, Spec[] childSpecs) {
		this(name, attributes, (Attribute<?>) null);
		if (childSpecs != null) {
			for (Spec spec : childSpecs) {
				this.childSpecs.put(spec.name, spec);
			}
		}
	}

	/**
	 * Gets the name for {@link SpecObject}s with this spec. When a
	 * {@link SpecObject} is converted to XML, the XML node will be given the
	 * name of the message's spec. For example, use "OrdStatus" for an
	 * "&lt;OrdStatus ..." message.
	 * 
	 * @return the spec name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the {@link Attribute} with the given name.
	 * 
	 * @param name
	 *            the name of the attribute to get.
	 * @return the {@code Attribute} with the given name, or {@code null} if
	 *         none is part of this spec.
	 */
	public Attribute<?> getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Gets all {@link Attribute}s in this spec.
	 * 
	 * @return an unmodifiable {@link Collection} of {@code Attribute}s.
	 */
	public Collection<Attribute<?>> getAttributes() {
		return Collections.unmodifiableCollection(attributes.values());
	}

	/**
	 * Gets all content {@link Attribute} of this spec. When a
	 * {@link SpecObject} is converted to XML, if its spec has a content
	 * attribute, the value of the content attribute is used as the XML node's
	 * text content.
	 * 
	 * @return the content attribute, if this spec has one, or {@code null}
	 *         otherwise.
	 */
	public Attribute<?> getContentAttribute() {
		return contentAttribute;
	}

	/**
	 * Gets the child spec with the given name.
	 * 
	 * @param name
	 *            the name of the child spec to get.
	 * @return the child spec with the given name, if this spec has one, or
	 *         {@code null} otherwise.
	 */
	public Spec getChildSpec(String name) {
		return childSpecs.get(name);
	}

	/**
	 * Gets the child specs.
	 * 
	 * @return an unmodifiable {@link Collection} of child specs.
	 */
	public Collection<Spec> getChildSpecs() {
		return Collections.unmodifiableCollection(childSpecs.values());
	}
}

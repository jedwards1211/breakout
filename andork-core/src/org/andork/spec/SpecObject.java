package org.andork.spec;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A type-safe property set that can be converted to XML via {@link #toString()}
 * and converted from XML via {@link #parseXml(String)}.<br>
 * <br>
 * All types of XML may be parsed into an instance of {@code SpecObject}; they
 * will simply have different {@link Spec}s and {@link Attribute}s.<br>
 * <br>
 * {@code SpecObject} cannot be constructed directly; it must be created via
 * {@link #parseXml(String)} or {@link Builder#build()}.
 * 
 * @param <S>
 *            the type of the {@link Spec} for this {@code SpecObject}.
 * 
 * @author james.a.edwards
 */
@SuppressWarnings("serial")
public class SpecObject<S extends Spec> {
	private final S							spec;
	private final Map<Attribute<?>, Object>	attributes	= new HashMap<Attribute<?>, Object>();
	private final List<SpecObject<?>>		children	= new ArrayList<SpecObject<?>>();

	private static final Logger				LOGGER		= Logger.getLogger("schwab");

	/**
	 * Creates a {@code SpecObject} with the given spec.
	 * 
	 * @param spec
	 *            the {@link Spec} for this SpecObject.
	 */
	private SpecObject(S spec) {
		this.spec = spec;
	}

	/**
	 * @return the {@link Spec} of this SpecObject.
	 */
	public S getSpec() {
		return spec;
	}

	/**
	 * Gets the value of an attribute.<br>
	 * <br>
	 * To see the list of attributes this SpecObject has, use
	 * {@link Spec#getAttributes() getSpec().getAttributes()}.
	 * 
	 * @param Attribute
	 *            the attribute to get the value of.
	 * @return the value of the attribute (may be {@code null}).
	 */
	public <T> T get(Attribute<T> Attribute) {
		return (T) attributes.get(Attribute);
	}

	public boolean has(Attribute<?> attribute) {
		return attributes.containsKey(attribute);
	}

	/**
	 * @return the number of children.
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * Gets a child SpecObject (corresponding to a child XML node).
	 * 
	 * @param index
	 *            the index of the child to get.
	 * @return the child at {@code index}.
	 */
	public SpecObject<?> getChild(int index) {
		return children.get(index);
	}

	/**
	 * Gets all child SpecObjects with the given {@link Spec}.
	 * 
	 * @param childSpec
	 *            the {@link Spec} of children to get.
	 * @return all children whose {@link #getSpec() spec} is {@code childSpec}.
	 */
	public <T extends Spec> List<SpecObject<T>> getChildren(T childSpec) {
		List<SpecObject<T>> result = new ArrayList<SpecObject<T>>();
		for (SpecObject<?> child : children) {
			if (child.spec == childSpec) {
				result.add((SpecObject<T>) child);
			}
		}
		return result;
	}

	/**
	 * Converts into XML.
	 */
	public String toXml() {
		Collection<Attribute<?>> specAttributes = spec.getAttributes();
		StringBuffer sb = new StringBuffer();
		sb.append('<').append(spec.getName());
		for (Attribute Attribute : specAttributes) {
			Object value = attributes.get(Attribute);
			if (value != null && Attribute != spec.getContentAttribute()) {
				sb.append(' ').append(Attribute.name).append("=\"").append(Attribute.format.format(value)).append('"');
			}
		}
		sb.append('>');
		if (children.isEmpty()) {
			if (spec.getContentAttribute() != null) {
				Object value = attributes.get(spec.getContentAttribute());
				if (value != null) {
					sb.append(((Attribute) spec.getContentAttribute()).format.format(value));
				}
			}
		} else {
			for (SpecObject<?> child : children) {
				sb.append(child.toXml());
			}
		}
		sb.append("</").append(spec.getName()).append('>');

		return sb.toString();
	}

	/**
	 * Parses XML into a {@link SpecObject}.
	 * 
	 * @param xml
	 *            the XML to parse.
	 * @return a {@link SpecObject} parsed from {@code xml}.
	 * @throws Exception
	 *             if the XML type is unrecognized or has format errors.
	 */
	public static <S extends Spec> SpecObject<S> parseXml(String xml, S spec) throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(false);
		docBuilderFactory.setExpandEntityReferences(false);
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

		Element element = doc.getDocumentElement();

		return parseXml(spec, element);
	}

	public static SpecObject<?> parseXml(String xml, Map<String, Spec> specs) throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(false);
		docBuilderFactory.setExpandEntityReferences(false);
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

		Element element = doc.getDocumentElement();

		Spec spec = specs.get(element.getTagName());

		if (spec == null) {
			throw new IllegalArgumentException("Unknown XML type: " + element.getNodeName());
		}

		return parseXml(spec, element);
	}

	public static <S extends Spec> SpecObject<S> parseXml(S spec, Element element) throws Exception {
		if (!element.getNodeName().equals(spec.getName())) {
			throw new NonrecoverableParseException("element name: " + element.getNodeName() + " does not match required: " + spec.getName());
		}

		SpecObject<S> result = new SpecObject<S>(spec);

		NamedNodeMap nodeMap = element.getAttributes();
		if (nodeMap != null) {
			for (int i = 0; i < nodeMap.getLength(); i++) {
				Node attr = nodeMap.item(i);
				String name = attr.getNodeName();
				String value = attr.getNodeValue();
				if (value == null || value.length() == 0) {
					continue;
				}
				Attribute<?> attribute = spec.getAttribute(name);
				if (attribute != null) {
					try {
						result.attributes.put(attribute, attribute.format.parse(value));
					} catch (RecoverableParseException ex) {
						LOGGER.warning("Failed to parse value \"" + value + "\" for attribute \"" + name + "\" in \"" + spec.getName() + "\" message.");
						result.attributes.put(attribute, ex.getRecoveryValue());
					}
				}
				else {
					LOGGER.warning("Attribute \"" + name + "\" not found in \"" + spec.getName() + "\" message spec.");
				}
			}
		}

		if (!spec.getChildSpecs().isEmpty()) {
			NodeList children = element.getChildNodes();
			if (children != null) {
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child instanceof Element) {
						Element childElement = (Element) child;
						String tag = childElement.getNodeName();
						Spec childSpec = spec.getChildSpec(tag);
						if (childSpec != null) {
							result.children.add(parseXml(childSpec, childElement));
						}
						else {
							LOGGER.warning("Child spec \"" + tag + "\" not found in \"" + spec.getName() + "\" message spec.");
						}
					}
				}
			}
		} else if (spec.getContentAttribute() != null) {
			Attribute<?> contentAttribute = spec.getContentAttribute();
			if (contentAttribute != null) {
				String value = element.getTextContent();
				if (value != null) {
					try {
						result.attributes.put(contentAttribute, contentAttribute.format.parse(value));
					} catch (RecoverableParseException ex) {
						LOGGER.warning("Failed to parse text content \"" + value + "\" of \"" + spec.getName() + "\" message");
						if (ex.getRecoveryValue() != null) {
							result.attributes.put(contentAttribute, ex.getRecoveryValue());
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Creates a {@link SpecObject} builder for the given spec.
	 * 
	 * @param spec
	 *            the {@link Spec} of the {@link SpecObject} to build.
	 * @return a {@link Builder} for {@code spec}.
	 */
	public static <S extends Spec> Builder<S> newBuilder(S spec) {
		return new Builder<S>(spec);
	}

	/**
	 * Converts an attribute value to and from {@link String} for XML.
	 * 
	 * @author james.a.edwards
	 * 
	 * @param <T>
	 *            the type of the attribute value.
	 */
	public static interface Format<T> {
		public String format(T t);

		public T parse(String s) throws Exception;
	}

	/**
	 * Defines an attribute that can be parsed by
	 * {@link SpecObject#parseXml(String)}.
	 * 
	 * @author james.a.edwards
	 * 
	 * @param <T>
	 *            the type of the attribute value.
	 */
	public static class Attribute<T> {
		private final String	name;
		private final Format<T>	format;

		/**
		 * Creates an attribute with the given name and format.
		 * 
		 * @param name
		 *            the name used in XML.
		 * @param format
		 *            the format for converting from the attribute value to and
		 *            from a {@code String} for XML.
		 */
		public Attribute(String name, Format<T> format) {
			super();
			this.name = name;
			this.format = format;
		}

		/**
		 * Gets the attribute name used in XML.
		 * 
		 * @return the attribute name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the format for converting from the attribute value to and from a
		 * {@code String} for XML.
		 * 
		 * @return the format.
		 */
		public Format<T> getFormat() {
			return format;
		}
	}

	/**
	 * Builds a SpecObject. To get a builder, call
	 * {@link SpecObject#newBuilder(Spec)}.<br>
	 * <br>
	 * Example:<br>
	 * 
	 * <pre>
	 * osrb = SpecObject.newBuilder(OrdStatusXmlSpec.INSTANCE);
	 * osrb.set(SpecObject.ACCOUNT_NUMBER, &quot;90322569&quot;).set(OrdStatusXmlSpec.ORDER_TRACKING_ID, &quot;18433&quot;);
	 * System.out.println(osrb.build());
	 * // prints: &lt;OrdStatus Acct=&quot;90322569&quot; _OrdTrkId=&quot;18433&quot;&gt;&lt;/OrdStatus&gt;
	 * </pre>
	 */
	public static class Builder<S extends Spec> {
		private SpecObject<S>	result;

		private Builder(S spec) {
			result = new SpecObject<S>(spec);
		}

		/**
		 * Sets an attribute of the SpecObject being built.
		 * 
		 * @param attribute
		 *            the attribute to set.
		 * @param value
		 *            the new value for the attribute.
		 * @return this {@code Builder}.
		 */
		public <T> Builder<S> set(Attribute<T> attribute, T value) {
			result.attributes.put(attribute, value);
			return this;
		}

		/**
		 * Adds a child to the SpecObject being built.
		 * 
		 * @param childd
		 *            the child to add.
		 * @return this {@code Builder}.
		 * @throws IllegalArgumentException
		 *             if {@code child}'s {@link Spec spec} is not one of the
		 *             built SpecObject's allowed child specs.
		 */
		public Builder<S> addChild(SpecObject<?> child) {
			if (!result.spec.getChildSpecs().contains(child.spec)) {
				throw new IllegalArgumentException("Spec: " + child.spec.getName() + " cannot be a child of " + result.spec.getName());
			}
			result.children.add(child);
			return this;
		}

		/**
		 * Creates an immutable SpecObject from the stored temporary values.
		 * After this method is called none of this {@code Builder}'s methods
		 * will work anymore; you may create one SpecObject per {@code Builder}.
		 * 
		 * @return the {@link SpecObject} that was built.
		 */
		public SpecObject<S> build() {
			SpecObject<S> result = this.result;
			this.result = null;
			return result;
		}
	}

	public static class StringFormat implements Format<String> {
		public String format(String t) {
			return t;
		}

		public String parse(String s) {
			return s;
		}
	}

	public static class IntegerFormat implements Format<Integer> {
		public String format(Integer t) {
			return String.valueOf(t);
		}

		public Integer parse(String s) throws RecoverableParseException {
			try {
				return Integer.valueOf(s);
			} catch (Exception e) {
				throw new RecoverableParseException(e, null);
			}
		}
	}

	public static class LongFormat implements Format<Long> {
		public String format(Long t) {
			return String.valueOf(t);
		}

		public Long parse(String s) throws RecoverableParseException {
			try {
				return Long.valueOf(s);
			} catch (Exception e) {
				throw new RecoverableParseException(e, null);
			}
		}
	}

	public static class FloatFormat implements Format<Float> {
		public String format(Float t) {
			return String.valueOf(t);
		}

		public Float parse(String s) throws RecoverableParseException {
			try {
				return Float.valueOf(s);
			} catch (Exception e) {
				throw new RecoverableParseException(e, null);
			}
		}
	}

	public static class DoubleFormat implements Format<Double> {
		public String format(Double t) {
			return String.valueOf(t);
		}

		public Double parse(String s) throws RecoverableParseException {
			try {
				return Double.valueOf(s);
			} catch (Exception e) {
				throw new RecoverableParseException(e, null);
			}
		}
	}

	public static class BigDecimalFormat implements Format<BigDecimal> {
		public String format(BigDecimal t) {
			return String.valueOf(t);
		}

		public BigDecimal parse(String s) throws RecoverableParseException {
			try {
				return new BigDecimal(s);
			} catch (Exception e) {
				throw new RecoverableParseException(e, null);
			}
		}
	}

	public static class RecoverableParseException extends Exception {
		private final boolean	includeRecoveryValueEvenIfNull	= true;
		private final Object	recoveryValue;

		public RecoverableParseException(Object recoveryValue) {
			super();
			this.recoveryValue = recoveryValue;
		}

		public RecoverableParseException(String message, Object recoveryValue) {
			super(message);
			this.recoveryValue = recoveryValue;
		}

		public RecoverableParseException(Throwable cause, Object recoveryValue) {
			super(cause);
			this.recoveryValue = recoveryValue;
		}

		public RecoverableParseException(String message, Throwable cause, Object recoveryValue) {
			super(message, cause);
			this.recoveryValue = recoveryValue;
		}

		public boolean isIncludeRecoveryValueEvenIfNull() {
			return includeRecoveryValueEvenIfNull;
		}

		public Object getRecoveryValue() {
			return recoveryValue;
		}
	}

	public static class NonrecoverableParseException extends Exception {

		public NonrecoverableParseException() {
			super();
		}

		public NonrecoverableParseException(String message, Throwable cause) {
			super(message, cause);
		}

		public NonrecoverableParseException(String message) {
			super(message);
		}

		public NonrecoverableParseException(Throwable cause) {
			super(cause);
		}
	}

}

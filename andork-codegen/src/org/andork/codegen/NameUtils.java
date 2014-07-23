package org.andork.codegen;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.andork.codegen.builder.BuilderElementName;


public class NameUtils {
	private NameUtils() {

	}

	public static String cap(String s) {
		StringBuffer sb = new StringBuffer(s);
		if (sb.length() > 0) {
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		}
		return sb.toString();
	}

	public static String a(String s) {
		char c = s.charAt(0);
		if (!Character.isLetter(c)) {
			c = s.charAt(s.indexOf(' ') + 1);
		}
		if (isVowel(c)) {
			return "an " + s;
		} else {
			return "a " + s;
		}
	}

	public static boolean isVowel(char c) {
		switch (c) {
		case 'a':
		case 'A':
		case 'e':
		case 'E':
		case 'i':
		case 'I':
		case 'o':
		case 'O':
		case 'u':
		case 'U':
			return true;
		default:
			return false;
		}
	}

	public static String singularize(String s) {
		if (s.endsWith("es")) {
			return s.substring(0, s.length() - 2);
		} else if (s.endsWith("s")) {
			return s.substring(0, s.length() - 1);
		}
		return s;
	}

	public static String getElementSingularName(Field parameterizedField) {
		String fieldName = parameterizedField.getName();
		String elemName = singularize(fieldName);
		BuilderElementName elemNameAnnotation = parameterizedField.getAnnotation(BuilderElementName.class);
		if (elemNameAnnotation != null) {
			elemName = elemNameAnnotation.singular();
		}
		return elemName;
	}

	public static String getElementPluralName(Field parameterizedField) {
		String fieldName = parameterizedField.getName();
		String elemName = fieldName;
		BuilderElementName elemNameAnnotation = parameterizedField.getAnnotation(BuilderElementName.class);
		if (elemNameAnnotation != null) {
			elemName = elemNameAnnotation.plural();
		}
		return elemName;
	}

	public static String constantify(String name) {
		StringBuffer sb = new StringBuffer();
		char prev = '\0';
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (i > 0 && Character.isUpperCase(c) && Character.isLetter(prev) && !Character.isUpperCase(prev)) {
				sb.append('_');
			}
			sb.append(Character.toUpperCase(c));
			prev = c;
		}
		return sb.toString();
	}

}

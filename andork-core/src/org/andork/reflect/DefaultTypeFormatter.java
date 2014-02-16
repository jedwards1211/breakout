package org.andork.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class DefaultTypeFormatter implements TypeFormatter {
	@Override
	public String format(Type t) {
		if (t instanceof WildcardType) {
			WildcardType wt = (WildcardType) t;
			StringBuffer sb = new StringBuffer("?");
			Type[] lowerBounds = wt.getLowerBounds();
			Type[] upperBounds = wt.getUpperBounds();

			if (lowerBounds != null && lowerBounds.length > 0) {
				for (int i = 0; i < lowerBounds.length; i++) {
					if (lowerBounds[i] != Object.class) {
						sb.append(" super ").append(format(lowerBounds[i]));
					}
				}
			}
			if (upperBounds != null && upperBounds.length > 0) {
				for (int i = 0; i < upperBounds.length; i++) {
					if (upperBounds[i] != Object.class) {
						sb.append(" extends ").append(format(upperBounds[i]));
					}
				}
			}
			return sb.toString();
		} else if (t instanceof TypeVariable<?>) {
			return ((TypeVariable<?>) t).getName();
		} else if (t instanceof GenericArrayType) {
			return format(((GenericArrayType) t).getGenericComponentType()) + "[]";
		} else if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			StringBuffer sb = new StringBuffer();
			sb.append(format(pt.getRawType()));
			sb.append('<');
			for (int i = 0; i < pt.getActualTypeArguments().length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(format(pt.getActualTypeArguments()[i]));
			}
			sb.append('>');
			return sb.toString();
		} else if (t instanceof Class<?>) {
			Class<?> c = (Class<?>) t;
			if (c.isArray()) {
				return format(c.getComponentType()) + "[]";
			}
			if (c.getEnclosingClass() != null) {
				return format(c.getEnclosingClass()) + "." + c.getSimpleName();
			}
			return c.getName();
		}
		return t.toString();
	}
}

/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import org.andork.collect.CollectionUtils;

public class ObjectTraverserTest {

	public static class TestVisitor extends ObjectTraverser.DefaultVisitorBase {
		String tabs = "";

		@Override
		public void exitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index) {
			tabs = tabs.substring(0, tabs.length() - 4);
			super.exitObject(traverser, obj, parent, parentField, index);
		}

		private String getDescription(Object obj, Object parent, Field parentField, int index) {
			StringBuffer sb = new StringBuffer();

			boolean isObject = parentField == null || !parentField.getType().isPrimitive();

			Class<?> type = null;
			if (obj == null) {
				if (parentField != null) {
					type = parentField.getType();
				}
				if (index >= 0) {
					type = type.getComponentType();
				}
			} else if (parentField != null && parentField.getType().isPrimitive()) {
				type = parentField.getType();
			} else {
				type = obj.getClass();
			}

			if (type != null) {
				if (index >= 0) {
					sb.append(parentField.getType().getComponentType().getSimpleName());
				} else {
					sb.append(type.getSimpleName());
				}
			}

			if (parentField != null) {
				sb.append(' ').append(parentField.getName());
			}

			if (index >= 0) {
				sb.append('[').append(index).append(']');
			}

			if (obj != null && isObject) {
				sb.append(" (").append(String.format("%x", System.identityHashCode(obj))).append(')');
			}
			String s;
			if (obj != null && type.isArray()) {
				s = toString(obj, 100);
			} else {
				s = String.valueOf(obj);
				if (s.length() > 100) {
					s = s.substring(0, 100) + "...";
				}
			}
			s.replaceAll("\n", "\\n");
			sb.append(" = ").append(s);

			return sb.toString();
		}

		private String toString(Object array, int maxLength) {
			StringBuffer sb = new StringBuffer("[");
			for (int i = 0; i < Array.getLength(array); i++) {
				String nextChunk = "";

				if (i > 0) {
					nextChunk += ", ";
				}
				nextChunk += Array.get(array, i);

				if (i < Array.getLength(array) - 1 && sb.length() + nextChunk.length() > maxLength - 6) {
					sb.append(", ...");
					break;
				} else {
					sb.append(nextChunk);
				}
			}
			sb.append(']');
			return sb.toString();
		}

		@Override
		public boolean visitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index) {
			if (index < 0 || obj != null) {
				System.out.println(tabs + getDescription(obj, parent, parentField, index));
			}

			boolean result = super.visitObject(traverser, obj, parent, parentField, index);
			if (result) {
				tabs += "|   ";
			}
			return result;
		}

		@Override
		public void visitPrimitive(ObjectTraverser traverser, Object prim, Object parent, Field parentField) {
			System.out.println(tabs + getDescription(prim, parent, parentField, -1));
			super.visitPrimitive(traverser, prim, parent, parentField);
		}
	}

	public static void main(String[] args) {
		Map<Date, Map<Double, String>> optionHash = CollectionUtils.newHashMap();

		Map<Double, String> strikeMap = CollectionUtils.newHashMap();

		strikeMap.put(750.0, "GOOG 01/18/2014 750.00 C");

		optionHash.put(new Date(2014 - 1900, 0, 18), strikeMap);

		try {
			new ObjectTraverser().traverse(optionHash, new TestVisitor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

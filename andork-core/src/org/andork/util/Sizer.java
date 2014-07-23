/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Sizer implements ObjectTraverser.Visitor {
	private static class Instance {
		final Object	obj;

		protected Instance(Object obj) {
			super();
			this.obj = obj;
		}

		public boolean equals(Object o) {
			if (o instanceof Instance) {
				return this.obj == ((Instance) o).obj;
			}
			return false;
		}

		public int hashCode() {
			return System.identityHashCode(obj);
		}
	}

	protected Map<Class<?>, Integer>	typeCounts		= null;
	protected Map<Field, Integer>		fieldCounts		= null;

	protected Set<Instance>				instances		= new HashSet<Instance>();

	protected int						referenceSize	= 32;
	protected int						booleanSize		= 32;
	protected int						byteSize		= 32;
	protected int						shortSize		= 32;
	protected int						charSize		= 32;
	protected int						intSize			= 32;
	protected int						floatSize		= 32;
	protected int						longSize		= 64;
	protected int						doubleSize		= 64;

	protected int						referenceCount	= 0;
	protected int						booleanCount	= 0;
	protected int						byteCount		= 0;
	protected int						shortCount		= 0;
	protected int						charCount		= 0;
	protected int						intCount		= 0;
	protected int						floatCount		= 0;
	protected int						longCount		= 0;
	protected int						doubleCount		= 0;

	protected boolean					sizingActive	= true;

	protected long						size			= 0;

	public long sizeOf(Object obj) {
		reset();
		try {
			new ObjectTraverser().traverse(obj, this);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return size;
	}

	public Sizer enableTypeCounts() {
		typeCounts = new HashMap<Class<?>, Integer>();
		return this;
	}

	public Sizer enableFieldCounts() {
		fieldCounts = new HashMap<Field, Integer>();
		return this;
	}

	public Map<Class<?>, Integer> getTypeCounts() {
		return Collections.unmodifiableMap(typeCounts);
	}

	public Map<Field, Integer> getFieldCounts() {
		return Collections.unmodifiableMap(fieldCounts);
	}

	private void reset() {
		instances.clear();
		if (typeCounts != null) {
			typeCounts.clear();
		}
		if (fieldCounts != null) {
			fieldCounts.clear();
		}
		referenceCount = 0;
		booleanCount = 0;
		byteCount = 0;
		shortCount = 0;
		charCount = 0;
		intCount = 0;
		floatCount = 0;
		longCount = 0;
		doubleCount = 0;
		size = 0;
	}

	public boolean visitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index) {
		if (parentField != null && Modifier.isStatic(parentField.getModifiers())) {
			return false;
		}

		if (sizingActive && parent != null) {
			referenceCount++;
			size += referenceSize;
		}

		if (obj != null && instances.add(new Instance(obj))) {
			Class<?> oclass = obj.getClass();

			if (sizingActive && typeCounts != null) {
				Integer count = typeCounts.get(oclass);
				if (count == null) {
					count = 0;
				}
				typeCounts.put(oclass, count + 1);
			}

			if (sizingActive && oclass.isArray() && oclass.getComponentType().isPrimitive()) {
				int length = Array.getLength(obj);

				if (oclass.equals(boolean[].class)) {
					booleanCount += length;
					size += booleanSize * length;
				} else if (oclass.equals(byte[].class)) {
					byteCount += length;
					size += byteSize * length;
				} else if (oclass.equals(short[].class)) {
					shortCount += length;
					size += shortSize * length;
				} else if (oclass.equals(char[].class)) {
					charCount += length;
					size += charSize * length;
				} else if (oclass.equals(int[].class)) {
					intCount += length;
					size += intSize;
				} else if (oclass.equals(float[].class)) {
					floatCount += length;
					size += floatSize * length;
				} else if (oclass.equals(long[].class)) {
					longCount += length;
					size += longSize * length;
				} else if (oclass.equals(double[].class)) {
					doubleCount += length;
					size += doubleSize * length;
				}
			}

			return true;
		}
		return false;
	}

	public void exitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index) {

	}

	public void visitPrimitive(ObjectTraverser traverser, Object prim, Object parent, Field parentField) {
		if (Modifier.isStatic(parentField.getModifiers())) {
			return;
		}

		if (!sizingActive) {
			return;
		}

		if (typeCounts != null) {
			Integer count = typeCounts.get(parentField.getType());
			if (count == null) {
				count = 0;
			}
			typeCounts.put(parentField.getType(), count + 1);
		}

		if (prim instanceof Boolean) {
			booleanCount++;
			size += booleanSize;
		} else if (prim instanceof Byte) {
			byteCount++;
			size += byteSize;
		} else if (prim instanceof Short) {
			shortCount++;
			size += shortSize;
		} else if (prim instanceof Character) {
			charCount++;
			size += charSize;
		} else if (prim instanceof Integer) {
			intCount++;
			size += intSize;
		} else if (prim instanceof Float) {
			floatCount++;
			size += floatSize;
		} else if (prim instanceof Long) {
			longCount++;
			size += longSize;
		} else if (prim instanceof Double) {
			doubleCount++;
			size += doubleSize;
		}
	}
}

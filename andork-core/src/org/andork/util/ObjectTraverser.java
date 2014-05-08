package org.andork.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class ObjectTraverser {
	public static interface Visitor {
		public boolean visitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index);

		public void exitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index);

		public void visitPrimitive(ObjectTraverser traverser, Object prim, Object parent, Field parentField);
	}

	public static class DefaultVisitorBase implements Visitor {
		int				depth		= 0;
		int				maxDepth	= 10000;

		Set<Instance>	instances	= new HashSet<Instance>();

		public boolean visitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index) {
			if (depth == maxDepth) {
				return false;
			}

			if (obj != null && instances.add(new Instance(obj))) {
				depth++;
				return true;
			}
			
			return false;
		}

		public void exitObject(ObjectTraverser traverser, Object obj, Object parent, Field parentField, int index) {
			depth--;
		}

		public void visitPrimitive(ObjectTraverser traverser, Object prim, Object parent, Field parentField) {
		}
	}

	public static final class Instance {
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

	public void traverse(Object obj, Visitor visitor) throws Exception {
		traverse(obj, null, null, visitor, -1);
	}

	private void traverse(Object obj, Object parent, Field parentField, Visitor visitor, int index) throws Exception {
		if (visitor.visitObject(this, obj, parent, parentField, index) && obj != null && (parentField == null || !parentField.getType().isPrimitive())) {
			Class<?> oclass = obj.getClass();

			if (oclass.isArray()) {
				if (!oclass.getComponentType().isPrimitive()) {
					int length = Array.getLength(obj);
					for (int i = 0; i < length; i++) {
						traverse(Array.get(obj, i), parent, parentField, visitor, i);
					}
				}
			} else {
				while (oclass != null) {
					for (Field field : oclass.getDeclaredFields()) {
						if (Modifier.isStatic(field.getModifiers())) {
							continue;
						}

						field.setAccessible(true);
						Object value = field.get(obj);

						if (field.getType().isPrimitive()) {
							visitor.visitPrimitive(this, value, obj, field);
						} else {
							traverse(value, obj, field, visitor, -1);
						}
					}

					oclass = oclass.getSuperclass();
				}
			}

			visitor.exitObject(this, obj, parent, parentField, index);
		}
	}
}

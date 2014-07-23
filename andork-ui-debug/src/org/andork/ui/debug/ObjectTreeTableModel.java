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
package org.andork.ui.debug;


import static org.andork.reflect.ReflectionUtils.format;
import static org.andork.reflect.ReflectionUtils.getComponentType;
import static org.andork.reflect.ReflectionUtils.getRawType;
import static org.andork.reflect.ReflectionUtils.getSupertypeParameters;
import static org.andork.reflect.ReflectionUtils.getTypeParameterOrObject;
import static org.andork.reflect.ReflectionUtils.isAssignableFrom;
import static org.andork.reflect.ReflectionUtils.parameterize;
import static org.andork.reflect.ReflectionUtils.resolveType;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.andork.collect.CollectionUtils;
import org.andork.reflect.DefaultTypeFormatter;
import org.andork.reflect.ReflectionUtils;
import org.andork.reflect.TypeFormatter;
import org.jdesktop.swingx.treetable.TreeTableModel;

public class ObjectTreeTableModel implements TreeTableModel {
	private static final Logger				LOGGER			= Logger.getLogger(ObjectTreeTableModel.class.getName());

	private Node							root;

	private final List<TreeModelListener>	listeners		= new ArrayList<TreeModelListener>();

	private boolean							rootNodeMutable;

	private int								maxDepth		= 50;

	protected Set<Class<?>>					leafClasses;
	protected Set<Class<?>>					editableLeafClasses;

	protected TypeFormatter					typeFormatter	= new DefaultTypeFormatter();

	// static {
	// ConsoleHandler ch = new ConsoleHandler();
	// ch.setLevel(Level.FINE);
	// LOGGER.addHandler(ch);
	// LOGGER.setLevel(Level.FINE);
	// }

	public ObjectTreeTableModel() {
		init();
	}

	protected void init() {
		leafClasses = CollectionUtils.<Class<?>> asHashSet(
				boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class,
				char.class, Character.class, int.class, Integer.class, float.class, Float.class,
				long.class, Long.class, double.class, Double.class, BigInteger.class,
				BigDecimal.class, String.class, Date.class, Calendar.class);
		editableLeafClasses = new HashSet<Class<?>>(leafClasses);
	}

	protected boolean isLeafType(Type type) {
		return leafClasses.contains(type);
	}

	protected boolean isEditableLeafType(Type type) {
		return Enum.class.isAssignableFrom(getRawType(type)) || editableLeafClasses.contains(type);
	}

	public TypeFormatter getTypeFormatter() {
		return typeFormatter;
	}

	public void setTypeFormatter(TypeFormatter typeFormatter) {
		this.typeFormatter = typeFormatter;
	}

	public void setRoot(Object rootObj) {
		if (rootObj == null) {
			root = null;
		}
		else {
			root = new Node(null, rootObj, new RootStorage(rootObj.getClass()));
			nodeStructureChanged(root);
		}
	}

	public void setRoot(Object rootObj, Type resolvedRootType) {
		if (rootObj == null) {
			root = null;
		}
		else {
			root = new Node(null, rootObj, new RootStorage(resolvedRootType));
			nodeStructureChanged(root);
		}
	}

	public Object getRootObject() {
		return root == null ? null : root.getValue();
	}

	public Object getRoot() {
		return root;
	}

	public Object getChild(Object parent, int index) {
		return ((Node) parent).getChild(index);
	}

	public int getChildCount(Object parent) {
		return ((Node) parent).getChildCount();
	}

	public boolean isLeaf(Object node) {
		return ((Node) node).isLeaf();
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		Node node = (Node) path.getLastPathComponent();
		node.setValue(newValue);
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((Node) parent).childNodes.indexOf(child);
	}

	protected void nodesChanged(List<Node> nodes) {
		nodesChanged(nodes.toArray(new Node[nodes.size()]));
	}

	protected void nodesChanged(Node... nodes) {
		TreeModelEvent event = createNodesChangedOrInsertedEvent(nodes);
		for (TreeModelListener listener : listeners) {
			listener.treeNodesChanged(event);
		}
	}

	protected void nodesInserted(List<Node> nodes) {
		nodesInserted(nodes.toArray(new Node[nodes.size()]));
	}

	protected void nodesInserted(Node... nodes) {
		TreeModelEvent event = createNodesChangedOrInsertedEvent(nodes);
		for (TreeModelListener listener : listeners) {
			listener.treeNodesInserted(event);
		}
	}

	protected void nodesRemoved(TreeModelEvent event) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesRemoved(event);
		}
	}

	protected void nodeStructureChanged(Node node) {
		TreeModelEvent event = new TreeModelEvent(this, node == null ? null : node.getPath());
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(event);
		}
	}

	public TreeModelEvent createNodesChangedOrInsertedEvent(Node... nodes) {
		Node parent = null;

		int[] childIndices = new int[nodes.length];

		parent = nodes[0].getParent();
		if (parent == null) {
			throw new IllegalArgumentException("all nodes must have the same non-null parent");
		}

		childIndices[0] = parent.indexOf(nodes[0]);

		for (int i = 1; i < nodes.length; i++) {
			if (nodes[i].getParent() != parent) {
				throw new IllegalArgumentException("all nodes must have the same parent");
			}
			childIndices[i] = parent.indexOf(nodes[i]);
		}

		TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), childIndices, nodes);
		return event;
	}

	public void addTreeModelListener(TreeModelListener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return columnIndex == 0 ? Node.class : Object.class;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		return column == 0 ? "Name" : "Value";
	}

	public int getHierarchicalColumn() {
		return 0;
	}

	public Object getValueAt(Object node, int column) {
		return column == 0 ? node : ((Node) node).getValue();
	}

	public boolean isCellEditable(Object node, int column) {
		return column == 1 && ((Node) node).isEditable();
	}

	public void setValueAt(Object value, Object node, int column) {
		if (column != 1) {
			throw new IllegalArgumentException("invalid column: " + column);
		}
		Node nnode = (Node) node;
		try {
			nnode.setValue(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class Node {
		protected final int		depth;
		protected final Node	parent;
		protected Object		value;
		protected final Storage	storage;
		protected Aspect		aspect;
		protected boolean		aspectIsFixed;

		List<Node>				childNodes;

		protected Node(Node parent, Object value, Storage storage) {
			super();
			if (storage == null) {
				throw new IllegalArgumentException("storage must be non-null");
			}
			this.depth = parent == null ? 0 : parent.depth + 1;
			this.parent = parent;
			this.value = value;
			this.storage = storage;
			chooseAspect();
			expand();
		}

		protected void chooseAspect() {
			Class<?> storageClass = storage.getStorageClass();
			Class<?> valueClass = value == null ? Object.class : value.getClass();

			if (storage instanceof MapEntryStorage) {
				aspect = new EntryAspect(this);
			}
			else if (isLeafType(storageClass) || isLeafType(valueClass) || storageClass.isEnum() || valueClass.isEnum()) {
				aspect = new LeafAspect(this);
			}
			else if (isAssignableFrom(List.class, storage.resolvedType) || List.class.isAssignableFrom(valueClass)) {
				aspect = new ListAspect(this);
			}
			else if (isAssignableFrom(Map.class, storage.resolvedType) || Map.class.isAssignableFrom(valueClass)) {
				aspect = new MapAspect(this);
			}
			else if (storage.resolvedType instanceof GenericArrayType || storageClass.isArray() || valueClass.isArray()) {
				aspect = new ArrayAspect(this);
			}
			else {
				aspect = new ObjectAspect(this);
			}
		}

		public boolean isPrimitive() {
			return storage.getStorageClass().isPrimitive();
		}

		public boolean isMutable() {
			return storage.isMutable();
		}

		public Node getParent() {
			return parent;
		}

		public Object getValue() {
			return value;
		}

		public Storage getStorage() {
			return storage;
		}

		public Aspect getAspect() {
			return aspect;
		}

		public Object[] getPath() {
			int count = 0;
			Node n = this;
			while (n != null) {
				count++;
				n = n.getParent();
			}

			Object[] result = new Object[count];
			n = this;
			for (int i = count - 1; i >= 0; i--) {
				result[i] = n;
				n = n.getParent();
			}

			return result;
		}

		public Node getChild(int index) {
			return childNodes.get(index);
		}

		public int getChildCount() {
			return childNodes == null ? 0 : childNodes.size();
		}

		public boolean isLeaf() {
			return value == null || aspect.isLeaf();
		}

		public int indexOf(Node child) {
			if (childNodes == null) {
				return -1;
			}
			return childNodes.indexOf(child);
		}

		public void setValue(Object newValue) {
			if (!storage.canStore(newValue)) {
				throw new IllegalArgumentException("Can't set value to " + newValue + ", probably because it is the wrong type.");
			}
			if (value != newValue) {
				value = newValue;
				storage.store(this);
				if (!aspectIsFixed) {
					chooseAspect();
				}
				aspect.onValueChanged();
			}
		}

		public boolean isEditable() {
			return aspect.isEditable();
		}

		private boolean isCycle() {
			Node parent = this.parent;
			while (parent != null) {
				if (parent.value != null && parent.value == value) {
					return true;
				}
				parent = parent.getParent();
			}
			return false;
		}

		private void expand() {
			if (!isCycle() && depth < maxDepth) {
				if (LOGGER.getLevel() == Level.FINE) {
					StringBuffer sb = new StringBuffer();
					sb.append("expanding ");
					for (int i = 0; i < depth; i++) {
						sb.append('\t');
					}
					sb.append(this);
					LOGGER.fine(sb.toString());
				}
				aspect.expand();
			}
		}

		public void reexpand() {
			childNodes = null;
			expand();
			nodeStructureChanged(this);
		}

		public String toString() {
			return toString(typeFormatter);
		}

		public String toString(TypeFormatter typeFormatter) {
			return storage.toString(this, typeFormatter);
		}

		public void setValueToNewInstance() throws Exception {
			setValue(newInstance(storage.getStorageClass()));
		}

		public Object newInstance(Class<?> type) throws Exception {
			if (type.isArray()) {
				return Array.newInstance(type.getComponentType(), 0);
			}
			Class<?> enclosing = type.getEnclosingClass();
			if (enclosing != null && !Modifier.isStatic(type.getModifiers())) {
				Object enclosingInstance = findAncestralInstance(enclosing);
				Constructor<?> constructor = findInnerClassConstructor(type);
				if (constructor != null) {
					constructor.setAccessible(true);
					return constructor.newInstance(enclosingInstance);
				}
				throw new RuntimeException("Couldn't find constructor for instance class");
			} else {
				Constructor<?> constructor = findNullaryConstructor(type);
				if (constructor != null) {
					constructor.setAccessible(true);
					return constructor.newInstance();
				}
			}
			throw new RuntimeException("Unable to instantiate " + type.getSimpleName());
		}

		private Constructor<?> findNullaryConstructor(Class<?> type) {
			for (Constructor<?> constructor : type.getDeclaredConstructors()) {
				if (constructor.getDeclaringClass().equals(type) && constructor.getParameterTypes().length == 0) {
					return constructor;
				}
			}
			return null;
		}

		private Constructor<?> findInnerClassConstructor(Class<?> type) {
			for (Constructor<?> constructor : type.getDeclaredConstructors()) {
				if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].equals(type.getEnclosingClass())) {
					return constructor;
				}
			}
			return null;
		}

		public Object findAncestralInstance(Class<?> enclosing) {
			Node n = this;
			while (n != null) {
				if (n.value != null && enclosing.isInstance(n.value)) {
					return n.value;
				}
				n = n.getParent();
			}
			return null;
		}

		public Class<?> getStorageClass() {
			return storage.getStorageClass();
		}
	}

	public abstract class Storage {
		Type	resolvedType;

		protected Storage(Type resolvedType) {
			super();
			if (resolvedType == null) {
				throw new IllegalArgumentException("resolvedType must be non-null");
			}
			this.resolvedType = resolvedType;
		}

		public Type getResolvedType() {
			return resolvedType;
		}

		public Class<?> getStorageClass() {
			return getRawType(resolvedType);
		}

		public boolean canStore(Object newValue) {
			return newValue == null || ReflectionUtils.isFieldSettableFrom(getStorageClass(), newValue.getClass());
		}

		public abstract boolean isMutable();

		public abstract void store(Node node);

		public abstract String toString(Node node, TypeFormatter typeFormatter);
	}

	public class RootStorage extends Storage {
		protected RootStorage(Type resolvedType) {
			super(resolvedType);
		}

		public String toString(Node node, TypeFormatter typeFormatter) {
			return "Root";
		}

		@Override
		public boolean isMutable() {
			return rootNodeMutable;
		}

		public boolean canStore(Object newValue) {
			return true;
		}

		@Override
		public void store(Node node) {
			if (node.getValue() != null) {
				resolvedType = node.getValue().getClass();
			}
		}
	}

	public class FieldStorage extends Storage {
		final Field	field;

		public FieldStorage(Type resolvedType, Field field) {
			super(resolvedType);
			this.field = field;
		}

		public String toString(Node node, TypeFormatter typeFormatter) {
			return field.getName() + ": " + typeFormatter.format(resolvedType);
		}

		@Override
		public void store(Node node) {
			try {
				field.set(node.parent.value, node.value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean isMutable() {
			return !Modifier.isFinal(field.getModifiers());
		}
	}

	public abstract class ElementStorage extends Storage {
		protected final int	index;

		protected ElementStorage(Type resolvedType, int index) {
			super(resolvedType);
			this.index = index;
		}

		public int getIndex() {
			return index;
		}
	}

	public class ArrayElementStorage extends ElementStorage {
		protected ArrayElementStorage(Type resolvedType, int index) {
			super(resolvedType, index);
		}

		public String toString(Node node, TypeFormatter typeFormatter) {
			return "[" + index + "]: " + typeFormatter.format(resolvedType);
		}

		@Override
		public void store(Node node) {
			Array.set(node.parent.value, index, node.value);
		}

		@Override
		public boolean isMutable() {
			return true;
		}
	}

	public class ListElementStorage extends ElementStorage {
		protected ListElementStorage(Type resolvedType, int index) {
			super(resolvedType, index);
		}

		public String toString(Node node, TypeFormatter typeFormatter) {
			return "[" + index + "]: " + typeFormatter.format(resolvedType);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void store(Node node) {
			((List) node.parent.value).set(index, node.value);
		}

		@Override
		public boolean isMutable() {
			return true;
		}
	}

	public class MapEntryStorage extends Storage {
		protected MapEntryStorage(Type resolvedType) {
			super(resolvedType);
		}

		@Override
		public boolean isMutable() {
			return false;
		}

		public String toString(Node node, TypeFormatter typeFormatter) {
			try {
				return "[" + ((EntryAspect) node.aspect).getKeyNode().value + "]: " + typeFormatter.format(resolvedType);
			} catch (Exception e) {
				return format(resolvedType);
			}
		}

		@Override
		public void store(Node node) {

		}
	}

	public class MapEntryKeyStorage extends Storage {
		Object	currentKey;

		public MapEntryKeyStorage(Type resolvedType, Object key) {
			super(resolvedType);
			currentKey = key;
		}

		public String toString(Node node, TypeFormatter typeFormatter) {
			return "key: " + typeFormatter.format(resolvedType);
		}

		@Override
		public boolean isMutable() {
			return true;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void store(Node node) {
			Node entryNode = node.getParent();
			EntryAspect entryAspect = (EntryAspect) entryNode.aspect;
			Node mapNode = entryNode.getParent();
			Map map = (Map) mapNode.value;
			if (currentKey != null) {
				map.remove(currentKey);
			}
			currentKey = entryAspect.getKeyNode().value;
			if (currentKey != null) {
				map.put(currentKey, entryAspect.getValueNode().value);
			}
		}
	}

	public class MapEntryValueStorage extends Storage {
		protected MapEntryValueStorage(Type resolvedType) {
			super(resolvedType);
		}

		@Override
		public boolean isMutable() {
			return true;
		}

		public String toString(Node node, TypeFormatter typeFormatter) {
			return "value: " + typeFormatter.format(resolvedType);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void store(Node node) {
			Node entryNode = node.getParent();
			EntryAspect entryAspect = (EntryAspect) entryNode.aspect;
			Node mapNode = entryNode.getParent();
			Map map = (Map) mapNode.value;
			map.put(entryAspect.getKeyNode().value, entryAspect.getValueNode().value);
		}
	}

	public abstract class Aspect {
		protected final Node	node;

		protected Aspect(Node node) {
			super();
			this.node = node;
		}

		public boolean isEditable() {
			return false;
		}

		public boolean isLeaf() {
			return false;
		}

		protected abstract void expand();

		protected abstract void onValueChanged();

	}

	public class LeafAspect extends Aspect {
		public LeafAspect(Node node) {
			super(node);
		}

		@Override
		protected void onValueChanged() {
			if (node.getStorage() instanceof RootStorage) {
				setRoot(node.getValue());
			} else {
				nodesChanged(node);
			}
		}

		@Override
		protected void expand() {

		}

		@Override
		public boolean isEditable() {
			return isEditableLeafType(node.getStorageClass()) ||
					(node.getValue() != null && isEditableLeafType(node.getValue().getClass()));
		}

		@Override
		public boolean isLeaf() {
			return true;
		}
	}

	public class ObjectAspect extends Aspect {
		List<Field>	fields;

		public ObjectAspect(Node node) {
			super(node);
		}

		protected List<Field> getFields() {
			if (node.value == null) {
				return Collections.emptyList();
			}
			if (fields == null) {
				fields = getSortedFields(node.value.getClass());
				for (Field field : fields) {
					field.setAccessible(true);
				}
			}
			return fields;
		}

		@Override
		protected void onValueChanged() {
			fields = null;
			node.reexpand();
		}

		@Override
		protected void expand() {
			if (node.childNodes == null && node.value != null) {
				node.childNodes = new ArrayList<Node>();
				for (Field field : getFields()) {
					try {
						Object fieldValue = field.get(node.value);
						Type resolvedType = resolveType(field.getGenericType(), node.storage.resolvedType);
						node.childNodes.add(new Node(node, fieldValue, new FieldStorage(resolvedType, field)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public interface ListlikeAspect {
		public void removeElement(int index);

		public void addNewElement(int index, Object newElement) throws Exception;

		public boolean canAddOrRemoveChildren();
	}

	public class ArrayAspect extends Aspect implements ListlikeAspect {
		public ArrayAspect(Node node) {
			super(node);
		}

		@Override
		protected void onValueChanged() {
			node.reexpand();
		}

		@Override
		protected void expand() {
			if (node.childNodes == null && node.value != null) {
				node.childNodes = new ArrayList<Node>();
				for (int i = 0; i < Array.getLength(node.value); i++) {
					Object element = Array.get(node.value, i);
					Type resolvedType = getComponentType(node.storage.resolvedType);
					node.childNodes.add(new Node(node, element, new ArrayElementStorage(resolvedType, i)));
				}
			}
		}

		@Override
		public boolean canAddOrRemoveChildren() {
			return node.value != null && node.storage.isMutable();
		}

		@Override
		public void removeElement(int index) {
			Object newArray = Array.newInstance(node.storage.getStorageClass()
					.getComponentType(), node.getChildCount() - 1);
			System.arraycopy(node.value, 0, newArray, 0, index);
			System.arraycopy(node.value, index + 1, newArray, index, node.getChildCount() - index - 1);
			node.setValue(newArray);
		}

		@Override
		public void addNewElement(int index, Object newElement) throws Exception {
			Class<?> componentType = node.storage.getStorageClass().getComponentType();
			Object newArray = Array.newInstance(componentType, node.getChildCount() + 1);
			System.arraycopy(node.value, 0, newArray, 0, index);
			Array.set(newArray, index, newElement);
			System.arraycopy(node.value, index, newArray, index + 1, node.getChildCount() - index);
			node.setValue(newArray);
		}
	}

	public class ListAspect extends Aspect implements ListlikeAspect {
		public ListAspect(Node node) {
			super(node);
		}

		@Override
		protected void onValueChanged() {
			node.reexpand();
		}

		@Override
		protected void expand() {
			Type resolvedComponentType = getTypeParameterOrObject(node.storage.resolvedType, 0);
			if (node.childNodes == null && node.value != null) {
				node.childNodes = new ArrayList<Node>();
				int i = 0;
				for (Object element : ((List<?>) node.value)) {
					node.childNodes.add(new Node(node, element, new ListElementStorage(resolvedComponentType, i++)));
				}
			}
		}

		@Override
		public boolean canAddOrRemoveChildren() {
			return node.value != null;
		}

		public Class<?> getElementType() {
			return getRawType(getTypeParameterOrObject(node.storage.resolvedType, 0));
		}

		@Override
		public void removeElement(int index) {
			((List<?>) node.value).remove(index);
			node.reexpand();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void addNewElement(int index, Object newElement) throws Exception {
			((List) node.value).add(index, newElement);
			node.reexpand();
		}
	}

	public interface MaplikeAspect {
		public void removeEntry(Node entryNode);

		public void removeEntry(Object key);

		public void putNewEntry(Object key, Object value) throws Exception;

		public boolean canAddOrRemoveChildren();

		public abstract Class<?> getValueType();

		public abstract Class<?> getKeyType();
	}

	public class MapAspect extends Aspect implements MaplikeAspect {
		public MapAspect(Node node) {
			super(node);
		}

		@Override
		protected void onValueChanged() {
			node.reexpand();
		}

		@Override
		protected void expand() {
			Type resolvedEntryType = parameterize(Map.Entry.class, getSupertypeParameters(Map.class, node.storage.resolvedType));
			if (node.childNodes == null && node.value != null) {
				node.childNodes = new ArrayList<Node>();
				for (Map.Entry<?, ?> entry : ((Map<?, ?>) node.value).entrySet()) {
					node.childNodes.add(new Node(node, entry, new MapEntryStorage(resolvedEntryType)));
				}
			}
		}

		@Override
		public boolean canAddOrRemoveChildren() {
			return node.value != null;
		}

		@Override
		public Class<?> getKeyType() {
			return getRawType(getTypeParameterOrObject(node.storage.resolvedType, 0));
		}

		@Override
		public Class<?> getValueType() {
			return getRawType(getTypeParameterOrObject(node.storage.resolvedType, 1));
		}

		@Override
		public void removeEntry(Object key) {
			((Map<?, ?>) node.value).remove(key);

			int removeIndex = -1;
			Node removeEntry = null;

			if (node.childNodes != null) {
				int i = 0;
				for (Node child : node.childNodes) {
					Object childKey = ((EntryAspect) child.aspect).getKeyNode().value;
					if (key.equals(childKey)) {
						removeIndex = i;
						removeEntry = child;
						break;
					}
					i++;
				}
			}

			if (removeEntry != null) {
				node.childNodes.remove(removeIndex);
				nodesRemoved(new TreeModelEvent(ObjectTreeTableModel.this, node.getPath(),
						new int[] { removeIndex }, new Object[] { removeEntry }));
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void removeEntry(Node entryNode) {
			int removeIndex = -1;

			if (node.childNodes != null) {
				int i = 0;
				for (Node child : node.childNodes) {
					if (child == entryNode) {
						removeIndex = i;
						break;
					}
					i++;
				}
			}

			if (removeIndex >= 0) {
				Object key = ((EntryAspect) entryNode.aspect).getKeyNode().value;
				if (key != null) {
					((Map) node.value).remove(key);
				}
				node.childNodes.remove(removeIndex);
				nodeStructureChanged(node);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void putNewEntry(Object key, Object value) throws Exception {
			Map map = (Map) node.value;
			if (key != null) {
				map.put(key, value);
			}
			Type resolvedEntryType = parameterize(Map.Entry.class, getSupertypeParameters(Map.class, node.storage.resolvedType));
			Map.Entry<Object, Object> newEntry = new Entry<Object, Object>(key, value);
			if (node.childNodes == null) {
				node.childNodes = new ArrayList<Node>();
			}
			Node newNode = new Node(node, newEntry, new MapEntryStorage(resolvedEntryType));
			node.childNodes.add(newNode);

			nodeStructureChanged(node);
		}
	}

	public class EntryAspect extends Aspect {
		public EntryAspect(Node node) {
			super(node);
		}

		public Node getKeyNode() {
			return node.childNodes.get(0);
		}

		public Node getValueNode() {
			return node.childNodes.get(1);
		}

		@Override
		protected void expand() {
			if (node.childNodes == null) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) node.value;
				node.childNodes = new ArrayList<Node>();
				Type resolvedKeyType = getTypeParameterOrObject(node.storage.resolvedType, 0);
				Type resolvedValueType = getTypeParameterOrObject(node.storage.resolvedType, 1);
				node.childNodes.add(new Node(node, entry.getKey(), new MapEntryKeyStorage(resolvedKeyType, entry.getKey())));
				node.childNodes.add(new Node(node, entry.getValue(), new MapEntryValueStorage(resolvedValueType)));
			}
		}

		@Override
		protected void onValueChanged() {

		}
	}

	protected static class Entry<K, V> implements Map.Entry<K, V> {
		protected Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		K	key;
		V	value;

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V result = this.value;
			this.value = value;
			return result;
		}
	}

	private static List<Field> getSortedFields(Class<?> type) {
		List<Field> result = new ArrayList<Field>();

		while (type != null) {
			for (Field field : type.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers()) && !field.isSynthetic()) {
					result.add(field);
				}
			}
			type = type.getSuperclass();
		}

		Collections.sort(result, new FieldNameComparator());
		return result;
	}

	private static class FieldNameComparator implements Comparator<Field> {
		public int compare(Field o1, Field o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}

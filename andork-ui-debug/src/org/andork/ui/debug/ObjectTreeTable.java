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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.andork.awt.GridBagWizard;
import org.andork.collect.CollectionUtils;
import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.LinkedListMultiMap;
import org.andork.collect.MultiMap;
import org.andork.format.Format;
import org.andork.reflect.CompactTypeFormatter;
import org.andork.reflect.ReflectionUtils;
import org.andork.reflect.TypeFormatter;
import org.andork.swing.RendererButtonModel;
import org.andork.swing.selector.DefaultSelector;
import org.andork.swing.table.CellEditorBoundsOverridingTableUI;
import org.andork.swing.table.CheckboxTableCellRenderer;
import org.andork.swing.table.TableCellRendererButtonModelContext;
import org.andork.swing.table.TableCellRendererRetargeter;
import org.andork.swing.table.TableCellRendererWithButtons;
import org.andork.ui.debug.ObjectTreeTableModel.ArrayElementStorage;
import org.andork.ui.debug.ObjectTreeTableModel.ElementStorage;
import org.andork.ui.debug.ObjectTreeTableModel.ListElementStorage;
import org.andork.ui.debug.ObjectTreeTableModel.ListlikeAspect;
import org.andork.ui.debug.ObjectTreeTableModel.MapEntryStorage;
import org.andork.ui.debug.ObjectTreeTableModel.MaplikeAspect;
import org.andork.ui.debug.ObjectTreeTableModel.Node;
import org.andork.util.ArrayUtils;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.jdesktop.swingx.treetable.TreeTableModel;


@SuppressWarnings("serial")
public class ObjectTreeTable extends JXTreeTable {
	protected NodeCellRenderer											nodeCellRenderer;
	protected ValueCellRenderer											defaultValueCellRenderer;
	protected ValueCellRenderer											booleanValueCellRenderer;
	protected final Map<Class<?>, Format<?>>							leafFormats				= CollectionUtils.newHashMap();
	protected final LinkedListMultiMap<Class<?>, Object>				enumlikeTypes			= LinkedListMultiMap.newInstance();
	protected final LinkedHashSetMultiMap<Class<?>, Instantiator<?>>	instantiators			= LinkedHashSetMultiMap.newInstance();
	protected TypeFormatter												typeFormatter			= new CompactTypeFormatter();

	protected ArbitraryInstantiator										arbitraryInstantiator	= new ArbitraryInstantiator();

	private static ImageIcon											insertChildIcon			= new ImageIcon(ObjectTreeTable.class.getResource("insert-child.png"));
	private static ImageIcon											insertSiblingIcon		= new ImageIcon(ObjectTreeTable.class.getResource("insert-sibling.png"));
	private static ImageIcon											removeNodeIcon			= new ImageIcon(ObjectTreeTable.class.getResource("remove.png"));
	private static ImageIcon											nullifyIcon				= new ImageIcon(ObjectTreeTable.class.getResource("nullify.png"));
	private static ImageIcon											newIcon					= new ImageIcon(ObjectTreeTable.class.getResource("new.png"));

	protected Retargeter												retargeter				= new Retargeter();

	public ObjectTreeTable() {
		this(new ObjectTreeTableModel());
	}

	public ObjectTreeTable(ObjectTreeTableModel model) {
		super(model);
		model.setTypeFormatter(typeFormatter);
		init();
	}

	@Override
	public void setTreeTableModel(TreeTableModel treeModel) {
		if (!(treeModel instanceof ObjectTreeTableModel)) {
			throw new IllegalArgumentException("treeModel must be an instanceof ObjectTreeTableModel");
		}
		((ObjectTreeTableModel) treeModel).setTypeFormatter(typeFormatter);
		super.setTreeTableModel(treeModel);
	}

	@Override
	public ObjectTreeTableModel getTreeTableModel() {
		return (ObjectTreeTableModel) super.getTreeTableModel();
	}

	protected void init() {
		initFormats();
		initInstantiators();

		setRowHeight(getRowHeight() + 1);

		setOpenIcon(null);
		setClosedIcon(null);
		setLeafIcon(null);

		getTableHeader().setReorderingAllowed(false);

		retargeter = new Retargeter();
		addMouseListener(retargeter);
		addMouseMotionListener(retargeter);

		nodeCellRenderer = new NodeCellRenderer();
		// setTreeCellRenderer(nodeCellRenderer);

		defaultValueCellRenderer = new ValueCellRenderer();
		booleanValueCellRenderer = new ValueCellRenderer(new CheckboxTableCellRenderer(new JCheckBox()));
		setUI(new CellEditorBoundsOverridingTableUI());
	}

	protected static void put(MultiMap<Class<?>, Instantiator<?>> map, Instantiator<?> instantiator) {
		put(map, instantiator.getInstantiatingType(), instantiator);
	}

	protected static void put(MultiMap<Class<?>, Instantiator<?>> map, Class<?> c, Instantiator<?> instantiator) {
		while (c != null && c != Object.class) {
			map.put(c, instantiator);
			for (Class<?> iface : c.getInterfaces()) {
				put(map, iface, instantiator);
			}
			c = c.getSuperclass();
		}
	}

	protected void initFormats() {
		Format<Boolean> booleanFormat = new DefaultAbstractFormat<Boolean>() {
			public Boolean parse(String s) throws Exception {
				return Boolean.valueOf(s);
			}
		};
		leafFormats.put(boolean.class, booleanFormat);
		leafFormats.put(Boolean.class, new NullableFormat<Boolean>(booleanFormat));

		Format<Byte> byteFormat = new DefaultAbstractFormat<Byte>() {
			public Byte parse(String s) throws Exception {
				return Byte.valueOf(s);
			}
		};
		leafFormats.put(byte.class, byteFormat);
		leafFormats.put(Byte.class, new NullableFormat<Byte>(byteFormat));

		Format<Character> charFormat = new DefaultAbstractFormat<Character>() {
			public Character parse(String s) throws Exception {
				return s.charAt(0);
			}
		};
		leafFormats.put(char.class, charFormat);
		leafFormats.put(Character.class, new NullableFormat<Character>(charFormat));

		Format<Short> shortFormat = new DefaultAbstractFormat<Short>() {
			public Short parse(String s) throws Exception {
				return Short.valueOf(s);
			}
		};
		leafFormats.put(short.class, shortFormat);
		leafFormats.put(Short.class, new NullableFormat<Short>(shortFormat));

		Format<Integer> intFormat = new DefaultAbstractFormat<Integer>() {
			public Integer parse(String s) throws Exception {
				return Integer.valueOf(s);
			}
		};
		leafFormats.put(int.class, intFormat);
		leafFormats.put(Integer.class, new NullableFormat<Integer>(intFormat));

		Format<Float> floatFormat = new DefaultAbstractFormat<Float>() {
			public Float parse(String s) throws Exception {
				return Float.valueOf(s);
			}
		};
		leafFormats.put(float.class, floatFormat);
		leafFormats.put(Float.class, new NullableFormat<Float>(floatFormat));

		Format<Long> longFormat = new DefaultAbstractFormat<Long>() {
			public Long parse(String s) throws Exception {
				return Long.valueOf(s);
			}
		};
		leafFormats.put(long.class, longFormat);
		leafFormats.put(Long.class, new NullableFormat<Long>(longFormat));

		Format<Double> doubleFormat = new DefaultAbstractFormat<Double>() {
			public Double parse(String s) throws Exception {
				return Double.valueOf(s);
			}
		};
		leafFormats.put(double.class, doubleFormat);
		leafFormats.put(Double.class, new NullableFormat<Double>(doubleFormat));

		Format<BigInteger> bigIntegerFormat = new DefaultAbstractFormat<BigInteger>() {
			public BigInteger parse(String s) throws Exception {
				return new BigInteger(s);
			}
		};
		leafFormats.put(BigInteger.class, bigIntegerFormat);

		Format<BigDecimal> bigDecimalFormat = new DefaultAbstractFormat<BigDecimal>() {
			public BigDecimal parse(String s) throws Exception {
				return new BigDecimal(s);
			}
		};
		leafFormats.put(BigDecimal.class, bigDecimalFormat);

		Format<String> stringFormat = new DefaultAbstractFormat<String>() {
			public String parse(String s) throws Exception {
				return String.valueOf(s);
			}
		};
		leafFormats.put(String.class, stringFormat);

		final Format<Date> dateFormat = new Format<Date>() {
			SimpleDateFormat	format	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			@Override
			public String format(Date t) {
				return format.format(t);
			}

			@Override
			public Date parse(String s) throws Exception {
				return format.parse(s);
			}
		};
		leafFormats.put(Date.class, dateFormat);

		leafFormats.put(Calendar.class, new Format<Calendar>() {
			@Override
			public String format(Calendar t) {
				return dateFormat.format(t.getTime());
			}

			@Override
			public Calendar parse(String s) throws Exception {
				Date date = dateFormat.parse(s);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				return calendar;
			}
		});
	}

	protected void initInstantiators() {
		put(instantiators, new DefaultInstantiator<ArrayList<?>>() {
			@Override
			public ArrayList<?> newInstance(Node target) {
				return new ArrayList<Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<LinkedList<?>>() {
			@Override
			public LinkedList<?> newInstance(Node target) {
				return new LinkedList<Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<Vector<?>>() {
			@Override
			public Vector<?> newInstance(Node target) {
				return new Vector<Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<HashSet<?>>() {
			@Override
			public HashSet<?> newInstance(Node target) {
				return new HashSet<Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<TreeSet<?>>() {
			@Override
			public TreeSet<?> newInstance(Node target) {
				return new TreeSet<Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<LinkedHashSet<?>>() {
			@Override
			public LinkedHashSet<?> newInstance(Node target) {
				return new LinkedHashSet<Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<HashMap<?, ?>>() {
			@Override
			public HashMap<?, ?> newInstance(Node target) {
				return new HashMap<Object, Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<LinkedHashMap<?, ?>>() {
			@Override
			public LinkedHashMap<?, ?> newInstance(Node target) {
				return new LinkedHashMap<Object, Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<Hashtable<?, ?>>() {
			@Override
			public Hashtable<?, ?> newInstance(Node target) {
				return new Hashtable<Object, Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<TreeMap<?, ?>>() {
			@Override
			public TreeMap<?, ?> newInstance(Node target) {
				return new TreeMap<Object, Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<WeakHashMap<?, ?>>() {
			@Override
			public WeakHashMap<?, ?> newInstance(Node target) {
				return new WeakHashMap<Object, Object>();
			}
		});
		put(instantiators, new DefaultInstantiator<Date>() {
			@Override
			public Date newInstance(Node target) {
				return new Date();
			}

			@Override
			public String getDescription() {
				return "new Date()";
			}
		});
		put(instantiators, new DefaultInstantiator<Calendar>() {
			@Override
			public Calendar newInstance(Node target) {
				return Calendar.getInstance();
			}

			@Override
			public String getDescription() {
				return "Calendar.getInstance()";
			}
		});
		put(instantiators, new DefaultInstantiator<Boolean>() {
			@Override
			public Boolean newInstance(Node target) {
				return new Boolean(false);
			}

			@Override
			public String getDescription() {
				return "new Boolean(false)";
			}
		});
		put(instantiators, new DefaultInstantiator<Boolean>() {
			@Override
			public Boolean newInstance(Node target) {
				return new Boolean(true);
			}

			@Override
			public String getDescription() {
				return "new Boolean(true)";
			}
		});
		put(instantiators, new DefaultInstantiator<Boolean>() {
			@Override
			public Boolean newInstance(Node target) {
				return Boolean.FALSE;
			}

			@Override
			public String getDescription() {
				return "Boolean.FALSE";
			}
		});
		put(instantiators, new DefaultInstantiator<Boolean>() {
			@Override
			public Boolean newInstance(Node target) {
				return Boolean.TRUE;
			}

			@Override
			public String getDescription() {
				return "Boolean.TRUE";
			}
		});
		put(instantiators, new DefaultInstantiator<Byte>() {
			@Override
			public Byte newInstance(Node target) {
				return new Byte((byte) 0);
			}

			@Override
			public String getDescription() {
				return "new Byte((byte) 0)";
			}
		});
		put(instantiators, new DefaultInstantiator<Short>() {
			@Override
			public Short newInstance(Node target) {
				return new Short((short) 0);
			}

			@Override
			public String getDescription() {
				return "new Short((short) 0)";
			}
		});
		put(instantiators, new DefaultInstantiator<Character>() {
			@Override
			public Character newInstance(Node target) {
				return new Character((char) 0);
			}

			@Override
			public String getDescription() {
				return "new Character((char) 0)";
			}
		});
		put(instantiators, new DefaultInstantiator<Integer>() {
			@Override
			public Integer newInstance(Node target) {
				return new Integer(0);
			}

			@Override
			public String getDescription() {
				return "new Integer(0)";
			}
		});
		put(instantiators, new DefaultInstantiator<Float>() {
			@Override
			public Float newInstance(Node target) {
				return new Float(0f);
			}

			@Override
			public String getDescription() {
				return "new Float(0f)";
			}
		});
		put(instantiators, new DefaultInstantiator<Long>() {
			@Override
			public Long newInstance(Node target) {
				return new Long(0L);
			}

			@Override
			public String getDescription() {
				return "new Long(0L)";
			}
		});
		put(instantiators, new DefaultInstantiator<Double>() {
			@Override
			public Double newInstance(Node target) {
				return new Double(0.0);
			}

			@Override
			public String getDescription() {
				return "new Double(0.0)";
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object formatNodeValue(Object value) {
		if (value == null) {
			return "null";
		} else {
			Format format = leafFormats.get(value.getClass());
			if (format != null) {
				return "<html>" + format.format(value) + " <font color=\"gray\">(" + typeFormatter.format(value.getClass()) + String.format(" @%x)</font></html>", System.identityHashCode(value));
			} else if (value.getClass().isEnum()) {
				return "<html>" + value + " <font color=\"gray\">(" + typeFormatter.format(value.getClass()) + ")</font></html>";
			} else {
				return "<html>" + typeFormatter.format(value.getClass()) + String.format(" <font color=\"gray\"> @%x</font></html>", System.identityHashCode(value));
			}
		}
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 1) {
			Node node = getNodeAt(row);
			if (node.getValue() instanceof Boolean) {
				return booleanValueCellRenderer;
			}
			return defaultValueCellRenderer;
		}
		return super.getCellRenderer(row, column);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component comp = super.prepareRenderer(renderer, row, column);
		if (comp instanceof ValueCellRenderer) {
			ValueCellRenderer vcr = (ValueCellRenderer) comp;
			if (column == 1) {
				Node node = getNodeAt(row);
				if (node.getValue() == null) {
					vcr.getContent().setForeground(Color.GRAY);
				} else if (!isRowSelected(row)) {
					vcr.getContent().setForeground(Color.BLACK);
				}
			}
		}
		return comp;
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		Node node = getNodeAt(row);
		Class<?> valueClass = node.getValue() == null ? null : node.getValue().getClass();
		Class<?> storageClass = node.getStorageClass();
		if (node.getValue() instanceof Enum) {
			DefaultSelector<Object> selector = new DefaultSelector<Object>();
			selector.setAvailableValues(Arrays.asList(valueClass.getEnumConstants()));
			selector.comboBox().setRenderer(new ComboBoxCellRenderer());
			return new DefaultCellEditor(selector.comboBox());
		} else if (storageClass.isEnum()) {
			DefaultSelector<Object> selector = new DefaultSelector<Object>();
			selector.setAvailableValues(Arrays.asList(storageClass.getEnumConstants()));
			selector.comboBox().setRenderer(new ComboBoxCellRenderer());
			return new DefaultCellEditor(selector.comboBox());
		} else if (enumlikeTypes.containsKey(valueClass)) {
			DefaultSelector<Object> selector = new DefaultSelector<Object>();
			selector.setAvailableValues(enumlikeTypes.get(valueClass));
			selector.comboBox().setRenderer(new ComboBoxCellRenderer());
			return new DefaultCellEditor(selector.comboBox());
		} else if (enumlikeTypes.containsKey(storageClass)) {
			DefaultSelector<Object> selector = new DefaultSelector<Object>();
			selector.setAvailableValues(enumlikeTypes.get(storageClass));
			selector.comboBox().setRenderer(new ComboBoxCellRenderer());
			return new DefaultCellEditor(selector.comboBox());
		} else if (valueClass == boolean.class || valueClass == Boolean.class ||
				storageClass == boolean.class || storageClass == Boolean.class) {
			return new DefaultCellEditor(new JCheckBox());
		} else if (valueClass == Date.class || storageClass == Date.class) {
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			DatePickerCellEditor datePicker = new DatePickerCellEditor(format);
			datePicker.setClickCountToStart(0);
			return datePicker;
		}
		return super.getCellEditor(row, column);
	}

	private Node getNodeAt(int row) {
		return (Node) super.getValueAt(row, 0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (aValue != null) {
			Node node = getNodeAt(row);

			if (node.getValue() != null) {
				Class<?> valueClass = node.getValue().getClass();
				if (valueClass.isAssignableFrom(aValue.getClass())) {
					super.setValueAt(aValue, row, column);
					return;
				} else {
					Format format = leafFormats.get(valueClass);
					if (format != null) {
						try {
							aValue = format.parse(aValue.toString());
							super.setValueAt(aValue, row, column);
							return;
						} catch (Exception e) {
						}
					}
				}
			}

			Class<?> storageClass = node.getStorageClass();
			if (!storageClass.isAssignableFrom(aValue.getClass())) {
				Format format = leafFormats.get(storageClass);
				if (format != null) {
					try {
						aValue = format.parse(aValue.toString());
					} catch (Exception e) {
						handleParseException(e);
						return;
					}
				}
			}
		}
		super.setValueAt(aValue, row, column);
	}

	protected void handleParseException(Exception e) {
		e.printStackTrace();
	}

	protected boolean shouldShowNullifyButton(Node node) {
		return node.getValue() != null && node.isMutable() && !node.isPrimitive();
	}

	protected boolean shouldShowNewifyButton(Node node) {
		return node.isMutable();
	}

	protected void handleNewButtonPressed(ActionEvent e, final Node node) throws Exception {
		// node.setValueToNewInstance();
		// expandPath(new TreePath(node.getPath()));
		showInstantiatorPopupMenu(node, this,
				retargeter.lastReleaseEvent.getX(), retargeter.lastReleaseEvent.getY(),
				new InstantiationListener() {
					@Override
					public void objectInstantiated(Object instantiatedObject) {
						node.setValue(instantiatedObject);
						expandPath(new TreePath(node.getPath()));
					}
				});
	}

	public void promptForNewRootClass() {
		if (getTreeTableModel().getRootObject() == null) {
			getTreeTableModel().setRoot(new Object());
		}

		Object newInstance = null;
		try {
			newInstance = arbitraryInstantiator.newInstance((Node) getTreeTableModel().getRoot());
		} catch (Exception e) {
			handleInstantiationExecption(e);
		}
		if (newInstance != null) {
			getTreeTableModel().setRoot(newInstance);
		}
	}

	protected void handleNullifyButtonPressed(Node node) {
		node.setValue(null);
	}

	protected void handleRemoveButtonPressed(Node node) {
		removeEditor();
		if (node.getStorage() instanceof ListElementStorage || node.getStorage() instanceof ArrayElementStorage) {
			((ListlikeAspect) node.getParent().getAspect()).removeElement(node.getParent().indexOf(node));
		} else if (node.getStorage() instanceof MapEntryStorage) {
			Node mapNode = node.getParent();
			((MaplikeAspect) mapNode.getAspect()).removeEntry(node);
		}
	}

	protected void handleInsertChildButtonPressed(Node node) {
		removeEditor();
		try {
			if (node.getAspect() instanceof ListlikeAspect) {
				((ListlikeAspect) node.getAspect()).addNewElement(node.getChildCount(), null);
				expandPath(new TreePath(node.getPath()));
				TreePath childPath = new TreePath(node.getChild(node.getChildCount() - 1).getPath());
				expandPath(childPath);
				int row = getRowForPath(childPath);
				getSelectionModel().setSelectionInterval(row, row);
			} else if (node.getAspect() instanceof MaplikeAspect) {
				((MaplikeAspect) node.getAspect()).putNewEntry(null, null);
				TreePath childPath = new TreePath(node.getChild(node.getChildCount() - 1).getPath());
				expandPath(childPath);
				int row = getRowForPath(childPath);
				getSelectionModel().setSelectionInterval(row, row);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void handleInsertSiblingButtonPressed(Node node) {
		removeEditor();
		try {
			int index = node.getParent().indexOf(node);
			((ListlikeAspect) node.getParent().getAspect()).addNewElement(index, null);
			TreePath childPath = new TreePath(node.getParent().getChild(index).getPath());
			expandPath(childPath);
			int row = getRowForPath(childPath);
			getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void showInstantiatorPopupMenu(final Node node, Component invoker, int x, int y, final InstantiationListener instantiationListener) {
		showInstantiatorPopupMenu(node, node.getStorageClass(), invoker, x, y, instantiationListener);
	}

	protected void showInstantiatorPopupMenu(final Node node, final Class<?> storageClass, Component invoker, int x, int y, final InstantiationListener instantiationListener) {
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Instantiator<?> instantiator = ((InstantiatorMenuItem) e.getSource()).instantiator;
				try {
					Object instance = instantiator.newInstance(node);
					if (instance != null) {
						instantiationListener.objectInstantiated(instance);
					}
				} catch (Exception e1) {
					handleInstantiationExecption(e1);
				}
			}
		};

		JPopupMenu menu = new JPopupMenu();
		for (Instantiator<?> instantiator : instantiators.get(storageClass)) {
			InstantiatorMenuItem item = new InstantiatorMenuItem(instantiator);
			item.addActionListener(al);
			menu.add(item);
		}

		if (menu.getComponentCount() == 0 && !storageClass.isInterface() && (storageClass.isArray()
				|| !Modifier.isAbstract(storageClass.getModifiers()))) {
			Instantiator<Object> defaultInstantiator = new Instantiator<Object>() {
				@Override
				public Object newInstance(Node node) throws Exception {
					return node.newInstance(storageClass);
				}

				@Override
				public String getDescription() {
					return "new " + typeFormatter.format(storageClass);
				}

				@Override
				public Class<?> getInstantiatingType() {
					return Object.class;
				}
			};

			InstantiatorMenuItem defaultItem = new InstantiatorMenuItem(defaultInstantiator);
			defaultItem.addActionListener(al);
			menu.add(defaultItem);
		}

		InstantiatorMenuItem arbitraryItem = new InstantiatorMenuItem(arbitraryInstantiator);
		arbitraryItem.addActionListener(al);
		menu.add(arbitraryItem);

		if (menu.getComponentCount() == 0) {
			JMenuItem notice = new JMenuItem("No instantiators available");
			menu.add(notice);
		}

		menu.show(invoker, x, y);
	}

	protected void handleInstantiationExecption(Exception e) {
		JOptionPane.showMessageDialog(ObjectTreeTable.this, e.getLocalizedMessage(), "Instantiation failed", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	private static Constructor<?> findNullaryConstructor(Class<?> type) {
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			if (constructor.getDeclaringClass().equals(type) && constructor.getParameterTypes().length == 0) {
				return constructor;
			}
		}
		return null;
	}

	protected class NodeCellRenderer extends DefaultTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			if (value instanceof Node) {
				value = ((Node) value).toString(typeFormatter);
			}
			Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (comp instanceof JLabel) {
				((JLabel) comp).setIcon(null);
			}
			return comp;
		}
	}

	protected class ValueCellRenderer extends TableCellRendererWithButtons {
		JButton								insertChildButton;
		JButton								insertSiblingButton;
		JButton								removeButton;
		JButton								nullifyButton;
		JButton								newifyButton;

		RendererButtonModel.RendererContext	rendererButtonModelContext;

		public ValueCellRenderer() {
			this(new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					return super.getTableCellRendererComponent(table, formatNodeValue(value), isSelected, hasFocus, row, column);
				}
			});
		}

		public ValueCellRenderer(TableCellRenderer wrapped) {
			super(wrapped);
			initListeners();
		}

		protected JButton[] initButtons() {
			rendererButtonModelContext = new TableCellRendererButtonModelContext(retargeter, this);

			newifyButton = new JButton(newIcon);
			nullifyButton = new JButton(nullifyIcon);
			insertChildButton = new JButton(insertChildIcon);
			insertSiblingButton = new JButton(insertSiblingIcon);
			removeButton = new JButton(removeNodeIcon);

			JButton[] buttons = new JButton[] { insertChildButton, insertSiblingButton, removeButton, newifyButton, nullifyButton };

			for (JButton button : buttons) {
				button.setOpaque(false);
				button.setMargin(new Insets(1, 1, 1, 1));
				button.setModel(createButtonModel());
				add(button);
			}

			return buttons;
		}

		protected void initListeners() {
			newifyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					removeEditor();
					Node node = getNodeAt(rendererRow);
					try {
						handleNewButtonPressed(e, node);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			nullifyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					removeEditor();
					Node node = getNodeAt(rendererRow);
					try {
						handleNullifyButtonPressed(node);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Node node = getNodeAt(rendererRow);
					handleRemoveButtonPressed(node);
				}
			});

			insertChildButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Node node = getNodeAt(rendererRow);
					handleInsertChildButtonPressed(node);
				}
			});

			insertSiblingButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Node node = getNodeAt(rendererRow);
					handleInsertSiblingButtonPressed(node);
				}
			});
		}

		protected javax.swing.ButtonModel createButtonModel() {
			return new RendererButtonModel(rendererButtonModelContext);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Node node = getNodeAt(row);
			if (node.getStorage() instanceof ElementStorage) {
				ListlikeAspect listAspect = (ListlikeAspect) node.getParent().getAspect();
				insertSiblingButton.setVisible(listAspect.canAddOrRemoveChildren());
				removeButton.setVisible(listAspect.canAddOrRemoveChildren() && node.getParent().getChildCount() > 0);
			} else if (node.getStorage() instanceof MapEntryStorage) {
				insertChildButton.setVisible(false);
				insertSiblingButton.setVisible(false);
				removeButton.setVisible(((MaplikeAspect) node.getParent().getAspect()).canAddOrRemoveChildren());
			} else {
				insertChildButton.setVisible(false);
				insertSiblingButton.setVisible(false);
				removeButton.setVisible(false);
			}

			if (node.getAspect() instanceof ListlikeAspect) {
				insertChildButton.setVisible(((ListlikeAspect) node.getAspect()).canAddOrRemoveChildren());
			} else if (node.getAspect() instanceof MaplikeAspect) {
				insertChildButton.setVisible(((MaplikeAspect) node.getAspect()).canAddOrRemoveChildren());
			} else {
				insertChildButton.setVisible(false);
			}
			newifyButton.setVisible(shouldShowNewifyButton(node));
			nullifyButton.setVisible(shouldShowNullifyButton(node));

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
	
	protected class ComboBoxCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			value = formatNodeValue(value);
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}

	protected class Retargeter extends TableCellRendererRetargeter {
		protected MouseEvent	lastReleaseEvent;

		@Override
		protected boolean shouldRetarget(MouseEvent e) {
			return columnAtPoint(e.getPoint()) == 1;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			lastReleaseEvent = e;
			super.mouseReleased(e);
		}
	}

	protected static abstract class DefaultAbstractFormat<T> implements Format<T> {
		public String format(T t) {
			return String.valueOf(t);
		}
	}

	protected static class NullableFormat<T> implements Format<T> {
		Format<T>	wrapped;

		protected NullableFormat(Format<T> wrapped) {
			this.wrapped = wrapped;
		}

		public String format(T t) {
			return wrapped.format(t);
		}

		public T parse(String s) throws Exception {
			if (s == null || "".equals(s) || "null".equals(s)) {
				return null;
			}
			return wrapped.parse(s);
		}
	}

	protected static interface Instantiator<T> {
		public T newInstance(Node target) throws Exception;

		public String getDescription();

		public abstract Class<?> getInstantiatingType();
	}

	protected abstract class DefaultInstantiator<T> implements Instantiator<T> {
		public abstract T newInstance(Node target);

		@Override
		public Class<?> getInstantiatingType() {
			Type type = getClass().getGenericSuperclass();
			if (type instanceof ParameterizedType) {
				return ReflectionUtils.getRawType(((ParameterizedType) type).getActualTypeArguments()[0]);
			}
			return Object.class;
		}

		public String getDescription() {
			try {
				return "new " + typeFormatter.format(getClass().getMethod("newInstance", Node.class).getGenericReturnType()) + "()";
			} catch (Exception e) {
				e.printStackTrace();
				return super.toString();
			}
		}

		public String toString() {
			return getDescription();
		}
	}

	protected static class InstantiatorMenuItem extends JMenuItem {
		public final Instantiator<?>	instantiator;

		public InstantiatorMenuItem(Instantiator<?> instantiator) {
			super(instantiator.getDescription());
			this.instantiator = instantiator;
		}
	}

	protected static interface InstantiationListener {
		public void objectInstantiated(Object instantiatedObject);
	}

	protected class ArbitraryInstantiator implements Instantiator<Object> {
		@Override
		public Object newInstance(final Node target) throws Exception {
			String className = ClassChooserDialog.showDialog("Select type to create", ObjectTreeTable.this);
			if (className != null) {
				try {
					Class<?> clazz = Class.forName(className);

					final List<Instantiator<?>> instantiators = new ArrayList<Instantiator<?>>(ObjectTreeTable.this.instantiators.get(clazz));

					Collections.sort(instantiators, new Comparator<Instantiator<?>>() {
						@Override
						public int compare(Instantiator<?> first, Instantiator<?> second) {
							int result = first.getInstantiatingType().getSimpleName().compareTo(
									second.getInstantiatingType().getSimpleName());
							if (result != 0) {
								return result;
							}
							return first.getDescription().compareTo(second.getDescription());
						}
					});

					if (instantiators.size() > 1 || (!instantiators.isEmpty() &&
							instantiators.get(0).getInstantiatingType() != clazz)) {
						final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(ObjectTreeTable.this));
						dialog.setTitle("New Instance");
						dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
						JPanel buttonPanel = new JPanel();
						buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
						dialog.getContentPane().add(buttonPanel, BorderLayout.CENTER);
						dialog.setResizable(false);

						GridBagWizard gbw = GridBagWizard.create(buttonPanel);
						gbw.defaults().fillx(1.0);

						gbw.put(new JLabel("<html>Select instantiator for " + className + ":</html>")).xy(0, 0);
						gbw.put(Box.createVerticalStrut(10)).belowLast();

						final JButton[] buttons = new JButton[instantiators.size()];
						int i = 0;
						for (Instantiator<?> instantiator : instantiators) {
							buttons[i] = new JButton(instantiator.getDescription());
							gbw.put(buttons[i]).belowLast();
							i++;
						}

						gbw.put(Box.createVerticalStrut(10)).belowLast();

						final JButton cancelButton = new JButton("Cancel");
						gbw.put(cancelButton).belowLast();

						class SelectionHandler implements ActionListener {
							Object	newInstance;

							@Override
							public void actionPerformed(ActionEvent e) {
								dialog.setVisible(false);

								if (e.getSource() != cancelButton) {
									int index = ArrayUtils.strictIndexOf(buttons, e.getSource());

									Instantiator<?> instantiator = instantiators.get(index);
									try {
										newInstance = instantiator.newInstance(target);
									} catch (Exception e1) {
										handleInstantiationExecption(e1);
									}
								}
							}
						}
						;

						SelectionHandler selectionHandler = new SelectionHandler();

						cancelButton.addActionListener(selectionHandler);
						for (JButton button : buttons) {
							button.addActionListener(selectionHandler);
						}

						dialog.pack();
						dialog.setLocationRelativeTo(dialog.getOwner());
						dialog.setVisible(true);

						return selectionHandler.newInstance;
					} else if (instantiators.size() == 1) {
						return instantiators.get(0).newInstance(target);
					}

					return target.newInstance(Class.forName(className));
				} catch (Exception ex) {
					throw new RuntimeException("Unable to instantiate " + className, ex);
				}
			}
			return null;
		}

		@Override
		public String getDescription() {
			return "Enter class name...";
		}

		public Class<?> getInstantiatingType() {
			return null;
		}
	}
}
package org.andork.codegen.model;

import static com.schwab.codegen.NameUtils.cap;
import static com.schwab.codegen.NameUtils.constantify;
import static com.schwab.codegen.NameUtils.getElementSingularName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.schwab.att.core.model.gen.ModelTemplate;
import com.schwab.att.core.type.CompactTypeFormatter;
import com.schwab.att.core.type.ReflectionUtils;
import com.schwab.att.core.type.TypeFormatter;
import com.schwab.codegen.CodeBuilder;
import com.schwab.codegen.Model;
import com.schwab.codegen.CodeBuilder.Block;
import com.schwab.codegen.builder.BuilderIgnore;

public class ModelGenerator {
	public static void main(String[] args) {
		// System.out.println(new
		// ModelGenerator().generateModel(MultiLegChainResponse.class));
		System.out.println(new ModelGenerator().generateModel(TestTemplate.class));
	}

	private final Map<Class<?>, String>	nameMap			= new HashMap<Class<?>, String>();
	private final Map<Type, String>		fqNameMap		= new HashMap<Type, String>();

	private final TypeFormatter			typeFormatter	= new CompactTypeFormatter();

	private String						equalsMethod	= "Java16PlusMethods.equals";

	public Block generateModel(Class<?> templateType) {
		String typeDecl = Modifier.toString(templateType.getModifiers());
		if (templateType.isInterface()) {
			// Modifier.toString() already added "interface"
			typeDecl += " ";
		} else {
			typeDecl += " class ";
		}
		typeDecl += nameMap(templateType);
		if (templateType.getSuperclass() != null && templateType.getSuperclass() != Object.class) {
			typeDecl += " extends " + nameMap(templateType.getSuperclass());
		}

		Class<?>[] ifaces = templateType.getInterfaces();
		if (!templateType.isInterface()) {
			ifaces = Arrays.copyOf(ifaces, ifaces.length + 1);
			ifaces[ifaces.length - 1] = Model.class;
		}
		if (ifaces.length > 0) {
			typeDecl += " implements " + nameMap(ifaces[0]);
			for (int i = 1; i < ifaces.length; i++) {
				typeDecl += ", " + nameMap(ifaces[i]);
			}
		}

		Block typeBlock = CodeBuilder.newJavaBlock(typeDecl);

		if (!templateType.isInterface()) {
			Block propertiesBlock = typeBlock.newJavaBlock("public static enum Properties");
			StringBuffer lastLine = null;
			for (Field field : templateType.getDeclaredFields()) {
				if (ignoreField(field)) {
					continue;
				}
				StringBuffer line = propertiesBlock.addLine(constantify(field.getName()));
				if (lastLine != null) {
					lastLine.append(",");
				}
				lastLine = line;
			}
			if (lastLine != null) {
				lastLine.append(";");
			}

			typeBlock.addLine();

			for (Field field : templateType.getDeclaredFields()) {
				if (ignoreField(field)) {
					continue;
				}
				StringBuffer fieldLine = typeBlock.addLine();
				fieldLine.append(Modifier.toString(field.getModifiers()));
				fieldLine.append(" ").append(fqNameMap(field.getGenericType())).append(" ").append(field.getName()).append(";");
			}

			typeBlock.addLine();
			typeBlock.addLine("@BuilderIgnore");
			typeBlock.addLine("private HierarchicalBasicPropertyChangeSupport propertyChangeSupport = new HierarchicalBasicPropertyChangeSupport();");
			typeBlock.addLine();
			typeBlock.addLine("@BuilderIgnore");
			typeBlock.addLine("private HierarchicalBasicPropertyChangeListener childChangeListener = new HierarchicalBasicPropertyChangePropagator(this, propertyChangeSupport);");

			typeBlock.addLine();
			typeBlock.addLine("@Override");
			Block getPropChangeSupportBlock = typeBlock.newJavaBlock("public HierarchicalBasicPropertyChangeSupport getPropertyChangeSupport()");
			getPropChangeSupportBlock.addLine("return propertyChangeSupport;");
		}

		for (Field field : templateType.getDeclaredFields()) {
			if (ignoreField(field)) {
				continue;
			}
			StringBuffer getterLine = new StringBuffer();
			StringBuffer setterLine = new StringBuffer();

			String fieldTypeStr = fqNameMap(field.getGenericType());

			String setterTypeStr = fieldTypeStr;
			if (Collection.class.isAssignableFrom(field.getType())) {
				setterTypeStr = fqNameMap(ReflectionUtils.getAddAllableType(field.getGenericType()));
			}

			getterLine.append("public ").append(fieldTypeStr).append(" get").append(cap(field.getName())).append("()");
			setterLine.append("public void set").append(cap(field.getName())).append("(").append(setterTypeStr).append(" ").append(field.getName()).append(")");

			typeBlock.addLine();
			Block getterBlock = typeBlock.newJavaBlock(getterLine.toString());
			typeBlock.addLine();
			Block setterBlock = typeBlock.newJavaBlock(setterLine.toString());

			if (Collection.class.isAssignableFrom(field.getType())) {
				Type elemType = ReflectionUtils.getTypeParameter(field.getGenericType(), 0);
				if (elemType == null) {
					elemType = Object.class;
				}
				String elemTypeStr = fqNameMap(elemType);

				String elemNameSingular = getElementSingularName(field);

				String unmodTypeStr = "";
				if (List.class.isAssignableFrom(field.getType())) {
					unmodTypeStr = "List";
				} else if (Set.class.isAssignableFrom(field.getType())) {
					unmodTypeStr = "Set";
				}

				getterBlock.addLine("return Collections.unmodifiable" + unmodTypeStr + "(").append(field.getName()).append(");");

				if (isModelType(elemType)) {
					Block removeLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : this." + field.getName() + ")");
					removeLoop.addLine("a.getPropertyChangeSupport().removePropertyChangeListener(childChangeListener);");
				} else if (couldBeModelType(elemType)) {
					Block removeLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : this." + field.getName() + ")");
					Block ifModelBlock = removeLoop.newJavaBlock("if (a instanceof Model)");
					ifModelBlock.addLine("((Model) a).getPropertyChangeSupport().removePropertyChangeListener(childChangeListener);");
				}

				setterBlock.addLine("this.").append(field.getName()).append(".clear();");
				setterBlock.addLine("this.").append(field.getName()).append(".addAll(").append(field.getName()).append(");");

				if (isModelType(elemType)) {
					Block addLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : " + field.getName() + ")");
					addLoop.addLine("a.getPropertyChangeSupport().addPropertyChangeListener(childChangeListener);");
				} else if (couldBeModelType(elemType)) {
					Block addLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : " + field.getName() + ")");
					Block ifModelBlock = addLoop.newJavaBlock("if (a instanceof Model)");
					ifModelBlock.addLine("((Model) a).getPropertyChangeSupport().addPropertyChangeListener(childChangeListener);");
				}

				setterBlock.addLine("propertyChangeSupport.fireChildrenChanged(this);");

				typeBlock.addLine();
				Block addBlock = typeBlock.newJavaBlock("public void add" + cap(elemNameSingular) + "(" + elemTypeStr + " " + elemNameSingular + ")");

				Block ifAddedBlock = addBlock.newJavaBlock("if (this." + field.getName() + ".add(" + elemNameSingular + "))");

				if (isModelType(elemType)) {
					ifAddedBlock.addLine(elemNameSingular).append(".getPropertyChangeSupport().addPropertyChangeListener(childChangeListener);");
				} else if (couldBeModelType(elemType)) {
					Block ifModelBlock = ifAddedBlock.newJavaBlock("if (" + elemNameSingular + " instanceof Model)");
					ifModelBlock.addLine("((Model) ").append(elemNameSingular).append(").addPropertyChangeListener(childChangeListener);");
				}
				ifAddedBlock.addLine("propertyChangeSupport.fireChildAdded(this, ").append(elemNameSingular).append(");");

				typeBlock.addLine();
				Block removeBlock = typeBlock.newJavaBlock("public void remove" + cap(elemNameSingular) + "(" + elemTypeStr + " " + elemNameSingular + ")");
				Block ifRemovedBlock = removeBlock.newJavaBlock("if (this." + field.getName() + ".remove(" + elemNameSingular + "))");

				if (isModelType(elemType)) {
					ifRemovedBlock.addLine(elemNameSingular).append(".getPropertyChangeSupport().removePropertyChangeListener(childChangeListener);");
				} else if (couldBeModelType(elemType)) {
					Block ifModelBlock = ifRemovedBlock.newJavaBlock("if (" + elemNameSingular + " instanceof Model)");
					ifModelBlock.addLine("((Model) ").append(elemNameSingular).append(").getPropertyChangeSupport().removePropertyChangeListener(childChangeListener);");
				}
				ifRemovedBlock.addLine("propertyChangeSupport.fireChildRemoved(this, ").append(elemNameSingular).append(");");

			} else if (field.getType().isArray()) {

			} else {
				getterBlock.addLine("return ").append(field.getName()).append(";");
				StringBuffer ifStmt = new StringBuffer();
				ifStmt.append("if (!").append(equalsMethod).append("(this.").append(field.getName()).append(", ").append(field.getName()).append("))");
				Block equalsBlock = setterBlock.newJavaBlock(ifStmt.toString());

				if (isModelType(field.getType())) {
					Block removeListenerBlock = equalsBlock.newJavaBlock("if (this." + field.getName() + " != null)");
					removeListenerBlock.addLine("this.").append(field.getName()).append(".getPropertyChangeSupport().removePropertyChangeListener(childChangeListener);");
				}

				equalsBlock.addLine(fieldTypeStr).append(" old = this.").append(field.getName()).append(";");
				equalsBlock.addLine("this.").append(field.getName()).append(" = ").append(field.getName()).append(";");
				if (isModelType(field.getType())) {
					Block addListenerBlock = equalsBlock.newJavaBlock("if (" + field.getName() + " != null)");
					addListenerBlock.addLine(field.getName()).append(".getPropertyChangeSupport().addPropertyChangeListener(childChangeListener);");
				}
				equalsBlock.addLine("propertyChangeSupport.firePropertyChange(this, Properties.").append(constantify(field.getName())).append(", old, ").append(field.getName()).append(");");
			}
		}

		for (Class<?> enclosed : templateType.getDeclaredClasses()) {
			if (!isModelType(enclosed)) {
				continue;
			}

			typeBlock.addLine();

			typeBlock.addBlock(generateModel(enclosed));
		}

		return typeBlock;
	}

	protected boolean ignoreField(Field field) {
		return field.getAnnotation(BuilderIgnore.class) != null || Modifier.isStatic(field.getModifiers());
	}

	protected boolean isModelType(Type type) {
		return ReflectionUtils.getRawType(type).getAnnotation(ModelTemplate.class) != null;
	}

	protected boolean couldBeModelType(Type type) {
		return !ReflectionUtils.getRawType(type).isEnum() &&
				!ReflectionUtils.getRawType(type).getName().startsWith("java");
	}

	public String nameMap(Class<?> type) {
		String name = nameMap.get(type);
		if (name == null) {
			name = type.getSimpleName();
			nameMap.put(type, name);
		}
		return name;
	}

	public String fqNameMap(Type type) {
		String name = fqNameMap.get(type);
		if (name == null) {
			name = typeFormatter.format(type);
			fqNameMap.put(type, name);
		}
		return name;
	}
}

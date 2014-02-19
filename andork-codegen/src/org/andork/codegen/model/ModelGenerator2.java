package org.andork.codegen.model;

import static com.schwab.codegen.NameUtils.cap;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import com.schwab.att.core.event.HierarchicalBasicPropertyChangeListener;
import com.schwab.att.core.event.HierarchicalBasicPropertyChangePropagator;
import com.schwab.att.core.event.HierarchicalBasicPropertyChangeSupport;
import com.schwab.att.core.type.CompactTypeFormatter;
import com.schwab.att.core.type.ReflectionUtils;
import com.schwab.att.core.type.TypeFormatter;
import com.schwab.att.core.util.FileFinder;
import com.schwab.codegen.CodeBuilder2;
import com.schwab.codegen.CodeBuilder2.DeclBlock;
import com.schwab.codegen.CodeBuilder2.JavaBlock;
import com.schwab.codegen.Generated;
import com.schwab.codegen.LineUtils;
import com.schwab.codegen.Model;
import com.schwab.codegen.ModifiableCompilationUnit;
import com.schwab.codegen.ModifiableCompilationUnit.Modifier;
import com.schwab.codegen.NameUtils;
import com.schwab.codegen.builder.BuilderElementName;
import com.schwab.codegen.builder.BuilderIgnore;
import com.schwab.japa.JavaParserUtils;

public class ModelGenerator2 {
	public static void main(String[] args) throws Exception {
		Class<?> cls = TestTemplate2.class;
		File file = FileFinder.findFile(cls.getSimpleName() + ".java", new File("applet/src"), 10);
		List<String> lines = LineUtils.readLines(new FileInputStream(file));
		ModifiableCompilationUnit compUnit = new ModifiableCompilationUnit(lines);
		new ModelGenerator2().generate(cls, compUnit);

		System.out.println(compUnit.getCompilationUnit());

	}

	private final TypeFormatter	typeFormatter	= new CompactTypeFormatter();
	private String				equalsMethod	= "Common.equals";

	public void generate(Class<?> cls, ModifiableCompilationUnit unit) throws Exception {
		CompilationUnit u = unit.getCompilationUnit();

		Modifier modifier = unit.modify();

		if (isModelType(cls)) {
			insertLineIfMissing(unit, modifier, "import " + HierarchicalBasicPropertyChangeListener.class.getName() + ";", 1);
			insertLineIfMissing(unit, modifier, "import " + HierarchicalBasicPropertyChangePropagator.class.getName() + ";", 1);
			insertLineIfMissing(unit, modifier, "import " + HierarchicalBasicPropertyChangeSupport.class.getName() + ";", 1);
		}

		generate(cls, unit, modifier, (TypeDeclaration) JavaParserUtils.getDeclaration(u, cls));

		modifier.commit();
	}

	private void insertLineIfMissing(ModifiableCompilationUnit unit, Modifier modifier, String line, int index) {
		if (!unit.getLines().contains(line)) {
			modifier.insert(1, 0, line);
		}
	}

	private void generate(Class<?> cls, ModifiableCompilationUnit unit, Modifier modifier, TypeDeclaration typeDecl) throws SecurityException, NoSuchFieldException {
		String property = "Property";
		if (cls.getEnclosingClass() != null && !java.lang.reflect.Modifier.isStatic(cls.getModifiers())) {
			property = cls.getSimpleName() + "Property";
		}
		DeclBlock propertiesBlock = CodeBuilder2.newDeclBlock("public static enum " + property);
		propertiesBlock.addAnnotation("@" + Generated.class.getName());

		if (isModelType(cls)) {
			String newDecl;

			FieldDeclaration childChangeListenerDecl = (FieldDeclaration) JavaParserUtils.getNode(typeDecl, "childChangeListener");
			newDecl = "@" + Generated.class.getName() + "\nprivate HierarchicalBasicPropertyChangeListener childChangeListener = new HierarchicalBasicPropertyChangePropagator(this, propertyChangeSupport);";
			insertOrReplace(modifier, typeDecl, childChangeListenerDecl, newDecl);

			FieldDeclaration propertyChangeSupportDecl = (FieldDeclaration) JavaParserUtils.getNode(typeDecl, "propertyChangeSupport");
			newDecl = "@" + Generated.class.getName() + "\nprivate HierarchicalBasicPropertyChangeSupport propertyChangeSupport = new HierarchicalBasicPropertyChangeSupport();";
			insertOrReplace(modifier, typeDecl, propertyChangeSupportDecl, newDecl);

			MethodDeclaration changeSupportDecl = (MethodDeclaration) JavaParserUtils.getNode(typeDecl, "changeSupport()");
			newDecl = "@" + Generated.class.getName() + "\npublic HierarchicalBasicPropertyChangeSupport.External changeSupport() { return propertyChangeSupport.external(); }";
			insertOrReplace(modifier, typeDecl, changeSupportDecl, newDecl);
		}

		Field lastField = null;

		for (Field field : cls.getDeclaredFields()) {
			if (ignoreField(field)) {
				continue;
			}

			String name = field.getName();

			DeclBlock getterBlock = createGetter(field);
			MethodDeclaration getterDecl = (MethodDeclaration) JavaParserUtils.getNode(typeDecl, "get" + NameUtils.cap(name) + "()");
			insertOrReplace(modifier, typeDecl, getterDecl, getterBlock.toString());

			DeclBlock setterBlock = createSetter(field, property);
			MethodDeclaration setterDecl = (MethodDeclaration) JavaParserUtils.getNode(typeDecl, "set" + NameUtils.cap(name) + "(java.util.Collection)");
			insertOrReplace(modifier, typeDecl, setterDecl, setterBlock.toString());

			if (List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType())) {
				Type elemType = ReflectionUtils.getTypeParameter(field.getGenericType(), 0);
				if (elemType == null) {
					elemType = Object.class;
				}

				String elemNameSingular = NameUtils.singularize(field.getName());
				BuilderElementName elemName = field.getAnnotation(BuilderElementName.class);
				if (elemName != null) {
					elemNameSingular = elemName.singular();
				}

				DeclBlock addBlock = createAdder(field);
				MethodDeclaration adderDecl = (MethodDeclaration) JavaParserUtils.getNode(typeDecl, "add" + NameUtils.cap(elemNameSingular) + "(" + JavaParserUtils.formatParameterType(ReflectionUtils.getRawType(elemType)) + ")");
				insertOrReplace(modifier, typeDecl, adderDecl, addBlock.toString());

				DeclBlock removeBlock = createRemover(field);
				MethodDeclaration removerDecl = (MethodDeclaration) JavaParserUtils.getNode(typeDecl, "remove" + NameUtils.cap(elemNameSingular) + "(" + JavaParserUtils.formatParameterType(ReflectionUtils.getRawType(elemType)) + ")");
				insertOrReplace(modifier, typeDecl, removerDecl, removeBlock.toString());
			}

			if (lastField != null) {
				propertiesBlock.addLine(NameUtils.constantify(lastField.getName()) + ",");
			}
			lastField = field;
		}

		if (lastField != null) {
			propertiesBlock.addLine(NameUtils.constantify(lastField.getName()) + ";");
		}

		TypeDeclaration propertiesDecl = null;
		if (cls.getEnclosingClass() != null && !java.lang.reflect.Modifier.isStatic(cls.getModifiers())) {
			TypeDeclaration enclosingDecl = JavaParserUtils.getDeclaration(unit.getCompilationUnit(), cls.getEnclosingClass());
			if (enclosingDecl != null) {
				propertiesDecl = (TypeDeclaration) JavaParserUtils.getNode(enclosingDecl, property);
				insertOrReplace(modifier, enclosingDecl, propertiesDecl, propertiesBlock.toString());
			}
		} else {
			propertiesDecl = (TypeDeclaration) JavaParserUtils.getNode(typeDecl, property);
			insertOrReplace(modifier, typeDecl, propertiesDecl, propertiesBlock.toString());
		}
	}

	protected final void insertOrReplace(Modifier modifier, TypeDeclaration parentDecl, BodyDeclaration bodyDecl, String code) {
		if (bodyDecl != null) {
			modifier.replace(LineUtils.fullRegion(bodyDecl), code);
		} else {
			modifier.insert(parentDecl.getEndLine() - 1, parentDecl.getEndColumn() - 1, code);
		}
	}

	protected DeclBlock createGetter(Field field) {
		String name = field.getName();
		String ftype = typeFormatter.format(field.getType());

		DeclBlock getterBlock;

		if (List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType())) {
			Type elemType = ReflectionUtils.getTypeParameter(field.getGenericType(), 0);
			if (elemType == null) {
				elemType = Object.class;
			}
			String elemTypeStr = typeFormatter.format(elemType);

			String ifaceTypeStr;
			if (List.class.isAssignableFrom(field.getType())) {
				ifaceTypeStr = "List";
			} else {
				ifaceTypeStr = "Set";
			}

			String fqIfaceTypeStr = "java.util." + ifaceTypeStr;

			getterBlock = CodeBuilder2.newDeclBlock("public " + fqIfaceTypeStr + "<" + elemTypeStr + "> get" + cap(name) + "()");

			getterBlock.addLine("return Collections.<" + elemTypeStr + ">unmodifiable" + ifaceTypeStr + "(").append(name).append(");");
		} else {
			getterBlock = CodeBuilder2.newDeclBlock("public " + ftype + " get" + cap(name) + "()");
			getterBlock.addLine("return " + name + ";");
		}
		getterBlock.addAnnotation("@" + Generated.class.getName());
		return getterBlock;
	}

	protected DeclBlock createSetter(Field field, String property) {
		String name = field.getName();
		String ftype = typeFormatter.format(field.getType());

		DeclBlock setterBlock = null;

		if (List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType())) {
			Type elemType = ReflectionUtils.getTypeParameter(field.getGenericType(), 0);
			if (elemType == null) {
				elemType = Object.class;
			}
			String elemTypeStr = typeFormatter.format(elemType);

			String ifaceTypeStr;
			if (List.class.isAssignableFrom(field.getType())) {
				ifaceTypeStr = "List";
			} else {
				ifaceTypeStr = "Set";
			}

			setterBlock = CodeBuilder2.newDeclBlock("public void set" + cap(name) + "(Collection<? extends " + elemTypeStr + "> " + name + ")");

			if (isModelType(field.getDeclaringClass())) {
				if (isModelType(elemType)) {
					JavaBlock removeLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : this." + field.getName() + ")");
					removeLoop.addLine("a.changeSupport().removePropertyChangeListener(childChangeListener);");
				} else if (couldBeModelType(elemType)) {
					JavaBlock removeLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : this." + field.getName() + ")");
					JavaBlock ifModelBlock = removeLoop.newJavaBlock("if (a instanceof Model)");
					ifModelBlock.addLine("((Model) a).changeSupport().removePropertyChangeListener(childChangeListener);");
				}
			}

			setterBlock.addLine("this.").append(field.getName()).append(".clear();");
			setterBlock.addLine("this.").append(field.getName()).append(".addAll(").append(field.getName()).append(");");

			if (isModelType(field.getDeclaringClass())) {
				if (isModelType(elemType)) {
					JavaBlock addLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : " + field.getName() + ")");
					addLoop.addLine("a.changeSupport().addPropertyChangeListener(childChangeListener);");
				} else if (couldBeModelType(elemType)) {
					JavaBlock addLoop = setterBlock.newJavaBlock("for (" + elemTypeStr + " a : " + field.getName() + ")");
					JavaBlock ifModelBlock = addLoop.newJavaBlock("if (a instanceof Model)");
					ifModelBlock.addLine("((Model) a).changeSupport().addPropertyChangeListener(childChangeListener);");
				}

				setterBlock.addLine("propertyChangeSupport.fireChildrenChanged(this);");
			}
		} else if (field.getType().isArray()) {

		} else {
			setterBlock = CodeBuilder2.newDeclBlock("public void set" + cap(name) + "(" + ftype + " " + name + ")");

			StringBuffer ifStmt = new StringBuffer();
			ifStmt.append("if (!").append(equalsMethod).append("(this.").append(field.getName()).append(", ").append(field.getName()).append("))");
			JavaBlock equalsBlock = setterBlock.newJavaBlock(ifStmt.toString());

			if (isModelType(field.getDeclaringClass()) && isModelType(field.getType())) {
				JavaBlock removeListenerBlock = equalsBlock.newJavaBlock("if (this." + field.getName() + " != null)");
				removeListenerBlock.addLine("this.").append(field.getName()).append(".changeSupport().removePropertyChangeListener(childChangeListener);");
			}

			equalsBlock.addLine(ftype).append(" old = this.").append(field.getName()).append(";");
			equalsBlock.addLine("this.").append(field.getName()).append(" = ").append(field.getName()).append(";");
			if (isModelType(field.getDeclaringClass())) {
				if (isModelType(field.getType())) {
					JavaBlock addListenerBlock = equalsBlock.newJavaBlock("if (" + field.getName() + " != null)");
					addListenerBlock.addLine(field.getName()).append(".changeSupport().addPropertyChangeListener(childChangeListener);");
				}
				equalsBlock.addLine("propertyChangeSupport.firePropertyChange(this, ").append(property).append(".").append(NameUtils.constantify(field.getName())).append(", old, ").append(field.getName()).append(");");
			}
		}

		setterBlock.addAnnotation("@" + Generated.class.getName());
		return setterBlock;
	}

	protected DeclBlock createAdder(Field field) {
		Type elemType = ReflectionUtils.getTypeParameter(field.getGenericType(), 0);
		if (elemType == null) {
			elemType = Object.class;
		}
		String elemTypeStr = typeFormatter.format(elemType);

		String elemNameSingular = NameUtils.singularize(field.getName());
		BuilderElementName elemName = field.getAnnotation(BuilderElementName.class);
		if (elemName != null) {
			elemNameSingular = elemName.singular();
		}

		DeclBlock addBlock = CodeBuilder2.newDeclBlock("public void add" + cap(elemNameSingular) + "(" + elemTypeStr + " " + elemNameSingular + ")");

		if (isModelType(field.getDeclaringClass())) {
			JavaBlock ifAddedBlock = addBlock.newJavaBlock("if (this." + field.getName() + ".add(" + elemNameSingular + "))");

			if (isModelType(elemType)) {
				ifAddedBlock.addLine(elemNameSingular).append(".changeSupport().addPropertyChangeListener(childChangeListener);");
			} else if (couldBeModelType(elemType)) {
				JavaBlock ifModelBlock = ifAddedBlock.newJavaBlock("if (" + elemNameSingular + " instanceof Model)");
				ifModelBlock.addLine("((Model) ").append(elemNameSingular).append(").changeSupport().addPropertyChangeListener(childChangeListener);");
			}
			ifAddedBlock.addLine("propertyChangeSupport.fireChildAdded(this, ").append(elemNameSingular).append(");");
		} else {
			addBlock.addLine("this.").append(field.getName()).append(".add(").append(elemNameSingular).append(");");
		}

		addBlock.addAnnotation("@" + Generated.class.getName());
		return addBlock;
	}

	protected DeclBlock createRemover(Field field) {
		Type elemType = ReflectionUtils.getTypeParameter(field.getGenericType(), 0);
		if (elemType == null) {
			elemType = Object.class;
		}
		String elemTypeStr = typeFormatter.format(elemType);

		String elemNameSingular = NameUtils.singularize(field.getName());
		BuilderElementName elemName = field.getAnnotation(BuilderElementName.class);
		if (elemName != null) {
			elemNameSingular = elemName.singular();
		}

		DeclBlock removeBlock = CodeBuilder2.newDeclBlock("public void remove" + cap(elemNameSingular) + "(" + elemTypeStr + " " + elemNameSingular + ")");

		if (isModelType(field.getDeclaringClass())) {
			JavaBlock ifRemovedBlock = removeBlock.newJavaBlock("if (this." + field.getName() + ".remove(" + elemNameSingular + "))");

			if (isModelType(elemType)) {
				ifRemovedBlock.addLine(elemNameSingular).append(".changeSupport().removePropertyChangeListener(childChangeListener);");
			} else if (couldBeModelType(elemType)) {
				JavaBlock ifModelBlock = ifRemovedBlock.newJavaBlock("if (" + elemNameSingular + " instanceof Model)");
				ifModelBlock.addLine("((Model) ").append(elemNameSingular).append(").changeSupport().removePropertyChangeListener(childChangeListener);");
			}
			ifRemovedBlock.addLine("propertyChangeSupport.fireChildRemoved(this, ").append(elemNameSingular).append(");");
		} else {
			removeBlock.addLine("this.").append(field.getName()).append(".remove(").append(elemNameSingular).append(");");
		}

		removeBlock.addAnnotation("@" + Generated.class.getName());
		return removeBlock;
	}

	protected boolean ignoreField(Field field) {
		return field.getAnnotation(Generated.class) != null ||
				field.getAnnotation(BuilderIgnore.class) != null ||
				java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
				field.getName().startsWith("this$");
	}

	protected boolean isModelType(Type type) {
		return Model.class.isAssignableFrom(ReflectionUtils.getRawType(type));
	}

	protected boolean couldBeModelType(Type type) {
		return !ReflectionUtils.getRawType(type).isEnum() &&
				!ReflectionUtils.getRawType(type).getName().startsWith("java");
	}

}

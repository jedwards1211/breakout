package org.andork.japa;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.util.StringUtils;

public class JavaParserUtils {

	private static final Pattern	parameterPattern		= Pattern.compile("([a-zA-Z_][a-zA-Z_0-9]*(\\.[a-zA-Z_][a-zA-Z_0-9]*)*)(\\[\\])*");
	private static final Pattern	parameterListPattern	= Pattern.compile("\\s*\\(\\s*(" + parameterPattern + "(\\s*,\\s*" + parameterPattern + ")*\\s*)?\\)\\s*");

	public static String getQualifiedName(Field field) {
		StringBuffer sb = new StringBuffer();
		sb.append(field.getDeclaringClass().getName()).append('.').append(field.getName());
		return sb.toString();
	}

	public static String getQualifiedName(Constructor<?> constructor) {
		StringBuffer sb = new StringBuffer();
		sb.append(constructor.getDeclaringClass().getName()).append('.').append(constructor.getName()).append('(');
		Class<?>[] parameters = constructor.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(formatParameterType(parameters[i]));
		}
		return sb.append(')').toString();
	}

	public static String getQualifiedName(Method method) {
		StringBuffer sb = new StringBuffer();
		sb.append(method.getDeclaringClass().getName()).append('.').append(method.getName()).append('(');
		Class<?>[] parameters = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(formatParameterType(parameters[i]));
		}
		return sb.append(')').toString();
	}

	public static Node getNode(CompilationUnit unit, String fqName) {
		String pkg = getQualifiedName(unit.getPackage().getName());
		if (!fqName.startsWith(pkg)) {
			return null;
		}
		if (fqName.equals(pkg)) {
			return unit.getPackage();
		}

		String name = fqName.substring(pkg.length() + 1);

		for (TypeDeclaration typeDecl : unit.getTypes()) {
			if (name.startsWith(typeDecl.getName())) {
				if (name.length() == typeDecl.getName().length()) {
					return typeDecl;
				} else {
					return getNode(typeDecl, name.substring(typeDecl.getName().length() + 1));
				}
			}
		}

		return null;
	}

	public static TypeDeclaration getDeclaration(CompilationUnit unit, Class<?> cls) {
		if (cls.getDeclaringClass() != null) {
			Node node = getDeclaration(unit, cls.getDeclaringClass());
			if (node instanceof TypeDeclaration) {
				for (BodyDeclaration decl : ((TypeDeclaration) node).getMembers()) {
					if (decl instanceof TypeDeclaration) {
						TypeDeclaration typeDecl = (TypeDeclaration) decl;
						if (typeDecl.getName().equals(cls.getSimpleName())) {
							return typeDecl;
						}
					}
				}
			}
		} else {
			for (TypeDeclaration typeDecl : unit.getTypes()) {
				if (cls.getName().equals(getQualifiedName(unit.getPackage().getName()) + "." + typeDecl.getName())) {
					return typeDecl;
				}
			}
		}
		return null;
	}

	public static FieldDeclaration getDeclaration(CompilationUnit unit, Field field) {
		TypeDeclaration typeDecl = getDeclaration(unit, field.getDeclaringClass());
		if (typeDecl == null) {
			return null;
		}
		for (BodyDeclaration decl : typeDecl.getMembers()) {
			if (decl instanceof FieldDeclaration) {
				FieldDeclaration fieldDecl = (FieldDeclaration) decl;
				List<VariableDeclarator> variables = fieldDecl.getVariables();
				if (variables.size() != 1) {
					continue;
				}
				if (field.getName().equals(variables.get(0).getId().getName())) {
					return fieldDecl;
				}
			}
		}
		return null;
	}

	public static ConstructorDeclaration getDeclaration(CompilationUnit unit, Constructor<?> constructor) {
		TypeDeclaration typeDecl = getDeclaration(unit, constructor.getDeclaringClass());
		if (typeDecl == null) {
			return null;
		}
		for (BodyDeclaration decl : typeDecl.getMembers()) {
			if (decl instanceof ConstructorDeclaration) {
				ConstructorDeclaration constructorDecl = (ConstructorDeclaration) decl;
				if (constructorDeclMatches(constructorDecl, constructor)) {
					return constructorDecl;
				}
			}
		}
		return null;
	}

	public static MethodDeclaration getDeclaration(CompilationUnit unit, Method method) {
		TypeDeclaration typeDecl = getDeclaration(unit, method.getDeclaringClass());
		if (typeDecl == null) {
			return null;
		}
		for (BodyDeclaration decl : typeDecl.getMembers()) {
			if (decl instanceof MethodDeclaration) {
				MethodDeclaration methodDecl = (MethodDeclaration) decl;
				if (methodDeclMatches(methodDecl, method)) {
					return methodDecl;
				}
			}
		}
		return null;
	}

	public static AnnotationExpr getAnnotation(BodyDeclaration decl, Class<?> annotationClass) {
		for (AnnotationExpr annotation : decl.getAnnotations()) {
			if (annotation.getName().equals(annotationClass.getSimpleName()) ||
					annotation.getName().equals(annotationClass.getName())) {
				return annotation;
			}
		}

		return null;
	}

	public static Object formatParameterType(Class<?> cls) {
		return formatParameterType(cls, true);
	}

	public static Object formatParameterType(Class<?> cls, boolean fullyQualified) {
		if (cls.isArray()) {
			return formatParameterType(cls.getComponentType(), fullyQualified) + "[]";
		}
		if (fullyQualified) {
			return cls.getName().replace('$', '.');
		}
		return cls.getSimpleName();
	}

	private static String getQualifiedName(NameExpr name) {
		if (name instanceof QualifiedNameExpr) {
			return getQualifiedName(((QualifiedNameExpr) name).getQualifier()) + '.' + name.getName();
		}
		return name.getName();
	}

	public static Node getNode(TypeDeclaration typeDecl, String name) {
		if (typeDecl.getMembers() == null) {
			return null;
		}
		for (BodyDeclaration bodyDecl : typeDecl.getMembers()) {
			String bodyDeclName = getName(bodyDecl);
			if (bodyDeclName != null && name.startsWith(bodyDeclName)) {
				if (name.length() == bodyDeclName.length()) {
					return bodyDecl;
				}
				else if (bodyDecl instanceof MethodDeclaration || bodyDecl instanceof ConstructorDeclaration) {
					if (signatureMatches(bodyDecl, name.substring(bodyDeclName.length()))) {
						return bodyDecl;
					}
				}
				else if (bodyDecl instanceof TypeDeclaration) {
					return getNode((TypeDeclaration) bodyDecl, name.substring(bodyDeclName.length() + 1));
				}
			}
		}
		return null;
	}

	private static boolean signatureMatches(BodyDeclaration decl, String signature) {
		if (!parameterListPattern.matcher(signature).matches()) {
			return false;
		}
		Matcher m = parameterPattern.matcher(signature);

		List<Parameter> parameters = null;

		if (decl instanceof ConstructorDeclaration) {
			parameters = ((ConstructorDeclaration) decl).getParameters();
		} else if (decl instanceof MethodDeclaration) {
			parameters = ((MethodDeclaration) decl).getParameters();
		} else {
			throw new IllegalArgumentException("decl must be a ConstructorDeclaration or a MethodDeclaration");
		}

		if (parameters == null)
		{
			parameters = Collections.emptyList();
		}

		int i = 0;
		while (m.find()) {
			if (i >= parameters.size()) {
				return false;
			}
			Parameter parameter = parameters.get(i);
			String typeName = getRawTypeName(parameter.getType());
			if (!m.group().endsWith(typeName)) {
				return false;
			}
			i++;
		}

		return i == parameters.size();
	}

	private static String getRawTypeName(Type type) {
		if (type instanceof ClassOrInterfaceType) {
			return getQualifiedName((ClassOrInterfaceType) type);
		}
		if (type instanceof ReferenceType) {
			ReferenceType refType = (ReferenceType) type;
			return getRawTypeName(refType.getType()) + StringUtils.multiply("[]", refType.getArrayCount());
		}
		if (type instanceof PrimitiveType) {
			return ((PrimitiveType) type).getType().name().toLowerCase();
		}
		if (type instanceof VoidType) {
			return "void";
		}
		if (type instanceof WildcardType) {
			return "java.lang.Object";
		}
		return type.toString();
	}

	private static String getQualifiedName(ClassOrInterfaceType type) {
		if (type.getScope() != null) {
			return getQualifiedName(type.getScope()) + "." + type.getName();
		}
		return type.getName();
	}

	private static String getName(BodyDeclaration decl) {
		if (decl instanceof AnnotationMemberDeclaration) {
			return ((AnnotationMemberDeclaration) decl).getName();
		}
		if (decl instanceof ConstructorDeclaration) {
			return ((ConstructorDeclaration) decl).getName();
		}
		if (decl instanceof EnumConstantDeclaration) {
			return ((EnumConstantDeclaration) decl).getName();
		}
		if (decl instanceof FieldDeclaration) {
			List<VariableDeclarator> variables = ((FieldDeclaration) decl).getVariables();
			if (variables.size() == 1) {
				return variables.get(0).getId().getName();
			} else {
				return null;
			}
		}
		if (decl instanceof MethodDeclaration) {
			return ((MethodDeclaration) decl).getName();
		}
		if (decl instanceof AnnotationDeclaration) {
			return ((AnnotationDeclaration) decl).getName();
		}
		if (decl instanceof ClassOrInterfaceDeclaration) {
			return ((ClassOrInterfaceDeclaration) decl).getName();
		}
		if (decl instanceof EnumDeclaration) {
			return ((EnumDeclaration) decl).getName();
		}
		return null;
	}

	private static boolean methodDeclMatches(MethodDeclaration methodDecl, Method method) {
		if (!method.getName().equals(methodDecl.getName())) {
			return false;
		}

		if (!typeMatches(getRawTypeName(methodDecl.getType()), method.getReturnType())) {
			return false;
		}

		List<Parameter> declParams = methodDecl.getParameters();
		Class<?>[] methodParams = method.getParameterTypes();

		if (declParams.size() != methodParams.length) {
			return false;
		}

		for (int i = 0; i < declParams.size(); i++) {
			if (!typeMatches(getRawTypeName(declParams.get(i).getType()), methodParams[i])) {
				return false;
			}
		}

		return true;
	}

	private static boolean constructorDeclMatches(ConstructorDeclaration constructorDecl, Constructor<?> constructor) {
		if (!constructor.getName().equals(constructorDecl.getName())) {
			return false;
		}

		List<Parameter> declParams = constructorDecl.getParameters();
		Class<?>[] constructorParams = constructor.getParameterTypes();

		if (declParams.size() != constructorParams.length) {
			return false;
		}

		for (int i = 0; i < declParams.size(); i++) {
			if (!typeMatches(getRawTypeName(declParams.get(i).getType()), constructorParams[i])) {
				return false;
			}
		}

		return true;
	}

	private static boolean typeMatches(String rawTypeName, Class<?> returnType) {
		return rawTypeName.equals(formatParameterType(returnType, true)) || rawTypeName.equals(formatParameterType(returnType, false));
	}

}

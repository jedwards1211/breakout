package org.andork.codegen.builder;

import static org.andork.codegen.NameUtils.a;
import static org.andork.codegen.NameUtils.cap;
import static org.andork.codegen.NameUtils.getElementPluralName;
import static org.andork.codegen.NameUtils.getElementSingularName;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.andork.codegen.CodeBuilder;
import org.andork.codegen.CodeBuilder.Block;
import org.andork.codegen.LineRegion;

/**
 * Does some Judo to generate builders for interrelated classes.<br>
 * To use it, just toss all the classes you want to generate builders for into {@link #generateBuilders(Class...)}, and see if it works!
 * 
 * @author james.a.edwards
 */
public class BuilderGenerator
{
	public static final boolean	DRY_RUN	= false;
	
	public static void main( String[ ] args ) throws Exception
	{
		// String[] builders = generateBuilders(
		// AbstractOrderActionMessage.class,
		// AbstractEnterOrChangeOrderMessage.class,
		// AbstractEnterOrChangeOrderMessage.Leg.class,
		// AbstractEnterOrChangeOrderMessage.VSPInst.class,
		// OrdEnterMessage.class,
		// OrdCancelMessage.class,
		// OrdChangeMessage.class,
		// OrdStateMessage.class);
		
		String[ ] builders = new String[ 0 ];
		
		// String[] builders = generateBuilders(
		// MultiLegRequest.class,
		// MultiLegRequestWithEcho.class,
		// MultiLegChainRequest.class,
		// MultiLegChainRequest.Leg.class,
		// MultiLegCancelRequest.class);
		
		// String[] builders = generateBuilders(
		// MultiLegResponse.class,
		// LegQuoteResponse.class,
		// DerivedQuoteResponse.class);
		
		// String[] builders = generateBuilders(
		// // LegQuoteResponse.class,
		// DerivedQuoteResponse.class,
		// MultiLegResponse.class,
		// MultiLegErrorResponse.class,
		// // MultiLegResponseWithServerSideId.class,
		// DefaultStrategyResponse.class,
		// DefaultStrategyResponse.Leg.class);
		// // MultiLegChainResponse.class,
		// // MultiLegChainResponse.ChainRoot.class,
		// // MultiLegChainResponse.Leg.class,
		// // MultiLegChainResponse.Menu.class,
		// // MultiLegChainResponse.RootGroup.class,
		// // MultiLegChainResponse.StrategyInstance.class,
		// // MultiLegChainResponse.ExpirationGroup.class);
		
		// String[] builders = generateBuilders(
		// MultiLegResponse.class,
		// DefaultStrategyResponse.class,
		// MultiLegResponseWithServerSideId.class,
		// MultiLegChainResponse.class);
		
		// String[] builders = generateBuilders(
		// MultiLegResponse.class,
		// MultiLegErrorResponse.class);
		
		// String[] builders = generateBuilders(
		// MultiLegChainResponse.Menu.class,
		// MultiLegChainResponse.StrikesPerExpirationMenu.class,
		// MultiLegMessages.PreparsedMenu.class);
		// String[] builders = generateBuilders(
		// MultiLegChainResponse.Menu.class,
		// MultiLegChainResponse.ExpirationGroupListMenu.class,
		// MultiLegMessages.PreparsedMenu.class);
		
		// String[] builders = generateBuilders(
		// SpecifiedLot.class);
		
		// String[] builders = generateBuilders(LegQuoteResponse.class);
		
		for( String builder : builders )
		{
			if( DRY_RUN )
			{
				System.out.println( builder );
			}
			else
			{
				try
				{
					writeBuilder( builder );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
		}
	}
	
	private static String toString( Type type )
	{
		if( type instanceof ParameterizedType )
		{
			ParameterizedType paramType = ( ParameterizedType ) type;
			Type[ ] typeArgs = paramType.getActualTypeArguments( );
			StringBuffer sb = new StringBuffer( );
			sb.append( ( ( Class<?> ) paramType.getRawType( ) ).getSimpleName( ) );
			sb.append( '<' );
			for( int i = 0 ; i < typeArgs.length ; i++ )
			{
				if( i > 0 )
				{
					sb.append( ',' );
				}
				sb.append( toString( typeArgs[ i ] ) );
			}
			sb.append( '>' );
			return sb.toString( );
		}
		else if( type instanceof Class )
		{
			return ( ( Class<?> ) type ).getSimpleName( );
		}
		return type.toString( );
	}
	
	private static String getTypeName( Field field )
	{
		if( field.getGenericType( ) != null )
		{
			return toString( field.getGenericType( ) );
		}
		else
		{
			return field.getType( ).getSimpleName( );
		}
	}
	
	private static String getListElementTypeName( Field field )
	{
		if( field.getGenericType( ) != null && field.getGenericType( ) instanceof ParameterizedType )
		{
			return toString( ( ( ParameterizedType ) field.getGenericType( ) ).getActualTypeArguments( )[ 0 ] );
		}
		return "java.lang.Object";
	}
	
	private static String getMapKeyTypeName( Field field )
	{
		if( field.getGenericType( ) != null && field.getGenericType( ) instanceof ParameterizedType )
		{
			return toString( ( ( ParameterizedType ) field.getGenericType( ) ).getActualTypeArguments( )[ 0 ] );
		}
		return "java.lang.Object";
	}
	
	private static String getMapValueTypeName( Field field )
	{
		if( field.getGenericType( ) != null && field.getGenericType( ) instanceof ParameterizedType )
		{
			return toString( ( ( ParameterizedType ) field.getGenericType( ) ).getActualTypeArguments( )[ 1 ] );
		}
		return "java.lang.Object";
	}
	
	/**
	 * Generates builder code for a group of types using reflection. <br>
	 * <br>
	 * <b>Features</b>:
	 * <ul>
	 * <li>A builder will have setters for each field in its target class/superclasses except fields annotated with {@link BuilderIgnore}.
	 * <li>A builder will have {@code add} methods for {@link List} fields in its target class/superclasses. The {@code List}s must have a non-wildcard generic
	 * type argument.
	 * <li>A builder's {@code create()} method will throw an exception if any field was not set (except for fields/fields of classes annotated with
	 * {@link BuilderAllowNull}).
	 * <li>A builder's {@code create()} method will call any methods in its target class/superclasses that are annotated with
	 * <li>A builder interface will be generated for each class for which a subclass is also provided in {@code types}. It will extend any builder interfaces
	 * generated for superclasses.
	 * <li>A concrete builder class will be generated for each concrete class. It will implement any builder interfaces generated for superclasses.
	 * {@link BuilderValidator}.
	 * <li>
	 * If two types share a common ancestor, and the ancestor is provided, a common builder interface for those types will be generated.
	 * </ul>
	 * 
	 * @param types
	 *            the types to generate builder code for. If you want common interfaces for builders for types with a common ancestor, call this method with
	 *            those types and the common ancestors together.
	 * @return an array of {@code String}s, each containing builder code for the corresponding type in {@code types}.
	 */
	public static String[ ] generateBuilders( Class<?> ... types )
	{
		String[ ] result = new String[ types.length ];
		
		String bgLink = "{@link " + BuilderGenerator.class.getName( ) + " " + BuilderGenerator.class.getSimpleName( ) + "}";
		
		Class<?>[ ] superclasses = new Class<?>[ types.length ];
		boolean[ ] hasSubclasses = new boolean[ types.length ];
		
		for( int i = 0 ; i < types.length ; i++ )
		{
			for( int j = i + 1 ; j < types.length ; j++ )
			{
				if( types[ i ].isAssignableFrom( types[ j ] ) )
				{
					if( superclasses[ j ] == null || superclasses[ j ].isAssignableFrom( types[ i ] ) )
					{
						superclasses[ j ] = types[ i ];
						hasSubclasses[ i ] = true;
					}
				}
			}
		}
		
		for( int i = 0 ; i < types.length ; i++ )
		{
			Block topBlock = CodeBuilder.newBlock( );
			
			Class<?> type = types[ i ];
			Class<?> superclass = superclasses[ i ];
			String typeName = type.getSimpleName( );
			String superclassName = superclass == null ? null : superclass.getSimpleName( );
			boolean isAbstract = Modifier.isAbstract( type.getModifiers( ) );
			
			if( i > 0 )
			{
				topBlock.addLine( );
				topBlock.addLine( );
			}
			
			topBlock.addLine( "@fullname(\"" + type.getName( ) + "\")" );
			Block typeBlock = topBlock.newJavaBlock( "class " + typeName );
			
			if( hasSubclasses[ i ] )
			{
				Block ifaceJavadoc = typeBlock.newJavadocBlock( );
				StringBuffer line = ifaceJavadoc.addLine( "Interface for builders for" );
				if( isAbstract )
				{
					line.append( " subclasses of {@code " ).append( typeName ).append( "}." );
				}
				else
				{
					line.append( " {@code " ).append( typeName ).append( "} and subclasses." );
				}
				ifaceJavadoc.addLine( "<br><br>" );
				ifaceJavadoc.addLine( "<i>This interface was auto-generated by " + bgLink + ".</i>" );
				
				String interfaceHeader = "public static interface IBuilder";
				if( superclass != null )
				{
					interfaceHeader += " extends " + superclassName + ".IBuilder";
				}
				Block ifaceBlock = typeBlock.newJavaBlock( interfaceHeader );
				
				for( Field field : type.getDeclaredFields( ) )
				{
					if( field.getAnnotation( BuilderIgnore.class ) != null )
					{
						continue;
					}
					
					if( Modifier.isStatic( field.getModifiers( ) ) )
					{
						continue;
					}
					Class<?> ftype = field.getType( );
					String fieldName = field.getName( );
					String ftypeName = getTypeName( field );
					if( ftype.isAssignableFrom( ArrayList.class ) )
					{
						if( !( field.getGenericType( ) instanceof ParameterizedType ) )
						{
							continue;
						}
						
						String elemName = getElementSingularName( field );
						String elemsName = getElementPluralName( field );
						String listTypeName = getListElementTypeName( field );
						
						Block setJavadoc = ifaceBlock.newJavadocBlock( );
						setJavadoc.addLine( "Sets the " + elemsName + " of the result." );
						setJavadoc.addLine( );
						setJavadoc.addLine( "@param " + elemsName );
						setJavadoc.addLine( "\t\tThe " + elemsName + " to set." );
						setJavadoc.addLine( "@return" );
						setJavadoc.addLine( "\t\tThis {@code IBuilder}, for chaining." );
						
						ifaceBlock.addLine( "public abstract IBuilder set" + cap( elemsName ) + "(Collection<? extends " + listTypeName + "> " + elemsName + ");" );
						
						Block addJavadoc = ifaceBlock.newJavadocBlock( );
						addJavadoc.addLine( "Adds " + a( elemName ) + " to the result." );
						addJavadoc.addLine( );
						addJavadoc.addLine( "@param " + elemName );
						addJavadoc.addLine( "\t\tThe " + elemName + " to add." );
						addJavadoc.addLine( "@return" );
						addJavadoc.addLine( "\t\tThis {@code IBuilder}, for chaining." );
						
						ifaceBlock.addLine( "public abstract IBuilder add" + cap( elemName ) + "(" + listTypeName + " " + elemName + ");" );
						
						Block addAllJavadoc = ifaceBlock.newJavadocBlock( );
						addAllJavadoc.addLine( "Adds multiple " + elemsName + " to the result." );
						addAllJavadoc.addLine( );
						addAllJavadoc.addLine( "@param " + elemsName );
						addAllJavadoc.addLine( "\t\tThe " + elemsName + " to add." );
						addAllJavadoc.addLine( "@return" );
						addAllJavadoc.addLine( "\t\tThis {@code IBuilder}, for chaining." );
						
						ifaceBlock.addLine( "public abstract IBuilder add" + cap( elemsName ) + "(Collection<? extends " + listTypeName + "> " + elemsName + ");" );
						
						Block addAll2Javadoc = ifaceBlock.newJavadocBlock( );
						addAll2Javadoc.addLine( "Adds multiple " + elemsName + " to the result." );
						addAll2Javadoc.addLine( );
						addAll2Javadoc.addLine( "@param " + elemsName );
						addAll2Javadoc.addLine( "\t\tThe " + elemsName + " to add." );
						addAll2Javadoc.addLine( "@return" );
						addAll2Javadoc.addLine( "\t\tThis {@code IBuilder}, for chaining." );
						
						ifaceBlock.addLine( "public abstract IBuilder add" + cap( elemsName ) + "(" + listTypeName + "... " + elemsName + ");" );
					}
					else if( ftype.isAssignableFrom( HashMap.class ) )
					{
						if( !( field.getGenericType( ) instanceof ParameterizedType ) )
						{
							continue;
						}
						String mapKeyTypeName = getMapKeyTypeName( field );
						String mapValueTypeName = getMapValueTypeName( field );
						String entryName = getElementSingularName( field );
						
						Block addJavadoc = ifaceBlock.newJavadocBlock( );
						addJavadoc.addLine( "Puts " + a( entryName ) + " entry into the result." );
						addJavadoc.addLine( );
						addJavadoc.addLine( "@param key" );
						addJavadoc.addLine( "\t\tThe key for the entry." );
						addJavadoc.addLine( "@param value" );
						addJavadoc.addLine( "\t\tThe value for the entry." );
						addJavadoc.addLine( "@return" );
						addJavadoc.addLine( "\t\tThis {@code Builder}, for chaining." );
						
						ifaceBlock.addLine( "public abstract IBuilder put" + cap( entryName ) + "(" + mapKeyTypeName + " key, " + mapValueTypeName + " value)" );
					}
					else
					{
						Block setJavadoc = ifaceBlock.newJavadocBlock( );
						String getter = "get";
						if( boolean.class.equals( field.getType( ) ) )
						{
							getter = "is";
						}
						
						setJavadoc.addLine( "Sets the {@link " + typeName + "#" + getter + cap( fieldName ) + "() " + fieldName + "} of the result." );
						setJavadoc.addLine( );
						setJavadoc.addLine( "@param " + fieldName );
						setJavadoc.addLine( "\t\tThe value for the {@code " + fieldName + "}." );
						setJavadoc.addLine( "@return" );
						setJavadoc.addLine( "\t\tThis {@code IBuilder}, for chaining." );
						
						String paramTypeName = ftypeName;
						String returnTypeName = ftypeName;
						
						if( ftype.isArray( ) )
						{
							paramTypeName = toString( ftype.getComponentType( ) ) + "...";
							returnTypeName = toString( ftype.getComponentType( ) ) + "[]";
						}
						
						ifaceBlock.addLine( "public abstract IBuilder " + fieldName + "(" + paramTypeName + " " + fieldName + ");" );
						
						Block getJavadoc = ifaceBlock.newJavadocBlock( );
						getJavadoc.addLine( "Gets the current {@link " + typeName + "#" + fieldName + " " + fieldName + "} of the result." );
						getJavadoc.addLine( );
						getJavadoc.addLine( "@return" );
						getJavadoc.addLine( "\t\tthe current {@link " + typeName + "#" + fieldName + " " + fieldName + "} of the result." );
						
						ifaceBlock.addLine( "public abstract " + returnTypeName + " " + fieldName + "();" );
					}
					ifaceBlock.addLine( );
				}
				
				Block createJavadoc = ifaceBlock.newJavadocBlock( );
				createJavadoc.addLine( "Creates a new {@code " + typeName + "} with the properties that have been set on this {@code IBuilder}." );
				createJavadoc.addLine( "<br>" );
				createJavadoc.addLine( "<b>Note</b>: this method may only be called once.  Afterward it will return {@code null}." );
				createJavadoc.addLine( );
				createJavadoc.addLine( "@return" );
				createJavadoc.addLine( "\tA new {@code " + typeName + "} with the properties that have been set on this {@code IBuilder}." );
				
				ifaceBlock.addLine( "public abstract " + typeName + " create();" );
			}
			
			typeBlock.addLine( );
			
			if( !isAbstract )
			{
				Block newBuilderJavadoc = typeBlock.newJavadocBlock( );
				newBuilderJavadoc.addLine( "@return" );
				newBuilderJavadoc.addLine( "\ta new {@link Builder}." );
				newBuilderJavadoc.addLine( "@see" );
				newBuilderJavadoc.addLine( "\t{@link Builder#create()}" );
				
				Block newBuilderBlock = typeBlock.newJavaBlock( "public static Builder newBuilder()" );
				newBuilderBlock.addLine( "return new Builder();" );
				
				typeBlock.addLine( );
				
				Block builderJavadoc = typeBlock.newJavadocBlock( );
				builderJavadoc.addLine( "Builds " + a( "{@link " + typeName + "}" ) + "." );
				builderJavadoc.addLine( "<br>" );
				builderJavadoc.addLine( "If you are not familiar with the builder pattern, see <a href=\"http://en.wikipedia.org/wiki/Builder_pattern\">http://en.wikipedia.org/wiki/Builder_pattern</a>." );
				builderJavadoc.addLine( "<br>" );
				builderJavadoc.addLine( "{@code Builder} has no public constructor; use {@link #newBuilder()} to create a {@code Builder}." );
				builderJavadoc.addLine( "<br><br>" );
				builderJavadoc.addLine( "<i>This class was auto-generated by " + bgLink + ".</i>" );
				
				String builderHeader = "public static class Builder";
				if( superclass != null )
				{
					builderHeader += " implements IBuilder";
				}
				Block builderBlock = typeBlock.newJavaBlock( builderHeader );
				
				builderBlock.addLine( "private " + typeName + " result;" );
				builderBlock.addLine( );
				
				Block constructor = builderBlock.newJavaBlock( "private Builder()" );
				constructor.addLine( "result = new " + typeName + "();" );
				builderBlock.addLine( );
				
				List<Field> fields = new ArrayList<Field>( );
				List<Method> validators = new ArrayList<Method>( );
				Class<?> c = type;
				while( !c.equals( Object.class ) )
				{
					fields.addAll( Arrays.asList( c.getDeclaredFields( ) ) );
					
					for( Method m : c.getDeclaredMethods( ) )
					{
						if( Modifier.isStatic( m.getModifiers( ) ) )
						{
							continue;
						}
						if( !m.getReturnType( ).equals( void.class ) )
						{
							continue;
						}
						Class<?>[ ] paramTypes = m.getParameterTypes( );
						if( paramTypes != null && paramTypes.length > 0 )
						{
							continue;
						}
						if( c != type && Modifier.isPrivate( m.getModifiers( ) ) )
						{
							continue;
						}
						
						validators.add( m );
					}
					c = c.getSuperclass( );
				}
				
				for( Field field : fields )
				{
					if( field.getAnnotation( BuilderIgnore.class ) != null )
					{
						continue;
					}
					
					if( Modifier.isStatic( field.getModifiers( ) ) )
					{
						continue;
					}
					Class<?> ftype = field.getType( );
					String fieldName = field.getName( );
					String ftypeName = getTypeName( field );
					if( ftype.isAssignableFrom( ArrayList.class ) )
					{
						if( !( field.getGenericType( ) instanceof ParameterizedType ) )
						{
							continue;
						}
						String listTypeName = getListElementTypeName( field );
						String elemName = getElementSingularName( field );
						String elemsName = getElementPluralName( field );
						
						boolean lazyInit = field.getAnnotation( BuilderLazyInitialize.class ) != null;
						
						Block setJavadoc = builderBlock.newJavadocBlock( );
						setJavadoc.addLine( "Sets the " + elemsName + " of the result." );
						setJavadoc.addLine( );
						setJavadoc.addLine( "@param " + elemsName );
						setJavadoc.addLine( "\t\tThe " + elemsName + " to set." );
						setJavadoc.addLine( "@return" );
						setJavadoc.addLine( "\t\tThis {@code Builder}, for chaining." );
						Block setBlock = builderBlock.newJavaBlock( "public Builder set" + cap( elemsName ) + "(Collection<? extends " + listTypeName + "> " + elemsName + ")" );
						builderBlock.addLine( );
						
						Block addJavadoc = builderBlock.newJavadocBlock( );
						addJavadoc.addLine( "Adds " + a( elemName ) + " to the result." );
						addJavadoc.addLine( );
						addJavadoc.addLine( "@param " + elemName );
						addJavadoc.addLine( "\t\tThe " + elemName + " to add." );
						addJavadoc.addLine( "@return" );
						addJavadoc.addLine( "\t\tThis {@code Builder}, for chaining." );
						Block addBlock = builderBlock.newJavaBlock( "public Builder add" + cap( elemName ) + "(" + listTypeName + " " + elemName + ")" );
						builderBlock.addLine( );
						
						Block addAllJavadoc = builderBlock.newJavadocBlock( );
						addAllJavadoc.addLine( "Adds multiple " + elemsName + " to the result." );
						addAllJavadoc.addLine( );
						addAllJavadoc.addLine( "@param " + elemsName );
						addAllJavadoc.addLine( "\t\tThe " + elemsName + " to add." );
						addAllJavadoc.addLine( "@return" );
						addAllJavadoc.addLine( "\t\tThis {@code Builder}, for chaining." );
						Block addAllBlock = builderBlock.newJavaBlock( "public Builder add" + cap( elemsName ) + "(Collection<? extends " + listTypeName + "> " + elemsName + ")" );
						builderBlock.addLine( );
						
						Block addAll2Javadoc = builderBlock.newJavadocBlock( );
						addAll2Javadoc.addLine( "Adds multiple " + elemsName + " to the result." );
						addAll2Javadoc.addLine( );
						addAll2Javadoc.addLine( "@param " + elemsName );
						addAll2Javadoc.addLine( "\t\tThe " + elemsName + " to add." );
						addAll2Javadoc.addLine( "@return" );
						addAll2Javadoc.addLine( "\t\tThis {@code Builder}, for chaining." );
						Block addAll2Block = builderBlock.newJavaBlock( "public Builder add" + cap( elemsName ) + "(" + listTypeName + "... " + elemsName + ")" );
						addAll2Block.addLine( "return add" + cap( elemsName ) + "(Arrays.asList(" + elemsName + "));" );
						builderBlock.addLine( );
						
						if( lazyInit )
						{
							Block constructBlock;
							
							constructBlock = setBlock.newJavaBlock( "if (result." + fieldName + " == null)" );
							constructBlock.addLine( "result." + fieldName + " = new ArrayList<" + listTypeName + ">();" );
							
							constructBlock = addBlock.newJavaBlock( "if (result." + fieldName + " == null)" );
							constructBlock.addLine( "result." + fieldName + " = new ArrayList<" + listTypeName + ">();" );
							
							constructBlock = addAllBlock.newJavaBlock( "if (result." + fieldName + " == null)" );
							constructBlock.addLine( "result." + fieldName + " = new ArrayList<" + listTypeName + ">();" );
						}
						else
						{
							constructor.addLine( "result." + fieldName + " = new ArrayList<" + listTypeName + ">();" );
						}
						setBlock.addLine( "result." + fieldName + ".clear();" );
						setBlock.addLine( "result." + fieldName + ".addAll(" + elemsName + ");" );
						setBlock.addLine( "return this;" );
						
						addBlock.addLine( "result." + fieldName + ".add(" + elemName + ");" );
						addBlock.addLine( "return this;" );
						
						addAllBlock.addLine( "result." + fieldName + ".addAll(" + elemsName + ");" );
						addAllBlock.addLine( "return this;" );
					}
					else if( ftype.isAssignableFrom( HashMap.class ) )
					{
						if( !( field.getGenericType( ) instanceof ParameterizedType ) )
						{
							continue;
						}
						String mapKeyTypeName = getMapKeyTypeName( field );
						String mapValueTypeName = getMapValueTypeName( field );
						String entryName = getElementSingularName( field );
						
						boolean lazyInit = field.getAnnotation( BuilderLazyInitialize.class ) != null;
						
						Block addJavadoc = builderBlock.newJavadocBlock( );
						addJavadoc.addLine( "Puts " + a( entryName ) + " entry into the result." );
						addJavadoc.addLine( );
						addJavadoc.addLine( "@param key" );
						addJavadoc.addLine( "\t\tThe key for the entry." );
						addJavadoc.addLine( "@param value" );
						addJavadoc.addLine( "\t\tThe value for the entry." );
						addJavadoc.addLine( "@return" );
						addJavadoc.addLine( "\t\tThis {@code Builder}, for chaining." );
						Block addBlock = builderBlock.newJavaBlock( "public Builder put" + cap( entryName ) + "(" + mapKeyTypeName + " key, " + mapValueTypeName + " value)" );
						builderBlock.addLine( );
						
						if( lazyInit )
						{
							Block constructBlock = addBlock.newJavaBlock( "if (result." + fieldName + " == null)" );
							constructBlock.addLine( "result." + fieldName + " = new HashMap<" + mapKeyTypeName + ", " + mapValueTypeName + ">();" );
						}
						else
						{
							constructor.addLine( "result." + fieldName + " = new HashMap<" + mapKeyTypeName + ", " + mapValueTypeName + ">();" );
						}
						addBlock.addLine( "result." + fieldName + ".put(key, value);" );
						addBlock.addLine( "return this;" );
					}
					else
					{
						Block setJavadoc = builderBlock.newJavadocBlock( );
						String getter = "get";
						if( boolean.class.equals( field.getType( ) ) )
						{
							getter = "is";
						}
						
						setJavadoc.addLine( "Sets the {@link " + typeName + "#" + getter + cap( fieldName ) + "() " + fieldName + "} of the result." );
						setJavadoc.addLine( );
						setJavadoc.addLine( "@param " + fieldName );
						setJavadoc.addLine( "\t\tThe value for the {@code " + fieldName + "}." );
						setJavadoc.addLine( "@return" );
						setJavadoc.addLine( "\t\tThis {@code Builder}, for chaining." );
						
						String paramTypeName = ftypeName;
						String returnTypeName = ftypeName;
						
						if( ftype.isArray( ) )
						{
							paramTypeName = toString( ftype.getComponentType( ) ) + "...";
							returnTypeName = toString( ftype.getComponentType( ) ) + "[]";
						}
						
						Block setBlock = builderBlock.newJavaBlock( "public Builder " + fieldName + "(" + paramTypeName + " " + fieldName + ")" );
						setBlock.addLine( "result." + fieldName + " = " + fieldName + ";" );
						setBlock.addLine( "return this;" );
						builderBlock.addLine( );
						
						Block getJavadoc = builderBlock.newJavadocBlock( );
						getJavadoc.addLine( "Gets the current {@link " + typeName + "#" + fieldName + " " + fieldName + "} of the result." );
						getJavadoc.addLine( );
						getJavadoc.addLine( "@return" );
						getJavadoc.addLine( "\t\tThe current {@link " + typeName + "#" + fieldName + " " + fieldName + "} of the result." );
						
						Block getBlock = builderBlock.newJavaBlock( "public " + returnTypeName + " " + fieldName + "()" );
						getBlock.addLine( "return result." + fieldName + ";" );
						builderBlock.addLine( );
					}
				}
				
				Block createJavadoc = builderBlock.newJavadocBlock( );
				createJavadoc.addLine( "Creates a new {@code " + typeName + "} with the properties that have been set on this {@code Builder}." );
				createJavadoc.addLine( "<br>" );
				createJavadoc.addLine( "<b>Note</b>: this method may only be called once.  Afterward it will return {@code null}." );
				createJavadoc.addLine( );
				createJavadoc.addLine( "@return" );
				createJavadoc.addLine( "\tA new {@code " + typeName + "} with the properties that have been set on this {@code Builder}." );
				
				Block createBlock = builderBlock.newJavaBlock( "public " + typeName + " create()" );
				if( type.getAnnotation( BuilderAllowNull.class ) == null )
				{
					for( Field field : fields )
					{
						if( field.getAnnotation( BuilderIgnore.class ) != null )
						{
							continue;
						}
						if( field.getAnnotation( BuilderAllowNull.class ) != null )
						{
							continue;
						}
						if( Modifier.isStatic( field.getModifiers( ) ) )
						{
							continue;
						}
						if( field.getType( ).equals( List.class ) )
						{
							continue;
						}
						if( field.getType( ).isPrimitive( ) )
						{
							continue;
						}
						String fieldName = field.getName( );
						
						Block nullBlock = createBlock.newJavaBlock( "if (result." + fieldName + " == null)" );
						nullBlock.addLine( "throw new IllegalStateException(\"" + fieldName + " must be non-null\");" );
					}
				}
				for( Method m : validators )
				{
					createBlock.addLine( "result." + m.getName( ) + "();" );
				}
				createBlock.addLine( typeName + " result = this.result;" );
				createBlock.addLine( "this.result = null;" );
				createBlock.addLine( "return result;" );
			}
			
			result[ i ] = topBlock.toString( );
		}
		return result;
	}
	
	/**
	 * Finds the java file a generated builder belongs in, and attempts to insert the builder code, or replace the existing auto-generated types and methods if
	 * necessary.<br>
	 * <i>Requres JavaParser 1.0.8 or greater.</i><br>
	 * <br>
	 * This method is crazy. Finding and modifying the file is complicated, and this method makes a backup, but who knows what could go wrong. USE AT YOUR OWN
	 * RISK!
	 * 
	 * @param builder
	 *            a builder generated by {@code #generateBuilders(Class...)}.
	 * @throws Exception
	 *             for any number of reasons...
	 */
	public static void writeBuilder( String builder ) throws Exception
	{
		CompilationUnit compunit = JavaParser.parse( new ByteArrayInputStream( builder.getBytes( ) ) );
		
		for( TypeDeclaration typeDecl : compunit.getTypes( ) )
		{
			File javaFile = findJavaFile( typeDecl );
			if( javaFile != null )
			{
				List<String> lines = insertBuilder( typeDecl , javaFile );
				
				File backup = new File( javaFile + "_backup" );
				File newFile = new File( javaFile + "_new" );
				System.out.println( "Copying " + javaFile + " to " + backup );
				copy( javaFile , backup );
				System.out.println( "Writing " + newFile + "..." );
				write( lines , newFile );
				System.out.println( "Writing " + javaFile + "..." );
				write( lines , javaFile );
				System.out.println( "Deleting " + backup + "..." );
				backup.delete( );
				System.out.println( "Deleting " + newFile + "..." );
				newFile.delete( );
			}
		}
	}
	
	private static void copy( File src , File dest ) throws Exception
	{
		InputStream in = new FileInputStream( src );
		OutputStream out = new FileOutputStream( dest );
		
		byte[ ] bytes = new byte[ 1024 ];
		
		int count;
		while( ( count = in.read( bytes ) ) >= 0 )
		{
			out.write( bytes , 0 , count );
		}
		
		out.flush( );
		out.close( );
		in.close( );
	}
	
	private static void write( List<String> lines , Writer writer ) throws Exception
	{
		for( String line : lines )
		{
			writer.write( line );
			writer.write( "\n" );
		}
	}
	
	private static void write( List<String> lines , File dest ) throws Exception
	{
		BufferedWriter writer = new BufferedWriter( new FileWriter( dest ) );
		write( lines , writer );
		writer.flush( );
		writer.close( );
	}
	
	private static String[ ] getNestedClassNames( TypeDeclaration decl )
	{
		SingleMemberAnnotationExpr fullNameAnnot = ( SingleMemberAnnotationExpr ) decl.getAnnotations( ).get( 0 );
		StringLiteralExpr fullNameExpr = ( StringLiteralExpr ) fullNameAnnot.getMemberValue( );
		
		String fullName = fullNameExpr.getValue( );
		
		int lastDot = fullName.lastIndexOf( '.' );
		return fullName.substring( lastDot + 1 ).split( "\\$" );
	}
	
	private static File findJavaFile( TypeDeclaration builderParentDecl )
	{
		SingleMemberAnnotationExpr fullNameAnnot = ( SingleMemberAnnotationExpr ) builderParentDecl.getAnnotations( ).get( 0 );
		StringLiteralExpr fullNameExpr = ( StringLiteralExpr ) fullNameAnnot.getMemberValue( );
		
		String fullName = fullNameExpr.getValue( );
		
		int lastDot = fullName.lastIndexOf( '.' );
		String path = fullName.substring( 0 , lastDot ).replace( '.' , File.separatorChar );
		String[ ] classNames = fullName.substring( lastDot + 1 ).split( "\\$" );
		
		String filePath = path + File.separator + classNames[ 0 ] + ".java";
		return FileFinder.findFile( filePath , new File( "applet/src" ) , 2 );
	}
	
	private static List<String> readLines( File file ) throws IOException
	{
		List<String> lines = new ArrayList<String>( );
		
		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		
		String line;
		while( ( line = reader.readLine( ) ) != null )
		{
			lines.add( line );
		}
		
		reader.close( );
		
		return lines;
	}
	
	private static CompilationUnit parse( List<String> lines ) throws Exception
	{
		StringWriter writer = new StringWriter( );
		write( lines , writer );
		CompilationUnit result = JavaParser.parse( new ByteArrayInputStream( writer.getBuffer( ).toString( ).getBytes( ) ) );
		
		return result;
	}
	
	private static List<String> insertBuilder( TypeDeclaration builderParentDecl , File javaFile ) throws Exception
	{
		List<String> lines = readLines( javaFile );
		
		String[ ] classNames = getNestedClassNames( builderParentDecl );
		
		List<BodyDeclaration> members = builderParentDecl.getMembers( );
		
		for( int m = members.size( ) - 1 ; m >= 0 ; m-- )
		{
			BodyDeclaration decl = members.get( m );
			
			// re-parse after every insertion, since the end line of the
			// destination type will have changed
			CompilationUnit compunit = parse( lines );
			
			TypeDeclaration destType = find( compunit , classNames );
			
			BodyDeclaration toReplace = null;
			if( decl instanceof MethodDeclaration )
			{
				toReplace = find( destType , ( MethodDeclaration ) decl );
			}
			else if( decl instanceof TypeDeclaration )
			{
				toReplace = find( destType , ( ( TypeDeclaration ) decl ).getName( ) );
			}
			
			if( toReplace != null )
			{
				replace( lines , getTotalLineRegion( toReplace ) , decl.toString( ) );
			}
			else
			{
				insert( lines , destType.getEndLine( ) - 1 , destType.getEndColumn( ) - 1 , decl.toString( ) );
			}
		}
		
		return lines;
	}
	
	private static TypeDeclaration find( CompilationUnit compunit , String[ ] nestedTypeNames )
	{
		for( TypeDeclaration decl : compunit.getTypes( ) )
		{
			if( decl.getName( ).equals( nestedTypeNames[ 0 ] ) )
			{
				if( nestedTypeNames.length == 1 )
				{
					return decl;
				}
				else
				{
					return find( decl , nestedTypeNames , 1 );
				}
			}
		}
		return null;
	}
	
	private static TypeDeclaration find( TypeDeclaration decl , String[ ] nestedTypeNames , int start )
	{
		TypeDeclaration nested = find( decl , nestedTypeNames[ start ] );
		if( nested != null )
		{
			if( start == nestedTypeNames.length - 1 )
			{
				return nested;
			}
			else
			{
				return find( nested , nestedTypeNames , start + 1 );
			}
		}
		return null;
	}
	
	private static TypeDeclaration find( TypeDeclaration decl , String nestedTypeName )
	{
		for( BodyDeclaration bodyDecl : decl.getMembers( ) )
		{
			if( bodyDecl instanceof TypeDeclaration && ( ( TypeDeclaration ) bodyDecl ).getName( ).equals( nestedTypeName ) )
			{
				return ( TypeDeclaration ) bodyDecl;
			}
		}
		return null;
	}
	
	private static MethodDeclaration find( TypeDeclaration decl , MethodDeclaration analogous )
	{
		for( BodyDeclaration bodyDecl : decl.getMembers( ) )
		{
			if( bodyDecl instanceof MethodDeclaration )
			{
				MethodDeclaration methodDecl = ( MethodDeclaration ) bodyDecl;
				if( methodDecl.getName( ).equals( analogous.getName( ) )
						&& methodDecl.getModifiers( ) == analogous.getModifiers( )
						&& sameTypes( methodDecl.getParameters( ) , analogous.getParameters( ) ) )
				{
					return methodDecl;
				}
			}
		}
		return null;
	}
	
	private static boolean sameTypes( List<Parameter> l1 , List<Parameter> l2 )
	{
		if( l1 == null && l2 == null )
		{
			return true;
		}
		if( l1.size( ) != l2.size( ) )
		{
			return false;
		}
		for( int i = 0 ; i < l1.size( ) ; i++ )
		{
			Parameter p1 = l1.get( i );
			Parameter p2 = l2.get( i );
			if( !p1.getType( ).toString( ).equals( p2.getType( ).toString( ) ) )
			{
				return false;
			}
		}
		
		return true;
	}
	
	private static void replace( List<String> lines , LineRegion region , String replacement )
	{
		StringBuffer sb = new StringBuffer( );
		String startLine = lines.get( region.startLine );
		int startIndex = indexOfColumn( startLine , region.startColumn );
		String endLine = lines.get( region.endLine );
		int endIndex = indexOfColumn( endLine , region.endColumn );
		sb.append( startLine.substring( 0 , startIndex ) );
		sb.append( replacement );
		sb.append( endLine.substring( endIndex + 1 ) );
		
		for( int line = region.endLine ; line >= region.startLine ; line-- )
		{
			lines.remove( line );
		}
		
		String[ ] newLines = sb.toString( ).split( "(\r\n|\n\r|\n|\r)" );
		for( int line = newLines.length - 1 ; line >= 0 ; line-- )
		{
			lines.add( region.startLine , newLines[ line ] );
		}
	}
	
	private static void insert( List<String> lines , int line , int column , String replacement )
	{
		StringBuffer sb = new StringBuffer( );
		String lineStr = lines.get( line );
		int index = indexOfColumn( lineStr , column );
		sb.append( lineStr.substring( 0 , index ) );
		sb.append( replacement );
		sb.append( lineStr.substring( index ) );
		
		lines.remove( line );
		
		String[ ] newLines = sb.toString( ).split( "(\r\n|\n\r|\n|\r)" );
		for( int newLine = newLines.length - 1 ; newLine >= 0 ; newLine-- )
		{
			lines.add( line , newLines[ newLine ] );
		}
	}
	
	private static int indexOfColumn( String s , int column )
	{
		int curColumn = 0;
		int i;
		for( i = 0 ; i < s.length( ) && curColumn < column ; i++ )
		{
			if( s.charAt( i ) == '\t' )
			{
				curColumn += 8;
			}
			else
			{
				curColumn++ ;
			}
		}
		return i;
	}
	
	private static LineRegion getLineRegion( Node node )
	{
		return new LineRegion( node.getBeginLine( ) - 1 , node.getBeginColumn( ) - 1 , node.getEndLine( ) - 1 , node.getEndColumn( ) - 1 );
	}
	
	private static LineRegion getTotalLineRegion( BodyDeclaration decl )
	{
		LineRegion region = getLineRegion( decl );
		if( decl.getJavaDoc( ) != null )
		{
			region = region.union( getLineRegion( decl.getJavaDoc( ) ) );
		}
		if( decl.getAnnotations( ) != null )
		{
			for( AnnotationExpr annotation : decl.getAnnotations( ) )
			{
				region = region.union( getLineRegion( annotation ) );
			}
		}
		return region;
	}
}

package org.andork.swing.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.andork.reflect.ReflectionUtils;
import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlSpec;
import org.andork.snakeyaml.YamlSpec.Attribute;

@SuppressWarnings( "serial" )
public class EasyTableModel<T> extends AbstractTableModel
{
	private RowFormat<T>				prototypeFormat;
	
	private final ArrayList<T>			rows				= new ArrayList<T>( );
	
	private final Map<T, Integer>		rowIndexCache;
	
	private final Map<String, Integer>	columnIndexCache	= new HashMap<String, Integer>( );
	
	public EasyTableModel( boolean useRowIndexCache )
	{
		if( useRowIndexCache )
		{
			rowIndexCache = new HashMap<T, Integer>( );
		}
		else
		{
			rowIndexCache = null;
		}
	}
	
	public void setPrototypeFormat( RowFormat<T> prototype )
	{
		columnIndexCache.clear( );
		this.prototypeFormat = prototype;
		fireTableStructureChanged( );
	}
	
	public RowFormat<T> getRowFormat( int row )
	{
		return prototypeFormat;
	}
	
	public static interface RowFormat<T>
	{
		Object getValueAt( T row , int columnIndex );
		
		/**
		 * @return {@code true} if {@link EasyTableModel} should {@link EasyTableModel#fireTableCellUpdated(int, int) fireTableCellUpdated()}.
		 */
		boolean setValueAt( T row , Object value , int columnIndex );
		
		boolean isCellEditable( T row , int columnIndex );
		
		String getColumnName( int columnIndex );
		
		Class<?> getColumnClass( int columnIndex );
		
		int getColumnCount( );
	}
	
	public static class YamlObjectRowFormat<S extends YamlSpec<S>> implements RowFormat<YamlObject<S>>
	{
		protected final YamlSpec<S>	spec;
		
		public YamlObjectRowFormat( YamlSpec<S> spec )
		{
			this.spec = spec;
		}
		
		@Override
		public Object getValueAt( YamlObject<S> row , int columnIndex )
		{
			return row.valueAt( columnIndex );
		}
		
		@Override
		public boolean setValueAt( YamlObject<S> row , Object value , int columnIndex )
		{
			row.setValueAt( columnIndex , value );
			return true;
		}
		
		@Override
		public boolean isCellEditable( YamlObject<S> row , int columnIndex )
		{
			return true;
		}
		
		@Override
		public String getColumnName( int columnIndex )
		{
			return spec.attributeAt( columnIndex ).getName( );
		}
		
		@Override
		public Class<?> getColumnClass( int columnIndex )
		{
			return spec.attributeAt( columnIndex ).getValueClass( );
		}
		
		@Override
		public int getColumnCount( )
		{
			return spec.getAttributeCount( );
		}
		
	}
	
	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.FIELD , ElementType.METHOD } )
	public static @interface ColumnIndex
	{
		int value( );
	}
	
	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.FIELD } )
	public static @interface ColumnField
	{
		/**
		 * @return the name of the column.
		 */
		String value( );
	}
	
	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.METHOD } )
	public static @interface ColumnGetter
	{
		/**
		 * @return the name of the column.
		 */
		String value( );
	}
	
	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.METHOD } )
	public static @interface ColumnSetter
	{
		/**
		 * @return the name of the column.
		 */
		String value( );
	}
	
	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.FIELD } )
	public static @interface IsEditable
	{
		boolean value( );
	}
	
	public static class ReflectionRowFormat<T> implements RowFormat<T>
	{
		private String[ ]	columnNames;
		private Field[ ]	fields;
		private Method[ ]	getters;
		private Method[ ]	setters;
		
		public static <T> ReflectionRowFormat<T> create( Class<? extends T> type )
		{
			return new ReflectionRowFormat<T>( type );
		}
		
		public static <T> ReflectionRowFormat<T> create( Class<? extends T> type , List<String> columnNames )
		{
			return new ReflectionRowFormat<T>( type , columnNames );
		}
		
		protected ReflectionRowFormat( Class<? extends T> type )
		{
			init( type );
		}
		
		protected ReflectionRowFormat( Class<? extends T> type , List<String> columnNames )
		{
			init( type , columnNames );
		}
		
		private void init( Class<? extends T> type )
		{
			List<String> columnNames = new ArrayList<String>( );
			
			for( Field field : ReflectionUtils.getInstanceFieldList( type ) )
			{
				ColumnField columnField = field.getAnnotation( ColumnField.class );
				ColumnIndex columnIndex = field.getAnnotation( ColumnIndex.class );
				if( columnField == null || columnIndex == null )
				{
					continue;
				}
				
				int index = columnIndex.value( );
				String name = columnField.value( );
				
				while( columnNames.size( ) <= index )
				{
					columnNames.add( null );
				}
				
				String prev = columnNames.set( index , name );
				if( prev != null && name.equals( prev ) )
				{
					throw new IllegalStateException( "Conflicting names for column " + index + ": " + prev + ", " + name );
				}
			}
			
			for( Method method : ReflectionUtils.getInstanceMethodList( type ) )
			{
				ColumnGetter columnGetter = method.getAnnotation( ColumnGetter.class );
				ColumnSetter columnSetter = method.getAnnotation( ColumnSetter.class );
				ColumnIndex columnIndex = method.getAnnotation( ColumnIndex.class );
				
				if( columnIndex == null )
				{
					continue;
				}
				
				int index = columnIndex.value( );
				
				String name;
				if( columnGetter != null )
				{
					if( columnSetter != null )
					{
						throw new IllegalStateException( "Methods must not be annotated with both @ColumnGetter and @ColumnSetter" );
					}
					
					name = columnGetter.value( );
				}
				else if( columnSetter != null )
				{
					name = columnSetter.value( );
				}
				else
				{
					continue;
				}
				
				while( columnNames.size( ) <= index )
				{
					columnNames.add( null );
				}
				
				String prev = columnNames.set( index , name );
				if( prev != null && name.equals( prev ) )
				{
					throw new IllegalStateException( "Conflicting names for column " + index + ": " + prev + ", " + name );
				}
			}
			
			init( type , columnNames );
		}
		
		private void init( Class<? extends T> type , List<String> columnNames )
		{
			this.columnNames = columnNames.toArray( new String[ columnNames.size( ) ] );
			
			Map<String, Integer> columnNameToIndexMap = new HashMap<String, Integer>( );
			
			int i = 0;
			for( String columnName : columnNames )
			{
				if( columnNameToIndexMap.put( columnName , i++ ) != null )
				{
					throw new IllegalStateException( "Multiple columns named " + columnName );
				}
			}
			
			fields = new Field[ columnNames.size( ) ];
			getters = new Method[ columnNames.size( ) ];
			setters = new Method[ columnNames.size( ) ];
			
			for( Field field : ReflectionUtils.getInstanceFieldList( type ) )
			{
				ColumnField columnField = field.getAnnotation( ColumnField.class );
				if( columnField == null )
				{
					continue;
				}
				
				String name = columnField.value( );
				
				Integer index = columnNameToIndexMap.get( name );
				if( index != null )
				{
					if( fields[ index ] != null )
					{
						throw new IllegalStateException( "Multiple @ColumnFields named " + name );
					}
					fields[ index ] = field;
				}
			}
			
			for( Method method : ReflectionUtils.getInstanceMethodList( type ) )
			{
				ColumnGetter columnGetter = method.getAnnotation( ColumnGetter.class );
				ColumnSetter columnSetter = method.getAnnotation( ColumnSetter.class );
				if( columnGetter != null )
				{
					if( columnSetter != null )
					{
						throw new IllegalStateException( "Methods must not be annotated with both @ColumnGetter and @ColumnSetter" );
					}
					
					if( method.getParameterTypes( ).length != 0 )
					{
						throw new IllegalStateException( "@ColumnGetters must take no parameters" );
					}
					
					String name = columnGetter.value( );
					
					Integer index = columnNameToIndexMap.get( name );
					if( index != null )
					{
						if( getters[ index ] != null )
						{
							throw new IllegalStateException( "Multiple @ColumnGetters named " + name );
						}
						getters[ index ] = method;
					}
				}
				else if( columnSetter != null )
				{
					if( method.getParameterTypes( ).length != 1 )
					{
						throw new IllegalStateException( "@ColumnSetters must take only one parameter" );
					}
					
					if( method.getReturnType( ) == null )
					{
						throw new IllegalStateException( "@ColumnSetters must have a return type" );
					}
					
					String name = columnSetter.value( );
					
					Integer index = columnNameToIndexMap.get( name );
					if( index != null )
					{
						if( setters[ index ] != null )
						{
							throw new IllegalStateException( "Multiple @ColumnSetters named " + name );
						}
						setters[ index ] = method;
					}
				}
			}
			
			for( i = 0 ; i < columnNames.size( ) ; i++ )
			{
				if( getters[ i ] == null && fields[ i ] == null )
				{
					throw new IllegalStateException( "Column " + i + " has no @ColumnField or @ColumnGetter" );
				}
			}
		}
		
		public Object getValueAt( T row , int column )
		{
			try
			{
				if( getters[ column ] != null )
				{
					return getters[ column ].invoke( row );
				}
				return fields[ column ].get( row );
			}
			catch( Exception e )
			{
				throw new RuntimeException( e );
			}
		}
		
		public boolean setValueAt( T row , Object value , int column )
		{
			if( !isCellEditable( row , column ) )
			{
				throw new UnsupportedOperationException( "column " + column + " is not editable" );
			}
			try
			{
				if( setters[ column ] != null )
				{
					setters[ column ].invoke( row , value );
					return false;
				}
				fields[ column ].set( row , value );
				return true;
			}
			catch( Exception e )
			{
				throw new RuntimeException( e );
			}
		}
		
		public boolean isCellEditable( T row , int column )
		{
			if( setters[ column ] != null )
			{
				return true;
			}
			if( fields[ column ] != null )
			{
				IsEditable isEditable = fields[ column ].getAnnotation( IsEditable.class );
				if( isEditable != null )
				{
					return isEditable.value( );
				}
			}
			return false;
		}
		
		public String getColumnName( int column )
		{
			return columnNames[ column ];
		}
		
		public Class<?> getColumnClass( int column )
		{
			if( getters[ column ] != null )
			{
				return getters[ column ].getReturnType( );
			}
			return fields[ column ].getType( );
		}
		
		public int getColumnCount( )
		{
			return columnNames.length;
		}
	}
	
	@Override
	public int getRowCount( )
	{
		return rows.size( );
	}
	
	@Override
	public int getColumnCount( )
	{
		return prototypeFormat.getColumnCount( );
	}
	
	@Override
	public Object getValueAt( int rowIndex , int columnIndex )
	{
		return getRowFormat( rowIndex ).getValueAt( rows.get( rowIndex ) , columnIndex );
	}
	
	@Override
	public String getColumnName( int column )
	{
		return prototypeFormat.getColumnName( column );
	}
	
	@Override
	public Class<?> getColumnClass( int columnIndex )
	{
		return prototypeFormat.getColumnClass( columnIndex );
	}
	
	@Override
	public boolean isCellEditable( int rowIndex , int columnIndex )
	{
		return getRowFormat( rowIndex ).isCellEditable( rows.get( rowIndex ) , columnIndex );
	}
	
	public void setValueAt( Object aValue , int rowIndex , int columnIndex , boolean fireEvent )
	{
		if( getRowFormat( rowIndex ).setValueAt( rows.get( rowIndex ) , aValue , columnIndex ) && fireEvent )
		{
			fireTableCellUpdated( rowIndex , columnIndex );
		}
	}
	
	@Override
	public void setValueAt( Object aValue , int rowIndex , int columnIndex )
	{
		setValueAt( aValue , rowIndex , columnIndex , true );
	}
	
	public T getRow( int index )
	{
		return rows.get( index );
	}
	
	public void setRow( int index , T row )
	{
		rows.set( index , row );
		super.fireTableRowsUpdated( index , index );
	}
	
	public void addRow( T row )
	{
		rows.add( row );
		super.fireTableRowsInserted( rows.size( ) - 1 , rows.size( ) - 1 );
	}
	
	public void addRow( int index , T row )
	{
		rows.add( index , row );
		super.fireTableRowsInserted( index , index );
	}
	
	public void addRows( Collection<T> rows )
	{
		if( !rows.isEmpty( ) )
		{
			this.rows.addAll( rows );
			super.fireTableRowsInserted( this.rows.size( ) - rows.size( ) , this.rows.size( ) - 1 );
		}
	}
	
	public void addRows( int index , Collection<T> rows )
	{
		if( !rows.isEmpty( ) )
		{
			this.rows.addAll( index , rows );
			super.fireTableRowsInserted( index , index + rows.size( ) - 1 );
		}
	}
	
	public void removeRow( int index )
	{
		T row = rows.remove( index );
		if( rowIndexCache != null )
		{
			rowIndexCache.remove( row );
		}
		super.fireTableRowsDeleted( index , index );
	}
	
	public void removeRow( T row )
	{
		int index = rows.indexOf( row );
		if( index >= 0 )
		{
			removeRow( index );
		}
	}
	
	public void setRows(List<T> rows) {
		this.rows.clear( );
		this.rows.addAll( rows );
		rowIndexCache.clear( );
		fireTableDataChanged( );
	}
	
	public void removeAllRows( boolean fireEvent )
	{
		rows.clear( );
		rowIndexCache.clear( );
		if( fireEvent )
		{
			fireTableDataChanged( );
		}
	}
	
	public void removeAllRows( )
	{
		rows.clear( );
		rowIndexCache.clear( );
		fireTableDataChanged( );
	}
	
	public void fireTableRowUpdated( T row )
	{
		int rowIndex = indexOfRow( row );
		if( rowIndex >= 0 )
		{
			fireTableRowsUpdated( rowIndex , rowIndex );
		}
	}
	
	public void fireTableCellUpdated( T row , int columnIndex )
	{
		int rowIndex = indexOfRow( row );
		if( rowIndex >= 0 )
		{
			fireTableCellUpdated( rowIndex , columnIndex );
		}
	}
	
	public void fireTableCellUpdated( T row , String columnName )
	{
		fireTableCellUpdated( row , indexOfColumn( columnName ) );
	}
	
	public int indexOfColumn( String columnName )
	{
		Integer index = columnIndexCache.get( columnName );
		if( index != null )
		{
			if( index < getColumnCount( ) && columnName.equals( getColumnName( index ) ) )
			{
				return index;
			}
			else
			{
				columnIndexCache.remove( columnName );
			}
		}
		for( int i = 0 ; i < getColumnCount( ) ; i++ )
		{
			if( columnName.equals( getColumnName( i ) ) )
			{
				columnIndexCache.put( columnName , i );
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfRow( T row )
	{
		if( rowIndexCache != null )
		{
			Integer index = rowIndexCache.get( row );
			if( index != null )
			{
				if( index < rows.size( ) && rows.get( index ) == row )
				{
					return index;
				}
				else
				{
					rowIndexCache.remove( row );
				}
			}
		}
		for( int i = 0 ; i < rows.size( ) ; i++ )
		{
			if( rows.get( i ) == row )
			{
				rowIndexCache.put( row , i );
				return i;
			}
		}
		return -1;
	}
	
	public void copyRowsFrom( EasyTableModel<T> src , int srcStart , int srcEnd , int myStart )
	{
		int origRowCount = rows.size( );
		int myEnd = myStart + srcEnd - srcStart;
		for( int i = srcStart ; i <= srcEnd ; i++ )
		{
			T srcRow = src.getRow( i );
			int destI = i + myStart - srcStart;
			if( destI == rows.size( ) )
			{
				rows.add( null );
			}
			rows.set( destI , srcRow );
		}
		int updateEnd = Math.min( origRowCount - 1 , myEnd );
		if( updateEnd >= myStart )
		{
			fireTableRowsUpdated( myStart , updateEnd );
		}
		if( myEnd >= origRowCount )
		{
			fireTableRowsInserted( origRowCount , myEnd );
		}
	}
}

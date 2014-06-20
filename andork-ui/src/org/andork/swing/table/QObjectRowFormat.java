package org.andork.swing.table;

import java.util.Collection;

import org.andork.collect.CollectionUtils;
import org.andork.func.ToStringMapper;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.q.QSpec.Attribute;
import org.andork.util.ArrayUtils;

public class QObjectRowFormat<S extends QSpec<S>> implements EasyTableModel.RowFormat<QObject<S>>
{
	protected final QSpec<S>		spec;
	protected final Attribute<?>[ ]	columnAttrs;
	protected final String[ ]		columnNames;
	
	public QObjectRowFormat( QSpec<S> spec )
	{
		this( spec , spec.getAttributes( ) );
	}
	
	public QObjectRowFormat( QSpec<S> spec , Collection<Attribute<?>> columnAttrs )
	{
		this( spec , columnAttrs , CollectionUtils.map( ToStringMapper.instance , columnAttrs ) );
	}
	
	public QObjectRowFormat( QSpec<S> spec , Collection<Attribute<?>> columnAttrs , Iterable<String> columnNames )
	{
		this.spec = spec;
		this.columnAttrs = columnAttrs.toArray( new Attribute[ columnAttrs.size( ) ] );
		this.columnNames = ArrayUtils.toArray( columnNames , String.class );
		if( this.columnAttrs.length != this.columnNames.length )
		{
			throw new IllegalArgumentException( "columnAttrs and columnNames must be the same length" );
		}
	}
	
	@Override
	public Object getValueAt( QObject<S> row , int columnIndex )
	{
		return row.get( columnAttrs[ columnIndex ] );
	}
	
	@Override
	public boolean setValueAt( QObject<S> row , Object value , int columnIndex )
	{
		row.set( columnAttrs[ columnIndex ] , value );
		return true;
	}
	
	@Override
	public boolean isCellEditable( QObject<S> row , int columnIndex )
	{
		return true;
	}
	
	@Override
	public String getColumnName( int columnIndex )
	{
		return columnNames[ columnIndex ];
	}
	
	@Override
	public Class<?> getColumnClass( int columnIndex )
	{
		return columnAttrs[ columnIndex ].getValueClass( );
	}
	
	@Override
	public int getColumnCount( )
	{
		return columnAttrs.length;
	}
	
}
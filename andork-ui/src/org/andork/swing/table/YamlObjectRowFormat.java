package org.andork.swing.table;

import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlSpec;
import org.andork.swing.table.EasyTableModel.RowFormat;

public class YamlObjectRowFormat<S extends YamlSpec<S>> implements EasyTableModel.RowFormat<YamlObject<S>>
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
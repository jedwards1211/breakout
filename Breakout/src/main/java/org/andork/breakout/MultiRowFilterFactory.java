package org.andork.breakout;

import java.util.function.BiFunction;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.andork.swing.table.RowFilterFactory;
import org.andork.util.StringUtils;

public class MultiRowFilterFactory implements RowFilterFactory<String, TableModel, Integer>
{
	public MultiRowFilterFactory( BiFunction<String, String, RowFilter<TableModel, Integer>> filterMap )
	{
		this.filterMap = filterMap;
	}
	
	BiFunction<String, String, RowFilter<TableModel, Integer>>	filterMap;
	
	@Override
	public RowFilter<TableModel, Integer> createFilter( String input )
	{
		int colonIndex = StringUtils.unescapedIndexOf( input , ':' , '\\' );
		if( colonIndex < 0 )
		{
			return filterMap.apply( null , input );
		}
		return filterMap.apply( StringUtils.escape( input.substring( 0 , colonIndex ) , '\\' ) ,
				input.substring( colonIndex + 1 ) );
	}
}

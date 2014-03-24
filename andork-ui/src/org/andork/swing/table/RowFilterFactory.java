package org.andork.swing.table;

import javax.swing.RowFilter;

public interface RowFilterFactory<T, M, I>
{
	public RowFilter<M, I> createFilter( T input );
}

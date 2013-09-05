package org.andork.torquescape.model.section;

public class FixedSectionFn implements ISectionFn
{
	int			sectionSize;
	float[ ]	coords;
	
	public FixedSectionFn( float ... coords )
	{
		this.coords = coords;
		this.sectionSize = coords.length / 3;
	}
	
	@Override
	public int getVertexCount( float param )
	{
		return sectionSize;
	}
	
	@Override
	public void eval( float param , int index , float[ ] result )
	{
		System.arraycopy( coords , index * 3 , result , 0 , 3 );
	}
}

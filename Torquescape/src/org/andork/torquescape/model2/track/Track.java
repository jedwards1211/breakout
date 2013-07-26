package org.andork.torquescape.model2.track;

import org.andork.torquescape.model2.meshing.IMeshingFunction;
import org.andork.torquescape.model2.section.ISectionFunction;
import org.andork.torquescape.model.xform.IXformFunction;

public class Track
{
	protected IXformFunction	xformFunction;
	protected ISectionFunction	sectionFunction;
	protected IMeshingFunction	meshingFunction;
	
	public IXformFunction getXformFunction( )
	{
		return xformFunction;
	}
	
	public void setXformFunction( IXformFunction xformFunction )
	{
		this.xformFunction = xformFunction;
	}
	
	public ISectionFunction getSectionFunction( )
	{
		return sectionFunction;
	}
	
	public void setSectionFunction( ISectionFunction sectionFunction )
	{
		this.sectionFunction = sectionFunction;
	}
	
	public IMeshingFunction getMeshingFunction( )
	{
		return meshingFunction;
	}
	
	public void setMeshingFunction( IMeshingFunction meshingFunction )
	{
		this.meshingFunction = meshingFunction;
	}
}

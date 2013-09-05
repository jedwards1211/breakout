package org.andork.torquescape.model.track;

import org.andork.torquescape.model.meshing.IMeshingFn;
import org.andork.torquescape.model.section.ISectionFn;

public class Track
{
	protected ISectionFn		SectionFn;
	protected IMeshingFn		meshingFn;
	
	public ISectionFn getSectionFn( )
	{
		return SectionFn;
	}
	
	public void setSectionFn( ISectionFn SectionFn )
	{
		this.SectionFn = SectionFn;
	}
	
	public IMeshingFn getMeshingFn( )
	{
		return meshingFn;
	}
	
	public void setMeshingFn( IMeshingFn meshingFn )
	{
		this.meshingFn = meshingFn;
	}
}

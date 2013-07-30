package org.andork.torquescape.model.old.track;

import org.andork.torquescape.model.old.section.ISectionFunction;
import org.andork.torquescape.model.old.xform.IXformFunction;

public class Track
{
	protected IXformFunction xformFunction;
	protected ISectionFunction sectionFunction;
	
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
}

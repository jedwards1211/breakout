package org.andork.torquescape.model.old.gen;

import java.util.List;

import org.andork.torquescape.model.old.Triangle;
import org.andork.torquescape.model.old.section.ISectionFunction;
import org.andork.torquescape.model.old.xform.IXformFunction;

public interface ITrackSegmentGenerator
{
	public void generate( IXformFunction trackAxis , ISectionFunction crossSection , float start , float end , float step , List<List<Triangle>> result);
}

package org.andork.torquescape.model.gen;

import java.util.List;

import org.andork.torquescape.model.Triangle;
import org.andork.torquescape.model.section.ISectionFunction;
import org.andork.torquescape.model.xform.IXformFunction;

public interface ITrackSegmentGenerator
{
	public void generate( IXformFunction trackAxis , ISectionFunction crossSection , float start , float end , float step , List<List<Triangle>> result);
}

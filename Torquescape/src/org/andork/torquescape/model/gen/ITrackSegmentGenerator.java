package org.andork.torquescape.model.gen;

import java.util.List;

import javax.media.j3d.GeometryArray;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.torquescape.model.Triangle;
import org.andork.torquescape.model.section.ISectionFunction;
import org.andork.torquescape.model.xform.IXformFunction;

public interface ITrackSegmentGenerator
{
	public void generate( IXformFunction trackAxis , ISectionFunction crossSection , float start , float end , float step , J3DTempsPool pool , List<GeometryArray> outGeom , List<Triangle> outTriangles );
}

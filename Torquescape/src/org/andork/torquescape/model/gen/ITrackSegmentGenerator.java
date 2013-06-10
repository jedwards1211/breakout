package org.andork.torquescape.model.gen;

import java.util.List;

import javax.media.j3d.GeometryArray;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.math3d.curve.ICurve3f;
import org.andork.torquescape.model.Triangle;
import org.andork.torquescape.model.section.ICrossSectionFunction;

public interface ITrackSegmentGenerator
{
	public void generate( ICurve3f trackAxis , ICrossSectionFunction crossSection , float start , float end , float step , J3DTempsPool pool , List<GeometryArray> outGeom , List<Triangle> outTriangles );
}

package org.andork.math3d.curve;

import javax.media.j3d.Transform3D;

import org.andork.j3d.math.J3DTempsPool;

public interface ICurve3f
{
	public Transform3D eval(float param, J3DTempsPool pool, Transform3D out);
}

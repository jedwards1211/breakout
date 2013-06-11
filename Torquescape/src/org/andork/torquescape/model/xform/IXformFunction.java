package org.andork.torquescape.model.xform;

import javax.media.j3d.Transform3D;

import org.andork.j3d.math.J3DTempsPool;

public interface IXformFunction
{
	public Transform3D eval(float param, J3DTempsPool pool, Transform3D out);
}

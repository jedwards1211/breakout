package org.andork.torquescape.model.slice;

import org.andork.torquescape.model.AbstractIndexedSlice;

public class ColorWaveSlice extends AbstractIndexedSlice
{
	public final float[ ]	ambientColor	= new float[ 8 ];
	public final float[ ]	diffuseColor	= new float[ 8 ];
	public final float[ ]	specularColor	= new float[ 8 ];
	public float			shininess;
	public float			wavelength;
	public float			velocity;
}

package org.andork.torquescape.model.slice;

import org.andork.torquescape.model.AbstractIndexedSlice;


public class StandardSlice extends AbstractIndexedSlice
{
	public final float[ ]	ambientColor	= new float[ 4 ];
	public final float[ ]	diffuseColor	= new float[ 4 ];
	public final float[ ]	specularColor	= new float[ 4 ];
	public float			shininess;
}
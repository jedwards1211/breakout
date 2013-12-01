package org.andork.pick;

import static org.andork.vecmath.Vecmath.*;

public class PickPyramid
{
	public final float[ ]	origin		= new float[ 3 ];
	public final float[ ]	direction	= new float[ 3 ];
	public final float[ ]	rays		= new float[ 12 ];
	public final float[ ]	normals		= new float[ 12 ];
	
	public void calculate( int x , int y , int canvasWidth , int canvasHeight , PickXform xform )
	{
		xform.getOrigin( origin );
		xform.xform( x , y , canvasWidth , canvasHeight , direction , 0 );
		xform.xform( x + 0.5f , y + 0.5f , canvasWidth , canvasHeight , rays , 0 );
		xform.xform( x - 0.5f , y + 0.5f , canvasWidth , canvasHeight , rays , 3 );
		xform.xform( x - 0.5f , y - 0.5f , canvasWidth , canvasHeight , rays , 6 );
		xform.xform( x + 0.5f , y - 0.5f , canvasWidth , canvasHeight , rays , 9 );
		
		cross( rays , 6 , rays , 3 , normals , 0 );
		cross( rays , 9 , rays , 6 , normals , 1 );
		cross( rays , 0 , rays , 9 , normals , 2 );
		cross( rays , 3 , rays , 6 , normals , 3 );
	}
}

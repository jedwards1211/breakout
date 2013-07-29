package org.andork.torquescape.model.param;


public class LinearParamFunction implements IParamFunction
{
	private float	a1;
	private float	a2;
	private float	b1;
	private float	b2;
	
	public LinearParamFunction( float a1 , float a2 , float b1 , float b2 )
	{
		super( );
		this.a1 = a1;
		this.a2 = a2;
		this.b1 = b1;
		this.b2 = b2;
	}
	
	@Override
	public float eval( float param )
	{
		return b1 + ( param - a1 ) * ( b2 - b1 ) / ( a2 - a1 );
	}
	
}

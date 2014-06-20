package org.andork.q;

import org.andork.func.Bimapper;
import org.andork.func.IdentityHashMapBimapper;

public class QElementDeepCloneBimapper implements Bimapper<Object, Object>
{
	private Bimapper<Object, Object>	nonQElementBimapper;
	private Bimapper<Object, Object>	QElementBimapper;
	
	public QElementDeepCloneBimapper( )
	{
		this( null );
	}
	
	public QElementDeepCloneBimapper( Bimapper<Object, Object> nonQElementBimapper )
	{
		this.QElementBimapper = new IdentityHashMapBimapper<Object, Object>( this );
		this.nonQElementBimapper = null;
	}
	
	public QElementDeepCloneBimapper( Bimapper<Object, Object> QElementBimapper , Bimapper<Object, Object> nonQElementBimapper )
	{
		super( );
		this.QElementBimapper = QElementBimapper;
		this.nonQElementBimapper = nonQElementBimapper;
	}
	
	@Override
	public Object map( Object in )
	{
		if( in instanceof QElement )
		{
			return map( ( QElement ) in );
		}
		else
		{
			return nonQElementBimapper == null ? in : nonQElementBimapper.map( in );
		}
	}
	
	@Override
	public Object unmap( Object out )
	{
		return map( out );
	}
	
	public QElement map( QElement in )
	{
		return in.deepClone( QElementBimapper );
	}
}

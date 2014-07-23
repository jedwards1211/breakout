/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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

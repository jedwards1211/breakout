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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.func.BigDecimalBimapper;
import org.andork.func.BigIntegerBimapper;
import org.andork.func.Bimapper;
import org.andork.func.BooleanBimapper;
import org.andork.func.DoubleBimapper;
import org.andork.func.EnumBimapper;
import org.andork.func.FloatArray2ListBimapper;
import org.andork.func.FloatBimapper;
import org.andork.func.IntegerBimapper;
import org.andork.func.LongBimapper;
import org.andork.func.StringBimapper;
import org.andork.q.QSpec.Attribute;

public class QObjectTabDelimBimapper<S extends QSpec<S>> implements Bimapper<QObject<S>, String>
{
	private S							spec;
	
	private final List<String>			colNames		= new ArrayList<String>( );
	private final List<Attribute<?>>	colAttributes	= new ArrayList<Attribute<?>>( );
	private final List<Bimapper>		colBimappers	= new ArrayList<Bimapper>( );
	
	private static final Logger			LOGGER			= Logger.getLogger( QObjectTabDelimBimapper.class.getName( ) );
	
	private QObjectTabDelimBimapper( S spec )
	{
		this.spec = spec;
	}
	
	public static <S extends QSpec<S>> QObjectTabDelimBimapper<S> newInstance( S spec )
	{
		return new QObjectTabDelimBimapper<S>( spec );
	}
	
	public QObjectTabDelimBimapper<S> addColumn( Attribute<?> attribute , Bimapper bimapper )
	{
		return addColumn( attribute == null ? null : attribute.getName( ) , attribute , bimapper );
	}
	
	public QObjectTabDelimBimapper<S> addColumn( String name , Attribute<?> attribute , Bimapper bimapper )
	{
		colNames.add( name );
		colAttributes.add( attribute );
		colBimappers.add( bimapper );
		return this;
	}
	
	public QObjectTabDelimBimapper<S> addColumn( Attribute<?> attribute )
	{
		return addColumn( attribute == null ? null : attribute.getName( ) , attribute );
	}
	
	public QObjectTabDelimBimapper<S> addColumn( String name , Attribute<?> attribute )
	{
		if( attribute == null )
		{
			return addColumn( name , null , null );
		}
		Bimapper bimapper = null;
		Class<?> valueClass = attribute.getValueClass( );
		if( valueClass == String.class )
		{
			bimapper = StringBimapper.instance;
		}
		if( valueClass == Boolean.class )
		{
			bimapper = BooleanBimapper.instance;
		}
		else if( valueClass == Integer.class )
		{
			bimapper = IntegerBimapper.instance;
		}
		else if( valueClass == Long.class )
		{
			bimapper = LongBimapper.instance;
		}
		else if( valueClass == Float.class )
		{
			bimapper = FloatBimapper.instance;
		}
		else if( valueClass == float[ ].class )
		{
			bimapper = FloatArray2ListBimapper.instance;
		}
		else if( valueClass.isEnum( ) )
		{
			bimapper = EnumBimapper.newInstance( ( Class<Enum> ) valueClass );
		}
		else if( valueClass == Double.class )
		{
			bimapper = DoubleBimapper.instance;
		}
		else if( valueClass == BigInteger.class )
		{
			bimapper = BigIntegerBimapper.instance;
		}
		else if( valueClass == BigDecimal.class )
		{
			bimapper = BigDecimalBimapper.instance;
		}
		return addColumn( name , attribute , bimapper );
	}
	
	public String createHeader( )
	{
		StringBuilder sb = new StringBuilder( );
		for( int i = 0 ; i < colAttributes.size( ) ; i++ )
		{
			if( i > 0 )
			{
				sb.append( '\t' );
			}
			if( colNames.get( i ) == null )
			{
				continue;
			}
			sb.append( colNames.get( i ) );
		}
		return sb.toString( );
	}
	
	public QObjectTabDelimBimapper<S> deriveFromHeader( String header )
	{
		Map<String, Integer> colIndices = new HashMap<String, Integer>( );
		for( int i = 0 ; i < colNames.size( ) ; i++ )
		{
			colIndices.put( colNames.get( i ) , i );
		}
		
		QObjectTabDelimBimapper<S> result = newInstance( spec );
		
		String[ ] cols = header.split( "\\t" );
		for( String name : cols )
		{
			Integer index = colIndices.get( name );
			if( index != null )
			{
				result.addColumn( colNames.get( index ) , colAttributes.get( index ) , colBimappers.get( index ) );
			}
			else
			{
				result.addColumn( null , null , null );
			}
		}
		
		return result;
	}
	
	@Override
	public String map( QObject<S> in )
	{
		if( in == null )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder( );
		
		for( int i = 0 ; i < colAttributes.size( ) ; i++ )
		{
			if( i > 0 )
			{
				sb.append( '\t' );
			}
			if( colAttributes.get( i ) == null )
			{
				continue;
			}
			Bimapper bimapper = colBimappers.get( i );
			Object value = in.get( colAttributes.get( i ) );
			if( value != null )
			{
				sb.append( bimapper == null ? value.toString( ) : bimapper.map( value ) );
			}
		}
		return sb.toString( );
	}
	
	@Override
	public QObject<S> unmap( String out )
	{
		if( out == null )
		{
			return null;
		}
		QObject<S> result = spec.newObject( );
		
		String[ ] cols = out.split( "\\t" );
		
		for( int i = 0 ; i < Math.min( cols.length , colAttributes.size( ) ) ; i++ )
		{
			Attribute<?> attr = colAttributes.get( i );
			if( attr == null )
			{
				continue;
			}
			Bimapper bimapper = colBimappers.get( i );
			try
			{
				result.set( attr , bimapper == null ? cols[ i ] : bimapper.unmap( cols[ i ] ) );
			}
			catch( Throwable t )
			{
				LOGGER.log( Level.WARNING , "Failed to set attribute: " + attr , t );
			}
		}
		
		return result;
	}
}

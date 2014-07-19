package org.andork.breakout.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.func.Bimapper;
import org.andork.jogl.AutoClipOrthoProjectionCalculator;
import org.andork.jogl.PerspectiveProjectionCalculator;
import org.andork.jogl.ProjectionCalculator;
import org.andork.math3d.Vecmath;
import org.andork.util.ArrayUtils;
import org.andork.util.StringUtils;

public class ProjectionCalculatorBimapper implements Bimapper<ProjectionCalculator, Object>
{
	public static final ProjectionCalculatorBimapper	instance	= new ProjectionCalculatorBimapper( );
	
	private ProjectionCalculatorBimapper( )
	{
	}
	
	@Override
	public Object map( ProjectionCalculator in )
	{
		if( in == null )
		{
			return null;
		}
		
		if( in instanceof PerspectiveProjectionCalculator )
		{
			return map( ( PerspectiveProjectionCalculator ) in );
		}
		else if( in instanceof AutoClipOrthoProjectionCalculator )
		{
			return map( ( AutoClipOrthoProjectionCalculator ) in );
		}
		throw new IllegalArgumentException( "Unsupported type: " + in.getClass( ) );
	}
	
	public Map<Object, Object> map( PerspectiveProjectionCalculator in )
	{
		Map<Object, Object> result = new LinkedHashMap<>( );
		result.put( "type" , "perspective" );
		result.put( "fovAngle" , in.fovAngle );
		result.put( "zNear" , in.zNear );
		result.put( "zFar" , in.zFar );
		return result;
	}
	
	public Map<Object, Object> map( AutoClipOrthoProjectionCalculator in )
	{
		Map<Object, Object> result = new LinkedHashMap<>( );
		result.put( "type" , "ortho" );
		result.put( "hSpan" , in.hSpan );
		result.put( "vSpan" , in.vSpan );
		result.put( "center" , ArrayUtils.toArrayList( in.center ) );
		result.put( "radius" , in.radius );
		if( in.useNearClipPoint )
		{
			result.put( "nearClipPoint" , ArrayUtils.toArrayList( in.nearClipPoint ) );
		}
		if( in.useFarClipPoint )
		{
			result.put( "farClipPoint" , ArrayUtils.toArrayList( in.farClipPoint ) );
		}
		return result;
	}
	
	@Override
	public ProjectionCalculator unmap( Object out )
	{
		if( out == null )
		{
			return null;
		}
		
		if( out instanceof Map )
		{
			Map<Object, Object> map = ( Map<Object, Object> ) out;
			String type = StringUtils.toStringOrNull( map.get( "type" ) );
			if( "perspective".equals( type ) )
			{
				return unmapPerspective( map );
			}
			else if( "ortho".equals( type ) )
			{
				return unmapOrtho( map );
			}
			throw new IllegalArgumentException( "unsupported type: " + type );
		}
		throw new IllegalArgumentException( "Invalid type: " + out.getClass( ) );
	}
	
	public PerspectiveProjectionCalculator unmapPerspective( Map<Object, Object> map )
	{
		return new PerspectiveProjectionCalculator(
				getFloat( map , "fovAngle" ) ,
				getFloat( map , "zNear" ) ,
				getFloat( map , "zFar" ) );
	}
	
	private static float getFloat( Map<Object, Object> map , Object key )
	{
		return ( ( Number ) map.get( key ) ).floatValue( );
	}
	
	public AutoClipOrthoProjectionCalculator unmapOrtho( Map<Object, Object> map )
	{
		AutoClipOrthoProjectionCalculator result = new AutoClipOrthoProjectionCalculator( );
		result.hSpan = getFloat( map , "hSpan" );
		result.vSpan = getFloat( map , "vSpan" );
		Vecmath.setf( result.center , ArrayUtils.toFloatArray2( ( List<Number> ) map.get( "center" ) ) );
		result.radius = getFloat( map , "radius" );
		result.useNearClipPoint = map.containsKey( "nearClipPoint" );
		if( result.useNearClipPoint )
		{
			Vecmath.setf( result.nearClipPoint , ArrayUtils.toFloatArray2( ( List<Number> ) map.get( "nearClipPoint" ) ) );
		}
		result.useFarClipPoint = map.containsKey( "farClipPoint" );
		if( result.useFarClipPoint )
		{
			Vecmath.setf( result.farClipPoint , ArrayUtils.toFloatArray2( ( List<Number> ) map.get( "farClipPoint" ) ) );
		}
		return result;
	}
}

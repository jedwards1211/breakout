package org.andork.math3d.curve;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.j3d.math.TransformComputer3f;
import org.andork.math3d.Spline3d;

public class SplineCurve3f implements ICurveWithNormals3f
{
	private Vector3f	m_startTangent	= new Vector3f( );
	private Vector3f	m_endTangent	= new Vector3f( );
	private Vector3f	m_startNormalX	= new Vector3f( );
	private Vector3f	m_startNormalY	= new Vector3f( );
	
	private float		m_lowerBound;
	private float		m_upperBound;
	private float		m_step;
	
	private int			m_count;
	private float[ ]	m_depths;
	private Point3f[ ]	m_points;
	private Vector3f[ ]	m_tangents;
	private Vector3f[ ]	m_normalsX;
	private Vector3f[ ]	m_normalsY;
	
	public SplineCurve3f( float[ ] depths , Point3f[ ] points , float angle , int smoothing , float startDepth , float endDepth , float step )
	{
		init( depths , points , angle , smoothing , startDepth , endDepth , step );
	}
	
	private void init( float[ ] depths , Point3f[ ] points , float angle , int smoothing , float startDepth , float endDepth , float step )
	{
		m_depths = depths;
		m_points = points;
		
		m_count = depths.length;
		m_tangents = org.andork.vecmath.VecmathUtils.allocVector3fArray( m_count );
		m_normalsX = org.andork.vecmath.VecmathUtils.allocVector3fArray( m_count );
		
		// compute start tangent
		
		if( m_count < 2 )
		{
			m_startTangent.set( 0 , 0 , 1 );
			m_endTangent.set( 0 , 0 , 1 );
			m_step = 0;
			m_lowerBound = m_depths[ 0 ];
			m_upperBound = m_lowerBound;
		}
		else
		{
			m_startTangent.sub( points[ 1 ] , points[ 0 ] );
			m_endTangent.sub( points[ m_count - 1 ] , points[ m_count - 2 ] );
			m_step = m_depths[ 1 ] - m_depths[ 0 ];
			m_lowerBound = m_depths[ 0 ];
			m_upperBound = m_depths[ m_count - 1 ];
		}
		m_tangents[ 0 ].normalize( m_startTangent );
		
		// compute start normal
		
		// canonical zero-angle start normal: vector in XY plane 90 deg counterclockwise from start tangent
		// when looking in +z direction. If start tangent is +z, start normal is +x
		
		if( m_startTangent.x == 0 && m_startTangent.y == 0 )
		{
			m_startNormalX.set( 1 , 0 , 0 );
		}
		else
		{
			m_startNormalX.set( m_startTangent );
			m_startNormalX.z = 0;
			m_startNormalX.normalize( );
			
			// rotate 90 degrees CCW
			float y = m_startNormalX.y;
			m_startNormalX.y = -m_startNormalX.x;
			m_startNormalX.x = y;
		}
		
		// rotate start normal to start angle
		
		Transform3D xform = new Transform3D( );
		AxisAngle4f aa = new AxisAngle4f( m_startTangent , angle );
		xform.set( aa );
		xform.transform( m_startNormalX );
		
		m_normalsX[ 0 ].set( m_startNormalX );
		
		// compute 90deg start normal
		
		m_startNormalY.cross( m_startTangent , m_startNormalX );
		
		// compute normals
		
		TransformComputer3f tc = new TransformComputer3f( );
		
		for( int i = 1 ; i < m_count ; i++ )
		{
			m_tangents[ i ].sub( points[ i ] , points[ i - 1 ] );
			m_tangents[ i ].normalize( );
			
			tc.orient( m_tangents[ i - 1 ] , m_tangents[ i ] , xform );
			
			xform.transform( m_normalsX[ i - 1 ] , m_normalsX[ i ] );
		}
		
		// smooth normals
		
		Vector3f[ ] normals1s = org.andork.vecmath.VecmathUtils.allocVector3fArray( m_count );
		
		if( smoothing > 0 )
		{
			Vector3f v = new Vector3f( );
			for( int i = -smoothing ; i < m_count + smoothing ; i++ )
			{
				int ahead = i + smoothing;
				int behind = i - smoothing - 1;
				
				if( ahead < m_count )
				{
					v.add( m_normalsX[ ahead ] );
				}
				if( behind >= 0 )
				{
					v.sub( m_normalsX[ behind ] );
				}
				
				if( i >= 0 && i < m_count )
				{
					normals1s[ i ].normalize( v );
				}
			}
			m_normalsX = normals1s;
		}
		
		if( m_lowerBound != startDepth || m_upperBound != endDepth || m_step != step )
		{
			// compute 90deg normals and convert to double arrays
			
			double[ ] m = new double[ m_count ];
			double[ ] px = new double[ m_count ];
			double[ ] py = new double[ m_count ];
			double[ ] pz = new double[ m_count ];
			double[ ] tx = new double[ m_count ];
			double[ ] ty = new double[ m_count ];
			double[ ] tz = new double[ m_count ];
			double[ ] n1x = new double[ m_count ];
			double[ ] n1y = new double[ m_count ];
			double[ ] n1z = new double[ m_count ];
			
			for( int i = 0 ; i < m_count ; i++ )
			{
				m[ i ] = depths[ i ];
				px[ i ] = points[ i ].x;
				py[ i ] = points[ i ].y;
				pz[ i ] = points[ i ].z;
				tx[ i ] = m_tangents[ i ].x;
				ty[ i ] = m_tangents[ i ].y;
				tz[ i ] = m_tangents[ i ].z;
				n1x[ i ] = m_normalsX[ i ].x;
				n1y[ i ] = m_normalsX[ i ].y;
				n1z[ i ] = m_normalsX[ i ].z;
			}
			
			// create splines
			
			Spline3d m_pointSpline = new Spline3d( m , px , py , pz );
			Spline3d m_tangentSpline = new Spline3d( m , tx , ty , tz );
			Spline3d m_normal1Spline = new Spline3d( m , n1x , n1y , n1z );
			
			// compute final points from splines
			
			m_lowerBound = startDepth;
			m_upperBound = endDepth;
			m_step = step;
			m_count = ( int ) Math.ceil( ( m_upperBound - m_lowerBound ) / m_step ) + 1;
			
			m_depths = new float[ m_count ];
			m_points = org.andork.vecmath.VecmathUtils.allocPoint3fArray( m_count );
			m_tangents = org.andork.vecmath.VecmathUtils.allocVector3fArray( m_count );
			m_normalsX = org.andork.vecmath.VecmathUtils.allocVector3fArray( m_count );
			
			float depth = m_lowerBound;
			for( int i = 0 ; i < m_count ; i++ , depth += m_step )
			{
				m_depths[ i ] = depth;
				
				if( depth < m_lowerBound )
				{
					m_points[ i ].scaleAdd( ( depth - m_lowerBound ) / m_step , m_startTangent , m_points[ 0 ] );
				}
				else if( depth > m_upperBound )
				{
					m_points[ i ].scaleAdd( ( depth - m_upperBound ) / m_step , m_endTangent , m_points[ m_count - 1 ] );
				}
				else
				{
					m_pointSpline.evaluate( depth , m_points[ i ] );
				}
				
				float cdepth = Math.min( Math.max( m_lowerBound , depth ) , m_upperBound );
				
				m_tangentSpline.evaluate( cdepth , m_tangents[ i ] );
				m_normal1Spline.evaluate( cdepth , m_normalsX[ i ] );
			}
		}
		
		m_normalsY = org.andork.vecmath.VecmathUtils.allocVector3fArray( m_count );
		for( int i = 0 ; i < m_count ; i++ )
		{
			m_normalsY[ i ].cross( m_tangents[ i ] , m_normalsX[ i ] );
		}
	}
	
	public float getLowerBound( )
	{
		return m_lowerBound;
	}
	
	public float getUpperBound( )
	{
		return m_upperBound;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.petronworld.rigfocus.gui.tvd.scenegraph.utils.Sweep3f#getPoint(float, javax.vecmath.Point3f)
	 */
	@Override
	public Point3f getPoint( float depth , Point3f result )
	{
		float fi = ( depth - m_lowerBound ) / m_step;
		int i = ( int ) fi;
		float f = fi - i;
		
		if( fi < 0 )
		{
			result.scaleAdd( fi , m_startTangent , m_points[ 0 ] );
		}
		else if( i >= m_count - 1 )
		{
			result.scaleAdd( fi - m_count , m_endTangent , m_points[ m_count - 1 ] );
		}
		else
		{
			if( m_depths[ i ] == depth )
			{
				result.set( m_points[ i ] );
			}
			else
			{
				result.interpolate( m_points[ i ] , m_points[ i + 1 ] , f );
			}
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.petronworld.rigfocus.gui.tvd.scenegraph.utils.Sweep3f#getTangent(float, javax.vecmath.Vector3f)
	 */
	@Override
	public Vector3f getTangent( float depth , Vector3f result )
	{
		if( depth <= m_lowerBound )
		{
			result.set( m_tangents[ 0 ] );
		}
		else if( depth >= m_upperBound )
		{
			result.set( m_tangents[ m_count - 1 ] );
		}
		else
		{
			float fi = ( depth - m_lowerBound ) / m_step;
			int i = ( int ) fi;
			float f = fi - i;
			
			if( m_depths[ i ] == depth )
			{
				result.set( m_tangents[ i ] );
			}
			else
			{
				result.interpolate( m_tangents[ i ] , m_tangents[ i + 1 ] , f );
			}
		}
		return result;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.petronworld.rigfocus.gui.tvd.scenegraph.utils.Sweep3f#getNormal(float, javax.vecmath.Vector3f)
	 */
	@Override
	public Vector3f getNormalX( float depth , Vector3f result )
	{
		if( depth <= m_lowerBound )
		{
			result.set( m_normalsX[ 0 ] );
		}
		else if( depth >= m_upperBound )
		{
			result.set( m_normalsX[ m_count - 1 ] );
		}
		else
		{
			float fi = ( depth - m_lowerBound ) / m_step;
			int i = ( int ) fi;
			float f = fi - i;
			
			if( m_depths[ i ] == depth )
			{
				result.set( m_normalsX[ i ] );
			}
			else
			{
				result.interpolate( m_normalsX[ i ] , m_normalsX[ i + 1 ] , f );
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.petronworld.rigfocus.gui.tvd.scenegraph.utils.Sweep3f#get90DegNormal(float, javax.vecmath.Vector3f)
	 */
	@Override
	public Vector3f getNormalY( float depth , Vector3f result )
	{
		if( depth <= m_lowerBound )
		{
			result.set( m_normalsY[ 0 ] );
		}
		else if( depth >= m_upperBound )
		{
			result.set( m_normalsY[ m_count - 1 ] );
		}
		else
		{
			float fi = ( depth - m_lowerBound ) / m_step;
			int i = ( int ) fi;
			float f = fi - i;
			
			if( m_depths[ i ] == depth )
			{
				result.set( m_normalsY[ i ] );
			}
			else
			{
				result.interpolate( m_normalsY[ i ] , m_normalsY[ i + 1 ] , f );
			}
		}
		return result;
	}
}
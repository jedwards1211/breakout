package org.andork.survey;

import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.HashSet;

import org.andork.vecmath.Vecmath;

import com.andork.plot.AlphaDepthComposite;
import com.andork.plot.Axis;
import com.andork.plot.IPlotLayer;
import com.andork.plot.LinearAxisConversion;

public class PlanSurveyLayer implements IPlotLayer
{
	public LinearAxisConversion		xAxisConversion;
	public LinearAxisConversion		yAxisConversion;
	
	public Collection<SurveyShot>	shots		= new HashSet<SurveyShot>( );
	public GradientMap				gradientMap;
	
	final double[ ]					fromLoc		= new double[ 3 ];
	final double[ ]					toLoc		= new double[ 3 ];
	final double[ ]					toToLoc		= new double[ 3 ];
	final double[ ]					leftAtTo	= new double[ 3 ];
	final double[ ]					leftAtTo2	= new double[ 3 ];
	final double[ ]					leftAtFrom	= new double[ 3 ];
	
	public PlanSurveyLayer( Axis xaxis , Axis yaxis )
	{
		xAxisConversion = xaxis.getAxisConversion( );
		yAxisConversion = yaxis.getAxisConversion( );
		
		gradientMap = new GradientMap( );
		gradientMap.map.put( 0.0 , Color.YELLOW );
		gradientMap.map.put( -100.0 , new Color( 255 , 100 , 10 ) );
		gradientMap.map.put( -200.0 , new Color( 103 , 0 , 168 ) );
	}
	
	@Override
	public void render( Graphics2D g2 , Rectangle bounds )
	{
		Paint prevPaint = g2.getPaint( );
		// Composite prevComp = g2.getComposite( );
		
		// g2.setComposite( new AlphaDepthComposite( ) );
		
		if( shots != null )
		{
			for( SurveyShot shot : shots )
			{
				draw( shot , g2 );
			}
		}
		
		g2.setPaint( prevPaint );
		// g2.setComposite( prevComp );
	}
	
	private void draw( SurveyShot shot , Graphics2D g2 )
	{
		fromLoc[ 0 ] = shot.from.position[ 0 ];
		fromLoc[ 2 ] = shot.from.position[ 2 ];
		
		toLoc[ 0 ] = shot.to.position[ 0 ];
		toLoc[ 2 ] = shot.to.position[ 2 ];
		
		leftAtFrom[ 0 ] = fromLoc[ 2 ] - toLoc[ 2 ];
		leftAtFrom[ 2 ] = toLoc[ 0 ] - fromLoc[ 0 ];
		leftAtFrom[ 1 ] = 0;
		
		Vecmath.normalize3( leftAtFrom );
		
		Path2D.Double path = new Path2D.Double( );
		
		path.moveTo( xAxisConversion.convert( fromLoc[ 0 ] + leftAtFrom[ 0 ] * shot.left ) , yAxisConversion.convert( fromLoc[ 2 ] + leftAtFrom[ 2 ] * shot.left ) );
		path.lineTo( xAxisConversion.convert( fromLoc[ 0 ] - leftAtFrom[ 0 ] * shot.right ) , yAxisConversion.convert( fromLoc[ 2 ] - leftAtFrom[ 2 ] * shot.right ) );
		
		if( !shot.to.frontsights.isEmpty( ) )
		{
			double bestWidth = 0;
			SurveyShot bestShot = null;
			
			for( SurveyShot nextShot : shot.to.frontsights )
			{
				toToLoc[ 0 ] = nextShot.to.position[ 0 ];
				toToLoc[ 2 ] = nextShot.to.position[ 2 ];
				
				leftAtTo2[ 0 ] = toLoc[ 2 ] - toToLoc[ 2 ];
				leftAtTo2[ 2 ] = toToLoc[ 0 ] - toLoc[ 0 ];
				leftAtTo2[ 1 ] = 0;
				
				Vecmath.normalize3( leftAtTo2 );
				
				double dot = Vecmath.dot3( leftAtFrom , leftAtTo2 );
				double width = Math.abs( dot );
				if( width > bestWidth )
				{
					bestShot = nextShot;
					bestWidth = width;
					leftAtTo[ 0 ] = Math.signum( dot ) * leftAtTo2[ 0 ];
					leftAtTo[ 2 ] = Math.signum( dot ) * leftAtTo2[ 2 ];
				}
			}
			
			path.lineTo( xAxisConversion.convert( toLoc[ 0 ] - leftAtTo[ 0 ] * bestShot.right ) , yAxisConversion.convert( toLoc[ 2 ] - leftAtTo[ 2 ] * bestShot.right ) );
			path.lineTo( xAxisConversion.convert( toLoc[ 0 ] + leftAtTo[ 0 ] * bestShot.left ) , yAxisConversion.convert( toLoc[ 2 ] + leftAtTo[ 2 ] * bestShot.left ) );
		}
		else
		{
			path.lineTo( xAxisConversion.convert( toLoc[ 0 ] ) , yAxisConversion.convert( toLoc[ 2 ] ) );
		}
		
		path.closePath( );
		
		g2.setPaint( new GradientPaint( ( float ) xAxisConversion.convert( fromLoc[ 0 ] ) , ( float ) yAxisConversion.convert( fromLoc[ 2 ] ) , gradientMap.getColor( shot.from.position[ 1 ] ) ,
				( float ) xAxisConversion.convert( toLoc[ 0 ] ) , ( float ) yAxisConversion.convert( toLoc[ 2 ] ) , gradientMap.getColor( shot.to.position[ 1 ] ) ) );
		
		g2.fill( path );
	}
}

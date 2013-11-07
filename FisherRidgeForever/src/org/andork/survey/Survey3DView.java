package org.andork.survey;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.DepthFragmentShader;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.jogl.basic.JOGLLineWidthModifier;
import org.andork.jogl.basic.JOGLPolygonOffsetModifier;
import org.andork.jogl.basic.awt.BasicJOGLSetup;
import org.andork.vecmath.Vecmath;

import com.andork.plot.Axis;
import com.andork.plot.Axis.LabelPosition;
import com.andork.plot.Axis.Orientation;
import com.andork.plot.AxisController;
import com.andork.plot.MouseAdapterChain;
import com.andork.plot.Plot;
import com.andork.plot.PlotController;
import com.andork.plot.PlotPanelLayout;

public class Survey3DView extends BasicJOGLSetup
{
	final double[ ]		fromLoc		= new double[ 3 ];
	final double[ ]		toLoc		= new double[ 3 ];
	final double[ ]		toToLoc		= new double[ 3 ];
	final double[ ]		leftAtTo	= new double[ 3 ];
	final double[ ]		leftAtTo2	= new double[ 3 ];
	final double[ ]		leftAtFrom	= new double[ 3 ];
	
	Axis				xaxis;
	Axis				yaxis;
	
	Plot				plot;
	JPanel				plotPanel;
	JPanel				mainPanel;
	
	PlotController		plotController;
	MouseAdapterChain	mouseAdapterChain;
	
	JComboBox			modeComboBox;
	
	public Survey3DView( )
	{
		super( );
		
		scene.orthoFrame[ 4 ] = -10000f;
		scene.orthoFrame[ 5 ] = 10000f;
		
		plot = new Plot( );
		plot.setLayout( new BorderLayout( ) );
		plot.add( canvas , BorderLayout.CENTER );
		
		xaxis = new Axis( Orientation.HORIZONTAL , LabelPosition.TOP );
		yaxis = new Axis( Orientation.VERTICAL , LabelPosition.LEFT );
		
		yaxis.getAxisConversion( ).set( 50 , 0 , -50 , 400 );
		
		xaxis.addPlot( plot );
		yaxis.addPlot( plot );
		
		new AxisController( xaxis )
		{
			@Override
			protected void setAxisRange( double start , double end )
			{
				super.setAxisRange( start , end );
				scene.orthoFrame[ 0 ] = ( float ) xaxis.getAxisConversion( ).invert( 0 );
				scene.orthoFrame[ 1 ] = ( float ) xaxis.getAxisConversion( ).invert( plot.getWidth( ) );
				scene.recomputeProjection( );
				canvas.repaint( );
			}
		};
		new AxisController( yaxis )
		{
			@Override
			protected void setAxisRange( double start , double end )
			{
				super.setAxisRange( start , end );
				scene.orthoFrame[ 2 ] = ( float ) yaxis.getAxisConversion( ).invert( plot.getHeight( ) );
				scene.orthoFrame[ 3 ] = ( float ) yaxis.getAxisConversion( ).invert( 0 );
				scene.recomputeProjection( );
				canvas.repaint( );
			}
		};
		
		plotController = new PlotController( plot , xaxis , yaxis );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( plotController );
		
		plotPanel = new JPanel( new PlotPanelLayout( ) );
		plotPanel.add( plot );
		plotPanel.add( xaxis );
		plotPanel.add( yaxis );
		
		canvas.removeMouseListener( navigator );
		canvas.removeMouseMotionListener( navigator );
		canvas.removeMouseWheelListener( navigator );
		
		canvas.removeMouseListener( orbiter );
		canvas.removeMouseMotionListener( orbiter );
		canvas.removeMouseWheelListener( orbiter );
		
		perspectiveMode( );
		
		modeComboBox = new JComboBox( );
		modeComboBox.addItem( "Perspective" );
		modeComboBox.addItem( "Plan" );
		modeComboBox.addItem( "North-Facing Profile" );
		modeComboBox.addItem( "South-Facing Profile" );
		modeComboBox.addItem( "East-Facing Profile" );
		modeComboBox.addItem( "West-Facing Profile" );
		
		mainPanel = new JPanel( new BorderLayout( ) );
		mainPanel.add( modeComboBox , BorderLayout.NORTH );
		mainPanel.add( plotPanel , BorderLayout.CENTER );
		
		modeComboBox.addItemListener( new ItemListener( )
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				if( e.getStateChange( ) == ItemEvent.DESELECTED )
				{
					return;
				}
				switch( modeComboBox.getSelectedIndex( ) )
				{
					case 0:
						perspectiveMode( );
						break;
					case 1:
						planMode( );
						break;
					case 2:
						northFacingProfileMode( );
						break;
					case 3:
						southFacingProfileMode( );
						break;
					case 4:
						eastFacingProfileMode( );
						break;
					case 5:
						westFacingProfileMode( );
						break;
				}
			}
		} );
	}
	
	@Override
	protected void init( )
	{
		super.init( );
		
		navigator.setMoveFactor( 5f );
		navigator.setWheelFactor( 5f );
	}
	
	public void planMode( )
	{
		orthoMode( );
		
		Vecmath.setRow4( scene.v , 0 , 1 , 0 , 0 , 0 );
		Vecmath.setRow4( scene.v , 1 , 0 , 0 , -1 , 0 );
		Vecmath.setRow4( scene.v , 2 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( scene.v , 3 , 0 , 0 , 0 , 1 );
	}
	
	public void northFacingProfileMode( )
	{
		orthoMode( );
		
		Vecmath.setRow4( scene.v , 0 , 1 , 0 , 0 , 0 );
		Vecmath.setRow4( scene.v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( scene.v , 2 , 0 , 0 , 1 , 0 );
		Vecmath.setRow4( scene.v , 3 , 0 , 0 , 0 , 1 );
	}
	
	public void southFacingProfileMode( )
	{
		orthoMode( );
		
		Vecmath.setRow4( scene.v , 0 , -1 , 0 , 0 , 0 );
		Vecmath.setRow4( scene.v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( scene.v , 2 , 0 , 0 , -1 , 0 );
		Vecmath.setRow4( scene.v , 3 , 0 , 0 , 0 , 1 );
	}
	
	public void eastFacingProfileMode( )
	{
		orthoMode( );
		
		Vecmath.setRow4( scene.v , 0 , 0 , 0 , 1 , 0 );
		Vecmath.setRow4( scene.v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( scene.v , 2 , -1 , 0 , 0 , 0 );
		Vecmath.setRow4( scene.v , 3 , 0 , 0 , 0 , 1 );
	}
	
	public void westFacingProfileMode( )
	{
		orthoMode( );
		
		Vecmath.setRow4( scene.v , 0 , 0 , 0 , -1 , 0 );
		Vecmath.setRow4( scene.v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( scene.v , 2 , 1 , 0 , 0 , 0 );
		Vecmath.setRow4( scene.v , 3 , 0 , 0 , 0 , 1 );
	}
	
	private void orthoMode( )
	{
		xaxis.setVisible( true );
		yaxis.setVisible( true );
		
		mouseAdapterChain.uninstall( canvas );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( plotController );
		mouseAdapterChain.install( canvas );
		
		scene.setOrthoMode( true );
	}
	
	public void perspectiveMode( )
	{
		xaxis.setVisible( false );
		yaxis.setVisible( false );
		
		mouseAdapterChain.uninstall( canvas );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( navigator );
		mouseAdapterChain.addMouseAdapter( orbiter );
		mouseAdapterChain.install( canvas );
		
		scene.setOrthoMode( false );
	}
	
	@Override
	protected BasicJOGLScene createScene( )
	{
		return new BasicJOGLScene( );
	}
	
	public GLCanvas getCanvas( )
	{
		return canvas;
	}
	
	public JPanel getMainPanel( )
	{
		return mainPanel;
	}
	
	public void updateModel( List<SurveyShot> shots )
	{
		BufferHelper vertHelper = new BufferHelper( );
		BufferHelper fillIndexHelper = new BufferHelper( );
		BufferHelper lineIndexHelper = new BufferHelper( );
		
		int vertCount = 0;
		int fillIndexCount = 0;
		int lineIndexCount = 0;
		
		for( SurveyShot shot : shots )
		{
			fromLoc[ 0 ] = shot.from.position[ 0 ];
			fromLoc[ 2 ] = shot.from.position[ 1 ];
			
			toLoc[ 0 ] = shot.to.position[ 0 ];
			toLoc[ 1 ] = shot.to.position[ 1 ];
			
			if (Vecmath.distance3( shot.from.position , shot.to.position ) > 200) {
				System.out.println(shot.from.name + ": " +Arrays.toString(shot.from.position) + " - " + shot.to.name + ": " + Arrays.toString(shot.to.position));
			}
			
			leftAtFrom[ 0 ] = shot.from.position[ 2 ] - shot.to.position[ 2 ];
			leftAtFrom[ 1 ] = 0;
			leftAtFrom[ 2 ] = shot.to.position[ 0 ] - shot.from.position[ 0 ];
			
			if( leftAtFrom[ 0 ] != 0 || leftAtFrom[ 2 ] != 0 )
			{
				Vecmath.normalize3( leftAtFrom );
			}
			
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putFloats( shot.from.position[ i ] + leftAtFrom[ i ] * shot.left );
			}
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putFloats( shot.from.position[ i ] - leftAtFrom[ i ] * shot.right );
			}
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putFloats( shot.from.position[ i ] + ( i == 1 ? shot.up : 0.0 ) );
			}
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putFloats( shot.from.position[ i ] - ( i == 1 ? shot.down : 0.0 ) );
			}
			
			SurveyShot nextNonVertical = nextNonVerticalShot( shot );
			
			if( nextNonVertical != null )
			{
				double bestWidth = -1.0;
				SurveyShot bestShot = null;
				
				for( SurveyShot nextShot : nextNonVertical.from.frontsights )
				{
					toToLoc[ 0 ] = nextShot.to.position[ 0 ];
					toToLoc[ 2 ] = nextShot.to.position[ 2 ];
					
					leftAtTo2[ 0 ] = shot.to.position[ 2 ] - nextShot.to.position[ 2 ];
					leftAtTo2[ 1 ] = 0;
					leftAtTo2[ 2 ] = nextShot.to.position[ 0 ] - shot.to.position[ 0 ];
					
					if( leftAtTo2[ 0 ] != 0 || leftAtTo2[ 2 ] != 0 )
					{
						Vecmath.normalize3( leftAtTo2 );
					}
					
					double dot = Vecmath.dot3( leftAtFrom , leftAtTo2 );
					double width = Math.abs( dot );
					if( width > bestWidth )
					{
						bestShot = nextShot;
						bestWidth = width;
						leftAtTo[ 0 ] = leftAtTo2[ 0 ];
						leftAtTo[ 2 ] = leftAtTo2[ 2 ];
					}
				}
				
				if( bestShot == null )
				{
					System.err.println( shot.from.name );
				}
				
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putFloats( shot.to.position[ i ] + leftAtTo[ i ] * bestShot.left );
				}
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putFloats( shot.to.position[ i ] - leftAtTo[ i ] * bestShot.right );
				}
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putFloats( shot.to.position[ i ] + ( i == 1 ? bestShot.up : 0.0 ) );
				}
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putFloats( shot.to.position[ i ] - ( i == 1 ? bestShot.down : 0.0 ) );
				}
				
				fillIndexHelper.put( offset( vertCount ,
						0 , 4 , 2 , 6 , 2 , 4 ,
						2 , 6 , 1 , 5 , 1 , 6 ,
						1 , 5 , 3 , 7 , 3 , 5 ,
						3 , 7 , 0 , 4 , 0 , 7 ) );
				
				lineIndexHelper.put( offset( vertCount ,
						0 , 4 , 0 , 2 , 4 , 2 , 4 , 6 ,
						2 , 6 , 2 , 1 , 6 , 1 , 6 , 5 ,
						1 , 5 , 1 , 3 , 5 , 3 , 5 , 7 ,
						3 , 7 , 3 , 0 , 7 , 0 , 7 , 4 ) );
				
				vertCount += 8;
				fillIndexCount += 24;
				lineIndexCount += 32;
			}
			else
			{
				vertHelper.putFloats( shot.to.position );
				
				fillIndexHelper.put( offset( vertCount ,
						0 , 4 , 2 , 2 , 4 , 1 , 1 , 4 , 3 , 3 , 4 , 0 ) );
				
				lineIndexHelper.put( offset( vertCount ,
						0 , 4 , 0 , 2 ,
						2 , 4 , 2 , 1 ,
						1 , 4 , 1 , 3 ,
						3 , 4 , 3 , 0 ) );
				
				vertCount += 5;
				fillIndexCount += 12;
				lineIndexCount += 16;
			}
		}
		
		ByteBuffer vertBuffer = vertHelper.toByteBuffer( );
		float[ ] bounds = getBounds( vertBuffer , 0 , 12 , vertCount , 3 );
		
		float[ ] c = new float[ 3 ];
		c[ 0 ] = ( bounds[ 0 ] + bounds[ 3 ] ) * .5f;
		c[ 1 ] = ( bounds[ 1 ] + bounds[ 4 ] ) * .5f;
		c[ 2 ] = ( bounds[ 2 ] + bounds[ 5 ] ) * .5f;
		
		orbiter.setCenter( c );
		
		float dx = bounds[ 3 ] - c[ 0 ];
		float dy = bounds[ 4 ] - c[ 1 ];
		float dz = bounds[ 5 ] - c[ 2 ];
		float radius = ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
		
		scene.clear( );
		
		if( vertCount > 0 )
		{
			BasicJOGLObject fillObj = new BasicJOGLObject( );
			fillObj.addVertexBuffer( vertBuffer ).vertexCount( vertCount );
			fillObj.drawMode( GL2ES2.GL_TRIANGLES );
			fillObj.indexBuffer( fillIndexHelper.toByteBuffer( ) ).indexCount( fillIndexCount ).indexType( GL2ES2.GL_UNSIGNED_INT );
			fillObj.transpose( true );
			fillObj.vertexShaderCode( new BasicVertexShader( ).passPosToFragmentShader( true ).toString( ) );
			fillObj.fragmentShaderCode( new DepthFragmentShader( ).nearColor( 1 , 0 , 0 , 1 ).farColor( 0.3f , 0 , 0 , 1 ).center( c[ 0 ] , c[ 1 ] , c[ 2 ] ).radius( radius ).toString( ) );
			fillObj.add( fillObj.new Attribute3fv( ).name( "a_pos" ) );
			fillObj.add( new JOGLPolygonOffsetModifier( -5f , -5f ) );
			fillObj.add( new JOGLDepthModifier( ) );
			
			scene.initLater( fillObj );
			scene.add( fillObj );
			
			BasicJOGLObject lineObj = new BasicJOGLObject( );
			lineObj.addVertexBuffer( vertBuffer ).vertexCount( vertCount );
			lineObj.drawMode( GL2ES2.GL_LINES );
			lineObj.indexBuffer( lineIndexHelper.toByteBuffer( ) ).indexCount( lineIndexCount ).indexType( GL2ES2.GL_UNSIGNED_INT );
			lineObj.transpose( true );
			lineObj.vertexShaderCode( new BasicVertexShader( ).passPosToFragmentShader( true ).toString( ) );
			lineObj.fragmentShaderCode( new DepthFragmentShader( ).nearColor( 1 , 1 , 1 , 1 ).farColor( 0.3f , 0.3f , 0.3f , 1 ).center( c[ 0 ] , c[ 1 ] , c[ 2 ] ).radius( radius ).toString( ) );
			lineObj.add( lineObj.new Attribute3fv( ).name( "a_pos" ) );
			lineObj.add( new JOGLLineWidthModifier( 2.0f ) );
			lineObj.add( new JOGLDepthModifier( ) );
			
			scene.initLater( lineObj );
			scene.add( lineObj );
		}
		
		canvas.repaint( );
	}
	
	private SurveyShot nextNonVerticalShot( SurveyShot shot )
	{
		if( shot.to.frontsights.isEmpty( ) )
		{
			return null;
		}
		for( SurveyShot next : shot.to.frontsights )
		{
			if( !isVertical( next ) )
			{
				return next;
			}
		}
		for( SurveyShot next : shot.to.frontsights )
		{
			SurveyShot nextNext = nextNonVerticalShot( next );
			if( nextNext != null )
			{
				return nextNext;
			}
		}
		return null;
	}
	
	private static boolean isVertical( SurveyShot shot )
	{
		double inc = ( shot.fsInc + shot.bsInc ) * 0.5;
		inc = Math.floor( inc * 1000.0 ) / 1000.0;
		inc %= 360.0;
		return inc == 90.0 || inc == -90.0;
	}
	
	private float[ ] getBounds( ByteBuffer vertexBuffer , int vertexOffset , int vertexStride , int vertexCount , int dimension )
	{
		float[ ] result = new float[ dimension * 2 ];
		
		for( int d = 0 ; d < dimension ; d++ )
		{
			result[ d ] = Float.MAX_VALUE;
			result[ d + dimension ] = -Float.MAX_VALUE;
		}
		
		for( int i = 0 ; i < vertexCount ; i++ )
		{
			for( int d = 0 ; d < dimension ; d++ )
			{
				int index = vertexOffset + i * vertexStride + d * 4;
				float f = vertexBuffer.getFloat( index );
				result[ d ] = Math.min( result[ d ] , f );
				result[ dimension + d ] = Math.max( result[ dimension + d ] , f );
			}
		}
		
		return result;
	}
	
	private int[ ] offset( int offset , int ... in )
	{
		for( int i = 0 ; i < in.length ; i++ )
		{
			in[ i ] += offset;
		}
		return in;
	}
}

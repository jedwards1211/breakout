package org.andork.frf;

import static org.andork.math3d.Vecmath.newMat4f;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.awt.ColorUtils;
import org.andork.awt.DoSwing;
import org.andork.awt.GradientFillBorder;
import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.I18n;
import org.andork.awt.InnerGradientBorder;
import org.andork.awt.LayeredBorder;
import org.andork.awt.OverrideInsetsBorder;
import org.andork.awt.PaintablePanel;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.DrawerLayoutDelegate;
import org.andork.awt.layout.Side;
import org.andork.awt.layout.TabLayoutDelegate;
import org.andork.frf.model.Survey3dModel;
import org.andork.frf.model.SurveyShot;
import org.andork.frf.update.UpdateProperties;
import org.andork.frf.update.UpdateStatus;
import org.andork.frf.update.UpdateStatusPanel;
import org.andork.frf.update.UpdateStatusPanelController;
import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.FlatFragmentShader;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.awt.BasicJOGLSetup;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.spatial.Rectmath;

import com.andork.plot.AxisLinkButton;
import com.andork.plot.MouseAdapterChain;
import com.andork.plot.MouseLooper;
import com.andork.plot.Plot;
import com.andork.plot.PlotAxis;
import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;
import com.andork.plot.PlotAxisController;
import com.andork.plot.PlotController;
import com.andork.plot.PlotPanelLayout;

public class MapsView extends BasicJOGLSetup
{
	final double[ ]					fromLoc			= new double[ 3 ];
	final double[ ]					toLoc			= new double[ 3 ];
	final double[ ]					toToLoc			= new double[ 3 ];
	final double[ ]					leftAtTo		= new double[ 3 ];
	final double[ ]					leftAtTo2		= new double[ 3 ];
	final double[ ]					leftAtFrom		= new double[ 3 ];
	
	PlotAxis						xaxis;
	PlotAxis						yaxis;
	AxisLinkButton					axisLinkButton;
	PlotAxis						distColorationAxis;
	
	PaintablePanel					settingsPanel;
	JButton							settingsButton;
	JSlider							mouseSensitivitySlider;
	
	Plot							plot;
	JPanel							plotPanel;
	JPanel							mainPanel;
	JLayeredPane					layeredPane;
	
	PlotController					plotController;
	MouseLooper						mouseLooper;
	MouseAdapterChain				mouseAdapterChain;
	
	JComboBox						modeComboBox;
	
	JLayeredPane					surveyTableDrawer;
	
	SurveyTable						surveyTable;
	JScrollPane						surveyTableScrollPane;
	
	JPanel							statusBar;
	UpdateStatusPanel				updateStatusPanel;
	UpdateStatusPanelController		updateStatusPanelController;
	
	JButton							updateViewButton;
	
	Survey3dModel					model;
	
	float[ ]						v				= newMat4f( );
	
	int								debugMbrCount	= 0;
	List<BasicJOGLObject>			debugMbrs		= new ArrayList<BasicJOGLObject>( );
	
	final LinePlaneIntersection3f	lpx				= new LinePlaneIntersection3f( );
	final float[ ]					p0				= new float[ 3 ];
	final float[ ]					p1				= new float[ 3 ];
	final float[ ]					p2				= new float[ 3 ];
	
	public MapsView( )
	{
		super( );
		
		surveyTable = new SurveyTable( );
		surveyTableScrollPane = new JScrollPane( surveyTable );
		
		surveyTableDrawer = new JLayeredPane( );
		surveyTableDrawer.setLayout( new DelegatingLayoutManager( ) );
		
		JButton maximizeSurveyTableButton = new JButton( "Max" );
		surveyTableDrawer.setLayer( maximizeSurveyTableButton , JLayeredPane.DEFAULT_LAYER + 1 );
		
		surveyTableDrawer.add( surveyTableScrollPane );
		surveyTableDrawer.add( maximizeSurveyTableButton , new DrawerLayoutDelegate( maximizeSurveyTableButton , Corner.TOP_RIGHT , Side.TOP ) );
		
		final DrawerLayoutDelegate surveyTableDrawerDelegate = new DrawerLayoutDelegate( surveyTableDrawer , Side.BOTTOM , true );
		
		JButton openSurveyTableDrawerButton = new JButton( "\u2261" );
		openSurveyTableDrawerButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				surveyTableDrawerDelegate.toggleOpen( );
			}
		} );
		maximizeSurveyTableButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				surveyTableDrawerDelegate.toggleMaximized( );
			}
		} );
		
		scene.orthoFrame[ 4 ] = -10000f;
		scene.orthoFrame[ 5 ] = 10000f;
		
		plot = new Plot( );
		plot.setLayout( new BorderLayout( ) );
		plot.add( canvas , BorderLayout.CENTER );
		
		xaxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		xaxis.setBorder( new InnerGradientBorder( new Insets( 0 , 0 , 4 , 0 ) , Color.GRAY ) );
		LayeredBorder.addBorder( new InnerGradientBorder( new Insets( 0 , 20 , 0 , 20 ) , new Color( 240 , 240 , 240 ) ) , xaxis );
		OverrideInsetsBorder.override( xaxis , new Insets( 0 , 0 , 0 , 0 ) );
		
		yaxis = new PlotAxis( Orientation.VERTICAL , LabelPosition.LEFT );
		yaxis.setBorder( new InnerGradientBorder( new Insets( 0 , 0 , 0 , 4 ) , Color.GRAY ) );
		LayeredBorder.addBorder( new InnerGradientBorder( new Insets( 20 , 0 , 20 , 0 ) , new Color( 240 , 240 , 240 ) ) , yaxis );
		OverrideInsetsBorder.override( yaxis , new Insets( 0 , 0 , 0 , 0 ) );
		
		final GradientMap gradientMap = new GradientMap( );
		gradientMap.map.put( 0.0 , Color.RED );
		gradientMap.map.put( 1.0 , new Color( 255 * 3 / 10 , 0 , 0 ) );
		
		distColorationAxis = new PlotAxis( Orientation.VERTICAL , LabelPosition.RIGHT )
		{
			GradientBackgroundPainter	bgPainter	= new GradientBackgroundPainter( GradientBackgroundPainter.Orientation.VERTICAL , gradientMap );
			
			@Override
			protected void paintComponent( Graphics g )
			{
				bgPainter.paint( this , ( Graphics2D ) g );
				super.paintComponent( g );
			}
		};
		
		distColorationAxis.setForeground( Color.WHITE );
		distColorationAxis.setMajorTickColor( Color.WHITE );
		distColorationAxis.setMinorTickColor( Color.WHITE );
		distColorationAxis.addPlot( plot );
		
		yaxis.getAxisConversion( ).set( 50 , 0 , -50 , 400 );
		
		xaxis.addPlot( plot );
		yaxis.addPlot( plot );
		
		PlotAxisController xAxisController = new PlotAxisController( xaxis )
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
		PlotAxisController yAxisController = new PlotAxisController( yaxis )
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
		
		axisLinkButton = new AxisLinkButton( xAxisController , yAxisController );
		axisLinkButton.setSelected( true );
		
		new PlotAxisController( distColorationAxis )
		{
			@Override
			protected void setAxisRange( double start , double end )
			{
				super.setAxisRange( start , end );
				if( model != null )
				{
					model.setNearDist( ( float ) distColorationAxis.getAxisConversion( ).invert( 0 ) );
					model.setFarDist( ( float ) distColorationAxis.getAxisConversion( ).invert( distColorationAxis.getHeight( ) ) );
				}
				canvas.repaint( );
			}
		};
		
		plotController = new PlotController( plot , xAxisController , yAxisController );
		
		mouseLooper = new MouseLooper( );
		canvas.addMouseListener( mouseLooper );
		canvas.addMouseMotionListener( mouseLooper );
		canvas.addMouseWheelListener( mouseLooper );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( plotController );
		
		plotPanel = new JPanel( new PlotPanelLayout( ) );
		plotPanel.add( plot );
		plotPanel.add( xaxis );
		plotPanel.add( yaxis );
		plotPanel.add( distColorationAxis );
		plotPanel.add( axisLinkButton , Corner.TOP_LEFT );
		
		canvas.removeMouseListener( navigator );
		canvas.removeMouseMotionListener( navigator );
		canvas.removeMouseWheelListener( navigator );
		
		canvas.removeMouseListener( orbiter );
		canvas.removeMouseMotionListener( orbiter );
		canvas.removeMouseWheelListener( orbiter );
		
		perspectiveMode( );
		
		updateViewButton = new JButton( "Update View" );
		
		updateViewButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				updateModel( surveyTable.createShots( ) );
			}
		} );
		
		modeComboBox = new JComboBox( );
		modeComboBox.addItem( "Perspective" );
		modeComboBox.addItem( "Plan" );
		modeComboBox.addItem( "North-Facing Profile" );
		modeComboBox.addItem( "South-Facing Profile" );
		modeComboBox.addItem( "East-Facing Profile" );
		modeComboBox.addItem( "West-Facing Profile" );
		
		mouseSensitivitySlider = new JSlider( );
		mouseSensitivitySlider.setValue( 20 );
		mouseSensitivitySlider.setOpaque( false );
		mouseSensitivitySlider.addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				float sensitivity = mouseSensitivitySlider.getValue( ) / 20f;
				navigator.setSensitivity( sensitivity );
				orbiter.setSensitivity( sensitivity );
			}
		} );
		
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				URL updateUrl = null;
				File updateDir = null;
				File lastUpdateFile = null;
				try
				{
					Properties props = UpdateProperties.getUpdateProperties( );
					updateUrl = new URL( props.getProperty( UpdateProperties.SOURCE ) );
					updateDir = new File( props.getProperty( UpdateProperties.UPDATE_DIR ) );
					lastUpdateFile = new File( props.getProperty( UpdateProperties.LAST_UPDATE_FILE ) );
				}
				catch( MalformedURLException e1 )
				{
					e1.printStackTrace( );
				}
				
				updateStatusPanel = new UpdateStatusPanel( new I18n( ) );
				updateStatusPanel.setOpaque( false );
				updateStatusPanel.setBorder( new EmptyBorder( 3 , 3 , 3 , 3 ) );
				updateStatusPanel.setStatus( UpdateStatus.UNCHECKED );
				updateStatusPanelController = new UpdateStatusPanelController( updateStatusPanel ,
						lastUpdateFile , updateUrl , new File( updateDir , "update.zip" ) );
				updateStatusPanelController.downloadUpdateIfAvailable( );
			}
		};
		
		settingsPanel = new PaintablePanel( );
		settingsPanel.addUnderpaintBorder( new GradientFillBorder(
				Side.TOP , ColorUtils.darkerColor( settingsPanel.getBackground( ) , 0.05 ) ,
				Side.BOTTOM , ColorUtils.darkerColor( Color.LIGHT_GRAY , 0.05 ) ) );
		settingsPanel.setBorder( new OverrideInsetsBorder(
				new InnerGradientBorder( new Insets( 0 , 5 , 0 , 0 ) , Color.GRAY ) ,
				new Insets( 3 , 8 , 3 , 3 ) ) );
		GridBagWizard w = GridBagWizard.create( settingsPanel );
		w.defaults( ).autoinsets( new DefaultAutoInsets( 3 , 3 ) );
		JLabel modeLabel = new JLabel( "View:" );
		w.put( updateViewButton ).xy( 0 , 0 ).fillx( 1.0 );
		w.put( modeLabel ).below( updateViewButton ).weightx( 1.0 ).west( );
		w.put( modeComboBox ).below( modeLabel ).fillx( ).north( );
		JLabel sensLabel = new JLabel( "Mouse Sensitivity:" );
		w.put( sensLabel ).below( modeComboBox ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( mouseSensitivitySlider ).below( sensLabel ).fillx( ).weighty( 1.0 ).north( );
		
		w.put( updateStatusPanel ).belowAll( ).fillx( ).south( );
		
		settingsButton = new JButton( "\u2261" );
		settingsButton.setOpaque( false );
		settingsButton.setMargin( new Insets( 10 , 5 , 10 , 5 ) );
		
		layeredPane = new JLayeredPane( );
		layeredPane.setLayout( new DelegatingLayoutManager( ) );
		layeredPane.setLayer( settingsPanel , JLayeredPane.DEFAULT_LAYER + 1 );
		layeredPane.setLayer( settingsButton , JLayeredPane.DEFAULT_LAYER + 2 );
		layeredPane.setLayer( surveyTableDrawer , JLayeredPane.DEFAULT_LAYER + 3 );
		layeredPane.setLayer( openSurveyTableDrawerButton , JLayeredPane.DEFAULT_LAYER + 4 );
		final DrawerLayoutDelegate settingsDrawerDelegate = new DrawerLayoutDelegate( settingsPanel , Side.RIGHT );
		settingsDrawerDelegate.close( false );
		layeredPane.add( settingsPanel , settingsDrawerDelegate );
		TabLayoutDelegate tabDelegate = new TabLayoutDelegate( settingsPanel , Corner.TOP_LEFT , Side.LEFT );
		tabDelegate.setInsets( new Insets( 10 , 5 , -10 , -5 ) );
		layeredPane.add( settingsButton , tabDelegate );
		layeredPane.add( plotPanel );
		layeredPane.add( surveyTableDrawer , surveyTableDrawerDelegate );
		TabLayoutDelegate openSurveyDrawerButtonDelegate = new TabLayoutDelegate( surveyTableDrawer , Corner.TOP_LEFT , Side.TOP );
		openSurveyDrawerButtonDelegate.setInsets( new Insets( 5 , 10 , -5 , -10 ) );
		layeredPane.add( openSurveyTableDrawerButton , openSurveyDrawerButtonDelegate );
		
		mainPanel = new JPanel( new BorderLayout( ) );
		// mainPanel.add( modeComboBox , BorderLayout.NORTH );
		mainPanel.add( layeredPane , BorderLayout.CENTER );
		
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
		
		settingsButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				settingsDrawerDelegate.toggleOpen( );
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
		
		scene.getViewXform( v );
		Vecmath.setRow4( v , 0 , 1 , 0 , 0 , 0 );
		Vecmath.setRow4( v , 1 , 0 , 0 , -1 , 0 );
		Vecmath.setRow4( v , 2 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( v , 3 , 0 , 0 , 0 , 1 );
		scene.setViewXform( v );
	}
	
	public void northFacingProfileMode( )
	{
		orthoMode( );
		
		scene.getViewXform( v );
		Vecmath.setRow4( v , 0 , 1 , 0 , 0 , 0 );
		Vecmath.setRow4( v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( v , 2 , 0 , 0 , 1 , 0 );
		Vecmath.setRow4( v , 3 , 0 , 0 , 0 , 1 );
		scene.setViewXform( v );
	}
	
	public void southFacingProfileMode( )
	{
		orthoMode( );
		
		scene.getViewXform( v );
		Vecmath.setRow4( v , 0 , -1 , 0 , 0 , 0 );
		Vecmath.setRow4( v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( v , 2 , 0 , 0 , -1 , 0 );
		Vecmath.setRow4( v , 3 , 0 , 0 , 0 , 1 );
		scene.setViewXform( v );
	}
	
	public void eastFacingProfileMode( )
	{
		orthoMode( );
		
		scene.getViewXform( v );
		Vecmath.setRow4( v , 0 , 0 , 0 , 1 , 0 );
		Vecmath.setRow4( v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( v , 2 , -1 , 0 , 0 , 0 );
		Vecmath.setRow4( v , 3 , 0 , 0 , 0 , 1 );
		scene.setViewXform( v );
	}
	
	public void westFacingProfileMode( )
	{
		orthoMode( );
		
		scene.getViewXform( v );
		Vecmath.setRow4( v , 0 , 0 , 0 , -1 , 0 );
		Vecmath.setRow4( v , 1 , 0 , 1 , 0 , 0 );
		Vecmath.setRow4( v , 2 , 1 , 0 , 0 , 0 );
		Vecmath.setRow4( v , 3 , 0 , 0 , 0 , 1 );
		scene.setViewXform( v );
	}
	
	private void orthoMode( )
	{
		xaxis.setVisible( true );
		yaxis.setVisible( true );
		
		mouseLooper.removeMouseAdapter( mouseAdapterChain );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( plotController );
		mouseLooper.addMouseAdapter( mouseAdapterChain );
		
		scene.setOrthoMode( true );
	}
	
	public void perspectiveMode( )
	{
		xaxis.setVisible( false );
		yaxis.setVisible( false );
		
		mouseLooper.removeMouseAdapter( mouseAdapterChain );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( navigator );
		mouseAdapterChain.addMouseAdapter( orbiter );
		mouseLooper.addMouseAdapter( mouseAdapterChain );
		mouseAdapterChain.addMouseAdapter( new MouseAdapter( )
		{
			@Override
			public void mouseMoved( MouseEvent e )
			{
				float[ ] origin = new float[ 3 ];
				float[ ] direction = new float[ 3 ];
				scene.pickXform( ).getOrigin( origin );
				scene.pickXform( ).xform( e.getX( ) , e.getY( ) , e.getComponent( ).getWidth( ) , e.getComponent( ).getHeight( ) , direction , 0 );
				
				System.out.println( Arrays.toString( origin ) + ", " + Arrays.toString( direction ) );
				
				for( BasicJOGLObject obj : debugMbrs )
				{
					scene.destroyLater( obj );
					scene.remove( obj );
				}
				
				debugMbrs = new ArrayList<BasicJOGLObject>( );
				
				// if( rtree != null )
				// {
				// List<PickResult<Integer>> pickResults = new ArrayList<PickResult<Integer>>( );
				//
				// pickNodes( rtree.getRoot( ) , origin , direction , debugMbrs , pickResults );
				//
				// for( BasicJOGLObject obj : debugMbrs )
				// {
				// scene.add( obj );
				// scene.initLater( obj );
				// }
				//
				// canvas.display( );
				// }
			}
		} );
		
		scene.setOrthoMode( false );
	}
	
	private static class PickResult<T>
	{
		final float[ ]	location	= new float[ 3 ];
		float			distance;
		T				picked;
	}
	
	// private boolean pickNodes( RNode<float[ ], Integer> node , float[ ] rayOrigin , float[ ] rayDirection , List<BasicJOGLObject> renderedMbrs ,
	// List<PickResult<Integer>> pickResults )
	// {
	// boolean render = false;
	//
	// if( rayIntersects( rayOrigin , rayDirection , node.mbr( ) ) )
	// {
	// PickResult<Integer> result = null;
	//
	// if( node instanceof RBranch )
	// {
	// RBranch<float[ ], Integer> branch = ( RBranch<float[ ], Integer> ) node;
	// for( int i = 0 ; i < branch.numChildren( ) ; i++ )
	// {
	// render |= pickNodes( branch.childAt( i ) , rayOrigin , rayDirection , renderedMbrs , pickResults );
	// }
	// }
	// else if( node instanceof RLeaf )
	// {
	// int shotIndex = ( ( RLeaf<float[ ], Integer> ) node ).object( );
	// ByteBuffer indexBuffer = fillObj.indexBuffer( ).buffer( );
	// ByteBuffer vertBuffer = fillObj.vertexBuffer( 0 );
	// indexBuffer.position( shotIndex * 24 * 4 );
	// for( int i = 0 ; i < 8 ; i++ )
	// {
	// int i0 = indexBuffer.getInt( );
	// int i1 = indexBuffer.getInt( );
	// int i2 = indexBuffer.getInt( );
	//
	// vertBuffer.position( i0 * 24 );
	// p0[ 0 ] = vertBuffer.getFloat( );
	// p0[ 1 ] = vertBuffer.getFloat( );
	// p0[ 2 ] = vertBuffer.getFloat( );
	//
	// vertBuffer.position( i1 * 24 );
	// p1[ 0 ] = vertBuffer.getFloat( );
	// p1[ 1 ] = vertBuffer.getFloat( );
	// p1[ 2 ] = vertBuffer.getFloat( );
	//
	// vertBuffer.position( i2 * 24 );
	// p2[ 0 ] = vertBuffer.getFloat( );
	// p2[ 1 ] = vertBuffer.getFloat( );
	// p2[ 2 ] = vertBuffer.getFloat( );
	//
	// try
	// {
	// lpx.lineFromRay( rayOrigin , rayDirection );
	// lpx.planeFromPoints( p0 , p1 , p2 );
	// lpx.findIntersection( );
	// if( lpx.isPointIntersection( ) && lpx.isOnRay( ) && lpx.isInTriangle( ) )
	// {
	// if( result == null || lpx.t < result.distance )
	// {
	// result = new PickResult<Integer>( );
	// result.picked = shotIndex;
	// result.distance = lpx.t;
	// setf( result.location , lpx.result );
	// }
	// }
	// }
	// catch( Exception ex )
	// {
	//
	// }
	// }
	//
	// if( result != null )
	// {
	// render = true;
	//
	// pickResults.add( result );
	// }
	//
	// vertBuffer.position( 0 );
	// indexBuffer.position( 0 );
	// }
	// }
	//
	// if( render )
	// {
	// renderedMbrs.add( renderMbr( node.mbr( ) , 1 , 1 , 0 ) );
	// if( node instanceof RfBranch )
	// {
	// RfNode<Integer>[ ] children = ( ( RfBranch<Integer> ) node ).children( );
	// if( children.length > 0 && children[ 0 ] instanceof RfLeaf )
	// {
	// for( RfNode<Integer> child : children )
	// {
	// renderedMbrs.add( renderMbr( child.mbr( ) , 0 , 0 , 1 ) );
	// }
	// }
	// }
	// }
	//
	// return render;
	// }
	
	private static BasicJOGLObject renderMbr( float[ ] mbr , float r , float g , float b )
	{
		BufferHelper vh = new BufferHelper( );
		BufferHelper ih = new BufferHelper( );
		
		vh.putAsFloats( mbr[ 0 ] , mbr[ 1 ] , mbr[ 2 ] );
		vh.putAsFloats( mbr[ 0 ] , mbr[ 1 ] , mbr[ 5 ] );
		vh.putAsFloats( mbr[ 0 ] , mbr[ 4 ] , mbr[ 2 ] );
		vh.putAsFloats( mbr[ 0 ] , mbr[ 4 ] , mbr[ 5 ] );
		vh.putAsFloats( mbr[ 3 ] , mbr[ 1 ] , mbr[ 2 ] );
		vh.putAsFloats( mbr[ 3 ] , mbr[ 1 ] , mbr[ 5 ] );
		vh.putAsFloats( mbr[ 3 ] , mbr[ 4 ] , mbr[ 2 ] );
		vh.putAsFloats( mbr[ 3 ] , mbr[ 4 ] , mbr[ 5 ] );
		
		ih.putInts( 0 , 1 );
		ih.putInts( 1 , 3 );
		ih.putInts( 2 , 0 );
		ih.putInts( 3 , 2 );
		ih.putInts( 4 , 5 );
		ih.putInts( 5 , 7 );
		ih.putInts( 6 , 4 );
		ih.putInts( 7 , 6 );
		ih.putInts( 0 , 4 );
		ih.putInts( 1 , 5 );
		ih.putInts( 2 , 6 );
		ih.putInts( 3 , 7 );
		
		BasicJOGLObject obj = new BasicJOGLObject( );
		ByteBuffer vb = vh.toByteBuffer( );
		
		obj.addVertexBuffer( vb ).vertexCount( 8 );
		obj.indexBuffer( ih.toByteBuffer( ) );
		obj.drawMode( GL.GL_LINES );
		obj.indexType( GL.GL_UNSIGNED_INT );
		obj.indexCount( ih.count( ) );
		obj.add( obj.new Attribute3fv( ).name( "a_pos" ) );
		obj.vertexShaderCode( new BasicVertexShader( ).toString( ) );
		obj.fragmentShaderCode( new FlatFragmentShader( ).color( r , g , b , 1 ).toString( ) );
		
		return obj;
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
		if( model != null )
		{
			scene.remove( model.getRootGroup( ) );
			scene.destroyLater( model.getRootGroup( ) );
		}
		
		model = Survey3dModel.create( shots , 10 , 3 , 3 , 3 );
		
		scene.add( model.getRootGroup( ) );
		scene.initLater( model.getRootGroup( ) );
		
		float[ ] center = new float[ 3 ];
		Rectmath.center( model.getTree( ).getRoot( ).mbr( ) , center );
		orbiter.setCenter( center );
		
		canvas.repaint( );
	}
}

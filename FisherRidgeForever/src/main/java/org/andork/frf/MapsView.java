package org.andork.frf;

import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.spatial.Rectmath.nmax;
import static org.andork.spatial.Rectmath.nmin;
import static org.andork.spatial.Rectmath.rayIntersects;
import static org.andork.spatial.Rectmath.voidRectf;

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
import javax.media.opengl.GL2ES2;
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
import org.andork.frf.model.SurveyShot;
import org.andork.frf.update.UpdateProperties;
import org.andork.frf.update.UpdateStatus;
import org.andork.frf.update.UpdateStatusPanel;
import org.andork.frf.update.UpdateStatusPanelController;
import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.DistanceFragmentShader;
import org.andork.jogl.basic.BasicJOGLObject.FlatFragmentShader;
import org.andork.jogl.basic.BasicJOGLObject.Uniform1fv;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.jogl.basic.JOGLLineWidthModifier;
import org.andork.jogl.basic.JOGLObject;
import org.andork.jogl.basic.JOGLPolygonModeModifier;
import org.andork.jogl.basic.SharedBuffer;
import org.andork.jogl.basic.awt.BasicJOGLSetup;
import org.andork.jogl.shader.DefaultNormalVertexShader;
import org.andork.jogl.shader.DefaultPositionVertexShader;
import org.andork.jogl.shader.GradientFragmentShader;
import org.andork.jogl.shader.MainCodeBlock;
import org.andork.jogl.shader.ShaderSegment;
import org.andork.jogl.shader.SimpleLightingFragmentShader;
import org.andork.jogl.shader.VariableDeclarations;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.spatial.RBranch;
import org.andork.spatial.RLeaf;
import org.andork.spatial.RNode;
import org.andork.spatial.RfBranch;
import org.andork.spatial.RfLeaf;
import org.andork.spatial.RfNode;
import org.andork.spatial.RfStarTree;

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
	
	BasicJOGLObject					fillObj;
	Uniform1fv						fillNearDist;
	Uniform1fv						fillFarDist;
	BasicJOGLObject					lineObj;
	Uniform1fv						lineNearDist;
	Uniform1fv						lineFarDist;
	
	JLayeredPane					surveyTableDrawer;
	
	SurveyTable						surveyTable;
	JScrollPane						surveyTableScrollPane;
	
	JPanel							statusBar;
	UpdateStatusPanel				updateStatusPanel;
	UpdateStatusPanelController		updateStatusPanelController;
	
	JButton							updateViewButton;
	
	List<SurveyShot>				surveyShots;
	RfStarTree<Integer>				rtree;
	
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
				if( fillNearDist != null )
				{
					fillNearDist.value( ( float ) distColorationAxis.getAxisConversion( ).invert( 0 ) );
				}
				if( fillFarDist != null )
				{
					fillFarDist.value( ( float ) distColorationAxis.getAxisConversion( ).invert( distColorationAxis.getHeight( ) ) );
				}
				if( lineNearDist != null )
				{
					lineNearDist.value( ( float ) distColorationAxis.getAxisConversion( ).invert( 0 ) );
				}
				if( lineFarDist != null )
				{
					lineFarDist.value( ( float ) distColorationAxis.getAxisConversion( ).invert( distColorationAxis.getHeight( ) ) );
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
				
				if( rtree != null )
				{
					List<PickResult<Integer>> pickResults = new ArrayList<PickResult<Integer>>( );
					
					pickNodes( rtree.getRoot( ) , origin , direction , debugMbrs , pickResults );
					
					for( BasicJOGLObject obj : debugMbrs )
					{
						scene.add( obj );
						scene.initLater( obj );
					}
					
					canvas.display( );
				}
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
	
	private boolean pickNodes( RNode<float[ ], Integer> node , float[ ] rayOrigin , float[ ] rayDirection , List<BasicJOGLObject> renderedMbrs , List<PickResult<Integer>> pickResults )
	{
		boolean render = false;
		
		if( rayIntersects( rayOrigin , rayDirection , node.mbr( ) ) )
		{
			PickResult<Integer> result = null;
			
			if( node instanceof RBranch )
			{
				RBranch<float[ ], Integer> branch = ( RBranch<float[ ], Integer> ) node;
				for( int i = 0 ; i < branch.numChildren( ) ; i++ )
				{
					render |= pickNodes( branch.childAt( i ) , rayOrigin , rayDirection , renderedMbrs , pickResults );
				}
			}
			else if( node instanceof RLeaf )
			{
				int shotIndex = ( ( RLeaf<float[ ], Integer> ) node ).object( );
				ByteBuffer indexBuffer = fillObj.indexBuffer( ).buffer( );
				ByteBuffer vertBuffer = fillObj.vertexBuffer( 0 );
				indexBuffer.position( shotIndex * 24 * 4 );
				for( int i = 0 ; i < 8 ; i++ )
				{
					int i0 = indexBuffer.getInt( );
					int i1 = indexBuffer.getInt( );
					int i2 = indexBuffer.getInt( );
					
					vertBuffer.position( i0 * 24 );
					p0[ 0 ] = vertBuffer.getFloat( );
					p0[ 1 ] = vertBuffer.getFloat( );
					p0[ 2 ] = vertBuffer.getFloat( );
					
					vertBuffer.position( i1 * 24 );
					p1[ 0 ] = vertBuffer.getFloat( );
					p1[ 1 ] = vertBuffer.getFloat( );
					p1[ 2 ] = vertBuffer.getFloat( );
					
					vertBuffer.position( i2 * 24 );
					p2[ 0 ] = vertBuffer.getFloat( );
					p2[ 1 ] = vertBuffer.getFloat( );
					p2[ 2 ] = vertBuffer.getFloat( );
					
					try
					{
						lpx.lineFromRay( rayOrigin , rayDirection );
						lpx.planeFromPoints( p0 , p1 , p2 );
						lpx.findIntersection( );
						if( lpx.isPointIntersection( ) && lpx.isOnRay( ) && lpx.isInTriangle( ) )
						{
							if( result == null || lpx.t < result.distance )
							{
								result = new PickResult<Integer>( );
								result.picked = shotIndex;
								result.distance = lpx.t;
								setf( result.location , lpx.result );
							}
						}
					}
					catch( Exception ex )
					{
						
					}
				}
				
				if( result != null )
				{
					render = true;
					
					SurveyShot shot = surveyShots.get( result.picked );
					
					System.out.println( shot.from.name + " - " + shot.to.name );
					
					pickResults.add( result );
				}
				
				vertBuffer.position( 0 );
				indexBuffer.position( 0 );
			}
		}
		
		if( render )
		{
			renderedMbrs.add( renderMbr( node.mbr( ) , 1 , 1 , 0 ) );
			if( node instanceof RfBranch )
			{
				RfNode<Integer>[ ] children = ( ( RfBranch<Integer> ) node ).children( );
				if( children.length > 0 && children[ 0 ] instanceof RfLeaf )
				{
					for( RfNode<Integer> child : children )
					{
						renderedMbrs.add( renderMbr( child.mbr( ) , 0 , 0 , 1 ) );
					}
				}
			}
		}
		
		return render;
	}
	
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
		BufferHelper vertHelper = new BufferHelper( );
		BufferHelper fillIndexHelper = new BufferHelper( );
		BufferHelper lineIndexHelper = new BufferHelper( );
		
		int vertCount = 0;
		int fillIndexCount = 0;
		int lineIndexCount = 0;
		
		surveyShots = shots;
		
		int shotIndex = 0;
		
		rtree = new RfStarTree<Integer>( 3 , 10 , 3 , 3 );
		
		for( SurveyShot shot : shots )
		{
			fromLoc[ 0 ] = shot.from.position[ 0 ];
			fromLoc[ 2 ] = shot.from.position[ 1 ];
			
			toLoc[ 0 ] = shot.to.position[ 0 ];
			toLoc[ 1 ] = shot.to.position[ 1 ];
			
			if( Vecmath.distance3( shot.from.position , shot.to.position ) > 200 )
			{
				System.out.println( shot.from.name + ": " + Arrays.toString( shot.from.position ) + " - " + shot.to.name + ": " + Arrays.toString( shot.to.position ) );
			}
			
			leftAtFrom[ 0 ] = shot.from.position[ 2 ] - shot.to.position[ 2 ];
			leftAtFrom[ 1 ] = 0;
			leftAtFrom[ 2 ] = shot.to.position[ 0 ] - shot.from.position[ 0 ];
			
			if( leftAtFrom[ 0 ] != 0 || leftAtFrom[ 2 ] != 0 )
			{
				Vecmath.normalize3( leftAtFrom );
			}
			
			vertHelper.putAsFloats( shot.from.position );
			vertHelper.putAsFloats( 0 , 0 , 0 );
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putAsFloats( shot.from.position[ i ] + leftAtFrom[ i ] * shot.left );
			}
			vertHelper.putAsFloats( leftAtFrom );
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putAsFloats( shot.from.position[ i ] - leftAtFrom[ i ] * shot.right );
			}
			vertHelper.putAsFloats( -leftAtFrom[ 0 ] , -leftAtFrom[ 1 ] , -leftAtFrom[ 2 ] );
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putAsFloats( shot.from.position[ i ] + ( i == 1 ? shot.up : 0.0 ) );
			}
			vertHelper.putAsFloats( 0 , 1 , 0 );
			for( int i = 0 ; i < 3 ; i++ )
			{
				vertHelper.putAsFloats( shot.from.position[ i ] - ( i == 1 ? shot.down : 0.0 ) );
			}
			vertHelper.putAsFloats( 0 , -1 , 0 );
			
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
				
				vertHelper.putAsFloats( shot.to.position );
				vertHelper.putAsFloats( 0 , 0 , 0 );
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putAsFloats( shot.to.position[ i ] + leftAtTo[ i ] * bestShot.left );
				}
				vertHelper.putAsFloats( leftAtTo );
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putAsFloats( shot.to.position[ i ] - leftAtTo[ i ] * bestShot.right );
				}
				vertHelper.putAsFloats( -leftAtTo[ 0 ] , -leftAtTo[ 1 ] , -leftAtTo[ 2 ] );
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putAsFloats( shot.to.position[ i ] + ( i == 1 ? bestShot.up : 0.0 ) );
				}
				vertHelper.putAsFloats( 0 , 1 , 0 );
				for( int i = 0 ; i < 3 ; i++ )
				{
					vertHelper.putAsFloats( shot.to.position[ i ] - ( i == 1 ? bestShot.down : 0.0 ) );
				}
				vertHelper.putAsFloats( 0 , -1 , 0 );
			}
			else
			{
				vertHelper.putAsFloats( shot.to.position );
				vertHelper.putAsFloats( 0 , 0 , 0 );
				vertHelper.putAsFloats( shot.to.position );
				vertHelper.putAsFloats( leftAtFrom );
				vertHelper.putAsFloats( shot.to.position );
				vertHelper.putAsFloats( -leftAtFrom[ 0 ] , -leftAtFrom[ 1 ] , -leftAtFrom[ 2 ] );
				vertHelper.putAsFloats( shot.to.position );
				vertHelper.putAsFloats( 0 , 1 , 0 );
				vertHelper.putAsFloats( shot.to.position );
				vertHelper.putAsFloats( 0 , -1 , 0 );
			}
			
			fillIndexHelper.put( offset( vertCount ,
					1 , 6 , 3 , 8 , 3 , 6 ,
					3 , 8 , 2 , 7 , 2 , 8 ,
					2 , 7 , 4 , 9 , 4 , 7 ,
					4 , 9 , 1 , 6 , 1 , 9 ) );
			
			lineIndexHelper.put( offset( vertCount ,
					1 , 6 , 1 , 3 , 6 , 3 , 6 , 8 ,
					3 , 8 , 3 , 2 , 8 , 2 , 8 , 7 ,
					2 , 7 , 2 , 4 , 7 , 4 , 7 , 9 ,
					4 , 9 , 4 , 1 , 9 , 1 , 9 , 6 ) );
			
			vertCount += 10;
			fillIndexCount += 24;
			lineIndexCount += 32;
			
			float[ ] mbr = voidRectf( 3 );
			
			for( int i = 1 ; i <= 10 ; i++ )
			{
				int offset = -i * 6 + 1;
				float x = ( Float ) vertHelper.getBackward( offset );
				float y = ( Float ) vertHelper.getBackward( offset + 1 );
				float z = ( Float ) vertHelper.getBackward( offset + 2 );
				
				mbr[ 0 ] = nmin( mbr[ 0 ] , x );
				mbr[ 1 ] = nmin( mbr[ 1 ] , y );
				mbr[ 2 ] = nmin( mbr[ 2 ] , z );
				mbr[ 3 ] = nmax( mbr[ 3 ] , x );
				mbr[ 4 ] = nmax( mbr[ 4 ] , y );
				mbr[ 5 ] = nmax( mbr[ 5 ] , z );
			}
			
			RfStarTree.Leaf<Integer> leaf = rtree.createLeaf( mbr , shotIndex++ );
			rtree.insert( leaf );
		}
		
		ByteBuffer vertBuffer = vertHelper.toByteBuffer( );
		float[ ] bounds = getBounds( vertBuffer , 0 , 24 , vertCount , 3 );
		
		float[ ] c = new float[ 3 ];
		c[ 0 ] = ( bounds[ 0 ] + bounds[ 3 ] ) * .5f;
		c[ 1 ] = ( bounds[ 1 ] + bounds[ 4 ] ) * .5f;
		c[ 2 ] = ( bounds[ 2 ] + bounds[ 5 ] ) * .5f;
		
		orbiter.setCenter( c );
		
		float dx = bounds[ 3 ] - c[ 0 ];
		float dy = bounds[ 4 ] - c[ 1 ];
		float dz = bounds[ 5 ] - c[ 2 ];
		float radius = ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
		
		for( JOGLObject object : scene.getObjects( ) )
		{
			scene.destroyLater( object );
		}
		scene.clear( );
		
		if( vertCount > 0 )
		{
			SharedBuffer sVertBuffer = new SharedBuffer( ).buffer( vertBuffer );
			SharedBuffer sIndexBuffer = new SharedBuffer( ).elementArray( ).buffer( fillIndexHelper.toByteBuffer( ) );
			
			fillObj = new BasicJOGLObject( );
			fillObj.addVertexBuffer( sVertBuffer ).vertexCount( vertCount );
			fillObj.drawMode( GL2ES2.GL_TRIANGLES );
			fillObj.indexBuffer( sIndexBuffer ).indexCount( fillIndexCount ).indexType( GL2ES2.GL_UNSIGNED_INT );
			fillObj.transpose( false );
			fillObj.add( fillObj.new Attribute3fv( ).name( "a_pos" ) );
			fillObj.add( fillObj.new Attribute3fv( ).name( "a_norm" ) );
			fillObj.add( new JOGLDepthModifier( ) );
//			fillObj.add( new JOGLPolygonModeModifier( GL.GL_BACK ) );
			// fillObj.add( new JOGLDepthRangeModifier( 0.0f , 0.9f ) );
			fillObj.add( fillObj.new Uniform4fv( ).name( "nearColor" ).value( 1 , 0 , 0 , 1 ) );
			fillObj.add( fillObj.new Uniform4fv( ).name( "farColor" ).value( 0 , 0 , 1 , 1 ) );
			fillObj.add( fillNearDist = fillObj.new Uniform1fv( ).name( "nearDist" ).value( 0 ) );
			fillObj.add( fillFarDist = fillObj.new Uniform1fv( ).name( "farDist" ).value( 1000 ) );
			fillObj.normalMatrixName( "n" );
			// fillObj.vertexShaderCode( new BasicVertexShader( ).passPosToFragmentShader( true ).toString( ) );
			// fillObj.fragmentShaderCode( new DistanceFragmentShader( ).toString( ) );
			DefaultPositionVertexShader fillObjVertShader = new DefaultPositionVertexShader( );
			DefaultNormalVertexShader fillObjNormShader = new DefaultNormalVertexShader( );
			GradientFragmentShader fillObjFragShader = new GradientFragmentShader( );
			fillObjFragShader.in( "v_z" ).loValue( "nearDist" ).hiValue( "farDist" ).loColor( "nearColor" ).hiColor( "farColor" );
			SimpleLightingFragmentShader lightingFragShader = new SimpleLightingFragmentShader( );
			lightingFragShader.color( "gl_FragColor" ).ambientAmt( "0.3" );
			
			fillObj.vertexShaderCode( "precision highp float;" + ShaderSegment.combine(
					fillObjVertShader.defaultVariableDecls( ) ,
					fillObjNormShader.defaultVariableDecls( ) ,
					fillObjVertShader ,
					fillObjNormShader ,
					new VariableDeclarations( "varying float v_z;" ) ,
					new MainCodeBlock( "  v_z = -(v * m * vec4(a_pos, 1.0)).z;" )
					) );
			fillObj.fragmentShaderCode( ShaderSegment.combine(
					new VariableDeclarations( "varying float v_z;" ) ,
					fillObjFragShader.defaultVariableDecls( ) ,
					new VariableDeclarations( "varying vec3 v_norm;" ) ,
					fillObjFragShader ,
					lightingFragShader ) );
			
			scene.initLater( fillObj );
			scene.add( fillObj );
			
			lineObj = new BasicJOGLObject( );
			lineObj.addVertexBuffer( sVertBuffer ).vertexCount( vertCount );
			lineObj.drawMode( GL2ES2.GL_LINES );
			lineObj.indexBuffer( lineIndexHelper.toByteBuffer( ) ).indexCount( lineIndexCount ).indexType( GL2ES2.GL_UNSIGNED_INT );
			lineObj.transpose( false );
			lineObj.vertexShaderCode( new BasicVertexShader( ).passPosToFragmentShader( true ).toString( ) );
			lineObj.fragmentShaderCode( new DistanceFragmentShader( ).toString( ) );
			lineObj.add( lineObj.new Attribute3fv( ).name( "a_pos" ) );
			lineObj.add( lineObj.new PlaceholderAttribute( 12 ) );
			// fillObj.add( new JOGLDepthRangeModifier( 0.1f , 1f ) );
			lineObj.add( new JOGLLineWidthModifier( 1.5f ) );
			lineObj.add( new JOGLDepthModifier( ) );
			lineObj.add( lineObj.new Uniform4fv( ).name( "nearColor" ).value( 0.7f , 0f , 0f , 1f ) );
			lineObj.add( lineObj.new Uniform4fv( ).name( "farColor" ).value( 0.0f , 0f , 0.7f , 1 ) );
			lineObj.add( lineNearDist = lineObj.new Uniform1fv( ).name( "nearDist" ).value( 0 ) );
			lineObj.add( lineFarDist = lineObj.new Uniform1fv( ).name( "farDist" ).value( 1000 ) );
			
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

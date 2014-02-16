package org.andork.frf;

import static org.andork.math3d.Vecmath.add3;
import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.scale3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicButtonUI;

import org.andork.awt.ColorUtils;
import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.I18n;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.DrawerAutoshowController;
import org.andork.awt.layout.DrawerLayoutDelegate;
import org.andork.awt.layout.ResizeKnobHandler;
import org.andork.awt.layout.Side;
import org.andork.awt.layout.TabLayoutDelegate;
import org.andork.frf.model.Survey3dModel;
import org.andork.frf.model.Survey3dModel.SelectionEditor;
import org.andork.frf.model.Survey3dModel.Shot;
import org.andork.frf.model.Survey3dModel.ShotPickContext;
import org.andork.frf.model.Survey3dModel.ShotPickResult;
import org.andork.frf.model.SurveyShot;
import org.andork.frf.update.UpdateProperties;
import org.andork.frf.update.UpdateStatus;
import org.andork.frf.update.UpdateStatusPanel;
import org.andork.frf.update.UpdateStatusPanelController;
import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.awt.BasicJOGLSetup;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.spatial.Rectmath;
import org.andork.swing.DoSwing;
import org.andork.swing.PaintablePanel;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.border.GradientFillBorder;
import org.andork.swing.border.InnerGradientBorder;
import org.andork.swing.border.LayeredBorder;
import org.andork.swing.border.MultipleGradientFillBorder;
import org.andork.swing.border.OverrideInsetsBorder;
import org.andork.swing.table.old.FilteringTableController;
import org.andork.swing.table.old.HighlightingTableScroller;
import org.andork.swing.table.old.FilteringTableModel.AndFilter;
import org.andork.swing.table.old.FilteringTableModel.Filter;
import org.andork.swing.table.old.FilteringTableModel.HighlightingFilter;
import org.andork.swing.table.old.FilteringTableModel.PatternFilter;

import com.andork.plot.AxisLinkButton;
import com.andork.plot.LinearAxisConversion;
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
	PaintablePanel					distColorationAxisPanel;
	PlotAxis						paramColorationAxis;
	PaintablePanel					paramColorationAxisPanel;
	PlotAxis						highlightDistAxis;
	PaintablePanel					highlightDistAxisPanel;
	
	PaintablePanel					settingsPanel;
	DrawerLayoutDelegate			settingsDrawerDelegate;
	JToggleButton					pinSettingsPanelButton;
	JSlider							mouseSensitivitySlider;
	
	Plot							plot;
	JPanel							plotPanel;
	JPanel							mainPanel;
	JLayeredPane					layeredPane;
	
	PlotController					plotController;
	MouseLooper						mouseLooper;
	MousePickHandler				pickHandler;
	MouseAdapterChain				mouseAdapterChain;
	
	DrawerAutoshowController		autoshowController;
	
	TableSelectionHandler			selectionHandler;
	
	JComboBox						modeComboBox;
	
	JPanel							surveyTableDrawer;
	DrawerLayoutDelegate			surveyTableDrawerDelegate;
	JButton							surveyTableResizeHandle;
	TextComponentWithHintAndClear	filterField;
	
	SurveyTable						surveyTable;
	HighlightingTableScroller		surveyTableScrollPane;
	
	FilteringTableController		surveyTableFilteringController;
	
	JPanel							statusBar;
	UpdateStatusPanel				updateStatusPanel;
	UpdateStatusPanelController		updateStatusPanelController;
	
	JButton							updateViewButton;
	
	Survey3dModel					model;
	
	float[ ]						v				= newMat4f( );
	
	int								debugMbrCount	= 0;
	List<BasicJOGLObject>			debugMbrs		= new ArrayList<BasicJOGLObject>( );
	
	ShotPickContext					spc				= new ShotPickContext( );
	
	final LinePlaneIntersection3f	lpx				= new LinePlaneIntersection3f( );
	final float[ ]					p0				= new float[ 3 ];
	final float[ ]					p1				= new float[ 3 ];
	final float[ ]					p2				= new float[ 3 ];
	private JToggleButton			pinSurveyTableButton;
	
	private JButton					debugButton		= new JButton( );
	
	public MapsView( )
	{
		super( );
		
		filterField = new TextComponentWithHintAndClear( "Enter filter pattern" );
		
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				surveyTable = new SurveyTable( );
			}
		};
		surveyTableScrollPane = new HighlightingTableScroller( surveyTable );
		
		surveyTableFilteringController = new FilteringTableController( filterField.textComponent , surveyTable )
		{
			@Override
			protected Filter createFilter( Pattern p )
			{
				// PatternFilter patternFilter = new PatternFilter( p );
				AndFilter combFilter = new AndFilter( new PatternFilter( p , 0 ) , new PatternFilter( p , 1 ) );
				surveyTable.setHighlightColors( Collections.<Filter,Color>singletonMap( combFilter , Color.YELLOW ) );
				return new HighlightingFilter( null , combFilter );
			}
		};
		
		surveyTableDrawer = new JPanel( );
		
		surveyTableResizeHandle = new JButton( "" );
		surveyTableResizeHandle.setUI( new BasicButtonUI( ) );
		surveyTableResizeHandle.setMargin( new Insets( 0 , 0 , 0 , 0 ) );
		surveyTableResizeHandle.setPreferredSize( new Dimension( 200 , 3 ) );
		surveyTableResizeHandle.setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
		
		ResizeKnobHandler surveyTableResizeHandler = new ResizeKnobHandler( surveyTableDrawer , Side.TOP )
		{
			protected void onResize( Component target )
			{
				super.onResize( target );
				Window w = SwingUtilities.getWindowAncestor( target );
				if( w != null )
				{
					w.invalidate( );
					w.validate( );
					w.repaint( );
				}
			}
		};
		surveyTableResizeHandle.addMouseListener( surveyTableResizeHandler );
		surveyTableResizeHandle.addMouseMotionListener( surveyTableResizeHandler );
		
		GridBagWizard gbw = GridBagWizard.create( surveyTableDrawer );
		
		JButton maximizeSurveyTableButton = new JButton( "Max" );
		
		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 2 , 2 ) );
		gbw.put( surveyTableResizeHandle ).xy( 0 , 0 ).fillx( 1.0 );
		gbw.put( filterField ).below( surveyTableResizeHandle ).fillx( 1.0 );
		gbw.put( maximizeSurveyTableButton ).rightOf( filterField ).east( );
		gbw.put( surveyTableScrollPane ).below( filterField , maximizeSurveyTableButton ).fillboth( 0.0 , 1.0 );
		
		surveyTableDrawerDelegate = new DrawerLayoutDelegate( surveyTableDrawer , Side.BOTTOM , true )
		{
			protected void onLayoutAnimated( Container parent , Component target )
			{
				Window w = SwingUtilities.getWindowAncestor( target );
				if( w != null )
				{
					w.invalidate( );
					w.validate( );
				}
			}
		};
		
		pinSurveyTableButton = new JToggleButton( "\u2261" );
		pinSurveyTableButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				surveyTableDrawerDelegate.setPinned( pinSurveyTableButton.isSelected( ) );
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
		LayeredBorder.addOnTop( new InnerGradientBorder( new Insets( 0 , 20 , 0 , 20 ) , new Color( 240 , 240 , 240 ) ) , xaxis );
		OverrideInsetsBorder.override( xaxis , new Insets( 0 , 0 , 0 , 0 ) );
		
		yaxis = new PlotAxis( Orientation.VERTICAL , LabelPosition.LEFT );
		yaxis.setBorder( new InnerGradientBorder( new Insets( 0 , 0 , 0 , 4 ) , Color.GRAY ) );
		LayeredBorder.addOnTop( new InnerGradientBorder( new Insets( 20 , 0 , 20 , 0 ) , new Color( 240 , 240 , 240 ) ) , yaxis );
		OverrideInsetsBorder.override( yaxis , new Insets( 0 , 0 , 0 , 0 ) );
		
		Color darkColor = new Color( 255 * 3 / 10 , 255 * 3 / 10 , 255 * 3 / 10 );
		
		distColorationAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		distColorationAxisPanel = PaintablePanel.wrap( distColorationAxis );
		distColorationAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { ColorUtils.alphaColor( darkColor , 0 ) , darkColor } ) );
		
		distColorationAxis.getAxisConversion( ).set( 0 , 0 , 10000 , 200 );
		distColorationAxis.setForeground( Color.WHITE );
		distColorationAxis.setMajorTickColor( Color.WHITE );
		distColorationAxis.setMinorTickColor( Color.WHITE );
		distColorationAxis.addPlot( plot );
		
		paramColorationAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		paramColorationAxisPanel = PaintablePanel.wrap( paramColorationAxis );
		paramColorationAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { Color.RED , Color.BLUE } ) );
		
		paramColorationAxis.setForeground( Color.WHITE );
		paramColorationAxis.setMajorTickColor( Color.WHITE );
		paramColorationAxis.setMinorTickColor( Color.WHITE );
		paramColorationAxis.addPlot( plot );
		
		highlightDistAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		highlightDistAxisPanel = PaintablePanel.wrap( highlightDistAxis );
		highlightDistAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { Color.YELLOW , ColorUtils.alphaColor( Color.YELLOW , 0 ) } ) );
		
		highlightDistAxis.setForeground( Color.BLACK );
		highlightDistAxis.setMajorTickColor( Color.BLACK );
		highlightDistAxis.setMinorTickColor( Color.BLACK );
		highlightDistAxis.addPlot( plot );
		
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
		
		new PlotAxisController( paramColorationAxis )
		{
			@Override
			protected void setAxisRange( double start , double end )
			{
				super.setAxisRange( start , end );
				if( model != null )
				{
					model.setLoParam( ( float ) paramColorationAxis.getAxisConversion( ).invert( 0 ) );
					model.setHiParam( ( float ) paramColorationAxis.getAxisConversion( ).invert( paramColorationAxis.getHeight( ) ) );
				}
				canvas.repaint( );
			}
		};
		
		new PlotAxisController( highlightDistAxis );
		
		plotController = new PlotController( plot , xAxisController , yAxisController );
		
		pickHandler = new MousePickHandler( );
		
		mouseLooper = new MouseLooper( );
		canvas.addMouseListener( mouseLooper );
		canvas.addMouseMotionListener( mouseLooper );
		canvas.addMouseWheelListener( mouseLooper );
		
		autoshowController = new DrawerAutoshowController( );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( plotController );
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
		
		plotPanel = new JPanel( new PlotPanelLayout( ) );
		plotPanel.add( plot );
		plotPanel.add( xaxis );
		plotPanel.add( yaxis );
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
		
		debugButton = new JButton( "Debug" );
		debugButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				System.out.println( "Debug" );
			}
		} );
		
		settingsPanel = new PaintablePanel( );
		settingsPanel.setUnderpaintBorder( GradientFillBorder.from( Side.TOP ).to( Side.BOTTOM ).colors(
				ColorUtils.darkerColor( settingsPanel.getBackground( ) , 0.05 ) ,
				ColorUtils.darkerColor( Color.LIGHT_GRAY , 0.05 ) ) );
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
		w.put( mouseSensitivitySlider ).below( sensLabel ).fillx( ).north( );
		JLabel distLabel = new JLabel( "Distance coloration:" );
		w.put( distLabel ).belowLast( ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( distColorationAxisPanel ).belowLast( ).fillx( );
		JLabel paramLabel = new JLabel( "Depth coloration:" );
		w.put( paramLabel ).belowLast( ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( paramColorationAxisPanel ).belowLast( ).fillx( );
		JLabel highlightLabel = new JLabel( "Highlight range:" );
		w.put( highlightLabel ).belowLast( ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( highlightDistAxisPanel ).belowLast( ).fillx( );
		
		w.put( debugButton ).belowLast( ).southwest( ).weighty( 1.0 );
		
		w.put( updateStatusPanel ).belowAll( ).fillx( ).weighty( 0.0 ).south( );
		
		pinSettingsPanelButton = new JToggleButton( "\u2261" );
		pinSettingsPanelButton.setOpaque( false );
		pinSettingsPanelButton.setMargin( new Insets( 10 , 5 , 10 , 5 ) );
		
		layeredPane = new JLayeredPane( );
		layeredPane.setLayout( new DelegatingLayoutManager( ) );
		layeredPane.setLayer( settingsPanel , JLayeredPane.DEFAULT_LAYER + 1 );
		layeredPane.setLayer( pinSettingsPanelButton , JLayeredPane.DEFAULT_LAYER + 2 );
		layeredPane.setLayer( surveyTableDrawer , JLayeredPane.DEFAULT_LAYER + 3 );
		layeredPane.setLayer( pinSurveyTableButton , JLayeredPane.DEFAULT_LAYER + 4 );
		
		settingsDrawerDelegate = new DrawerLayoutDelegate( settingsPanel , Side.RIGHT )
		{
			protected void onLayoutAnimated( Container parent , Component target )
			{
				Window w = SwingUtilities.getWindowAncestor( target );
				if( w != null )
				{
					w.invalidate( );
					w.validate( );
				}
			}
		};
		settingsDrawerDelegate.close( false );
		layeredPane.add( settingsPanel , settingsDrawerDelegate );
		TabLayoutDelegate tabDelegate = new TabLayoutDelegate( settingsPanel , Corner.TOP_LEFT , Side.LEFT );
		tabDelegate.insets( new Insets( 10 , 5 , -10 , -5 ) );
		layeredPane.add( pinSettingsPanelButton , tabDelegate );
		layeredPane.add( plotPanel );
		layeredPane.add( surveyTableDrawer , surveyTableDrawerDelegate );
		TabLayoutDelegate openSurveyDrawerButtonDelegate = new TabLayoutDelegate( surveyTableDrawer , Corner.TOP_LEFT , Side.TOP );
		openSurveyDrawerButtonDelegate.insets( new Insets( 5 , 10 , -5 , -10 ) );
		layeredPane.add( pinSurveyTableButton , openSurveyDrawerButtonDelegate );
		
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
		
		pinSettingsPanelButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				settingsDrawerDelegate.setPinned( pinSettingsPanelButton.isSelected( ) );
			}
		} );
		
		selectionHandler = new TableSelectionHandler( );
		surveyTable.getSelectionModel( ).addListSelectionListener( selectionHandler );
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
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
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
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
		mouseLooper.addMouseAdapter( mouseAdapterChain );
		
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
		if( model != null )
		{
			scene.remove( model.getRootGroup( ) );
			scene.destroyLater( model.getRootGroup( ) );
		}
		
		model = Survey3dModel.create( shots , 10 , 3 , 3 , 3 );
		
		model.setNearDist( ( float ) distColorationAxis.getAxisConversion( ).invert( 0 ) );
		model.setFarDist( ( float ) distColorationAxis.getAxisConversion( ).invert( distColorationAxis.getHeight( ) ) );
		model.setLoParam( ( float ) paramColorationAxis.getAxisConversion( ).invert( 0 ) );
		model.setHiParam( ( float ) paramColorationAxis.getAxisConversion( ).invert( paramColorationAxis.getHeight( ) ) );
		
		scene.add( model.getRootGroup( ) );
		scene.initLater( model.getRootGroup( ) );
		
		float[ ] center = new float[ 3 ];
		Rectmath.center( model.getTree( ).getRoot( ).mbr( ) , center );
		orbiter.setCenter( center );
		
		canvas.repaint( );
	}
	
	private void updateCenterOfOrbit( )
	{
		List<SurveyShot> origShots = model.getOriginalShots( );
		
		Set<Survey3dModel.Shot> newSelectedShots = model.getSelectedShots( );
		
		double[ ] center = new double[ 3 ];
		for( Survey3dModel.Shot shot : newSelectedShots )
		{
			SurveyShot origShot = origShots.get( shot.getIndex( ) );
			add3( center , origShot.from.position , center );
			add3( center , origShot.to.position , center );
		}
		
		scale3( center , 0.5 / newSelectedShots.size( ) );
		
		orbiter.setCenter( Vecmath.toFloats( center ) );
	}
	
	private class MousePickHandler extends MouseAdapter
	{
		private ShotPickResult pick( MouseEvent e )
		{
			float[ ] origin = new float[ 3 ];
			float[ ] direction = new float[ 3 ];
			scene.pickXform( ).xform( e.getX( ) , e.getY( ) , e.getComponent( ).getWidth( ) , e.getComponent( ).getHeight( ) , origin , direction );
			
			if( model != null )
			{
				List<PickResult<Shot>> pickResults = new ArrayList<PickResult<Shot>>( );
				model.pickShots( origin , direction , spc , pickResults );
				
				if( !pickResults.isEmpty( ) )
				{
					Collections.sort( pickResults );
					return ( ShotPickResult ) pickResults.get( 0 );
				}
			}
			
			return null;
		}
		
		@Override
		public void mouseMoved( MouseEvent e )
		{
			ShotPickResult picked = pick( e );
			
			if( model != null )
			{
				SelectionEditor editor = model.editSelection( );
				
				for( Shot shot : model.getHoveredShots( ) )
				{
					editor.unhover( shot );
				}
				
				if( picked != null )
				{
					LinearAxisConversion viewConversion = highlightDistAxis.getAxisConversion( );
					LinearAxisConversion conversion = new LinearAxisConversion( );
					conversion.set( viewConversion.invert( 0 ) , 1 , viewConversion.invert( highlightDistAxis.getViewSpan( ) ) , 0 );
					editor.hover( picked.picked , picked.locationAlongShot , conversion );
				}
				
				editor.commit( );
				
				canvas.display( );
			}
		}
		
		@Override
		public void mousePressed( MouseEvent e )
		{
			if( e.getButton( ) != MouseEvent.BUTTON1 )
			{
				return;
			}
			
			ShotPickResult picked = pick( e );
			
			if( picked == null )
			{
				return;
			}
			
			ListSelectionModel selModel = surveyTable.getSelectionModel( );
			
			if( model != null )
			{
				int index = picked.picked.getIndex( );
				
				int row = surveyTable.rowOfShot( index );
				
				if( row >= 0 )
				{
					if( ( e.getModifiersEx( ) & MouseEvent.CTRL_DOWN_MASK ) != 0 )
					{
						if( selModel.isSelectedIndex( row ) )
						{
							selModel.removeSelectionInterval( row , row );
						}
						else
						{
							selModel.addSelectionInterval( row , row );
						}
					}
					else
					{
						selModel.setSelectionInterval( row , row );
					}
					
					surveyTable.scrollRectToVisible( surveyTable.getCellRect( row , 0 , true ) );
				}
				
				canvas.display( );
			}
		}
	}
	
	private class TableSelectionHandler implements ListSelectionListener
	{
		@Override
		public void valueChanged( ListSelectionEvent e )
		{
			if( model == null )
			{
				return;
			}
			
			List<Survey3dModel.Shot> shots = model.getShots( );
			
			SelectionEditor editor = model.editSelection( );
			
			ListSelectionModel selModel = ( ListSelectionModel ) e.getSource( );
			
			if( e.getFirstIndex( ) < 0 )
			{
				for( Survey3dModel.Shot shot : shots )
				{
					editor.deselect( shot );
				}
			}
			else
			{
				for( int i = e.getFirstIndex( ) ; i <= e.getLastIndex( ) ; i++ )
				{
					SurveyShot shot = ( SurveyShot ) surveyTable.getModel( ).getValueAt( i , SurveyTable.SHOT_COLUMN );
					if( shot == null )
					{
						continue;
					}
					if( selModel.isSelectedIndex( i ) )
					{
						editor.select( shots.get( shot.index ) );
					}
					else
					{
						editor.deselect( shots.get( shot.index ) );
					}
				}
			}
			
			editor.commit( );
			
			updateCenterOfOrbit( );
			
			canvas.display( );
		}
	}
}

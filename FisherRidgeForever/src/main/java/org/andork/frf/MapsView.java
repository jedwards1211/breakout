package org.andork.frf;

import static org.andork.math3d.Vecmath.newMat4f;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import org.andork.awt.AWTUtil;
import org.andork.awt.GridBagWizard;
import org.andork.awt.anim.Animation;
import org.andork.awt.anim.AnimationQueue;
import org.andork.awt.event.UIBindings;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.DrawerAutoshowController;
import org.andork.awt.layout.DrawerLayoutDelegate;
import org.andork.awt.layout.Side;
import org.andork.event.BasicPropertyChangeListener;
import org.andork.event.Binder;
import org.andork.event.Binder.BindingAdapter;
import org.andork.frf.SettingsDrawer.CameraView;
import org.andork.frf.SettingsDrawer.FilterType;
import org.andork.frf.SurveyTableModel.SurveyTableModelCopier;
import org.andork.frf.model.Survey3dModel;
import org.andork.frf.model.Survey3dModel.SelectionEditor;
import org.andork.frf.model.Survey3dModel.Shot;
import org.andork.frf.model.Survey3dModel.ShotPickContext;
import org.andork.frf.model.Survey3dModel.ShotPickResult;
import org.andork.frf.model.SurveyShot;
import org.andork.frf.model.WeightedAverageTiltAxisInferrer;
import org.andork.jogl.BasicJOGLObject;
import org.andork.jogl.BasicJOGLScene;
import org.andork.jogl.awt.BasicJOGLSetup;
import org.andork.jogl.awt.anim.RandomOrbit;
import org.andork.jogl.awt.anim.SinusoidalTranslation;
import org.andork.jogl.awt.anim.SpringOrbit;
import org.andork.math3d.FittingFrustum;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.snakeyaml.YamlArrayList;
import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlObjectStringBimapper;
import org.andork.snakeyaml.YamlSpec;
import org.andork.spatial.Rectmath;
import org.andork.swing.AnnotatingRowSorter;
import org.andork.swing.AnnotatingRowSorter.SortRunner;
import org.andork.swing.DoSwing;
import org.andork.swing.DoSwingR2;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.async.SingleThreadedTaskService;
import org.andork.swing.async.Task;
import org.andork.swing.async.TaskService;
import org.andork.swing.async.TaskServiceFilePersister;
import org.andork.swing.border.InnerGradientBorder;
import org.andork.swing.border.LayeredBorder;
import org.andork.swing.border.OverrideInsetsBorder;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingJTables;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;
import org.andork.swing.table.RowFilterFactory;

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
	JMenuBar											menuBar;
	JMenu												fileMenu;
	JMenu												recentFilesMenu;
	
	DefaultNavigator									navigator;
	
	TaskService											taskService;
	final double[ ]										fromLoc					= new double[ 3 ];
	final double[ ]										toLoc					= new double[ 3 ];
	final double[ ]										toToLoc					= new double[ 3 ];
	final double[ ]										leftAtTo				= new double[ 3 ];
	final double[ ]										leftAtTo2				= new double[ 3 ];
	final double[ ]										leftAtFrom				= new double[ 3 ];
	
	PlotAxis											xaxis;
	PlotAxis											yaxis;
	AxisLinkButton										axisLinkButton;
	Plot												plot;
	JPanel												plotPanel;
	JPanel												mainPanel;
	JLayeredPane										layeredPane;
	
	PlotController										plotController;
	MouseLooper											mouseLooper;
	CameraAnimationInterrupter							cameraAnimationInterrupter;
	MousePickHandler									pickHandler;
	MouseAdapterChain									mouseAdapterChain;
	
	DrawerAutoshowController							autoshowController;
	
	TableSelectionHandler								selectionHandler;
	
	SurveyFilterFactory									surveyFilterFactory		= new SurveyFilterFactory( );
	
	SurveyDrawer										surveyDrawer;
	TaskListDrawer										taskListDrawer;
	SettingsDrawer										settingsDrawer;
	
	Survey3dModel										model3d;
	
	float[ ]											v						= newMat4f( );
	
	int													debugMbrCount			= 0;
	List<BasicJOGLObject>								debugMbrs				= new ArrayList<BasicJOGLObject>( );
	
	ShotPickContext										spc						= new ShotPickContext( );
	
	final LinePlaneIntersection3f						lpx						= new LinePlaneIntersection3f( );
	final float[ ]										p0						= new float[ 3 ];
	final float[ ]										p1						= new float[ 3 ];
	final float[ ]										p2						= new float[ 3 ];
	
	YamlObject<Model>									model;
	final Binder<YamlObject<Model>>						binder					= new Binder<YamlObject<Model>>( );
	final TaskServiceFilePersister<YamlObject<Model>>	persister;
	
	final AnimationQueue								cameraAnimationQueue	= new AnimationQueue( );
	
	public MapsView( )
	{
		super( );
		
		recentFilesMenu = new JMenu( "Recent Files" );
		fileMenu = new JMenu( "File" );
		fileMenu.add( recentFilesMenu );
		
		menuBar = new JMenuBar( );
		menuBar.add( fileMenu );
		
		JLabel highlightLabel = new JLabel( "Highlight: " );
		JLabel filterLabel = new JLabel( "Filter: " );
		
		final SortRunner sortRunner = new SortRunner( )
		{
			@Override
			public void submit( final Runnable r )
			{
				Task task = new Task( "Sorting survey table..." )
				{
					@Override
					protected void execute( )
					{
						r.run( );
					}
				};
				
				taskService.submit( task );
			}
		};
		
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				surveyDrawer = new SurveyDrawer( sortRunner );
				surveyDrawer.filterField( ).textComponent.getDocument( ).addDocumentListener(
						AnnotatingJTables.createFilterFieldListener( surveyDrawer.table( ) ,
								surveyDrawer.filterField( ).textComponent , surveyFilterFactory ) );
				surveyDrawer.highlightField( ).textComponent.getDocument( ).addDocumentListener(
						AnnotatingJTables.createHighlightFieldListener( surveyDrawer.table( ) ,
								surveyDrawer.highlightField( ).textComponent , surveyFilterFactory , Color.YELLOW ) );
			}
		};
		
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
		
		plotController = new PlotController( plot , xAxisController , yAxisController );
		
		pickHandler = new MousePickHandler( );
		
		mouseLooper = new MouseLooper( );
		canvas.addMouseListener( mouseLooper );
		canvas.addMouseMotionListener( mouseLooper );
		canvas.addMouseWheelListener( mouseLooper );
		
		autoshowController = new DrawerAutoshowController( );
		
		cameraAnimationInterrupter = new CameraAnimationInterrupter( );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( plotController );
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
		mouseAdapterChain.addMouseAdapter( cameraAnimationInterrupter );
		
		plotPanel = new JPanel( new PlotPanelLayout( ) );
		plotPanel.add( plot );
		plotPanel.add( xaxis );
		plotPanel.add( yaxis );
		plotPanel.add( axisLinkButton , Corner.TOP_LEFT );
		
		canvas.removeMouseListener( super.navigator );
		canvas.removeMouseMotionListener( super.navigator );
		canvas.removeMouseWheelListener( super.navigator );
		
		canvas.removeMouseListener( orbiter );
		canvas.removeMouseMotionListener( orbiter );
		canvas.removeMouseWheelListener( orbiter );
		
		perspectiveMode( );
		
		layeredPane = new JLayeredPane( );
		layeredPane.setLayout( new DelegatingLayoutManager( )
		{
			public void onLayoutChanged( Container target )
			{
				Window w = SwingUtilities.getWindowAncestor( target );
				if( w != null )
				{
					w.invalidate( );
					w.validate( );
				}
				target.invalidate( );
				target.validate( );
			}
		} );
		
		taskService = new SingleThreadedTaskService( );
		taskListDrawer = new TaskListDrawer( taskService );
		taskListDrawer.addTo( layeredPane , JLayeredPane.DEFAULT_LAYER + 5 );
		
		taskListDrawer.delegate( ).changeSupport( ).addPropertyChangeListener( DrawerLayoutDelegate.OPEN , new BasicPropertyChangeListener( )
		{
			@Override
			public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
			{
				JFrame frame = AWTUtil.getAncestorOfClass( JFrame.class , mainPanel );
				if( frame != null )
				{
					if( frame.getJMenuBar( ) == null )
					{
						frame.setJMenuBar( menuBar );
					}
					menuBar.setVisible( taskListDrawer.delegate( ).isOpen( ) );
				}
			}
		} );
		
		settingsDrawer = new SettingsDrawer( binder.subBinder( Model.settingsDrawerModel ) );
		settingsDrawer.addTo( layeredPane , 1 );
		
		settingsDrawer.updateViewButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Task task = new Task( "Updating view: parsing data..." )
				{
					@Override
					protected void execute( )
					{
						List<SurveyShot> shots = new DoSwingR2<List<SurveyShot>>( )
						{
							@Override
							protected List<SurveyShot> doRun( )
							{
								return surveyDrawer.table( ).createShots( );
							}
						}.result( );
						
						updateModel( shots , this );
					}
				};
				
				taskService.submit( task );
			}
			
			public void updateModel( List<SurveyShot> shots , Task task )
			{
				task.setStatus( "Updating view..." );
				
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						if( model3d != null )
						{
							scene.remove( model3d.getRootGroup( ) );
							scene.destroyLater( model3d.getRootGroup( ) );
						}
					}
				};
				
				task.setStatus( "Updating view: constructing new model..." );
				
				final Survey3dModel model = Survey3dModel.create( shots , 10 , 3 , 3 , 3 );
				
				task.setStatus( "Updating view: installing new model..." );
				
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						MapsView.this.model3d = model;
						scene.add( model.getRootGroup( ) );
						scene.initLater( model.getRootGroup( ) );
						
						binder.modelToView( );
						
						float[ ] center = new float[ 3 ];
						Rectmath.center( model.getTree( ).getRoot( ).mbr( ) , center );
						orbiter.setCenter( center );
						
						canvas.repaint( );
					}
				};
			}
		} );
		
		layeredPane.add( plotPanel );
		surveyDrawer.addTo( layeredPane , 5 );
		
		mainPanel = new JPanel( new BorderLayout( ) );
		// mainPanel.add( modeComboBox , BorderLayout.NORTH );
		mainPanel.add( layeredPane , BorderLayout.CENTER );
		
		selectionHandler = new TableSelectionHandler( );
		surveyDrawer.table( ).getModelSelectionModel( ).addListSelectionListener( selectionHandler );
		
		final AnnotatingJTable quickTable = new DoSwingR2<AnnotatingJTable>( )
		{
			@Override
			public AnnotatingJTable doRun( )
			{
				DefaultTableColumnModel quickTableColumnModel = new DefaultTableColumnModel( );
				TableColumn fromColumn = new TableColumn( SurveyTable.FROM_COLUMN );
				fromColumn.setIdentifier( "From" );
				fromColumn.setHeaderValue( "From" );
				TableColumn toColumn = new TableColumn( SurveyTable.TO_COLUMN );
				toColumn.setIdentifier( "To" );
				toColumn.setHeaderValue( "To" );
				quickTableColumnModel.addColumn( fromColumn );
				quickTableColumnModel.addColumn( toColumn );
				
				AnnotatingJTable result = new AnnotatingJTable( surveyDrawer.table( ).getModel( ) , quickTableColumnModel );
				result.setModelSelectionModel( surveyDrawer.table( ).getModelSelectionModel( ) );
				
				return result;
			}
		}.result( );
		
		DefaultAnnotatingJTableSetup<SurveyTableModel, RowFilter<SurveyTableModel, Integer>> quickTableSetup =
				new DoSwingR2<DefaultAnnotatingJTableSetup<SurveyTableModel, RowFilter<SurveyTableModel, Integer>>>( )
				{
					@Override
					protected DefaultAnnotatingJTableSetup<SurveyTableModel, RowFilter<SurveyTableModel, Integer>> doRun( )
					{
						DefaultAnnotatingJTableSetup<SurveyTableModel, RowFilter<SurveyTableModel, Integer>> quickTableSetup =
								new DefaultAnnotatingJTableSetup<SurveyTableModel, RowFilter<SurveyTableModel, Integer>>(
										quickTable , sortRunner );
						quickTableSetup.table.getAnnotatingRowSorter( ).setModelCopier( new SurveyTableModelCopier( ) );
						return quickTableSetup;
					}
				}.result( );
		
		JLabel quickTableFilterLabel = new JLabel( "Filter: " );
		TextComponentWithHintAndClear quickTableFilterField = new TextComponentWithHintAndClear( "Enter Filter Regexp" );
		quickTableFilterField.textComponent.getDocument( ).addDocumentListener(
				AnnotatingJTables.createFilterFieldListener( quickTableSetup.table ,
						quickTableFilterField.textComponent , surveyFilterFactory ) );
		
		JLabel quickTableHighlightLabel = new JLabel( "Highlight: " );
		TextComponentWithHintAndClear quickTableHighlightField = new TextComponentWithHintAndClear( "Enter Highlight Regexp" );
		quickTableHighlightField.textComponent.getDocument( ).addDocumentListener(
				AnnotatingJTables.createHighlightFieldListener( quickTableSetup.table ,
						quickTableHighlightField.textComponent , surveyFilterFactory , Color.YELLOW ) );
		
		JPanel quickTablePanel = new JPanel( );
		quickTablePanel.setPreferredSize( new Dimension( 150 , 500 ) );
		GridBagWizard gbw = GridBagWizard.create( quickTablePanel );
		gbw.put( quickTableFilterLabel ).xy( 0 , 0 ).west( ).insets( 2 , 2 , 0 , 0 );
		gbw.put( quickTableFilterField ).rightOf( quickTableFilterLabel ).fillx( 1.0 ).insets( 2 , 2 , 0 , 0 );
		gbw.put( quickTableHighlightLabel ).below( quickTableFilterLabel ).west( ).insets( 2 , 2 , 2 , 0 );
		gbw.put( quickTableHighlightField ).rightOf( quickTableHighlightLabel ).fillx( 1.0 ).insets( 2 , 2 , 2 , 0 );
		
		gbw.put( quickTableSetup.scrollPane ).below( quickTableHighlightLabel , quickTableHighlightField ).fillboth( 1.0 , 1.0 );
		
		Drawer quickTableDrawer = new Drawer( quickTablePanel );
		quickTableDrawer.delegate( ).dockingSide( Side.LEFT );
		quickTableDrawer.mainResizeHandle( );
		quickTableDrawer.pinButton( );
		quickTableDrawer.pinButtonDelegate( ).corner( Corner.TOP_RIGHT ).side( Side.RIGHT );
		quickTableDrawer.addTo( layeredPane , 3 );
		
		surveyDrawer.table( ).setTransferHandler( new SurveyTableTransferHandler( ) );
		
		surveyDrawer.table( ).addPropertyChangeListener( "model" , new PropertyChangeListener( )
		{
			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				AnnotatingRowSorter<SurveyTableModel, Integer, RowFilter<SurveyTableModel, Integer>> sorter =
						( AnnotatingRowSorter<SurveyTableModel, Integer, RowFilter<SurveyTableModel, Integer>> ) quickTable.getRowSorter( );
				
				SurveyTableModel newModel = ( SurveyTableModel ) evt.getNewValue( );
				
				quickTable.setRowSorter( null );
				quickTable.setModel( newModel );
				sorter.setModel( newModel );
				quickTable.setRowSorter( sorter );
			}
		} );
		
		UIBindings.bind( binder , surveyDrawer.pinButton( ) , Model.surveyDrawerPinned );
		UIBindings.bind( binder , surveyDrawer.maxButton( ) , Model.surveyDrawerMaximized );
		UIBindings.bind( binder , settingsDrawer.pinButton( ) , Model.settingsDrawerPinned );
		UIBindings.bind( binder , taskListDrawer.pinButton( ) , Model.taskListDrawerPinned );
		UIBindings.bind( binder , quickTableDrawer.pinButton( ) , Model.miniSurveyDrawerPinned );
		
		binder.subBinder( Model.settingsDrawerModel ).bind( new BindingAdapter( SettingsDrawer.Model.cameraView )
		{
			public void modelToView( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null )
				{
					setCameraView( ( CameraView ) model.get( SettingsDrawer.Model.cameraView ) );
				}
			}
		} );
		
		binder.subBinder( Model.settingsDrawerModel ).bind( new BindingAdapter( SettingsDrawer.Model.mouseSensitivity )
		{
			@Override
			public void modelToView( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null )
				{
					float sensitivity = ( Integer ) model.get( SettingsDrawer.Model.mouseSensitivity ) / 20f;
					orbiter.setSensitivity( sensitivity );
					navigator.setSensitivity( sensitivity );
				}
			}
		} );
		
		binder.subBinder( Model.settingsDrawerModel ).bind( new BindingAdapter( SettingsDrawer.Model.distRange )
		{
			@Override
			public void modelToView( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model3d != null )
				{
					LinearAxisConversion range = ( LinearAxisConversion ) model.get( SettingsDrawer.Model.distRange );
					float nearDist = ( float ) range.invert( 0.0 );
					float farDist = ( float ) range.invert( settingsDrawer.getDistColorationAxis( ).getViewSpan( ) );
					model3d.setNearDist( nearDist );
					model3d.setFarDist( farDist );
					canvas.display( );
				}
			}
		} );
		
		binder.subBinder( Model.settingsDrawerModel ).bind( new BindingAdapter( SettingsDrawer.Model.paramRange )
		{
			@Override
			public void modelToView( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model3d != null )
				{
					LinearAxisConversion range = ( LinearAxisConversion ) model.get( SettingsDrawer.Model.paramRange );
					float loParam = ( float ) range.invert( 0.0 );
					float hiParam = ( float ) range.invert( settingsDrawer.getParamColorationAxis( ).getViewSpan( ) );
					model3d.setLoParam( loParam );
					model3d.setHiParam( hiParam );
					canvas.display( );
				}
			}
		} );
		
		persister = new TaskServiceFilePersister<YamlObject<Model>>( taskService , "Saving settings..." ,
				YamlObjectStringBimapper.newInstance( Model.instance ) , new File( "settings.frf.yaml" ) );
		YamlObject<Model> model = null;
		
		try
		{
			model = persister.load( );
		}
		catch( Exception ex )
		{
		}
		
		if( model == null )
		{
			model = Model.instance.newObject( );
			YamlObject<SettingsDrawer.Model> settingsDrawerModel = SettingsDrawer.Model.instance.newObject( );
			settingsDrawerModel.set( SettingsDrawer.Model.cameraView , CameraView.PERSPECTIVE );
			settingsDrawerModel.set( SettingsDrawer.Model.mouseSensitivity , 20 );
			settingsDrawerModel.set( SettingsDrawer.Model.distRange , new LinearAxisConversion( 0 , 0 , 20000 , 200 ) );
			settingsDrawerModel.set( SettingsDrawer.Model.paramRange , new LinearAxisConversion( 0 , 0 , 500 , 200 ) );
			settingsDrawerModel.set( SettingsDrawer.Model.highlightRange , new LinearAxisConversion( 0 , 0 , 1000 , 200 ) );
			settingsDrawerModel.set( SettingsDrawer.Model.filterType , FilterType.ALPHA_DESIGNATION );
			model.set( Model.settingsDrawerModel , settingsDrawerModel );
		}
		
		setModel( model );
		
		settingsDrawer.getFitViewToSelectedButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				fitViewToSelected( );
			}
		} );
		
		settingsDrawer.getFitViewToEverythingButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				fitViewToEverything( );
			}
		} );
		
		settingsDrawer.getOrbitToPlanButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}
				
				float[ ] center = new float[ 3 ];
				orbiter.getCenter( center );
				
				if( Vecmath.hasNaNsOrInfinites( center ) )
				{
					model3d.getCenter( center );
				}
				
				cameraAnimationQueue.clear( );
				cameraAnimationQueue.add( new SpringOrbit( MapsView.this , center , 0f , ( float ) -Math.PI * .5f , .1f , .05f , 30 ) );
			}
		} );
		
		settingsDrawer.getInferDepthAxisTiltButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}
				model3d.setDepthAxis( new WeightedAverageTiltAxisInferrer( ).inferTiltAxis( model3d.getOriginalShots( ) ) );
				canvas.display( );
			}
		} );
		
		settingsDrawer.getResetDepthAxisTiltButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}
				model3d.setDepthAxis( new float[ ] { 0f , -1f , 0f } );
				canvas.display( );
			}
		} );
		
		( ( JTextField ) surveyDrawer.filterField( ).textComponent ).addActionListener( new FitToFilteredHandler( surveyDrawer.table( ) ) );
		( ( JTextField ) quickTableFilterField.textComponent ).addActionListener( new FitToFilteredHandler( quickTable ) );
	}
	
	public void setModel( YamlObject<Model> model )
	{
		if( this.model != model )
		{
			if( this.model != null )
			{
				this.model.changeSupport( ).removePropertyChangeListener( persister );
			}
			this.model = model;
			binder.setModel( model );
			binder.modelToView( );
			if( model != null )
			{
				model.changeSupport( ).addPropertyChangeListener( persister );
			}
		}
	}
	
	protected void fitViewToSelected( )
	{
		if( settingsDrawer.getModel( ).get( SettingsDrawer.Model.cameraView ) != CameraView.PERSPECTIVE )
		{
			return;
		}
		
		ListSelectionModel selectionModel = surveyDrawer.table( ).getModelSelectionModel( );
		
		if( selectionModel.getMinSelectionIndex( ) < 0 )
		{
			return;
		}
		
		FittingFrustum frustum = new FittingFrustum( );
		
		frustum.init( scene.pickXform( ) , .8f );
		
		for( int i = selectionModel.getMinSelectionIndex( ) ; i <= selectionModel.getMaxSelectionIndex( ) ; i++ )
		{
			if( !selectionModel.isSelectedIndex( i ) )
			{
				continue;
			}
			SurveyShot shot = ( SurveyShot ) surveyDrawer.table( ).getModel( ).getValueAt( i , SurveyTable.SHOT_COLUMN );
			if( shot == null )
			{
				continue;
			}
			
			frustum.addPoint(
					( float ) shot.from.position[ 0 ] ,
					( float ) shot.from.position[ 1 ] ,
					( float ) shot.from.position[ 2 ]
					);
			frustum.addPoint(
					( float ) shot.to.position[ 0 ] ,
					( float ) shot.to.position[ 1 ] ,
					( float ) shot.to.position[ 2 ]
					);
			
		}
		
		float[ ] coord = new float[ 3 ];
		
		frustum.calculateOrigin( coord );
		
		cameraAnimationQueue.clear( );
		cameraAnimationQueue.add( new SinusoidalTranslation( this , coord , 30 , 1000 ) );
	}
	
	protected void fitViewToEverything( )
	{
		if( settingsDrawer.getModel( ).get( SettingsDrawer.Model.cameraView ) != CameraView.PERSPECTIVE )
		{
			return;
		}
		
		FittingFrustum frustum = new FittingFrustum( );
		
		frustum.init( scene.pickXform( ) , .8f );
		
		for( int i = 0 ; i < surveyDrawer.table( ).getModel( ).getRowCount( ) ; i++ )
		{
			SurveyShot shot = ( SurveyShot ) surveyDrawer.table( ).getModel( ).getValueAt( i , SurveyTable.SHOT_COLUMN );
			if( shot == null )
			{
				continue;
			}
			
			frustum.addPoint(
					( float ) shot.from.position[ 0 ] ,
					( float ) shot.from.position[ 1 ] ,
					( float ) shot.from.position[ 2 ]
					);
			frustum.addPoint(
					( float ) shot.to.position[ 0 ] ,
					( float ) shot.to.position[ 1 ] ,
					( float ) shot.to.position[ 2 ]
					);
			
		}
		
		float[ ] coord = new float[ 3 ];
		
		frustum.calculateOrigin( coord );
		
		cameraAnimationQueue.clear( );
		cameraAnimationQueue.add( new SinusoidalTranslation( this , coord , 30 , 1000 ) );
	}
	
	protected void flyToFiltered( final AnnotatingJTable<?, ?> table )
	{
		if( model3d == null )
		{
			return;
		}
		
		float[ ] center = new float[ 3 ];
		orbiter.getCenter( center );
		
		if( Vecmath.hasNaNsOrInfinites( center ) )
		{
			model3d.getCenter( center );
		}
		
		cameraAnimationQueue.clear( );
		cameraAnimationQueue.add( new SpringOrbit( this , center , 0f , ( float ) -Math.PI / 4 , .1f , .05f , 30 ) );
		cameraAnimationQueue.add( new Animation( )
		{
			@Override
			public long animate( long animTime )
			{
				table.getModelSelectionModel( ).clearSelection( );
				table.selectAll( );
				
				fitViewToSelected( );
				
				float[ ] center = new float[ 3 ];
				orbiter.getCenter( center );
				
				if( Vecmath.hasNaNsOrInfinites( center ) )
				{
					model3d.getCenter( center );
				}
				
				cameraAnimationQueue.add( new RandomOrbit( MapsView.this , center , 0.0005f ,
						( float ) -Math.PI / 4 , ( float ) -Math.PI / 9 , 30 , 60000 ) );
				return 0;
			}
		} );
	}
	
	@Override
	protected void init( )
	{
		super.init( );
		
		navigator = new DefaultNavigator( this );
		
		navigator.setMoveFactor( 5f );
		navigator.setWheelFactor( 5f );
	}
	
	CameraView	currentView;
	
	public void setCameraView( CameraView view )
	{
		if( view != currentView )
		{
			currentView = view;
			switch( view )
			{
				case PERSPECTIVE:
					perspectiveMode( );
					break;
				case PLAN:
					planMode( );
					break;
				case NORTH_FACING_PROFILE:
					northFacingProfileMode( );
					break;
				case SOUTH_FACING_PROFILE:
					southFacingProfileMode( );
					break;
				case EAST_FACING_PROFILE:
					eastFacingProfileMode( );
					break;
				case WEST_FACING_PROFILE:
					westFacingProfileMode( );
					break;
			}
		}
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
		mouseAdapterChain.addMouseAdapter( cameraAnimationInterrupter );
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
		mouseAdapterChain.addMouseAdapter( cameraAnimationInterrupter );
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
	
	private void updateCenterOfOrbit( )
	{
		List<SurveyShot> origShots = model3d.getOriginalShots( );
		
		Set<Survey3dModel.Shot> newSelectedShots = model3d.getSelectedShots( );
		
		float[ ] bounds = Rectmath.voidRectf( 3 );
		float[ ] p = Rectmath.voidRectf( 3 );
		
		double[ ] center = new double[ 3 ];
		for( Survey3dModel.Shot shot : newSelectedShots )
		{
			SurveyShot origShot = origShots.get( shot.getIndex( ) );
			p[ 0 ] = ( float ) Math.min( origShot.from.position[ 0 ] , origShot.to.position[ 0 ] );
			p[ 1 ] = ( float ) Math.min( origShot.from.position[ 1 ] , origShot.to.position[ 1 ] );
			p[ 2 ] = ( float ) Math.min( origShot.from.position[ 2 ] , origShot.to.position[ 2 ] );
			p[ 3 ] = ( float ) Math.max( origShot.from.position[ 0 ] , origShot.to.position[ 0 ] );
			p[ 4 ] = ( float ) Math.max( origShot.from.position[ 1 ] , origShot.to.position[ 1 ] );
			p[ 5 ] = ( float ) Math.max( origShot.from.position[ 2 ] , origShot.to.position[ 2 ] );
			Rectmath.union3( bounds , p , bounds );
			// add3( center , origShot.from.position , center );
			// add3( center , origShot.to.position , center );
		}
		
		if( !newSelectedShots.isEmpty( ) )
		{
			// scale3( center , 0.5 / newSelectedShots.size( ) );
			p[ 0 ] = ( float ) ( bounds[ 0 ] + bounds[ 3 ] ) * 0.5f;
			p[ 1 ] = ( float ) ( bounds[ 1 ] + bounds[ 4 ] ) * 0.5f;
			p[ 2 ] = ( float ) ( bounds[ 2 ] + bounds[ 5 ] ) * 0.5f;
			orbiter.setCenter( p );
		}
	}
	
	private class MousePickHandler extends MouseAdapter
	{
		private ShotPickResult pick( MouseEvent e )
		{
			float[ ] origin = new float[ 3 ];
			float[ ] direction = new float[ 3 ];
			scene.pickXform( ).xform( e.getX( ) , e.getY( ) , e.getComponent( ).getWidth( ) , e.getComponent( ).getHeight( ) , origin , direction );
			
			if( model3d != null )
			{
				List<PickResult<Shot>> pickResults = new ArrayList<PickResult<Shot>>( );
				model3d.pickShots( origin , direction , spc , pickResults );
				
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
			
			if( model3d != null )
			{
				SelectionEditor editor = model3d.editSelection( );
				
				for( Shot shot : model3d.getHoveredShots( ) )
				{
					editor.unhover( shot );
				}
				
				if( picked != null )
				{
					LinearAxisConversion conversion = settingsDrawer.getModel( ).get( SettingsDrawer.Model.highlightRange );
					LinearAxisConversion conversion2 = new LinearAxisConversion( conversion.invert( 0.0 ) , 1.0 , conversion.invert( settingsDrawer.getHighlightDistAxis( ).getViewSpan( ) ) , 0.0 );
					editor.hover( picked.picked , picked.locationAlongShot , conversion2 );
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
			
			ListSelectionModel selModel = surveyDrawer.table( ).getModelSelectionModel( );
			
			if( model3d != null )
			{
				int index = picked.picked.getIndex( );
				
				int modelRow = surveyDrawer.table( ).getModel( ).rowOfShot( index );
				
				if( modelRow >= 0 )
				{
					if( ( e.getModifiersEx( ) & MouseEvent.CTRL_DOWN_MASK ) != 0 )
					{
						if( selModel.isSelectedIndex( modelRow ) )
						{
							selModel.removeSelectionInterval( modelRow , modelRow );
						}
						else
						{
							selModel.addSelectionInterval( modelRow , modelRow );
						}
					}
					else
					{
						selModel.setSelectionInterval( modelRow , modelRow );
					}
					
					int viewRow = surveyDrawer.table( ).convertRowIndexToView( modelRow );
					
					if( viewRow >= 0 )
					{
						Rectangle visibleRect = surveyDrawer.table( ).getVisibleRect( );
						Rectangle cellRect = surveyDrawer.table( ).getCellRect( viewRow , 0 , true );
						visibleRect.y = cellRect.y + cellRect.height / 2 - visibleRect.height / 2;
						surveyDrawer.table( ).scrollRectToVisible( visibleRect );
					}
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
			if( e.getValueIsAdjusting( ) || model3d == null )
			{
				return;
			}
			
			List<Survey3dModel.Shot> shots = model3d.getShots( );
			
			SelectionEditor editor = model3d.editSelection( );
			
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
					SurveyShot shot = ( SurveyShot ) surveyDrawer.table( ).getModel( ).getValueAt( i , SurveyTable.SHOT_COLUMN );
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
	
	public static final class Model extends YamlSpec<Model>
	{
		public static final Attribute<YamlObject<SettingsDrawer.Model>>	settingsDrawerModel		= yamlObjectAttribute( "settingsDrawerModel" , SettingsDrawer.Model.instance );
		public static final Attribute<Boolean>							settingsDrawerPinned	= booleanAttribute( "settingsDrawerPinned" );
		public static final Attribute<Boolean>							surveyDrawerPinned		= booleanAttribute( "surveyDrawerPinned" );
		public static final Attribute<Boolean>							surveyDrawerMaximized	= booleanAttribute( "surveyDrawerMaximized" );
		public static final Attribute<Boolean>							miniSurveyDrawerPinned	= booleanAttribute( "miniSurveyDrawerPinned" );
		public static final Attribute<Boolean>							taskListDrawerPinned	= booleanAttribute( "taskListDrawerPinned" );
		public static final Attribute<YamlArrayList<File>>				recentFiles				= yamlArrayListAttribute( "recentFiles" , YamlSpec.fileObjectBimapper );
		
		private Model( )
		{
			super( settingsDrawerModel , settingsDrawerPinned , surveyDrawerPinned , surveyDrawerMaximized , miniSurveyDrawerPinned , taskListDrawerPinned );
		}
		
		public static final Model	instance	= new Model( );
	}
	
	class FitToFilteredHandler implements ActionListener
	{
		AnnotatingJTable<SurveyTableModel, RowFilter<SurveyTableModel, Integer>>	table;
		long																		lastAction	= 0;
		
		public FitToFilteredHandler( AnnotatingJTable<SurveyTableModel, RowFilter<SurveyTableModel, Integer>> table )
		{
			super( );
			this.table = table;
		}
		
		@Override
		public void actionPerformed( ActionEvent e )
		{
			final long time = System.currentTimeMillis( );
			lastAction = time;
			
			table.getAnnotatingRowSorter( ).invokeWhenDoneSorting( new Runnable( )
			{
				@Override
				public void run( )
				{
					if( time >= lastAction )
					{
						flyToFiltered( table );
					}
				}
			} );
		}
	};
	
	class CameraAnimationInterrupter extends MouseAdapter
	{
		@Override
		public void mousePressed( MouseEvent e )
		{
			cameraAnimationQueue.clear( );
		}
	}
	
	class SurveyFilterFactory implements RowFilterFactory<String, SurveyTableModel, Integer>
	{
		@Override
		public RowFilter<SurveyTableModel, Integer> createFilter( String input )
		{
			switch( model.get( Model.settingsDrawerModel ).get( SettingsDrawer.Model.filterType ) )
			{
				case ALPHA_DESIGNATION:
					return new SurveyDesignationFilter( input );
				case REGEXP:
					return new SurveyRegexFilter( input );
				default:
					return null;
			}
		}
	}
}

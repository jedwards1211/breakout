package org.andork.breakout;

import static org.andork.math3d.Vecmath.newMat4f;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.anim.Animation;
import org.andork.awt.anim.AnimationQueue;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.DrawerAutoshowController;
import org.andork.awt.layout.DrawerModel;
import org.andork.awt.layout.Side;
import org.andork.breakout.SettingsDrawer.CameraView;
import org.andork.breakout.SettingsDrawer.FilterType;
import org.andork.breakout.model.ProjectModel;
import org.andork.breakout.model.RootModel;
import org.andork.breakout.model.Survey3dModel;
import org.andork.breakout.model.Survey3dModel.SelectionEditor;
import org.andork.breakout.model.Survey3dModel.Shot;
import org.andork.breakout.model.Survey3dModel.ShotPickContext;
import org.andork.breakout.model.Survey3dModel.ShotPickResult;
import org.andork.breakout.model.SurveyShot;
import org.andork.breakout.model.SurveyStation;
import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.SurveyTableModelCopier;
import org.andork.breakout.model.WeightedAverageTiltAxisInferrer;
import org.andork.event.BasicPropertyChangeListener;
import org.andork.event.Binder;
import org.andork.event.Binder.BindingAdapter;
import org.andork.func.CompoundBimapper;
import org.andork.func.FileStringBimapper;
import org.andork.func.StringObjectBimapper;
import org.andork.jogl.BasicJOGLObject;
import org.andork.jogl.OrthoProjectionCalculator;
import org.andork.jogl.PerspectiveProjectionCalculator;
import org.andork.jogl.awt.ScreenCaptureDialog;
import org.andork.jogl.awt.ScreenCaptureDialogModel;
import org.andork.jogl.awt.anim.RandomOrbit;
import org.andork.jogl.awt.anim.SinusoidalTranslation;
import org.andork.jogl.awt.anim.SpringOrbit;
import org.andork.jogl.neu.JoglScene;
import org.andork.jogl.neu.awt.BasicJoglSetup;
import org.andork.math3d.FittingFrustum;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.model.Model;
import org.andork.snakeyaml.EDTYamlObjectStringBimapper;
import org.andork.snakeyaml.YamlArrayList;
import org.andork.snakeyaml.YamlObject;
import org.andork.spatial.Rectmath;
import org.andork.swing.AnnotatingRowSorter;
import org.andork.swing.AnnotatingRowSorter.SortRunner;
import org.andork.swing.DoSwing;
import org.andork.swing.DoSwingR2;
import org.andork.swing.FromEDT;
import org.andork.swing.OnEDT;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.async.SelfReportingTask;
import org.andork.swing.async.SingleThreadedTaskService;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.SubtaskFilePersister;
import org.andork.swing.async.SubtaskStreamBimapper;
import org.andork.swing.async.SubtaskStreamBimapperFactory;
import org.andork.swing.async.Task;
import org.andork.swing.async.TaskService;
import org.andork.swing.async.TaskServiceBatcher;
import org.andork.swing.async.TaskServiceFilePersister;
import org.andork.swing.async.TaskServiceSubtaskFilePersister;
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

public class BreakoutMainView extends BasicJoglSetup
{
	I18n												i18n					= new I18n( );
	
	PerspectiveProjectionCalculator						perspCalculator			= new PerspectiveProjectionCalculator( ( float ) Math.PI / 2 , 1f , 1e7f );
	OrthoProjectionCalculator							orthoCalculator			= new OrthoProjectionCalculator( -1 , 1 , -1 , 1 , -10000 , 10000 );
	DefaultNavigator									navigator;
	
	TaskService											rebuildTaskService;
	TaskService											sortTaskService;
	TaskService											ioTaskService;
	
	SurveyTableChangeHandler							surveyTableChangeHandler;
	
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
	OtherMouseHandler									otherMouseHandler;
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
	
	ScreenCaptureDialog									screenCaptureDialog;
	
	final LinePlaneIntersection3f						lpx						= new LinePlaneIntersection3f( );
	final float[ ]										p0						= new float[ 3 ];
	final float[ ]										p1						= new float[ 3 ];
	final float[ ]										p2						= new float[ 3 ];
	
	File												rootFile;
	TaskServiceFilePersister<YamlObject<RootModel>>		rootPersister;
	final Binder<YamlObject<RootModel>>					rootModelBinder			= new Binder<YamlObject<RootModel>>( );
	
	final Binder<YamlObject<ProjectModel>>				projectModelBinder		= new Binder<YamlObject<ProjectModel>>( );
	TaskServiceFilePersister<YamlObject<ProjectModel>>	projectPersister;
	
	SubtaskFilePersister<SurveyTableModel>				surveyPersister;
	
	final AnimationQueue								cameraAnimationQueue	= new AnimationQueue( );
	
	NewProjectAction									newProjectAction		= new NewProjectAction( this );
	OpenProjectAction									openProjectAction		= new OpenProjectAction( this );
	
	public BreakoutMainView( )
	{
		super( createCanvas( ) );
		
		ioTaskService = new SingleThreadedTaskService( );
		rebuildTaskService = new SingleThreadedTaskService( );
		sortTaskService = new SingleThreadedTaskService( );
		
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
				
				sortTaskService.submit( task );
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
				OrthoProjectionCalculator calc = ( OrthoProjectionCalculator ) scene.getProjectionCalculator( );
				orthoCalculator.orthoFrame[ 0 ] = ( float ) xaxis.getAxisConversion( ).invert( 0 );
				orthoCalculator.orthoFrame[ 1 ] = ( float ) xaxis.getAxisConversion( ).invert( plot.getWidth( ) );
				scene.recalculateProjection( );
				canvas.repaint( );
			}
		};
		PlotAxisController yAxisController = new PlotAxisController( yaxis )
		{
			@Override
			protected void setAxisRange( double start , double end )
			{
				super.setAxisRange( start , end );
				orthoCalculator.orthoFrame[ 2 ] = ( float ) yaxis.getAxisConversion( ).invert( plot.getHeight( ) );
				orthoCalculator.orthoFrame[ 3 ] = ( float ) yaxis.getAxisConversion( ).invert( 0 );
				scene.recalculateProjection( );
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
		
		otherMouseHandler = new OtherMouseHandler( );
		
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( plotController );
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
		mouseAdapterChain.addMouseAdapter( otherMouseHandler );
		
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
		
		taskListDrawer = new TaskListDrawer( );
		taskListDrawer.addTaskService( rebuildTaskService );
		taskListDrawer.addTaskService( sortTaskService );
		taskListDrawer.addTaskService( ioTaskService );
		taskListDrawer.addTo( layeredPane , JLayeredPane.DEFAULT_LAYER + 5 );
		
		settingsDrawer = new SettingsDrawer( i18n , rootModelBinder , projectModelBinder );
		settingsDrawer.addTo( layeredPane , 1 );
		
		surveyTableChangeHandler = new SurveyTableChangeHandler( rebuildTaskService );
		surveyDrawer.table( ).getModel( ).addTableModelListener( surveyTableChangeHandler );
		
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
				TableColumn fromColumn = new TableColumn( SurveyTableModel.Row.from.getIndex( ) );
				fromColumn.setIdentifier( "From" );
				fromColumn.setHeaderValue( "From" );
				TableColumn toColumn = new TableColumn( SurveyTableModel.Row.to.getIndex( ) );
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
		
		surveyDrawer.setBinder( projectModelBinder.subBinder( ProjectModel.surveyDrawer ) );
		settingsDrawer.setBinder( projectModelBinder.subBinder( ProjectModel.settingsDrawer ) );
		taskListDrawer.setBinder( projectModelBinder.subBinder( ProjectModel.taskListDrawer ) );
		
		projectModelBinder.bind( new BindingAdapter( ProjectModel.viewXform )
		{
			@Override
			public void modelToViewImpl( )
			{
				Model model = getModel( );
				if( model != null )
				{
					scene.setViewXform( ( float[ ] ) model.get( ProjectModel.viewXform ) );
				}
			}
		} );
		
		projectModelBinder.bind( new BindingAdapter( ProjectModel.cameraView )
		{
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null )
				{
					setCameraView( ( CameraView ) model.get( ProjectModel.cameraView ) );
				}
			}
		} );
		
		projectModelBinder.bind( new BindingAdapter( ProjectModel.backgroundColor )
		{
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null )
				{
					Color bgColor = ( Color ) model.get( ProjectModel.backgroundColor );
					if( bgColor != null )
					{
						scene.setBgColor( bgColor.getRed( ) / 255f , bgColor.getGreen( ) / 255f , bgColor.getBlue( ) / 255f , 1f );
					}
				}
			}
		} );
		
		rootModelBinder.bind( new BindingAdapter( RootModel.mouseSensitivity )
		{
			@Override
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model.get( RootModel.mouseSensitivity ) instanceof Integer )
				{
					float sensitivity = ( Integer ) model.get( RootModel.mouseSensitivity ) / 20f;
					orbiter.setSensitivity( sensitivity );
					navigator.setSensitivity( sensitivity );
				}
			}
		} );
		
		rootModelBinder.bind( new BindingAdapter( RootModel.mouseWheelSensitivity )
		{
			@Override
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model.get( RootModel.mouseWheelSensitivity ) instanceof Integer )
				{
					float sensitivity = ( Integer ) model.get( RootModel.mouseWheelSensitivity ) / 20f;
					navigator.setWheelFactor( sensitivity );
				}
			}
		} );
		
		projectModelBinder.bind( new BindingAdapter( ProjectModel.ambientLight )
		{
			@Override
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model3d != null )
				{
					Float ambientLight = ( Float ) model.get( ProjectModel.ambientLight );
					if( ambientLight != null )
					{
						model3d.setAmbientLight( ambientLight );
						canvas.display( );
					}
				}
			}
		} );
		
		projectModelBinder.bind( new BindingAdapter( ProjectModel.distRange )
		{
			@Override
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model3d != null )
				{
					LinearAxisConversion range = ( LinearAxisConversion ) model.get( ProjectModel.distRange );
					if( range != null )
					{
						float nearDist = ( float ) range.invert( 0.0 );
						float farDist = ( float ) range.invert( settingsDrawer.getDistColorationAxis( ).getViewSpan( ) );
						model3d.setNearDist( nearDist );
						model3d.setFarDist( farDist );
						canvas.display( );
					}
				}
			}
		} );
		
		projectModelBinder.bind( new BindingAdapter( ProjectModel.paramRange )
		{
			@Override
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model3d != null )
				{
					LinearAxisConversion range = ( LinearAxisConversion ) model.get( ProjectModel.paramRange );
					if( range != null )
					{
						float loParam = ( float ) range.invert( 0.0 );
						float hiParam = ( float ) range.invert( settingsDrawer.getParamColorationAxis( ).getViewSpan( ) );
						model3d.setLoParam( loParam );
						model3d.setHiParam( hiParam );
						canvas.display( );
					}
				}
			}
		} );
		
		projectModelBinder.bind( new BindingAdapter( ProjectModel.depthAxis )
		{
			@Override
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null && model3d != null )
				{
					float[ ] depthAxis = ( float[ ] ) model.get( ProjectModel.depthAxis );
					if( depthAxis != null && depthAxis.length == 3 )
					{
						model3d.setDepthAxis( depthAxis );
						canvas.display( );
					}
				}
			}
		} );
		
		settingsDrawer.getProjectFileMenuButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Component source = ( Component ) e.getSource( );
				
				JPopupMenu popupMenu = new JPopupMenu( );
				popupMenu.setLightWeightPopupEnabled( false );
				popupMenu.add( new JMenuItem( newProjectAction ) );
				popupMenu.add( new JMenuItem( openProjectAction ) );
				YamlArrayList<File> recentProjectFiles = getRootModel( ).get( RootModel.recentProjectFiles );
				if( recentProjectFiles != null && !recentProjectFiles.isEmpty( ) )
				{
					popupMenu.add( new JSeparator( ) );
					for( File file : recentProjectFiles )
					{
						popupMenu.add( new JMenuItem( new OpenRecentProjectAction( BreakoutMainView.this , file ) ) );
					}
				}
				
				popupMenu.show( source , source.getWidth( ) , source.getHeight( ) );
			}
		} );
		
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
				cameraAnimationQueue.add( new SpringOrbit( BreakoutMainView.this , center , 0f , ( float ) -Math.PI * .5f , .1f , .05f , 30 ) );
				cameraAnimationQueue.add( new AnimationViewSaver( ) );
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
				getProjectModel( ).set( ProjectModel.depthAxis , new WeightedAverageTiltAxisInferrer( ).inferTiltAxis( model3d.getOriginalShots( ) ) );
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
				getProjectModel( ).set( ProjectModel.depthAxis , new float[ ] { 0f , -1f , 0f } );
			}
		} );
		
		( ( JTextField ) surveyDrawer.filterField( ).textComponent ).addActionListener( new FitToFilteredHandler( surveyDrawer.table( ) ) );
		( ( JTextField ) quickTableFilterField.textComponent ).addActionListener( new FitToFilteredHandler( quickTable ) );
		
		rootModelBinder.bind( new BindingAdapter( RootModel.desiredNumSamples )
		{
			@Override
			public void modelToViewImpl( )
			{
				org.andork.model.Model model = getModel( );
				if( model != null )
				{
					Integer desiredNumSamples = ( Integer ) model.get( RootModel.desiredNumSamples );
					if( desiredNumSamples != null )
					{
						scene.setDesiredNumSamples( desiredNumSamples );
					}
					canvas.display( );
				}
			}
		} );
		
		scene.changeSupport( ).addPropertyChangeListener( JoglScene.INITIALIZED , new BasicPropertyChangeListener( )
		{
			public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
			{
				if( scene.isInitialized( ) )
				{
					SwingUtilities.invokeLater( new Runnable( )
					{
						public void run( )
						{
							settingsDrawer.setMaxNumSamples( scene.getMaxNumSamples( ) );
						}
					} );
				}
			}
		} );
		
		rootFile = new File( new File( ".breakout" ) , "settings.yaml" );
		rootPersister = new TaskServiceFilePersister<YamlObject<RootModel>>( ioTaskService , "Saving settings..." ,
				EDTYamlObjectStringBimapper.newInstance( RootModel.instance ) , rootFile );
		YamlObject<RootModel> rootModel = null;
		
		try
		{
			rootModel = rootPersister.load( );
		}
		catch( Exception ex )
		{
		}
		
		if( rootModel == null )
		{
			rootModel = RootModel.instance.newObject( );
		}
		
		if( rootModel.get( RootModel.currentProjectFile ) == null )
		{
			rootModel.set( RootModel.currentProjectFile , new File( new File( ".breakout" ) , "defaultProject.yaml" ) );
			rootModel.set( RootModel.desiredNumSamples , 2 );
		}
		
		setRootModel( rootModel );
		
		settingsDrawer.getExportImageButton( ).addActionListener( new ActionListener( )
		{
			public void actionPerformed( ActionEvent e )
			{
				// // scene.doLater( new ScreenshotHandler( scene ) );
				// scene.doLater( new HiResScreenshotHandler( scene ,
				// new int[ ] { 500 , 500 , 500 } , new int[ ] { 500 , 500 , 500 } , Fit.BOTH ) );
				// canvas.display( );
				
				if( screenCaptureDialog == null )
				{
					screenCaptureDialog = new ScreenCaptureDialog( SwingUtilities.getWindowAncestor( mainPanel ) , canvas.getContext( ) , i18n );
					screenCaptureDialog.setTitle( "Export Image" );
					YamlObject<ScreenCaptureDialogModel> screenCaptureDialogModel =
							BreakoutMainView.this.getProjectModel( ).get( ProjectModel.screenCaptureDialogModel );
					if( screenCaptureDialogModel == null )
					{
						screenCaptureDialogModel = ScreenCaptureDialogModel.instance.newObject( );
						screenCaptureDialogModel.set( ScreenCaptureDialogModel.outputDirectory , "screenshots" );
						screenCaptureDialogModel.set( ScreenCaptureDialogModel.fileNamePrefix , "breakout-screenshot" );
						screenCaptureDialogModel.set( ScreenCaptureDialogModel.fileNumber , 1 );
						screenCaptureDialogModel.set( ScreenCaptureDialogModel.pixelWidth , canvas.getWidth( ) );
						screenCaptureDialogModel.set( ScreenCaptureDialogModel.pixelHeight , canvas.getHeight( ) );
						screenCaptureDialogModel.set( ScreenCaptureDialogModel.resolution , new BigDecimal( 300 ) );
						screenCaptureDialogModel.set( ScreenCaptureDialogModel.resolutionUnit , ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_IN );
						BreakoutMainView.this.getProjectModel( ).set( ProjectModel.screenCaptureDialogModel , screenCaptureDialogModel );
					}
					Binder<YamlObject<ScreenCaptureDialogModel>> screenCaptureBinder = projectModelBinder.subBinder( ProjectModel.screenCaptureDialogModel );
					screenCaptureDialog.setBinder( screenCaptureBinder );
					screenCaptureBinder.modelToView( );
					
					Dimension size = mainPanel.getSize( );
					size.width = size.width * 3 / 4;
					size.height = size.height * 3 / 4;
					screenCaptureDialog.setSize( size );
					screenCaptureDialog.setLocationRelativeTo( mainPanel );
				}
				
				screenCaptureDialog.setScene( scene );
				
				screenCaptureDialog.setVisible( true );
			}
		} );
		
		openProject( getRootModel( ).get( RootModel.currentProjectFile ) );
	}
	
	public File getRootFile( )
	{
		return rootFile;
	}
	
	public Binder<YamlObject<RootModel>> getRootModelBinder( )
	{
		return rootModelBinder;
	}
	
	public YamlObject<RootModel> getRootModel( )
	{
		return rootModelBinder.getModel( );
	}
	
	public void setRootModel( YamlObject<RootModel> rootModel )
	{
		YamlObject<RootModel> currentModel = getRootModel( );
		if( currentModel != rootModel )
		{
			if( currentModel != null )
			{
				currentModel.changeSupport( ).removePropertyChangeListener( rootPersister );
			}
			rootModelBinder.setModel( rootModel );
			rootModelBinder.modelToView( );
			if( rootModel != null )
			{
				rootModel.changeSupport( ).addPropertyChangeListener( rootPersister );
			}
		}
	}
	
	public YamlObject<ProjectModel> getProjectModel( )
	{
		return projectModelBinder.getModel( );
	}
	
	public I18n getI18n( )
	{
		return i18n;
	}
	
	private static GLCanvas createCanvas( )
	{
		GLProfile profile = GLProfile.get( GLProfile.GL3 );
		final GLCapabilities caps = new GLCapabilities( profile );
		GLCanvas canvas = new GLCanvas( caps );
		return canvas;
	}
	
	@Override
	protected JoglScene createScene( )
	{
		JoglScene scene = new JoglScene( );
		scene.setRenderToFbo( true );
		scene.setDesiredNumSamples( 4 );
		
		return scene;
	}
	
	protected void fitViewToSelected( )
	{
		if( getProjectModel( ).get( ProjectModel.cameraView ) != CameraView.PERSPECTIVE )
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
			SurveyShot shot = ( SurveyShot ) surveyDrawer.table( ).getModel( ).shotAtRow( i );
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
		cameraAnimationQueue.add( new AnimationViewSaver( ) );
	}
	
	protected void fitViewToEverything( )
	{
		if( getProjectModel( ).get( ProjectModel.cameraView ) != CameraView.PERSPECTIVE )
		{
			return;
		}
		
		FittingFrustum frustum = new FittingFrustum( );
		
		frustum.init( scene.pickXform( ) , .8f );
		
		for( int i = 0 ; i < surveyDrawer.table( ).getModel( ).getRowCount( ) ; i++ )
		{
			SurveyShot shot = ( SurveyShot ) surveyDrawer.table( ).getModel( ).shotAtRow( i );
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
		cameraAnimationQueue.add( new AnimationViewSaver( ) );
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
		cameraAnimationQueue.add( new AnimationViewSaver( ) );
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
				
				cameraAnimationQueue.add( new RandomOrbit( BreakoutMainView.this , center , 0.0005f ,
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
		mouseAdapterChain.addMouseAdapter( otherMouseHandler );
		mouseLooper.addMouseAdapter( mouseAdapterChain );
		
		scene.setProjectionCalculator( orthoCalculator );
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
		mouseAdapterChain.addMouseAdapter( otherMouseHandler );
		mouseLooper.addMouseAdapter( mouseAdapterChain );
		
		scene.setProjectionCalculator( perspCalculator );
	}
	
	public JPanel getMainPanel( )
	{
		return mainPanel;
	}
	
	public NewProjectAction getNewProjectAction( )
	{
		return newProjectAction;
	}
	
	public void setNewProjectAction( NewProjectAction newProjectAction )
	{
		this.newProjectAction = newProjectAction;
	}
	
	public OpenProjectAction getOpenProjectAction( )
	{
		return openProjectAction;
	}
	
	public void setOpenProjectAction( OpenProjectAction openProjectAction )
	{
		this.openProjectAction = openProjectAction;
	}
	
	private void updateCenterOfOrbit( )
	{
		List<SurveyShot> origShots = model3d.getOriginalShots( );
		
		Set<Survey3dModel.Shot> newSelectedShots = model3d.getSelectedShots( );
		
		float[ ] bounds = Rectmath.voidRectf( 3 );
		float[ ] p = Rectmath.voidRectf( 3 );
		
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
				model3d.pickShots( origin , direction , ( float ) Math.PI / 64 , spc , pickResults );
				
				PickResult<Shot> best = null;
				
				for( PickResult<Shot> result : pickResults )
				{
					if( best == null || result.lateralDistance * best.distance < best.lateralDistance * result.distance ||
							( result.lateralDistance == 0 && best.lateralDistance == 0 && result.distance < best.distance ) )
					{
						best = result;
					}
				}
				
				return ( ShotPickResult ) best;
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
				
				if( picked != null )
				{
					LinearAxisConversion conversion = getProjectModel( ).get( ProjectModel.highlightRange );
					LinearAxisConversion conversion2 = new LinearAxisConversion( conversion.invert( 0.0 ) , 1.0 , conversion.invert( settingsDrawer.getGlowDistAxis( ).getViewSpan( ) ) , 0.0 );
					editor.hover( picked.picked , picked.locationAlongShot , conversion2 );
				}
				else
				{
					editor.unhover( );
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
				for( int i = e.getFirstIndex( ) ; i <= e.getLastIndex( ) && i < surveyDrawer.table( ).getModel( ).getRowCount( ) ; i++ )
				{
					SurveyShot shot = ( SurveyShot ) surveyDrawer.table( ).getModel( ).shotAtRow( i );
					if( shot == null )
					{
						continue;
					}
					if( selModel.isSelectedIndex( i ) )
					{
						editor.select( shots.get( shot.number ) );
					}
					else
					{
						editor.deselect( shots.get( shot.number ) );
					}
				}
			}
			
			editor.commit( );
			
			updateCenterOfOrbit( );
			
			canvas.display( );
		}
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
	
	private class AnimationViewSaver implements Animation
	{
		@Override
		public long animate( long animTime )
		{
			saveViewXform( );
			return 0;
		}
	}
	
	private void saveViewXform( )
	{
		float[ ] viewXform = Vecmath.newMat4f( );
		scene.getViewXform( viewXform );
		getProjectModel( ).set( ProjectModel.viewXform , viewXform );
	}
	
	private void replaceNulls( YamlObject<ProjectModel> projectModel , File projectFile )
	{
		if( projectModel.get( ProjectModel.cameraView ) == null )
		{
			projectModel.set( ProjectModel.cameraView , CameraView.PERSPECTIVE );
		}
		if( projectModel.get( ProjectModel.filterType ) == null )
		{
			projectModel.set( ProjectModel.filterType , FilterType.ALPHA_DESIGNATION );
		}
		if( projectModel.get( ProjectModel.backgroundColor ) == null )
		{
			projectModel.set( ProjectModel.backgroundColor , Color.black );
		}
		if( projectModel.get( ProjectModel.distRange ) == null )
		{
			projectModel.set( ProjectModel.distRange , new LinearAxisConversion( 0 , 0 , 20000 , 200 ) );
		}
		if( projectModel.get( ProjectModel.viewXform ) == null )
		{
			projectModel.set( ProjectModel.viewXform , Vecmath.newMat4f( ) );
		}
		if( projectModel.get( ProjectModel.paramRange ) == null )
		{
			projectModel.set( ProjectModel.paramRange , new LinearAxisConversion( 0 , 0 , 500 , 200 ) );
		}
		if( projectModel.get( ProjectModel.highlightRange ) == null )
		{
			projectModel.set( ProjectModel.highlightRange , new LinearAxisConversion( 0 , 0 , 1000 , 200 ) );
		}
		if( projectModel.get( ProjectModel.surveyDrawer ) == null )
		{
			projectModel.set( ProjectModel.surveyDrawer , DrawerModel.instance.newObject( ) );
		}
		if( projectModel.get( ProjectModel.settingsDrawer ) == null )
		{
			projectModel.set( ProjectModel.settingsDrawer , DrawerModel.instance.newObject( ) );
		}
		if( projectModel.get( ProjectModel.miniSurveyDrawer ) == null )
		{
			projectModel.set( ProjectModel.miniSurveyDrawer , DrawerModel.instance.newObject( ) );
		}
		if( projectModel.get( ProjectModel.taskListDrawer ) == null )
		{
			projectModel.set( ProjectModel.taskListDrawer , DrawerModel.instance.newObject( ) );
		}
		if( projectModel.get( ProjectModel.surveyFile ) == null )
		{
			projectModel.set( ProjectModel.surveyFile , new File( NewProjectAction.pickDefaultSurveyFile( projectFile ).getName( ) ) );
		}
	}
	
	class OtherMouseHandler extends MouseAdapter
	{
		@Override
		public void mousePressed( MouseEvent e )
		{
			cameraAnimationQueue.clear( );
		}
		
		@Override
		public void mouseReleased( MouseEvent e )
		{
			saveViewXform( );
		}
		
		@Override
		public void mouseWheelMoved( MouseWheelEvent e )
		{
			saveViewXform( );
		}
	}
	
	class SurveyFilterFactory implements RowFilterFactory<String, SurveyTableModel, Integer>
	{
		@Override
		public RowFilter<SurveyTableModel, Integer> createFilter( String input )
		{
			switch( getProjectModel( ).get( ProjectModel.filterType ) )
			{
				case ALPHA_DESIGNATION:
					return new SurveyDesignationFilter( input );
				case REGEXP:
					return new SurveyRegexFilter( input );
				case SURVEYORS:
					return new SurveyorFilter( input );
				case DESCRIPTION:
					return new DescriptionFilter( input );
				default:
					return null;
			}
		}
	}
	
	class SurveyTableChangeHandler extends TaskServiceBatcher<TableModelEvent> implements TableModelListener
	{
		private boolean	persistOnUpdate		= true;
		private boolean	rebuildViewOnUpdate	= true;
		
		public SurveyTableChangeHandler( TaskService taskService )
		{
			super( taskService , true );
		}
		
		public boolean isPersistOnUpdate( )
		{
			return persistOnUpdate;
		}
		
		public void setPersistOnUpdate( boolean persistOnUpdate )
		{
			this.persistOnUpdate = persistOnUpdate;
		}
		
		public boolean isRebuildViewOnUpdate( )
		{
			return rebuildViewOnUpdate;
		}
		
		public void setRebuildViewOnUpdate( boolean rebuildViewOnUpdate )
		{
			this.rebuildViewOnUpdate = rebuildViewOnUpdate;
		}
		
		@Override
		public void tableChanged( TableModelEvent e )
		{
			if( persistOnUpdate && surveyPersister != null )
			{
				surveyPersister.saveLater( ( SurveyTableModel ) e.getSource( ) );
			}
			if( rebuildViewOnUpdate )
			{
				add( e );
			}
		}
		
		@Override
		public BatcherTask<TableModelEvent> createTask( final LinkedList<TableModelEvent> batch )
		{
			BatcherTask<TableModelEvent> task = new BatcherTask<TableModelEvent>( "Updating view" )
			{
				@Override
				protected void execute( )
				{
					setTotal( 1000 );
					Subtask copySubtask = new Subtask( this );
					copySubtask.setStatus( "Parsing shot data" );
					copySubtask.setIndeterminate( false );
					
					SurveyTableModel copy = new SurveyTableModel( );
					SurveyTableModelCopier copier = new SurveyTableModelCopier( );
					
					SurveyTableModel model = new FromEDT<SurveyTableModel>( )
					{
						@Override
						public SurveyTableModel run( ) throws Throwable
						{
							return surveyDrawer.table( ).getModel( );
						}
					}.result( );
					
					copier.copyInBackground( model , copy , 1000 , copySubtask );
					
					if( copySubtask.isCanceling( ) )
					{
						return;
					}
					
					copySubtask.end( );
					
					Subtask parsingSubtask = new Subtask( this );
					parsingSubtask.setStatus( "Parsing shot data" );
					parsingSubtask.setIndeterminate( false );
					
					final List<SurveyShot> shots = copy.createShots( parsingSubtask );
					
					if( parsingSubtask.isCanceling( ) )
					{
						return;
					}
					
					new OnEDT( )
					{
						@Override
						public void run( ) throws Throwable
						{
							surveyDrawer.table( ).getModel( ).setShots( shots );
						}
					};
					
					final List<SurveyShot> nonNullShots = new ArrayList<SurveyShot>( );
					
					if( !shots.isEmpty( ) )
					{
						Subtask calculatingSubtask = new Subtask( this );
						calculatingSubtask.setStatus( "calculating" );
						calculatingSubtask.setIndeterminate( true );
						
						LinkedHashSet<SurveyStation> stations = new LinkedHashSet<SurveyStation>( );
						
						for( SurveyShot shot : shots )
						{
							if( shot != null )
							{
								nonNullShots.add( shot );
								stations.add( shot.from );
								stations.add( shot.to );
							}
						}
						
						SurveyShot.computeConnected( stations );
						
						calculatingSubtask.end( );
					}
					
					updateModel( nonNullShots );
				}
				
				public boolean isCancelable( )
				{
					return true;
				}
				
				public void updateModel( List<SurveyShot> shots )
				{
					setStatus( "Updating view..." );
					
					new DoSwing( )
					{
						@Override
						public void run( )
						{
							if( model3d != null )
							{
								scene.remove( model3d );
								scene.disposeLater( model3d );
								model3d = null;
							}
						}
					};
					
					setStatus( "Updating view: constructing new model..." );
					
					final Survey3dModel model = Survey3dModel.create( shots , 10 , 3 , 3 , this );
					if( isCanceling( ) )
					{
						return;
					}
					
					setStatus( "Updating view: installing new model..." );
					
					new DoSwing( )
					{
						@Override
						public void run( )
						{
							BreakoutMainView.this.model3d = model;
							model.setParamPaint( settingsDrawer.getParamColorationAxisPaint( ) );
							scene.add( model );
							scene.initLater( model );
							
							// GlyphCache cache = new GlyphCache( scene , new Font( "Arial" , Font.PLAIN , 72 ) , 1024 , 1024 ,
							// new BufferedImageIntFactory( BufferedImage.TYPE_INT_ARGB ) , new OutlinedGlyphPagePainter(
							// new BasicStroke( 3f , BasicStroke.CAP_ROUND , BasicStroke.JOIN_ROUND ) ,
							// Color.BLACK , Color.WHITE ) );
							//
							// JoglText text = new JoglText.Builder( )
							// .ascent( 0 , cache.fontMetrics.getAscent( ) , 0 ).baseline( cache.fontMetrics.getAscent( ) , 0 , 0 )
							// .add( "This is a test" , cache , 1f , 1f , 1f , 1f ).create( scene );
							//
							// text.use( );
							// scene.add( text );
							
							projectModelBinder.modelToView( );
							
							float[ ] center = new float[ 3 ];
							Rectmath.center( model.getTree( ).getRoot( ).mbr( ) , center );
							orbiter.setCenter( center );
							
							canvas.repaint( );
						}
					};
				}
			};
			return task;
		}
	}
	
	public void openProject( File newProjectFile )
	{
		ioTaskService.submit( new OpenProjectTask( newProjectFile ) );
	}
	
	private class OpenProjectTask extends SelfReportingTask
	{
		boolean	taskListWasOpen;
		File	newProjectFile;
		
		private OpenProjectTask( File newProjectFile )
		{
			super( getMainPanel( ) );
			this.newProjectFile = newProjectFile;
			setStatus( "Saving current project..." );
			setIndeterminate( true );
			
			taskListWasOpen = taskListDrawer.delegate( ).isOpen( );
			taskListDrawer.delegate( ).open( );
			showDialogLater( );
		}
		
		@Override
		protected void duringDialog( ) throws Exception
		{
			setStatus( "Opening project: " + newProjectFile + "..." );
			
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					taskListDrawer.delegate( ).setOpen( taskListWasOpen );
					
					YamlObject<RootModel> rootModel = getRootModel( );
					rootModel.set( RootModel.currentProjectFile , newProjectFile );
					YamlArrayList<File> recentProjectFiles = rootModel.get( RootModel.recentProjectFiles );
					if( recentProjectFiles == null )
					{
						recentProjectFiles = YamlArrayList.newInstance( CompoundBimapper.compose( FileStringBimapper.instance , StringObjectBimapper.instance ) );
						rootModel.set( RootModel.recentProjectFiles , recentProjectFiles );
					}
					
					recentProjectFiles.remove( newProjectFile );
					while( recentProjectFiles.size( ) > 20 )
					{
						recentProjectFiles.remove( recentProjectFiles.size( ) - 1 );
					}
					recentProjectFiles.add( 0 , newProjectFile );
					
					if( getProjectModel( ) != null && projectPersister != null )
					{
						getProjectModel( ).changeSupport( ).removePropertyChangeListener( projectPersister );
					}
					projectPersister = new TaskServiceFilePersister<YamlObject<ProjectModel>>( ioTaskService , "Saving project..." ,
							EDTYamlObjectStringBimapper.newInstance( ProjectModel.instance ) , newProjectFile );
				}
			};
			YamlObject<ProjectModel> projectModel = null;
			
			try
			{
				projectModel = projectPersister.load( );
				
				if( projectModel == null )
				{
					projectModel = ProjectModel.instance.newObject( );
				}
				replaceNulls( projectModel , newProjectFile );
			}
			catch( final Exception ex )
			{
				ex.printStackTrace( );
				new OnEDT( )
				{
					@Override
					public void run( ) throws Throwable
					{
						JOptionPane.showMessageDialog( getMainPanel( ) ,
								ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
								"Failed to load project" ,
								JOptionPane.ERROR_MESSAGE );
					}
				};
				return;
			}
			
			final YamlObject<ProjectModel> finalProjectModel = projectModel;
			
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					finalProjectModel.changeSupport( ).addPropertyChangeListener( projectPersister );
					projectModelBinder.setModel( finalProjectModel );
					projectModelBinder.modelToView( );
				}
			};
			
			openSurveyFile( finalProjectModel.get( ProjectModel.surveyFile ) );
		}
	}
	
	public void openSurveyFile( File newSurveyFile )
	{
		ioTaskService.submit( new OpenSurveyTask( newSurveyFile ) );
	}
	
	private class OpenSurveyTask extends SelfReportingTask
	{
		boolean	taskListWasOpen;
		File	newSurveyFile;
		
		private OpenSurveyTask( File newSurveyFile )
		{
			super( getMainPanel( ) );
			this.newSurveyFile = newSurveyFile;
			setStatus( "Saving current survey..." );
			setIndeterminate( true );
			
			taskListWasOpen = taskListDrawer.delegate( ).isOpen( );
			taskListDrawer.delegate( ).open( );
			showDialogLater( );
		}
		
		@Override
		protected void duringDialog( ) throws Exception
		{
			final File absoluteSurveyFile;
			if( newSurveyFile.isAbsolute( ) )
			{
				absoluteSurveyFile = newSurveyFile;
			}
			else
			{
				absoluteSurveyFile = new File( getRootModel( ).get( RootModel.currentProjectFile ).getParentFile( ) , newSurveyFile.getPath( ) );
			}
			
			boolean changed = new FromEDT<Boolean>( )
			{
				@Override
				public Boolean run( ) throws Throwable
				{
					taskListDrawer.delegate( ).setOpen( taskListWasOpen );
					
					if( surveyPersister == null || !absoluteSurveyFile.equals( surveyPersister.getFile( ) ) )
					{
						surveyPersister = new TaskServiceSubtaskFilePersister<SurveyTableModel>( ioTaskService , "Saving survey..." ,
								new SubtaskStreamBimapperFactory<SurveyTableModel, SubtaskStreamBimapper<SurveyTableModel>>( )
								{
									@Override
									public SubtaskStreamBimapper<SurveyTableModel> createSubtaskStreamBimapper( Subtask subtask )
									{
										return new SurveyTableModelStreamBimapper( subtask );
									}
								} , absoluteSurveyFile );
					}
					else
					{
						return false;
					}
					getProjectModel( ).set( ProjectModel.surveyFile , newSurveyFile );
					
					try
					{
						surveyTableChangeHandler.setPersistOnUpdate( false );
						surveyDrawer.table( ).getModel( ).clear( );
					}
					finally
					{
						surveyTableChangeHandler.setPersistOnUpdate( true );
					}
					return true;
				}
			}.result( );
			
			if( !changed )
			{
				return;
			}
			
			setStatus( "Opening survey: " + absoluteSurveyFile + "..." );
			
			SurveyTableModel surveyModel;
			
			try
			{
				surveyModel = surveyPersister.load( null );
			}
			catch( final Exception ex )
			{
				ex.printStackTrace( );
				new OnEDT( )
				{
					@Override
					public void run( ) throws Throwable
					{
						JOptionPane.showConfirmDialog( getMainPanel( ) ,
								ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
								"Failed to load survey" ,
								JOptionPane.ERROR_MESSAGE );
					}
				};
				
				return;
			}
			
			final SurveyTableModel finalSurveyModel = surveyModel;
			
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					if( finalSurveyModel != null && finalSurveyModel.getRowCount( ) > 0 )
					{
						try
						{
							surveyTableChangeHandler.setPersistOnUpdate( false );
							surveyDrawer.table( ).getModel( ).copyRowsFrom( finalSurveyModel , 0 ,
									finalSurveyModel.getRowCount( ) - 1 , 0 );
						}
						finally
						{
							surveyTableChangeHandler.setPersistOnUpdate( true );
						}
					}
				}
			};
		}
	}
}

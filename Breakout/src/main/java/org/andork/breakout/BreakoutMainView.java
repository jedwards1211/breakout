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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
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
import org.andork.awt.I18n.Localizer;
import org.andork.awt.anim.Animation;
import org.andork.awt.anim.AnimationQueue;
import org.andork.awt.event.MouseAdapterChain;
import org.andork.awt.event.MouseAdapterWrapper;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.DrawerAutoshowController;
import org.andork.awt.layout.DrawerModel;
import org.andork.awt.layout.Side;
import org.andork.bind.Binder;
import org.andork.bind.BinderWrapper;
import org.andork.bind.DefaultBinder;
import org.andork.bind.QMapKeyedBinder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.breakout.SettingsDrawer.CameraView;
import org.andork.breakout.SettingsDrawer.FilterType;
import org.andork.breakout.model.ColorParam;
import org.andork.breakout.model.ProjectArchiveModel;
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
import org.andork.jogl.BasicJOGLObject;
import org.andork.jogl.OnJogl;
import org.andork.jogl.OrthoProjectionCalculator;
import org.andork.jogl.PerspectiveProjectionCalculator;
import org.andork.jogl.awt.anim.RandomOrbit;
import org.andork.jogl.awt.anim.SinusoidalTranslation;
import org.andork.jogl.awt.anim.SpringOrbit;
import org.andork.jogl.neu.JoglScene;
import org.andork.jogl.neu.awt.BasicJoglSetup;
import org.andork.math3d.FittingFrustum;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.q.QArrayList;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QMap;
import org.andork.q.QObject;
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
	I18n												i18n						= new I18n( );
	
	PerspectiveProjectionCalculator						perspCalculator				= new PerspectiveProjectionCalculator( ( float ) Math.PI / 2 , 1f , 1e7f );
	OrthoProjectionCalculator							orthoCalculator				= new OrthoProjectionCalculator( -1 , 1 , -1 , 1 , -10000 , 10000 );
	DefaultNavigator									navigator;
	
	TaskService											rebuildTaskService;
	TaskService											sortTaskService;
	TaskService											ioTaskService;
	
	SurveyTableChangeHandler							surveyTableChangeHandler;
	
	final double[ ]										fromLoc						= new double[ 3 ];
	final double[ ]										toLoc						= new double[ 3 ];
	final double[ ]										toToLoc						= new double[ 3 ];
	final double[ ]										leftAtTo					= new double[ 3 ];
	final double[ ]										leftAtTo2					= new double[ 3 ];
	final double[ ]										leftAtFrom					= new double[ 3 ];
	
	PlotAxis											xaxis;
	PlotAxis											yaxis;
	AxisLinkButton										axisLinkButton;
	Plot												plot;
	JPanel												plotPanel;
	JPanel												mainPanel;
	JLayeredPane										layeredPane;
	
	MouseAdapterWrapper									canvasMouseAdapterWrapper;
	
	// normal mouse mode
	MouseLooper											mouseLooper;
	MouseAdapterChain									mouseAdapterChain;
	PlotController										plotController;
	MousePickHandler									pickHandler;
	DrawerAutoshowController							autoshowController;
	OtherMouseHandler									otherMouseHandler;
	
	WindowSelectionMouseHandler							windowSelectionMouseHandler;
	
	TableSelectionHandler								selectionHandler;
	
	SurveyFilterFactory									surveyFilterFactory			= new SurveyFilterFactory( );
	
	SurveyDrawer										surveyDrawer;
	Drawer												quickTableDrawer;
	TaskListDrawer										taskListDrawer;
	SettingsDrawer										settingsDrawer;
	
	Survey3dModel										model3d;
	float[ ]											v							= newMat4f( );
	
	int													debugMbrCount				= 0;
	List<BasicJOGLObject>								debugMbrs					= new ArrayList<BasicJOGLObject>( );
	
	ShotPickContext										spc							= new ShotPickContext( );
	
	final LinePlaneIntersection3f						lpx							= new LinePlaneIntersection3f( );
	final float[ ]										p0							= new float[ 3 ];
	final float[ ]										p1							= new float[ 3 ];
	final float[ ]										p2							= new float[ 3 ];
	
	File												rootFile;
	TaskServiceFilePersister<QObject<RootModel>>		rootPersister;
	final Binder<QObject<RootModel>>					rootModelBinder				= new DefaultBinder<QObject<RootModel>>( );
	
	final Binder<QObject<ProjectModel>>					projectModelBinder			= new DefaultBinder<QObject<ProjectModel>>( );
	Binder<ColorParam>									colorParamBinder			= QObjectAttributeBinder.bind( ProjectModel.colorParam , projectModelBinder );
	Binder<QMap<ColorParam, LinearAxisConversion, ?>>	paramRangesBinder			= QObjectAttributeBinder.bind( ProjectModel.paramRanges , projectModelBinder );
	Binder<LinearAxisConversion>						paramRangeBinder			= QMapKeyedBinder.bindKeyed( colorParamBinder , paramRangesBinder );
	TaskServiceFilePersister<QObject<ProjectModel>>		projectPersister;
	
	SubtaskFilePersister<SurveyTableModel>				surveyPersister;
	
	final AnimationQueue								cameraAnimationQueue		= new AnimationQueue( );
	
	NewProjectAction									newProjectAction			= new NewProjectAction( this );
	OpenProjectAction									openProjectAction			= new OpenProjectAction( this );
	ImportProjectArchiveAction							importProjectArchiveAction	= new ImportProjectArchiveAction( this );
	ExportProjectArchiveAction							exportProjectArchiveAction	= new ExportProjectArchiveAction( this );
	ExportImageAction									exportImageAction			= new ExportImageAction( this );
	
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
		
		canvasMouseAdapterWrapper = new MouseAdapterWrapper( );
		canvas.addMouseListener( canvasMouseAdapterWrapper );
		canvas.addMouseMotionListener( canvasMouseAdapterWrapper );
		canvas.addMouseWheelListener( canvasMouseAdapterWrapper );
		
		mouseLooper = new MouseLooper( );
		windowSelectionMouseHandler = new WindowSelectionMouseHandler(
				new WindowSelectionMouseHandler.Context( )
				{
					@Override
					public Survey3dModel getSurvey3dModel( )
					{
						return model3d;
					}
					
					@Override
					public JoglScene getScene( )
					{
						return scene;
					}
					
					@Override
					public GLCanvas getCanvas( )
					{
						return canvas;
					}
					
					@Override
					public void endSelection( )
					{
						canvasMouseAdapterWrapper.setWrapped( mouseLooper );
					}
					
					@Override
					public TaskService getRebuildTaskService( )
					{
						return rebuildTaskService;
					}
					
					@Override
					public void selectShots( Set<Shot> newSelected , boolean add , boolean toggle )
					{
						OnEDT.onEDT( ( ) -> {
							ListSelectionModel selModel = surveyDrawer.table( ).getModelSelectionModel( );
							SurveyTableModel model = surveyDrawer.table( ).getModel( );
							
							selModel.setValueIsAdjusting( true );
							if( !add && !toggle )
							{
								selModel.clearSelection( );
							}
							for( Shot shot : newSelected )
							{
								int row = model.rowOfShot( shot.getNumber( ) );
								if( toggle && selModel.isSelectedIndex( row ) )
								{
									selModel.removeSelectionInterval( row , row );
								}
								else
								{
									selModel.addSelectionInterval( row , row );
								}
							}
							selModel.setValueIsAdjusting( false );
						} );
					}
				} );
		canvasMouseAdapterWrapper.setWrapped( mouseLooper );
		
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
		
		quickTableDrawer = new Drawer( quickTablePanel );
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
		
		surveyDrawer.setBinder( QObjectAttributeBinder.bind( ProjectModel.surveyDrawer , projectModelBinder ) );
		settingsDrawer.setBinder( QObjectAttributeBinder.bind( ProjectModel.settingsDrawer , projectModelBinder ) );
		taskListDrawer.setBinder( QObjectAttributeBinder.bind( ProjectModel.taskListDrawer , projectModelBinder ) );
		
		new BinderWrapper<float[ ]>( )
		{
			protected void onValueChanged( float[ ] newValue )
			{
				if( newValue != null )
				{
					scene.setViewXform( newValue );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.viewXform , projectModelBinder ) );
		
		new BinderWrapper<CameraView>( )
		{
			protected void onValueChanged( CameraView newValue )
			{
				if( newValue != null )
				{
					setCameraView( newValue );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.cameraView , projectModelBinder ) );
		
		new BinderWrapper<Color>( )
		{
			protected void onValueChanged( Color bgColor )
			{
				if( bgColor != null )
				{
					scene.setBgColor( bgColor.getRed( ) / 255f , bgColor.getGreen( ) / 255f , bgColor.getBlue( ) / 255f , 1f );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.backgroundColor , projectModelBinder ) );
		
		new BinderWrapper<Integer>( )
		{
			protected void onValueChanged( Integer newValue )
			{
				if( newValue != null )
				{
					float sensitivity = newValue / 20f;
					orbiter.setSensitivity( sensitivity );
					navigator.setSensitivity( sensitivity );
				}
			}
		}.bind( QObjectAttributeBinder.bind( RootModel.mouseSensitivity , rootModelBinder ) );
		
		new BinderWrapper<Integer>( )
		{
			protected void onValueChanged( Integer newValue )
			{
				if( newValue != null )
				{
					float sensitivity = newValue / 5f;
					navigator.setWheelFactor( sensitivity );
				}
			}
		}.bind( QObjectAttributeBinder.bind( RootModel.mouseWheelSensitivity , rootModelBinder ) );
		
		new BinderWrapper<Float>( )
		{
			protected void onValueChanged( final Float newValue )
			{
				if( model3d != null && newValue != null )
				{
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					new OnJogl( getCanvas( ) )
					{
						@Override
						public void run( GLAutoDrawable drawable ) throws Throwable
						{
							model3d.setAmbientLight( newValue );
						}
					};
					getCanvas( ).repaint( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.ambientLight , projectModelBinder ) );
		
		new BinderWrapper<LinearAxisConversion>( )
		{
			protected void onValueChanged( LinearAxisConversion range )
			{
				if( model3d != null && range != null )
				{
					final float nearDist = ( float ) range.invert( 0.0 );
					final float farDist = ( float ) range.invert( settingsDrawer.getDistColorationAxis( ).getViewSpan( ) );
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					new OnJogl( getCanvas( ) )
					{
						@Override
						public void run( GLAutoDrawable drawable ) throws Throwable
						{
							model3d.setNearDist( nearDist );
							model3d.setFarDist( farDist );
						}
					};
					getCanvas( ).repaint( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.distRange , projectModelBinder ) );
		
		new BinderWrapper<LinearAxisConversion>( )
		{
			protected void onValueChanged( LinearAxisConversion range )
			{
				if( model3d != null && range != null )
				{
					final float loParam = ( float ) range.invert( 0.0 );
					final float hiParam = ( float ) range.invert( settingsDrawer.getParamColorationAxis( ).getViewSpan( ) );
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					new OnJogl( getCanvas( ) )
					{
						@Override
						public void run( GLAutoDrawable drawable ) throws Throwable
						{
							model3d.setLoParam( loParam );
							model3d.setHiParam( hiParam );
						}
					};
					canvas.repaint( );
				}
			}
		}.bind( paramRangeBinder );
		
		new BinderWrapper<float[ ]>( )
		{
			protected void onValueChanged( float[ ] depthAxis )
			{
				if( depthAxis == null )
				{
					return;
				}
				final float[ ] finalDepthAxis = Arrays.copyOf( depthAxis , depthAxis.length );
				if( model3d != null && depthAxis != null && depthAxis.length == 3 )
				{
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					new OnJogl( getCanvas( ) )
					{
						@Override
						public void run( GLAutoDrawable drawable ) throws Throwable
						{
							model3d.setDepthAxis( finalDepthAxis );
						}
					};
					canvas.display( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.depthAxis , projectModelBinder ) );
		
		new BinderWrapper<ColorParam>( )
		{
			protected void onValueChanged( final ColorParam colorParam )
			{
				if( colorParam != null && model3d != null )
				{
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					Task task = new Task( )
					{
						@Override
						protected void execute( ) throws Exception
						{
							Subtask rootSubtask = new Subtask( this );
							rootSubtask.setTotal( 1 );
							rootSubtask.setIndeterminate( false );
							
							Subtask subtask = rootSubtask.beginSubtask( 1 );
							subtask.setIndeterminate( false );
							subtask.setStatus( "Recoloring" );
							model3d.setColorParamInBackground( colorParam , subtask , getCanvas( ) );
							rootSubtask.setCompleted( 1 );
							subtask.end( );
						}
					};
					task.setTotal( 1000 );
					rebuildTaskService.submit( task );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.colorParam , projectModelBinder ) );
		
		settingsDrawer.getProjectFileMenuButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Component source = ( Component ) e.getSource( );
				
				Localizer localizer = i18n.forClass( BreakoutMainView.class );
				
				JPopupMenu popupMenu = new JPopupMenu( );
				popupMenu.setLightWeightPopupEnabled( false );
				popupMenu.add( new JMenuItem( newProjectAction ) );
				popupMenu.add( new JMenuItem( openProjectAction ) );
				popupMenu.add( new JSeparator( ) );
				JMenu importMenu = new JMenu( );
				localizer.setText( importMenu , "importMenu.text" );
				importMenu.add( new JMenuItem( importProjectArchiveAction ) );
				popupMenu.add( importMenu );
				JMenu exportMenu = new JMenu( );
				localizer.setText( exportMenu , "exportMenu.text" );
				exportMenu.add( new JMenuItem( exportProjectArchiveAction ) );
				exportMenu.add( new JMenuItem( exportImageAction ) );
				popupMenu.add( exportMenu );
				
				QArrayList<File> recentProjectFiles = getRootModel( ).get( RootModel.recentProjectFiles );
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
		
		settingsDrawer.getFitParamColorationAxisButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}
				
				final Survey3dModel model3d = BreakoutMainView.this.model3d;
				
				rebuildTaskService.submit( new Task( )
				{
					@Override
					protected void execute( ) throws Exception
					{
						setTotal( 1000 );
						Subtask rootSubtask = new Subtask( this );
						rootSubtask.setTotal( 1 );
						Subtask calcSubtask = rootSubtask.beginSubtask( 1 );
						float[ ] range = model3d.calcAutofitParamRangeInBackground( calcSubtask , getCanvas( ) );
						rootSubtask.setCompleted( 1 );
						calcSubtask.end( );
						
						if( range != null )
						{
							ColorParam colorParam = getProjectModel( ).get( ProjectModel.colorParam );
							if( !colorParam.isLoBright( ) )
							{
								float swap = range[ 0 ];
								range[ 0 ] = range[ 1 ];
								range[ 1 ] = swap;
							}
							LinearAxisConversion conversion = new LinearAxisConversion(
									range[ 0 ] , 0.0 , range[ 1 ] , settingsDrawer.getParamColorationAxis( ).getViewSpan( ) );
							
							paramRangeBinder.set( conversion );
						}
					}
				} );
			}
		} );
		
		settingsDrawer.getFlipParamColorationAxisButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				PlotAxis axis = settingsDrawer.getParamColorationAxis( );
				LinearAxisConversion conversion = axis.getAxisConversion( );
				double start = conversion.invert( 0.0 );
				double end = conversion.invert( axis.getViewSpan( ) );
				LinearAxisConversion newConversion = new LinearAxisConversion( end , 0.0 , start , axis.getViewSpan( ) );
				paramRangeBinder.set( newConversion );
			}
		} );
		
		settingsDrawer.getRecalcColorByDistanceButton( ).addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}
				final Survey3dModel model3d = BreakoutMainView.this.model3d;
				rebuildTaskService.submit( new Task( )
				{
					@Override
					protected void execute( ) throws Exception
					{
						setTotal( 1000 );
						Subtask rootSubtask = new Subtask( this );
						rootSubtask.setTotal( 1 );
						Subtask calcSubtask = rootSubtask.beginSubtask( 1 );
						model3d.calcDistFromSelectInBackground( calcSubtask , getCanvas( ) );
						rootSubtask.setCompleted( 1 );
						calcSubtask.end( );
					}
				} );
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
				getProjectModel( ).set( ProjectModel.depthAxis , new float[ ] { 0f , -1f , 0f } );
			}
		} );
		
		( ( JTextField ) surveyDrawer.filterField( ).textComponent ).addActionListener( new FitToFilteredHandler( surveyDrawer.table( ) ) );
		( ( JTextField ) quickTableFilterField.textComponent ).addActionListener( new FitToFilteredHandler( quickTable ) );
		
		new BinderWrapper<Integer>( )
		{
			protected void onValueChanged( Integer desiredNumSamples )
			{
				if( desiredNumSamples != null )
				{
					scene.setDesiredNumSamples( desiredNumSamples );
					canvas.display( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( RootModel.desiredNumSamples , rootModelBinder ) );
		
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
		rootPersister = new TaskServiceFilePersister<QObject<RootModel>>( ioTaskService , "Saving settings..." ,
				QObjectBimappers.defaultBimapper( RootModel.defaultMapper ) , rootFile );
		QObject<RootModel> rootModel = null;
		
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
			rootModel.set( RootModel.currentProjectFile , new File( new File( ".breakout" ) , "defaultProject.bop" ) );
			rootModel.set( RootModel.desiredNumSamples , 2 );
		}
		
		setRootModel( rootModel );
		
		openProject( getRootModel( ).get( RootModel.currentProjectFile ) );
	}
	
	public File getRootFile( )
	{
		return rootFile;
	}
	
	public Binder<QObject<RootModel>> getRootModelBinder( )
	{
		return rootModelBinder;
	}
	
	public QObject<RootModel> getRootModel( )
	{
		return rootModelBinder.get( );
	}
	
	public void setRootModel( QObject<RootModel> rootModel )
	{
		QObject<RootModel> currentModel = getRootModel( );
		if( currentModel != rootModel )
		{
			if( currentModel != null )
			{
				currentModel.changeSupport( ).removePropertyChangeListener( rootPersister );
			}
			rootModelBinder.set( rootModel );
			if( rootModel != null )
			{
				rootModel.changeSupport( ).addPropertyChangeListener( rootPersister );
			}
		}
	}
	
	public Binder<QObject<ProjectModel>> getProjectModelBinder( )
	{
		return projectModelBinder;
	}
	
	public QObject<ProjectModel> getProjectModel( )
	{
		return projectModelBinder.get( );
	}
	
	public I18n getI18n( )
	{
		return i18n;
	}
	
	private static GLCanvas createCanvas( )
	{
		GLProfile profile = GLProfile.getMaximum( true );
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
				if( model3d == null )
				{
					return 0;
				}
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
		if( model3d == null )
		{
			return;
		}
		List<SurveyShot> origShots = model3d.getOriginalShots( );
		
		Set<Survey3dModel.Shot> newSelectedShots = model3d.getSelectedShots( );
		
		float[ ] bounds = Rectmath.voidRectf( 3 );
		float[ ] p = Rectmath.voidRectf( 3 );
		
		for( Survey3dModel.Shot shot : newSelectedShots )
		{
			SurveyShot origShot = origShots.get( shot.getNumber( ) );
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
	
	private static ShotPickContext	hoverUpdaterSpc	= new ShotPickContext( );
	
	private class HoverUpdater extends Task
	{
		Survey3dModel	model3d;
		MouseEvent		e;
		
		public HoverUpdater( Survey3dModel model3d , MouseEvent e )
		{
			super( "Updating mouseover glow..." );
			this.model3d = model3d;
			this.e = e;
		}
		
		public boolean isCancelable( )
		{
			return true;
		}
		
		@Override
		protected void execute( ) throws Exception
		{
			final ShotPickResult picked = pick( model3d , e , hoverUpdaterSpc );
			
			Subtask subtask = new Subtask( this );
			Subtask glowSubtask = subtask.beginSubtask( 1 );
			glowSubtask.setStatus( "Updating mouseover glow" );
			
			if( picked != null )
			{
				LinearAxisConversion conversion = new FromEDT<LinearAxisConversion>( )
				{
					@Override
					public LinearAxisConversion run( ) throws Throwable
					{
						LinearAxisConversion conversion = getProjectModel( ).get( ProjectModel.highlightRange );
						LinearAxisConversion conversion2 = new LinearAxisConversion( conversion.invert( 0.0 ) , 1.0 , conversion.invert( settingsDrawer.getGlowDistAxis( ).getViewSpan( ) ) , 0.0 );
						return conversion2;
					}
				}.result( );
				
				model3d.updateGlowInBackground( picked.picked , picked.locationAlongShot , conversion , glowSubtask );
			}
			else
			{
				model3d.updateGlowInBackground( null , null , null , glowSubtask );
			}
			if( !isCanceling( ) )
			{
				canvas.display( );
			}
		}
	}
	
	private ShotPickResult pick( Survey3dModel model3d , MouseEvent e , ShotPickContext spc )
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
	
	private class MousePickHandler extends MouseAdapter
	{
		@Override
		public void mouseMoved( MouseEvent e )
		{
			if( model3d != null )
			{
				HoverUpdater updater = new HoverUpdater( model3d , e );
				for( Task task : rebuildTaskService.getTasks( ) )
				{
					if( task instanceof HoverUpdater )
					{
						task.cancel( );
					}
				}
				rebuildTaskService.submit( updater );
			}
		}
		
		@Override
		public void mousePressed( MouseEvent e )
		{
			if( e.getButton( ) != MouseEvent.BUTTON1 )
			{
				return;
			}
			
			if( ( e.getModifiersEx( ) & MouseEvent.ALT_DOWN_MASK ) == MouseEvent.ALT_DOWN_MASK )
			{
				for( Drawer drawer : Arrays.asList( surveyDrawer , quickTableDrawer , taskListDrawer , settingsDrawer ) )
				{
					if( !drawer.delegate( ).isPinned( ) )
					{
						drawer.delegate( ).close( );
					}
				}
				canvasMouseAdapterWrapper.setWrapped( windowSelectionMouseHandler );
				windowSelectionMouseHandler.start( e );
				return;
			}
			
			ShotPickResult picked = pick( model3d , e , spc );
			
			if( picked == null )
			{
				return;
			}
			
			ListSelectionModel selModel = surveyDrawer.table( ).getModelSelectionModel( );
			
			int index = picked.picked.getNumber( );
			
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
			
			final SelectionEditor editor = model3d.editSelection( );
			
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
			
			rebuildTaskService.submit( new Task( )
			{
				@Override
				protected void execute( ) throws Exception
				{
					editor.commit( );
					
					new OnEDT( )
					{
						@Override
						public void run( ) throws Throwable
						{
							updateCenterOfOrbit( );
						}
					};
					
					canvas.display( );
				}
			} );
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
	
	private void replaceNulls( QObject<ProjectModel> projectModel , File projectFile )
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
		if( projectModel.get( ProjectModel.colorParam ) == null )
		{
			projectModel.set( ProjectModel.colorParam , ColorParam.DEPTH );
		}
		if( projectModel.get( ProjectModel.paramRanges ) == null )
		{
			projectModel.set( ProjectModel.paramRanges , QLinkedHashMap.<ColorParam,LinearAxisConversion>newInstance( ) );
		}
		QMap<ColorParam, LinearAxisConversion, ?> paramRanges = projectModel.get( ProjectModel.paramRanges );
		for( ColorParam colorParam : ColorParam.values( ) )
		{
			if( !paramRanges.containsKey( colorParam ) )
			{
				paramRanges.put( colorParam , new LinearAxisConversion( ) );
			}
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
							
							projectModelBinder.update( true );
							
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
	
	public void importProjectArchive( File newProjectFile )
	{
		ioTaskService.submit( new ImportProjectArchiveTask( newProjectFile ) );
	}
	
	private class ImportProjectArchiveTask extends SelfReportingTask
	{
		boolean	taskListWasOpen;
		File	newProjectFile;
		
		private ImportProjectArchiveTask( File newProjectFile )
		{
			super( getMainPanel( ) );
			this.newProjectFile = newProjectFile;
			setStatus( "Saving project..." );
			setIndeterminate( true );
			
			taskListWasOpen = taskListDrawer.delegate( ).isOpen( );
			taskListDrawer.delegate( ).open( );
			showDialogLater( );
		}
		
		@Override
		protected void duringDialog( ) throws Exception
		{
			setStatus( "Importing project archive: " + newProjectFile + "..." );
			
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					taskListDrawer.delegate( ).setOpen( taskListWasOpen );
				}
			};
			
			ProjectArchiveModel projectModel = null;
			
			Subtask rootSubtask = new Subtask( this );
			
			try
			{
				projectModel = new ProjectArchiveModelStreamBimapper( getI18n( ) , rootSubtask )
						.read( new FileInputStream( newProjectFile ) );
				
				if( projectModel == null )
				{
					return;
				}
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
								ex.getClass( ).getName( ) + ": " + ex.getLocalizedMessage( ) ,
								"Failed to import project archive" ,
								JOptionPane.ERROR_MESSAGE );
					}
				};
				return;
			}
			
			final ProjectArchiveModel finalProjectModel = projectModel;
			
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					finalProjectModel.getProjectModel( ).set( ProjectModel.surveyFile , getProjectModel( ).get( ProjectModel.surveyFile ) );
					if( projectPersister != null )
					{
						if( getProjectModel( ) != null )
						{
							getProjectModel( ).changeSupport( ).removePropertyChangeListener( projectPersister );
						}
						finalProjectModel.getProjectModel( ).changeSupport( ).addPropertyChangeListener( projectPersister );
						projectPersister.saveLater( finalProjectModel.getProjectModel( ) );
					}
					projectModelBinder.set( finalProjectModel.getProjectModel( ) );
					
					surveyDrawer.table( ).getModel( ).copyRowsFrom( finalProjectModel.getSurveyTableModel( ) ,
							0 , finalProjectModel.getSurveyTableModel( ).getRowCount( ) - 1 , 0 );
				}
			};
		}
	}
	
	public void exportProjectArchive( File newProjectFile )
	{
		ioTaskService.submit( new ExportProjectArchiveTask( newProjectFile ) );
	}
	
	private class ExportProjectArchiveTask extends SelfReportingTask
	{
		boolean	taskListWasOpen;
		File	newProjectFile;
		
		private ExportProjectArchiveTask( File newProjectFile )
		{
			super( getMainPanel( ) );
			this.newProjectFile = newProjectFile;
			setStatus( "Saving project..." );
			setIndeterminate( true );
			
			taskListWasOpen = taskListDrawer.delegate( ).isOpen( );
			taskListDrawer.delegate( ).open( );
			showDialogLater( );
		}
		
		@Override
		protected void duringDialog( ) throws Exception
		{
			setStatus( "Exporting project archive: " + newProjectFile + "..." );
			
			setTotal( 1000 );
			
			Subtask rootSubtask = new Subtask( this );
			rootSubtask.setTotal( 3 );
			Subtask prepareSubtask = rootSubtask.beginSubtask( 1 );
			prepareSubtask.setStatus( "Preparing for export" );
			
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					taskListDrawer.delegate( ).setOpen( taskListWasOpen );
				}
			};
			
			SurveyTableModel surveyTableModelCopy = new SurveyTableModel( );
			SurveyTableModelCopier copier = new SurveyTableModelCopier( );
			copier.copyInBackground( surveyDrawer.table( ).getModel( ) , surveyTableModelCopy , 1000 , prepareSubtask );
			
			ProjectArchiveModel projectModel = new ProjectArchiveModel( getProjectModel( ) , surveyTableModelCopy );
			
			prepareSubtask.end( );
			rootSubtask.setCompleted( prepareSubtask.getProportion( ) );
			
			Subtask exportSubtask = rootSubtask.beginSubtask( 2 );
			exportSubtask.setStatus( "Exporting project to " + newProjectFile );
			
			try
			{
				new ProjectArchiveModelStreamBimapper( getI18n( ) , exportSubtask )
						.write( projectModel , new FileOutputStream( newProjectFile ) );
				exportSubtask.end( );
				rootSubtask.setCompleted( rootSubtask.getCompleted( ) + exportSubtask.getProportion( ) );
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
								ex.getClass( ).getName( ) + ": " + ex.getLocalizedMessage( ) ,
								"Failed to export project archive" ,
								JOptionPane.ERROR_MESSAGE );
					}
				};
				return;
			}
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
					
					QObject<RootModel> rootModel = getRootModel( );
					rootModel.set( RootModel.currentProjectFile , newProjectFile );
					QArrayList<File> recentProjectFiles = rootModel.get( RootModel.recentProjectFiles );
					if( recentProjectFiles == null )
					{
						recentProjectFiles = QArrayList.newInstance( );
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
					projectPersister = new TaskServiceFilePersister<QObject<ProjectModel>>( ioTaskService , "Saving project..." ,
							QObjectBimappers.defaultBimapper( ProjectModel.defaultMapper ) , newProjectFile );
				}
			};
			QObject<ProjectModel> projectModel = null;
			
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
			
			final QObject<ProjectModel> finalProjectModel = projectModel;
			
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					finalProjectModel.changeSupport( ).addPropertyChangeListener( projectPersister );
					projectModelBinder.set( finalProjectModel );
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

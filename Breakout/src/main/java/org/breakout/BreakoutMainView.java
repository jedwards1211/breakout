/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.breakout;

import static org.andork.math3d.Vecmath.newMat4f;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3;
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
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.anim.Animation;
import org.andork.awt.anim.AnimationQueue;
import org.andork.awt.event.MouseAdapterChain;
import org.andork.awt.event.MouseAdapterWrapper;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.DrawerAutoshowController;
import org.andork.awt.layout.DrawerModel;
import org.andork.awt.layout.Side;
import org.andork.awt.layout.SideConstraint;
import org.andork.awt.layout.SideConstraintLayoutDelegate;
import org.andork.bind.Binder;
import org.andork.bind.BinderWrapper;
import org.andork.bind.DefaultBinder;
import org.andork.bind.QMapKeyedBinder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.collect.CollectionUtils;
import org.andork.func.FloatUnaryOperator;
import org.andork.jogl.AutoClipOrthoProjection;
import org.andork.jogl.DefaultJoglRenderer;
import org.andork.jogl.GL3Framebuffer;
import org.andork.jogl.InterpolationProjection;
import org.andork.jogl.JoglBackgroundColor;
import org.andork.jogl.JoglScene;
import org.andork.jogl.JoglViewSettings;
import org.andork.jogl.JoglViewState;
import org.andork.jogl.PerspectiveProjection;
import org.andork.jogl.Projection;
import org.andork.jogl.awt.JoglOrbiter;
import org.andork.jogl.awt.JoglOrthoNavigator;
import org.andork.jogl.awt.anim.GeneralViewXformOrbitAnimation;
import org.andork.jogl.awt.anim.ProjXformAnimation;
import org.andork.jogl.awt.anim.RandomViewOrbitAnimation;
import org.andork.jogl.awt.anim.SpringViewOrbitAnimation;
import org.andork.jogl.awt.anim.ViewXformAnimation;
import org.andork.jogl.old.BasicJOGLObject;
import org.andork.math.misc.Fitting;
import org.andork.math3d.Fitting3d;
import org.andork.math3d.FittingFrustum;
import org.andork.math3d.LineLineIntersection2d;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.PickXform;
import org.andork.math3d.PlanarHull3f;
import org.andork.math3d.Vecmath;
import org.andork.q.QArrayList;
import org.andork.q.QLinkedHashMap;
import org.andork.q.QMap;
import org.andork.q.QObject;
import org.andork.spatial.Rectmath;
import org.andork.swing.AnnotatingRowSorter;
import org.andork.swing.FromEDT;
import org.andork.swing.OnEDT;
import org.andork.swing.async.DrawerPinningTask;
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
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingJTables;
import org.andork.swing.table.RowFilterFactory;
import org.breakout.StatsModel.MinAvgMax;
import org.breakout.model.ColorParam;
import org.breakout.model.ProjectArchiveModel;
import org.breakout.model.ProjectModel;
import org.breakout.model.RootModel;
import org.breakout.model.Shot;
import org.breakout.model.Station;
import org.breakout.model.Survey3dModel;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.TransparentTerrain;
import org.breakout.model.Survey3dModel.SelectionEditor;
import org.breakout.model.Survey3dModel.Shot3d;
import org.breakout.model.Survey3dModel.Shot3dPickContext;
import org.breakout.model.Survey3dModel.Shot3dPickResult;
import org.breakout.model.SurveyTableModel.SurveyTableModelCopier;
import org.breakout.update.UpdateStatusPanelController;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.MouseLooper;
import com.andork.plot.PlotAxis;

public class BreakoutMainView
{
	GLAutoDrawable										autoDrawable;
	GLCanvas											canvas;
	JoglScene											scene;
	JoglBackgroundColor									bgColor;
	DefaultJoglRenderer									renderer;

	DefaultNavigator									navigator;
	JoglOrbiter											orbiter;
	JoglOrthoNavigator									orthoNavigator;

	I18n												i18n						= new I18n( );

	PerspectiveProjection								perspCalculator				= new PerspectiveProjection(
																						( float ) Math.PI / 2 , 1f ,
																						1e7f );

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

	JPanel												mainPanel;
	JLayeredPane										layeredPane;

	MouseAdapterWrapper									canvasMouseAdapterWrapper;

	// normal mouse mode
	MouseLooper											mouseLooper;
	MouseAdapterChain									mouseAdapterChain;
	MousePickHandler									pickHandler;
	DrawerAutoshowController							autoshowController;
	OtherMouseHandler									otherMouseHandler;

	WindowSelectionMouseHandler							windowSelectionMouseHandler;

	TableSelectionHandler								selectionHandler;

	RowFilterFactory<String, TableModel, Integer>		rowFilterFactory;

	SurveyDrawer										surveyDrawer;
	MiniSurveyDrawer									miniSurveyDrawer;
	TaskListDrawer										taskListDrawer;
	SettingsDrawer										settingsDrawer;

	Survey3dModel										model3d;
	float[ ]											v							= newMat4f( );

	int													debugMbrCount				= 0;
	List<BasicJOGLObject>								debugMbrs					= new ArrayList<BasicJOGLObject>( );

	Shot3dPickContext									spc							= new Shot3dPickContext( );

	final LinePlaneIntersection3f						lpx							= new LinePlaneIntersection3f( );
	final float[ ]										p0							= new float[ 3 ];
	final float[ ]										p1							= new float[ 3 ];
	final float[ ]										p2							= new float[ 3 ];

	File												rootFile;
	Path												rootDirectory;
	TaskServiceFilePersister<QObject<RootModel>>		rootPersister;
	final Binder<QObject<RootModel>>					rootModelBinder				= new DefaultBinder<QObject<RootModel>>( );

	final Binder<QObject<ProjectModel>>					projectModelBinder			= new DefaultBinder<QObject<ProjectModel>>( );
	Binder<ColorParam>									colorParamBinder			= QObjectAttributeBinder.bind(
																						ProjectModel.colorParam ,
																						projectModelBinder );
	Binder<QMap<ColorParam, LinearAxisConversion, ?>>	paramRangesBinder			= QObjectAttributeBinder.bind(
																						ProjectModel.paramRanges ,
																						projectModelBinder );
	Binder<LinearAxisConversion>						paramRangeBinder			= QMapKeyedBinder.bindKeyed(
																						colorParamBinder ,
																						paramRangesBinder );
	TaskServiceFilePersister<QObject<ProjectModel>>		projectPersister;

	SubtaskFilePersister<SurveyTableModel>				surveyPersister;

	final AnimationQueue								cameraAnimationQueue		= new AnimationQueue( );

	NewProjectAction									newProjectAction			= new NewProjectAction( this );
	OpenProjectAction									openProjectAction			= new OpenProjectAction( this );
	ImportProjectArchiveAction							importProjectArchiveAction	= new ImportProjectArchiveAction(
																						this );
	ExportProjectArchiveAction							exportProjectArchiveAction	= new ExportProjectArchiveAction(
																						this );
	ExportImageAction									exportImageAction			= new ExportImageAction( this );

	final WeakHashMap<Animation, Object>				protectedAnimations			= new WeakHashMap<>( );

	JLabel												hintLabel;

	public BreakoutMainView( )
	{
		final GLProfile glp = GLProfile.get( GLProfile.GL2ES2 );
		final GLCapabilities caps = new GLCapabilities( glp );
		autoDrawable = canvas = new GLCanvas( caps );
		autoDrawable.display( );

		scene = new JoglScene( );
		bgColor = new JoglBackgroundColor( );
		scene.add( bgColor );

		renderer = new DefaultJoglRenderer( scene , new GL3Framebuffer( ) , 1 );

		autoDrawable.addGLEventListener( renderer );

		navigator = new DefaultNavigator( autoDrawable , renderer.getViewSettings( ) );
		navigator.setMoveFactor( 5f );
		navigator.setWheelFactor( 5f );

		orbiter = new JoglOrbiter( autoDrawable , renderer.getViewSettings( ) );
		orthoNavigator = new JoglOrthoNavigator( autoDrawable , renderer.getViewState( ) , renderer.getViewSettings( ) );

		ioTaskService = new SingleThreadedTaskService( );
		rebuildTaskService = new SingleThreadedTaskService( );
		sortTaskService = new SingleThreadedTaskService( );

		JLabel highlightLabel = new JLabel( "Highlight: " );
		JLabel filterLabel = new JLabel( "Filter: " );

		hintLabel = new JLabel( "A" );
		hintLabel.setForeground( Color.WHITE );
		hintLabel.setBackground( Color.BLACK );
		hintLabel.setOpaque( true );
		Font hintFont = hintLabel.getFont( );
		hintLabel.setFont( hintFont.deriveFont( Font.PLAIN ).deriveFont( hintFont.getSize2D( ) + 3f ) );
		hintLabel.setPreferredSize( new Dimension( 200 , hintLabel.getPreferredSize( ).height ) );
		hintLabel.setText( " " );
		hintLabel.setVerticalAlignment( JLabel.TOP );

		final Consumer<Runnable> sortRunner = r ->
		{
			Task task = new Task( "Sorting survey table..." ) {
				@Override
				protected void execute( )
				{
					r.run( );
				}
			};

			sortTaskService.submit( task );
		};

		OnEDT.onEDT( ( ) ->
		{
			surveyDrawer = new SurveyDrawer( sortRunner );

			rowFilterFactory = new MultiRowFilterFactory( new SurveyTableFilterMap( surveyDrawer.table( ) ) );

			surveyDrawer.filterField( ).textComponent.getDocument( ).addDocumentListener(
				AnnotatingJTables.createFilterFieldListener( surveyDrawer.table( ) ,
					surveyDrawer.filterField( ).textComponent , rowFilterFactory ) );
			surveyDrawer.highlightField( ).textComponent.getDocument( ).addDocumentListener(
				AnnotatingJTables.createHighlightFieldListener( surveyDrawer.table( ) ,
					surveyDrawer.highlightField( ).textComponent , rowFilterFactory , Color.YELLOW ) );
		} );

		Color darkColor = new Color( 255 * 3 / 10 , 255 * 3 / 10 , 255 * 3 / 10 );

		pickHandler = new MousePickHandler( );

		canvasMouseAdapterWrapper = new MouseAdapterWrapper( );
		canvas.addMouseListener( canvasMouseAdapterWrapper );
		canvas.addMouseMotionListener( canvasMouseAdapterWrapper );
		canvas.addMouseWheelListener( canvasMouseAdapterWrapper );

		mouseLooper = new MouseLooper( );
		windowSelectionMouseHandler = new WindowSelectionMouseHandler( new WindowSelectionMouseHandler.Context( ) {
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

			public JoglViewState getViewState( )
			{
				return renderer.getViewState( );
			}

			@Override
			public GLAutoDrawable getDrawable( )
			{
				return autoDrawable;
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
			public void selectShots( Set<Shot3d> newSelected , boolean add , boolean toggle )
			{
				OnEDT.onEDT( ( ) ->
				{
					ListSelectionModel selModel = surveyDrawer.table( ).getModelSelectionModel( );
					SurveyTableModel model = surveyDrawer.table( ).getModel( );

					selModel.setValueIsAdjusting( true );
					if( !add && !toggle )
					{
						selModel.clearSelection( );
					}
					for( Shot3d shot3d : newSelected )
					{
						int row = model.rowOfShot( shot3d.getNumber( ) );
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

//		glWindow.addMouseListener( new NEWT2AWTMouseEventConverter( canvas , canvasMouseAdapterWrapper ) );

		autoshowController = new DrawerAutoshowController( );

		otherMouseHandler = new OtherMouseHandler( );

		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
		mouseAdapterChain.addMouseAdapter( otherMouseHandler );

		layeredPane = new JLayeredPane( );
		layeredPane.setLayout( new DelegatingLayoutManager( ) {
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

		layeredPane.add( canvas );
		surveyDrawer.addTo( layeredPane , 5 );

		mainPanel = new JPanel( new BorderLayout( ) );
		mainPanel.add( layeredPane , BorderLayout.CENTER );

		selectionHandler = new TableSelectionHandler( );
		surveyDrawer.table( ).getModelSelectionModel( ).addListSelectionListener( selectionHandler );

		OnEDT.onEDT( ( ) ->
		{
			miniSurveyDrawer = new MiniSurveyDrawer( i18n , sortRunner );

			miniSurveyDrawer.table( ).setModel( surveyDrawer.table( ).getModel( ) );
			miniSurveyDrawer.table( ).setModelSelectionModel( surveyDrawer.table( ).getModelSelectionModel( ) );

			miniSurveyDrawer.filterField( ).textComponent.getDocument( ).addDocumentListener(
				AnnotatingJTables.createFilterFieldListener( miniSurveyDrawer.table( ) ,
					miniSurveyDrawer.filterField( ).textComponent , rowFilterFactory ) );
			miniSurveyDrawer.highlightField( ).textComponent.getDocument( ).addDocumentListener(
				AnnotatingJTables.createHighlightFieldListener( miniSurveyDrawer.table( ) ,
					miniSurveyDrawer.highlightField( ).textComponent , rowFilterFactory , Color.YELLOW ) );

			miniSurveyDrawer.delegate( ).dockingSide( Side.LEFT );
			miniSurveyDrawer.mainResizeHandle( );
			miniSurveyDrawer.addTo( layeredPane , 3 );

			miniSurveyDrawer.delegate( )
				.putExtraConstraint( Side.BOTTOM , new SideConstraint( surveyDrawer , Side.TOP , 0 ) );
		} );

		settingsDrawer.delegate( ).putExtraConstraint( Side.BOTTOM , new SideConstraint( surveyDrawer , Side.TOP , 0 ) );

		taskListDrawer.delegate( ).putExtraConstraint( Side.LEFT ,
			new SideConstraint( miniSurveyDrawer , Side.RIGHT , 0 ) );
		taskListDrawer.delegate( ).putExtraConstraint( Side.RIGHT ,
			new SideConstraint( settingsDrawer , Side.LEFT , 0 ) );

		SideConstraintLayoutDelegate spinnerDelegate = new SideConstraintLayoutDelegate( );
		spinnerDelegate.putExtraConstraint(
			Side.LEFT , new SideConstraint( miniSurveyDrawer , Side.RIGHT , 0 ) );
		spinnerDelegate.putExtraConstraint(
			Side.BOTTOM , new SideConstraint( surveyDrawer , Side.TOP , 0 ) );

		SideConstraintLayoutDelegate hintLabelDelegate = new SideConstraintLayoutDelegate( );
		hintLabelDelegate.putExtraConstraint(
			Side.LEFT , new SideConstraint( taskListDrawer.pinButton( ) , Side.RIGHT , 0 ) );
		hintLabelDelegate.putExtraConstraint(
			Side.RIGHT , new SideConstraint( settingsDrawer , Side.LEFT , 0 ) );
		hintLabelDelegate.putExtraConstraint(
			Side.BOTTOM , new SideConstraint( surveyDrawer , Side.TOP , 0 ) );

		layeredPane.add( taskListDrawer.pinButton( ) , spinnerDelegate );
		layeredPane.setLayer( taskListDrawer.pinButton( ) , JLayeredPane.getLayer( settingsDrawer ) );
		layeredPane.add( hintLabel , hintLabelDelegate );
		layeredPane.setLayer( hintLabel , JLayeredPane.getLayer( settingsDrawer ) );

		surveyDrawer.table( ).setTransferHandler( new SurveyTableTransferHandler( ) );

		surveyDrawer.table( ).addPropertyChangeListener( "model" , new PropertyChangeListener( ) {
			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				AnnotatingRowSorter<TableModel, Integer> sorter = ( AnnotatingRowSorter<TableModel, Integer> )
					miniSurveyDrawer.table( ).getRowSorter( );

				SurveyTableModel newModel = ( SurveyTableModel ) evt.getNewValue( );

				miniSurveyDrawer.table( ).setRowSorter( null );
				miniSurveyDrawer.table( ).setModel( newModel );
				sorter.setModel( newModel );
				miniSurveyDrawer.table( ).setRowSorter( sorter );
			}
		} );

		surveyDrawer.setBinder( QObjectAttributeBinder.bind( ProjectModel.surveyDrawer , projectModelBinder ) );
		settingsDrawer.setBinder( QObjectAttributeBinder.bind( ProjectModel.settingsDrawer , projectModelBinder ) );
		taskListDrawer.setBinder( QObjectAttributeBinder.bind( ProjectModel.taskListDrawer , projectModelBinder ) );

		new BinderWrapper<Color>( ) {
			protected void onValueChanged( Color bgColor )
			{
				if( bgColor != null )
				{
					BreakoutMainView.this.bgColor.set( bgColor.getRed( ) / 255f , bgColor.getGreen( ) / 255f ,
						bgColor.getBlue( ) / 255f , 1f );
					autoDrawable.display( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.backgroundColor , projectModelBinder ) );

		new BinderWrapper<Integer>( ) {
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

		new BinderWrapper<Integer>( ) {
			protected void onValueChanged( Integer newValue )
			{
				if( newValue != null )
				{
					float sensitivity = newValue / 5f;
					navigator.setWheelFactor( sensitivity );
				}
			}
		}.bind( QObjectAttributeBinder.bind( RootModel.mouseWheelSensitivity , rootModelBinder ) );

		new BinderWrapper<Float>( ) {
			protected void onValueChanged( final Float newValue )
			{
				if( model3d != null && newValue != null )
				{
					model3d.setAmbientLight( newValue );
					autoDrawable.display( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.ambientLight , projectModelBinder ) );

		new BinderWrapper<LinearAxisConversion>( ) {
			protected void onValueChanged( LinearAxisConversion range )
			{
				if( model3d != null && range != null )
				{
					final float nearDist = ( float ) range.invert( 0.0 );
					final float farDist = ( float ) range
						.invert( settingsDrawer.getDistColorationAxis( ).getViewSpan( ) );
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					model3d.setNearDist( nearDist );
					model3d.setFarDist( farDist );
					autoDrawable.display( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.distRange , projectModelBinder ) );

		new BinderWrapper<LinearAxisConversion>( ) {
			protected void onValueChanged( LinearAxisConversion range )
			{
				if( model3d != null && range != null )
				{
					final float loParam = ( float ) range.invert( 0.0 );
					final float hiParam = ( float ) range.invert( settingsDrawer.getParamColorationAxis( )
						.getViewSpan( ) );
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					model3d.setLoParam( loParam );
					model3d.setHiParam( hiParam );
					autoDrawable.display( );
				}
			}
		}.bind( paramRangeBinder );

		new BinderWrapper<float[ ]>( ) {
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
					model3d.setDepthAxis( finalDepthAxis );
					autoDrawable.display( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.depthAxis , projectModelBinder ) );

		new BinderWrapper<ColorParam>( ) {
			protected void onValueChanged( final ColorParam colorParam )
			{
				if( colorParam != null && model3d != null )
				{
					final Survey3dModel model3d = BreakoutMainView.this.model3d;
					Task task = new Task( ) {
						@Override
						protected void execute( ) throws Exception
						{
							Subtask rootSubtask = new Subtask( this );
							rootSubtask.setTotal( 1 );
							rootSubtask.setIndeterminate( false );

							Subtask subtask = rootSubtask.beginSubtask( 1 );
							subtask.setIndeterminate( false );
							subtask.setStatus( "Recoloring" );
							model3d.setColorParam( colorParam , subtask );
							rootSubtask.setCompleted( 1 );
							subtask.end( );

							autoDrawable.display( );
						}
					};
					task.setTotal( 1000 );
					rebuildTaskService.submit( task );
				}
			}
		}.bind( QObjectAttributeBinder.bind( ProjectModel.colorParam , projectModelBinder ) );

		settingsDrawer.getProjectFileMenuButton( ).addActionListener( new ActionListener( ) {
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

				QArrayList<Path> recentProjectFiles = getRootModel( ).get( RootModel.recentProjectFiles );
				if( recentProjectFiles != null && !recentProjectFiles.isEmpty( ) )
				{
					popupMenu.add( new JSeparator( ) );
					for( Path file : recentProjectFiles )
					{
						popupMenu.add( new JMenuItem( new OpenRecentProjectAction( BreakoutMainView.this , file ) ) );
					}
				}

				popupMenu.show( source , source.getWidth( ) , source.getHeight( ) );
			}
		} );

		settingsDrawer.getFitViewToSelectedButton( ).addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				fitViewToSelected( );
			}
		} );

		settingsDrawer.getFitViewToEverythingButton( ).addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				fitViewToEverything( );
			}
		} );

		settingsDrawer.getFitParamColorationAxisButton( ).addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}

				final Survey3dModel model3d = BreakoutMainView.this.model3d;

				rebuildTaskService.submit( new Task( ) {
					@Override
					protected void execute( ) throws Exception
					{
						setTotal( 1000 );
						Subtask rootSubtask = new Subtask( this );
						rootSubtask.setTotal( 1 );
						Subtask calcSubtask = rootSubtask.beginSubtask( 1 );
						float[ ] range = model3d.calcAutofitParamRange( getDefaultShotsForOperations( ) , calcSubtask );
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
							LinearAxisConversion conversion = new LinearAxisConversion( range[ 0 ] , 0.0 , range[ 1 ] ,
								settingsDrawer.getParamColorationAxis( ).getViewSpan( ) );

							paramRangeBinder.set( conversion );
						}
					}
				} );
			}
		} );

		settingsDrawer.getFlipParamColorationAxisButton( ).addActionListener( new ActionListener( ) {
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

		settingsDrawer.getRecalcColorByDistanceButton( ).addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}
				final Survey3dModel model3d = BreakoutMainView.this.model3d;
				rebuildTaskService.submit( new Task( ) {
					@Override
					protected void execute( ) throws Exception
					{
						setTotal( 1000 );
						Subtask rootSubtask = new Subtask( this );
						rootSubtask.setTotal( 1 );
						Subtask calcSubtask = rootSubtask.beginSubtask( 1 );
						model3d.calcDistFromShots( getDefaultShotsForOperations( ) , calcSubtask );
						autoDrawable.display( );

						rootSubtask.setCompleted( 1 );
						calcSubtask.end( );
					}
				} );
			}
		} );

		settingsDrawer.getResetViewButton( ).addActionListener( e ->
		{
			renderer.getViewSettings( ).setViewXform( newMat4f( ) );
			autoDrawable.display( );
		} );

		settingsDrawer.getOrbitToPlanButton( ).addActionListener( new ActionListener( ) {
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

				float[ ] v = newMat4f( );
				renderer.getViewSettings( ).getViewXform( v );

				removeUnprotectedCameraAnimations( );
				cameraAnimationQueue.add( new SpringViewOrbitAnimation( autoDrawable , renderer.getViewSettings( ) ,
					center , 0f , ( float ) -Math.PI * .5f , .1f , .05f , 30 ) );
				cameraAnimationQueue.add( new AnimationViewSaver( ) );
			}
		} );

		ViewButtonsPanel viewButtonsPanel = settingsDrawer.getViewButtonsPanel( );
		for( CameraView view : CameraView.values( ) )
		{
			JToggleButton button = viewButtonsPanel.getButton( view );
			if( button != null )
			{
				button.addActionListener( new ActionListener( ) {
					@Override
					public void actionPerformed( ActionEvent e )
					{
						setCameraView( view );
					}
				} );
			}
		}

		settingsDrawer.getInferDepthAxisTiltButton( ).addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( model3d == null )
				{
					return;
				}
				List<float[ ]> vectors = new ArrayList<>( );
				for( Shot3d shot3d : getDefaultShotsForOperations( ) )
				{
					SurveyTableModel tableModel = surveyDrawer.table( ).getModel( );
					Shot shot = tableModel.shotAtRow( tableModel.rowOfShot( shot3d.getNumber( ) ) );
					float[ ] vector = new float[ 3 ];
					Vecmath.sub3( shot.to.position , shot.from.position , vector );

					if( !Vecmath.hasNaNsOrInfinites( vector ) )
					{
						vectors.add( vector );
					}
				}
				float[ ] normal = Fitting3d.planeNormalLeastSquares2f( vectors.stream( ) );
				Vecmath.normalize3( normal );

				if( normal[ 1 ] > 0 )
				{
					Vecmath.negate3( normal );
				}

				getProjectModel( ).set( ProjectModel.depthAxis , normal );
			}
		} );

		settingsDrawer.getResetDepthAxisTiltButton( ).addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				getProjectModel( ).set( ProjectModel.depthAxis , new float[ ] { 0f , -1f , 0f } );
			}
		} );

		settingsDrawer.getCameraToDepthAxisTiltButton( ).addActionListener( new ActionListener( ) {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				float[ ] axis = new float[ 3 ];
				Vecmath.negate3( renderer.getViewState( ).inverseViewXform( ) , 8 , axis , 0 );
				getProjectModel( ).set( ProjectModel.depthAxis , axis );
			}
		} );

		( ( JTextField ) surveyDrawer.filterField( ).textComponent ).addActionListener( new FitToFilteredHandler(
			surveyDrawer.table( ) ) );
		( ( JTextField ) miniSurveyDrawer.filterField( ).textComponent )
			.addActionListener( new FitToFilteredHandler( miniSurveyDrawer.table( ) ) );

		new BinderWrapper<Integer>( ) {
			protected void onValueChanged( Integer desiredNumSamples )
			{
				if( desiredNumSamples != null )
				{
					renderer.setDesiredNumSamples( desiredNumSamples );
					autoDrawable.display( );
				}
			}
		}.bind( QObjectAttributeBinder.bind( RootModel.desiredNumSamples , rootModelBinder ) );

		autoDrawable.invoke( false , drawable ->
		{
			GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
			int[ ] temp = new int[ 1 ];
			( ( GL3 ) gl ).glGetIntegerv( GL3.GL_MAX_SAMPLES , temp , 0 );
			SwingUtilities.invokeLater( ( ) -> settingsDrawer.setMaxNumSamples( temp[ 0 ] ) );
			return true;
		} );

		File rootFile;
		String rootFilePath = System.getProperty( "rootFile" );

		if( rootFilePath == null )
		{
			rootFile = new File( new File( ".breakout" ) , "settings.yaml" );
		}
		else
		{
			rootFile = new File( rootFilePath );
		}

		rootDirectory = rootFile.toPath( ).getParent( );

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
			rootModel.set( RootModel.currentProjectFile , Paths.get( "defaultProject.bop" ) );
			rootModel.set( RootModel.desiredNumSamples , 2 );
		}

		setRootModel( rootModel );

		Path projectFile = rootDirectory.resolve( rootModel.get( RootModel.currentProjectFile ) )
			.normalize( );

		openProject( projectFile );

		try( FileInputStream updateIn = new FileInputStream( "update.properties" ) )
		{
			Properties updateProps = new Properties( );
			updateProps.load( updateIn );
			updateIn.close( );

			UpdateStatusPanelController updateStatusPanelController = new UpdateStatusPanelController(
				settingsDrawer.getUpdateStatusPanel( ) ,
				settingsDrawer.getLoadedVersion( ) ,
				new URL( updateProps.get( "latestVersionInfoUrl" ).toString( ) ) ,
				new File( updateProps.get( "updateDir" ).toString( ) ) );

			updateStatusPanelController.checkForUpdate( );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
		}
	}

	public File getRootFile( )
	{
		return rootFile;
	}

	public Path getRootDirectory( )
	{
		return rootDirectory;
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

	public JoglScene getScene( )
	{
		return scene;
	}

	public JoglViewSettings getViewSettings( )
	{
		return renderer.getViewSettings( );
	}

	public GLAutoDrawable getAutoDrawable( )
	{
		return autoDrawable;
	}

	public Component getCanvas( )
	{
		return canvas;
	}

	protected void fitViewToSelected( )
	{
		if( model3d == null )
		{
			return;
		}

		changeView( CollectionUtils.toHashSet( getSelectedShotsFromTable( )
			.map( shot -> model3d.getShot( shot.number ) ) ) );
	}

	protected void fitViewToEverything( )
	{
		if( model3d == null )
		{
			return;
		}

		changeView( CollectionUtils.toHashSet( getShotsFromTable( ).map( shot -> model3d.getShot( shot.number ) ) ) );
	}

	protected void flyToFiltered( final AnnotatingJTable table )
	{
		if( model3d == null )
		{
			return;
		}

		removeUnprotectedCameraAnimations( );

		if( getProjectModel( ).get( ProjectModel.cameraView ) == CameraView.PERSPECTIVE )
		{
			float[ ] center = new float[ 3 ];
			orbiter.getCenter( center );

			if( Vecmath.hasNaNsOrInfinites( center ) )
			{
				model3d.getCenter( center );
			}
			cameraAnimationQueue.add( new SpringViewOrbitAnimation( autoDrawable , renderer.getViewSettings( ) ,
				center ,
				0f , ( float ) -Math.PI / 4 , .1f , .05f , 30 ) );
			cameraAnimationQueue.add( new AnimationViewSaver( ) );
		}
		cameraAnimationQueue.add( new Animation( ) {
			@Override
			public long animate( long animTime )
			{
				table.getModelSelectionModel( ).clearSelection( );
				table.selectAll( );

				fitViewToSelected( );

				if( getProjectModel( ).get( ProjectModel.cameraView ) != CameraView.PERSPECTIVE )
				{
					return 0;
				}
				rebuildTaskService.submit( task -> SwingUtilities.invokeLater( ( ) ->
				{

					float[ ] center = new float[ 3 ];
					orbiter.getCenter( center );

					if( Vecmath.hasNaNsOrInfinites( center ) )
					{
						model3d.getCenter( center );
					}

					cameraAnimationQueue.add( new RandomViewOrbitAnimation( autoDrawable , renderer.getViewSettings( ) ,
						center , 0.0005f , ( float ) -Math.PI / 4 , ( float ) -Math.PI / 9 , 30 , 60000 ) );
				} ) );
				return 0;
			}
		} );
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
			case AUTO_PROFILE:
				autoProfileMode( );
				break;
			}
		}
	}

	public void perspectiveMode( )
	{
		float[ ] forward = new float[ 3 ];
		float[ ] right = new float[ 3 ];

		Vecmath.negate3( renderer.getViewState( ).inverseViewXform( ) , 8 , forward , 0 );
		Vecmath.getColumn3( renderer.getViewState( ).inverseViewXform( ) , 0 , right );

		changeView( forward , right , false , getDefaultShotsForOperations( ) );
	}

	public void planMode( )
	{
		changeView( new float[ ] { 0 , -1 , 0 } , new float[ ] { 1 , 0 , 0 } , true , getDefaultShotsForOperations( ) );
	}

	public void northFacingProfileMode( )
	{
		changeView( new float[ ] { 0 , 0 , -1 } , new float[ ] { 1 , 0 , 0 } , true , getDefaultShotsForOperations( ) );
	}

	public void southFacingProfileMode( )
	{
		changeView( new float[ ] { 0 , 0 , 1 } , new float[ ] { -1 , 0 , 0 } , true , getDefaultShotsForOperations( ) );
	}

	public void eastFacingProfileMode( )
	{
		changeView( new float[ ] { 1 , 0 , 0 } , new float[ ] { 0 , 0 , 1 } , true , getDefaultShotsForOperations( ) );
	}

	public void westFacingProfileMode( )
	{
		changeView( new float[ ] { -1 , 0 , 0 } , new float[ ] { 0 , 0 , -1 } , true , getDefaultShotsForOperations( ) );
	}

	public void autoProfileMode( )
	{
		Set<Shot3d> shots = getDefaultShotsForOperations( );
		List<float[ ]> forFitting = new ArrayList<>( );
		SurveyTableModel tableModel = surveyDrawer.table( ).getModel( );
		for( Shot3d shot : shots )
		{
			Shot origShot = tableModel.shotAtRow( tableModel.rowOfShot( shot.getNumber( ) ) );
			forFitting
				.add( new float[ ] { ( float ) origShot.from.position[ 0 ] , ( float ) origShot.from.position[ 2 ] } );
			forFitting.add( new float[ ] { ( float ) origShot.to.position[ 0 ] , ( float ) origShot.to.position[ 2 ] } );
		}

		float[ ] fit = Fitting.linearLeastSquares2f( forFitting );

		if( Vecmath.hasNaNsOrInfinites( fit ) )
		{
			return;
		}

		double azimuth = Math.atan2( 1 , -fit[ 0 ] );

		float[ ] right = new float[ ] { ( float ) Math.sin( azimuth ) , 0 , ( float ) -Math.cos( azimuth ) };
		float[ ] forward = new float[ ] { ( float ) Math.sin( azimuth - Math.PI * 0.5 ) , 0 ,
			( float ) -Math.cos( azimuth - Math.PI * 0.5 ) };

		if( Vecmath.dot3( renderer.getViewState( ).inverseViewXform( ) , 8 , forward , 0 ) > 0 )
		{
			Vecmath.negate3( right );
			Vecmath.negate3( forward );
		}

		changeView( forward , right , true , shots );
	}

	protected Stream<Shot> getShotsFromTable( )
	{
		SurveyTableModel model = surveyDrawer.table( ).getModel( );
		return IntStream.range( 0 , model.getRowCount( ) ).mapToObj( i -> model.shotAtRow( i ) )
			.filter( o -> o != null );
	}

	protected Stream<Shot> getSelectedShotsFromTable( )
	{
		SurveyTableModel model = surveyDrawer.table( ).getModel( );
		ListSelectionModel selModel = surveyDrawer.table( ).getModelSelectionModel( );
		return IntStream.range( 0 , model.getRowCount( ) ).filter( i -> selModel.isSelectedIndex( i ) )
			.mapToObj( i -> model.shotAtRow( i ) ).filter( o -> o != null );
	}

	protected Set<Shot3d> getDefaultShotsForOperations( )
	{
		if( model3d == null )
		{
			return Collections.emptySet( );
		}
		Set<Shot3d> result = new HashSet<Shot3d>( );
		getSelectedShotsFromTable( ).forEach( shot -> result.add( model3d.getShot( shot.number ) ) );
		if( result.size( ) < 2 )
		{
			result.clear( );
			PlanarHull3f hull = new PlanarHull3f( );
			renderer.getViewState( ).pickXform( ).exportViewVolume( hull , canvas.getWidth( ) , canvas.getHeight( ) );
			model3d.getShotsIn( hull , result );
			if( result.isEmpty( ) )
			{
				result.addAll( model3d.getShots( ) );
			}
		}

		return result;
	}

	protected void changeView( Set<Shot3d> shotsToFit )
	{
		float[ ] forward = new float[ 3 ];
		float[ ] right = new float[ 3 ];

		float[ ] vi = renderer.getViewState( ).inverseViewXform( );

		Vecmath.negate3( vi , 8 , forward , 0 );
		Vecmath.getColumn3( vi , 0 , right );

		changeView( forward , right , getProjectModel( ).get( ProjectModel.cameraView ) != CameraView.PERSPECTIVE ,
			shotsToFit );
	}

	private void changeView( float[ ] forward , float[ ] right , boolean ortho , Set<Shot3d> shotsToFit )
	{
		if( Vecmath.hasNaNsOrInfinites( forward ) || Vecmath.hasNaNsOrInfinites( right ) )
		{
			throw new IllegalArgumentException( "forward and right must not contain NaN or infinite values" );
		}

		mouseLooper.removeMouseAdapter( mouseAdapterChain );

		float[ ] up = new float[ 3 ];
		Vecmath.cross( right , forward , up );

		Projection newProjCalculator;
		float[ ] vi = renderer.getViewState( ).inverseViewXform( );
		float[ ] endLocation = { vi[ 12 ] , vi[ 13 ] , vi[ 14 ] };

		Animation finisher;

		if( ortho )
		{
			AutoClipOrthoProjection orthoCalculator = new AutoClipOrthoProjection( );
			newProjCalculator = orthoCalculator;
			orbiter.getCenter( orthoCalculator.center );

			if( model3d != null )
			{
				orthoCalculator.radius = Rectmath.radius3( model3d.getTree( ).getRoot( ).mbr( ) );
				float[ ] orthoBounds = model3d.getOrthoBounds( shotsToFit , right , up , forward );
				Rectmath.scaleFromCenter3( orthoBounds , 1 / 0.9f , 1 / 0.9f , 1f , orthoBounds );

				float[ ] endOrthoLocation = new float[ 3 ];
				Rectmath.center( orthoBounds , endOrthoLocation );

				Vecmath.combine( endLocation , endOrthoLocation , right , up , forward );

				float dist = Vecmath.distance3( vi , 12 , endLocation , 0 );
				endOrthoLocation[ 2 ] -= dist;
				Vecmath.combine( endLocation , endOrthoLocation , right , up , forward );

				Rectmath.center( orthoBounds , endOrthoLocation );
				endOrthoLocation[ 2 ] = orthoBounds[ 2 ];
				Vecmath.combine( orthoCalculator.nearClipPoint , endOrthoLocation , right , up , forward );
				endOrthoLocation[ 2 ] = orthoBounds[ 5 ];
				Vecmath.combine( orthoCalculator.farClipPoint , endOrthoLocation , right , up , forward );

				orthoCalculator.hSpan = ( orthoBounds[ 3 ] - orthoBounds[ 0 ] );
				orthoCalculator.vSpan = ( orthoBounds[ 4 ] - orthoBounds[ 1 ] );
			}

			finisher = l ->
			{
				orthoCalculator.useNearClipPoint = orthoCalculator.useFarClipPoint = true;
				renderer.getViewSettings( ).setProjection( orthoCalculator );
				saveProjection( );

				installOrthoMouseAdapters( );

				autoDrawable.display( );
				return 0;
			};
		}
		else
		{
			newProjCalculator = perspCalculator;

			if( model3d != null )
			{
				FittingFrustum frustum = new FittingFrustum( );
				float[ ] projXform = newMat4f( );
				perspCalculator.calculate( renderer.getViewState( ) , projXform );
				PickXform pickXform = new PickXform( );
				pickXform.calculate( projXform , renderer.getViewState( ).viewXform( ) );
				frustum.init( pickXform , 0.9f );

				for( Shot3d shot : shotsToFit )
				{
					for( float[ ] coord : shot.coordIterable( endLocation ) )
					{
						frustum.addPoint( coord );
					}
				}

				frustum.calculateOrigin( endLocation );
			}

			finisher = l ->
			{
				renderer.getViewSettings( ).setProjection( perspCalculator );
				saveProjection( );

				installPerspectiveMouseAdapters( );

				autoDrawable.display( );
				return 0;
			};
		}

		GeneralViewXformOrbitAnimation viewAnimation = new GeneralViewXformOrbitAnimation( autoDrawable ,
			renderer.getViewSettings( ) , 1750 , 30 );
		float[ ] viewXform = newMat4f( );
		viewAnimation.setUpWithEndLocation( renderer.getViewState( ).viewXform( ) , endLocation , forward , right );

		Projection currentProjCalculator = renderer.getViewSettings( ).getProjection( );

		InterpolationProjection calc = new InterpolationProjection( renderer.getViewSettings( ).getProjection( ) ,
			newProjCalculator , 0f );

		FloatUnaryOperator viewReparam = f -> 1 - ( 1 - f ) * ( 1 - f );
		FloatUnaryOperator projReparam;
		if( currentProjCalculator instanceof AutoClipOrthoProjection )
		{
			AutoClipOrthoProjection currentOrthoCalc = ( AutoClipOrthoProjection ) currentProjCalculator;
			currentOrthoCalc.useNearClipPoint = currentOrthoCalc.useFarClipPoint = false;
			if( ortho )
			{
				projReparam = viewReparam;
			}
			else
			{
				float b = 10f;
				float a = 1 / b;
				float ra = 1 / a / a;
				float rb = 1 / b / b;
				projReparam = f ->
				{
					float ff = b + f * ( a - b );
					float rf = 1 / ff / ff;
					return viewReparam.applyAsFloat( ( rf - rb ) / ( ra - rb ) );
				};
			}
		}
		else
		{
			if( ortho )
			{
				float b = 10f;
				float a = 1 / b;
				float ra = 1 / a / a;
				float rb = 1 / b / b;
				projReparam = f ->
				{
					float ff = a + viewReparam.applyAsFloat( f ) * ( b - a );
					float rf = 1 / ff / ff;
					return ( rf - ra ) / ( rb - ra );
				};
			}
			else
			{
				projReparam = viewReparam;
			}
		}

		removeUnprotectedCameraAnimations( );
		cameraAnimationQueue.add( new ProjXformAnimation( autoDrawable , renderer.getViewSettings( ) , 1750 , false ,
			f ->
			{
				calc.f = projReparam.applyAsFloat( f );
				return calc;
			} ).also( new ViewXformAnimation( autoDrawable , renderer.getViewSettings( ) , 1750 , true , f ->
		{
			viewAnimation.calcViewXform( viewReparam.applyAsFloat( f ) , viewXform );
			return viewXform;
		} ) ) );
		finisher = finisher.also( new AnimationViewSaver( ) );
		protectedAnimations.put( finisher , null );
		cameraAnimationQueue.add( finisher );
	}

	private void installPerspectiveMouseAdapters( )
	{
		if( mouseAdapterChain != null )
		{
			mouseLooper.removeMouseAdapter( mouseAdapterChain );
		}
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( navigator );
		mouseAdapterChain.addMouseAdapter( orbiter );
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
		mouseAdapterChain.addMouseAdapter( otherMouseHandler );
		mouseLooper.addMouseAdapter( mouseAdapterChain );
	}

	private void installOrthoMouseAdapters( )
	{
		if( mouseAdapterChain != null )
		{
			mouseLooper.removeMouseAdapter( mouseAdapterChain );
		}
		mouseAdapterChain = new MouseAdapterChain( );
		mouseAdapterChain.addMouseAdapter( orthoNavigator );
		mouseAdapterChain.addMouseAdapter( pickHandler );
		mouseAdapterChain.addMouseAdapter( autoshowController );
		mouseAdapterChain.addMouseAdapter( otherMouseHandler );
		mouseLooper.addMouseAdapter( mouseAdapterChain );
	}

	protected void removeUnprotectedCameraAnimations( )
	{
		cameraAnimationQueue.removeAll( anim -> !protectedAnimations.containsKey( anim ) );
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

	private static Shot3dPickContext	hoverUpdaterSpc	= new Shot3dPickContext( );

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
			final Shot3dPickResult picked = pick( model3d , e , hoverUpdaterSpc );

			Subtask subtask = new Subtask( this );
			Subtask glowSubtask = subtask.beginSubtask( 1 );
			glowSubtask.setStatus( "Updating mouseover glow" );

			if( picked != null )
			{
				LinearAxisConversion conversion = new FromEDT<LinearAxisConversion>( ) {
					@Override
					public LinearAxisConversion run( ) throws Throwable
					{
						Shot shot = model3d.getOriginalShots( ).get( picked.picked.getNumber( ) );
						hintLabel.setText( String.format(
							"<html>Stations: <b>%s - %s</b>&emsp;Dist: <b>%.2f</b>&emsp;Azm: <b>%.2f</b>"
								+ "&emsp;Inc: <b>%.2f</b>&emsp;<i>%s</i></html>" , shot.from.name , shot.to.name ,
							shot.dist , shot.azm , shot.inc , shot.desc ) );

						LinearAxisConversion conversion = getProjectModel( ).get( ProjectModel.highlightRange );
						LinearAxisConversion conversion2 = new LinearAxisConversion( conversion.invert( 0.0 ) , 1.0 ,
							conversion.invert( settingsDrawer.getGlowDistAxis( ).getViewSpan( ) ) , 0.0 );
						return conversion2;
					}
				}.result( );

				model3d.updateGlow( picked.picked , picked.locationAlongShot , conversion , glowSubtask );
			}
			else
			{
				OnEDT.onEDT( ( ) -> hintLabel.setText( " " ) );
				model3d.updateGlow( null , null , null , glowSubtask );
			}
			if( !isCanceling( ) )
			{
				autoDrawable.display( );
			}
		}
	}

	private final PlanarHull3f	hull	= new PlanarHull3f( );

	private Shot3dPickResult pick( Survey3dModel model3d , MouseEvent e , Shot3dPickContext spc )
	{
		PlanarHull3f hull = new PlanarHull3f( );
		float[ ] origin = new float[ 3 ];
		float[ ] direction = new float[ 3 ];
		renderer
			.getViewState( )
			.pickXform( )
			.xform( e.getX( ) , e.getComponent( ).getHeight( ) - e.getY( ) , e.getComponent( ).getWidth( ) ,
				e.getComponent( ).getHeight( ) , origin , direction );
		renderer.getViewState( ).pickXform( ).exportViewVolume( hull , e , 10 );

		if( model3d != null )
		{
			List<PickResult<Shot3d>> pickResults = new ArrayList<PickResult<Shot3d>>( );
			//			model3d.pickShots( origin , direction , ( float ) Math.PI / 64 , spc , pickResults );
			model3d.pickShots( hull , spc , pickResults );

			PickResult<Shot3d> best = null;

			for( PickResult<Shot3d> result : pickResults )
			{
				if( best == null || result.lateralDistance * best.distance < best.lateralDistance * result.distance
					|| ( result.lateralDistance == 0 && best.lateralDistance == 0 && result.distance < best.distance ) )
				{
					best = result;
				}
			}

			return ( Shot3dPickResult ) best;
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
		public void mouseClicked( MouseEvent e )
		{
			if( e.getButton( ) != MouseEvent.BUTTON1 || e.isAltDown( ) )
			{
				return;
			}

			Shot3dPickResult picked = pick( model3d , e , spc );

			if( picked == null )
			{
				surveyDrawer.table( ).clearSelection( );
			}
		}

		@Override
		public void mousePressed( MouseEvent e )
		{
			if( e.getButton( ) != MouseEvent.BUTTON1 )
			{
				return;
			}

			if( e.isAltDown( ) )
			{
				for( Drawer drawer : Arrays.asList( surveyDrawer , miniSurveyDrawer , taskListDrawer , settingsDrawer ) )
				{
					drawer.holder( ).release( DrawerAutoshowController.autoshowDrawerHolder );
				}
				canvasMouseAdapterWrapper.setWrapped( windowSelectionMouseHandler );
				windowSelectionMouseHandler.start( e );
				return;
			}

			Shot3dPickResult picked = pick( model3d , e , spc );

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

			autoDrawable.display( );
		}
	}

	private class MinAvgMaxCalc
	{
		int		count	= 0;
		double	total	= 0.0;
		double	min		= Double.NaN;
		double	max		= Double.NaN;

		public void add( double value )
		{
			min = Vecmath.nmin( min , value );
			max = Vecmath.nmax( max , value );
			total += value;
			count++;
		}

		public double getAvg( )
		{
			return total / count;
		}

		public QObject<MinAvgMax> toModel( )
		{
			QObject<MinAvgMax> result = MinAvgMax.spec.newObject( );
			result.set( MinAvgMax.min , min );
			result.set( MinAvgMax.avg , getAvg( ) );
			result.set( MinAvgMax.max , max );
			return result;
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

			final Survey3dModel model3d = BreakoutMainView.this.model3d;

			List<Survey3dModel.Shot3d> shot3ds = model3d.getShots( );

			final SelectionEditor editor = model3d.editSelection( );

			ListSelectionModel selModel = ( ListSelectionModel ) e.getSource( );

			if( e.getFirstIndex( ) < 0 )
			{
				for( Survey3dModel.Shot3d shot3d : shot3ds )
				{
					editor.deselect( shot3d );
				}

				miniSurveyDrawer.statsPanel( ).getModelBinder( ).set( StatsModel.spec.newObject( ) );
			}
			else
			{
				MinAvgMaxCalc distCalc = new MinAvgMaxCalc( );
				MinAvgMaxCalc northCalc = new MinAvgMaxCalc( );
				MinAvgMaxCalc eastCalc = new MinAvgMaxCalc( );
				MinAvgMaxCalc depthCalc = new MinAvgMaxCalc( );

				QObject<StatsModel> statsModel = StatsModel.spec.newObject( );

				for( int i = e.getFirstIndex( ) ; i <= e.getLastIndex( )
					&& i < surveyDrawer.table( ).getModel( ).getRowCount( ) ; i++ )
				{
					Shot shot = ( Shot ) surveyDrawer.table( ).getModel( ).shotAtRow( i );
					if( shot == null )
					{
						continue;
					}

					if( selModel.isSelectedIndex( i ) )
					{
						editor.select( shot3ds.get( shot.number ) );
						if( !Double.isNaN( shot.dist ) )
						{
							distCalc.add( shot.dist );
						}
						if( !Vecmath.hasNaNsOrInfinites( shot.from.position ) )
						{
							northCalc.add( -shot.from.position[ 2 ] );
							eastCalc.add( shot.from.position[ 0 ] );
							depthCalc.add( -shot.from.position[ 1 ] );
						}
						if( !Vecmath.hasNaNsOrInfinites( shot.to.position ) )
						{
							northCalc.add( -shot.to.position[ 2 ] );
							eastCalc.add( shot.to.position[ 0 ] );
							depthCalc.add( -shot.to.position[ 1 ] );
						}
					}
					else
					{
						editor.deselect( shot3ds.get( shot.number ) );
					}
				}

				statsModel.set( StatsModel.numSelected , distCalc.count );
				statsModel.set( StatsModel.totalDistance , distCalc.total );
				statsModel.set( StatsModel.distStats , distCalc.toModel( ) );
				statsModel.set( StatsModel.northStats , northCalc.toModel( ) );
				statsModel.set( StatsModel.eastStats , eastCalc.toModel( ) );
				statsModel.set( StatsModel.depthStats , depthCalc.toModel( ) );

				miniSurveyDrawer.statsPanel( ).getModelBinder( ).set( statsModel );
			}

			rebuildTaskService.submit( task ->
			{
				editor.commit( );

				List<Shot> origShots = new ArrayList<>( );
				Set<Survey3dModel.Shot3d> newSelectedShots = new HashSet<>( );

				model3d.addOriginalShotsTo( origShots );
				model3d.addSelectedShotsTo( newSelectedShots );

				float[ ] bounds = Rectmath.voidRectf( 3 );
				float[ ] p = Rectmath.voidRectf( 3 );

				for( Survey3dModel.Shot3d shot3d : newSelectedShots )
				{
					Shot origShot = origShots.get( shot3d.getNumber( ) );
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

				SwingUtilities.invokeLater( ( ) ->
				{
					orbiter.setCenter( p );
					navigator.setCenter( p );
				} );
			}

			autoDrawable.display( );
		} )	;
		}
	}

	class FitToFilteredHandler implements ActionListener
	{
		AnnotatingJTable	table;
		long				lastAction	= 0;

		public FitToFilteredHandler( AnnotatingJTable table )
		{
			super( );
			this.table = table;
		}

		@Override
		public void actionPerformed( ActionEvent e )
		{
			final long time = System.currentTimeMillis( );
			lastAction = time;

			table.getAnnotatingRowSorter( ).invokeWhenDoneSorting( ( ) ->
			{
				if( time >= lastAction )
				{
					flyToFiltered( table );
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
		renderer.getViewSettings( ).getViewXform( viewXform );
		getProjectModel( ).set( ProjectModel.viewXform , viewXform );
	}

	private void saveProjection( )
	{
		getProjectModel( ).set( ProjectModel.projCalculator , renderer.getViewSettings( ).getProjection( ) );
	}

	private void replaceNulls( QObject<ProjectModel> projectModel , Path projectFile )
	{
		if( projectModel.get( ProjectModel.cameraView ) == null )
		{
			projectModel.set( ProjectModel.cameraView , CameraView.PERSPECTIVE );
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
			projectModel
				.set( ProjectModel.paramRanges , QLinkedHashMap.<ColorParam, LinearAxisConversion>newInstance( ) );
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
			Path surveyFile = projectFile.relativize(
				NewProjectAction.pickDefaultSurveyFile( projectFile.toFile( ) ).toPath( ) );
			projectModel.set( ProjectModel.surveyFile , surveyFile );
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

	//	class SurveyFilterFactory implements RowFilterFactory<String, TableModel, Integer>
	//	{
	//		@Override
	//		public RowFilter<TableModel, Integer> createFilter( String input )
	//		{
	//			switch( getProjectModel( ).get( ProjectModel.filterType ) )
	//			{
	//				case ALPHA_DESIGNATION:
	//					return new SurveyDesignationFilter( input );
	//				case REGEXP:
	//					return new SurveyRegexFilter( input );
	//				case SURVEYORS:
	//					return new SurveyorFilter( input );
	//				case DESCRIPTION:
	//					return new DescriptionFilter( input );
	//				default:
	//					return null;
	//			}
	//		}
	//	}

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
			BatcherTask<TableModelEvent> task = new BatcherTask<TableModelEvent>( "Updating view" ) {
				@Override
				protected void execute( )
				{
					try
					{
						taskListDrawer.holder( ).hold( this );
						reallyExecute( );
					}
					finally
					{
						taskListDrawer.holder( ).release( this );
					}
				}

				protected void reallyExecute( )
				{
					setTotal( 1000 );
					Subtask copySubtask = new Subtask( this );
					copySubtask.setStatus( "Parsing shot data" );
					copySubtask.setIndeterminate( false );

					SurveyTableModel copy = new SurveyTableModel( );
					SurveyTableModelCopier copier = new SurveyTableModelCopier( );

					SurveyTableModel model = new FromEDT<SurveyTableModel>( ) {
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

					final List<Shot> shots = copy.createShots( parsingSubtask );

					if( parsingSubtask.isCanceling( ) )
					{
						return;
					}

					new OnEDT( ) {
						@Override
						public void run( ) throws Throwable
						{
							surveyDrawer.table( ).getModel( ).setShots( shots );
						}
					};

					final List<Shot> nonNullShots = new ArrayList<Shot>( );

					if( !shots.isEmpty( ) )
					{
						Subtask calculatingSubtask = new Subtask( this );
						calculatingSubtask.setStatus( "calculating" );
						calculatingSubtask.setIndeterminate( true );

						LinkedHashSet<Station> stations = new LinkedHashSet<Station>( );

						for( Shot shot : shots )
						{
							if( shot != null )
							{
								nonNullShots.add( shot );
								stations.add( shot.from );
								stations.add( shot.to );
							}
						}

						Shot.computeConnected( stations );

						LineLineIntersection2d llx = new LineLineIntersection2d( );

						for( Station station : stations )
						{
							station.calcSplayPoints( llx );
						}

						calculatingSubtask.end( );
					}

					updateModel( nonNullShots );
				}

				public boolean isCancelable( )
				{
					return true;
				}

				public void updateModel( List<Shot> shots )
				{
					setStatus( "Updating view..." );

					SwingUtilities.invokeLater( ( ) ->
					{
						if( model3d != null )
						{
							final Survey3dModel model3d = BreakoutMainView.this.model3d;
							BreakoutMainView.this.model3d = null;

							autoDrawable.invoke( false , drawable ->
							{
								scene.remove( model3d );
								scene.disposeLater( model3d );
								return false;
							} );
						}
					} );

					setStatus( "Updating view: constructing new model..." );

					final Survey3dModel model = Survey3dModel.create( shots , 10 , 3 , 3 , this );
					if( isCanceling( ) )
					{
						return;
					}

					setStatus( "Updating view: installing new model..." );

					float[ ] bounds = Arrays.copyOf( model.getTree( ).getRoot( ).mbr( ) , 6 );

					bounds[ 1 ] = bounds[ 4 ] + 100;
					bounds[ 4 ] = bounds[ 1 ] + 100;

					float[ ][ ][ ] vertices = new float[ 100 ][ 100 ][ 3 ];

					Random rand = new Random( 2 );

					TransparentTerrain.randomVerts( vertices , bounds , rand );

					TransparentTerrain terrain = new TransparentTerrain( vertices );

					SwingUtilities.invokeLater( ( ) ->
					{
						BreakoutMainView.this.model3d = model;
						model.setParamPaint( settingsDrawer.getParamColorationAxisPaint( ) );

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
						navigator.setCenter( center );

						autoDrawable.invoke( false , drawable ->
						{
							scene.add( model );
							scene.initLater( model );
							//							scene.add( terrain );
							//							scene.initLater( terrain );
							return false;
						} );
					} );
				}
			};
			return task;
		}
	}

	public void importProjectArchive( File newProjectFile )
	{
		ioTaskService.submit( new ImportProjectArchiveTask( newProjectFile ) );
	}

	public TaskListDrawer getTaskListDrawer( )
	{
		return taskListDrawer;
	}

	private class ImportProjectArchiveTask extends DrawerPinningTask
	{
		File	newProjectFile;

		private ImportProjectArchiveTask( File newProjectFile )
		{
			super( getMainPanel( ) , taskListDrawer.holder( ) );
			this.newProjectFile = newProjectFile;
			setStatus( "Saving project..." );
			setIndeterminate( true );

			showDialogLater( );
		}

		@Override
		protected void reallyDuringDialog( ) throws Exception
		{
			setStatus( "Importing project archive: " + newProjectFile + "..." );

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
				new OnEDT( ) {
					@Override
					public void run( ) throws Throwable
					{
						JOptionPane.showMessageDialog( getMainPanel( ) ,
							ex.getClass( ).getName( ) + ": " + ex.getLocalizedMessage( ) ,
							"Failed to import project archive" , JOptionPane.ERROR_MESSAGE );
					}
				};
				return;
			}

			final ProjectArchiveModel finalProjectModel = projectModel;

			new OnEDT( ) {
				@Override
				public void run( ) throws Throwable
				{
					finalProjectModel.getProjectModel( ).set( ProjectModel.surveyFile ,
						getProjectModel( ).get( ProjectModel.surveyFile ) );
					replaceNulls( finalProjectModel.getProjectModel( ) ,
						getRootModel( ).get( RootModel.currentProjectFile ) );
					if( projectPersister != null )
					{
						if( getProjectModel( ) != null )
						{
							getProjectModel( ).changeSupport( ).removePropertyChangeListener( projectPersister );
						}
						finalProjectModel.getProjectModel( ).changeSupport( )
							.addPropertyChangeListener( projectPersister );
						projectPersister.saveLater( finalProjectModel.getProjectModel( ) );
					}
					projectModelBinder.set( finalProjectModel.getProjectModel( ) );

					surveyDrawer
						.table( )
						.getModel( )
						.copyRowsFrom( finalProjectModel.getSurveyTableModel( ) , 0 ,
							finalProjectModel.getSurveyTableModel( ).getRowCount( ) - 1 , 0 );
				}
			};
		}
	}

	public void exportProjectArchive( File newProjectFile )
	{
		ioTaskService.submit( new ExportProjectArchiveTask( newProjectFile ) );
	}

	private class ExportProjectArchiveTask extends DrawerPinningTask
	{
		File	newProjectFile;

		private ExportProjectArchiveTask( File newProjectFile )
		{
			super( getMainPanel( ) , taskListDrawer.holder( ) );
			this.newProjectFile = newProjectFile;
			setStatus( "Saving project..." );
			setIndeterminate( true );

			showDialogLater( );
		}

		@Override
		protected void reallyDuringDialog( ) throws Exception
		{
			setStatus( "Exporting project archive: " + newProjectFile + "..." );

			setTotal( 1000 );

			Subtask rootSubtask = new Subtask( this );
			rootSubtask.setTotal( 3 );
			Subtask prepareSubtask = rootSubtask.beginSubtask( 1 );
			prepareSubtask.setStatus( "Preparing for export" );

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
				new ProjectArchiveModelStreamBimapper( getI18n( ) , exportSubtask ).write( projectModel ,
					new FileOutputStream( newProjectFile ) );
				exportSubtask.end( );
				rootSubtask.setCompleted( rootSubtask.getCompleted( ) + exportSubtask.getProportion( ) );
			}
			catch( final Exception ex )
			{
				ex.printStackTrace( );
				new OnEDT( ) {
					@Override
					public void run( ) throws Throwable
					{
						JOptionPane.showMessageDialog( getMainPanel( ) ,
							ex.getClass( ).getName( ) + ": " + ex.getLocalizedMessage( ) ,
							"Failed to export project archive" , JOptionPane.ERROR_MESSAGE );
					}
				};
				return;
			}
		}
	}

	/**
	 * Opens the given project file.
	 * 
	 * @param newProjectFile
	 *            the path to the project file to open; must be absolute or relative to the working directory.
	 */
	public void openProject( Path newProjectFile )
	{
		ioTaskService.submit( new OpenProjectTask( newProjectFile ) );
	}

	private class OpenProjectTask extends DrawerPinningTask
	{
		Path	newProjectFile;
		Path	relativizedNewProjectFile;

		private OpenProjectTask( Path newProjectFile )
		{
			super( getMainPanel( ) , taskListDrawer.holder( ) );
			this.newProjectFile = newProjectFile;
			this.relativizedNewProjectFile = rootDirectory.toAbsolutePath( ).relativize(
				newProjectFile.toAbsolutePath( ) );
			setStatus( "Saving current project..." );
			setIndeterminate( true );

			showDialogLater( );
		}

		@Override
		protected void reallyDuringDialog( ) throws Exception
		{
			setStatus( "Opening project: " + newProjectFile + "..." );

			new OnEDT( ) {
				@Override
				public void run( ) throws Throwable
				{
					QObject<RootModel> rootModel = getRootModel( );
					rootModel.set( RootModel.currentProjectFile , relativizedNewProjectFile );
					QArrayList<Path> recentProjectFiles = rootModel.get( RootModel.recentProjectFiles );
					if( recentProjectFiles == null )
					{
						recentProjectFiles = QArrayList.newInstance( );
						rootModel.set( RootModel.recentProjectFiles , recentProjectFiles );
					}

					recentProjectFiles.remove( relativizedNewProjectFile );
					while( recentProjectFiles.size( ) > 20 )
					{
						recentProjectFiles.remove( recentProjectFiles.size( ) - 1 );
					}
					recentProjectFiles.add( 0 , relativizedNewProjectFile );

					if( getProjectModel( ) != null && projectPersister != null )
					{
						getProjectModel( ).changeSupport( ).removePropertyChangeListener( projectPersister );
					}
					projectPersister = new TaskServiceFilePersister<QObject<ProjectModel>>( ioTaskService ,
						"Saving project..." , QObjectBimappers.defaultBimapper( ProjectModel.defaultMapper ) ,
						newProjectFile.toFile( ) );
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
				new OnEDT( ) {
					@Override
					public void run( ) throws Throwable
					{
						JOptionPane.showMessageDialog( getMainPanel( ) ,
							ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
							"Failed to load project" , JOptionPane.ERROR_MESSAGE );
					}
				};
				return;
			}

			final QObject<ProjectModel> finalProjectModel = projectModel;

			new OnEDT( ) {
				@Override
				public void run( ) throws Throwable
				{
					finalProjectModel.changeSupport( ).addPropertyChangeListener( projectPersister );
					projectModelBinder.set( finalProjectModel );

					float[ ] viewXform = finalProjectModel.get( ProjectModel.viewXform );
					if( viewXform != null )
					{
						renderer.getViewSettings( ).setViewXform( viewXform );
					}

					Projection projCalculator = finalProjectModel.get( ProjectModel.projCalculator );
					if( projCalculator != null )
					{
						renderer.getViewSettings( ).setProjection( projCalculator );
					}

					if( finalProjectModel.get( ProjectModel.cameraView ) == CameraView.PERSPECTIVE )
					{
						installPerspectiveMouseAdapters( );
					}
					else
					{
						installOrthoMouseAdapters( );
					}
				}
			};

			Path surveyFile = newProjectFile.toAbsolutePath( ).getParent( ).resolve(
				projectModel.get( ProjectModel.surveyFile ) ).normalize( );

			openSurveyFile( surveyFile );
		}
	}

	public void openSurveyFile( Path newSurveyFile )
	{
		ioTaskService.submit( new OpenSurveyTask( newSurveyFile ) );
	}

	private class OpenSurveyTask extends DrawerPinningTask
	{
		Path	newSurveyFile;
		Path	relativizedNewSurveyFile;

		private OpenSurveyTask( Path newSurveyFile )
		{
			super( getMainPanel( ) , taskListDrawer.holder( ) );
			this.newSurveyFile = newSurveyFile;
			Path projectPath = rootDirectory.toAbsolutePath( )
				.resolve( getRootModel( ).get( RootModel.currentProjectFile ) ).normalize( );
			this.relativizedNewSurveyFile = projectPath.toAbsolutePath( ).getParent( )
				.relativize( newSurveyFile.toAbsolutePath( ) );
			setStatus( "Saving current survey..." );
			setIndeterminate( true );

			showDialogLater( );
		}

		@Override
		protected void reallyDuringDialog( ) throws Exception
		{
			boolean changed = new FromEDT<Boolean>( ) {
				@Override
				public Boolean run( ) throws Throwable
				{
					File absoluteSurveyFile = newSurveyFile.toAbsolutePath( ).toFile( );

					if( surveyPersister == null || !absoluteSurveyFile.equals( surveyPersister.getFile( ) ) )
					{
						surveyPersister = new TaskServiceSubtaskFilePersister<SurveyTableModel>(
							ioTaskService ,
							"Saving survey..." ,
							new SubtaskStreamBimapperFactory<SurveyTableModel, SubtaskStreamBimapper<SurveyTableModel>>( )
							{
								@Override
								public SubtaskStreamBimapper<SurveyTableModel> createSubtaskStreamBimapper(
									Subtask subtask )
								{
									return new SurveyTableModelStreamBimapper( subtask );
								}
							} , absoluteSurveyFile );
					}
					else
					{
						return false;
					}
					getProjectModel( ).set( ProjectModel.surveyFile , relativizedNewSurveyFile );

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

			setStatus( "Opening survey: " + newSurveyFile + "..." );

			SurveyTableModel surveyModel;

			try
			{
				surveyModel = surveyPersister.load( null );
			}
			catch( final Exception ex )
			{
				ex.printStackTrace( );
				new OnEDT( ) {
					@Override
					public void run( ) throws Throwable
					{
						JOptionPane.showConfirmDialog( getMainPanel( ) ,
							ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
							"Failed to load survey" , JOptionPane.ERROR_MESSAGE );
					}
				};

				return;
			}

			final SurveyTableModel finalSurveyModel = surveyModel;

			new OnEDT( ) {
				@Override
				public void run( ) throws Throwable
				{
					if( finalSurveyModel != null && finalSurveyModel.getRowCount( ) > 0 )
					{
						try
						{
							surveyTableChangeHandler.setPersistOnUpdate( false );
							surveyDrawer.table( ).getModel( )
								.copyRowsFrom( finalSurveyModel , 0 , finalSurveyModel.getRowCount( ) - 1 , 0 );
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
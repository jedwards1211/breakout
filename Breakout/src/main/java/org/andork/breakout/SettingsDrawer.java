package org.andork.breakout;

import static org.andork.awt.event.UIBindings.bind;
import static org.andork.awt.event.UIBindings.bindBgColor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.awt.ColorUtils;
import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.breakout.model.ProjectModel;
import org.andork.breakout.model.RootModel;
import org.andork.event.Binder;
import org.andork.func.FileStringBimapper;
import org.andork.plot.PlotAxisConversionBinding;
import org.andork.q.QObject;
import org.andork.swing.OnEDT;
import org.andork.swing.PaintablePanel;
import org.andork.swing.border.FillBorder;
import org.andork.swing.border.GradientFillBorder;
import org.andork.swing.border.InnerGradientBorder;
import org.andork.swing.border.MultipleGradientFillBorder;
import org.andork.swing.border.OverrideInsetsBorder;
import org.andork.swing.selector.DefaultSelector;
import org.jdesktop.swingx.JXColorSelectionButton;

import com.andork.plot.PlotAxis;
import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;
import com.andork.plot.PlotAxisController;

@SuppressWarnings( "serial" )
public class SettingsDrawer extends Drawer
{
	Localizer							localizer;
	
	JLabel								projectFileLabel;
	JTextField							projectFileField;
	JButton								projectFileMenuButton;
	
	JButton								importButton;
	JButton								exportButton;
	
	ViewButtonsPanel					viewButtonsPanel;
	JSlider								mouseSensitivitySlider;
	JSlider								mouseWheelSensitivitySlider;
	JXColorSelectionButton				bgColorButton;
	JSlider								ambientLightSlider;
	PlotAxis							distColorationAxis;
	PaintablePanel						distColorationAxisPanel;
	PlotAxis							paramColorationAxis;
	PaintablePanel						paramColorationAxisPanel;
	JButton								inferDepthAxisTiltButton;
	JButton								resetDepthAxisTiltButton;
	PlotAxis							glowDistAxis;
	PaintablePanel						glowDistAxisPanel;
	JButton								fitViewToEverythingButton;
	JButton								fitViewToSelectedButton;
	JButton								orbitToPlanButton;
	JButton								debugButton;
	JButton								exportImageButton;
	
	JLabel								numSamplesLabel;
	JSlider								numSamplesSlider;
	
	DefaultSelector<FilterType>			filterTypeSelector;
	
	JPanel								mainPanel;
	JScrollPane							mainPanelScrollPane;
	
	Binder<QObject<RootModel>>		rootBinder;
	Binder<QObject<ProjectModel>>	projectBinder;
	
	public SettingsDrawer( final I18n i18n , Binder<QObject<RootModel>> rootBinder , Binder<QObject<ProjectModel>> projectBinder )
	{
		this.rootBinder = rootBinder;
		this.projectBinder = projectBinder;
		
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				localizer = i18n.forClass( SettingsDrawer.this.getClass( ) );
				
				delegate( ).dockingSide( Side.RIGHT );
				pinButton( );
				pinButtonDelegate( ).corner( Corner.TOP_LEFT ).side( Side.LEFT );
				
				setUnderpaintBorder( GradientFillBorder.from( Side.TOP ).to( Side.BOTTOM ).colors(
						ColorUtils.darkerColor( getBackground( ) , 0.05 ) ,
						ColorUtils.darkerColor( Color.LIGHT_GRAY , 0.05 ) ) );
				setBorder( new OverrideInsetsBorder(
						new InnerGradientBorder( new Insets( 0 , 5 , 0 , 0 ) , Color.GRAY ) ,
						new Insets( 0 , 8 , 0 , 0 ) ) );
				
				createComponents( );
				createLayout( );
				createListeners( );
				createBindings( );
			}
		};
	}
	
	private void createComponents( )
	{
		projectFileLabel = new JLabel( );
		projectFileLabel.setFont( projectFileLabel.getFont( ).deriveFont( Font.BOLD ).deriveFont( 14f ) );
		localizer.setText( projectFileLabel , "projectFileLabel.text" );
		projectFileField = new JTextField( );
		projectFileField.setEditable( false );
		projectFileField.setPreferredSize( new Dimension( 150 , projectFileField.getPreferredSize( ).height ) );
		projectFileMenuButton = new JButton( "..." );
		projectFileMenuButton.setMargin( new Insets( 2 , 4 , 2 , 4 ) );
		
		importButton = new JButton( );
		localizer.setText( importButton , "importButton.text" );
		exportButton = new JButton( );
		localizer.setText( exportButton , "exportButton.text" );
		
		viewButtonsPanel = new ViewButtonsPanel( );
		
		Color darkColor = new Color( 255 * 3 / 10 , 255 * 3 / 10 , 255 * 3 / 10 );
		
		bgColorButton = new JXColorSelectionButton( );
		
		ambientLightSlider = new JSlider( 0 , 100 , 50 );
		ambientLightSlider.setOpaque( false );
		
		distColorationAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		distColorationAxisPanel = PaintablePanel.wrap( distColorationAxis );
		distColorationAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { ColorUtils.alphaColor( darkColor , 0 ) , darkColor } ) );
		
		distColorationAxis.getAxisConversion( ).set( 0 , 0 , 10000 , 200 );
		distColorationAxis.setForeground( Color.WHITE );
		distColorationAxis.setMajorTickColor( Color.WHITE );
		distColorationAxis.setMinorTickColor( Color.WHITE );
		
		paramColorationAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		paramColorationAxisPanel = PaintablePanel.wrap( paramColorationAxis );
		paramColorationAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 0.24f , 0.64f , 1f } ,
						new Color[ ] { new Color( 255 , 249 , 204 ) , new Color( 255 , 195 , 0 ) , new Color( 214 , 6 , 127 ) , new Color( 34 , 19 , 150 ) } ) );
		
		paramColorationAxis.setForeground( Color.WHITE );
		paramColorationAxis.setMajorTickColor( Color.WHITE );
		paramColorationAxis.setMinorTickColor( Color.WHITE );
		
		inferDepthAxisTiltButton = new JButton( "Infer Depth Axis Tilt" );
		resetDepthAxisTiltButton = new JButton( "Reset Depth Axis Tilt" );
		
		glowDistAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		glowDistAxisPanel = PaintablePanel.wrap( glowDistAxis );
		glowDistAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { Color.CYAN , ColorUtils.alphaColor( Color.CYAN , 0 ) } ) );
		
		glowDistAxis.setForeground( Color.BLACK );
		glowDistAxis.setMajorTickColor( Color.BLACK );
		glowDistAxis.setMinorTickColor( Color.BLACK );
		
		mouseSensitivitySlider = new JSlider( );
		mouseSensitivitySlider.setValue( 20 );
		mouseSensitivitySlider.setOpaque( false );
		
		mouseWheelSensitivitySlider = new JSlider( 1 , 2000 , 200 );
		mouseWheelSensitivitySlider.setOpaque( false );
		
		fitViewToSelectedButton = new JButton( "Fit View to Selected" );
		fitViewToEverythingButton = new JButton( "Fit View to Everything" );
		orbitToPlanButton = new JButton( "Orbit to Plan" );
		
		filterTypeSelector = new DefaultSelector<FilterType>( );
		filterTypeSelector.setAvailableValues( Arrays.asList( FilterType.values( ) ) );
		
		exportImageButton = new JButton( "Export Image..." );
		
		numSamplesLabel = new JLabel( "Multisampling: Off" );
		numSamplesSlider = new JSlider( 1 , 1 , 1 );
		numSamplesSlider.setOpaque( false );
		numSamplesSlider.setEnabled( false );
		
		debugButton = new JButton( "Debug" );
		
		mainPanel = new JPanel( );
		mainPanel.setBorder( new EmptyBorder( 5 , 0 , 5 , 5 ) );
		mainPanel.setOpaque( false );
		mainPanelScrollPane = new JScrollPane( mainPanel );
		mainPanelScrollPane.setBorder( null );
		mainPanelScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		mainPanelScrollPane.setOpaque( false );
		mainPanelScrollPane.getViewport( ).setOpaque( false );
		JScrollBar verticalScrollBar = mainPanelScrollPane.getVerticalScrollBar( );
		verticalScrollBar.setUnitIncrement( 5 );
	}
	
	private void createLayout( )
	{
		GridBagWizard w = GridBagWizard.create( mainPanel );
		w.defaults( ).autoinsets( new DefaultAutoInsets( 3 , 3 ) );
		w.put( projectFileLabel ).xy( 0 , 0 ).west( );
		GridBagWizard projectFilePanel = GridBagWizard.quickPanel( );
		projectFilePanel.defaults( ).filly( );
		projectFilePanel.put( projectFileField ).xy( 0 , 0 ).fillx( 1.0 );
		projectFilePanel.put( projectFileMenuButton ).rightOfLast( );
		
		w.put( projectFilePanel.getTarget( ) ).belowLast( ).fillx( );
		
		GridBagWizard importExportPanel = GridBagWizard.quickPanel( );
		importExportPanel.put( importButton ).fillx( 0.5 );
		importExportPanel.put( exportButton ).fillx( 0.5 );
		
		w.put( importExportPanel.getTarget( ) ).belowLast( ).fillx( );
		
		w.put( viewButtonsPanel ).belowLast( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( fitViewToSelectedButton ).belowLast( ).fillx( 1.0 );
		w.put( fitViewToEverythingButton ).belowLast( ).fillx( 1.0 );
		w.put( orbitToPlanButton ).belowLast( ).fillx( 1.0 );
		JLabel sensLabel = new JLabel( "Mouse Sensitivity:" );
		w.put( sensLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( mouseSensitivitySlider ).belowLast( ).fillx( ).north( );
		
		JLabel wheelSensLabel = new JLabel( "Mouse Wheel Sensitivity:" );
		w.put( wheelSensLabel ).belowLast( ).west( ).addToInsets( 0 , 0 , 0 , 0 );
		w.put( mouseWheelSensitivitySlider ).belowLast( ).fillx( ).north( );
		
		GridBagWizard bgPanel = GridBagWizard.quickPanel( );
		bgPanel.put( new JLabel( "Background Color: " ) ).xy( 0 , 0 ).west( );
		bgPanel.put( bgColorButton ).rightOfLast( ).west( ).weightx( 1.0 );
		w.put( bgPanel.getTarget( ) ).belowLast( ).fillx( ).addToInsets( 10 , 0 , 0 , 0 );
		
		w.put( new JLabel( "Ambient Light:" ) ).belowLast( ).west( );
		w.put( ambientLightSlider ).belowLast( ).fillx( );
		
		JLabel distLabel = new JLabel( "Distance coloration:" );
		w.put( distLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( distColorationAxisPanel ).belowLast( ).fillx( );
		JLabel paramLabel = new JLabel( "Depth coloration:" );
		w.put( paramLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( paramColorationAxisPanel ).belowLast( ).fillx( );
		w.put( inferDepthAxisTiltButton ).belowLast( ).fillx( );
		w.put( resetDepthAxisTiltButton ).belowLast( ).fillx( );
		JLabel highlightRangeLabel = new JLabel( "Highlight range:" );
		w.put( highlightRangeLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( glowDistAxisPanel ).belowLast( ).fillx( );
		w.put( numSamplesLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( numSamplesSlider ).belowLast( ).fillx( );
		w.put( exportImageButton ).belowLast( ).fillx( ).addToInsets( 10 , 0 , 0 , 0 );
		JLabel filterTypeLabel = new JLabel( "Filter type:" );
		w.put( filterTypeLabel ).belowLast( ).west( ).insets( 40 , 0 , 0 , 0 );
		w.put( filterTypeSelector.getComboBox( ) ).belowLast( ).fillx( );
		
		JPanel paddingPanel = new JPanel( );
		paddingPanel.setOpaque( false );
		w.put( paddingPanel ).belowLast( ).fillboth( 1.0 , 1.0 );
		
		w.put( debugButton ).belowLast( ).southwest( );
		
		debugButton.setVisible( false );
		
		setLayout( new BorderLayout( ) );
		add( mainPanelScrollPane , BorderLayout.CENTER );
	}
	
	private void createListeners( )
	{
		new PlotAxisController( distColorationAxis ).removeMouseWheelListener( );
		new PlotAxisController( paramColorationAxis ).removeMouseWheelListener( );
		new PlotAxisController( glowDistAxis ).removeMouseWheelListener( );
		
		numSamplesSlider.addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				updateNumSamplesLabel( );
			}
		} );
	}
	
	private void createBindings( )
	{
		bind( rootBinder , projectFileField , RootModel.currentProjectFile , FileStringBimapper.instance );
		
		bindBgColor( projectBinder , bgColorButton , ProjectModel.backgroundColor );
		
		bind( projectBinder , viewButtonsPanel.getPlanButton( ) , ProjectModel.cameraView , CameraView.PLAN );
		bind( projectBinder , viewButtonsPanel.getPerspectiveButton( ) , ProjectModel.cameraView , CameraView.PERSPECTIVE );
		bind( projectBinder , viewButtonsPanel.getNorthButton( ) , ProjectModel.cameraView , CameraView.NORTH_FACING_PROFILE );
		bind( projectBinder , viewButtonsPanel.getSouthButton( ) , ProjectModel.cameraView , CameraView.SOUTH_FACING_PROFILE );
		bind( projectBinder , viewButtonsPanel.getEastButton( ) , ProjectModel.cameraView , CameraView.EAST_FACING_PROFILE );
		bind( projectBinder , viewButtonsPanel.getWestButton( ) , ProjectModel.cameraView , CameraView.WEST_FACING_PROFILE );
		bind( rootBinder , mouseSensitivitySlider , RootModel.mouseSensitivity );
		bind( rootBinder , mouseWheelSensitivitySlider , RootModel.mouseWheelSensitivity );
		projectBinder.bind( new PlotAxisConversionBinding( ProjectModel.distRange , distColorationAxis ) );
		projectBinder.bind( new PlotAxisConversionBinding( ProjectModel.paramRange , paramColorationAxis ) );
		projectBinder.bind( new PlotAxisConversionBinding( ProjectModel.highlightRange , glowDistAxis ) );
		bind( projectBinder , filterTypeSelector , ProjectModel.filterType );
		bind( projectBinder , ambientLightSlider , ProjectModel.ambientLight , 0f , 1f );
		
		bind( rootBinder , numSamplesSlider , RootModel.desiredNumSamples );
	}
	
	public void setMaxNumSamples( int maxNumSamples )
	{
		if( maxNumSamples != numSamplesSlider.getMaximum( ) )
		{
			Integer value = rootBinder.getModel( ).get( RootModel.desiredNumSamples );
			if( value == null )
			{
				value = numSamplesSlider.getValue( );
			}
			value = Math.min( value , maxNumSamples );
			DefaultBoundedRangeModel newModel = new DefaultBoundedRangeModel( value , 0 , 1 , maxNumSamples );
			numSamplesSlider.setModel( newModel );
			numSamplesSlider.setEnabled( maxNumSamples > 1 );
			updateNumSamplesLabel( );
		}
	}
	
	private void updateNumSamplesLabel( )
	{
		numSamplesLabel.setText( "<html>Multisampling: <b>" + ( numSamplesSlider.getValue( ) < 2 ? "Off" : numSamplesSlider.getValue( ) + "X" ) + "</b></html>" );
	}
	
	public JButton getProjectFileMenuButton( )
	{
		return projectFileMenuButton;
	}
	
	public JButton getFitViewToSelectedButton( )
	{
		return fitViewToSelectedButton;
	}
	
	public JButton getFitViewToEverythingButton( )
	{
		return fitViewToEverythingButton;
	}
	
	public JButton getOrbitToPlanButton( )
	{
		return orbitToPlanButton;
	}
	
	public PlotAxis getDistColorationAxis( )
	{
		return distColorationAxis;
	}
	
	public PlotAxis getParamColorationAxis( )
	{
		return paramColorationAxis;
	}
	
	public PaintablePanel getParamColorationAxisPanel( )
	{
		return paramColorationAxisPanel;
	}
	
	public LinearGradientPaint getParamColorationAxisPaint( )
	{
		return ( LinearGradientPaint ) ( ( FillBorder ) paramColorationAxisPanel.getUnderpaintBorder( ) )
				.getPaint( paramColorationAxisPanel , null ,
						0 , 0 , paramColorationAxisPanel.getWidth( ) , paramColorationAxisPanel.getHeight( ) );
	}
	
	public PlotAxis getGlowDistAxis( )
	{
		return glowDistAxis;
	}
	
	public static enum CameraView
	{
		PERSPECTIVE( "Perspective" ) ,
		PLAN( "Plan" ) ,
		NORTH_FACING_PROFILE( "North-Facing Profile" ) ,
		SOUTH_FACING_PROFILE( "South-Facing Profile" ) ,
		EAST_FACING_PROFILE( "East-Facing Profile" ) ,
		WEST_FACING_PROFILE( "West-Facing Profile" );
		
		private String	displayText;
		
		CameraView( String displayText )
		{
			this.displayText = displayText;
		}
		
		public String toString( )
		{
			return displayText;
		}
	}
	
	public static enum FilterType
	{
		ALPHA_DESIGNATION( "Alphabetic Designation" ) ,
		REGEXP( "Regular Expression" ) ,
		SURVEYORS( "Surveyors" ) ,
		DESCRIPTION( "Description" );
		
		private String	displayText;
		
		private FilterType( String displayText )
		{
			this.displayText = displayText;
		}
		
		public String toString( )
		{
			return displayText;
		}
	}
	
	public JButton getInferDepthAxisTiltButton( )
	{
		return inferDepthAxisTiltButton;
	}
	
	public JButton getResetDepthAxisTiltButton( )
	{
		return resetDepthAxisTiltButton;
	}
	
	public JButton getExportImageButton( )
	{
		return exportImageButton;
	}
	
	public AbstractButton getImportButton( )
	{
		return importButton;
	}
	
	public AbstractButton getExportButton( )
	{
		return exportButton;
	}
	
}
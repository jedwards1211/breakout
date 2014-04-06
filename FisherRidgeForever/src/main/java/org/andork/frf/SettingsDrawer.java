package org.andork.frf;

import static org.andork.awt.event.UIBindings.bind;

import java.awt.Color;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.andork.awt.ColorUtils;
import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.event.Binder;
import org.andork.frf.model.LinearAxisConversionYamlBimapper;
import org.andork.plot.PlotAxisConversionBinding;
import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlSpec;
import org.andork.swing.PaintablePanel;
import org.andork.swing.border.FillBorder;
import org.andork.swing.border.GradientFillBorder;
import org.andork.swing.border.InnerGradientBorder;
import org.andork.swing.border.MultipleGradientFillBorder;
import org.andork.swing.border.OverrideInsetsBorder;
import org.andork.swing.selector.DefaultSelector;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.PlotAxis;
import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;
import com.andork.plot.PlotAxisController;

@SuppressWarnings( "serial" )
public class SettingsDrawer extends Drawer
{
	
	JButton						updateViewButton;
	ViewButtonsPanel			viewButtonsPanel;
	JSlider						mouseSensitivitySlider;
	PlotAxis					distColorationAxis;
	PaintablePanel				distColorationAxisPanel;
	PlotAxis					paramColorationAxis;
	PaintablePanel				paramColorationAxisPanel;
	JButton						inferDepthAxisTiltButton;
	JButton						resetDepthAxisTiltButton;
	PlotAxis					highlightDistAxis;
	PaintablePanel				highlightDistAxisPanel;
	JButton						fitViewToEverythingButton;
	JButton						fitViewToSelectedButton;
	JButton						orbitToPlanButton;
	JButton						debugButton;
	
	DefaultSelector<FilterType>	filterTypeSelector;
	
	Binder<YamlObject<Model>>	binder;
	
	public SettingsDrawer( Binder<YamlObject<Model>> binder )
	{
		this.binder = binder;
		
		delegate( ).dockingSide( Side.RIGHT );
		pinButton( );
		pinButtonDelegate( ).corner( Corner.TOP_LEFT ).side( Side.LEFT );
		
		setUnderpaintBorder( GradientFillBorder.from( Side.TOP ).to( Side.BOTTOM ).colors(
				ColorUtils.darkerColor( getBackground( ) , 0.05 ) ,
				ColorUtils.darkerColor( Color.LIGHT_GRAY , 0.05 ) ) );
		setBorder( new OverrideInsetsBorder(
				new InnerGradientBorder( new Insets( 0 , 5 , 0 , 0 ) , Color.GRAY ) ,
				new Insets( 3 , 8 , 3 , 3 ) ) );
		
		createComponents( );
		createLayout( );
		createListeners( );
		createBindings( );
		
		binder.modelToView( );
	}
	
	private void createComponents( )
	{
		updateViewButton = new JButton( "Update View" );
		
		viewButtonsPanel = new ViewButtonsPanel( );
		
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
		
		paramColorationAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		paramColorationAxisPanel = PaintablePanel.wrap( paramColorationAxis );
		paramColorationAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { Color.RED , Color.BLUE } ) );
		
		paramColorationAxis.setForeground( Color.WHITE );
		paramColorationAxis.setMajorTickColor( Color.WHITE );
		paramColorationAxis.setMinorTickColor( Color.WHITE );
		
		inferDepthAxisTiltButton = new JButton( "Infer Depth Axis Tilt" );
		resetDepthAxisTiltButton = new JButton( "Reset Depth Axis Tilt" );
		
		highlightDistAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		highlightDistAxisPanel = PaintablePanel.wrap( highlightDistAxis );
		highlightDistAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { Color.YELLOW , ColorUtils.alphaColor( Color.YELLOW , 0 ) } ) );
		
		highlightDistAxis.setForeground( Color.BLACK );
		highlightDistAxis.setMajorTickColor( Color.BLACK );
		highlightDistAxis.setMinorTickColor( Color.BLACK );
		
		mouseSensitivitySlider = new JSlider( );
		mouseSensitivitySlider.setValue( 20 );
		mouseSensitivitySlider.setOpaque( false );
		
		fitViewToSelectedButton = new JButton( "Fit View to Selected" );
		fitViewToEverythingButton = new JButton( "Fit View to Everything" );
		orbitToPlanButton = new JButton( "Orbit to Plan" );
		
		filterTypeSelector = new DefaultSelector<FilterType>( );
		filterTypeSelector.setAvailableValues( Arrays.asList( FilterType.values( ) ) );
		
		debugButton = new JButton( "Debug" );
	}
	
	private void createLayout( )
	{
		GridBagWizard w = GridBagWizard.create( this );
		w.defaults( ).autoinsets( new DefaultAutoInsets( 3 , 3 ) );
		w.put( updateViewButton ).xy( 0 , 0 ).fillx( 1.0 ).insets( 3 , 3 , 3 , 3 );
		w.put( viewButtonsPanel ).belowLast( ).insets( 23 , 3 , 3 , 3 );
		w.put( fitViewToSelectedButton ).belowLast( ).fillx( 1.0 );
		w.put( fitViewToEverythingButton ).belowLast( ).fillx( 1.0 );
		w.put( orbitToPlanButton ).belowLast( ).fillx( 1.0 );
		JLabel sensLabel = new JLabel( "Mouse Sensitivity:" );
		w.put( sensLabel ).belowLast( ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( mouseSensitivitySlider ).belowLast( ).fillx( ).north( );
		JLabel distLabel = new JLabel( "Distance coloration:" );
		w.put( distLabel ).belowLast( ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( distColorationAxisPanel ).belowLast( ).fillx( );
		JLabel paramLabel = new JLabel( "Depth coloration:" );
		w.put( paramLabel ).belowLast( ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( paramColorationAxisPanel ).belowLast( ).fillx( );
		w.put( inferDepthAxisTiltButton ).belowLast( ).fillx( );
		w.put( resetDepthAxisTiltButton ).belowLast( ).fillx( );
		JLabel highlightRangeLabel = new JLabel( "Highlight range:" );
		w.put( highlightRangeLabel ).belowLast( ).west( ).insets( 13 , 3 , 3 , 3 );
		w.put( highlightDistAxisPanel ).belowLast( ).fillx( );
		JLabel filterTypeLabel = new JLabel( "Filter type:" );
		w.put( filterTypeLabel ).belowLast( ).west( ).insets( 43 , 3 , 3 , 3 );
		w.put( filterTypeSelector.getComboBox( ) ).belowLast( ).fillx( );
		
		JPanel paddingPanel = new JPanel( );
		paddingPanel.setOpaque( false );
		w.put( paddingPanel ).belowLast( ).fillboth( 1.0 , 1.0 );
		
		w.put( debugButton ).belowLast( ).southwest( );
		
		debugButton.setVisible( false );
	}
	
	private void createListeners( )
	{
		new PlotAxisController( distColorationAxis );
		new PlotAxisController( paramColorationAxis );
		new PlotAxisController( highlightDistAxis );
	}
	
	private void createBindings( )
	{
		bind( binder , viewButtonsPanel.getPlanButton( ) , Model.cameraView , CameraView.PLAN );
		bind( binder , viewButtonsPanel.getPerspectiveButton( ) , Model.cameraView , CameraView.PERSPECTIVE );
		bind( binder , viewButtonsPanel.getNorthButton( ) , Model.cameraView , CameraView.NORTH_FACING_PROFILE );
		bind( binder , viewButtonsPanel.getSouthButton( ) , Model.cameraView , CameraView.SOUTH_FACING_PROFILE );
		bind( binder , viewButtonsPanel.getEastButton( ) , Model.cameraView , CameraView.EAST_FACING_PROFILE );
		bind( binder , viewButtonsPanel.getWestButton( ) , Model.cameraView , CameraView.WEST_FACING_PROFILE );
		bind( binder , mouseSensitivitySlider , Model.mouseSensitivity );
		binder.bind( new PlotAxisConversionBinding( Model.distRange , distColorationAxis ) );
		binder.bind( new PlotAxisConversionBinding( Model.paramRange , paramColorationAxis ) );
		binder.bind( new PlotAxisConversionBinding( Model.highlightRange , highlightDistAxis ) );
		bind( binder , filterTypeSelector , Model.filterType );
	}
	
	public JButton updateViewButton( )
	{
		return updateViewButton;
	}
	
	public void setModel( YamlObject<Model> model )
	{
		binder.setModel( model );
		binder.modelToView( );
	}
	
	public YamlObject<Model> getModel( )
	{
		return binder.getModel( );
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
	
	public PlotAxis getHighlightDistAxis( )
	{
		return highlightDistAxis;
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
		REGEXP( "Regular Expression" );
		
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
	
	public static class Model extends YamlSpec<Model>
	{
		public static final Attribute<CameraView>			cameraView			= enumAttribute( "cameraView" , CameraView.class );
		public static final Attribute<Integer>				mouseSensitivity	= integerAttribute( "mouseSensitivity" );
		public static final Attribute<LinearAxisConversion>	distRange			= Attribute.newInstance( LinearAxisConversion.class , "distRange" , new LinearAxisConversionYamlBimapper( ) );
		public static final Attribute<LinearAxisConversion>	paramRange			= Attribute.newInstance( LinearAxisConversion.class , "paramRange" , new LinearAxisConversionYamlBimapper( ) );
		public static final Attribute<LinearAxisConversion>	highlightRange		= Attribute.newInstance( LinearAxisConversion.class , "highlightRange" , new LinearAxisConversionYamlBimapper( ) );
		public static final Attribute<FilterType>			filterType			= enumAttribute( "filterType" , FilterType.class );
		
		private Model( )
		{
			super( );
		}
		
		public static final Model	instance	= new Model( );
	}
	
	public PlotAxis highlightDistAxis( )
	{
		return highlightDistAxis;
	}
	
	public JButton getInferDepthAxisTiltButton( )
	{
		return inferDepthAxisTiltButton;
	}
	
	public JButton getResetDepthAxisTiltButton( )
	{
		return resetDepthAxisTiltButton;
	}
}

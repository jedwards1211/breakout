package org.andork.breakout;

import static org.andork.bind.EqualsBinder.bindEquals;
import static org.andork.func.CompoundBimapper.compose;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
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
import org.andork.awt.layout.BetterCardLayout;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.bind.BimapperBinder;
import org.andork.bind.Binder;
import org.andork.bind.BinderWrapper;
import org.andork.bind.QMapKeyedBinder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.bind.ui.BetterCardLayoutBinder;
import org.andork.bind.ui.ButtonSelectedBinder;
import org.andork.bind.ui.ComponentBackgroundBinder;
import org.andork.bind.ui.ComponentTextBinder;
import org.andork.bind.ui.ISelectorSelectionBinder;
import org.andork.bind.ui.JSliderValueBinder;
import org.andork.breakout.model.ColorParam;
import org.andork.breakout.model.ProjectModel;
import org.andork.breakout.model.RootModel;
import org.andork.collect.Visitor;
import org.andork.func.FileStringBimapper;
import org.andork.func.LinearFloatBimapper;
import org.andork.func.RoundingFloat2IntegerBimapper;
import org.andork.plot.PlotAxisConversionBinder;
import org.andork.q.QMap;
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

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.PlotAxis;
import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;
import com.andork.plot.PlotAxisController;

@SuppressWarnings( "serial" )
public class SettingsDrawer extends Drawer
{
	Localizer											localizer;
	
	JLabel												projectFileLabel;
	JTextField											projectFileField;
	JButton												projectFileMenuButton;
	
	ViewButtonsPanel									viewButtonsPanel;
	JLabel												mouseSensitivityLabel;
	JSlider												mouseSensitivitySlider;
	JLabel												mouseWheelSensitivityLabel;
	JSlider												mouseWheelSensitivitySlider;
	JLabel												bgColorLabel;
	JXColorSelectionButton								bgColorButton;
	JLabel												ambientLightLabel;
	JSlider												ambientLightSlider;
	JLabel												distColorationLabel;
	PlotAxis											distColorationAxis;
	PaintablePanel										distColorationAxisPanel;
	
	JLabel												colorParamLabel;
	DefaultSelector<ColorParam>							colorParamSelector;
	JPanel												colorParamButtonsPanel;
	BetterCardLayout									colorParamButtonsLayout;
	JButton												fitParamColorationAxisButton;
	JButton												flipParamColorationAxisButton;
	JPanel												colorParamDetailsPanel;
	BetterCardLayout									colorParamDetailsLayout;
	PlotAxis											paramColorationAxis;
	PaintablePanel										paramColorationAxisPanel;
	
	JPanel												colorByDepthButtonsPanel;
	JButton												inferDepthAxisTiltButton;
	JButton												resetDepthAxisTiltButton;
	
	JPanel												colorByDistanceButtonsPanel;
	JButton												recalcColorByDistanceButton;
	
	JLabel												glowDistLabel;
	PlotAxis											glowDistAxis;
	PaintablePanel										glowDistAxisPanel;
	JButton												fitViewToEverythingButton;
	JButton												fitViewToSelectedButton;
	JButton												orbitToPlanButton;
	JButton												debugButton;
	
	JLabel												numSamplesLabel;
	JSlider												numSamplesSlider;
	
	JLabel												filterTypeLabel;
	DefaultSelector<FilterType>							filterTypeSelector;
	
	JLabel												versionLabel;
	
	JPanel												mainPanel;
	JScrollPane											mainPanelScrollPane;
	
	BinderWrapper<QObject<RootModel>>					rootBinder					= new BinderWrapper<QObject<RootModel>>( );
	Binder<File>										currentProjectFileBinder	= QObjectAttributeBinder.bind( RootModel.currentProjectFile , rootBinder );
	Binder<Integer>										desiredNumSamplesBinder		= QObjectAttributeBinder.bind( RootModel.desiredNumSamples , rootBinder );
	Binder<Integer>										mouseSensitivityBinder		= QObjectAttributeBinder.bind( RootModel.mouseSensitivity , rootBinder );
	Binder<Integer>										mouseWheelSensitivityBinder	= QObjectAttributeBinder.bind( RootModel.mouseWheelSensitivity , rootBinder );
	
	BinderWrapper<QObject<ProjectModel>>				projectBinder				= new BinderWrapper<QObject<ProjectModel>>( );
	Binder<CameraView>									cameraViewBinder			= QObjectAttributeBinder.bind( ProjectModel.cameraView , projectBinder );
	Binder<Color>										backgroundColorBinder		= QObjectAttributeBinder.bind( ProjectModel.backgroundColor , projectBinder );
	Binder<LinearAxisConversion>						distRangeBinder				= QObjectAttributeBinder.bind( ProjectModel.distRange , projectBinder );
	Binder<ColorParam>									colorParamBinder			= QObjectAttributeBinder.bind( ProjectModel.colorParam , projectBinder );
	Binder<QMap<ColorParam, LinearAxisConversion, ?>>	paramRangesBinder			= QObjectAttributeBinder.bind( ProjectModel.paramRanges , projectBinder );
	Binder<LinearAxisConversion>						paramRangeBinder			= QMapKeyedBinder.bindKeyed( colorParamBinder , paramRangesBinder );
	Binder<LinearAxisConversion>						highlightRangeBinder		= QObjectAttributeBinder.bind( ProjectModel.highlightRange , projectBinder );
	Binder<Float>										ambientLightBinder			= QObjectAttributeBinder.bind( ProjectModel.ambientLight , projectBinder );
	Binder<FilterType>									filterTypeBinder			= QObjectAttributeBinder.bind( ProjectModel.filterType , projectBinder );
	
	public SettingsDrawer( final I18n i18n , Binder<QObject<RootModel>> rootBinder , Binder<QObject<ProjectModel>> projectBinder )
	{
		this.rootBinder.bind( rootBinder );
		this.projectBinder.bind( projectBinder );
		
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
				
				org.andork.awt.AWTUtil.traverse( SettingsDrawer.this , new Visitor<Component>( )
				{
					@Override
					public boolean visit( Component t )
					{
						if( t instanceof AbstractButton )
						{
							AbstractButton button = ( AbstractButton ) t;
							button.setOpaque( false );
						}
						return true;
					}
				} );
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
		ImageIcon dropdownIcon = new ImageIcon( getClass( ).getResource( "dropdown.png" ) );
		projectFileMenuButton = new JButton( dropdownIcon );
		projectFileMenuButton.setMargin( new Insets( 2 , 4 , 2 , 4 ) );
		
		viewButtonsPanel = new ViewButtonsPanel( );
		
		Color darkColor = new Color( 255 * 3 / 10 , 255 * 3 / 10 , 255 * 3 / 10 );

		bgColorLabel = new JLabel( );
		localizer.setText( bgColorLabel , "bgColorLabel.text" );
		
		bgColorButton = new JXColorSelectionButton( );

		ambientLightLabel = new JLabel( );
		localizer.setText( ambientLightLabel , "ambientLightLabel.text" );
		
		ambientLightSlider = new JSlider( 0 , 100 , 50 );
		ambientLightSlider.setOpaque( false );
		
		distColorationLabel = new JLabel( );
		localizer.setText( distColorationLabel , "distColorationLabel.text" );

		distColorationAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		distColorationAxisPanel = PaintablePanel.wrap( distColorationAxis );
		distColorationAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { ColorUtils.alphaColor( darkColor , 0 ) , darkColor } ) );
		
		distColorationAxis.getAxisConversion( ).set( 0 , 0 , 10000 , 200 );
		distColorationAxis.setForeground( Color.WHITE );
		distColorationAxis.setMajorTickColor( Color.WHITE );
		distColorationAxis.setMinorTickColor( Color.WHITE );
		
		colorParamLabel = new JLabel( );
		localizer.setText( colorParamLabel , "colorParamLabel.text" );
		colorParamSelector = new DefaultSelector<ColorParam>( );
		colorParamSelector.setAvailableValues( ColorParam.values( ) );
		colorParamButtonsPanel = new JPanel( );
		colorParamButtonsPanel.setOpaque( false );
		fitParamColorationAxisButton = new JButton( new ImageIcon( getClass( ).getResource( "fit.png" ) ) );
		fitParamColorationAxisButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
		localizer.setToolTipText( fitParamColorationAxisButton , "fitParamColorationAxisButton.tooltip" );
		flipParamColorationAxisButton = new JButton( new ImageIcon( getClass( ).getResource( "flip.png" ) ) );
		flipParamColorationAxisButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
		localizer.setToolTipText( flipParamColorationAxisButton , "flipParamColorationAxisButton.tooltip" );
		colorParamDetailsPanel = new JPanel( );
		colorParamDetailsPanel.setOpaque( false );

		paramColorationAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		paramColorationAxisPanel = PaintablePanel.wrap( paramColorationAxis );
		paramColorationAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 0.24f , 0.64f , 1f } ,
						new Color[ ] { new Color( 255 , 249 , 204 ) , new Color( 255 , 195 , 0 ) , new Color( 214 , 6 , 127 ) , new Color( 34 , 19 , 150 ) } ) );
		
		paramColorationAxis.setForeground( Color.WHITE );
		paramColorationAxis.setMajorTickColor( Color.WHITE );
		paramColorationAxis.setMinorTickColor( Color.WHITE );
		
		colorByDepthButtonsPanel = new JPanel( );
		colorByDepthButtonsPanel.setOpaque( false );
		inferDepthAxisTiltButton = new JButton( new ImageIcon( getClass( ).getResource( "tilted.png" ) ) );
		inferDepthAxisTiltButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
		localizer.setToolTipText( inferDepthAxisTiltButton , "inferDepthAxisTiltButton.tooltip" );
		resetDepthAxisTiltButton = new JButton( new ImageIcon( getClass( ).getResource( "straight-down.png" ) ) );
		resetDepthAxisTiltButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
		localizer.setToolTipText( resetDepthAxisTiltButton , "resetDepthAxisTiltButton.tooltip" );
		
		colorByDistanceButtonsPanel = new JPanel( );
		colorByDistanceButtonsPanel.setOpaque( false );
		recalcColorByDistanceButton = new JButton( new ImageIcon( getClass( ).getResource( "refresh.png" ) ) );
		recalcColorByDistanceButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
		localizer.setToolTipText( recalcColorByDistanceButton , "recalcColorByDistanceButton.tooltip" );
		
		glowDistLabel = new JLabel( );
		localizer.setText( glowDistLabel , "highlightRangeLabel.text" );
		
		glowDistAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		glowDistAxisPanel = PaintablePanel.wrap( glowDistAxis );
		glowDistAxisPanel.setUnderpaintBorder(
				MultipleGradientFillBorder.from( Side.LEFT ).to( Side.RIGHT ).linear(
						new float[ ] { 0f , 1f } , new Color[ ] { Color.CYAN , ColorUtils.alphaColor( Color.CYAN , 0 ) } ) );
		
		glowDistAxis.setForeground( Color.BLACK );
		glowDistAxis.setMajorTickColor( Color.BLACK );
		glowDistAxis.setMinorTickColor( Color.BLACK );
		
		mouseSensitivityLabel = new JLabel( );
		localizer.setText( mouseSensitivityLabel , "mouseSensitivityLabel.text" );

		mouseSensitivitySlider = new JSlider( );
		mouseSensitivitySlider.setValue( 20 );
		mouseSensitivitySlider.setOpaque( false );
		
		mouseWheelSensitivityLabel = new JLabel( );
		localizer.setText( mouseWheelSensitivityLabel , "mouseWheelSensitivityLabel.text" );

		mouseWheelSensitivitySlider = new JSlider( 1 , 2000 , 200 );
		mouseWheelSensitivitySlider.setOpaque( false );
		
		fitViewToSelectedButton = new JButton( "Fit View to Selected" );
		fitViewToEverythingButton = new JButton( "Fit View to Everything" );
		orbitToPlanButton = new JButton( "Orbit to Plan" );
		
		filterTypeLabel = new JLabel( );
		localizer.setText( filterTypeLabel , "filterTypeLabel.text" );

		filterTypeSelector = new DefaultSelector<FilterType>( );
		filterTypeSelector.setAvailableValues( Arrays.asList( FilterType.values( ) ) );
		
		numSamplesLabel = new JLabel( );
		localizer.setText( numSamplesLabel , "numSamplesLabel.text.off" );
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
		
		Dimension iconButtonSize = flipParamColorationAxisButton.getPreferredSize( );
		iconButtonSize.height = colorParamSelector.getComboBox( ).getPreferredSize( ).height;
		fitParamColorationAxisButton.setPreferredSize( iconButtonSize );
		recalcColorByDistanceButton.setPreferredSize( iconButtonSize );
		inferDepthAxisTiltButton.setPreferredSize( iconButtonSize );
		resetDepthAxisTiltButton.setPreferredSize( iconButtonSize );
		
		versionLabel = new JLabel( );
		versionLabel.setHorizontalAlignment( JLabel.CENTER );
		Properties versionProperties = loadVersionProperties( );
		String version = versionProperties.getProperty( "version" , "unknown" );
		String buildDate = versionProperties.getProperty( "build.date" , "unknown" );
		
		localizer.setFormattedText( versionLabel , "versionLabel.text" , version , buildDate );
	}
	
	private Properties loadVersionProperties( )
	{
		Properties props = new Properties( );
		try
		{
			props.load( getClass( ).getResourceAsStream( "version.properties" ) );
		}
		catch( Exception ex )
		{
			
		}
		return props;
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
		
		w.put( viewButtonsPanel ).belowLast( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( fitViewToSelectedButton ).belowLast( ).fillx( 1.0 );
		w.put( fitViewToEverythingButton ).belowLast( ).fillx( 1.0 );
		w.put( orbitToPlanButton ).belowLast( ).fillx( 1.0 );
		w.put( mouseSensitivityLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( mouseSensitivitySlider ).belowLast( ).fillx( ).north( );
		
		w.put( mouseWheelSensitivityLabel ).belowLast( ).west( ).addToInsets( 0 , 0 , 0 , 0 );
		w.put( mouseWheelSensitivitySlider ).belowLast( ).fillx( ).north( );
		
		GridBagWizard bgPanel = GridBagWizard.quickPanel( );
		bgPanel.put( bgColorLabel ).xy( 0 , 0 ).west( );
		bgPanel.put( bgColorButton ).rightOfLast( ).west( ).weightx( 1.0 );
		w.put( bgPanel.getTarget( ) ).belowLast( ).fillx( ).addToInsets( 10 , 0 , 0 , 0 );
		
		w.put( new JLabel( "Ambient Light:" ) ).belowLast( ).west( );
		w.put( ambientLightSlider ).belowLast( ).fillx( );
		
		w.put( distColorationLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( distColorationAxisPanel ).belowLast( ).fillx( );
		
		GridBagWizard colorParamPanel = GridBagWizard.quickPanel( );
		colorParamPanel.put( colorParamLabel ).xy( 0 , 0 ).filly( ).west( );
		colorParamPanel.put( colorParamSelector.getComboBox( ) ).rightOfLast( ).fillboth( 1.0 , 0.0 ).addToInsets( 0 , 5 , 0 , 0 );
		colorParamButtonsPanel.setLayout( colorParamButtonsLayout = new BetterCardLayout( ) );
		colorParamButtonsLayout.setSizeHidden( false );
		colorParamPanel.put( colorParamButtonsPanel ).rightOfLast( ).filly( 1.0 );
		colorParamPanel.put( fitParamColorationAxisButton ).rightOfLast( ).filly( );
		colorParamPanel.put( flipParamColorationAxisButton ).rightOfLast( ).filly( );
		w.put( colorParamPanel.getTarget( ) ).belowLast( ).fillx( ).addToInsets( 10 , 0 , 0 , 0 );
		colorParamDetailsPanel.setLayout( colorParamDetailsLayout = new BetterCardLayout( ) );
		colorParamDetailsLayout.setSizeHidden( false );
		w.put( colorParamDetailsPanel ).belowLast( ).fillx( );
		w.put( paramColorationAxisPanel ).belowLast( ).fillx( );
		
		colorByDepthButtonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT , 0 , 0 ) );
		colorByDepthButtonsPanel.add( inferDepthAxisTiltButton );
		colorByDepthButtonsPanel.add( resetDepthAxisTiltButton );
		colorParamButtonsPanel.add( colorByDepthButtonsPanel , ColorParam.DEPTH );
		
		colorByDistanceButtonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT , 0 , 0 ) );
		colorByDistanceButtonsPanel.add( recalcColorByDistanceButton );
		colorParamButtonsPanel.add( colorByDistanceButtonsPanel , ColorParam.DISTANCE_ALONG_SHOTS );
		
		w.put( glowDistLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( glowDistAxisPanel ).belowLast( ).fillx( );
		w.put( numSamplesLabel ).belowLast( ).west( ).addToInsets( 10 , 0 , 0 , 0 );
		w.put( numSamplesSlider ).belowLast( ).fillx( );
		
		w.put( filterTypeLabel ).belowLast( ).west( ).insets( 40 , 0 , 0 , 0 );
		w.put( filterTypeSelector.getComboBox( ) ).belowLast( ).fillx( );
		
		w.put( versionLabel ).belowLast( ).south( ).weighty( 1.0 ).fillx( );
		
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
		ComponentTextBinder.bind( projectFileField , BimapperBinder.bind( FileStringBimapper.instance , currentProjectFileBinder ) );
		
		ComponentBackgroundBinder.bind( bgColorButton , backgroundColorBinder );
		
		ButtonSelectedBinder.bind( viewButtonsPanel.getPlanButton( ) , bindEquals( CameraView.PLAN , cameraViewBinder ) );
		ButtonSelectedBinder.bind( viewButtonsPanel.getPerspectiveButton( ) , bindEquals( CameraView.PERSPECTIVE , cameraViewBinder ) );
		ButtonSelectedBinder.bind( viewButtonsPanel.getNorthButton( ) , bindEquals( CameraView.NORTH_FACING_PROFILE , cameraViewBinder ) );
		ButtonSelectedBinder.bind( viewButtonsPanel.getSouthButton( ) , bindEquals( CameraView.SOUTH_FACING_PROFILE , cameraViewBinder ) );
		ButtonSelectedBinder.bind( viewButtonsPanel.getEastButton( ) , bindEquals( CameraView.EAST_FACING_PROFILE , cameraViewBinder ) );
		ButtonSelectedBinder.bind( viewButtonsPanel.getWestButton( ) , bindEquals( CameraView.WEST_FACING_PROFILE , cameraViewBinder ) );
		JSliderValueBinder.bind( mouseSensitivitySlider , mouseSensitivityBinder );
		JSliderValueBinder.bind( mouseWheelSensitivitySlider , mouseWheelSensitivityBinder );
		PlotAxisConversionBinder.bind( distColorationAxis , distRangeBinder );
		ISelectorSelectionBinder.bind( colorParamSelector , colorParamBinder );
		BetterCardLayoutBinder.bind( colorParamDetailsPanel , colorParamDetailsLayout , colorParamBinder );
		BetterCardLayoutBinder.bind( colorParamButtonsPanel , colorParamButtonsLayout , colorParamBinder );
		PlotAxisConversionBinder.bind( paramColorationAxis , paramRangeBinder );
		PlotAxisConversionBinder.bind( glowDistAxis , highlightRangeBinder );
		ISelectorSelectionBinder.bind( filterTypeSelector , filterTypeBinder );
		JSliderValueBinder.bind( ambientLightSlider ,
				BimapperBinder.bind( compose( new LinearFloatBimapper( 0f , 0f , 1f , ambientLightSlider.getMaximum( ) ) ,
						RoundingFloat2IntegerBimapper.instance ) , ambientLightBinder ) );
		
		JSliderValueBinder.bind( numSamplesSlider , desiredNumSamplesBinder );
	}
	
	public void setMaxNumSamples( int maxNumSamples )
	{
		if( maxNumSamples != numSamplesSlider.getMaximum( ) )
		{
			Integer value = rootBinder.get( ).get( RootModel.desiredNumSamples );
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
		localizer.unregister( numSamplesLabel );
		if( numSamplesSlider.getValue( ) < 2 )
		{
			localizer.setText( numSamplesLabel , "numSamplesLabel.text.off" );
		}
		else
		{
			localizer.setFormattedText( numSamplesLabel , "numSamplesLabel.text.on" , numSamplesSlider.getValue( ) );
		}
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
	
	public JButton getInferDepthAxisTiltButton( )
	{
		return inferDepthAxisTiltButton;
	}
	
	public JButton getResetDepthAxisTiltButton( )
	{
		return resetDepthAxisTiltButton;
	}
	
	public AbstractButton getFitParamColorationAxisButton( )
	{
		return fitParamColorationAxisButton;
	}
	
	public AbstractButton getFlipParamColorationAxisButton( )
	{
		return flipParamColorationAxisButton;
	}
	
	public AbstractButton getRecalcColorByDistanceButton( )
	{
		return recalcColorByDistanceButton;
	}
}

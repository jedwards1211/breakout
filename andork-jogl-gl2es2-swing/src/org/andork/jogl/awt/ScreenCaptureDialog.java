package org.andork.jogl.awt;

import static javax.media.opengl.GL.GL_BGRA;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT0;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_ATTACHMENT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_COMPONENT32;
import static javax.media.opengl.GL.GL_FRAMEBUFFER;
import static javax.media.opengl.GL.GL_RENDERBUFFER;
import static javax.media.opengl.GL.GL_RGB;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLProfile;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.PlainDocument;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.IconScaler;
import org.andork.awt.LocalizedException;
import org.andork.awt.layout.RectangleUtils;
import org.andork.bind.Binder;
import org.andork.bind.BinderWrapper;
import org.andork.bind.DefaultBinder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.bind.ui.ComponentTextBinder;
import org.andork.bind.ui.ISelectorSelectionBinder;
import org.andork.bind.ui.JSpinnerValueBinder;
import org.andork.jogl.awt.ScreenCaptureDialogModel.ResolutionUnit;
import org.andork.jogl.neu.JoglScene;
import org.andork.q.QObject;
import org.andork.swing.BetterSpinnerNumberModel;
import org.andork.swing.OnEDT;
import org.andork.swing.async.SelfReportingTask;
import org.andork.swing.async.SingleThreadedTaskService;
import org.andork.swing.async.TaskService;
import org.andork.swing.border.InnerGradientBorder;
import org.andork.swing.event.EasyDocumentListener;
import org.andork.swing.selector.DefaultSelector;
import org.andork.swing.selector.ISelector;
import org.andork.swing.selector.ISelectorListener;
import org.andork.swing.text.Formats;
import org.andork.swing.text.PatternDocumentFilter;
import org.andork.swing.text.Patterns;
import org.andork.swing.text.SimpleFormatter;
import org.andork.swing.text.SimpleSpinnerEditor;
import org.andork.util.Format;
import org.andork.util.StringUtils;

import com.jogamp.nativewindow.awt.DirectDataBufferInt;
import com.jogamp.nativewindow.awt.DirectDataBufferInt.BufferedImageInt;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;

@SuppressWarnings( "serial" )
public class ScreenCaptureDialog extends JDialog
{
	private static final BigDecimal								IN_TO_CM				= new BigDecimal( "2.54" );
	private static final BigDecimal								CM_TO_IN				= new BigDecimal( 1.0 / IN_TO_CM.doubleValue( ) );
	
	Localizer													localizer;
	
	JPanel														canvasHolder;
	NewtCanvasAWT												canvas;
	GLContext													glContext;
	GLWindow													newtWindow;
	Renderer													renderer				= new Renderer( );
	
	JFileChooser												outputDirectoryChooser;
	
	JLabel														outputDirectoryLabel;
	JTextField													outputDirectoryField;
	JButton														chooseOutputDirectoryButton;
	
	JLabel														fileNamePrefixLabel;
	JTextField													fileNamePrefixField;
	
	JLabel														fileNumberLabel;
	JSpinner													fileNumberSpinner;
	
	JLabel														outputFormatLabel;
	DefaultSelector<?>											outputFormatSelector;
	
	Icon														warningIcon				= IconScaler.rescale( UIManager.getIcon( "OptionPane.warningIcon" ) , 20 , 20 );
	JLabel														outputFileOrWarningLabel;
	JPanel														outputFileOrWarningLabelHolder;
	
	JLabel														pixelSizeHeaderLabel;
	JLabel														pixelWidthLabel;
	JSpinner													pixelWidthSpinner;
	JLabel														pixelWidthUnitLabel;
	JLabel														pixelHeightLabel;
	JSpinner													pixelHeightSpinner;
	JLabel														pixelHeightUnitLabel;
	JLabel														resolutionLabel;
	JSpinner													resolutionSpinner;
	DefaultSelector<ScreenCaptureDialogModel.ResolutionUnit>	resolutionUnitSelector;
	
	JLabel														printSizeHeaderLabel;
	JLabel														printWidthLabel;
	JSpinner													printWidthSpinner;
	JLabel														printHeightLabel;
	JSpinner													printHeightSpinner;
	JLabel														printUnitLabel;
	DefaultSelector<ScreenCaptureDialogModel.PrintSizeUnit>		printUnitSelector;
	
	JLabel														numSamplesLabel;
	JSlider														numSamplesSlider;
	
	JButton														exportButton;
	JButton														cancelButton;
	
	boolean														updating;
	
	BinderWrapper<QObject<ScreenCaptureDialogModel>>			binder					= new BinderWrapper<QObject<ScreenCaptureDialogModel>>( );
	QObjectAttributeBinder<String>								outputDirectoryBinder	= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.outputDirectory , binder );
	QObjectAttributeBinder<String>								fileNamePrefixBinder	= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.fileNamePrefix , binder );
	QObjectAttributeBinder<Integer>								fileNumberBinder		= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.fileNumber , binder );
	QObjectAttributeBinder<Integer>								pixelWidthBinder		= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.pixelWidth , binder );
	QObjectAttributeBinder<Integer>								pixelHeightBinder		= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.pixelHeight , binder );
	QObjectAttributeBinder<BigDecimal>							resolutionBinder		= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.resolution , binder );
	QObjectAttributeBinder<ResolutionUnit>						resolutionUnitBinder	= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.resolutionUnit , binder );
	QObjectAttributeBinder<Integer>								numSamplesBinder		= QObjectAttributeBinder.bind( ScreenCaptureDialogModel.numSamples , binder );
	
	JoglScene													scene;
	
	TaskService													taskService;
	
	public static void main( String[ ] args )
	{
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
				
				ScreenCaptureDialog dialog = new ScreenCaptureDialog( null , new I18n( ) );
				
				Binder<QObject<ScreenCaptureDialogModel>> binder = new DefaultBinder<QObject<ScreenCaptureDialogModel>>( );
				QObject<ScreenCaptureDialogModel> model = ScreenCaptureDialogModel.instance.newObject( );
				
				model.set( ScreenCaptureDialogModel.outputDirectory , "screenshots" );
				model.set( ScreenCaptureDialogModel.fileNamePrefix , "breakout-screenshot" );
				model.set( ScreenCaptureDialogModel.fileNumber , 1 );
				model.set( ScreenCaptureDialogModel.pixelWidth , 600 );
				model.set( ScreenCaptureDialogModel.pixelHeight , 400 );
				model.set( ScreenCaptureDialogModel.resolution , new BigDecimal( 300 ) );
				model.set( ScreenCaptureDialogModel.resolutionUnit , ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_IN );
				
				dialog.setBinder( binder );
				binder.set( model );
				
				dialog.setSize( 800 , 600 );
				dialog.setLocationRelativeTo( null );
				dialog.setVisible( true );
			}
		};
	}
	
	public ScreenCaptureDialog( GLContext shareWith , I18n i18n )
	{
		super( );
		init( shareWith , i18n );
	}
	
	public ScreenCaptureDialog( Frame owner , GLContext shareWith , I18n i18n )
	{
		super( owner );
		init( shareWith , i18n );
	}
	
	public ScreenCaptureDialog( Dialog owner , GLContext shareWith , I18n i18n )
	{
		super( owner );
		init( shareWith , i18n );
	}
	
	public ScreenCaptureDialog( Window owner , GLContext shareWith , I18n i18n )
	{
		super( owner );
		init( shareWith , i18n );
	}
	
	protected void init( GLContext shareWith , final I18n i18n )
	{
		this.glContext = shareWith;
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				localizer = i18n.forClass( ScreenCaptureDialog.class );
				setModalityType( ModalityType.DOCUMENT_MODAL );
				createComponents( );
				createLayout( );
				createListeners( );
				createBindings( );
			}
		};
		
		taskService = new SingleThreadedTaskService( );
	}
	
	public void setBinder( Binder<QObject<ScreenCaptureDialogModel>> binder )
	{
		this.binder.bind( binder );
	}
	
	public void setScene( JoglScene scene )
	{
		this.scene = scene;
		renderer.setBgColor( scene.getBgColor( ) );
		renderer.setDesiredNumSamples( scene.getDesiredNumSamples( ) );
		renderer.setProjectionCalculator( scene.getProjectionCalculator( ) );
		renderer.setViewXform( scene.viewXform( ) );
		renderer.setRenderToFbo( scene.isRenderToFbo( ) );
		renderer.setDrawablesDirect( scene.getDrawablesDirect( ) );
		
		if( newtWindow != null )
		{
			newtWindow.display( );
		}
	}
	
	protected void createComponents( )
	{
		canvas = new NewtCanvasAWT( );
		canvasHolder = new JPanel( );
		canvasHolder.setBackground( Color.GRAY );
		
		outputDirectoryLabel = new JLabel( );
		localizer.setText( outputDirectoryLabel , "outputDirectoryLabel.text" );
		
		outputDirectoryField = new JTextField( );
		chooseOutputDirectoryButton = new JButton( UIManager.getIcon( "FileView.directoryIcon" ) );
		chooseOutputDirectoryButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
		localizer.setToolTipText( chooseOutputDirectoryButton , "chooseOutputDirectoryButton.toolTipText" );
		
		fileNamePrefixLabel = new JLabel( );
		localizer.setText( fileNamePrefixLabel , "fileNamePrefixLabel.text" );
		fileNamePrefixField = new JTextField( );
		
		fileNumberLabel = new JLabel( );
		localizer.setText( fileNumberLabel , "nextFileNumberLabel.text" );
		fileNumberSpinner = createIntegerSpinner( 4 , 1 );
		
		outputFileOrWarningLabel = new JLabel( );
		outputFileOrWarningLabelHolder = new JPanel( );
		
		pixelSizeHeaderLabel = new JLabel( );
		localizer.setText( pixelSizeHeaderLabel , "pixelSizeHeaderLabel.text" );
		
		pixelWidthLabel = new JLabel( );
		localizer.setText( pixelWidthLabel , "widthLabel.text" );
		pixelWidthSpinner = createIntegerSpinner( 6 , 10 );
		pixelWidthUnitLabel = new JLabel( );
		localizer.setText( pixelWidthUnitLabel , "pixelsLabel.text" );
		
		pixelHeightLabel = new JLabel( );
		localizer.setText( pixelHeightLabel , "heightLabel.text" );
		pixelHeightSpinner = createIntegerSpinner( 6 , 10 );
		pixelHeightUnitLabel = new JLabel( );
		localizer.setText( pixelHeightUnitLabel , "pixelsLabel.text" );
		
		resolutionLabel = new JLabel( );
		localizer.setText( resolutionLabel , "resolutionLabel.text" );
		resolutionSpinner = createBigDecimalSpinner( 4 , 2 , new BigDecimal( 50 ) );
		resolutionUnitSelector = new DefaultSelector<ScreenCaptureDialogModel.ResolutionUnit>( );
		resolutionUnitSelector.setAvailableValues( ScreenCaptureDialogModel.ResolutionUnit.values( ) );
		
		printSizeHeaderLabel = new JLabel( );
		localizer.setText( printSizeHeaderLabel , "printSizeHeaderLabel.text" );
		
		printWidthLabel = new JLabel( );
		localizer.setText( printWidthLabel , "widthLabel.text" );
		printWidthSpinner = createBigDecimalSpinner( 4 , 2 , new BigDecimal( 1 ) );
		printUnitSelector = new DefaultSelector<ScreenCaptureDialogModel.PrintSizeUnit>( );
		printUnitSelector.setAvailableValues( ScreenCaptureDialogModel.PrintSizeUnit.values( ) );
		
		printHeightLabel = new JLabel( );
		localizer.setText( printHeightLabel , "heightLabel.text" );
		printHeightSpinner = createBigDecimalSpinner( 4 , 2 , new BigDecimal( 1 ) );
		printUnitLabel = new JLabel( );
		localizer.setText( printUnitLabel , "inches" );
		
		localizer.register( new Object( ) , new I18nUpdater<Object>( )
		{
			@Override
			public void updateI18n( Localizer localizer , Object localizedObject )
			{
				ScreenCaptureDialogModel.PrintSizeUnit.INCHES.displayName = localizer.getString( "inches" );
				ScreenCaptureDialogModel.PrintSizeUnit.CENTIMETERS.displayName = localizer.getString( "centimeters" );
				
				ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_CM.displayName = localizer.getString( "pixelsPerCm" );
				ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_IN.displayName = localizer.getString( "pixelsPerIn" );
				
				printUnitSelector.getComboBox( ).repaint( );
				resolutionUnitSelector.getComboBox( ).repaint( );
			}
		} );
		
		numSamplesLabel = new JLabel( );
		localizer.setText( numSamplesLabel , "numSamplesLabel.text.off" );
		numSamplesSlider = new JSlider( 1 , 20 , 1 );
		numSamplesSlider.setPreferredSize( new Dimension( 150 , numSamplesSlider.getPreferredSize( ).height ) );
		
		numSamplesLabel.setVisible( false );
		numSamplesSlider.setVisible( false );
		
		exportButton = new JButton( );
		localizer.setText( exportButton , "exportButton.text" );
		
		cancelButton = new JButton( );
		localizer.setText( cancelButton , "cancelButton.text" );
	}
	
	protected void createLayout( )
	{
		outputFileOrWarningLabelHolder.setLayout( new BorderLayout( ) );
		outputFileOrWarningLabelHolder.add( outputFileOrWarningLabel , BorderLayout.CENTER );
		
		GridBagWizard gbw = GridBagWizard.quickPanel( );
		gbw.defaults( ).fillx( 1.0 ).defaultAutoinsets( 0 , 10 );
		
		GridBagWizard dirPanel = GridBagWizard.quickPanel( );
		dirPanel.defaults( ).west( ).defaultAutoinsets( 0 , 2 );
		dirPanel.put( outputDirectoryLabel ).xy( 0 , 0 );
		dirPanel.put( outputDirectoryField ).below( outputDirectoryLabel ).fillboth( 1.0 , 0.0 );
		dirPanel.put( chooseOutputDirectoryButton ).rightOf( outputDirectoryField ).filly( );
		gbw.put( dirPanel.getTarget( ) ).xy( 0 , 0 );
		
		GridBagWizard namePanel = GridBagWizard.quickPanel( );
		
		namePanel.defaults( ).west( );
		namePanel.defaults( ).defaultAutoinsets( 2 , 2 );
		namePanel.put( fileNamePrefixLabel ).xy( 0 , 0 ).addToInsets( 0 , 0 , 0 , 10 );
		namePanel.put( fileNumberLabel ).rightOf( fileNamePrefixLabel );
		namePanel.put( fileNamePrefixField ).below( fileNamePrefixLabel ).fillboth( 1.0 , 0.0 );
		namePanel.put( fileNumberSpinner ).below( fileNumberLabel ).fillboth( );
		namePanel.put( outputFileOrWarningLabelHolder ).below( fileNamePrefixField , fileNumberSpinner ).addToInsets( 10 , 0 , 0 , 0 );
		
		gbw.put( namePanel.getTarget( ) ).below( dirPanel.getTarget( ) );
		
		GridBagWizard sizePanel = GridBagWizard.quickPanel( );
		sizePanel.defaults( ).west( ).defaultAutoinsets( 2 , 2 );
		
		sizePanel.put( pixelSizeHeaderLabel ).xy( 0 , 0 ).width( 3 );
		sizePanel.put( new JSeparator( ) ).below( pixelSizeHeaderLabel ).fillx( ).ipady( 3 );
		sizePanel.put( pixelWidthLabel ).belowLast( ).x( 0 ).width( 1 );
		sizePanel.put( pixelWidthSpinner ).rightOf( pixelWidthLabel );
		sizePanel.put( pixelWidthUnitLabel ).rightOf( pixelWidthSpinner );
		sizePanel.put( pixelHeightLabel ).below( pixelWidthLabel );
		sizePanel.put( pixelHeightSpinner ).below( pixelWidthSpinner );
		sizePanel.put( pixelHeightUnitLabel ).below( pixelWidthUnitLabel );
		sizePanel.put( resolutionLabel ).below( pixelHeightLabel );
		sizePanel.put( resolutionSpinner ).below( pixelHeightSpinner );
		sizePanel.put( resolutionUnitSelector.getComboBox( ) ).below( pixelHeightUnitLabel );
		
		sizePanel.put( pixelWidthLabel , pixelHeightLabel , resolutionLabel ).addToInsets( 0 , 10 , 0 , 0 );
		sizePanel.put( pixelWidthSpinner , pixelHeightSpinner , resolutionSpinner ).fillboth( 1.0 , 0.0 );
		sizePanel.put( resolutionUnitSelector.getComboBox( ) ).fillboth( );
		
		sizePanel.put( printSizeHeaderLabel ).below( resolutionLabel ).width( 3 ).addToInsets( 10 , 0 , 0 , 0 );
		sizePanel.put( new JSeparator( ) ).below( printSizeHeaderLabel ).fillx( ).ipady( 3 );
		sizePanel.put( printWidthLabel ).belowLast( ).x( 0 ).width( 1 );
		sizePanel.put( printWidthSpinner ).rightOf( printWidthLabel );
		sizePanel.put( printUnitSelector.getComboBox( ) ).rightOf( printWidthSpinner );
		sizePanel.put( printHeightLabel ).below( printWidthLabel );
		sizePanel.put( printHeightSpinner ).below( printWidthSpinner );
		sizePanel.put( printUnitLabel ).below( printUnitSelector.getComboBox( ) );
		
		sizePanel.put( printWidthLabel , printHeightLabel ).addToInsets( 0 , 10 , 0 , 0 );
		sizePanel.put( printWidthSpinner , printHeightSpinner ).fillboth( 1.0 , 0.0 );
		sizePanel.put( printUnitSelector.getComboBox( ) ).fillboth( );
		
		sizePanel.put( pixelWidthUnitLabel , pixelHeightUnitLabel , printUnitLabel ).addToInsets( 0 , 4 , 0 , 0 );
		
		gbw.put( sizePanel.getTarget( ) ).below( namePanel.getTarget( ) ).addToInsets( 10 , 0 , 0 , 0 );
		
		GridBagWizard numSamplesPanel = GridBagWizard.quickPanel( );
		
		numSamplesPanel.put( numSamplesLabel , numSamplesSlider ).intoRow( );
		numSamplesPanel.put( numSamplesLabel ).west( ).weightx( 1.0 ).insets( 0 , 0 , 0 , 15 );
		
		gbw.put( numSamplesPanel.getTarget( ) ).belowLast( ).fillx( 1.0 ).addToInsets( 10 , 0 , 0 , 0 );
		
		GridBagWizard buttonPanel = GridBagWizard.quickPanel( );
		buttonPanel.defaults( ).defaultAutoinsets( 2 , 2 );
		buttonPanel.put( exportButton , cancelButton ).intoRow( ).fillx( 1.0 );
		
		gbw.put( buttonPanel.getTarget( ) ).belowLast( ).south( ).fillx( 1.0 ).weighty( 1.0 ).addToInsets( 10 , 0 , 0 , 0 );
		
		JPanel leftPanel = ( JPanel ) gbw.getTarget( );
		leftPanel.setBorder( new CompoundBorder( new InnerGradientBorder( new Insets( 0 , 0 , 0 , 8 ) , new Color( 164 , 164 , 164 ) ) ,
				new EmptyBorder( 5 , 5 , 5 , 2 ) ) );
		
		outputFileOrWarningLabelHolder.setPreferredSize( new Dimension( leftPanel.getPreferredSize( ).width , 40 ) );
		outputFileOrWarningLabelHolder.setMinimumSize( outputFileOrWarningLabelHolder.getPreferredSize( ) );
		
		leftPanel.setMinimumSize( leftPanel.getPreferredSize( ) );
		leftPanel.setPreferredSize( leftPanel.getPreferredSize( ) );
		leftPanel.setMaximumSize( leftPanel.getPreferredSize( ) );
		
		canvasHolder.add( canvas );
		canvasHolder.setLayout( new CanvasHolderLayout( ) );
		
		getContentPane( ).add( leftPanel , BorderLayout.WEST );
		getContentPane( ).add( canvasHolder , BorderLayout.CENTER );
	}
	
	protected void createListeners( )
	{
		resolutionUnitSelector.addSelectorListener( new ISelectorListener<ScreenCaptureDialogModel.ResolutionUnit>( )
		{
			@Override
			public void selectionChanged( ISelector<ScreenCaptureDialogModel.ResolutionUnit> selector , ScreenCaptureDialogModel.ResolutionUnit oldSelection , ScreenCaptureDialogModel.ResolutionUnit newSelection )
			{
				if( updating || newSelection == null )
				{
					return;
				}
				updating = true;
				try
				{
					switch( newSelection )
					{
						case PIXELS_PER_IN:
							printUnitSelector.setSelection( ScreenCaptureDialogModel.PrintSizeUnit.INCHES );
							if( oldSelection == ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_CM )
							{
								scaleUnits( CM_TO_IN );
							}
							break;
						case PIXELS_PER_CM:
							printUnitSelector.setSelection( ScreenCaptureDialogModel.PrintSizeUnit.CENTIMETERS );
							if( oldSelection == ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_IN )
							{
								scaleUnits( IN_TO_CM );
							}
							break;
						default:
							break;
					}
				}
				finally
				{
					updating = false;
				}
			}
		} );
		
		printUnitSelector.addSelectorListener( new ISelectorListener<ScreenCaptureDialogModel.PrintSizeUnit>( )
		{
			@Override
			public void selectionChanged( ISelector<ScreenCaptureDialogModel.PrintSizeUnit> selector , ScreenCaptureDialogModel.PrintSizeUnit oldSelection , ScreenCaptureDialogModel.PrintSizeUnit newSelection )
			{
				if( updating || newSelection == null )
				{
					return;
				}
				updating = true;
				try
				{
					switch( newSelection )
					{
						case INCHES:
							resolutionUnitSelector.setSelection( ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_IN );
							if( oldSelection == ScreenCaptureDialogModel.PrintSizeUnit.CENTIMETERS )
							{
								scaleUnits( CM_TO_IN );
							}
							break;
						case CENTIMETERS:
							resolutionUnitSelector.setSelection( ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_CM );
							if( oldSelection == ScreenCaptureDialogModel.PrintSizeUnit.INCHES )
							{
								scaleUnits( IN_TO_CM );
							}
							break;
						default:
							break;
					}
					printUnitLabel.setText( newSelection.toString( ) );
				}
				finally
				{
					updating = false;
				}
			}
		} );
		
		ChangeListener sizeChangeListener = new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				if( updating )
				{
					return;
				}
				updating = true;
				try
				{
					if( e.getSource( ) == pixelWidthSpinner || e.getSource( ) == pixelHeightSpinner ||
							e.getSource( ) == resolutionSpinner )
					{
						updatePrintSize( );
					}
					else if( e.getSource( ) == printWidthSpinner || e.getSource( ) == printHeightSpinner )
					{
						updatePixelSize( );
					}
					canvasHolder.invalidate( );
					canvasHolder.validate( );
				}
				finally
				{
					updating = false;
				}
			}
		};
		
		pixelWidthSpinner.addChangeListener( sizeChangeListener );
		pixelHeightSpinner.addChangeListener( sizeChangeListener );
		resolutionSpinner.addChangeListener( sizeChangeListener );
		printWidthSpinner.addChangeListener( sizeChangeListener );
		printHeightSpinner.addChangeListener( sizeChangeListener );
		
		chooseOutputDirectoryButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				showOutputDirectoryChooser( );
			}
		} );
		
		class FileNameListener extends EasyDocumentListener implements ChangeListener
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				handleFileNameChange( );
			}
			
			@Override
			public void documentChanged( DocumentEvent e )
			{
				handleFileNameChange( );
			}
		}
		
		FileNameListener fileNameListener = new FileNameListener( );
		fileNumberSpinner.addChangeListener( fileNameListener );
		outputDirectoryField.getDocument( ).addDocumentListener( fileNameListener );
		fileNamePrefixField.getDocument( ).addDocumentListener( fileNameListener );
		
		cancelButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				dispose( );
			}
		} );
		
		exportButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				taskService.submit( new CaptureTask( ) );
			}
		} );
		
		numSamplesSlider.addChangeListener( new ChangeListener( )
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				updateNumSamplesLabel( );
			}
		} );
	}
	
	protected void createBindings( )
	{
		ComponentTextBinder.bind( outputDirectoryField , outputDirectoryBinder );
		ComponentTextBinder.bind( fileNamePrefixField , fileNamePrefixBinder );
		JSpinnerValueBinder.bind( fileNumberSpinner , Integer.class , fileNumberBinder );
		JSpinnerValueBinder.bind( pixelWidthSpinner , Integer.class , pixelWidthBinder );
		JSpinnerValueBinder.bind( pixelHeightSpinner , Integer.class , pixelHeightBinder );
		JSpinnerValueBinder.bind( resolutionSpinner , BigDecimal.class , resolutionBinder );
		ISelectorSelectionBinder.bind( resolutionUnitSelector , resolutionUnitBinder );
	}
	
	private void updateNumSamplesLabel( )
	{
		localizer.unregister( numSamplesLabel );
		
		int value = numSamplesSlider.getValue( );
		if( value <= 1 )
		{
			localizer.setText( numSamplesLabel , "numSamplesLabel.text.off" );
		}
		else
		{
			localizer.setFormattedText( numSamplesLabel , "numSamplesLabel.text.on" , value );
		}
	}
	
	private void showOutputDirectoryChooser( )
	{
		if( outputDirectoryChooser == null )
		{
			outputDirectoryChooser = new JFileChooser( );
			outputDirectoryChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		}
		
		File directory = null;
		
		try
		{
			directory = new File( outputDirectoryField.getText( ) );
		}
		catch( Exception ex )
		{
		}
		
		if( directory != null && directory.exists( ) )
		{
			outputDirectoryChooser.setCurrentDirectory( directory );
		}
		
		int choice = outputDirectoryChooser.showOpenDialog( this );
		if( choice == JFileChooser.APPROVE_OPTION )
		{
			directory = outputDirectoryChooser.getSelectedFile( );
			outputDirectoryField.setText( directory.getAbsolutePath( ) );
		}
	}
	
	private File createOutputFile( ) throws LocalizedException
	{
		if( outputDirectoryField.getText( ) == null || "".equals( outputDirectoryField.getText( ) ) )
		{
			throw new LocalizedException( "createOutputFile.exception.enterDirectory" );
		}
		
		File directory = null;
		try
		{
			directory = new File( outputDirectoryField.getText( ) );
		}
		catch( Exception ex )
		{
			throw new LocalizedException( "createOutputFile.exception.invalidDirectory" ,
					outputDirectoryField.getText( ) , ex.getLocalizedMessage( ) );
		}
		
		if( fileNamePrefixField.getText( ) == null || "".equals( fileNamePrefixField.getText( ) ) )
		{
			throw new LocalizedException( "createOutputFile.exception.enterFileNamePrefix" );
		}
		
		String fileName = fileNamePrefixField.getText( );
		
		if( fileNumberSpinner.getValue( ) instanceof Integer )
		{
			fileName += String.format( "-%04d" , fileNumberSpinner.getValue( ) );
		}
		fileName += ".png";
		
		try
		{
			return new File( directory , fileName );
		}
		catch( Exception ex )
		{
			throw new LocalizedException( "createOutputFile.exception.invalidFileNamePrefix" ,
					fileNamePrefixField.getText( ) , ex.getLocalizedMessage( ) );
		}
	}
	
	private void handleFileNameChange( )
	{
		File outputFile = null;
		try
		{
			outputFile = createOutputFile( );
			localizer.unregister( outputFileOrWarningLabel );
			localizer.setFormattedText( outputFileOrWarningLabel , "fileNameExampleLabel.text" , outputFile );
			outputFileOrWarningLabel.setIcon( null );
		}
		catch( LocalizedException ex )
		{
			localizer.unregister( outputFileOrWarningLabel );
			if( ex.getArgs( ) != null )
			{
				localizer.setFormattedText( outputFileOrWarningLabel , ex.getKey( ) , ex.getArgs( ) );
			}
			else
			{
				localizer.setText( outputFileOrWarningLabel , ex.getKey( ) );
			}
			outputFileOrWarningLabel.setIcon( warningIcon );
		}
		
		exportButton.setEnabled( outputFile != null );
	}
	
	private void scaleUnits( BigDecimal factor )
	{
		BigDecimal resolution = ( BigDecimal ) resolutionSpinner.getValue( );
		if( resolution != null )
		{
			resolutionSpinner.setValue( resolution.divide( factor , RoundingMode.HALF_EVEN ) );
		}
		
		BigDecimal printWidth = ( BigDecimal ) printWidthSpinner.getValue( );
		if( printWidth != null )
		{
			printWidthSpinner.setValue( printWidth.multiply( factor ) );
		}
		
		BigDecimal printHeight = ( BigDecimal ) printHeightSpinner.getValue( );
		if( printHeight != null )
		{
			printHeightSpinner.setValue( printHeight.multiply( factor ) );
		}
	}
	
	private void updatePixelSize( )
	{
		BigDecimal resolution = ( BigDecimal ) resolutionSpinner.getValue( );
		if( resolution == null )
		{
			return;
		}
		
		BigDecimal printWidth = ( BigDecimal ) printWidthSpinner.getValue( );
		if( printWidth != null && printWidth.compareTo( BigDecimal.ZERO ) != 0 )
		{
			pixelWidthSpinner.setValue( resolution.multiply( printWidth ).intValue( ) );
		}
		else
		{
			pixelWidthSpinner.setValue( null );
		}
		
		BigDecimal printHeight = ( BigDecimal ) printHeightSpinner.getValue( );
		if( printHeight != null && printHeight.compareTo( BigDecimal.ZERO ) != 0 )
		{
			pixelHeightSpinner.setValue( resolution.multiply( printHeight ).intValue( ) );
		}
		else
		{
			pixelHeightSpinner.setValue( null );
		}
	}
	
	private void updatePrintSize( )
	{
		BigDecimal resolution = ( BigDecimal ) resolutionSpinner.getValue( );
		if( resolution == null )
		{
			return;
		}
		
		Integer pixelWidth = ( Integer ) pixelWidthSpinner.getValue( );
		if( pixelWidth != null && pixelWidth != 0 )
		{
			printWidthSpinner.setValue( new BigDecimal( pixelWidth ).divide( resolution , 2 , BigDecimal.ROUND_HALF_EVEN ) );
		}
		else
		{
			printWidthSpinner.setValue( null );
		}
		
		Integer pixelHeight = ( Integer ) pixelHeightSpinner.getValue( );
		if( pixelHeight != null && pixelHeight != 0 )
		{
			printHeightSpinner.setValue( new BigDecimal( pixelHeight ).divide( resolution , 2 , BigDecimal.ROUND_HALF_EVEN ) );
		}
		else
		{
			printWidthSpinner.setValue( null );
		}
	}
	
	private JSpinner createIntegerSpinner( int maxDigits , int step )
	{
		BetterSpinnerNumberModel<Integer> model = BetterSpinnerNumberModel.newInstance( 1 , 1 ,
				( int ) Math.floor( Math.pow( 10 , maxDigits ) ) - 1 , step );
		JSpinner spinner = new JSpinner( model );
		SimpleSpinnerEditor editor = new SimpleSpinnerEditor( spinner );
		editor.getTextField( ).setColumns( maxDigits );
		Format<Integer> format = Formats.createIntegerFormat( maxDigits );
		SimpleFormatter formatter = new SimpleFormatter( format );
		formatter.install( editor.getTextField( ) );
		Pattern pattern = Patterns.createNumberPattern( maxDigits , 0 , true );
		PatternDocumentFilter docFilter = new PatternDocumentFilter( pattern );
		( ( PlainDocument ) editor.getTextField( ).getDocument( ) ).setDocumentFilter( docFilter );
		spinner.setEditor( editor );
		editor.getTextField( ).putClientProperty( "value" , spinner.getValue( ) );
		
		return spinner;
	}
	
	private JSpinner createBigDecimalSpinner( int maxIntegerDigits , int fractionDigits , BigDecimal step )
	{
		BigDecimal minFraction = new BigDecimal( StringUtils.multiply( "0" , fractionDigits - 1 ) + "1" );
		
		BetterSpinnerNumberModel<BigDecimal> model = BetterSpinnerNumberModel.newInstance( minFraction , minFraction ,
				new BigDecimal( 10 ).pow( maxIntegerDigits ).subtract( minFraction ) , step );
		JSpinner spinner = new JSpinner( model );
		SimpleSpinnerEditor editor = new SimpleSpinnerEditor( spinner );
		editor.getTextField( ).setColumns( maxIntegerDigits + fractionDigits + 1 );
		Format<BigDecimal> format = Formats.createBigDecimalFormat( maxIntegerDigits , fractionDigits );
		SimpleFormatter formatter = new SimpleFormatter( format );
		formatter.install( editor.getTextField( ) );
		Pattern pattern = Patterns.createNumberPattern( maxIntegerDigits , fractionDigits , true );
		PatternDocumentFilter docFilter = new PatternDocumentFilter( pattern );
		( ( PlainDocument ) editor.getTextField( ).getDocument( ) ).setDocumentFilter( docFilter );
		spinner.setEditor( editor );
		editor.getTextField( ).putClientProperty( "value" , spinner.getValue( ) );
		
		return spinner;
	}
	
	private class CanvasHolderLayout implements LayoutManager
	{
		@Override
		public void addLayoutComponent( String name , Component comp )
		{
		}
		
		@Override
		public void removeLayoutComponent( Component comp )
		{
		}
		
		@Override
		public Dimension preferredLayoutSize( Container parent )
		{
			return canvas.getPreferredSize( );
		}
		
		@Override
		public Dimension minimumLayoutSize( Container parent )
		{
			return canvas.getMinimumSize( );
		}
		
		@Override
		public void layoutContainer( Container parent )
		{
			Rectangle insetBounds = RectangleUtils.insetCopy( new Rectangle( parent.getSize( ) ) , parent.getInsets( ) );
			
			Integer width = ( Integer ) pixelWidthSpinner.getValue( );
			Integer height = ( Integer ) pixelHeightSpinner.getValue( );
			
			if( width != null && height != null && width != 0 && height != 0 )
			{
				Rectangle canvasBounds = new Rectangle( );
				
				if( width * insetBounds.height > insetBounds.width * height )
				{
					canvasBounds.width = insetBounds.width;
					canvasBounds.height = canvasBounds.width * height / width;
				}
				else
				{
					canvasBounds.height = insetBounds.height;
					canvasBounds.width = canvasBounds.height * width / height;
				}
				canvasBounds.x = insetBounds.x + insetBounds.width / 2 - canvasBounds.width / 2;
				canvasBounds.y = insetBounds.y + insetBounds.height / 2 - canvasBounds.height / 2;
				canvas.setBounds( canvasBounds );
				canvas.setVisible( true );
			}
			else
			{
				canvas.setVisible( false );
			}
		}
	}
	
	private class Renderer extends JoglScene
	{
		volatile CaptureTask	captureTask;
		
		final Object			lock	= new Object( );
		boolean					captureFinished;
		BufferedImage			capturedImage;
		
		@Override
		public void drawObjects( GL2ES2 gl )
		{
			super.drawObjects( gl );
			
			if( captureTask != null )
			{
				setCaptureFinished( false );
				
				int[ ] tileWidths = captureTask.tileWidths;
				int[ ] tileHeights = captureTask.tileHeights;
				
				captureTask.setStatus( "Rendering image..." );
				captureTask.setIndeterminate( false );
				captureTask.setCompleted( 0 );
				captureTask.setTotal( tileWidths.length * tileHeights.length );
				
				int[ ] i = new int[ 3 ];
				gl.glGenFramebuffers( 1 , i , 0 );
				gl.glGenRenderbuffers( 2 , i , 1 );
				int fbo = i[ 0 ];
				int colorBuf = i[ 1 ];
				int depthBuf = i[ 2 ];
				
				int bufferWidth = max( tileWidths ), bufferHeight = max( tileHeights );
				int totalWidth = total( tileWidths ), totalHeight = total( tileHeights );
				
				BufferedImage capturedImage = new BufferedImage( totalWidth , totalHeight , BufferedImage.TYPE_INT_ARGB );
				
				try
				{
					gl.glBindFramebuffer( GL_FRAMEBUFFER , fbo );
					gl.glBindRenderbuffer( GL_RENDERBUFFER , colorBuf );
					gl.glRenderbufferStorage( GL_RENDERBUFFER , GL_RGB , bufferWidth , bufferHeight );
					gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_RENDERBUFFER , colorBuf );
					gl.glBindRenderbuffer( GL_RENDERBUFFER , depthBuf );
					gl.glRenderbufferStorage( GL_RENDERBUFFER , GL_DEPTH_COMPONENT32 , bufferWidth , bufferHeight );
					gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_DEPTH_ATTACHMENT , GL_RENDERBUFFER , depthBuf );
					
					Graphics2D g2 = ( Graphics2D ) capturedImage.createGraphics( );
					
					int viewportX = 0;
					for( int tileX = 0 ; tileX < tileWidths.length ; tileX++ )
					{
						int viewportY = 0;
						for( int tileY = 0 ; tileY < tileHeights.length ; tileY++ )
						{
							if( captureTask.isCanceling( ) )
							{
								return;
							}
							
							gl.glViewport( viewportX , viewportY , totalWidth , totalHeight );
							
							gl.glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT );
							
							super.drawObjects( gl );
							
							BufferedImageInt tile = DirectDataBufferInt.createBufferedImage( tileWidths[ tileX ] , tileHeights[ tileY ] ,
									BufferedImage.TYPE_INT_ARGB , new Point( 0 , 0 ) , new Hashtable<Object, Object>( ) );
							DirectDataBufferInt tileBuffer = ( DirectDataBufferInt ) tile.getRaster( ).getDataBuffer( );
							
							gl.glReadPixels( 0 , 0 , tileWidths[ tileX ] , tileHeights[ tileY ] , GL_BGRA , GL_UNSIGNED_BYTE , tileBuffer.getData( ) );
							
							if( captureTask.isCanceling( ) )
							{
								return;
							}
							
							AffineTransform prevXform = g2.getTransform( );
							g2.translate( -viewportX , totalHeight - 1 + viewportY );
							g2.scale( 1.0 , -1.0 );
							g2.drawImage( tile , 0 , 0 , null );
							g2.setTransform( prevXform );
							
							captureTask.setCompleted( captureTask.getCompleted( ) + 1 );
							
							viewportY -= tileHeights[ tileY ];
						}
						viewportX -= tileWidths[ tileX ];
					}
					
					setCapturedImage( capturedImage );
				}
				finally
				{
					captureTask = null;
					try
					{
						gl.glBindFramebuffer( GL_FRAMEBUFFER , 0 );
						
						gl.glDeleteFramebuffers( 1 , i , 0 );
						gl.glDeleteRenderbuffers( 1 , i , 1 );
						gl.glDeleteRenderbuffers( 1 , i , 2 );
						
						gl.glViewport( 0 , 0 , scene.getWidth( ) , scene.getHeight( ) );
					}
					finally
					{
						setCaptureFinished( true );
					}
				}
			}
		}
		
		private void setCapturedImage( BufferedImage capturedImage )
		{
			synchronized( lock )
			{
				this.capturedImage = capturedImage;
			}
		}
		
		private void setCaptureFinished( boolean newValue )
		{
			synchronized( lock )
			{
				captureFinished = newValue;
				lock.notifyAll( );
			}
		}
		
		public BufferedImage takeCapturedImage( ) throws InterruptedException
		{
			synchronized( lock )
			{
				while( !captureFinished )
				{
					lock.wait( );
				}
				
				BufferedImage capturedImage = this.capturedImage;
				this.capturedImage = null;
				return capturedImage;
			}
		}
	}
	
	private class CaptureTask extends SelfReportingTask
	{
		File					outputFile;
		
		int[ ]					tileWidths;
		int[ ]					tileHeights;
		
		public static final int	MAX_TILE_WIDTH	= 1024;
		public static final int	MAX_TILE_HEIGHT	= 1024;
		
		public CaptureTask( )
		{
			super( "Rendering image..." , ScreenCaptureDialog.this );
		}
		
		public boolean isCancelable( )
		{
			return true;
		}
		
		@Override
		protected void duringDialog( ) throws Exception
		{
			new OnEDT( )
			{
				@Override
				public void run( ) throws Throwable
				{
					dialog.setTitle( "Exporting..." );
					
					try
					{
						outputFile = createOutputFile( );
					}
					catch( Exception ex )
					{
						return;
					}
					
					if( outputFile.exists( ) )
					{
						if( outputFile.isDirectory( ) )
						{
							JOptionPane.showMessageDialog( dialog ,
									"Weird...output file " + outputFile + " is a directory" ,
									"Can't Export" ,
									JOptionPane.ERROR_MESSAGE );
							
							outputFile = null;
							return;
						}
						else
						{
							int option = JOptionPane.showConfirmDialog( dialog ,
									"Output file " + outputFile + " already exists.  Overwrite?" ,
									"Export Image" , JOptionPane.OK_CANCEL_OPTION , JOptionPane.WARNING_MESSAGE );
							
							if( option != JOptionPane.OK_OPTION )
							{
								outputFile = null;
								return;
							}
						}
					}
					
					File outputDir = outputFile.getParentFile( );
					
					if( !outputDir.exists( ) )
					{
						int option = JOptionPane.showConfirmDialog( dialog ,
								"Output directory " + outputDir + " does not exist.  Create it?" ,
								"Export Image" , JOptionPane.OK_CANCEL_OPTION , JOptionPane.INFORMATION_MESSAGE );
						
						if( option == JOptionPane.OK_OPTION )
						{
							try
							{
								outputDir.mkdirs( );
							}
							catch( Exception ex )
							{
								ex.printStackTrace( );
								JOptionPane.showMessageDialog( dialog ,
										"Failed to create directory " + outputDir + "; " +
												ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
										"Export Failed" , JOptionPane.ERROR_MESSAGE );
								outputFile = null;
								return;
							}
						}
						else
						{
							outputFile = null;
							return;
						}
					}
					
					Integer totalWidth = ( Integer ) pixelWidthSpinner.getValue( );
					Integer totalHeight = ( Integer ) pixelHeightSpinner.getValue( );
					
					if( totalWidth != null && totalHeight != null )
					{
						int xTiles = ( totalWidth + MAX_TILE_WIDTH - 1 ) / MAX_TILE_WIDTH;
						tileWidths = new int[ xTiles ];
						Arrays.fill( tileWidths , MAX_TILE_WIDTH );
						tileWidths[ xTiles - 1 ] = totalWidth % MAX_TILE_WIDTH;
						
						int yTiles = ( totalHeight + MAX_TILE_HEIGHT - 1 ) / MAX_TILE_HEIGHT;
						tileHeights = new int[ yTiles ];
						Arrays.fill( tileHeights , MAX_TILE_HEIGHT );
						tileHeights[ yTiles - 1 ] = totalHeight % MAX_TILE_HEIGHT;
					}
				}
			};
			
			if( outputFile == null || tileWidths == null || tileHeights == null || isCanceling( ) )
			{
				return;
			}
			
			renderer.captureTask = this;
			newtWindow.display( );
			BufferedImage capturedImage = renderer.takeCapturedImage( );
			
			if( capturedImage != null )
			{
				setStatus( "Saving image to " + outputFile + "..." );
				setIndeterminate( true );
				
				if( !isCanceling( ) )
				{
					try
					{
						ImageIO.write( capturedImage , "png" , outputFile );
						
						new OnEDT( )
						{
							@Override
							public void run( ) throws Throwable
							{
								Integer value = ( Integer ) fileNumberSpinner.getValue( );
								if( value == null )
								{
									fileNumberSpinner.setValue( 1 );
								}
								else
								{
									fileNumberSpinner.setValue( value + 1 );
								}
								ScreenCaptureDialog.this.dispose( );
							}
						};
					}
					catch( final Exception ex )
					{
						ex.printStackTrace( );
						new OnEDT( )
						{
							@Override
							public void run( ) throws Throwable
							{
								JOptionPane.showMessageDialog( dialog ,
										ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
										"Export Failed" ,
										JOptionPane.ERROR_MESSAGE );
							}
						};
					}
				}
			}
		}
	}
	
	private static int max( int ... values )
	{
		int max = Integer.MIN_VALUE;
		for( int i : values )
		{
			max = Math.max( max , i );
		}
		return max;
	}
	
	private static int total( int ... values )
	{
		int total = 0;
		for( int i : values )
		{
			total += i;
		}
		return total;
	}
	
	public void setVisible( boolean visible )
	{
		if( visible && newtWindow == null )
		{
			taskService.submit( new NEWTInitializer( ) );
		}
		if( newtWindow != null )
		{
			SwingUtilities.invokeLater( new Runnable( )
			{
				@Override
				public void run( )
				{
					if( canvas.getNEWTChild( ) != newtWindow )
					{
						canvas.setNEWTChild( newtWindow );
					}
					canvasHolder.revalidate( );
					canvas.repaint( );
					newtWindow.display( );
				}
			} );
		}
		super.setVisible( visible );
	}
	
	private class NEWTInitializer extends SelfReportingTask
	{
		public NEWTInitializer( )
		{
			super( "Initializing preview..." , ScreenCaptureDialog.this );
		}
		
		@Override
		protected void duringDialog( ) throws Exception
		{
			final GLProfile glp = GLProfile.get( GLProfile.GL2ES2 );
			final GLCapabilities caps = new GLCapabilities( glp );
			if( newtWindow == null )
			{
				newtWindow = GLWindow.create( caps );
				if( glContext != null )
				{
					newtWindow.setSharedContext( glContext );
					newtWindow.addGLEventListener( renderer );
				}
			}
			canvas.setNEWTChild( newtWindow );
			newtWindow.display( );
		}
	}
}

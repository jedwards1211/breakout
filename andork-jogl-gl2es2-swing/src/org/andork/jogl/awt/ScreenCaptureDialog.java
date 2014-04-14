package org.andork.jogl.awt;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.PlainDocument;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.I18n.I18nUpdater;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.event.UIBindings;
import org.andork.awt.IconScaler;
import org.andork.awt.LocalizedException;
import org.andork.event.Binder;
import org.andork.event.Binder.Binding;
import org.andork.snakeyaml.YamlObject;
import org.andork.swing.BetterSpinnerNumberModel;
import org.andork.swing.OnEDT;
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

import com.jogamp.newt.awt.NewtCanvasAWT;

@SuppressWarnings( "serial" )
public class ScreenCaptureDialog extends JDialog
{
	private static final BigDecimal					IN_TO_CM	= new BigDecimal( "2.54" );
	private static final BigDecimal					CM_TO_IN	= new BigDecimal( 1.0 / IN_TO_CM.doubleValue( ) );
	
	Localizer										localizer;
	
	NewtCanvasAWT									canvas;
	
	JFileChooser									outputDirectoryChooser;
	
	JLabel											outputDirectoryLabel;
	JTextField										outputDirectoryField;
	JButton											chooseOutputDirectoryButton;
	
	JLabel											fileNamePrefixLabel;
	JTextField										fileNamePrefixField;
	
	JLabel											fileNumberLabel;
	JSpinner										fileNumberSpinner;
	
	JLabel											outputFormatLabel;
	DefaultSelector<?>								outputFormatSelector;
	
	Icon											warningIcon	= IconScaler.rescale( UIManager.getIcon( "OptionPane.warningIcon" ) , 20 , 20 );
	JLabel											outputFileOrWarningLabel;
	JPanel											outputFileOrWarningLabelHolder;
	
	JLabel											pixelSizeHeaderLabel;
	JLabel											pixelWidthLabel;
	JSpinner										pixelWidthSpinner;
	JLabel											pixelWidthUnitLabel;
	JLabel											pixelHeightLabel;
	JSpinner										pixelHeightSpinner;
	JLabel											pixelHeightUnitLabel;
	JLabel											resolutionLabel;
	JSpinner										resolutionSpinner;
	DefaultSelector<ResolutionUnit>					resolutionUnitSelector;
	
	JLabel											printSizeHeaderLabel;
	JLabel											printWidthLabel;
	JSpinner										printWidthSpinner;
	JLabel											printHeightLabel;
	JSpinner										printHeightSpinner;
	JLabel											printUnitLabel;
	DefaultSelector<PrintSizeUnit>					printUnitSelector;
	
	JButton											exportButton;
	JButton											cancelButton;
	
	boolean											updating;
	
	Binder<YamlObject<ScreenCaptureDialogModel>>	binder;
	final List<Binding>								bindings	= new ArrayList<Binding>( );
	
	public static void main( String[ ] args )
	{
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
				
				ScreenCaptureDialog dialog = new ScreenCaptureDialog( new I18n( ) );
				
				Binder<YamlObject<ScreenCaptureDialogModel>> binder = new Binder<YamlObject<ScreenCaptureDialogModel>>( );
				YamlObject<ScreenCaptureDialogModel> model = ScreenCaptureDialogModel.instance.newObject( );
				
				model.set( ScreenCaptureDialogModel.outputDirectory , "screenshots" );
				model.set( ScreenCaptureDialogModel.fileNamePrefix , "breakout-screenshot" );
				model.set( ScreenCaptureDialogModel.fileNumber , 1 );
				model.set( ScreenCaptureDialogModel.pixelWidth , 600 );
				model.set( ScreenCaptureDialogModel.pixelHeight , 400 );
				model.set( ScreenCaptureDialogModel.resolution , new BigDecimal( 300 ) );
				model.set( ScreenCaptureDialogModel.resolutionUnit , ResolutionUnit.PIXELS_PER_IN );
				
				dialog.setBinder( binder );
				binder.setModel( model );
				binder.modelToView( );
				
				dialog.pack( );
				dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				dialog.setLocationRelativeTo( null );
				dialog.setVisible( true );
			}
		};
	}
	
	public ScreenCaptureDialog( I18n i18n )
	{
		super( );
		init( i18n );
	}
	
	public ScreenCaptureDialog( Dialog owner , String title , I18n i18n )
	{
		super( owner , title );
		init( i18n );
	}
	
	public ScreenCaptureDialog( Frame owner , String title , I18n i18n )
	{
		super( owner , title );
		init( i18n );
	}
	
	public ScreenCaptureDialog( Window owner , String title , I18n i18n )
	{
		super( owner , title );
		init( i18n );
	}
	
	protected void init( final I18n i18n )
	{
		new OnEDT( )
		{
			@Override
			public void run( ) throws Throwable
			{
				localizer = i18n.forClass( ScreenCaptureDialog.class );
				createComponents( );
				createLayout( );
				createListeners( );
			}
		};
	}
	
	public void setBinder( Binder<YamlObject<ScreenCaptureDialogModel>> binder )
	{
		if( this.binder != binder )
		{
			if( this.binder != null )
			{
				for( Binding binding : bindings )
				{
					this.binder.unbind( binding );
				}
			}
			bindings.clear( );
			
			this.binder = binder;
			if( binder != null )
			{
				bindings.add( UIBindings.bind( binder , outputDirectoryField , ScreenCaptureDialogModel.outputDirectory ) );
				bindings.add( UIBindings.bind( binder , fileNamePrefixField , ScreenCaptureDialogModel.fileNamePrefix ) );
				bindings.add( UIBindings.bind( binder , fileNumberSpinner , ScreenCaptureDialogModel.fileNumber ) );
				bindings.add( UIBindings.bind( binder , pixelWidthSpinner , ScreenCaptureDialogModel.pixelWidth ) );
				bindings.add( UIBindings.bind( binder , pixelHeightSpinner , ScreenCaptureDialogModel.pixelHeight ) );
				bindings.add( UIBindings.bind( binder , resolutionSpinner , ScreenCaptureDialogModel.resolution ) );
				bindings.add( UIBindings.bind( binder , resolutionUnitSelector , ScreenCaptureDialogModel.resolutionUnit ) );
			}
		}
	}
	
	protected void createComponents( )
	{
		canvas = new NewtCanvasAWT( );
		
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
		resolutionUnitSelector = new DefaultSelector<ScreenCaptureDialog.ResolutionUnit>( );
		resolutionUnitSelector.setAvailableValues( ResolutionUnit.values( ) );
		
		printSizeHeaderLabel = new JLabel( );
		localizer.setText( printSizeHeaderLabel , "printSizeHeaderLabel.text" );
		
		printWidthLabel = new JLabel( );
		localizer.setText( printWidthLabel , "widthLabel.text" );
		printWidthSpinner = createBigDecimalSpinner( 4 , 2 , new BigDecimal( 1 ) );
		printUnitSelector = new DefaultSelector<PrintSizeUnit>( );
		printUnitSelector.setAvailableValues( PrintSizeUnit.values( ) );
		
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
				PrintSizeUnit.INCHES.displayName = localizer.getString( "inches" );
				PrintSizeUnit.CENTIMETERS.displayName = localizer.getString( "centimeters" );
				
				ResolutionUnit.PIXELS_PER_CM.displayName = localizer.getString( "pixelsPerCm" );
				ResolutionUnit.PIXELS_PER_IN.displayName = localizer.getString( "pixelsPerIn" );
				
				printUnitSelector.getComboBox( ).repaint( );
				resolutionUnitSelector.getComboBox( ).repaint( );
			}
		} );
		
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
		
		gbw.put( sizePanel.getTarget( ) ).below( namePanel.getTarget( ) ).addToInsets( 10 , 0 , 0 , 0 );
		
		GridBagWizard buttonPanel = GridBagWizard.quickPanel( );
		buttonPanel.defaults( ).defaultAutoinsets( 2 , 2 );
		buttonPanel.put( exportButton , cancelButton ).intoRow( ).fillx( 1.0 );
		
		gbw.put( buttonPanel.getTarget( ) ).below( sizePanel.getTarget( ) ).south( ).fillx( 1.0 ).weighty( 1.0 ).addToInsets( 10 , 0 , 0 , 0 );
		
		JPanel leftPanel = ( JPanel ) gbw.getTarget( );
		leftPanel.setBorder( new EmptyBorder( 5 , 5 , 5 , 5 ) );
		
		outputFileOrWarningLabelHolder.setPreferredSize( new Dimension( leftPanel.getPreferredSize( ).width , 40 ) );
		outputFileOrWarningLabelHolder.setMinimumSize( outputFileOrWarningLabelHolder.getPreferredSize( ) );
		
		leftPanel.setMinimumSize( leftPanel.getPreferredSize( ) );
		leftPanel.setPreferredSize( leftPanel.getPreferredSize( ) );
		leftPanel.setMaximumSize( leftPanel.getPreferredSize( ) );
		
		getContentPane( ).add( leftPanel , BorderLayout.WEST );
		getContentPane( ).add( canvas , BorderLayout.CENTER );
		
		setResizable( false );
	}
	
	protected void createListeners( )
	{
		resolutionUnitSelector.addSelectorListener( new ISelectorListener<ResolutionUnit>( )
		{
			@Override
			public void selectionChanged( ISelector<ResolutionUnit> selector , ResolutionUnit oldSelection , ResolutionUnit newSelection )
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
							printUnitSelector.setSelection( PrintSizeUnit.INCHES );
							if( oldSelection == ResolutionUnit.PIXELS_PER_CM )
							{
								scaleUnits( CM_TO_IN );
							}
							break;
						case PIXELS_PER_CM:
							printUnitSelector.setSelection( PrintSizeUnit.CENTIMETERS );
							if( oldSelection == ResolutionUnit.PIXELS_PER_IN )
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
		
		printUnitSelector.addSelectorListener( new ISelectorListener<ScreenCaptureDialog.PrintSizeUnit>( )
		{
			@Override
			public void selectionChanged( ISelector<PrintSizeUnit> selector , PrintSizeUnit oldSelection , PrintSizeUnit newSelection )
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
							resolutionUnitSelector.setSelection( ResolutionUnit.PIXELS_PER_IN );
							if( oldSelection == PrintSizeUnit.CENTIMETERS )
							{
								scaleUnits( CM_TO_IN );
							}
							break;
						case CENTIMETERS:
							resolutionUnitSelector.setSelection( ResolutionUnit.PIXELS_PER_CM );
							if( oldSelection == PrintSizeUnit.INCHES )
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
					updateDialogSize( );
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
	}
	
	private void updateDialogSize( )
	{
		Dimension prefSize = getContentPane( ).getPreferredSize( );
		Integer width = ( Integer ) pixelWidthSpinner.getValue( );
		Integer height = ( Integer ) pixelHeightSpinner.getValue( );
		
		if( width != null && height != null && width != 0 && height != 0 )
		{
			width = prefSize.height * width / height;
			width = ( int ) Math.max( 100 , Math.min( width , getGraphicsConfiguration( ).getBounds( ).getMaxX( ) - getX( ) ) );
			canvas.setPreferredSize( new Dimension( width , prefSize.height ) );
		}
		
		setSize( getPreferredSize( ) );
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
		
		BigDecimal printHeight = ( BigDecimal ) printHeightSpinner.getValue( );
		if( printHeight != null && printHeight.compareTo( BigDecimal.ZERO ) != 0 )
		{
			pixelHeightSpinner.setValue( resolution.multiply( printHeight ).intValue( ) );
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
		
		Integer pixelHeight = ( Integer ) pixelHeightSpinner.getValue( );
		if( pixelHeight != null && pixelHeight != 0 )
		{
			printHeightSpinner.setValue( new BigDecimal( pixelHeight ).divide( resolution , 2 , BigDecimal.ROUND_HALF_EVEN ) );
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
	
	public ScreenCaptureDialog( Frame owner , I18n i18n )
	{
		super( owner );
		init( i18n );
	}
	
	public ScreenCaptureDialog( Dialog owner , I18n i18n )
	{
		super( owner );
		init( i18n );
	}
	
	public ScreenCaptureDialog( Window owner , I18n i18n )
	{
		super( owner );
		init( i18n );
	}
	
	public static enum PrintSizeUnit
	{
		INCHES , CENTIMETERS;
		
		String	displayName;
		
		public String toString( )
		{
			return displayName;
		}
	}
	
	public static enum ResolutionUnit
	{
		PIXELS_PER_IN , PIXELS_PER_CM;
		
		String	displayName;
		
		public String toString( )
		{
			return displayName;
		}
	}
}

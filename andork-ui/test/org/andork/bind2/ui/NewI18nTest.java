package org.andork.bind2.ui;

import java.awt.Container;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.bind2.DefaultBinder;
import org.andork.bind2.FunctionBinder;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.swing.CellRenderers;
import org.andork.swing.selector.DefaultSelector;

public class NewI18nTest
{
	public static void main( String[ ] args )
	{
		I18n i18n = new I18n( );
		
		Localizer localizer = i18n.forClass( NewI18nTest.class );
		
		JFrame frame = new JFrame( );
		
		Container content = frame.getContentPane( );
		
		JLabel localeLabel = new JLabel( );
		new JLabelTextBinding( localeLabel ).textLink.bind( localizer.stringBinder( "localeLabel.text" ) );
		
		DefaultSelector<Locale> localeSelector = new DefaultSelector<>( );
		localeSelector.getComboBox( ).setRenderer(
				CellRenderers.map( value -> {
					if( value == null )
					{
						return null;
					}
					Locale locale = ( Locale ) value;
					return locale.getDisplayCountry( locale ) + " " + locale.getDisplayLanguage( locale );
				} , new DefaultListCellRenderer( ) ) );
		i18n.setLocaleBinder( new ISelectorSelectionBinder<>( localeSelector ) );
		
		localeSelector.addAvailableValue( Locale.US );
		localeSelector.addAvailableValue( Locale.UK );
		localeSelector.addAvailableValue( Locale.CANADA );
		localeSelector.addAvailableValue( Locale.CANADA_FRENCH );
		localeSelector.addAvailableValue( Locale.GERMANY );
		
		JLabel dateLabel = new JLabel( );
		new JLabelTextBinding( dateLabel ).textLink.bind( localizer.formattedStringBinder(
				new DefaultBinder<String>( "dateLabel.text" ) ,
				i18n.formattedDateTimeBinder( new DefaultBinder<>( new Date( ) ) ,
						DateFormat.MEDIUM , DateFormat.MEDIUM ) ) );
		
		JButton testButton = new JButton( );
		new ButtonTextBinding( testButton ).textLink.bind( localizer.stringBinder( "testButton.text" ) );
		
		GridBagWizard gbw = GridBagWizard.create( content );
		
		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 5 , 5 ) );
		gbw.put( localeLabel , localeSelector.getComboBox( ) ).y( 0 ).intoRow( );
		gbw.put( dateLabel ).below( localeLabel , localeSelector.getComboBox( ) ).fillx( );
		gbw.put( testButton ).below( dateLabel ).fillx( );
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
}

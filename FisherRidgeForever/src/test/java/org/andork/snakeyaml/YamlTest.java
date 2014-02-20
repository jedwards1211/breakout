package org.andork.snakeyaml;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.andork.event.Binder;
import org.andork.swing.QuickTestFrame;
import org.andork.util.ExecutorServiceFilePersister;

import static org.andork.awt.event.UIBindings.*;

public class YamlTest
{
	public static void main( String[ ] args )
	{
		JFormattedTextField a1Field = new JFormattedTextField( 0.0 );
		JFormattedTextField a2Field = new JFormattedTextField( 0.0 );
		JFormattedTextField b1Field = new JFormattedTextField( 0.0 );
		JFormattedTextField b2Field = new JFormattedTextField( 0.0 );
		
		Binder<YamlObject<Settings>> binder = new Binder<YamlObject<Settings>>( );
		bind( binder.subBinder( Settings.distanceConversion ) , a1Field , Conversion.a1 );
		bind( binder.subBinder( Settings.distanceConversion ) , a2Field , Conversion.a2 );
		bind( binder.subBinder( Settings.distanceConversion ) , b1Field , Conversion.b1 );
		bind( binder.subBinder( Settings.distanceConversion ) , b2Field , Conversion.b2 );
		
		JPanel panel = new JPanel( new GridLayout( 2 , 4 ) );
		panel.add( new JLabel( "a1: " ) );
		panel.add( a1Field );
		panel.add( new JLabel( "a2: " ) );
		panel.add( a2Field );
		panel.add( new JLabel( "b1: " ) );
		panel.add( b1Field );
		panel.add( new JLabel( "b2: " ) );
		panel.add( b2Field );
		
		ExecutorServiceFilePersister<YamlObject<Settings>> persister = new ExecutorServiceFilePersister<YamlObject<Settings>>(
				new File( "Settings.yaml" ) , YamlObjectFormat.newInstance( Settings.instance ) );
		
		YamlObject<Settings> settings;
		try
		{
			settings = persister.load( );
		}
		catch( Exception ex )
		{
			settings = Settings.instance.newObject( );
		}
		binder.setModel( settings );
		binder.modelToView( );
		
		settings.changeSupport( ).addPropertyChangeListener( persister );
		
		JFrame frame = QuickTestFrame.frame( panel );
		frame.setVisible( true );
	}
	
	public static class Conversion extends YamlSpec<Conversion>
	{
		public static final Attribute<Double>	a1	= doubleAttribute( "a1" );
		public static final Attribute<Double>	a2	= doubleAttribute( "a2" );
		public static final Attribute<Double>	b1	= doubleAttribute( "b1" );
		public static final Attribute<Double>	b2	= doubleAttribute( "b2" );
		
		private Conversion( )
		{
			super( a1 , a2 , b1 , b2 );
		}
		
		public static final Conversion	instance	= new Conversion( );
		
	}
	
	public static class Settings extends YamlSpec<Settings>
	{
		public static final Attribute<YamlObject<Conversion>>	distanceConversion	= yamlObjectAttribute( "distanceConversion" , Conversion.instance );
		
		private Settings( )
		{
			super( distanceConversion );
		}
		
		public static final Settings	instance	= new Settings( );
		
	}
}

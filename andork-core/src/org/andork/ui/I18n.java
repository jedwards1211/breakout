package org.andork.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import javax.swing.AbstractButton;
import javax.swing.JLabel;

import static org.andork.ui.CheckEDT.checkEDT;

public class I18n
{
	private Locale							locale;
	
	private final Map<String, Localizer>	localizers	= new HashMap<String, Localizer>( );
	
	public I18n( )
	{
		locale = Locale.getDefault( );
	}
	
	public Localizer forName( String name )
	{
		checkEDT( );
		Localizer result = localizers.get( name );
		if( result == null )
		{
			result = new Localizer( name );
			localizers.put( name , result );
		}
		return result;
	}
	
	public Localizer forClass( Class<?> cls )
	{
		return forName( cls.getName( ) );
	}
	
	public Locale getLocale( )
	{
		checkEDT( );
		return locale;
	}
	
	public void setLocale( Locale locale )
	{
		checkEDT( );
		
		if( !this.locale.equals( locale ) )
		{
			this.locale = locale;
			
			for( Localizer localizer : localizers.values( ) )
			{
				localizer.localeChanged( );
			}
		}
	}
	
	public class Localizer
	{
		public final String									name;
		
		private ResourceBundle								bundle;
		
		private final WeakHashMap<Object, I18nUpdater<?>>	updaters	= new WeakHashMap<Object, I18nUpdater<?>>( );
		
		private Localizer( String name )
		{
			super( );
			this.name = name;
			updateBundle( );
		}
		
		@SuppressWarnings( { "rawtypes" , "unchecked" } )
		private void localeChanged( )
		{
			updateBundle( );
			
			for( Map.Entry<Object, I18nUpdater<?>> entry : updaters.entrySet( ) )
			{
				I18nUpdater updater = entry.getValue( );
				updater.updateI18n( this , entry.getKey( ) );
			}
		}
		
		private void updateBundle( )
		{
			try
			{
				bundle = ResourceBundle.getBundle( name , locale );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
		}
		
		public String getString( String key )
		{
			try
			{
				return bundle == null ? key : bundle.getString( key );
			}
			catch( Exception ex )
			{
				return key;
			}
		}
		
		public String getFormattedString( String key , Object ... args )
		{
			try
			{
				return MessageFormat.format( getString( key ) , args );
			}
			catch( Exception ex )
			{
				return key;
			}
		}
		
		public <T> void register( T localizedObject , I18nUpdater<T> updater )
		{
			checkEDT( );
			updaters.put( localizedObject , updater );
			updater.updateI18n( this , localizedObject );
		}
		
		public void unregister( Object localizedObject )
		{
			checkEDT( );
			updaters.remove( localizedObject );
		}
		
		public void setText( JLabel label , String key )
		{
			checkEDT( );
			register( label , new LabelI18nUpdater( key ) );
		}
		
		public void setFormattedText( JLabel label , String key , Object ... args )
		{
			checkEDT( );
			register( label , new LabelI18nFormattedUpdater( key , args ) );
		}
		
		public void setText( AbstractButton button , String key )
		{
			checkEDT( );
			register( button , new ButtonI18nUpdater( key ) );
		}
		
		public void setTitle( Dialog dialog , String key )
		{
			checkEDT( );
			register( dialog , new DialogTitleI18nUpdater( key ) );
		}
		
		public void setTitle( Frame frame , String key )
		{
			checkEDT( );
			register( frame , new FrameTitleI18nUpdater( key ) );
		}
	}
	
	public static interface I18nUpdater<T>
	{
		public void updateI18n( Localizer localizer , T localizedObject );
	}
	
	private static class LabelI18nUpdater implements I18nUpdater<JLabel>
	{
		String	key;
		
		protected LabelI18nUpdater( String key )
		{
			super( );
			this.key = key;
		}
		
		public void updateI18n( Localizer localizer , JLabel localizedObject )
		{
			localizedObject.setText( localizer.getString( key ) );
		}
	}
	
	private static class LabelI18nFormattedUpdater implements I18nUpdater<JLabel>
	{
		String		key;
		Object[ ]	args;
		
		protected LabelI18nFormattedUpdater( String key , Object ... args )
		{
			this.key = key;
			this.args = args;
		}
		
		public void updateI18n( Localizer localizer , JLabel localizedObject )
		{
			localizedObject.setText( localizer.getFormattedString( key , args ) );
		}
	}
	
	private static class ButtonI18nUpdater implements I18nUpdater<AbstractButton>
	{
		String	key;
		
		protected ButtonI18nUpdater( String key )
		{
			super( );
			this.key = key;
		}
		
		public void updateI18n( Localizer localizer , AbstractButton localizedObject )
		{
			localizedObject.setText( localizer.getString( key ) );
		}
	}
	
	private static class DialogTitleI18nUpdater implements I18nUpdater<Dialog>
	{
		String	key;
		
		protected DialogTitleI18nUpdater( String key )
		{
			super( );
			this.key = key;
		}
		
		public void updateI18n( Localizer localizer , Dialog localizedObject )
		{
			localizedObject.setTitle( localizer.getString( key ) );
		}
	}
	
	private static class FrameTitleI18nUpdater implements I18nUpdater<Frame>
	{
		String	key;
		
		protected FrameTitleI18nUpdater( String key )
		{
			super( );
			this.key = key;
		}
		
		public void updateI18n( Localizer localizer , Frame localizedObject )
		{
			localizedObject.setTitle( localizer.getString( key ) );
		}
	}
}
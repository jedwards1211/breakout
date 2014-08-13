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
package org.andork.i18n;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.bind2.BiFunctionBinder;
import org.andork.bind2.Binder;
import org.andork.bind2.BinderHolder;
import org.andork.bind2.DefaultBinder;
import org.andork.bind2.FunctionBinder;
import org.andork.bind2.ListBinder;
import org.andork.bind2.OpaqueBinderHolder;

public class I18n
{
	private static final Logger				logger						= Logger.getLogger( I18n.class.getName( ) );
	
	private BinderHolder<Locale>			localeBinderHolder			= new BinderHolder<>( );
	
	private OpaqueBinderHolder<Locale>		opaqueLocaleBinderHolder	= new OpaqueBinderHolder<>( localeBinderHolder );
	
	private final Map<String, Localizer>	localizers					= new HashMap<String, Localizer>( );
	
	private boolean							disableBundleLoading		= System.getProperties( ).containsKey( "disableBundleLoading" );
	
	public I18n( )
	{
		setLocale( Locale.getDefault( ) );
	}
	
	public boolean isDisableBundleLoading( )
	{
		return disableBundleLoading;
	}
	
	public void setDisableBundleLoading( boolean disableBundleLoading )
	{
		this.disableBundleLoading = disableBundleLoading;
	}
	
	public Localizer forName( String name )
	{
		Localizer result = localizers.get( name );
		if( result == null )
		{
			result = new Localizer( name , localeBinderHolder );
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
		return localeBinderHolder.get( );
	}
	
	public void setLocale( Locale locale )
	{
		localeBinderHolder.binderLink.bind( new DefaultBinder<>( locale ) );
	}
	
	public void setLocaleBinder( Binder<? extends Locale> binder )
	{
		localeBinderHolder.binderLink.bind( binder );
	}
	
	public Binder<Locale> getLocaleBinder( )
	{
		return opaqueLocaleBinderHolder;
	}
	
	public Binder<String> formattedDateTimeBinder( Binder<Date> dateBinder , int dateStyle , int timeStyle )
	{
		Binder<DateFormat> formatBinder = new FunctionBinder<>( localeBinderHolder ,
				locale -> locale == null ? DateFormat.getDateTimeInstance( dateStyle , timeStyle ) :
						DateFormat.getDateTimeInstance( dateStyle , timeStyle , locale ) );
		
		return new BiFunctionBinder<>( dateBinder , formatBinder ,
				( date , format ) -> date == null || format == null ? null : format.format( date ) );
	}
	
	public class Localizer
	{
		public final String				name;
		
		private Binder<ResourceBundle>	bundleBinder;
		
		private final Set<String>		missingKeys	= new HashSet<String>( );
		
		private Localizer( String name , Binder<Locale> localeBinder )
		{
			super( );
			this.name = name;
			bundleBinder = new FunctionBinder<>( localeBinder , locale -> {
				if( disableBundleLoading || locale == null )
				{
					return null;
				}
				
				try
				{
					return ResourceBundle.getBundle( name , locale );
				}
				catch( MissingResourceException ex )
				{
					ex.printStackTrace( );
				}
				return null;
			} ).convertToWeakReferencing( );
		}
		
		public ResourceBundle getBundle( )
		{
			return bundleBinder.get( );
		}
		
		public String getString( String key )
		{
			return getString( bundleBinder.get( ) , key );
		}
		
		private String getString( ResourceBundle bundle , String key )
		{
			try
			{
				return bundle == null || key == null ? key : bundle.getString( key );
			}
			catch( MissingResourceException ex )
			{
				if( missingKeys.add( key ) )
				{
					logger.log( Level.SEVERE , "Missing I18n key: \"" + key + '"' , ex );
				}
				return key;
			}
		}
		
		public String getFormattedString( String key , Object ... args )
		{
			return MessageFormat.format( getString( key ) , args );
		}
		
		public Binder<String> stringBinder( String key )
		{
			return new FunctionBinder<ResourceBundle, String>(
					bundleBinder ,
					bundle -> getString( bundle , key ) )
					.convertToWeakReferencing( );
		}
		
		public Binder<String> stringBinder( Binder<String> keyBinder )
		{
			return new BiFunctionBinder<ResourceBundle, String, String>(
					bundleBinder ,
					keyBinder ,
					( bundle , key ) -> getString( bundle , key ) )
					.convertToWeakReferencing( );
		}
		
		public Binder<String> formattedStringBinder( String key , Object ... args )
		{
			return new FunctionBinder<ResourceBundle, String>(
					bundleBinder ,
					bundle ->
					MessageFormat.format( getString( bundle , key ) , args ) )
					.convertToWeakReferencing( );
		}
		
		public Binder<String> formattedStringBinder( Binder<String> keyBinder , Binder<?> ... argBinders )
		{
			return new BiFunctionBinder<>(
					stringBinder( keyBinder ) ,
					new ListBinder<>( Arrays.asList( argBinders ) ) ,
					( pattern , args ) -> pattern == null || args == null ? null : MessageFormat.format( pattern , args.toArray( ) ) )
					.convertToWeakReferencing( );
		}
	}
}

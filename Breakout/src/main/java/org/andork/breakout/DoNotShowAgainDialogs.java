package org.andork.breakout;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.event.UIBindings;
import org.andork.event.Binder;

public class DoNotShowAgainDialogs
{
	private DoNotShowAgainDialogs( )
	{
		
	}
	
	public static final int	NOT_SHOWN_OPTION	= -2;
	
	private static Object wrapMessageWithDoNotShowAgainCheckBox( Object message , I18n i18n , Binder binder , Object doNotShowAgainProperty )
	{
		Localizer localizer = i18n.forClass( DoNotShowAgainDialogs.class );
		JCheckBox doNotShowAgainCheckBox = new JCheckBox( );
		localizer.setText( doNotShowAgainCheckBox , "doNotShowAgainCheckBox.text" );
		
		UIBindings.bind( binder , doNotShowAgainCheckBox , doNotShowAgainProperty ).modelToView( );
		
		message = new Object[ ] { message , doNotShowAgainCheckBox };
		return message;
	}

	public static void showMessageDialog( Component parent , Object message ,
			String title , int messageType , I18n i18n , Binder binder , Object doNotShowAgainProperty )
	{
		if( Boolean.TRUE.equals( binder.getModel( ).get( doNotShowAgainProperty ) ) )
		{
			return;
		}
		message = wrapMessageWithDoNotShowAgainCheckBox( message , i18n , binder , doNotShowAgainProperty );
		
		JOptionPane.showMessageDialog( parent , message , title , messageType );
	}
	
	public static int showConfirmDialog( Component parent , Object message ,
			String title , int optionType , I18n i18n , Binder binder , Object doNotShowAgainProperty )
	{
		if( Boolean.TRUE.equals( binder.getModel( ).get( doNotShowAgainProperty ) ) )
		{
			return NOT_SHOWN_OPTION;
		}
		message = wrapMessageWithDoNotShowAgainCheckBox( message , i18n , binder , doNotShowAgainProperty );
		
		return JOptionPane.showConfirmDialog( parent , message , title , optionType );
	}
}

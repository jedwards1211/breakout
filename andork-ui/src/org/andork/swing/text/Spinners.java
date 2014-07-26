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
package org.andork.swing.text;

import java.util.regex.Pattern;

import javax.swing.JSpinner;
import javax.swing.text.PlainDocument;

import org.andork.format.Format;
import org.andork.swing.BetterSpinnerNumberModel;

public class Spinners
{
	private Spinners( )
	{
		
	}
	
	public static JSpinner createIntegerSpinner( int value , int min , int max , int step )
	{
		BetterSpinnerNumberModel<Integer> model = BetterSpinnerNumberModel.newInstance( value , min ,
				max , step );
		
		int maxDigits = Integer.toString( max ).length( );
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
}

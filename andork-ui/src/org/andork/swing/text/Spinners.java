package org.andork.swing.text;

import java.util.regex.Pattern;

import javax.swing.JSpinner;
import javax.swing.text.PlainDocument;

import org.andork.swing.BetterSpinnerNumberModel;
import org.andork.util.Format;

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

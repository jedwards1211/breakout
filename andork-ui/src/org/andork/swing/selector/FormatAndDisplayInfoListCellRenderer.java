package org.andork.swing.selector;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import org.andork.swing.table.FormatAndDisplayInfo;

@SuppressWarnings( "serial" )
public class FormatAndDisplayInfoListCellRenderer extends DefaultListCellRenderer
{
	public FormatAndDisplayInfoListCellRenderer( )
	{
	}
	
	@Override
	public Component getListCellRendererComponent( JList<?> list , Object value , int index , boolean isSelected , boolean cellHasFocus )
	{
		if( value instanceof FormatAndDisplayInfo )
		{
			FormatAndDisplayInfo<?> info = ( FormatAndDisplayInfo<?> ) value;
			value = index < 0 ? info.name( ) : info.description( );
			Component comp = super.getListCellRendererComponent( list , value , index , isSelected , cellHasFocus );
			( ( JLabel ) comp ).setIcon( info.icon( ) );
			comp.setFont( comp.getFont( ).deriveFont( Font.PLAIN ) );
			return comp;
		}
		return super.getListCellRendererComponent( list , value , index , isSelected , cellHasFocus );
	}

	public static void setUpComboBox( JComboBox<?> comboBox )
	{
		comboBox.setRenderer( new FormatAndDisplayInfoListCellRenderer( ) );
		comboBox.setUI( new BiggerPopupComboBoxUI( ) );
	}
}

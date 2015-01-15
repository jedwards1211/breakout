package org.andork.breakout.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.swing.table.MouseInputTableCellRenderer;

/**
 * Renders a cell as a hyperlink. For a hyperlink to be displayed, the cell contents must be a
 * {@link ParsedTextWithType} whose value is a {@link URL}. If the user holds control and clicks on the cell, then it
 * will open the {@link URL} in the system browser via {@link Desktop#browse(URI)}.
 * 
 * @author James
 */
@SuppressWarnings( "serial" )
public class HyperlinkTableCellRenderer extends ParsedTextTableCellRenderer<URL> implements MouseInputTableCellRenderer
{
	Localizer	localizer;
	Color		hyperlinkColor	= Color.BLUE;

	/**
	 * @param i18n
	 *            internationalization provider in case errors occur when the user tries to open a link
	 * @param valueFormatter
	 *            a function that takes a {@link URL} and returns an {@link Object} (typically a {@link String}
	 *            representing the {@code URL} for {@link DefaultTableCellRenderer} code to render
	 * @param forceShowText
	 *            a function that takes a cell value and returns {@code true} if and only if there was an error
	 *            parsing the last text typed into that cell (if so the original text will be displayed, instead of the
	 *            reformatted value)
	 * @param backgroundColorFn
	 *            a function that takes a cell value and returns the background color to use for the cell (this is
	 *            provided so that parse errors and warnings can be highlighted red and yellow)
	 * @param tooltipFn
	 *            a function that takes a cell value and returns the tooltip to display for the cell (this is provided
	 *            so that parse error and warning messages can be displayed as tooltips)
	 */
	public HyperlinkTableCellRenderer(
		I18n i18n ,
		Function<? super URL, ?> valueFormatter , Predicate<? super ParsedText<?>> forceShowText ,
		Function<? super ParsedText<?>, Color> backgroundColorFn , Function<? super ParsedText<?>, String> tooltipFn )
	{
		super( url -> url == null ? null : "<html><u>" + valueFormatter.apply( url ) + "</u></html>" ,
			forceShowText , backgroundColorFn , tooltipFn );
		localizer = i18n.forClass( HyperlinkTableCellRenderer.class );
	}

	public Color getHyperlinkColor( )
	{
		return hyperlinkColor;
	}

	public void setHyperlinkColor( Color hyperlinkColor )
	{
		this.hyperlinkColor = hyperlinkColor;
	}

	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected ,
		boolean hasFocus , int row , int column )
	{
		JLabel result = ( JLabel ) super.getTableCellRendererComponent( table , value , isSelected , hasFocus ,
			row , column );
		result.setForeground( hyperlinkColor );
		if( value instanceof ParsedText )
		{
			ParsedText<URL> p = ( ParsedText<URL> ) value;
			if( p.getValue( ) != null )
			{
				result.setToolTipText( localizer.getString( "tooltip" ) );
			}
		}
		return result;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public void mouseClicked( JTable table , Object value , int row , int column , MouseEvent e )
	{
		if( e.isControlDown( ) && value instanceof ParsedText )
		{
			ParsedText<URL> p = ( ParsedText<URL> ) value;
			URI uri;
			try
			{
				URL url = p.getValue( );
				if( url != null )
				{
					Desktop.getDesktop( ).browse( url.toURI( ) );
				}
			}
			catch( IOException e1 )
			{
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( table ) ,
					localizer.getFormattedString( "unableToOpenUrlDialog.message" , e1.getLocalizedMessage( ) ) ,
					localizer.getString( "unableToOpenUrlDialog.title" ) ,
					JOptionPane.ERROR_MESSAGE );
			}
			catch( URISyntaxException e1 )
			{
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( table ) ,
					localizer.getFormattedString( "uriSyntaxExceptionDialog.message" , e1.getLocalizedMessage( ) ) ,
					localizer.getString( "uriSyntaxExceptionDialog.title" ) ,
					JOptionPane.ERROR_MESSAGE );
			}
		}
	}
}

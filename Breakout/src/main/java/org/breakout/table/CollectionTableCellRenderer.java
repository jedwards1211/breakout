package org.breakout.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.andork.awt.layout.LayoutSize;

/**
 * The renderer used for cells containing tags, inspired by the look of tags on Stack Overflow. Each is drawn
 * in a light gray rectangle, and the user sees the actual tag data instead of any escape sequences they may have typed
 * in to create tags with commas or quotes.<br>
 * <br>
 * The value for such sells should be some {@link Collection} of tags.
 * 
 * @author James
 *
 * @param <E>
 *            the element type.
 */
@SuppressWarnings( "serial" )
public class CollectionTableCellRenderer<E> extends JPanel implements TableCellRenderer
{
	DefaultTableCellRenderer	defaultRenderer			= new DefaultTableCellRenderer( );
	Function<? super E, String>	elemFormatter;
	int							maxNumElementsToShow	= 10;

	final ArrayList<JLabel>		labels					= new ArrayList<JLabel>( );
	JLabel						ellipsis;

	/**
	 * @param elemFormatter
	 *            takes an element of the cell value {@link Collection} and returns the formatted string representation
	 *            of the element.
	 */
	public CollectionTableCellRenderer( Function<? super E, String> elemFormatter )
	{
		this.elemFormatter = elemFormatter;

		ellipsis = new JLabel( "..." );
		setLayout( new Layout( ) );
		setBorder( new EmptyBorder( 2 , 2 , 2 , 2 ) );
	}

	@Override
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected ,
		boolean hasFocus , int row , int column )
	{
		Component defaults = defaultRenderer.getTableCellRendererComponent( table , null , isSelected , hasFocus , row ,
			column );

		setBackground( defaults.getBackground( ) );
		ellipsis.setBackground( defaults.getBackground( ) );
		ellipsis.setForeground( defaults.getForeground( ) );

		removeAll( );
		if( value instanceof Collection )
		{
			Collection<? extends E> collection = ( Collection<? extends E> ) value;

			Iterator<? extends E> iter = collection.iterator( );
			int i = 0;
			while( iter.hasNext( ) && i < maxNumElementsToShow )
			{
				if( labels.size( ) <= i )
				{
					labels.add( createElementLabel( ) );
				}
				JLabel label = labels.get( i );
				label.setFont( table.getFont( ) );

				E element = iter.next( );
				label.setText( elemFormatter.apply( element ) );
				add( label );

				i++;
			}

			if( collection.size( ) > maxNumElementsToShow )
			{
				ellipsis.setFont( table.getFont( ) );
				add( ellipsis );
			}
		}

		return this;
	}

	private JLabel createElementLabel( )
	{
		JLabel label = new JLabel( );
		label.setBackground( new Color( 214 , 214 , 214 ) );
		label.setBorder( new EmptyBorder( 1 , 1 , 1 , 1 ) );
		label.setOpaque( true );
		return label;
	}

	private class Layout implements LayoutManager
	{
		int	hgap	= 2;

		@Override
		public void addLayoutComponent( String name , Component comp )
		{
		}

		@Override
		public void removeLayoutComponent( Component comp )
		{
		}

		private Dimension layoutSize( Container parent , LayoutSize size )
		{
			Dimension result = new Dimension( );

			int i = 0;
			for( Component comp : getComponents( ) )
			{
				Dimension compSize = size.get( comp );
				result.height = Math.max( result.height , compSize.height );
				if( i++ > 0 )
				{
					result.width += hgap;
				}
				result.width += compSize.width;
			}
			Insets insets = parent.getInsets( );
			result.width += insets.left + insets.right;
			result.height += insets.top + insets.bottom;
			return result;
		}

		@Override
		public Dimension preferredLayoutSize( Container parent )
		{
			return layoutSize( parent , LayoutSize.PREFERRED );
		}

		@Override
		public Dimension minimumLayoutSize( Container parent )
		{
			return layoutSize( parent , LayoutSize.MINIMUM );
		}

		@Override
		public void layoutContainer( Container parent )
		{
			Rectangle bounds = SwingUtilities.calculateInnerArea( ( JComponent ) parent , null );
			int x = bounds.x;

			for( Component comp : getComponents( ) )
			{
				Dimension prefSize = comp.getPreferredSize( );
				Rectangle compBounds = new Rectangle( prefSize );
				compBounds.x = x;
				compBounds.y = bounds.y;
				compBounds.height = bounds.height;
				comp.setBounds( compBounds );

				x += prefSize.width + hgap;
			}
		}
	}
}

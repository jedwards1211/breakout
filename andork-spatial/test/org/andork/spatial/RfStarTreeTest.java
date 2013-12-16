package org.andork.spatial;

import static org.andork.spatial.Rectmath.voidRectf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.andork.spatial.RfStarTree.Branch;
import org.andork.spatial.RfStarTree.Leaf;
import org.andork.spatial.RfStarTree.Node;

public class RfStarTreeTest
{
	public static void main( String[ ] args )
	{
		final int dimension = 2;
		final int M = 8;
		final RfStarTree<String> tree = new RfStarTree<String>( dimension , M , 3 , 3 );
		
		final List<Node<String>> nodes = new ArrayList<Node<String>>( );
		
		final JPanel drawPanel = new JPanel( )
		{
			Rectangle2D.Float	rect	= new Rectangle2D.Float( );
			
			protected void paintComponent( Graphics g )
			{
				super.paintComponent( g );
				
				draw( tree.root , ( Graphics2D ) g );
				
				for( Node<String> node : nodes )
				{
					draw( node , ( Graphics2D ) g );
				}
			}
			
			private void draw( Node<String> node , Graphics2D g )
			{
				List<Node<String>> queue = new LinkedList<Node<String>>( );
				List<Node<String>> queue2 = new LinkedList<Node<String>>( );
				queue.add( node );
				
				int alpha = 31;
				
				while( !queue.isEmpty( ) )
				{
					g.setColor( new Color( 255 , 0 , 0 , alpha ) );
//					alpha = Math.min( 255 , alpha + 40 );
					
					for( Node<String> next : queue )
					{
						rect.setFrame( next.mbr[ 0 ] , next.mbr[ 1 ] , next.mbr[ dimension ] - next.mbr[ 0 ] , next.mbr[ dimension + 1 ] - next.mbr[ 1 ] );
						g.fill( rect );
						g.draw( rect );
						
						if( next instanceof Branch )
						{
							Branch<String> branch = ( Branch<String> ) next;
							for( int i = 0 ; i < branch.numChildren ; i++ )
							{
								queue2.add( branch.children[ i ] );
							}
						}
					}
					
					List<Node<String>> temp = queue;
					queue = queue2;
					queue2 = temp;
					queue2.clear( );
				}
			}
		};
		
		MouseAdapter adapter = new MouseAdapter( )
		{
			MouseEvent	pressEvent	= null;
			int			nodeCount	= 0;
			
			@Override
			public void mousePressed( MouseEvent e )
			{
				if( pressEvent != null || e.getButton( ) != MouseEvent.BUTTON1 )
				{
					return;
				}
				if( nodes.size( ) > M )
				{
					nodes.clear( );
				}
				
				pressEvent = e;
				float[ ] mbr = voidRectf( dimension );
				mbr[ 0 ] = mbr[ dimension ] = e.getX( );
				mbr[ 1 ] = mbr[ dimension + 1 ] = e.getY( );
				Leaf<String> node = ( Leaf<String> ) tree.createLeaf( mbr , Integer.toString( nodeCount++ ) );
				nodes.add( node );
				
				drawPanel.repaint( );
			}
			
			@Override
			public void mouseReleased( MouseEvent e )
			{
				if( pressEvent == null || e.getButton( ) != pressEvent.getButton( ) )
				{
					return;
				}
				
				while( !nodes.isEmpty( ) )
				{
					Leaf<String> leaf = ( Leaf<String> ) nodes.remove( nodes.size( ) - 1 );
					tree.insert( leaf );
				}
				
				drawPanel.repaint( );
				
				pressEvent = null;
			}
			
			@Override
			public void mouseDragged( MouseEvent e )
			{
				Node<String> node = nodes.get( nodes.size( ) - 1 );
				node.mbr[ 0 ] = Math.min( e.getX( ) , pressEvent.getX( ) );
				node.mbr[ 1 ] = Math.min( e.getY( ) , pressEvent.getY( ) );
				node.mbr[ dimension ] = Math.max( e.getX( ) , pressEvent.getX( ) );
				node.mbr[ dimension + 1 ] = Math.max( e.getY( ) , pressEvent.getY( ) );
				
				drawPanel.repaint( );
			}
		};
		
		drawPanel.addMouseListener( adapter );
		drawPanel.addMouseMotionListener( adapter );
		
		JFrame frame = new JFrame( );
		frame.getContentPane( ).add( drawPanel );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 800 , 600 );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
}

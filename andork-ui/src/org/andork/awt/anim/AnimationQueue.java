package org.andork.awt.anim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class AnimationQueue
{
	boolean						animating;
	final LinkedList<Animation>	pendingAdd		= new LinkedList<Animation>( );
	final LinkedList<Animation>	queue			= new LinkedList<Animation>( );
	final TimerHandler			timerHandler	= new TimerHandler( );
	final Timer					timer;
	long						lastAnimTime	= 0;
	
	public AnimationQueue( )
	{
		timer = new Timer( 0 , timerHandler );
		timer.setRepeats( false );
	}
	
	public void add( Animation animation )
	{
		if( !SwingUtilities.isEventDispatchThread( ) )
		{
			throw new RuntimeException( "must be called on EDT" );
		}
		if( animating )
		{
			pendingAdd.add( animation );
		}
		else
		{
			if( queue.isEmpty( ) )
			{
				timer.setInitialDelay( 0 );
				timer.start( );
			}
			queue.add( animation );
		}
	}
	
	public void clear( )
	{
		if( !SwingUtilities.isEventDispatchThread( ) )
		{
			throw new RuntimeException( "must be called on EDT" );
		}
		timer.stop( );
		queue.clear( );
		lastAnimTime = 0;
	}
	
	private class TimerHandler implements ActionListener
	{
		@Override
		public void actionPerformed( ActionEvent e )
		{
			long currentTime = System.currentTimeMillis( );
			long animTime = lastAnimTime == 0 ? 0 : currentTime - lastAnimTime;
			lastAnimTime = currentTime;
			while( !queue.isEmpty( ) )
			{
				Animation current = queue.getFirst( );
				
				animating = true;
				try
				{
					int result = ( int ) Math.min( current.animate( animTime ) , Integer.MAX_VALUE );
					
					if( result <= 0 )
					{
						queue.poll( );
						lastAnimTime = 0;
					}
					else
					{
						timer.setInitialDelay( result );
						timer.start( );
						break;
					}
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
					queue.poll( );
					lastAnimTime = 0;
				}
				finally
				{
					animating = false;
					queue.addAll( pendingAdd );
					pendingAdd.clear( );
				}
			}
		}
	}
}

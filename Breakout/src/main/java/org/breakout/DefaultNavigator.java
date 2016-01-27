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
package org.breakout;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import org.andork.jogl.DefaultJoglRenderer;
import org.andork.jogl.JoglViewSettings;
import org.andork.math3d.Vecmath;

import com.jogamp.opengl.GLAutoDrawable;

public class DefaultNavigator extends MouseAdapter
{
	final GLAutoDrawable	drawable;
	final DefaultJoglRenderer renderer;

	MouseEvent				lastEvent	= null;
	float[ ]				o			= new float[ 3 ];
	float[ ]				v			= new float[ 3 ];
	MouseEvent				pressEvent	= null;

	final float[ ]			temp		= Vecmath.newMat4f( );
	final float[ ]			cam			= Vecmath.newMat4f( );

	float					lastPan		= 0;

	boolean					active		= true;
	boolean					callDisplay	= true;

	float					moveFactor	= 0.05f;
	float					panFactor	= ( float ) Math.PI;
	float					tiltFactor	= ( float ) Math.PI;
	float					wheelFactor	= 1f;

	float					sensitivity	= 1f;

	final float[ ]			center		=
										{ Float.NaN , Float.NaN , Float.NaN };
	
	boolean 				zoomQueued = false;
	float 					queuedWheelRotation = 0f;

	public DefaultNavigator( GLAutoDrawable drawable , DefaultJoglRenderer renderer )
	{
		super( );
		this.drawable = drawable;
		this.renderer = renderer;
	}

	public boolean isActive( )
	{
		return active;
	}

	public void setActive( boolean active )
	{
		this.active = active;
	}

	public boolean isCallDisplay( )
	{
		return callDisplay;
	}

	public void setCallDisplay( boolean callDisplay )
	{
		this.callDisplay = callDisplay;
	}

	public float getMoveFactor( )
	{
		return moveFactor;
	}

	public void setMoveFactor( float moveFactor )
	{
		this.moveFactor = moveFactor;
	}

	public float getPanFactor( )
	{
		return panFactor;
	}

	public void setPanFactor( float panFactor )
	{
		this.panFactor = panFactor;
	}

	public float getTiltFactor( )
	{
		return tiltFactor;
	}

	public void setTiltFactor( float tiltFactor )
	{
		this.tiltFactor = tiltFactor;
	}

	public float getWheelFactor( )
	{
		return wheelFactor;
	}

	public void setWheelFactor( float wheelFactor )
	{
		this.wheelFactor = wheelFactor;
	}

	public float getSensitivity( )
	{
		return sensitivity;
	}

	public void setSensitivity( float sensitivity )
	{
		this.sensitivity = sensitivity;
	}

	public float[ ] getCenter( float[ ] result )
	{
		Vecmath.setf( result , this.center );
		return result;
	}

	public void setCenter( float[ ] center )
	{
		Vecmath.setf( this.center , center );
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		if( pressEvent != null && e.getButton( ) == pressEvent.getButton( ) )
		{
			pressEvent = null;
		}
	}

	@Override
	public void mousePressed( MouseEvent e )
	{
		if( pressEvent == null && !e.isAltDown( ) )
		{
			pressEvent = e;
			lastEvent = e;
		}
	}

	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( !active || pressEvent == null )
		{
			return;
		}

		int button = pressEvent.getButton( );

		if( e.isAltDown( ) )
		{
			if( button == MouseEvent.BUTTON1 )
			{
				button = MouseEvent.BUTTON3;
			}
			else if( button == MouseEvent.BUTTON3 )
			{
				button = MouseEvent.BUTTON1;
			}
		}

		float dx = e.getX( ) - lastEvent.getX( );
		float dy = e.getY( ) - lastEvent.getY( );
		if( e.isControlDown( ) )
		{
			dx /= 10f;
			dy /= 10f;
		}
		lastEvent = e;

		JoglViewSettings viewSettings = renderer.getViewSettings();
		viewSettings.getViewXform( cam );
		Vecmath.invAffine( cam );

		Vecmath.mvmulAffine( cam , 0 , 0 , 1 , v );

		float xz = ( float ) Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 2 ] * v[ 2 ] );

		float tilt = ( float ) Math.atan2( v[ 1 ] , xz );
		float pan = Math.abs( tilt ) == Math.PI / 2 ? lastPan : ( float ) Math.atan2( v[ 0 ] , v[ 2 ] );

		lastPan = pan;

		Component canvas = ( Component ) e.getSource( );

		float scaledMoveFactor = moveFactor * sensitivity;
		if( button == MouseEvent.BUTTON1 )
		{
			if( e.isShiftDown( ) )
			{
				float dpan = ( float ) ( dx * panFactor * sensitivity / canvas.getWidth( ) );

				Vecmath.rotY( temp , dpan );
				Vecmath.mmulRotational( temp , cam , cam );

				float dtilt = ( float ) ( dy * tiltFactor * sensitivity / canvas.getHeight( ) );
				Vecmath.mvmulAffine( cam , 1 , 0 , 0 , v );
				Vecmath.setRotation( temp , v , dtilt );
				Vecmath.mmulRotational( temp , cam , cam );

				Vecmath.invAffine( cam );
				viewSettings.setViewXform( cam );
			}
		}
		else if( button == MouseEvent.BUTTON2 )
		{
			if( e.isShiftDown( ) && !Vecmath.hasNaNsOrInfinites( center ) )
			{
				Vecmath.sub3( center , 0 , cam , 12 , v , 0 );
				float dist = Vecmath.length3( v );
				Vecmath.scale3( v , 1f / dist );
				double motion = Math.min( Math.max( 0 , dist - 1 ) , -dy * scaledMoveFactor );
				cam[ 12 ] += v[ 0 ] * motion;
				cam[ 13 ] += v[ 1 ] * motion;
				cam[ 14 ] += v[ 2 ] * motion;
			}
			else
			{
				cam[ 12 ] += cam[ 8 ] * dy * scaledMoveFactor;
				cam[ 13 ] += cam[ 9 ] * dy * scaledMoveFactor;
				cam[ 14 ] += cam[ 10 ] * dy * scaledMoveFactor;
			}
			Vecmath.invAffine( cam );
			viewSettings.setViewXform( cam );
		}
		else if( button == MouseEvent.BUTTON3 )
		{
			if( e.isShiftDown( ) )
			{
				float dpan = ( float ) ( dx * panFactor * sensitivity / canvas.getWidth( ) );

				Vecmath.rotY( temp , dpan );
				Vecmath.mmulRotational( temp , cam , cam );

				cam[ 12 ] -= cam[ 8 ] / xz * dy * scaledMoveFactor;
				cam[ 14 ] -= cam[ 10 ] / xz * dy * scaledMoveFactor;

				Vecmath.invAffine( cam );
				viewSettings.setViewXform( cam );
			}
			else
			{
				cam[ 12 ] += cam[ 0 ] * -dx * scaledMoveFactor + cam[ 4 ] * dy * scaledMoveFactor;
				cam[ 13 ] += cam[ 1 ] * -dx * scaledMoveFactor + cam[ 5 ] * dy * scaledMoveFactor;
				cam[ 14 ] += cam[ 2 ] * -dx * scaledMoveFactor + cam[ 6 ] * dy * scaledMoveFactor;
				Vecmath.invAffine( cam );
				viewSettings.setViewXform( cam );
			}
		}

		if( callDisplay )
		{
			this.drawable.display( );
		}
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( !active )
		{
			return;
		}
		
		queuedWheelRotation += e.getPreciseWheelRotation();

		if (!zoomQueued) {
			zoomQueued = true;
			SwingUtilities.invokeLater(() -> {
				float distance = -queuedWheelRotation * wheelFactor * sensitivity;
				queuedWheelRotation = 0f;
				zoomQueued = false;

				JoglViewSettings viewSettings = renderer.getViewSettings();
				viewSettings.getViewXform( cam );
				Vecmath.invAffine( cam );

				if( e.isControlDown( ) )
				{
					distance /= 10f;
				}

				if( e.isShiftDown( ) && !Vecmath.hasNaNsOrInfinites( center ) )
				{
					Vecmath.sub3( center , 0 , cam , 12 , v , 0 );
					float dist = Vecmath.length3( v );
					Vecmath.scale3( v , 1f / dist );
					distance = Math.min( Math.max( 0 , dist - 1 ) , distance );
				}
				else
				{
					renderer.getViewState().pickXform().xform(e, o, v);
				}
				cam[ 12 ] += v[ 0 ] * distance;
				cam[ 13 ] += v[ 1 ] * distance;
				cam[ 14 ] += v[ 2 ] * distance;

				Vecmath.invAffine( cam );
				viewSettings.setViewXform( cam );

				if( callDisplay )
				{
					this.drawable.display( );
				}
			});
		}
	}
}

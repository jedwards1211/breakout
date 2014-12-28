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
package org.andork.jogl.neu2.awt;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GLAutoDrawable;

import org.andork.jogl.neu2.JoglCamera;
import org.andork.math3d.Vecmath;

public class JoglNavigator extends MouseAdapter
{
	final GLAutoDrawable	drawable;
	final JoglCamera		camera;

	MouseEvent				lastEvent	= null;
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

	public JoglNavigator( GLAutoDrawable drawable , JoglCamera camera )
	{
		super( );
		this.drawable = drawable;
		this.camera = camera;
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
		if( pressEvent == null )
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

		float dx = e.getX( ) - lastEvent.getX( );
		float dy = e.getY( ) - lastEvent.getY( );
		if( e.isControlDown( ) )
		{
			dx /= 10f;
			dy /= 10f;
		}
		lastEvent = e;

		camera.getViewXform( cam );
		Vecmath.invAffine( cam );

		Vecmath.mvmulAffine( cam , 0 , 0 , 1 , v );

		float xz = ( float ) Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 2 ] * v[ 2 ] );

		float tilt = ( float ) Math.atan2( v[ 1 ] , xz );
		float pan = Math.abs( tilt ) == Math.PI / 2 ? lastPan : ( float ) Math.atan2( v[ 0 ] , v[ 2 ] );

		lastPan = pan;

		Component canvas = ( Component ) e.getSource( );

		float scaledMoveFactor = moveFactor * sensitivity;
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 )
		{
			if( pressEvent.isShiftDown( ) )
			{
				float dpan = ( float ) ( dx * panFactor * sensitivity / canvas.getWidth( ) );
				float dtilt = ( float ) ( dy * tiltFactor * sensitivity / canvas.getHeight( ) );

				Vecmath.rotY( temp , dpan );
				Vecmath.mmulRotational( temp , cam , cam );

				Vecmath.mvmulAffine( cam , 1 , 0 , 0 , v );
				Vecmath.setRotation( temp , v , dtilt );
				Vecmath.mmulRotational( temp , cam , cam );

				Vecmath.invAffine( cam );
				camera.setViewXform( cam );
			}
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON2 )
		{
			cam[ 12 ] += cam[ 8 ] * dy * scaledMoveFactor;
			cam[ 13 ] += cam[ 9 ] * dy * scaledMoveFactor;
			cam[ 14 ] += cam[ 10 ] * dy * scaledMoveFactor;
			Vecmath.invAffine( cam );
			camera.setViewXform( cam );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
		{
			cam[ 12 ] += cam[ 0 ] * -dx * scaledMoveFactor + cam[ 4 ] * dy * scaledMoveFactor;
			cam[ 13 ] += cam[ 1 ] * -dx * scaledMoveFactor + cam[ 5 ] * dy * scaledMoveFactor;
			cam[ 14 ] += cam[ 2 ] * -dx * scaledMoveFactor + cam[ 6 ] * dy * scaledMoveFactor;
			Vecmath.invAffine( cam );
			camera.setViewXform( cam );
		}

		if( callDisplay )
		{
			this.drawable.display( );
		}
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( !active || e.isControlDown( ) )
		{
			return;
		}

		camera.getViewXform( cam );
		Vecmath.invAffine( cam );

		float distance = e.getWheelRotation( ) * wheelFactor * sensitivity;

		cam[ 12 ] += cam[ 8 ] * distance;
		cam[ 13 ] += cam[ 9 ] * distance;
		cam[ 14 ] += cam[ 10 ] * distance;

		Vecmath.invAffine( cam );
		camera.setViewXform( cam );

		if( callDisplay )
		{
			this.drawable.display( );
		}
	}
}

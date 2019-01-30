/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl.awt;

import static org.andork.math3d.Vecmath.dot3;
import static org.andork.math3d.Vecmath.interp3;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mpmulAffine;
import static org.andork.math3d.Vecmath.scaleAdd3;
import static org.andork.math3d.Vecmath.sub3;
import static org.andork.math3d.Vecmath.subDot3;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import org.andork.jogl.AutoClipOrthoProjection;
import org.andork.jogl.JoglViewSettings;
import org.andork.jogl.JoglViewState;
import org.andork.math3d.Vecmath;

import com.jogamp.opengl.GLAutoDrawable;

public class JoglOrthoNavigator extends MouseAdapter {
	final GLAutoDrawable drawable;
	final JoglViewState viewState;
	final JoglViewSettings viewSettings;

	MouseEvent lastEvent = null;
	MouseEvent pressEvent = null;

	final float[] vi = Vecmath.newMat4f();
	final float[] v = Vecmath.newMat4f();

	boolean active = true;
	boolean callDisplay = true;

	float moveFactor = 0.05f;
	float panFactor = (float) Math.PI;
	float tiltFactor = (float) Math.PI;
	float wheelFactor = 1f;

	float sensitivity = 1f;

	final float[] p0 = new float[3];
	final float[] p1 = new float[3];
	final float[] p2 = new float[3];

	boolean zoomQueued = false;
	float queuedWheelRotation;

	public JoglOrthoNavigator(GLAutoDrawable drawable, JoglViewState viewState, JoglViewSettings viewSettings) {
		super();
		this.drawable = drawable;
		this.viewState = viewState;
		this.viewSettings = viewSettings;
	}

	private void calcZoom(MouseEvent e, float factor) {
		viewState.pickXform().xform(-1, -1, -1, p0);
		viewState.pickXform().xform(1, 1, -1, p1);
		viewState.pickXform().xform(e, -1, p2);

		sub3(p0, p2, p0);
		sub3(p1, p2, p1);

		scaleAdd3(factor, p0, p2, p0);
		scaleAdd3(factor, p1, p2, p1);

		AutoClipOrthoProjection orthoCalc = (AutoClipOrthoProjection) viewSettings.getProjection();
		orthoCalc.hSpan = Math.abs(dot3(p0, 0, vi, 0) - dot3(p1, 0, vi, 0));
		orthoCalc.vSpan = Math.abs(dot3(p0, 0, vi, 4) - dot3(p1, 0, vi, 4));

		interp3(p0, p1, 0.5f, p2);

		float dx = subDot3(p2, 0, vi, 12, vi, 0);
		float dy = subDot3(p2, 0, vi, 12, vi, 4);

		vi[12] += vi[0] * dx + vi[4] * dy;
		vi[13] += vi[1] * dx + vi[5] * dy;
		vi[14] += vi[2] * dx + vi[6] * dy;

		invAffine(vi, v);
		viewSettings.setViewXform(v);
	}

	public float getMoveFactor() {
		return moveFactor;
	}

	public float getPanFactor() {
		return panFactor;
	}

	public float getSensitivity() {
		return sensitivity;
	}

	public float getTiltFactor() {
		return tiltFactor;
	}

	public float getWheelFactor() {
		return wheelFactor;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isCallDisplay() {
		return callDisplay;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!active || pressEvent == null) {
			return;
		}

		viewState.pickXform().xform(lastEvent, -1, p0);
		viewState.pickXform().xform(e, -1, p1);

		mpmulAffine(viewState.viewMatrix(), p0);
		mpmulAffine(viewState.viewMatrix(), p1);

		float dx = p1[0] - p0[0];
		float dy = p1[1] - p0[1];
		if (e.isControlDown()) {
			dx /= 10f;
			dy /= 10f;
		}
		lastEvent = e;

		viewSettings.getViewXform(v);
		Vecmath.invAffine(v, vi);

		// float scaledMoveFactor = moveFactor * sensitivity;
		// if( pressEvent.getButton( ) == MouseEvent.BUTTON1 )
		// {
		// if( pressEvent.isShiftDown( ) )
		// {
		// float dpan = ( float ) ( dx * panFactor * sensitivity /
		// canvas.getWidth( ) );
		// float dtilt = ( float ) ( dy * tiltFactor * sensitivity /
		// canvas.getHeight( ) );
		//
		// Vecmath.rotY( temp , dpan );
		// Vecmath.mmulRotational( temp , cam , cam );
		//
		// Vecmath.mvmulAffine( cam , 1 , 0 , 0 , v );
		// Vecmath.setRotation( temp , v , dtilt );
		// Vecmath.mmulRotational( temp , cam , cam );
		//
		// Vecmath.invAffine( cam );
		// viewSettings.setViewXform( cam );
		// }
		// }
		if (pressEvent.getButton() == MouseEvent.BUTTON2) {
			if (e.isShiftDown()) {
				vi[12] += vi[8] * dy;
				vi[13] += vi[9] * dy;
				vi[14] += vi[10] * dy;
				Vecmath.invAffine(vi, v);
				viewSettings.setViewXform(v);
			} else {
				calcZoom(e, (float) Math.pow(1.1f, dy));
			}
		} else {
			vi[12] += vi[0] * -dx - vi[4] * dy;
			vi[13] += vi[1] * -dx - vi[5] * dy;
			vi[14] += vi[2] * -dx - vi[6] * dy;
			Vecmath.invAffine(vi, v);
			viewSettings.setViewXform(v);
		}

		if (callDisplay) {
			drawable.display();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (pressEvent == null) {
			pressEvent = e;
			lastEvent = e;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (pressEvent != null && e.getButton() == pressEvent.getButton()) {
			pressEvent = null;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!active) {
			return;
		}
		queuedWheelRotation += e.getPreciseWheelRotation();
		
		if (zoomQueued) return;
		if (!zoomQueued) {
			zoomQueued = true;
			SwingUtilities.invokeLater(() -> {
				float distance = queuedWheelRotation * wheelFactor * sensitivity;
				queuedWheelRotation = 0f;
				zoomQueued = false;
			
				viewSettings.getViewXform(v);
				Vecmath.invAffine(v, vi);

				if (e.isControlDown()) {
					distance /= 10;
				}

				if (e.isShiftDown()) {
					vi[12] += vi[8] * distance;
					vi[13] += vi[9] * distance;
					vi[14] += vi[10] * distance;
					Vecmath.invAffine(vi, v);
					viewSettings.setViewXform(v);
				} else {
					calcZoom(e, (float) Math.pow(1.1f, distance));
				}

				if (callDisplay) {
					drawable.display();
				}	
			});
		}

	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setCallDisplay(boolean callDisplay) {
		this.callDisplay = callDisplay;
	}

	public void setMoveFactor(float moveFactor) {
		this.moveFactor = moveFactor;
	}

	public void setPanFactor(float panFactor) {
		this.panFactor = panFactor;
	}

	public void setSensitivity(float sensitivity) {
		this.sensitivity = sensitivity;
	}

	public void setTiltFactor(float tiltFactor) {
		this.tiltFactor = tiltFactor;
	}

	public void setWheelFactor(float wheelFactor) {
		this.wheelFactor = wheelFactor;
	}
}

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

import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.rotY;
import static org.andork.math3d.Vecmath.setColumn3;
import static org.andork.math3d.Vecmath.setIdentity;
import static org.andork.math3d.Vecmath.setRotation;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.andork.jogl.JoglViewSettings;
import org.andork.math3d.Vecmath;

import com.jogamp.opengl.GLAutoDrawable;

public class JoglOrbiter extends MouseAdapter {
	final GLAutoDrawable drawable;
	final JoglViewSettings viewSettings;

	MouseEvent lastEvent = null;
	final float[] center = { 0, 0, 0 };
	final float[] axis = new float[3];
	MouseEvent pressEvent = null;
	final float[] v = Vecmath.newMat4f();
	final float[] m1 = Vecmath.newMat4f();
	final float[] m2 = Vecmath.newMat4f();
	boolean active = true;
	boolean callDisplay = true;
	float panFactor = (float) Math.PI;
	float tiltFactor = (float) Math.PI;
	float sensitivity = 1f;

	public JoglOrbiter(GLAutoDrawable drawable, JoglViewSettings viewSettings) {
		super();
		this.drawable = drawable;
		this.viewSettings = viewSettings;
	}

	public void getCenter(float[] out) {
		Vecmath.setf(out, center);
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

		int button = pressEvent.getButton();

		if (e.isAltDown()) {
			if (button == MouseEvent.BUTTON1) {
				button = MouseEvent.BUTTON3;
			} else if (button == MouseEvent.BUTTON3) {
				button = MouseEvent.BUTTON1;
			}
		}

		for (float f : center) {
			if (Float.isNaN(f) || Float.isInfinite(f)) {
				return;
			}
		}
		for (float f : axis) {
			if (Float.isNaN(f) || Float.isInfinite(f)) {
				return;
			}
		}
		float dx = e.getX() - lastEvent.getX();
		float dy = e.getY() - lastEvent.getY();
		if (e.isControlDown()) {
			dx /= 10f;
			dy /= 10f;
		}
		lastEvent = e;

		Component canvas = (Component) e.getSource();

		if (button == MouseEvent.BUTTON1 && !e.isShiftDown()) {
			viewSettings.getViewXform(v);
			invAffine(v, m1);
			mvmulAffine(m1, 1, 0, 0, axis);
			normalize3(axis);

			setIdentity(m1);
			setIdentity(m2);

			m2[12] = -center[0];
			m2[13] = -center[1];
			m2[14] = -center[2];

			float dpan = dx * panFactor * sensitivity / canvas.getWidth();
			float dtilt = dy * tiltFactor * sensitivity / canvas.getHeight();

			rotY(m1, dpan);
			mmulAffine(m1, m2, m2);

			setRotation(m1, axis, dtilt);
			mmulAffine(m1, m2, m2);

			setIdentity(m1);
			setColumn3(m1, 3, center);

			mmulAffine(m1, m2, m2);
			mmulAffine(v, m2, v);
			viewSettings.setViewXform(v);
		}

		if (callDisplay) {
			drawable.display();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (pressEvent == null && !e.isShiftDown() && !e.isAltDown()) {
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

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setCallDisplay(boolean callDisplay) {
		this.callDisplay = callDisplay;
	}

	public void setCenter(float[] center) {
		Vecmath.setf(this.center, center);
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
}

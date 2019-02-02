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
package org.breakout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import org.andork.awt.event.WheelEventAggregator;
import org.andork.jogl.JoglViewState;
import org.andork.math3d.Clip3f;
import org.andork.math3d.Vecmath;
import org.breakout.model.Survey3dModel;

import com.jogamp.opengl.GLAutoDrawable;

public class ClipMouseHandler extends MouseAdapter {
	public static interface Context {
		public GLAutoDrawable getDrawable();
		public Survey3dModel getSurvey3dModel();
		public void saveClip();
		public JoglViewState getViewState();
	}

	final Context context;

	final float[] forward = new float[3];
	final float[] nearFar = new float[2];
	
	float wheelFactor = 1f;
	float sensitivity = 1f;
	
	WheelEventAggregator nearAggregator;
	WheelEventAggregator farAggregator;

	boolean nearQueued = false;
	float nearQueuedWheelRotation = 0f;

	boolean farQueued = false;
	float farQueuedWheelRotation = 0f;

	public ClipMouseHandler(Context context) {
		this.context = context;

		nearAggregator = new WheelEventAggregator(rotation -> {
			JoglViewState viewState = context.getViewState();
			Vecmath.negate3(viewState.inverseViewMatrix(), 8, forward, 0);
			
			Survey3dModel model3d = context.getSurvey3dModel();
			if (model3d == null) return;
			
			final Clip3f clip = Vecmath.dot3(model3d.getClip().axis(), forward) < 0
				? model3d.getClip().flip()
				: model3d.getClip();

			float distance = (float) rotation * wheelFactor * sensitivity;
			
			clip.getNearFarOfMbr(model3d.getMbr(), nearFar);
			model3d.setClip(clip.setNear(Math.max(nearFar[0], clip.near()) + distance));
			
			context.saveClip();
			context.getDrawable().display();	
		});
		farAggregator = new WheelEventAggregator(rotation -> {
			JoglViewState viewState = context.getViewState();
			Vecmath.negate3(viewState.inverseViewMatrix(), 8, forward, 0);
			
			Survey3dModel model3d = context.getSurvey3dModel();
			if (model3d == null) return;
			
			final Clip3f clip = Vecmath.dot3(model3d.getClip().axis(), forward) < 0
				? model3d.getClip().flip()
				: model3d.getClip();

			float distance = (float) rotation * wheelFactor * sensitivity;
			
			clip.getNearFarOfMbr(model3d.getMbr(), nearFar);
			model3d.setClip(clip.setFar(Math.min(nearFar[1], clip.far()) + distance));
			
			context.saveClip();
			context.getDrawable().display();	
		});
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.isAltDown()) {
			(e.isShiftDown() ? farAggregator : nearAggregator).mouseWheelMoved(e);
		}
	}

	public float getWheelFactor() {
		return wheelFactor;
	}

	public void setWheelFactor(float wheelFactor) {
		this.wheelFactor = wheelFactor;
	}

	public float getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(float sensitivity) {
		this.sensitivity = sensitivity;
	}
}

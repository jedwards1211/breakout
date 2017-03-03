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
package org.breakout.model;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_BGRA;
import static com.jogamp.opengl.GL.GL_CLAMP_TO_EDGE;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_RGBA;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;
import static com.jogamp.opengl.GL.GL_UNSIGNED_BYTE;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.spatial.Rectmath.nmax;
import static org.andork.spatial.Rectmath.nmin;
import static org.andork.spatial.Rectmath.rayIntersects;
import static org.andork.spatial.Rectmath.voidRectf;

import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.func.FloatBinaryOperator;
import org.andork.graph.Graphs;
import org.andork.jogl.BufferHelper;
import org.andork.jogl.JoglBuffer;
import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglResource;
import org.andork.jogl.old.BasicJOGLObject.Uniform1fv;
import org.andork.jogl.old.BasicJOGLObject.Uniform3fv;
import org.andork.jogl.old.BasicJOGLObject.Uniform4fv;
import org.andork.jogl.util.JoglUtils;
import org.andork.math3d.InConeTester3f;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.PlanarHull3f;
import org.andork.math3d.TwoPlaneIntersection3f;
import org.andork.math3d.TwoPlaneIntersection3f.ResultType;
import org.andork.math3d.Vecmath;
import org.andork.spatial.RBranch;
import org.andork.spatial.RLeaf;
import org.andork.spatial.RNode;
import org.andork.spatial.RTraversal;
import org.andork.spatial.Rectmath;
import org.andork.spatial.RfStarTree;
import org.andork.spatial.RfStarTree.Branch;
import org.andork.spatial.RfStarTree.Leaf;
import org.andork.spatial.RfStarTree.Node;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.Task;
import org.andork.util.IterableUtils;
import org.breakout.PickResult;
import org.breakout.awt.ParamGradientMapPaint;
import org.omg.CORBA.FloatHolder;

import com.andork.plot.LinearAxisConversion;
import com.jogamp.nativewindow.awt.DirectDataBufferInt;
import com.jogamp.nativewindow.awt.DirectDataBufferInt.BufferedImageInt;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GL3;

public class Survey3dModel implements JoglDrawable, JoglResource {

	private class AxialSegment3dDrawer extends OneParamSegment3dDrawer {
		private int u_axis_location;
		private int u_origin_location;

		@Override
		protected void afterDraw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.afterDraw(segment3ds, context, gl, m, n);

			gl.glDisable(GL2GL3.GL_PRIMITIVE_RESTART);
		}

		@Override
		protected void beforeDraw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.beforeDraw(segment3ds, context, gl, m, n);
			gl.glUniform3fv(u_axis_location, 1, depthAxis.value(), 0);
			gl.glUniform3fv(u_origin_location, 1, depthOrigin.value(), 0);

			gl.glEnable(GL2GL3.GL_PRIMITIVE_RESTART);
			((GL3) gl).glPrimitiveRestartIndex(RESTART_INDEX);
		}

		@Override
		protected void beforeDraw(Segment3d segment3d, GL2ES2 gl, float[] m, float[] n) {
			super.beforeDraw(segment3d, gl, m, n);
			segment3d.lineIndices.init(gl);
			segment3d.fillIndices.init(gl);
		}

		@Override
		protected String createVertexShaderCode() {
			return super.createVertexShaderCode() +
					"  v_param = dot(a_pos - u_origin, u_axis);";
		}

		@Override
		protected String createVertexShaderVariables() {
			return super.createVertexShaderVariables() +
					"uniform vec3 u_axis;" +
					"uniform vec3 u_origin;" +
					"out float v_param;";
		}

		@Override
		protected void doDraw(Segment3d segment3d, GL2ES2 gl) {
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, segment3d.lineIndices.id());
			gl.glDrawElements(GL_LINES, segment3d.lineIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT, 0);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, segment3d.fillIndices.id());
			gl.glDrawElements(GL_TRIANGLE_STRIP, segment3d.fillIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT,
					0);
		}

		@Override
		public void init(GL2ES2 gl) {
			if (program <= 0) {
				super.init(gl);
				u_axis_location = gl.glGetUniformLocation(program, "u_axis");
				u_origin_location = gl.glGetUniformLocation(program, "u_origin");
			}
		}
	}

	private abstract class BaseSegment3dDrawer implements Segment3dDrawer {
		protected int program = 0;

		protected int m_location;
		protected int v_location;
		protected int p_location;
		protected int n_location;
		protected int a_pos_location;
		protected int a_norm_location;
		protected int u_ambient_location;

		protected int u_nearDist_location;
		protected int u_farDist_location;

		protected int a_glow_location;
		protected int u_glowColor_location;

		protected int a_highlightIndex_location;
		protected int u_highlightColors_location;

		protected void afterDraw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			gl.glDisable(GL_DEPTH_TEST);
			gl.glDisableVertexAttribArray(a_pos_location);
			gl.glDisableVertexAttribArray(a_norm_location);
			gl.glDisableVertexAttribArray(a_glow_location);
			gl.glDisableVertexAttribArray(a_highlightIndex_location);

			gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		}

		protected void beforeDraw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			gl.glUniformMatrix4fv(m_location, 1, false, m, 0);
			gl.glUniformMatrix3fv(n_location, 1, false, n, 0);
			gl.glUniformMatrix4fv(v_location, 1, false, context.viewXform(), 0);
			gl.glUniformMatrix4fv(p_location, 1, false, context.projXform(), 0);

			gl.glUniform1fv(u_ambient_location, 1, ambient.value(), 0);

			gl.glUniform1fv(u_nearDist_location, 1, nearDist.value(), 0);
			gl.glUniform1fv(u_farDist_location, 1, farDist.value(), 0);

			gl.glUniform4fv(u_glowColor_location, 1, glowColor.value(), 0);

			gl.glUniform4fv(u_highlightColors_location, highlightColors.count(), highlightColors.value(), 0);

			gl.glEnableVertexAttribArray(a_pos_location);
			gl.glEnableVertexAttribArray(a_norm_location);
			gl.glEnableVertexAttribArray(a_glow_location);
			gl.glEnableVertexAttribArray(a_highlightIndex_location);

			gl.glEnable(GL_DEPTH_TEST);
		}

		protected void beforeDraw(Segment3d segment3d, GL2ES2 gl, float[] m, float[] n) {
			segment3d.geometry.init(gl);
			segment3d.stationAttrs.init(gl);
			if (segment3d.stationAttrsNeedRebuffering.compareAndSet(true, false)) {
				segment3d.stationAttrs.rebuffer(gl);
			}

			gl.glBindBuffer(GL_ARRAY_BUFFER, segment3d.geometry.id());
			gl.glVertexAttribPointer(a_pos_location, 3, GL_FLOAT, false, GEOM_BPV, 0);
			gl.glVertexAttribPointer(a_norm_location, 3, GL_FLOAT, false, GEOM_BPV, 12);

			gl.glBindBuffer(GL_ARRAY_BUFFER, segment3d.stationAttrs.id());
			gl.glVertexAttribPointer(a_glow_location, 2, GL_FLOAT, false, STATION_ATTR_BPV, 0);
			gl.glVertexAttribPointer(a_highlightIndex_location, 1, GL_FLOAT, false, STATION_ATTR_BPV, 8);
		}

		protected String createFragmentShader() {
			return "#version 330\n" +
					createFragmentShaderVariables() +
					"void main() {" +
					createFragmentShaderCode() +
					"}";
		}

		private String fragmentShader;

		protected String createFragmentShaderCode() {
			return "  float temp;"
					+
					"  vec4 indexedHighlight;"
					+

					// distance coloration
					"  color.xyz *= mix(1.0, u_ambient, clamp((v_dist - u_nearDist) / (u_farDist - u_nearDist), 0.0, 1.0));"
					+

					// glow
					"  color = mix(color, u_glowColor, clamp(min(v_glow.x, v_glow.y), 0.0, 1.0));"
					+

					// lighting
					"  temp = dot(v_norm, vec3(0.0, 0.0, 1.0));"
					+
					"  temp = u_ambient + temp * (1.0 - u_ambient);"
					+
					"  color.xyz *= temp;"
					+

					// highlights
					"  indexedHighlight = u_highlightColors[int(floor(v_highlightIndex + 0.5))];"
					+
					"  color = clamp(color + vec4(indexedHighlight.xyz * indexedHighlight.w, 0.0), 0.0, 1.0);";
		}

		protected String createFragmentShaderVariables() {
			// lighting
			return "in vec3 v_norm;" +
					"uniform float u_ambient;" +

					// distance coloration
					"in float v_dist;" +
					"uniform float u_farDist;" +
					"uniform float u_nearDist;" +

					// glow
					"in vec2 v_glow;" +
					"uniform vec4 u_glowColor;" +

					// highlights
					"uniform vec4 u_highlightColors[3];" +
					"in float v_highlightIndex;" +

					"out vec4 color;";

		}

		protected String createVertexShader() {
			return "#version 330\n" +
					createVertexShaderVariables() +
					"void main() {" +
					createVertexShaderCode() +
					"}";
		}

		protected String createVertexShaderCode() {
			return "  gl_Position = p * v * m * vec4(a_pos, 1.0);" +
					"  v_norm = (v * vec4(normalize(n * a_norm), 0.0)).xyz;" +
					"  v_dist = -(v * m * vec4(a_pos, 1.0)).z;" +
					"  v_glow = a_glow;" +
					"  v_highlightIndex = a_highlightIndex;";
		}

		protected String createVertexShaderVariables() {
			return "uniform mat4 m;" +
					"uniform mat4 v;" +
					"uniform mat4 p;" +
					"in vec3 a_pos;" +

					// lighting
					"in vec3 a_norm;" +
					"out vec3 v_norm;" +
					"uniform mat3 n;" +

					// distance coloration
					"out float v_dist;" +

					// glow
					"in vec2 a_glow;" +
					"out vec2 v_glow;" +

					// highlights
					"in float a_highlightIndex;" +
					"out float v_highlightIndex;";
		}

		@Override
		public void dispose(GL2ES2 gl) {
			if (program > 0) {
				gl.glDeleteProgram(program);
				program = 0;
			}
		}

		protected abstract void doDraw(Segment3d segment3d, GL2ES2 gl);

		@Override
		public void draw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			if (program <= 0) {
				init(gl);
			}

			gl.glUseProgram(program);

			beforeDraw(segment3ds, context, gl, m, n);

			for (Segment3d segment3d : segment3ds) {
				draw(segment3d, context, gl, m, n);
			}

			afterDraw(segment3ds, context, gl, m, n);
		}

		public void draw(Segment3d segment3d, JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
			beforeDraw(segment3d, gl, m, n);

			doDraw(segment3d, gl);
		}

		@Override
		public void init(GL2ES2 gl) {
			String vertShader, fragShader;

			if (program <= 0) {
				vertShader = createVertexShader();
				fragShader = createFragmentShader();

				program = JoglUtils.loadProgram(gl, vertShader, fragShader);

				m_location = gl.glGetUniformLocation(program, "m");
				v_location = gl.glGetUniformLocation(program, "v");
				p_location = gl.glGetUniformLocation(program, "p");
				n_location = gl.glGetUniformLocation(program, "n");

				a_pos_location = gl.glGetAttribLocation(program, "a_pos");
				a_norm_location = gl.glGetAttribLocation(program, "a_norm");

				a_glow_location = gl.glGetAttribLocation(program, "a_glow");
				a_highlightIndex_location = gl.glGetAttribLocation(program, "a_highlightIndex");

				u_ambient_location = gl.glGetUniformLocation(program, "u_ambient");

				u_nearDist_location = gl.glGetUniformLocation(program, "u_nearDist");
				u_farDist_location = gl.glGetUniformLocation(program, "u_farDist");

				u_glowColor_location = gl.glGetUniformLocation(program, "u_glowColor");

				u_highlightColors_location = gl.glGetUniformLocation(program, "u_highlightColors");
			}
		}
	}

	private static enum Direction {
		FORWARD, BACKWARD;
	}

	private abstract class OneParamSegment3dDrawer extends BaseSegment3dDrawer {
		private int u_loParam_location;
		private int u_hiParam_location;
		private int u_paramSampler_location;

		@Override
		protected void beforeDraw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.beforeDraw(segment3ds, context, gl, m, n);

			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_2D, paramTexture);
			gl.glUniform1i(u_paramSampler_location, 0);

			gl.glUniform1fv(u_loParam_location, 1, loParam.value(), 0);
			gl.glUniform1fv(u_hiParam_location, 1, hiParam.value(), 0);
		}

		@Override
		protected String createFragmentShaderCode() {
			// param coloration
			return "  color = texture(u_paramSampler, vec2(0.5, clamp((v_param - u_loParam) / (u_hiParam - u_loParam), 0.0, 1.0)));"
					+
					super.createFragmentShaderCode();
		}

		@Override
		protected String createFragmentShaderVariables() {
			return super.createFragmentShaderVariables() +
					"uniform float u_loParam;" +
					"uniform float u_hiParam;" +
					"uniform sampler2D u_paramSampler;" +
					"in float v_param;";
		}

		@Override
		public void init(GL2ES2 gl) {
			if (program <= 0) {
				super.init(gl);

				u_loParam_location = gl.glGetUniformLocation(program, "u_loParam");
				u_hiParam_location = gl.glGetUniformLocation(program, "u_hiParam");
				u_paramSampler_location = gl.glGetUniformLocation(program, "u_paramSampler");
			}
		}
	}

	private class Param0Segment3dDrawer extends OneParamSegment3dDrawer {
		private int a_param0_location;

		@Override
		protected void afterDraw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.afterDraw(segment3ds, context, gl, m, n);
			gl.glDisableVertexAttribArray(a_param0_location);

			gl.glDisable(GL2GL3.GL_PRIMITIVE_RESTART);
		}

		@Override
		protected void beforeDraw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.beforeDraw(segment3ds, context, gl, m, n);

			gl.glEnableVertexAttribArray(a_param0_location);

			gl.glEnable(GL2GL3.GL_PRIMITIVE_RESTART);
			((GL3) gl).glPrimitiveRestartIndex(RESTART_INDEX);
		}

		@Override
		protected void beforeDraw(Segment3d segment3d, GL2ES2 gl, float[] m, float[] n) {
			if (segment3d.param0 == null) {
				return;
			}
			super.beforeDraw(segment3d, gl, m, n);
			segment3d.param0.init(gl);
			if (segment3d.param0NeedsRebuffering.compareAndSet(true, false)) {
				segment3d.param0.rebuffer(gl);
			}
			segment3d.lineIndices.init(gl);
			segment3d.fillIndices.init(gl);

			gl.glBindBuffer(GL_ARRAY_BUFFER, segment3d.param0.id());
			gl.glVertexAttribPointer(a_param0_location, 1, GL_FLOAT, false, 4, 0);
		}

		@Override
		protected String createVertexShaderCode() {
			return super.createVertexShaderCode() +
					"  v_param = a_param0;";
		}

		@Override
		protected String createVertexShaderVariables() {
			return super.createVertexShaderVariables() +
					"in float a_param0;" +
					"out float v_param;";
		}

		@Override
		protected void doDraw(Segment3d segment3d, GL2ES2 gl) {
			if (segment3d.param0 == null) {
				return;
			}
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, segment3d.lineIndices.id());
			gl.glDrawElements(GL_LINES, segment3d.lineIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT, 0);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, segment3d.fillIndices.id());
			gl.glDrawElements(GL_TRIANGLE_STRIP, segment3d.fillIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT,
					0);
		}

		@Override
		public void init(GL2ES2 gl) {
			if (program <= 0) {
				super.init(gl);
				a_param0_location = gl.glGetAttribLocation(program, "a_param0");
			}
		}
	}

	public static class Segment3d {
		final ArrayList<Shot3d> shot3ds = new ArrayList<>();

		final LinkedList<Segment3dDrawer> drawers = new LinkedList<>();

		JoglBuffer geometry;
		JoglBuffer stationAttrs;
		final AtomicBoolean stationAttrsNeedRebuffering = new AtomicBoolean();
		JoglBuffer param0;
		final AtomicBoolean param0NeedsRebuffering = new AtomicBoolean();
		JoglBuffer fillIndices;
		JoglBuffer lineIndices;

		int[] shotIndicesInVertexArrays;
		int[] shotIndicesInFillIndices;

		void addShot(Shot3d shot3d) {
			shot3d.segment3d = this;
			shot3d.indexInSegment = shot3ds.size();
			shot3ds.add(shot3d);
		}

		void calcParam0(Survey3dModel model, ColorParam param) {
			param0 = new JoglBuffer().buffer(createBuffer(shot3ds.size() * GEOM_VPS * 4));
			param0.buffer().position(0);
			for (Shot3d shot3d : shot3ds) {
				CalcShot origShot = model.originalShots.get(shot3d.key);

				if (!param.isStationMetric()) {
					return;
				}
				float fromValue = param.calcStationParam(origShot, origShot.fromStation);
				float toValue = param.calcStationParam(origShot, origShot.toStation);

				if (Float.isNaN(fromValue)) {
					fromValue = toValue;
				}
				if (Float.isNaN(toValue)) {
					toValue = fromValue;
				}

				param0.buffer().putFloat(fromValue);
				param0.buffer().putFloat(fromValue);
				param0.buffer().putFloat(fromValue);
				param0.buffer().putFloat(fromValue);
				param0.buffer().putFloat(toValue);
				param0.buffer().putFloat(toValue);
				param0.buffer().putFloat(toValue);
				param0.buffer().putFloat(toValue);
			}
			param0NeedsRebuffering.set(true);
		}

		void calcParam0(Survey3dModel model, Map<CalcStation, Double> stationValues) {
			param0 = new JoglBuffer().buffer(createBuffer(shot3ds.size() * GEOM_VPS * 4));
			param0.buffer().position(0);
			for (Shot3d shot3d : shot3ds) {
				CalcShot origShot = model.originalShots.get(shot3d.key);
				Double fromValue = stationValues.get(origShot.fromStation);
				if (fromValue == null) {
					fromValue = Double.NaN;
				}
				Double toValue = stationValues.get(origShot.toStation);
				if (toValue == null) {
					toValue = Double.NaN;
				}
				param0.buffer().putFloat(fromValue.floatValue());
				param0.buffer().putFloat(fromValue.floatValue());
				param0.buffer().putFloat(fromValue.floatValue());
				param0.buffer().putFloat(fromValue.floatValue());
				param0.buffer().putFloat(toValue.floatValue());
				param0.buffer().putFloat(toValue.floatValue());
				param0.buffer().putFloat(toValue.floatValue());
				param0.buffer().putFloat(toValue.floatValue());
			}
			param0NeedsRebuffering.set(true);
		}

		void calcParamRange(Survey3dModel model, ColorParam param, float[] rangeInOut) {
			float[] origin = model.depthOrigin.value();
			float[] axis = model.depthAxis.value();

			if (param == ColorParam.DEPTH) {
				geometry.buffer().position(0);
				for (int i = 0; i < geometry.buffer().capacity(); i += GEOM_BPV) {
					float x = geometry.buffer().getFloat(i);
					float y = geometry.buffer().getFloat(i + 4);
					float z = geometry.buffer().getFloat(i + 8);

					float f = (x - origin[0]) * axis[0] + (y - origin[1]) * axis[1] + (z - origin[2])
							* axis[2];
					if (!Double.isNaN(f)) {
						rangeInOut[0] = Math.min(rangeInOut[0], f);
						rangeInOut[1] = Math.max(rangeInOut[1], f);
					}
				}
			} else {
				param0.buffer().position(0);
				while (param0.buffer().hasRemaining()) {
					float f = param0.buffer().getFloat();
					if (!Double.isNaN(f)) {
						rangeInOut[0] = Math.min(rangeInOut[0], f);
						rangeInOut[1] = Math.max(rangeInOut[1], f);
					}
				}
				param0.buffer().position(0);
			}
		}

		void clearGlow() {
			ByteBuffer buffer = stationAttrs.buffer();
			buffer.position(0);
			for (int i = 0; i < buffer.capacity(); i += Survey3dModel.STATION_ATTR_BPV) {
				buffer.putFloat(i, -Float.MAX_VALUE);
				buffer.putFloat(i + 4, -Float.MAX_VALUE);
			}
		}

		void clearHighlights() {
			ByteBuffer buffer = stationAttrs.buffer();
			buffer.position(0);
			for (int i = 8; i < buffer.capacity(); i += Survey3dModel.STATION_ATTR_BPV) {
				if (buffer.getFloat(i) != 1) {
					buffer.putFloat(i, 0);
				}
			}
		}

		public void dispose(GL2ES2 gl) {
			geometry.dispose(gl);
			stationAttrs.dispose(gl);
			if (param0 != null) {
				param0.dispose(gl);
			}
			fillIndices.dispose(gl);
			lineIndices.dispose(gl);
		}

		void populateData(ByteBuffer allGeomBuffer) {
			geometry = new JoglBuffer().buffer(createBuffer(shot3ds.size() * GEOM_BPS));
			stationAttrs = new JoglBuffer().buffer(createBuffer(shot3ds.size() * STATION_ATTR_BPS));

			shotIndicesInVertexArrays = new int[shot3ds.size()];
			shotIndicesInFillIndices = new int[shot3ds.size()];

			List<Integer> fillIndicesList = new ArrayList<>();
			List<Integer> lineIndicesList = new ArrayList<>();

			int k = 0;
			for (Shot3d shot3d : shot3ds) {
				shotIndicesInVertexArrays[shot3d.indexInSegment] = geometry.buffer().position() / GEOM_BPV;
				shotIndicesInFillIndices[shot3d.indexInSegment] = fillIndicesList.size() * BPI;

				copyBytes(allGeomBuffer, geometry.buffer(), k++, GEOM_BPS);

				for (int index : offset(shot3d.indexInSegment * GEOM_VPS, 0, 4, 2, 6, 1, 5, 3, 7, 0, 4)) {
					fillIndicesList.add(index);
					lineIndicesList.add(index);
				}
				fillIndicesList.add(RESTART_INDEX);
			}

			ByteBuffer fillIndicesBuffer = createBuffer(fillIndicesList.size() * BPI);
			fillIndices = new JoglBuffer().buffer(fillIndicesBuffer);
			for (Integer i : fillIndicesList) {
				fillIndicesBuffer.putInt(i);
			}
			ByteBuffer lineIndicesBuffer = createBuffer(lineIndicesList.size() * BPI);
			lineIndices = new JoglBuffer().buffer(lineIndicesBuffer);
			for (Integer i : lineIndicesList) {
				lineIndicesBuffer.putInt(i);
			}

			geometry.buffer().position(0);
			stationAttrs.buffer().position(0);
			fillIndices.buffer().position(0);
			lineIndices.buffer().position(0);
		}
	}

	private static interface Segment3dDrawer extends JoglResource {
		public void draw(Collection<Segment3d> segment3ds, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n);
	}

	public final class SelectionEditor {
		final Set<ShotKey> selected = new HashSet<>();

		final Set<ShotKey> deselected = new HashSet<>();
		boolean committed = false;

		private SelectionEditor() {

		}

		public void commit() {
			if (committed) {
				throw new IllegalStateException("already committed");
			}
			committed = true;

			Map<ShotKey, Shot3d> affectedShots = new HashMap<>();
			for (ShotKey shotKey : selected) {
				Shot3d shot3d = shot3ds.get(shotKey);
				if (shot3d != null) {
					affectedShots.put(shotKey, shot3d);
					selectedShots.add(shotKey);
				}
			}
			for (ShotKey shotKey : deselected) {
				selectedShots.remove(shotKey);
				affectedShots.remove(shotKey);
			}

			updateHighlights(affectedShots.values());
		}

		public SelectionEditor deselect(ShotKey shotKey) {
			selected.remove(shotKey);
			deselected.add(shotKey);
			return this;
		}

		public SelectionEditor select(ShotKey shotKey) {
			selected.add(shotKey);
			deselected.remove(shotKey);
			return this;
		}
	}

	public static class Shot3d {
		ShotKey key;

		Segment3d segment3d;
		int indexInSegment;

		RfStarTree.Leaf<Shot3d> leaf;

		Shot3d(ShotKey key) {
			super();
			this.key = key;
		}

		public void calcParamRange(Survey3dModel model, ColorParam param, float[] rangeInOut) {
			float[] origin = model.depthOrigin.value();
			float[] axis = model.depthAxis.value();

			if (param == ColorParam.DEPTH) {
				for (float[] coord : coordIterable()) {
					float f = (coord[0] - origin[0]) * axis[0] +
							(coord[1] - origin[1]) * axis[1] +
							(coord[2] - origin[2]) * axis[2];
					if (Float.isFinite(f)) {
						rangeInOut[0] = Math.min(rangeInOut[0], f);
						rangeInOut[1] = Math.max(rangeInOut[1], f);
					}
				}
			} else {
				int i = segment3d.shotIndicesInVertexArrays[indexInSegment];
				boolean last = indexInSegment == segment3d.shot3ds.size() - 1;

				ByteBuffer param0buffer = segment3d.param0.buffer();

				int maxIndex = last ? param0buffer.capacity() / 4
						: segment3d.shotIndicesInVertexArrays[indexInSegment + 1];

				while (i < maxIndex) {
					float f = param0buffer.getFloat(i * 4);
					if (Float.isFinite(f)) {
						rangeInOut[0] = Math.min(rangeInOut[0], f);
						rangeInOut[1] = Math.max(rangeInOut[1], f);
					}
					i++;
				}
			}
		}

		public Iterable<float[]> coordIterable() {
			return coordIterable(new float[3]);
		}

		public Iterable<float[]> coordIterable(float[] coord) {
			return new Iterable<float[]>() {
				@Override
				public Iterator<float[]> iterator() {
					return new Iterator<float[]>() {
						int index = 0;

						@Override
						public boolean hasNext() {
							return index < GEOM_VPS;
						}

						@Override
						public float[] next() {
							getCoordinate(index, coord);
							index++;
							return coord;
						}
					};
				}
			};
		}

		public void getCoordinate(int i, float[] result) {
			if (i < 0) {
				throw new IllegalArgumentException("i must be > 0");
			}
			if (indexInSegment < segment3d.shotIndicesInVertexArrays.length - 1) {
				if (i >= segment3d.shotIndicesInVertexArrays[indexInSegment + 1]) {
					throw new IndexOutOfBoundsException("i is not in the bounds of this shot");
				}
			}
			ByteBuffer vertBuffer = segment3d.geometry.buffer();
			int baseIndex = (segment3d.shotIndicesInVertexArrays[indexInSegment] + i) * GEOM_BPV;
			result[0] = vertBuffer.getFloat(baseIndex);
			result[1] = vertBuffer.getFloat(baseIndex + 4);
			result[2] = vertBuffer.getFloat(baseIndex + 8);
			vertBuffer.position(0);
		}

		public float getFromGlowA() {
			return Survey3dModel.getFromGlowA(segment3d.stationAttrs.buffer(), indexInSegment);
		}

		public float getFromGlowB() {
			return Survey3dModel.getFromGlowB(segment3d.stationAttrs.buffer(), indexInSegment);
		}

		public float getToGlowA() {
			return Survey3dModel.getToGlowA(segment3d.stationAttrs.buffer(), indexInSegment);
		}

		public float getToGlowB() {
			return Survey3dModel.getToGlowB(segment3d.stationAttrs.buffer(), indexInSegment);
		}

		public void pick(float[] coneOrigin, float[] coneDirection, float coneAngle, Shot3dPickContext c,
				List<PickResult<Shot3d>> pickResults) {
			Shot3dPickResult result = null;

			ByteBuffer indexBuffer = segment3d.fillIndices.buffer();
			ByteBuffer vertBuffer = segment3d.geometry.buffer();

			int k = segment3d.shotIndicesInFillIndices[indexInSegment];
			indexBuffer.position(k);
			int i = 0;

			int i0 = 0;
			int i1 = 0;
			int i2;

			boolean last = indexInSegment == segment3d.shot3ds.size() - 1;
			int maxIndex = last ? indexBuffer.capacity() : segment3d.shotIndicesInFillIndices[indexInSegment + 1];
			while (k < maxIndex) {
				i2 = indexBuffer.getInt();
				k += 4;
				if (i2 == RESTART_INDEX) {
					i = 0;
					continue;
				}

				if (i >= 2) {
					boolean even = i % 2 == 0;

					vertBuffer.position((even ? i0 : i2) * GEOM_BPV);
					c.p0[0] = vertBuffer.getFloat();
					c.p0[1] = vertBuffer.getFloat();
					c.p0[2] = vertBuffer.getFloat();

					vertBuffer.position(i1 * GEOM_BPV);
					c.p1[0] = vertBuffer.getFloat();
					c.p1[1] = vertBuffer.getFloat();
					c.p1[2] = vertBuffer.getFloat();

					vertBuffer.position((even ? i2 : i0) * GEOM_BPV);
					c.p2[0] = vertBuffer.getFloat();
					c.p2[1] = vertBuffer.getFloat();
					c.p2[2] = vertBuffer.getFloat();

					try {
						c.lpx.lineFromRay(coneOrigin, coneDirection);
						c.lpx.planeFromPoints(c.p0, c.p1, c.p2);
						c.lpx.findIntersection();
						if (c.lpx.isPointIntersection() && c.lpx.isOnRay() && c.lpx.isInTriangle()) {
							if (result == null || result.lateralDistance > 0 || c.lpx.t < result.distance) {
								if (result == null) {
									result = new Shot3dPickResult();
								}
								result.picked = this;
								result.distance = c.lpx.t;
								result.locationAlongShot = even ? c.lpx.u : 1 - c.lpx.u;
								result.lateralDistance = 0;
								setf(result.location, c.lpx.result);
							}
						} else if (result == null || result.lateralDistance > 0) {
							if (c.inConeTester.isLineSegmentInCone(c.p0, c.p1, coneOrigin, coneDirection,
									coneAngle)) {
								if (result == null || c.inConeTester.lateralDistance
										* result.distance < result.lateralDistance * c.inConeTester.t) {
									if (result == null) {
										result = new Shot3dPickResult();
									}
									result.picked = this;
									result.distance = c.inConeTester.t;
									result.lateralDistance = c.inConeTester.lateralDistance;
									result.locationAlongShot = even ? c.inConeTester.s : 1 - c.inConeTester.s;
									Vecmath.interp3(c.p0, c.p1, c.inConeTester.s, result.location);
								}
							} else if (c.inConeTester.isLineSegmentInCone(c.p1, c.p2, coneOrigin, coneDirection,
									coneAngle)) {
								if (result == null || c.inConeTester.lateralDistance
										* result.distance < result.lateralDistance * c.inConeTester.t) {
									if (result == null) {
										result = new Shot3dPickResult();
									}
									result.picked = this;
									result.distance = c.inConeTester.t;
									result.lateralDistance = c.inConeTester.lateralDistance;
									result.locationAlongShot = even ? 1 - c.inConeTester.s : c.inConeTester.s;
									Vecmath.interp3(c.p1, c.p2, c.inConeTester.s, result.location);
								}
							} else if (c.inConeTester.isLineSegmentInCone(c.p2, c.p0, coneOrigin, coneDirection,
									coneAngle)) {
								if (result == null || c.inConeTester.lateralDistance
										* result.distance < result.lateralDistance * c.inConeTester.t) {
									if (result == null) {
										result = new Shot3dPickResult();
									}
									result.picked = this;
									result.distance = c.inConeTester.t;
									result.lateralDistance = c.inConeTester.lateralDistance;
									result.locationAlongShot = i % 2;
									Vecmath.interp3(c.p2, c.p0, c.inConeTester.s, result.location);
								}
							}
						}
					} catch (Exception ex) {

					}
				}

				i0 = i1;
				i1 = i2;

				i++;
			}

			if (result != null) {
				pickResults.add(result);
			}

			vertBuffer.position(0);
			indexBuffer.position(0);
		}

		public void pick(float[] rayOrigin, float[] rayDirection, Shot3dPickContext c,
				List<PickResult<Shot3d>> pickResults) {
			Shot3dPickResult result = null;

			ByteBuffer indexBuffer = segment3d.fillIndices.buffer();
			ByteBuffer vertBuffer = segment3d.geometry.buffer();
			indexBuffer.position(segment3d.shotIndicesInFillIndices[indexInSegment]);
			int i = 0;

			int i0 = 0;
			int i1 = 0;
			int i2;

			boolean last = indexInSegment == segment3d.shot3ds.size() - 1;
			int maxIndex = last ? indexBuffer.capacity() : segment3d.shotIndicesInFillIndices[indexInSegment + 1];
			while (indexBuffer.position() < maxIndex) {
				i2 = indexBuffer.getInt();
				if (i2 == RESTART_INDEX) {
					i = 0;
					continue;
				}

				if (i >= 2) {
					boolean even = i % 2 == 0;

					vertBuffer.position((even ? i0 : i2) * GEOM_BPV);
					c.p0[0] = vertBuffer.getFloat();
					c.p0[1] = vertBuffer.getFloat();
					c.p0[2] = vertBuffer.getFloat();

					vertBuffer.position(i1 * GEOM_BPV);
					c.p1[0] = vertBuffer.getFloat();
					c.p1[1] = vertBuffer.getFloat();
					c.p1[2] = vertBuffer.getFloat();

					vertBuffer.position((even ? i2 : i0) * GEOM_BPV);
					c.p2[0] = vertBuffer.getFloat();
					c.p2[1] = vertBuffer.getFloat();
					c.p2[2] = vertBuffer.getFloat();
					try {
						c.lpx.lineFromRay(rayOrigin, rayDirection);
						c.lpx.planeFromPoints(c.p0, c.p1, c.p2);
						c.lpx.findIntersection();
						if (c.lpx.isPointIntersection() && c.lpx.isOnRay() && c.lpx.isInTriangle()) {
							if (result == null || c.lpx.t < result.distance) {
								result = new Shot3dPickResult();
								result.picked = this;
								result.distance = c.lpx.t;
								result.locationAlongShot = i % 2 == 0 ? c.lpx.u : 1 - c.lpx.u;
								setf(result.location, c.lpx.result);
							}
						}
					} catch (Exception ex) {

					}
				}

				i0 = i1;
				i1 = i2;

				i++;
			}

			if (result != null) {
				pickResults.add(result);
			}

			vertBuffer.position(0);
			indexBuffer.position(0);
		}

		public void pick(PlanarHull3f hull, Shot3dPickContext c, List<PickResult<Shot3d>> pickResults) {
			Shot3dPickResult result = null;

			ByteBuffer indexBuffer = segment3d.fillIndices.buffer();
			ByteBuffer vertBuffer = segment3d.geometry.buffer();

			int k = segment3d.shotIndicesInFillIndices[indexInSegment];
			indexBuffer.position(k);
			int i = 0;

			int i0 = 0;
			int i1 = 0;
			int i2;

			boolean last = indexInSegment == segment3d.shot3ds.size() - 1;
			int maxIndex = last ? indexBuffer.capacity() : segment3d.shotIndicesInFillIndices[indexInSegment + 1];
			while (k < maxIndex) {
				i2 = indexBuffer.getInt();
				k += 4;
				if (i2 == RESTART_INDEX) {
					i = 0;
					continue;
				}

				if (i >= 2) {
					boolean even = i % 2 == 0;

					vertBuffer.position((even ? i0 : i2) * GEOM_BPV);
					c.p0[0] = vertBuffer.getFloat();
					c.p0[1] = vertBuffer.getFloat();
					c.p0[2] = vertBuffer.getFloat();

					vertBuffer.position(i1 * GEOM_BPV);
					c.p1[0] = vertBuffer.getFloat();
					c.p1[1] = vertBuffer.getFloat();
					c.p1[2] = vertBuffer.getFloat();

					vertBuffer.position((even ? i2 : i0) * GEOM_BPV);
					c.p2[0] = vertBuffer.getFloat();
					c.p2[1] = vertBuffer.getFloat();
					c.p2[2] = vertBuffer.getFloat();

					try {
						c.lpx.lineFromPoints(hull.origins[4], hull.origins[5]);
						c.lpx.planeFromPoints(c.p0, c.p1, c.p2);
						c.lpx.findIntersection();
						if (c.lpx.isPointIntersection() && c.lpx.isOnRay() && c.lpx.isInTriangle()
								&& hull.containsPoint(c.lpx.result)) {
							if (result == null || result.lateralDistance > 0 || c.lpx.t < result.distance) {
								if (result == null) {
									result = new Shot3dPickResult();
								}
								result.picked = this;
								result.distance = c.lpx.t;
								result.locationAlongShot = even ? c.lpx.u : 1 - c.lpx.u;
								result.lateralDistance = 0;
								setf(result.location, c.lpx.result);
							}
						} else if (result == null || result.lateralDistance > 0) {
							for (int[] triangle : hullTriangleIndices) {
								c.tpx.plane1FromPoints(c.p0, c.p1, c.p2);
								c.tpx.plane2FromPoints(
										hull.vertices[triangle[0]],
										hull.vertices[triangle[1]],
										hull.vertices[triangle[2]]);

								c.tpx.twoTriangleIntersection();

								if (c.tpx.intersectionType == ResultType.LINEAR_INTERSECTION) {
									c.tpx.calcIntersectionPoint(c.tpx.t2[0], c.x0);
									c.tpx.calcIntersectionPoint(c.tpx.t2[1], c.x1);

									float distance0 = Vecmath.subDot3(c.x0, hull.origins[4], hull.normals[4]);
									float distance1 = Vecmath.subDot3(c.x1, hull.origins[4], hull.normals[4]);

									if (distance1 < distance0) {
										distance0 = distance1;
										setf(c.x0, c.x1);
									}
									float diagonal = Vecmath.distance3sq(hull.origins[4], c.x0);
									float lateralDistance = (float) Math.sqrt(diagonal - distance0 * distance0);

									if (result == null
											|| lateralDistance * result.distance < result.lateralDistance * distance0) {
										if (result == null) {
											result = new Shot3dPickResult();
										}
										result.picked = this;
										result.distance = distance0;
										result.lateralDistance = lateralDistance;
										result.locationAlongShot = k >= 4 ? 1 : 0;
										setf(result.location, c.x0);
									}
								}
							}
						}
					} catch (Exception ex) {
					}
				}

				i0 = i1;
				i1 = i2;

				i++;
			}
			if (result == null) {
				k = 0;
				for (float[] coord : coordIterable()) {
					if (hull.containsPoint(coord)) {
						float distance = Vecmath.subDot3(coord, hull.origins[4], hull.normals[4]);
						float diagonal = Vecmath.distance3sq(hull.origins[4], coord);
						float lateralDistance = (float) Math.sqrt(diagonal - distance * distance);
						if (result == null || lateralDistance * result.distance < result.lateralDistance * distance) {
							if (result == null) {
								result = new Shot3dPickResult();
							}
							result.picked = this;
							result.distance = distance;
							result.lateralDistance = lateralDistance;
							result.locationAlongShot = k >= 4 ? 1 : 0;
							setf(result.location, coord);
						}
					}
					k++;
				}
			}
			if (result != null) {
				pickResults.add(result);
			}

			vertBuffer.position(0);
			indexBuffer.position(0);
		}

		public void setFromGlowA(float glow) {
			Survey3dModel.setFromGlowA(segment3d.stationAttrs.buffer(), indexInSegment, glow);
		}

		public void setFromGlowB(float glow) {
			Survey3dModel.setFromGlowB(segment3d.stationAttrs.buffer(), indexInSegment, glow);
		}

		public void setToGlowA(float glow) {
			Survey3dModel.setToGlowA(segment3d.stationAttrs.buffer(), indexInSegment, glow);
		}

		public void setToGlowB(float glow) {
			Survey3dModel.setToGlowB(segment3d.stationAttrs.buffer(), indexInSegment, glow);
		}

		public void unionMbrInto(float[] mbr) {
			Rectmath.union3(mbr, leaf.mbr(), mbr);
		}

		public ShotKey key() {
			return key;
		}
	}

	public static final class Shot3dPickContext {
		final LinePlaneIntersection3f lpx = new LinePlaneIntersection3f();
		final float[] p0 = new float[3];
		final float[] p1 = new float[3];
		final float[] p2 = new float[3];
		final float[] x0 = new float[3];
		final float[] x1 = new float[3];
		final float[] adjacent = new float[3];
		final float[] opposite = new float[3];
		final InConeTester3f inConeTester = new InConeTester3f();
		final TwoPlaneIntersection3f tpx = new TwoPlaneIntersection3f();
	}

	public static class Shot3dPickResult extends PickResult<Shot3d> {
		public float locationAlongShot;
	}

	private static final int GEOM_BPV = 24;

	private static final int GEOM_VPS = 8;

	private static final int GEOM_BPS = GEOM_BPV * GEOM_VPS;
	private static final int STATION_ATTR_BPV = 12;

	private static final int STATION_ATTR_VPS = GEOM_VPS;

	private static final int STATION_ATTR_BPS = STATION_ATTR_BPV
			* STATION_ATTR_VPS;
	private static final int BPI = 4;
	private static final int RESTART_INDEX = 0xffffffff;
	private static final int[][] hullTriangleIndices = {
			{ 0, 6, 4 }, { 6, 0, 2 },
			{ 2, 7, 6 }, { 7, 2, 3 },
			{ 3, 5, 7 }, { 5, 3, 1 },
			{ 1, 4, 5 }, { 4, 1, 0 },
			{ 0, 3, 2 }, { 3, 0, 1 },
			{ 4, 7, 6 }, { 7, 4, 5 }
	};

	private static void addShots(Node<Shot3d> node, Segment3d segment3d) {
		if (node instanceof Leaf) {
			segment3d.addShot(((Leaf<Shot3d>) node).object());
		} else if (node instanceof Branch) {
			Branch<Shot3d> branch = (Branch<Shot3d>) node;
			for (int i = 0; i < branch.numChildren(); i++) {
				addShots(branch.childAt(i), segment3d);
			}
		}
	}

	private static void copyBytes(ByteBuffer src, ByteBuffer dest, int shotIndex, int bytesPerShot) {
		src.clear();
		src.position(shotIndex * bytesPerShot);
		src.limit(src.position() + bytesPerShot);
		dest.put(src);
	}

	public static Survey3dModel create(Map<ShotKey, CalcShot> originalShots, int maxChildrenPerBranch, int minSplitSize,
			int numToReinsert, Task task) {
		Subtask rootSubtask = null;
		int renderProportion = 5;

		if (task != null) {
			task.setTotal(1000);
			rootSubtask = new Subtask(task);
		} else {
			rootSubtask = Subtask.dummySubtask();
		}
		rootSubtask.setStatus("Updating view");
		rootSubtask.setTotal(renderProportion + 5);

		Map<ShotKey, Shot3d> shot3ds = new HashMap<>();
		for (ShotKey key : originalShots.keySet()) {
			shot3ds.put(key, new Shot3d(key));
		}
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		ByteBuffer geomBuffer = createInitialGeometry(originalShots, rootSubtask.beginSubtask(1));
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		RfStarTree<Shot3d> tree = createTree(shot3ds.values(), geomBuffer, maxChildrenPerBranch, minSplitSize,
				numToReinsert, rootSubtask.beginSubtask(1));
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		int segmentLevel = Math.min(tree.getRoot().level(), 3);

		Set<Segment3d> segment3ds = createSegments(tree, segmentLevel, rootSubtask.beginSubtask(1));
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		Subtask renderSubtask = rootSubtask.beginSubtask(renderProportion);
		renderSubtask.setStatus("sending data to graphics card");
		renderSubtask.setTotal(segment3ds.size() * 2);

		for (Segment3d segment3d : segment3ds) {
			segment3d.populateData(geomBuffer);
			if (renderSubtask.isCanceling()) {
				return null;
			}
			renderSubtask.setCompleted(renderSubtask.getCompleted() + 1);
		}
		Survey3dModel model = new Survey3dModel(originalShots, shot3ds, tree, segment3ds, renderSubtask);
		if (rootSubtask.isCanceling()) {
			return null;
		}
		renderSubtask.end();
		rootSubtask.setCompleted(rootSubtask.getCompleted() + renderProportion);

		return model;
	}

	private static ByteBuffer createBuffer(int capacity) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}

	private static void createFillIndices(ByteBuffer dest, int shotCount) {
		for (int i = 0; i < shotCount; i++) {
			for (int index : offset(i * GEOM_VPS,
					0, 4, 2, 6, 2, 4,
					2, 6, 1, 5, 1, 6,
					1, 5, 3, 7, 3, 5,
					3, 7, 0, 4, 0, 7)) {
				dest.putInt(index);
			}
		}
	}

	private static ByteBuffer createInitialGeometry(Map<ShotKey, CalcShot> originalShots, Subtask task) {
		task.setStatus("creating geometry");
		task.setTotal(originalShots.size());

		BufferHelper geomHelper = new BufferHelper();

		int count = 0;
		for (CalcShot shot : originalShots.values()) {
			if (Vecmath.distance3(shot.fromStation.position, shot.toStation.position) > 200) {
				System.err.println(shot.fromStation.name + ": " + Arrays.toString(shot.fromStation.position) + " - "
						+ shot.toStation.name + ": " + Arrays.toString(shot.toStation.position));
			}

			putValues(geomHelper, shot.fromSplayPoints, shot.fromSplayNormals);
			putValues(geomHelper, shot.toSplayPoints, shot.toSplayNormals);

			if (count++ % 100 == 0 && task != null) {
				if (task.isCanceling()) {
					return null;
				}
				task.setCompleted(count);
			}
		}

		task.end();
		return geomHelper.toByteBuffer();
	}

	private static void createLineIndices(ByteBuffer dest, int shotCount) {
		for (int i = 0; i < shotCount; i++) {
			for (int index : offset(i * GEOM_VPS,
					0, 4, 0, 2, 4, 2, 4, 6,
					2, 6, 2, 1, 6, 1, 6, 5,
					1, 5, 1, 3, 5, 3, 5, 7,
					3, 7, 3, 0, 7, 0, 7, 4)) {
				dest.putInt(index);
			}
		}
	}

	private static Segment3d createSegment(Node<Shot3d> node) {
		Segment3d segment3d = new Segment3d();

		addShots(node, segment3d);

		segment3d.shot3ds.trimToSize();

		return segment3d;
	}

	private static void createSegments(RfStarTree.Node<Shot3d> node, int segmentLevel, Set<Segment3d> result) {
		if (node.level() == segmentLevel) {
			result.add(createSegment(node));
		} else if (node instanceof RfStarTree.Branch) {
			RfStarTree.Branch<Shot3d> branch = (RfStarTree.Branch<Shot3d>) node;
			for (int i = 0; i < branch.numChildren(); i++) {
				createSegments(branch.childAt(i), segmentLevel, result);
			}
		}
	}

	private static Set<Segment3d> createSegments(RfStarTree<Shot3d> tree, int segmentLevel, Subtask task) {
		task.setStatus("creating render segments");
		task.setIndeterminate(true);
		Set<Segment3d> result = new HashSet<>();

		createSegments(tree.getRoot(), segmentLevel, result);

		task.end();
		return result;
	}

	private static RfStarTree<Shot3d> createTree(Collection<Shot3d> shot3ds, ByteBuffer geomBuffer,
			int maxChildrenPerBranch, int minSplitSize, int numToReinsert, Subtask task) {
		RfStarTree<Shot3d> tree = new RfStarTree<>(3, maxChildrenPerBranch, minSplitSize, numToReinsert);

		int numShots = geomBuffer.capacity() / GEOM_BPS;

		task.setStatus("creating spatial index");
		task.setTotal(numShots);

		int s = 0;
		for (Shot3d shot : shot3ds) {
			float[] mbr = voidRectf(3);

			int shotStart = s * GEOM_BPS;

			for (int v = 0; v < GEOM_VPS; v++) {
				geomBuffer.position(shotStart + v * GEOM_BPV);
				float x = geomBuffer.getFloat();
				float y = geomBuffer.getFloat();
				float z = geomBuffer.getFloat();

				mbr[0] = nmin(mbr[0], x);
				mbr[1] = nmin(mbr[1], y);
				mbr[2] = nmin(mbr[2], z);
				mbr[3] = nmax(mbr[3], x);
				mbr[4] = nmax(mbr[4], y);
				mbr[5] = nmax(mbr[5], z);
			}

			shot.leaf = tree.createLeaf(mbr, shot);
			tree.insert(shot.leaf);

			s++;
			if (s % 100 == 0 && task.isCanceling()) {
				return null;
			}
			task.setCompleted(s);
		}

		task.end();
		return tree;
	}

	private static float getFromGlowA(ByteBuffer buffer, int shotIndex) {
		return buffer.getFloat(shotIndex * STATION_ATTR_BPS);
	}

	private static float getFromGlowB(ByteBuffer buffer, int shotIndex) {
		return buffer.getFloat(shotIndex * STATION_ATTR_BPS + 4);
	}

	private static float getToGlowA(ByteBuffer buffer, int shotIndex) {
		return buffer.getFloat(shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2);
	}

	private static float getToGlowB(ByteBuffer buffer, int shotIndex) {
		return buffer.getFloat(shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2 + 4);
	}

	private static int[] offset(int offset, int... in) {
		for (int i = 0; i < in.length; i++) {
			in[i] += offset;
		}
		return in;
	}

	private static void putValues(BufferHelper geomHelper, float[] splayPoints, float[] splayNormals) {
		for (int i = 0; i < splayPoints.length; i += 3) {
			geomHelper.put(splayPoints[i], splayPoints[i + 1], splayPoints[i + 2]);
			geomHelper.put(splayNormals[i], splayNormals[i + 1], splayNormals[i + 2]);
		}
	}

	private static void setFromGlowA(ByteBuffer buffer, int shotIndex, float value) {
		int index = shotIndex * STATION_ATTR_BPS;
		for (int i = 0; i < STATION_ATTR_VPS / 2; i++) {
			buffer.putFloat(index + i * STATION_ATTR_BPV, value);
		}
	}

	private static void setFromGlowB(ByteBuffer buffer, int shotIndex, float value) {
		int index = shotIndex * STATION_ATTR_BPS + 4;
		for (int i = 0; i < STATION_ATTR_VPS / 2; i++) {
			buffer.putFloat(index + i * STATION_ATTR_BPV, value);
		}
	}

	private static void setToGlowA(ByteBuffer buffer, int shotIndex, float value) {
		int index = shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2;
		for (int i = 0; i < STATION_ATTR_VPS / 2; i++) {
			buffer.putFloat(index + i * STATION_ATTR_BPV, value);
		}
	}

	private static void setToGlowB(ByteBuffer buffer, int shotIndex, float value) {
		int index = shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2 + 4;
		for (int i = 0; i < STATION_ATTR_VPS / 2; i++) {
			buffer.putFloat(index + i * STATION_ATTR_BPV, value);
		}
	}

	final Map<ShotKey, CalcShot> originalShots;

	final Map<ShotKey, Shot3d> shot3ds;

	final RfStarTree<Shot3d> tree;

	final Set<Segment3d> segment3ds;

	ColorParam colorParam = ColorParam.DEPTH;

	AxialSegment3dDrawer axialSegment3dDrawer = new AxialSegment3dDrawer();

	Param0Segment3dDrawer param0Segment3dDrawer = new Param0Segment3dDrawer();

	AtomicReference<MultiMap<Segment3dDrawer, Segment3d>> drawers = new AtomicReference<>();

	final Set<ShotKey> selectedShots = new HashSet<>();
	final Set<ShotKey> unmodifiableSelectedShots = Collections.unmodifiableSet(selectedShots);

	Shot3d hoveredShot;

	Float hoverLocation;

	LinearAxisConversion glowExtentConversion;

	final Set<Segment3d> segmentsWithGlow = new HashSet<>();

	LinearGradientPaint paramPaint;

	int paramTexture;

	BufferedImageInt paramTextureImage;

	boolean paramTextureNeedsUpdate;

	Uniform4fv highlightColors;

	Uniform3fv depthAxis;

	Uniform3fv depthOrigin;

	Uniform1fv ambient;

	Uniform1fv nearDist;

	Uniform1fv farDist;

	Uniform1fv loParam;

	Uniform1fv hiParam;

	Uniform4fv glowColor;

	private Survey3dModel(Map<ShotKey, CalcShot> originalShots, Map<ShotKey, Shot3d> shot3ds, RfStarTree<Shot3d> tree,
			Set<Segment3d> segment3ds, Subtask renderSubtask) {
		super();
		this.originalShots = originalShots;
		this.shot3ds = shot3ds;
		this.tree = tree;
		this.segment3ds = segment3ds;

		highlightColors = new Uniform4fv().name("u_highlightColors");
		highlightColors.value(
				0f, 0f, 0f, 0f,
				0f, 1f, 1f, 0.5f,
				0f, 1f, 1f, 0.5f);
		highlightColors.count(3);

		depthAxis = new Uniform3fv().name("u_axis").value(0f, -1f, 0f);
		depthOrigin = new Uniform3fv().name("u_origin").value(0f, 0f, 0f);

		glowColor = new Uniform4fv().name("u_glowColor").value(0f, 1f, 1f, 1f);

		ambient = new Uniform1fv().name("u_ambient").value(0.5f);

		loParam = new Uniform1fv().name("u_loParam").value(0);
		hiParam = new Uniform1fv().name("u_hiParam").value(1000);
		nearDist = new Uniform1fv().name("u_nearDist").value(0);
		farDist = new Uniform1fv().name("u_farDist").value(1000);

		for (Segment3d segment3d : segment3ds) {
			segment3d.drawers.add(axialSegment3dDrawer);
		}

		MultiMap<Segment3dDrawer, Segment3d> drawers = LinkedHashSetMultiMap.newInstance();
		drawers.putAll(axialSegment3dDrawer, segment3ds);

		this.drawers.set(drawers);
	}

	private void applySelectionHighlights(Shot3d shot3d) {
		ByteBuffer buffer = shot3d.segment3d.stationAttrs.buffer();
		for (int i = 0; i < STATION_ATTR_VPS; i++) {
			int index = shot3d.indexInSegment * STATION_ATTR_BPS + 8 + i * STATION_ATTR_BPV;
			if (buffer.getFloat(index) != 1) {
				buffer.putFloat(index, 2f);
			}
		}
	}

	public float[] calcAutofitAllParamRange(Subtask subtask) {
		if (subtask != null) {
			subtask.setTotal(segment3ds.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}

		final float[] range = { Float.MAX_VALUE, -Float.MAX_VALUE };

		int completed = 0;
		for (Segment3d segment3d : segment3ds) {
			segment3d.calcParamRange(Survey3dModel.this, colorParam, range);

			if (completed++ % 100 == 0) {
				if (subtask != null) {
					if (subtask.isCanceling()) {
						return null;
					}
					subtask.setCompleted(completed);
				}
			}
		}

		return range;
	}

	public float[] calcAutofitParamRange(Collection<ShotKey> shots, Subtask subtask) {
		if (subtask != null) {
			subtask.setTotal(segment3ds.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}

		final float[] range = { Float.MAX_VALUE, -Float.MAX_VALUE };

		int completed = 0;
		for (ShotKey key : shots) {
			Shot3d shot = shot3ds.get(key);
			if (shot == null) {
				continue;
			}
			shot.calcParamRange(Survey3dModel.this, colorParam, range);

			if (completed++ % 100 == 0) {
				if (subtask != null) {
					if (subtask.isCanceling()) {
						return null;
					}
					subtask.setCompleted(completed);
				}
			}
		}

		return range;
	}

	public float[] calcAutofitParamRange(Subtask subtask) {
		if (selectedShots.size() < 2 || colorParam == ColorParam.DISTANCE_ALONG_SHOTS) {
			return calcAutofitAllParamRange(subtask);
		} else {
			return calcAutofitSelectedParamRange(subtask);
		}
	}

	public float[] calcAutofitSelectedParamRange(Subtask subtask) {
		if (subtask != null) {
			subtask.setTotal(segment3ds.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}

		final float[] range = { Float.MAX_VALUE, -Float.MAX_VALUE };

		int completed = 0;
		for (ShotKey key : selectedShots) {
			Shot3d shot3d = shot3ds.get(key);
			shot3d.calcParamRange(Survey3dModel.this, colorParam, range);

			if (completed++ % 100 == 0) {
				if (subtask != null) {
					if (subtask.isCanceling()) {
						return null;
					}
					subtask.setCompleted(completed);
				}
			}
		}

		return range;
	}

	public void calcDistFromSelected(Subtask subtask) {
		final Map<CalcStation, Double> distances = new HashMap<>();

		class PEntry implements Comparable<PEntry> {
			public final double priority;
			public final CalcStation station;

			public PEntry(double priority, CalcStation station) {
				super();
				this.priority = priority;
				this.station = station;
			}

			@Override
			public int compareTo(PEntry o) {
				return Double.compare(priority, o.priority);
			}
		}

		PriorityQueue<PEntry> queue = new PriorityQueue<>();

		for (ShotKey key : selectedShots) {
			CalcShot origShot = originalShots.get(key);
			distances.put(origShot.fromStation, 0.0);
			distances.put(origShot.toStation, 0.0);
			queue.add(new PEntry(0.0, origShot.fromStation));
			queue.add(new PEntry(0.0, origShot.toStation));
		}

		while (!queue.isEmpty()) {
			PEntry next = queue.poll();
			for (CalcShot shot : next.station.shots.values()) {
				CalcStation nextStation = shot.otherStation(next.station);
				if (!distances.containsKey(nextStation)) {
					double distance = next.priority + colorParam.calcTraversalDistance(shot);
					distances.put(nextStation, distance);
					queue.add(new PEntry(distance, nextStation));
				}
			}
		}

		if (subtask != null) {
			subtask.setTotal(segment3ds.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}
		final Iterator<Segment3d> segmentIterator = segment3ds.iterator();

		int processed = 0;
		while (segmentIterator.hasNext()) {
			Segment3d segment3d = segmentIterator.next();
			segment3d.calcParam0(Survey3dModel.this, distances);

			if (processed++ % 100 == 0) {
				if (subtask != null) {
					if (subtask.isCanceling()) {
						return;
					}
					subtask.setCompleted(processed);
				}
			}
		}
	}

	public void calcDistFromShots(Set<ShotKey> shots, Subtask subtask) {
		final Map<CalcStation, Double> distances = new HashMap<>();

		class PEntry implements Comparable<PEntry> {
			public final double priority;
			public final CalcStation station;

			public PEntry(double priority, CalcStation station) {
				super();
				this.priority = priority;
				this.station = station;
			}

			@Override
			public int compareTo(PEntry o) {
				return Double.compare(priority, o.priority);
			}
		}

		PriorityQueue<PEntry> queue = new PriorityQueue<>();

		for (ShotKey key : shots) {
			CalcShot origShot = originalShots.get(key);
			distances.put(origShot.fromStation, 0.0);
			distances.put(origShot.toStation, 0.0);
			queue.add(new PEntry(0.0, origShot.fromStation));
			queue.add(new PEntry(0.0, origShot.toStation));
		}

		while (!queue.isEmpty()) {
			PEntry next = queue.poll();
			for (CalcShot shot : next.station.shots.values()) {
				CalcStation nextStation = shot.otherStation(next.station);
				if (!distances.containsKey(nextStation)) {
					double distance = next.priority + colorParam.calcTraversalDistance(shot);
					distances.put(nextStation, distance);
					queue.add(new PEntry(distance, nextStation));
				}
			}
		}

		if (subtask != null) {
			subtask.setTotal(segment3ds.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}
		final Iterator<Segment3d> segmentIterator = segment3ds.iterator();

		int processed = 0;
		while (segmentIterator.hasNext()) {
			Segment3d segment3d = segmentIterator.next();
			segment3d.calcParam0(Survey3dModel.this, distances);

			if (processed++ % 100 == 0) {
				if (subtask != null) {
					if (subtask.isCanceling()) {
						return;
					}
					subtask.setCompleted(processed);
				}
			}
		}
	}

	@Override
	public void dispose(GL2ES2 gl) {
		for (Segment3d segment3d : segment3ds) {
			segment3d.dispose(gl);
		}

		MultiMap<Segment3dDrawer, Segment3d> drawers = this.drawers.get();

		for (Segment3dDrawer drawer : drawers.keySet()) {
			drawer.dispose(gl);
		}

		disposeParamTexture(gl);
	}

	private void disposeParamTexture(GL2ES2 gl) {
		if (paramTexture > 0) {
			gl.glDeleteTextures(1, new int[] { paramTexture }, 0);
			paramTexture = 0;
		}
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (paramTextureNeedsUpdate) {
			updateParamTexture(gl);
			paramTextureNeedsUpdate = false;
		}
		MultiMap<Segment3dDrawer, Segment3d> drawers = this.drawers.get();

		for (Segment3dDrawer drawer : drawers.keySet()) {
			drawer.draw(drawers.get(drawer), context, gl, m, n);
		}
	}

	public SelectionEditor editSelection() {
		return new SelectionEditor();
	}

	public void getCenter(float[] center) {
		float[] mbr = tree.getRoot().mbr();
		center[0] = (mbr[0] + mbr[3]) * 0.5f;
		center[1] = (mbr[1] + mbr[4]) * 0.5f;
		center[2] = (mbr[2] + mbr[5]) * 0.5f;
	}

	private float getFarthestExtent(Set<ShotKey> shotsInView, float[] shotsInViewMbr, float[] direction,
			FloatBinaryOperator extentFunction) {
		FloatHolder farthest = new FloatHolder(Float.NaN);

		float[] testPoint = new float[3];

		RTraversal.traverse(getTree().getRoot(),
				node -> {
					if (!Rectmath.intersects3(shotsInViewMbr, node.mbr())) {
						return false;
					}
					// return Rectmath.findCorner3( node.mbr( ) , testPoint ,
					// corner -> {
					// float dist = Vecmath.dot3( corner , direction );
					// return farthest.value != extentFunction.applyAsFloat(
					// farthest.value , dist ) ? true : null;
					// } ) != null;
					return true;
				},
				leaf -> {
					if (shotsInView.contains(leaf.object().key)) {
						for (float[] coord : leaf.object().coordIterable()) {
							float dist = Vecmath.dot3(coord, direction);
							farthest.value = extentFunction.applyAsFloat(farthest.value, dist);
						}
					}
					return true;
				});

		return farthest.value;
	}

	public Set<Shot3d> getHoveredShots() {
		return hoveredShot == null ? Collections.<Shot3d>emptySet() : Collections.singleton(hoveredShot);
	}

	public float[] getOrthoBounds(Set<ShotKey> shotsInView, float[] orthoRight, float[] orthoUp,
			float[] orthoForward) {
		float[] result = new float[6];

		float[] shotsInViewMbr = Rectmath.voidRectf(3);

		for (ShotKey key : shotsInView) {
			Shot3d shot = shot3ds.get(key);
			if (shot != null) {
				shot.unionMbrInto(shotsInViewMbr);
			}
		}

		FloatBinaryOperator minFunc = (a, b) -> Float.isNaN(a) || b < a ? b : a;
		FloatBinaryOperator maxFunc = (a, b) -> Float.isNaN(a) || b > a ? b : a;

		result[0] = getFarthestExtent(shotsInView, shotsInViewMbr, orthoRight, minFunc);
		result[1] = getFarthestExtent(shotsInView, shotsInViewMbr, orthoUp, minFunc);
		result[2] = getFarthestExtent(shotsInView, shotsInViewMbr, orthoForward, minFunc);
		result[3] = getFarthestExtent(shotsInView, shotsInViewMbr, orthoRight, maxFunc);
		result[4] = getFarthestExtent(shotsInView, shotsInViewMbr, orthoUp, maxFunc);
		result[5] = getFarthestExtent(shotsInView, shotsInViewMbr, orthoForward, maxFunc);

		return result;
	}

	public Shot3d getShot(ShotKey key) {
		return shot3ds.get(key);
	}

	public Set<ShotKey> getSelectedShots() {
		return unmodifiableSelectedShots;
	}

	public void getShotsIn(PlanarHull3f hull, Set<ShotKey> result) {
		RTraversal.traverse(getTree().getRoot(), node -> {
			if (hull.containsBox(node.mbr())) {
				RTraversal.traverse(node, node2 -> true, leaf -> result.add(leaf.object().key));
				return false;
			}
			return hull.intersectsBox(node.mbr());
		}, leaf -> {
			for (float[] coord : leaf.object().coordIterable()) {
				if (!hull.containsPoint(coord)) {
					return true;
				}
			}
			result.add(leaf.object().key);
			return true;
		});
	}

	public RfStarTree<Shot3d> getTree() {
		return tree;
	}

	@Override
	public void init(GL2ES2 gl) {
	}

	public void pickShots(float[] coneOrigin, float[] coneDirection, float coneAngle,
			Shot3dPickContext spc, List<PickResult<Shot3d>> pickResults) {
		pickShots(tree.getRoot(), coneOrigin, coneDirection, coneAngle, spc, pickResults);
	}

	public void pickShots(float[] rayOrigin, float[] rayDirection,
			Shot3dPickContext spc, List<PickResult<Shot3d>> pickResults) {
		pickShots(tree.getRoot(), rayOrigin, rayDirection, spc, pickResults);
	}

	public void pickShots(PlanarHull3f pickHull, Shot3dPickContext spc, List<PickResult<Shot3d>> pickResults) {
		RTraversal.traverse(tree.getRoot(),
				node -> pickHull.intersectsBox(node.mbr()),
				leaf -> {
					leaf.object().pick(pickHull, spc, pickResults);
					return true;
				});
	}

	private void pickShots(RNode<float[], Shot3d> node, float[] coneOrigin, float[] coneDirection,
			float coneAngle,
			Shot3dPickContext spc, List<PickResult<Shot3d>> pickResults) {
		if (spc.inConeTester.boxIntersectsCone(node.mbr(), coneOrigin, coneDirection, coneAngle)) {
			if (node instanceof RBranch) {
				RBranch<float[], Shot3d> branch = (RBranch<float[], Shot3d>) node;
				for (int i = 0; i < branch.numChildren(); i++) {
					pickShots(branch.childAt(i), coneOrigin, coneDirection, coneAngle, spc, pickResults);
				}
			} else if (node instanceof RLeaf) {
				Shot3d shot3d = ((RLeaf<float[], Shot3d>) node).object();
				shot3d.pick(coneOrigin, coneDirection, coneAngle, spc, pickResults);
			}
		}
	}

	private void pickShots(RNode<float[], Shot3d> node, float[] rayOrigin, float[] rayDirection,
			Shot3dPickContext spc, List<PickResult<Shot3d>> pickResults) {
		if (rayIntersects(rayOrigin, rayDirection, node.mbr())) {
			if (node instanceof RBranch) {
				RBranch<float[], Shot3d> branch = (RBranch<float[], Shot3d>) node;
				for (int i = 0; i < branch.numChildren(); i++) {
					pickShots(branch.childAt(i), rayOrigin, rayDirection, spc, pickResults);
				}
			} else if (node instanceof RLeaf) {
				Shot3d shot3d = ((RLeaf<float[], Shot3d>) node).object();
				shot3d.pick(rayOrigin, rayDirection, spc, pickResults);
			}
		}
	}

	public void setAmbientLight(float ambientLight) {
		ambient.value(ambientLight);
	}

	public void setColorParam(final ColorParam colorParam, Subtask subtask) {
		if (this.colorParam == colorParam) {
			return;
		}
		this.colorParam = colorParam;

		if (subtask != null) {
			subtask.setTotal(segment3ds.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}
		final Iterator<Segment3d> segmentIterator = segment3ds.iterator();

		MultiMap<Segment3dDrawer, Segment3d> newDrawers = LinkedHashSetMultiMap.newInstance();

		int completed = 0;
		while (segmentIterator.hasNext()) {
			Segment3d segment3d = segmentIterator.next();

			segment3d.drawers.clear();

			if (colorParam == ColorParam.DEPTH) {
				segment3d.drawers.add(axialSegment3dDrawer);
			} else {
				if (colorParam.isStationMetric()) {
					segment3d.calcParam0(Survey3dModel.this, colorParam);
				}
				segment3d.drawers.add(param0Segment3dDrawer);
			}

			for (Segment3dDrawer drawer : segment3d.drawers) {
				newDrawers.put(drawer, segment3d);
			}

			if (completed++ % 100 == 0) {
				if (subtask != null) {
					if (subtask.isCanceling()) {
						return;
					}
					subtask.setCompleted(completed);
				}
			}
		}

		drawers.set(newDrawers);

		if (colorParam.isTraversalMetric()) {
			calcDistFromSelected(subtask);
		}
	}

	public void setDepthAxis(float[] axis) {
		depthAxis.value(axis);
	}

	public void setDepthOrigin(float[] origin) {
		depthOrigin.value(origin);
	}

	public void setFarDist(float farDist) {
		this.farDist.value(farDist);
	}

	public void setHiParam(float hiParam) {
		this.hiParam.value(hiParam);
	}

	public void setLoParam(float loParam) {
		this.loParam.value(loParam);
	}

	public void setNearDist(float nearDist) {
		this.nearDist.value(nearDist);
	}

	public void setParamPaint(LinearGradientPaint paint) {
		if (paramPaint != paint) {
			paramPaint = paint;
			paramTextureNeedsUpdate = true;
		}
	}

	public void updateGlow(Shot3d hoveredShot, Float hoverLocation, LinearAxisConversion glowExtentConversion,
			Subtask subtask) {
		this.hoveredShot = hoveredShot;
		this.hoverLocation = hoverLocation;
		this.glowExtentConversion = glowExtentConversion;

		Set<Segment3d> newSegmentsWithGlow = new HashSet<>();

		if (hoveredShot != null) {
			CalcShot origShot = originalShots.get(hoveredShot.key);

			final Function<CalcStation, Stream<CalcShot>> connected = station -> station.shots.values().stream();

			Graphs.traverse2(Stream.<CalcStation>builder().add(origShot.fromStation).add(origShot.toStation).build(),
					station -> (station == origShot.fromStation ? hoverLocation : 1 - hoverLocation)
							* origShot.distance,
					(station, priority) -> {
						float glow = (float) glowExtentConversion.convert(priority);

						connected.apply(station).forEach(
								connectedShot -> {
									Shot3d shot3d = shot3ds.get(connectedShot.key());

									if (newSegmentsWithGlow.add(shot3d.segment3d)) {
										segmentsWithGlow.add(shot3d.segment3d);
										shot3d.segment3d.clearGlow();
									}

									if (station == connectedShot.fromStation) {
										shot3d.setFromGlowA(glow);
										shot3d.setFromGlowB(glow);
									} else {
										shot3d.setToGlowA(glow);
										shot3d.setToGlowB(glow);
									}

									shot3d.segment3d.stationAttrsNeedRebuffering.set(true);
								});
						return glow > 0;
					},
					connected,
					(CalcShot shot) -> shot.distance,
					(station, shot) -> station == shot.fromStation ? shot.toStation : shot.fromStation,
					() -> subtask != null && !subtask.isCanceling());

			hoveredShot.setFromGlowA(2 - hoveredShot.getFromGlowB());
			hoveredShot.setToGlowA(2 - hoveredShot.getToGlowB());
		}

		Iterator<Segment3d> segIter = segmentsWithGlow.iterator();

		for (Segment3d segment3d : IterableUtils.iterable(segIter)) {
			if (subtask != null && subtask.isCanceling()) {
				return;
			}
			if (!newSegmentsWithGlow.contains(segment3d)) {
				segIter.remove();
				segment3d.clearGlow();
				segment3d.stationAttrsNeedRebuffering.set(true);
			}
		}
	}

	private void updateHighlights(Collection<Shot3d> affectedShots) {
		// find the segments that are affected by the affected shots
		// (not just the segments containing those shots but segments containing
		// shots within highlight distance from an affected shot)
		Set<Segment3d> affectedSegments = new HashSet<>();
		for (Shot3d shot3d : affectedShots) {
			affectedSegments.add(shot3d.segment3d);
		}

		for (Segment3d segment3d : affectedSegments) {
			segment3d.clearHighlights();
		}

		for (ShotKey key : selectedShots) {
			Shot3d shot3d = shot3ds.get(key);
			if (affectedSegments.contains(shot3d.segment3d)) {
				applySelectionHighlights(shot3d);
			}
		}

		for (Segment3d segment3d : affectedSegments) {
			segment3d.stationAttrsNeedRebuffering.set(true);
		}
	}

	private void updateParamTexture(GL2ES2 gl) {
		if (paramTextureImage == null) {
			paramTextureImage = DirectDataBufferInt.createBufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB,
					new Point(), new Hashtable<>());
		}

		Graphics2D g2 = paramTextureImage.createGraphics();

		g2.clearRect(0, 0, paramTextureImage.getWidth(), paramTextureImage.getHeight());

		if (paramPaint != null) {
			g2.setPaint(new ParamGradientMapPaint(
					new float[] { 0, 0 },
					new float[] { 0, paramTextureImage.getHeight() },
					new float[] { paramTextureImage.getWidth(), 0 },
					0, 1,
					paramPaint.getFractions(),
					paramPaint.getColors()));
			g2.fillRect(0, 0, paramTextureImage.getWidth(), paramTextureImage.getHeight());
		}

		g2.dispose();

		IntBuffer paramTextureBuffer = ((DirectDataBufferInt) paramTextureImage.getRaster().getDataBuffer())
				.getData();

		if (paramTexture == 0) {
			int textures[] = new int[1];
			gl.glGenTextures(1, textures, 0);
			paramTexture = textures[0];
		}
		gl.glBindTexture(GL_TEXTURE_2D, paramTexture);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, paramTextureImage.getWidth(), paramTextureImage.getHeight(),
				0, GL_BGRA, GL_UNSIGNED_BYTE, paramTextureBuffer);
		gl.glBindTexture(GL_TEXTURE_2D, 0);
	}
}

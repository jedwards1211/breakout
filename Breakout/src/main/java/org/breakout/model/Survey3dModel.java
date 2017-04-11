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
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_BYTE;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.spatial.Rectmath.nmax;
import static org.andork.spatial.Rectmath.nmin;
import static org.andork.spatial.Rectmath.voidRectf;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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

import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.collect.PriorityEntry;
import org.andork.func.FloatBinaryOperator;
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
import org.andork.spatial.RTraversal;
import org.andork.spatial.Rectmath;
import org.andork.spatial.RfStarTree;
import org.andork.spatial.RfStarTree.Branch;
import org.andork.spatial.RfStarTree.Leaf;
import org.andork.spatial.RfStarTree.Node;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.Task;
import org.andork.util.Iterables;
import org.breakout.PickResult;
import org.breakout.awt.ParamGradientMapPaint;
import org.omg.CORBA.FloatHolder;

import com.andork.plot.LinearAxisConversion;
import com.jogamp.nativewindow.awt.DirectDataBufferInt;
import com.jogamp.nativewindow.awt.DirectDataBufferInt.BufferedImageInt;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.awt.TextRenderer;

public class Survey3dModel implements JoglDrawable, JoglResource {
	private class AxialSectionRenderer extends OneParamSectionRenderer {
		private int u_axis_location;
		private int u_origin_location;

		@Override
		protected void afterDraw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.afterDraw(sections, context, gl, m, n);
		}

		@Override
		protected void beforeDraw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.beforeDraw(sections, context, gl, m, n);
			gl.glUniform3fv(u_axis_location, 1, depthAxis.value(), 0);
			gl.glUniform3fv(u_origin_location, 1, depthOrigin.value(), 0);
		}

		@Override
		protected void beforeDraw(Section section, GL2ES2 gl, float[] m, float[] n) {
			super.beforeDraw(section, gl, m, n);
			section.lineIndices.init(gl);
			section.fillIndices.init(gl);
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
		protected void doDraw(Section section, GL2ES2 gl) {
			if (drawLines) {
				gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, section.lineIndices.id());
				gl.glDrawElements(GL_LINES, section.lineIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT, 0);
			}
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, section.fillIndices.id());
			gl.glDrawElements(GL_TRIANGLES, section.fillIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT,
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

	private class CenterlineRenderer implements SectionRenderer {
		protected int program = 0;

		protected int m_location;
		protected int v_location;
		protected int p_location;
		protected int a_pos_location;
		protected int u_color_location;

		protected String createFragmentShader() {
			return "#version 330\n" +
					createFragmentShaderVariables() +
					"void main() {" +
					createFragmentShaderCode() +
					"}";
		}

		protected String createFragmentShaderCode() {
			return "  if (v_dist > 200) discard;" +
					"  color = u_color;";
		}

		protected String createFragmentShaderVariables() {
			// lighting
			return "uniform float u_ambient;" +

			// distance coloration
					"in float v_dist;" +

					// color
					"uniform vec4 u_color;" +

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
					"  v_dist = -(v * m * vec4(a_pos, 1.0)).z;";
		}

		protected String createVertexShaderVariables() {
			return "uniform mat4 m;" +
					"uniform mat4 v;" +
					"uniform mat4 p;" +
					"in vec3 a_pos;" +

					// distance coloration
					"out float v_dist;";
		}

		@Override
		public void dispose(GL2ES2 gl) {
			if (program > 0) {
				gl.glDeleteProgram(program);
				program = 0;
			}
		}

		@Override
		public void draw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			if (program <= 0) {
				init(gl);
			}

			gl.glUseProgram(program);

			gl.glUniformMatrix4fv(m_location, 1, false, m, 0);
			gl.glUniformMatrix4fv(v_location, 1, false, context.viewMatrix(), 0);
			gl.glUniformMatrix4fv(p_location, 1, false, context.projectionMatrix(), 0);

			gl.glUniform4fv(u_color_location, 1, centerlineColor.value(), 0);

			gl.glEnableVertexAttribArray(a_pos_location);

			gl.glEnable(GL_DEPTH_TEST);
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);

			for (Section section : sections) {
				section.centerlineGeometry.init(gl);

				gl.glBindBuffer(GL_ARRAY_BUFFER, section.centerlineGeometry.id());
				gl.glVertexAttribPointer(a_pos_location, 3, GL_FLOAT, false, 12, 0);

				gl.glDrawArrays(GL_LINES, 0, section.shot3ds.size() * 2);
			}

			gl.glDisable(GL_DEPTH_TEST);
			gl.glDisable(GL.GL_STENCIL_TEST);
			gl.glDisableVertexAttribArray(a_pos_location);

			gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
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

				a_pos_location = gl.glGetAttribLocation(program, "a_pos");

				u_color_location = gl.glGetUniformLocation(program, "u_color");
			}
		}
	}

	private abstract class BaseSectionRenderer implements SectionRenderer {
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

		protected void afterDraw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			gl.glDisable(GL_DEPTH_TEST);
			gl.glDisableVertexAttribArray(a_pos_location);
			gl.glDisableVertexAttribArray(a_norm_location);
			gl.glDisableVertexAttribArray(a_glow_location);
			gl.glDisableVertexAttribArray(a_highlightIndex_location);

			gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		}

		protected void beforeDraw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			gl.glUniformMatrix4fv(m_location, 1, false, m, 0);
			gl.glUniformMatrix3fv(n_location, 1, false, n, 0);
			gl.glUniformMatrix4fv(v_location, 1, false, context.viewMatrix(), 0);
			gl.glUniformMatrix4fv(p_location, 1, false, context.projectionMatrix(), 0);

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

		protected void beforeDraw(Section section, GL2ES2 gl, float[] m, float[] n) {
			section.geometry.init(gl);
			section.stationAttrs.init(gl);
			if (section.stationAttrsNeedRebuffering.compareAndSet(true, false)) {
				section.stationAttrs.rebuffer(gl);
			}

			gl.glBindBuffer(GL_ARRAY_BUFFER, section.geometry.id());
			gl.glVertexAttribPointer(a_pos_location, 3, GL_FLOAT, false, GEOM_BPV, 0);
			gl.glVertexAttribPointer(a_norm_location, 3, GL_FLOAT, false, GEOM_BPV, 12);

			gl.glBindBuffer(GL_ARRAY_BUFFER, section.stationAttrs.id());
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

		protected abstract void doDraw(Section section, GL2ES2 gl);

		@Override
		public void draw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			if (program <= 0) {
				init(gl);
			}

			gl.glUseProgram(program);

			beforeDraw(sections, context, gl, m, n);

			for (Section section : sections) {
				draw(section, context, gl, m, n);
			}

			afterDraw(sections, context, gl, m, n);
		}

		public void draw(Section section, JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
			beforeDraw(section, gl, m, n);

			doDraw(section, gl);
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

	private abstract class OneParamSectionRenderer extends BaseSectionRenderer {
		private int u_loParam_location;
		private int u_hiParam_location;
		private int u_paramSampler_location;
		boolean drawLines = false;

		@Override
		protected void beforeDraw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.beforeDraw(sections, context, gl, m, n);

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

	private class Param0SectionRenderer extends OneParamSectionRenderer {
		private int a_param0_location;

		@Override
		protected void afterDraw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.afterDraw(sections, context, gl, m, n);
			gl.glDisableVertexAttribArray(a_param0_location);
		}

		@Override
		protected void beforeDraw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			super.beforeDraw(sections, context, gl, m, n);

			gl.glEnableVertexAttribArray(a_param0_location);
		}

		@Override
		protected void beforeDraw(Section section, GL2ES2 gl, float[] m, float[] n) {
			if (section.param0 == null) {
				return;
			}
			super.beforeDraw(section, gl, m, n);
			section.param0.init(gl);
			if (section.param0NeedsRebuffering.compareAndSet(true, false)) {
				section.param0.rebuffer(gl);
			}
			section.lineIndices.init(gl);
			section.fillIndices.init(gl);

			gl.glBindBuffer(GL_ARRAY_BUFFER, section.param0.id());
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
		protected void doDraw(Section section, GL2ES2 gl) {
			if (section.param0 == null) {
				return;
			}
			if (drawLines) {
				gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, section.lineIndices.id());
				gl.glDrawElements(GL_LINES, section.lineIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT, 0);
			}
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, section.fillIndices.id());
			gl.glDrawElements(GL_TRIANGLES, section.fillIndices.buffer().capacity() / BPI, GL_UNSIGNED_INT,
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

	public static class Label {
		final float[] position;
		float width;
		float height;
		float xOffset;
		float yOffset;
		float pushForward;
		float maxSpacing;
		final String text;

		public Label(CalcStation station) {
			position = new float[] {
					(float) station.position[0],
					(float) station.position[1],
					(float) station.position[2] };
			text = station.name;
			pushForward = 0;
			for (CalcShot shot : station.shots.values()) {
				pushForward = Math.max(pushForward, (float) shot.distance);
				CalcCrossSection crossSection = shot.getCrossSectionAt(station);
				if (crossSection == null || crossSection.measurements == null) {
					continue;
				}
				for (double measurement : crossSection.measurements) {
					pushForward = Math.max(pushForward, (float) measurement);
				}
			}
		}
	}

	public static class Section {
		final float[] mbr;
		final ArrayList<Shot3d> shot3ds;
		final Map<StationKey, Label> stationLabels;
		final Map<Float, Set<StationKey>> stationsToLabel;
		final List<Float> stationsToLabelSpacings;

		final LinkedList<SectionRenderer> renderers = new LinkedList<>();
		final float[] tempPoint = new float[3];

		int vertexCount;
		JoglBuffer centerlineGeometry;
		JoglBuffer geometry;
		JoglBuffer stationAttrs;
		final AtomicBoolean stationAttrsNeedRebuffering = new AtomicBoolean();
		JoglBuffer param0;
		final AtomicBoolean param0NeedsRebuffering = new AtomicBoolean();
		JoglBuffer fillIndices;
		JoglBuffer lineIndices;

		Section(float[] mbr, ArrayList<Shot3d> shot3ds, Map<Float, Set<StationKey>> stationsToLabel) {
			this.mbr = mbr;
			this.shot3ds = shot3ds;
			stationLabels = new HashMap<>();
			for (Shot3d shot3d : shot3ds) {
				CalcStation fromStation = shot3d.shot.fromStation;
				CalcStation toStation = shot3d.shot.toStation;
				if (!stationLabels.containsKey(fromStation.key())) {
					stationLabels.put(fromStation.key(), new Label(fromStation));
				}
				if (!stationLabels.containsKey(toStation.key())) {
					stationLabels.put(toStation.key(), new Label(toStation));
				}
			}
			stationsToLabelSpacings = new ArrayList<>(stationsToLabel.keySet());
			Collections.sort(stationsToLabelSpacings);
			this.stationsToLabel = new HashMap<>();
			for (Map.Entry<Float, Set<StationKey>> entry : stationsToLabel.entrySet()) {
				float spacing = entry.getKey();
				Set<StationKey> stationsToLabelForSection = new HashSet<>(entry.getValue());
				stationsToLabelForSection.retainAll(stationLabels.keySet());
				this.stationsToLabel.put(spacing, stationsToLabelForSection);
				for (StationKey key : stationsToLabelForSection) {
					Label stationLabel = stationLabels.get(key);
					if (stationLabel == null) {
						continue;
					}
					stationLabel.maxSpacing = Math.max(spacing, stationLabel.maxSpacing);
				}
			}
			populateData();
		}

		void populateData() {
			BufferHelper geomHelper = new BufferHelper();
			BufferHelper fillIndicesHelper = new BufferHelper();
			BufferHelper lineIndicesHelper = new BufferHelper();
			BufferHelper centerlineGeomHelper = new BufferHelper();

			for (Shot3d shot3d : shot3ds) {
				shot3d.section = this;
				shot3d.indexInVertices = geomHelper.sizeInBytes() / GEOM_BPV;
				shot3d.indexInFillIndices = fillIndicesHelper.sizeInBytes() / BPI;

				CalcShot shot = shot3d.shot;
				if (Vecmath.distance3(shot.fromStation.position, shot.toStation.position) > 500) {
					System.err.println(shot.fromStation.name + ": " + Arrays.toString(shot.fromStation.position) + " - "
							+ shot.toStation.name + ": " + Arrays.toString(shot.toStation.position));
				}

				putValues(geomHelper, shot.vertices, shot.normals);
				centerlineGeomHelper.putAsFloats(shot.fromStation.position);
				centerlineGeomHelper.putAsFloats(shot.toStation.position);

				for (int index : shot.indices) {
					fillIndicesHelper.put(index + shot3d.indexInVertices);
					lineIndicesHelper.put(index + shot3d.indexInVertices);
				}
			}
			vertexCount = geomHelper.sizeInBytes() / GEOM_BPV;
			stationAttrs = new JoglBuffer().buffer(createBuffer(vertexCount * STATION_ATTR_BPV));

			geometry = new JoglBuffer().buffer(geomHelper.toByteBuffer());
			fillIndices = new JoglBuffer().buffer(fillIndicesHelper.toByteBuffer());
			lineIndices = new JoglBuffer().buffer(lineIndicesHelper.toByteBuffer());
			centerlineGeometry = new JoglBuffer().buffer(centerlineGeomHelper.toByteBuffer());

			geometry.buffer().position(0);
			stationAttrs.buffer().position(0);
			fillIndices.buffer().position(0);
			lineIndices.buffer().position(0);
			centerlineGeometry.buffer().position(0);
		}

		void calcParam0(Survey3dModel model, ColorParam param) {
			param0 = new JoglBuffer().buffer(createBuffer(vertexCount * 4));
			param0.buffer().position(0);
			for (Shot3d shot3d : shot3ds) {
				CalcShot origShot = shot3d.shot;

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

				for (int i = 0; i < shot3d.vertexCount; i++) {
					param0.buffer().putFloat(
							shot3d.shot.interpolateParamAtVertex(fromValue, toValue, i));
				}
			}
			param0NeedsRebuffering.set(true);
		}

		void calcParam0(Survey3dModel model, Map<StationKey, Double> stationValues) {
			param0 = new JoglBuffer().buffer(createBuffer(vertexCount * 4));
			param0.buffer().position(0);
			for (Shot3d shot3d : shot3ds) {
				CalcShot origShot = shot3d.shot;
				Double fromValue = stationValues.get(origShot.fromStation.key());
				if (fromValue == null) {
					fromValue = Double.NaN;
				}
				Double toValue = stationValues.get(origShot.toStation.key());
				if (toValue == null) {
					toValue = Double.NaN;
				}
				for (int i = 0; i < shot3d.vertexCount; i++) {
					param0.buffer().putFloat(
							shot3d.shot.interpolateParamAtVertex(fromValue.floatValue(), toValue.floatValue(), i));
				}
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

		float minDistanceForSpacing(JoglDrawContext context, float spacing) {
			// TODO: something view-dependent and less arbitrary
			return spacing * 30;
		}

		public void updateLabels(Font font, FontRenderContext frc) {
			for (Map.Entry<StationKey, Label> entry : stationLabels.entrySet()) {
				Label label = entry.getValue();
				Rectangle2D bounds = font.getStringBounds(label.text, frc);
				label.width = (float) bounds.getWidth();
				label.height = (float) bounds.getHeight();
				label.xOffset = (float) -bounds.getWidth() / 2;
				label.yOffset = (float) bounds.getHeight() / 2;
			}
		}

		public void drawLabels(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n, TextRenderer textRenderer,
				RfStarTree<Void> labelTree, Set<StationKey> stationsToEmphasize) {
			context.getViewPoint(tempPoint);
			float minDistance = Rectmath.minDistance3(mbr, tempPoint);

			Set<StationKey> stationsToLabel = stationLabels.keySet();
			for (int i = stationsToLabelSpacings.size() - 1; i >= 0; i--) {
				float spacing = stationsToLabelSpacings.get(i);
				if (minDistance > minDistanceForSpacing(context, spacing)) {
					stationsToLabel = this.stationsToLabel.get(spacing);
					break;
				}
			}

			for (StationKey key : stationsToEmphasize) {
				if (!stationsToLabel.contains(key)) {
					Label label = stationLabels.get(key);
					if (label != null) {
						drawLabel(label, context, gl, m, n, textRenderer, labelTree, true, 1f);
					}
				}
			}
			for (StationKey key : stationsToLabel) {
				Label label = stationLabels.get(key);
				boolean emphasize = stationsToEmphasize.contains(key);
				drawLabel(label, context, gl, m, n, textRenderer, labelTree, emphasize, emphasize ? 1f : 0.5f);
			}
		}

		void drawLabel(Label label, JoglDrawContext context, GL2ES2 gl, float[] m, float[] n,
				TextRenderer textRenderer,
				RfStarTree<Void> labelTree, boolean force, float scale) {
			Vecmath.mpmulAffine(context.viewMatrix(), label.position, tempPoint);
			if (tempPoint[2] > 0) {
				// label is behind camera
				return;
			}
			float labelDistanceSquared = Vecmath.dot3(tempPoint, tempPoint);
			float minDistanceForLabel = minDistanceForSpacing(context, label.maxSpacing);
			if (labelDistanceSquared > minDistanceForLabel * minDistanceForLabel && !force) {
				return;
			}

			float z = Float.NaN;
			if (labelDistanceSquared < label.pushForward * label.pushForward) {
				z = 0.99999f;
			} else {
				float labelDistance = (float) Math.sqrt(labelDistanceSquared);
				float pushedForwardDistance = labelDistance - label.pushForward;
				float scaleFactor = pushedForwardDistance / labelDistance;
				tempPoint[0] *= scaleFactor;
				tempPoint[1] *= scaleFactor;
				tempPoint[2] *= scaleFactor;
			}

			Vecmath.mpmul(context.viewToScreen(), tempPoint);
			if (tempPoint[2] < -1) {
				// label is outside of clipping bounds
				return;
			}
			float x = tempPoint[0] + label.xOffset;
			float y = tempPoint[1] + label.yOffset;
			float[] labelMbr = {
					x - label.width * scale / 2,
					y,
					x + label.width * scale / 2,
					y + label.height * scale };
			if (labelTree.containsLeafIntersecting(labelMbr)) {
				return;
			} else {
				Leaf<Void> leaf = new Leaf<>(labelMbr, null);
				labelTree.insert(leaf);
			}
			if (Float.isNaN(z)) {
				z = tempPoint[2];
			}
			textRenderer.draw3D(label.text, x, y, z, scale);
		}
	}

	private static interface SectionRenderer extends JoglResource {
		public void draw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
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
				affectedShots.put(shotKey, shot3ds.get(shotKey));
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
		CalcShot shot;

		Section section;
		int indexInVertices;
		int vertexCount;
		int indexInFillIndices;
		int triangleCount;

		RfStarTree.Leaf<Shot3d> leaf;

		Shot3d(ShotKey key, CalcShot shot) {
			super();
			this.key = key;
			this.shot = shot;
			vertexCount = shot.vertices.length / 3;
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
				int i = indexInVertices;
				boolean last = indexInVertices + vertexCount == section.vertexCount;

				ByteBuffer param0buffer = section.param0.buffer();

				int maxIndex = last ? param0buffer.capacity() / 4 : indexInVertices + vertexCount;

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
							return index < vertexCount;
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
			if (i < 0 || i >= vertexCount) {
				throw new IllegalArgumentException("i must be between 0 and " + vertexCount);
			}
			ByteBuffer vertBuffer = section.geometry.buffer();
			int baseIndex = indexInVertices * GEOM_BPV;
			result[0] = vertBuffer.getFloat(baseIndex);
			result[1] = vertBuffer.getFloat(baseIndex + 4);
			result[2] = vertBuffer.getFloat(baseIndex + 8);
			vertBuffer.position(0);
		}

		public void pick(float[] coneOrigin, float[] coneDirection, float coneAngle, Shot3dPickContext c,
				List<PickResult<Shot3d>> pickResults) {
			Shot3dPickResult result = null;

			int i = 0;
			float polarity0, polarity1, polarity2;
			while (i < shot.indices.length) {
				int vertexIndex = i++;
				polarity0 = shot.polarities[vertexIndex];
				int coordIndex = vertexIndex * 3;
				c.p0[0] = shot.vertices[coordIndex];
				c.p0[1] = shot.vertices[coordIndex + 1];
				c.p0[2] = shot.vertices[coordIndex + 2];
				vertexIndex = i++;
				polarity1 = shot.polarities[vertexIndex];
				coordIndex = vertexIndex * 3;
				c.p1[0] = shot.vertices[coordIndex];
				c.p1[1] = shot.vertices[coordIndex + 1];
				c.p1[2] = shot.vertices[coordIndex + 2];
				vertexIndex = i++;
				polarity2 = shot.polarities[vertexIndex];
				coordIndex = vertexIndex * 3;
				c.p2[0] = shot.vertices[coordIndex];
				c.p2[1] = shot.vertices[coordIndex + 1];
				c.p2[2] = shot.vertices[coordIndex + 2];

				float uPolarity = polarity1 - polarity0;
				float vPolarity = polarity2 - polarity0;

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
							result.locationAlongShot = polarity0 + uPolarity * c.lpx.u + vPolarity * c.lpx.v;
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
								result.locationAlongShot = polarity0 * (1 - c.inConeTester.s) +
										polarity1 * c.inConeTester.s;
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
								result.locationAlongShot = polarity1 * (1 - c.inConeTester.s) +
										polarity2 * c.inConeTester.s;
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
								result.locationAlongShot = polarity2 * (1 - c.inConeTester.s) +
										polarity0 * c.inConeTester.s;
								Vecmath.interp3(c.p2, c.p0, c.inConeTester.s, result.location);
							}
						}
					}
				} catch (Exception ex) {

				}
			}

			if (result != null) {
				pickResults.add(result);
			}
		}

		public void pick(PlanarHull3f hull, Shot3dPickContext c, List<PickResult<Shot3d>> pickResults) {
			Shot3dPickResult result = null;

			int i = 0;
			float polarity0, polarity1, polarity2;
			while (i < shot.indices.length) {
				int vertexIndex = shot.indices[i++];
				polarity0 = shot.polarities[vertexIndex];
				int coordIndex = vertexIndex * 3;
				c.p0[0] = shot.vertices[coordIndex];
				c.p0[1] = shot.vertices[coordIndex + 1];
				c.p0[2] = shot.vertices[coordIndex + 2];
				vertexIndex = shot.indices[i++];
				polarity1 = shot.polarities[vertexIndex];
				coordIndex = vertexIndex * 3;
				c.p1[0] = shot.vertices[coordIndex];
				c.p1[1] = shot.vertices[coordIndex + 1];
				c.p1[2] = shot.vertices[coordIndex + 2];
				vertexIndex = shot.indices[i++];
				polarity2 = shot.polarities[vertexIndex];
				coordIndex = vertexIndex * 3;
				c.p2[0] = shot.vertices[coordIndex];
				c.p2[1] = shot.vertices[coordIndex + 1];
				c.p2[2] = shot.vertices[coordIndex + 2];

				float uPolarity = polarity1 - polarity0;
				float vPolarity = polarity2 - polarity0;

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
							result.locationAlongShot = polarity0 + uPolarity * c.lpx.u + vPolarity * c.lpx.v;
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
									// TODO if only I had documented
									// TwoPlaneIntersectionBetter...
									result.locationAlongShot = 0.5f;
									setf(result.location, c.x0);
								}
							}
						}
					}
				} catch (Exception ex) {
				}
			}
			if (result != null) {
				pickResults.add(result);
			}
		}

		public void setGlow(float fromGlowA, float toGlowA, float fromGlowB, float toGlowB) {
			ByteBuffer buffer = section.stationAttrs.buffer();
			for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
				buffer.putFloat(
						(indexInVertices + vertexIndex) * STATION_ATTR_BPV,
						shot.interpolateParamAtVertex(fromGlowA, toGlowA, vertexIndex));
				buffer.putFloat(
						(indexInVertices + vertexIndex) * STATION_ATTR_BPV + 4,
						shot.interpolateParamAtVertex(fromGlowB, toGlowB, vertexIndex));
			}
		}

		public void setGlow(float fromGlow, float toGlow) {
			setGlow(fromGlow, toGlow, fromGlow, toGlow);
		}

		public void unionMbrInto(float[] mbr) {
			Rectmath.union3(mbr, leaf.mbr(), mbr);
		}

		public ShotKey key() {
			return key;
		}

		void applySelectionHighlights() {
			ByteBuffer buffer = section.stationAttrs.buffer();
			for (int i = 0; i < vertexCount; i++) {
				int index = (indexInVertices + i) * STATION_ATTR_BPV + 8;
				if (buffer.getFloat(index) != 1) {
					buffer.putFloat(index, 2f);
				}
			}
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

	/**
	 * Bytes per vertex
	 */
	private static final int GEOM_BPV = 24;
	/**
	 * Station attribute bytes per vertex
	 */
	private static final int STATION_ATTR_BPV = 12;
	/**
	 * Bytes per index
	 */
	private static final int BPI = 4;
	private static final int[][] hullTriangleIndices = {
			{ 0, 6, 4 }, { 6, 0, 2 },
			{ 2, 7, 6 }, { 7, 2, 3 },
			{ 3, 5, 7 }, { 5, 3, 1 },
			{ 1, 4, 5 }, { 4, 1, 0 },
			{ 0, 3, 2 }, { 3, 0, 1 },
			{ 4, 7, 6 }, { 7, 4, 5 }
	};

	private static void addShots(Node<Shot3d> node, Collection<Shot3d> shot3ds) {
		if (node instanceof Leaf) {
			shot3ds.add(((Leaf<Shot3d>) node).object());
		} else if (node instanceof Branch) {
			Branch<Shot3d> branch = (Branch<Shot3d>) node;
			for (int i = 0; i < branch.numChildren(); i++) {
				addShots(branch.childAt(i), shot3ds);
			}
		}
	}

	private static ArrayList<Shot3d> getShots(Node<Shot3d> node) {
		ArrayList<Shot3d> shot3ds = new ArrayList<>();
		addShots(node, shot3ds);
		shot3ds.trimToSize();
		return shot3ds;
	}

	public static Survey3dModel create(CalcProject project, int maxChildrenPerBranch, int minSplitSize,
			int numToReinsert, Task task) {
		Subtask rootSubtask = null;
		int renderProportion = 6;

		if (task != null) {
			task.setTotal(1000);
			rootSubtask = new Subtask(task);
		} else {
			rootSubtask = Subtask.dummySubtask();
		}
		rootSubtask.setStatus("Updating view");
		rootSubtask.setTotal(renderProportion + 5);

		Map<ShotKey, Shot3d> shot3ds = new HashMap<>();
		for (Map.Entry<ShotKey, CalcShot> entry : project.shots.entrySet()) {
			shot3ds.put(entry.getKey(), new Shot3d(entry.getKey(), entry.getValue()));
		}
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		RfStarTree<Shot3d> tree = createTree(shot3ds.values(), maxChildrenPerBranch, minSplitSize,
				numToReinsert, rootSubtask.beginSubtask(1));
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		int sectionLevel = Math.min(tree.getRoot().level(), 3);

		Map<Float, Set<StationKey>> stationsToLabel = computeStationsToLabel(
				project.stations.values(), Arrays.asList(5f, 10f, 20f, 40f, 80f, 160f, 320f));
		stationsToLabel.put(2f, project.stations.keySet());

		Set<Section> sections = createSections(tree, sectionLevel, stationsToLabel, rootSubtask.beginSubtask(1));
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		Font labelFont = new Font("Arial", Font.BOLD, 24);
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		for (Section section : sections) {
			section.updateLabels(labelFont, frc);
		}

		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + 1);

		Survey3dModel model = new Survey3dModel(shot3ds, tree, sections, labelFont);
		if (rootSubtask.isCanceling()) {
			return null;
		}
		rootSubtask.setCompleted(rootSubtask.getCompleted() + renderProportion);

		return model;
	}

	private static ByteBuffer createBuffer(int capacity) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}

	private static void createSections(RfStarTree.Node<Shot3d> node, int sectionLevel,
			Map<Float, Set<StationKey>> stationsToLabel, Set<Section> result) {
		if (node.level() == sectionLevel) {
			ArrayList<Shot3d> shot3ds = getShots(node);
			result.add(new Section(node.mbr(), shot3ds, stationsToLabel));
		} else if (node instanceof RfStarTree.Branch) {
			RfStarTree.Branch<Shot3d> branch = (RfStarTree.Branch<Shot3d>) node;
			for (int i = 0; i < branch.numChildren(); i++) {
				createSections(branch.childAt(i), sectionLevel, stationsToLabel, result);
			}
		}
	}

	private static Set<Section> createSections(RfStarTree<Shot3d> tree, int sectionLevel,
			Map<Float, Set<StationKey>> stationsToLabel, Subtask task) {
		task.setStatus("creating render sections");
		task.setIndeterminate(true);
		Set<Section> result = new HashSet<>();

		createSections(tree.getRoot(), sectionLevel, stationsToLabel, result);

		task.end();
		return result;
	}

	private static RfStarTree<Shot3d> createTree(Collection<Shot3d> shot3ds,
			int maxChildrenPerBranch, int minSplitSize, int numToReinsert, Subtask task) {
		RfStarTree<Shot3d> tree = new RfStarTree<>(3, maxChildrenPerBranch, minSplitSize, numToReinsert);

		task.setStatus("creating spatial index");
		task.setTotal(shot3ds.size());

		int s = 0;
		for (Shot3d shot3d : shot3ds) {
			float[] mbr = voidRectf(3);

			CalcShot shot = shot3d.shot;

			for (int i = 0; i < shot.vertices.length; i += 3) {
				float x = shot.vertices[i];
				float y = shot.vertices[i + 1];
				float z = shot.vertices[i + 2];
				mbr[0] = nmin(mbr[0], x);
				mbr[1] = nmin(mbr[1], y);
				mbr[2] = nmin(mbr[2], z);
				mbr[3] = nmax(mbr[3], x);
				mbr[4] = nmax(mbr[4], y);
				mbr[5] = nmax(mbr[5], z);
			}

			shot3d.leaf = tree.createLeaf(mbr, shot3d);
			tree.insert(shot3d.leaf);

			s++;
			if (s % 100 == 0 && task.isCanceling()) {
				return null;
			}
			task.setCompleted(s);
		}

		task.end();
		return tree;
	}

	private static void putValues(BufferHelper geomHelper, float[] splayPoints, float[] splayNormals) {
		for (int i = 0; i < splayPoints.length; i += 3) {
			geomHelper.put(splayPoints[i], splayPoints[i + 1], splayPoints[i + 2]);
			geomHelper.put(splayNormals[i], splayNormals[i + 1], splayNormals[i + 2]);
		}
	}

	private static float getLabelPriority(CalcStation station) {
		float priority = 0;
		// priority starts out as sum total of all the cross section measurements at a station,
		// so that bigger passage is more likely to get labeled
		for (CalcShot shot : station.shots.values()) {
			CalcCrossSection crossSection = shot.getCrossSectionAt(station);
			if (crossSection == null) {
				continue;
			}
			for (double measurement : crossSection.measurements) {
				priority += measurement;
			}
		}
		// make junctions significantly more likely to get labeled
		priority *= station.shots.size();
		// make dead ends a bit more likely to get labeled
		if (station.shots.size() == 1) {
			priority *= 2.5;
		}
		return -priority;
	}

	private static Map<Float, Set<StationKey>> computeStationsToLabel(
			Collection<CalcStation> stations, Collection<Float> spacings) {
		Map<Float, Set<StationKey>> stationsToLabel = new HashMap<>();
		Map<Float, RfStarTree<StationKey>> spatialIndexes = new HashMap<>();

		for (float spacing : spacings) {
			stationsToLabel.put(spacing, new HashSet<>());
			spatialIndexes.put(spacing, new RfStarTree<>(3, 10, 3, 3));
		}

		PriorityQueue<PriorityEntry<Float, CalcStation>> queue = new PriorityQueue<>();
		for (CalcStation station : stations) {
			queue.add(new PriorityEntry<>(getLabelPriority(station), station));
		}

		float[] mbr = new float[6];

		while (!queue.isEmpty()) {
			CalcStation station = queue.poll().getValue();
			double[] position = station.position;
			for (Map.Entry<Float, RfStarTree<StationKey>> entry : spatialIndexes.entrySet()) {
				float spacing = entry.getKey();
				RfStarTree<StationKey> tree = entry.getValue();
				mbr[0] = (float) position[0] - spacing;
				mbr[1] = (float) position[1] - spacing;
				mbr[2] = (float) position[2] - spacing;
				mbr[3] = (float) position[0] + spacing;
				mbr[4] = (float) position[1] + spacing;
				mbr[5] = (float) position[2] + spacing;

				if (!tree.containsLeafIntersecting(mbr)) {
					tree.insert(new Leaf<>(Arrays.copyOf(mbr, 6), station.key()));
					stationsToLabel.get(spacing).add(station.key());
				}
			}
		}

		return stationsToLabel;
	}

	final Map<ShotKey, Shot3d> shot3ds;

	final RfStarTree<Shot3d> tree;

	final Set<Section> sections;

	final Font labelFont;

	TextRenderer textRenderer;

	ColorParam colorParam = ColorParam.DEPTH;

	AxialSectionRenderer axialSectionRenderer = new AxialSectionRenderer();

	Param0SectionRenderer param0SectionRenderer = new Param0SectionRenderer();

	CenterlineRenderer centerlineRenderer = new CenterlineRenderer();

	AtomicReference<MultiMap<SectionRenderer, Section>> renderers = new AtomicReference<>();

	final Set<ShotKey> selectedShots = new HashSet<>();
	final Set<ShotKey> unmodifiableSelectedShots = Collections.unmodifiableSet(selectedShots);

	Shot3d hoveredShot;

	Float hoverLocation;

	LinearAxisConversion glowExtentConversion;

	final Set<Section> sectionsWithGlow = new HashSet<>();

	LinearGradientPaint paramPaint;

	int paramTexture;

	BufferedImageInt paramTextureImage;

	boolean paramTextureNeedsUpdate;

	Uniform4fv highlightColors;

	Uniform4fv centerlineColor;

	Uniform3fv depthAxis;

	Uniform3fv depthOrigin;

	Uniform1fv ambient;

	Uniform1fv nearDist;

	Uniform1fv farDist;

	Uniform1fv loParam;

	Uniform1fv hiParam;

	Uniform4fv glowColor;

	boolean showStationLabels;

	private Survey3dModel(Map<ShotKey, Shot3d> shot3ds, RfStarTree<Shot3d> tree, Set<Section> sections,
			Font labelFont) {
		super();
		this.shot3ds = shot3ds;
		this.tree = tree;
		this.sections = sections;
		this.labelFont = labelFont;

		textRenderer = new TextRenderer(labelFont, true, false);

		highlightColors = new Uniform4fv().name("u_highlightColors");
		highlightColors.value(
				0f, 0f, 0f, 0f,
				0f, 1f, 1f, 0.5f,
				0f, 1f, 1f, 0.5f);
		highlightColors.count(3);

		centerlineColor = new Uniform4fv();
		centerlineColor.value(1f, 1f, 1f, 1f);

		depthAxis = new Uniform3fv().name("u_axis").value(0f, -1f, 0f);
		depthOrigin = new Uniform3fv().name("u_origin").value(0f, 0f, 0f);

		glowColor = new Uniform4fv().name("u_glowColor").value(0f, 1f, 1f, 1f);

		ambient = new Uniform1fv().name("u_ambient").value(0.5f);

		loParam = new Uniform1fv().name("u_loParam").value(0);
		hiParam = new Uniform1fv().name("u_hiParam").value(1000);
		nearDist = new Uniform1fv().name("u_nearDist").value(0);
		farDist = new Uniform1fv().name("u_farDist").value(1000);

		for (Section section : sections) {
			section.renderers.add(axialSectionRenderer);
		}

		MultiMap<SectionRenderer, Section> renderers = LinkedHashSetMultiMap.newInstance();
		renderers.putAll(axialSectionRenderer, sections);

		this.renderers.set(renderers);
	}

	public float[] calcAutofitParamRange(Collection<ShotKey> shots, Subtask subtask) {
		if (subtask != null) {
			subtask.setTotal(sections.size());
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
			return calcAutofitParamRange(shot3ds.keySet(), subtask);
		} else {
			return calcAutofitParamRange(selectedShots, subtask);
		}
	}

	public void calcDistFromShots(Set<ShotKey> shots, Subtask subtask) {
		final Map<StationKey, Double> distancesToStations = new HashMap<>();
		PriorityQueue<PriorityEntry<Double, CalcStation>> queue = new PriorityQueue<>();

		for (ShotKey key : shots) {
			CalcShot shot = shot3ds.get(key).shot;
			queue.add(new PriorityEntry<>(0.0, shot.fromStation));
			queue.add(new PriorityEntry<>(0.0, shot.toStation));
		}
		while (!queue.isEmpty() && !subtask.isCanceling()) {
			PriorityEntry<Double, CalcStation> entry = queue.poll();
			double distanceToStation = entry.getKey();
			CalcStation station = entry.getValue();
			distancesToStations.put(station.key(), distanceToStation);
			for (CalcShot shot : station.shots.values()) {
				CalcStation nextStation = shot.otherStation(station);
				if (distancesToStations.containsKey(nextStation.key())) {
					continue;
				}
				queue.add(new PriorityEntry<>(distanceToStation + shot.distance, nextStation));
			}
		}

		if (subtask != null) {
			subtask.setTotal(sections.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}

		int processed = 0;
		for (Section section : sections) {
			section.calcParam0(Survey3dModel.this, distancesToStations);

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
		for (Section section : sections) {
			section.dispose(gl);
		}

		MultiMap<SectionRenderer, Section> renderers = this.renderers.get();

		for (SectionRenderer renderer : renderers.keySet()) {
			renderer.dispose(gl);
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

		OneParamSectionRenderer renderer = colorParam == ColorParam.DEPTH
				? axialSectionRenderer : param0SectionRenderer;

		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);

		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_FRONT);

		renderer.drawLines = false;
		renderer.draw(sections, context, gl, m, n);
		centerlineRenderer.draw(sections, context, gl, m, n);

		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_EQUAL, 0, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);

		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);
		renderer.drawLines = true;
		renderer.draw(sections, context, gl, m, n);

		gl.glDisable(GL.GL_CULL_FACE);
		gl.glDisable(GL.GL_STENCIL_TEST);

		if (showStationLabels) {
			gl.glEnable(GL.GL_DEPTH_TEST);
			textRenderer.beginRendering(context.width(), context.height(), false);

			RfStarTree<Void> labelTree = new RfStarTree<>(2, 10, 3, 3);
			Set<StationKey> stationsToEmphasize = Collections.emptySet();
			if (hoveredShot != null) {
				stationsToEmphasize = new HashSet<>();
				stationsToEmphasize.add(hoveredShot.shot.fromKey());
				stationsToEmphasize.add(hoveredShot.shot.toKey());
			}

			for (Section section : sections) {
				section.drawLabels(context, gl, m, n, textRenderer, labelTree, stationsToEmphasize);
			}

			textRenderer.endRendering();
			gl.glDisable(GL.GL_DEPTH_TEST);
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

		// float[] testPoint = new float[3];

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
		return hoveredShot == null ? Collections.<Shot3d> emptySet() : Collections.singleton(hoveredShot);
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
		textRenderer.init();
		textRenderer.setUseVertexArrays(true);
	}

	public void pickShots(PlanarHull3f pickHull, Shot3dPickContext spc, List<PickResult<Shot3d>> pickResults) {
		RTraversal.traverse(tree.getRoot(),
				node -> pickHull.intersectsBox(node.mbr()),
				leaf -> {
					leaf.object().pick(pickHull, spc, pickResults);
					return true;
				});
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
			subtask.setTotal(sections.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}
		final Iterator<Section> sectionIterator = sections.iterator();

		MultiMap<SectionRenderer, Section> newRenderers = LinkedHashSetMultiMap.newInstance();

		int completed = 0;
		while (sectionIterator.hasNext()) {
			Section section = sectionIterator.next();

			section.renderers.clear();

			if (colorParam == ColorParam.DEPTH) {
				section.renderers.add(axialSectionRenderer);
				//				section.renderers.add(centerlineRenderer);
			} else {
				if (colorParam.isStationMetric()) {
					section.calcParam0(Survey3dModel.this, colorParam);
				}
				section.renderers.add(param0SectionRenderer);
			}

			for (SectionRenderer renderer : section.renderers) {
				newRenderers.put(renderer, section);
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

		renderers.set(newRenderers);

		if (colorParam.isTraversalMetric()) {
			calcDistFromShots(selectedShots, subtask);
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

		Set<Section> newSectionsWithGlow = new HashSet<>();

		if (hoveredShot != null) {
			CalcShot origShot = hoveredShot.shot;

			// Use Dijkstra's algorithm to go through all stations within glow distance
			// and compute the glow amount at each of those stations
			final Map<StationKey, Float> glowAtStations = new HashMap<>();
			final Set<Shot3d> affectedShot3ds = new HashSet<>();
			final PriorityQueue<PriorityEntry<Double, CalcStation>> unvisited = new PriorityQueue<>();
			unvisited.add(new PriorityEntry<>(hoverLocation * origShot.distance, origShot.fromStation));
			unvisited.add(new PriorityEntry<>(1 - hoverLocation * origShot.distance, origShot.toStation));

			while (!unvisited.isEmpty() && (subtask == null || !subtask.isCanceling())) {
				PriorityEntry<Double, CalcStation> entry = unvisited.poll();
				CalcStation station = entry.getValue();
				double distanceToStation = entry.getKey();

				float glowAtStation = (float) glowExtentConversion.convert(distanceToStation);
				glowAtStations.put(station.key(), glowAtStation);
				if (glowAtStation <= 0) {
					continue;
				}
				for (CalcShot nextShot : station.shots.values()) {
					Shot3d shot3d = shot3ds.get(nextShot.key());
					if (shot3d == null || !affectedShot3ds.add(shot3d)) {
						continue;
					}
					CalcStation nextStation = nextShot.otherStation(station);
					if (glowAtStations.containsKey(nextStation)) {
						continue;
					}
					unvisited.add(new PriorityEntry<>(distanceToStation + nextShot.distance, nextStation));
				}
			}

			for (Shot3d shot3d : affectedShot3ds) {
				sectionsWithGlow.add(shot3d.section);
				newSectionsWithGlow.add(shot3d.section);
			}
			for (Section section : newSectionsWithGlow) {
				section.clearGlow();
				section.stationAttrsNeedRebuffering.set(true);
			}

			/*
			 * The values set on the hoveredShot are a special case.
			 * They will be something like shown below so that the
			 * rendered glow (which is min of A and B) will peak at
			 * the hoverLocation.
			 *
			 *             hoverLocation
			 * From Station     |        To Station
			 *       V          V           V
			 *                            ...
			 * GlowB ****              ...    - 1.1
			 *           ****       ...
			 *               ****...          - 1.0
			 *                ...****
			 *             ...       ****     - 0.9
			 *          ...              ****
			 * GlowA ...                      - 0.8
			 */

			hoveredShot.setGlow(
					(float) glowExtentConversion.convert(hoverLocation * origShot.distance),
					(float) glowExtentConversion.convert((hoverLocation - 1) * origShot.distance),
					(float) glowExtentConversion.convert((1 - hoverLocation) * origShot.distance),
					(float) glowExtentConversion.convert(-hoverLocation * origShot.distance));
			affectedShot3ds.remove(hoveredShot);

			for (Shot3d shot3d : affectedShot3ds) {
				Float glowAtFromStation = glowAtStations.get(shot3d.shot.fromStation.key());
				Float glowAtToStation = glowAtStations.get(shot3d.shot.toStation.key());
				if (glowAtFromStation == null) {
					glowAtFromStation = 0f;
				}
				if (glowAtToStation == null) {
					glowAtFromStation = 0f;
				}
				shot3d.setGlow(glowAtFromStation, glowAtToStation);
			}
		}

		Iterator<Section> segIter = sectionsWithGlow.iterator();

		for (Section section : Iterables.of(segIter)) {
			if (subtask != null && subtask.isCanceling()) {
				return;
			}
			if (!newSectionsWithGlow.contains(section)) {
				segIter.remove();
				section.clearGlow();
				section.stationAttrsNeedRebuffering.set(true);
			}
		}
	}

	private void updateHighlights(Collection<Shot3d> affectedShots) {
		// find the sections that are affected by the affected shots
		// (not just the sections containing those shots but sections containing
		// shots within highlight distance from an affected shot)
		Set<Section> affectedSections = new HashSet<>();
		for (Shot3d shot3d : affectedShots) {
			affectedSections.add(shot3d.section);
		}

		for (Section section : affectedSections) {
			section.clearHighlights();
		}

		for (ShotKey key : selectedShots) {
			Shot3d shot3d = shot3ds.get(key);
			if (affectedSections.contains(shot3d.section)) {
				shot3d.applySelectionHighlights();
			}
		}

		for (Section section : affectedSections) {
			section.stationAttrsNeedRebuffering.set(true);
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

	public void setShowStationLabels(boolean showStationLabels) {
		this.showStationLabels = showStationLabels;
	}
}

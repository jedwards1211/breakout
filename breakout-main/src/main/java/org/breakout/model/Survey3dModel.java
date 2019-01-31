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
import static org.andork.util.JavaScript.or;
import static org.breakout.util.StationNames.getSurveyDesignation;

import java.awt.Color;
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

import org.andork.awt.FontMetricsUtils;
import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.collect.PriorityEntry;
import org.andork.jogl.BufferHelper;
import org.andork.jogl.JoglBuffer;
import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglResource;
import org.andork.jogl.shader.FlatColorProgram;
import org.andork.jogl.shader.FlatColorScreenProgram;
import org.andork.jogl.uniform.Uniform1fv;
import org.andork.jogl.uniform.Uniform3fv;
import org.andork.jogl.uniform.Uniform4fv;
import org.andork.jogl.util.JoglUtils;
import org.andork.jogl.util.PipelinedRenderer;
import org.andork.jogl.util.PipelinedRenderer.Options;
import org.andork.math3d.Clip3f;
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
import org.andork.task.Task;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.util.StringUtils;
import org.breakout.PickResult;
import org.breakout.awt.ParamGradientMapPaint;
import org.breakout.model.calc.CalcCave;
import org.breakout.model.calc.CalcCrossSection;
import org.breakout.model.calc.CalcProject;
import org.breakout.model.calc.CalcShot;
import org.breakout.model.calc.CalcStation;
import org.breakout.model.calc.CalcTrip;
import org.breakout.model.parsed.Lead;
import org.breakout.model.shader.CenterlineProgram;

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
			depthAxis.put(gl, u_axis_location);
			depthOrigin.put(gl, u_origin_location);
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
		public boolean init(GL2ES2 gl) {
			if (program <= 0) {
				super.init(gl);
				u_axis_location = gl.glGetUniformLocation(program, "u_axis");
				u_origin_location = gl.glGetUniformLocation(program, "u_origin");
			}
			return true;
		}
	}

	private class CenterlineRenderer implements SectionRenderer {
		@Override
		public boolean init(GL2ES2 gl) {
			CenterlineProgram.INSTANCE.init(gl);
			return true;
		}

		@Override
		public void dispose(GL2ES2 gl) {
			CenterlineProgram.INSTANCE.dispose(gl);
		}

		@Override
		public void draw(Collection<Section> sections, JoglDrawContext context, GL2ES2 gl, float[] m,
				float[] n) {
			CenterlineProgram program = CenterlineProgram.INSTANCE;
			program.use(gl);

			program.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), m);

			centerlineColor.put(gl, program.color);
			maxCenterlineDistance.put(gl, program.maxCenterlineDistance);

			program.position.enableArray(gl);
			program.clipLocations.put(gl, clip);

			gl.glEnable(GL_DEPTH_TEST);
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);

			for (Section section : sections) {
				section.centerlineGeometry.init(gl);

				gl.glBindBuffer(GL_ARRAY_BUFFER, section.centerlineGeometry.id());
				gl.glVertexAttribPointer(program.position.location(), 3, GL_FLOAT, false, 12, 0);

				gl.glDrawArrays(GL_LINES, 0, section.shot3ds.size() * 2);
			}

			gl.glDisable(GL_DEPTH_TEST);
			gl.glDisable(GL.GL_STENCIL_TEST);
			program.position.disableArray(gl);

			gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
			program.use(gl, false);
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
		
		protected int u_clipAxis_location;
		protected int u_clipNear_location;
		protected int u_clipFar_location;

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
			
			ambient.put(gl, u_ambient_location);

			nearDist.put(gl, u_nearDist_location);
			farDist.put(gl, u_farDist_location);

			glowColor.put(gl, u_glowColor_location);

			highlightColors.put(gl, u_highlightColors_location);

			gl.glEnableVertexAttribArray(a_pos_location);
			gl.glEnableVertexAttribArray(a_norm_location);
			gl.glEnableVertexAttribArray(a_glow_location);
			gl.glEnableVertexAttribArray(a_highlightIndex_location);
			
			gl.glUniform3fv(u_clipAxis_location, 1, clip.axis(), 0);
			gl.glUniform1f(u_clipNear_location, clip.near());
			gl.glUniform1f(u_clipFar_location, clip.far());

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
					"  temp = dot(v_Position, u_clipAxis);"
					+
					"  if (temp < u_clipNear || temp > u_clipFar) discard;"
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

					"out vec4 color;" +
					"in vec3 v_Position;" +
					"uniform vec3 u_clipAxis;" +
					"uniform float u_clipNear;" +
					"uniform float u_clipFar;";

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
					" v_Position = a_pos;" +
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
					"out float v_highlightIndex;" +
					"out vec3 v_Position;";
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
		public boolean init(GL2ES2 gl) {
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

				u_clipAxis_location = gl.glGetUniformLocation(program, "u_clipAxis");
				u_clipNear_location = gl.glGetUniformLocation(program, "u_clipNear");
				u_clipFar_location = gl.glGetUniformLocation(program, "u_clipFar");
			}

			return true;
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

			loParam.put(gl, u_loParam_location);
			hiParam.put(gl, u_hiParam_location);
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
		public boolean init(GL2ES2 gl) {
			if (program <= 0) {
				super.init(gl);

				u_loParam_location = gl.glGetUniformLocation(program, "u_loParam");
				u_hiParam_location = gl.glGetUniformLocation(program, "u_hiParam");
				u_paramSampler_location = gl.glGetUniformLocation(program, "u_paramSampler");
			}

			return true;
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
		public boolean init(GL2ES2 gl) {
			if (program <= 0) {
				super.init(gl);
				a_param0_location = gl.glGetAttribLocation(program, "a_param0");
			}

			return true;
		}
	}

	private static class Label {
		final float[] position;
		float width;
		float height;
		float lineHeight;
		float xOffset;
		float yOffset;
		float pushForward;
		float maxSpacing;
		final String[] text;
		final CalcStation station;

		Label(CalcStation station) {
			this(station, station.name);
		}

		Label(CalcStation station, String text) {
			this.station = station;
				position = new float[] {
					(float) station.position[0],
					(float) station.position[1],
					(float) station.position[2] };
			this.text = text.split("\r\n?|\n");
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

		void updateBounds(Font font, FontRenderContext frc) {
			Rectangle2D bounds = FontMetricsUtils.getMultilineStringBounds(text, font, frc);
			width = (float) bounds.getWidth();
			lineHeight = (float) font.getStringBounds(text[0], frc).getHeight();
			height = (float) bounds.getHeight();
			xOffset = (float) -bounds.getWidth() / 2;
			yOffset = (float) bounds.getHeight() / 2;
		}

		/**
		 * @param context
		 * @param labelContext TODO
		 * @param scale
		 * @param result the origin will be stored in this
		 * @return true if the label is on screen, false otherwise
		 */
		boolean getOrigin(JoglDrawContext context, LabelDrawingContext labelContext, float scale, float[] result) {
			if (!labelContext.clip.contains(position)) {
				return false;
			}
			Vecmath.mpmulAffine(context.viewMatrix(), position, result);
			if (result[2] > 0) {
				// label is behind camera
				return false;
			}
			float labelDistanceSquared = Vecmath.dot3(result, result);
		
			float z = Float.NaN;
			if (labelDistanceSquared < pushForward * pushForward) {
				z = 0.99999f;
			} else {
				float labelDistance = (float) Math.sqrt(labelDistanceSquared);
				float pushedForwardDistance = labelDistance - pushForward;
				float pushForwardFactor = pushedForwardDistance / labelDistance;
				result[0] *= pushForwardFactor;
				result[1] *= pushForwardFactor;
				result[2] *= pushForwardFactor;
			}
		
			Vecmath.mpmul(context.viewToScreen(), result);
			if (result[2] < -1) {
				// label is outside of clipping bounds
				return false;
			}
			result[0] += xOffset * scale;
			result[1] += yOffset * scale;
			if (!Float.isNaN(z)) {
				result[2] = z;
			}
			return true;
		}

		float[] draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n, LabelDrawingContext labelContext, boolean force, float scale) {
			if (!getOrigin(context, labelContext, scale, labelContext.tempPoint)) return null;
			float x = labelContext.tempPoint[0];
			float y = labelContext.tempPoint[1];
			float z = labelContext.tempPoint[2];
		
			float[] labelMbr = { x, y, x + width * scale, y + height * scale };
			if (!force && labelContext.labelTree.containsLeafIntersecting(labelMbr)) {
				return null;
			} else {
				Leaf<Void> leaf = new Leaf<>(labelMbr, null);
				labelContext.labelTree.insert(leaf);
			}
			
			for (String line : text) {
				labelContext.textRenderer.draw3D(line, x, y, z, scale);
				y -= height * scale;
			}
			
			return labelMbr;
		}

	}
	
	private static class LeadLabels {
		Label icon;
		Label descriptionFeet;
		Label descriptionMeters;
		
		LeadLabels(CalcStation station) {
			icon = new Label(station, "?");
			descriptionFeet = new Label(station, leadText(station.name, station.leads, Length.feet));
			descriptionMeters = new Label(station, leadText(station.name, station.leads, Length.meters));
		}
		
		static String leadText(String stationName, List<Lead> leads, Unit<Length> displayLengthUnit) {
			if (leads == null) return "";
			StringBuilder builder = new StringBuilder();
			builder.append(leads.size() == 1
					? "Lead at "
					: leads.size() + " leads at ")
				.append(stationName)
				.append(":");
			int i = 1;
			for (Lead lead : leads) {
				builder.append('\n');
				StringBuilder description = new StringBuilder();
				String size = lead.describeSize(displayLengthUnit);
				if (size != null) description.append('[').append(size).append("] ");
				description.append(lead.description);
				String wrapped = StringUtils.wrap(description.toString(), 55);
				if (leads.size() > 1) {
					builder.append("[").append(i++).append("] ")
						.append(wrapped.replaceAll("(\r\n?|\n)", "$1     "));
				} else {
					builder.append(wrapped);
				}
			}
			return builder.toString();
		}

		void updateBounds(Font font, FontRenderContext frc) {
			icon.updateBounds(font, frc);
			icon.yOffset = -icon.lineHeight / 3;
			descriptionFeet.updateBounds(font, frc);
			descriptionFeet.xOffset = 0;
			descriptionFeet.yOffset = 0;
			descriptionMeters.updateBounds(font, frc);
			descriptionMeters.xOffset = 0;
			descriptionMeters.yOffset = 0;
		}

		void drawIcon(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n, LabelDrawingContext labelContext) {
			float[] labelMbr = icon.draw(context,gl, m, n, labelContext, true, labelContext.textScale);
		
			if (labelMbr != null) {
				Leaf<StationKey> leadLeaf = new Leaf<>(labelMbr, icon.station.key( ));
				labelContext.leadLabelTree.insert( leadLeaf );
			}
		}
		
		void drawDetail(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n,
				LabelDrawingContext labelContext) {
			Label label = Length.imperialUnits.contains(labelContext.displayLengthUnit)
				? descriptionFeet
				: descriptionMeters;

			float scale = labelContext.textScale;

			if (!label.getOrigin(context, labelContext, scale, labelContext.tempPoint)) return;
			float x = labelContext.tempPoint[0] + icon.width * scale * 2;
			float y = labelContext.tempPoint[1];
			
			float padding = 5 * context.devicePixelRatio();

			float x0 = x - padding;
			float y0 = y + label.lineHeight * scale;
			float x1 = x + label.width * scale + padding;
			float y1 = y - (label.height - label.lineHeight) * scale - padding;

			FlatColorScreenProgram program = FlatColorScreenProgram.INSTANCE;
			program.use(gl);
			program.screenXform.put(gl, context.inverseViewportMatrix());

			Uniform4fv leadDetailFillColor = labelContext.leadDetailFillColor;
			leadDetailFillColor.put(gl, program.color);
			PipelinedRenderer triangleRenderer = labelContext.triangleRenderer;
			triangleRenderer.setVertexAttribLocations(program.position);
			triangleRenderer.put(x0, y0, 0);
			triangleRenderer.put(x1, y0, 0);
			triangleRenderer.put(x1, y1, 0);
			triangleRenderer.put(x0, y0, 0);
			triangleRenderer.put(x0, y1, 0);
			triangleRenderer.put(x1, y1, 0);
			triangleRenderer.draw();
			
			Uniform4fv leadDetailOutlineColor = labelContext.leadDetailOutlineColor;
			leadDetailOutlineColor.put(gl, program.color);
			PipelinedRenderer lineRenderer = labelContext.lineRenderer;
			lineRenderer.setVertexAttribLocations(program.position);
			lineRenderer.put(x0, y0, 0);
			lineRenderer.put(x1, y0, 0);
			lineRenderer.put(x1, y0, 0);
			lineRenderer.put(x1, y1, 0);
			lineRenderer.put(x1, y1, 0);
			lineRenderer.put(x0, y1, 0);
			lineRenderer.put(x0, y1, 0);
			lineRenderer.put(x0, y0, 0);
			lineRenderer.draw();

			program.done(gl);

			labelContext.textRenderer.beginRendering(context.width(), context.height(), false);

			String[] lines = label.text;
			for (String line : lines) {
				labelContext.textRenderer.draw3D(line, x, y, 0, scale);
				y -= label.lineHeight * scale;
			}

			labelContext.textRenderer.endRendering();
		}
	}

	private static class LabelDrawingContext {
		float[] tempPoint = new float[3];
		TextRenderer textRenderer;
		RfStarTree<Void> labelTree;
		RfStarTree<StationKey> leadLabelTree;
		Set<StationKey> stationsToEmphasize;
		float textScale;
		float density;
		Unit<Length> displayLengthUnit;
		StationKey hoveredStation;
		PipelinedRenderer lineRenderer;
		PipelinedRenderer triangleRenderer;
		Uniform4fv leadDetailOutlineColor;
		Uniform4fv leadDetailFillColor;
		Clip3f clip;
	}

	private static class Section {
		final float[] mbr;
		final ArrayList<Shot3d> shot3ds;
		final Map<StationKey, Label> stationLabels;
		final Map<StationKey, LeadLabels> leadLabels;
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
			leadLabels = new HashMap<>();
			for (Shot3d shot3d : shot3ds) {
				CalcStation fromStation = shot3d.shot.fromStation;
				CalcStation toStation = shot3d.shot.toStation;
				if (!stationLabels.containsKey(fromStation.key())) {
					stationLabels.put(fromStation.key(), new Label(fromStation));
				}
				if (!stationLabels.containsKey(toStation.key())) {
					stationLabels.put(toStation.key(), new Label(toStation));
				}
				if (fromStation.leads != null && !fromStation.leads.isEmpty() && !leadLabels.containsKey(fromStation.key())) {
					leadLabels.put(fromStation.key(), new LeadLabels(fromStation));
				}
				if (toStation.leads != null && !toStation.leads.isEmpty() && !leadLabels.containsKey(toStation.key())) {
					leadLabels.put(toStation.key(), new LeadLabels(toStation));
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

		void dispose(GL2ES2 gl) {
			geometry.dispose(gl);
			stationAttrs.dispose(gl);
			if (param0 != null) {
				param0.dispose(gl);
			}
			fillIndices.dispose(gl);
			lineIndices.dispose(gl);
		}

		float minDistanceForSpacing(JoglDrawContext context, float spacing, float density) {
			// TODO: something view-dependent and less arbitrary
			return spacing * density;
		}

		void updateLabels(Font font, FontRenderContext frc, Task<?> task) {
			task.setTotal(stationLabels.size() + leadLabels.size() * 3);
			for (Label label : stationLabels.values()) {
				label.updateBounds(font, frc);
				task.increment();
			}
			for (LeadLabels labels : leadLabels.values()) {
				labels.updateBounds(font, frc);
				task.increment(3);
			}
		}
		
		void drawLeadLabels(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n,
				LabelDrawingContext labelContext) {
			for (LeadLabels labels : leadLabels.values()) {
				labels.drawIcon(context, gl, m, n, labelContext);
			}
		}

		void drawEmphasizedLabels(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n,
				LabelDrawingContext labelContext) {
			for (StationKey key : labelContext.stationsToEmphasize) {
				Label label = stationLabels.get(key);
				if (label != null) {
					label.draw(context, gl, m, n, labelContext, false, labelContext.textScale * 2f);
				}
			}
		}
		
		void drawLabels(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n,
				LabelDrawingContext labelContext) {
			context.getViewPoint(tempPoint);
			float minDistance = Rectmath.minDistance3(mbr, tempPoint);

			if (labelContext.density > 0) {
				Set<StationKey> stationsToLabel = stationLabels.keySet();
				for (int i = stationsToLabelSpacings.size() - 1; i >= 0; i--) {
					float spacing = stationsToLabelSpacings.get(i);
					if (minDistance > minDistanceForSpacing(context, spacing, labelContext.density)) {
						stationsToLabel = this.stationsToLabel.get(spacing);
						break;
					}
				}

				for (StationKey key : stationsToLabel) {
					Label label = stationLabels.get(key);
					label.draw(context, gl, m, n, labelContext, false, labelContext.textScale);
				}
			}
		}

		void drawHoveredLead(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n,
				LabelDrawingContext labelContext) {
			LeadLabels labels = leadLabels.get(labelContext.hoveredStation);
			if (labels != null) labels.drawDetail(context, gl, m, n, labelContext);
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
				Shot3d shot3d = shot3ds.get(shotKey);
				if (shot3d != null) {
					affectedShots.put(shotKey, shot3d);
				}
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
				if (param.isStationMetric()) {
					float value;
					value = param.calcStationParam(shot, shot.fromStation);
					if (Float.isFinite(value)) {
						rangeInOut[0] = Math.min(rangeInOut[0], value);
						rangeInOut[1] = Math.max(rangeInOut[1], value);
					}
					value = param.calcStationParam(shot, shot.toStation);
					if (Float.isFinite(value)) {
						rangeInOut[0] = Math.min(rangeInOut[0], value);
						rangeInOut[1] = Math.max(rangeInOut[1], value);
					}
					return;
				}

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
			int baseIndex = (indexInVertices + i) * GEOM_BPV;
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
			int numToReinsert, Task<?> task) throws Exception {
		task.setStatus("creating 3D model");
		task.setTotal(6);

		Map<ShotKey, Shot3d> shot3ds = new HashMap<>();
		task.runSubtask(1, subtask -> {
			subtask.setTotal(project.shots.size());
			for (Map.Entry<ShotKey, CalcShot> entry : project.shots.entrySet()) {
				shot3ds.put(entry.getKey(), new Shot3d(entry.getKey(), entry.getValue()));
				subtask.increment();
			}
		});

		RfStarTree<Shot3d> tree = task.callSubtask(1,
				subtask -> createTree(shot3ds.values(), maxChildrenPerBranch, minSplitSize, numToReinsert, subtask));

		int sectionLevel = Math.min(tree.getRoot().level(), 3);

		Map<Float, Set<StationKey>> stationsToLabel = task.callSubtask(1, subtask -> computeStationsToLabel(
				project.stations.values(), Arrays.asList(5f, 10f, 20f, 40f, 80f, 160f, 320f),
				subtask));
		stationsToLabel.put(2f, project.stations.keySet());

		Set<Section> sections = task.callSubtask(1,
				subtask -> createSections(tree, sectionLevel, stationsToLabel, subtask));

		Font labelFont = new Font("Arial", Font.BOLD, 72);
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		task.runSubtask(1, subtask -> {
			subtask.setTotal(sections.size());
			for (Section section : sections) {
				subtask.runSubtask(1, labelSubtask -> section.updateLabels(labelFont, frc, labelSubtask));
			}
		});

		Survey3dModel model = new Survey3dModel(project, shot3ds, tree, sections, labelFont);
		task.runSubtask(1, subtask -> model.calcColorParam(subtask));

		return task.isCanceled() ? null : model;
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
			Map<Float, Set<StationKey>> stationsToLabel, Task<?> task) {
		task.setStatus("creating render sections");
		task.setIndeterminate(true);
		Set<Section> result = new HashSet<>();

		createSections(tree.getRoot(), sectionLevel, stationsToLabel, result);

		return result;
	}

	private static RfStarTree<Shot3d> createTree(Collection<Shot3d> shot3ds,
			int maxChildrenPerBranch, int minSplitSize, int numToReinsert, Task<?> task) {
		RfStarTree<Shot3d> tree = new RfStarTree<>(3, maxChildrenPerBranch, minSplitSize, numToReinsert);

		task.setStatus("creating spatial index");
		task.setTotal(shot3ds.size());

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

			task.increment();
		}

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
			Collection<CalcStation> stations, Collection<Float> spacings, Task<?> subtask) {
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

		subtask.setTotal(queue.size());

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
			subtask.increment();
		}

		return stationsToLabel;
	}

	final CalcProject project;

	final Map<ShotKey, Shot3d> shot3ds;

	final RfStarTree<Shot3d> tree;

	RfStarTree<StationKey> leadLabelTree = null;
	
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
	
	StationKey hoveredStation;

	LinearAxisConversion glowExtentConversion;

	Set<Section> sectionsWithGlow = new HashSet<>();

	LinearGradientPaint paramPaint;

	int paramTexture;

	BufferedImageInt paramTextureImage;

	boolean paramTextureNeedsUpdate;

	Uniform4fv highlightColors;

	Uniform4fv centerlineColor;

	Uniform1fv maxCenterlineDistance;

	Uniform3fv depthAxis;

	Uniform3fv depthOrigin;

	Uniform1fv ambient;

	Uniform1fv nearDist;

	Uniform1fv farDist;

	Uniform1fv loParam;

	Uniform1fv hiParam;

	Uniform4fv glowColor;
	
	Uniform4fv backgroundColor;

	float stationLabelDensity;
	
	float stationLabelFontSize;
	
	boolean showLeadLabels;

	Color stationLabelColor;

	PipelinedRenderer lineRenderer;
	PipelinedRenderer triangleRenderer;

	boolean showSpatialIndex = false;
	
	Unit<Length> displayLengthUnit = Length.meters;
	
	Clip3f clip = new Clip3f(new float[] {0,  -1,  0}, -Float.MAX_VALUE, Float.MAX_VALUE);

	private Survey3dModel(
			CalcProject project,
			Map<ShotKey, Shot3d> shot3ds, RfStarTree<Shot3d> tree, Set<Section> sections,
			Font labelFont) {
		super();
		this.project = project;
		this.shot3ds = shot3ds;
		this.tree = tree;
		this.sections = sections;
		this.labelFont = labelFont;

		textRenderer = new TextRenderer(labelFont, true, true, null, false);

		highlightColors = new Uniform4fv()
				.value(
						0f, 0f, 0f, 0f,
						0f, 1f, 1f, 0.5f,
						0f, 1f, 1f, 0.5f)
				.count(3);

		stationLabelFontSize = 12;
		stationLabelDensity = 40;

		centerlineColor = new Uniform4fv();
		centerlineColor.value(1f, 1f, 1f, 1f);

		maxCenterlineDistance = new Uniform1fv();
		maxCenterlineDistance.value(1000f);

		depthAxis = new Uniform3fv().value(0f, -1f, 0f);
		depthOrigin = new Uniform3fv().value(0f, 0f, 0f);

		glowColor = new Uniform4fv().value(0f, 1f, 1f, 1f);
		backgroundColor = new Uniform4fv().value(0f, 0f, 0f, 1f);

		ambient = new Uniform1fv().value(0.5f);

		loParam = new Uniform1fv().value(0);
		hiParam = new Uniform1fv().value(1000);
		nearDist = new Uniform1fv().value(0);
		farDist = new Uniform1fv().value(1000);

		for (Section section : sections) {
			section.renderers.add(axialSectionRenderer);
		}

		MultiMap<SectionRenderer, Section> renderers = LinkedHashSetMultiMap.newInstance();
		renderers.putAll(axialSectionRenderer, sections);

		this.renderers.set(renderers);

		lineRenderer = new PipelinedRenderer(new Options(true, GL.GL_LINES, 100).addAttribute(3, GL.GL_FLOAT, false));
		triangleRenderer = new PipelinedRenderer(new Options(true, GL.GL_TRIANGLES, 100).addAttribute(3, GL.GL_FLOAT, false));
	}

	public float[] calcAutofitParamRange(Collection<ShotKey> shots, Task<?> subtask) {
		if (subtask != null) {
			subtask.setTotal(sections.size());
			subtask.setCompleted(0);
			subtask.setIndeterminate(false);
		}

		final float[] range = { Float.MAX_VALUE, -Float.MAX_VALUE };

		for (ShotKey key : shots) {
			Shot3d shot = shot3ds.get(key);
			if (shot == null) {
				continue;
			}
			shot.calcParamRange(Survey3dModel.this, colorParam, range);
			subtask.increment();
		}

		return range;
	}

	public float[] calcAutofitParamRange(Task<?> subtask) {
		if (selectedShots.size() < 2 || colorParam == ColorParam.DISTANCE_ALONG_SHOTS) {
			return calcAutofitParamRange(shot3ds.keySet(), subtask);
		} else {
			return calcAutofitParamRange(selectedShots, subtask);
		}
	}

	public void calcDistFromShots(Set<ShotKey> shots, Task<?> task) throws Exception {
		final Map<StationKey, Double> distancesToStations = new HashMap<>();
		PriorityQueue<PriorityEntry<Double, CalcStation>> queue = new PriorityQueue<>();

		task.setTotal(3);

		task.runSubtask(1, enqueueTask -> {
			enqueueTask.setTotal(shots.size());
			for (ShotKey key : shots) {
				CalcShot shot = shot3ds.get(key).shot;
				queue.add(new PriorityEntry<>(0.0, shot.fromStation));
				queue.add(new PriorityEntry<>(0.0, shot.toStation));
				enqueueTask.increment();
			}
		});
		task.runSubtask(1, dequeueTask -> {
			dequeueTask.setTotal(queue.size());
			while (!queue.isEmpty()) {
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
				dequeueTask.increment();
			}
		});
		task.runSubtask(1, sectionTask -> {
			sectionTask.setTotal(sections.size());
			for (Section section : sections) {
				section.calcParam0(Survey3dModel.this, distancesToStations);
				sectionTask.increment();
			}
		});
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

		lineRenderer.dispose(gl);
		triangleRenderer.dispose(gl);
		centerlineRenderer.dispose(gl);
		FlatColorProgram.INSTANCE.dispose(gl);
		FlatColorScreenProgram.INSTANCE.dispose(gl);
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

		if (stationLabelDensity > 0 || showLeadLabels) {
			RfStarTree<Void> labelTree = new RfStarTree<>(2, 10, 3, 3);
			leadLabelTree = new RfStarTree<>(2, 10, 3, 3);
			Set<StationKey> stationsToEmphasize = Collections.emptySet();
			if (hoveredShot != null) {
				stationsToEmphasize = new HashSet<>();
				stationsToEmphasize.add(hoveredShot.shot.fromKey());
				stationsToEmphasize.add(hoveredShot.shot.toKey());
			}

			LabelDrawingContext labelContext = new LabelDrawingContext();
			labelContext.textRenderer = textRenderer;
			labelContext.stationsToEmphasize = stationsToEmphasize;
			labelContext.labelTree = labelTree;
			labelContext.leadLabelTree = leadLabelTree;
			labelContext.density = stationLabelDensity;
			labelContext.textScale = stationLabelFontSize / labelFont.getSize() * context.devicePixelRatio( );
			labelContext.displayLengthUnit = displayLengthUnit;
			labelContext.hoveredStation = hoveredStation;
			labelContext.lineRenderer = lineRenderer;
			labelContext.triangleRenderer = triangleRenderer;
			labelContext.leadDetailOutlineColor = new Uniform4fv().value(
				stationLabelColor.getRed() / 255f,
				stationLabelColor.getGreen() / 255f,
				stationLabelColor.getBlue() / 255f,
				stationLabelColor.getAlpha() / 255f
			);
			labelContext.leadDetailFillColor = new Uniform4fv().value(
				backgroundColor.value()[0],
				backgroundColor.value()[1],
				backgroundColor.value()[2],
				.7f
			);
			labelContext.clip = clip;

			if (!stationsToEmphasize.isEmpty()) {
				textRenderer.beginRendering(context.width(), context.height(), false);
				for (Section section : sections) {
					section.drawEmphasizedLabels(context, gl, m, n, labelContext);
				}
				textRenderer.endRendering();
			}
			
			if (showLeadLabels) {
				gl.glEnable(GL.GL_DEPTH_TEST);
				textRenderer.beginRendering(context.width(), context.height(), false);

				for (Section section : sections) {
					section.drawLeadLabels(context, gl, m, n, labelContext);
				}
				textRenderer.endRendering();
				gl.glDisable(GL.GL_DEPTH_TEST);
			}

			if (stationLabelDensity > 0) {
				gl.glEnable(GL.GL_DEPTH_TEST);
				textRenderer.beginRendering(context.width(), context.height(), false);

				for (Section section : sections) {
					section.drawLabels(context, gl, m, n, labelContext);
				}
				textRenderer.endRendering();
				gl.glDisable(GL.GL_DEPTH_TEST);
			}

			if (showLeadLabels && hoveredStation != null) {
				gl.glEnable(GL.GL_BLEND);
				for (Section section : sections) {
					section.drawHoveredLead(context, gl, m, n, labelContext);
				}
				gl.glDisable(GL.GL_BLEND);
			}
		}

		if (showSpatialIndex && hoveredShot != null) {
			gl.glEnable(GL.GL_DEPTH_TEST);
			drawMBRsForNode(hoveredShot.leaf, context, gl, m, n);
			gl.glDisable(GL.GL_DEPTH_TEST);
		}
	}

	/**
	 * Draws a wireframe bounding box to {@link #lineRenderer}.
	 * 
	 * @param bbox
	 *            a bounding box of the form [xmin, ymin, zmin, xmax, ymax,
	 *            zmax]
	 */
	void drawBoundingBox(float[] bbox) {
		for (int x = 0; x < 6; x += 3) {
			lineRenderer.put(bbox[x], bbox[1], bbox[2]);
			lineRenderer.put(bbox[x], bbox[4], bbox[2]);
			lineRenderer.put(bbox[x], bbox[1], bbox[5]);
			lineRenderer.put(bbox[x], bbox[4], bbox[5]);
		}
		for (int y = 1; y < 6; y += 3) {
			lineRenderer.put(bbox[0], bbox[y], bbox[2]);
			lineRenderer.put(bbox[0], bbox[y], bbox[5]);
			lineRenderer.put(bbox[3], bbox[y], bbox[2]);
			lineRenderer.put(bbox[3], bbox[y], bbox[5]);
		}
		for (int z = 2; z < 6; z += 3) {
			lineRenderer.put(bbox[0], bbox[1], bbox[z]);
			lineRenderer.put(bbox[3], bbox[1], bbox[z]);
			lineRenderer.put(bbox[0], bbox[4], bbox[z]);
			lineRenderer.put(bbox[3], bbox[4], bbox[z]);
		}
	}

	void drawMBR(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		FlatColorProgram flatColorProgram = FlatColorProgram.INSTANCE;
		flatColorProgram.use(gl);
		flatColorProgram.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), m);
		centerlineColor.put(gl, flatColorProgram.color);

		lineRenderer.setVertexAttribLocations(flatColorProgram.position);

		drawBoundingBox(tree.getRoot().mbr());

		lineRenderer.draw();

		flatColorProgram.done(gl);
	}

	void drawMBRsForNode(RfStarTree.Node<?> node, JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		FlatColorProgram flatColorProgram = FlatColorProgram.INSTANCE;
		flatColorProgram.use(gl, true);
		flatColorProgram.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), m);
		centerlineColor.put(gl, flatColorProgram.color);

		lineRenderer.setVertexAttribLocations(flatColorProgram.position);

		while (node != null) {
			drawBoundingBox(node.mbr());
			node = node.parent();
		}

		lineRenderer.draw();

		flatColorProgram.use(gl, false);
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

	public Set<Shot3d> getHoveredShots() {
		return hoveredShot == null ? Collections.<Shot3d> emptySet() : Collections.singleton(hoveredShot);
	}

	public float[] getOrthoBounds(Set<ShotKey> shotsInView, float[] orthoRight, float[] orthoUp,
			float[] orthoForward) {
		float[] result = {
			Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY,
			Float.NEGATIVE_INFINITY,
			Float.NEGATIVE_INFINITY,
			Float.NEGATIVE_INFINITY
		};

		for (ShotKey key : shotsInView) {
			Shot3d shot = shot3ds.get(key);
			if (shot == null) continue;
			for (float[] coord : shot.coordIterable()) {
				float right = Vecmath.dot3(coord, orthoRight);
				float up = Vecmath.dot3(coord, orthoUp);
				float forward = Vecmath.dot3(coord, orthoForward);
				if (right < result[0]) result[0] = right;
				if (right > result[3]) result[3] = right;
				if (up < result[1]) result[1] = up;
				if (up > result[4]) result[4] = up;
				if (forward < result[2]) result[2] = forward;
				if (forward > result[5]) result[5] = forward;
			}
		}

		for (int i = 0; i < result.length; i++) {
			if (Float.isInfinite(result[i])) result[i] = Float.NaN;
		}
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
	public boolean init(GL2ES2 gl) {
		textRenderer.init();
		textRenderer.setUseVertexArrays(true);
		textRenderer.setColor(stationLabelColor);

		lineRenderer.init(gl);
		triangleRenderer.init(gl);
		FlatColorProgram.INSTANCE.init(gl);
		FlatColorScreenProgram.INSTANCE.init(gl);
		
		centerlineRenderer.init(gl);

		return true;
	}
	
	public void pickLeadStations(float x, float y, List<PickResult<StationKey>> pickResults) {
		if (leadLabelTree == null) {
			return;
		}
		RTraversal.traverse(leadLabelTree.getRoot(),
			node -> Rectmath.contains2(node.mbr( ), x, y),
			leaf -> {
				float[] mbr = leaf.mbr();
				if (!Rectmath.contains2(mbr, x, y)) {
					return true;
				}
				PickResult<StationKey> pickResult = new PickResult<>();
				pickResult.location[0] = (mbr[0] + mbr[2]) / 2;
				pickResult.location[1] = (mbr[1] + mbr[3]) / 2;
				pickResult.location[2] = 0;
				pickResult.lateralDistance = Vecmath.distance3(
					pickResult.location,
					x, y, pickResult.location[2]);
				pickResult.distance = pickResult.lateralDistance;
				pickResult.picked = leaf.object();
				pickResults.add(pickResult);
				return true;
			});
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

	public void setColorParam(final ColorParam colorParam, Task<?> subtask) throws Exception {
		if (this.colorParam == colorParam) {
			return;
		}
		this.colorParam = colorParam;

		calcColorParam(subtask);
	}

	void calcColorParam(Task<?> subtask) throws Exception {
		subtask.setTotal(sections.size());
		final Iterator<Section> sectionIterator = sections.iterator();

		MultiMap<SectionRenderer, Section> newRenderers = LinkedHashSetMultiMap.newInstance();

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

			subtask.increment();
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

	public static interface UpdateGlowOptions {
		boolean highlightSameTrip();

		boolean highlightSameSurveyDesignation();

		LinearAxisConversion glowExtentConversion();

		Task<?> task();
	}
	
	public void setHoveredStation(StationKey hoveredStation) {
		this.hoveredStation = hoveredStation;
	}

	public void updateGlow(Shot3d hoveredShot, Float hoverLocation, UpdateGlowOptions options) {
		this.hoveredShot = hoveredShot;
		this.hoverLocation = hoverLocation;
		this.glowExtentConversion = options.glowExtentConversion();

		final Task<?> task = options.task();

		task.setStatus("Updating mouseover glow");
		task.setIndeterminate(true);

		final Set<Shot3d> affectedShot3ds = new HashSet<>();
		final Map<StationKey, Float> glowAtStations = new HashMap<>();

		if (hoveredShot != null) {
			CalcShot origShot = hoveredShot.shot;

			if (options.highlightSameTrip() || options.highlightSameSurveyDesignation()) {
				if (options.highlightSameTrip()) {
					CalcTrip trip = origShot.trip;
					addGlowForTrip(trip, glowAtStations, affectedShot3ds, task);
				}
				if (options.highlightSameSurveyDesignation()) {
					addGlowForSameSurveyDesignation(origShot, glowAtStations, affectedShot3ds, task);
				}
			} else {
				addGlowForNearbyStations(origShot, hoverLocation, glowAtStations, affectedShot3ds, task);
			}
		}

		if (task.isCanceled()) {
			return;
		}

		Set<Section> newSectionsWithGlow = new HashSet<>();

		for (Shot3d shot3d : affectedShot3ds) {
			newSectionsWithGlow.add(shot3d.section);
		}

		if (task.isCanceled()) {
			return;
		}

		for (Section section : sectionsWithGlow) {
			section.clearGlow();
			section.stationAttrsNeedRebuffering.set(true);
		}
		for (Section section : newSectionsWithGlow) {
			section.clearGlow();
			section.stationAttrsNeedRebuffering.set(true);
		}

		sectionsWithGlow = newSectionsWithGlow;

		if (task.isCanceled()) {
			return;
		}
	
		if (hoveredShot != null && 
				!options.highlightSameTrip() && !options.highlightSameSurveyDesignation()) {
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

			CalcShot origShot = hoveredShot.shot;

			hoveredShot.setGlow(
					(float) glowExtentConversion.convert(hoverLocation * origShot.distance),
					(float) glowExtentConversion.convert((hoverLocation - 1) * origShot.distance),
					(float) glowExtentConversion.convert((1 - hoverLocation) * origShot.distance),
					(float) glowExtentConversion.convert(-hoverLocation * origShot.distance));
			affectedShot3ds.remove(hoveredShot);
		}

		for (Shot3d shot3d : affectedShot3ds) {
			if (task.isCanceled()) {
				return;
			}
			Float glowAtFromStation = glowAtStations.get(shot3d.shot.fromStation.key());
			Float glowAtToStation = glowAtStations.get(shot3d.shot.toStation.key());
			if (glowAtFromStation == null) {
				glowAtFromStation = 0f;
			}
			if (glowAtToStation == null) {
				glowAtToStation = 0f;
			}
			shot3d.setGlow(glowAtFromStation, glowAtToStation);
		}
	}

	private void addGlowForNearbyStations(CalcShot origShot, Float hoverLocation,
			final Map<StationKey, Float> glowAtStations, final Set<Shot3d> affectedShot3ds, final Task<?> task) {
		// Use Dijkstra's algorithm to go through all stations within glow distance
		// and compute the glow amount at each of those stations
		final PriorityQueue<PriorityEntry<Double, CalcStation>> unvisited = new PriorityQueue<>();
		unvisited.add(new PriorityEntry<>(hoverLocation * origShot.distance, origShot.fromStation));
		unvisited.add(new PriorityEntry<>(1 - hoverLocation * origShot.distance, origShot.toStation));

		while (!unvisited.isEmpty() && !task.isCanceled()) {
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
				if (glowAtStations.containsKey(nextStation.key())) {
					continue;
				}
				unvisited.add(new PriorityEntry<>(distanceToStation + nextShot.distance, nextStation));
			}
		}
	}
	
	private void addAffectedShot3ds(CalcStation station, Set<Shot3d> affectedShot3ds) {
		for (CalcShot shot : station.shots.values()) {
			Shot3d shot3d = shot3ds.get(shot.key());
			if (shot3d != null) {
				affectedShot3ds.add(shot3d);
			}
		}
	}

	private void addGlowForSameSurveyDesignation(CalcShot origShot, final Map<StationKey, Float> glowAtStations,
			Set<Shot3d> affectedShot3ds, final Task<?> task) {
		String fromSurveyDesignation = getSurveyDesignation(origShot.fromStation.name);
		String fromCaveName = or(origShot.fromStation.cave, origShot.trip.cave.name, "");
		CalcCave fromCave = project.caves.get(fromCaveName);
		if (fromCave != null) {
			for (CalcStation station : fromCave.stationsBySurveyDesignation.get(fromSurveyDesignation)) {
				if (task.isCanceled()) {
					break;
				}
				glowAtStations.put(station.key(), 1f);
				addAffectedShot3ds(station, affectedShot3ds);
			}
		}

		String toSurveyDesignation = getSurveyDesignation(origShot.toStation.name);
		String toCaveName = or(origShot.toStation.cave, origShot.trip.cave.name, "");
		CalcCave toCave = project.caves.get(toCaveName);
		if (toCave != null && (toCave != fromCave) || !fromSurveyDesignation.equals(toSurveyDesignation)) {
			for (CalcStation station : toCave.stationsBySurveyDesignation.get(toSurveyDesignation)) {
				if (task.isCanceled()) {
					break;
				}
				glowAtStations.put(station.key(), 1f);
				addAffectedShot3ds(station, affectedShot3ds);
			}
		}
	}

	private void addGlowForTrip(CalcTrip trip, final Map<StationKey, Float> glowAtStations,
			final Set<Shot3d> affectedShot3ds, final Task<?> task) {
		for (ShotKey key : trip.shots.keySet()) {
			if (task.isCanceled()) {
				break;
			}
			if (key == null) {
				Thread.dumpStack();
			}
			glowAtStations.put(key.fromKey(), 1f);
			glowAtStations.put(key.toKey(), 1f);
			Shot3d shot3d = shot3ds.get(key);
			if (shot3d != null) {
				affectedShot3ds.add(shot3d);
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

	public void setStationLabelColor(Color stationLabelColor) {
		this.stationLabelColor = stationLabelColor;
		if (textRenderer != null) {
			textRenderer.setColor(stationLabelColor);
		}
	}

	public void setCenterlineColor(Color centerlineColor) {
		this.centerlineColor.value(
				centerlineColor.getRed() / 255.0f,
				centerlineColor.getGreen() / 255.0f,
				centerlineColor.getBlue() / 255.0f,
				centerlineColor.getAlpha() / 255.0f);
	}

	public void setStationLabelDensity(float stationLabelDensity) {
		this.stationLabelDensity = stationLabelDensity;
	}
	
	public void setStationLabelFontSize(float stationLabelFontSize) {
		this.stationLabelFontSize = stationLabelFontSize;
	}

	public void setShowLeadLabels(boolean showLeadLabels) {
		this.showLeadLabels = showLeadLabels;
	}

	public void setMaxCenterlineDistance(float maxCenterlineDistance) {
		this.maxCenterlineDistance.value(maxCenterlineDistance);
	}

	public void setShowSpatialIndex(boolean showSpatialIndex) {
		this.showSpatialIndex = showSpatialIndex;
	}

	public Unit<Length> getDisplayLengthUnit() {
		return displayLengthUnit;
	}

	public void setDisplayLengthUnit(Unit<Length> displayLengthUnit) {
		this.displayLengthUnit = displayLengthUnit;
	}
	
	public void setBackgroundColor(Color color) {
		backgroundColor.value(
			color.getRed() / 255f,
			color.getGreen() / 255f,
			color.getBlue() / 255f,
			color.getAlpha() / 255f
		);
	}
}

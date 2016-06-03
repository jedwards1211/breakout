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

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andork.jogl.BufferHelper;
import org.andork.jogl.Dumps;
import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.JoglResourceManager;

import com.jogamp.opengl.GL2ES2;

public class JoglText extends JoglManagedResource implements JoglDrawable {
	public static class Builder {
		private final float[] lrtb = new float[4];

		private Map<SegmentKey, BufferHelper> buffers = new HashMap<SegmentKey, BufferHelper>();
		private float[] dot = new float[3];
		private float[] nextDot = new float[3];
		private final float[] baseline = new float[3];
		private final float[] ascent = new float[3];
		private JoglTextProgram program;

		public Builder() {
			baseline[0] = 1;
			ascent[1] = 1;
		}

		public Builder add(String text, GlyphCache cache, float... color) {
			SegmentKey key = null;
			BufferHelper buffer = null;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				GlyphPage page = cache.getPage(c);
				if (key == null || key.page != page) {
					key = new SegmentKey(page, color);
					buffer = buffers.get(key);
					if (buffer == null) {
						buffer = new BufferHelper();
						buffers.put(key, buffer);
					}
				}

				page.getTexcoordBounds(c, lrtb);

				float scale = 1f / page.metrics.getAscent();

				float width = page.metrics.charWidth(c) * scale;
				nextDot[0] = dot[0] + baseline[0] * width;
				nextDot[1] = dot[1] + baseline[1] * width;
				nextDot[2] = dot[2] + baseline[2] * width;

				float ascentScale = page.metrics.getMaxAscent() * scale;
				float descentScale = -page.metrics.getMaxDescent() * scale;

				buffer.putAsFloats(dot[0] + ascent[0] * descentScale);
				buffer.putAsFloats(dot[1] + ascent[1] * descentScale);
				buffer.putAsFloats(dot[2] + ascent[2] * descentScale);
				buffer.putAsFloats(lrtb[0], lrtb[3]);

				buffer.putAsFloats(dot[0] + ascent[0] * ascentScale);
				buffer.putAsFloats(dot[1] + ascent[1] * ascentScale);
				buffer.putAsFloats(dot[2] + ascent[2] * ascentScale);
				buffer.putAsFloats(lrtb[0], lrtb[2]);

				buffer.putAsFloats(nextDot[0] + ascent[0] * descentScale);
				buffer.putAsFloats(nextDot[1] + ascent[1] * descentScale);
				buffer.putAsFloats(nextDot[2] + ascent[2] * descentScale);
				buffer.putAsFloats(lrtb[1], lrtb[3]);

				buffer.putAsFloats(dot[0] + ascent[0] * ascentScale);
				buffer.putAsFloats(dot[1] + ascent[1] * ascentScale);
				buffer.putAsFloats(dot[2] + ascent[2] * ascentScale);
				buffer.putAsFloats(lrtb[0], lrtb[2]);

				buffer.putAsFloats(nextDot[0] + ascent[0] * descentScale);
				buffer.putAsFloats(nextDot[1] + ascent[1] * descentScale);
				buffer.putAsFloats(nextDot[2] + ascent[2] * descentScale);
				buffer.putAsFloats(lrtb[1], lrtb[3]);

				buffer.putAsFloats(nextDot[0] + ascent[0] * ascentScale);
				buffer.putAsFloats(nextDot[1] + ascent[1] * ascentScale);
				buffer.putAsFloats(nextDot[2] + ascent[2] * ascentScale);
				buffer.putAsFloats(lrtb[1], lrtb[2]);

				float[] temp = dot;
				dot = nextDot;
				nextDot = temp;
			}

			return this;
		}

		public Builder ascent(float... ascent) {
			System.arraycopy(ascent, 0, this.ascent, 0, 3);
			return this;
		}

		public Builder baseline(float... baseline) {
			System.arraycopy(baseline, 0, this.baseline, 0, 3);
			return this;
		}

		public JoglText create(JoglResourceManager manager) {
			Segment[] segments = new Segment[buffers.size()];
			int k = 0;
			for (Map.Entry<SegmentKey, BufferHelper> entry : buffers.entrySet()) {
				FloatBuffer data = entry.getValue().toByteBuffer().asFloatBuffer();
				Dumps.dumpBuffer(data, "%9.2f, ", 5);
				segments[k++] = new Segment(data, entry.getKey().page, data.capacity() / 5, entry.getKey().color);
			}

			return new JoglText(manager, Arrays.asList(segments), program);
		}

		public Builder dot(float... dot) {
			System.arraycopy(dot, 0, this.dot, 0, 3);
			return this;
		}

		public Builder program(JoglTextProgram program) {
			this.program = program;
			return this;
		}
	}

	public static class Segment {
		private final FloatBuffer data;
		private int buffer;
		public final GlyphPage page;
		public final int count;
		public final float[] color;

		private Segment(FloatBuffer data, GlyphPage page, int count, float[] color) {
			this.data = data;
			this.page = page;
			this.count = count;
			this.color = color;
		}

		public int buffer() {
			return buffer;
		}
	}

	private static class SegmentKey {
		private final GlyphPage page;
		private final float[] color;

		public SegmentKey(GlyphPage page, float[] color) {
			this.page = page;
			this.color = Arrays.copyOf(color, 4);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof SegmentKey) {
				SegmentKey ps = (SegmentKey) o;
				return ps.page == page && Arrays.equals(ps.color, color);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return 31 * page.hashCode() ^ Arrays.hashCode(color);
		}
	}

	public final List<Segment> segments;

	private JoglTextProgram program;

	public final float[] origin = new float[3];

	private JoglText(JoglResourceManager manager, List<Segment> segments, JoglTextProgram program) {
		super(manager);
		this.segments = segments;
		this.program = program;
	}

	@Override
	protected void doDispose(GL2ES2 gl) {
		if (!segments.isEmpty()) {
			int[] temp = new int[segments.size()];
			int k = 0;
			for (Segment segment : segments) {
				temp[k++] = segment.buffer;
				segment.buffer = 0;
			}

			gl.glDeleteBuffers(k, temp, 0);
		}
	}

	@Override
	protected void doInit(GL2ES2 gl) {
		if (!segments.isEmpty()) {
			int[] temp = new int[segments.size()];
			gl.glGenBuffers(segments.size(), temp, 0);

			int k = 0;
			for (Segment segment : segments) {
				segment.buffer = temp[k++];
				segment.data.position(0);
				gl.glBindBuffer(GL_ARRAY_BUFFER, segment.buffer);
				gl.glBufferData(GL_ARRAY_BUFFER, segment.data.capacity() * 4, segment.data, GL_STATIC_DRAW);
			}

			gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
	}

	@Override
	protected void doRelease() {
		program.removeUser(this);

		for (Segment segment : segments) {
			segment.page.removeUser(this);
		}
	}

	@Override
	protected void doUse() {
		program.addUser(this);

		for (Segment segment : segments) {
			segment.page.addUser(this);
		}
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		requireInitialized();

		if (program != null) {
			program.draw(this, context, gl, m, n);
		}
	}

	public JoglTextProgram program() {
		return program;
	}

	public JoglText program(JoglTextProgram program) {
		if (this.program != program) {
			if (isInUse()) {
				if (this.program != null) {
					this.program.removeUser(this);
				}
				if (program != null) {
					program.addUser(this);
				}
			}
			this.program = program;
		}
		return this;
	}
}

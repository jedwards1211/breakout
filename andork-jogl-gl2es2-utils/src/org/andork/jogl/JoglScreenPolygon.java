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
package org.andork.jogl;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_DYNAMIC_DRAW;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINE_LOOP;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static org.andork.math3d.Vecmath.setf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import org.andork.jogl.util.JoglUtils;

import com.jogamp.opengl.GL2ES2;

public class JoglScreenPolygon implements JoglDrawable, JoglResource {
	private static int programUseCount = 0;
	private static int program = 0;
	private static int screenXform_location;
	private static int a_pos_location;
	private static int u_outlineColor_location;

	private boolean initialized;

	private final float[] outlineColor = { 0f, 1f, 0f, 1f };
	private final float[] fillColor = { 0f, 1f, 0f, 0.5f };

	private final int[] buffers = new int[2];
	private final ByteBuffer[] data = new ByteBuffer[2];
	private boolean dataChanged;

	public void clearPoints() {
		Arrays.fill(data, null);
	}

	@Override
	public void dispose(GL2ES2 gl) {
		if (!initialized) {
			return;
		}
		initialized = false;
		if (--programUseCount == 0) {
			disposeProgram(gl);
		}

		gl.glDeleteBuffers(2, buffers, 0);
	}

	private void disposeProgram(GL2ES2 gl) {
		gl.glDeleteProgram(program);
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (data[0] == null) {
			dispose(gl);
			return;
		}
		if (!initialized) {
			init(gl);
		}

		gl.glUseProgram(program);

		gl.glUniformMatrix4fv(screenXform_location, 1, false, context.screenXform(), 0);

		gl.glEnableVertexAttribArray(a_pos_location);

		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// outline

		gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);

		if (dataChanged) {
			gl.glBufferData(GL_ARRAY_BUFFER, data[0].capacity(), data[0], GL_DYNAMIC_DRAW);
		}

		gl.glUniform4fv(u_outlineColor_location, 1, outlineColor, 0);
		gl.glVertexAttribPointer(a_pos_location, 2, GL_FLOAT, false, 8, 0);

		gl.glDrawArrays(GL_LINE_LOOP, 0, data[0].capacity() / 8);

		// fill

		if (data[1] != null) {
			gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);

			if (dataChanged) {
				gl.glBufferData(GL_ARRAY_BUFFER, data[1].capacity(), data[1], GL_STATIC_DRAW);
			}

			gl.glUniform4fv(u_outlineColor_location, 1, fillColor, 0);
			gl.glVertexAttribPointer(a_pos_location, 2, GL_FLOAT, false, 8, 0);

			gl.glDrawArrays(GL_TRIANGLES, 0, data[1].capacity() / 8);
		}

		// //////////////////////////////

		gl.glDisable(GL_BLEND);
		gl.glDisableVertexAttribArray(a_pos_location);

		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		gl.glUseProgram(0);

		dataChanged = false;
	}

	@Override
	public void init(GL2ES2 gl) {
		if (initialized || data[0] == null) {
			return;
		}
		initialized = true;
		dataChanged = true;
		if (programUseCount++ == 0) {
			initProgram(gl);
		}

		gl.glGenBuffers(2, buffers, 0);
	}

	private void initProgram(GL2ES2 gl) {
		String vertexShaderCode = "#version 330\n" +
				"uniform mat4 screenXform;" +
				"in vec2 a_pos;" +
				"void main() {" +
				"  gl_Position = screenXform * vec4(a_pos, 0.0, 1.0);" +
				"}";

		String fragmentShaderCode = "#version 330\n" +
				"uniform vec4 u_outlineColor;" +
				"out vec4 color;" +
				"void main() {" +
				"  color = u_outlineColor;" +
				"}";

		program = JoglUtils.loadProgram(gl, vertexShaderCode, fragmentShaderCode);
		screenXform_location = gl.glGetUniformLocation(program, "screenXform");
		a_pos_location = gl.glGetAttribLocation(program, "a_pos");
		u_outlineColor_location = gl.glGetUniformLocation(program, "u_outlineColor");
	}

	public void setColor(float... color) {
		setf(outlineColor, color);
	}

	public void setPoints(float[]... points) {
		setPoints(Arrays.asList(points));
	}

	public void setPoints(int stride, float... points) {
		int nPoints = points.length / stride;
		if (data[0] == null || nPoints * 8 != data[0].capacity()) {
			data[0] = ByteBuffer.allocateDirect(nPoints * 8);
			data[0].order(ByteOrder.nativeOrder());
		}
		data[0].position(0);
		// Outline outline = new Outline( );
		for (int i = 0; i < points.length; i += stride) {
			data[0].putFloat(points[i]).putFloat(points[i + 1]);
			// outline.addVertex( new SVertex( point[ 0 ] , point[ 1 ] , 0f ,
			// true ) );
		}
		data[0].position(0);

		// if( points.size( ) > 2 )
		// {
		// outline.setClosed( true );
		// CDTriangulator2D triangulator2d = new CDTriangulator2D( );
		// triangulator2d.addCurve( outline );
		// ArrayList<Triangle> triangles = triangulator2d.generate( );
		//
		// if( data[ 1 ] == null || triangles.size( ) * 24 != data[ 1
		// ].capacity( ) )
		// {
		// data[ 1 ] = ByteBuffer.allocateDirect( triangles.size( ) * 24 );
		// data[ 1 ].order( ByteOrder.nativeOrder( ) );
		// }
		// data[ 1 ].position( 0 );
		// for( Triangle triangle : triangles )
		// {
		// for( Vertex vertex : triangle.getVertices( ) )
		// {
		// data[ 1 ].putFloat( vertex.getX( ) ).putFloat( vertex.getY( ) );
		// }
		// }
		// data[ 1 ].position( 0 );
		// }
		// else
		// {
		// data[ 1 ] = null;
		// }
		dataChanged = true;
	}

	public void setPoints(List<float[]> points) {
		if (data[0] == null || points.size() * 8 != data[0].capacity()) {
			data[0] = ByteBuffer.allocateDirect(points.size() * 8);
			data[0].order(ByteOrder.nativeOrder());
		}
		data[0].position(0);
		// Outline outline = new Outline( );
		for (float[] point : points) {
			data[0].putFloat(point[0]).putFloat(point[1]);
			// outline.addVertex( new SVertex( point[ 0 ] , point[ 1 ] , 0f ,
			// true ) );
		}
		data[0].position(0);

		// if( points.size( ) > 2 )
		// {
		// outline.setClosed( true );
		// CDTriangulator2D triangulator2d = new CDTriangulator2D( );
		// triangulator2d.addCurve( outline );
		// ArrayList<Triangle> triangles = triangulator2d.generate( );
		//
		// if( data[ 1 ] == null || triangles.size( ) * 24 != data[ 1
		// ].capacity( ) )
		// {
		// data[ 1 ] = ByteBuffer.allocateDirect( triangles.size( ) * 24 );
		// data[ 1 ].order( ByteOrder.nativeOrder( ) );
		// }
		// data[ 1 ].position( 0 );
		// for( Triangle triangle : triangles )
		// {
		// for( Vertex vertex : triangle.getVertices( ) )
		// {
		// data[ 1 ].putFloat( vertex.getX( ) ).putFloat( vertex.getY( ) );
		// }
		// }
		// data[ 1 ].position( 0 );
		// }
		// else
		// {
		// data[ 1 ] = null;
		// }
		dataChanged = true;
	}

}

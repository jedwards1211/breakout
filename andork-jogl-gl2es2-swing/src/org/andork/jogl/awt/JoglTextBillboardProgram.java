package org.andork.jogl.awt;

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TRIANGLES;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglResourceManager;
import org.andork.jogl.awt.JoglText.Segment;
import org.andork.jogl.util.JoglUtils;
import org.andork.jogl.util.JoglUtils.LoadedProgram;

public class JoglTextBillboardProgram extends JoglTextProgram
{
	private LoadedProgram program;

	private int mLoc;

	private int vLoc;

	private int pLoc;

	private int pxLoc;

	private int u_originLoc;

	private int u_colorLoc;

	private int a_posLoc;

	private int a_texcoordLoc;

	private int u_textureLoc;

	private static final String vertexShaderCode =
		"uniform mat4 p;" +
			"uniform mat4 v;" +
			"uniform mat4 m;" +
			"uniform vec2 px;" +

			"uniform vec3 u_origin;" +
			"attribute vec3 a_pos;" +
			"attribute vec2 a_texcoord;" +

			"varying vec2 v_texcoord;" +

			"void main() {" +
			"  v_texcoord = a_texcoord;" +
			"  gl_Position = p * v * m * vec4(u_origin, 1.0);" +
			"  gl_Position.xy += vec2(a_pos.xy * px * gl_Position.w);" +
			"}";

	private static final String fragmentShaderCode =
		"uniform sampler2D u_texture;" +
			"uniform vec4 u_color;" +
			"varying vec2 v_texcoord;" +

			"void main() {" +
			// "  gl_FragColor = vec4(u_color.xyz , u_color.w * texture2D(u_texture, v_texcoord).r);" +
			"  gl_FragColor = vec4(u_color * texture2D(u_texture, v_texcoord));" +
			"  if (gl_FragColor.a == 0.0) {" +
			"    discard;" +
			"  }" +
			"}";

	public JoglTextBillboardProgram( JoglResourceManager manager )
	{
		super( manager );
	}

	@Override
	public void draw( JoglText text , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
	{
		gl.glUseProgram( program.program );

		gl.glEnable( GL_DEPTH_TEST );

		gl.glEnable( GL_BLEND );
		gl.glBlendFunc( GL_SRC_ALPHA , GL_ONE_MINUS_SRC_ALPHA );

		gl.glUniformMatrix4fv( mLoc , 1 , false , m , 0 );

		gl.glUniformMatrix4fv( vLoc , 1 , false , context.viewXform( ) , 0 );

		gl.glUniformMatrix4fv( pLoc , 1 , false , context.projXform( ) , 0 );

		gl.glUniform2fv( pxLoc , 1 , context.pixelScale( ) , 0 );

		gl.glUniform3fv( u_originLoc , 1 , text.origin , 0 );

		gl.glUniform1i( u_textureLoc , 0 );

		gl.glEnableVertexAttribArray( a_posLoc );
		gl.glEnableVertexAttribArray( a_texcoordLoc );

		gl.glActiveTexture( GL_TEXTURE0 );

		for( Segment segment : text.segments )
		{
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.buffer( ) );
			gl.glBindTexture( GL_TEXTURE_2D , segment.page.getTexture( ) );

			gl.glUniform4fv( u_colorLoc , 1 , segment.color , 0 );

			gl.glVertexAttribPointer( a_posLoc , 3 , GL_FLOAT , false , 20 , 0 );
			gl.glVertexAttribPointer( a_texcoordLoc , 2 , GL_FLOAT , false , 20 , 12 );

			gl.glDrawArrays( GL_TRIANGLES , 0 , segment.count );
		}

		gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
		gl.glBindTexture( GL_TEXTURE_2D , 0 );

		gl.glDisable( GL_BLEND );
		gl.glDisable( GL_DEPTH_TEST );

		gl.glUseProgram( 0 );
	}

	@Override
	protected void doDispose( GL2ES2 gl )
	{
		program.dispose( gl );
		program = null;
	}

	@Override
	protected void doInit( GL2ES2 gl )
	{
		program = JoglUtils.loadProgram2( gl , vertexShaderCode , fragmentShaderCode );

		mLoc = gl.glGetUniformLocation( program.program , "m" );
		vLoc = gl.glGetUniformLocation( program.program , "v" );
		pLoc = gl.glGetUniformLocation( program.program , "p" );
		pxLoc = gl.glGetUniformLocation( program.program , "px" );
		u_originLoc = gl.glGetUniformLocation( program.program , "u_origin" );
		u_colorLoc = gl.glGetUniformLocation( program.program , "u_color" );
		a_posLoc = gl.glGetAttribLocation( program.program , "a_pos" );
		a_texcoordLoc = gl.glGetAttribLocation( program.program , "a_texcoord" );
		u_textureLoc = gl.glGetUniformLocation( program.program , "u_texture" );
	}

}

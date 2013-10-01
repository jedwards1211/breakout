package org.andork.jogl.util;

import static org.andork.jogl.util.GLUtils.checkGLError;
import static org.andork.jogl.util.GLUtils.vertexAttribPointer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;

import org.andork.util.Reparam;

import sun.awt.image.PixelConverter.Rgba;

public class JOGLOverlay
{
	String				vertexShaderCode	=
													"attribute vec2 a_position;" +
															"attribute vec2 a_texcoord;" +
															"varying vec2 v_texcoord;" +
															"void main() {" +
															"  v_texcoord = a_texcoord;" +
															"  gl_Position = vec4(a_position, 0.0, 1.0);" +
															"}";
	
	String				fragmentShaderCode	=
													"varying vec2 v_texcoord;" +
															"uniform sampler2D u_texture;" +
															"void main() {" +
															"  gl_FragColor = texture2D(u_texture, v_texcoord);" +
															"}";
	
	BufferedImage		image;
	int[ ]				rgbArray;
	
	FloatBuffer			vertexBuffer;
	
	int					program;
	
	int					texture;
	int					texwidth;
	int					texheight;
	IntBuffer			pixels;
	
	int					vao;
	int					vbo;
	
	private Rectangle	area;
	
	public void init( GL3 gl )
	{
		program = GLUtils.loadProgram( gl , vertexShaderCode , fragmentShaderCode , true );
		
		vertexBuffer = BufferUtils.newFloatBuffer( 4 * 4 * 6 );
		
		texture = GLUtils.genTexture( gl );
		vao = GLUtils.genVertexArray( gl );
		vbo = GLUtils.genBuffer( gl );
		
		gl.glBindTexture( GL3.GL_TEXTURE_2D , texture );
		checkGLError( gl );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D , GL3.GL_TEXTURE_MIN_FILTER , GL3.GL_NEAREST );
		checkGLError( gl );
		
		gl.glTexParameteri( GL3.GL_TEXTURE_2D , GL3.GL_TEXTURE_MAG_FILTER , GL3.GL_NEAREST );
		checkGLError( gl );
		
		gl.glBindVertexArray( vao );
		checkGLError( gl );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbo );
		checkGLError( gl );
		
		vertexAttribPointer( gl , program , "a_position" , 2 , GL3.GL_FLOAT , false , 16 , 0 , true );
		vertexAttribPointer( gl , program , "a_texcoord" , 2 , GL3.GL_FLOAT , false , 16 , 8 , true );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		checkGLError( gl );
	}
	
	public void draw( GL3 gl )
	{
		gl.glEnable( GL3.GL_BLEND );
		checkGLError( gl );
		
		gl.glBlendFunc( GL3.GL_SRC_ALPHA , GL3.GL_ONE_MINUS_SRC_ALPHA );
		checkGLError( gl );
		
		gl.glUseProgram( program );
		checkGLError( gl );
		
		gl.glBindVertexArray( vao );
		checkGLError( gl );
		
		gl.glActiveTexture( GL3.GL_TEXTURE0 );
		checkGLError( gl );
		
		gl.glBindTexture( GL3.GL_TEXTURE_2D , texture );
		checkGLError( gl );
		
		int textureLocation = gl.glGetUniformLocation( program , "u_texture" );
		gl.glUniform1i( textureLocation , 0 );
		checkGLError( gl );
		
		gl.glDrawArrays( GL3.GL_TRIANGLES , 0 , 6 );
		checkGLError( gl );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl );
	}
	
	private static int ceiling2( int x )
	{
		int result = 2;
		while( result < x )
		{
			result <<= 1;
		}
		return result;
	}
	
	public void setPosition( GL3 gl , Rectangle area , Dimension viewSize , boolean preserve )
	{
		this.area = area;
		texwidth = ceiling2( area.width );
		texheight = ceiling2( area.height );
		
		if( image == null || image.getWidth( ) != texwidth || image.getHeight( ) != texheight )
		{
			BufferedImage newImage = new BufferedImage( texwidth , texheight , BufferedImage.TYPE_INT_ARGB );
			if( preserve && image != null )
			{
				newImage.createGraphics( ).drawImage( image , 0 , 0 , null );
			}
			image = newImage;
			pixels = BufferUtils.newIntBuffer( texwidth * texheight );
		}
		
		if( rgbArray == null || rgbArray.length != area.width * area.height )
		{
			rgbArray = new int[ area.width * area.height ];
		}
		
		float left = Reparam.linear( area.x , 0 , viewSize.width , -1 , 1 );
		float right = Reparam.linear( area.x + area.width , 0 , viewSize.width , -1 , 1 );
		float top = Reparam.linear( area.y , 0 , viewSize.height , 1 , -1 );
		float bottom = Reparam.linear( area.y + area.height , 0 , viewSize.height , 1 , -1 );
		
		float uright = Reparam.linear( area.width , 0 , texwidth , 0 , 1 );
		float vbottom = Reparam.linear( area.height , 0 , texheight , 1 , 0 );
		
		vertexBuffer.put( left ).put( top ).put( 0 ).put( 1 );
		vertexBuffer.put( right ).put( bottom ).put( uright ).put( vbottom );
		vertexBuffer.put( right ).put( top ).put( uright ).put( 1 );
		vertexBuffer.put( right ).put( bottom ).put( uright ).put( vbottom );
		vertexBuffer.put( left ).put( top ).put( 0 ).put( 1 );
		vertexBuffer.put( left ).put( bottom ).put( 0 ).put( vbottom );
		
		vertexBuffer.position( 0 );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbo );
		checkGLError( gl );
		
		gl.glBufferData( GL3.GL_ARRAY_BUFFER , 4 * 4 * 6 , vertexBuffer , GL3.GL_STATIC_DRAW );
		checkGLError( gl );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		checkGLError( gl );
	}
	
	public Graphics2D getGraphics( )
	{
		return image.createGraphics( );
	}
	
	public void updateBuffer( GL3 gl )
	{
		gl.glActiveTexture( GL3.GL_TEXTURE0 );
		checkGLError( gl );
		
		gl.glBindTexture( GL3.GL_TEXTURE_2D , texture );
		checkGLError( gl );
		
		image.getRGB( 0 , 0 , area.width , area.height , rgbArray , 0 , area.width );
		
		pixels.position( 0 );
		for( int y = texheight - 1 ; y >= 0 ; y-- )
		{
			for( int x = 0 ; x < texwidth ; x++ )
			{
				if( y < area.height && x < area.width )
				{
					int argb = rgbArray[ y * area.width + x ];
					int rgba = ( argb << 8 ) + (( argb >> 24 ) & 0xff);
					pixels.put( rgba );
				}
				else
				{
					pixels.put( 0 );
				}
			}
		}
		
		pixels.position( 0 );
		
		gl.glTexImage2D( GL3.GL_TEXTURE_2D , 0 , GL3.GL_RGBA , texwidth , texheight , 0 , GL3.GL_RGBA , GL3.GL_UNSIGNED_INT_8_8_8_8 , pixels );
		checkGLError( gl );
		
		gl.glBindTexture( GL3.GL_TEXTURE_2D , 0 );
		checkGLError( gl );
	}
}

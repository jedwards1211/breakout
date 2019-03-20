package org.breakout.model;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglManagedResource;
import org.breakout.model.shader.TerrainProgram;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class TempTile extends JoglManagedResource implements JoglDrawable {
	public static TerrainTile load() throws IOException {
		BufferedImage heightmap = ImageIO.read(TempTile.class.getResourceAsStream("terrain-rgb.png"));
		// TODO tile xyz - [29, 57, 7]
		return new TerrainTile(heightmap, (float[] xyz) -> {
			float x = xyz[0];
			float y = xyz[1];
			float z = xyz[2];
			xyz[0] = x * 400;
			xyz[1] = z - 2200;
			xyz[2] = y * 400;
		});
	}
	
	TerrainTile tile;
	TerrainProgram program = TerrainProgram.INSTANCE;
	Texture texture;
	
	protected boolean doInit(GL2ES2 gl) {
		program.init(gl);
		try {
			tile = load();
			tile.positionLocation = program.position;
			tile.normalLocation = program.normal;
			tile.texcoordLocation = program.texcoord;
			tile.init(gl);
			texture = TextureIO.newTexture(TempTile.class.getResourceAsStream("satellite.png"), false, "png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	protected void doDispose(GL2ES2 gl) {
		program.dispose(gl);

		if (tile != null) {
			tile.dispose(gl);
		}
		tile = null;
		
		if (texture != null) {
			texture.destroy(gl);
		}
		texture = null;
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		if (tile == null) {
			return;
		}
		
		program.use(gl);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		program.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), m, n);
		program.position.enableArray(gl);
		program.normal.enableArray(gl);
		program.texcoord.enableArray(gl);
		program.lightDirection.put(gl, 0, 1, 1);
		program.ambient.put(gl, 0.5f);
		program.alpha.put(gl, 1.0f);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		texture.bind(gl);
		program.satelliteImagery.put(gl, 0);
		
		tile.draw(context, gl, m, n);

		program.position.disableArray(gl);
		program.normal.disableArray(gl);
		program.texcoord.disableArray(gl);
		gl.glDisable(GL.GL_BLEND);
		gl.glDisable(GL.GL_DEPTH_TEST);
		program.use(gl, false);
	}

}

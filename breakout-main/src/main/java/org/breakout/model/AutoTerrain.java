package org.breakout.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import org.andork.io.InputStreamUtils;
import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglManagedResource;
import org.andork.math3d.Clip3f;
import org.breakout.mabox.MapboxClient;
import org.breakout.mabox.MapboxClient.ImageTileFormat;
import org.breakout.mabox.Tilebelt;
import org.breakout.model.shader.TerrainProgram;
import org.breakout.proj4.Proj4Utils;
import org.breakout.proj4.WebMercatorProjection;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.Datum;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class AutoTerrain extends JoglManagedResource implements JoglDrawable {
	class ManagedTile extends JoglManagedResource implements JoglDrawable {
		TerrainTile tile;
		final TerrainProgram program = TerrainProgram.INSTANCE;
		Texture texture;
		
		public ManagedTile(TerrainTile tile, Texture texture) {
			this.tile = tile;
			this.texture = texture;
		}

		protected boolean doInit(GL2ES2 gl) {
			program.init(gl);
			tile.positionLocation = program.position;
			tile.normalLocation = program.normal;
			tile.texcoordLocation = program.texcoord;
			tile.init(gl);
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
			program.alpha.put(gl, 0.7f);
			program.putClip(gl, clip);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			if (texture != null) {
				texture.enable(gl);
				texture.bind(gl);
			}
			program.satelliteImagery.put(gl, 0);
			
			tile.draw(context, gl, m, n);
	
			if (texture != null) texture.disable(gl);
			program.position.disableArray(gl);
			program.normal.disableArray(gl);
			program.texcoord.disableArray(gl);
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_DEPTH_TEST);
			program.use(gl, false);
		}
	
	}
	
	Clip3f clip = new Clip3f(new float[] { 0,  1,  0}, -Float.MAX_VALUE, Float.MAX_VALUE);

	MapboxClient mapbox;
	ExecutorService fetchService;
	GLAutoDrawable autoDrawable;
	CoordinateReferenceSystem coordinateReferenceSystem;
	float[] mbr;
	
	List<ManagedTile> newTiles = new ArrayList<>();
	List<ManagedTile> tiles = new ArrayList<>();
	
	class CoordinateConverter implements Consumer<float[]> {
		int tileSize;
		long[] tile;
		long x0;
		long y0; 
		final CoordinateReferenceSystem coordinateReferenceSystem;
		final CoordinateTransform xform;
		final ProjCoordinate p = new ProjCoordinate();
		
		public CoordinateConverter(int tileSize, long[] tile) {
			this.tileSize = tileSize;
			this.tile = tile;
			WebMercatorProjection webmerc = new WebMercatorProjection(tileSize, (double) tile[2]);
			coordinateReferenceSystem =
				new CoordinateReferenceSystem(null, new String[0], Datum.WGS84, webmerc);
			xform = new BasicCoordinateTransform(
				coordinateReferenceSystem,
				AutoTerrain.this.coordinateReferenceSystem);
			x0 = tile[0] * tileSize;
			y0 = tile[1] * tileSize;
		}

		@Override
		public void accept(float[] t) {
			p.x = x0 + t[0];
			p.y = y0 + t[1];
			p.z = t[2];
			xform.transform(p, p);
			t[0] = (float) p.x;
			t[1] = (float) p.z;
			t[2] = (float) -p.y;
		}
	}

	public AutoTerrain(MapboxClient mapbox, ExecutorService fetchService, GLAutoDrawable autoDrawable, CoordinateReferenceSystem coordinateReferenceSystem, float[] mbr) {
		super();
		this.mapbox = mapbox;
		this.fetchService = fetchService;
		this.autoDrawable = autoDrawable;
		this.coordinateReferenceSystem = coordinateReferenceSystem;
		this.mbr = mbr;
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		for (ManagedTile tile : newTiles) {
			tile.init(gl);
			tiles.add(tile);
		}
		newTiles.clear();
		for (ManagedTile tile : tiles) {
			tile.draw(context, gl, m, n);
		}
	}

	@Override
	protected boolean doInit(GL2ES2 gl) {
		fetchService.submit(() -> {
			ProjCoordinate min = Proj4Utils.convertToGeographic(new ProjCoordinate(mbr[0], -mbr[2], mbr[1]), coordinateReferenceSystem);
			ProjCoordinate max = Proj4Utils.convertToGeographic(new ProjCoordinate(mbr[3], -mbr[5], mbr[4]), coordinateReferenceSystem);
			long[] tileId = Tilebelt.bboxToTile(new double[] {min.x, min.y, max.x, max.y});
			
			try {
				BufferedImage terrain = mapbox.getTile(
					MapboxClient.TERRAIN_RGB, tileId, false, ImageTileFormat.PNGRAW);
				final byte[] satelliteData = InputStreamUtils.readAllBytes(mapbox.getTileStream(
					MapboxClient.SATELLITE, tileId, false, ImageTileFormat.PNGRAW));
				autoDrawable.invoke(true, drawable -> {
					try {
						Texture satellite = TextureIO.newTexture(
							new ByteArrayInputStream(satelliteData),
							false, "png");
						TerrainTile terrainTile = new TerrainTile(terrain,
							new CoordinateConverter(terrain.getWidth(), tileId));
						terrainTile.usePrecomputedIndexPointers();
						newTiles.add(new ManagedTile(terrainTile, satellite));
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return true;
	}

	@Override
	protected void doDispose(GL2ES2 gl) {
		for (ManagedTile tile : tiles) {
			tile.dispose(gl);
		}
	}
	
	public Clip3f getClip() {
		return clip;
	}

	public void setClip(Clip3f clip) {
		this.clip = clip;
	}
}

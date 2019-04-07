package org.breakout.model;

import static org.andork.math3d.Vecmath.subDot3;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import org.andork.io.InputStreamUtils;
import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.JoglResource;
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

public class AutoTerrain implements JoglDrawable, JoglResource {
	class ManagedTile extends JoglManagedResource implements JoglDrawable {
		long[] id;
		TerrainTile tile;
		final TerrainProgram program = TerrainProgram.INSTANCE;
		Texture texture;
		
		public ManagedTile(long[] id, TerrainTile tile, Texture texture) {
			this.id = id;
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
	float[][] corners;
	
	List<ManagedTile> newTiles = new ArrayList<>();
	SortedMap<Integer, TileGroup> tileGroups = new TreeMap<>(new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			// descending
			return o2 - o1;
		}
	});
	
	boolean visible = false;
	boolean initialized = false;
	boolean reloadRequested = false;
	
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
		if (!visible) {
			dispose(gl);
			return;
		}
		
		if (reloadRequested) {
			reloadRequested = false;
			dispose(gl);
		}
		
		init(gl);
		
		for (ManagedTile tile : newTiles) {
			tile.init(gl);
			int z = (int) tile.id[2];
			TileGroup group = tileGroups.get(z);
			if (group == null) {
				group = new TileGroup();
				tileGroups.put(z, group);
			}
			group.add(tile);
		}
		newTiles.clear();
		calcOrder(context);
		
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		
		for (TileGroup group : tileGroups.values()) {
			// draw where stencil buffer is 0 (i.e., no higher-res terrain has been drawn)
			gl.glColorMask(true, true, true, true);
			gl.glEnable(GL.GL_BLEND);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glStencilFunc(GL.GL_EQUAL, 0, 0xff);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

			for (ManagedTile tile : group.iterable(tileComparator)) {
				tile.draw(context, gl, m, n);
			}

			// set stencil buffer to 1 everywhere we just drew terrain
			gl.glColorMask(false, false, false, false);
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_STENCIL_TEST);
			gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xff);
			gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);

			for (ManagedTile tile : group.iterable(tileComparator)) {
				tile.draw(context, gl, m, n);
			}
		}
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glColorMask(true, true, true, true);
	}

	public boolean init(GL2ES2 gl) {
		if (initialized) return true;
		initialized = true;
		
		List<long[]> tiles = new ArrayList<>();
		ProjCoordinate min = Proj4Utils.convertToGeographic(new ProjCoordinate(mbr[0], -mbr[2], mbr[1]), coordinateReferenceSystem);
		ProjCoordinate max = Proj4Utils.convertToGeographic(new ProjCoordinate(mbr[3], -mbr[5], mbr[4]), coordinateReferenceSystem);
		long[] rootTile = Tilebelt.bboxToTile(new double[] {min.x, min.y, max.x, max.y});
		
		final int tileSize = 256;
		CoordinateConverter rootConverter = new CoordinateConverter(tileSize, rootTile);
		corners = new float[][] {
			{0, 0, 0},
			{0, tileSize, 0},
			{tileSize, tileSize, 0},
			{tileSize, 0, 0}
		};
		for (float[] corner : corners) {
			rootConverter.accept(corner);
		}
		
		for (long[] child : Tilebelt.getChildren(rootTile)) {
			for (long[] grandchild : Tilebelt.getChildren(child)) {
				tiles.add(grandchild);
			}
		}
		
		for (long[] tileId : tiles) {
			fetchService.submit(() -> {
				
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
							newTiles.add(new ManagedTile(tileId, terrainTile, satellite));
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
		}
		return true;
	}

	public void dispose(GL2ES2 gl) {
		if (!initialized) return;
		initialized = false;
		for (TileGroup group : tileGroups.values()) {
			for (ManagedTile tile : group.iterable(null)) {
				tile.dispose(gl);
			}
		}
		tileGroups.clear();
	}
	
	public Clip3f getClip() {
		return clip;
	}

	public void setClip(Clip3f clip) {
		this.clip = clip;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public void reload() {
		reloadRequested = true;
	}
	
	private int lastBestCorner = -1;
	private int lastSecondBestCorner = -1;
	private ManagedTileComparator tileComparator = null;
	
	private boolean calcOrder(JoglDrawContext context) {
		float bestDist = -Float.MAX_VALUE;
		float secondBestDist = -Float.MAX_VALUE;
		int bestCorner = -1;
		int secondBestCorner = -1;
	
		float[] vi = context.inverseViewMatrix();
	
		for (int i = 0; i < corners.length; i++) {
			float dist = subDot3(vi, 12, corners[i], 0, vi, 8);
			if (dist > bestDist) {
				secondBestDist = bestDist;
				secondBestCorner = bestCorner;
				bestDist = dist;
				bestCorner = i;
			} else if (dist > secondBestDist) {
				secondBestDist = dist;
				secondBestCorner = i;
			}
		}
		
		if (bestDist < 0f) {
			return false;
		}
		
		if (bestCorner == lastBestCorner && secondBestCorner == lastSecondBestCorner) {
			return false;
		}

		lastBestCorner = bestCorner;
		lastSecondBestCorner = secondBestCorner;
		
		boolean yFirst = (bestCorner < 2) == (secondBestCorner < 2);
		boolean yDescending = bestCorner < 2;
		boolean xDescending = (bestCorner & 0x1) == 0;
		
		tileComparator = new ManagedTileComparator(yFirst, xDescending, yDescending);
		return true;
	}
	
	private static class TileIdComparator implements Comparator<long[]> {
		private boolean yFirst;
		private boolean xDescending;
		private boolean yDescending;
		
		public TileIdComparator(boolean yFirst, boolean xDescending, boolean yDescending) {
			super();
			this.yFirst = yFirst;
			this.xDescending = xDescending;
			this.yDescending = yDescending;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TileIdComparator other = (TileIdComparator) obj;
			if (xDescending != other.xDescending)
				return false;
			if (yDescending != other.yDescending)
				return false;
			if (yFirst != other.yFirst)
				return false;
			return true;
		}

		@Override
		public int compare(long[] o1, long[] o2) {
			int z = Long.compare(o1[2], o2[2]);
			if (z != 0) return -z;
			
			int x = Long.compare(o1[0], o2[0]);
			if (xDescending) x = -x;
			int y = Long.compare(o1[1], o2[1]);
			if (yDescending) y = -y;
			
			if (yFirst && y != 0) return y;
			if (x != 0) return x;
			return y;
		}
	}
	
	private static class ManagedTileComparator implements Comparator<ManagedTile> {
		TileIdComparator idComparator;
		
		public ManagedTileComparator(boolean yFirst, boolean xDescending, boolean yDescending) {
			idComparator = new TileIdComparator(yFirst, xDescending, yDescending);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ManagedTileComparator other = (ManagedTileComparator) obj;
			return idComparator.equals(other.idComparator);
		}

		@Override
		public int compare(ManagedTile o1, ManagedTile o2) {
			return idComparator.compare(o1.id, o2.id);
		}
	}
	
	private static class TileGroup {
		final List<ManagedTile> tiles = new ArrayList<>();
		ManagedTileComparator comparator = null;
		
		public void add(ManagedTile tile) {
			tiles.add(tile);
			// ensure resort
			comparator = null;
		}

		public Iterable<ManagedTile> iterable(ManagedTileComparator comparator) {
			if (comparator != this.comparator) {
				this.comparator = comparator;
				if (comparator != null) tiles.sort(comparator);
			}
			return tiles;
		}
	}
}

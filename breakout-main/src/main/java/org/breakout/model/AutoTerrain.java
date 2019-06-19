package org.breakout.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.JoglResource;
import org.andork.math3d.Clip3f;
import org.andork.nativewindow.util.PixelRectangles;
import org.andork.spatial.Rectmath;
import org.andork.util.ArrayUtils;
import org.breakout.mabox.MapboxClient;
import org.breakout.mabox.MapboxClient.ImageTileFormat;
import org.breakout.mabox.Tilebelt;
import org.breakout.model.TerrainTile.PaintOrder;
import org.breakout.model.shader.TerrainProgram;
import org.breakout.proj4.Proj4Utils;
import org.breakout.proj4.WebMercatorProjection;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.Datum;

import com.jogamp.common.nio.Buffers;
import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.nativewindow.util.DimensionImmutable;
import com.jogamp.nativewindow.util.PixelFormat;
import com.jogamp.nativewindow.util.PixelRectangle;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.PNGPixelRect;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
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
			gl.glBlendFunc(GL3.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glBlendEquation(GL3.GL_FUNC_ADD);
	
			program.putMatrices(gl, context.projectionMatrix(), context.viewMatrix(), m, n);
			program.position.enableArray(gl);
			program.normal.enableArray(gl);
			program.texcoord.enableArray(gl);
			program.lightDirection.put(gl, 0, 1, 1);
			program.ambient.put(gl, 0.5f);
			program.alpha.put(gl, 0.5f);
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

	float[] fullMbr = Rectmath.voidRectf(3);
	
	public float[] getFullMbr() {
		if (Rectmath.isVoid(fullMbr)) {
			for (TileGroup group : tileGroups.values()) {
				for (ManagedTile tile : group.tiles) {
					Rectmath.union3(fullMbr, tile.tile.mbr(), fullMbr);
				}
			}
		}
		return fullMbr;
	}
	
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
		
		if (!newTiles.isEmpty()) {
			Rectmath.makeVoid(fullMbr);
		}
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
		CoordinateReferenceSystem geographic = coordinateReferenceSystem.createGeographic();
		CoordinateTransform toGeographic = new BasicCoordinateTransform(coordinateReferenceSystem, geographic);
		CoordinateTransform fromGeographic = new BasicCoordinateTransform(geographic, coordinateReferenceSystem);
		ProjCoordinate min = Proj4Utils.convert(toGeographic, new ProjCoordinate(mbr[0], -mbr[2], mbr[1]));
		ProjCoordinate max = Proj4Utils.convert(toGeographic, new ProjCoordinate(mbr[3], -mbr[5], mbr[4]));

		
		int zoom;
		long[] minTile = null;
		long[] maxTile = null;
		long maxNumTiles = 64;
		for (zoom = 15; zoom >= 7; zoom--) {
			minTile = Tilebelt.pointToTile(min.x, min.y, zoom);
			maxTile = Tilebelt.pointToTile(max.x, max.y, zoom);
			long numTiles = (maxTile[0] - minTile[0] + 1) * (maxTile[1] - minTile[1] + 1);
			if (numTiles <= maxNumTiles) break;
		}
		
		if (maxTile[0] - minTile[0] < maxTile[1] - minTile[1]) {
			while (maxTile[0] - minTile[0] < maxTile[1] - minTile[1]) {
				long moreNumTiles = (maxTile[0] - minTile[0] + 3) * (maxTile[1] - minTile[1] + 1);
				if (moreNumTiles > maxNumTiles) break;
				minTile[0]--;
				maxTile[0]++;
			}
		}
	
		while (maxTile[1] - minTile[1] < maxTile[0] - minTile[0]) {
			long moreNumTiles = (maxTile[1] - minTile[1] + 3) * (maxTile[0] - minTile[0] + 1);
			if (moreNumTiles > maxNumTiles) break;
			minTile[1]--;
			maxTile[1]++;
		}

		
		while ((maxTile[0] - minTile[0] + 2) * (maxTile[1] - minTile[1] + 1) < maxNumTiles ||
				(maxTile[0] - minTile[0] + 1) * (maxTile[1] - minTile[1] + 2) < maxNumTiles) {
			if ((maxTile[0] - minTile[0] + 3) * (maxTile[1] - minTile[1] + 1) < maxNumTiles) {
				minTile[0]--;
				maxTile[0]++;
			}
			else if ((maxTile[0] - minTile[0] + 2) * (maxTile[1] - minTile[1] + 1) < maxNumTiles) {
				minTile[0]--;
			}
			if ((maxTile[0] - minTile[0] + 1) * (maxTile[1] - minTile[1] + 3) < maxNumTiles) {
				minTile[1]--;
				maxTile[1]++;
			}
			else if ((maxTile[0] - minTile[0] + 1) * (maxTile[1] - minTile[1] + 2) < maxNumTiles) {
				minTile[1]--;
			}
		}

		double[][] cornerBBoxes = {
			Tilebelt.tileToBBox(minTile),
			Tilebelt.tileToBBox(new long[] {maxTile[0], minTile[1], zoom}),
			Tilebelt.tileToBBox(new long[] {minTile[0], maxTile[1], zoom}),
			Tilebelt.tileToBBox(maxTile),
		};
		
		ProjCoordinate[] cornerCoords = {
			new ProjCoordinate(cornerBBoxes[0][0], cornerBBoxes[0][3], 0),
			new ProjCoordinate(cornerBBoxes[1][2], cornerBBoxes[1][3], 0),
			new ProjCoordinate(cornerBBoxes[2][0], cornerBBoxes[2][1], 0),
			new ProjCoordinate(cornerBBoxes[3][2], cornerBBoxes[3][1], 0),
		};
		for (ProjCoordinate corner : cornerCoords) {
			fromGeographic.transform(corner, corner);
		}		
		
		corners = ArrayUtils.map(cornerCoords, new float[4][], coord -> new float[] {
			(float) coord.x, (float) coord.z, (float) -coord.y
		});
		
		for (long x = minTile[0]; x <= maxTile[0]; x++) {
			for (long y = minTile[1]; y <= maxTile[1]; y++) {
				tiles.add(new long[] {x, y, zoom});
			}
		}
		
		for (long[] tileId : tiles) {
			GLProfile profile = gl.getGLProfile();
			fetchService.submit(() -> {
				try {
					TextureData textureData = TextureIO.getTextureData(profile, getTileData(MapboxClient.SATELLITE, tileId, false));
					PixelRectangle terrain = getTileData(MapboxClient.TERRAIN_RGB, tileId, false);

					autoDrawable.invoke(true, drawable -> {
						try {
							Texture satellite = TextureIO.newTexture(textureData);
							TerrainTile terrainTile = new TerrainTile(terrain,
								new CoordinateConverter(terrain.getSize().getWidth() - 1, tileId));
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
		Rectmath.makeVoid(fullMbr);
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
	
	private ManagedTileComparator tileComparator = null;
	
	private PaintOrder paintOrder = null;
	private PaintOrder nextPaintOrder = new PaintOrder();
	
	private boolean calcOrder(JoglDrawContext context) {
		if (nextPaintOrder.compute(context, corners).equals(paintOrder)) return false;
		
		if (paintOrder == null) paintOrder = new PaintOrder();
		PaintOrder swap = paintOrder;
		paintOrder = nextPaintOrder;
		nextPaintOrder = swap;
		
		tileComparator = new ManagedTileComparator(
			paintOrder.rowsFirst, paintOrder.colsDescending, paintOrder.rowsDescending);
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
	
	public final PixelRectangle getTileData(String mapId, long[] tileId, boolean highDpi) throws IOException {
        PNGPixelRect main = PNGPixelRect.read(mapbox.getTileStream(
				mapId, tileId, highDpi, ImageTileFormat.PNGRAW), null, true /* directBuffer */, 0 /* destMinStrideInBytes */, true /* destIsGLOriented */);
        tileId[0]++;
        PNGPixelRect right = PNGPixelRect.read(mapbox.getTileStream(
				mapId, tileId, highDpi, ImageTileFormat.PNGRAW), null, true /* directBuffer */, 0 /* destMinStrideInBytes */, true /* destIsGLOriented */);
        tileId[1]++;
        PNGPixelRect belowRight = PNGPixelRect.read(mapbox.getTileStream(
				mapId, tileId, highDpi, ImageTileFormat.PNGRAW), null, true /* directBuffer */, 0 /* destMinStrideInBytes */, true /* destIsGLOriented */);
        tileId[0]--;
        PNGPixelRect below = PNGPixelRect.read(mapbox.getTileStream(
				mapId, tileId, highDpi, ImageTileFormat.PNGRAW), null, true /* directBuffer */, 0 /* destMinStrideInBytes */, true /* destIsGLOriented */);
        tileId[1]--;
        
        DimensionImmutable size = main.getSize();
        
        int newWidth = size.getWidth() + 1;
        int newHeight = size.getHeight() + 1;
        
        PixelFormat format = main.getPixelformat();
        int newStride = format.comp.bytesPerPixel() * newWidth;

        final ByteBuffer destPixels = Buffers.newDirectByteBuffer(newStride * newHeight);
        
        PixelRectangle result = new PixelRectangle.GenericPixelRect(main.getPixelformat(),
        	new Dimension(newWidth, newHeight),
        	newStride,
        	main.isGLOriented(),
        	destPixels);
        
        PixelRectangles.copy(main, 0, 0, result, 0, 0, size.getWidth(), size.getHeight());
        PixelRectangles.copy(right, 0, 0, result, size.getWidth(), 0, 1, size.getHeight());
        PixelRectangles.copy(belowRight, 0, 0, result, size.getWidth(), size.getHeight(), 1, 1);
        PixelRectangles.copy(below, 0, 0, result, 0, size.getHeight(), size.getWidth(), 1);
	
        return result;
	}
}

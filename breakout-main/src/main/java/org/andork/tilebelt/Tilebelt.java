package org.andork.tilebelt;

public class Tilebelt {

	private Tilebelt() {
	}
	
	static final double d2r = Math.PI / 180;
	static final double r2d = 180 / Math.PI;


	/**
	 * Get the bbox of a tile
	 *
	 * @name tileToBBOX
	 * @param {Array<number>} tile
	 * @returns {Array<number>} bbox
	 * @example
	 * var bbox = tileToBBOX([5, 10, 10])
	 * //=bbox
	 */
	public static double[] tileToBBox(long[] tile) {
		double e = tile2lon(tile[0] + 1, tile[2]);
		double w = tile2lon(tile[0], tile[2]);
		double s = tile2lat(tile[1] + 1, tile[2]);
		double n = tile2lat(tile[1], tile[2]);
		return new double[] {w, s, e, n};
	}
	

	public static double tile2lon(long x, long z) {
		return x / Math.pow(2, z) * 360 - 180;
	}

	public static double tile2lat(long y, long z) {
		double n = Math.PI - 2 * Math.PI * y / Math.pow(2, z);
		return r2d * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
	}
	

	/**
	 * Get the tile for a point at a specified zoom level
	 *
	 * @name pointToTile
	 * @param {number} lon
	 * @param {number} lat
	 * @param {number} z
	 * @returns {Array<number>} tile
	 * @example
	 * var tile = pointToTile(1, 1, 20)
	 * //=tile
	 */
	public static long[] pointToTile(double lon, double lat, double z) {
		double[] tile = pointToTileFraction(lon, lat, z);
		tile[0] = Math.floor(tile[0]);
		tile[1] = Math.floor(tile[1]);
		return new long[] {
			(long)Math.floor(tile[0]),
			(long)Math.floor(tile[1]), (long)tile[2]};
	}
	

	/**
	 * Get the precise fractional tile location for a point at a zoom level
	 *
	 * @name pointToTileFraction
	 * @param {number} lon
	 * @param {number} lat
	 * @param {number} z
	 * @returns {Array<number>} tile fraction
	 * var tile = pointToTileFraction(30.5, 50.5, 15)
	 * //=tile
	 */
	public static double[] pointToTileFraction(double lon, double lat, double z) {
		double sin = Math.sin(lat * d2r),
			z2 = Math.pow(2, z),
			x = z2 * (lon / 360 + 0.5),
			y = z2 * (0.5 - 0.25 * Math.log((1 + sin) / (1 - sin)) / Math.PI);

		// Wrap Tile X
		x %= z2;
		if (x < 0) x += z2;
		return new double[] {x, y, z};
	}

	/**
	 * Get the 4 tiles one zoom level higher
	 *
	 * @name getChildren
	 * @param {Array<number>} tile
	 * @returns {Array<Array<number>>} tiles
	 * @example
	 * var tiles = getChildren([5, 10, 10])
	 * //=tiles
	 */
	public static long[][] getChildren(long[] tile) {
		return new long[][] {
			{tile[0] * 2, tile[1] * 2, tile[2] + 1},
			{tile[0] * 2 + 1, tile[1] * 2, tile[2 ] + 1},
			{tile[0] * 2 + 1, tile[1] * 2 + 1, tile[2] + 1},
			{tile[0] * 2, tile[1] * 2 + 1, tile[2] + 1}
		};
	}

	/**
	 * Get the tile one zoom level lower
	 *
	 * @name getParent
	 * @param {Array<number>} tile
	 * @returns {Array<number>} tile
	 * @example
	 * var tile = getParent([5, 10, 10])
	 * //=tile
	 */
	public static long[] getParent(long[] tile) {
		return new long[] {tile[0] >> 1, tile[1] >> 1, tile[2] - 1};
	}

	public static long[][] getSiblings(long[] tile) {
		return getChildren(getParent(tile));
	}

	/**
	 * Get the 3 sibling tiles for a tile
	 *
	 * @name getSiblings
	 * @param {Array<number>} tile
	 * @returns {Array<Array<number>>} tiles
	 * @example
	 * var tiles = getSiblings([5, 10, 10])
	 * //=tiles
	 */
	public static boolean hasSiblings(long[] tile, long[][] tiles) {
		long[][] siblings = getSiblings(tile);
		for (int i = 0; i < siblings.length; i++) {
			if (!hasTile(tiles, siblings[i])) return false;
		}
		return true;
	}

	/**
	 * Check to see if an array of tiles contains a particular tile
	 *
	 * @name hasTile
	 * @param {Array<Array<number>>} tiles
	 * @param {Array<number>} tile
	 * @returns {boolean}
	 * @example
	 * var tiles = [
	 *     [0, 0, 5],
	 *     [0, 1, 5],
	 *     [1, 1, 5],
	 *     [1, 0, 5]
	 * ]
	 * hasTile(tiles, [0, 0, 5])
	 * //=boolean
	 */
	public static boolean hasTile(long[][] tiles, long[] tile) {
		for (int i = 0; i < tiles.length; i++) {
			if (tilesEqual(tiles[i], tile)) return true;
		}
		return false;
	}

	/**
	 * Check to see if two tiles are the same
	 *
	 * @name tilesEqual
	 * @param {Array<number>} tile1
	 * @param {Array<number>} tile2
	 * @returns {boolean}
	 * @example
	 * tilesEqual([0, 1, 5], [0, 0, 5])
	 * //=boolean
	 */
	public static boolean tilesEqual(long[] tile1, long[] tile2) {
		return (
			tile1[0] == tile2[0] &&
			tile1[1] == tile2[1] &&
			tile1[2] == tile2[2]
		);
	}


	/**
	 * Get the smallest tile to cover a bbox
	 *
	 * @name bboxToTile
	 * @param {Array<number>} bbox
	 * @returns {Array<number>} tile
	 * @example
	 * var tile = bboxToTile([ -178, 84, -177, 85 ])
	 * //=tile
	 */
	public static long[] bboxToTile(double[] bboxCoords) {
		long[] min = pointToTile(bboxCoords[0], bboxCoords[1], 32);
		long[] max = pointToTile(bboxCoords[2], bboxCoords[3], 32);
		long[] bbox = {min[0], min[1], max[0], max[1]};

		long z = getBBoxZoom(bbox);
		if (z == 0) return new long[] {0, 0, 0};
		long x = bbox[0] >>> (32 - z);
		long y = bbox[1] >>> (32 - z);
		return new long[] {x, y, z};
	}

	static long getBBoxZoom(long[] bbox) {
		long MAX_ZOOM = 28;
		for (int z = 0; z < MAX_ZOOM; z++) {
			long mask = 1 << (32 - (z + 1));
			if (((bbox[0] & mask) != (bbox[2] & mask)) ||
				((bbox[1] & mask) != (bbox[3] & mask))) {
				return z;
			}
		}

		return MAX_ZOOM;
	}
}

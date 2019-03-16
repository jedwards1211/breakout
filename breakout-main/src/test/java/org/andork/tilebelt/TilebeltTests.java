package org.andork.tilebelt;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TilebeltTests {
	private static final long[] tile1 = {5, 10, 10};
	
	@Test
	public void testTileToBbox() {
	    double[] ext = Tilebelt.tileToBBox(tile1);
	    assertArrayEquals(
	    	new double[] {-178.2421875, 84.7060489350415, -177.890625, 84.73838712095339},
	    	ext,
	    	0.0);
	}
	
	@Test
	public void testGetParent() {
	    long[] parent = Tilebelt.getParent(tile1);
	    assertArrayEquals(
	    	new long[] {2, 5, 9},
	    	parent);
	}
	
	@Test
	public void testGetSiblings() {
	    long[][] siblings = Tilebelt.getSiblings(tile1);
	    assertArrayEquals(new long[][] {
	    	{4, 10, 10}, {5, 10, 10}, {5, 11, 10}, {4, 11, 10}
	    }, siblings);
	}
	
	@Test
	public void testHasSiblings() {
	    long[][] tiles1 = {
			{0, 0, 5},
			{0, 1, 5},
			{1, 1, 5},
			{1, 0, 5}
		};
		long[][] tiles2 = {
			{0, 0, 5},
			{0, 1, 5},
			{1, 1, 5}
		};

	    assertTrue(Tilebelt.hasSiblings(new long[] {0, 0, 5}, tiles1));
	    assertTrue(Tilebelt.hasSiblings(new long[] {0, 1, 5}, tiles1));
	    assertFalse(Tilebelt.hasSiblings(new long[] {0, 0, 5}, tiles2));
	    assertFalse(Tilebelt.hasSiblings(new long[] {0, 0, 5}, tiles2));
	}
	
	@Test
	public void testpointToTile() {
	    long[] tile = Tilebelt.pointToTile(-77.03239381313323, 38.91326516559442, 10);
	    assertArrayEquals(new long[] {292, 391, 10}, tile);
	}
	
	@Test
	public void testBBoxToTileBig() {
	    double[] bbox = {-84.72656249999999,
	                11.178401873711785,
	                -5.625,
	                61.60639637138628};
	    long[] tile = Tilebelt.bboxToTile(bbox);
	    assertArrayEquals(new long[] {1, 1, 2}, tile);
	}
	
	@Test
	public void testBBoxToTileNoArea() {
	    double[] bbox = {-84,
	                11,
	                -84,
	                11};
	    long[] tile = Tilebelt.bboxToTile(bbox);
	    assertArrayEquals(new long[] {71582788, 125964677, 28}, tile);
	}
	
	@Test
	public void testBBoxToTileDC() {
	    double[] bbox = {-77.04615354537964,
	                 38.899967510782346,
	                 -77.03664779663086,
	                 38.90728142481329};
	     long[] tile = Tilebelt.bboxToTile(bbox);
	     assertArrayEquals(new long[] {9371, 12534, 15}, tile);
	}
	
	@Test
	public void testTileToBBoxOrder() {
	    long[] tile =  {13, 11, 5};
	    double[] bbox = Tilebelt.tileToBBox(tile);
	    assertTrue("east is less than west", bbox[0] < bbox[2]);
	    assertTrue("south is less than north", bbox[1] < bbox[3]);

	    tile = new long[] {20, 11, 5};
	    bbox = Tilebelt.tileToBBox(tile);
	    assertTrue("east is less than west", bbox[0] < bbox[2]);
	    assertTrue("south is less than north", bbox[1] < bbox[3]);

	    tile = new long[] {143, 121, 8};
	    bbox = Tilebelt.tileToBBox(tile);
	    assertTrue("east is less than west", bbox[0] < bbox[2]);
	    assertTrue("south is less than north", bbox[1] < bbox[3]);

	    tile = new long[] {999, 1000, 17};
	    bbox = Tilebelt.tileToBBox(tile);
	    assertTrue("east is less than west", bbox[0] < bbox[2]);
	    assertTrue("south is less than north", bbox[1] < bbox[3]);
	}
	
	@Test
	public void testpointToTileFraction() {
	    double[] tile = Tilebelt.pointToTileFraction(-95.93965530395508, 41.26000108568697, 9);
	    assertArrayEquals(new double[] {119.552490234375, 191.47119140625, 9}, tile, 0.0);
	}
	
	@Test
	public void testpointToTileCrossMeridian() {
	    // X axis
	    // https://github.com/mapbox/tile-cover/issues/75
	    // https://github.com/mapbox/tilebelt/pull/32
	    assertArrayEquals("[-180, 0] zoom 0", new long[] {0, 0, 0}, Tilebelt.pointToTile(-180, 0, 0));
	    assertArrayEquals("[-180, 85] zoom 2", new long[] {0, 0, 2}, Tilebelt.pointToTile(-180, 85, 2));
	    assertArrayEquals("[+180, 85] zoom 2", new long[] {0, 0, 2}, Tilebelt.pointToTile(180, 85, 2));
	    assertArrayEquals("[-185, 85] zoom 2", new long[] {3, 0, 2}, Tilebelt.pointToTile(-185, 85, 2));
	    assertArrayEquals("[+185, 85] zoom 2", new long[] {0, 0, 2}, Tilebelt.pointToTile(185, 85, 2));

	    // Y axis
	    // Does not wrap Tile Y
	    assertArrayEquals("[-175, -95] zoom 2", new long[] {0, 3, 2}, Tilebelt.pointToTile(-175, -95, 2));
	    assertArrayEquals("[-175, +95] zoom 2", new long[] {0, 0, 2}, Tilebelt.pointToTile(-175, 95, 2));
	    assertArrayEquals("[-175, +95] zoom 2", new long[] {0, 0, 2}, Tilebelt.pointToTile(-175, 95, 2));

	    // BBox
	    // https://github.com/mapbox/tilebelt/issues/12
	    assertArrayEquals(new long[] {0, 0, 0}, Tilebelt.bboxToTile(new double[] {-0.000001, -85, 1000000, 85}));
	}
}

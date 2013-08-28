package org.andork.torquescape;

import java.util.ArrayList;
import java.util.List;

public class TorquescapeScene {
	public final List<ZoneRenderer> zoneRenderers = new ArrayList<ZoneRenderer>();

	public void draw(float[] mvMatrix, float[] pMatrix) {
		for (ZoneRenderer renderer : zoneRenderers) {
			renderer.draw(mvMatrix, pMatrix);
		}
	}
}

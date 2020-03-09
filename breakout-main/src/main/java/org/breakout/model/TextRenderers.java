package org.breakout.model;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.util.awt.TextRenderer;

public class TextRenderers {
	private static Map<Font, TextRenderer> renderers = new HashMap<Font, TextRenderer>();

	public static synchronized TextRenderer textRenderer(Font font) {
		TextRenderer renderer = renderers.get(font);
		if (renderer == null) {
			renderer = new TextRenderer(font, true, true, null, false);
			renderers.put(font, renderer);
		}
		renderer.init();
		renderer.setUseVertexArrays(true);
		return renderer;
	}
}

package org.andork.jogl.awt;

import java.awt.Graphics2D;

public interface GlyphPagePainter
{
	public void drawGlyphs( Graphics2D g , char startChar , int rows , int cols , int cellHeight , int cellWidth , int cellBaseline );
}

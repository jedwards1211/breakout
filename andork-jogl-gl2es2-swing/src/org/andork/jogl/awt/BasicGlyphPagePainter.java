package org.andork.jogl.awt;

import java.awt.Graphics2D;

public class BasicGlyphPagePainter implements GlyphPagePainter
{
	public void drawGlyphs( Graphics2D g , char startChar , int rows , int cols , int cellHeight , int cellWidth , int cellBaseline )
	{
		char[ ] chars = { startChar };
		int y = cellBaseline;
		for( int row = 0 ; row < rows ; row++ , y += cellHeight )
		{
			int x = 0;
			for( int col = 0 ; col < cols ; col++ , x += cellWidth , chars[ 0 ]++ )
			{
				g.drawChars( chars , 0 , 1 , x , y );
			}
		}
	}
}

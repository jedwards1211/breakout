
package com.andork.plot;
import java.awt.Graphics2D;
import java.awt.Rectangle;


public interface IPlotLayer
{
	public void render( Graphics2D g2 , Rectangle bounds );
}

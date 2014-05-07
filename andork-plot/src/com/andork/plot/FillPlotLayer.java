package com.andork.plot;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

public class FillPlotLayer implements IPlotLayer
{
	Paint	paint;
	
	public FillPlotLayer( Paint paint )
	{
		super( );
		this.paint = paint;
	}
	
	@Override
	public void render( Graphics2D g2 , Rectangle bounds )
	{
		g2.setPaint( paint );
		g2.fillRect( bounds.x , bounds.y , bounds.width , bounds.height );
	}
}

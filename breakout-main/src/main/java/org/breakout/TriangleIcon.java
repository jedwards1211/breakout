package org.breakout;

import static javax.swing.SwingConstants.EAST;
import static javax.swing.SwingConstants.NORTH;
import static javax.swing.SwingConstants.SOUTH;
import static javax.swing.SwingConstants.WEST;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.UIManager;

public class TriangleIcon implements Icon {

	public TriangleIcon(int direction) {
		this(direction, 5);
	}
	public TriangleIcon(int direction, int size) {
		this(direction, size,
			UIManager.getColor("controlShadow"),
			UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"));
	}
	public TriangleIcon(int direction, int size, Color shadow, Color darkShadow, Color highlight) {
		super();
		this.direction = direction;
		this.size = size;
		this.shadow = shadow;
		this.darkShadow = darkShadow;
		this.highlight = highlight;
	}

	/**
     * The direction of the arrow. One of
     * {@code SwingConstants.NORTH}, {@code SwingConstants.SOUTH},
     * {@code SwingConstants.EAST} or {@code SwingConstants.WEST}.
     */
    private int direction;
    private int size;

    private Color shadow;
    private Color darkShadow;
    private Color highlight;

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color oldColor = g.getColor();
		int mid, i, j;
		
		boolean isPressed = c instanceof AbstractButton && ((AbstractButton) c).getModel().isPressed();

        if (isPressed) {
            g.translate(1, 1);
        }

		j = 0;
		size = Math.max(size, 2);
		mid = (size / 2) - 1;

		g.translate(x, y);
		if(c.isEnabled())
			g.setColor(darkShadow);
		else
			g.setColor(shadow);

		switch(direction)       {
		case NORTH:
			for(i = 0; i < size; i++)      {
				g.drawLine(mid-i, i, mid+i, i);
			}
			if(!c.isEnabled())  {
				g.setColor(highlight);
				g.drawLine(mid-i+2, i, mid+i, i);
			}
			break;
		case SOUTH:
			if(!c.isEnabled())  {
				g.translate(1, 1);
				g.setColor(highlight);
				for(i = size-1; i >= 0; i--)   {
					g.drawLine(mid-i, j, mid+i, j);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(shadow);
			}

			j = 0;
			for(i = size-1; i >= 0; i--)   {
				g.drawLine(mid-i, j, mid+i, j);
				j++;
			}
			break;
		case WEST:
			for(i = 0; i < size; i++)      {
				g.drawLine(i, mid-i, i, mid+i);
			}
			if(!c.isEnabled())  {
				g.setColor(highlight);
				g.drawLine(i, mid-i+2, i, mid+i);
			}
			break;
		case EAST:
			if(!c.isEnabled())  {
				g.translate(1, 1);
				g.setColor(highlight);
				for(i = size-1; i >= 0; i--)   {
					g.drawLine(j, mid-i, j, mid+i);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(shadow);
			}

			j = 0;
			for(i = size-1; i >= 0; i--)   {
				g.drawLine(j, mid-i, j, mid+i);
				j++;
			}
			break;
		}
		g.translate(-x, -y);
		g.setColor(oldColor);
        if (isPressed) {
            g.translate(-1, -1);
        }
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

}

/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/

package com.andork.plot;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.lang.ref.WeakReference;

class CheckerPaintContext implements PaintContext {
	static ColorModel xrgbmodel = new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);
	static ColorModel xbgrmodel = new DirectColorModel(24, 0x000000ff, 0x0000ff00, 0x00ff0000);

	static ColorModel cachedModel;
	static WeakReference cached;

	static synchronized Raster getCachedRaster(ColorModel cm, int w, int h) {
		if (cm == cachedModel) {
			if (cached != null) {
				Raster ras = (Raster) cached.get();
				if (ras != null &&
						ras.getWidth() >= w &&
						ras.getHeight() >= h) {
					cached = null;
					return ras;
				}
			}
		}
		return cm.createCompatibleWritableRaster(w, h);
	}

	static synchronized void putCachedRaster(ColorModel cm, Raster ras) {
		if (cached != null) {
			Raster cras = (Raster) cached.get();
			if (cras != null) {
				int cw = cras.getWidth();
				int ch = cras.getHeight();
				int iw = ras.getWidth();
				int ih = ras.getHeight();
				if (cw >= iw && ch >= ih) {
					return;
				}
				if (cw * ch >= iw * ih) {
					return;
				}
			}
		}
		cachedModel = cm;
		cached = new WeakReference(ras);
	}

	Color c1;
	Color c2;
	int size;
	Raster saved;
	ColorModel model;

	public CheckerPaintContext(ColorModel cm, Color c1, Color c2, int size) {
		model = cm;
		this.c1 = c1;
		this.c2 = c2;
		this.size = size;
	}

	/**
	 * Release the resources allocated for the operation.
	 */
	@Override
	public void dispose() {
		if (saved != null) {
			putCachedRaster(model, saved);
			saved = null;
		}
	}

	void fillRaster(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
		int ic1 = c1.getRGB();
		int ic2 = c2.getRGB();
		int c = y / size % 2 == 0 ^ x / size % 2 == 0 ? ic1 : ic2;

		int initRx = size - x % size;
		int ry = size - y % size;

		while (h-- > 0) {
			int rx = initRx;
			int cc = c;
			for (int ix = 0; ix < w; ix++) {
				pixels[off++] = c;
				if (--rx == 0) {
					rx = size;
					c ^= ic1;
					c ^= ic2;
				}
			}
			off += adjust;

			c = cc;

			if (--ry == 0) {
				ry = size;
				c ^= ic1;
				c ^= ic2;
			}
		}
	}

	/**
	 * Return the ColorModel of the output.
	 */
	@Override
	public ColorModel getColorModel() {
		return model;
	}

	/**
	 * Return a Raster containing the colors generated for the graphics
	 * operation.
	 *
	 * @param x
	 *            ,y,w,h The area in device space for which colors are
	 *            generated.
	 */
	@Override
	public Raster getRaster(int x, int y, int w, int h) {
		Raster rast = saved;
		if (rast == null || rast.getWidth() < w || rast.getHeight() < h) {
			rast = getCachedRaster(model, w, h);
			saved = rast;
		}
		DataBufferInt rasterDB = (DataBufferInt) rast.getDataBuffer();
		int[] pixels = rasterDB.getBankData()[0];
		int off = rasterDB.getOffset();
		int scanlineStride = ((SinglePixelPackedSampleModel) rast.getSampleModel()).getScanlineStride();
		int adjust = scanlineStride - w;

		fillRaster(pixels, off, adjust, x, y, w, h);

		return rast;
	}
}

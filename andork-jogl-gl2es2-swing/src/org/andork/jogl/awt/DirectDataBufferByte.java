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
package org.andork.jogl.awt;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * {@link DataBuffer} specialization using NIO direct buffer of type
 * {@link DataBuffer#TYPE_INT} as storage.
 */
public final class DirectDataBufferByte extends DataBuffer {

	public static class BufferedImageByte extends BufferedImage {
		final int customImageType;

		public BufferedImageByte(int customImageType, ColorModel cm, WritableRaster raster,
				Hashtable<?, ?> properties) {
			super(cm, raster, false /* isRasterPremultiplied */, properties);
			this.customImageType = customImageType;
		}

		/**
		 * @return one of the custom image-type values
		 *         {@link BufferedImage#TYPE_INT_ARGB TYPE_INT_ARGB},
		 *         {@link BufferedImage#TYPE_INT_ARGB_PRE TYPE_INT_ARGB_PRE},
		 *         {@link BufferedImage#TYPE_INT_RGB TYPE_INT_RGB} or
		 *         {@link BufferedImage#TYPE_INT_BGR TYPE_INT_BGR}.
		 */
		public int getCustomType() {
			return customImageType;
		}

		@Override
		public String toString() {
			return new String("BufferedImageByte@" + Integer.toHexString(hashCode())
					+ ": custum/internal type = " + customImageType + "/" + getType()
					+ " " + getColorModel() + " " + getRaster());
		}
	}

	public static class DirectWritableRaster extends WritableRaster {
		protected DirectWritableRaster(SampleModel sampleModel, DirectDataBufferByte dataBuffer, Point origin) {
			super(sampleModel, dataBuffer, origin);
		}
	}

	/**
	 * Creates a {@link BufferedImageByte} using a {@link DirectColorModel
	 * direct color model} in {@link ColorSpace#CS_sRGB sRGB color space}.<br>
	 * It uses a {@link DirectWritableRaster} utilizing
	 * {@link DirectDataBufferByte} storage.
	 * <p>
	 * Note that due to using the custom storage type
	 * {@link DirectDataBufferByte}, the resulting {@link BufferedImage}'s
	 * {@link BufferedImage#getType() image-type} is of
	 * {@link BufferedImage#TYPE_CUSTOM TYPE_CUSTOM}. We are not able to change
	 * this detail, since the AWT image implementation associates the
	 * {@link BufferedImage#getType() image-type} with a build-in storage-type.
	 * Use {@link BufferedImageByte#getCustomType()} to retrieve the custom
	 * image-type, which will return the <code>imageType</code> value passed
	 * here.
	 * </p>
	 *
	 * @param width
	 * @param height
	 * @param imageType
	 *            one of {@link BufferedImage#TYPE_INT_ARGB TYPE_INT_ARGB},
	 *            {@link BufferedImage#TYPE_INT_ARGB_PRE TYPE_INT_ARGB_PRE},
	 *            {@link BufferedImage#TYPE_INT_RGB TYPE_INT_RGB} or
	 *            {@link BufferedImage#TYPE_INT_BGR TYPE_INT_BGR}.
	 * @param location
	 *            origin, if <code>null</code> 0/0 is assumed.
	 * @param properties
	 *            <code>Hashtable</code> of <code>String</code>/
	 *            <code>Object</code> pairs. Used for
	 *            {@link BufferedImage#getProperty(String)} etc.
	 * @return
	 */
	public static BufferedImageByte createBufferedImage(int width, int height, int imageType, Point location,
			Hashtable<?, ?> properties) {
		final int[] bandOffsets = new int[imageType];
		for (int i = 0; i < imageType; i++) {
			bandOffsets[i] = i;
		}
		ColorModel colorModel;
		SampleModel sampleModel;
		switch (imageType) {
		case BufferedImage.TYPE_BYTE_GRAY:
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			int[] nBits = new int[] { 8 };
			colorModel = new ComponentColorModel(cs, nBits, false, true,
					Transparency.OPAQUE,
					DataBuffer.TYPE_BYTE);
			sampleModel = new BandedSampleModel(TYPE_BYTE, width, height, 1);
			break;
		default:
			throw new IllegalArgumentException("Unsupported imageType, must be [BYTE_GRAY], has " + imageType);
		}

		final DirectDataBufferByte dataBuffer = new DirectDataBufferByte(width * height);
		if (null == location) {
			location = new Point(0, 0);
		}
		final WritableRaster raster = new DirectWritableRaster(sampleModel, dataBuffer, location);

		return new BufferedImageByte(imageType, colorModel, raster, properties);
	}

	/** Default data bank. */
	private ByteBuffer data;

	/** All data banks */
	private ByteBuffer bankdata[];

	/**
	 * Constructs an nio integer-based {@link DataBuffer} with a single bank
	 * using the specified array.
	 * <p>
	 * Only the first <code>size</code> elements should be used by accessors of
	 * this {@link DataBuffer}. <code>dataArray</code> must be large enough to
	 * hold <code>size</code> elements.
	 * </p>
	 *
	 * @param dataArray
	 *            The integer array for the {@link DataBuffer}.
	 * @param size
	 *            The size of the {@link DataBuffer} bank.
	 */
	public DirectDataBufferByte(ByteBuffer dataArray, int size) {
		super(TYPE_BYTE, size);
		data = dataArray;
		bankdata = new ByteBuffer[1];
		bankdata[0] = data;
	}

	/**
	 * Constructs an nio integer-based {@link DataBuffer} with a single bank and
	 * the specified size.
	 *
	 * @param size
	 *            The size of the {@link DataBuffer}.
	 */
	public DirectDataBufferByte(int size) {
		super(TYPE_BYTE, size);
		data = ByteBuffer.allocateDirect(size);
		bankdata = new ByteBuffer[1];
		bankdata[0] = data;
	}

	/**
	 * Constructs an nio integer-based {@link DataBuffer} with the specified
	 * number of banks, all of which are the specified size.
	 *
	 * @param size
	 *            The size of the banks in the {@link DataBuffer}.
	 * @param numBanks
	 *            The number of banks in the a{@link DataBuffer}.
	 */
	public DirectDataBufferByte(int size, int numBanks) {
		super(TYPE_BYTE, size, numBanks);
		bankdata = new ByteBuffer[numBanks];
		for (int i = 0; i < numBanks; i++) {
			bankdata[i] = ByteBuffer.allocateDirect(size);
		}
		data = bankdata[0];
	}

	/**
	 * Returns the default (first) int data array in {@link DataBuffer}.
	 *
	 * @return The first integer data array.
	 */
	public ByteBuffer getData() {
		return data;
	}

	/**
	 * Returns the data array for the specified bank.
	 *
	 * @param bank
	 *            The bank whose data array you want to get.
	 * @return The data array for the specified bank.
	 */
	public ByteBuffer getData(int bank) {
		return bankdata[bank];
	}

	/**
	 * Returns the requested data array element from the first (default) bank.
	 *
	 * @param i
	 *            The data array element you want to get.
	 * @return The requested data array element as an integer.
	 * @see #setElem(int, int)
	 * @see #setElem(int, int, int)
	 */
	@Override
	public int getElem(int i) {
		return data.get(i + offset);
	}

	/**
	 * Returns the requested data array element from the specified bank.
	 *
	 * @param bank
	 *            The bank from which you want to get a data array element.
	 * @param i
	 *            The data array element you want to get.
	 * @return The requested data array element as an integer.
	 * @see #setElem(int, int)
	 * @see #setElem(int, int, int)
	 */
	@Override
	public int getElem(int bank, int i) {
		return bankdata[bank].get(i + offsets[bank]);
	}

	/**
	 * Sets the requested data array element in the first (default) bank to the
	 * specified value.
	 *
	 * @param i
	 *            The data array element you want to set.
	 * @param val
	 *            The integer value to which you want to set the data array
	 *            element.
	 * @see #getElem(int)
	 * @see #getElem(int, int)
	 */
	@Override
	public void setElem(int i, int val) {
		data.put(i + offset, (byte) (val & 0xff));
	}

	/**
	 * Sets the requested data array element in the specified bank to the
	 * integer value <code>i</code>.
	 *
	 * @param bank
	 *            The bank in which you want to set the data array element.
	 * @param i
	 *            The data array element you want to set.
	 * @param val
	 *            The integer value to which you want to set the specified data
	 *            array element.
	 * @see #getElem(int)
	 * @see #getElem(int, int)
	 */
	@Override
	public void setElem(int bank, int i, int val) {
		bankdata[bank].put(i + offsets[bank], (byte) (val & 0xff));
	}
}

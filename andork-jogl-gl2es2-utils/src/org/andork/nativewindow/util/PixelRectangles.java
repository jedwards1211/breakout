package org.andork.nativewindow.util;

import java.nio.ByteBuffer;

import com.jogamp.nativewindow.util.PixelFormat;
import com.jogamp.nativewindow.util.PixelRectangle;

public class PixelRectangles {
	// reads a pixel from the image into 32-bit int ARGB8888 format.
	public static int getARGB(PixelRectangle rect, int x, int y) {
        PixelFormat format = rect.getPixelformat();
		ByteBuffer pixels = rect.getPixels();
		int bpp = format.comp.bytesPerPixel();
		if (bpp < 3) {
			throw new UnsupportedOperationException("componentCount < 3 is not currently supported");
		}
		int destStrideInBytes = rect.getStride();
        int dataOff = rect.isGLOriented()
        	? (rect.getSize().getHeight() - 1 - y) * destStrideInBytes
        	: y * destStrideInBytes;
		dataOff += bpp * x;
		int a = (int) pixels.get(dataOff++) & 0xff;
		int b = (int) pixels.get(dataOff++) & 0xff;
		int c = (int) pixels.get(dataOff++) & 0xff;
		int d = bpp >= 4 ? (int) pixels.get(dataOff++)  & 0xff: 0;
		
		return getARGB(format, a, b, c, d);
	}
	
	private static int getARGB(PixelFormat format, int a, int b, int c, int d) {
		if (format == PixelFormat.RGB888) {
			return (a << 16) + (b << 8) + c;
		}
		if (format == PixelFormat.BGR888) {
			return (c << 16) + (b << 8) + a;
		}
		if (format == PixelFormat.ARGB8888) {
			return (a << 24) + (b << 16) + (c << 8) + d;
		}
		if (format == PixelFormat.ABGR8888) {
			return (a << 24) + (d << 16) + (c << 8) + b;
		}
		if (format == PixelFormat.RGBA8888) {
			return (d << 24) + (a << 16) + (b << 8) + c;
		}
		if (format == PixelFormat.BGRA8888) {
			return (d << 24) + (c << 16) + (b << 8) + a;
		}
		throw new UnsupportedOperationException("format " + format.name() + " is not currently supported");
	}
	
	public static void copy(PixelRectangle src, int srcx, int srcy, PixelRectangle dest, int destx, int desty, int width, int height) {
        PixelFormat format = src.getPixelformat();
        if (dest.getPixelformat() != format) {
			throw new UnsupportedOperationException("copying between different pixel formats (src: " + format + ", dest: " + dest.getPixelformat() + " is not currently supported");
        }
		int bpp = format.comp.bytesPerPixel();
		ByteBuffer srcPixels = src.getPixels();
		ByteBuffer destPixels = dest.getPixels();
		int srcStride = src.getStride();
		int destStride = dest.getStride();
		for (int y = 0; y < height; y++) {
			int srcOff = src.isGLOriented()
				? (src.getSize().getHeight() - 1 - (srcy + y)) * srcStride
				: (srcy + y) * srcStride;
			srcOff += srcx * bpp;
			int destOff = dest.isGLOriented()
				? (dest.getSize().getHeight() - 1 - (desty + y)) * destStride
				: (desty + y) * destStride;
			destOff += destx * bpp;
			for (int x = 0; x < width * bpp; x++) {
				destPixels.put(destOff++, srcPixels.get(srcOff++));
			}
		}

	}
}

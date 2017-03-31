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
package org.andork.jogl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class BufferHelper {
	private final ArrayList<Object> result = new ArrayList<>();
	private int sizeInBytes;

	public void clear() {
		result.clear();
	}

	public int count() {
		return result.size();
	}

	public Object get(int offset) {
		return result.get(offset);
	}

	public Object getBackward(int offset) {
		return result.get(result.size() - 1 + offset);
	}

	public BufferHelper put(byte... values) {
		for (byte f : values) {
			result.add(f);
		}
		sizeInBytes += values.length;
		return this;
	}

	public BufferHelper put(char... values) {
		for (char f : values) {
			result.add(f);
		}
		sizeInBytes += values.length * 2;
		return this;
	}

	public BufferHelper put(double... values) {
		for (double f : values) {
			result.add(f);
		}
		sizeInBytes += values.length * 8;
		return this;
	}

	public BufferHelper put(float... values) {
		for (float f : values) {
			result.add(f);
		}
		sizeInBytes += values.length * 4;
		return this;
	}

	public BufferHelper put(int... values) {
		for (int f : values) {
			result.add(f);
		}
		sizeInBytes += values.length * 4;
		return this;
	}

	public BufferHelper put(long... values) {
		for (long f : values) {
			result.add(f);
		}
		sizeInBytes += values.length * 8;
		return this;
	}

	public BufferHelper put(short... values) {
		for (short f : values) {
			result.add(f);
		}
		sizeInBytes += values.length * 2;
		return this;
	}

	public BufferHelper putAsFloats(double... values) {
		for (double d : values) {
			result.add((float) d);
		}
		sizeInBytes += values.length * 4;
		return this;
	}

	public int sizeInBytes() {
		return sizeInBytes;
	}

	public ByteBuffer toByteBuffer() {
		int capacity = 0;

		for (Object n : result) {
			if (n instanceof Byte) {
				capacity++;
			} else if (n instanceof Short || n instanceof Character) {
				capacity += 2;
			} else if (n instanceof Integer || n instanceof Float) {
				capacity += 4;
			} else if (n instanceof Long || n instanceof Double) {
				capacity += 8;
			}
		}

		ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
		buffer.order(ByteOrder.nativeOrder());
		writeTo(buffer);
		buffer.position(0);
		return buffer;
	}

	public void writeTo(ByteBuffer buffer) {
		for (Object n : result) {
			if (n instanceof Byte) {
				buffer.put((Byte) n);
			} else if (n instanceof Short) {
				buffer.putShort((Short) n);
			} else if (n instanceof Character) {
				buffer.putChar((Character) n);
			} else if (n instanceof Integer) {
				buffer.putInt((Integer) n);
			} else if (n instanceof Long) {
				buffer.putLong((Long) n);
			} else if (n instanceof Float) {
				buffer.putFloat((Float) n);
			} else if (n instanceof Double) {
				buffer.putDouble((Double) n);
			} else if (n instanceof Short) {
				buffer.putShort((Short) n);
			}
		}
	}
}

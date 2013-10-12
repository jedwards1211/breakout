package org.andork.torquescape;

import static org.andork.torquescape.GLUtils.checkGlError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andork.torquescape.model.ISlice;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.slice.StandardSlice;

import android.opengl.GLES20;

public class ZoneRenderer
{
	private static Map<Class<? extends ISlice>, ISliceRendererFactory<?>> sliceRendererFactories = new HashMap<Class<? extends ISlice>, ISliceRendererFactory<?>>();

	static
	{
		sliceRendererFactories.put(StandardSlice.class, StandardSliceRenderer.FACTORY);
	}

	public Zone zone;

	public List<ISliceRenderer<?>> sliceRenderers = new ArrayList<ISliceRenderer<?>>();

	public int vertVbo;

	public ZoneRenderer(Zone zone)
	{
		super();
		this.zone = zone;

		for (ISlice slice : zone.slices)
		{
			ISliceRendererFactory<ISlice> rendererFactory = (org.andork.torquescape.ISliceRendererFactory<ISlice>) sliceRendererFactories.get(slice.getClass());
			if (rendererFactory != null)
			{
				sliceRenderers.add(rendererFactory.create(this, slice));
			}
		}
	}

	public void init()
	{
		int[] buffers = new int[1];
		GLES20.glGenBuffers(1, buffers, 0);
		vertVbo = buffers[0];

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertVbo);
		checkGlError("glBindBuffer");
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, zone.vertBuffer.capacity() * 4, zone.vertBuffer, GLES20.GL_STATIC_DRAW);
		checkGlError("glBufferData");
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		for (ISliceRenderer<?> sliceRenderer : sliceRenderers)
		{
			sliceRenderer.init();
		}
	}

	public void draw(float[] mvMatrix, float[] pMatrix) {
		for (ISliceRenderer<?> sliceRenderer : sliceRenderers) {
			sliceRenderer.draw(mvMatrix, pMatrix);
		}
	}
}

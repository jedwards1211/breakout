package org.andork.torquescape;

import org.andork.torquescape.model.ISlice;

public interface ISliceRendererFactory<S extends ISlice>
{
	public ISliceRenderer<S> create( ZoneRenderer zoneRenderer , S slice );
}

package org.andork.torquescape.model.slice;

import org.andork.torquescape.model.AbstractIndexedSlice;
import org.andork.torquescape.model.Zone;

public class RainbowSlice extends AbstractIndexedSlice
{
	public String	coordBufferKey	= Zone.PRIMARY_VERT_BUFFER_KEY;
	public int		coordOffset		= 0;
	public int		coordStride		= -1;
	
	public String	normalBufferKey	= Zone.PRIMARY_VERT_BUFFER_KEY;
	public int		normalOffset	= 12;
	public int		normalStride	= -1;
	
	public String	uBufferKey		= Zone.PRIMARY_VERT_BUFFER_KEY;
	public int		uOffset			= 24;
	public int		uStride			= -1;
	
	public String	vBufferKey		= Zone.PRIMARY_VERT_BUFFER_KEY;
	public int		vOffset			= 36;
	public int		vStride			= -1;
}

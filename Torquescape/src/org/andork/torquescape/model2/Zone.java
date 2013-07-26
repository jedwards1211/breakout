package org.andork.torquescape.model2;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.LinkedHashMultimap;

public class Zone
{
	public float[ ]									verts;
	public FloatBuffer								vertBuffer;
	
	public final LinkedHashMultimap<Point, Integer>	pointToTriMap	= LinkedHashMultimap.create( );
	public final LinkedHashMultimap<Edge, Integer>	edgeToTriMap	= LinkedHashMultimap.create( );
	public final LinkedHashMultimap<Point, Integer>	pointToPointMap	= LinkedHashMultimap.create( );
	public final LinkedHashMultimap<Edge, Integer>	edgeToPointMap	= LinkedHashMultimap.create( );
	
	public final List<ISlice>						slices			= new ArrayList<ISlice>( );
}

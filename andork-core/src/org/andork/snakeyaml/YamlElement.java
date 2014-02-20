package org.andork.snakeyaml;

import org.andork.event.HierarchicalBasicPropertyChangePropagator;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.model.HasChangeSupport;

public abstract class YamlElement implements HasChangeSupport
{
	protected final HierarchicalBasicPropertyChangeSupport		changeSupport	= new HierarchicalBasicPropertyChangeSupport( );
	protected final HierarchicalBasicPropertyChangePropagator	propagator		= new HierarchicalBasicPropertyChangePropagator( this , changeSupport );
	
	public HierarchicalBasicPropertyChangeSupport.External changeSupport( )
	{
		return changeSupport.external( );
	}
	
	public abstract Object toYaml( );
}

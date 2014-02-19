package org.andork.spec.json;

import org.andork.event.HierarchicalBasicPropertyChangePropagator;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.model.Model;

import com.google.gson.JsonElement;

public abstract class JsonSpecElement implements Model {
	protected final HierarchicalBasicPropertyChangeSupport		changeSupport	= new HierarchicalBasicPropertyChangeSupport();
	protected final HierarchicalBasicPropertyChangePropagator	propagator		= new HierarchicalBasicPropertyChangePropagator(this, changeSupport);

	public HierarchicalBasicPropertyChangeSupport.External changeSupport() {
		return changeSupport.external();
	}

	public abstract JsonElement toJson();
}

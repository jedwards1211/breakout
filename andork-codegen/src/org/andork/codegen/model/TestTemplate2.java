package org.andork.codegen.model;

import java.util.List;

import com.schwab.att.core.event.BasicPropertyChangeSupport.External;
import com.schwab.att.core.event.HierarchicalBasicPropertyChangeSupport;
import com.schwab.codegen.Model;

public class TestTemplate2 implements Model {
	List<RootGroup>	rootGroups;

	public class RootGroup implements Model {
		String	rootSymbol;

		@Override
		public External changeSupport() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public HierarchicalBasicPropertyChangeSupport.External changeSupport() {
		// TODO Auto-generated method stub
		return null;
	}
}

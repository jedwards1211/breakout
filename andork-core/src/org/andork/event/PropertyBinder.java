package org.andork.event;

import java.util.Map;

import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.model.Model;

public class PropertyBinder<M extends Model> {
	protected boolean							ignoreChanges	= false;

	protected M									model;

	protected final MultiMap<Object, Binding>	bindings		= LinkedHashSetMultiMap.newInstance();

	protected final ChangeHandler				changeHandler	= new ChangeHandler();

	public M getModel() {
		return model;
	}

	public void setModel(M model) {
		if (this.model != model) {
			if (this.model != null) {
				this.model.changeSupport().removePropertyChangeListener(changeHandler);
			}
			this.model = model;
			if (model != null) {
				model.changeSupport().removePropertyChangeListener(changeHandler);
			}
		}
	}

	public void bind(Binding binding) {
		if (bindings.put(binding.getProperty(), binding)) {
			binding.registerWithView();
		}
	}

	public void unbind(Binding binding) {
		if (bindings.remove(binding.getProperty(), binding)) {
			binding.unregisterFromView();
		}
	}

	public void unbindAll(Object property) {
		for (Binding binding : bindings.get(property)) {
			binding.unregisterFromView();
		}
		bindings.removeAll(property, bindings.get(property));
	}

	public void modelToView() {
		ignoreChanges = true;
		try {
			for (Map.Entry<Object, Binding> entry : bindings.entrySet()) {
				((Binding) entry.getValue()).modelToView();
			}
		} finally {
			ignoreChanges = false;
		}
	}

	public void viewToModel() {
		ignoreChanges = true;
		try {
			for (Map.Entry<Object, Binding> entry : bindings.entrySet()) {
				((Binding) entry.getValue()).viewToModel();
			}
		} finally {
			ignoreChanges = false;
		}
	}

	public interface Binding {
		public Object getProperty();

		public void modelToView();

		public void viewToModel();

		public void registerWithView();

		public void unregisterFromView();
	}

	protected class ChangeHandler implements HierarchicalBasicPropertyChangeListener {
		@Override
		public void propertyChange(Object source, Object property, Object oldValue, Object newValue, int index) {
			if (ignoreChanges || source != model) {
				return;
			}

			for (Binding binding : bindings.get(property)) {
				binding.modelToView();
			}
		}

		@Override
		public void childrenChanged(Object source, ChangeType changeType, Object child) {

		}
	}
}

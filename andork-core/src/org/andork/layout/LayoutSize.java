package org.andork.layout;
import java.awt.Component;
import java.awt.Dimension;

public enum LayoutSize {
	MINIMUM {
		@Override
		public Dimension get(Component comp) {
			return comp.getMinimumSize();
		}

		@Override
		public void set(Component comp, Dimension size) {
			comp.setMinimumSize(size);
		}

		@Override
		public boolean isSet(Component comp) {
			return comp.isMinimumSizeSet();
		}
	},
	PREFERRED {
		@Override
		public Dimension get(Component comp) {
			return comp.getPreferredSize();
		}

		@Override
		public void set(Component comp, Dimension size) {
			comp.setPreferredSize(size);
		}

		@Override
		public boolean isSet(Component comp) {
			return comp.isPreferredSizeSet();
		}
	},
	MAXIMUM {
		@Override
		public Dimension get(Component comp) {
			return comp.getMaximumSize();
		}

		@Override
		public void set(Component comp, Dimension size) {
			comp.setMaximumSize(size);
		}

		@Override
		public boolean isSet(Component comp) {
			return comp.isMaximumSizeSet();
		}
	};

	public abstract Dimension get(Component comp);

	public abstract void set(Component comp, Dimension size);
	
	public abstract boolean isSet(Component comp);
}
package org.andork.bind.ui;

import static org.andork.q.QAutorun.autorun;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;

import org.andork.awt.layout.BetterCardLayout;
import org.andork.func.Bimapper;
import org.andork.func.LinearFloatBimapper;
import org.andork.model.Cell;
import org.andork.q.QAutorun;
import org.andork.ref.Ref;
import org.andork.swing.FloatSlider;
import org.andork.swing.selector.ISelector;
import org.andork.swing.selector.ISelectorListener;
import org.andork.util.Java7.Objects;

public class BindUI {
	public static AutoCloseable bindBackground(Component component, Cell<Color> cell) {
		QAutorun autorun = autorun(() -> {
			Color color = cell.get();
			if (!Objects.equals(component.getBackground(), color)) {
				component.setBackground(color);
			}
		});
		PropertyChangeListener listener = (e) -> {
			if ("background".equals(e.getPropertyName())) {
				cell.set(component.getBackground());
			}
		};
		component.addPropertyChangeListener("background", listener);
		return () -> {
			autorun.close();
			component.removePropertyChangeListener("background", listener);
		};
	}

	public static AutoCloseable bindSelected(JToggleButton button, Cell<Boolean> cell) {
		QAutorun autorun = autorun(() -> {
			button.setSelected(Boolean.TRUE.equals(cell.get()));
		});
		ItemListener listener = (e) -> {
			cell.set(button.isSelected());
		};
		button.addItemListener(listener);
		return () -> {
			autorun.close();
			button.removeItemListener(listener);
		};
	}

	public static <T> SelectionMap<T> bindSelectedMap(Cell<T> cell) {
		SelectionMap<T> selectionMap = new SelectionMap<>();
		QAutorun autorun = autorun(() -> {
			T value = cell.get();
			selectionMap.entrySet().stream().forEach(e -> {
				e.getValue().setSelected(Objects.equals(e.getKey(), value));
			});
		});
		Map<T, ItemListener> listeners = new HashMap<>();
		selectionMap.entrySet().stream().forEach(e -> {
			ItemListener listener = (ev) -> {
				if (e.getValue().isSelected())
					cell.set(e.getKey());
			};
			e.getValue().addItemListener(listener);
			listeners.put(e.getKey(), listener);
		});
		selectionMap.close = () -> {
			autorun.close();
			listeners.entrySet().stream().forEach(e -> {
				selectionMap.get(e.getKey()).removeItemListener(e.getValue());
			});
		};
		return selectionMap;
	}

	@SuppressWarnings("serial")
	public static class SelectionMap<T> extends HashMap<T, AbstractButton> implements AutoCloseable {
		AutoCloseable close;

		SelectionMap() {
			super();
		}

		public SelectionMap<T> map(T value, AbstractButton button) {
			super.put(value, button);
			return this;
		}

		@Override
		public void close() throws Exception {
			if (close != null)
				close.close();
		}
	}

	public static AutoCloseable bindEnabled(Component component, Supplier<Boolean> enabled) {
		return autorun(() -> component.setEnabled(Boolean.TRUE.equals(enabled.get())));
	}

	public static AutoCloseable bindValue(JSlider slider, Cell<Float> cell, float minDataValue, float maxDataValue) {
		return bindValue(
			slider,
			cell,
			new LinearFloatBimapper(minDataValue, slider.getMinimum(), maxDataValue, slider.getMaximum()));
	}

	public static <N extends Number> AutoCloseable
		bindValue(JSlider slider, Cell<N> cell, Bimapper<N, Float> conversion) {
		return bindValue(slider, Cell.from(() -> {
			N value = cell.get();
			return value == null ? null : Math.round(conversion.map(value));
		}, (newValue) -> {
			cell.set(newValue == null ? null : conversion.unmap((float) newValue));
		}));
	}

	public static AutoCloseable bindValue(JSlider slider, Cell<Integer> cell) {
		Ref<Boolean> changing = new Ref<>(false);
		QAutorun autorun = autorun(() -> {
			if (changing.value)
				return;
			try {
				changing.value = true;
				Integer value = cell.get();
				if (value != null && slider.getValue() != value)
					slider.setValue(value);
			} finally {
				changing.value = false;
			}
		});
		ChangeListener listener = (e) -> {
			if (changing.value)
				return;
			try {
				changing.value = true;
				cell.set(slider.getValue());
			} finally {
				changing.value = false;
			}
		};
		slider.addChangeListener(listener);
		return () -> {
			autorun.close();
			slider.removeChangeListener(listener);
		};
	}

	public static AutoCloseable
		bindValue(FloatSlider slider, Cell<Float> cell, float minDataValue, float maxDataValue) {
		return bindValue(
			slider,
			cell,
			new LinearFloatBimapper(minDataValue, slider.getFloatMinimum(), maxDataValue, slider.getFloatMaximum()));
	}

	public static AutoCloseable bindValue(FloatSlider slider, Cell<Float> cell, Bimapper<Float, Float> conversion) {
		return bindValue(slider, Cell.compose(cell, conversion));
	}

	public static AutoCloseable bindValue(FloatSlider slider, Cell<Float> cell) {
		Ref<Boolean> changing = new Ref<>(false);
		QAutorun autorun = autorun(() -> {
			if (changing.value)
				return;
			try {
				changing.value = true;
				Float value = cell.get();
				if (value != null)
					slider.setFloatValue(value);
			} finally {
				changing.value = false;
			}
		});
		PropertyChangeListener listener = (e) -> {
			if (changing.value)
				return;
			try {
				changing.value = true;
				cell.set(slider.getFloatValue());
			} finally {
				changing.value = false;
			}
		};
		slider.addPropertyChangeListener("floatValue", listener);
		return () -> {
			autorun.close();
			slider.removePropertyChangeListener("floatValue", listener);
		};
	}

	public static <T> AutoCloseable bindValue(ISelector<T> selector, Cell<T> cell) {
		QAutorun autorun = autorun(() -> {
			T value = cell.get();
			selector.setSelection(value);
		});
		ISelectorListener<T> listener = (e, oldValue, newValue) -> {
			cell.set(selector.getSelection());
		};
		selector.addSelectorListener(listener);
		return () -> {
			autorun.close();
			selector.removeSelectorListener(listener);
		};
	}

	public static <T> AutoCloseable bindValue(JComboBox<T> comboBox, Cell<T> cell) {
		QAutorun autorun = autorun(() -> {
			T value = cell.get();
			comboBox.setSelectedItem(value);
		});
		@SuppressWarnings("unchecked")
		ItemListener listener = (e) -> {
			cell.set((T) comboBox.getSelectedItem());
		};
		comboBox.addItemListener(listener);
		return () -> {
			autorun.close();
			comboBox.removeItemListener(listener);
		};
	}

	public static AutoCloseable bindLayout(Container parent, BetterCardLayout layout, Cell<?> cell) {
		return autorun(() -> {
			layout.show(parent, cell.get());
		});
	}
}

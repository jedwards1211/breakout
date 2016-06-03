package org.andork.tracker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.andork.tracker.model.QArrayObject;
import org.andork.tracker.model.QObject;
import org.andork.tracker.model.QSpec;

public class TrackerSandbox {
	public static final class Model extends QSpec {
		public static final Property<Number> value = property("value", Number.class);
		public static final Model spec = new Model();

		private Model() {
			super(value);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			Container content = frame.getContentPane();
			JSlider slider = new JSlider(0, 100, 50);
			JFormattedTextField textField = new JFormattedTextField();
			textField.setFormatterFactory(
					new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance())));
			content.setLayout(new BorderLayout());
			content.add(slider, BorderLayout.CENTER);
			content.add(textField, BorderLayout.SOUTH);

			QObject<Model> model = new QArrayObject<>(Model.spec);
			model.set(Model.value, 25);

			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					model.set(Model.value, slider.getValue());
				}
			});
			textField.addPropertyChangeListener("value", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					model.set(Model.value, (Number) evt.getNewValue());
				}
			});

			Tracker.EDT.autorun(() -> {
				int value = model.get(Model.value).intValue();
				slider.setValue(value);
				textField.setValue(value);
			});

			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}
}

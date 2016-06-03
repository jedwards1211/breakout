package org.andork.redux.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.andork.redux.Action;
import org.andork.redux.Redux;
import org.andork.redux.Store;

public class ReduxUITest {
	public static class SetValueAction extends Action {
		public final Number payload;

		public SetValueAction(Number newValue) {
			super("SET_VALUE");
			payload = newValue;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();

			JPanel mainContent = new JPanel();

			JSlider slider = new JSlider(0, 100, 50);
			JFormattedTextField textField = new JFormattedTextField();
			textField.setFormatterFactory(
					new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance())));
			mainContent.setLayout(new BorderLayout());
			mainContent.add(slider, BorderLayout.CENTER);
			mainContent.add(textField, BorderLayout.SOUTH);

			Connector<Number> connector = new Connector<Number>(mainContent) {
				private static final long serialVersionUID = -5296821559052373735L;

				@Override
				public void update(Number state) {
					int value = state.intValue();
					slider.setValue(value);
					textField.setValue(value);
				}
			};
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					connector.dispatch(new SetValueAction(slider.getValue()));
				}
			});
			textField.addPropertyChangeListener("value", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					connector.dispatch(new SetValueAction((Number) textField.getValue()));
				}
			});

			Store<Number> store = Redux.createStore((state, action) -> {
				return action instanceof SetValueAction
						? ((SetValueAction) action).payload
						: state;
			} , 25);

			frame.setContentPane(new Provider<Number>(store, connector));

			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}
}

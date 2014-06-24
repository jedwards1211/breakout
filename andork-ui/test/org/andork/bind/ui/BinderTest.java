package org.andork.bind.ui;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.andork.bind.BimapperBinder;
import org.andork.bind.DefaultBinder;
import org.andork.bind.ui.ComponentTextBinder;
import org.andork.bind.ui.JSliderValueBinder;
import org.andork.func.IntegerStringBimapper;

public class BinderTest {
	public static void main(String[] args) {
		JTextField textField = new JTextField();
		JSlider slider = new JSlider(0, 100, 50);

		DefaultBinder<Integer> rootBinder = new DefaultBinder<Integer>();
		rootBinder.set(50);

		ComponentTextBinder.bind(textField,
				BimapperBinder.bind(IntegerStringBimapper.instance, rootBinder));

		JSliderValueBinder.bind(slider, rootBinder);

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(textField);
		frame.getContentPane().add(slider);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

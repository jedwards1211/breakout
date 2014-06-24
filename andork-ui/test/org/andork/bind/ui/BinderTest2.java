package org.andork.bind.ui;

import static org.andork.bind.BimapperBinder.bind;
import static org.andork.bind.QObjectAttributeBinder.bind;
import static org.andork.bind.ui.ComponentTextBinder.bind;
import static org.andork.bind.ui.ISelectorSelectionBinder.bind;
import static org.andork.bind.ui.JSliderValueBinder.bind;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.andork.bind.DefaultBinder;
import org.andork.bind.QMapKeyedBinder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.func.IntegerStringBimapper;
import org.andork.q.QHashMap;
import org.andork.q.QMap;
import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.swing.selector.DefaultSelector;

public class BinderTest2 {
	private static enum ValueType {
		APPLES, ORANGES, BANANAS;
	}

	private static class BasicModel extends QSpec<BasicModel> {
		public static final Attribute<ValueType>					valueType	= newAttribute(ValueType.class, "valueType");
		public static final Attribute<QMap<ValueType, Integer, ?>>	values		= newAttribute(QMap.class, "values");

		public static final BasicModel								instance	= new BasicModel();

		private BasicModel() {
			super();
		}
	}

	public static void main(String[] args) {
		DefaultSelector<ValueType> selector = new DefaultSelector<ValueType>();
		selector.setAvailableValues(ValueType.values());
		JTextField textField = new JTextField();
		JSlider slider = new JSlider(0, 100, 50);

		QObject<BasicModel> model = BasicModel.instance.newObject();
		model.set(BasicModel.valueType, ValueType.APPLES);
		model.set(BasicModel.values, QHashMap.newInstance());

		DefaultBinder<QObject<BasicModel>> rootBinder = DefaultBinder.bind(model);
		QObjectAttributeBinder<ValueType> valueTypeBinder = bind(BasicModel.valueType, rootBinder);
		QObjectAttributeBinder<QMap<ValueType, Integer, ?>> valuesBinder = bind(BasicModel.values, rootBinder);
		QMapKeyedBinder<ValueType, Integer> valueBinder = new QMapKeyedBinder<ValueType, Integer>().bind(
				valueTypeBinder, valuesBinder);

		bind(selector, valueTypeBinder);
		bind(textField, bind(IntegerStringBimapper.instance, valueBinder));
		bind(slider, valueBinder);

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(selector.getComboBox());
		frame.getContentPane().add(textField);
		frame.getContentPane().add(slider);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

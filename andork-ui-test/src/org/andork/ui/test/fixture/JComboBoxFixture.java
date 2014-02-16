package org.andork.ui.test.fixture;

import java.awt.Component;
import java.lang.reflect.Field;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

import org.andork.func.Predicate;
import org.andork.reflect.ReflectionUtils;
import org.andork.swing.DoSwingR2;

public interface JComboBoxFixture extends ComponentFixture {
	public void selectItemAtIndex(JComboBox cb, int index);

	public void selectItem(JComboBox cb, Object item);

	public void selectItemMatching(JComboBox cb, Predicate<Object> p);

	public abstract void selectItemByText(JComboBox cb, Predicate<String> p);

	public static class Common {
		public static String readText(final JComboBox cb, final int index) {
			return new DoSwingR2<String>() {
				@Override
				protected String doRun() {
					Object value = cb.getItemAt(index);
					try {
						BasicComboBoxUI ui = (BasicComboBoxUI) cb.getUI();
						Field popupField = ReflectionUtils.getField(ui.getClass(), "popup");
						popupField.setAccessible(true);
						ComboPopup popup = (ComboPopup) popupField.get(ui);
						JList list = popup.getList();
						ListCellRenderer renderer = list.getCellRenderer();
						Component rendComp = renderer.getListCellRendererComponent(popup.getList(), value, index, false, false);
						return ComponentFixture.Common.readText(rendComp);
					} catch (Exception ex) {
					}
					return value.toString();
				}
			}.result();
		}
	}
}

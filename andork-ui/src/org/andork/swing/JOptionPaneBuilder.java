package org.andork.swing;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class JOptionPaneBuilder extends JOptionPane {
	public JOptionPaneBuilder message(Object... message) {
		setMessage(message);
		return this;
	}

	public JOptionPaneBuilder icon(Icon icon) {
		setIcon(icon);
		return this;
	}

	public JOptionPaneBuilder options(Object... options) {
		setOptions(options);
		return this;
	}

	public JOptionPaneBuilder initialValue(Object newInitialValue) {
		setInitialValue(newInitialValue);
		return this;
	}
	
	public JOptionPaneBuilder error() {
		setMessageType(ERROR_MESSAGE);
		return this;
	}
	public JOptionPaneBuilder information() {
		setMessageType(INFORMATION_MESSAGE);
		return this;
	}
	public JOptionPaneBuilder warning() {
		setMessageType(WARNING_MESSAGE);
		return this;
	}
	public JOptionPaneBuilder question() {
		setMessageType(QUESTION_MESSAGE);
		return this;
	}
	public JOptionPaneBuilder plain() {
		setMessageType(PLAIN_MESSAGE);
		return this;
	}

	public JOptionPaneBuilder defaultOption() {
		setOptionType(DEFAULT_OPTION);
		return this;
	}
	public JOptionPaneBuilder yesNo() {
		setOptionType(YES_NO_OPTION);
		return this;
	}
	public JOptionPaneBuilder yesNoCancel() {
		setOptionType(YES_NO_CANCEL_OPTION);
		return this;
	}
	public JOptionPaneBuilder okCancel() {
		setOptionType(OK_CANCEL_OPTION);
		return this;
	}
	public JOptionPaneBuilder selectionValues(Object... newValues) {
		setSelectionValues(newValues);
		return this;
	}
	public JOptionPaneBuilder initialSelectionValue(Object newValue) {
		setInitialSelectionValue(newValue);
		return this;
	}

	public int showDialog(Component parentComponent, String title) {
        setComponentOrientation(((parentComponent == null) ?
            getRootFrame() : parentComponent).getComponentOrientation());

        JDialog dialog = createDialog(parentComponent, title);

        selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = getValue();

        if(selectedValue == null)
            return CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return ((Integer)selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
	}
	
	public Object showInputDialog(Component parentComponent, String title) {
		setWantsInput(true);
		setComponentOrientation(((parentComponent == null) ?
			getRootFrame() : parentComponent).getComponentOrientation());

		JDialog dialog = createDialog(parentComponent, title);

		selectInitialValue();
		dialog.setVisible(true);
		dialog.dispose();

		Object value = getInputValue();

		if (value == UNINITIALIZED_VALUE) {
			return null;
		}
		return value;
	}
}

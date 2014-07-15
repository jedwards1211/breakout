package org.andork.swing.selector;

import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

public class BiggerPopupComboBoxUI extends MetalComboBoxUI
{
	@Override
	protected ComboPopup createPopup( )
	{
		return new BiggerComboPopup( comboBox );
	}
}

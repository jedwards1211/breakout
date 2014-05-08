package org.andork.ui.test.fixture;

public class HardComponentFixtureProvider implements ComponentFixtureProvider {
	ComponentFixture	component	= new HardComponentFixture();
	JCheckBoxFixture	checkBox	= new HardJCheckBoxFixture();
	JComboBoxFixture	comboBox	= new HardJComboBoxFixture();
	JTableFixture		table		= new HardJTableFixture();

	@Override
	public ComponentFixture component() {
		return component;
	}

	@Override
	public JCheckBoxFixture checkBox() {
		return checkBox;
	}

	@Override
	public JComboBoxFixture comboBox() {
		return comboBox;
	}

	@Override
	public JTableFixture table() {
		return table;
	}

}

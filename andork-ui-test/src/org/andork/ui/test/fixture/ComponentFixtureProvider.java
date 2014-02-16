package org.andork.ui.test.fixture;

public interface ComponentFixtureProvider {
	public ComponentFixture component();

	public JCheckBoxFixture checkBox();

	public JComboBoxFixture comboBox();

	public JTableFixture table();
}

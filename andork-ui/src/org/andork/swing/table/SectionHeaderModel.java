package org.andork.swing.table;

public interface SectionHeaderModel {
	/**
	 * Gets section header data.
	 * 
	 * @param row
	 *            the row to get the data of. For the section header before the
	 *            first row in the table, use -1.
	 * @return a non-null {@code Object} if the row is a section header,
	 *         {@code null} otherwise.
	 */
	public Object getSectionHeader(int row);
}

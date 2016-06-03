/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.breakout;

import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.JLabel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.SurveyTableModelCopier;

@SuppressWarnings("serial")
public class SurveyDrawer extends Drawer {
	/**
	 *
	 */
	private static final long serialVersionUID = -8494125520077495833L;
	JLabel highlightLabel;
	TextComponentWithHintAndClear highlightField;
	JLabel filterLabel;
	TextComponentWithHintAndClear filterField;

	SurveyTable surveyTable;
	DefaultAnnotatingJTableSetup surveyTableSetup;

	public SurveyDrawer(Consumer<Runnable> sortRunner) {
		setPreferredSize(new Dimension(800, 250));

		highlightLabel = new JLabel("Highlight: ");
		filterLabel = new JLabel("Filter: ");

		highlightField = new TextComponentWithHintAndClear("Enter search terms");
		filterField = new TextComponentWithHintAndClear("Enter search terms");

		surveyTable = new SurveyTable();
		surveyTableSetup = new DefaultAnnotatingJTableSetup(surveyTable, sortRunner);
		((AnnotatingTableRowSorter<SurveyTableModel>) surveyTableSetup.table.getAnnotatingRowSorter())
				.setModelCopier(new SurveyTableModelCopier());

		delegate().dockingSide(Side.BOTTOM);
		mainResizeHandle();

		GridBagWizard gbw = GridBagWizard.create(this);

		gbw.defaults().autoinsets(new DefaultAutoInsets(2, 2));
		gbw.put(mainResizeHandle()).xy(0, 0).fillx(1.0).remWidth();
		gbw.put(filterLabel).xy(0, 1).west().insets(2, 2, 0, 0);
		gbw.put(filterField).rightOf(filterLabel).fillboth(1.0, 0.0);
		gbw.put(highlightLabel).rightOf(filterField).west().insets(2, 10, 0, 0);
		gbw.put(highlightField).rightOf(highlightLabel).fillboth(1.0, 0.0);
		gbw.put(pinButton()).rightOf(highlightField).east().filly(0.0);
		gbw.put(maxButton()).rightOf(pinButton()).east().filly(0.0);
		gbw.put(surveyTableSetup.scrollPane).below(filterLabel, maxButton()).fillboth(0.0, 1.0);
	}

	public TextComponentWithHintAndClear filterField() {
		return filterField;
	}

	public TextComponentWithHintAndClear highlightField() {
		return highlightField;
	}

	public SurveyTable table() {
		return surveyTable;
	}
}

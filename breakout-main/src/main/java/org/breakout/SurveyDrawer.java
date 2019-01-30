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
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;
import org.breakout.SurveyTable.Aspect;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.SurveyTableModelCopier;

public class SurveyDrawer extends Drawer {
	/**
	 *
	 */
	private static final long serialVersionUID = -8494125520077495833L;
	JLabel searchLabel;
	TextComponentWithHintAndClear searchField;
	JRadioButton filterButton;
	JRadioButton highlightButton;

	SurveyTable surveyTable;
	DefaultAnnotatingJTableSetup surveyTableSetup;

	JRadioButton shotsButton;
	JRadioButton nevButton;
	JRadioButton tripButton;
	
	JButton setCaveButton;

	JToggleButton editButton;

	public SurveyDrawer(Consumer<Runnable> sortRunner) {
		setPreferredSize(new Dimension(800, 250));

		searchLabel = new JLabel("Search: ");
		searchField = new TextComponentWithHintAndClear("Enter search terms");
		filterButton = new JRadioButton("Filter");
		highlightButton = new JRadioButton("Highlight");

		ButtonGroup searchGroup = new ButtonGroup();
		searchGroup.add(filterButton);
		searchGroup.add(highlightButton);

		highlightButton.setSelected(true);

		surveyTable = new SurveyTable();
		surveyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		surveyTableSetup = new DefaultAnnotatingJTableSetup(surveyTable, sortRunner);
		((AnnotatingTableRowSorter<SurveyTableModel>) surveyTableSetup.table.getAnnotatingRowSorter())
				.setModelCopier(new SurveyTableModelCopier());
		
		setCaveButton = new JButton("Set Cave...");
		setCaveButton.setVisible(false);

		shotsButton = new JRadioButton("Shots");
		nevButton = new JRadioButton("NEV");
		tripButton = new JRadioButton("Trip");
		ButtonGroup aspectGroup = new ButtonGroup();
		aspectGroup.add(shotsButton);
		aspectGroup.add(nevButton);
		aspectGroup.add(tripButton);

		editButton = new JToggleButton("Edit");
		editButton.setMargin(new Insets(0, 0, 0, 0));
		editButton.addItemListener(e -> {
			SurveyTableModel model = surveyTable.getModel();
			boolean editing = e.getStateChange() == ItemEvent.SELECTED;
			if (model != null) {
				model.setEditable(editing);
			}
			setCaveButton.setVisible(editing);
			editButton.setText(editing ? "Done" : "Edit");
		});

		delegate().dockingSide(Side.BOTTOM);
		mainResizeHandle();

		GridBagWizard gbw = GridBagWizard.create(this);

		gbw.defaults().autoinsets(new DefaultAutoInsets(2, 2));
		gbw.put(mainResizeHandle()).xy(0, 0).fillx(1.0).remWidth();
		gbw.put(searchLabel).xy(0, 1).west().insets(2, 2, 0, 0);
		gbw.put(searchField).rightOfLast().fillboth(1.0, 0.0);
		gbw.put(highlightButton).rightOfLast().west().insets(2, 5, 0, 0);
		gbw.put(filterButton).rightOfLast().west().insets(2, 5, 0, 0);
		gbw.put(setCaveButton).rightOfLast().filly(0.0);
		gbw.put(new JSeparator(SwingConstants.VERTICAL)).rightOfLast().filly(0.0);
		gbw.put(shotsButton).rightOfLast().filly(0.0);
		gbw.put(nevButton).rightOfLast().filly(0.0);
		gbw.put(tripButton).rightOfLast().filly(0.0);
		gbw.put(editButton).rightOfLast().filly(0.0);
		gbw.put(new JSeparator(SwingConstants.VERTICAL)).rightOfLast().filly(0.0);
		gbw.put(pinButton()).rightOfLast().east().filly(0.0);
		gbw.put(maxButton()).rightOfLast().east().filly(0.0);
		gbw.put(surveyTableSetup.scrollPane).below(searchLabel, maxButton()).fillboth(0.0, 1.0);

		ItemListener aspectListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					return;
				}
				if (e.getSource() == shotsButton) {
					surveyTable.setAspect(Aspect.SHOTS);
				} else if (e.getSource() == nevButton) {
					surveyTable.setAspect(Aspect.NEV);
				} else if (e.getSource() == tripButton) {
					surveyTable.setAspect(Aspect.TRIP);
				}
			}
		};
		shotsButton.addItemListener(aspectListener);
		nevButton.addItemListener(aspectListener);
		tripButton.addItemListener(aspectListener);

		shotsButton.setSelected(true);
	}

	public TextComponentWithHintAndClear searchField() {
		return searchField;
	}

	public JRadioButton highlightButton() {
		return highlightButton;
	}

	public JRadioButton filterButton() {
		return filterButton;
	}

	public JToggleButton editButton() {
		return editButton;
	}
	
	public JButton setCaveButton() {
		return setCaveButton;
	}

	public SurveyTable table() {
		return surveyTable;
	}
}

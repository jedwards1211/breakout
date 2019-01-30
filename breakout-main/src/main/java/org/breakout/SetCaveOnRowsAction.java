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

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import org.andork.awt.I18n.Localizer;
import org.andork.swing.OnEDT;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.raw.SurveyTrip;

public class SetCaveOnRowsAction extends AbstractAction {
	/**
	 *
	 */
	private static final long serialVersionUID = -8899030390228292424L;

	BreakoutMainView mainView;

	public SetCaveOnRowsAction(final BreakoutMainView mainView) {
		super();
		this.mainView = mainView;
		OnEDT.onEDT(() -> {
			Localizer localizer = mainView.getI18n().forClass(SetCaveOnRowsAction.this.getClass());
			localizer.setName(SetCaveOnRowsAction.this, "name");
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final String caveName = JOptionPane.showInputDialog(mainView.getMainPanel(), "Enter new cave name:", "Set Cave",
				JOptionPane.QUESTION_MESSAGE);
		final SurveyTableModel model = mainView.getSurveyTable().getModel();
		final ListSelectionModel selModel = mainView.getSurveyTable().getModelSelectionModel();
		
		final IdentityHashMap<SurveyTrip, SurveyTrip> newTrips = new IdentityHashMap<>();
		final IdentityHashMap<SurveyTrip, Boolean> isWholeTripSelected = new IdentityHashMap<>();
		
		final Function<SurveyTrip, SurveyTrip> updateTrip = trip -> {
			if (caveName.equals(trip.getCave())) return trip;
			SurveyTrip updated = newTrips.get(trip);
			if (updated == null) {
				updated = trip.setCave(caveName);
				newTrips.put(trip, updated);
			}
			return updated;
		};
		
		for (int i = 0; i < model.getRowCount(); i++) {
			SurveyTrip trip = model.getRow(i).getTrip();
			if (!selModel.isSelectionEmpty() && !selModel.isSelectedIndex(i)) {
				isWholeTripSelected.put(trip, false);
			}
			else if (!isWholeTripSelected.containsKey(trip)) {
				isWholeTripSelected.put(trip, true);
			}
		}
		
		for (int i = 0; i < model.getRowCount(); i++) {
			if (selModel.isSelectionEmpty() || selModel.isSelectedIndex(i)) {
				model.setRow(i, model.getRow(i).withMutations(mrow -> {
					if (Boolean.TRUE.equals(isWholeTripSelected.get(mrow.getTrip()))) {
						mrow.updateTrip(updateTrip);
						mrow.setOverrideFromCave(null);
						mrow.setOverrideToCave(null);
					} else {
						mrow.setOverrideFromCave(caveName);
						mrow.setOverrideToCave(caveName);
					}
				}));
			}
		}
		
		model.updateRows(row -> {
			SurveyTrip newTrip = newTrips.get(row.getTrip());
			return newTrip != null ? row.setTrip(newTrip) : row;
		});
	}
}

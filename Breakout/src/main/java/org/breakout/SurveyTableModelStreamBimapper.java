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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.andork.swing.async.Subtask;
import org.andork.swing.async.SubtaskStreamBimapper;
import org.breakout.model.MetacaveExporter;
import org.breakout.model.MetacaveImporter;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.SurveyTableModelCopier;

import com.google.gson.Gson;

public class SurveyTableModelStreamBimapper extends SubtaskStreamBimapper<SurveyTableModel> {
	boolean closeStreams;

	boolean makeCopy;

	public SurveyTableModelStreamBimapper(Subtask subtask) {
		super(subtask);
	}

	public SurveyTableModelStreamBimapper closeStreams(boolean closeStreams) {
		this.closeStreams = closeStreams;
		return this;
	}

	public SurveyTableModelStreamBimapper makeCopy(boolean makeCopy) {
		this.makeCopy = makeCopy;
		return this;
	}

	@Override
	public SurveyTableModel read(InputStream in) throws Exception {
		subtask().setIndeterminate(true);

		try {
			MetacaveImporter importer = new MetacaveImporter();
			importer.importMetacave(in);
			return new SurveyTableModel(importer.getRows());
		} finally {
			subtask().end();

			if (closeStreams) {
				try {
					in.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	@Override
	public void write(SurveyTableModel model, OutputStream out) throws Exception {
		if (makeCopy) {
			SurveyTableModel copy = new SurveyTableModel();
			SurveyTableModelCopier copier = new SurveyTableModelCopier();

			copier.copyInBackground(model, copy, 1000, null);
			model = copy;
		}

		subtask().setTotal(model.getRowCount());
		subtask().setCompleted(0);
		subtask().setIndeterminate(false);

		Writer writer = null;
		try {
			writer = new OutputStreamWriter(out, "UTF-8");
			MetacaveExporter exporter = new MetacaveExporter();
			exporter.export(model.getRows());

			new Gson().toJson(exporter.getRoot(), writer);
			writer.flush();
		} finally {
			subtask().end();

			if (closeStreams && writer != null) {
				try {
					writer.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}

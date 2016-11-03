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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.andork.swing.async.Subtask;
import org.andork.swing.async.SubtaskStreamBimapper;
import org.andork.util.ArrayUtils;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.SurveyTableModelCopier;

public class SurveyTableModelStreamBimapper extends SubtaskStreamBimapper<SurveyTableModel> {
	interface Column {
		public void deserialize(String raw, Row row);

		public boolean matches(String name);

		public String name();

		public String serialize(Row row);
	}

	static Column column(
			String name,
			Pattern nameMatcher,
			Function<Row, String> serialize,
			BiConsumer<String, Row> deserialize) {
		return new Column() {
			@Override
			public void deserialize(String raw, Row row) {
				deserialize.accept(raw, row);
			}

			@Override
			public boolean matches(String name) {
				return nameMatcher.matcher(name).matches();
			}

			@Override
			public String name() {
				return name;
			}

			@Override
			public String serialize(Row row) {
				String result = serialize.apply(row);
				return result == null ? "" : result;
			}
		};
	}

	final Column[] columns = {
			column("From Cave", Pattern.compile("from\\s+cave", Pattern.CASE_INSENSITIVE),
					row -> row.getOverrideFromCave(),
					(fromCave, row) -> row.setOverrideFromCave(fromCave)),
			column("From Station", Pattern.compile("from(\\s*station)?", Pattern.CASE_INSENSITIVE),
					row -> row.getFromStation(),
					(fromStation, row) -> row.setFromStation(fromStation)),
			column("To Cave", Pattern.compile("to\\s*cave", Pattern.CASE_INSENSITIVE),
					row -> row.getOverrideToCave(),
					(toCave, row) -> row.setOverrideToCave(toCave)),
			column("To Station", Pattern.compile("to(\\s*station)?", Pattern.CASE_INSENSITIVE),
					row -> row.getToStation(),
					(toStation, row) -> row.setToStation(toStation)),
			column("Distance", Pattern.compile("dist(ance)?", Pattern.CASE_INSENSITIVE),
					row -> row.getDistance(),
					(distance, row) -> row.setDistance(distance)),
			column("Frontsight Azimuth", Pattern.compile("(front(sight)?|fs)\\s*(azm?|azimuth|comp(ass)?|bearing)",
					Pattern.CASE_INSENSITIVE),
					row -> row.getFrontAzimuth(),
					(frontAzimuth, row) -> row.setFrontAzimuth(frontAzimuth)),
			column("Frontsight Inclination", Pattern.compile(
					"(front(sight)?|fs)\\s*(inc(lination)?|clino|vert(ical)?\\s*angle)", Pattern.CASE_INSENSITIVE),
					row -> row.getFrontInclination(),
					(frontInclination, row) -> row.setFrontInclination(frontInclination)),
			column("Backsight Azimuth",
					Pattern.compile("(back(sight)?|bs)\\s*(azm?|azimuth)", Pattern.CASE_INSENSITIVE),
					row -> row.getBackAzimuth(),
					(backAzimuth, row) -> row.setBackAzimuth(backAzimuth)),
			column("Backsight Inclination", Pattern.compile(
					"(back(sight)?|bs)\\s*(inc(lination)?|clino|vert(ical)?\\s*angle)", Pattern.CASE_INSENSITIVE),
					row -> row.getBackInclination(),
					(backInclination, row) -> row.setBackInclination(backInclination)),
			column("Left", Pattern.compile(
					"l(eft)?", Pattern.CASE_INSENSITIVE),
					row -> row.getLeft(),
					(left, row) -> row.setLeft(left)),
			column("Right", Pattern.compile(
					"r(ight)?", Pattern.CASE_INSENSITIVE),
					row -> row.getRight(),
					(right, row) -> row.setRight(right)),
			column("Up", Pattern.compile(
					"up?", Pattern.CASE_INSENSITIVE),
					row -> row.getUp(),
					(up, row) -> row.setUp(up)),
			column("Down", Pattern.compile(
					"d(own)?", Pattern.CASE_INSENSITIVE),
					row -> row.getDown(),
					(down, row) -> row.setDown(down)),
			column("North", Pattern.compile(
					"north(ing)?", Pattern.CASE_INSENSITIVE),
					row -> row.getNorthing(),
					(northing, row) -> row.setNorthing(northing)),
			column("East", Pattern.compile(
					"east(ing)?", Pattern.CASE_INSENSITIVE),
					row -> row.getEasting(),
					(easting, row) -> row.setEasting(easting)),
			column("Elevation", Pattern.compile(
					"elev(ation)?", Pattern.CASE_INSENSITIVE),
					row -> row.getElevation(),
					(elevation, row) -> row.setElevation(elevation))
	};

	boolean closeStreams;

	boolean makeCopy;

	public SurveyTableModelStreamBimapper(Subtask subtask) {
		super(subtask);
		// rowBimapper = QObjectTabDelimBimapper.newInstance(Row.instance)
		// .addColumn("From", Row.from)
		// .addColumn("To", Row.to)
		// .addColumn("Distance", Row.distance)
		// .addColumn("Frontsight Azimuth", Row.fsAzm)
		// .addColumn("Frontsight Inclination", Row.fsInc)
		// .addColumn("Backsight Azimuth", Row.bsAzm)
		// .addColumn("Backsight Inclination", Row.bsInc)
		// .addColumn("Left", Row.left)
		// .addColumn("Right", Row.right)
		// .addColumn("Up", Row.up)
		// .addColumn("Down", Row.down)
		// .addColumn("North", Row.north)
		// .addColumn("East", Row.east)
		// .addColumn("Elevation", Row.elev)
		// .addColumn("Description", Row.desc)
		// .addColumn("Date", Row.date)
		// .addColumn("Surveyors", Row.surveyors)
		// .addColumn("Comment", Row.comment)
		// .addColumn("Scanned Notes", Row.scannedNotes);
	}

	public SurveyTableModelStreamBimapper closeStreams(boolean closeStreams) {
		this.closeStreams = closeStreams;
		return this;
	}

	Column columnFor(String name) {
		for (Column column : columns) {
			if (column.matches(name)) {
				return column;
			}
		}
		return null;
	}

	public SurveyTableModelStreamBimapper makeCopy(boolean makeCopy) {
		this.makeCopy = makeCopy;
		return this;
	}

	@Override
	public SurveyTableModel read(InputStream in) throws Exception {
		subtask().setIndeterminate(true);

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));

			SurveyTableModel result = new SurveyTableModel();

			String line = reader.readLine();
			if (line == null) {
				return result;
			}

			String[] headerNames = line.split("\t");
			Column[] columns = ArrayUtils.map(headerNames, new Column[headerNames.length], this::columnFor);

			int ri = 0;
			while ((line = reader.readLine()) != null) {
				String[] cells = line.split("\t");
				Row row = new Row();
				for (int i = 0; i < cells.length; i++) {
					if (columns[i] != null) {
						columns[i].deserialize(cells[i], row);
					}
				}
				result.setRow(ri++, row);
			}

			return result;
		} finally {
			subtask().end();

			if (closeStreams && reader != null) {
				try {
					reader.close();
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

		PrintStream p = null;

		subtask().setTotal(model.getRowCount());
		subtask().setCompleted(0);
		subtask().setIndeterminate(false);

		try {
			p = new PrintStream(out);

			p.print(columns[0].name());
			for (int i = 1; i < columns.length; i++) {
				p.print('\t');
				p.print(columns[i].name());
			}
			p.println();

			for (int ri = 0; ri < model.getRowCount(); ri++) {
				Row row = model.getRow(ri);
				p.print(columns[0].serialize(row));
				for (int i = 1; i < columns.length; i++) {
					p.print('\t');
					p.print(columns[i].serialize(row));
				}
				p.println();
				subtask().setCompleted(ri);
			}
		} finally {
			subtask().end();

			if (closeStreams && p != null) {
				try {
					p.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}

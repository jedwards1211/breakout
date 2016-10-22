package org.breakout;

import java.text.NumberFormat;

import org.andork.swing.table.ListTableColumn;
import org.breakout.model.NewSurveyTableModel.Row;

public class NewSurveyTable {
	public static class Columns {
		static final NumberFormat numberFormat = NumberFormat.getInstance();
		static final RawNumberCellRenderer numberCellRenderer = new RawNumberCellRenderer(numberFormat);

		static {
			numberFormat.setGroupingUsed(false);
			numberFormat.setMinimumFractionDigits(1);
			numberFormat.setMaximumFractionDigits(1);
		}

		public static ListTableColumn<Row, String> fromCave = new ListTableColumn<Row, String>()
				.headerValue("From Cave")
				.getter(row -> row.getFromCave())
				.setter((row, value) -> row.setFromCave(value));
		public static ListTableColumn<Row, String> fromStation = new ListTableColumn<Row, String>()
				.headerValue("From Station")
				.getter(row -> row.getFromStation())
				.setter((row, value) -> row.setFromStation(value));
		public static ListTableColumn<Row, String> toCave = new ListTableColumn<Row, String>()
				.headerValue("To Cave")
				.getter(row -> row.getToCave())
				.setter((row, value) -> row.setToCave(value));
		public static ListTableColumn<Row, String> toStation = new ListTableColumn<Row, String>()
				.headerValue("To Station")
				.getter(row -> row.getToStation())
				.setter((row, value) -> row.setToStation(value));
		public static ListTableColumn<Row, String> distance = new ListTableColumn<Row, String>()
				.headerValue("Distance")
				.renderer(numberCellRenderer)
				.getter(row -> row.getDistance())
				.setter((row, value) -> row.setDistance(value));
		public static ListTableColumn<Row, String> frontAzimuth = new ListTableColumn<Row, String>()
				.headerValue("Front Azimuth")
				.renderer(numberCellRenderer)
				.getter(row -> row.getFrontAzimuth())
				.setter((row, value) -> row.setFrontAzimuth(value));
		public static ListTableColumn<Row, String> backAzimuth = new ListTableColumn<Row, String>()
				.headerValue("Back Azimuth")
				.renderer(numberCellRenderer)
				.getter(row -> row.getBackAzimuth())
				.setter((row, value) -> row.setBackAzimuth(value));
		public static ListTableColumn<Row, String> frontInclination = new ListTableColumn<Row, String>()
				.headerValue("Front Inclination")
				.renderer(numberCellRenderer)
				.getter(row -> row.getFrontInclination())
				.setter((row, value) -> row.setFrontInclination(value));
		public static ListTableColumn<Row, String> backInclination = new ListTableColumn<Row, String>()
				.headerValue("Back Inclination")
				.renderer(numberCellRenderer)
				.getter(row -> row.getBackInclination())
				.setter((row, value) -> row.setBackInclination(value));
		public static ListTableColumn<Row, String> left = new ListTableColumn<Row, String>()
				.headerValue("Left")
				.renderer(numberCellRenderer)
				.getter(row -> row.getLeft())
				.setter((row, value) -> row.setLeft(value));
		public static ListTableColumn<Row, String> right = new ListTableColumn<Row, String>()
				.headerValue("Right")
				.renderer(numberCellRenderer)
				.getter(row -> row.getRight())
				.setter((row, value) -> row.setRight(value));
		public static ListTableColumn<Row, String> up = new ListTableColumn<Row, String>()
				.headerValue("Up")
				.renderer(numberCellRenderer)
				.getter(row -> row.getUp())
				.setter((row, value) -> row.setUp(value));
		public static ListTableColumn<Row, String> down = new ListTableColumn<Row, String>()
				.headerValue("Down")
				.renderer(numberCellRenderer)
				.getter(row -> row.getDown())
				.setter((row, value) -> row.setDown(value));
		public static ListTableColumn<Row, String> northing = new ListTableColumn<Row, String>()
				.headerValue("Northing")
				.renderer(numberCellRenderer)
				.getter(row -> row.getNorthing())
				.setter((row, value) -> row.setNorthing(value));
		public static ListTableColumn<Row, String> easting = new ListTableColumn<Row, String>()
				.headerValue("Easting")
				.renderer(numberCellRenderer)
				.getter(row -> row.getEasting())
				.setter((row, value) -> row.setEasting(value));
		public static ListTableColumn<Row, String> elevation = new ListTableColumn<Row, String>()
				.headerValue("Elevation")
				.renderer(numberCellRenderer)
				.getter(row -> row.getElevation())
				.setter((row, value) -> row.setElevation(value));
	}
}

package org.breakout.compass;

import java.awt.Dimension;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.andork.compass.survey.CompassSurveyParser;
import org.andork.compass.survey.CompassTrip;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.list.RealListModel;
import org.andork.swing.table.ListTableModel;
import org.breakout.SurveyTable;
import org.breakout.model.SurveyTableModel.Row;

public class CompassConverterDemo {
	public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException {
		CompassSurveyParser parser = new CompassSurveyParser();
		List<CompassTrip> trips = parser.parseCompassSurveyData(
				Paths.get("/Users", "andy", "FRCS OwnCloud", "compass", "all-caves.compass.dat"));
		List<Row> rows = CompassConverter.convertFromCompass(trips);
		ListTableModel<Row> tableModel = new ListTableModel<>((ListModel<Row>) new RealListModel<>(rows));
		JTable surveyTable = new JTable(tableModel);
		TableColumnModel columnModel = new DefaultTableColumnModel();
		for (Field field : SurveyTable.Columns.class.getFields()) {
			columnModel.addColumn((TableColumn) field.get(null));
		}
		surveyTable.setColumnModel(columnModel);
		JScrollPane scrollPane = new JScrollPane(surveyTable);
		scrollPane.setPreferredSize(new Dimension(800, 600));
		QuickTestFrame.frame(scrollPane).setVisible(true);
	}
}

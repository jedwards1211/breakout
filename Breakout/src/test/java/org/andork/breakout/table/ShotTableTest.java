package org.andork.breakout.table;

import java.util.Arrays;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.andork.bind2.DefaultBinder;
import org.andork.breakout.model.NewProjectModel;
import org.andork.breakout.table.DaiShotVector;
import org.andork.breakout.table.ParsedText;
import org.andork.breakout.table.ParsedTextWithValue;
import org.andork.breakout.table.Shot;
import org.andork.breakout.table.ShotColumnDef;
import org.andork.breakout.table.ShotColumnType;
import org.andork.breakout.table.ShotList;
import org.andork.breakout.table.ShotTableColumnNames;
import org.andork.breakout.table.ShotTableModelPresenter;
import org.andork.q2.QArrayObject;
import org.andork.q2.QObject;
import org.andork.swing.QuickTestFrame;

public class ShotTableTest
{
	public static void main( String[ ] args )
	{
		Shot shot0 = new Shot( );

		DaiShotVector vector = new DaiShotVector( );

		vector.dist = 5.0;
		vector.distText = new ParsedText( "5.00" , null );
		vector.azmFs = 10.0;
		vector.azmFsText = new ParsedText( "10.00" , null );
		vector.incFs = 23.5;
		vector.incBs = 26.0;
		vector.incFsBsText = new ParsedText( "23.5  /26.0" , null );

		shot0.vector = vector;
		shot0.from = "A1";
		shot0.custom = new Object[ 1 ];
		shot0.custom[ 0 ] = new ParsedTextWithValue( "3.502" , null , 3.502 );

		ShotList shotList = new ShotList( );

		shotList.setColumnDefs( Arrays.asList(
			new ShotColumnDef( ShotTableColumnNames.from , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.to , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.dist , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.azmFs , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.azmBs , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.azmFsBs , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.incFs , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.incBs , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( ShotTableColumnNames.incFsBs , ShotColumnType.BUILTIN ) ,
			new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE )
			) );
		shotList.add( shot0 );

		QObject<NewProjectModel> model = QArrayObject.create( NewProjectModel.spec );

		ShotTableModelPresenter presenter = new ShotTableModelPresenter( new DefaultBinder<>( model ) );
		model.set( NewProjectModel.decimalSep , ',' );

		presenter.setShotList( shotList );

		JTable table = new JTable( presenter );
		JScrollPane scrollPane = new JScrollPane( table );

		QuickTestFrame.frame( scrollPane ).setVisible( true );
	}
}

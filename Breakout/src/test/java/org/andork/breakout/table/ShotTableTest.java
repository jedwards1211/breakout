package org.andork.breakout.table;

import java.awt.Font;
import java.util.Arrays;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.andork.bind2.DefaultBinder;
import org.andork.breakout.model.NewProjectModel;
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

		Shot shot1 = new Shot( );

		OffsetShotVector vect1 = new OffsetShotVector( );
		vect1.n = 100.0;
		vect1.d = 205.0;
		vect1.nText = new ParsedText( "100" , null );
		vect1.dText = new ParsedText( "205" , null );

		shot1.vector = vect1;
		shot1.custom = new Object[ 2 ];

		ShotList shotList = new ShotList( );

		shotList.setCustomColumnDefs( Arrays.asList(
			new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE )
			) );
		shotList.add( shot0 );

		shotList.setCustomColumnDefs( Arrays.asList(
			new ShotColumnDef( "Test" , ShotColumnType.INTEGER ) ,
			new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE )
			) );

		shot0.custom[ 0 ] = new ParsedTextWithValue( "123" , null , 123 );

		QObject<NewProjectModel> model = QArrayObject.create( NewProjectModel.spec );

		ShotTableModelPresenter presenter = new ShotTableModelPresenter( new DefaultBinder<>( model ) );
		model.set( NewProjectModel.decimalSep , ',' );

		presenter.setShotList( shotList );

		JTable table = new JTable( presenter );
		table.setFont( new Font( "Monospaced" , Font.PLAIN , 11 ) );
		JScrollPane scrollPane = new JScrollPane( table );

		shotList.add( shot1 );

		ShotTableColumnModelPresenter columnModel = new ShotTableColumnModelPresenter( );
		columnModel.update( presenter , Arrays.asList(
			ShotColumnDef.from ,
			ShotColumnDef.to ,
			ShotColumnDef.vector ,
			ShotColumnDef.dist ,
			ShotColumnDef.azmFsBs ,
			ShotColumnDef.azmFs ,
			ShotColumnDef.azmBs ,
			ShotColumnDef.incFsBs ,
			ShotColumnDef.incFs ,
			ShotColumnDef.incBs ,
			ShotColumnDef.offsN ,
			ShotColumnDef.offsE ,
			ShotColumnDef.offsD ) );

		table.setColumnModel( columnModel );

		QuickTestFrame.frame( scrollPane ).setVisible( true );
	}
}

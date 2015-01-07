package org.andork.breakout.table;

import java.awt.Font;
import java.util.Arrays;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.andork.bind2.DefaultBinder;
import org.andork.breakout.model.NewProjectModel;
import org.andork.i18n.I18n;
import org.andork.q2.QArrayObject;
import org.andork.q2.QObject;
import org.andork.swing.QuickTestFrame;

public class ShotTableTest
{
	public static void main( String[ ] args )
	{
		I18n i18n = new I18n( );

		Shot shot0 = new Shot( );

		DaicShotVector vector = new DaicShotVector( );

		vector.distText = new ParsedText( "a5.00" , new ParseNote( "Value contains invalid characters" ,
			ParseStatus.ERROR ) );
		vector.azmFs = -30.0;
		vector.azmFsText = new ParsedText( "-30.00" , new ParseNote( "Azimuth < 0" , ParseStatus.WARNING ) );
		vector.incFs = 23.5;
		vector.incBs = 26.0;
		vector.incFsBsText = new ParsedText( "23.5  /26.0" , null );

		shot0.vector = vector;
		shot0.from = "A1";
		shot0.custom = new Object[ 1 ];
		shot0.custom[ 0 ] = new ParsedTextWithValue( "3.502" , null , 3.502 );

		Shot shot1 = new Shot( );

		NedShotVector vect1 = new NedShotVector( );
		vect1.n = 100.0;
		vect1.v = 205.0;
		vect1.nText = new ParsedText( "100" , null );
		vect1.vText = new ParsedText( "205" , null );

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

		ShotTableModel presenter = new ShotTableModel( i18n , new DefaultBinder<>( model ) );
		model.set( NewProjectModel.decimalSep , ',' );

		presenter.setShotList( shotList );

		JTable table = new JTable( presenter );
		table.setFont( new Font( "Monospaced" , Font.PLAIN , 11 ) );
		JScrollPane scrollPane = new JScrollPane( table );

		shotList.add( shot1 );

		ShotTableColumnModel columnModel = new ShotTableColumnModel( i18n );
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
			ShotColumnDef.offsD ,
			new ShotColumnDef( "Test" , ShotColumnType.INTEGER ) ,
			new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE )
			) );

		table.setColumnModel( columnModel );

		QuickTestFrame.frame( scrollPane ).setVisible( true );
	}
}

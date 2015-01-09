package org.andork.breakout.table;

import java.awt.Font;
import java.util.Arrays;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.andork.bind2.DefaultBinder;
import org.andork.i18n.I18n;
import org.andork.q2.QArrayObject;
import org.andork.q2.QObject;
import org.andork.q2.QObjectBinder;
import org.andork.swing.QuickTestFrame;

public class ShotTableTest
{
	public static void main( String[ ] args )
	{
		I18n i18n = new I18n( );

		Shot shot0 = new Shot( );

		ShotVector.Dai.c vector0 = new ShotVector.Dai.c( );
		ShotVectorText.Dai.SplitAngles vectorText0 = new ShotVectorText.Dai.SplitAngles( );

		vectorText0.distText = new ParsedText( "a5.00" ,
			new ParseNote( ParseStatus.ERROR ) {
				@Override
				public String apply( I18n t )
				{
					return t.forClass( ParseNote.class ).getFormattedString( "invalidChar" , "a" );
				}
			} );
		vector0.azmFs = -30.0;
		vectorText0.azmFsText = new ParsedText( "-30.00" ,
			ParseNote.forMessageKey( ParseStatus.WARNING , "negativeAzm" ) );
		vector0.incFs = 23.5;
		vector0.incBs = 26.0;
		vectorText0.incFsText = new ParsedText( "23.50" , null );
		vectorText0.incBsText = new ParsedText( "26.00" , null );

		shot0.vector = vector0;
		shot0.vectorText = vectorText0;
		shot0.from = "A1";
		shot0.custom = new Object[ 1 ];
		shot0.custom[ 0 ] = new ParsedTextWithValue( "3.502" , null , 3.502 );

		Shot shot1 = new Shot( );

		ShotVector.Nev.d vector1 = new ShotVector.Nev.d( );
		vector1.n = 100.0;
		vector1.v = 205.0;

		ShotVectorText.Nev vectorText1 = new ShotVectorText.Nev( );
		vectorText1.nText = new ParsedText( "100" , null );
		vectorText1.eText = new ParsedText( "205" , null );

		shot1.vector = vector1;
		shot1.vectorText = vectorText1;
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

		QObject<ProjectModel> projModel = QArrayObject.create( ProjectModel.spec );
		QObjectBinder<ProjectModel> projModelBinder = new QObjectBinder<ProjectModel>( ProjectModel.spec );
		projModelBinder.objLink.bind( new DefaultBinder<>( projModel ) );

		ShotTableLogic logic = new ShotTableLogic( );
		logic.dataDefaultsLink( ).bind( projModelBinder.property( ProjectModel.defaults ) );

		ShotTableModel tableModel = new ShotTableModel( i18n , logic );
		tableModel.dataDefaultsLink( ).bind( projModelBinder.property( ProjectModel.defaults ) );
		tableModel.shotListLink( ).bind( projModelBinder.property( ProjectModel.shotList ) );

		projModel.get( ProjectModel.defaults ).set( DataDefaults.decimalSep , ',' );
		projModel.set( ProjectModel.shotList , shotList );

		JTable table = new JTable( tableModel );
		table.setFont( new Font( "Monospaced" , Font.PLAIN , 11 ) );
		JScrollPane scrollPane = new JScrollPane( table );

		shotList.add( shot1 );

		ShotTableFormats formats = new ShotTableFormats( i18n );
		formats.defaultsLink( ).bind( projModelBinder.property( ProjectModel.defaults ) );

		ShotTableColumnModel columnModel = new ShotTableColumnModel( i18n , formats );
		columnModel.update( tableModel , Arrays.asList(
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
			ShotColumnDef.offsV ,
			new ShotColumnDef( "Test" , ShotColumnType.INTEGER ) ,
			new ShotColumnDef( "Water Level" , ShotColumnType.DOUBLE )
			) );

		table.setColumnModel( columnModel );
		table.setRowHeight( 20 );

		QuickTestFrame.frame( scrollPane ).setVisible( true );
	}
}

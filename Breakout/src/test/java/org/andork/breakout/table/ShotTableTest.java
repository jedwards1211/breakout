package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.andork.bind2.DefaultBinder;
import org.andork.breakout.model.NewProjectModel;
import org.andork.q2.QArrayObject;
import org.andork.q2.QHashMapObject;
import org.andork.q2.QObject;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.table.QObjectList;

public class ShotTableTest
{
	public static void main( String[ ] args )
	{
		QObjectList<Shot> shotList = new QObjectList<Shot>( Shot.spec , Shot.index );
		QObjectList<ShotText> shotTextList = new QObjectList<ShotText>( ShotText.spec , ShotText.index );

		QObject<Shot> shot0 = QArrayObject.create( Shot.spec );
		QObject<ShotText> shotText0 = QHashMapObject.create( ShotText.spec );

		shot0.set( Shot.vector , new DistAzmIncShotVector( 5.0 , 10.0 , null , 23.5 , 26.0 ) );
		shot0.set( Shot.from , "A1" );
		shotText0.set( ShotText.dist , new ParsedText( "5.0" , null ) );
		shotText0.set( ShotText.azmFs , new ParsedText( "10.00" , null ) );
		shotText0.set( ShotText.incFsBs , new ParsedText( "23.5  /26.0" , null ) );

		shotList.add( shot0 );
		shotTextList.add( shotText0 );

		QObject<NewProjectModel> model = QArrayObject.create( NewProjectModel.spec );

		ShotTablePresenter presenter = new ShotTablePresenter( new DefaultBinder<>( model ) );
		presenter.setColumns( Arrays.asList(
			presenter.fromColumn ,
			presenter.toColumn ,
			presenter.distColumn ,
			presenter.azmFsColumn ,
			presenter.azmBsColumn ,
			presenter.azmFsBsColumn ,
			presenter.incFsColumn ,
			presenter.incBsColumn ,
			presenter.incFsBsColumn ) );

		model.set( NewProjectModel.decimalSeparator , ',' );
		model.set( NewProjectModel.shotList , shotList );
		model.set( NewProjectModel.shotTextList , shotTextList );

		JTable table = new JTable( presenter );
		JScrollPane scrollPane = new JScrollPane( table );

		QuickTestFrame.frame( scrollPane ).setVisible( true );
	}
}

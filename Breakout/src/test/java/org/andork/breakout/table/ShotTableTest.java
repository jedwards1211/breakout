package org.andork.breakout.table;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.andork.bind2.DefaultBinder;
import org.andork.breakout.model.NewProjectModel;
import org.andork.q2.QArrayObject;
import org.andork.q2.QHashMap;
import org.andork.q2.QHashMapObject;
import org.andork.q2.QObject;
import org.andork.q2.QSpec.Property;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.table.QObjectList;

public class ShotTableTest
{
	public static void main( String[ ] args )
	{
		class CustShot extends Shot
		{
			private CustShot( )
			{
				super( property( "cust9" , Double.class ) );
			}
		}

		CustShot shotSpec = new CustShot( );

		class CustShotText extends ShotText
		{
			private CustShotText( )
			{
				super( property( "cust9" , ParsedText.class ) );
			}
		}

		CustShotText shotTextSpec = new CustShotText( );

		QObjectList<Shot> shotList = new QObjectList<Shot>( shotSpec , Shot.index );
		QObjectList<ShotText> shotTextList = new QObjectList<ShotText>( shotTextSpec , ShotText.index );

		QObject<Shot> shot0 = QArrayObject.create( shotSpec );
		QObject<ShotText> shotText0 = QHashMapObject.create( shotTextSpec );

		shot0.set( Shot.vector , new DistAzmIncShotVector( 5.0 , 10.0 , null , 23.5 , 26.0 ) );
		shot0.set( Shot.from , "A1" );
		shot0.set( shotSpec.propertyNamed( "cust9" ).cast( Double.class ) , 3.5 );
		shotText0.set( ShotText.dist , new ParsedText( "5.0" , null ) );
		shotText0.set( ShotText.azmFs , new ParsedText( "10.00" , null ) );
		shotText0.set( ShotText.incFsBs , new ParsedText( "23.5  /26.0" , null ) );
		shotText0.set( shotTextSpec.propertyNamed( "cust9" ).cast( ParsedText.class ) , new ParsedText( "3.52" ,
			null ) );

		shotList.add( shot0 );
		shotTextList.add( shotText0 );

		QObject<NewProjectModel> model = QArrayObject.create( NewProjectModel.spec );

		ShotTableModelPresenter presenter = new ShotTableModelPresenter( new DefaultBinder<>( model ) );
		model.set( NewProjectModel.decimalSep , ',' );
		model.set( NewProjectModel.shotList , shotList );
		model.set( NewProjectModel.shotTextList , shotTextList );

		QHashMap<Integer, ShotTableColumn> shotCols = QHashMap.newInstance( );
		model.set( NewProjectModel.shotCols , shotCols );
		shotCols.put( 0 , new ShotTableColumn( ShotTableColumnNames.from , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 1 , new ShotTableColumn( ShotTableColumnNames.to , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 2 , new ShotTableColumn( ShotTableColumnNames.dist , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 3 , new ShotTableColumn( ShotTableColumnNames.azmFs , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 4 , new ShotTableColumn( ShotTableColumnNames.azmBs , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 5 , new ShotTableColumn( ShotTableColumnNames.azmFsBs , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 6 , new ShotTableColumn( ShotTableColumnNames.incFs , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 7 , new ShotTableColumn( ShotTableColumnNames.incBs , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 8 , new ShotTableColumn( ShotTableColumnNames.incFsBs , ShotTableColumnType.BUILTIN ) );
		shotCols.put( 9 , new ShotTableColumn( "Water Level" , ShotTableColumnType.DOUBLE ) );

		JTable table = new JTable( presenter );
		JScrollPane scrollPane = new JScrollPane( table );

		QuickTestFrame.frame( scrollPane ).setVisible( true );
	}
}

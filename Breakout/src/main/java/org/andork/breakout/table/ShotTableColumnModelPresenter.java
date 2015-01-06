package org.andork.breakout.table;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class ShotTableColumnModelPresenter extends DefaultTableColumnModel
{
	public final TableColumn						fromColumn;
	public final TableColumn						toColumn;
	public final TableColumn						vectorColumn;
	public final TableColumn						distColumn;
	public final TableColumn						azmFsBsColumn;
	public final TableColumn						azmFsColumn;
	public final TableColumn						azmBsColumn;
	public final TableColumn						incFsBsColumn;
	public final TableColumn						incFsColumn;
	public final TableColumn						incBsColumn;
	public final TableColumn						offsNColumn;
	public final TableColumn						offsEColumn;
	public final TableColumn						offsDColumn;

	private final Map<ShotColumnDef, TableColumn>	builtInColumns	= new HashMap<>( );

	private final Map<ParseStatus, Color>			noteColors;

	public ShotTableColumnModelPresenter( )
	{
		noteColors = new HashMap<>( );
		noteColors.put( ParseStatus.WARNING , Color.YELLOW );
		noteColors.put( ParseStatus.ERROR , Color.RED );

		Function<Object, Color> noteColor =
			n -> n instanceof ParseNote ? noteColors.get( ( ( ParseNote ) n ).status ) : null;
		Function<Object, String> noteMessage =
			n -> n instanceof ParseNote ? ( ( ParseNote ) n ).messageKey : null;

		fromColumn = new TableColumn( );
		fromColumn.setIdentifier( ShotColumnDef.from );

		toColumn = new TableColumn( );
		toColumn.setIdentifier( ShotColumnDef.to );

		ParsedTextTableCellRenderer parsedTextRenderer = new ParsedTextTableCellRenderer( noteColor , noteMessage );

		vectorColumn = new TableColumn( );
		vectorColumn.setIdentifier( ShotColumnDef.vector );
		ParsedTextWithTypeTableCellRenderer vectorRenderer =
			new ParsedTextWithTypeTableCellRenderer( parsedTextRenderer );
		vectorRenderer.setAvailableTypes( Arrays.asList( DaiShotVector.class , OffsetShotVector.class ) );
		vectorColumn.setCellRenderer( vectorRenderer );

		distColumn = new TableColumn( );
		distColumn.setIdentifier( ShotColumnDef.dist );
		distColumn.setCellRenderer( parsedTextRenderer );

		azmFsBsColumn = new TableColumn( );
		azmFsBsColumn.setIdentifier( ShotColumnDef.azmFsBs );
		azmFsBsColumn.setCellRenderer( parsedTextRenderer );

		azmFsColumn = new TableColumn( );
		azmFsColumn.setIdentifier( ShotColumnDef.azmFs );
		azmFsColumn.setCellRenderer( parsedTextRenderer );

		azmBsColumn = new TableColumn( );
		azmBsColumn.setIdentifier( ShotColumnDef.azmBs );
		azmBsColumn.setCellRenderer( parsedTextRenderer );

		incFsBsColumn = new TableColumn( );
		incFsBsColumn.setIdentifier( ShotColumnDef.incFsBs );
		incFsBsColumn.setCellRenderer( parsedTextRenderer );

		incFsColumn = new TableColumn( );
		incFsColumn.setIdentifier( ShotColumnDef.incFs );
		incFsColumn.setCellRenderer( parsedTextRenderer );

		incBsColumn = new TableColumn( );
		incBsColumn.setIdentifier( ShotColumnDef.incBs );
		incBsColumn.setCellRenderer( parsedTextRenderer );

		offsNColumn = new TableColumn( );
		offsNColumn.setIdentifier( ShotColumnDef.offsN );
		offsNColumn.setCellRenderer( parsedTextRenderer );

		offsEColumn = new TableColumn( );
		offsEColumn.setIdentifier( ShotColumnDef.offsE );
		offsEColumn.setCellRenderer( parsedTextRenderer );

		offsDColumn = new TableColumn( );
		offsDColumn.setIdentifier( ShotColumnDef.offsD );
		offsDColumn.setCellRenderer( parsedTextRenderer );

		for( TableColumn column : Arrays.asList(
			fromColumn ,
			toColumn ,
			vectorColumn ,
			distColumn ,
			azmFsBsColumn ,
			azmFsColumn ,
			azmBsColumn ,
			incFsBsColumn ,
			incFsColumn ,
			incBsColumn ,
			offsNColumn ,
			offsEColumn ,
			offsDColumn ) )
		{
			builtInColumns.put( ( ShotColumnDef ) column.getIdentifier( ) , column );
		}
	}

	public void update( ShotTableModelPresenter model , List<ShotColumnDef> columnDefs )
	{
		while( getColumnCount( ) > 0 )
		{
			removeColumn( getColumn( 0 ) );
		}

		for( ShotColumnDef def : columnDefs )
		{
			if( def.type == ShotColumnType.BUILTIN )
			{
				TableColumn column = builtInColumns.get( def );
				int index = model.indexOfColumn( def );
				if( column != null && index >= 0 )
				{
					column.setModelIndex( index );
					column.setHeaderValue( def );
					addColumn( column );
				}
			}
		}
	}
}

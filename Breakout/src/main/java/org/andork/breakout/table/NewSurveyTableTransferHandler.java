package org.andork.breakout.table;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.andork.collect.InputStreamLineIterable;
import org.andork.collect.LineIterable;
import org.andork.io.FileStreamFlattener;
import org.andork.swing.OnEDT;

public class NewSurveyTableTransferHandler extends TransferHandler
{
	Consumer<Runnable>	backgroundRunner;
	
	public NewSurveyTableTransferHandler( Consumer<Runnable> backgroundRunner )
	{
		super( );
		this.backgroundRunner = backgroundRunner;
	}
	
	@Override
	public boolean importData( TransferSupport support )
	{
		if( !canImport( support ) )
		{
			return false;
		}
		
		NewSurveyTable table = ( NewSurveyTable ) support.getComponent( );
		JTable.DropLocation location = table.getDropLocation( );
		
		try
		{
			if( support.isDataFlavorSupported( DataFlavor.stringFlavor ) )
			{
				String data = ( String ) support.getTransferable( ).getTransferData( DataFlavor.stringFlavor );
				backgroundRunner.accept( ( ) -> {
					ByteArrayInputStream in = new ByteArrayInputStream( data.getBytes( ) );
					Stream<String> lines = StreamSupport.stream( InputStreamLineIterable.linesOf( in ).spliterator( ) , false );
					
					importData( lines , table , location );
				} );
				return true;
			}
			else if( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) )
			{
				List<File> files = ( List<File> ) support.getTransferable( ).getTransferData( DataFlavor.javaFileListFlavor );
				backgroundRunner.accept( ( ) -> {
					Stream<String> lines = files.stream( ).flatMap( FileStreamFlattener.instance ).flatMap( file ->
							StreamSupport.stream( LineIterable.linesOf( file ).spliterator( ) , false ) );
					
					importData( lines , table , location );
				} );
				return true;
			}
		}
		catch( IOException e )
		{
			e.printStackTrace( );
		}
		catch( UnsupportedFlavorException e )
		{
			e.printStackTrace( );
		}
		
		return false;
	}
	
	private boolean importData( Stream<String> lines , NewSurveyTable table , JTable.DropLocation location )
	{
		List<Object[ ]> block = new ArrayList<>( );
		
		lines.forEach( line -> {
			String[ ] split = line.split( "\t" );
			Object[ ] row = new Object[ split.length ];
			for( int i = 0 ; i < split.length ; i++ )
			{
				row[ i ] = split[ i ];
			}
			block.add( row );
		} );
		
		OnEDT.onEDT( ( ) -> {
			NewSurveyTableModel model = table.getModel( );
			
			int dropColumnIndex = location.getColumn( );
			int dropRowIndex = location.getRow( );
			
			model.blockSetValues(
					block ,
					srcRowIndex -> {
						return srcRowIndex + dropRowIndex >= table.getRowCount( ) ?
								srcRowIndex + dropRowIndex - table.getRowCount( ) + model.getRowCount( ) :
								table.convertRowIndexToModel( srcRowIndex + dropRowIndex );
					} ,
					srcColumnIndex -> {
						return srcColumnIndex + dropColumnIndex >= table.getColumnCount( ) ?
								srcColumnIndex + dropColumnIndex - table.getColumnCount( ) + model.getColumnCount( ) :
								table.convertColumnIndexToModel( srcColumnIndex + dropColumnIndex );
					} );
		} );
		
		return true;
	}
	
	@Override
	public boolean canImport( TransferSupport support )
	{
		if( !( support.getComponent( ) instanceof NewSurveyTable ) )
		{
			return false;
		}
		
		return support.isDataFlavorSupported( DataFlavor.stringFlavor ) ||
				support.isDataFlavorSupported( DataFlavor.javaFileListFlavor );
	}
}

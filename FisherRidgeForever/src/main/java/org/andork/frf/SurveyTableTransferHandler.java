package org.andork.frf;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import org.andork.frf.SurveyTableModel.SurveyTableModelCopier;
import org.andork.swing.AnnotatingRowSorter;

public class SurveyTableTransferHandler extends TransferHandler
{
	@Override
	public boolean canImport( TransferSupport support )
	{
		return support.isDataFlavorSupported( DataFlavor.stringFlavor ) || support.isDataFlavorSupported( DataFlavor.javaFileListFlavor );
	}
	
	@Override
	public boolean importData( TransferSupport support )
	{
		String text = null;
		try
		{
			if( support.isDataFlavorSupported( DataFlavor.stringFlavor ) )
			{
				text = ( String ) support.getTransferable( ).getTransferData( DataFlavor.stringFlavor );
			}
			else if( support.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) )
			{
				text = getTextFromFileList( support );
			}
			else
			{
				return false;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
			return false;
		}
		
		SurveyTable table = ( SurveyTable ) support.getComponent( );
		
		// TableModel model = getModel( );
		SurveyTableModelCopier copier = new SurveyTableModelCopier( );
		SurveyTableModel oldModel = table.getModel( );
		SurveyTableModel newModel = copier.createEmptyCopy( oldModel );
		for( int row = 0 ; row < oldModel.getRowCount( ) ; row++ )
		{
			copier.copyRow( oldModel , row , newModel );
		}
		
		JTable.DropLocation dropLocation = ( JTable.DropLocation ) support.getDropLocation( );
		
		int row = dropLocation.getRow( );
		for( String line : text.split( "\r|\n|\r\n|\n\r" ) )
		{
			int column = dropLocation.getColumn( );
			for( String cell : line.split( "\\t" ) )
			{
				newModel.setValueAt( cell , row , column );
				column++ ;
			}
			
			row++ ;
		}
		
		AnnotatingRowSorter<SurveyTableModel, Integer, RowFilter<SurveyTableModel, Integer>> sorter =
				( AnnotatingRowSorter<SurveyTableModel, Integer, RowFilter<SurveyTableModel, Integer>> ) table.getRowSorter( );
		
		table.setRowSorter( null );
		table.setModel( newModel );
		sorter.setModel( newModel );
		table.setRowSorter( sorter );
		
		return true;
	}
	
	private String getTextFromFileList( TransferSupport support ) throws IOException , UnsupportedFlavorException
	{
		List<File> files = null;
		files = ( List<File> ) support.getTransferable( ).getTransferData( DataFlavor.javaFileListFlavor );
		
		if( files.isEmpty( ) )
		{
			return null;
		}
		
		StringBuilder sb = new StringBuilder( );
		
		BufferedReader reader = new BufferedReader( new FileReader( files.get( 0 ) ) );
		
		try
		{
			String line;
			while( ( line = reader.readLine( ) ) != null )
			{
				sb.append( line ).append( '\n' );
			}
		}
		finally
		{
			if( reader != null )
			{
				reader.close( );
			}
		}
		
		return sb.toString( );
	}
}

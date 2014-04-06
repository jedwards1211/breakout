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
import org.andork.swing.AnnotatingRowSorter.ModelCopier;

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
		SurveyTableModel model = table.getModel( );
		
		SurveyTableModel newModel = new SurveyTableModel( );
		for( int row = 0 ; row < model.getRowCount( ) ; row++ )
		{
			for( int col = 0 ; col < model.getColumnCount( ) - 1 ; col++ ) // exclude shot column
			{
				newModel.setValueAt( model.getValueAt( row , col ) , row , col );
			}
		}
		
		JTable.DropLocation dropLocation = ( JTable.DropLocation ) support.getDropLocation( );
		
		String[ ] lines = text.split( "\r|\n|\r\n|\n\r" );
		int viewRow = dropLocation.getRow( );
		for( String line : lines )
		{
			int modelRow;
			if( viewRow >= table.getRowCount( ) )
			{
				modelRow = newModel.getRowCount( ) - 1;
			}
			else
			{
				modelRow = table.convertRowIndexToModel( viewRow );
			}
			
			int viewColumn = dropLocation.getColumn( );
			for( String cell : line.split( "\\t" ) )
			{
				if( viewColumn >= table.getColumnCount( ) )
				{
					break;
				}
				int modelColumn = table.convertColumnIndexToModel( viewColumn );
				newModel.setValueAt( cell , modelRow , modelColumn );
				viewColumn++ ;
			}
			
			viewRow++ ;
		}
		
		model.copyRowsFrom( newModel , 0 , newModel.getRowCount( ) - 1 , 0 );
		
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

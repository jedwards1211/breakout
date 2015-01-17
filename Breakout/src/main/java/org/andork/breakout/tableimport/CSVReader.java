package org.andork.breakout.tableimport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.Vector;

import javax.swing.table.TableModel;

import org.andork.io.CSVFormat;
import org.andork.swing.async.Subtask;

public class CSVReader
{
	public static TableModel
		read( SeekableByteChannel channel , String charsetName , CSVFormat csvFormat , Subtask task )
			throws IOException
	{
		BufferedReader reader = new BufferedReader( Channels.newReader( channel , charsetName ) );

		if( task != null )
		{
			task.setCompleted( 0 );
			task.setTotal( 10000 );
			task.setIndeterminate( false );
		}

		Vector<Vector<String>> rows = new Vector<>( );

		int numColumns = 0;

		String line;
		while( ( line = reader.readLine( ) ) != null )
		{
			Vector<String> row = new Vector<>( );
			csvFormat.parseLine( line , row );
			numColumns = Math.max( numColumns , row.size( ) );
			rows.add( row );

			if( task != null )
			{
				if( task.isCanceling( ) )
				{
					return null;
				}
				task.setCompleted( ( int ) ( task.getTotal( ) * ( channel.position( ) ) / channel.size( ) ) );
			}
		}

		Vector<Object> columnNames = new Vector<>( );

		for( int i = 0 ; i < numColumns ; i++ )
		{
			columnNames.add( ( char ) ( 'A' + i ) );
		}

		return new IrregularDefaultTableModel( rows , columnNames );
	}
}

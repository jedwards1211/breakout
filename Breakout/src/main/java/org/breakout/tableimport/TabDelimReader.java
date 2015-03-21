package org.breakout.tableimport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.Vector;

import javax.swing.table.TableModel;

import org.andork.swing.async.Subtask;

public class TabDelimReader
{
	public static TableModel read( SeekableByteChannel channel , String charsetName , Subtask task ) throws IOException
	{
		BufferedReader reader = new BufferedReader( Channels.newReader( channel , charsetName ) );

		if( task != null )
		{
			task.setCompleted( 0 );
			task.setTotal( 10000 );
			task.setIndeterminate( false );
		}

		Vector<Vector<Object>> rows = new Vector<>( );
		Vector<Object> currentRow = new Vector<>( );
		StringBuilder currentCell = new StringBuilder( );

		int numColumns = 0;

		int c = reader.read( );
		while( c >= 0 )
		{
			for( int i = 0 ; i < 100 && c >= 0 ; i++ )
			{
				switch( c )
				{
				case '\t':
					currentRow.add( currentCell.toString( ) );
					currentCell = new StringBuilder( );
					break;
				case '\r':
				case '\n':
					currentRow.add( currentCell.toString( ) );
					currentCell = new StringBuilder( );
					rows.add( currentRow );
					numColumns = Math.max( numColumns , currentRow.size( ) );
					currentRow = new Vector<>( );

					c = reader.read( );
					if( c != '\r' && c != '\n' )
					{
						// let next iteration handle this character
						continue;
					}
					break;
				default:
					currentCell.append( ( char ) c );
					break;
				}
				c = reader.read( );
			}
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

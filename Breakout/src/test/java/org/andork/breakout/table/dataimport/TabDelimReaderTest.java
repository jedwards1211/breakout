package org.andork.breakout.table.dataimport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.andork.breakout.tableimport.TabDelimReader;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.async.SelfReportingTask;
import org.andork.swing.async.SingleThreadedTaskService;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.TaskService;
import org.junit.Test;

public class TabDelimReaderTest
{
	@Test
	public void testRead( ) throws InterruptedException
	{
		final Path path = Paths.get( ".breakout/defaultProject-survey.txt" );

		SelfReportingTask task = new SelfReportingTask( null ) {
			TableModel	tableModel;

			@Override
			protected void duringDialog( ) throws Exception
			{
				setTotal( 1000 );

				Subtask subtask = new Subtask( this );
				subtask.setStatus( "Reading " + path + "..." );

				try( SeekableByteChannel channel = Files.newByteChannel( path , StandardOpenOption.READ ) )
				{
					tableModel = TabDelimReader.read( channel , Charset.defaultCharset( ).name( ) , subtask );
					subtask.end( );
				}
				catch( IOException ex )
				{
					ex.printStackTrace( );
				}

				System.out.println( tableModel.getRowCount( ) );

				JTable table = new JTable( tableModel );
				JScrollPane scrollPane = new JScrollPane( table );
				QuickTestFrame.frame( scrollPane ).setVisible( true );
				Thread.sleep( 60000 );
			}

			public boolean isCancelable( )
			{
				return true;
			}
		};

		TaskService service = new SingleThreadedTaskService( );
		service.submit( task );

		task.waitUntilHasEnded( );
	}
}

/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.breakout.table;

import java.awt.Color;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.table.NewSurveyTableModel.NewSurveyTableModelCopier;
import org.andork.format.FormatWarning;
import org.andork.io.KVLiteChannel;
import org.andork.io.KVLiteChannel.Record;
import org.andork.swing.OnEDT;
import org.andork.swing.QuickTestFrame;
import org.andork.swing.async.SingleThreadedTaskService;
import org.andork.swing.async.Task;
import org.andork.swing.async.TaskService;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;
import org.andork.swing.table.FormattedTextTableRowAnnotator;
import org.andork.util.Batcher2;

public class NewSurveyTableTest
{
	public static void main( String[ ] args ) throws IOException
	{
		FileChannel fileChannel = FileChannel.open( Paths.get( "saved-table.bkv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE , StandardOpenOption.CREATE );
		TaskService saveTaskService = new SingleThreadedTaskService( );
		KVLiteChannel channel = null;
		try
		{
			channel = KVLiteChannel.load( fileChannel );
		}
		catch( Exception ex )
		{
			channel = new KVLiteChannel( fileChannel , 64 );
		}
		
		final KVLiteChannel finalChannel = channel;
		
		Batcher2<List<Record>> writeQueue = new Batcher2<>(
				runnable -> saveTaskService.submit( new Task( )
				{
					@Override
					protected void execute( ) throws Exception
					{
						runnable.run( );
					}
				} ) ,
				records -> {
					List<Record> flatRecords = new LinkedList<Record>( );
					for( List<Record> list : records )
					{
						flatRecords.addAll( list );
					}
					try
					{
						finalChannel.write( flatRecords );
					}
					catch( Exception ex )
					{
						throw new RuntimeException( ex );
					}
				} );
		
		String prefix = "t";
		
		NewSurveyTablePersister persister = new NewSurveyTablePersister( "cols" , "firstShot" , "shot" , writeQueue );
		NewSurveyTableModel model;
		try
		{
			model = persister.load( channel );
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
			model = new NewSurveyTableModel( );
		}
		
		NewSurveyTableModel finalModel = model;
		
		OnEDT.onEDT( ( ) -> {
			NewSurveyTable table = new NewSurveyTable( finalModel == null ? new NewSurveyTableModel( ) : finalModel );
			persister.setTable( table );
			if( finalModel == null )
			{
				persister.rewriteAll( );
			}
			
			ExecutorService executor = Executors.newSingleThreadExecutor( );
			DefaultAnnotatingJTableSetup setup = new DefaultAnnotatingJTableSetup( table , r -> executor.submit( r ) );
			FormattedTextTableRowAnnotator annotator = new FormattedTextTableRowAnnotator( );
			annotator.setCombiner( ( e1 , e2 ) -> e1 instanceof FormatWarning ? e2 : e1 );
			AnnotatingTableRowSorter<NewSurveyTableModel> sorter = ( AnnotatingTableRowSorter<NewSurveyTableModel> ) table.getRowSorter( );
			sorter.setModelCopier( new NewSurveyTableModelCopier( ) );
			sorter.setSortsOnUpdates( true );
			( ( AnnotatingTableRowSorter<SurveyTableModel> ) table.getRowSorter( ) ).setRowAnnotator( annotator );
			setup.scrollPane.getJumpBar( ).setColorer( a -> {
				if( a instanceof FormatWarning )
				{
					return Color.YELLOW;
				}
					else if( a instanceof Exception )
					{
						return Color.RED;
					}
					return null;
				} );
			
			setup.scrollPane.setTopRightCornerComp( new JButton( new CustomizeNewSurveyTableAction( table ) ) );
			
			table.setTransferHandler( new NewSurveyTableTransferHandler( r -> executor.submit( r ) ) );
			
			QuickTestFrame.frame( setup.scrollPane ).setVisible( true );
		} );
	}
}

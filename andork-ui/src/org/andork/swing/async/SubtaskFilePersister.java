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
package org.andork.swing.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.event.SourcePath;
import org.andork.func.Bimapper;
import org.andork.func.BimapperStreamBimapper;
import org.andork.func.StreamBimapper;
import org.andork.util.Batcher;

public abstract class SubtaskFilePersister<M> implements HierarchicalBasicPropertyChangeListener
{
	protected SubtaskStreamBimapperFactory<M, SubtaskStreamBimapper<M>>	bimapperFactory;
	protected Batcher<M>												batcher;
	protected File														file;
	
	public SubtaskFilePersister( File file , SubtaskStreamBimapperFactory<M, SubtaskStreamBimapper<M>> bimapperFactory )
	{
		this.file = file;
		this.bimapperFactory = bimapperFactory;
		
		batcher = new Batcher<M>( )
		{
			@Override
			protected void handleLater( final LinkedList<M> batch )
			{
				saveInBackground( batch );
			}
		};
	}
	
	public File getFile( )
	{
		return file;
	}
	
	public void saveLater( M model )
	{
		batcher.add( model );
	}
	
	protected abstract void saveInBackground( final LinkedList<M> batch );
	
	protected void save( M model , Task task )
	{
		Subtask subtask = Subtask.defaultCreate( task );
		
		try
		{
			if( !file.getParentFile( ).exists( ) )
			{
				file.getParentFile( ).mkdirs( );
			}
			SubtaskStreamBimapper<M> bimapper = bimapperFactory.createSubtaskStreamBimapper( subtask );
			bimapper.write( model , new FileOutputStream( file ) );
		}
		catch( Throwable t )
		{
			t.printStackTrace( );
		}
	}
	
	public M load( Task task ) throws Exception
	{
		Subtask subtask = Subtask.defaultCreate( task );
		
		if( !file.exists( ) )
		{
			return null;
		}
		SubtaskStreamBimapper<M> bimapper = bimapperFactory.createSubtaskStreamBimapper( subtask );
		return bimapper.read( new FileInputStream( file ) );
	}
	
	private M getRootModel( Object source )
	{
		if( source instanceof SourcePath )
		{
			return getRootModel( ( M ) ( ( SourcePath ) source ).parent );
		}
		return ( M ) source;
	}
	
	@Override
	public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		batcher.add( getRootModel( source ) );
	}
	
	@Override
	public void childrenChanged( Object source , ChangeType changeType , Object ... children )
	{
		batcher.add( getRootModel( source ) );
	}
	
}
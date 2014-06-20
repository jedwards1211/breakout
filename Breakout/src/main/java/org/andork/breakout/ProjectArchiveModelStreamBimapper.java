package org.andork.breakout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.breakout.model.ProjectArchiveModel;
import org.andork.breakout.model.ProjectModel;
import org.andork.breakout.model.SurveyTableModel;
import org.andork.func.BimapperStreamBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.swing.FromEDT;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.SubtaskStreamBimapper;

public class ProjectArchiveModelStreamBimapper extends SubtaskStreamBimapper<ProjectArchiveModel>
{
	private final Localizer	localizer;
	
	public ProjectArchiveModelStreamBimapper( final I18n i18n , Subtask subtask )
	{
		super( subtask );
		localizer = new FromEDT<Localizer>( )
		{
			@Override
			public Localizer run( ) throws Throwable
			{
				return i18n.forClass( ProjectArchiveModelStreamBimapper.class );
			}
		}.result( );
	}
	
	@Override
	public void write( final ProjectArchiveModel model , OutputStream out ) throws Exception
	{
		ZipOutputStream zipOut = null;
		try
		{
			zipOut = new ZipOutputStream( out );
			zipOut.setMethod( ZipOutputStream.DEFLATED );
			zipOut.putNextEntry( new ZipEntry( "project.yaml" ) );
			
			new BimapperStreamBimapper( QObjectBimappers.defaultBimapper( ProjectModel.defaultMapper ) , false )
					.write( model.getProjectModel( ) , zipOut );
			
			zipOut.putNextEntry( new ZipEntry( "project-survey.txt" ) );
			
			new SurveyTableModelStreamBimapper( subtask( ) ).closeStreams( false ).makeCopy( false )
					.write( model.getSurveyTableModel( ) , zipOut );
		}
		finally
		{
			if( zipOut != null )
			{
				zipOut.close( );
			}
		}
	}
	
	@Override
	public ProjectArchiveModel read( InputStream in ) throws Exception
	{
		QObject<ProjectModel> projectModel = null;
		SurveyTableModel surveyTableModel = null;
		
		ZipInputStream zipIn = null;
		try
		{
			zipIn = new ZipInputStream( in );
			ZipEntry zipEntry;
			while( ( zipEntry = zipIn.getNextEntry( ) ) != null )
			{
				if( zipEntry.getName( ).equals( "project.yaml" ) )
				{
					projectModel = new BimapperStreamBimapper<QObject<ProjectModel>>(
							QObjectBimappers.defaultBimapper( ProjectModel.defaultMapper ) , false ).read( zipIn );
				}
				else if( zipEntry.getName( ).equals( "project-survey.txt" ) )
				{
					surveyTableModel = new SurveyTableModelStreamBimapper( subtask( ) )
							.closeStreams( false ).makeCopy( false ).read( zipIn );
				}
			}
		}
		finally
		{
			if( zipIn != null )
			{
				zipIn.close( );
			}
		}
		
		if( projectModel == null )
		{
			throw new IOException( localizer.getString( "projectEntryNotFound.exception.message" ) );
		}
		if( surveyTableModel == null )
		{
			throw new IOException( localizer.getString( "surveyEntryNotFound.exception.message" ) );
		}
		
		return new ProjectArchiveModel( projectModel , surveyTableModel );
	}
}

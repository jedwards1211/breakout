package org.andork.breakout.model;

import org.andork.q.QObject;

public class ProjectArchiveModel
{
	QObject<ProjectModel>	projectModel;
	SurveyTableModel			surveyTableModel;
	
	public ProjectArchiveModel( QObject<ProjectModel> projectModel , SurveyTableModel surveyTableModel )
	{
		super( );
		this.projectModel = projectModel;
		this.surveyTableModel = surveyTableModel;
	}
	
	public QObject<ProjectModel> getProjectModel( )
	{
		return projectModel;
	}
	
	public SurveyTableModel getSurveyTableModel( )
	{
		return surveyTableModel;
	}
}

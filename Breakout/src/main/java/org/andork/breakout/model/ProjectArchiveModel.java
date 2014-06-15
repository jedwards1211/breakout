package org.andork.breakout.model;

import org.andork.snakeyaml.YamlObject;

public class ProjectArchiveModel
{
	YamlObject<ProjectModel>	projectModel;
	SurveyTableModel			surveyTableModel;
	
	public ProjectArchiveModel( YamlObject<ProjectModel> projectModel , SurveyTableModel surveyTableModel )
	{
		super( );
		this.projectModel = projectModel;
		this.surveyTableModel = surveyTableModel;
	}
	
	public YamlObject<ProjectModel> getProjectModel( )
	{
		return projectModel;
	}
	
	public SurveyTableModel getSurveyTableModel( )
	{
		return surveyTableModel;
	}
}

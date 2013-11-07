package org.andork.survey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyTableModel
{
	final Map<String, SurveyStation>	stations	= new HashMap<String, SurveyStation>( );
	final List<SurveyShot>				shots		= new ArrayList<SurveyShot>( );
}

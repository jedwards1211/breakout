package org.andork.frf;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.RowFilter;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.frf.SurveyTableModel.SurveyTableModelCopier;
import org.andork.swing.AnnotatingRowSorter.SortRunner;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;

@SuppressWarnings( "serial" )
public class SurveyDrawer extends Drawer
{
	JLabel																							highlightLabel;
	TextComponentWithHintAndClear																	highlightField;
	JLabel																							filterLabel;
	TextComponentWithHintAndClear																	filterField;
	
	SurveyTable																						surveyTable;
	DefaultAnnotatingJTableSetup<SurveyTableModel, ? super RowFilter<SurveyTableModel, Integer>>	surveyTableSetup;
	
	public SurveyDrawer( SortRunner sortRunner )
	{
		setPreferredSize( new Dimension( 800 , 250 ) );
		
		highlightLabel = new JLabel( "Highlight: " );
		filterLabel = new JLabel( "Filter: " );
		
		highlightField = new TextComponentWithHintAndClear( "Enter regular expression" );
		filterField = new TextComponentWithHintAndClear( "Enter regular expression" );
		
		surveyTable = new SurveyTable( );
		surveyTableSetup = new DefaultAnnotatingJTableSetup<SurveyTableModel, RowFilter<SurveyTableModel, Integer>>(
				surveyTable , sortRunner );
		surveyTableSetup.table.getAnnotatingRowSorter( ).setModelCopier( new SurveyTableModelCopier( ) );
		
		delegate( ).dockingSide( Side.BOTTOM );
		mainResizeHandle( );
		pinButton( ).setText( "Survey Table" );
		pinButtonDelegate( ).corner( null ).side( Side.TOP ).insets( 5 , 0 , -5 , 0 );
		
		GridBagWizard gbw = GridBagWizard.create( this );
		
		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 2 , 2 ) );
		gbw.put( mainResizeHandle( ) ).xy( 0 , 0 ).fillx( 1.0 ).remWidth( );
		gbw.put( filterLabel ).xy( 0 , 1 ).west( ).insets( 2 , 2 , 0 , 0 );
		gbw.put( filterField ).rightOf( filterLabel ).fillboth( 1.0 , 0.0 );
		gbw.put( highlightLabel ).rightOf( filterField ).west( ).insets( 2 , 10 , 0 , 0 );
		gbw.put( highlightField ).rightOf( highlightLabel ).fillboth( 1.0 , 0.0 );
		gbw.put( maxButton( ) ).rightOf( highlightField ).east( ).filly( 0.0 );
		gbw.put( surveyTableSetup.scrollPane ).below( filterLabel , maxButton( ) ).fillboth( 0.0 , 1.0 );
	}
	
	public SurveyTable table( )
	{
		return surveyTable;
	}
	
	public TextComponentWithHintAndClear highlightField( )
	{
		return highlightField;
	}
	
	public TextComponentWithHintAndClear filterField( )
	{
		return filterField;
	}
}

package org.andork.breakout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.I18n.Localizer;
import org.andork.awt.layout.Drawer;
import org.andork.bind.DefaultBinder;
import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.SurveyTableModelCopier;
import org.andork.swing.TextComponentWithHintAndClear;
import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.AnnotatingTableRowSorter;
import org.andork.swing.table.DefaultAnnotatingJTableSetup;

@SuppressWarnings( "serial" )
public class MiniSurveyDrawer extends Drawer
{
	private JPanel							content;

	private AnnotatingJTable				table;
	private DefaultAnnotatingJTableSetup	tableSetup;

	private JLabel							filterLabel;
	private TextComponentWithHintAndClear	filterField;

	private JLabel							highlightLabel;
	private TextComponentWithHintAndClear	highlightField;

	private StatsPanel						statsPanel;

	private Localizer						localizer;

	public MiniSurveyDrawer( I18n i18n , Consumer<Runnable> sortRunner )
	{
		localizer = i18n.forClass( MiniSurveyDrawer.class );

		initComponents( sortRunner );
		initLayout( );
	}

	private void initComponents( Consumer<Runnable> sortRunner )
	{
		content = new JPanel( );

		DefaultTableColumnModel quickTableColumnModel = new DefaultTableColumnModel( );
		TableColumn fromColumn = new TableColumn( SurveyTableModel.Row.from.getIndex( ) );
		fromColumn.setIdentifier( "From" );
		fromColumn.setHeaderValue( "From" );
		TableColumn toColumn = new TableColumn( SurveyTableModel.Row.to.getIndex( ) );
		toColumn.setIdentifier( "To" );
		toColumn.setHeaderValue( "To" );
		quickTableColumnModel.addColumn( fromColumn );
		quickTableColumnModel.addColumn( toColumn );

		table = new AnnotatingJTable( new SurveyTableModel( ) , quickTableColumnModel );

		tableSetup = new DefaultAnnotatingJTableSetup( table , sortRunner );
		( ( AnnotatingTableRowSorter<SurveyTableModel> ) table.getAnnotatingRowSorter( ) )
			.setModelCopier( new SurveyTableModelCopier( ) );

		filterLabel = new JLabel( "Filter: " );
		filterField = new TextComponentWithHintAndClear( "Enter search terms" );

		highlightLabel = new JLabel( "Highlight: " );
		highlightField = new TextComponentWithHintAndClear( "Enter search terms" );

		statsPanel = new StatsPanel( new DefaultBinder<>( ) );
		statsPanel.setBorder( new EmptyBorder( 5 , 5 , 5 , 0 ) );
	}

	private void initLayout( )
	{
		add( content , BorderLayout.CENTER );

		setPreferredSize( new Dimension( 250 , 500 ) );
		GridBagWizard gbw = GridBagWizard.create( content );
		gbw.put( filterLabel ).xy( 0 , 0 ).west( ).insets( 2 , 2 , 0 , 0 );
		gbw.put( filterField ).rightOf( filterLabel ).fillx( 1.0 ).insets( 2 , 2 , 0 , 0 );
		gbw.put( pinButton( ) ).rightOf( filterField ).filly( ).insets( 2 , 0 , 0 , 0 );
		gbw.put( highlightLabel ).below( filterLabel ).west( ).insets( 2 , 2 , 2 , 0 );
		gbw.put( highlightField ).below( filterField , pinButton( ) ).fillx( 1.0 )
			.insets( 2 , 2 , 2 , 0 );
		gbw.put( tableSetup.scrollPane ).below( highlightLabel , highlightField )
			.fillboth( 1.0 , 1.0 );
		gbw.put( statsPanel ).below( tableSetup.scrollPane ).fillx( 1.0 );
	}

	public AnnotatingJTable table( )
	{
		return table;
	}

	public DefaultAnnotatingJTableSetup tableSetup( )
	{
		return tableSetup;
	}

	public JLabel filterLabel( )
	{
		return filterLabel;
	}

	public TextComponentWithHintAndClear filterField( )
	{
		return filterField;
	}

	public JLabel highlightLabel( )
	{
		return highlightLabel;
	}

	public TextComponentWithHintAndClear highlightField( )
	{
		return highlightField;
	}

	public StatsPanel statsPanel( )
	{
		return statsPanel;
	}
}

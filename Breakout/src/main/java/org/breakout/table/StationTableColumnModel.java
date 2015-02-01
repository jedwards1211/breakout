package org.breakout.table;

import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.i18n.I18n;

@SuppressWarnings( "serial" )
public class StationTableColumnModel extends SurveyDataTableColumnModel<Station>
{
	public final TableColumn	nameColumn;
	public final TableColumn	northColumn;
	public final TableColumn	eastColumn;
	public final TableColumn	upColumn;

	public StationTableColumnModel( I18n i18n , SurveyDataFormatter formats )
	{
		super( i18n , formats );

		nameColumn = createNameColumn( );
		northColumn = createLengthColumn( StationColumnDefs.north );
		eastColumn = createLengthColumn( StationColumnDefs.east );
		upColumn = createLengthColumn( StationColumnDefs.up );

		for( TableColumn column : Arrays.asList(
			nameColumn ,
			northColumn ,
			eastColumn ,
			upColumn ) )
		{
			SurveyDataColumnDef def = ( SurveyDataColumnDef ) column.getIdentifier( );
			Binder<String> b = localizer.stringBinder( def.name );
			Binding nameBinding = f -> column.setHeaderValue( b.get( ) );
			b.addBinding( nameBinding );
			nameBinding.update( true );
			builtInColumns.put( def , column );
		}
	}

	private TableColumn createNameColumn( )
	{
		TableColumn result = new TableColumn( );
		result.setIdentifier( StationColumnDefs.name );
		result
			.setCellRenderer( new MonospaceFontRenderer( new DefaultTableCellRenderer( ) ) );
		result.setCellEditor( new MonospaceFontEditor( new DefaultCellEditor( new JTextField( ) ) ) );
		return result;
	}
}

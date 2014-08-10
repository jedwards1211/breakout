package org.andork.breakout.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.andork.breakout.ObjectYamlBimapper2;
import org.andork.breakout.table.NewSurveyTableModel.Row;
import org.andork.breakout.table.NewSurveyTableModel.SurveyColumn;
import org.andork.format.FormattedText;
import org.andork.func.Bimapper;
import org.andork.func.CompoundBimapper;
import org.andork.func.ListBimapper;
import org.andork.io.KVLiteChannel;
import org.andork.io.KVLiteChannel.ChangeType;
import org.andork.io.KVLiteChannel.Key;
import org.andork.io.KVLiteChannel.Record;
import org.andork.q.QObject;
import org.andork.swing.FormatAndDisplayInfo;
import org.andork.swing.event.TableModelEventInterpretation;
import org.andork.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

public class NewSurveyTablePersister implements TableModelListener , TableColumnModelListener , PropertyChangeListener
{
	private List<Record>										pending	= new LinkedList<Record>( );
	
	private String												columnsKey;
	private String												firstRowKey;
	private String												rowKeyPrefix;
	private byte[ ]												rowKeyPrefixBytes;
	private Consumer<? super List<Record>>						commitHandler;
	
	private Bimapper<List<QObject<SurveyColumnModel>>, String>	columnsYamlBimapper;
	
	private List<Integer>										rowIds	= new ArrayList<>( );
	
	private NewSurveyTable										table;
	private NewSurveyTableModel									model;
	private SurveyTableColumnModel								columnModel;
	
	private Yaml												rowYaml	= new Yaml( );
	
	public NewSurveyTablePersister( String columnsKey , String firstRowKey , String rowKeyPrefix , Consumer<? super List<Record>> commitHandler )
	{
		super( );
		this.columnsKey = columnsKey;
		this.firstRowKey = firstRowKey;
		this.rowKeyPrefix = rowKeyPrefix;
		rowKeyPrefixBytes = rowKeyPrefix.getBytes( );
		this.commitHandler = commitHandler;
		
		columnsYamlBimapper = CompoundBimapper.compose(
				new ListBimapper<QObject<SurveyColumnModel>, Object>( SurveyColumnModel.objectBimapper ) ,
				new ObjectYamlBimapper2<List<Object>>( ) );
	}
	
	public void setTable( NewSurveyTable newTable )
	{
		if( table != newTable )
		{
			if( table != null )
			{
				table.removePropertyChangeListener( "model" , this );
				table.removePropertyChangeListener( "columnModel" , this );
			}
			
			table = newTable;
			
			if( newTable != null )
			{
				newTable.addPropertyChangeListener( "model" , this );
				newTable.addPropertyChangeListener( "columnModel" , this );
			}
			
			setModel( newTable == null ? null : newTable.getModel( ) );
			setColumnModel( newTable == null ? null : newTable.getColumnModel( ) );
		}
		
		rowIds = new ArrayList<>( );
		
		if( model != null )
		{
			for( int rowIndex = 0 ; rowIndex < model.getRowCount( ) ; rowIndex++ )
			{
				rowIds.add( model.getRow( rowIndex ).get( Row.id ) );
			}
		}
	}
	
	private void setModel( NewSurveyTableModel newModel )
	{
		if( model != newModel )
		{
			if( model != null )
			{
				model.removeTableModelListener( this );
			}
			model = newModel;
			if( newModel != null )
			{
				newModel.addTableModelListener( this );
			}
		}
	}
	
	private void setColumnModel( SurveyTableColumnModel newColumnModel )
	{
		if( columnModel != newColumnModel )
		{
			if( columnModel != null )
			{
				columnModel.removeColumnModelListener( this );
			}
			columnModel = newColumnModel;
			if( newColumnModel != null )
			{
				newColumnModel.addColumnModelListener( this );
			}
		}
	}
	
	private static int numBytes( int id )
	{
		if( ( id & 0xff000000 ) != 0 )
		{
			return 4;
		}
		else if( ( id & 0xff0000 ) != 0 )
		{
			return 3;
		}
		else if( ( id & 0xff00 ) != 0 )
		{
			return 2;
		}
		else
		{
			return 1;
		}
	}
	
	private byte[ ] createRowKey( int id )
	{
		int idBytes = numBytes( id );
		byte[ ] bytes = new byte[ rowKeyPrefixBytes.length + idBytes ];
		System.arraycopy( rowKeyPrefixBytes , 0 , bytes , 0 , rowKeyPrefixBytes.length );
		
		int k = bytes.length - 1;
		while( k >= rowKeyPrefixBytes.length )
		{
			bytes[ k-- ] = ( byte ) ( id & 0xff );
			id >>= 8;
		}
		
		return bytes;
	}
	
	private byte[ ] createRowKey( byte[ ] idBytes )
	{
		byte[ ] bytes = new byte[ rowKeyPrefixBytes.length + idBytes.length ];
		System.arraycopy( rowKeyPrefixBytes , 0 , bytes , 0 , rowKeyPrefixBytes.length );
		System.arraycopy( idBytes , 0 , bytes , rowKeyPrefixBytes.length , idBytes.length );
		return bytes;
	}
	
	private static int toId( byte[ ] bytes )
	{
		int result = 0;
		for( int i = 0 ; i < bytes.length ; i++ )
		{
			result = ( result << 8 ) | bytes[ i ];
		}
		return result;
	}
	
	private static byte[ ] toBytes( int id )
	{
		byte[ ] result = new byte[ numBytes( id ) ];
		int k = result.length - 1;
		while( k >= 0 )
		{
			result[ k-- ] = ( byte ) ( id & 0xff );
			id >>= 8;
		}
		return result;
	}
	
	public NewSurveyTableModel load( KVLiteChannel channel ) throws IOException
	{
		NewSurveyTableModel newModel = new NewSurveyTableModel( );
		
		byte[ ] columnBytes = channel.read( new Key( columnsKey ) );
		
		List<QObject<SurveyColumnModel>> columnModels = null;
		
		if( columnBytes != null )
		{
			columnModels = columnsYamlBimapper.unmap( new String( columnBytes ) );
			newModel.setColumnModels( columnModels );
		}
		else
		{
			return null;
		}
		
		List<QObject<Row>> rows = new ArrayList<>( );
		
		byte[ ] rowIdBytes = channel.read( new Key( firstRowKey ) );
		while( rowIdBytes != null )
		{
			byte[ ] rowBytes = channel.read( new Key( createRowKey( rowIdBytes ) ) );
			if( rowBytes == null )
			{
				break;
			}
			
			QObject<Row> row = Row.spec.newObject( );
			row.set( Row.id , toId( rowIdBytes ) );
			rowIdBytes = readRowRecord( newModel , rowBytes , row );
			rows.add( row );
		}
		
		newModel.setRows( rows );
		
		return newModel;
	}
	
	/**
	 * Reads a row record.
	 * 
	 * @param newModel
	 *        the new {@link NewSurveyTableModel} from which to get column settings.
	 * @param rowBytes
	 *        the row record data.
	 * @param out
	 *        the {@link QObject} to store the read data in.
	 * @return
	 *         the encoded id of the next row in the table.
	 */
	@SuppressWarnings( "unchecked" )
	private byte[ ] readRowRecord( NewSurveyTableModel newModel , byte[ ] rowBytes , QObject<Row> out )
	{
		String yaml = new String( rowBytes );
		Map<Integer, Object> m;
		try
		{
			m = ( Map<Integer, Object> ) rowYaml.load( yaml );
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
			return null;
		}
		
		for( Map.Entry<Integer, Object> entry : m.entrySet( ) )
		{
			QObject<SurveyColumnModel> columnModel = newModel.getColumnModelWithId( entry.getKey( ) );
			SurveyColumn column = newModel.getColumnWithId( entry.getKey( ) );
			
			if( columnModel == null || column == null )
			{
				continue;
			}
			
			Object value = entry.getValue( );
			
			if( value instanceof List )
			{
				
				List<?> list = ( List<?> ) entry.getValue( );
				if( list.size( ) != 2 )
				{
					continue;
				}
				
				String text = StringUtils.toStringOrNull( list.get( 0 ) );
				String formatName = StringUtils.toStringOrNull( list.get( 1 ) );
				
				FormatAndDisplayInfo<?> format = null;
				
				if( formatName != null )
				{
					format = columnModel.get( SurveyColumnModel.type ).availableFormatIdMap.get( formatName );
				}
				if( format == null )
				{
					format = columnModel.get( SurveyColumnModel.defaultFormat );
				}
				
				value = new FormattedText( text , format );
			}
			
			column.setValueAt( value , out );
		}
		
		Integer nextId = ( Integer ) m.get( -1 );
		return nextId == null ? null : toBytes( nextId );
	}
	
	public void rewriteAll( )
	{
		writeTableColumnsChanged( columnModel.getColumnModels( ) );
		writeTableDataChanged( );
	}
	
	private Record createUpdateColumnsRecord( List<QObject<SurveyColumnModel>> models )
	{
		return new Record( new Key( columnsKey ) ,
				ChangeType.UPDATE ,
				columnsYamlBimapper.map( models ).getBytes( ) );
	}
	
	private Record createUpdateFirstRowPointerRecord( )
	{
		byte[ ] firstRowId = rowIds.isEmpty( ) ? new byte[ 0 ] : toBytes( rowIds.get( 0 ) );
		return new Record( new Key( firstRowKey ) ,
				ChangeType.UPDATE ,
				firstRowId );
	}
	
	private Record createDeleteRowRecord( int rowId )
	{
		return new Record( new Key( createRowKey( rowId ) ) ,
				ChangeType.DELETE ,
				new byte[ 0 ] );
	}
	
	private Record createUpdateRowRecord( int rowIndex )
	{
		QObject<Row> row = model.getRow( rowIndex );
		QObject<Row> nextRow = rowIndex < model.getRowCount( ) - 1 ? model.getRow( rowIndex + 1 ) : null;
		
		Map<Integer, Object> m = new LinkedHashMap<Integer, Object>( );
		
		if( nextRow != null )
		{
			m.put( -1 , nextRow.get( Row.id ) );
		}
		
		List<QObject<SurveyColumnModel>> columnModels = model.getColumnModels( );
		for( int k = 0 ; k < columnModels.size( ) ; k++ )
		{
			QObject<SurveyColumnModel> columnModel = columnModels.get( k );
			Object value = model.getValueAt( rowIndex , k );
			
			if( value instanceof FormattedText )
			{
				FormattedText ft = ( FormattedText ) value;
				
				List<String> l = new ArrayList<>( 2 );
				l.add( ft.getText( ) );
				if( ft.getFormat( ) instanceof FormatAndDisplayInfo )
				{
					l.add( ( ( FormatAndDisplayInfo<?> ) ft.getFormat( ) ).id( ) );
				}
				else
				{
					l.add( null );
				}
				
				m.put( columnModel.get( SurveyColumnModel.id ) , l );
			}
			else if( value != null )
			{
				m.put( columnModel.get( SurveyColumnModel.id ) , value );
			}
		}
		
		return new Record( new Key( createRowKey( row.get( Row.id ) ) ) ,
				ChangeType.UPDATE ,
				rowYaml.dump( m ).getBytes( ) );
	}
	
	private void writeRowsUpdated( TableModelEvent e )
	{
		for( int rowIndex = e.getFirstRow( ) ; rowIndex <= e.getLastRow( ) ; rowIndex++ )
		{
			pend( createUpdateRowRecord( rowIndex ) );
		}
		
	}
	
	private void writeRowsDeleted( TableModelEvent e )
	{
		for( int i = e.getFirstRow( ) ; i <= e.getLastRow( ) ; i++ )
		{
			int deletedRowId = rowIds.remove( e.getFirstRow( ) );
			pend( createDeleteRowRecord( deletedRowId ) );
		}
		if( e.getFirstRow( ) == 0 )
		{
			pend( createUpdateFirstRowPointerRecord( ) );
		}
		else
		{
			pend( createUpdateRowRecord( e.getFirstRow( ) - 1 ) );
		}
	}
	
	private void writeRowsInserted( TableModelEvent e )
	{
		for( int i = e.getFirstRow( ) ; i <= e.getLastRow( ) ; i++ )
		{
			rowIds.add( i , model.getRow( i ).get( Row.id ) );
		}
		
		if( e.getFirstRow( ) == 0 )
		{
			pend( createUpdateFirstRowPointerRecord( ) );
		}
		else
		{
			pend( createUpdateRowRecord( e.getFirstRow( ) - 1 ) );
		}
		for( int i = e.getFirstRow( ) ; i <= e.getLastRow( ) ; i++ )
		{
			pend( createUpdateRowRecord( i ) );
		}
	}
	
	private void writeTableDataChanged( )
	{
		Set<Integer> newRowIds = new LinkedHashSet<Integer>( );
		
		for( int rowIndex = 0 ; rowIndex < model.getRowCount( ) ; rowIndex++ )
		{
			pend( createUpdateRowRecord( rowIndex ) );
			newRowIds.add( model.getRow( rowIndex ).get( Row.id ) );
		}
		
		for( int rowId : rowIds )
		{
			if( !newRowIds.contains( rowId ) )
			{
				pend( createDeleteRowRecord( rowId ) );
			}
		}
		
		pend( createUpdateFirstRowPointerRecord( ) );
		
		rowIds = new ArrayList<>( newRowIds );
	}
	
	private void writeTableColumnsChanged( List<QObject<SurveyColumnModel>> columnModels )
	{
		pend( createUpdateColumnsRecord( columnModels ) );
	}
	
	private void pend( Record record )
	{
		if( pending.isEmpty( ) )
		{
			SwingUtilities.invokeLater( new Committer( ) );
		}
		
		pending.add( record );
	}
	
	@Override
	public void tableChanged( TableModelEvent e )
	{
		if( e.getSource( ) != model )
		{
			return;
		}
		
		try
		{
			switch( TableModelEventInterpretation.interpret( e ) )
			{
				case CELLS_UPDATED:
				case ROWS_UPDATED:
					writeRowsUpdated( e );
					break;
				case COLUMN_DELETED:
				case COLUMN_INSERTED:
				case COLUMN_UPDATED:
					break;
				case ROWS_DELETED:
					writeRowsDeleted( e );
					break;
				case ROWS_INSERTED:
					writeRowsInserted( e );
					break;
				case TABLE_DATA_CHANGED:
					writeTableDataChanged( );
					break;
				case TABLE_STRUCTURE_CHANGED:
					writeTableColumnsChanged( model.getColumnModels( ) );
					writeTableDataChanged( );
					break;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
		}
	}
	
	private class Committer implements Runnable
	{
		@Override
		public void run( )
		{
			commitHandler.accept( pending );
			pending = new LinkedList<>( );
		}
	}
	
	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		if( evt.getSource( ) == table )
		{
			if( "model".equals( evt.getPropertyName( ) ) )
			{
				setModel( table.getModel( ) );
				if( model != null )
				{
					writeTableDataChanged( );
				}
			}
			if( "columnModel".equals( evt.getPropertyName( ) ) )
			{
				setColumnModel( table.getColumnModel( ) );
				if( columnModel != null )
				{
					writeTableColumnsChanged( columnModel.getColumnModels( ) );
				}
			}
		}
	}
	
	@Override
	public void columnAdded( TableColumnModelEvent e )
	{
		
	}
	
	@Override
	public void columnRemoved( TableColumnModelEvent e )
	{
		
	}
	
	@Override
	public void columnMoved( TableColumnModelEvent e )
	{
		if( e.getSource( ) != columnModel )
		{
			return;
		}
		
		writeTableColumnsChanged( columnModel.getColumnModels( ) );
	}
	
	@Override
	public void columnMarginChanged( ChangeEvent e )
	{
		
	}
	
	@Override
	public void columnSelectionChanged( ListSelectionEvent e )
	{
		
	}
}

package org.breakout.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.andork.swing.table.NiceTableModel;
import org.andork.unit.UnitizedDouble;
import org.metacave.MetacaveJson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@SuppressWarnings( "serial" )
public class MetacaveTableModel extends NiceTableModel<MetacaveTableModel.Row>
{
	public static class Row
	{
		public final JsonNode trip;
		public final JsonNode from;
		public final JsonNode shot;
		public final JsonNode to;

		public Row( JsonNode trip , JsonNode from , JsonNode shot , JsonNode to )
		{
			super( );
			this.trip = trip;
			this.from = from;
			this.shot = shot;
			this.to = to;
		}
	}

	public static class DefaultColumn implements Column<Row>
	{
		String columnName;
		Class<?> columnClass;
		Function<Row, ?> getter;
		BiConsumer<Row, Object> setter;

		public DefaultColumn( String columnName , Class<?> columnClass , Function<Row, ?> getter )
		{
			super( );
			this.columnName = columnName;
			this.columnClass = columnClass;
			this.getter = getter;
		}

		@Override
		public String getColumnName( )
		{
			return columnName;
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return columnClass;
		}

		@Override
		public boolean isCellEditable( Row row )
		{
			return setter != null;
		}

		@Override
		public Object getValueAt( Row row )
		{
			return getter.apply( row );
		}

		@Override
		public boolean setValueAt( Object aValue , Row row )
		{
			setter.accept( row , aValue );
			return true;
		}

	}

	private final DefaultColumn from = new DefaultColumn( "From" , String.class ,
		row -> row.from.get( "station" ).asText( ) );
	private final DefaultColumn to = new DefaultColumn( "To" , String.class ,
		row -> row.to.get( "station" ).asText( ) );

	private final DefaultColumn dist = new DefaultColumn( "Dist" , UnitizedDouble.class ,
		row -> MetacaveJson.dist( row.shot , row.trip ) );
	private final DefaultColumn fsAzm = new DefaultColumn( "FS Azm" , UnitizedDouble.class ,
		row -> MetacaveJson.fsAzm( row.shot , row.trip ) );
	private final DefaultColumn bsAzm = new DefaultColumn( "BS Azm" , UnitizedDouble.class ,
		row -> MetacaveJson.bsAzm( row.shot , row.trip ) );
	private final DefaultColumn fsInc = new DefaultColumn( "FS Inc" , UnitizedDouble.class ,
		row -> MetacaveJson.fsInc( row.shot , row.trip ) );
	private final DefaultColumn bsInc = new DefaultColumn( "BS Inc" , UnitizedDouble.class ,
		row -> MetacaveJson.bsInc( row.shot , row.trip ) );

	private final DefaultColumn fromLrudType = new DefaultColumn( "From LRUD type" , String.class ,
		row -> lrudType( row.from ) );
	private final DefaultColumn[ ] fromLruds = createFromLrudColumns( );

	private final DefaultColumn toLrudType = new DefaultColumn( "To LRUD type" , String.class ,
		row -> lrudType( row.to ) );
	private final DefaultColumn[ ] toLruds = createToLrudColumns( );

	public MetacaveTableModel( )
	{
		setColumns( Arrays.asList(
			from ,
			to ,
			dist ,
			fsAzm ,
			bsAzm ,
			fsInc ,
			bsInc ,
			fromLrudType ,
			fromLruds[ 0 ] ,
			fromLruds[ 1 ] ,
			fromLruds[ 2 ] ,
			fromLruds[ 3 ] ,
			toLrudType ,
			toLruds[ 0 ] ,
			toLruds[ 1 ] ,
			toLruds[ 2 ] ,
			toLruds[ 3 ]
			) );
	}

	public void setData( JsonNode root )
	{
		List<Row> rows = new ArrayList<Row>( );

		JsonNode trips = root.get( "trips" );
		if( trips == null || ! ( trips instanceof ArrayNode ) )
		{
			clearRows( );
		}

		for( int i = 0 ; i < trips.size( ) ; i++ )
		{
			JsonNode trip = trips.get( i );

			JsonNode survey = trip.get( "survey" );
			if( survey == null || ! ( survey instanceof ArrayNode ) )
			{
				continue;
			}

			for( int k = 0 ; k < trips.size( ) - 2 ; k += 2 )
			{
				JsonNode from = trips.get( k );
				JsonNode shot = trips.get( k + 1 );
				JsonNode to = trips.get( k + 2 );

				if( !from.has( "station" ) || !shot.has( "dist" ) || !to.has( "station" ) )
				{
					continue;
				}

				rows.add( new Row( trip , from , shot , to ) );
			}
		}

		setRows( rows );
	}

	private DefaultColumn[ ] createFromLrudColumns( )
	{
		String[ ] names = { "L" , "R" , "U" , "D" };
		DefaultColumn[ ] result = new DefaultColumn[ 4 ];
		for( int i = 0 ; i < 4 ; i++ )
		{
			final int index = i;
			result[ i ] = new DefaultColumn( names[ index ] , UnitizedDouble.class ,
				row -> MetacaveJson.lrudOrNsewElem( row.from , row.trip , index ) );
		}
		return result;
	}

	private DefaultColumn[ ] createToLrudColumns( )
	{
		String[ ] names = { "L" , "R" , "U" , "D" };
		DefaultColumn[ ] result = new DefaultColumn[ 4 ];
		for( int i = 0 ; i < 4 ; i++ )
		{
			final int index = i;
			result[ i ] = new DefaultColumn( names[ index ] , UnitizedDouble.class ,
				row -> MetacaveJson.lrudOrNsewElem( row.to , row.trip , index ) );
		}
		return result;
	}

	private static String lrudType( JsonNode station )
	{
		return station.has( "lrud" ) ? "LRUD" :
			station.has( "nsew" ) ? "NSEW" : null;
	}
}

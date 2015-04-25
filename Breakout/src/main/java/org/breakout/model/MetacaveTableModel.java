package org.breakout.model;

import org.andork.swing.table.NiceTableModel;
import org.andork.unit.UnitizedDouble;
import org.metacave.MetacaveJson;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class MetacaveTableModel extends NiceTableModel<MetacaveTableModel.Row>
{
	public static class Row
	{
		public final ObjectNode trip;
		public final ObjectNode from;
		public final ObjectNode shot;
		public final ObjectNode to;

		public Row( ObjectNode trip , ObjectNode from , ObjectNode shot , ObjectNode to )
		{
			super( );
			this.trip = trip;
			this.from = from;
			this.shot = shot;
			this.to = to;
		}
	}

	public class FromStationColumn implements Column<Row>
	{
		@Override
		public String getColumnName( )
		{
			return "From";
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return String.class;
		}

		@Override
		public boolean isCellEditable( Row row )
		{
			return true;
		}

		@Override
		public Object getValueAt( Row row )
		{
			return row.from.get( "station" ).asText( );
		}

		@Override
		public boolean setValueAt( Object aValue , Row row )
		{
			row.from.set( "station" , new TextNode( aValue.toString( ) ) );
			return true;
		}
	}

	public class ToStationColumn implements Column<Row>
	{
		@Override
		public String getColumnName( )
		{
			return "To";
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return String.class;
		}

		@Override
		public boolean isCellEditable( Row row )
		{
			return true;
		}

		@Override
		public Object getValueAt( Row row )
		{
			return row.to.get( "station" ).asText( );
		}

		@Override
		public boolean setValueAt( Object aValue , Row row )
		{
			row.to.set( "station" , new TextNode( aValue.toString( ) ) );
			return true;
		}
	}

	public class DistanceColumn implements Column<Row>
	{
		@Override
		public String getColumnName( )
		{
			return "Dist";
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return UnitizedDouble.class;
		}

		@Override
		public boolean isCellEditable( Row row )
		{
			return true;
		}

		@Override
		public Object getValueAt( Row row )
		{
			return MetacaveJson.dist( row.shot , row.trip );
		}

		@Override
		public boolean setValueAt( Object aValue , Row row )
		{
			row.from.set( "station" , new TextNode( aValue.toString( ) ) );
			return true;
		}
	}
}

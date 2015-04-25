package org.breakout.model;

import org.andork.swing.table.NiceTableModel;
import org.andork.unit.UnitType;
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
	
	public static class FromStationColumn implements Column<Row>
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

	public static class ToStationColumn implements Column<Row>
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

	@FunctionalInterface
	private static interface QuantityProp
	{
		public UnitizedDouble<?> get( ObjectNode shotOrStation , ObjectNode trip );
	}

	public static class ShotPropColumn implements Column<Row>
	{
		private final String columnName;
		private final QuantityProp prop;

		public ShotPropColumn( String columnName , QuantityProp prop )
		{
			super( );
			this.columnName = columnName;
			this.prop = prop;
		}

		@Override
		public String getColumnName( )
		{
			return columnName;
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return UnitizedDouble.class;
		}

		@Override
		public boolean isCellEditable( Row row )
		{
			return false;
		}

		@Override
		public Object getValueAt( Row row )
		{
			return prop.get( row.shot , row.trip );
		}

		@Override
		public boolean setValueAt( Object aValue , Row row )
		{
			return false;
		}
	}

	private final FromStationColumn from = new FromStationColumn( );
	private final ToStationColumn to = new ToStationColumn( );
	private final ShotPropColumn dist = new ShotPropColumn( "Dist" , MetacaveJson::dist );
	private final ShotPropColumn fsAzm = new ShotPropColumn( "FS Azm" , MetacaveJson::fsAzm );
	private final ShotPropColumn bsAzm = new ShotPropColumn( "BS Azm" , MetacaveJson::bsAzm );
	private final ShotPropColumn fsInc = new ShotPropColumn( "FS Inc" , MetacaveJson::fsInc );
	private final ShotPropColumn bsInc = new ShotPropColumn( "BS Inc" , MetacaveJson::bsInc );
}

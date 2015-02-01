package org.breakout.table;

import java.util.List;

import org.andork.unit.Length;
import org.andork.unit.Unit;

@SuppressWarnings( "serial" )
public class StationTableModel extends SurveyDataTableModel<Station>
{
	public final SurveyDataModelColumn	nameColumn;
	public final SurveyDataModelColumn	northColumn;
	public final SurveyDataModelColumn	eastColumn;
	public final SurveyDataModelColumn	upColumn;
	public final SurveyDataModelColumn	lengthUnitColumn;

	public StationTableModel( )
	{
		nameColumn = new DefaultColumn(
			StationColumnDefs.name , s -> s.getName( ) ,
			( s , v ) -> s.setName( ( String ) v ) );

		northColumn = new DefaultColumn(
			StationColumnDefs.north , s -> s.getNorth( ) ,
			( s , v ) -> s.setNorth( ( ParsedText<Double> ) v ) );

		eastColumn = new DefaultColumn(
			StationColumnDefs.east , s -> s.getEast( ) ,
			( s , v ) -> s.setEast( ( ParsedText<Double> ) v ) );

		upColumn = new DefaultColumn(
			StationColumnDefs.up , s -> s.getUp( ) ,
			( s , v ) -> s.setUp( ( ParsedText<Double> ) v ) );

		lengthUnitColumn = new UnitColumn(
			StationColumnDefs.lengthUnit , s -> s.getLengthUnit( ) ,
			( s , u ) -> s.setLengthUnit( ( Unit<Length> ) u ) );
	}

	@Override
	protected void addBuiltinColumnsTo( List<SurveyDataModelColumn> result )
	{
		result.add( nameColumn );
		result.add( northColumn );
		result.add( eastColumn );
		result.add( upColumn );
		result.add( lengthUnitColumn );
	}
}

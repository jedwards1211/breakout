package org.breakout.table;

import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

@SuppressWarnings( "serial" )
public class ShotTableModel extends SurveyDataTableModel<Shot>
{
	public final SurveyDataModelColumn	fromStationNameColumn;
	public final SurveyDataModelColumn	toStationNameColumn;
	public final SurveyDataModelColumn	vectorColumn;
	public final SurveyDataModelColumn	xSectionAtFromColumn;
	public final SurveyDataModelColumn	xSectionAtToColumn;
	public final SurveyDataModelColumn	lengthUnitColumn;
	public final SurveyDataModelColumn	angleUnitColumn;
	public ShotTableModel( )
	{
		fromStationNameColumn = new DefaultColumn(
			ShotColumnDefs.fromStationName , s -> s.getFromStationName( ) ,
			( s , v ) -> s.setFromStationName( ( String ) v ) );

		toStationNameColumn = new DefaultColumn(
			ShotColumnDefs.toStationName , s -> s.getToStationName( ) ,
			( s , v ) -> s.setToStationName( ( String ) v ) );

		vectorColumn = new DefaultColumn(
			ShotColumnDefs.vector , s -> s.getVector( ) ,
			( s , v ) -> s.setVector( ( ParsedTextWithType<ShotVector> ) v ) );

		xSectionAtFromColumn = new DefaultColumn(
			ShotColumnDefs.xSectionAtFrom , s -> s.getXSectionAtFrom( ) ,
			( s , v ) -> s.setXSectionAtFrom( ( ParsedTextWithType<XSection> ) v ) );

		xSectionAtToColumn = new DefaultColumn(
			ShotColumnDefs.xSectionAtTo , s -> s.getXSectionAtTo( ) ,
			( s , v ) -> s.setXSectionAtTo( ( ParsedTextWithType<XSection> ) v ) );

		lengthUnitColumn = new UnitColumn(
			ShotColumnDefs.lengthUnit , s -> s.getLengthUnit( ) ,
			( s , u ) -> s.setLengthUnit( ( Unit<Length> ) u ) );

		angleUnitColumn = new UnitColumn(
			ShotColumnDefs.angleUnit , s -> s.getAngleUnit( ) ,
			( s , u ) -> s.setAngleUnit( ( Unit<Angle> ) u ) );
	}

	@Override
	protected void addBuiltinColumnsTo( List<SurveyDataTableModel<Shot>.SurveyDataModelColumn> result )
	{
		result.add( fromStationNameColumn );
		result.add( toStationNameColumn );
		result.add( vectorColumn );
		result.add( xSectionAtFromColumn );
		result.add( xSectionAtToColumn );
		result.add( lengthUnitColumn );
		result.add( angleUnitColumn );
	}
}

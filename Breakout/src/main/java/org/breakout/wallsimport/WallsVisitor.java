package org.breakout.wallsimport;

import java.util.List;

import org.andork.parse.Segment;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public interface WallsVisitor
{
	public void beginFile( Object source );

	public void endFile( Object source );

	public void beginVectorLine( );

	public void visitFrom( String from );

	public void visitTo( String to );

	public void visitDistance( UnitizedDouble<Length> distance );

	public void visitFrontsightAzimuth( UnitizedDouble<Angle> fsAzimuth );

	public void visitBacksightAzimuth( UnitizedDouble<Angle> bsAzimuth );

	public void visitFrontsightInclination( UnitizedDouble<Angle> fsInclination );

	public void visitBacksightInclination( UnitizedDouble<Angle> bsInclination );

	public void visitNorth( UnitizedDouble<Length> north );

	public void visitLatitude( UnitizedDouble<Angle> latitude );

	public void visitEast( UnitizedDouble<Length> east );

	public void visitLongitude( UnitizedDouble<Angle> longitude );

	public void visitRectUp( UnitizedDouble<Length> up );

	public void visitInstrumentHeight( UnitizedDouble<Length> instrumentHeight );

	public void visitTargetHeight( UnitizedDouble<Length> targetHeight );

	public void visitLeft( UnitizedDouble<Length> left );

	public void visitRight( UnitizedDouble<Length> right );

	public void visitUp( UnitizedDouble<Length> up );

	public void visitDown( UnitizedDouble<Length> down );

	public void visitLrudFacingAngle( UnitizedDouble<Angle> facingAngle );

	public void visitCFlag( );

	public void visitHorizontalVarianceOverride( VarianceOverride variance );

	public void visitVerticalVarianceOverride( VarianceOverride variance );

	/**
	 * In this case "segment" refers to Walls' #Segment directive, not {@link Segment}. Nonetheless, the
	 * argument to the #Segment directive is given as a {@link Segment} :)
	 * 
	 * @param segment
	 *            the argument to the #Segment directive
	 */
	public void visitInlineSegment( String segment );

	public void visitInlineNote( String note );

	public void visitCommentLine( String comment );

	public void abortVectorLine( );

	public void endVectorLine( );

	public void visitFlaggedStations( String flag , List<String> stations );

	public void visitBlockCommentLine( String string );

	public void visitNoteLine( String station , String note );

	public void beginFixLine( );

	public void abortFixLine( );

	public void endFixLine( );

	public void beginUnitsLine( );

	public void abortUnitsLine( );

	public void endUnitsLine( );

	public void visitFixedStation( String string );

	public void visitInlineComment( String string );
}
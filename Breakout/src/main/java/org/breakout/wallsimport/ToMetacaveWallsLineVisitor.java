package org.breakout.wallsimport;

import java.text.SimpleDateFormat;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;
import org.andork.util.Java7.Objects;
import org.breakout.wallsimport.WallsParser.WallsLineVisitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class ToMetacaveWallsLineVisitor implements WallsLineVisitor
{
	// thoughts

	// create a new trip with every units directive

	// split off everything before the last colon -- this is the cave name

	// use prefixes as cave names

	// in the end, rename prefixes to actual caves if desired

	// INCH will have to be applied

	// how to handle UTM zones and datum?

	private final SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mmZ" );

	private WallsParser parser;

	private boolean usePrefixesAsCaveNames = false;

	private JsonNodeFactory nodeFactory;

	private ObjectNode root;
	private ArrayNode fixedRoot;
	private ArrayNode tripsRoot;

	/**
	 * The current fixed station group
	 */
	private ObjectNode fixedGroup;

	/**
	 * The stations in the current fixed station group
	 */
	private ArrayNode fixedGroupStations;

	/**
	 * the current trip
	 */
	private ObjectNode trip;

	/**
	 * The survey of the current trip
	 */
	private ArrayNode survey;

	/**
	 * The from station of the current shot (often the to station of the previous shot)
	 */
	private ObjectNode fromStation;

	private String from;
	private String to;

	private UnitizedDouble<Length> distance;
	private UnitizedDouble<Angle> fsAzm;
	private UnitizedDouble<Angle> bsAzm;
	private UnitizedDouble<Angle> fsInc;
	private UnitizedDouble<Angle> bsInc;

	private UnitizedDouble<Length> ih;
	private UnitizedDouble<Length> th;

	private UnitizedDouble<Length> north;
	private UnitizedDouble<Length> east;
	private UnitizedDouble<Length> rUp;

	private UnitizedDouble<Length> left;
	private UnitizedDouble<Length> right;
	private UnitizedDouble<Length> up;
	private UnitizedDouble<Length> down;
	private UnitizedDouble<Angle> facingAngle;

	private String fixedStation;
	private UnitizedDouble<Angle> lat;
	private UnitizedDouble<Angle> lon;

	private String inlineComment;
	private String inlineSegment;
	private String inlineNote;
	private StringBuilder priorComments = new StringBuilder( );

	private static String asTextOrNull( JsonNode node , String prop )
	{
		node = node.get( prop );
		return node == null ? null : node.asText( );
	}

	/**
	 * @param angle
	 * @return {@code true} if the given inclination angle is approximately vertical
	 */
	private static boolean isVertical( UnitizedDouble<Angle> angle )
	{
		return Math.abs( Math.abs( angle.doubleValue( Angle.degrees ) ) - 90.0 ) < 0.0001;
	}

	private static UnitizedDouble<Angle> negateAzm( UnitizedDouble<Angle> azm )
	{
		return azm.add( new UnitizedDouble<>(
			azm.doubleValue( Angle.degrees ) > 180.0 ?
				-180.0 : 180.0 , Angle.degrees ) );
	}

	/**
	 * Converts a {@link UnitizedDouble} quantity to its representation metacave format.
	 * This is just a number if it is in the default unit for its context, otherwise an array
	 * containing the number and the unit id string (for inches, the first two entries are feet
	 * and the last two are inches)
	 * @param value
	 * @param defaultUnit
	 * @return
	 */
	private <T extends UnitType<T>> JsonNode quantity( UnitizedDouble<T> value , Unit<T> defaultUnit )
	{
		if( value == null )
		{
			return NullNode.instance;
		}
		if( value.unit == Length.inches )
		{
			@SuppressWarnings( "unchecked" )
			UnitizedDouble<Length> lvalue = ( UnitizedDouble<Length> ) value;

			double inches = lvalue.doubleValue( Length.inches );
			double feet = Math.floor( inches / 12.0 );
			inches = Math.abs( inches ) % 12.0;

			return new ArrayNode( nodeFactory )
				.add( new DoubleNode( feet ) )
				.add( new TextNode( Length.feet.id ) )
				.add( new DoubleNode( inches ) )
				.add( new TextNode( Length.inches.id ) );
		}
		if( value.unit == defaultUnit )
		{
			return new DoubleNode( value.doubleValue( value.unit ) );
		}
		return new ArrayNode( nodeFactory )
			.add( new DoubleNode( value.doubleValue( value.unit ) ) )
			.add( new TextNode( value.unit.id ) );
	}

	/**
	 * Adds the current fixed station group to the output if it is not empty,
	 * and sets the current fixed station group to a new one.
	 */
	private void newFixedStationGroup( )
	{
		if( fixedGroupStations != null && fixedGroupStations.size( ) > 0 )
		{
			fixedRoot.add( fixedGroup );
		}
		fixedGroup = new ObjectNode( nodeFactory );
		fixedGroupStations = new ArrayNode( nodeFactory );
		fixedGroup.set( "stations" , fixedGroupStations );
	}

	/**
	 * Adds the current trip to the output if it is not empty,
	 * and sets the current trip to a new one.
	 */
	private void newTrip( )
	{
		if( survey != null && survey.size( ) > 0 )
		{
			tripsRoot.add( trip );
		}
		trip = new ObjectNode( nodeFactory );
		survey = new ArrayNode( nodeFactory );
		trip.set( "survey" , survey );
		fromStation = null;
	}

	/**
	 * Nullifies all fields that temporarily store values received by the visitor methods.
	 */
	private void nullifyFields( )
	{
		from = null;
		to = null;
		distance = null;
		fsAzm = null;
		bsAzm = null;
		fsInc = null;
		bsInc = null;
		ih = null;
		th = null;
		north = null;
		east = null;
		rUp = null;
		left = null;
		right = null;
		up = null;
		down = null;
		facingAngle = null;
		fixedStation = null;
		lat = null;
		lon = null;
		inlineComment = null;
		inlineSegment = null;
	}

	/**
	 * Splits a processed (i.e. with prefixes) station name into the "cave" name
	 * (all prefixes) and station name (the rest).
	 */
	private String[ ] caveAndStation( String procStationName )
	{
		String cave , station;
		int index = procStationName.lastIndexOf( ':' );
		if( index >= 0 )
		{
			cave = index > 0 ? procStationName.substring( 0 , index ) : null;
			station = procStationName.substring( index + 1 );
		}
		else
		{
			cave = null;
			station = procStationName;
		}

		if( Objects.equals( cave , asTextOrNull( trip , "cave" ) ) )
		{
			cave = null;
		}

		return new String[ ] { cave , station };
	}

	/**
	 * Applies inch (height adjustment, instrument height, and target height,
	 * by adjusting the distance and inclination(s), since these corrections
	 * are not supported by the metacave format.
	 */
	private void applyUnsupportedCorrections( )
	{
		WallsUnits units = parser.units( );

		if( !units.inch.isZero( ) || ih != null || th != null )
		{
			UnitizedDouble<Angle> uinc = avgInc( fsInc , bsInc );
			if( uinc == null )
			{
				return;
			}
			if( !isVertical( uinc ) )
			{
				double inc = uinc.doubleValue( Angle.radians );
				double ne = Math.cos( inc ) * distance.doubleValue( distance.unit );
				double u = Math.sin( inc ) * distance.doubleValue( distance.unit );

				u += units.inch.doubleValue( distance.unit );
				if( ih != null )
				{
					u += ih.doubleValue( distance.unit );
				}
				if( th != null )
				{
					u -= th.doubleValue( distance.unit );
				}

				double newInc = Math.atan2( u , ne );
				UnitizedDouble<Angle> dinc = new UnitizedDouble<>( newInc - inc , Angle.radians );

				if( fsInc != null )
				{
					fsInc = fsInc.add( dinc );
				}
				if( bsInc != null )
				{
					if( parser.units( ).typevb_corrected )
					{
						bsInc = bsInc.add( dinc );
					}
					else
					{
						bsInc = bsInc.subtract( dinc );
					}
				}

				distance = new UnitizedDouble<>( Math.sqrt( ne * ne + u * u ) , distance.unit );
			}
		}
	}

	/**
	 * @param fsInc
	 * @param bsInc
	 * @return the average inclination, relative to frontsight.
	 */
	private UnitizedDouble<Angle> avgInc( UnitizedDouble<Angle> fsInc , UnitizedDouble<Angle> bsInc )
	{
		if( bsInc != null && !parser.units( ).typevb_corrected )
		{
			bsInc = bsInc.negate( );
		}
		if( fsInc == null )
		{
			return bsInc;
		}
		if( bsInc == null )
		{
			return fsInc;
		}

		return new UnitizedDouble<>( ( fsInc.doubleValue( fsInc.unit ) + bsInc.doubleValue( fsInc.unit ) ) * 0.5 , fsInc.unit );
	}

	/**
	 * Converts rectangular vector measurements into compass-and-tape measurements,
	 * since rectangular measurements are not supported by the metacave format.
	 */
	private void rectToCt( )
	{
		WallsUnits units = parser.units( );

		double n = north.doubleValue( units.d_unit );
		double e = east.doubleValue( units.d_unit );
		double u = rUp == null ? 0 : rUp.doubleValue( units.d_unit );

		double ne2 = n * n + e * e;
		double ne = Math.sqrt( ne2 );

		distance = new UnitizedDouble<>( Math.sqrt( ne2 + u * u ) , units.d_unit );
		fsAzm = new UnitizedDouble<>( Math.atan2( e , n ) , units.a_unit );
		fsInc = new UnitizedDouble<>( Math.atan2( u , ne ) , units.v_unit );
	}

	/**
	 * @return {@code true} if the current line contained any LRUDs.
	 */
	private boolean gotLruds( )
	{
		return left != null || right != null || up != null || down != null;
	}

	public ToMetacaveWallsLineVisitor( WallsParser parser )
	{
		this.parser = parser;

		root = new ObjectNode( nodeFactory );
		root.set( "trips" , tripsRoot = new ArrayNode( nodeFactory ) );
		root.set( "fixedStations" , fixedRoot = new ArrayNode( nodeFactory ) );

		newFixedStationGroup( );
		newTrip( );
	}

	public boolean isUsePrefixesAsCaveNames( )
	{
		return usePrefixesAsCaveNames;
	}

	public void setUsePrefixesAsCaveNames( boolean usePrefixesAsCaveNames )
	{
		this.usePrefixesAsCaveNames = usePrefixesAsCaveNames;
	}

	public JsonNode getMetacaveData( )
	{
		newFixedStationGroup( );
		newTrip( );
		return root;
	}

	@Override
	public void beginVectorLine( )
	{
		nullifyFields( );
	}

	@Override
	public void abortVectorLine( )
	{
		nullifyFields( );
	}

	public boolean newFromStationIfNecessary( String procStationName )
	{
		String cave , station;

		if( usePrefixesAsCaveNames )
		{
			String[ ] caveAndStation = caveAndStation( procStationName );
			cave = caveAndStation[ 0 ];
			station = caveAndStation[ 1 ];
		}
		else
		{
			cave = null;
			station = procStationName;
		}

		if( fromStation == null ||
			!Objects.equals( cave , asTextOrNull( fromStation , "cave" ) ) ||
			!Objects.equals( station , asTextOrNull( fromStation , "station" ) ) )
		{

			if( fromStation != null )
			{
				survey.add( new ObjectNode( nodeFactory ) );
			}

			fromStation = new ObjectNode( nodeFactory );
			fromStation.set( "station" , new TextNode( station ) );
			if( cave != null )
			{
				fromStation.set( "cave" , new TextNode( cave ) );
			}

			survey.add( fromStation );

			return true;
		}

		return false;
	}

	@Override
	public void endVectorLine( )
	{
		WallsUnits units = parser.units( );

		if( units.vectorType == VectorType.RECT && north != null )
		{
			rectToCt( );
		}

		newFromStationIfNecessary( units.processStationName( from ) );

		if( distance != null )
		{
			applyUnsupportedCorrections( );

			if( bsAzm != null && units.typeab_corrected && !units.typevb_corrected )
			{
				// uncorrect bsAzm
				bsAzm = negateAzm( bsAzm );
			}
			if( bsInc != null && units.typevb_corrected && !units.typeab_corrected )
			{
				// uncorrect bsInc
				bsInc = bsInc.negate( );
			}

			ObjectNode shot = new ObjectNode( nodeFactory );
			ObjectNode toStation = new ObjectNode( nodeFactory );

			if( usePrefixesAsCaveNames )
			{
				String[ ] toCaveAndStation = caveAndStation( units.processStationName( to ) );
				if( toCaveAndStation[ 0 ] != null )
				{
					toStation.set( "cave" , new TextNode( toCaveAndStation[ 0 ] ) );
				}
				toStation.set( "station" , new TextNode( toCaveAndStation[ 1 ] ) );
			}
			else
			{
				toStation.set( "station" , new TextNode( units.processStationName( to ) ) );
			}

			shot.set( "dist" , quantity( distance , units.d_unit ) );
			if( fsAzm != null )
			{
				shot.set( "fsAzm" , quantity( fsAzm , units.a_unit ) );
			}
			if( bsAzm != null )
			{
				shot.set( "bsAzm" , quantity( bsAzm , units.ab_unit ) );
			}
			if( fsInc != null )
			{
				shot.set( "fsInc" , quantity( fsInc , units.v_unit ) );
			}
			if( bsInc != null )
			{
				shot.set( "bsInc" , quantity( bsInc , units.vb_unit ) );
			}

			if( gotLruds( ) )
			{
				switch( units.lrud )
				{
				case From:
					if( facingAngle == null )
					{
						fromStation.set( "lrudPerp" , new TextNode( "next" ) );
					}
					// don't break
				case FB:
					fromStation.set( "lrud" , new ArrayNode( nodeFactory )
						.add( quantity( left.add( units.incs ) , units.d_unit ) )
						.add( quantity( right.add( units.incs ) , units.d_unit ) )
						.add( quantity( up.add( units.incs ) , units.d_unit ) )
						.add( quantity( down.add( units.incs ) , units.d_unit ) ) );

					if( facingAngle != null )
					{
						fromStation.set( "lrudAngle" , quantity( facingAngle , units.a_unit ) );
					}
					break;
				case To:
					if( facingAngle == null )
					{
						toStation.set( "lrudPerp" , new TextNode( "prev" ) );
					}
					// don't break
				case TB:
					toStation.set( "lrud" , new ArrayNode( nodeFactory )
						.add( quantity( left.add( units.incs ) , units.d_unit ) )
						.add( quantity( right.add( units.incs ) , units.d_unit ) )
						.add( quantity( up.add( units.incs ) , units.d_unit ) )
						.add( quantity( down.add( units.incs ) , units.d_unit ) ) );

					if( facingAngle != null )
					{
						toStation.set( "lrudAngle" , quantity( facingAngle , units.a_unit ) );
					}
					break;
				}
			}

			if( inlineComment != null )
			{
				shot.set( "comment" , new TextNode( inlineComment ) );
			}
			if( inlineSegment != null )
			{
				shot.set( "segment" , new TextNode( inlineSegment ) );
			}
			else if( units.segment != null )
			{
				shot.set( "segment" , new TextNode( units.segment ) );
			}
			if( units.flag != null )
			{
				shot.set( "flag" , new TextNode( units.flag ) );
			}

			if( priorComments.length( ) > 0 )
			{
				shot.set( "priorComments" , new TextNode( priorComments.toString( ) ) );
				priorComments = new StringBuilder( );
			}

			survey.add( shot );
			survey.add( toStation );

			fromStation = toStation;
			shot = null;
			toStation = null;

			nullifyFields( );
		}
		else if( gotLruds( ) )
		{
			fromStation.set( "lrud" , new ArrayNode( nodeFactory )
				.add( quantity( left.add( units.incs ) , units.d_unit ) )
				.add( quantity( right.add( units.incs ) , units.d_unit ) )
				.add( quantity( up.add( units.incs ) , units.d_unit ) )
				.add( quantity( down.add( units.incs ) , units.d_unit ) ) );

			if( facingAngle != null )
			{
				fromStation.set( "lrudAngle" , quantity( facingAngle , units.a_unit ) );
			}

			if( inlineComment != null )
			{
				fromStation.set( "comment" , new TextNode( inlineComment ) );
			}
		}
	}

	@Override
	public void visitFrom( String from )
	{
		this.from = from;
	}

	@Override
	public void visitTo( String to )
	{
		this.to = to;
	}

	@Override
	public void visitDistance( UnitizedDouble<Length> distance )
	{
		this.distance = distance;
	}

	@Override
	public void visitFrontsightAzimuth( UnitizedDouble<Angle> fsAzm )
	{
		this.fsAzm = fsAzm;
	}

	@Override
	public void visitBacksightAzimuth( UnitizedDouble<Angle> bsAzm )
	{
		this.bsAzm = bsAzm;
	}

	@Override
	public void visitFrontsightInclination( UnitizedDouble<Angle> fsInc )
	{
		this.fsInc = fsInc;
	}

	@Override
	public void visitBacksightInclination( UnitizedDouble<Angle> bsInc )
	{
		this.bsInc = bsInc;
	}

	@Override
	public void visitNorth( UnitizedDouble<Length> north )
	{
		this.north = north;
	}

	@Override
	public void visitLatitude( UnitizedDouble<Angle> latitude )
	{
		this.lat = latitude;
	}

	@Override
	public void visitEast( UnitizedDouble<Length> east )
	{
		this.east = east;
	}

	@Override
	public void visitLongitude( UnitizedDouble<Angle> longitude )
	{
		this.lon = longitude;
	}

	@Override
	public void visitRectUp( UnitizedDouble<Length> up )
	{
		this.rUp = up;
	}

	@Override
	public void visitInstrumentHeight( UnitizedDouble<Length> instrumentHeight )
	{
		this.ih = instrumentHeight;
	}

	@Override
	public void visitTargetHeight( UnitizedDouble<Length> targetHeight )
	{
		this.th = targetHeight;
	}

	@Override
	public void visitLeft( UnitizedDouble<Length> left )
	{
		this.left = left;
	}

	@Override
	public void visitRight( UnitizedDouble<Length> right )
	{
		this.right = right;
	}

	@Override
	public void visitUp( UnitizedDouble<Length> up )
	{
		this.up = up;
	}

	@Override
	public void visitDown( UnitizedDouble<Length> down )
	{
		this.down = down;
	}

	@Override
	public void visitLrudFacingAngle( UnitizedDouble<Angle> facingAngle )
	{
		this.facingAngle = facingAngle;
	}

	@Override
	public void visitCFlag( )
	{
	}

	@Override
	public void visitHorizontalVarianceOverride( VarianceOverride variance )
	{
	}

	@Override
	public void visitVerticalVarianceOverride( VarianceOverride variance )
	{
	}

	@Override
	public void visitInlineSegment( String inlineSegment )
	{
		this.inlineSegment = inlineSegment;
	}

	@Override
	public void visitInlineNote( String inlineNote )
	{
		this.inlineNote = inlineNote;
	}

	@Override
	public void visitCommentLine( String comment )
	{
		if( priorComments.length( ) > 0 )
		{
			priorComments.append( '\n' );
		}
		priorComments.append( comment );
	}

	@Override
	public void visitInlineComment( String inlineComment )
	{
		this.inlineComment = inlineComment;
	}

	@Override
	public void visitFlaggedStations( String flag , List<String> stations )
	{
		WallsUnits units = parser.units( );
		for( String station : stations )
		{
			newFromStationIfNecessary( units.processStationName( station ) );
			fromStation.set( "flag" , new TextNode( flag ) );
		}
	}

	@Override
	public void visitBlockCommentLine( String string )
	{
		visitCommentLine( string );
	}

	@Override
	public void visitNoteLine( String station , String note )
	{
		newFromStationIfNecessary( parser.units( ).processStationName( station ) );
		fromStation.set( "note" , new TextNode( note ) );
	}

	@Override
	public void beginFixLine( )
	{
		nullifyFields( );
	}

	@Override
	public void abortFixLine( )
	{
		nullifyFields( );
	}

	@Override
	public void endFixLine( )
	{
		WallsUnits units = parser.units( );
		ObjectNode station = new ObjectNode( nodeFactory );
		station.set( "station" , new TextNode( fixedStation ) );

		if( north != null )
		{
			station.set( "north" , quantity( north , units.d_unit ) );
		}
		if( east != null )
		{
			station.set( "east" , quantity( east , units.d_unit ) );
		}
		if( rUp != null )
		{
			station.set( "up" , quantity( rUp , units.d_unit ) );
		}
		if( lat != null )
		{
			station.set( "lat" , new DoubleNode( lat.doubleValue( Angle.degrees ) ) );
		}
		if( lon != null )
		{
			station.set( "lon" , new DoubleNode( lon.doubleValue( Angle.degrees ) ) );
		}

		if( inlineSegment != null )
		{
			station.set( "segment" , new TextNode( inlineSegment ) );
		}
		else if( units.segment != null )
		{
			station.set( "segment" , new TextNode( units.segment ) );
		}

		if( inlineNote != null )
		{
			station.set( "note" , new TextNode( inlineNote ) );
		}

		if( inlineComment != null )
		{
			station.set( "comment" , new TextNode( inlineComment ) );
		}

		if( units.flag != null )
		{
			station.set( "flag" , new TextNode( units.flag ) );
		}

		if( priorComments.length( ) > 0 )
		{
			station.set( "priorComments" , new TextNode( priorComments.toString( ) ) );
			priorComments = new StringBuilder( );
		}

		fixedGroupStations.add( station );

		nullifyFields( );
	}

	@Override
	public void visitFixedStation( String fixedStation )
	{
		this.fixedStation = fixedStation;
	}

	@Override
	public void beginUnitsLine( )
	{
	}

	@Override
	public void abortUnitsLine( )
	{
	}

	@Override
	public void endUnitsLine( )
	{
		WallsUnits units = parser.units( );

		newTrip( );

		trip.set( "distUnit" , new TextNode( units.d_unit.id ) );
		trip.set( "angleUnit" , new TextNode( units.a_unit.id ) );

		if( units.ab_unit != units.a_unit )
		{
			trip.set( "azmBsUnit" , new TextNode( units.ab_unit.id ) );
		}
		if( units.v_unit != units.a_unit )
		{
			trip.set( "azmFsUnit" , new TextNode( units.v_unit.id ) );
		}
		if( units.vb_unit != units.a_unit )
		{
			trip.set( "incBsUnit" , new TextNode( units.vb_unit.id ) );
		}
		trip.set( "backsightsCorrected" , units.typeab_corrected && units.typevb_corrected ?
			BooleanNode.getTrue( ) : BooleanNode.getFalse( ) );

		if( usePrefixesAsCaveNames )
		{
			String prefix = units.processStationName( "" );
			if( !prefix.isEmpty( ) )
			{
				trip.set( "cave" , new TextNode( prefix ) );
			}
		}

		if( units.date != null )
		{
			trip.set( "date" , new TextNode( dateFormat.format( units.date ) ) );
		}
		if( !units.decl.isZero( ) )
		{
			trip.set( "declination" , quantity( units.decl , units.a_unit ) );
		}
		if( !units.incd.isZero( ) )
		{
			trip.set( "distCorrection" , quantity( units.incd , units.d_unit ) );
		}
		if( !units.inca.isZero( ) )
		{
			trip.set( "azmFsCorrection" , quantity( units.inca , units.a_unit ) );
		}
		if( !units.incab.isZero( ) )
		{
			trip.set( "azmBsCorrection" , quantity( units.incab , units.ab_unit ) );
		}
		if( !units.incv.isZero( ) )
		{
			trip.set( "incFsCorrection" , quantity( units.incv , units.v_unit ) );
		}
		if( !units.incvb.isZero( ) )
		{
			trip.set( "incBsCorrection" , quantity( units.incvb , units.vb_unit ) );
		}

		newFixedStationGroup( );

		fixedGroup.set( "distUnit" , new TextNode( units.d_unit.id ) );
		fixedGroup.set( "angleUnit" , new TextNode( units.a_unit.id ) );

		if( usePrefixesAsCaveNames )
		{
			String prefix = units.processStationName( "" );
			if( !prefix.isEmpty( ) )
			{
				fixedGroup.set( "cave" , new TextNode( prefix ) );
			}
		}
	}
}

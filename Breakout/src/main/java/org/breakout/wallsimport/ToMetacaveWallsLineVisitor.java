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

	/**
	 * The current shot
	 */
	private ObjectNode shot;

	/**
	 * The to station of the current shot
	 */
	private ObjectNode toStation;

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

	private UnitizedDouble<Angle> lat;
	private UnitizedDouble<Angle> lon;

	private UnitizedDouble<Length> left;
	private UnitizedDouble<Length> right;
	private UnitizedDouble<Length> up;
	private UnitizedDouble<Length> down;
	private UnitizedDouble<Angle> facingAngle;

	private String inlineComment;

	private static String asTextOrNull( JsonNode node , String prop )
	{
		node = node.get( prop );
		return node == null ? null : node.asText( );
	}

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

	private <T extends UnitType<T>> JsonNode quantity( UnitizedDouble<T> value , Unit<T> defaultUnit )
	{
		if( value == null )
		{
			return NullNode.instance;
		}
		if( value.unit == Length.inches )
		{
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
		shot = null;
		toStation = null;
	}

	private void nullifyVectorLineFields( )
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
		lat = null;
		lon = null;
		left = null;
		right = null;
		up = null;
		down = null;
		facingAngle = null;
		inlineComment = null;
	}

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
					bsInc = bsInc.add( dinc );
				}

				distance = new UnitizedDouble<>( Math.sqrt( ne * ne + u * u ) , distance.unit );
			}
		}
	}

	private UnitizedDouble<Angle> avgInc( UnitizedDouble<Angle> a , UnitizedDouble<Angle> b )
	{
		if( b != null && !parser.units( ).typevb_corrected )
		{
			b = b.negate( );
		}
		if( a == null )
		{
			return b;
		}
		if( b == null )
		{
			return a;
		}

		return new UnitizedDouble<>( ( a.doubleValue( a.unit ) + b.doubleValue( a.unit ) ) * 0.5 , a.unit );
	}

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
		nullifyVectorLineFields( );
	}

	@Override
	public void abortVectorLine( )
	{
		nullifyVectorLineFields( );
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

			shot = new ObjectNode( nodeFactory );
			toStation = new ObjectNode( nodeFactory );

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

			survey.add( shot );
			survey.add( toStation );

			fromStation = toStation;
			shot = null;
			toStation = null;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void visitHorizontalVarianceOverride( VarianceOverride variance )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitVerticalVarianceOverride( VarianceOverride variance )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitInlineSegment( String segment )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitInlineNote( String note )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitComment( String comment )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitInlineComment( String inlineComment )
	{
		this.inlineComment = inlineComment;
	}

	@Override
	public void visitFlaggedStations( String flag , List<String> stations )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBlockCommentLine( String string )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitNoteLine( String station , String note )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void beginFixLine( )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void abortFixLine( )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void endFixLine( )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFixedStation( String string )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void beginUnitsLine( )
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void abortUnitsLine( )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void endUnitsLine( )
	{
		newTrip( );

		WallsUnits units = parser.units( );

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
	}
}

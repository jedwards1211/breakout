package org.breakout.wallsimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

/**
 * The state of everything that can be controlled by a #Units directive in Walls .srv files.
 * Entire groups of state can be pushed and popped by #Units Save and #Units Restore.<br>
 * <br>
 * I have chosen to name most of the fields in this class according to the parameter names in
 * Walls .srv files, rather than more human-readable names. See the
 * <a href="http://www.texasspeleologicalsurvey.org/software/walls/Walls_manual.pdf">Walls manual</a> for more
 * information.
 * 
 * @author James
 */
public class WallsUnits
{
	/**
	 * The order of measurements in vector rows.
	 */
	public List<VectorElement>		order				= Arrays.asList( VectorElement.D , VectorElement.A ,
															VectorElement.V );
	/**
	 * Unit for primary distance measurements (distance between stations, components of RECT vectors, FIX positions)
	 */
	public Unit<Length>				d_unit				= Length.meters;
	/**
	 * Unit for secondary distance measurements (distance between the instrument and station, LRUDs)
	 */
	public Unit<Length>				s_unit				= Length.meters;
	/**
	 * Frontsight azimuth unit.
	 */
	public Unit<Angle>				a_unit				= Angle.degrees;
	/**
	 * Backsight azimuth unit.
	 */
	public Unit<Angle>				ab_unit				= Angle.degrees;
	/**
	 * Frontsight inclination unit.
	 */
	public Unit<Angle>				v_unit				= Angle.degrees;
	/**
	 * Backsight inclination unit.
	 */
	public Unit<Angle>				vb_unit				= Angle.degrees;

	/**
	 * Declination (true north minus magnetic north) that would need to be added to the azimuth measurements of
	 * compass and tape data lines to obtain a vector's true north direction.
	 */
	public UnitizedDouble<Angle>	decl				= new UnitizedDouble<>( 0.0 , Angle.degrees );

	/**
	 * UTM grid convergence angle, #fix fectors would need to be rotated by this amount to obtain true north relative
	 * vectors.
	 */
	public UnitizedDouble<Angle>	grid				= new UnitizedDouble<>( 0.0 , Angle.degrees );

	/**
	 * The displacement vectors corresponding to RECT data lines would need to be rotated clockwise by this amount to
	 * obtain true north relative vectors.
	 */
	public UnitizedDouble<Angle>	rect				= new UnitizedDouble<>( 0.0 , Angle.degrees );

	/**
	 * Distance instrument correction.
	 */
	public UnitizedDouble<Length>	incd				= new UnitizedDouble<>( 0.0 , Length.meters );
	/**
	 * Frontsight azimuth instrument correction.
	 */
	public UnitizedDouble<Angle>	inca				= new UnitizedDouble<>( 0.0 , Angle.degrees );
	/**
	 * Backsight azimuth instrument correction.
	 */
	public UnitizedDouble<Angle>	incab				= new UnitizedDouble<>( 0.0 , Angle.degrees );
	/**
	 * Frontsight inclination instrument correction.
	 */
	public UnitizedDouble<Angle>	incv				= new UnitizedDouble<>( 0.0 , Angle.degrees );
	/**
	 * Backsight inclination instrument correction.
	 */
	public UnitizedDouble<Angle>	incvb				= new UnitizedDouble<>( 0.0 , Angle.degrees );
	/**
	 * Secondary distance measurement correction (distance btw. instruments and stations or LRUDs)
	 */
	public UnitizedDouble<Length>	incs				= new UnitizedDouble<>( 0.0 , Length.meters );
	/**
	 * Height adjustment, amount added to the elevation of the To station relative to the From station.
	 */
	public UnitizedDouble<Length>	inch				= new UnitizedDouble<>( 0.0 , Length.meters );
	/**
	 * Whether the azimuth backsight is corrected
	 */
	public boolean					typeab_corrected	= false;
	/**
	 * Whether the azimuth backsight is averaged into the vector azimuth
	 */
	public boolean					typeab_noAverage	= false;
	/**
	 * Whether the inclination backsight is corrected
	 */
	public boolean					typevb_corrected	= false;
	/**
	 * Whether the inclination backsight is averaged into the vector inclination
	 */
	public boolean					typevb_noAverage	= false;
	/**
	 * Whether to change or preserve the case of station names.
	 */
	public CaseType					case_				= CaseType.Mixed;
	/**
	 * Where LRUDs are positioned and oriented
	 */
	public LrudType					lrud				= LrudType.From;
	/**
	 * The order LRUDs are given in
	 */
	public List<LrudElement>		lrud_order			= Arrays.asList( LrudElement.values( ) );
	/**
	 * Taping method
	 */
	public TapeType					tape				= TapeType.IT;
	/**
	 * Flag (arbitrary text)
	 */
	public String					flag				= null;
	/**
	 * Prefixes for station names (the first is applied rightmost)
	 */
	public List<String>				prefix				= new ArrayList<String>( );
	/**
	 * Survey date
	 */
	public Date						date;
	/**
	 * Survey segment (absolute or relative path separated by /)
	 */
	public String					segment				= null;

	public String processStationName( String name )
	{
		name = case_.apply( name );
		int explicitPrefixCount = ( int ) name.chars( ).filter( c -> c == ':' ).count( );
		for( int i = explicitPrefixCount ; i < prefix.size( ) ; i++ )
		{
			name = prefix.get( i ) + ":" + name;
		}
		return name;
	}

	public static enum CaseType
	{
		/**
		 * Convert station names to uppercase.
		 */
		Upper
		{
			@Override
			public String apply( String s )
			{
				return s.toUpperCase( );
			}
		},
		/**
		 * Convert station names to lowercase.
		 */
		Lower
		{
			@Override
			public String apply( String s )
			{
				return s.toLowerCase( );
			}
		},
		/**
		 * Preserve station name case.
		 */
		Mixed
		{
			@Override
			public String apply( String s )
			{
				return s;
			}
		};

		public abstract String apply( String s );
	}

	public static enum LrudType
	{
		/**
		 * Perpendicular to shot at from station
		 */
		From,
		/**
		 * Perpendicular to shot at to station
		 */
		To,
		/**
		 * Bisecting shots at from station
		 */
		FB,
		/**
		 * Bisecting shots at to station
		 */
		TB;
	}

	public static enum TapeType
	{
		/**
		 * Instrument-to-target
		 */
		IT
		{
			@Override
			public void visit( TapeTypeVisitor visitor )
			{
				visitor.visitIT( );
			}
		},
		/**
		 * Station-to-station
		 */
		SS
		{
			@Override
			public void visit( TapeTypeVisitor visitor )
			{
				visitor.visitSS( );
			}
		},
		/**
		 * Instrument-to-station
		 */
		IS
		{
			@Override
			public void visit( TapeTypeVisitor visitor )
			{
				visitor.visitIS( );
			}
		},
		/**
		 * Station-to-target
		 */
		ST
		{
			@Override
			public void visit( TapeTypeVisitor visitor )
			{
				visitor.visitST( );
			}
		};

		public abstract void visit( TapeTypeVisitor visitor );
	}
}

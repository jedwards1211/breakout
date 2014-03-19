package org.andork.frf;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.RowFilter;

public class SurveyDesignationFilter extends RowFilter<SurveyTableModel, Integer>
{
	// String designation;
	
	Segment[ ]						segments;
	
	private static final Pattern	SEGMENT_PATTERN	= Pattern.compile( "\\s*(\\p{Alpha}+)\\s*(([0-9]+(\\.[0-9]+)?)\\s*(\\+|(-\\s*([0-9]+(\\.[0-9]+)?)))?)?\\s*" );
	
	private static class Segment
	{
		public Segment( String s )
		{
			Matcher m = SEGMENT_PATTERN.matcher( s );
			if( !m.find( ) )
			{
				throw new IllegalArgumentException( "Not a valid segment: " + s );
			}
			
			designation = m.group( 1 );
			requireFrom = true;
			requireTo = true;
			
			if( m.group( 2 ) != null )
			{
				lowerBound = upperBound = new BigDecimal( m.group( 3 ) );
			}
			
			if( m.group( 5 ) != null )
			{
				if( m.group( 5 ).equals( "+" ) )
				{
					upperBound = null;
				}
				else
				{
					upperBound = new BigDecimal( m.group( 7 ) );
				}
			}
		}
		
		String		designation;
		boolean		requireFrom;
		boolean		requireTo;
		BigDecimal	lowerBound;
		BigDecimal	upperBound;
		
		public boolean include( javax.swing.RowFilter.Entry<? extends SurveyTableModel, ? extends Integer> entry )
		{
			String from = entry.getStringValue( SurveyTable.FROM_COLUMN );
			String to = entry.getStringValue( SurveyTable.TO_COLUMN );
			
			if( ( requireFrom && !from.startsWith( designation ) ) || ( requireTo && !to.startsWith( designation ) ) )
			{
				return false;
			}
			
			if( requireFrom && from.length( ) > designation.length( ) && Character.isLetter( from.charAt( designation.length( ) ) ) )
			{
				return false;
			}
			
			if( requireTo && to.length( ) > designation.length( ) && Character.isLetter( to.charAt( designation.length( ) ) ) )
			{
				return false;
			}
			
			try
			{
				if( lowerBound != null )
				{
					if( requireFrom )
					{
						checkBound( from , lowerBound );
					}
					if( requireTo )
					{
						checkBound( to , lowerBound );
					}
				}
				
				if( upperBound != null )
				{
					if( requireFrom )
					{
						checkBound( from , upperBound );
					}
					if( requireTo )
					{
						checkBound( to , upperBound );
					}
				}
			}
			catch( Exception ex )
			{
				return false;
			}
			
			return true;
		}
		
		protected void checkBound( String s , BigDecimal bound ) throws Exception
		{
			BigDecimal number = new BigDecimal( s.substring( designation.length( ) ) );
			if( bound == lowerBound && number.compareTo( bound ) < 0 )
			{
				throw new RuntimeException( "number out of bounds" );
			}
			if( bound == upperBound && number.compareTo( bound ) > 0 )
			{
				throw new RuntimeException( "number out of bounds" );
			}
		}
	}
	
	public SurveyDesignationFilter( String designation )
	{
		// this.designation = designation;
		String[ ] split = designation.split( "[,;]" );
		segments = new Segment[ split.length ];
		int k = 0;
		for( String s : split )
		{
			try
			{
				segments[ k ] = new Segment( s );
				k++ ;
			}
			catch( Exception ex )
			{
			}
		}
		segments = Arrays.copyOf( segments , k );
	}
	
	@Override
	public boolean include( javax.swing.RowFilter.Entry<? extends SurveyTableModel, ? extends Integer> entry )
	{
		// String from = entry.getStringValue( SurveyTable.FROM_COLUMN );
		// String to = entry.getStringValue( SurveyTable.TO_COLUMN );
		//
		// return from.startsWith( designation ) && from.length( ) > designation.length( ) && Character.isDigit( from.charAt( designation.length( ) ) )
		// && to.startsWith( designation ) && to.length( ) > designation.length( ) && Character.isDigit( to.charAt( designation.length( ) ) );
		
		for( Segment segment : segments )
		{
			if( segment.include( entry ) )
			{
				return true;
			}
		}
		return false;
	}
}

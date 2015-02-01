package org.breakout.wallsimport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.q2.QArrayObject;
import org.andork.q2.QObject;
import org.andork.swing.async.Subtask;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.parse.LineTokenizer;
import org.breakout.parse.Token;
import org.breakout.parse.ValueToken;
import org.breakout.table.DaiShotVector;
import org.breakout.table.LrudXSection;
import org.breakout.table.NevShotVector;
import org.breakout.table.ParsedTextWithType;
import org.breakout.table.Shot;
import org.breakout.table.ShotVector;
import org.breakout.table.ShotVectorType;
import org.breakout.table.Station;
import org.breakout.table.SurveyDataList;
import org.breakout.table.SurveyModel;
import org.breakout.table.XSection;
import org.breakout.table.XSectionType;
import org.breakout.table.LrudXSection.FacingAzimuth;
import org.breakout.wallsimport.WallsImportMessage.Severity;
import org.breakout.wallsimport.WallsUnits.LrudType;
import org.breakout.wallsimport.WallsUnits.TapeType;

/**
 * Imports survey data from the format for David McKenzie's Walls program.
 * 
 * @author James
 *
 */
public class WallsImporter
{
	private static final Pattern	BEGIN_LRUD_PATTERN	= Pattern.compile( "[<*]" );
	private static final Pattern	END_LRUD_PATTERN	= Pattern.compile( "[>*]" );
	private static final Pattern	LRUD_DELIMITER		= Pattern.compile( "\\s+|\\s*,\\s*" );

	Stack<WallsUnits>				stack				= new Stack<>( );
	WallsUnits						units				= new WallsUnits( );
	QObject<SurveyModel>			outputModel;

	Map<String, String>				macros				= new HashMap<>( );

	String							globalComments		= "";
	String							localComments		= "";

	final List<WallsImportMessage>	statusMessages		= new ArrayList<WallsImportMessage>( );

	I18n							i18n;
	Localizer						localizer;

	Path							pathToSourceFile;
	String							line;
	String							preprocessedLine;
	int								lineNumber;
	LineTokenizer					lineTokenizer;
	int								blockCommentLevel;

	WallsParser						parser;

	public WallsImporter( I18n i18n )
	{
		this.i18n = i18n;
		localizer = i18n.forClass( WallsImporter.class );

		parser = new WallsParser( i18n );

		outputModel = new QArrayObject<>( SurveyModel.spec );
		outputModel.set( SurveyModel.shotList , new SurveyDataList<>( new Shot( ) ) );
		outputModel.set( SurveyModel.stationList , new SurveyDataList<>( new Station( ) ) );
	}

	public QObject<SurveyModel> getOutputModel( )
	{
		return outputModel;
	}

	/**
	 * Imports data from a single Walls .srv file.
	 * 
	 * @param path
	 *            the path to the file to import.
	 * @param task
	 *            if not {@code null}, this {@link Subtask} will be periodically updated to reflect the current
	 *            state of the import process, and if it is {@link Subtask#isCanceling() canceling} this method
	 *            will exit as soon as possible, even if it has not finished importing data.
	 * @throws IOException
	 */
	public void importSrvFile( Path path , Subtask task )
		throws IOException
	{
		BufferedReader reader = new BufferedReader( Channels.newReader(
			Files.newByteChannel( path , StandardOpenOption.READ ) , "cp1252" ) );

		stack.clear( );
		units = new WallsUnits( );

		pathToSourceFile = path;
		lineNumber = -1;
		blockCommentLevel = 0;

		while( ( line = reader.readLine( ) ) != null )
		{
			parseLine( );
		}
	}

	private void parseLine( )
	{
		lineNumber++;

		line = line.trim( );
		lineTokenizer = new LineTokenizer( line , lineNumber );

		lineTokenizer.pull( Character::isWhitespace );

		if( lineTokenizer.pull( '#' ) != null )
		{

		}
		else if( lineTokenizer.pull( ';' ) != null )
		{

		}
		else
		{
			if( blockCommentLevel > 0 )
			{
				return;
			}

			preprocessedLine = line;
			Shot shot = parseVectorLine( lineTokenizer );
			if( shot != null )
			{
				outputModel.get( SurveyModel.shotList ).add( shot );
			}
		}
	}

	public Shot parseVectorLine( LineTokenizer lineTokenizer )
	{
		// at this point we're assuming it will be a valid vector line; other possibilities have been ruled out.

		Shot shot = new Shot( );

		lineTokenizer.pull( Character::isWhitespace );

		// FROM STATION

		Token fromToken = lineTokenizer.pull( c -> !Character.isWhitespace( c ) );
		if( fromToken == null )
		{
			throwMessage( Severity.ERROR , localizer.getString( "expectedFromStationName" ) ,
				lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
		}
		shot.setFromStationName( units.processStationName( fromToken.image ) );

		pullRequiredWhitespace( );

		// TO STATION

		Token toToken = lineTokenizer.pull( c -> !Character.isWhitespace( c ) );
		if( toToken == null )
		{
			throwMessage( Severity.ERROR , localizer.getString( "expectedToStationName" ) ,
				lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
		}
		shot.setToStationName( units.processStationName( toToken.image ) );

		// DAU/ENU

		VectorElementParser vectorElementParser = new VectorElementParser( shot );

		for( VectorElement element : units.order )
		{
			pullRequiredWhitespace( );

			element.visit( vectorElementParser );
		}

		lineTokenizer.pull( Character::isWhitespace );

		// LRUDs

		if( lineTokenizer.pull( BEGIN_LRUD_PATTERN ) != null )
		{
			lineTokenizer.pull( Character::isWhitespace );

			LrudElementParser lrudElementParser = new LrudElementParser( shot );

			int i = 0;
			for( LrudElement element : units.lrud_order )
			{
				if( i++ > 0 )
				{
					if( lineTokenizer.pull( LRUD_DELIMITER ) == null )
					{
						throwMessage( Severity.ERROR , localizer.getString( "expectedLrudDelimiter" ) ,
							lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
					}
				}

				element.visit( lrudElementParser );
			}

			lineTokenizer.pull( LRUD_DELIMITER );

			// FACING ANGLE

			ValueToken<UnitizedDouble<Angle>> angle = parser.pullAzimuth( lineTokenizer , units.a_unit ,
				this::logMessage );
			if( angle != null )
			{
				coerceLrudXSection( shot ).setAngle(
					new FacingAzimuth( angle.value.doubleValue( shot.getAngleUnit( ) ) ) );
				coerceXSectionParsedText( shot ).setType( XSectionType.LRUD_WITH_FACING_AZIMUTH );
			}
			else
			{
				// C-flag (not used)
				lineTokenizer.pull( c -> c == 'c' || c == 'C' );
			}

			lineTokenizer.pull( Character::isWhitespace );

			if( lineTokenizer.pull( END_LRUD_PATTERN ) == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedEndLruds" ) ,
					lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
			}
		}

		return shot;
	}

	public void replaceMacroReferences( StringBuilder line )
	{
		int fromIndex = 0;

		while( fromIndex < line.length( ) )
		{
			int macroIndex = line.indexOf( "$" );

			if( macroIndex < 0 )
			{
				return;
			}

			if( macroIndex + 1 < line.length( ) && line.charAt( macroIndex + 1 ) == '(' )
			{
				int length = replaceMacroReference( line , macroIndex );
				fromIndex = macroIndex + length;
			}
			else
			{
				fromIndex = macroIndex + 1;
			}
		}
	}

	/**
	 * Replaces a macro reference at {@code start} in the given {@code line}.<br>
	 * Precondition: {@code line.substring( start ).startsWith( "$(" )}
	 * 
	 * @param line
	 *            the line in which the reference occurs. It will be replaced with the defined macro value.
	 * @param start
	 *            the start index of the reference in {@code line} to replace.
	 * @return the length of the macro value the reference was replaced with.
	 * @throws WallsImportMessageException
	 *             if the macro reference contains a space, if the referenced macro is not defined,
	 *             or if the reference is missing a closing parenthesis.
	 */
	private int replaceMacroReference( StringBuilder line , int start )
	{
		// precondition: line.substring( start ).startsWith( "$(" );

		for( int i = start + 2 ; i < line.length( ) ; i++ )
		{
			char next = line.charAt( i );

			if( Character.isWhitespace( next ) )
			{
				throw new WallsImportMessageException( new WallsImportMessage(
					localizer.getString( "noSpacesAllowedInMacroReferences" ) , Severity.FATAL ,
					pathToSourceFile , lineNumber , WallsImporter.this.line , line.toString( ) , i ) );
			}
			switch( next )
			{
			case '$':
				if( i + 1 < line.length( ) && line.charAt( i + 1 ) == '(' )
				{
					int length = replaceMacroReference( line , i );
					// subtract 1 because it will be added back by the for loop
					i += length - 1;
				}
				break;
			case ')':
				String macro = line.substring( start + 2 , i );
				String replacement = macros.get( macro );
				if( replacement == null )
				{
					throw new WallsImportMessageException( new WallsImportMessage(
						localizer.getFormattedString( "undefinedMacro" , macro ) , Severity.FATAL ,
						pathToSourceFile , lineNumber , WallsImporter.this.line , line.toString( ) , start + 2 ) );
				}
				line.replace( start , i + 1 , replacement );
				return replacement.length( );
			}
		}

		throw new WallsImportMessageException( new WallsImportMessage(
			localizer.getString( "missingClosingParenthesisInMacroReference" ) , Severity.FATAL ,
			pathToSourceFile , lineNumber , WallsImporter.this.line , line.toString( ) , line.length( ) ) );
	}

	private DaiShotVector coerceDaiShotVector( Shot shot )
	{
		ParsedTextWithType<ShotVector> text = shot.getVector( );
		if( text == null )
		{
			shot.setVector( text = new ParsedTextWithType<>( ) );
			text.setType( units.typeab_corrected ? ShotVectorType.DAIc : ShotVectorType.DAIu );
		}
		if( ! ( text.getValue( ) instanceof DaiShotVector ) )
		{
			DaiShotVector value = new DaiShotVector( );
			value.setBacksightsAreCorrected( units.typeab_corrected );
			text.setValue( new DaiShotVector( ) );
		}
		return ( DaiShotVector ) text.getValue( );
	}

	private NevShotVector coerceNevShotVector( Shot shot )
	{
		ParsedTextWithType<ShotVector> text = shot.getVector( );
		if( text == null )
		{
			shot.setVector( text = new ParsedTextWithType<>( ) );
		}
		if( ! ( text.getValue( ) instanceof NevShotVector ) )
		{
			NevShotVector value = new NevShotVector( );
			value.setDownwardIsPositive( false );
			text.setValue( value );
		}
		return ( NevShotVector ) text.getValue( );
	}

	private ParsedTextWithType<XSection> coerceXSectionParsedText( Shot shot )
	{
		boolean from = units.lrud == LrudType.FB || units.lrud == LrudType.From;
		boolean bisector = units.lrud == LrudType.FB || units.lrud == LrudType.TB;

		ParsedTextWithType<XSection> text = from ? shot.getXSectionAtFrom( ) : shot.getXSectionAtTo( );

		if( text == null )
		{
			if( from )
			{
				shot.setXSectionAtFrom( text = new ParsedTextWithType<>( ) );
			}
			else
			{
				shot.setXSectionAtTo( text = new ParsedTextWithType<>( ) );
			}
			text.setType( bisector ? XSectionType.BISECTOR_LRUD : XSectionType.PERPENDICULAR_LRUD );
		}
		return text;
	}

	private LrudXSection coerceLrudXSection( Shot shot )
	{
		boolean bisector = units.lrud == LrudType.FB || units.lrud == LrudType.TB;

		ParsedTextWithType<XSection> text = coerceXSectionParsedText( shot );

		if( ! ( text.getValue( ) instanceof LrudXSection ) )
		{
			LrudXSection value = new LrudXSection( );
			value.setAngle( bisector ? LrudXSection.XAngle.BISECTOR
				: LrudXSection.XAngle.PERPENDICULAR );
			text.setValue( value );
		}
		return ( LrudXSection ) text.getValue( );
	}

	private void pullRequiredWhitespace( )
	{
		if( lineTokenizer.pull( Character::isWhitespace ) == null )
		{
			throwMessage( Severity.ERROR , localizer.getString( "expectedWhitespace" ) ,
				lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
		}
	}

	private WallsImportMessage createMessage( Severity severity , String message , int lineNumber , int column )
	{
		return new WallsImportMessage(
			message , severity , pathToSourceFile , lineNumber , line , preprocessedLine , column );
	}

	private void logMessage( Severity severity , String message , int line , int column )
	{
		statusMessages.add( createMessage( severity , message , lineNumber , column ) );
	}

	private void throwMessage( Severity severity , String message , int line , int column )
	{
		throw new WallsImportMessageException( createMessage( severity , message , line , column ) );
	}

	private void adjustVector( Shot shot , UnitizedDouble<Length> instrumentHeightAboveStation ,
		UnitizedDouble<Length> targetHeightAboveStation )
	{
		DaiShotVector vector = coerceDaiShotVector( shot );

		double distance = vector.getDistance( );

		double azimuthRad = Angle.type.convert(
			average( vector.getFrontsightAzimuth( ) , vector.getBacksightAzimuth( ) ) ,
			shot.getAngleUnit( ) , Angle.radians );

		double inclination = average( vector.getFrontsightInclination( ) , vector.getBacksightInclination( ) );
		double inclinationRad = Angle.type.convert( inclination , shot.getAngleUnit( ) , Angle.radians );

		// compute the (north, east, up) components of the vector

		double up = Math.sin( inclinationRad ) * distance;
		double north = Math.cos( azimuthRad ) * Math.cos( inclinationRad ) * distance;
		double east = Math.sin( azimuthRad ) * Math.cos( inclinationRad ) * distance;

		// apply height adjustments to up

		if( instrumentHeightAboveStation != null )
		{
			up += instrumentHeightAboveStation.doubleValue( shot.getLengthUnit( ) );
			// apply correction to the instrument height measurement...
			up += units.incs.doubleValue( shot.getLengthUnit( ) );
		}
		if( targetHeightAboveStation != null )
		{
			up -= targetHeightAboveStation.doubleValue( shot.getLengthUnit( ) );
			// apply correction to the instrument height measurement...
			up -= units.incs.doubleValue( shot.getLengthUnit( ) );
		}
		// apply systematic height correction...
		up += units.inch.doubleValue( shot.getLengthUnit( ) );

		// recompute distance and inclination from (north, east, adjusted up)

		vector.setDistance( Math.sqrt( north * north + east * east + up * up ) );
		double northEast = Math.sqrt( north * north + east * east );
		double newInclinationRad = Math.atan2( up , northEast );
		double newInclination = Angle.type.convert( newInclinationRad , Angle.radians , shot.getAngleUnit( ) );

		// adjust the frontsight and backsight inclination of the shot by the amount the average inclination
		// changed.

		if( vector.getFrontsightInclination( ) != null )
		{
			vector.setFrontsightInclination( vector.getFrontsightInclination( ) + newInclination - inclination );
		}
		if( vector.getBacksightInclination( ) != null )
		{
			vector.setBacksightInclination( vector.getBacksightInclination( ) + newInclination - inclination );
		}
	}

	private static double average( Double frontsightAzimuth , Double backsightAzimuth )
	{
		return frontsightAzimuth == null ? backsightAzimuth : backsightAzimuth == null ? frontsightAzimuth
			: ( frontsightAzimuth + backsightAzimuth ) * 0.5;
	}

	private class VectorElementParser implements VectorElementVisitor
	{
		private Shot	shot;

		private VectorElementParser( Shot shot )
		{
			this.shot = shot;
		}

		@Override
		public void visitDistance( )
		{
			ValueToken<UnitizedDouble<Length>> distance = parser.pullUnsignedDistance( lineTokenizer , units.d_unit ,
				WallsImporter.this::logMessage );
			if( distance == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedDistance" ) , lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			if( shot.getLengthUnit( ) == null )
			{
				shot.setLengthUnit( distance.value.unit );
			}

			coerceDaiShotVector( shot ).setDistance( distance.value.doubleValue( shot.getLengthUnit( ) ) +
				units.incd.doubleValue( shot.getLengthUnit( ) ) );
		}

		@Override
		public void visitAzimuth( )
		{
			ValueToken<UnitizedDouble<Angle>> bs = null , fs = parser.pullAzimuth( lineTokenizer , units.v_unit ,
				WallsImporter.this::logMessage );
			Token slash = lineTokenizer.pull( '/' );
			if( slash != null )
			{
				bs = parser.pullAzimuth( lineTokenizer , units.vb_unit , WallsImporter.this::logMessage );
			}
			else if( fs == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedAzimuth" ) , lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			if( shot.getAngleUnit( ) == null )
			{
				shot.setAngleUnit( fs.value.unit );
			}

			if( fs != null )
			{
				coerceDaiShotVector( shot ).setFrontsightAzimuth( fs.value.doubleValue( shot.getAngleUnit( ) ) +
					units.inca.doubleValue( shot.getAngleUnit( ) ) + units.decl.doubleValue( shot.getAngleUnit( ) ) );
			}
			if( bs != null && ( fs == null || !units.typeab_noAverage ) )
			{
				coerceDaiShotVector( shot ).setBacksightAzimuth( bs.value.doubleValue( shot.getAngleUnit( ) ) +
					units.incab.doubleValue( shot.getAngleUnit( ) ) + units.decl.doubleValue( shot.getAngleUnit( ) ) );
			}
		}

		@Override
		public void visitInclination( )
		{
			ValueToken<UnitizedDouble<Angle>> bs = null , fs = parser.pullInclination( lineTokenizer , units.v_unit ,
				WallsImporter.this::logMessage );
			Token slash = lineTokenizer.pull( '/' );
			if( slash != null )
			{
				bs = parser.pullInclination( lineTokenizer , units.vb_unit , WallsImporter.this::logMessage );
			}
			else if( fs == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedInclination" ) ,
					lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			if( shot.getAngleUnit( ) == null )
			{
				shot.setAngleUnit( fs.value.unit );
			}

			if( fs != null )
			{
				coerceDaiShotVector( shot ).setFrontsightInclination( fs.value.doubleValue( shot.getAngleUnit( ) ) +
					units.incv.doubleValue( shot.getAngleUnit( ) ) );
			}
			if( bs != null && ( fs == null || !units.typevb_noAverage ) )
			{
				double value = bs.value.doubleValue( shot.getAngleUnit( ) );
				if( units.typeab_corrected != units.typevb_corrected )
				{
					value = -value;
				}
				coerceDaiShotVector( shot ).setBacksightInclination(
					value + units.incvb.doubleValue( shot.getAngleUnit( ) ) );
			}
		}

		@Override
		public void visitEast( )
		{
			ValueToken<UnitizedDouble<Length>> east = parser.pullSignedDistance( lineTokenizer , units.d_unit ,
				WallsImporter.this::logMessage );
			if( east == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedEast" ) , lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			if( shot.getLengthUnit( ) == null )
			{
				shot.setLengthUnit( east.value.unit );
			}

			coerceNevShotVector( shot ).setEastOffset( east.value.doubleValue( shot.getLengthUnit( ) ) );
		}

		@Override
		public void visitNorth( )
		{
			ValueToken<UnitizedDouble<Length>> north = parser.pullSignedDistance( lineTokenizer , units.d_unit ,
				WallsImporter.this::logMessage );
			if( north == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedNorth" ) , lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			if( shot.getLengthUnit( ) == null )
			{
				shot.setLengthUnit( north.value.unit );
			}

			coerceNevShotVector( shot ).setNorthOffset( north.value.doubleValue( shot.getLengthUnit( ) ) );
		}

		@Override
		public void visitUp( )
		{
			ValueToken<UnitizedDouble<Length>> up = parser.pullSignedDistance( lineTokenizer , units.d_unit ,
				WallsImporter.this::logMessage );
			if( up == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedUp" ) , lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			if( shot.getLengthUnit( ) == null )
			{
				shot.setLengthUnit( up.value.unit );
			}

			coerceNevShotVector( shot ).setVerticalOffset( up.value.doubleValue( shot.getLengthUnit( ) ) );
		}
	}

	private class LrudElementParser implements LrudElementVisitor
	{
		private Shot	shot;

		private LrudElementParser( Shot shot )
		{
			this.shot = shot;
		}

		private void handleLrudElement( Consumer<Double> setter )
		{
			ValueToken<UnitizedDouble<Length>> dist = parser.pullUnsignedDistance( lineTokenizer , units.s_unit ,
				WallsImporter.this::logMessage );
			if( dist == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedLrudElement" ) ,
					lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			setter.accept( dist.value.doubleValue( shot.getLengthUnit( ) ) );
		}

		@Override
		public void visitLeft( )
		{
			handleLrudElement( left -> coerceLrudXSection( shot ).setLeft( left ) );
		}

		@Override
		public void visitRight( )
		{
			handleLrudElement( right -> coerceLrudXSection( shot ).setRight( right ) );
		}

		@Override
		public void visitUp( )
		{
			handleLrudElement( up -> coerceLrudXSection( shot ).setUp( up ) );

		}

		@Override
		public void visitDown( )
		{
			handleLrudElement( down -> coerceLrudXSection( shot ).setDown( down ) );
		}
	}
}

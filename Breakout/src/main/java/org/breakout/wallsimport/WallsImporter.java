package org.breakout.wallsimport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.q2.QArrayObject;
import org.andork.q2.QObject;
import org.andork.swing.async.Subtask;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.util.StringUtils;
import org.breakout.parse.LineTokenizer;
import org.breakout.parse.Token;
import org.breakout.parse.ValueToken;
import org.breakout.table.DaiShotVector;
import org.breakout.table.LrudXSection;
import org.breakout.table.LrudXSection.FacingAzimuth;
import org.breakout.table.NevShotVector;
import org.breakout.table.ParsedTextWithType;
import org.breakout.table.Shot;
import org.breakout.table.ShotVector;
import org.breakout.table.ShotVectorType;
import org.breakout.table.Station;
import org.breakout.table.SurveyDataColumnDef;
import org.breakout.table.SurveyDataColumnType;
import org.breakout.table.SurveyDataList;
import org.breakout.table.SurveyModel;
import org.breakout.table.XSection;
import org.breakout.table.XSectionType;
import org.breakout.wallsimport.WallsImportMessage.Severity;
import org.breakout.wallsimport.WallsUnits.LrudType;

/**
 * Imports survey data from the format for David McKenzie's Walls program.
 * 
 * @author James
 *
 */
public class WallsImporter
{
	private static final Pattern		BEGIN_LRUD_PATTERN			= Pattern.compile( "[<*]" );
	private static final Pattern		END_LRUD_PATTERN			= Pattern.compile( "[>*]" );
	private static final Pattern		LRUD_DELIMITER				= Pattern.compile( "\\s+|\\s*,\\s*" );

	private static final Pattern		PREFIX_PATTERN				= Pattern.compile( "pre(fix)?([123])?" );
	Stack<WallsUnits>					stack						= new Stack<>( );
	WallsUnits							units						= new WallsUnits( );
	QObject<SurveyModel>				outputModel;

	Map<String, String>					macros						= new HashMap<>( );

	String								globalComments				= "";
	String								localComments				= "";

	final List<WallsImportMessage>		statusMessages				= new ArrayList<WallsImportMessage>( );

	I18n								i18n;
	Localizer							localizer;

	Path								pathToSourceFile;
	String								line;
	String								preprocessedLine;
	int									lineNumber;
	LineTokenizer						lineTokenizer;
	int									blockCommentLevel;

	WallsParser							parser;

	private int							SHOT_CUSTOM_COLUMN_COUNT	= 1;
	private int							SHOT_LINE_COMMENT_COLUMN	= 0;

	private final Map<String, Runnable>	unitsOptionHandlers;

	public WallsImporter( I18n i18n )
	{
		this.i18n = i18n;
		localizer = i18n.forClass( WallsImporter.class );

		parser = new WallsParser( i18n );

		outputModel = new QArrayObject<>( SurveyModel.spec );
		SurveyDataList<Shot> shotList = new SurveyDataList<>( new Shot( ) );
		shotList.setCustomColumnDefs( Arrays.asList( new SurveyDataColumnDef( "Line Comment" ,
			SurveyDataColumnType.STRING ) ) );
		SurveyDataList<Station> stationList = new SurveyDataList<>( new Station( ) );
		stationList.setCustomColumnDefs( Arrays.asList( new SurveyDataColumnDef( "Line Comment" ,
			SurveyDataColumnType.STRING ) ) );
		outputModel.set( SurveyModel.shotList , shotList );
		outputModel.set( SurveyModel.stationList , stationList );

		unitsOptionHandlers = Collections.unmodifiableMap( createUnitsOptionHandlers( ) );
	}

	private Map<String, Runnable> createUnitsOptionHandlers( )
	{
		Map<String, Runnable> result = new HashMap<>( );

		Runnable metersHandler = ( ) ->
		{
			units.d_unit = units.s_unit = Length.meters;
		};

		result.put( "m" , metersHandler );
		result.put( "meters" , metersHandler );

		Runnable feetHandler = ( ) ->
		{
			units.d_unit = units.s_unit = Length.feet;
		};

		result.put( "f" , feetHandler );
		result.put( "feet" , feetHandler );

		result.put( "d" , ( ) ->
		{
			pullRequiredEquals( );
			units.d_unit = pullRequiredDistanceUnit( );
		} );
		result.put( "s" , ( ) ->
		{
			pullRequiredEquals( );
			units.s_unit = pullRequiredDistanceUnit( );
		} );

		return result;
	}

	private void pullRequiredEquals( )
	{
		if( lineTokenizer.pull( '=' ) == null )
		{
			throwMessage( Severity.ERROR , "expectedEqualsSign" , lineTokenizer.lineNumber( ) ,
				lineTokenizer.columnNumber( ) );
		}
	}

	private Unit<Length> pullRequiredDistanceUnit( )
	{
		Token token = lineTokenizer.pull( c -> !Character.isWhitespace( c ) && c != ';' );
		Unit<Length> unit = parser.parseDistanceUnit( token == null ? "" : token.image );
		if( unit == null )
		{
			throwMessage( Severity.ERROR , "expectedDistanceUnit" , token.beginLine , token.beginColumn );
		}
		return unit;
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

		if( line.charAt( 0 ) == '#' )
		{
			// Process macros

			StringBuilder sb = new StringBuilder( line );
			replaceMacroReferences( sb );
			preprocessedLine = sb.toString( );
			lineTokenizer = new LineTokenizer( preprocessedLine , lineNumber );
		}
		else
		{
			lineTokenizer = new LineTokenizer( line , lineNumber );
		}

		lineTokenizer.pull( Character::isWhitespace );

		if( lineTokenizer.pull( '#' ) != null )
		{
			parseDirectiveLine( );
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

	private void parseDirectiveLine( )
	{
		Token token = lineTokenizer.pull( c -> !Character.isWhitespace( c ) && c != ';' );
		String directive = token.image.toLowerCase( );

		Integer prefixIndex;

		if( directive.equals( "[" ) )
		{
			blockCommentLevel++;
		}
		else if( directive.equals( "]" ) )
		{
			if( blockCommentLevel == 0 )
			{
				throwMessage( Severity.ERROR , localizer.getString( "missingOpenBlockComment" ) ,
					token.beginLine , token.beginColumn );
			}
			blockCommentLevel--;
		}
		else if( blockCommentLevel > 0 )
		{
			return;
		}
		else if( directive.equals( "units" ) )
		{
			parseUnits( );
		}
		else if( ( prefixIndex = prefixIndex( directive ) ) != null )
		{
			parsePrefix( prefixIndex );
		}
	}

	private void parseUnits( )
	{
		while( !lineTokenizer.isAtEnd( ) )
		{
			lineTokenizer.pull( Character::isWhitespace );

			if( lineTokenizer.pull( '$' ) != null )
			{
				parseMacroDefinition( );
			}
			else
			{
				Token optionName = lineTokenizer.pull( c -> !Character.isWhitespace( c ) && c != '=' && c != ';' );
				if( optionName == null )
				{
					break;
				}
				Runnable r = unitsOptionHandlers.get( optionName.image.toLowerCase( ) );
				if( r == null )
				{
					throwMessage( Severity.ERROR , "invalidUnitsOption" , optionName.beginLine , optionName.beginColumn );
				}
				r.run( );
			}
		}
	}

	private void parseMacroDefinition( )
	{
		Token macroName = lineTokenizer.pull( c -> !Character.isWhitespace( c ) && c != '=' && c != ';' );
		if( macroName == null )
		{
			throwMessage( Severity.ERROR , localizer.getString( "missingMacroName" ) ,
				lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
		}

		String newValue = "";

		if( lineTokenizer.pull( '=' ) != null )
		{
			ValueToken<String> value = lineTokenizer.pullNonWhitespaceOrQuoted( );
			if( value != null )
			{
				newValue = value.value;
			}
		}

		macros.put( macroName.image , newValue );
	}

	private void parsePrefix( int prefixIndex )
	{
		lineTokenizer.pull( Character::isWhitespace );
		Token prefixToken = lineTokenizer.pull( c -> c != ';' );
		String prefix = prefixToken == null ? "" : prefixToken.image.trim( );

		while( units.prefix.size( ) <= prefixIndex )
		{
			units.prefix.add( "" );
		}
		units.prefix.set( prefixIndex , prefix );

		// remove trailing empty prefixes

		while( units.prefix.size( ) > 0 && StringUtils.isNullOrEmpty( units.prefix.get( units.prefix.size( ) - 1 ) ) )
		{
			units.prefix.remove( units.prefix.size( ) - 1 );
		}
	}

	private Integer prefixIndex( String directive )
	{
		Matcher m = PREFIX_PATTERN.matcher( directive );
		if( m.matches( ) )
		{
			return m.group( 2 ) == null ? 0 : Integer.parseInt( m.group( 2 ) ) - 1;
		}
		return null;
	}

	public Shot parseVectorLine( LineTokenizer lineTokenizer )
	{
		// at this point we're assuming it will be a valid vector line; other possibilities have been ruled out.

		Shot shot = new Shot( );
		shot.setCustom( new Object[ SHOT_CUSTOM_COLUMN_COUNT ] );

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

		// INSTRUMENT HEIGHT

		ValueToken<UnitizedDouble<Length>> token = parser.pullSignedDistance( lineTokenizer , units.s_unit ,
			this::logMessage );

		if( token != null )
		{
			coerceDaiShotVector( shot ).setInstrumentHeight( token.value.add( units.incs ) );

			lineTokenizer.pull( Character::isWhitespace );

			// TARGET HEIGHT

			token = parser.pullSignedDistance( lineTokenizer , units.s_unit , this::logMessage );

			if( token != null )
			{
				coerceDaiShotVector( shot ).setTargetHeight( token.value.add( units.incs ).add( units.inch ) );

				lineTokenizer.pull( Character::isWhitespace );
			}
		}

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
				coerceLrudXSection( shot ).setAngle( new FacingAzimuth( angle.value ) );
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

		lineTokenizer.pull( Character::isWhitespace );

		if( lineTokenizer.pull( ';' ) != null )
		{
			Token comment = lineTokenizer.pullRemaining( );
			shot.getCustom( )[ SHOT_LINE_COMMENT_COLUMN ] = comment.image;
		}

		return shot;
	}

	public void replaceMacroReferences( StringBuilder line )
	{
		int fromIndex = 0;

		while( fromIndex < line.length( ) )
		{
			int macroIndex = line.indexOf( "$" , fromIndex );

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

			coerceDaiShotVector( shot ).setDistance( distance.value.add( units.incd ) );
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

			if( fs != null )
			{
				coerceDaiShotVector( shot ).setFrontsightAzimuth( fs.value.add( units.inca ).add( units.decl ) );
			}
			if( bs != null && ( fs == null || !units.typeab_noAverage ) )
			{
				coerceDaiShotVector( shot ).setBacksightAzimuth( bs.value.add( units.incab ).add( units.decl ) );
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

			if( fs != null )
			{
				coerceDaiShotVector( shot ).setFrontsightInclination( fs.value.add( units.incv ) );
			}
			if( bs != null && ( fs == null || !units.typevb_noAverage ) )
			{
				UnitizedDouble<Angle> value = bs.value;
				if( units.typeab_corrected != units.typevb_corrected )
				{
					value = value.negate( );
				}
				coerceDaiShotVector( shot ).setBacksightInclination( value.add( units.incvb ) );
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

			coerceNevShotVector( shot ).setEastOffset( east.value );
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

			coerceNevShotVector( shot ).setNorthOffset( north.value );
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

			coerceNevShotVector( shot ).setVerticalOffset( up.value );
		}
	}

	private class LrudElementParser implements LrudElementVisitor
	{
		private Shot	shot;

		private LrudElementParser( Shot shot )
		{
			this.shot = shot;
		}

		private void handleLrudElement( Consumer<UnitizedDouble<Length>> setter )
		{
			ValueToken<UnitizedDouble<Length>> dist = parser.pullUnsignedDistance( lineTokenizer , units.s_unit ,
				WallsImporter.this::logMessage );
			if( dist == null )
			{
				throwMessage( Severity.ERROR , localizer.getString( "expectedLrudElement" ) ,
					lineTokenizer.lineNumber( ) ,
					lineTokenizer.columnNumber( ) );
			}

			setter.accept( dist.value );
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

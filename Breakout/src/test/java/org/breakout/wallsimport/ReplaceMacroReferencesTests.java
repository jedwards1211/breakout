package org.breakout.wallsimport;

import java.nio.file.Paths;

import org.andork.i18n.I18n;
import org.breakout.wallsimport.WallsImportMessageException;
import org.breakout.wallsimport.WallsImporter;
import org.junit.Assert;
import org.junit.Test;

public class ReplaceMacroReferencesTests
{
	@Test
	public void testBackToBackReferences( )
	{
		WallsImporter importer = new WallsImporter( new I18n( ) );
		importer.macros.put( "macro1" , "value of macro1" );
		importer.macros.put( "macro2" , "value of macro2" );

		StringBuilder line = new StringBuilder( "text with $(macro1)$(macro2) mixed in" );
		importer.replaceMacroReferences( line );

		Assert.assertEquals( "text with value of macro1value of macro2 mixed in" , line.toString( ) );
	}

	@Test
	public void testNestedReferences( )
	{
		WallsImporter importer = new WallsImporter( new I18n( ) );
		importer.macros.put( "index" , "2" );
		importer.macros.put( "macro2" , "value of macro2" );

		StringBuilder line = new StringBuilder( "text with $(macro$(index)) mixed in" );
		importer.replaceMacroReferences( line );

		Assert.assertEquals( "text with value of macro2 mixed in" , line.toString( ) );
	}

	@Test
	public void testUndefinedMacro( )
	{
		I18n i18n = new I18n( );
		WallsImporter importer = new WallsImporter( i18n );
		importer.pathToSourceFile = Paths.get( "fake.srv" );
		importer.line = "line with an undefined $(weird) macro";
		importer.lineNumber = 3;
		StringBuilder sb = new StringBuilder( importer.line );
		try
		{
			importer.replaceMacroReferences( sb );
			Assert.fail( "expected an exception" );
		}
		catch( WallsImportMessageException ex )
		{
			Assert.assertEquals(
				i18n.forClass( WallsImporter.class ).getFormattedString( "undefinedMacro" , "weird" ) , ex
					.getImportMessage( ).getMessage( ) );
			Assert.assertEquals( Paths.get( "fake.srv" ) , ex.getImportMessage( ).getPathToSourceFile( ) );
			Assert.assertEquals( 3 , ex.getImportMessage( ).getLineNumber( ) );
			Assert.assertEquals( importer.line , ex.getImportMessage( ).getLine( ) );
			Assert.assertEquals( importer.line , ex.getImportMessage( ).getPreprocessedLine( ) );
			Assert.assertEquals( importer.line.indexOf( '$' ) + 2 , ex.getImportMessage( ).getColumnNumber( ) );
		}
	}

	@Test
	public void testSpaceInsideMacroReference( )
	{
		I18n i18n = new I18n( );
		WallsImporter importer = new WallsImporter( i18n );
		importer.pathToSourceFile = Paths.get( "fake.srv" );
		importer.line = "line with a $(macro with spaces in it)";
		importer.lineNumber = 3;
		StringBuilder sb = new StringBuilder( importer.line );
		try
		{
			importer.replaceMacroReferences( sb );
			Assert.fail( "expected an exception" );
		}
		catch( WallsImportMessageException ex )
		{
			Assert.assertEquals(
				i18n.forClass( WallsImporter.class ).getString( "noSpacesAllowedInMacroReferences" ) ,
				ex.getImportMessage( ).getMessage( ) );
			Assert.assertEquals( Paths.get( "fake.srv" ) , ex.getImportMessage( ).getPathToSourceFile( ) );
			Assert.assertEquals( 3 , ex.getImportMessage( ).getLineNumber( ) );
			Assert.assertEquals( importer.line , ex.getImportMessage( ).getLine( ) );
			Assert.assertEquals( importer.line , ex.getImportMessage( ).getPreprocessedLine( ) );
			Assert.assertEquals( importer.line.indexOf( ' ' , importer.line.indexOf( '$' ) ) , ex.getImportMessage( )
				.getColumnNumber( ) );
		}
	}

	@Test
	public void testMissingClosingParenthesis( )
	{
		I18n i18n = new I18n( );
		WallsImporter importer = new WallsImporter( i18n );
		importer.pathToSourceFile = Paths.get( "fake.srv" );
		importer.line = "line with a $(macro-$(with)-closing-parenthesis";
		importer.macros.put( "with" , "missing" );
		importer.lineNumber = 3;
		StringBuilder sb = new StringBuilder( importer.line );
		try
		{
			importer.replaceMacroReferences( sb );
			Assert.fail( "expected an exception" );
		}
		catch( WallsImportMessageException ex )
		{
			Assert.assertEquals(
				i18n.forClass( WallsImporter.class ).getString( "missingClosingParenthesisInMacroReference" ) ,
				ex.getImportMessage( ).getMessage( ) );
			Assert.assertEquals( Paths.get( "fake.srv" ) , ex.getImportMessage( ).getPathToSourceFile( ) );
			Assert.assertEquals( 3 , ex.getImportMessage( ).getLineNumber( ) );
			Assert.assertEquals( importer.line , ex.getImportMessage( ).getLine( ) );
			String expectedPreprocessedLine = "line with a $(macro-missing-closing-parenthesis";
			Assert.assertEquals( expectedPreprocessedLine , ex.getImportMessage( )
				.getPreprocessedLine( ) );
			Assert.assertEquals( expectedPreprocessedLine.length( ) , ex.getImportMessage( ).getColumnNumber( ) );
		}
	}
}

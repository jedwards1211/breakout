package org.breakout.wallsimport;

import java.io.IOException;

import org.andork.collect.InputStreamLineIterable;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WallsKauaToMetacaveTest
{
	@Test
	public void testParseAll( ) throws IOException
	{
		WallsParser parser = new WallsParser( );
		ToMetacaveWallsLineVisitor visitor = new ToMetacaveWallsLineVisitor( parser );
		//visitor.setUsePrefixesAsCaveNames( true );
		parser.setVisitor( visitor );

		InputStreamLineIterable.grepFiles( "kauadata/*.srv" ).forEach( System.out::println );

		parser.parse( InputStreamLineIterable.grepFiles( "kauadata/*.srv" ) );

		ObjectMapper om = new ObjectMapper( );
		JsonGenerator gen = new JsonFactory( ).createGenerator( System.out );
		gen.useDefaultPrettyPrinter( );
		om.writeTree( gen , visitor.getMetacaveData( ) );
	}
}

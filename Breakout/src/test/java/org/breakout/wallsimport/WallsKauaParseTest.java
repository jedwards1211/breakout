package org.breakout.wallsimport;

import java.io.IOException;

import org.andork.collect.InputStreamLineIterable;
import org.junit.Test;

public class WallsKauaParseTest
{
	@Test
	public void testParseAll( ) throws IOException
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		InputStreamLineIterable.grepFiles( "kauadata/*.srv" ).forEach( System.out::println );

		parser.parse( InputStreamLineIterable.grepFiles( "kauadata/*.srv" ) );
	}
}

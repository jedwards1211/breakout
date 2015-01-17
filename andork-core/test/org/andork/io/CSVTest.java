package org.andork.io;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

public class CSVTest
{
	@Test
	public void testParseLine( )
	{
		CSVFormat csv = new CSVFormat( );

		csv.trimWhitespace( false );

		String original = "\"Test \"  \"\"one,\"Test \"\" two\",1997\n,  Ford,,E350,\"Super, \"\"luxurious\"\" truck\",";
		List<String> fields = Arrays.asList( "Test   one" , "Test \" two" , "1997\n" , "  Ford" , "" , "E350" ,
			"Super, \"luxurious\" truck" , "" );

		List<String> parsed = csv.parseLine( original );

		Assert.assertEquals( fields , parsed );

		String formatted = csv.formatLine( fields );

		Assert.assertEquals( "Test   one,\"Test \"\" two\",1997\n,  Ford,,E350,\"Super, \"\"luxurious\"\" truck\"," ,
			formatted );
	}
}

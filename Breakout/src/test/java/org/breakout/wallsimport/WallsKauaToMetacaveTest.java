package org.breakout.wallsimport;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.andork.collect.InputStreamLineIterable;
import org.andork.swing.QuickTestFrame;
import org.breakout.model.MetacaveShotTableModel;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WallsKauaToMetacaveTest
{
	public static void main( String[ ] args ) throws Exception
	{
		new WallsKauaToMetacaveTest( ).testParseAll( );
	}

	@Test
	public void testParseAll( ) throws IOException
	{
		WallsParser parser = new WallsParser( );
		ToMetacaveWallsVisitor visitor = new ToMetacaveWallsVisitor( parser );
		//visitor.setUsePrefixesAsCaveNames( true );
		parser.setVisitor( visitor );

		InputStreamLineIterable.grepFiles( "kauadata/*.srv" ).forEach( System.out::println );

		parser.parse( InputStreamLineIterable.grepFiles( "kauadata/*.srv" ) );

		ObjectMapper om = new ObjectMapper( );
		JsonGenerator gen = new JsonFactory( ).createGenerator( System.out );
		gen.useDefaultPrettyPrinter( );
		om.writeTree( gen , visitor.getMetacaveData( ) );

		MetacaveShotTableModel model = new MetacaveShotTableModel( );
		model.setData( visitor.getMetacaveData( ) );

		JTable table = new JTable( model );
		JScrollPane scrollPane = new JScrollPane( table );

		JFrame frame = QuickTestFrame.frame( scrollPane );
		frame.setVisible( true );
	}
}

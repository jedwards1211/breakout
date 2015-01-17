package org.andork.breakout.wallsimport;

import java.io.BufferedReader;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;

import org.andork.breakout.table.SurveyModel;
import org.andork.q2.QObject;
import org.andork.swing.async.Subtask;

public class WallsImporter
{
	private static void importSrvFile( SeekableByteChannel channel , QObject<SurveyModel> model , Subtask task )
	{
		BufferedReader reader = new BufferedReader( Channels.newReader( channel , "cp1252" ) );
	}
}

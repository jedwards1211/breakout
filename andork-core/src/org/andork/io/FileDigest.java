package org.andork.io;

import static org.andork.func.ShorterCode.swallowException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

public class FileDigest
{
	public static void main( String[ ] args ) throws Exception
	{
		Path path = Paths.get( FileDigest.class.getResource( "logging.properties" ).toURI( ) );
		System.out.println( format( checksum( path , "md5" ) ) );
	}

	public static String format( byte[ ] checksum )
	{
		byte[ ] b2 = new byte[ checksum.length + 1 ];
		System.arraycopy( checksum , 0 , b2 , 1 , checksum.length );
		b2[ 0 ] = 0;
		return String.format( "%x" , new BigInteger( b2 ) );
	}

	public static byte[ ] checksum( Path path , String algorithm ) throws IOException
	{
		try( FileChannel channel = FileChannel.open( path , StandardOpenOption.READ ) )
		{
			MessageDigest digest = swallowException( ( ) -> MessageDigest.getInstance( algorithm ) );

			ByteBuffer buffer = ByteBuffer.allocateDirect( 1024 );

			int bytesRead;
			while( ( bytesRead = channel.read( buffer ) ) > 0 )
			{
				buffer.position( 0 );
				buffer.limit( bytesRead );
				digest.update( buffer );
				buffer.position( 0 );
				buffer.limit( buffer.capacity( ) );
			}

			return digest.digest( );
		}
	}
}

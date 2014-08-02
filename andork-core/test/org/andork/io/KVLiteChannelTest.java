package org.andork.io;

import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.andork.collect.CollectionUtils;
import org.andork.collect.LineIterable;
import org.andork.io.KVLiteChannel.ChangeType;
import org.andork.io.KVLiteChannel.Key;
import org.andork.io.KVLiteChannel.Record;
import org.andork.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class KVLiteChannelTest
{
	@Test
	public void singleWriteReadTest( ) throws Exception
	{
		FileChannel channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE , StandardOpenOption.CREATE , StandardOpenOption.TRUNCATE_EXISTING );
		
		KVLiteChannel kvChannel = new KVLiteChannel( channel , 128 );
		
		String expected = StringUtils.multiply( "this is a test. " , 100 );
		
		kvChannel.write( Arrays.asList( new Record(
				new Key( "key1".getBytes( ) ) , ChangeType.UPDATE , expected.getBytes( ) ) ) );
		
		byte[ ] b = kvChannel.read( new Key( "key1".getBytes( ) ) );
		
		String actual = new String( b );
		
		Assert.assertEquals( expected , actual );
		
		channel.close( );
	}
	
	@Test
	public void singleWriteReopenReadTest( ) throws Exception
	{
		FileChannel channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE , StandardOpenOption.CREATE , StandardOpenOption.TRUNCATE_EXISTING );
		
		KVLiteChannel kvChannel = new KVLiteChannel( channel , 128 );
		
		String expected = StringUtils.multiply( "this is a test. " , 100 );
		
		kvChannel.write( Arrays.asList( new Record(
				new Key( "key1".getBytes( ) ) , ChangeType.UPDATE , expected.getBytes( ) ) ) );
		
		channel.close( );
		
		channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE );
		
		kvChannel = KVLiteChannel.load( channel );
		
		byte[ ] b = kvChannel.read( new Key( "key1".getBytes( ) ) );
		
		String actual = new String( b );
		
		Assert.assertEquals( expected , actual );
		
		channel.close( );
	}
	
	@Test
	public void overwriteReopenReadTest( ) throws Exception
	{
		FileChannel channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE , StandardOpenOption.CREATE , StandardOpenOption.TRUNCATE_EXISTING );
		
		KVLiteChannel kvChannel = new KVLiteChannel( channel , 128 );
		
		String expected = StringUtils.multiply( "this is a test. " , 100 );
		
		kvChannel.write( Arrays.asList( new Record(
				new Key( "key1".getBytes( ) ) , ChangeType.UPDATE , expected.getBytes( ) ) ) );
		
		expected = StringUtils.multiply( "this is a test 2. " , 100 );
		
		kvChannel.write( Arrays.asList( new Record(
				new Key( "key1".getBytes( ) ) , ChangeType.UPDATE , expected.getBytes( ) ) ) );
		
		channel.close( );
		
		channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE );
		
		kvChannel = KVLiteChannel.load( channel );
		
		byte[ ] b = kvChannel.read( new Key( "key1".getBytes( ) ) );
		
		String actual = new String( b );
		
		Assert.assertEquals( expected , actual );
		Assert.assertEquals( Collections.singleton( new Key( "key1".getBytes( ) ) ) , kvChannel.keySet( ) );
		
		channel.close( );
	}
	
	@Test
	public void overwriteReopenReadTest2( ) throws Exception
	{
		FileChannel channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE , StandardOpenOption.CREATE , StandardOpenOption.TRUNCATE_EXISTING );
		
		KVLiteChannel kvChannel = new KVLiteChannel( channel , 128 );
		
		String expected = StringUtils.multiply( "this is a test. " , 100 );
		String expected2 = StringUtils.multiply( "blah" , 100 );
		
		kvChannel.write( Arrays.asList(
				new Record( new Key( "key1".getBytes( ) ) , ChangeType.UPDATE , expected.getBytes( ) ) ,
				new Record( new Key( "key2".getBytes( ) ) , ChangeType.UPDATE , expected2.getBytes( ) )
				) );
		
		expected = StringUtils.multiply( "this is a test 2. " , 100 );
		
		kvChannel.write( Arrays.asList( new Record(
				new Key( "key1".getBytes( ) ) , ChangeType.UPDATE , expected.getBytes( ) ) ) );
		
		kvChannel.write( Arrays.asList( new Record(
				new Key( "key2".getBytes( ) ) , ChangeType.DELETE , new byte[ 0 ] ) ) );
		
		channel.close( );
		
		channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE );
		
		kvChannel = KVLiteChannel.load( channel );
		
		byte[ ] b = kvChannel.read( new Key( "key1".getBytes( ) ) );
		
		String actual = new String( b );
		
		Assert.assertEquals( expected , actual );
		Assert.assertEquals( Collections.singleton( new Key( "key1".getBytes( ) ) ) , kvChannel.keySet( ) );
		
		channel.close( );
	}
	
	@Test
	public void randomTest( ) throws Exception
	{
		List<String> words = CollectionUtils.toArrayList( LineIterable.linesOf( getClass( ).getResource( "words.txt" ) ) );
		
		FileChannel channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE , StandardOpenOption.CREATE , StandardOpenOption.TRUNCATE_EXISTING );
		
		KVLiteChannel kvChannel = new KVLiteChannel( channel , 128 );
		
		Map<String, String> expected = new HashMap<>( );
		Set<String> everKeys = new HashSet<String>( );
		
		long start = System.currentTimeMillis( );
		
		Random rand = new Random( );
		
		int newCount = 100;
		
		while( System.currentTimeMillis( ) - start < 10000 )
		{
			double p;
			
			int recordCount = rand.nextInt( 20 ) + 1;
			
			Record[ ] records = new Record[ recordCount ];
			
			Map<String, String> added = new HashMap<>( );
			Set<String> removed = new HashSet<>( );
			
			for( int k = 0 ; k < recordCount ; k++ )
			{
				p = rand.nextDouble( );
				
				if( newCount > 0 )
				{
					newCount-- ;
					String key = randText( words , rand , 1 , 3 );
					
					if( rand.nextDouble( ) > 0.75 )
					{
						records[ k ] = new Record( new Key( key.getBytes( ) ) ,
								ChangeType.DELETE , new byte[ 0 ] );
						removed.add( key );
						added.remove( key );
					}
					else
					{
						String value = randText( words , rand , 1 , 50 );
						records[ k ] = new Record( new Key( key.getBytes( ) ) ,
								ChangeType.UPDATE , value.getBytes( ) );
						removed.remove( key );
						added.put( key , value );
					}
				}
				else
				{
					String key = randElem( everKeys , rand );
					
					if( rand.nextDouble( ) > 0.75 )
					{
						records[ k ] = new Record( new Key( key.getBytes( ) ) ,
								ChangeType.DELETE , new byte[ 0 ] );
						removed.add( key );
						added.remove( key );
					}
					else
					{
						String value = randText( words , rand , 1 , 50 );
						records[ k ] = new Record( new Key( key.getBytes( ) ) ,
								ChangeType.UPDATE , value.getBytes( ) );
						removed.remove( key );
						added.put( key , value );
					}
				}
			}
			
			everKeys.addAll( added.keySet( ) );
			everKeys.addAll( removed );
			kvChannel.write( Arrays.asList( records ) );
			expected.keySet( ).removeAll( removed );
			expected.putAll( added );
		}
		
		channel.close( );
		
		channel = FileChannel.open( Paths.get( "test.kv" ) , StandardOpenOption.READ , StandardOpenOption.WRITE );
		
		kvChannel = KVLiteChannel.load( channel );
		
		Map<String, String> actual = new HashMap<>( );
		for( Key key : kvChannel.keySet( ) )
		{
			byte[ ] value = kvChannel.read( key );
			actual.put( new String( key.getBytes( ) ) , new String( value ) );
		}
		
		Assert.assertEquals( expected , actual );
	}
	
	private static <K> K randElem( Set<K> keySet , Random rand )
	{
		int i = rand.nextInt( keySet.size( ) );
		for( K key : keySet )
		{
			if( i-- <= 0 )
			{
				return key;
			}
		}
		return null;
	}
	
	private static String randText( List<String> words , Random rand , int minWords , int maxWords )
	{
		int n = rand.nextInt( maxWords - minWords ) + 1 + minWords;
		StringBuilder sb = new StringBuilder( );
		for( int i = 0 ; i < n ; i++ )
		{
			sb.append( words.get( rand.nextInt( words.size( ) ) ) );
		}
		return sb.toString( );
	}
}

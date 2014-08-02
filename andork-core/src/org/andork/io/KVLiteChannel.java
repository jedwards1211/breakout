package org.andork.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.andork.util.ArrayUtils;
import org.omg.CORBA.IntHolder;

public class KVLiteChannel
{
	SeekableByteChannel		channel;
	
	int						blockSize;
	
	int						blockCount;
	PriorityQueue<Integer>	freeBlocks;
	
	int						revision;
	
	Map<Key, RecordInfo>	headRecordInfos;
	Set<Key>				keySet;
	
	ByteBuffer				buffer;
	
	public KVLiteChannel( SeekableByteChannel channel , int blockSize )
	{
		if( blockSize < 16 )
		{
			throw new IllegalArgumentException( "blockSize must be at least 16" );
		}
		
		this.channel = channel;
		this.blockSize = blockSize;
		
		this.blockCount = 0;
		this.freeBlocks = new PriorityQueue<>( );
		this.revision = 0;
		this.headRecordInfos = new HashMap<>( );
		this.keySet = new HashSet<>( );
		this.buffer = ByteBuffer.allocateDirect( blockSize );
		this.buffer.order( ByteOrder.BIG_ENDIAN );
	}
	
	public static KVLiteChannel load( SeekableByteChannel channel ) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect( 8 );
		buffer.order( ByteOrder.BIG_ENDIAN );
		
		channel.position( 0 );
		channel.read( buffer );
		buffer.rewind( );
		
		int revision = buffer.getInt( );
		int blockSize = buffer.getInt( );
		
		KVLiteChannel result = new KVLiteChannel( channel , blockSize );
		result.revision = revision;
		result.blockCount = ( int ) ( channel.size( ) / blockSize );
		
		mainLoop: for( int block = 1 ; block < result.blockCount ; block++ )
		{
			channel.position( block * blockSize );
			result.buffer.rewind( );
			channel.read( result.buffer );
			result.buffer.rewind( );
			
			int nextBlock = result.buffer.getInt( );
			if( ( nextBlock & 0x80000000 ) == 0 )
			{
				continue;
			}
			nextBlock &= 0x7fffffff;
			
			List<byte[ ]> parts = new ArrayList<byte[ ]>( );
			
			int recordRevision = result.buffer.getInt( );
			if( revision > result.revision )
			{
				continue;
			}
			
			byte[ ] firstPart = new byte[ result.buffer.remaining( ) ];
			result.buffer.get( firstPart );
			parts.add( firstPart );
			int dataSize = firstPart.length;
			
			List<Integer> blocks = new ArrayList<Integer>( );
			blocks.add( block );
			
			while( nextBlock != 0 )
			{
				blocks.add( nextBlock );
				result.buffer.rewind( );
				channel.position( nextBlock * blockSize );
				channel.read( result.buffer );
				result.buffer.rewind( );
				
				nextBlock = result.buffer.getInt( );
				if( ( nextBlock & 0x80000000 ) != 0 )
				{
					continue mainLoop;
				}
				nextBlock &= 0x7fffffff;
				
				byte[ ] nextPart = new byte[ result.buffer.remaining( ) ];
				result.buffer.get( nextPart );
				parts.add( nextPart );
				dataSize += nextPart.length;
			}
			
			ByteBuffer recordBuffer = ByteBuffer.allocateDirect( dataSize );
			recordBuffer.order( ByteOrder.BIG_ENDIAN );
			
			for( byte[ ] part : parts )
			{
				recordBuffer.put( part );
			}
			recordBuffer.rewind( );
			
			Record record;
			try
			{
				record = new Record( recordBuffer );
			}
			catch( Exception ex )
			{
				continue;
			}
			RecordInfo recordInfo = new RecordInfo( recordRevision , ArrayUtils.toIntArray( blocks ) ,
					record.changeType( ) == ChangeType.DELETE );
			RecordInfo existingInfo = result.headRecordInfos.get( record.key( ) );
			
			if( existingInfo == null || existingInfo.revision < recordInfo.revision )
			{
				if( existingInfo != null )
				{
					for( int existingBlock : existingInfo.blocks )
					{
						result.freeBlocks.add( existingBlock );
					}
				}
				result.headRecordInfos.put( record.key( ) , recordInfo );
				if( recordInfo.deleted )
				{
					result.keySet.remove( record.key( ) );
				}
				else
				{
					result.keySet.add( record.key( ) );
				}
			}
		}
		
		return result;
	}
	
	public Set<Key> keySet( )
	{
		return Collections.unmodifiableSet( keySet );
	}
	
	public byte[ ] read( Key key ) throws IOException
	{
		RecordInfo info = headRecordInfos.get( key );
		if( info == null || info.deleted )
		{
			return null;
		}
		
		List<byte[ ]> parts = new ArrayList<byte[ ]>( );
		
		int dataSize = 0;
		
		for( int block : info.blocks )
		{
			buffer.rewind( );
			channel.position( block * blockSize );
			channel.read( buffer );
			buffer.rewind( );
			if( block == info.blocks[ 0 ] )
			{
				buffer.getInt( );
			}
			buffer.getInt( );
			byte[ ] part = new byte[ buffer.remaining( ) ];
			buffer.get( part );
			parts.add( part );
			dataSize += part.length;
		}
		
		ByteBuffer recordBuffer = ByteBuffer.allocateDirect( dataSize );
		recordBuffer.order( ByteOrder.BIG_ENDIAN );
		
		for( byte[ ] part : parts )
		{
			recordBuffer.put( part );
		}
		
		return new Record( recordBuffer ).value( );
	}
	
	private int nextFreeBlock( Collection<Integer> unfreedBlocks , IntHolder newBlockCount )
	{
		Integer result = freeBlocks.poll( );
		if( result != null )
		{
			unfreedBlocks.add( result );
		}
		else
		{
			result = newBlockCount.value++ ;
		}
		return result;
	}
	
	public void write( List<Record> newRecords ) throws IOException
	{
		Map<Key, Record> uniqueRecords = new LinkedHashMap<Key, Record>( );
		for( Record record : newRecords )
		{
			uniqueRecords.put( record.key , record );
		}
		
		int newRevision = revision + 1;
		
		List<Integer> unfreedBlocks = new ArrayList<>( );
		List<Integer> newFreeBlocks = new ArrayList<>( );
		Set<Key> addedKeys = new HashSet<>( );
		Set<Key> removedKeys = new HashSet<>( );
		Map<Key, RecordInfo> newHeadRecordInfos = new HashMap<Key, RecordInfo>( );
		IntHolder newBlockCount = new IntHolder( blockCount );
		
		if( newBlockCount.value == 0 )
		{
			newBlockCount.value++ ;
		}
		
		try
		{
			for( Record record : uniqueRecords.values( ) )
			{
				record.buffer.rewind( );
				
				if( record.changeType == ChangeType.DELETE )
				{
					removedKeys.add( record.key );
				}
				else
				{
					addedKeys.add( record.key );
				}
				
				int blockCount = ( record.buffer.capacity( ) + blockSize ) / ( blockSize - 4 );
				int[ ] blocks = new int[ blockCount ];
				
				for( int i = 0 ; i < blockCount ; i++ )
				{
					blocks[ i ] = nextFreeBlock( unfreedBlocks , newBlockCount );
				}
				
				RecordInfo info = new RecordInfo( newRevision , blocks , record.changeType( ) == ChangeType.DELETE );
				newHeadRecordInfos.put( record.key , info );
				
				RecordInfo oldInfo = headRecordInfos.get( record.key );
				if( oldInfo != null )
				{
					for( int block : oldInfo.blocks )
					{
						newFreeBlocks.add( block );
					}
				}
				
				for( int i = 0 ; i < blockCount ; i++ )
				{
					buffer.rewind( );
					
					int nextBlock = i < blockCount - 1 ? blocks[ i + 1 ] : 0;
					
					if( i == 0 )
					{
						buffer.putInt( 0x80000000 | nextBlock );
						buffer.putInt( newRevision );
						record.buffer.limit( Math.min( record.buffer.capacity( ) , blockSize - 8 ) );
						buffer.put( record.buffer );
					}
					else
					{
						buffer.putInt( 0x7fffffff & nextBlock );
						record.buffer.limit( Math.min( record.buffer.capacity( ) , record.buffer.position( ) + blockSize - 4 ) );
						buffer.put( record.buffer );
					}
					
					buffer.rewind( );
					channel.position( blocks[ i ] * blockSize );
					channel.write( buffer );
					
					record.buffer.position( record.buffer.limit( ) );
				}
			}
			
			buffer.rewind( );
			buffer.putInt( 0 , newRevision );
			if( blockCount == 0 )
			{
				buffer.putInt( 4 , blockSize );
				buffer.limit( 8 );
			}
			else
			{
				buffer.limit( 4 );
			}
			channel.position( 0 );
			channel.write( buffer );
			
			buffer.rewind( );
			buffer.limit( buffer.capacity( ) );
			
			freeBlocks.addAll( newFreeBlocks );
			headRecordInfos.putAll( newHeadRecordInfos );
			keySet.addAll( addedKeys );
			keySet.removeAll( removedKeys );
			blockCount = newBlockCount.value;
			revision = newRevision;
		}
		catch( IOException ex )
		{
			freeBlocks.addAll( unfreedBlocks );
			throw ex;
		}
	}
	
	public static enum ChangeType
	{
		UPDATE( 'U' ) , DELETE( 'D' );
		
		private final byte	byteCode;
		
		private ChangeType( char code )
		{
			this.byteCode = ( byte ) ( code & 0xff );
		}
		
		public static ChangeType fromCode( byte code )
		{
			for( ChangeType c : values( ) )
			{
				if( c.byteCode == code )
				{
					return c;
				}
			}
			throw new IllegalArgumentException( "Invalid code: " + ( char ) ( code ) );
		}
	}
	
	public static final class Record
	{
		private Key			key;
		private ChangeType	changeType;
		private byte[ ]		value;
		private ByteBuffer	buffer;
		
		public Record( ByteBuffer buffer )
		{
			this.buffer = buffer;
			buffer.rewind( );
			changeType = ChangeType.fromCode( buffer.get( ) );
			int keyLength = buffer.getInt( );
			if( keyLength > buffer.remaining( ) - 4 )
			{
				throw new IllegalArgumentException( "buffer's keyLength is too large!" );
			}
			byte[ ] keyBytes = new byte[ keyLength ];
			buffer.get( keyBytes );
			key = new Key( keyBytes );
			int valueLength = buffer.getInt( );
			if( valueLength > buffer.remaining( ) )
			{
				throw new IllegalArgumentException( "buffer's valueLength is too large!" );
			}
			value = new byte[ valueLength ];
			buffer.get( value );
			
		}
		
		public Record( Key key , ChangeType changeType , byte[ ] value )
		{
			super( );
			this.key = key;
			this.changeType = changeType;
			this.value = value;
			
			buffer = ByteBuffer.allocateDirect( key.bytes.length + 9 + value.length );
			buffer.order( ByteOrder.BIG_ENDIAN );
			
			buffer.put( changeType.byteCode );
			buffer.putInt( key.bytes.length );
			buffer.put( key.bytes );
			buffer.putInt( value.length );
			buffer.put( value );
			buffer.rewind( );
		}
		
		public ChangeType changeType( )
		{
			return changeType;
		}
		
		public Key key( )
		{
			return key;
		}
		
		public byte[ ] value( )
		{
			return value;
		}
	}
	
	private static final class RecordInfo
	{
		public final int		revision;
		public final int[ ]		blocks;
		public final boolean	deleted;
		
		private RecordInfo( int revision , int[ ] blocks , boolean deleted )
		{
			super( );
			this.revision = revision;
			this.blocks = blocks;
			this.deleted = deleted;
		}
	}
	
	public static final class Key
	{
		byte[ ]	bytes;
		int		hashCode;
		
		public Key( byte[ ] bytes )
		{
			super( );
			this.bytes = bytes;
			this.hashCode = Arrays.hashCode( bytes );
		}
		
		@Override
		public int hashCode( )
		{
			return hashCode;
		}
		
		@Override
		public boolean equals( Object obj )
		{
			if( obj instanceof Key )
			{
				return Arrays.equals( this.bytes , ( ( Key ) obj ).bytes );
			}
			return false;
		}
		
		public String toString( )
		{
			return new String( bytes );
		}
		
		public byte[ ] getBytes( )
		{
			return bytes;
		}
	}
}

package org.andork.collect;

import org.junit.Assert;
import org.junit.Test;

public class WeakValueMapTest
{
	@Test
	public void test001( ) throws InterruptedException
	{
		WeakValueMap<String, Object> map = WeakValueMap.newWeakValueHashMap( );
		
		Object o1 = new Object( );
		
		map.put( "Test" , o1 );
		
		Assert.assertEquals( o1 , map.get( "Test" ) );
		Assert.assertTrue( map.keySet( ).iterator( ).hasNext( ) );
		Assert.assertTrue( map.values( ).iterator( ).hasNext( ) );
		Assert.assertTrue( map.entrySet( ).iterator( ).hasNext( ) );
		Assert.assertFalse( map.isEmpty( ) );
		Assert.assertEquals( 1 , map.size( ) );
		
		o1 = null;
		System.gc( );
		Thread.sleep( 100 );
		System.gc( );
		System.gc( );
		
		Assert.assertFalse( map.keySet( ).iterator( ).hasNext( ) );
		Assert.assertFalse( map.values( ).iterator( ).hasNext( ) );
		Assert.assertFalse( map.entrySet( ).iterator( ).hasNext( ) );
		Assert.assertEquals( null , map.get( "Test" ) );
		Assert.assertTrue( map.isEmpty( ) );
		Assert.assertEquals( 0 , map.size( ) );
	}
}

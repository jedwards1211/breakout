package org.breakout.parse;

import org.junit.Assert;
import org.junit.Test;

public class SegmentTests
{
	@Test
	public void constructionEndLocTests( )
	{
		Segment segment;

		segment = new Segment( "test\r\none" , null , 5 , 3 );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n\r\none" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n \r\none" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n \rone" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n\none" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n\none\n" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 3 , segment.endCol );

		segment = new Segment( "test\r\n\none\r\n" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 4 , segment.endCol );

		segment = new Segment( "test\r\n\none\r\n\n\r" , null , 5 , 3 );
		Assert.assertEquals( 9 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );
	}

	@Test
	public void substringLocTests( )
	{
		Segment segment;

		segment = new Segment( "hello world" , null , 5 , 3 ).substring( 2 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 5 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 10 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 2 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 5 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 2 , 6 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 5 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 8 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 6 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 8 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 7 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 9 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 6 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 9 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 7 , 9 );
		Assert.assertEquals( 6 , segment.startLine );
		Assert.assertEquals( 0 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\n\r\nworld" , null , 5 , 3 ).substring( 5 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\n\r\nworld" , null , 5 , 3 ).substring( 5 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\n\r\nworld" , null , 5 , 3 ).substring( 5 , 10 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );
		

		segment = new Segment( "hello\r\n\n\rworld" , null , 5 , 3 ).substring( 5 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\n\n\rworld" , null , 5 , 3 ).substring( 5 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\n\n\rworld" , null , 5 , 3 ).substring( 5 , 10 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 8 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );
	}
}

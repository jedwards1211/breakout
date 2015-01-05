package org.andork.breakout.table;

import org.andork.q2.QObject;
import org.andork.q2.QSpec;

public class ShotText extends QSpec
{
	public static final Property<Integer>			index			= property( "index" , Integer.class );
	public static final Property<ParsedText>		vector			= property( "vector" , ParsedText.class );
	public static final Property<ParsedText>		dist			= property( "dist" , ParsedText.class );
	public static final Property<ParsedText>		azmFsBs			= property( "azmFsBs" , ParsedText.class );
	public static final Property<ParsedText>		azmFs			= property( "azmFs" , ParsedText.class );
	public static final Property<ParsedText>		azmBs			= property( "azmBs" , ParsedText.class );
	public static final Property<ParsedText>		incFsBs			= property( "incFsBs" , ParsedText.class );
	public static final Property<ParsedText>		incFs			= property( "incFs" , ParsedText.class );
	public static final Property<ParsedText>		incBs			= property( "incBs" , ParsedText.class );
	public static final Property<ParsedText>		offsN			= property( "offsN" , ParsedText.class );
	public static final Property<ParsedText>		offsE			= property( "offsE" , ParsedText.class );
	public static final Property<ParsedText>		offsD			= property( "offsD" , ParsedText.class );
	public static final Property<ParsedText>		fromXsect		= property( "fromXsect" , ParsedText.class );
	public static final Property<ParsedText>		fromL			= property( "fromL" , ParsedText.class );
	public static final Property<ParsedText>		fromR			= property( "fromR" , ParsedText.class );
	public static final Property<ParsedText>		fromU			= property( "fromU" , ParsedText.class );
	public static final Property<ParsedText>		fromD			= property( "fromD" , ParsedText.class );
	public static final Property<ParsedText>		fromLn			= property( "fromLn" , ParsedText.class );
	public static final Property<ParsedText>		fromLe			= property( "fromLe" , ParsedText.class );
	public static final Property<ParsedText>		fromRn			= property( "fromRn" , ParsedText.class );
	public static final Property<ParsedText>		fromRe			= property( "fromRe" , ParsedText.class );
	public static final Property<ParsedText>		fromN			= property( "fromN" , ParsedText.class );
	public static final Property<ParsedText>		fromS			= property( "fromS" , ParsedText.class );
	public static final Property<ParsedText>		fromE			= property( "fromE" , ParsedText.class );
	public static final Property<ParsedText>		fromW			= property( "fromW" , ParsedText.class );
	public static final Property<CrossSectionType>	fromXsectType	= property( "fromXsectType" ,
																		CrossSectionType.class );
	public static final Property<ParsedText>		toXsect			= property( "toXsect" , ParsedText.class );
	public static final Property<ParsedText>		toL				= property( "toL" , ParsedText.class );
	public static final Property<ParsedText>		toR				= property( "toR" , ParsedText.class );
	public static final Property<ParsedText>		toU				= property( "toU" , ParsedText.class );
	public static final Property<ParsedText>		toD				= property( "toD" , ParsedText.class );
	public static final Property<ParsedText>		toLn			= property( "toLn" , ParsedText.class );
	public static final Property<ParsedText>		toLe			= property( "toLe" , ParsedText.class );
	public static final Property<ParsedText>		toRn			= property( "toRn" , ParsedText.class );
	public static final Property<ParsedText>		toRe			= property( "toRe" , ParsedText.class );
	public static final Property<ParsedText>		toN				= property( "toN" , ParsedText.class );
	public static final Property<ParsedText>		toS				= property( "toS" , ParsedText.class );
	public static final Property<ParsedText>		toE				= property( "toE" , ParsedText.class );
	public static final Property<ParsedText>		toW				= property( "toW" , ParsedText.class );
	public static final Property<CrossSectionType>	toXsectType		= property( "toXsectType" ,
																		CrossSectionType.class );

	public static final ShotText					spec			= new ShotText( );

	private ShotText( )
	{
		super( index ,
			vector , dist , azmFsBs , azmFs , azmBs , incFsBs , incFs , incBs , offsN , offsE , offsD ,
			fromXsect , fromL , fromR , fromU , fromD , fromLn , fromLe , fromRn , fromRe , fromN , fromS , fromE ,
			fromW , fromXsectType ,
			toXsect , toL , toR , toU , toD , toLn , toLe , toRn , toRe , toN , toS , toE , toW , toXsectType );
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	public boolean equals( QObject a , Object b )
	{
		return a == b;
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	public int hashCode( QObject o )
	{
		return System.identityHashCode( o );
	}
}

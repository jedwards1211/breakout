package org.andork.breakout.table;


public abstract class ShotVectorText
{
	public static class Joint extends ShotVectorText
	{
		ParsedText	text;
	}

	public static abstract class Dai extends Split
	{
		ParsedText	distText;
	
		public static class PairedAngles extends Dai
		{
			ParsedText	azmFsBsText;
			ParsedText	incFsBsText;
		}
	
		public static class SplitAngles extends Dai
		{
			ParsedText	azmFsText;
			ParsedText	azmBsText;
			ParsedText	incFsText;
			ParsedText	incBsText;
		}
	}

	public static class Nev extends Split
	{
		ParsedText	nText;
		ParsedText	eText;
		ParsedText	vText;
	}

	public static abstract class Split extends ShotVectorText
	{
	}
}

package org.andork.jogl.shadelet;

public class IndexedHighlightShadelet extends Shadelet
{
	public IndexedHighlightShadelet( )
	{
		setProperty( "colorCount" , "2" );
		setProperty( "highlightColors" , "highlightColors" );
		setProperty( "in" , "gl_FragColor" );
		setProperty( "out" , "gl_FragColor" );
		setProperty( "temp" , "indexedHighlight" );
		setProperty( "fragIndex" , "v_highlightIndex" );
		setProperty( "vertIndex" , "a_highlightIndex" );
		
		setProperty( "fragIndexDeclaration" , "varying float $fragIndex;" );
		setProperty( "vertIndexDeclaration" , "attribute float $vertIndex;" );
		setProperty( "highlightColorsDeclaration" , "/* fragment */ uniform vec4 $highlightColors[$colorCount];" );
		setProperty( "tempDeclaration" , "/* fragment */ vec4 $temp;" );
	}
	
	public String vertIndexParam( )
	{
		return replaceProperties( "$vertIndex" );
	}
	
	public IndexedHighlightShadelet colorCount( Object colorCount )
	{
		setProperty( "colorCount" , colorCount );
		return this;
	}
	
	public IndexedHighlightShadelet highlightColors( Object highlightColors )
	{
		setProperty( "highlightColors" , highlightColors );
		return this;
	}
	
	public IndexedHighlightShadelet in( String in )
	{
		setProperty( "in" , in );
		return this;
	}
	
	public IndexedHighlightShadelet out( String out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public IndexedHighlightShadelet fragIndex( String fragIndex )
	{
		setProperty( "fragIndex" , fragIndex );
		return this;
	}
	
	public IndexedHighlightShadelet vertIndex( String vertIndex )
	{
		setProperty( "vertIndex" , vertIndex );
		return this;
	}
	
	public IndexedHighlightShadelet highlightColorsDeclaration( Object highlightColorsDeclaration )
	{
		setProperty( "highlightColorsDeclaration" , highlightColorsDeclaration );
		return this;
	}
	
	public IndexedHighlightShadelet inDeclaration( String inDeclaration )
	{
		setProperty( "inDeclaration" , inDeclaration );
		return this;
	}
	
	public IndexedHighlightShadelet outDeclaration( String outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public IndexedHighlightShadelet fragIndexDeclaration( String fragIndexDeclaration )
	{
		setProperty( "fragIndexDeclaration" , fragIndexDeclaration );
		return this;
	}
	
	public IndexedHighlightShadelet vertIndexDeclaration( String vertIndexDeclaration )
	{
		setProperty( "vertIndexDeclaration" , vertIndexDeclaration );
		return this;
	}
	
	public String getVertexShaderMainCode( )
	{
		return "$fragIndex = $vertIndex;";
	}
	
	public String getFragmentShaderMainCode( )
	{
		
		return "  $temp = $highlightColors[int(floor($fragIndex + 0.5))];" +
				// "  $out = mix($in, vec4($temp.xyz, 1.0), $temp.w);";
				"  $out = clamp($in + vec4($temp.xyz * $temp.w, 0.0), 0.0, 1.0);";
	}
}

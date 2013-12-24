package org.andork.frf.model;

import org.andork.jogl.shadelet.AxisParamShadelet;
import org.andork.jogl.shadelet.CombinedShadelet;
import org.andork.jogl.shadelet.DistParamShadelet;
import org.andork.jogl.shadelet.GradientShadelet;
import org.andork.jogl.shadelet.NormalVertexShadelet;
import org.andork.jogl.shadelet.PositionVertexShadelet;
import org.andork.jogl.shadelet.Shadelet;
import org.andork.jogl.shadelet.SimpleLightingShadelet;

public class ShadeletTest
{
	public static void main( String[ ] args )
	{
		PositionVertexShadelet posShadelet = new PositionVertexShadelet( );
		NormalVertexShadelet normShadelet = new NormalVertexShadelet( );
		AxisParamShadelet axisShadelet = new AxisParamShadelet( );
		GradientShadelet axisGradShadelet = new GradientShadelet( );
		DistParamShadelet distShadelet = new DistParamShadelet( );
		GradientShadelet distGradShadelet = new GradientShadelet( );
		HighlightShadelet highlightShadelet = new HighlightShadelet( );
		SimpleLightingShadelet lightShadelet = new SimpleLightingShadelet( );
		
		axisGradShadelet.param( axisShadelet.out( ) );
		
		distGradShadelet.loColor( "gl_FragColor" ).hiColor( "vec4(0.0, 0.0, 0.0, 1.0)" )
				.loValue( "nearDist" ).hiValue( "farDist" )
				.param( distShadelet.out( ) ).loColorDeclaration( null );
		
		highlightShadelet.color( "vec4(1.0, 1.0, 0.0, 1.0)" );
		highlightShadelet.colorDeclaration( null );
		
		lightShadelet.color( "gl_FragColor" ).colorDeclaration( null ).ambientAmt( "0.3" );
		
		CombinedShadelet combShadelet = new CombinedShadelet( posShadelet , normShadelet ,
				axisShadelet , axisGradShadelet , distShadelet , distGradShadelet , highlightShadelet , lightShadelet );
		
		System.out.println( Shadelet.prettyPrint( combShadelet.createVertexShaderCode( ) ) );
		System.out.println( Shadelet.prettyPrint( combShadelet.createFragmentShaderCode( ) ) );
	}
}

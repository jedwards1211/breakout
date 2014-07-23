/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl.shadelet;

public class SimpleLightingShadelet extends Shadelet
{
	public SimpleLightingShadelet()
	{
		setProperty("norm", "v_norm");
		setProperty("color", "color");
		setProperty("temp", "temp");
		setProperty("ambientAmt", "ambientAmt");
		setProperty("out", "gl_FragColor");

		setProperty("normDeclaration", "varying vec3 $norm;");
		setProperty("colorDeclaration", "/* fragment */ uniform vec4 $color;");
		setProperty("tempDeclaration", "/* fragment */ float $temp;");
		setProperty("ambientAmtDeclaration", "/* fragment */ uniform float $ambientAmt;");
	}

	public SimpleLightingShadelet norm(Object norm)
	{
		setProperty("norm", norm);
		return this;
	}

	public SimpleLightingShadelet ambientAmt(Object ambientAmt)
	{
		setProperty("ambientAmt", ambientAmt);
		return this;
	}

	public SimpleLightingShadelet temp(String temp)
	{
		setProperty("temp", temp);
		return this;
	}

	public SimpleLightingShadelet color(Object color)
	{
		setProperty("color", color);
		return this;
	}

	public SimpleLightingShadelet out(String out)
	{
		setProperty("out", out);
		return this;
	}

	public SimpleLightingShadelet normDeclaration(Object normDeclaration)
	{
		setProperty("normDeclaration", normDeclaration);
		return this;
	}

	public SimpleLightingShadelet ambientAmtDeclaration(Object ambientAmtDeclaration)
	{
		setProperty("ambientAmtDeclaration", ambientAmtDeclaration);
		return this;
	}

	public SimpleLightingShadelet tempDeclaration(String tempDeclaration)
	{
		setProperty("tempDeclaration", tempDeclaration);
		return this;
	}

	public SimpleLightingShadelet colorDeclaration(Object colorDeclaration)
	{
		setProperty("colorDeclaration", colorDeclaration);
		return this;
	}

	public SimpleLightingShadelet outDeclaration(String outDeclaration)
	{
		setProperty("outDeclaration", outDeclaration);
		return this;
	}

	public String getFragmentShaderMainCode()
	{
		return "  $temp = dot($norm, vec3(0.0, 0.0, 1.0));" +
				"  $temp = $ambientAmt + $temp * (1.0 - $ambientAmt);" +
				"  $out = $temp * $color;";
	}

	public static void main(String[] args) {
		PositionVertexShadelet posShader = new PositionVertexShadelet();
		NormalVertexShadelet normShader = new NormalVertexShadelet();
		GradientShadelet gradShader = new GradientShadelet();
		SimpleLightingShadelet lightShader = new SimpleLightingShadelet();

		gradShader.loValue("1.0");
		gradShader.hiValue("5.0");
		gradShader.loColor("vec4(1.0, 0.0, 0.0, 1.0)");
		lightShader.color("gl_FragColor");
		lightShader.colorDeclaration(null);
		lightShader.ambientAmt("0.2");

		CombinedShadelet combShader = new CombinedShadelet(posShader, normShader, gradShader, lightShader);

		System.out.println(prettyPrint(combShader.createVertexShaderCode()));
		System.out.println(prettyPrint(combShader.createFragmentShaderCode()));
	}
}

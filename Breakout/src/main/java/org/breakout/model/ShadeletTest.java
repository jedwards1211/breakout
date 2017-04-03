/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.breakout.model;

import org.andork.jogl.shadelet.AxisParamShadelet;
import org.andork.jogl.shadelet.CombinedShadelet;
import org.andork.jogl.shadelet.DistParamShadelet;
import org.andork.jogl.shadelet.GradientShadelet;
import org.andork.jogl.shadelet.IndexedHighlightShadelet;
import org.andork.jogl.shadelet.NormalVertexShadelet;
import org.andork.jogl.shadelet.PositionVertexShadelet;
import org.andork.jogl.shadelet.Shadelet;
import org.andork.jogl.shadelet.SimpleLightingShadelet;

public class ShadeletTest {
	public static void main(String[] args) {
		PositionVertexShadelet posShadelet = new PositionVertexShadelet();
		NormalVertexShadelet normShadelet = new NormalVertexShadelet();
		AxisParamShadelet axisShadelet = new AxisParamShadelet();
		GradientShadelet axisGradShadelet = new GradientShadelet();
		DistParamShadelet distShadelet = new DistParamShadelet();
		GradientShadelet distGradShadelet = new GradientShadelet();
		SimpleLightingShadelet lightShadelet = new SimpleLightingShadelet();
		IndexedHighlightShadelet highlightShadelet = new IndexedHighlightShadelet();

		axisGradShadelet.param(axisShadelet.out());

		distGradShadelet.loColor("gl_FragColor").hiColor("vec4(0.0, 0.0, 0.0, 1.0)")
				.loValue("nearDist").hiValue("farDist")
				.param(distShadelet.out()).loColorDeclaration(null);

		lightShadelet.color("gl_FragColor").colorDeclaration(null).ambientAmt("0.3");
		highlightShadelet.colorCount(10);

		CombinedShadelet combShadelet = new CombinedShadelet(posShadelet, normShadelet,
				axisShadelet, axisGradShadelet, distShadelet, distGradShadelet, lightShadelet,
				highlightShadelet);

		System.out.println(Shadelet.prettyPrint(combShadelet.createVertexShaderCode()));
		System.out.println(Shadelet.prettyPrint(combShadelet.createFragmentShaderCode()));
	}
}

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
package org.andork.jogl.shadelet;

public class NormalVertexShadelet extends Shadelet {
	public NormalVertexShadelet() {
		setProperty("v", "v");
		setProperty("n", "n");
		setProperty("norm", "a_norm");
		setProperty("out", "v_norm");
		setProperty("nDeclaration", "/* vertex */ uniform mat3 $n;");
		setProperty("vDeclaration", "/* vertex */ uniform mat4 $v;");
		setProperty("normDeclaration", "attribute vec3 $norm;");
		setProperty("outDeclaration", "varying vec3 $out;");
	}

	@Override
	public String getVertexShaderMainCode() {
		return "  $out = ($v * vec4(normalize($n * $norm), 0.0)).xyz;";
	}

	public NormalVertexShadelet n(Object n) {
		setProperty("n", n);
		return this;
	}

	public NormalVertexShadelet nDeclaration(String nDeclaration) {
		setProperty("nDeclaration", nDeclaration);
		return this;
	}

	public String norm() {
		return replaceProperties("$norm");
	}

	public NormalVertexShadelet norm(String norm) {
		setProperty("norm", norm);
		return this;
	}

	public NormalVertexShadelet normDeclaration(String normDeclaration) {
		setProperty("normDeclaration", normDeclaration);
		return this;
	}

	public NormalVertexShadelet out(Object out) {
		setProperty("out", out);
		return this;
	}

	public NormalVertexShadelet outDeclaration(String outDeclaration) {
		setProperty("outDeclaration", outDeclaration);
		return this;
	}

	public NormalVertexShadelet v(Object v) {
		setProperty("v", v);
		return this;
	}

	public NormalVertexShadelet vDeclaration(String vDeclaration) {
		setProperty("vDeclaration", vDeclaration);
		return this;
	}
}

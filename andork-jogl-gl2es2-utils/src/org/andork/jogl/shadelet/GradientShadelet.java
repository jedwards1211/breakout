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

public class GradientShadelet extends Shadelet {
	public GradientShadelet() {
		setProperty("loValue", "loValue");
		setProperty("hiValue", "hiValue");
		setProperty("loColor", "loColor");
		setProperty("hiColor", "hiColor");
		setProperty("param", "param");
		setProperty("out", "gl_FragColor");
		setProperty("loValueDeclaration", "/* fragment */ uniform float $loValue;");
		setProperty("hiValueDeclaration", "/* fragment */ uniform float $hiValue;");
		setProperty("loColorDeclaration", "/* fragment */ uniform vec4 $loColor;");
		setProperty("hiColorDeclaration", "/* fragment */ uniform vec4 $hiColor;");
		setProperty("paramDeclaration", "varying float $param;");
	}

	@Override
	public String getFragmentShaderMainCode() {
		return "  $out = mix($loColor, $hiColor, clamp(($param - $loValue) / ($hiValue - $loValue), 0.0, 1.0));";
	}

	public String hiColor() {
		return replaceProperties("$hiColor");
	}

	public GradientShadelet hiColor(Object hiColor) {
		setProperty("hiColor", hiColor);
		return this;
	}

	public GradientShadelet hiColorDeclaration(Object hiColorDeclaration) {
		setProperty("hiColorDeclaration", hiColorDeclaration);
		return this;
	}

	public String hiValue() {
		return replaceProperties("$hiValue");
	}

	public GradientShadelet hiValue(String hiValue) {
		setProperty("hiValue", hiValue);
		return this;
	}

	public GradientShadelet hiValueDeclaration(String hiValueDeclaration) {
		setProperty("hiValueDeclaration", hiValueDeclaration);
		return this;
	}

	public String loColor() {
		return replaceProperties("$loColor");
	}

	public GradientShadelet loColor(Object loColor) {
		setProperty("loColor", loColor);
		return this;
	}

	public GradientShadelet loColorDeclaration(Object loColorDeclaration) {
		setProperty("loColorDeclaration", loColorDeclaration);
		return this;
	}

	public String loValue() {
		return replaceProperties("$loValue");
	}

	public GradientShadelet loValue(String loValue) {
		setProperty("loValue", loValue);
		return this;
	}

	public GradientShadelet loValueDeclaration(String loValueDeclaration) {
		setProperty("loValueDeclaration", loValueDeclaration);
		return this;
	}

	public GradientShadelet out(String out) {
		setProperty("out", out);
		return this;
	}

	public GradientShadelet outDeclaration(String outDeclaration) {
		setProperty("outDeclaration", outDeclaration);
		return this;
	}

	public GradientShadelet param(Object param) {
		setProperty("param", param);
		return this;
	}

	public GradientShadelet paramDeclaration(Object paramDeclaration) {
		setProperty("paramDeclaration", paramDeclaration);
		return this;
	}
}

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

public class IndexedHighlightShadelet extends Shadelet {
	public IndexedHighlightShadelet() {
		setProperty("colorCount", "2");
		setProperty("highlightColors", "highlightColors");
		setProperty("in", "gl_FragColor");
		setProperty("out", "gl_FragColor");
		setProperty("temp", "indexedHighlight");
		setProperty("fragIndex", "v_highlightIndex");
		setProperty("vertIndex", "a_highlightIndex");

		setProperty("fragIndexDeclaration", "varying float $fragIndex;");
		setProperty("vertIndexDeclaration", "attribute float $vertIndex;");
		setProperty("highlightColorsDeclaration", "/* fragment */ uniform vec4 $highlightColors[$colorCount];");
		setProperty("tempDeclaration", "/* fragment */ vec4 $temp;");
	}

	public IndexedHighlightShadelet colorCount(Object colorCount) {
		setProperty("colorCount", colorCount);
		return this;
	}

	public IndexedHighlightShadelet fragIndex(String fragIndex) {
		setProperty("fragIndex", fragIndex);
		return this;
	}

	public IndexedHighlightShadelet fragIndexDeclaration(String fragIndexDeclaration) {
		setProperty("fragIndexDeclaration", fragIndexDeclaration);
		return this;
	}

	@Override
	public String getFragmentShaderMainCode() {

		return "  $temp = $highlightColors[int(floor($fragIndex + 0.5))];" +
				// " $out = mix($in, vec4($temp.xyz, 1.0), $temp.w);";
				"  $out = clamp($in + vec4($temp.xyz * $temp.w, 0.0), 0.0, 1.0);";
	}

	@Override
	public String getVertexShaderMainCode() {
		return "$fragIndex = $vertIndex;";
	}

	public IndexedHighlightShadelet highlightColors(Object highlightColors) {
		setProperty("highlightColors", highlightColors);
		return this;
	}

	public IndexedHighlightShadelet highlightColorsDeclaration(Object highlightColorsDeclaration) {
		setProperty("highlightColorsDeclaration", highlightColorsDeclaration);
		return this;
	}

	public IndexedHighlightShadelet in(String in) {
		setProperty("in", in);
		return this;
	}

	public IndexedHighlightShadelet inDeclaration(String inDeclaration) {
		setProperty("inDeclaration", inDeclaration);
		return this;
	}

	public IndexedHighlightShadelet out(String out) {
		setProperty("out", out);
		return this;
	}

	public IndexedHighlightShadelet outDeclaration(String outDeclaration) {
		setProperty("outDeclaration", outDeclaration);
		return this;
	}

	public IndexedHighlightShadelet vertIndex(String vertIndex) {
		setProperty("vertIndex", vertIndex);
		return this;
	}

	public IndexedHighlightShadelet vertIndexDeclaration(String vertIndexDeclaration) {
		setProperty("vertIndexDeclaration", vertIndexDeclaration);
		return this;
	}

	public String vertIndexParam() {
		return replaceProperties("$vertIndex");
	}
}

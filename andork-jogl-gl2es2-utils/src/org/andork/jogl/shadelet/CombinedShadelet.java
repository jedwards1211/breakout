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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CombinedShadelet extends Shadelet {
	final List<Shadelet>	segments	= new ArrayList<Shadelet>();

	public CombinedShadelet(Shadelet... segments) {
		this(Arrays.asList(segments));
	}

	public CombinedShadelet(Collection<Shadelet> segments) {
		this.segments.addAll(segments);
	}

	public void addSegment(Shadelet segment) {
		segments.add(segment);
	}

	@Override
	public Collection<String> getVertexShaderVariableDecls() {
		Map<String, String> vars = new LinkedHashMap<String, String>();

		for (Shadelet segment : segments) {
			for (String variableDecl : segment.getVertexShaderVariableDecls()) {
				vars.put(variableKey(variableDecl), variableDecl);
			}
		}

		return vars.values();
	}

	@Override
	public Collection<String> getFragmentShaderVariableDecls() {
		Map<String, String> vars = new LinkedHashMap<String, String>();

		for (Shadelet segment : segments) {
			for (String variableDecl : segment.getFragmentShaderVariableDecls()) {
				vars.put(variableKey(variableDecl), variableDecl);
			}
		}

		return vars.values();
	}

	@Override
	public Collection<String> getVertexShaderLocalDecls() {
		Map<String, String> vars = new LinkedHashMap<String, String>();

		for (Shadelet segment : segments) {
			for (String LocalDecl : segment.getVertexShaderLocalDecls()) {
				vars.put(variableKey(LocalDecl), LocalDecl);
			}
		}

		return vars.values();
	}

	@Override
	public Collection<String> getFragmentShaderLocalDecls() {
		Map<String, String> vars = new LinkedHashMap<String, String>();

		for (Shadelet segment : segments) {
			for (String LocalDecl : segment.getFragmentShaderLocalDecls()) {
				vars.put(variableKey(LocalDecl), LocalDecl);
			}
		}

		return vars.values();
	}

	@Override
	public String getVertexShaderMainCode() {
		StringBuffer sb = new StringBuffer();
		for (Shadelet segment : segments) {
			sb.append(segment.replaceProperties(segment.getVertexShaderMainCode()));
		}
		return sb.toString();
	}

	@Override
	public String getFragmentShaderMainCode() {
		StringBuffer sb = new StringBuffer();
		for (Shadelet segment : segments) {
			sb.append(segment.replaceProperties(segment.getFragmentShaderMainCode()));
		}
		return sb.toString();
	}

	@Override
	public Collection<String> getVertexShaderFunctionDecls() {
		List<String> result = new ArrayList<String>();
		for (Shadelet segment : segments) {
			result.addAll(segment.replaceProperties(segment.getVertexShaderFunctionDecls()));
		}
		return result;
	}

	@Override
	public Collection<String> getFragmentShaderFunctionDecls() {
		List<String> result = new ArrayList<String>();
		for (Shadelet segment : segments) {
			result.addAll(segment.replaceProperties(segment.getFragmentShaderFunctionDecls()));
		}
		return result;
	}
}

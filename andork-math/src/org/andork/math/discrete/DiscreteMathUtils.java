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
package org.andork.math.discrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class DiscreteMathUtils {
	public static int[][] generateMonomials(int powerSum, int variables) {
		List<int[]> result = new ArrayList<int[]>();
		Stack<Integer> stack = new Stack<Integer>();
		generateMonomials(result, stack, powerSum - 1, variables);
		return result.toArray(new int[result.size()][]);
	}

	private static void generateMonomials(List<int[]> result, Stack<Integer> stack, int remainingPowerSum,
			int variables) {
		if (stack.size() + 1 == variables) {
			int[] monomial = new int[variables];
			for (int i = 0; i < variables - 1; i++) {
				monomial[i] = stack.get(i);
			}
			monomial[variables - 1] = remainingPowerSum;
			result.add(monomial);
		} else {
			for (int i = remainingPowerSum; i >= 0; i--) {
				stack.push(i);
				generateMonomials(result, stack, remainingPowerSum - i, variables);
				stack.pop();
			}
		}
	}

	public static void main(String[] args) {
		for (int[] m : generateMonomials(3, 3)) {
			System.out.println(Arrays.toString(m));
		}
	}
}

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
package org.andork.awt.anim;

public abstract interface Animation {
	public default Animation also(Animation simultaneous) {
		return animTime -> {
			long myResult = animate(animTime);
			long simultaneousResult = simultaneous.animate(animTime);

			if (myResult > 0) {
				if (simultaneousResult > 0) {
					return Math.min(myResult, simultaneousResult);
				}
				return myResult;
			}
			return simultaneousResult;
		};
	}

	/**
	 * Advances the animation one frame.
	 *
	 * @return if a value less than zero is returned, {@link AnimationQueue}
	 *         will terminate this animation. Otherwise, it will attempt to call
	 *         {@link #animate()} again in that many milliseconds.
	 */
	public abstract long animate(long animTime);
}

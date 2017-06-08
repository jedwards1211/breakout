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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.function.Predicate;

import javax.swing.Timer;

import org.andork.awt.CheckEDT;
import org.andork.collect.Iterables;

public class AnimationQueue {
	private class TimerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			long currentTime = System.currentTimeMillis();
			long animTime = lastAnimTime == 0 ? 0 : currentTime - lastAnimTime;
			lastAnimTime = currentTime;
			while (!queue.isEmpty()) {
				Animation current = queue.getFirst();

				animating = true;
				try {
					int result = (int) Math.min(current.animate(animTime), Integer.MAX_VALUE);

					if (result <= 0) {
						queue.poll();
						lastAnimTime = 0;
					} else {
						timer.setInitialDelay(result);
						timer.start();
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					queue.poll();
					lastAnimTime = 0;
				} finally {
					animating = false;
					queue.addAll(pendingAdd);
					pendingAdd.clear();
				}
			}
		}
	}

	boolean animating;
	final LinkedList<Animation> pendingAdd = new LinkedList<Animation>();
	final LinkedList<Animation> queue = new LinkedList<Animation>();
	final TimerHandler timerHandler = new TimerHandler();
	final Timer timer;

	long lastAnimTime = 0;

	public AnimationQueue() {
		timer = new Timer(0, timerHandler);
		timer.setRepeats(false);
	}

	public void add(Animation animation) {
		CheckEDT.checkEDT();
		if (animating) {
			pendingAdd.add(animation);
		} else {
			if (queue.isEmpty()) {
				timer.setInitialDelay(0);
				timer.start();
			}
			queue.add(animation);
		}
	}

	public void clear() {
		CheckEDT.checkEDT();
		timer.stop();
		pendingAdd.clear();
		queue.clear();
		lastAnimTime = 0;
	}

	public void removeAll(Predicate<Animation> p) {
		CheckEDT.checkEDT();
		Iterables.removeAll(queue, p);
		Iterables.removeAll(pendingAdd, p);
		if (queue.isEmpty() && pendingAdd.isEmpty()) {
			timer.stop();
			lastAnimTime = 0;
		}
	}
}

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
package org.breakout;

import static org.andork.spatial.Rectmath.ppunion;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.andork.func.StreamUtils;
import org.andork.jogl.DevicePixelRatio;
import org.andork.jogl.JoglScene;
import org.andork.jogl.JoglScreenPolygon;
import org.andork.jogl.JoglViewState;
import org.andork.math3d.PickXform;
import org.andork.math3d.PlanarHull3f;
import org.andork.math3d.Vecmath;
import org.andork.spatial.EdgeTrees;
import org.andork.spatial.RBranch;
import org.andork.spatial.RTraversal;
import org.andork.spatial.RfStarTree;
import org.andork.task.TaskService;
import org.andork.util.Reparam;
import org.breakout.model.Survey3dModel;
import org.breakout.model.Survey3dModel.Shot3d;

import com.jogamp.opengl.GLAutoDrawable;

public class WindowSelectionMouseHandler extends MouseAdapter {
	public static interface Context {
		public void endSelection();

		public GLAutoDrawable getDrawable();

		public TaskService getRebuildTaskService();

		public JoglScene getScene();

		public Survey3dModel getSurvey3dModel();

		public JoglViewState getViewState();

		public void selectShots(Set<Shot3d> newSelected, boolean add, boolean toggle);
	}

	private final Context context;

	private final List<float[]> points = new ArrayList<float[]>();
	JoglScreenPolygon selectionPolygon = new JoglScreenPolygon();
	private PlanarHull3f hull = new PlanarHull3f();

	final float[] pointOnScreen = new float[3];

	public WindowSelectionMouseHandler(Context context) {
		this.context = context;
	}

	public void end() {
		points.clear();
		context.getDrawable().invoke(true, drawable -> {
			context.getScene().remove(selectionPolygon);
			return false;
		});
		context.endSelection();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!points.isEmpty()) {
			float dpr = DevicePixelRatio.getDevicePixelRatio(context.getDrawable());
			float[] last = points.get(points.size() - 1);
			last[0] = e.getX() * dpr;
			last[1] = context.getDrawable().getSurfaceHeight() - e.getY() * dpr;
			context.getDrawable().invoke(true, drawable -> {
				selectionPolygon.setPoints(points);
				return false;
			});
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			selectLoopedShots(
				(e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0,
				(e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0);
			end();
			return;
		}
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		float dpr = DevicePixelRatio.getDevicePixelRatio(context.getDrawable());
		points.add(new float[] { e.getX() * dpr, context.getDrawable().getSurfaceHeight() - e.getY() * dpr });
		context.getDrawable().invoke(false, drawable -> {
			selectionPolygon.setPoints(points);
			return false;
		});
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	protected void selectLoopedShots(boolean add, boolean toggle) {
		final Survey3dModel model3d = context.getSurvey3dModel();

		final List<float[]> points = new ArrayList<>(this.points);

		if (model3d == null) {
			return;
		}

		context.getRebuildTaskService().submit(task -> {
			task.setStatus("Finding lassoed shots...");
			task.setIndeterminate(true);
			RBranch<float[], Shot3d> root = model3d.getTree().getRoot();
			RfStarTree<float[]> edgeTree = new RfStarTree<>(2, 4, 1, 2);

			StreamUtils.forEachPairLooped(points.stream(), (p1, p2) -> {
				edgeTree.insert(edgeTree.createLeaf(ppunion(p1, p2), new float[] { p1[0], p1[1], p2[0], p2[1] }));
			});

			float[] mbr = edgeTree.getRoot().mbr();

			PickXform pickXform = context.getViewState().pickXform();

			GLAutoDrawable canvas = context.getDrawable();
			int cw = canvas.getSurfaceWidth();
			int ch = canvas.getSurfaceHeight();

			pickXform.exportViewVolume(hull, mbr, cw, ch);

			JoglViewState viewState = context.getViewState();

			float[] pv = Vecmath.newMat4f();
			Vecmath.mmul(viewState.projectionMatrix(), viewState.viewMatrix(), pv);

			Set<Shot3d> newSelected = new HashSet<>();

			float[] temp = new float[3];

			RTraversal.traverse(root, node -> hull.intersectsBox(node.mbr()), leaf -> {
				for (float[] point : leaf.object().vertices(temp)) {
					Vecmath.mpmul(pv, point, pointOnScreen);
					pointOnScreen[0] = Reparam.linear(pointOnScreen[0], -1, 1, 0, cw);
					pointOnScreen[1] = Reparam.linear(pointOnScreen[1], -1, 1, 0, ch);
					if (!EdgeTrees.isInPolygon(pointOnScreen, edgeTree.getRoot())) {
						return true;
					}
				}
				newSelected.add(leaf.object());
				return true;
			});

			context.selectShots(newSelected, add, toggle);

			context.getDrawable().display();
		});
	}

	public void start(MouseEvent e) {
		float dpr = DevicePixelRatio.getDevicePixelRatio(context.getDrawable());
		points.add(new float[] { e.getX() * dpr, context.getDrawable().getSurfaceHeight() - e.getY() * dpr });
		points.add(new float[] { e.getX() * dpr, context.getDrawable().getSurfaceHeight() - e.getY() * dpr });
		context.getDrawable().invoke(false, drawable -> {
			selectionPolygon.setPoints(points);
			context.getScene().add(selectionPolygon);
			return false;
		});
	}
}

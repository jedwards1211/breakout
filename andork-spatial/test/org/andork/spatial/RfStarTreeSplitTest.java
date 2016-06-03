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
package org.andork.spatial;

import static org.andork.spatial.Rectmath.voidRectf;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.andork.spatial.RfStarTree.Branch;
import org.andork.spatial.RfStarTree.Leaf;
import org.andork.spatial.RfStarTree.Node;

public class RfStarTreeSplitTest {
	public static void main(String[] args) {
		final int dimension = 2;
		final int M = 4;
		final RfStarTree<String> tree = new RfStarTree<String>(dimension, M, 2, 1);

		final List<Node<String>> nodes = new ArrayList<Node<String>>();

		final List<JLabel> labels = new ArrayList<JLabel>();

		final JPanel drawPanel = new JPanel();
		drawPanel.setLayout(null);

		MouseAdapter adapter = new MouseAdapter() {
			MouseEvent pressEvent = null;

			@Override
			public void mouseDragged(MouseEvent e) {
				Node<String> node = nodes.get(nodes.size() - 1);
				node.mbr[0] = Math.min(e.getX(), pressEvent.getX());
				node.mbr[1] = Math.min(e.getY(), pressEvent.getY());
				node.mbr[dimension] = Math.max(e.getX(), pressEvent.getX());
				node.mbr[dimension + 1] = Math.max(e.getY(), pressEvent.getY());

				updateLabels();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (pressEvent != null || e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				if (nodes.size() > M) {
					nodes.clear();
				}

				pressEvent = e;
				float[] mbr = voidRectf(dimension);
				mbr[0] = mbr[dimension] = e.getX();
				mbr[1] = mbr[dimension + 1] = e.getY();
				Node<String> node = new Leaf<String>(mbr, Integer.toString(nodes.size()));
				nodes.add(node);

				updateLabels();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (pressEvent == null || e.getButton() != pressEvent.getButton()) {
					return;
				}

				if (nodes.size() == M + 1) {
					Branch<String> branch = new Branch<String>(dimension, 0, M + 1);
					int k = 0;
					for (Node<String> node : nodes) {
						branch.children[k++] = node;
					}

					Branch<String>[] split = tree.split(branch);
					for (Branch<String> piece : split) {
						piece.recalcMbr();
						nodes.add(piece);
					}

					updateLabels();
				}

				pressEvent = null;
			}

			private void updateLabels() {
				while (labels.size() > nodes.size()) {
					drawPanel.remove(labels.remove(nodes.size()));
				}

				while (labels.size() < nodes.size()) {
					JLabel label = new JLabel();
					label.setBorder(new LineBorder(Color.BLACK, 1));
					label.setOpaque(false);
					labels.add(label);
					drawPanel.add(label);
				}

				for (int i = 0; i < labels.size(); i++) {
					JLabel label = labels.get(i);
					Node<String> node = nodes.get(i);

					if (node instanceof Leaf) {
						label.setText(((Leaf<String>) node).object());
					}

					float[] mbr = node.mbr;
					label.setBounds((int) mbr[0], (int) mbr[1], (int) (mbr[dimension] - mbr[0]),
							(int) (mbr[dimension + 1] - mbr[1]));
				}

				drawPanel.repaint();
			}
		};

		drawPanel.addMouseListener(adapter);
		drawPanel.addMouseMotionListener(adapter);

		JFrame frame = new JFrame();
		frame.getContentPane().add(drawPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

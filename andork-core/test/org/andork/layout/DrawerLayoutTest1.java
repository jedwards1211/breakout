package org.andork.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DrawerLayoutTest1 {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		content.setPreferredSize(new Dimension(640, 480));
		content.setLayout(new DelegatingLayoutManager());

		JPanel drawer = new JPanel();
		drawer.setBackground(Color.BLUE);
		drawer.setPreferredSize(new Dimension(200, 100));

		final DrawerLayoutDelegate delegate = new DrawerLayoutDelegate();
		// delegate.dockingCorner = Corner.TOP_RIGHT;
		delegate.dockingSide = Side.RIGHT;
		delegate.parent = content;

		JButton toggleButton = new JButton("Toggle");

		toggleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delegate.toggle();
			}
		});

		final DrawerLayoutDelegate buttonDelegate = new DrawerLayoutDelegate();
		buttonDelegate.dockingSide = Side.LEFT;
		buttonDelegate.dockingCorner = Corner.TOP_LEFT;

		content.add(drawer, delegate);
		content.add(toggleButton, buttonDelegate);

		frame.getContentPane().add(content, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

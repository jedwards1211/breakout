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
package org.andork.ui.debug;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.collect.ArrayLists;
import org.andork.collect.StringTrieMap;
import org.andork.collect.Visitor;
import org.andork.util.ClassFinder;
import org.andork.util.Java7;

@SuppressWarnings("serial")
public class ClassChooserPane extends JPanel {
	private static class ClassInfo {
		public String fqName;
		public String humanFqName;
		public String className;
		public String location;

		public ClassInfo(String fqName) {
			this.fqName = fqName;

			humanFqName = fqName.replace('$', '.');

			int splitPoint = humanFqName.lastIndexOf('.');
			if (splitPoint == 0) {
				className = humanFqName;
				location = "";
			} else {
				className = humanFqName.substring(splitPoint + 1);
				location = humanFqName.substring(0, splitPoint);
			}
		}
	}

	private class ClassNameSorter implements Comparator<ClassInfo> {
		@Override
		public int compare(ClassInfo o1, ClassInfo o2) {
			int result = o1.className.compareTo(o2.className);
			return result == 0 ? o1.fqName.compareTo(o2.fqName) : result;
		}
	}

	private class Loader implements Runnable {
		@Override
		public void run() {
			ClassFinder.findClasses(new Visitor<String>() {
				@Override
				public boolean visit(String className) {
					if (isAnonymous(className)) {
						return true;
					}
					ClassInfo info = new ClassInfo(className);
					if (info.location.length() > 0) {
						mapByPackageName.put(info.humanFqName.toLowerCase(), info);
					}
					mapByClassName.put(info.className.toLowerCase(), info);
					return true;
				}
			});

			doSwing(new Runnable() {
				@Override
				public void run() {
					loadingLabel.setVisible(false);
				}
			});
		}
	}

	@SuppressWarnings("serial")
	private class MatchListCellRenderer extends DefaultListCellRenderer {
		/**
		 *
		 */
		private static final long serialVersionUID = 3028841806077779491L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			ClassInfo info = (ClassInfo) value;
			value = "<html>" + info.className + " - <font color=\"gray\">" + info.location + "</font></html>";
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}

	private class Updater implements Callable<Object>, Visitor<ClassInfo> {
		private class AbortChecker implements Runnable {
			boolean abort;

			@Override
			public void run() {
				abort = Java7.Objects.equals(searchString, searchStringForDisplayedMatches);
			}
		}

		final String searchString;

		final Set<ClassInfo> matches = new HashSet<ClassInfo>();

		protected Updater(String searchString) {
			this.searchString = searchString;
		}

		@Override
		public Object call() throws Exception {
			AbortChecker checker = new AbortChecker();
			doSwing(checker);

			if (checker.abort) {
				return null;
			}

			if (!searchString.isEmpty() && !"*".equals(searchString) && !"?".equals(searchString)) {
				if (searchString.indexOf('.') >= 0) {
					mapByPackageName.get(searchString, this);
				} else {
					mapByClassName.get(searchString, this);
				}
			}

			final DefaultListModel model = new DefaultListModel();
			for (ClassInfo match : ArrayLists.sortedOf(matches, new ClassNameSorter())) {
				model.addElement(match);
			}

			doSwing(new Runnable() {
				@Override
				public void run() {
					matchList.setModel(model);

					if (model.getSize() > 0) {
						matchList.setSelectedIndex(0);
					}
				}
			});

			return null;
		}

		@Override
		public boolean visit(ClassInfo t) {
			matches.add(t);
			return !Thread.interrupted();
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 927372138232105734L;

	private static void doSwing(Runnable r) {
		try {
			SwingUtilities.invokeAndWait(r);
		} catch (InterruptedException e) {
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static boolean isAnonymous(String className) {
		int index = className.indexOf('$');
		while (index >= 0 && index < className.length() - 1) {
			if (Character.isDigit(className.charAt(index + 1))) {
				return true;
			}
			index = className.indexOf('$', index + 1);
		}
		return false;
	}

	public static void main(String[] args) {
		final ClassChooserPane pane = new ClassChooserPane();
		JOptionPane.showConfirmDialog(null, pane, "Find Type", JOptionPane.OK_CANCEL_OPTION);
		System.out.println(pane.getSelectedClassName());
	}

	JLabel promptLabel;

	JTextField typePrefixField;
	JLabel matchingItemsLabel;

	JLabel loadingLabel;

	JList matchList;

	JScrollPane matchListScrollPane;

	String searchStringForDisplayedMatches;

	ThreadPoolExecutor executor;

	Future<?> currentFuture;

	final StringTrieMap<ClassInfo> mapByPackageName = new StringTrieMap<ClassInfo>();

	final StringTrieMap<ClassInfo> mapByClassName = new StringTrieMap<ClassInfo>();

	public ClassChooserPane() {
		init();
	}

	public JList getMatchList() {
		return matchList;
	}

	public String getSelectedClassName() {
		ClassInfo info = (ClassInfo) matchList.getSelectedValue();
		return info == null ? typePrefixField.getText() : info.fqName;
	}

	private void init() {
		promptLabel = new JLabel("Enter type name prefix or pattern (*, ?, or camel case):");
		typePrefixField = new JTextField();

		matchingItemsLabel = new JLabel("Matching Items:");
		loadingLabel = new JLabel("Loading...");

		matchList = new JList(new DefaultListModel());
		matchList.setCellRenderer(new MatchListCellRenderer());
		matchList.setFont(matchList.getFont().deriveFont(Font.PLAIN));
		matchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		matchListScrollPane = new JScrollPane(matchList);
		matchListScrollPane.setPreferredSize(new Dimension(600, 400));

		GridBagWizard gbw = GridBagWizard.create(this);
		gbw.defaults().autoinsets(new DefaultAutoInsets(5, 5));
		gbw.put(promptLabel, typePrefixField,
				gbw.put(matchingItemsLabel, loadingLabel).intoRow(),
				matchListScrollPane).intoColumn();
		gbw.put(promptLabel, matchingItemsLabel).west();
		gbw.put(loadingLabel).east();
		gbw.put(typePrefixField).fillx(1.0);
		gbw.put(matchListScrollPane).fillboth(1.0, 1.0);

		Keymap kmap = JTextComponent.addKeymap(null, typePrefixField.getKeymap());
		kmap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				new AbstractAction() {
					/**
					 *
					 */
					private static final long serialVersionUID = 3599644162474077572L;

					@Override
					public void actionPerformed(ActionEvent e) {
						int selIndex = matchList.getSelectedIndex();
						matchList.setSelectedIndex((selIndex + matchList.getModel().getSize() - 1)
								% matchList.getModel().getSize());
					}
				});
		kmap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				new AbstractAction() {
					/**
					 *
					 */
					private static final long serialVersionUID = 7913353078046575088L;

					@Override
					public void actionPerformed(ActionEvent e) {
						int selIndex = matchList.getSelectedIndex();
						matchList.setSelectedIndex((selIndex + 1) % matchList.getModel().getSize());
					}
				});
		typePrefixField.setKeymap(kmap);

		executor = new ThreadPoolExecutor(0, 1, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(),
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName(ClassChooserPane.class.getSimpleName() + " matcher thread");
						thread.setDaemon(true);
						return thread;
					}
				});

		typePrefixField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateResultsLater();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateResultsLater();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateResultsLater();
			}
		});

		executor.submit(new Loader());
	}

	public void setTypePrefix(String typePrefix) {
		typePrefixField.setText(typePrefix);
	}

	private void updateResultsLater() {
		if (currentFuture != null) {
			currentFuture.cancel(true);
		}
		currentFuture = executor.submit(new Updater(typePrefixField.getText().toLowerCase()));
	}
}

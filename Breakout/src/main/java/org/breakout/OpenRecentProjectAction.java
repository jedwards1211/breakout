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
package org.breakout;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;

@SuppressWarnings( "serial" )
public class OpenRecentProjectAction extends AbstractAction
{
	BreakoutMainView	mainView;
	/**
	 * the path to the recent project file, relative to the current {@linkplain BreakoutMainView#getRootDirectory()
	 * root settings directory}.
	 */
	Path				recentProjectFile;

	/**
	 * @param mainView
	 * @param recentProjectFile
	 *            the path to the recent project file, relative to the current
	 *            {@linkplain BreakoutMainView#getRootDirectory()
	 *            root settings directory}.
	 */
	public OpenRecentProjectAction( final BreakoutMainView mainView , Path recentProjectFile )
	{
		super( );
		this.mainView = mainView;
		this.recentProjectFile = recentProjectFile;

		putValue( NAME , recentProjectFile.toString( ) );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		Path resolvedProjectFile = mainView.getRootDirectory( ).toAbsolutePath( ).resolve( recentProjectFile )
			.normalize( );
		mainView.openProject( resolvedProjectFile );
	}
}

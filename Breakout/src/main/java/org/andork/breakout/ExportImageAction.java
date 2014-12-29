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
package org.andork.breakout;

import static org.andork.bind.QObjectAttributeBinder.bind;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.andork.awt.I18n.Localizer;
import org.andork.bind.Binder;
import org.andork.breakout.model.ProjectModel;
import org.andork.jogl.awt.JoglExportImageDialog;
import org.andork.jogl.awt.JoglExportImageDialogModel;
import org.andork.q.QObject;
import org.andork.swing.OnEDT;

@SuppressWarnings( "serial" )
public class ExportImageAction extends AbstractAction
{
	BreakoutMainView	mainView;
	JoglExportImageDialog	screenCaptureDialog;

	public ExportImageAction( BreakoutMainView breakoutMainView )
	{
		this.mainView = breakoutMainView;
		new OnEDT( ) {

			@Override
			public void run( ) throws Throwable
			{
				Localizer localizer = mainView.getI18n( ).forClass( ExportImageAction.this.getClass( ) );
				localizer.setName( ExportImageAction.this , "name" );
			}
		};
	}

	public void actionPerformed( ActionEvent e )
	{
		JPanel mainPanel = mainView.getMainPanel( );
		Component canvas = mainView.getCanvas( );

		if( screenCaptureDialog == null )
		{
			screenCaptureDialog = new JoglExportImageDialog( mainView.getAutoDrawable( ) ,
				SwingUtilities.getWindowAncestor( mainPanel ) , mainView.getI18n( ) );
			screenCaptureDialog.setTitle( "Export Image" );
			QObject<JoglExportImageDialogModel> exportImageDialogModel = mainView.getProjectModel( ).get(
				ProjectModel.exportImageDialogModel );
			if( exportImageDialogModel == null )
			{
				exportImageDialogModel = JoglExportImageDialogModel.instance.newObject( );
				exportImageDialogModel.set( JoglExportImageDialogModel.outputDirectory , "screenshots" );
				exportImageDialogModel.set( JoglExportImageDialogModel.fileNamePrefix , "breakout-screenshot" );
				exportImageDialogModel.set( JoglExportImageDialogModel.fileNumber , 1 );
				exportImageDialogModel.set( JoglExportImageDialogModel.pixelWidth , canvas.getWidth( ) );
				exportImageDialogModel.set( JoglExportImageDialogModel.pixelHeight , canvas.getHeight( ) );
				exportImageDialogModel.set( JoglExportImageDialogModel.resolution , new BigDecimal( 300 ) );
				exportImageDialogModel.set( JoglExportImageDialogModel.resolutionUnit ,
					JoglExportImageDialogModel.ResolutionUnit.PIXELS_PER_IN );
				mainView.getProjectModel( ).set( ProjectModel.exportImageDialogModel , exportImageDialogModel );
			}

			Binder<QObject<JoglExportImageDialogModel>> screenCaptureBinder = bind(
				ProjectModel.exportImageDialogModel , mainView.getProjectModelBinder( ) );
			screenCaptureDialog.setBinder( screenCaptureBinder );

			Dimension size = mainPanel.getSize( );
			size.width = size.width * 3 / 4;
			size.height = size.height * 3 / 4;
			screenCaptureDialog.setSize( size );
			screenCaptureDialog.setLocationRelativeTo( mainPanel );
		}

		screenCaptureDialog.setViewSettings( mainView.getViewSettings( ) );
		screenCaptureDialog.setScene( mainView.getScene( ) );

		screenCaptureDialog.setVisible( true );
	}
}

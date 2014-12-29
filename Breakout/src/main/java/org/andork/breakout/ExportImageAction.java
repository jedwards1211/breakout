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
import org.andork.jogl.awt.JoglImageExportDialog;
import org.andork.jogl.awt.JoglImageExportDialogModel;
import org.andork.q.QObject;
import org.andork.swing.OnEDT;

@SuppressWarnings( "serial" )
public class ExportImageAction extends AbstractAction
{
	BreakoutMainView	mainView;
	JoglImageExportDialog	screenCaptureDialog;

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
			screenCaptureDialog = new JoglImageExportDialog( mainView.getAutoDrawable( ) ,
				SwingUtilities.getWindowAncestor( mainPanel ) , mainView.getI18n( ) );
			screenCaptureDialog.setTitle( "Export Image" );
			QObject<JoglImageExportDialogModel> screenCaptureDialogModel = mainView.getProjectModel( ).get(
				ProjectModel.screenCaptureDialogModel );
			if( screenCaptureDialogModel == null )
			{
				screenCaptureDialogModel = JoglImageExportDialogModel.instance.newObject( );
				screenCaptureDialogModel.set( JoglImageExportDialogModel.outputDirectory , "screenshots" );
				screenCaptureDialogModel.set( JoglImageExportDialogModel.fileNamePrefix , "breakout-screenshot" );
				screenCaptureDialogModel.set( JoglImageExportDialogModel.fileNumber , 1 );
				screenCaptureDialogModel.set( JoglImageExportDialogModel.pixelWidth , canvas.getWidth( ) );
				screenCaptureDialogModel.set( JoglImageExportDialogModel.pixelHeight , canvas.getHeight( ) );
				screenCaptureDialogModel.set( JoglImageExportDialogModel.resolution , new BigDecimal( 300 ) );
				screenCaptureDialogModel.set( JoglImageExportDialogModel.resolutionUnit ,
					JoglImageExportDialogModel.ResolutionUnit.PIXELS_PER_IN );
				mainView.getProjectModel( ).set( ProjectModel.screenCaptureDialogModel , screenCaptureDialogModel );
			}

			Binder<QObject<JoglImageExportDialogModel>> screenCaptureBinder = bind(
				ProjectModel.screenCaptureDialogModel , mainView.getProjectModelBinder( ) );
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

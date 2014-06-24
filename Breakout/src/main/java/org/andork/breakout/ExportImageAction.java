package org.andork.breakout;

import static org.andork.bind.QObjectAttributeBinder.bind;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.andork.awt.I18n.Localizer;
import org.andork.bind.Binder;
import org.andork.breakout.model.ProjectModel;
import org.andork.jogl.awt.ScreenCaptureDialog;
import org.andork.jogl.awt.ScreenCaptureDialogModel;
import org.andork.q.QObject;
import org.andork.swing.OnEDT;

public class ExportImageAction extends AbstractAction
{
	BreakoutMainView	mainView;
	ScreenCaptureDialog	screenCaptureDialog;
	
	public ExportImageAction( BreakoutMainView breakoutMainView )
	{
		this.mainView = breakoutMainView;
		new OnEDT( )
		{
			
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
		// // scene.doLater( new ScreenshotHandler( scene ) );
		// scene.doLater( new HiResScreenshotHandler( scene ,
		// new int[ ] { 500 , 500 , 500 } , new int[ ] { 500 , 500 , 500 } , Fit.BOTH ) );
		// canvas.display( );
		
		JPanel mainPanel = mainView.getMainPanel( );
		GLCanvas canvas = mainView.getCanvas( );
		
		if( screenCaptureDialog == null )
		{
			screenCaptureDialog = new ScreenCaptureDialog( SwingUtilities.getWindowAncestor( mainPanel ) ,
					canvas.getContext( ) , mainView.getI18n( ) );
			screenCaptureDialog.setTitle( "Export Image" );
			QObject<ScreenCaptureDialogModel> screenCaptureDialogModel =
					mainView.getProjectModel( ).get( ProjectModel.screenCaptureDialogModel );
			if( screenCaptureDialogModel == null )
			{
				screenCaptureDialogModel = ScreenCaptureDialogModel.instance.newObject( );
				screenCaptureDialogModel.set( ScreenCaptureDialogModel.outputDirectory , "screenshots" );
				screenCaptureDialogModel.set( ScreenCaptureDialogModel.fileNamePrefix , "breakout-screenshot" );
				screenCaptureDialogModel.set( ScreenCaptureDialogModel.fileNumber , 1 );
				screenCaptureDialogModel.set( ScreenCaptureDialogModel.pixelWidth , canvas.getWidth( ) );
				screenCaptureDialogModel.set( ScreenCaptureDialogModel.pixelHeight , canvas.getHeight( ) );
				screenCaptureDialogModel.set( ScreenCaptureDialogModel.resolution , new BigDecimal( 300 ) );
				screenCaptureDialogModel.set( ScreenCaptureDialogModel.resolutionUnit , ScreenCaptureDialogModel.ResolutionUnit.PIXELS_PER_IN );
				mainView.getProjectModel( ).set( ProjectModel.screenCaptureDialogModel , screenCaptureDialogModel );
			}
			Binder<QObject<ScreenCaptureDialogModel>> screenCaptureBinder = bind( ProjectModel.screenCaptureDialogModel , mainView.getProjectModelBinder( ) );
			screenCaptureDialog.setBinder( screenCaptureBinder );
			
			Dimension size = mainPanel.getSize( );
			size.width = size.width * 3 / 4;
			size.height = size.height * 3 / 4;
			screenCaptureDialog.setSize( size );
			screenCaptureDialog.setLocationRelativeTo( mainPanel );
		}
		
		screenCaptureDialog.setScene( mainView.getScene( ) );
		
		screenCaptureDialog.setVisible( true );
	}
	
}

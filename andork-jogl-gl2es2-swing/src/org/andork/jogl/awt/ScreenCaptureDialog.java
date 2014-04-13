package org.andork.jogl.awt;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;

import com.jogamp.newt.awt.NewtCanvasAWT;

public class ScreenCaptureDialog extends JDialog
{
	NewtCanvasAWT	canvas;
	
	public ScreenCaptureDialog( )
	{
		// TODO Auto-generated constructor stub
	}
	
	public ScreenCaptureDialog( Frame owner )
	{
		super( owner );
		// TODO Auto-generated constructor stub
	}
	
	public ScreenCaptureDialog( Dialog owner )
	{
		super( owner );
		// TODO Auto-generated constructor stub
	}
	
	public ScreenCaptureDialog( Window owner )
	{
		super( owner );
		// TODO Auto-generated constructor stub
	}
	
}

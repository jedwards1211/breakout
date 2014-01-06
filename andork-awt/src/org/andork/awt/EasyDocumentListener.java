package org.andork.awt;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class EasyDocumentListener implements DocumentListener
{
	public abstract void documentChanged( DocumentEvent e );
	
	@Override
	public void insertUpdate( DocumentEvent e )
	{
		documentChanged( e );
	}
	
	@Override
	public void removeUpdate( DocumentEvent e )
	{
		documentChanged( e );
	}
	
	@Override
	public void changedUpdate( DocumentEvent e )
	{
		documentChanged( e );
	}
	
}

package org.andork.breakout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.awt.event.MouseAdapterWrapper;
import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.q.QSpec.Attribute;
import org.andork.swing.table.AnnotatingJTable;
import org.jdesktop.swingx.table.TableColumnExt;

@SuppressWarnings( "serial" )
public class SurveyTable extends AnnotatingJTable
{
	public void createDefaultColumnsFromModel( )
	{
		TableModel m = getModel( );
		if( m != null )
		{
			// Remove any current columns
			TableColumnModel cm = getColumnModel( );
			while( cm.getColumnCount( ) > 0 )
			{
				cm.removeColumn( cm.getColumn( 0 ) );
			}
			
			Attribute<?>[ ] attrs = new Attribute<?>[ ] { Row.from , Row.to , Row.distance , Row.fsAzm , Row.fsInc , Row.bsAzm , Row.bsInc ,
					Row.left , Row.right , Row.up , Row.down , Row.north , Row.east , Row.elev , Row.desc , Row.date , Row.surveyors , Row.comment , Row.scannedNotes };
			String[ ] names = new String[ ] { "From" , "To" , "Distance" , "Front Azimuth" , "Front Inclination" , "Back Azimuth" , "Back Inclination" ,
					"Left" , "Right" , "Up" , "Down" , "Northing" , "Easting" , "Elevation" , "Description" , "Date" , "Surveyors" , "Comment" , "Scanned Notes" };
			
			for( int i = 0 ; i < attrs.length ; i++ )
			{
				TableColumnExt column = new TableColumnExt( attrs[ i ].getIndex( ) );
				column.setIdentifier( names[ i ] );
				column.setHeaderValue( names[ i ] );
				addColumn( column );
			}
		}
		
		addMouseListener( new MouseAdapter( )
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				if( !e.isControlDown( ) )
				{
					return;
				}
				
				int column = columnAtPoint( e.getPoint( ) );
				int row = rowAtPoint( e.getPoint( ) );
				
				int modelColumn = convertColumnIndexToModel( column );
				
				if( modelColumn == SurveyTableModel.Row.scannedNotes.getIndex( ) )
				{
					Object o = getValueAt( row , column );
					if( o != null )
					{
						try
						{
							Runtime.getRuntime( ).exec( "explorer " + o.toString( ) );
						}
						catch( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
			}
		} );
	}
	
	public SurveyTable( )
	{
		super( new SurveyTableModel( ) );
	}
	
	public SurveyTableModel getModel( )
	{
		return ( SurveyTableModel ) super.getModel( );
	}
	
}

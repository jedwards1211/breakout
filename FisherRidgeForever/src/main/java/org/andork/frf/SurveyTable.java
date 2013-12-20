package org.andork.frf;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

import org.andork.frf.model.SurveyShot;
import org.andork.frf.model.SurveyStation;
import org.andork.math3d.Vecmath;

@SuppressWarnings( "serial" )
public class SurveyTable extends JTable
{
	public SurveyTable( )
	{
		super( new DefaultTableModel( new Object[ ] { "From" , "To" , "Distance" , "FS Azm" , "FS Inc" , "BS Azm" , "BS Inc" , "L" , "R" , "U" , "D" } , 65536 ) );
		
		setTransferHandler( new TransferHandler( )
		{
			@Override
			public boolean importData( TransferSupport support )
			{
				String text = null;
				try
				{
					text = ( String ) support.getTransferable( ).getTransferData( DataFlavor.stringFlavor );
				}
				catch( UnsupportedFlavorException e )
				{
					e.printStackTrace( );
					return false;
				}
				catch( IOException e )
				{
					e.printStackTrace( );
					return false;
				}
				
				JTable.DropLocation dropLocation = ( JTable.DropLocation ) support.getDropLocation( );
				
				int row = dropLocation.getRow( );
				for( String line : text.split( "\r|\n|\r\n|\n\r" ) )
				{
					int column = dropLocation.getColumn( );
					for( String cell : line.split( "\\s+" ) )
					{
						setValueAt( cell , row , column );
						column++ ;
					}
					
					row++ ;
				}
				
				return true;
			}
			
			@Override
			public boolean canImport( TransferSupport support )
			{
				return support.isDataFlavorSupported( DataFlavor.stringFlavor );
			}
		} );
	}
	
	public static final int	FROM_COLUMN		= 0;
	public static final int	TO_COLUMN		= 1;
	public static final int	DISTANCE_COLUMN	= 2;
	public static final int	FS_AZM_COLUMN	= 3;
	public static final int	BS_AZM_COLUMN	= 5;
	public static final int	FS_INC_COLUMN	= 4;
	public static final int	BS_INC_COLUMN	= 6;
	public static final int	LEFT_COLUMN		= 7;
	public static final int	RIGHT_COLUMN	= 8;
	public static final int	UP_COLUMN		= 9;
	public static final int	DOWN_COLUMN		= 10;
	
	public List<SurveyShot> createShots( )
	{
		Map<String, SurveyStation> stations = new LinkedHashMap<String, SurveyStation>( );
		List<SurveyShot> shots = new ArrayList<SurveyShot>( );
		
		for( int row = 0 ; row < getRowCount( ) ; row++ )
		{
			try
			{
				Object fromName = getValueAt( row , FROM_COLUMN );
				Object toName = getValueAt( row , TO_COLUMN );
				Double dist = parse( getValueAt( row , DISTANCE_COLUMN ) );
				Double fsAzm = parse( getValueAt( row , FS_AZM_COLUMN ) );
				Double bsAzm = parse( getValueAt( row , BS_AZM_COLUMN ) );
				Double fsInc = parse( getValueAt( row , FS_INC_COLUMN ) );
				Double bsInc = parse( getValueAt( row , BS_INC_COLUMN ) );
				
				if( fromName == null || toName == null || dist == null || fsAzm == null || bsAzm == null || fsInc == null || bsInc == null )
				{
					continue;
				}
				
				Double left = parse( getValueAt( row , LEFT_COLUMN ) );
				Double right = parse( getValueAt( row , RIGHT_COLUMN ) );
				Double up = parse( getValueAt( row , UP_COLUMN ) );
				Double down = parse( getValueAt( row , DOWN_COLUMN ) );
				
				SurveyStation from = stations.get( fromName.toString( ) );
				if( from == null )
				{
					from = new SurveyStation( );
					Arrays.fill( from.position , Double.NaN );
					from.name = fromName.toString( );
					stations.put( from.name , from );
				}
				
				SurveyStation to = stations.get( toName.toString( ) );
				if( to == null )
				{
					to = new SurveyStation( );
					to.name = toName.toString( );
					stations.put( to.name , to );
					
					double azm = Math.toRadians( averageAzm( fsAzm , bsAzm ) );
					double inc = Math.toRadians( ( fsInc + bsInc ) * 0.5 );
					
					Arrays.fill( to.position , Double.NaN );
					// to.position[ 0 ] = from.position[ 0 ] + Math.sin( azm ) * Math.cos( inc ) * dist;
					// to.position[ 1 ] = from.position[ 1 ] + Math.sin( inc ) * dist;
					// to.position[ 2 ] = from.position[ 2 ] - Math.cos( azm ) * Math.cos( inc ) * dist;
				}
				
				SurveyShot shot = new SurveyShot( );
				shot.index = shots.size( );
				shot.from = from;
				shot.to = to;
				shot.dist = dist;
				shot.fsAzm = fsAzm;
				shot.bsAzm = bsAzm;
				shot.fsInc = fsInc;
				shot.bsInc = bsInc;
				shot.left = left == null ? 0.0 : left;
				shot.right = right == null ? 0.0 : right;
				shot.up = up == null ? 0.0 : up;
				shot.down = down == null ? 0.0 : down;
				
				from.frontsights.add( shot );
				to.backsights.add( shot );
				
				shots.add( shot );
			}
			catch( Exception ex )
			{
				continue;
			}
		}
		
		int numFixed;
		do
		{
			numFixed = 0;
			for( SurveyStation station : stations.values( ) )
			{
				if( computePosition( station ) )
				{
					numFixed++ ;
				}
			}
		} while( numFixed > 0 );
		
		return shots;
	}
	
	private static final double[ ]	temp	= new double[ 3 ];
	
	private static boolean computePosition( SurveyStation station )
	{
		if( Double.isNaN( station.position[ 0 ] ) )
		{
			if( !station.backsights.isEmpty( ) )
			{
				for( SurveyShot shot : station.backsights )
				{
					if( !Double.isNaN( shot.from.position[ 0 ] ) )
					{
						double azm = Math.toRadians( averageAzm( shot.fsAzm , shot.bsAzm ) );
						double inc = Math.toRadians( ( shot.fsInc + shot.bsInc ) * 0.5 );
						
						station.position[ 0 ] = shot.from.position[ 0 ] + Math.sin( azm ) * Math.cos( inc ) * shot.dist;
						station.position[ 1 ] = shot.from.position[ 1 ] + Math.sin( inc ) * shot.dist;
						station.position[ 2 ] = shot.from.position[ 2 ] - Math.cos( azm ) * Math.cos( inc ) * shot.dist;
					}
				}
			}
			else
			{
				Arrays.fill( station.position , 0 );
			}
			return true;
		}
		return false;
	}
	
	private static void computeFollowing( SurveyStation station )
	{
		LinkedList<SurveyStation> toCompute = new LinkedList<SurveyStation>( );
		for( SurveyShot shot : station.frontsights )
		{
			toCompute.add( shot.to );
		}
		while( !toCompute.isEmpty( ) )
		{
			SurveyStation next = toCompute.poll( );
			computePosition( next );
			for( SurveyShot shot : next.frontsights )
			{
				if( Double.isNaN( shot.to.position[ 0 ] ) )
				{
					toCompute.add( shot.to );
				}
			}
		}
	}
	
	private static double averageAzm( double fsAzm , double bsAzm )
	{
		fsAzm %= 360.0;
		if( fsAzm < 0 )
		{
			fsAzm += 360.0;
		}
		bsAzm %= 360.0;
		if( bsAzm < 0 )
		{
			bsAzm += 360.0;
		}
		
		if( Math.abs( bsAzm - fsAzm ) < 180 )
		{
			return ( fsAzm + bsAzm ) * 0.5;
		}
		else
		{
			return 180.0 + ( fsAzm - 180.0 + bsAzm - 180.0 ) * 0.5;
		}
	}
	
	private static Double parse( Object o )
	{
		if( o == null )
		{
			return null;
		}
		try
		{
			return Double.valueOf( o.toString( ) );
		}
		catch( Exception ex )
		{
			return null;
		}
	}
}

package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.andork.collect.CollectionUtils;
import org.andork.swing.table.TableModelList;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class ShotList extends TableModelList<Shot>
{
	private Class<? extends ShotVector>		defaultVectorType		= DaicShotVector.class;
	private Class<? extends DaiShotVector>	defaultDaiVectorType	= DaicShotVector.class;
	private Class<? extends NevShotVector>	defaultNevVectorType	= NedShotVector.class;
	private Class<? extends XSect>			defaultXSectType		= BisectorLrudXSect.class;
	private Unit<Length>					defaultLengthUnit		= Length.meters;
	private Unit<Angle>						defaultAngleUnit		= Angle.degrees;

	private List<ShotColumnDef>				customColumnDefs		= Collections.emptyList( );

	public ShotVector newDefaultVector( )
	{
		try
		{
			return defaultVectorType.newInstance( );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
			return null;
		}
	}

	public Class<? extends ShotVector> getDefaultVectorType( )
	{
		return defaultVectorType;
	}

	public void setDefaultVectorType( Class<? extends ShotVector> defaultVectorType )
	{
		this.defaultVectorType = Objects.requireNonNull( defaultVectorType );
	}

	public DaiShotVector newDefaultDaiVector( )
	{
		try
		{
			return defaultDaiVectorType.newInstance( );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
			return null;
		}
	}

	public Class<? extends DaiShotVector> getDefaultDaiVectorType( )
	{
		return defaultDaiVectorType;
	}

	public void setDefaultDaiVectorType( Class<? extends DaiShotVector> defaultDaiVectorType )
	{
		this.defaultDaiVectorType = Objects.requireNonNull( defaultDaiVectorType );
	}

	public NevShotVector newDefaultNevVector( )
	{
		try
		{
			return defaultNevVectorType.newInstance( );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
			return null;
		}
	}

	public Class<? extends NevShotVector> getDefaultNevVectorType( )
	{
		return defaultNevVectorType;
	}

	public void setDefaultNevVectorType( Class<? extends NevShotVector> defaultNevVectorType )
	{
		this.defaultNevVectorType = Objects.requireNonNull( defaultNevVectorType );
	}

	public Class<? extends XSect> getDefaultXSectType( )
	{
		return defaultXSectType;
	}

	public void setDefaultXSectType( Class<? extends XSect> defaultXSectType )
	{
		this.defaultXSectType = Objects.requireNonNull( defaultXSectType );
	}

	public Unit<Length> getDefaultLengthUnit( )
	{
		return defaultLengthUnit;
	}

	public void setDefaultLengthUnit( Unit<Length> defaultLengthUnit )
	{
		this.defaultLengthUnit = Objects.requireNonNull( defaultLengthUnit );
	}

	public Unit<Angle> getDefaultAngleUnit( )
	{
		return defaultAngleUnit;
	}

	public void setDefaultAngleUnit( Unit<Angle> defaultAngleUnit )
	{
		this.defaultAngleUnit = Objects.requireNonNull( defaultAngleUnit );
	}

	public List<ShotColumnDef> getCustomColumnDefs( )
	{
		return customColumnDefs;
	}

	public void setCustomColumnDefs( List<ShotColumnDef> newCustomColumnDefs )
	{
		newCustomColumnDefs = Collections.unmodifiableList( new ArrayList<>( newCustomColumnDefs ) );

		List<ShotColumnDef> oldCustomColumnDefs = customColumnDefs;

		// We may need to resize each Shot.custom array to correspond to the new columns.
		// For each new column that matches the name and type of an old column, we need to copy the value from
		// the old array (from the old column's position) to the new array (in the new column's position).

		// the index into copyMap will represent the index of the new column, and the value at that index 
		// will represent the index of the corresponding old column, or -1 if there is no corresponding old column.

		int[ ] copyMap = new int[ newCustomColumnDefs.size( ) ];
		Arrays.fill( copyMap , -1 );

		boolean customValuesChanged = newCustomColumnDefs.size( ) != oldCustomColumnDefs.size( );

		ListIterator<ShotColumnDef> i = newCustomColumnDefs.listIterator( );
		while( i.hasNext( ) )
		{
			int index = i.nextIndex( );
			ShotColumnDef newCustomColumnDef = i.next( );
			copyMap[ index ] = CollectionUtils.indexOf( oldCustomColumnDefs , d -> newCustomColumnDef.equals( d ) );
			customValuesChanged |= copyMap[ index ] != index;
		}

		// Now if necessary, resize each Shot.custom array and copy the values from the old array to the new array,
		// according to the mapping in copyMap.

		if( customValuesChanged )
		{
			for( Shot shot : this )
			{
				Object[ ] newCustom = new Object[ copyMap.length ];
				for( int k = 0 ; k < copyMap.length ; k++ )
				{
					if( copyMap[ k ] >= 0 )
					{
						newCustom[ k ] = shot.custom[ copyMap[ k ] ];
					}
				}
				shot.custom = newCustom;
			}
		}

		customColumnDefs = newCustomColumnDefs;

		fireStructureChanged( );
	}
}
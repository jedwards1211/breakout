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
package org.andork.swing.selector;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import org.andork.util.Java7;

/**
 * An {@link ISelector} controlled by a {@link JComboBox}. When the user selects a different item in the combo box, {@link ISelectorListener}s will be notified.
 * When the program sets the selection, the combo box will also be updated to that selection.<br>
 * <br>
 * If there are no available values in this selector, the {@link #setNothingAvailableItem(Object) nothingAvailableItem} will be temporarily displayed in the
 * combo box if it is not null. If there are values available, but the selection is null, the {@link #setNullItem(Object) nullItem} will be temporarily
 * displayed if it is not null. If the selection is not null, but not one of the available values, the {@link #setSelectionNotAvailableItem(Object)
 * selectionNotAvailableItem} will be temporarily displayed if it is not null. These items will not not affect the {@link #getSelection() selection} or the
 * {@link #setAvailableValues(List) available values}.
 * 
 * @author james.a.edwards
 * @param <T>
 *        the selection type.
 */
public class DefaultSelector<T> implements ISelector<T>
{
	public DefaultSelector( )
	{
		this( new JComboBox( ) );
	}
	
	public DefaultSelector( JComboBox comboBox )
	{
		this.comboBox = comboBox;
		init( );
	}
	
	private boolean								disableListeners;
	
	private Object								nullItem;
	private Object								nothingAvailableItem;
	private Object								selectionNotAvailableItem;
	
	private JComboBox							comboBox;
	
	private final List<ISelectorListener<T>>	listeners						= new ArrayList<ISelectorListener<T>>( );
	
	private final List<T>						availableValues					= new ArrayList<T>( );
	private T									selection;
	
	private boolean								allowSelectionNotAvailable		= false;
	
	private boolean								enabled							= true;
	
	private boolean								disableWhenOnlyOneAvailableItem	= false;
	
	private void init( )
	{
		comboBox.setModel( new BetterComboBoxModel( ) );
		
		comboBox.addItemListener( new ItemListener( )
		{
			public void itemStateChanged( ItemEvent e )
			{
				if( !disableListeners && e.getStateChange( ) == ItemEvent.SELECTED )
				{
					if( !isTransientItem( e.getItem( ) ) && availableValues.contains( e.getItem( ) ) )
					{
						setSelection( ( T ) e.getItem( ) );
					}
				}
			}
		} );
		
		updateComboBoxAvailableItems( );
	}
	
	/**
	 * Gets the {@link JComboBox} controlled by this {@code DefaultSelector}. You should not need to listen directly for ItemEvents or other user input from the
	 * combo box; use an {@link ISelectorListener} or this instead. Only use this method to put the combo box into a layout.
	 * 
	 * @return the combo box controlled by this {@code DefaultSelector}.
	 */
	public JComboBox getComboBox( )
	{
		return comboBox;
	}
	
	public Object getNullItem( )
	{
		return nullItem;
	}
	
	public void setNullItem( Object nullItem )
	{
		if( this.nullItem != nullItem )
		{
			this.nullItem = nullItem;
			updateComboBoxSelectedItem( );
		}
	}
	
	public Object getNothingAvailableItem( )
	{
		return nothingAvailableItem;
	}
	
	public void setNothingAvailableItem( Object nothingAvailableItem )
	{
		if( this.nothingAvailableItem != nothingAvailableItem )
		{
			this.nothingAvailableItem = nothingAvailableItem;
			updateComboBoxSelectedItem( );
		}
	}
	
	public Object getSelectionNotAvailableItem( )
	{
		return selectionNotAvailableItem;
	}
	
	public void setSelectionNotAvailableItem( Object selectionNotAvailableItem )
	{
		if( this.selectionNotAvailableItem != selectionNotAvailableItem )
		{
			this.selectionNotAvailableItem = selectionNotAvailableItem;
			updateComboBoxSelectedItem( );
		}
	}
	
	public boolean isAllowSelectionNotAvailable( )
	{
		return allowSelectionNotAvailable;
	}
	
	public void setAllowSelectionNotAvailable( boolean useSelectionNotAvailableItem )
	{
		if( this.allowSelectionNotAvailable != useSelectionNotAvailableItem )
		{
			this.allowSelectionNotAvailable = useSelectionNotAvailableItem;
			updateComboBoxSelectedItem( );
		}
	}
	
	public boolean isDisableWhenOnlyOneAvailableItem( )
	{
		return disableWhenOnlyOneAvailableItem;
	}
	
	public void setDisableWhenOnlyOneAvailableItem( boolean disableWhenOnlyOneAvailableItem )
	{
		if( this.disableWhenOnlyOneAvailableItem != disableWhenOnlyOneAvailableItem )
		{
			this.disableWhenOnlyOneAvailableItem = disableWhenOnlyOneAvailableItem;
			updateComboBoxEnabled( );
		}
	}
	
	protected void notifySelectionChanged( T oldSelection , T newSelection )
	{
		for( ISelectorListener<T> listener : listeners )
		{
			try
			{
				listener.selectionChanged( this , oldSelection , newSelection );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
		}
	}
	
	public void addSelectorListener( ISelectorListener<T> listener )
	{
		if( !listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}
	
	public void removeSelectorListener( ISelectorListener<T> listener )
	{
		listeners.remove( listener );
	}
	
	protected boolean isTransientItem( Object o )
	{
		return o != null && ( o == nullItem || o == nothingAvailableItem || o == selectionNotAvailableItem ||
				( selection == o && allowSelectionNotAvailable && !availableValues.contains( selection ) ) );
	}
	
	protected void updateComboBoxAvailableItems( )
	{
		boolean listenersWereDisabled = disableListeners;
		disableListeners = true;
		try
		{
			comboBox.removeAllItems( );
			for( T value : availableValues )
			{
				comboBox.addItem( value );
			}
			updateComboBoxSelectedItem( );
		}
		finally
		{
			disableListeners = listenersWereDisabled;
		}
	}
	
	protected void updateComboBoxEnabled( )
	{
		comboBox.setEnabled( enabled && ( availableValues.size( ) > 1 ||
				( availableValues.size( ) == 1 && !disableWhenOnlyOneAvailableItem ) ) );
	}
	
	protected Object pickItemToSelect( )
	{
		Object newSelectedItem = null;
		if( allowSelectionNotAvailable && selection != null && !availableValues.contains( selection ) )
		{
			newSelectedItem = selection;
		}
		else if( availableValues.isEmpty( ) )
		{
			newSelectedItem = nothingAvailableItem;
		}
		else if( selection == null )
		{
			newSelectedItem = nullItem;
		}
		else if( !availableValues.contains( selection ) )
		{
			newSelectedItem = selectionNotAvailableItem;
		}
		else
		{
			newSelectedItem = selection;
			if( nullItem != null )
			{
				comboBox.removeItem( nullItem );
			}
			if( selectionNotAvailableItem != null )
			{
				comboBox.removeItem( selectionNotAvailableItem );
			}
			if( nothingAvailableItem != null )
			{
				comboBox.removeItem( nothingAvailableItem );
			}
		}
		return newSelectedItem;
	}
	
	protected void updateComboBoxSelectedItem( )
	{
		boolean listenersWereDisabled = disableListeners;
		disableListeners = true;
		try
		{
			Object oldSelectedItem = comboBox.getSelectedItem( );
			Object newSelectedItem = pickItemToSelect( );
			
			if( oldSelectedItem != newSelectedItem )
			{
				if( isTransientItem( newSelectedItem ) )
				{
					comboBox.insertItemAt( newSelectedItem , 0 );
				}
				comboBox.setSelectedItem( newSelectedItem );
				if( isTransientItem( oldSelectedItem ) )
				{
					comboBox.removeItem( oldSelectedItem );
				}
			}
			
			updateComboBoxEnabled( );
		}
		finally
		{
			disableListeners = listenersWereDisabled;
		}
	}
	
	public <TT extends T> void setAvailableValues( TT ... newAvailableValues )
	{
		setAvailableValues( Arrays.asList( newAvailableValues ) );
	}
	
	/**
	 * Sets the list of values available for user selection. If the selected value is not in the list, the selection will be cleared.
	 * 
	 * @param newAvailableValues
	 *        the new list of available values.
	 */
	public void setAvailableValues( Collection<? extends T> newAvailableValues )
	{
		if( !availableValues.equals( newAvailableValues ) )
		{
			availableValues.clear( );
			availableValues.addAll( newAvailableValues );
			updateComboBoxAvailableItems( );
		}
	}
	
	/**
	 * Gets the list of values available for user selection.
	 */
	public List<T> getAvailableValues( )
	{
		return Collections.unmodifiableList( availableValues );
	}
	
	public void addAvailableValue( int index , T value )
	{
		availableValues.add( index , value );
		updateComboBoxAvailableItems( );
	}
	
	public void setAvailableValue( int index , T value )
	{
		availableValues.set( index , value );
		updateComboBoxAvailableItems( );
	}
	
	public void addAvailableValue( T value )
	{
		availableValues.add( value );
		updateComboBoxAvailableItems( );
	}
	
	public T removeAvailableValue( int index )
	{
		T result = availableValues.remove( index );
		updateComboBoxAvailableItems( );
		return result;
	}
	
	/**
	 * Sets the selected value.
	 * 
	 * @param newSelection
	 *        the new desired selection. Has no effect if it is not in the list of available value.
	 */
	public void setSelection( T newSelection )
	{
		if( !Java7.Objects.equals( newSelection , selection ) )
		{
			T oldSelection = selection;
			selection = newSelection;
			updateComboBoxSelectedItem( );
			notifySelectionChanged( oldSelection , newSelection );
		}
	}
	
	public T getSelection( )
	{
		return selection;
	}
	
	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
		updateComboBoxEnabled( );
	}
}

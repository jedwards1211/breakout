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
package org.andork.ui.test.fixture;

import javax.swing.JComboBox;

import org.andork.func.Predicate;
import org.andork.swing.DoSwing;
import org.andork.util.Java7;
import org.junit.Assert;

public class HardJComboBoxFixture extends HardComponentFixture implements JComboBoxFixture
{
	@Override
	public void selectItemAtIndex( final JComboBox cb , final int index )
	{
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				cb.setSelectedIndex( index );
			}
		};
	}
	
	@Override
	public void selectItem( final JComboBox cb , final Object item )
	{
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				if( cb.isEditable( ) )
				{
					cb.setSelectedItem( item );
				}
				else
				{
					for( int i = 0 ; i < cb.getItemCount( ) ; i++ )
					{
						if( Java7.Objects.equals( cb.getItemAt( i ) , item ) )
						{
							cb.setSelectedIndex( i );
							return;
						}
					}
					Assert.fail( "Couldn't find " + item );
				}
			}
		};
	}
	
	@Override
	public void selectItemMatching( final JComboBox cb , final Predicate<Object> p )
	{
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				for( int i = 0 ; i < cb.getItemCount( ) ; i++ )
				{
					if( p.eval( cb.getItemAt( i ) ) )
					{
						cb.setSelectedIndex( i );
						return;
					}
				}
				Assert.fail( "Couldn't find an item matching predicate " + p );
			}
		};
	}
	
	@Override
	public void selectItemByText( final JComboBox cb , final Predicate<String> p )
	{
		new DoSwing( )
		{
			@Override
			public void run( )
			{
				for( int i = 0 ; i < cb.getItemCount( ) ; i++ )
				{
					String text = JComboBoxFixture.Common.readText( cb , i );
					if( p.eval( text ) )
					{
						cb.setSelectedIndex( i );
						return;
					}
				}
				Assert.fail( "Couldn't find an item matching predicate " + p );
			}
		};
	}
}

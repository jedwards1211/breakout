package org.andork.awt.event;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.andork.event.Binder;
import org.andork.event.Binder.Binding;
import org.andork.model.Model;
import org.andork.swing.selector.ISelector;
import org.andork.swing.selector.ISelectorListener;

public class UIBindings
{
	public static ToggleButtonBinding bind( Binder b , AbstractButton button , Object property )
	{
		ToggleButtonBinding binding = new ToggleButtonBinding( button , property );
		b.bind( binding );
		return binding;
	}
	
	public static class ToggleButtonBinding extends Binding implements ItemListener
	{
		AbstractButton	button;
		
		public ToggleButtonBinding( AbstractButton button , Object property )
		{
			super( property );
			this.button = button;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			Boolean b = ( Boolean ) model.get( property );
			if( b != null )
			{
				button.setSelected( b );
			}
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			model.set( property , button.isSelected( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			button.removeItemListener( this );
		}
		
		@Override
		public void itemStateChanged( ItemEvent e )
		{
			viewToModel( );
		}
		
		@Override
		public void registerWithView( )
		{
			button.addItemListener( this );
		}
	}
	
	public static RadioButtonBinding bind( Binder b , AbstractButton button , Object property , Object key )
	{
		RadioButtonBinding binding = new RadioButtonBinding( button , property , key );
		b.bind( binding );
		return binding;
	}
	
	public static class RadioButtonBinding extends Binding implements ItemListener
	{
		AbstractButton	button;
		Object			key;
		
		public RadioButtonBinding( AbstractButton button , Object property , Object key )
		{
			super( property );
			this.button = button;
			this.key = key;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			Object key = model.get( property );
			if( key == this.key )
			{
				button.setSelected( true );
			}
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			if( button.isSelected( ) )
			{
				model.set( property , key );
			}
		}
		
		@Override
		public void unregisterFromView( )
		{
			button.removeItemListener( this );
		}
		
		@Override
		public void itemStateChanged( ItemEvent e )
		{
			viewToModel( );
		}
		
		@Override
		public void registerWithView( )
		{
			button.addItemListener( this );
		}
	}
	
	public static JTextComponentBinding bind( Binder b , JTextComponent textComp , Object property )
	{
		JTextComponentBinding binding = new JTextComponentBinding( textComp , property );
		b.bind( binding );
		return binding;
	}
	
	public static class JTextComponentBinding extends Binding implements DocumentListener
	{
		boolean			updating;
		JTextComponent	textComp;
		
		public JTextComponentBinding( JTextComponent textComp , Object property )
		{
			super( property );
			this.textComp = textComp;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			textComp.setText( ( String ) model.get( property ) );
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			model.set( property , textComp.getText( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			textComp.getDocument( ).removeDocumentListener( this );
		}
		
		@Override
		public void registerWithView( )
		{
			textComp.getDocument( ).addDocumentListener( this );
		}
		
		@Override
		public void insertUpdate( DocumentEvent e )
		{
			viewToModel( );
		}
		
		@Override
		public void removeUpdate( DocumentEvent e )
		{
			viewToModel( );
		}
		
		@Override
		public void changedUpdate( DocumentEvent e )
		{
			viewToModel( );
		}
	}
	
	public static JFormattedTextFieldBinding bind( Binder b , JFormattedTextField textComp , Object property )
	{
		JFormattedTextFieldBinding binding = new JFormattedTextFieldBinding( textComp , property );
		b.bind( binding );
		return binding;
	}
	
	public static class JFormattedTextFieldBinding extends Binding implements PropertyChangeListener
	{
		JFormattedTextField	textComp;
		
		public JFormattedTextFieldBinding( JFormattedTextField textComp , Object property )
		{
			super( property );
			this.textComp = textComp;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			textComp.setValue( model.get( property ) );
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			model.set( property , textComp.getValue( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			textComp.removePropertyChangeListener( "value" , this );
		}
		
		@Override
		public void registerWithView( )
		{
			textComp.addPropertyChangeListener( "value" , this );
		}
		
		@Override
		public void propertyChange( PropertyChangeEvent evt )
		{
			viewToModel( );
		}
	}
	
	public static JSpinnerBinding bind( Binder b , JSpinner spinner , Object property )
	{
		JSpinnerBinding binding = new JSpinnerBinding( spinner , property );
		b.bind( binding );
		return binding;
	}
	
	public static class JSpinnerBinding extends Binding implements ChangeListener
	{
		JSpinner	spinner;
		
		public JSpinnerBinding( JSpinner spinner , Object property )
		{
			super( property );
			this.spinner = spinner;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			if( model.get( property ) != null )
			{
				spinner.setValue( model.get( property ) );
			}
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			model.set( property , spinner.getValue( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			spinner.removeChangeListener( this );
		}
		
		@Override
		public void registerWithView( )
		{
			spinner.addChangeListener( this );
		}
		
		@Override
		public void stateChanged( ChangeEvent e )
		{
			viewToModel( );
		}
	}
	
	public static JSliderBinding bind( Binder b , JSlider slider , Object property )
	{
		JSliderBinding binding = new JSliderBinding( slider , property );
		b.bind( binding );
		return binding;
	}
	
	public static class JSliderBinding extends Binding implements ChangeListener
	{
		JSlider	slider;
		
		public JSliderBinding( JSlider slider , Object property )
		{
			super( property );
			this.slider = slider;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			if( model.get( property ) != null )
			{
				Integer value = ( Integer ) model.get( property );
				if( value != null )
				{
					slider.setValue( value );
				}
			}
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			model.set( property , slider.getValue( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			slider.removeChangeListener( this );
		}
		
		@Override
		public void registerWithView( )
		{
			slider.addChangeListener( this );
		}
		
		@Override
		public void stateChanged( ChangeEvent e )
		{
			viewToModel( );
		}
	}
	
	public static JComboBoxEnumBinding bind( Binder b , JComboBox comboBox , Object property )
	{
		JComboBoxEnumBinding binding = new JComboBoxEnumBinding( comboBox , property );
		b.bind( binding );
		return binding;
	}
	
	public static class JComboBoxEnumBinding extends Binding implements ItemListener
	{
		JComboBox	comboBox;
		
		public JComboBoxEnumBinding( JComboBox comboBox , Object property )
		{
			super( property );
			this.comboBox = comboBox;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			comboBox.setSelectedItem( model.get( property ) );
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			model.set( property , comboBox.getSelectedItem( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			comboBox.removeItemListener( this );
		}
		
		@Override
		public void itemStateChanged( ItemEvent e )
		{
			viewToModel( );
		}
		
		@Override
		public void registerWithView( )
		{
			comboBox.addItemListener( this );
		}
	}
	
	public static ISelectorEnumBinding bind( Binder b , ISelector selector , Object property )
	{
		ISelectorEnumBinding binding = new ISelectorEnumBinding( selector , property );
		b.bind( binding );
		return binding;
	}
	
	public static class ISelectorEnumBinding extends Binding implements ISelectorListener
	{
		ISelector	selector;
		
		public ISelectorEnumBinding( ISelector selector , Object property )
		{
			super( property );
			this.selector = selector;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			selector.setSelection( model.get( property ) );
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			model.set( property , selector.getSelection( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			selector.removeSelectorListener( this );
		}
		
		@Override
		public void registerWithView( )
		{
			selector.addSelectorListener( this );
		}
		
		@Override
		public void selectionChanged( ISelector selector , Object oldSelection , Object newSelection )
		{
			viewToModel( );
		}
	}
	
	public static BgColorBinding bindBgColor( Binder b , Component comp , Object property )
	{
		BgColorBinding binding = new BgColorBinding( comp , property );
		b.bind( binding );
		return binding;
	}

	public static class BgColorBinding extends Binding implements PropertyChangeListener
	{
		Component	comp;
		
		public BgColorBinding( Component comp , Object property )
		{
			super( property );
			this.comp = comp;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			if( model.get( property ) != null )
			{
				Color color = ( Color ) model.get( property );
				if( color != null )
				{
					comp.setBackground( color );
				}
			}
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model == null )
			{
				return;
			}
			if( comp.isBackgroundSet( ) )
			{
				model.set( property , comp.getBackground( ) );
			}
		}
		
		@Override
		public void unregisterFromView( )
		{
			comp.removePropertyChangeListener( "background" , this );
		}
		
		@Override
		public void registerWithView( )
		{
			comp.addPropertyChangeListener( "background" , this );
		}
		
		@Override
		public void propertyChange( PropertyChangeEvent e )
		{
			viewToModel( );
		}
	}
}

package org.andork.snakeyaml;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.andork.event.PropertyBinder;
import org.andork.snakeyaml.YamlSpec.Attribute;

public class YamlObjectAttributeBinder<S extends YamlSpec<S>> extends PropertyBinder<YamlObject<S>>
{
	@Override
	protected Object getProperty( YamlObject<S> model , Object property )
	{
		return model.get( ( Attribute<?> ) property );
	}
	
	public <B extends YamlSpec<B>> YamlObjectAttributeBinder<B> subBinder( Attribute<YamlObject<B>> property )
	{
		YamlObjectAttributeBinder<B> subBinder = ( YamlObjectAttributeBinder<B> ) subBinders.get( property );
		if( subBinder == null )
		{
			subBinder = new YamlObjectAttributeBinder<B>( );
			subBinders.put( property , subBinder );
		}
		return subBinder;
	}
	
	public ToggleButtonBinding bind( AbstractButton button , Attribute<Boolean> property )
	{
		ToggleButtonBinding binding = new ToggleButtonBinding( button , property );
		bind( binding );
		return binding;
	}
	
	public class ToggleButtonBinding implements Binding , ItemListener
	{
		AbstractButton		button;
		Attribute<Boolean>	property;
		
		public ToggleButtonBinding( AbstractButton button , Attribute<Boolean> property )
		{
			this.button = button;
			this.property = property;
		}
		
		@Override
		public void modelToView( )
		{
			if( model == null )
			{
				return;
			}
			Boolean b = model.get( property );
			if( b != null )
			{
				button.setSelected( b );
			}
		}
		
		@Override
		public void viewToModel( )
		{
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
		public Object getProperty( )
		{
			return property;
		}
		
		@Override
		public void registerWithView( )
		{
			button.addItemListener( this );
		}
	}
	
	public <K> RadioButtonBinding<K> bind( AbstractButton button , Attribute<K> property , K key )
	{
		RadioButtonBinding<K> binding = new RadioButtonBinding<K>( button , property , key );
		bind( binding );
		return binding;
	}
	
	public class RadioButtonBinding<K> implements Binding , ItemListener
	{
		AbstractButton	button;
		Attribute<K>	property;
		K				key;
		
		public RadioButtonBinding( AbstractButton button , Attribute<K> property , K key )
		{
			this.button = button;
			this.property = property;
			this.key = key;
		}
		
		@Override
		public void modelToView( )
		{
			if( model == null )
			{
				return;
			}
			K key = model.get( property );
			if( key == this.key )
			{
				button.setSelected( true );
			}
		}
		
		@Override
		public void viewToModel( )
		{
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
		public Object getProperty( )
		{
			return property;
		}
		
		@Override
		public void registerWithView( )
		{
			button.addItemListener( this );
		}
	}
	
	public JTextComponentBinding bind( JTextComponent textComp , Attribute<String> property )
	{
		JTextComponentBinding binding = new JTextComponentBinding( textComp , property );
		bind( binding );
		return binding;
	}
	
	public class JTextComponentBinding implements Binding , DocumentListener
	{
		JTextComponent		textComp;
		Attribute<String>	property;
		
		public JTextComponentBinding( JTextComponent textComp , Attribute<String> property )
		{
			this.textComp = textComp;
			this.property = property;
		}
		
		@Override
		public void modelToView( )
		{
			if( model == null )
			{
				return;
			}
			textComp.setText( model.get( property ) );
		}
		
		@Override
		public void viewToModel( )
		{
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
		public Object getProperty( )
		{
			return property;
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
	
	public JFormattedTextFieldBinding bind( JFormattedTextField textComp , Attribute property )
	{
		JFormattedTextFieldBinding binding = new JFormattedTextFieldBinding( textComp , property );
		bind( binding );
		return binding;
	}
	
	public class JFormattedTextFieldBinding implements Binding , PropertyChangeListener
	{
		JFormattedTextField	textComp;
		Attribute			property;
		
		public JFormattedTextFieldBinding( JFormattedTextField textComp , Attribute property )
		{
			this.textComp = textComp;
			this.property = property;
		}
		
		@Override
		public void modelToView( )
		{
			if( model == null )
			{
				return;
			}
			textComp.setValue( model.get( property ) );
		}
		
		@Override
		public void viewToModel( )
		{
			if( model == null )
			{
				return;
			}
			model.set( property , textComp.getValue( ) );
		}
		
		@Override
		public void unregisterFromView( )
		{
			textComp.removePropertyChangeListener( "value", this );
		}
		
		@Override
		public Object getProperty( )
		{
			return property;
		}
		
		@Override
		public void registerWithView( )
		{
			textComp.addPropertyChangeListener( "value", this );
		}
		
		@Override
		public void propertyChange( PropertyChangeEvent evt )
		{
			viewToModel( );
		}
	}
	
	public JSpinnerBinding bind( JSpinner spinner , Attribute property )
	{
		JSpinnerBinding binding = new JSpinnerBinding( spinner , property );
		bind( binding );
		return binding;
	}
	
	public class JSpinnerBinding implements Binding , ChangeListener
	{
		JSpinner	spinner;
		Attribute	property;
		
		public JSpinnerBinding( JSpinner spinner , Attribute property )
		{
			this.spinner = spinner;
			this.property = property;
		}
		
		@Override
		public void modelToView( )
		{
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
		public void viewToModel( )
		{
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
		public Object getProperty( )
		{
			return property;
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
}

package org.andork.event;

import java.util.Collection;
import java.util.Map;

import org.andork.collect.CollectionUtils;
import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.model.Model;

public class Binder<M extends Model>
{
	protected boolean							ignoreChanges	= false;
	
	protected M									model;
	
	protected final MultiMap<Object, Binding>	bindings		= LinkedHashSetMultiMap.newInstance( );
	
	protected final Map<Object, Binder>			subBinders		= CollectionUtils.newLinkedHashMap( );
	
	protected final ChangeHandler				changeHandler	= new ChangeHandler( );
	
	public M getModel( )
	{
		return model;
	}
	
	public void setModel( M model )
	{
		if( this.model != model )
		{
			if( this.model != null )
			{
				this.model.changeSupport( ).removePropertyChangeListener( changeHandler );
			}
			this.model = model;
			for( Map.Entry<Object, Binder> entry : subBinders.entrySet( ) )
			{
				updateSubBinderModel( entry.getKey( ) , entry.getValue( ) );
			}
			if( model != null )
			{
				model.changeSupport( ).addPropertyChangeListener( changeHandler );
			}
		}
	}
	
	public void bind( Binding binding )
	{
		if( binding.binder != null )
		{
			throw new IllegalArgumentException( "binding is already bound in a binder" );
		}
		if( bindings.put( binding.getProperty( ) , binding ) )
		{
			binding.binder = this;
			binding.registerWithView( );
		}
	}
	
	public OtherModelBinding bind( Model otherModel , Object property )
	{
		OtherModelBinding binding = new OtherModelBinding( property , otherModel );
		bind( binding );
		return binding;
	}
	
	public void unbind( Binding binding )
	{
		if( bindings.remove( binding.getProperty( ) , binding ) )
		{
			binding.binder = null;
			binding.unregisterFromView( );
		}
	}
	
	public void unbindAll( Object property )
	{
		for( Binding binding : bindings.get( property ) )
		{
			binding.unregisterFromView( );
		}
		bindings.removeAll( property , bindings.get( property ) );
	}
	
	public Binder subBinder( Object property )
	{
		Binder subBinder = subBinders.get( property );
		if( subBinder == null )
		{
			subBinder = new Binder( );
			updateSubBinderModel( property , subBinder );
			subBinders.put( property , subBinder );
		}
		return subBinder;
	}
	
	private void updateSubBinderModel( Object property , Binder subBinder )
	{
		if( model == null )
		{
			return;
		}
		Object value = model.get( property );
		if( value instanceof Model )
		{
			subBinder.setModel( ( Model ) value );
		}
		else
		{
			subBinder.setModel( null );
		}
	}
	
	public void modelToView( )
	{
		ignoreChanges = true;
		try
		{
			for( Map.Entry<Object, Binding> entry : bindings.entrySet( ) )
			{
				( ( Binding ) entry.getValue( ) ).modelToView( );
			}
			
			for( Binder subBinder : subBinders.values( ) )
			{
				subBinder.modelToView( );
			}
		}
		finally
		{
			ignoreChanges = false;
		}
	}
	
	public void modelToView( Object property )
	{
		boolean prevIgnoreChanges = ignoreChanges;
		ignoreChanges = true;
		try
		{
			for( Binding binding : bindings.get( property ) )
			{
				binding.modelToView( );
			}
			
			Binder subBinder = subBinders.get( property );
			if( subBinder != null )
			{
				subBinder.modelToView( );
			}
		}
		finally
		{
			ignoreChanges = prevIgnoreChanges;
		}
	}
	
	public void modelToView( Collection<Object> properties )
	{
		ignoreChanges = true;
		try
		{
			for( Object property : properties )
			{
				modelToView( property );
			}
		}
		finally
		{
			ignoreChanges = false;
		}
	}
	
	public void viewToModel( )
	{
		ignoreChanges = true;
		try
		{
			for( Map.Entry<Object, Binding> entry : bindings.entrySet( ) )
			{
				( ( Binding ) entry.getValue( ) ).viewToModel( );
			}
			
			for( Binder subBinder : subBinders.values( ) )
			{
				subBinder.viewToModel( );
			}
		}
		finally
		{
			ignoreChanges = false;
		}
	}
	
	public void viewToModel( Object property )
	{
		boolean prevIgnoreChanges = ignoreChanges;
		ignoreChanges = true;
		try
		{
			for( Binding binding : bindings.get( property ) )
			{
				binding.viewToModel( );
			}
			
			Binder subBinder = subBinders.get( property );
			if( subBinder != null )
			{
				subBinder.viewToModel( );
			}
		}
		finally
		{
			ignoreChanges = prevIgnoreChanges;
		}
	}
	
	public void viewToModel( Collection<Object> properties )
	{
		ignoreChanges = true;
		try
		{
			for( Object property : properties )
			{
				viewToModel( property );
			}
		}
		finally
		{
			ignoreChanges = false;
		}
	}
	
	public static abstract class Binding
	{
		boolean				updating;
		public final Object	property;
		private Binder<?>	binder;
		
		public Binding( Object property )
		{
			this.property = property;
		}
		
		public Model getModel( )
		{
			return binder == null ? null : binder.getModel( );
		}
		
		public Object getProperty( )
		{
			return property;
		}
		
		protected abstract void modelToViewImpl( );
		
		protected abstract void viewToModelImpl( );
		
		public final void modelToView( )
		{
			if( updating )
			{
				return;
			}
			updating = true;
			try
			{
				modelToViewImpl( );
			}
			finally
			{
				updating = false;
			}
		}
		
		public final void viewToModel( )
		{
			if( updating )
			{
				return;
			}
			updating = true;
			try
			{
				viewToModelImpl( );
			}
			finally
			{
				updating = false;
			}
		}
		
		public abstract void registerWithView( );
		
		public abstract void unregisterFromView( );
	}
	
	public static abstract class BindingAdapter extends Binding
	{
		public BindingAdapter( Object property )
		{
			super( property );
		}
		
		public void modelToViewImpl( )
		{
			
		}
		
		public void viewToModelImpl( )
		{
			
		}
		
		public void registerWithView( )
		{
			
		}
		
		public void unregisterFromView( )
		{
			
		}
	}
	
	public static class OtherModelBinding extends Binding implements HierarchicalBasicPropertyChangeListener
	{
		Model	otherModel;
		
		public OtherModelBinding( Object property , Model otherModel )
		{
			super( property );
			this.otherModel = otherModel;
		}
		
		@Override
		public void modelToViewImpl( )
		{
			Model model = getModel( );
			if( model != null )
			{
				otherModel.set( property , model.get( property ) );
			}
		}
		
		@Override
		public void viewToModelImpl( )
		{
			Model model = getModel( );
			if( model != null )
			{
				model.set( property , otherModel.get( property ) );
			}
		}
		
		@Override
		public void registerWithView( )
		{
			otherModel.changeSupport( ).addPropertyChangeListener( this );
		}
		
		@Override
		public void unregisterFromView( )
		{
			otherModel.changeSupport( ).removePropertyChangeListener( this );
		}
		
		@Override
		public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
		{
			if( source == otherModel && property == this.property )
			{
				viewToModel( );
			}
		}
		
		@Override
		public void childrenChanged( Object source , ChangeType changeType , Object ... children )
		{
		}
	}
	
	protected class ChangeHandler implements HierarchicalBasicPropertyChangeListener
	{
		@Override
		public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
		{
			if( ignoreChanges || source != model )
			{
				return;
			}
			
			Binder subBinder = subBinders.get( property );
			if( subBinder != null )
			{
				updateSubBinderModel( property , subBinder );
			}
			
			modelToView( property );
		}
		
		@Override
		public void childrenChanged( Object source , ChangeType changeType , Object ... children )
		{
			
		}
	}
}

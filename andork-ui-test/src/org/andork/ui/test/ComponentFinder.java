package org.andork.ui.test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.andork.awt.CheckEDT;
import org.andork.collect.ArrayIterator;
import org.andork.collect.CollectionUtils;
import org.andork.collect.CompoundComparator;
import org.andork.collect.EasyIterator;
import org.andork.collect.FilteringIterator;
import org.andork.collect.InverseComparator;
import org.andork.swing.DoSwing;
import org.andork.swing.DoSwingR;
import org.andork.swing.DoSwingR2;
import org.andork.ui.test.ConfigurableStringComparator.Option;
import org.andork.util.Java7;

/**
 * Helps you find components easily (and legibly) for automated GUI testing. ComponentFinder is like a nested iterator chain; it iterates over components, and
 * fluent methods wrap it in other ComponentFinders that filter out undesired results.<br>
 * <br>
 * To get a ComponentFinder, use {@link #only(Component)} or {@link #windows()}. Then chain filter methods (e.g. {@link #ofType(Class)}) onto that call, and
 * finally get the results with {@link #next()}.
 * 
 * @author andy.edwards
 * 
 * @param <C>
 *            the type of component to find
 */
public abstract class ComponentFinder<C extends Component> implements Iterable<C>
{
	protected ComponentFinder( )
	{
	}
	
	/**
	 * Gets the first component found. If no component was found an exception will be thrown. NOTE: This method blocks waiting for the AWT event dispatch
	 * thread.
	 * 
	 * @return the first component found.
	 * @throws NoSuchElementException
	 *             if no components were found.
	 */
	public C first( )
	{
		return new DoSwingR<C>( )
		{
			public void run( )
			{
				result = iterator( ).next( );
			}
		}.result;
	}
	
	/**
	 * Gets the first component found, or {@code null} if no components were found.NOTE: This method blocks waiting for the AWT event dispatch thread.
	 * 
	 * @return the first component found, or {@code null} if no components were found.
	 */
	public C firstOrNull( )
	{
		return new DoSwingR<C>( )
		{
			public void run( )
			{
				Iterator<C> i = iterator( );
				if( i.hasNext( ) )
				{
					result = i.next( );
				}
			}
		}.result;
	}
	
	/**
	 * Gets the only component found. If there are more or less than one components an exception will be thrown. NOTE: This method blocks waiting for the AWT
	 * event dispatch thread.
	 * 
	 * @return the only component found.
	 * @throws NoSuchElementException
	 *             if no components were found.
	 * @throws IllegalStateException
	 *             if more than one component was found.
	 */
	public C only( )
	{
		return new DoSwingR<C>( )
		{
			public void run( )
			{
				Iterator<C> i = iterator( );
				result = i.next( );
				if( i.hasNext( ) )
				{
					throw new IllegalStateException( "More than one component was found" );
				}
			}
		}.result;
	}
	
	/**
	 * Gets the <b>i</b>th component found. NOTE: This method blocks waiting for the AWT event dispatch thread.
	 * 
	 * @param i
	 *            the index of the component to get
	 * @return the {@code i}'th component found.
	 * @throws IndexOutOfBoundsException
	 *             if i is >= the number of components found.
	 */
	public C get( final int i )
	{
		return new DoSwingR2<C>( )
		{
			public C doRun( )
			{
				int k = 0;
				for( C comp : ComponentFinder.this )
				{
					if( k++ == i )
					{
						return comp;
					}
				}
				throw new IndexOutOfBoundsException( "Not enough components found for index: " + i );
			}
		}.result( );
	}
	
	/**
	 * Adds all components found to the given {@link Collection}. NOTE: this method blocks waiting for the AWT event dispatch thread.
	 * 
	 * @param collection
	 *            the collection to add the found components to.
	 */
	public void addAllTo( final Collection<? super C> collection )
	{
		new DoSwing( )
		{
			public void run( )
			{
				CollectionUtils.addAll( collection , ComponentFinder.this );
			}
		};
	}
	
	/**
	 * Gets a list of all components found. NOTE: this method blocks waiting for the AWT event dispatch thread.
	 * 
	 * @param collection
	 *            the collection to add the found components to.
	 * 
	 * @return a {@link List} of all components found.
	 */
	public List<C> all( )
	{
		ArrayList<C> result = new ArrayList<C>( );
		addAllTo( result );
		return result;
	}
	
	/**
	 * Blocks the current thread (which must not be the AWT event dispatch thread) until at least one component is found. However, the component may cease to
	 * exist before/after this method returns.
	 * 
	 * @return this {@link ComponentFinder}, for chaining.
	 * @throws MustNotBeCalledOnEDTException
	 *             if this method was called on the EDT.
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting for the component to be found.
	 */
	public ComponentFinder<C> waitUntilFound( ) throws InterruptedException
	{
		CheckEDT.checkNotEDT( );
		while( !isAnyFound( ) )
		{
			Thread.sleep( 100 );
		}
		return this;
	}
	
	/**
	 * Blocks the current thread (which must not be the AWT event dispatch thread) until no components are found. However, new components that would be found
	 * may come into existence before/after this method returns.
	 * 
	 * @return this {@link ComponentFinder}, for chaining.
	 * @throws MustNotBeCalledOnEDTException
	 *             if this method was called on the EDT.
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting for no components to be found.
	 */
	public ComponentFinder<C> waitUntilNotFound( ) throws InterruptedException
	{
		CheckEDT.checkNotEDT( );
		while( isAnyFound( ) )
		{
			Thread.sleep( 100 );
		}
		return this;
	}
	
	/**
	 * Determines if any components are found. NOTE: this method blocks waiting for the AWT event dispatch thread.
	 * 
	 * @return {@code true} if any components are found, {@code false} otherwise.
	 */
	public boolean isAnyFound( )
	{
		return new DoSwingR<Boolean>( )
		{
			public void run( )
			{
				result = iterator( ).hasNext( );
			}
		}.result;
	}
	
	/**
	 * Prints all components found to {@link System#out}.
	 */
	public void printAll( )
	{
		printAll( System.out );
	}
	
	/**
	 * Prints all components found to the given {@link PrintStream} on successive lines.
	 * 
	 * @param out
	 *            the {@code PrintStream} to print to.
	 */
	public void printAll( final PrintStream out )
	{
		new DoSwing( )
		{
			public void run( )
			{
				for( Component next : ComponentFinder.this )
				{
					out.println( next );
				}
			}
		};
	}
	
	/**
	 * A {@code ComponentFinder} that finds one component (or none). It provides a {@link #get()} method to get the component.
	 * 
	 * @author andy.edwards
	 * @param <C>
	 *            the component type being found
	 */
	public static class SingletonComponentFinder<C extends Component> extends ComponentFinder<C>
	{
		private C	comp;
		
		protected SingletonComponentFinder( C comp )
		{
			this.comp = comp;
		}
		
		public C get( )
		{
			return comp;
		}
		
		public Iterator<C> iterator( )
		{
			return new EasyIterator<C>( )
			{
				C	next	= comp;
				
				@Override
				protected C nextOrNull( )
				{
					C next = this.next;
					this.next = null;
					return next;
				}
			};
		}
	}
	
	private static class AncestorFinder extends ComponentFinder<Component>
	{
		private ComponentFinder<?>	wrapped;
		
		public AncestorFinder( ComponentFinder<?> wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}
		
		public Iterator<Component> iterator( )
		{
			return new EasyIterator<Component>( )
			{
				private Component						next		= null;
				private Iterator<? extends Component>	wrappedIter	= wrapped.iterator( );
				
				@Override
				protected Component nextOrNull( )
				{
					while( next == null && wrappedIter.hasNext( ) )
					{
						next = wrappedIter.next( ).getParent( );
					}
					
					Component result = next;
					
					if( next != null )
					{
						next = next.getParent( );
					}
					
					return result;
				}
			};
		}
	}
	
	private static class DescendantFinder extends ComponentFinder<Component>
	{
		private ComponentFinder<?>	wrapped;
		
		public DescendantFinder( ComponentFinder<?> wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}
		
		public Iterator<Component> iterator( )
		{
			return new EasyIterator<Component>( )
			{
				private final Set<Component>			visited		= new HashSet<Component>( );
				private final Queue<Component>			queue		= new LinkedList<Component>( );
				
				private Iterator<? extends Component>	wrappedIter	= wrapped.iterator( );
				
				@Override
				protected Component nextOrNull( )
				{
					if( queue.isEmpty( ) && wrappedIter.hasNext( ) )
					{
						Component next = wrappedIter.next( );
						if( next instanceof Container )
						{
							for( Component comp : ( ( Container ) next ).getComponents( ) )
							{
								if( visited.add( comp ) )
								{
									queue.add( comp );
								}
							}
						}
					}
					
					Component result = queue.poll( );
					
					if( result instanceof Container )
					{
						for( Component comp : ( ( Container ) result ).getComponents( ) )
						{
							if( visited.add( comp ) )
							{
								queue.add( comp );
							}
						}
					}
					
					return result;
				}
			};
		}
	}
	
	public static abstract class FilteringComponentFinder<C extends Component> extends ComponentFinder<C>
	{
		private final ComponentFinder<C>	wrapped;
		
		protected FilteringComponentFinder( ComponentFinder<C> wrapped )
		{
			super( );
			this.wrapped = wrapped;
		}
		
		public abstract boolean matches( C comp );
		
		public Iterator<C> iterator( )
		{
			return new FilteringIterator<C>( wrapped.iterator( ) )
			{
				@Override
				protected boolean matches( C next )
				{
					return FilteringComponentFinder.this.matches( next );
				}
			};
		}
	}
	
	/**
	 * A {@code ComponentFinder} that finds {@link Window}s. It provides some additional methods to filter out certain windows.
	 * 
	 * @author andy.edwards
	 * 
	 * @param <W>
	 *            the type of window to find.
	 */
	public static abstract class WindowFinder<W extends Window> extends ComponentFinder<W>
	{
		protected WindowFinder( )
		{
			
		}
		
		/**
		 * Wraps the given {@code ComponentFinder} with a new {@code WindowFinder}, enabling you to use {@code WindowFinder}'s filtering methods.
		 * 
		 * @param componentFinder
		 *            the {@code ComponentFinder} to wrap.
		 * @return a new {@code WindowFinder} wrapping {@code componentFinder}.
		 */
		public static <W extends Window> WindowFinder<W> decorate( final ComponentFinder<W> componentFinder )
		{
			return new WindowFinder<W>( )
			{
				public Iterator<W> iterator( )
				{
					return componentFinder.iterator( );
				}
			};
		}
		
		/**
		 * @return a {@code WindowFinder} that narrows the results down to owned windows.
		 */
		public WindowFinder<W> owned( )
		{
			return decorate( new FilteringComponentFinder<W>( this )
			{
				@Override
				public boolean matches( W comp )
				{
					return comp.getOwner( ) != null;
				}
			} );
		}
		
		/**
		 * @return a {@code WindowFinder} that narrows the results down to ownerless windows.
		 */
		public WindowFinder<W> ownerless( )
		{
			return decorate( new FilteringComponentFinder<W>( this )
			{
				@Override
				public boolean matches( W comp )
				{
					return comp.getOwner( ) == null;
				}
			} );
		}
		
		@Override
		public WindowFinder<W> waitUntilFound( ) throws InterruptedException
		{
			super.waitUntilFound( );
			return this;
		}
	}
	
	private static class OwnedWindowFinder extends WindowFinder<Window>
	{
		private final Window	target;
		
		private OwnedWindowFinder( Window target )
		{
			super( );
			this.target = target;
		}
		
		public Iterator<Window> iterator( )
		{
			return new ArrayIterator<Window>( target.getOwnedWindows( ) );
		}
	}
	
	public static WindowFinder<Window> windowsOwnedBy( Window window )
	{
		return new OwnedWindowFinder( window );
	}
	
	public static <C extends Component> SingletonComponentFinder<C> only( final C comp )
	{
		return new SingletonComponentFinder<C>( comp );
	}
	
	/**
	 * @param comp
	 *            the component to find ancestors of.
	 * @return a {@code ComponentFinder} that finds all of {@code comp}'s{ ancestors.
	 */
	public static ComponentFinder<?> ancestorsOf( Component comp )
	{
		return only( comp ).ancestors( );
	}
	
	/**
	 * @return a {@code ComponentFinder} that finds all of this {@code ComponentFinder}'s components' ancestors.
	 */
	public ComponentFinder<?> ancestors( )
	{
		return new AncestorFinder( this );
	}
	
	/**
	 * @param comp
	 *            the component to find descendants of.
	 * @return a {@code ComponentFinder} that finds all of {@code comp}'s descendants.
	 */
	public static ComponentFinder<?> descendantsOf( Component comp )
	{
		return only( comp ).descendants( );
	}
	
	/**
	 * @return a {@code ComponentFinder} that finds all of this {@code ComponentFinder}'s components' descendants.
	 */
	public ComponentFinder<?> descendants( )
	{
		return new DescendantFinder( this );
	}
	
	/**
	 * @param cls
	 *            the type to match.
	 * @return a {@code ComponentFinder} that narrows the results to components of the given type.
	 */
	@SuppressWarnings( { "rawtypes" , "unchecked" } )
	public <D extends Component> ComponentFinder<D> ofType( final Class<D> cls )
	{
		return new FilteringComponentFinder( this )
		{
			@Override
			public boolean matches( Component comp )
			{
				return cls.isInstance( comp );
			}
		};
	}
	
	public ComponentFinder<C> ofType2( final Class<?> cls )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( Component comp )
			{
				return cls.isInstance( comp );
			}
		};
	}
	
	public static ComponentFinder<JLabel> jlabelsIn( Component parent )
	{
		return descendantsOf( parent ).ofType( JLabel.class );
	}
	
	public static ComponentFinder<JTextComponent> textComponentsIn( Component parent )
	{
		return descendantsOf( parent ).ofType( JTextComponent.class );
	}
	
	public static ComponentFinder<AbstractButton> buttonsIn( Component parent )
	{
		return descendantsOf( parent ).ofType( AbstractButton.class );
	}
	
	public static ComponentFinder<JRadioButton> radioButtonsIn( Component parent )
	{
		return descendantsOf( parent ).ofType( JRadioButton.class );
	}
	
	public static ComponentFinder<JCheckBox> checkBoxesIn( Component parent )
	{
		return descendantsOf( parent ).ofType( JCheckBox.class );
	}
	
	public static ComponentFinder<JComboBox> comboBoxesIn( Component parent )
	{
		return descendantsOf( parent ).ofType( JComboBox.class );
	}
	
	public static TableFinder<JTable> tablesIn( Component parent )
	{
		return descendantsOf( parent ).tables( );
	}
	
	public TableFinder<JTable> tables( )
	{
		return TableFinder.cast( ofType( JTable.class ) );
	}
	
	/**
	 * Excludes the given component from the results.
	 * 
	 * @param toExclude
	 *            the component to exclude.
	 * @return a {@link ComponentFinder} that excludes the given component from the results.
	 */
	public ComponentFinder<C> exclude( final Component toExclude )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return comp != toExclude;
			}
		};
	}
	
	/**
	 * Excludes given components from the results.
	 * 
	 * @param toExclude
	 *            the set of components to exclude.
	 * @return a {@link ComponentFinder} that excludes the given components from the results.
	 */
	public ComponentFinder<C> exclude( final Set<? super C> toExclude )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return !toExclude.contains( comp );
			}
		};
	}
	
	/**
	 * @return a {@code ComponentFinder} that narrows the results to {@link Component#isEnabled() enabled} components.
	 */
	public ComponentFinder<C> enabled( )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return comp.isEnabled( );
			}
		};
	}
	
	/**
	 * @return a {@code ComponentFinder} that narrows the results to {@link Component#isEnabled() disabled} components.
	 */
	public ComponentFinder<C> disabled( )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return !comp.isEnabled( );
			}
		};
	}
	
	/**
	 * @return a {@code ComponentFinder} that narrows the results to {@link Component#isShowing() showing} components.
	 */
	public ComponentFinder<C> showing( )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return comp.isShowing( );
			}
		};
	}
	
	/**
	 * @return a {@code ComponentFinder} that narrows the results to the {@link Component#isShowing() focused} component.
	 */
	public ComponentFinder<C> withFocus( )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return comp.hasFocus( );
			}
		};
	}
	
	/**
	 * @param name
	 *            the name to match (may be null).
	 * @return a {@code ComponentFinder} that narrows the results to components with the given name.
	 */
	public ComponentFinder<C> named( final String name )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return Java7.Objects.equals( name , comp.getName( ) );
			}
		};
	}
	
	/**
	 * @param regex
	 *            a regular expression to match component names.
	 * @return a {@code ComponentFinder} that narrows the results to components that have a non-null {@link Component#getName() name} matching the given regular
	 *         expression.
	 */
	public ComponentFinder<C> withNameMatching( final String regex )
	{
		return new FilteringComponentFinder<C>( this )
		{
			Pattern	pattern	= Pattern.compile( regex );
			
			@Override
			public boolean matches( C comp )
			{
				return comp.getName( ) != null && pattern.matcher( comp.getName( ) ).matches( );
			}
		};
	};
	
	/**
	 * @param regex
	 *            a regular expression to match component names.
	 * @param flags
	 *            flags for {@link Pattern#compile(String, int)}.
	 * @return a {@code ComponentFinder} that narrows the results to components that have a non-null {@link Component#getName() name} matching the given regular
	 *         expression.
	 */
	public ComponentFinder<C> withNameMatching( final String regex , final int flags )
	{
		return new FilteringComponentFinder<C>( this )
		{
			Pattern	pattern	= Pattern.compile( regex , flags );
			
			@Override
			public boolean matches( C comp )
			{
				return comp.getName( ) != null && pattern.matcher( comp.getName( ) ).matches( );
			}
		};
	};
	
	private static boolean hasText( Component comp )
	{
		return comp instanceof AbstractButton || comp instanceof JLabel || comp instanceof JTextComponent;
	}
	
	private static String getText( Component comp )
	{
		if( comp instanceof AbstractButton )
		{
			return ( ( AbstractButton ) comp ).getText( );
		}
		else if( comp instanceof JLabel )
		{
			return ( ( JLabel ) comp ).getText( );
		}
		else if( comp instanceof JTextComponent )
		{
			return ( ( JTextComponent ) comp ).getText( );
		}
		throw new IllegalArgumentException( comp.getClass( ) + " components don't have text" );
	}
	
	/**
	 * @param text
	 *            a text to match (may be null).
	 * @return a {@code ComponentFinder} that narrows the results to components with the given text.
	 */
	public ComponentFinder<C> withText( final String text )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return hasText( comp ) && Java7.Objects.equals( text , getText( comp ) );
			}
		};
	}
	
	/**
	 * @param text
	 *            a text to match (may be null).
	 * @param options
	 *            the {@link Option}s for string comparison.
	 * @return a {@code ComponentFinder} that narrows the results to components with the given text.
	 */
	public ComponentFinder<C> withText( final String text , ConfigurableStringComparator.Option ... options )
	{
		final ConfigurableStringComparator comparator = new ConfigurableStringComparator( options );
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				return hasText( comp ) && comparator.equals( text , getText( comp ) );
			}
		};
	}
	
	/**
	 * @param text
	 *            a text to match (may be null).
	 * @return a {@code ComponentFinder} that narrows the results to components containing the given text.
	 */
	public ComponentFinder<C> containingText( final String text )
	{
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				if( hasText( comp ) )
				{
					String compText = getText( comp );
					if( compText != null )
					{
						return compText.contains( text );
					}
				}
				return false;
			}
		};
	}
	
	/**
	 * @param text
	 *            a text to match (may be null).
	 * @param options
	 *            the {@link Option}s for string comparison.
	 * @return a {@code ComponentFinder} that narrows the results to components containing the given text.
	 */
	public ComponentFinder<C> containingText( final String text , ConfigurableStringComparator.Option ... options )
	{
		final ConfigurableStringComparator comparator = new ConfigurableStringComparator( options );
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C comp )
			{
				if( hasText( comp ) )
				{
					String compText = getText( comp );
					if( compText != null )
					{
						return comparator.contains( compText , text );
					}
				}
				return false;
			}
		};
	}
	
	/**
	 * @param regex
	 *            a regular expression to match component text.
	 * @return a {@code ComponentFinder} that narrows the results to components that have a non-null text matching the given regular expression.
	 */
	public ComponentFinder<C> withTextMatching( final String regex )
	{
		return new FilteringComponentFinder<C>( this )
		{
			Pattern	pattern	= Pattern.compile( regex );
			
			@Override
			public boolean matches( C comp )
			{
				return hasText( comp ) && getText( comp ) != null && pattern.matcher( getText( comp ) ).matches( );
			}
		};
	}
	
	public ComponentFinder<C> above( final Component c1 )
	{
		final Rectangle r1 = c1.getBounds( );
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C c2 )
			{
				Rectangle r2 = c2.getBounds( );
				r2 = SwingUtilities.convertRectangle( c2 , r2 , c1 );
				
				return r2.getMaxY( ) < r1.y;
			}
		};
	}
	
	public ComponentFinder<C> below( final Component c1 )
	{
		final Rectangle r1 = c1.getBounds( );
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C c2 )
			{
				Rectangle r2 = c2.getBounds( );
				r2 = SwingUtilities.convertRectangle( c2 , r2 , c1 );
				
				return r2.y > r1.getMaxY( );
			}
		};
	}
	
	public ComponentFinder<C> leftOf( final Component c1 )
	{
		final Rectangle r1 = c1.getBounds( );
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C c2 )
			{
				Rectangle r2 = c2.getBounds( );
				r2 = SwingUtilities.convertRectangle( c2 , r2 , c1 );
				
				return r2.getMaxX( ) < r1.x;
			}
		};
	}
	
	public ComponentFinder<C> rightOf( final Component c1 )
	{
		final Rectangle r1 = c1.getBounds( );
		return new FilteringComponentFinder<C>( this )
		{
			@Override
			public boolean matches( C c2 )
			{
				Rectangle r2 = c2.getBounds( );
				r2 = SwingUtilities.convertRectangle( c2 , r2 , c1 );
				
				return r2.x > r1.getMaxX( );
			}
		};
	}
	
	/**
	 * @param regex
	 *            a regular expression to match component text.
	 * @param flags
	 *            flags for {@link Pattern#compile(String, int)}.
	 * @return a {@code ComponentFinder} that narrows the results to components that have a non-null text matching the given regular expression.
	 */
	public ComponentFinder<C> withTextMatching( final String regex , final int flags )
	{
		return new FilteringComponentFinder<C>( this )
		{
			Pattern	pattern	= Pattern.compile( regex , flags );
			
			@Override
			public boolean matches( C comp )
			{
				return hasText( comp ) && getText( comp ) != null && pattern.matcher( getText( comp ) ).matches( );
			}
		};
	}
	
	public static class SortingComponentFinder<C extends Component> extends ComponentFinder<C>
	{
		ComponentFinder<C>		parent;
		Comparator<? super C>	comparator;
		
		public SortingComponentFinder( ComponentFinder<C> parent , Comparator<? super C> comparator )
		{
			super( );
			this.parent = parent;
			this.comparator = comparator;
		}
		
		@Override
		public Iterator<C> iterator( )
		{
			return CollectionUtils.toSortedArrayList( parent , comparator ).iterator( );
		}
	}
	
	/**
	 * @param comparator
	 *            the comparator to sort with.
	 * @return a {@code ComponentFinder} that sorts the results to using {@code comparator}. If this {@code ComponentFinder} is also a
	 *         {@code SortingComponentFinder}, the result will sub-sort the results of this sort.
	 */
	public SortingComponentFinder<C> sort( Comparator<? super C> comparator )
	{
		if( this instanceof SortingComponentFinder )
		{
			SortingComponentFinder<C> thisSorting = ( SortingComponentFinder<C> ) this;
			return new SortingComponentFinder<C>( thisSorting.parent , new CompoundComparator(
					thisSorting.comparator , comparator ) );
		}
		else
		{
			return new SortingComponentFinder<C>( this , comparator );
		}
	}
	
	public static class ComponentCenterDistanceComparator implements Comparator<Component>
	{
		Component	referenceComp;
		int			x , y;
		
		protected ComponentCenterDistanceComparator( Component referenceComp , int x , int y )
		{
			super( );
			this.referenceComp = referenceComp;
			this.x = x;
			this.y = y;
		}
		
		public int compare( Component o1 , Component o2 )
		{
			Rectangle r1 = o1 instanceof JComponent ?
					( ( JComponent ) o1 ).getVisibleRect( ) : SwingUtilities.getLocalBounds( o1 );
			Rectangle r2 = o2 instanceof JComponent ?
					( ( JComponent ) o2 ).getVisibleRect( ) : SwingUtilities.getLocalBounds( o2 );
			r1 = SwingUtilities.convertRectangle( o1 , r1 , referenceComp );
			r2 = SwingUtilities.convertRectangle( o2 , r2 , referenceComp );
			int dx1 = r1.x + r1.width / 2 - x;
			int dy1 = r1.y + r1.height / 2 - y;
			int dx2 = r2.x + r2.width / 2 - x;
			int dy2 = r2.y + r2.height / 2 - y;
			int d1 = dx1 * dx1 + dy1 * dy1;
			int d2 = dx2 * dx2 + dy2 * dy2;
			int result = d1 - d2;
			if( result == 0 )
			{
				result = r1.x - r2.x;
			}
			if( result == 0 )
			{
				result = r1.y - r2.y;
			}
			return result;
		}
	}
	
	/**
	 * @param referenceComp
	 *            the component to find the component closest to.
	 * @return a {@code SingletonComponentFinder} that narrows the results to the component that whose center is closest to {@code comp}'s center, or
	 *         {@code null} if this {@code ComponentFinder} has no results.
	 */
	private ComponentFinder<C> closestTo( final Component referenceComp , final int x , final int y )
	{
		return sort( new ComponentCenterDistanceComparator( referenceComp , x , y ) );
	}
	
	public ComponentFinder<C> closestTo( Component center )
	{
		return exclude( center ).closestTo( center , center.getWidth( ) / 2 , center.getHeight( ) / 2 );
	}
	
	private ComponentFinder<C> closestToX( final Component referenceComp , final int x )
	{
		return sort( new ComponentCenterXDistanceComparator( referenceComp , x ) );
	}
	
	public ComponentFinder<C> closestToX( Component center )
	{
		return exclude( center ).closestToX( center , center.getWidth( ) / 2 );
	}
	
	public static class ComponentCenterXDistanceComparator implements Comparator<Component>
	{
		Component	referenceComp;
		int			x;
		
		protected ComponentCenterXDistanceComparator( Component comp , int x )
		{
			super( );
			this.referenceComp = comp;
			this.x = x;
		}
		
		public int compare( Component o1 , Component o2 )
		{
			Rectangle r1 = SwingUtilities.getLocalBounds( o1 );
			Rectangle r2 = SwingUtilities.getLocalBounds( o2 );
			r1 = SwingUtilities.convertRectangle( o1 , r1 , referenceComp );
			r2 = SwingUtilities.convertRectangle( o2 , r2 , referenceComp );
			int result = Math.abs( r1.x + r1.width / 2 - x ) - Math.abs( r2.x + r2.width / 2 - x );
			return result == 0 ? r1.y - r2.y : result;
		}
	}
	
	private ComponentFinder<C> closestToY( final Component referenceComp , final int y )
	{
		return sort( new ComponentCenterYDistanceComparator( referenceComp , y ) );
	}
	
	public ComponentFinder<C> closestToY( Component center )
	{
		return exclude( center ).closestToY( center , center.getHeight( ) / 2 );
	}
	
	public static class ComponentCenterYDistanceComparator implements Comparator<Component>
	{
		Component	referenceComp;
		int			y;
		
		protected ComponentCenterYDistanceComparator( Component comp , int y )
		{
			super( );
			this.referenceComp = comp;
			this.y = y;
		}
		
		public int compare( Component o1 , Component o2 )
		{
			Rectangle r1 = SwingUtilities.getLocalBounds( o1 );
			Rectangle r2 = SwingUtilities.getLocalBounds( o2 );
			r1 = SwingUtilities.convertRectangle( o1 , r1 , referenceComp );
			r2 = SwingUtilities.convertRectangle( o2 , r2 , referenceComp );
			int result = Math.abs( r1.y + r1.height / 2 - y ) - Math.abs( r2.y + r2.height / 2 - y );
			return result == 0 ? r1.y - r2.y : result;
		}
	}
	
	public ComponentFinder<C> leftmost( )
	{
		return sort( new ComponentCenterXComparator( ) );
	}
	
	public ComponentFinder<C> rightmost( )
	{
		return sort( new InverseComparator<C>( new ComponentCenterXComparator( ) ) );
	}
	
	public static class ComponentCenterXComparator implements Comparator<Component>
	{
		protected ComponentCenterXComparator( )
		{
			super( );
		}
		
		public int compare( Component o1 , Component o2 )
		{
			Rectangle r1 = SwingUtilities.getLocalBounds( o1 );
			Rectangle r2 = SwingUtilities.getLocalBounds( o2 );
			r2 = SwingUtilities.convertRectangle( o2 , r2 , o1 );
			return r1.x - r2.x;
		}
	}
	
	public ComponentFinder<C> topmost( )
	{
		return sort( new ComponentCenterYComparator( ) );
	}
	
	public ComponentFinder<C> bottommost( )
	{
		return sort( new InverseComparator<C>( new ComponentCenterYComparator( ) ) );
	}
	
	public static class ComponentCenterYComparator implements Comparator<Component>
	{
		protected ComponentCenterYComparator( )
		{
			super( );
		}
		
		public int compare( Component o1 , Component o2 )
		{
			Rectangle r1 = SwingUtilities.getLocalBounds( o1 );
			Rectangle r2 = SwingUtilities.getLocalBounds( o2 );
			r2 = SwingUtilities.convertRectangle( o2 , r2 , o1 );
			return r1.y - r2.y;
		}
	}
}

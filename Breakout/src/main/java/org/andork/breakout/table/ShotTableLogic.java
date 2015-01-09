package org.andork.breakout.table;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.andork.bind2.Link;
import org.andork.q2.QObject;
import org.andork.q2.QObjectBinder;

public class ShotTableLogic
{
	private final QObjectBinder<DataDefaults>		dataDefaultsBinder	= new QObjectBinder<>(
																			DataDefaults.spec );

	private final Map<ShotColumnDef, FieldSetter>	setters;

	public ShotTableLogic( )
	{
		Map<ShotColumnDef, FieldSetter> settersMod = new HashMap<>( );

		settersMod.put( ShotColumnDef.vector , ( shot , newValue , pt ) ->
		{
			shot.vector = ( ShotVector ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Joint.class ).text = pt;
			return true;
		} );
		settersMod.put(
			ShotColumnDef.dist ,
			( shot , newValue , pt ) ->
			{
//			ensureVectorType( shot , ShotVector.Dai.class ).dist = ( Double ) newValue;
			setVectorField( shot , ShotVector.Dai.class , newValue ,
				( v , value ) -> v.dist = ( Double ) value );
			ensureVectorTextType( shot , ShotVectorText.Dai.class ).distText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.azmFsBs , ( shot , newValue , pt ) ->
		{
			ShotVector.Dai vector = ensureVectorType( shot , ShotVector.Dai.class );
			vector.azmFs = ( ( Double[ ] ) newValue )[ 0 ];
			vector.azmBs = ( ( Double[ ] ) newValue )[ 1 ];
			ensureVectorTextType( shot , ShotVectorText.Dai.PairedAngles.class ).azmFsBsText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.azmFs , ( shot , newValue , pt ) ->
		{
			ensureVectorType( shot , ShotVector.Dai.class ).azmFs = ( Double ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Dai.SplitAngles.class ).azmFsText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.azmBs , ( shot , newValue , pt ) ->
		{
			ensureVectorType( shot , ShotVector.Dai.class ).azmBs = ( Double ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Dai.SplitAngles.class ).azmBsText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.incFsBs , ( shot , newValue , pt ) ->
		{
			ShotVector.Dai vector = ensureVectorType( shot , ShotVector.Dai.class );
			vector.incFs = ( ( Double[ ] ) newValue )[ 0 ];
			vector.incBs = ( ( Double[ ] ) newValue )[ 1 ];
			ensureVectorTextType( shot , ShotVectorText.Dai.PairedAngles.class ).incFsBsText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.incFs , ( shot , newValue , pt ) ->
		{
			ensureVectorType( shot , ShotVector.Dai.class ).incFs = ( Double ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Dai.SplitAngles.class ).incFsText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.incBs , ( shot , newValue , pt ) ->
		{
			ensureVectorType( shot , ShotVector.Dai.class ).incBs = ( Double ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Dai.SplitAngles.class ).incBsText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.offsN , ( shot , newValue , pt ) ->
		{
			ensureVectorType( shot , ShotVector.Nev.class ).n = ( Double ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Nev.class ).nText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.offsE , ( shot , newValue , pt ) ->
		{
			ensureVectorType( shot , ShotVector.Nev.class ).e = ( Double ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Nev.class ).eText = pt;
			return true;
		} );
		settersMod.put( ShotColumnDef.offsV , ( shot , newValue , pt ) ->
		{
			ensureVectorType( shot , ShotVector.Nev.class ).v = ( Double ) newValue;
			ensureVectorTextType( shot , ShotVectorText.Nev.class ).vText = pt;
			return true;
		} );

		setters = Collections.unmodifiableMap( settersMod );
	}

	public Link<QObject<DataDefaults>> dataDefaultsLink( )
	{
		return dataDefaultsBinder.objLink;
	}

	private <T extends ShotVector> T ensureVectorType( Shot shot , Class<? extends T> cls )
	{
		if( shot.vector == null || !cls.isInstance( shot.vector ) )
		{
			try
			{
				if( Modifier.isAbstract( cls.getModifiers( ) ) )
				{
					if( cls.isAssignableFrom( ShotVector.Dai.class ) )
					{
						shot.vector = dataDefaultsBinder.get( ).get( DataDefaults.daiShotVector ).newInstance( );
					}
					else if( cls.isAssignableFrom( ShotVector.Nev.class ) )
					{
						shot.vector = dataDefaultsBinder.get( ).get( DataDefaults.nevShotVector ).newInstance( );
					}
				}
				else
				{
					shot.vector = cls.newInstance( );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace( );
				throw new RuntimeException( e );
			}
		}
		return cls.cast( shot.vector );
	}

	/**
	 * Sets a field of the given {@code Shot}'s {@linkplain ShotVector vector} to {@code newValue}. If {@code newValue}
	 * is not {@code null} and the {@code shot}'s vector is not an instance of {@code vectorType}, it will be
	 * replaced with a new instance of {@code vectorType}.
	 * 
	 * This is necessary because the vector type should be changed only if the user has entered a valid (non-null)
	 * value.
	 * 
	 * @param shot
	 *            the {@link Shot} whose vector to change.
	 * @param vectorType
	 *            the type of {@link ShotVector} containing the field to set.
	 * @param newValue
	 *            the new value to set the vector's field to.
	 * @param setter
	 *            lambda that takes {@code shot.vector} and {@code newValue} and sets the vector's field to
	 *            {@code newValue}.
	 */
	private <T extends ShotVector> void setVectorField( Shot shot , Class<? extends T> vectorType , Object newValue ,
		BiConsumer<T, Object> setter )
	{
		if( shot.vector == null || !vectorType.isInstance( shot.vector ) )
		{
			if( newValue != null )
			{
				try
				{
					if( Modifier.isAbstract( vectorType.getModifiers( ) ) )
					{
						if( vectorType.isAssignableFrom( ShotVector.Dai.class ) )
						{
							shot.vector = dataDefaultsBinder.get( ).get( DataDefaults.daiShotVector ).newInstance( );
						}
						else if( vectorType.isAssignableFrom( ShotVector.Nev.class ) )
						{
							shot.vector = dataDefaultsBinder.get( ).get( DataDefaults.nevShotVector ).newInstance( );
						}
					}
					else
					{
						shot.vector = vectorType.newInstance( );
					}
				}
				catch( Exception e )
				{
					e.printStackTrace( );
					throw new RuntimeException( e );
				}
			}
			else
			{
				return;
			}
		}
		setter.accept( vectorType.cast( shot.vector ) , newValue );
	}

	private <T extends ShotVectorText> T ensureVectorTextType( Shot shot , Class<T> cls )
	{
		if( shot.vectorText == null || !cls.isInstance( shot.vectorText ) )
		{
			try
			{
				if( Modifier.isAbstract( cls.getModifiers( ) ) )
				{
					if( cls.isAssignableFrom( ShotVectorText.Dai.class ) )
					{
						shot.vectorText = new ShotVectorText.Dai.SplitAngles( );
					}
				}
				else
				{
					shot.vectorText = cls.newInstance( );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace( );
				throw new RuntimeException( e );
			}
		}
		return cls.cast( shot.vectorText );
	}

	@FunctionalInterface
	private interface FieldSetter
	{
		public boolean set( Shot shot , Object value , ParsedText pt );
	}

	public boolean set( Shot shot , ShotColumnDef def , ParsedTextWithValue pt )
	{
		ParsedText text = pt == null || ( pt.text == null && pt.note == null ) ? null :
			new ParsedText( pt.text , pt.note );

		Object newValue = pt == null ? null : pt.value;

		FieldSetter setter = setters.get( def );
		if( setter != null )
		{
			return setter.set( shot , newValue , text );
		}
		return false;
	}
}

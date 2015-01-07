package org.andork.breakout.table;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.collect.MultiMaps;

public class ShotTableLogic
{
	private static final MultiMap<ShotColumnDef, ShotColumnDef>					textColumnsToClear;

	private static final List<ShotColumnDef>									daiShotVectorDefs;

	private static final List<ShotColumnDef>									nevShotVectorDefs;

	private static final Map<ShotColumnDef, BiFunction<Shot, Object, Object>>	setters;

	static
	{
		MultiMap<ShotColumnDef, ShotColumnDef> textColumnsToClearMod = HashSetMultiMap.newInstance( );

		daiShotVectorDefs = Arrays.asList(
			ShotColumnDef.dist ,
			ShotColumnDef.azmFsBs ,
			ShotColumnDef.azmFs ,
			ShotColumnDef.azmBs ,
			ShotColumnDef.incFsBs ,
			ShotColumnDef.incFs ,
			ShotColumnDef.incBs
			);

		nevShotVectorDefs = Arrays.asList(
			ShotColumnDef.offsN ,
			ShotColumnDef.offsE ,
			ShotColumnDef.offsV
			);

		textColumnsToClear = MultiMaps.unmodifiableMultiMap( textColumnsToClearMod );

		textColumnsToClearMod.putAll( ShotColumnDef.vector , daiShotVectorDefs );
		textColumnsToClearMod.putAll( ShotColumnDef.vector , nevShotVectorDefs );

		for( ShotColumnDef def : daiShotVectorDefs )
		{
			textColumnsToClearMod.put( def , ShotColumnDef.vector );
			textColumnsToClearMod.putAll( def , nevShotVectorDefs );
		}

		for( ShotColumnDef def : nevShotVectorDefs )
		{
			textColumnsToClearMod.put( def , ShotColumnDef.vector );
			textColumnsToClearMod.putAll( def , daiShotVectorDefs );
		}

		textColumnsToClearMod.put( ShotColumnDef.azmFsBs , ShotColumnDef.azmFs );
		textColumnsToClearMod.put( ShotColumnDef.azmFsBs , ShotColumnDef.azmBs );
		textColumnsToClearMod.put( ShotColumnDef.azmFs , ShotColumnDef.azmFsBs );
		textColumnsToClearMod.put( ShotColumnDef.azmBs , ShotColumnDef.azmFsBs );

		textColumnsToClearMod.put( ShotColumnDef.incFsBs , ShotColumnDef.incFs );
		textColumnsToClearMod.put( ShotColumnDef.incFsBs , ShotColumnDef.incBs );
		textColumnsToClearMod.put( ShotColumnDef.incFs , ShotColumnDef.incFsBs );
		textColumnsToClearMod.put( ShotColumnDef.incBs , ShotColumnDef.incFsBs );

		Map<ShotColumnDef, BiFunction<Shot, Object, Object>> settersMod = new HashMap<>( );

		settersMod.put( ShotColumnDef.vector , ( shot , newValue ) ->
		{
			Object rv = shot.vector;
			shot.vector = ( ShotVector ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.dist , ( shot , newValue ) ->
		{
			Object rv = ( ( DaiShotVector ) shot.vector ).dist;
			( ( DaiShotVector ) shot.vector ).dist = ( Double ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.azmFsBs , ( shot , newValue ) ->
		{
			DaiShotVector vector = ( DaiShotVector ) shot.vector;
			Object rv = new Double[ ] { vector.azmFs , vector.azmBs };
			vector.azmFs = ( ( Double[ ] ) newValue )[ 0 ];
			vector.azmBs = ( ( Double[ ] ) newValue )[ 1 ];
			return rv;
		} );
		settersMod.put( ShotColumnDef.azmFs , ( shot , newValue ) ->
		{
			Object rv = ( ( DaiShotVector ) shot.vector ).azmFs;
			( ( DaiShotVector ) shot.vector ).azmFs = ( Double ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.azmBs , ( shot , newValue ) ->
		{
			Object rv = ( ( DaiShotVector ) shot.vector ).azmBs;
			( ( DaiShotVector ) shot.vector ).azmBs = ( Double ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.incFsBs , ( shot , newValue ) ->
		{
			DaiShotVector vector = ( DaiShotVector ) shot.vector;
			Object rv = new Double[ ] { vector.incFs , vector.incBs };
			vector.incFs = ( ( Double[ ] ) newValue )[ 0 ];
			vector.incBs = ( ( Double[ ] ) newValue )[ 1 ];
			return rv;
		} );
		settersMod.put( ShotColumnDef.incFs , ( shot , newValue ) ->
		{
			Object rv = ( ( DaiShotVector ) shot.vector ).incFs;
			( ( DaiShotVector ) shot.vector ).incFs = ( Double ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.incBs , ( shot , newValue ) ->
		{
			Object rv = ( ( DaiShotVector ) shot.vector ).incBs;
			( ( DaiShotVector ) shot.vector ).incBs = ( Double ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.offsN , ( shot , newValue ) ->
		{
			Object rv = ( ( NevShotVector ) shot.vector ).n;
			( ( NevShotVector ) shot.vector ).n = ( Double ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.offsE , ( shot , newValue ) ->
		{
			Object rv = ( ( NevShotVector ) shot.vector ).e;
			( ( NevShotVector ) shot.vector ).e = ( Double ) newValue;
			return rv;
		} );
		settersMod.put( ShotColumnDef.offsV , ( shot , newValue ) ->
		{
			Object rv = ( ( NevShotVector ) shot.vector ).v;
			( ( NevShotVector ) shot.vector ).v = ( Double ) newValue;
			return rv;
		} );

		setters = Collections.unmodifiableMap( settersMod );
	}

	public static boolean set( ShotList list , Shot shot , ShotColumnDef def , ParsedTextWithValue pt )
	{
		ParsedText text = pt == null || ( pt.text == null && pt.note == null ) ? null :
			new ParsedText( pt.text , pt.note );
		boolean changed = !Objects.equals( shot.text.put( def , text ) , text );
		changed |= shot.text.keySet( ).removeAll( textColumnsToClear.get( def ) );

		Object newValue = pt == null ? null : pt.value;

		if( daiShotVectorDefs.contains( def ) )
		{
			if( newValue != null && ! ( shot.vector instanceof DaiShotVector ) )
			{
				changed = true;
				ShotVector oldVector = shot.vector;
				shot.vector = list.newDefaultDaiVector( );
				shot.vector.copyApplicableProps( oldVector );
			}
		}
		else if( nevShotVectorDefs.contains( def ) )
		{
			if( newValue != null && ! ( shot.vector instanceof NevShotVector ) )
			{
				changed = true;
				ShotVector oldVector = shot.vector;
				shot.vector = list.newDefaultNevVector( );
				shot.vector.copyApplicableProps( oldVector );
			}
		}

		BiFunction<Shot, Object, Object> setter = setters.get( def );
		if( setter != null )
		{
			return !Objects.equals( setter.apply( shot , newValue ) , newValue ) || changed;
		}
		return changed;
	}
}

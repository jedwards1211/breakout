package org.andork.breakout;

import org.andork.awt.EDTBimapper;
import org.andork.func.Bimapper;
import org.andork.func.BimapperFactory;
import org.andork.func.CompoundBimapper;
import org.andork.q.QElementDeepCloneBimapper;
import org.andork.q.QObject;
import org.andork.q.QSpec;

public class QObjectBimappers
{
	public static <S extends QSpec<S>> Bimapper<QObject<S>, String> defaultBimapper( final Bimapper<QObject<S>, Object> mapper )
	{
		return new BimapperFactory( )
		{
			@Override
			public Bimapper newInstance( )
			{
				return CompoundBimapper.compose(
						CompoundBimapper.compose( ( Bimapper ) EDTBimapper.newInstance( new QElementDeepCloneBimapper( ) ) , mapper ) ,
						new ObjectYamlBimapper( ) );
			}
			
		};
	}
}

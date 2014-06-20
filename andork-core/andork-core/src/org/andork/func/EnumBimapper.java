package org.andork.func;


public class EnumBimapper<E extends Enum<E>> implements Bimapper<E, Object>
{
	Class<E>	cls;
	
	private EnumBimapper( Class<E> cls )
	{
		this.cls = cls;
	}
	
	public static <E extends Enum<E>> EnumBimapper<E> newInstance( Class<E> cls )
	{
		return new EnumBimapper<E>( cls );
	}
	
	@Override
	public Object map( E t )
	{
		return t == null ? null : t.name( );
	}
	
	@Override
	public E unmap( Object s )
	{
		return s == null || s == null ? null : Enum.valueOf( cls , s.toString( ) );
	}
}
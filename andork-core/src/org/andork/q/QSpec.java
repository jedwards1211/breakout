package org.andork.q;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.collect.CollectionUtils;

public abstract class QSpec<S extends QSpec<S>>
{
	final Attribute<?>[]			attributes;
	final List<Attribute<?>>		attributeList;
	final Map<String, Attribute<?>>	attributesByName	= new LinkedHashMap<String, Attribute<?>>();

	protected QSpec(Iterable<Attribute<?>> attrIterable)
	{
		for (Attribute<?> attr : attrIterable)
		{
			attributesByName.put(attr.getName(), attr);
		}
		ArrayList<Attribute<?>> attrList = CollectionUtils.toArrayList(attrIterable);
		attrList.trimToSize();
		attributeList = Collections.unmodifiableList(attrList);
		attributes = attributeList.toArray(new Attribute[attributeList.size()]);

		for (int i = 0; i < attributeList.size(); i++)
		{
			if (attributes[i].index < 0)
			{
				attributes[i].index = i;
			}
			else if (attributes[i].index != i)
			{
				throw new IllegalArgumentException("attributes[" + i + "].index == " + attributes[i].index);
			}
		}
	}

	protected QSpec(Attribute<?>... attributes) {
		this(Arrays.asList(attributes));
	}

	public QObject<S> newObject()
	{
		return QObject.newInstance((S) this);
	}

	public Attribute<?> getAttribute(String name)
	{
		return attributesByName.get(name);
	}

	public List<Attribute<?>> getAttributes()
	{
		return attributeList;
	}

	public int getAttributeCount()
	{
		return attributes.length;
	}

	public Attribute<?> attributeAt(int index)
	{
		return attributes[index];
	}

	public static class Attribute<T>
	{
		final Class<T>	valueClass;
		int				index	= -1;
		final String	name;

		public static <T> Attribute<T> newInstance(Class<T> valueClass, String name)
		{
			return new Attribute<T>(valueClass, name);
		}

		public Attribute(Class<T> valueClass, String name)
		{
			super();
			this.valueClass = valueClass;
			this.name = name;
		}

		public Class<? super T> getValueClass()
		{
			return valueClass;
		}

		public int getIndex()
		{
			return index;
		}

		public String getName()
		{
			return name;
		}
	}
}

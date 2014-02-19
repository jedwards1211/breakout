package org.andork.spec.json;

import java.util.Collection;

import org.andork.spec.json.JsonSpec.Format;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public abstract class JsonSpecCollection<E> extends JsonSpecElement {
	protected final Format<? super E>	format;
	protected final Collection<E>		collection;

	protected JsonSpecCollection(Format<? super E> format) {
		this.format = format;
		this.collection = createCollection();
	}

	protected abstract Collection<E> createCollection();

	public boolean add(E element) {
		if (collection.add(element)) {
			if (element instanceof JsonSpecElement) {
				((JsonSpecElement) element).changeSupport().addPropertyChangeListener(propagator);
			}
			changeSupport.fireChildAdded(this, element);
			return true;
		}
		return false;
	}

	public boolean remove(E element) {
		if (collection.remove(element)) {
			if (element instanceof JsonSpecElement) {
				((JsonSpecElement) element).changeSupport().removePropertyChangeListener(propagator);
			}
			changeSupport.fireChildRemoved(this, element);
			return true;
		}
		return false;
	}

	public void clear() {
		for (E element : collection) {
			if (element instanceof JsonSpecElement) {
				((JsonSpecElement) element).changeSupport().removePropertyChangeListener(propagator);
			}
		}
		collection.clear();
		changeSupport.fireChildrenChanged(this);
	}

	public JsonArray toJson() {
		JsonArray array = new JsonArray();
		for (E element : collection) {
			array.add(format.format(element));
		}
		return array;
	}

	public static void fromJson(JsonArray array, JsonSpecCollection collection) throws Exception {
		for (JsonElement elem : array) {
			collection.add(collection.format.parse(elem));
		}
	}
}

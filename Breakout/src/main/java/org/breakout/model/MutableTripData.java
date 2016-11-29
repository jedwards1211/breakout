package org.breakout.model;

import java.util.Objects;
import java.util.function.Function;

import com.github.krukow.clj_ds.TransientMap;
import com.github.krukow.clj_lang.PersistentHashMap;

public class MutableTripData {
	private volatile PersistentHashMap<String, Object> persisted;
	private final TransientMap<String, Object> data;

	@SuppressWarnings("unchecked")
	MutableTripData(PersistentHashMap<String, Object> data) {
		persisted = data;
		this.data = persisted.asTransient();
	}

	public MutableTripData() {
		this(PersistentHashMap.emptyMap());
	}

	boolean dataEquals(PersistentHashMap<String, Object> prevData) {
		return persisted == prevData;
	}

	PersistentHashMap<String, Object> persist() {
		if (persisted == null) {
			persisted = (PersistentHashMap<String, Object>) data.persist();
		}
		return persisted;
	}

	public TripData toImmutable() {
		return new TripData(persist());
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) persist().get(key);
	}

	public MutableTripData set(String key, Object value) {
		if (persisted != null && Objects.equals(value, persisted.get(key))) {
			return this;
		}
		persisted = null;
		data.plus(key, value);
		return this;
	}

	public <T> MutableTripData update(String key, Function<? super T, ? extends T> updater) {
		@SuppressWarnings("unchecked")
		T oldValue = (T) persist().get(key);
		T newValue = updater.apply(oldValue);
		if (Objects.equals(oldValue, newValue)) {
			return this;
		}
		data.plus(key, newValue);
		return this;
	}

	public MutableTripData delete(String key) {
		if (persisted != null && !persisted.containsKey(key)) {
			return this;
		}
		persisted = null;
		data.minus(key);
		return this;
	}

	public String getName() {
		return get(TripData.name);
	}

	public MutableTripData setName(String name) {
		return set(TripData.name, name);
	}

	public MutableTripData deleteName() {
		return delete(TripData.name);
	}
}

package org.breakout.model;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.krukow.clj_lang.PersistentHashMap;

public class TripData {
	private final PersistentHashMap<String, Object> data;

	TripData(PersistentHashMap<String, Object> data) {
		this.data = data;
	}

	public TripData() {
		this(PersistentHashMap.emptyMap());
	}

	public MutableTripData toMutable() {
		return new MutableTripData(data);
	}

	public TripData withMutations(Consumer<MutableTripData> mutator) {
		MutableTripData mutable = toMutable();
		mutator.accept(mutable);
		return mutable.dataEquals(data) ? this : mutable.toImmutable();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) data.get(key);
	}

	public TripData set(String key, Object newValue) {
		return withMutations(m -> m.set(key, newValue));
	}

	public <T> TripData update(String key, Function<? super T, ? extends T> updater) {
		@SuppressWarnings("unchecked")
		T oldValue = (T) data.get(key);
		T newValue = updater.apply(oldValue);
		if (Objects.equals(oldValue, newValue)) {
			return this;
		}
		return withMutations(m -> m.set(key, newValue));
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof TripData) {
			return ((TripData) obj).data.equals(data);
		}
		if (obj instanceof MutableTripData) {
			return ((MutableTripData) obj).persist().equals(data);
		}
		return false;
	}

	public static final String name = "name";

	public String getName() {
		return (String) data.get(name);
	}

	public TripData setName(String name) {
		return set(TripData.name, name);
	}
}

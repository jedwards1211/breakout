module.exports = {
	type: 'PersistentHashMap',
  generateProperties: true,
  generateSetters: true,
  generateUpdaters: true,
  fields: {
    overrideFromCave: {
      type: 'String',
      description: 'name of cave from station is in, if different from trip',
    },
    fromStation: {
      type: 'String',
      description: 'from station name'
    },
    overrideToCave: {
      type: 'String',
      description: 'name of cave of to station is in, if different to trip',
    },
    toStation: {
      type: 'String',
      description: 'to station name'
    },
    distance: {
      type: 'String',
      description: 'distance between from and to station'
    },
    frontAzimuth: {
      type: 'String',
      description: 'azimuth toward to station at from station',
    },
    backAzimuth: {
      type: 'String',
      description: 'azimuth toward from station at to station',
    },
    frontInclination: {
      type: 'String',
      description: 'inclination toward to station at from station',
    },
    backInclination: {
      type: 'String',
      description: 'inclination toward from station at to station',
    },
    left: {
      type: 'String',
      description: 'distance between from station and left wall',
    },
    right: {
      type: 'String',
      description: 'distance between from station and right wall',
    },
    up: {
      type: 'String',
      description: 'distance between from station and ceiling',
    },
    down: {
      type: 'String',
      description: 'distance between from station and floor',
    },
    northing: {
      type: 'String',
      description: 'distance north relative to coordinate origin',
    },
    latitude: {
      type: 'String',
      description: "from station's latitude",
    },
    longitude: {
      type: 'String',
      description: "from station's longitude",
    },
    easting: {
      type: 'String',
      description: "from station's distance east relative to coordinate origin",
    },
    elevation: {
      type: 'String',
      description: "from station's distance east relative to coordinate origin",
    },
    comment: {
      type: 'String',
      description: 'any user comment',
    },
    overrideAttachedFiles: {
      type: 'PersistentVector<String>',
      description: "attached files (if one they can't be associated with the entire trip)",
    },
    trip: {
      type: 'SurveyTrip',
      description: 'trip this row belongs to',
    },
    excludeDistance: {
      type: 'Boolean',
      defaultValue: 'false',
      description: 'whether to exclude this shot from the total cave length',
    },
    excludeFromPlotting: {
      type: 'Boolean',
      defaultValue: 'false',
      description: 'whether to exclude this shot from plotting',
    },
  },
  extraImports: [
    'java.util.List',
    'static org.andork.util.JavaScript.or',
    'org.andork.model.Property',
  	'com.github.krukow.clj_ds.PersistentVector',
  ],
  extraMutableImports: [
  	'com.github.krukow.clj_ds.PersistentVector',
  ],
  extraCode: `
  public String getFromCave() {
    return or(getOverrideFromCave(), getTrip() == null ? null : getTrip().getCave());
  }

  public String getToCave() {
    return or(getOverrideToCave(), getTrip() == null ? null : getTrip().getCave());
  }
  
  public PersistentVector<String> getAttachedFiles() {
  	return or(getOverrideAttachedFiles(), getTrip() == null ? null : getTrip().getAttachedFiles());
  }
  `,
  extraProperties: `
    public static <V> DefaultProperty<SurveyRow, V> createTripProperty(
        String name, Class<? super V> valueClass,
        Property<SurveyTrip, V> tripProperty) {
      return new DefaultProperty<SurveyRow, V>(name, valueClass,
        r -> r.getTrip() == null ? null : tripProperty.get(r.getTrip()),
        (row, v) -> {
          return row.withMutations(r -> r.updateTrip(trip -> {
            return tripProperty.set(trip == null ? new SurveyTrip() : trip, v);
          }));
        }
      );
    }

    public static DefaultProperty<SurveyRow, String> fromCave = create(
			"fromCave", String.class,
			r -> r.getFromCave(),
			(r, fromCave) -> r.setOverrideFromCave(fromCave)
		);
    public static DefaultProperty<SurveyRow, String> toCave = create(
			"toCave", String.class,
			r -> r.getToCave(),
			(r, toCave) -> r.setOverrideToCave(toCave)
		);
    public static DefaultProperty<SurveyRow, String> tripName = createTripProperty(
      "tripName", String.class, SurveyTrip.Properties.name);
    public static DefaultProperty<SurveyRow, String> date = createTripProperty(
      "date", String.class, SurveyTrip.Properties.date);
    public static DefaultProperty<SurveyRow, List<String>> surveyors = createTripProperty(
      "surveyors", List.class, SurveyTrip.Properties.surveyors);
    public static DefaultProperty<SurveyRow, PersistentVector<String>> attachedFiles = create(
			"attachedFiles", PersistentVector.class,
			r -> r.getAttachedFiles(),
			(r, attachedFiles) -> r.setOverrideAttachedFiles(attachedFiles)
		);
  `,
  extraMutableCode: `
	public MutableSurveyRow ensureTrip() {
		if (getTrip() == null) setTrip(new SurveyTrip());
		return this;
	}
  `,

}
module.exports = {
  type: 'PersistentHashMap',
  generateProperties: true,
  generateSetters: true,
  generateUpdaters: true,
  extraImports: [
    'com.google.gson.JsonArray',
    'java.text.DecimalFormat',
    'org.andork.unit.Unit',
    'org.andork.unit.UnitizedNumber',
    'org.andork.unit.Length',
  ], 
  extraMutableImports: [
    'com.google.gson.JsonArray',
    'org.andork.unit.UnitizedNumber',
    'org.andork.unit.Length',
  ], 
  fields: {
    cave: {
      type: 'String',
      description: 'name of cave the lead is in',
    },
    station: {
      type: 'String',
      description: 'the name of the nearest station'
    },
    description: {
      type: 'String',
      description: 'the description of the lead',
    },
    rawWidth: {
      type: 'JsonArray',
      description: 'the width of the lead from metacave',
    },
    rawHeight: {
      type: 'JsonArray',
      description: 'the height of the lead from metacave',
    },
    width: {
      type: 'UnitizedNumber<Length>',
      description: 'the width of the lead',
    },
    height: {
      type: 'UnitizedNumber<Length>',
      description: 'the height of the lead',
    },
    done: {
      type: 'Boolean',
      description: 'whether the lead is done or not',
      defaultValue: 'false',
    }
  },
  extraCode: `
  	private final DecimalFormat sizeFormat = new DecimalFormat("0.#");
  
	public String describeSize(Unit<Length> unit) {
		UnitizedNumber<Length> width = getWidth();
		UnitizedNumber<Length> height = getHeight();
		StringBuilder builder = new StringBuilder();
		if (width != null) {
			builder.append(sizeFormat.format(width.doubleValue(unit)))
				.append('w');
		}
		if (height != null) {
			if (builder.length() > 0) builder.append(' ');
			builder.append(sizeFormat.format(height.doubleValue(unit)))
				.append('h');
		}
		return builder.length() > 0 ? builder.toString() : null;
	}
`,
}
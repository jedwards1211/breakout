module.exports = {
	imports: [
	  'java.util.List',
	  'org.andork.unit.Unit',
	  'org.andork.unit.Angle',
	  'org.andork.unit.Length',
	  'static org.andork.util.JavaScript.or',
	],
	generateProperties: true,
	generateSetters: true,
	generateUpdaters: true,
	fields: {
		cave: {type: 'String', description: 'cave name'},
		name: {type: 'String', description: 'trip name'},
		date: {type: 'String', description: 'trip date'},
		surveyNotes: {type: 'String', description: 'survey notes file path'},
		surveyors: {type: 'List<String>', description: 'surveyor names'},
		distanceUnit: {type: 'Unit<Length>', description: 'default length unit', initValue: 'Length.meters'},
		angleUnit: {type: 'Unit<Angle>', description: 'default angle unit', initValue: 'Angle.degrees'},
		overrideFrontAzimuthUnit: {type: 'Unit<Angle>', description: 'default frontsight azimuth unit'},
		overrideBackAzimuthUnit: {type: 'Unit<Angle>', description: 'default backsight azimuth unit'},
		overrideFrontInclinationUnit: {type: 'Unit<Angle>', description: 'default frontsight inclination unit'},
		overrideBackInclinationUnit: {type: 'Unit<Angle>', description: 'default backsight inclination unit'},
		backAzimuthsCorrected: {
			type: 'boolean', 
			description: 'whether backsight azimuths are corrected',
			getterName: 'areBackAzimuthsCorrected',
		},
		backInclinationsCorrected: {
			type: 'boolean', 
			description: 'whether backsight inclinations are corrected',
			getterName: 'areBackInclinationsCorrected',
		},
		declination: {type: 'String', description: 'magnetic declination'},
		distanceCorrection: {type: 'String', description: 'correction for shot distances'},
		frontAzimuthCorrection: {type: 'String', description: 'correction for frontsight azimuths'},
		frontInclinationCorrection: {type: 'String', description: 'correction for frontsight inclinations'},
		backAzimuthCorrection: {type: 'String', description: 'correction for backsight azimuths'},
		backInclinationCorrection: {type: 'String', description: 'correction for backsight inclinations'},
	},
	extraCode: `
	public Unit<Angle> getFrontAzimuthUnit() {
		return or(data.overrideFrontAzimuthUnit, data.angleUnit);
	}

	public Unit<Angle> getBackAzimuthUnit() {
		return or(data.overrideBackAzimuthUnit, data.angleUnit);
	}

	public Unit<Angle> getFrontInclinationUnit() {
		return or(data.overrideFrontInclinationUnit, data.angleUnit);
	}

	public Unit<Angle> getBackInclinationUnit() {
		return or(data.overrideBackInclinationUnit, data.angleUnit);
	}
	`
}
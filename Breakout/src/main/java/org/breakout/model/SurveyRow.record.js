module.exports = {
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
    easting: {
    	type: 'String',
    	description: 'distance east relative to coordinate origin',
    },
    elevation: {
    	type: 'String',
    	description: 'distance east relative to coordinate origin',
    },
    comment: {
    	type: 'String',
    	description: 'any user comment',
    },
    trip: {
    	type: 'SurveyTrip',
    	description: 'trip this row belongs to',
    },
	}
}
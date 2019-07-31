module.exports = {
	type: 'PersistentHashMap',
  generateProperties: true,
  generateSetters: true,
  generateUpdaters: true,
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
    width: {
      type: 'String',
      description: 'the width of the lead',
    },
    height: {
      type: 'String',
      description: 'the height of the lead',
    },
    done: {
      type: 'Boolean',
      description: 'whether the lead is done or not',
      defaultValue: 'false',
    }
  },
}
/* eslint-env node */

module.exports = {
  webpack: config => {
    config.node = config.node || {}
    config.node.process = false
    return config
  },
}

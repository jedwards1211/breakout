/* eslint-env node */

module.exports = {
  webpack: config => {
    config.node = config.node || {}
    config.node.process = false
    config.module.rules.push({
      test: /\.md$/,
      use: 'raw-loader',
    })
    return config
  },
}

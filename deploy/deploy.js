var fs = require('fs')
var Promise = require('bluebird')
var path = require('path')
var request = require('superagent-bluebird-promise')
var apiSettings = require('./github-api.json')
var { execSync } = require('child_process')

var username = apiSettings.username
var token = apiSettings.token

process.chdir(path.resolve(__dirname, '..'))
var version = process.env.BREAKOUT_VERSION
if (!version) {
  console.error(`missing process.env.BREAKOUT_VERSION`)
  process.exit(1)
}

const assets = [
  path.resolve(
    __dirname,
    `../breakout/target/breakout-${version}-all-platforms.jar`
  ),
  path.resolve(__dirname, `../packages/bundles/Breakout-${version}.dmg`),
  path.resolve(__dirname, `../packages/bundles/Breakout-${version}-x64.msi`),
  path.resolve(__dirname, `../packages/bundles/Breakout-${version}-x86.msi`),
]

for (const asset of assets) {
  if (!fs.existsSync(asset)) {
    console.error(`asset not found: ${path.relative(process.cwd(), asset)}`)
    process.exit(1)
  }
}

version = 'v' + version

console.log('deploying Breakout ' + version)

async function go() {
  const {
    body: { upload_url },
  } = await request
    .post(
      'https://' +
        username +
        ':' +
        token +
        '@api.github.com/repos/' +
        username +
        '/breakout/releases'
    )
    .send(
      JSON.stringify({
        tag_name: version,
        name: version,
        draft: true,
      })
    )

  for (const asset of assets) {
    var options = {
      url: upload_url.replace('{?name,label}', ''),
      port: 443,
      auth: {
        pass: token,
        user: username,
      },
      headers: {
        'User-Agent': 'Release-Agent',
        'Content-Type': asset.endsWith('.jar')
          ? 'application/java-archive'
          : 'application/octet-stream',
        'Content-Length': fs.statSync(asset).size,
      },
      qs: {
        name: path.basename(asset),
      },
    }

    console.log(`Uploading ${path.basename(asset)}...`)
    await new Promise(function (resolve, reject) {
      fs.createReadStream(asset).pipe(
        require('request').post(options, function (err, res) {
          if (err) return reject(err)
          console.log(res)
          resolve(res)
        })
      )
    })
  }
}
go().then(
  () => process.exit(0),
  (err) => {
    console.error(err.stack)
    process.exit(1)
  }
)

var fs = require('fs');
var Promise = require('bluebird');
var path = require('path');
var shell = require('shelljs');
var request = require('superagent-bluebird-promise');

shell.cd('..');
var version = shell.exec("mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version " + 
  "| grep -Ev '(^\\[|Download\\w+:)'").output.trim();
var jarName = 'breakout-' + version + '.jar';
var jarFile = path.join(__dirname, '../Breakout/target', jarName);
version = 'v' + version;

var token = '7a9d705e276eb9d2f177ebb8d274882e2793f5ea';

shell.echo('deploying Breakout ' + version);

function streamToPromise(stream) {
    return new Promise(function(resolve, reject) {
        stream.on("end", resolve);
        stream.on("error", reject);
    });
}

request.post('https://jedwards1211:' + token + '@api.github.com/repos/jedwards1211/breakout/releases')
  .send(JSON.stringify({
    tag_name: version,
    name: version,
    draft: true,
  }))
  .then(function(res) {
    res = res.body;

    var options = {
      url: res.upload_url.replace('{?name,label}', ''),
      port: 443,
      auth: {
        pass: token,
        user: 'jedwards1211',
      },
      headers: {
        'User-Agent': 'Release-Agent',
        'Content-Type': 'application/java-archive',
        'Content-Length': fs.statSync(jarFile).size,
      },
      qs: {
        name: jarName
      }
    };

    // Better as a stream
    return new Promise(function(resolve, reject) {
      fs.createReadStream(jarFile).pipe(require('request').post(options, function(err, res){
        if (err) return reject(err);
        resolve(res);
      }));
    });
  })
  .then(function(res) {
    console.log(res);
  })
  .catch(function(err) {
    console.error(err.stack);
  });

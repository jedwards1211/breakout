#!/usr/bin/env node

const { spawn } = require('promisify-child-process')

async function go() {
  const version = process.argv[2]
  if (!version) throw new Error('version must be provided')

  spawn('mvn', ['clean', 'install'], {
    stdio: 'inherit',
  })
}

go()

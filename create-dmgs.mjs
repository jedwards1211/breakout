import appdmg from 'appdmg'
import { fileURLToPath } from 'url'
import fs from 'fs/promises'
import { once } from 'events'
import path from 'path'

const resolve = (relpath) => fileURLToPath(import.meta.resolve(relpath))

const version = (await fs.readFile(resolve('./version'), 'utf8')).trim()

const basepath = resolve('./breakout/target')

async function makeDmg({ app, target }) {
  console.error(`Creating ${path.relative(process.cwd(), target)}...`)

  await fs.unlink(target).catch((error) => {
    if (error.code !== 'ENOENT') throw error
  })

  const ee = appdmg({
    basepath,
    target,
    specification: {
      title: `Breakout ${version}`,
      icon: resolve('./breakout.icns'),
      'icon-size': 100,
      window: {
        size: { width: 600, height: 300 },
      },
      contents: [
        { x: 450, y: 150, type: 'link', path: '/Applications' },
        {
          x: 150,
          y: 150,
          type: 'file',
          path: app,
        },
      ],
    },
  })

  ee.on('progress', console.error)

  await once(ee, 'finish')
}

await makeDmg({
  app: path.resolve(basepath, 'macosx-aarch64/Breakout/Breakout.app'),
  target: path.resolve(basepath, `macosx-aarch64/Breakout_${version}.dmg`),
})
await makeDmg({
  app: path.resolve(basepath, 'macosx-x64/Breakout/Breakout.app'),
  target: path.resolve(basepath, `macosx-x64/Breakout_${version}.dmg`),
})

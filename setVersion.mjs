import fs from 'fs/promises'
import { glob } from 'glob'

const version = process.argv[2].replace(/-(\w+)\.(\d+)$/, '-$1$2')

console.error(`setting version to ${version}...`)
await fs.writeFile('version', version, 'utf8')

for (const file of await glob(['pom.xml', '*/pom.xml'])) {
  console.error(`updating ${file}...`)
  const content = await fs.readFile(file, 'utf8')
  const replaced = content.replace(/0\.0\.0-SNAPSHOT/g, version)
  if (replaced !== content) {
    fs.writeFile(file, replaced, 'utf8')
    console.error(`wrote ${file}`)
  }
}

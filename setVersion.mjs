import fs from 'fs/promises'

const version = process.argv[2]

console.error(`setting version to ${version}...`)

for await (const file of fs.glob(['pom.xml', '*/pom.xml'])) {
  console.error(`updating ${file}...`)
  const content = await fs.readFile(file, 'utf8')
  const replaced = content.replace('0.0.0-SNAPSHOT', version)
  if (replaced !== content) {
    fs.writeFile(file, replaced, 'utf8')
    console.error(`wrote ${file}`)
  }
}

import octokit from '../../../src/octokit'
import repo from '../../../src/repo'

export default async function handler(req, res) {
  const {
    query: { tag },
  } = req

  let release
  try {
    ;({ data: release } =
      tag === 'latest'
        ? await octokit.repos.getLatestRelease(repo)
        : await octokit.repos.getReleaseByTag({ ...repo, tag }))
  } catch (error) {
    if (error.status === 404) {
      res.statusCode = 404
      res.end(`Not Found: ${tag}`)
    } else {
      res.statusCode = 500
      res.end(error.message)
    }
    return
  }

  const { id, tag_name, name, published_at, assets } = release

  res.statusCode = 200
  res.setHeader('Content-Type', 'application/json')
  res.end(
    JSON.stringify({
      id,
      tag_name,
      name,
      published_at,
      assets: assets.map((asset) => {
        const { id, name, content_type, size, browser_download_url } = asset
        let os = 'any',
          arch = 'any'
        if (/\.dmg$/.test(name)) {
          os = 'macos'
        } else if (/x64\.msi$/.test(name)) {
          os = 'windows'
          arch = 'x64'
        } else if (/x86\.msi$/.test(name)) {
          os = 'windows'
          arch = 'x86'
        }
        return {
          id,
          name,
          content_type,
          size,
          browser_download_url,
          os,
          arch,
        }
      }),
    })
  )
}

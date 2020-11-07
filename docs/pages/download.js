/* eslint-disable react/no-unescaped-entities */

import * as React from 'react'
import repo from '../src/repo'
import Collapse from '@material-ui/core/Collapse'
import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'
import CloudDownload from '@material-ui/icons/CloudDownload'
import ListItemIcon from '@material-ui/core/ListItemIcon'
import withStyles from '@material-ui/core/styles/withStyles'

import ExitToApp from '@material-ui/icons/ExitToApp'
import AppleIcon from '../src/icons/Apple'
import WindowsIcon from '../src/icons/Windows'

const styles = (theme) => ({
  downloadButton: {
    width: 450,
    border: `1px solid ${theme.palette.grey[300]}`,
    borderRadius: theme.spacing(0.5),
  },
  downloadJavaButton: {
    marginLeft: '1em',
    verticalAlign: 'middle',
  },
  headerIcon: {
    verticalAlign: 'middle',
  },
})

function formatSize(bytes) {
  var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
  if (bytes == 0) return '0 Bytes'
  var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)))
  return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i]
}

class Download extends React.Component {
  state = { platform: null }
  static async getInitialProps() {
    const { default: octokit } = await import('../src/octokit')
    const {
      data: { tag_name, published_at, assets },
    } = await octokit.repos.getLatestRelease(repo)
    return {
      latest: {
        tag_name,
        published_at,
        assets: assets.map((asset) => {
          const { name, size, browser_download_url } = asset
          return { name, size, browser_download_url }
        }),
      },
    }
  }
  setPlatform = (platform) => this.setState({ platform })
  render() {
    const { platform } = this.state
    const {
      classes,
      latest: { tag_name, published_at, assets },
    } = this.props
    const dmg = assets.find((a) => /\.dmg$/.test(a.name))
    const x64msi = assets.find((a) => /x64\.msi$/.test(a.name))
    const x86msi = assets.find((a) => /x86\.msi$/.test(a.name))
    const jar = assets.find((a) => /\.jar$/.test(a.name))
    return (
      <div>
        <h1>Download</h1>
        <h2>
          Breakout {tag_name} (Released{' '}
          {new Date(published_at).toLocaleDateString()})
        </h2>
        {dmg && (
          <ListItem
            className={classes.downloadButton}
            button
            component="a"
            href={dmg.browser_download_url}
            selected={platform === 'macos'}
            onClick={() => this.setPlatform('macos')}
          >
            <ListItemIcon>
              <AppleIcon fontSize="large" />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>MacOS</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{dmg.name}</strong> ({formatSize(dmg.size)})
                </span>
              }
            />
          </ListItem>
        )}
        {x86msi && (
          <ListItem
            className={classes.downloadButton}
            button
            component="a"
            href={x86msi.browser_download_url}
            selected={platform === 'windows-x86'}
            onClick={() => this.setPlatform('windows-x86')}
          >
            <ListItemIcon>
              <WindowsIcon fontSize="large" />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>Windows 32-bit</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{x86msi.name}</strong> ({formatSize(x86msi.size)})
                </span>
              }
            />
          </ListItem>
        )}
        {x64msi && (
          <ListItem
            className={classes.downloadButton}
            button
            component="a"
            href={x64msi.browser_download_url}
            selected={platform === 'windows-x64'}
            onClick={() => this.setPlatform('windows-x64')}
          >
            <ListItemIcon>
              <WindowsIcon fontSize="large" />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>Windows 64-bit</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{x64msi.name}</strong> ({formatSize(x64msi.size)})
                </span>
              }
            />
          </ListItem>
        )}
        {jar && (
          <ListItem
            className={classes.downloadButton}
            button
            component="a"
            href={jar.browser_download_url}
            selected={platform === 'other'}
            onClick={() => this.setPlatform('other')}
          >
            <ListItemIcon>
              <CloudDownload fontSize="large" />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>Other Desktop Operating Systems</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{jar.name}</strong> ({formatSize(jar.size)})
                </span>
              }
            />
          </ListItem>
        )}

        <Collapse in={platform === 'other'}>
          <h2>Installation</h2>

          <p>
            Breakout requires <strong>Java 8</strong>
            <a href="https://www.oracle.com/java/technologies/javase-jre8-downloads.html">
              <img _fcksavedurl="https://www.oracle.com/java/technologies/javase-jre8-downloads.html" />
              <img
                className={classes.downloadJavaButton}
                src="http://download.oracle.com/technetwork/java/get-java/getjavasoftware-88x31.png"
                alt="Get Java Software"
                border="0"
                width="88"
                height="31"
              />
            </a>
          </p>
          <p>
            After you've installed Java, just open the downloaded file ({' '}
            <code>{assets[0].name}</code>) to launch Breakout.
          </p>
          <p>
            You may want to copy the program to your system applications folder,
            but that's up to you.
          </p>
        </Collapse>

        <a href="https://github.com/jedwards1211/breakout/releases">
          <h2>
            Previous Versions <ExitToApp className={classes.headerIcon} />
          </h2>
        </a>
      </div>
    )
  }
}

export default withStyles(styles)(Download)

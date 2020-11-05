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

const styles = theme => ({
  downloadButton: {
    width: 450,
    border: `1px solid ${theme.palette.grey[300]}`,
    borderRadius: theme.spacing.unit / 2,
  },
  downloadJavaButton: {
    marginLeft: '1em',
    verticalAlign: 'middle',
  },
  headerIcon: {
    verticalAlign: 'middle',
  },
})

class Download extends React.Component {
  state = { platform: null }
  static async getInitialProps() {
    const octokit = require('@octokit/rest')()
    const { data: releases } = await octokit.repos.listReleases({
      ...repo,
      per_page: 1,
    })
    return { releases }
  }
  setPlatform = platform => this.setState({ platform })
  render() {
    const { platform } = this.state
    const { classes, releases } = this.props
    const latest = releases[0]
    const releasedAt = new Date(latest.published_at).toLocaleDateString()
    const dmg = latest.assets.find(a => /\.dmg$/.test(a.name))
    const x64msi = latest.assets.find(a => /x64\.msi$/.test(a.name))
    const x86msi = latest.assets.find(a => /x86\.msi$/.test(a.name))
    const jar = latest.assets.find(a => /\.jar$/.test(a.name))
    return (
      <div>
        <h1>Download</h1>
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
              <CloudDownload />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>MacOS</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{dmg.name}</strong> (released {releasedAt})
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
              <CloudDownload />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>Windows 32-bit</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{x86msi.name}</strong> (released {releasedAt})
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
              <CloudDownload />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>Windows 64-bit</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{x64msi.name}</strong> (released {releasedAt})
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
              <CloudDownload />
            </ListItemIcon>
            <ListItemText
              primary={
                <span>
                  <strong>Other Desktop Operating Systems</strong>
                </span>
              }
              secondary={
                <span>
                  <strong>{jar.name}</strong> (released {releasedAt})
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
            <code>{latest.assets[0].name}</code>) to launch Breakout.
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

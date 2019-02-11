/* eslint-disable react/no-unescaped-entities */

import * as React from 'react'
import repo from '../src/repo'
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
  static async getInitialProps() {
    const octokit = require('@octokit/rest')()
    const { data: releases } = await octokit.repos.listReleases({
      ...repo,
      per_page: 1,
    })
    return { releases }
  }
  render() {
    const { classes, releases } = this.props
    const latest = releases[0]
    const releasedAt = new Date(latest.published_at).toLocaleDateString()
    return (
      <div>
        <h1>Download</h1>
        <ListItem
          className={classes.downloadButton}
          button
          component="a"
          href={latest.assets[0].browser_download_url}
        >
          <ListItemIcon>
            <CloudDownload />
          </ListItemIcon>
          <ListItemText
            primary={
              <span>
                <strong>Breakout</strong> (all Desktop Operating Systems)
              </span>
            }
            secondary={
              <span>
                <strong>{latest.name}</strong> (released {releasedAt})
              </span>
            }
          />
        </ListItem>

        <h2>Installation</h2>

        <p>
          Breakout requires <strong>Java 8+</strong>
          <a href="http://www.java.com">
            <img _fcksavedurl="http://www.java.com" />
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

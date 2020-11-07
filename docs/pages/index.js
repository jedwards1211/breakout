import * as React from 'react'

import withStyles from '@material-ui/core/styles/withStyles'

import Link from '../src/Link'

import CloudDownload from '@material-ui/icons/CloudDownload'

const homeStyles = (theme) => ({
  logo: {
    marginTop: theme.spacing(3),
    width: 600,
    maxWidth: '100%',
  },
  headerIcon: {
    verticalAlign: 'middle',
  },
})

const Home = ({ classes }) => (
  <div>
    <img src="/static/logo.png" className={classes.logo} />
    <h2>
      Breakout is the ideal software for analyzing large cave systems in 3D.
    </h2>
    <p>Things Breakout can do:</p>
    <ul>
      <li>colorize maps by many different parameters</li>
      <li>
        show terrain and satellite imagery from{' '}
        <a href="https://www.mapbox.com">Mapbox</a>
      </li>
      <li>
        search for and fly to stations by name, trip description, or surveyor
        names
      </li>
      <li>help you see which passages are connected</li>
      <li>create profiles and section cuts</li>
      <li>open survey notes when you double-click on a shot</li>
    </ul>
    <Link href="/download">
      <h2>
        <CloudDownload className={classes.headerIcon} /> Download
      </h2>
    </Link>
  </div>
)

export default withStyles(homeStyles)(Home)

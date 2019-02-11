/* eslint-disable react/no-unescaped-entities */

import * as React from 'react'

import withStyles from '@material-ui/core/styles/withStyles'

import KeyboardKey from '../src/KeyboardKey'

const styles = theme => ({
  img: {
    display: 'block',
    margin: theme.spacing.unit * 4,
    zoom: '50%',
    maxWidth: '90%',
  },
  highlightFilterTable: {
    '& td': {
      verticalAlign: 'top',
    },
  },
})

const FindingStations = ({ classes }) => (
  <div>
    <h1>Finding Stations</h1>

    <p>
      It's easy to search for stations and find them in the 3D view. You can
      search for:
    </p>

    <ul>
      <li>Stations</li>
      <ul>
        <li>"AJ" (all stations beginning with "AJ")</li>
        <li>"AJ51" (just station "AJ51")</li>
        <li>"AJ50-60" (stations "AJ50", "AJ51", ..., "AJ60")</li>
        <li>"AJ50+" (stations "AJ50" and higher)</li>
        <li>
          "AJ, QC35-40" (all "AJ" stations and "QC35", "QC36", ..., "QC40")
        </li>
      </ul>
      <li> Trip Names</li>
      <li> Surveyors' Names</li>
      <li> Shot comments</li>
    </ul>

    <p>
      For instance, "joe" would find all shots where anyone named Joe was on the
      survey team, plus shots from trips with names like "Joe's Wet Lead", etc.
    </p>

    <h2>The Search Field</h2>

    <p>
      The search field is at the top left of the search drawer (opened by moving
      the mouse to the left side of the window) and the survey drawer (opened by
      moving the mouse to the bottom of the window):
    </p>

    <img
      src="/static/search/search-drawer.png"
      className={classes.img}
      alt="Search Drawer"
    />
    <img
      src="/static/search/survey-drawer.png"
      className={classes.img}
      alt="Survey Drawer"
    />

    <h2>Enter Search Terms</h2>

    <p>
      After typing in the search field, matching shots will be highlighted in
      the table below. You can click the yellow rectangles next to the scroll
      bar to jump to the highlighted results.
    </p>

    <img
      src="/static/search/highlight-results.png"
      className={classes.img}
      alt="Search Results"
    />

    <h3>Highlight / Filter</h3>

    <p>
      When the <strong>Highlight</strong> option is selected, matching shots
      will be highlighted in the table below. If you select the{' '}
      <strong>Filter</strong> option above the table, it will show only matching
      shots:
    </p>

    <table className={classes.highlightFilterTable}>
      <tbody>
        <tr>
          <td>
            <img
              src="/static/search/highlight-results.png"
              className={classes.img}
              alt="Highlight Results"
            />
          </td>
          <td>
            <img
              src="/static/search/filter-results.png"
              className={classes.img}
              alt="Filter Results"
            />
          </td>
        </tr>
      </tbody>
    </table>

    <h2>Fly to Results</h2>

    <p>
      Hit the <KeyboardKey>Enter</KeyboardKey> key while the search field is
      focused to fly to the matching shots. Breakout will select the matching
      shots (highlighting them blue) and fly to them in the 3D view:
    </p>

    <img
      src="/static/search/fly-to-results.png"
      className={classes.img}
      alt="Fly to Results"
    />
  </div>
)

export default withStyles(styles)(FindingStations)

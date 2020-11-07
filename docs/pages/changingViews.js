import * as React from 'react'

import withStyles from '@material-ui/core/styles/withStyles'

const styles = (theme) => ({
  img: {
    maxWidth: '100%',
  },
})

const ChangingViews = ({ classes }) => (
  <div>
    <h1>Changing Views</h1>
    <p>
      At the top of the <strong>View Settings Drawer</strong> (open by moving
      the mouse to the right side of the window) there are buttons you can press
      to switch between views:
    </p>
    <img
      src="/static/navigation/view-selector.gif"
      alt="View Selector"
      className={classes.img}
    />

    <h2>Ortho and Perspective Views</h2>
    <p>
      The <strong>Plan and Profile Views</strong> are <em>Orthographic</em>{' '}
      views, meaning everything appears the same size regardless of how far away
      it is. In perspective view, passages that are far away appear smaller,
      just like eyesight.
    </p>
    <img
      src="/static/navigation/ortho-and-perspective.gif"
      alt="Ortho and Perspective"
      className={classes.img}
    />

    <h2>Auto Profile View</h2>
    <p>
      Auto profile view aligns to the average azimuth of the selected shots
      instead of a cardinal direction. If no shots are selected, the average
      azimuth of all shots on screen is used instead.
    </p>
    <img
      src="/static/navigation/auto-profile.gif"
      alt="Auto Profile View"
      className={classes.img}
    />
  </div>
)

export default withStyles(styles)(ChangingViews)

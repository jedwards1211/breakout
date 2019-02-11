import * as React from 'react'

import KeyboardKey from '../src/KeyboardKey'

import withStyles from '@material-ui/core/styles/withStyles'

const styles = theme => ({
  img: {
    maxWidth: '100%',
  },
})

const Navigation = ({ classes }) => (
  <div>
    <h1>Mouse Navigation</h1>

    <h2>Orbit (Perspective View only)</h2>
    <h4>Left Click and Drag</h4>
    <p>
      Rotates the camera around the selected shots, or if no shots are selected,
      tries to intelligently pick a center of rotation based upon where you
      clicked.
    </p>
    <img
      src="/static/navigation/orbit.gif"
      alt="Orbit"
      className={classes.img}
    />

    <h2>Pan</h2>
    <h4>Plan and Profile Views: Left Click and Drag</h4>
    <h4>Perspective View: Right Click and Drag</h4>
    <p>Moves the camera laterally.</p>
    <img src="/static/navigation/pan.gif" alt="Pan" className={classes.img} />

    <h2>Zoom</h2>
    <h4>Scroll / Rotate Mouse Wheel</h4>
    <p>
      In perspective view, the zoom sensitivity is automatically adjusted based
      upon how close you are to passages.
    </p>
    <img src="/static/navigation/zoom.gif" alt="Zoom" className={classes.img} />

    <h1>Shortcuts</h1>

    <h2>Orbit to Plan</h2>
    <h4>
      Press <KeyboardKey>O</KeyboardKey> key (or press button in View Settings)
    </h4>
    <img
      src="/static/navigation/orbit-to-plan.gif"
      alt="Orbit to Plan"
      className={classes.img}
    />

    <h2>Fit View to Everything</h2>
    <h4>
      Press <KeyboardKey>E</KeyboardKey> key (or press button in View Settings)
    </h4>
    <p>Zooms so that everything fills the screen.</p>
    <img
      src="/static/navigation/fit-view-to-everything.gif"
      alt="Fit View to Everything"
      className={classes.img}
    />

    <h2>Fit View to Selected</h2>
    <h4>
      Press <KeyboardKey>S</KeyboardKey> key (or press button in View Settings)
    </h4>
    <p>Zooms so that the selected shots fill the screen.</p>
    <img
      src="/static/navigation/fit-view-to-selected.gif"
      alt="Fit View to Selected"
      className={classes.img}
    />
  </div>
)

export default withStyles(styles)(Navigation)

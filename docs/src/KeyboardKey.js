import * as React from 'react'
import withStyles from '@material-ui/core/styles/withStyles'

const styles = theme => ({
  root: {
    display: 'inline-block',
    padding: theme.spacing.unit / 2,
    borderRadius: theme.spacing.unit / 2,
    border: '1px solid currentColor',
    textAlign: 'center',
    minWidth: theme.spacing.unit * 4,
  },
})

const KeyboardKey = ({ classes, children }) => (
  <div className={classes.root}>{children}</div>
)

export default withStyles(styles)(KeyboardKey)

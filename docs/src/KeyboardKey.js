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
    backgroundColor: theme.palette.grey[200],
  },
})

const KeyboardKey = ({ classes, children }) => (
  <span className={classes.root}>{children}</span>
)

export default withStyles(styles)(KeyboardKey)

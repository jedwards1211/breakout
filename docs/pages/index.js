import * as React from 'react'

import Paper from '@material-ui/core/Paper'
import withStyles from '@material-ui/core/styles/withStyles'

const homeStyles = theme => ({
  root: {
    margin: `${theme.spacing.unit * 2}px auto`,
    maxWidth: 600,
  },
})

const Home = ({ classes }) => (
  <Paper className={classes.root}>This is a test! {process.env.TEST}</Paper>
)

export default withStyles(homeStyles)(Home)

import * as React from 'react'

import withStyles from '@material-ui/core/styles/withStyles'

const homeStyles = theme => ({
  logo: {
    maxWidth: '100%',
  },
})

const Home = ({ classes }) => (
  <img src="/static/logo.png" className={classes.logo} />
)

export default withStyles(homeStyles)(Home)

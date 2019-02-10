/**
 * @flow
 * @prettier
 */

import * as React from 'react'
import Link from './Link'

import AppBar from '@material-ui/core/AppBar'
import IconButton from '@material-ui/core/IconButton'
import MenuIcon from '@material-ui/icons/Menu'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import withStyles from '@material-ui/core/styles/withStyles'

import CloseIcon from '@material-ui/icons/Close'
import Drawer from '@material-ui/core/Drawer'

import List from '@material-ui/core/List'

import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'

import Hidden from '@material-ui/core/Hidden'

const drawerWidth = 250
const appBarGradient = 'linear-gradient(#ba2b50, #700344)'

const styles = theme => ({
  root: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    display: 'flex',
  },
  main: {
    display: 'flex',
    flexGrow: 1,
    flexDirection: 'column',
  },
  grow: {
    flexGrow: 1,
  },
  appBar: {
    flexGrow: 0,
    background: appBarGradient,
  },
  title: {
    flexGrow: 1,
    textDecoration: 'none',
  },
  menuButton: {
    marginLeft: -12,
    marginRight: 20,
  },
  drawer: {
    [theme.breakpoints.up('sm')]: {
      width: drawerWidth,
      flexShrink: 0,
    },
  },
  drawerPaper: {
    width: drawerWidth,
  },
  drawerRoot: {
    display: 'flex',
    flexDirection: 'column',
  },
  drawerAppBar: {
    background: appBarGradient,
    flexGrow: 0,
  },
  drawerList: {
    flexGrow: 1,
    overflow: 'auto',
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing.unit,
    [theme.breakpoints.up('sm')]: {
      padding: theme.spacing.unit * 3,
    },
    overflow: 'auto',
  },
})

class Shell extends React.Component {
  state = {
    drawerOpen: false,
  }

  openDrawer = () => this.setState({ drawerOpen: true })
  closeDrawer = () => this.setState({ drawerOpen: false })

  render() {
    const { classes, children } = this.props

    const drawer = (
      <div className={classes.drawerRoot}>
        <AppBar color="primary" className={classes.drawerAppBar}>
          <Toolbar>
            <Hidden smUp>
              <IconButton
                className={classes.menuButton}
                color="inherit"
                aria-label="Menu"
                onClick={this.closeDrawer}
              >
                <CloseIcon />
              </IconButton>
            </Hidden>
            <Typography variant="h6" color="inherit" className={classes.grow}>
              Contents
            </Typography>
          </Toolbar>
        </AppBar>
        <List className={classes.drawerList}>
          <ListItem button component={Link} href="/importCompass" passHref>
            <ListItemText>Import Compass Data</ListItemText>
          </ListItem>
          <ListItem button component={Link} href="/importWalls" passHref>
            <ListItemText>Import Walls Data</ListItemText>
          </ListItem>
        </List>
      </div>
    )

    return (
      <div className={classes.root}>
        <nav className={classes.drawer}>
          <Hidden smUp implementation="css">
            <Drawer
              variant="temporary"
              anchor="left"
              open={this.state.drawerOpen}
              onClose={this.closeDrawer}
              classes={{ paper: classes.drawerPaper }}
            >
              {drawer}
            </Drawer>
          </Hidden>
          <Hidden xsDown implementation="css">
            <Drawer
              variant="permanent"
              anchor="left"
              open
              classes={{ paper: classes.drawerPaper }}
            >
              {drawer}
            </Drawer>
          </Hidden>
        </nav>
        <main className={classes.main}>
          <AppBar position="fixed" color="primary" className={classes.appBar}>
            <Toolbar>
              <Hidden smUp>
                <IconButton
                  className={classes.menuButton}
                  color="inherit"
                  aria-label="Menu"
                  onClick={this.openDrawer}
                >
                  <MenuIcon />
                </IconButton>
              </Hidden>
              <Typography
                variant="h6"
                color="inherit"
                className={classes.title}
                component={Link}
                href="/"
              >
                Breakout <Hidden xsDown>Cave Survey Visualizer</Hidden>
              </Typography>
            </Toolbar>
          </AppBar>
          <div className={classes.content}>{children}</div>
        </main>
      </div>
    )
  }
}

export default withStyles(styles)(Shell)

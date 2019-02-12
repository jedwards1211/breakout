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
import Hidden from '@material-ui/core/Hidden'
import SidebarLinks from './SidebarLinks'
import { withRouter } from 'next/router'
import GitHub from './icons/GitHub'
import repo from './repo'

import Tooltip from '@material-ui/core/Tooltip'

import NProgress from './NProgress'

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
  gitHubButton: {
    marginLeft: 20,
    marginRight: -12,
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
    paddingTop: 0,
    [theme.breakpoints.up('sm')]: {
      padding: theme.spacing.unit * 3,
      paddingTop: 0,
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

  componentDidUpdate(prevProps) {
    if (prevProps.router.pathname !== this.props.router.pathname) {
      if (this.content) this.content.scrollTop = 0
      this.closeDrawer()
    }
  }

  render() {
    const { theme, classes, children, router } = this.props

    const drawer = (
      <div className={classes.drawerRoot}>
        <AppBar
          position="static"
          color="primary"
          className={classes.drawerAppBar}
        >
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
          <SidebarLinks />
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
          <AppBar position="static" color="primary" className={classes.appBar}>
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
              <Tooltip title="GitHub repository">
                <IconButton
                  color="inherit"
                  component={Link}
                  href={`https://github.com/${repo.owner}/${repo.repo}`}
                  className={classes.gitHubButton}
                >
                  <GitHub />
                </IconButton>
              </Tooltip>
            </Toolbar>
          </AppBar>
          <div key={router.pathname} className={classes.content}>
            {children}
          </div>
        </main>
        <NProgress color={theme.palette.secondary.main} />
      </div>
    )
  }
}

export default withStyles(styles, { withTheme: true })(withRouter(Shell))

import * as React from 'react'
import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'
import Link from './Link'
import withStyles from '@material-ui/core/styles/withStyles'

const styles = ({ palette }) => ({
  activeLink: {
    background: `linear-gradient(${palette.secondary.light}, ${
      palette.secondary.main
    })`,
  },
})

const SidebarLinks = ({ classes: { activeLink } }) => (
  <React.Fragment>
    <ListItem
      button
      component={Link}
      href="/importWalls"
      activeClassName={activeLink}
    >
      <ListItemText>Import Walls Data</ListItemText>
    </ListItem>
    <ListItem
      button
      component={Link}
      href="/changingViews"
      activeClassName={activeLink}
    >
      <ListItemText>Changing Views</ListItemText>
    </ListItem>
    <ListItem
      button
      component={Link}
      href="/navigation"
      activeClassName={activeLink}
    >
      <ListItemText>Navigation</ListItemText>
    </ListItem>
    <ListItem
      button
      component={Link}
      href="/findingStations"
      activeClassName={activeLink}
    >
      <ListItemText>Finding Stations</ListItemText>
    </ListItem>
  </React.Fragment>
)

export default withStyles(styles)(SidebarLinks)

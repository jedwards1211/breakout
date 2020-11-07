import * as React from 'react'
import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'
import Link from './Link'
import withStyles from '@material-ui/core/styles/withStyles'

const styles = ({ palette }) => ({
  activeLink: {
    background: `linear-gradient(${palette.secondary.light}, ${palette.secondary.main})`,
  },
})

const SidebarLink = withStyles(styles)(({ classes, href, text }) => (
  <ListItem
    button
    component={Link}
    href={href}
    activeClassName={classes.activeLink}
  >
    <ListItemText>{text}</ListItemText>
  </ListItem>
))

const SidebarLinks = () => (
  <React.Fragment>
    <SidebarLink href="/download" text="Download" />
    <SidebarLink href="/importCompass" text="Import Compass Data" />
    <SidebarLink href="/importWalls" text="Import Walls Data" />
    <SidebarLink href="/changingViews" text="Changing Views" />
    <SidebarLink href="/navigation" text="Navigation" />
    <SidebarLink href="/findingStations" text="Finding Stations" />
  </React.Fragment>
)

export default SidebarLinks

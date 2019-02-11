import * as React from 'react'
import KeyboardKey from '../src/KeyboardKey'
import withStyles from '@material-ui/core/styles/withStyles'

import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'

import TableCell from '@material-ui/core/TableCell'
import TableRow from '@material-ui/core/TableRow'

import { withRouter } from 'next/router'

import Button from '@material-ui/core/Button'
import Link from '../src/Link'

import TableHead from '@material-ui/core/TableHead'

const styles = theme => ({
  img: {
    maxWidth: '100%',
  },
  titleCell: {
    ...theme.typography.display1,
    paddingLeft: 0,
  },
  actionCell: {
    width: 200,
    fontWeight: 'bold',
    [theme.breakpoints.down('xs')]: {
      width: 100,
    },
  },
  commandCell: {
    width: 300,
    [theme.breakpoints.down('xs')]: {
      width: 200,
    },
  },
  descriptionCell: {
    width: 500,
    [theme.breakpoints.down('xs')]: {
      width: 300,
    },
  },
})

const sharedRows = [['Zoom', 'Scroll', 'Zooms in toward/out from mouse cursor']]

const orthoRows = [['Move', 'Left Click + Drag'], ...sharedRows]

const perspectiveRows = [
  ['Move', 'Right Click + Drag'],
  [
    'Rotate',
    'Left Click + Drag',
    'Orbits around selected shots; if no shots are selected, chooses center of rotation based upon where you click',
  ],
  ...sharedRows,
]

const sharedAdvancedRows = [
  [
    'Zoom toward Selected',
    <span key="shift">
      Hold <KeyboardKey>Shift</KeyboardKey> + Scroll
    </span>,
    'Zooms in toward/out from selected shots',
  ],
  [
    'Finer Movement',
    <span key="ctrl">
      Hold <KeyboardKey>Ctrl</KeyboardKey> while Dragging/Scrolling
    </span>,
    'Makes any other motion more precise',
  ],
]

const orthoAdvancedRows = sharedAdvancedRows

const perspectiveAdvancedRows = [
  ...sharedAdvancedRows,
  [
    'Rotate in Place',
    <span key="shift">
      Hold <KeyboardKey>Shift</KeyboardKey> + Left Click + Drag
    </span>,
    'Rotates the camera without moving it, rather than orbiting around the selected shots',
  ],
  [
    'Walk',
    <span key="shift">
      Hold <KeyboardKey>Shift</KeyboardKey> + Right Click + Drag
    </span>,
    'Dragging vertically moves forward/backward; dragging horizontally turns left/right',
  ],
]

const selectionRows = [
  ['Select a Shot', 'Click on a shot'],
  ['Clear Selection', 'Click on empty space'],
  [
    'Select/unselect additional shots',
    <span key="ctrl">
      Hold <KeyboardKey>Ctrl</KeyboardKey> + Click on a shot
    </span>,
  ],
  [
    'Lasso Shots',
    <span key="ctrl">
      <KeyboardKey>Alt</KeyboardKey> + Click to start, Right Click to end
    </span>,
    'then keep clicking to add more points around shots to select; right click to finish selection',
  ],
]

const sectionCutRows = [
  [
    'Move Front Section Cut Plane',
    <span key="alt">
      Hold <KeyboardKey>Alt</KeyboardKey> + Scroll
    </span>,
  ],
  [
    'Move Back Section Cut Plane',
    <span key="alt">
      Hold <KeyboardKey>Alt</KeyboardKey> + <KeyboardKey>Shift</KeyboardKey> +
      Scroll
    </span>,
  ],
]

const sharedShortcutRows = [
  [
    'Zoom to Selected Shots',
    <span key="key">
      Press <KeyboardKey>S</KeyboardKey> Key
    </span>,
  ],
  [
    'Zoom to Everything',
    <span key="key">
      Press <KeyboardKey>E</KeyboardKey> Key
    </span>,
  ],
]

const perspectiveShortcutRows = [
  [
    'Rotate to Plan',
    <span key="key">
      Press <KeyboardKey>O</KeyboardKey> Key
    </span>,
  ],
  ...sharedShortcutRows,
]

const orthoShortcutRows = sharedShortcutRows

const NavigationTable = ({ title, rows, classes }) => (
  <React.Fragment>
    <TableHead>
      <TableRow>
        <TableCell className={classes.titleCell} colSpan={3}>
          {title}
        </TableCell>
      </TableRow>
    </TableHead>
    <TableBody>
      {rows.map(([action, command, description], key) => (
        <TableRow key={key}>
          <TableCell className={classes.actionCell}>{action}</TableCell>
          <TableCell className={classes.commandCell}>{command}</TableCell>
          <TableCell className={classes.descriptionCell}>
            {description}
          </TableCell>
        </TableRow>
      ))}
    </TableBody>
  </React.Fragment>
)

const Navigation = ({
  classes,
  router: {
    query: { mode },
  },
}) => {
  if (!mode) mode = 'perspective'
  return (
    <div>
      <h1>Navigation</h1>
      <Button
        component={props => (
          <Link {...props} href={{ query: { mode: 'perspective' } }} />
        )}
        variant={mode === 'perspective' ? 'contained' : 'text'}
        color={mode === 'perspective' ? 'primary' : 'default'}
      >
        Perspective View
      </Button>
      <Button
        component={props => (
          <Link {...props} href={{ query: { mode: 'ortho' } }} />
        )}
        variant={mode === 'ortho' ? 'contained' : 'text'}
        color={mode === 'ortho' ? 'primary' : 'default'}
      >
        Plan and Profile Views
      </Button>

      <Table>
        <NavigationTable
          title="Mouse Navigation"
          rows={mode === 'ortho' ? orthoRows : perspectiveRows}
          classes={classes}
        />
        <NavigationTable
          title="Shortcuts"
          rows={mode === 'ortho' ? orthoShortcutRows : perspectiveShortcutRows}
          classes={classes}
        />
        <NavigationTable
          title="Selecting Shots"
          rows={selectionRows}
          classes={classes}
        />
        <NavigationTable
          title="Section Cut"
          rows={sectionCutRows}
          classes={classes}
        />
        <NavigationTable
          title="Advanced Mouse Navigation"
          rows={mode === 'ortho' ? orthoAdvancedRows : perspectiveAdvancedRows}
          classes={classes}
        />
      </Table>
    </div>
  )
}

export default withStyles(styles)(withRouter(Navigation))

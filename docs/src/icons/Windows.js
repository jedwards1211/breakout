import * as React from 'react'
import createSvgIcon from '@material-ui/icons/utils/createSvgIcon'

const Base = createSvgIcon(
  <React.Fragment>
    <path d="m0,12.402,35.687-4.8602,0.0156,34.423-35.67,0.20313zm35.67,33.529,0.0277,34.453-35.67-4.9041-0.002-29.78zm4.3261-39.025,47.318-6.906,0,41.527-47.318,0.37565zm47.329,39.349-0.0111,41.34-47.318-6.6784-0.0663-34.739z" />
  </React.Fragment>,
  'Windows'
)

export default function WindowsIcon(props) {
  return <Base viewBox="0 0 88 88" {...props} />
}

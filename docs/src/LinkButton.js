import * as React from 'react'
import Button from '@material-ui/core/Button'
import Link from './Link'

export default function LinkButton({ href, ...props }) {
  return <Button {...props} component={Link} _href={href} />
}

import * as React from 'react'
import _Link from 'next/link'

const Link = ({
  style,
  className,
  href,
  children,
  replace,
  prefetch,
  scroll,
}) => (
  <_Link href={href} prefetch={prefetch} replace={replace} scroll={scroll}>
    <a style={style} className={className}>
      {children}
    </a>
  </_Link>
)

export default Link

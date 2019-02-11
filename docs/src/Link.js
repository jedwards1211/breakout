import * as React from 'react'
import _Link from 'next/link'
import { withRouter } from 'next/router'

const Link = ({
  style,
  className,
  activeClassName,
  href,
  children,
  replace,
  prefetch,
  router,
  scroll,
}) => (
  <_Link href={href} prefetch={prefetch} replace={replace} scroll={scroll}>
    <a
      style={style}
      className={
        router.pathname === href && activeClassName
          ? `${className} ${activeClassName}`
          : className
      }
    >
      {children}
    </a>
  </_Link>
)

export default withRouter(Link)

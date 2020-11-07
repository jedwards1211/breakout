import * as React from 'react'
import _Link from 'next/link'
import { withRouter } from 'next/router'

const Link = ({
  style,
  className,
  activeClassName,
  _href,
  href = _href,
  children,
  replace,
  prefetch,
  router,
  scroll,
  forwardedRef,
}) => (
  <_Link href={href} prefetch={prefetch} replace={replace} scroll={scroll}>
    <a
      style={style}
      className={
        router.pathname === href && activeClassName
          ? `${className} ${activeClassName}`
          : className
      }
      ref={forwardedRef}
    >
      {children}
    </a>
  </_Link>
)

const Link2 = withRouter(Link)

// eslint-disable-next-line react/display-name
const Link3 = React.forwardRef((props, forwardedRef) => (
  <Link2 {...props} forwardedRef={forwardedRef} />
))

export default Link3

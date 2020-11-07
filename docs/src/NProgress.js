import React from 'react'
import NProgress from 'nprogress'
import Router from 'next/router'
import { withStyles } from '@material-ui/core/styles'

const styles = {
  '@global': {
    '#nprogress': {
      pointerEvents: 'none',
    },
    '#nprogress .bar': {
      background: (props) => props.color,

      position: 'fixed',
      zIndex: 100000,
      top: 0,
      left: 0,

      width: '100%',
      height: 3,
    },

    '#nprogress .peg': {
      display: 'block',
      position: 'absolute',
      right: 0,
      width: 100,
      height: '100%',
      boxShadow: ({ color }) => `0 0 10px ${color}, 0 0 5px ${color}`,
      opacity: 1,
      transform: 'rotate(3deg) translate(0px, -4px)',
    },

    '#nprogress .spinner': {
      display: ({ spinner }) => (spinner ? 'block' : 'none'),
      position: 'fixed',
      zIndex: 100000,
      top: 15,
      right: 15,
    },

    '#nprogress .spinner-icon': {
      width: 18,
      height: 18,
      boxSizing: 'border-box',
      border: 'solid 2 transparent',
      borderTopColor: (props) => props.color,
      borderLeftColor: (props) => props.color,
      borderRadius: '50%',
      animation: 'nprogress-spinner 400ms linear infinite',
    },

    '.nprogress-custom-parent': {
      overflow: 'hidden',
      position: 'relative',
    },

    '.nprogress-custom-parent #nprogress .spinner, .nprogress-custom-parent #nprogress .bar': {
      position: 'absolute',
    },

    '@keyframes nprogress-spinner': {
      '0%': {
        transform: 'rotate(0deg)',
      },
      '100%': {
        transform: 'rotate(360deg)',
      },
    },
  },
}

class NProgressContainer extends React.Component {
  static defaultProps = {
    color: '#2299DD',
    showAfterMs: 300,
    spinner: false,
  }

  timer = null

  routeChangeStart = () => {
    const { showAfterMs } = this.props
    clearTimeout(this.timer)
    this.timer = setTimeout(NProgress.start, showAfterMs)
  }

  routeChangeEnd = () => {
    clearTimeout(this.timer)
    NProgress.done()
  }

  componentDidMount() {
    const { options } = this.props

    if (options) {
      NProgress.configure(options)
    }

    Router.events.on('routeChangeStart', this.routeChangeStart)
    Router.events.on('routeChangeComplete', this.routeChangeEnd)
    Router.events.on('routeChangeError', this.routeChangeEnd)
  }

  componentWillUnmount() {
    clearTimeout(this.timer)
    Router.events.off('routeChangeStart', this.routeChangeStart)
    Router.events.off('routeChangeComplete', this.routeChangeEnd)
    Router.events.off('routeChangeError', this.routeChangeEnd)
  }

  render() {
    return null
  }
}

export default withStyles(styles)(NProgressContainer)

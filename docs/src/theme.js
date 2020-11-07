import { createMuiTheme } from '@material-ui/core/styles'

// A theme with custom primary and secondary color.
// It's optional.
const theme = createMuiTheme({
  palette: {
    gradient: {
      light: '#ff6680',
      main: '#d42f54',
      dark: '#9c002c',
    },
    primary: {
      light: '#d04cb0',
      main: '#9c0a80',
      dark: '#6a0053',
    },
    secondary: {
      light: '#ffe64c',
      main: '#ffb400',
      dark: '#c68500',
    },
  },
  typography: {
    useNextVariants: true,
  },
})

export default theme

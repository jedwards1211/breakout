import React from 'react'
import ReactMarkdown from 'react-markdown'
import withStyles from '@material-ui/core/styles/withStyles'

const imageStyles = (theme) => ({
  root: {
    maxWidth: '90%',
    margin: theme.spacing(4),
    zoom: '50%',
  },
})

const ImageRenderer = withStyles(imageStyles)(({ classes, ...props }) => (
  <img {...props} className={classes.root} />
))

const renderers = {
  image: ImageRenderer,
}

export default function markdownPage(loadMarkdown) {
  return class MarkdownPage extends React.Component {
    static async getInitialProps({ req }) {
      const obj = await loadMarkdown()
      const content = obj && obj.__esModule ? obj.default : obj
      return { content }
    }

    render() {
      return (
        <div>
          <ReactMarkdown source={this.props.content} renderers={renderers} />
        </div>
      )
    }
  }
}

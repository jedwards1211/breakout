import { Octokit } from '@octokit/rest'

export default new Octokit({
  auth: process.env.GH_TOKEN,
})

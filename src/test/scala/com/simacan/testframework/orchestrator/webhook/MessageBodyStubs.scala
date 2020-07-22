package com.simacan.testframework.orchestrator.webhook

import akka.util.ByteString

case class BodyStub(text: String, signature: String) {
  def byteString: ByteString = ByteString(text)
}

case object MessageBodyStubs {

  val faulty: BodyStub = BodyStub(
    """
      |{
      |  "ref": "refs/heads/testing",
      |  "before": "0d9c20517c817692c64a91390473de83637846fa",
      |  "after": "ab63dc6209737b54c8140b8f588de27b4f958774",
      |  "repository": {
      |    "id": 229047034,
      |    "node_id": "MDEwOlJlcG9zaXRvcnkyMjkwNDcwMzQ=",
      |    "name": "TestFramework-Scripts",
      |    "full_name": "simacan/TestFramework-Scripts",
      |    "private": true,
      |    "owner": {
      |      "name": "simacan",
      |      "email": "info@simacan.com",
      |      "login": "simacan",
      |      "id": 3625494,
      |      "node_id": "MDEyOk9yZ2FuaXphdGlvbjM2MjU0OTQ=",
      |      "avatar_url": "https://avatars0.githubusercontent.com/u/3625494?v=4",
      |      "gravatar_id": "",
      |      "url": "https://api.github.com/users/simacan",
      |      "html_url": "https://github.com/simacan",
      |      "followers_url": "https://api.github.com/users/simacan/followers",
      |      "following_url": "https://api.github.com/users/simacan/following{/other_user}",
      |      "gists_url": "https://api.github.com/users/simacan/gists{/gist_id}",
      |      "starred_url": "https://api.github.com/users/simacan/starred{/owner}{/repo}",
      |      "subscriptions_url": "https://api.github.com/users/simacan/subscriptions",
      |      "organizations_url": "https://api.github.com/users/simacan/orgs",
      |      "repos_url": "https://api.github.com/users/simacan/repos",
      |      "events_url": "https://api.github.com/users/simacan/events{/privacy}",
      |      "received_events_url": "https://api.github.com/users/simacan/received_events",
      |      "type": "Organization",
      |      "site_admin": false
      |    },
      |    "html_url": "https://github.com/simacan/TestFramework-Scripts",
      |    "description": "Scripts for test framework",
      |    "fork": false,
      |    "url": "https://github.com/simacan/TestFramework-Scripts",
      |    "forks_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/forks",
      |    "keys_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/keys{/key_id}",
      |    "collaborators_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/collaborators{/collaborator}",
      |    "teams_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/teams",
      |    "hooks_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/hooks",
      |    "issue_events_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/issues/events{/number}",
      |    "events_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/events",
      |    "assignees_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/assignees{/user}",
      |    "branches_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/branches{/branch}",
      |    "tags_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/tags",
      |    "blobs_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/blobs{/sha}",
      |    "git_tags_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/tags{/sha}",
      |    "git_refs_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/refs{/sha}",
      |    "trees_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/trees{/sha}",
      |    "statuses_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/statuses/{sha}",
      |    "languages_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/languages",
      |    "stargazers_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/stargazers",
      |    "contributors_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/contributors",
      |    "subscribers_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/subscribers",
      |    "subscription_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/subscription",
      |    "commits_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/commits{/sha}",
      |    "git_commits_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/commits{/sha}",
      |    "comments_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/comments{/number}",
      |    "issue_comment_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/issues/comments{/number}",
      |    "contents_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/contents/{+path}",
      |    "compare_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/compare/{base}...{head}",
      |    "merges_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/merges",
      |    "archive_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/{archive_format}{/ref}",
      |    "downloads_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/downloads",
      |    "issues_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/issues{/number}",
      |    "pulls_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/pulls{/number}",
      |    "milestones_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/milestones{/number}",
      |    "notifications_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/notifications{?since,all,participating}",
      |    "labels_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/labels{/name}",
      |    "releases_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/releases{/id}",
      |    "deployments_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/deployments",
      |    "created_at": 1576757403,
      |    "updated_at": "2020-01-27T10:03:42Z",
      |    "pushed_at": 1580119606,
      |    "git_url": "git://github.com/simacan/TestFramework-Scripts.git",
      |    "ssh_url": "git@github.com:simacan/TestFramework-Scripts.git",
      |    "clone_url": "https://github.com/simacan/TestFramework-Scripts.git",
      |    "svn_url": "https://github.com/simacan/TestFramework-Scripts",
      |    "homepage": null,
      |    "size": 70,
      |    "stargazers_count": 0,
      |    "watchers_count": 0,
      |    "language": "Scala",
      |    "has_issues": true,
      |    "has_projects": true,
      |    "has_downloads": true,
      |    "has_wiki": true,
      |    "has_pages": false,
      |    "forks_count": 0,
      |    "mirror_url": null,
      |    "archived": false,
      |    "disabled": false,
      |    "open_issues_count": 0,
      |    "license": null,
      |    "forks": 0,
      |    "open_issues": 0,
      |    "watchers": 0,
      |    "default_branch": "develop",
      |    "stargazers": 0,
      |    "master_branch": "develop",
      |    "organization": "simacan"
      |  },
      |  "pusher": {
      |    "name": "Hans-Simacan",
      |    "email": "54808709+Hans-Simacan@users.noreply.github.com"
      |  },
      |  "organization": {
      |    "login": "simacan",
      |    "id": 3625494,
      |    "node_id": "MDEyOk9yZ2FuaXphdGlvbjM2MjU0OTQ=",
      |    "url": "https://api.github.com/orgs/simacan",
      |    "repos_url": "https://api.github.com/orgs/simacan/repos",
      |    "events_url": "https://api.github.com/orgs/simacan/events",
      |    "hooks_url": "https://api.github.com/orgs/simacan/hooks",
      |    "issues_url": "https://api.github.com/orgs/simacan/issues",
      |    "members_url": "https://api.github.com/orgs/simacan/members{/member}",
      |    "public_members_url": "https://api.github.com/orgs/simacan/public_members{/member}",
      |    "avatar_url": "https://avatars0.githubusercontent.com/u/3625494?v=4",
      |    "description": ""
      |  },
      |  "sender": {
      |    "login": "Hans-Simacan",
      |    "id": 54808709,
      |    "node_id": "MDQ6VXNlcjU0ODA4NzA5",
      |    "avatar_url": "https://avatars0.githubusercontent.com/u/54808709?v=4",
      |    "gravatar_id": "",
      |    "url": "https://api.github.com/users/Hans-Simacan",
      |    "html_url": "https://github.com/Hans-Simacan",
      |    "followers_url": "https://api.github.com/users/Hans-Simacan/followers",
      |    "following_url": "https://api.github.com/users/Hans-Simacan/following{/other_user}",
      |    "gists_url": "https://api.github.com/users/Hans-Simacan/gists{/gist_id}",
      |    "starred_url": "https://api.github.com/users/Hans-Simacan/starred{/owner}{/repo}",
      |    "subscriptions_url": "https://api.github.com/users/Hans-Simacan/subscriptions",
      |    "organizations_url": "https://api.github.com/users/Hans-Simacan/orgs",
      |    "repos_url": "https://api.github.com/users/Hans-Simacan/repos",
      |    "events_url": "https://api.github.com/users/Hans-Simacan/events{/privacy}",
      |    "received_events_url": "https://api.github.com/users/Hans-Simacan/received_events",
      |    "type": "User",
      |    "site_admin": false
      |  },
      |  "created": false,
      |  "deleted": false,
      |  "forced": false,
      |  "base_ref": null,
      |  "compare": "https://github.com/simacan/TestFramework-Scripts/compare/0d9c20517c81...ab63dc620973",
      |  "commits": [
      |    {
      |      "id": "ab63dc6209737b54c8140b8f588de27b4f958774",
      |      "tree_id": "455894eec26b47da1ca6089864de4d4d06907ffa",
      |      "distinct": true,
      |      "message": "temp change",
      |      "timestamp": "2020-01-27T11:06:45+01:00",
      |      "url": "https://github.com/simacan/TestFramework-Scripts/commit/ab63dc6209737b54c8140b8f588de27b4f958774",
      |      "author": {
      |        "name": "Hans-Simacan",
      |        "email": "54808709+Hans-Simacan@users.noreply.github.com",
      |        "username": "Hans-Simacan"
      |      },
      |      "committer": {
      |        "name": "GitHub",
      |        "email": "noreply@github.com",
      |        "username": "web-flow"
      |      },
      |      "added": [
      |
      |      ],
      |      "removed": [
      |
      |      ],
      |      "modified": [
      |        "README.md"
      |      ]
      |    }
      |  ],
      |  "head_commit": {
      |    "id": "ab63dc6209737b54c8140b8f588de27b4f958774",
      |    "tree_id": "455894eec26b47da1ca6089864de4d4d06907ffa",
      |    "distinct": true,
      |    "message": "temp change",
      |    "timestamp": "2020-01-27T11:06:45+01:00",
      |    "url": "https://github.com/simacan/TestFramework-Scripts/commit/ab63dc6209737b54c8140b8f588de27b4f958774",
      |    "author": {
      |      "name": "Hans-Simacan",
      |      "email": "54808709+Hans-Simacan@users.noreply.github.com",
      |      "username": "Hans-Simacan"
      |    },
      |    "committer": {
      |      "name": "GitHub",
      |      "email": "noreply@github.com",
      |      "username": "web-flow"
      |    },
      |    "added": [
      |
      |    ],
      |    "removed": [
      |
      |    ],
      |    "modified": [
      |      "README.md"
      |    ]
      |  }
      |}
      |""".stripMargin,
    "sha1=9ab15bff8f6e6eaa247ef484f4f87873dfe7ef87"
  )

  val correct: BodyStub = BodyStub(
    """
      |{
      |  "ref": "refs/heads/develop",
      |  "before": "a80229deb49abf364f8276e78b0e39b92cd325df",
      |  "after": "0d9c20517c817692c64a91390473de83637846fa",
      |  "repository": {
      |    "id": 229047034,
      |    "node_id": "MDEwOlJlcG9zaXRvcnkyMjkwNDcwMzQ=",
      |    "name": "TestFramework-Scripts",
      |    "full_name": "simacan/TestFramework-Scripts",
      |    "private": true,
      |    "owner": {
      |      "name": "simacan",
      |      "email": "info@simacan.com",
      |      "login": "simacan",
      |      "id": 3625494,
      |      "node_id": "MDEyOk9yZ2FuaXphdGlvbjM2MjU0OTQ=",
      |      "avatar_url": "https://avatars0.githubusercontent.com/u/3625494?v=4",
      |      "gravatar_id": "",
      |      "url": "https://api.github.com/users/simacan",
      |      "html_url": "https://github.com/simacan",
      |      "followers_url": "https://api.github.com/users/simacan/followers",
      |      "following_url": "https://api.github.com/users/simacan/following{/other_user}",
      |      "gists_url": "https://api.github.com/users/simacan/gists{/gist_id}",
      |      "starred_url": "https://api.github.com/users/simacan/starred{/owner}{/repo}",
      |      "subscriptions_url": "https://api.github.com/users/simacan/subscriptions",
      |      "organizations_url": "https://api.github.com/users/simacan/orgs",
      |      "repos_url": "https://api.github.com/users/simacan/repos",
      |      "events_url": "https://api.github.com/users/simacan/events{/privacy}",
      |      "received_events_url": "https://api.github.com/users/simacan/received_events",
      |      "type": "Organization",
      |      "site_admin": false
      |    },
      |    "html_url": "https://github.com/simacan/TestFramework-Scripts",
      |    "description": "Scripts for test framework",
      |    "fork": false,
      |    "url": "https://github.com/simacan/TestFramework-Scripts",
      |    "forks_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/forks",
      |    "keys_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/keys{/key_id}",
      |    "collaborators_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/collaborators{/collaborator}",
      |    "teams_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/teams",
      |    "hooks_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/hooks",
      |    "issue_events_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/issues/events{/number}",
      |    "events_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/events",
      |    "assignees_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/assignees{/user}",
      |    "branches_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/branches{/branch}",
      |    "tags_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/tags",
      |    "blobs_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/blobs{/sha}",
      |    "git_tags_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/tags{/sha}",
      |    "git_refs_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/refs{/sha}",
      |    "trees_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/trees{/sha}",
      |    "statuses_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/statuses/{sha}",
      |    "languages_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/languages",
      |    "stargazers_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/stargazers",
      |    "contributors_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/contributors",
      |    "subscribers_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/subscribers",
      |    "subscription_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/subscription",
      |    "commits_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/commits{/sha}",
      |    "git_commits_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/git/commits{/sha}",
      |    "comments_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/comments{/number}",
      |    "issue_comment_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/issues/comments{/number}",
      |    "contents_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/contents/{+path}",
      |    "compare_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/compare/{base}...{head}",
      |    "merges_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/merges",
      |    "archive_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/{archive_format}{/ref}",
      |    "downloads_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/downloads",
      |    "issues_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/issues{/number}",
      |    "pulls_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/pulls{/number}",
      |    "milestones_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/milestones{/number}",
      |    "notifications_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/notifications{?since,all,participating}",
      |    "labels_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/labels{/name}",
      |    "releases_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/releases{/id}",
      |    "deployments_url": "https://api.github.com/repos/simacan/TestFramework-Scripts/deployments",
      |    "created_at": 1576757403,
      |    "updated_at": "2020-01-23T08:14:39Z",
      |    "pushed_at": 1580119419,
      |    "git_url": "git://github.com/simacan/TestFramework-Scripts.git",
      |    "ssh_url": "git@github.com:simacan/TestFramework-Scripts.git",
      |    "clone_url": "https://github.com/simacan/TestFramework-Scripts.git",
      |    "svn_url": "https://github.com/simacan/TestFramework-Scripts",
      |    "homepage": null,
      |    "size": 70,
      |    "stargazers_count": 0,
      |    "watchers_count": 0,
      |    "language": "Scala",
      |    "has_issues": true,
      |    "has_projects": true,
      |    "has_downloads": true,
      |    "has_wiki": true,
      |    "has_pages": false,
      |    "forks_count": 0,
      |    "mirror_url": null,
      |    "archived": false,
      |    "disabled": false,
      |    "open_issues_count": 0,
      |    "license": null,
      |    "forks": 0,
      |    "open_issues": 0,
      |    "watchers": 0,
      |    "default_branch": "develop",
      |    "stargazers": 0,
      |    "master_branch": "develop",
      |    "organization": "simacan"
      |  },
      |  "pusher": {
      |    "name": "Hans-Simacan",
      |    "email": "54808709+Hans-Simacan@users.noreply.github.com"
      |  },
      |  "organization": {
      |    "login": "simacan",
      |    "id": 3625494,
      |    "node_id": "MDEyOk9yZ2FuaXphdGlvbjM2MjU0OTQ=",
      |    "url": "https://api.github.com/orgs/simacan",
      |    "repos_url": "https://api.github.com/orgs/simacan/repos",
      |    "events_url": "https://api.github.com/orgs/simacan/events",
      |    "hooks_url": "https://api.github.com/orgs/simacan/hooks",
      |    "issues_url": "https://api.github.com/orgs/simacan/issues",
      |    "members_url": "https://api.github.com/orgs/simacan/members{/member}",
      |    "public_members_url": "https://api.github.com/orgs/simacan/public_members{/member}",
      |    "avatar_url": "https://avatars0.githubusercontent.com/u/3625494?v=4",
      |    "description": ""
      |  },
      |  "sender": {
      |    "login": "Hans-Simacan",
      |    "id": 54808709,
      |    "node_id": "MDQ6VXNlcjU0ODA4NzA5",
      |    "avatar_url": "https://avatars0.githubusercontent.com/u/54808709?v=4",
      |    "gravatar_id": "",
      |    "url": "https://api.github.com/users/Hans-Simacan",
      |    "html_url": "https://github.com/Hans-Simacan",
      |    "followers_url": "https://api.github.com/users/Hans-Simacan/followers",
      |    "following_url": "https://api.github.com/users/Hans-Simacan/following{/other_user}",
      |    "gists_url": "https://api.github.com/users/Hans-Simacan/gists{/gist_id}",
      |    "starred_url": "https://api.github.com/users/Hans-Simacan/starred{/owner}{/repo}",
      |    "subscriptions_url": "https://api.github.com/users/Hans-Simacan/subscriptions",
      |    "organizations_url": "https://api.github.com/users/Hans-Simacan/orgs",
      |    "repos_url": "https://api.github.com/users/Hans-Simacan/repos",
      |    "events_url": "https://api.github.com/users/Hans-Simacan/events{/privacy}",
      |    "received_events_url": "https://api.github.com/users/Hans-Simacan/received_events",
      |    "type": "User",
      |    "site_admin": false
      |  },
      |  "created": false,
      |  "deleted": false,
      |  "forced": false,
      |  "base_ref": null,
      |  "compare": "https://github.com/simacan/TestFramework-Scripts/compare/a80229deb49a...0d9c20517c81",
      |  "commits": [
      |    {
      |      "id": "0d9c20517c817692c64a91390473de83637846fa",
      |      "tree_id": "a57e152a360253ea4173eba0ffc464a8eeae1a1d",
      |      "distinct": true,
      |      "message": "Updated README.md",
      |      "timestamp": "2020-01-27T11:03:39+01:00",
      |      "url": "https://github.com/simacan/TestFramework-Scripts/commit/0d9c20517c817692c64a91390473de83637846fa",
      |      "author": {
      |        "name": "Hans-Simacan",
      |        "email": "54808709+Hans-Simacan@users.noreply.github.com",
      |        "username": "Hans-Simacan"
      |      },
      |      "committer": {
      |        "name": "GitHub",
      |        "email": "noreply@github.com",
      |        "username": "web-flow"
      |      },
      |      "added": [
      |
      |      ],
      |      "removed": [
      |
      |      ],
      |      "modified": [
      |        "README.md"
      |      ]
      |    }
      |  ],
      |  "head_commit": {
      |    "id": "0d9c20517c817692c64a91390473de83637846fa",
      |    "tree_id": "a57e152a360253ea4173eba0ffc464a8eeae1a1d",
      |    "distinct": true,
      |    "message": "Updated README.md",
      |    "timestamp": "2020-01-27T11:03:39+01:00",
      |    "url": "https://github.com/simacan/TestFramework-Scripts/commit/0d9c20517c817692c64a91390473de83637846fa",
      |    "author": {
      |      "name": "Hans-Simacan",
      |      "email": "54808709+Hans-Simacan@users.noreply.github.com",
      |      "username": "Hans-Simacan"
      |    },
      |    "committer": {
      |      "name": "GitHub",
      |      "email": "noreply@github.com",
      |      "username": "web-flow"
      |    },
      |    "added": [
      |
      |    ],
      |    "removed": [
      |
      |    ],
      |    "modified": [
      |      "README.md"
      |    ]
      |  }
      |}
      |""".stripMargin,
    "sha1=5a93e4abd40bf203fcbe5d2ac2fcd9581f037db9"
  )
}

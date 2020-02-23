package self.com.gituserfinder.data.remote

import self.com.gituserfinder.data.model.UserResponse

object TestUtils {

    fun getMockGithubResponseData(): String {
        return """
            {
              "total_count": 2756,
              "incomplete_results": false,
              "items": [
                {
                  "login": "octo",
                  "id": 116087,
                  "node_id": "MDQ6VXNlcjExNjA4Nw==",
                  "avatar_url": "https://avatars0.githubusercontent.com/u/116087?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/octo",
                  "html_url": "https://github.com/octo",
                  "followers_url": "https://api.github.com/users/octo/followers",
                  "following_url": "https://api.github.com/users/octo/following{/other_user}",
                  "gists_url": "https://api.github.com/users/octo/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/octo/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/octo/subscriptions",
                  "organizations_url": "https://api.github.com/users/octo/orgs",
                  "repos_url": "https://api.github.com/users/octo/repos",
                  "events_url": "https://api.github.com/users/octo/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/octo/received_events",
                  "type": "User",
                  "site_admin": false,
                  "score": 137.8377
                },
                {
                  "login": "octocat",
                  "id": 583231,
                  "node_id": "MDQ6VXNlcjU4MzIzMQ==",
                  "avatar_url": "https://avatars3.githubusercontent.com/u/583231?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/octocat",
                  "html_url": "https://github.com/octocat",
                  "followers_url": "https://api.github.com/users/octocat/followers",
                  "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                  "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                  "organizations_url": "https://api.github.com/users/octocat/orgs",
                  "repos_url": "https://api.github.com/users/octocat/repos",
                  "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/octocat/received_events",
                  "type": "User",
                  "site_admin": false,
                  "score": 106.85666
                },
                {
                  "login": "octonion",
                  "id": 521890,
                  "node_id": "MDQ6VXNlcjUyMTg5MA==",
                  "avatar_url": "https://avatars0.githubusercontent.com/u/521890?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/octonion",
                  "html_url": "https://github.com/octonion",
                  "followers_url": "https://api.github.com/users/octonion/followers",
                  "following_url": "https://api.github.com/users/octonion/following{/other_user}",
                  "gists_url": "https://api.github.com/users/octonion/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/octonion/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/octonion/subscriptions",
                  "organizations_url": "https://api.github.com/users/octonion/orgs",
                  "repos_url": "https://api.github.com/users/octonion/repos",
                  "events_url": "https://api.github.com/users/octonion/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/octonion/received_events",
                  "type": "User",
                  "site_admin": false,
                  "score": 98.89368
                },
                {
                  "login": "OctopusLian",
                  "id": 25405160,
                  "node_id": "MDQ6VXNlcjI1NDA1MTYw",
                  "avatar_url": "https://avatars2.githubusercontent.com/u/25405160?v=4",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/OctopusLian",
                  "html_url": "https://github.com/OctopusLian",
                  "followers_url": "https://api.github.com/users/OctopusLian/followers",
                  "following_url": "https://api.github.com/users/OctopusLian/following{/other_user}",
                  "gists_url": "https://api.github.com/users/OctopusLian/gists{/gist_id}",
                  "starred_url": "https://api.github.com/users/OctopusLian/starred{/owner}{/repo}",
                  "subscriptions_url": "https://api.github.com/users/OctopusLian/subscriptions",
                  "organizations_url": "https://api.github.com/users/OctopusLian/orgs",
                  "repos_url": "https://api.github.com/users/OctopusLian/repos",
                  "events_url": "https://api.github.com/users/OctopusLian/events{/privacy}",
                  "received_events_url": "https://api.github.com/users/OctopusLian/received_events",
                  "type": "User",
                  "site_admin": false,
                  "score": 77.71609
                }
              ]
            }
        """.trimIndent()
    }

    fun getMockGithubErrorResponse400Data(): String {
        return """
            {"message":"Problems parsing JSON"}
        """.trimIndent()
    }


    fun getMockGithubErrorResponse422Data(): String {
        return """
            {
              "message": "Validation Failed",
              "errors": [
                {
                  "resource": "Issue",
                  "field": "title",
                  "code": "missing_field"
                }
              ]
            }
        """.trimIndent()
    }

    fun getMockGithubErrorResponse404Data(): String {
        return """
            {
              "message": "Not Found",
              "documentation_url": "https://developer.github.com/v3"
            }
        """.trimIndent()
    }

    fun getDummyUserResponse(
        withLogin: String = "",
        withId: Long = 0L,
        withNodeId: String = "",
        withAvatarUrl: String = "",
        withGravatarId: String = "",
        withUrl: String = "",
        withHtmlUrl: String = "",
        withFollowersUrl: String = "",
        withFollowingUrl: String = "",
        withGistsUrl: String = "",
        withStarredUrl: String = "",
        withSubscriptionsUrl: String = "",
        withOrganizationsUrl: String = "",
        withReposUrl: String = "",
        withEventsUrl: String = "",
        withReceivedEventsUrl: String = "",
        withType: String = "",
        withSiteAdmin: String = "",
        withScore: Double = 0.0
    ): UserResponse {
        return UserResponse(
            withLogin,
            withId,
            withNodeId,
            withAvatarUrl,
            withGravatarId,
            withUrl,
            withHtmlUrl,
            withFollowersUrl,
            withFollowingUrl,
            withGistsUrl,
            withStarredUrl,
            withSubscriptionsUrl,
            withOrganizationsUrl,
            withReposUrl,
            withEventsUrl,
            withReceivedEventsUrl,
            withType,
            withSiteAdmin,
            withScore
        )
    }

}
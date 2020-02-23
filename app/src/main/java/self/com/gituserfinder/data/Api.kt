package self.com.gituserfinder.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import self.com.gituserfinder.data.model.GithubResponse

interface Api {

    @GET("/search/users")
    fun fetchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") limit: Int
    ): Single<GithubResponse>
}
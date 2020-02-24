package self.com.gituserfinder.data.model

import com.google.gson.annotations.SerializedName

data class GithubResponse(
    @SerializedName("total_count")
    val totalCount: Long,
    @SerializedName("incomplete_results")
    val noMoreResult: Boolean,
    @SerializedName("items")
    val users: List<UserResponse>
)
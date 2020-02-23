package self.com.gituserfinder.data.remote

import com.google.gson.Gson
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.Function
import retrofit2.HttpException
import self.com.gituserfinder.data.Api
import self.com.gituserfinder.data.model.ErrorResponse
import self.com.gituserfinder.data.model.GithubResponse

interface RemoteGateway {
    fun getUsers(query: String, page: Int): Single<GithubResponse>
}

class RemoteGatewayImpl(private val api: Api, private val ioScheduler: Scheduler) : RemoteGateway {

    override fun getUsers(query: String, page: Int): Single<GithubResponse> {
        return api.fetchUsers(query, page, PER_PAGE_COUNT)
            .subscribeOn(ioScheduler)
            .onErrorResumeNext(mapErrorResponse())
    }


    private fun mapErrorResponse() = Function<Throwable, Single<GithubResponse>> { throwable ->
        if (throwable is HttpException) {
            val errorCode = throwable.code()
            val exception = when (errorCode == 400 || errorCode == 404 || errorCode == 422) {
                true -> {
                    val errorMessage = throwable.response().errorBody()?.string()
                    if (errorMessage.isNullOrBlank()) {
                        throwable
                    } else {
                        Gson().fromJson(errorMessage, ErrorResponse::class.java)
                    }
                }
                false -> {
                    throwable
                }
            }
            Single.error(exception)
        } else {
            Single.error(throwable)
        }
    }

    companion object {
        private const val PER_PAGE_COUNT = 30
    }

}
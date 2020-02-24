package self.com.gituserfinder.domain.usecase

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.BehaviorSubject
import self.com.gituserfinder.data.model.GithubResponse
import self.com.gituserfinder.data.remote.RemoteGateway
import self.com.gituserfinder.domain.model.UserModel
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase.State
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase.State.Data.FullyLoaded
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase.State.Data.PartialLoaded

interface GetGithubUserUseCase {
    fun observeState(): Observable<State>
    fun getUsers(username: String)
    fun getMore()
    fun dispose()

    sealed class State {
        object Loading : State()
        object LoadMoreFailed : State()
        data class Error(val message: String) : State()
        sealed class Data(open val users: List<UserModel>) : State() {
            object Empty : Data(emptyList())
            data class PartialLoaded(override val users: List<UserModel>) : Data(users)
            data class FullyLoaded(override val users: List<UserModel>) : Data(users)
        }
    }
}

class GetGithubUserUseCaseImpl(
    private val gateway: RemoteGateway
) : GetGithubUserUseCase {

    private var currentQuery = ""
    private var currentPage = 1
    private val currentUsers = mutableListOf<UserModel>()
    private val pubsub = BehaviorSubject.create<State>()
    private val serialDisposable = SerialDisposable()

    override fun observeState() = pubsub

    override fun getUsers(username: String) {
        this.currentQuery = username
        this.currentPage = 1
        currentUsers.clear()

        fetchUser(false)
    }

    override fun getMore() {
        if (currentQuery.isBlank() || currentUsers.isEmpty() || (pubsub.hasValue() && pubsub.value is FullyLoaded))
            return

        fetchUser(true)
    }

    override fun dispose() {
        serialDisposable.dispose()
    }

    private fun fetchUser(isLoadMore: Boolean) {
        gateway.getUsers(currentQuery, currentPage)
            .handleLoadingState(isLoadMore)
            .subscribe({
                currentUsers.addAll(it.toUserModels())
                val state = if (!it.noMoreResult) {
                    currentPage += 1
                    PartialLoaded(currentUsers)
                } else if (currentUsers.isEmpty()) {
                    State.Data.Empty
                } else {
                    FullyLoaded(currentUsers)
                }
                pubsub.onNext(state)
            }, {
                it.handleErrorState(isLoadMore)
            }).let(serialDisposable::set)

    }

    private fun Single<GithubResponse>.handleLoadingState(isLoadMore: Boolean): Single<GithubResponse> {
        return this.doOnSubscribe {
            if (!isLoadMore) {
                pubsub.onNext(State.Loading)
            }
        }
    }

    private fun Throwable.handleErrorState(isLoadMore: Boolean) {
        if (isLoadMore) {
            pubsub.onNext(State.LoadMoreFailed)
        } else {
            pubsub.onNext(State.Error(message ?: "Unknown Error"))
        }
    }

    private fun GithubResponse.toUserModels(): List<UserModel> {
        return users
            .map { user -> UserModel(user.login, user.avatarUrl, user.url) }
    }
}

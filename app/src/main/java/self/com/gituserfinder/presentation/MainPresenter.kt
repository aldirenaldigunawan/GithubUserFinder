package self.com.gituserfinder.presentation

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase

class MainPresenter(
    private val usecase: GetGithubUserUseCase,
    private val uiScheduler: Scheduler
) : MainContract.Presenter {
    private lateinit var view: MainContract.View
    private lateinit var disposable: Disposable
    override fun attachView(view: MainContract.View) {
        this.view = view

        disposable = usecase.observeState()
            .observeOn(uiScheduler)
            .doOnNext { if (it !is GetGithubUserUseCase.State.Loading) view.hideProgress() }
            .subscribe({
                when (it) {
                    is GetGithubUserUseCase.State.Error -> view.showError(it.message)
                    is GetGithubUserUseCase.State.Loading -> view.showProgress()
                    is GetGithubUserUseCase.State.LoadMoreProgress -> {
                    }
                    is GetGithubUserUseCase.State.LoadMoreFailed -> {
                        view.replaceLastItem(
                            MainContract.MainViewObject.LoadMoreProgress,
                            MainContract.MainViewObject.LoadMoreButton
                        )
                    }
                    is GetGithubUserUseCase.State.Data.PartialLoaded -> {
                        val viewObject =
                            it.users.map(MainContract.MainViewObject::Item) + listOf(
                                MainContract.MainViewObject.LoadMoreProgress
                            )
                        view.renderUsers(viewObject)
                    }
                    is GetGithubUserUseCase.State.Data.FullyLoaded -> {
                        view.renderUsers(it.users.map(MainContract.MainViewObject::Item))
                    }
                }

            }, {

            })
    }

    override fun detachView() {
        disposable.dispose()
    }

    override fun onSearchClick(input: String) {
        usecase.getUsers(input)
    }

    override fun onLoadMore() {
        view.replaceLastItem(
            MainContract.MainViewObject.LoadMoreButton,
            MainContract.MainViewObject.LoadMoreProgress
        )
        usecase.getMore()
    }

}
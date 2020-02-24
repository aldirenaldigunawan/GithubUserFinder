package self.com.gituserfinder.presentation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import self.com.gituserfinder.domain.model.UserModel
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase.State
import self.com.gituserfinder.presentation.MainContract.MainViewObject.LoadMoreButton
import self.com.gituserfinder.presentation.MainContract.MainViewObject.LoadMoreProgress
import self.com.gituserfinder.presentation.MainContract.Presenter
import self.com.gituserfinder.presentation.MainContract.View

class MainPresenterTest {

    private lateinit var usecase: GetGithubUserUseCase
    private lateinit var presenter: Presenter
    private lateinit var view: View


    @Before
    fun setup() {
        view = mock()
        usecase = mock()
        presenter = MainPresenter(usecase, Schedulers.trampoline())
        whenever(usecase.observeState()).thenReturn(Observable.never())
    }

    @Test
    fun given_view_attached_to_view__should_observe_usecase_state() {
        presenter.attachView(view)

        verify(usecase).observeState()
    }

    @Test
    fun given_onSearchClick__should_call_usecase_loadUser() {
        val query = "random query"
        presenter.onSearchClick(query)

        verify(usecase).getUsers(query)
    }

    @Test
    fun given_load_more_trigger__should_call_view_to_replace_last_item_to_progress() {
        presenter.attachView(view)
        presenter.onLoadMore()

        verify(view).replaceLastItem(LoadMoreButton, LoadMoreProgress)
    }

    @Test
    fun given_load_more_Trigger__should_call_usecase_GetMore() {
        presenter.attachView(view)
        presenter.onLoadMore()

        verify(usecase).getMore()
    }

    @Test
    fun given_presenter_detach__should_dispose_usecase() {
        presenter.attachView(view)
        presenter.detachView()

        verify(usecase).dispose()
    }

    @Test
    fun given_usecase_state_changed__should_handle_the_state() {
        fun testCase(name: String, state: State, block: () -> Unit) {
            reset(view)
            whenever(usecase.observeState()).thenReturn(Observable.just(state))
            presenter.attachView(view)

            block()
        }

        testCase("Loading state should show progress", State.Loading) {
            verify(view).showProgress()
        }
        testCase("Beside loading state, should hide progress", randomStateExcludeLoading()) {
            verify(view).hideProgress()
        }

        testCase("Error state, should show error", State.Error("random message")) {
            verify(view).showError("random message")
        }

        testCase("State LoadMoreFailed, should replace progress to button", State.LoadMoreFailed) {
            verify(view).replaceLastItem(LoadMoreProgress, LoadMoreButton)
        }

        testCase("State PartialLoaded, should show users and progress view at the end", State.Data.PartialLoaded(generateUserModels(1..5))) {
            val expectedViewObjects = generateUserModels(1..5).map { MainContract.MainViewObject.Item(it) } + listOf(LoadMoreProgress)
            verify(view).renderUsers(expectedViewObjects)
        }

        testCase("State FullyLoaded, should show users without progress load more", State.Data.FullyLoaded(generateUserModels(1..5))) {
            val expectedViewObjects = generateUserModels(1..5).map { MainContract.MainViewObject.Item(it) }
            verify(view).renderUsers(expectedViewObjects)
        }

        testCase("given state is empty data, should show no result to view", State.Data.Empty){
            verify(view).showEmptyResult()
        }
    }

    private fun randomStateExcludeLoading(): State {
        return listOf(
            State.LoadMoreFailed,
            State.Error(""),
            State.Data.Empty,
            State.Data.FullyLoaded(emptyList()),
            State.Data.PartialLoaded(
                emptyList()
            )
        ).shuffled().first()
    }

    private fun generateUserModels(range: IntRange): List<UserModel>{
        return range.map { generateUserModel("Name - $it", "Avatar-$it", "url-$it") }
    }

    private fun generateUserModel(withUserName: String = "", withAvatar: String = "", withUrl: String = "") =
        UserModel(withUserName, withAvatar, withUrl)
}
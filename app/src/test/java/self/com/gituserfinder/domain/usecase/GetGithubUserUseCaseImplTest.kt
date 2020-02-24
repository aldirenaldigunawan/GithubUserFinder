package self.com.gituserfinder.domain.usecase

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import self.com.gituserfinder.data.model.GithubResponse
import self.com.gituserfinder.data.remote.RemoteGateway
import self.com.gituserfinder.data.remote.TestUtils.getDummyUserResponse
import self.com.gituserfinder.domain.model.UserModel
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase.State.Data.FullyLoaded
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase.State.Data.PartialLoaded
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase.State.Loading

class GetGithubUserUseCaseImplTest {

    private lateinit var gateway: RemoteGateway
    private lateinit var usecase: GetGithubUserUseCase

    @Before
    fun setup() {
        gateway = mock()
        usecase = GetGithubUserUseCaseImpl(gateway)

        whenever(gateway.getUsers(any(), any())).thenReturn(Single.never())
    }

    //region Load User
    @Test
    fun given_observe_state_without_any_action__should_has_no_value() {
        usecase.observeState().test().assertNoValues().assertNotComplete()
    }

    @Test
    fun given_username_to_getUser__should_update_state_to_loading() {
        val username = "random"

        usecase.getUsers(username)

        usecase.observeState().test().assertValue(Loading)
    }

    @Test
    fun given_response_come_from_gateway__with_still_has_result__should_update_state_to_partial_loaded() {
        val username = "random"
        val userResponse = (1..5).map {
            getDummyUserResponse("user - $it")
        }
        val response = GithubResponse(0, false, userResponse)

        whenever(gateway.getUsers(username, 1)).thenReturn(Single.just(response))

        usecase.getUsers(username)

        val expectedUserModels = userResponse.map {
            UserModel(
                it.login,
                it.avatarUrl,
                it.url
            )
        }
        usecase.observeState().test().assertValue(PartialLoaded(expectedUserModels))
    }

    @Test
    fun given_response_come_from_gateway__with_no_more_data_to_load__should_update_state_to_fully_loaded() {
        val username = "random"
        val userResponse = (1..5).map {
            getDummyUserResponse("user - $it")
        }
        val response = GithubResponse(0, true, userResponse)

        whenever(gateway.getUsers(username, 1)).thenReturn(Single.just(response))

        usecase.getUsers(username)

        val expectedUserModels = userResponse.map {
            UserModel(
                it.login,
                it.avatarUrl,
                it.url
            )
        }
        usecase.observeState().test().assertValue(FullyLoaded(expectedUserModels))
    }

    //endregion

    //region Load More
    @Test
    fun given_get_more_trigger_without_previous_load_data__should_do_nothing() {
        usecase.getMore()

        verifyZeroInteractions(gateway)
    }

    @Test
    fun given_partially_loaded__when_get_more__should_increase_page_number() {
        val username = "random"
        val userResponse = (1..5).map {
            getDummyUserResponse("user - $it")
        }
        val response = GithubResponse(0, false, userResponse)

        whenever(gateway.getUsers(username, 1)).thenReturn(Single.just(response))
        whenever(gateway.getUsers(username, 2)).thenReturn(Single.just(response))

        usecase.getUsers(username)

        usecase.getMore()

        verify(gateway).getUsers(username, 1)
        verify(gateway).getUsers(username, 2)
    }

    @Test
    fun given_partially_loaded__when_get_more__should_append_user_to_previous_list() {
        val username = "random"
        val userResponse = (1..5).map {
            getDummyUserResponse("user - $it")
        }
        val userResponseNew = (20..25).map {
            getDummyUserResponse("user - $it")
        }
        val response = GithubResponse(0, false, userResponse)
        val responseNew = GithubResponse(0, false, userResponseNew)

        whenever(gateway.getUsers(username, 1)).thenReturn(Single.just(response))
        whenever(gateway.getUsers(username, 2)).thenReturn(Single.just(responseNew))

        usecase.getUsers(username)

        usecase.getMore()


        val expectedUserModels = userResponse.map {
            UserModel(
                it.login,
                it.avatarUrl,
                it.url
            )
        }
        val expectedUserModelsNew = userResponseNew.map {
            UserModel(
                it.login,
                it.avatarUrl,
                it.url
            )
        }
        val expectedState = PartialLoaded(expectedUserModels + expectedUserModelsNew)
        usecase.observeState().test().assertValue(expectedState)
    }

    @Test
    fun given_is_fully_loaded__when_get_More__should_do_nothing() {
        val username = "random"
        val userResponse = (1..5).map {
            getDummyUserResponse("user - $it")
        }
        val response = GithubResponse(0, true, userResponse)

        whenever(gateway.getUsers(username, 1)).thenReturn(Single.just(response))

        usecase.getUsers(username)

        usecase.getMore()

        verify(gateway).getUsers(username, 1)
        verify(gateway, never()).getUsers(any(), eq(2))
    }
    //endregion
}


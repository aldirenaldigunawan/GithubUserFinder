package self.com.gituserfinder.data.remote

import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import self.com.gituserfinder.data.Api
import self.com.gituserfinder.data.model.ErrorResponse
import self.com.gituserfinder.data.model.GithubResponse
import self.com.gituserfinder.data.remote.TestUtils.getMockGithubErrorResponse400Data
import self.com.gituserfinder.data.remote.TestUtils.getMockGithubErrorResponse404Data
import self.com.gituserfinder.data.remote.TestUtils.getMockGithubErrorResponse422Data
import self.com.gituserfinder.data.remote.TestUtils.getMockGithubResponseData
import java.util.*

class RemoteGatewayImplTest {

    @get:Rule
    val rule = MockWebServer()

    private lateinit var gateway: RemoteGateway
    private val searchQuery = UUID.randomUUID().toString()
    private val searchPageNumber = (1..5).random()

    @Before
    fun setup() {
        val api = Retrofit.Builder().baseUrl(rule.url(""))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Api::class.java)
        gateway = RemoteGatewayImpl(api, Schedulers.trampoline())
    }

    @Test
    fun `given query and page number when api response ok, should return list of users`() {

        val responseData = getMockGithubResponseData()

        rule.enqueue(MockResponse().setBody(responseData))

        val expectedResponseToObject = Gson().fromJson<GithubResponse>(responseData, GithubResponse::class.java)

        gateway.getUsers(searchQuery, searchPageNumber).test().assertValue(expectedResponseToObject)
    }

    @Test
    fun `given query and page number when api response error of bad request`() {

        fun testCase(withResponse: String, errorCode: Int, expectedErrorResponse: ErrorResponse) {
            rule.enqueue(MockResponse().setBody(withResponse).setResponseCode(errorCode))
            gateway.getUsers(searchQuery, searchPageNumber).test()
                .assertError(ErrorResponse::class.java)
                .assertError(expectedErrorResponse)
        }

        testCase(getMockGithubErrorResponse400Data(), 400, ErrorResponse("Problems parsing JSON"))
        testCase(getMockGithubErrorResponse404Data(), 404, ErrorResponse("Not Found"))
        testCase(getMockGithubErrorResponse422Data(), 422, ErrorResponse("Validation Failed"))

    }

    @Test
    fun `given query and page number when api response error 500, should return http error`() {

        val responseData = ""

        rule.enqueue(MockResponse().setBody(responseData).setResponseCode(500))

        gateway.getUsers(searchQuery, searchPageNumber).test().assertError(HttpException::class.java)
    }

}
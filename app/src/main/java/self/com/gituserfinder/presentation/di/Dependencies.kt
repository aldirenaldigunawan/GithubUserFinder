package self.com.gituserfinder.presentation.di

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import self.com.gituserfinder.data.Api
import self.com.gituserfinder.data.remote.RemoteGateway
import self.com.gituserfinder.data.remote.RemoteGatewayImpl
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCase
import self.com.gituserfinder.domain.usecase.GetGithubUserUseCaseImpl
import self.com.gituserfinder.presentation.MainContract
import self.com.gituserfinder.presentation.MainPresenter

object Dependencies {

    private fun generateApi(): Api {
        return retrofitFactory().create(Api::class.java)
    }

    fun generatePresenter(): MainContract.Presenter {
        return MainPresenter(generateUseCase(), AndroidSchedulers.mainThread())
    }

    private fun generateUseCase(): GetGithubUserUseCase {
        return GetGithubUserUseCaseImpl(generateGateway())
    }

    private fun generateGateway(): RemoteGateway {
        return RemoteGatewayImpl(generateApi(), Schedulers.io())
    }

    private fun retrofitFactory(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(generateOkHttpClient())
            .build()
    }

    private fun generateOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            ).build()
    }
}
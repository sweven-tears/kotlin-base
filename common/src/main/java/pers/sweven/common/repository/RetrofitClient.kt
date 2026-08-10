package pers.sweven.common.repository

import android.annotation.SuppressLint
import android.os.Build
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import android.util.Log
import pers.sweven.common.BuildConfig
import pers.sweven.common.GlobalApp
import pers.sweven.common.repository.converter.GsonConverterBodyFactory
import pers.sweven.common.repository.cookie.CookieJarImpl
import pers.sweven.common.repository.cookie.store.PersistentCookieStore
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitClient private constructor(
    private val config: Config,
) {
    private val okHttpClient: OkHttpClient by lazy { createOkHttpClient() }
    private val retrofit: Retrofit by lazy { createRetrofit() }

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            // 基本配置
            connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
            readTimeout(config.readTimeout, TimeUnit.SECONDS)
            writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
            retryOnConnectionFailure(config.retryOnConnectionFailure)

            cache(config.cache)

            // SSL配置
            config.sslConfig?.apply {
                sslSocketFactory(sslSocketFactory, trustManager)
                hostnameVerifier(hostnameVerifier)
            }

            // Cookie配置
            config.cookieJar?.let { cookieJar(it) }

            // 拦截器
            config.interceptors.forEach { addInterceptor(it) }
            config.networkInterceptors.forEach { addNetworkInterceptor(it) }
        }.build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .apply {
                config.converterFactories.forEach { addConverterFactory(it) }
                config.callAdapterFactories.forEach { addCallAdapterFactory(it) }
            }
            .build()
    }

    data class Config(
        val baseUrl: String,
        val connectTimeout: Long = 30,
        val readTimeout: Long = 30,
        val writeTimeout: Long = 30,
        val retryOnConnectionFailure: Boolean = true,
        val sslConfig: SslConfig? = null,
        val cookieJar: CookieJar? = null,
        val cache: Cache? = null,
        val interceptors: List<Interceptor> = emptyList(),
        val networkInterceptors: List<Interceptor> = emptyList(),
        val converterFactories: List<Converter.Factory> = emptyList(),
        val callAdapterFactories: List<CallAdapter.Factory> = emptyList(),
    )

    class SslConfig(
        val sslSocketFactory: javax.net.ssl.SSLSocketFactory,
        val trustManager: X509TrustManager,
        val hostnameVerifier: HostnameVerifier,
    )

    companion object {

        /**
         * 网络日志开关。默认跟随库自身 [pers.sweven.common.BuildConfig.DEBUG]
         * （发布的 AAR 中该值恒为 false，即默认不打印日志）。
         * 若三方需要在自己的 debug 构建中查看网络日志，可调用 [setLogEnabled](true)。
         */
        @JvmStatic
        fun isLogEnabled(): Boolean = _logEnabled

        @JvmStatic
        fun setLogEnabled(enabled: Boolean) {
            _logEnabled = enabled
        }

        private var _logEnabled: Boolean = BuildConfig.DEBUG

        fun create(
            baseUrl: String,
            connectTimeout: Long = 30,
            readTimeout: Long = 30,
            writeTimeout: Long = 30,
            retryOnConnectionFailure: Boolean = true,
            sslConfig: SslConfig? = createTrustAllSslConfig(),
            cookieJar: CookieJar? = createCookieJar(),
            cache: Cache? = null,
            interceptors: List<Interceptor> = createInterceptors(),
            networkInterceptors: List<Interceptor> = createNetworkInterceptors(),
            converterFactories: List<Converter.Factory> = createConverter(),
            callAdapterFactories: List<CallAdapter.Factory> = createCallAdapter(),
        ) = create(
            Config(
                baseUrl = baseUrl,
                connectTimeout = connectTimeout,
                readTimeout = readTimeout,
                writeTimeout = writeTimeout,
                retryOnConnectionFailure = retryOnConnectionFailure,
                sslConfig = sslConfig,
                cookieJar = cookieJar,
                cache = cache,
                interceptors = interceptors,
                networkInterceptors = networkInterceptors,
                converterFactories = converterFactories,
                callAdapterFactories = callAdapterFactories,
            )
        )

        fun create(config: Config) = RetrofitClient(config)

        /**
         * 创建信任所有 SSL 配置
         * @return [SslConfig]
         */
        fun createTrustAllSslConfig(): SslConfig {
            @SuppressLint("CustomX509TrustManager")
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?,
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?,
                ) {
                }

                override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())

            return SslConfig(
                sslSocketFactory = sslContext.socketFactory,
                trustManager = trustAllCerts[0] as X509TrustManager,
                hostnameVerifier = { _, _ -> true }
            )
        }

        /**
         * 创建默认标头拦截器
         * @return [Interceptor]
         */
        fun createDefaultHeaderInterceptor(vararg pairs: Pair<String, String>): Interceptor {
            return Interceptor { chain ->
                chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Device-Type", "Android")
                    .addHeader("App-Device", Build.DEVICE)
                    .addHeader("App-Brand", Build.BRAND)
                    .addHeader("App-Model", Build.MODEL)
                    .addHeader("path", chain.request().url().encodedPath())
                    .also {
                        for (pair in pairs) {
                            it.addHeader(pair.first, pair.second)
                        }
                    }
                    .build()
                    .let { chain.proceed(it) }
            }
        }

        /**
         * 创建身份验证拦截器
         * @param [tokenProvider] 令牌提供程序
         * @return [Interceptor]
         */
        fun createAuthInterceptor(tokenProvider: () -> String?): Interceptor {
            return Interceptor { chain ->
                val request = chain.request().newBuilder().apply {
                    tokenProvider()?.let { token ->
                        addHeader("Authorization", token)
                    }
                }.build()
                chain.proceed(request)
            }
        }

        /**
         * 创建日志记录拦截器
         * @return [Interceptor]
         */
        fun createLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (!message.matches(Regex("[\\s\\S\\d\\w]*"))) {
                        return
                    }
                    if (message.contains("Content-Disposition: form-data")) {
                        Log.d("OkHttp", "(binary file data omitted)")
                    } else {
                        Log.d("OkHttp", message)
                    }
                }

            }).apply {
                level = if (_logEnabled) {
                    HttpLoggingInterceptor.Level.BODY
                } else HttpLoggingInterceptor.Level.NONE
            }
        }

        /**
         * 创建 Cookie 罐
         * @return [CookieJar]
         */
        fun createCookieJar(): CookieJar {
            return CookieJarImpl(PersistentCookieStore(GlobalApp.getInstance().application))
        }

        /**
         * 创建异常拦截器
         * @param [predicate] 谓语
         * @return [Interceptor]
         */
        fun createExceptionInterceptor(predicate: (chain:Interceptor.Chain) -> Response): Interceptor {
            return Interceptor { chain -> predicate(chain) }
        }

        /**
         * 创建转换器
         * @param [factory] 厂
         * @return [List<Converter.Factory>]
         */
        fun createConverter(
            vararg factory: Converter.Factory = arrayOf(GsonConverterBodyFactory.create())
        ): List<Converter.Factory> {
            return factory.asList()
        }

        /**
         * 创建呼叫适配器
         * @param [factory] 厂
         * @return [List<CallAdapter.Factory>]
         */
        fun createCallAdapter(
            vararg factory: CallAdapter.Factory = arrayOf(RxJava2CallAdapterFactory.create())
        ): List<CallAdapter.Factory> {
            return factory.asList()
        }

        /**
         * 创建拦截器
         * @param [interceptor] 拦截 器
         * @return [List<Interceptor>]
         */
        fun createInterceptors(
            vararg interceptor: Interceptor = arrayOf(
                createDefaultHeaderInterceptor(),
                createLoggingInterceptor(),
                createExceptionInterceptor { chain ->
                    chain.proceed(chain.request())
                }
            )
        ): List<Interceptor> {
            return interceptor.asList()
        }

        /**
         * 创建网络拦截器
         * @param [interceptor] 拦截 器
         * @return [List<Interceptor>]
         */
        fun createNetworkInterceptors(
            vararg interceptor: Interceptor = emptyArray()
        ): List<Interceptor> {
            return interceptor.asList()
        }
    }
}
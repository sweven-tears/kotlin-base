package com.app.test.base;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.app.test.App;
import com.app.test.data.network.ApiException;
import com.app.test.data.network.ExceptionEntity;
import com.app.test.data.network.GsonConverterBodyFactory;
import com.app.test.data.network.HeaderInterceptor;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import pers.sweven.common.repository.cookie.CookieJarImpl;
import pers.sweven.common.repository.cookie.store.PersistentCookieStore;
import pers.sweven.common.repository.interceptor.CacheInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 不建议直接使用，有需要使用请复制该文件进行操作
 */
public class BaseRetrofit {
    public static OkHttpClient client;
    private static volatile Retrofit retrofit;

    /**
     * 配置网络请求头
     */
    public static HashMap<String, Object> getRequestHeader() {
        HashMap<String, Object> parameters = new HashMap<>();
        // 为接口统一添加access_token参数
        parameters.put("android_type", "1");
        parameters.put("Accept", "application/json");
        return parameters;
    }

    public static Retrofit getRetrofit(String baseUrl) {
        if (retrofit == null) {
            synchronized (BaseRetrofit.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(s -> {
                        if (!s.matches("[\\s\\S\\d\\w]*")) {
                            return;
                        }
                        Platform.get().log(s,Platform.INFO,null);
                    });
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    File cacheFile = new File(App.application.getCacheDir(), "cache");
                    Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb 缓存的大小

                    client = new OkHttpClient
                            .Builder()
                            .connectTimeout(60000, TimeUnit.MILLISECONDS)
                            .readTimeout(60000, TimeUnit.MILLISECONDS)
                            .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应
                            .addInterceptor(new CacheInterceptor(App.application))
                            .addInterceptor(new HeaderInterceptor(getRequestHeader()))
                            //不加以下两行代码,https请求不到自签名的服务器
                            .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())//创建一个证书对象
                            .hostnameVerifier(new TrustAllHostnameVerifier())//校验名称,这个对象就是信任所有的主机,也就是信任所有https的请求
                            .cache(cache)  //添加缓存
                            .connectTimeout(15, TimeUnit.SECONDS)//连接超时时间
                            .readTimeout(15, TimeUnit.SECONDS)//读取超时时间
                            .writeTimeout(15, TimeUnit.SECONDS)//写入超时时间
                            .retryOnConnectionFailure(true)//连接不上是否重连,false不重连
                            .cookieJar(new CookieJarImpl(new PersistentCookieStore(App.application)))
                            .addInterceptor(new ExceptionInterceptor())
                            .build();

                    // 获取retrofit的实例
                    retrofit = new Retrofit
                            .Builder()
                            .baseUrl(baseUrl) //baseUrl配置
                            .client(client)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterBodyFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * 实现https请求
     */
    private static SSLSocketFactory createSSLSocketFactory() {


        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }

        return ssfFactory;
    }

    /**
     * 拦截处理器，建议使用ApiException
     */
    public static class ExceptionInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            int code = response.code();
            //225 215 218 200
            if (code == 200) {
                return response;
            } else if (code == 220 || code == 215 || code == 216) {
                String content = new String(response.body().bytes());
                Log.i("okhttp.OkHttpClient", content + "/n <-- END HTTP ERROR");
                ExceptionEntity entity;
                try {
                    entity = new Gson().fromJson(content, ExceptionEntity.class);
                } catch (JsonSyntaxException ignore) {
                    entity = new ExceptionEntity();
                    entity.setMessage(response.message());
                }
                throw new ApiException(request.header("path"), code, entity);
            }
            return response;
        }
    }

    @SuppressLint({"CustomX509TrustManager", "TrustAllX509TrustManager"})
    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 信任所有的服务器,返回true
     */
    @SuppressLint("BadHostnameVerifier")
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
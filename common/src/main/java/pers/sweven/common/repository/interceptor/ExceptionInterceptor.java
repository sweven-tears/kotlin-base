package pers.sweven.common.repository.interceptor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pers.sweven.common.repository.exception.ApiException;
import pers.sweven.common.repository.exception.ExceptionEntity;

public class ExceptionInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int code = response.code();
        //225 215 218 200
        if (code == 200) {
            return response;
        } else if (code == 225 || code == 215 || code == 218) {
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
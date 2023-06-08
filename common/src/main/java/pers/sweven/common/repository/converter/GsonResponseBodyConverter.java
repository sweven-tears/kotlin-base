package pers.sweven.common.repository.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
//        try {
//            return adapter.read(jsonReader);  //原支持库没有catch
//        }  catch (Exception e) {
//            e.fillInStackTrace();
//        } finally {
//            value.close();
//        }
//        return null;


    }

}
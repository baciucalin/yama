package com.movies.calinbaciu.yama.data.remote;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieApiClient {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TMDB_API_KEY = "2696829a81b1b5827d515ff121700838";

    private static TmdbInteractor INSTANCE;
    private static final OkHttpClient httpClient;
    private static final Object sLock = new Object();

    static {
        httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder()
                                .addQueryParameter("api_key", TMDB_API_KEY)
                                .build();

                        request = request.newBuilder().url(url).build();
                        return chain.proceed(request);
                    }
                }).build();
    }


    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }

    public static TmdbInteractor getInstance() {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = getRetrofitInstance().create(TmdbInteractor.class);
            }
            return INSTANCE;
        }
    }
}

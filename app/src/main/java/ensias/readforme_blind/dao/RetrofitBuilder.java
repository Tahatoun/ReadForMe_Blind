package ensias.readforme_blind.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Idriss on 14/07/2017.
 */

public class RetrofitBuilder {

    private final static OkHttpClient client = buildClient();
    private  static Retrofit retrofit=buildRetrofit(client);
    private static final String BASE_URL="http://192.168.43.177:8000/api/v1/";
     //private static final String BASE_URL="http://192.168.0.172:8000/api/v1/";

    public static OkHttpClient getClient() {
        return client;
    }

    private static OkHttpClient buildClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().
                connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder()
                                .addHeader("Content-type", "multipart/form-data")
                                .addHeader("Accept", "application/json")
                                .addHeader("Connection", "close");

                        request = builder.build();

                        return chain.proceed(request);

                    }
                });

        return builder.build();
    }

    private static Retrofit buildRetrofit(OkHttpClient client)  {
        Gson gson = new GsonBuilder() .setLenient() .create();
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

    }

    public static <T> T createService(Class<T> service){
        return retrofit.create(service);
    }

    public static   <T> T createServiceWithAuth(Class<T> service, final Auth tokenManager){

        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if(tokenManager.getToken().getAccessToken() != null){
                    builder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
                }
                request = builder.build();
                return chain.proceed(request);
            }
        }).authenticator(AppAuthenticator.getInstance(tokenManager)).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}

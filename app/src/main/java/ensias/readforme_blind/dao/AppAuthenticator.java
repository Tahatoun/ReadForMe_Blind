package ensias.readforme_blind.dao;

import java.io.IOException;

import ensias.readforme_blind.model.AccessToken;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;


public class AppAuthenticator implements Authenticator {

    private Auth tokenAuth;
    private static AppAuthenticator INSTANCE;

    private AppAuthenticator(Auth tokenManager){
        this.tokenAuth = tokenManager;
    }

    static synchronized AppAuthenticator getInstance(Auth tokenManager){
        if(INSTANCE == null){
            INSTANCE = new AppAuthenticator(tokenManager);
        }

        return INSTANCE;
    }


    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        if(responseCount(response) >= 3){
            return null;
        }

        UserInterface service = RetrofitBuilder.createService(UserInterface.class);
        Call<AccessToken> call = service.refresh(tokenAuth.getToken().getRefreshToken());
        retrofit2.Response<AccessToken> res = call.execute();

        if(res.isSuccessful()){
            AccessToken newToken = res.body();
            tokenAuth.saveToken(newToken);

            return response.request().newBuilder().header("Authorization", "Bearer " + res.body().getAccessToken()).build();
        }else{
            return null;
        }
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}

package ensias.readforme_blind.dao;



import java.util.ArrayList;

import ensias.readforme_blind.model.AccessToken;
import ensias.readforme_blind.model.File;
import ensias.readforme_blind.model.Home;
import ensias.readforme_blind.model.Blind;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserInterface {
    @GET("blinds/{id}?include=files")
    Call<Blind> getBlindAllInfo(@Path("id") int user);

    @GET("auth_blind")
    Call<Blind> getAuthBlind(@Query("include") String include);

    @GET("blinds/{id}?include=playlists")
    Call<Blind> getBlindWithPlaylists(@Path("id") int user);

    @GET("blinds/{id}?include=tracks")
    Call<Blind> getBlindWithTracks(@Path("id") int user);

    @GET("blinds/{id}?include=favouritesTracks")
    Call<Blind> getBlindWithFavouritesTracks(@Path("id") int user);

    @GET("blinds/{id}?include=posts")
    Call<Blind> getBlindWithPosts(@Path("id") int user);

    @GET("blinds/{id}?include=followers")
    Call<Blind> getBlindWithFollowers(@Path("id") int user);

    @GET("blinds/{id}?include=followings")
    Call<Blind> getBlindWithFollowing(@Path("id") int user);

    @GET("blinds/{id}?include=notifications")
    Call<Blind> getBlindWithNotifications(@Path("id") int user);

    @GET("blinds/{id}?include=likedTracks")
    Call<Blind> getBlindWithlikedTracks(@Path("id") int user);

    @GET("blinds/{id}?include=reportedTracks")
    Call<Blind> getBlindWithReportedTracks(@Path("id") int user);

    @GET("blinds/{id}?include=commentedTracks")
    Call<Blind> getBlindWithCommentedTracks(@Path("id") int user);

    @GET("blinds/{id}")
    Call<Blind> getBlindWithIncludes(@Path("id") int user, @Query("include") String include);

    @GET("blind/home")
    Call<Home> getHomeContent(@Query("page") int pageIndex);

    @GET("blind/files")
    Call<Home> getBlindFiles(@Query("page") int pageIndex);

    @FormUrlEncoded
    @POST("blinds/login")
    Call<AccessToken> Login(@Field("username") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("blinds/signup")
    Call<AccessToken> signUp(@Field("email") String email,@Field("password") String password, @Field("first_name") String firstName,@Field("last_name") String lastName,@Field("code") String code);

    @FormUrlEncoded
    @POST("blinds/signup")
    Call<AccessToken> signupEmail(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("blinds/refresh")
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("blinds/logout")
    Call<ResponseBody> logout();

    @FormUrlEncoded
    @POST("blinds/{id}/postTrack")
    Call<ResponseBody> post(@Path("id") int Blind_id, @Field("track_id") int track_id, @Field("content") String content);

    @Multipart
    @POST("blinds/updateProfile")
    Call<Blind> updateProfile(@Part("firstName") RequestBody firstName, @Part("lastName") RequestBody lastName, @Part("email") RequestBody email, @Part("gender") RequestBody gender, @Part MultipartBody.Part image);
    @Multipart
    @POST("blinds/updateProfile")
    Call<Blind> updateProfileWithoutPic(@Part("firstName") RequestBody firstName, @Part("lastName") RequestBody lastName, @Part("email") RequestBody email, @Part("gender") RequestBody gender);

    @GET("blinds/auth_Blind")
    Call<ArrayList<File>> getSuggestions();
}

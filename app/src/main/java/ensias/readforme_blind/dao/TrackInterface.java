package ensias.readforme_blind.dao;


import ensias.readforme_blind.model.Track;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface TrackInterface {
    @DELETE("tracks/{id}")
    Call<ResponseBody> deleteTrack(@Path("id") int id);

    @FormUrlEncoded
    @POST("tracks/{id}/favourite")
    Call<ResponseBody> favouriteTrack(@Path("id") int id, @Field("blind_id") int blind_id);

    @FormUrlEncoded
    @POST("tracks/{id}/unfavourite")
    Call<ResponseBody> unFavouriteTrack(@Path("id") int id, @Field("blind_id") int blind_id);

    @FormUrlEncoded
    @POST("tracks/{id}/addToPlaylist")
    Call<ResponseBody> addTrackToPlaylist(@Path("id") int id, @Field("playlist_id") int playlist_id);

    @FormUrlEncoded
    @POST("tracks/{id}/removeFromPlaylist")
    Call<ResponseBody> removeTrackFromPlaylist(@Path("id") int id, @Field("playlist_id") int playlist_id);

    @Multipart
    @POST("uploads")
    Call<ResponseBody> uploadFile(@Part("name") RequestBody name, @Part MultipartBody.Part file);

    @Multipart
    @POST("tracks")
    Call<Track> postTrack(@Part("name") RequestBody name, @Part("description") RequestBody dec, @Part MultipartBody.Part image, @Part MultipartBody.Part mp3);
     @Multipart
    @POST("tracks")
    Call<Track> postTrackWithoutPic(@Part("name") RequestBody name, @Part("description") RequestBody dec, @Part MultipartBody.Part mp3);
}

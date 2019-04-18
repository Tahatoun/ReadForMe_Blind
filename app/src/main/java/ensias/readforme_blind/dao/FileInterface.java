package ensias.readforme_blind.dao;



import ensias.readforme_blind.model.File;
import ensias.readforme_blind.model.Playlist;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FileInterface {
    @GET("files/{id}")
    Call<File> getFileWithIncludes(@Path("id") int fileId, @Query("include") String include);

    @Multipart
    @POST("files")
    Call<File> uploadFile(@Part("name") RequestBody name,@Part("description") RequestBody description, @Part MultipartBody.Part file);
}

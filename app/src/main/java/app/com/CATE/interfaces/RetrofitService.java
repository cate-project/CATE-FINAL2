package app.com.CATE.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {
    static final String URL = "http://ghkdua1829.dothome.co.kr/fow/";

    @FormUrlEncoded
    @POST("fow_loginReq.php")
    Call<JsonObject> Login_cate(
            @Field("userID") String userID,
            @Field("userPassword") String userPassword
    );

    @FormUrlEncoded
    @POST("fow_tag.php")
    Call<JsonObject> all_category(
            @Field("user_name") String userName
    );

    @FormUrlEncoded
    @POST("fow_allvideo.php")
    Call<JsonObject> All_video(
            @Field("Id") String Id
    );

    @FormUrlEncoded
    @POST("fow_post.php")
    Call<JsonObject> Signup(
            @Field("Id") String Id,
            @Field("Pw") String Pw,
            @Field("Name") String Name
    );

    @FormUrlEncoded
    @POST("fow_MakeLikeTable.php")
    Call<JsonObject> MakeLikeTable(
            @Field("userName") String userName,
            @Field("video_id") int video_id
    );

    @FormUrlEncoded
    @POST("fow_updatelikes.php")
    Call<JsonObject> updatelikes(
            @Field("username") String username,
            @Field("videoid") String videoid,
            @Field("target") String target
    );

    @FormUrlEncoded
    @POST("fow_updatecommentlikes.php")
    Call<JsonObject> updatecommentlikes(
            @Field("username") String username,
            @Field("videoid") String videoid,
            @Field("_index") String _index,
            @Field("target") String target
    );

    @FormUrlEncoded
    @POST("fow_post_category.php")
    Call<String> postCategory(
            @FieldMap Map<String, String> category
    );

    @FormUrlEncoded
    @POST("fow_get_category.php")
    Call<JsonArray> getCategory(
            @Field("user_name") String userName
    );

    @GET("fow_category_video.php")
    Call<JsonArray> getCategoryVideo(
            @Query("category_name") String categoryName
    );

}



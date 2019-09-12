package com.cn.superearman.net;



import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * @author azheng
 * @date 2018/4/24.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public interface APIService {
    //    String url = APP_SERVER_ADDR + "/v1/rtc/token/admin/app/" + getAppId(context) + "/room/" + roomName + "/user/" + userId + "?bundleId=" + packageName(context);
//    @RequestConverter(ScalarsConverterFactory.class)
//    @GET("/v1/rtc/token/admin/app/d8lk7l4ed/room/{roomName}/user/{userId}")
    @FormUrlEncoded
    @POST("lian_mai/roomToke")
//    Flowable<String> indexFunction(@Path("roomName") String roomName, @Path("userId") String userId, @Query("bundleId") String packageName);
    Flowable<String> indexFunction(@Field("room_name") String roomName, @Field("user_id") String userId, @Field("permission") String packageName);

}

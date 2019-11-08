package zw.org.isoc.hype;

import com.google.api.services.customsearch.model.Result;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HypeServerApi {

    @GET("hype/webpages/search")
    Call<List<Result>> searchByKeyword(@Query("q") String searchKeyword);

    @GET("hype/webpages/cached")
    Call<Map<String, WebPage>> getCachedContent();

    @POST("hype/register")
    Call<ResponseBody> register(@Body HypeUser id);

    @GET("hype/balance")
    Call<HypeBalance> getBalance(@Query("id") String id);

}
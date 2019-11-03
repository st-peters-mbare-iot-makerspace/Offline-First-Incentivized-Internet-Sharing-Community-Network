package zw.org.isoc.hype;

import com.google.api.services.customsearch.model.Result;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HypeServerApi {
    @GET("hype/webpages/search")
    List<Result> searchByKeyword(@Query("q") String searchKeyword);
}
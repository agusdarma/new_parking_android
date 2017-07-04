package com.project.parking.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Yohanes on 19/06/2017.
 */

public interface ApiInterface {
//    @GET("movie/top_rated")
//    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);
//
//    @GET("movie/{id}")
//    Call<MoviesResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @POST("loginUser")
    Call<String> doLogin(@Body String body);

    @POST("logoutUser")
    Call<String> doLogout(@Body String body);

    @POST("userRegistration")
    Call<String> doSignUp(@Body String body);

    @POST("changePassword")
    Call<String> doForgetPassword(@Body String body);

    @POST("changePassword")
    Call<String> doChangePassword(@Body String body);

    @POST("refreshCacheMall")
    Call<String> doRefreshingMall(@Body String body);

    @POST("checkBookingCode")
    Call<String> checkBookingCode(@Body String body);

    @POST("confirmCodeBooking")
    Call<String> confirmCodeBooking(@Body String body);
}

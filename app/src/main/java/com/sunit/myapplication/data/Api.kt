package com.sunit.myapplication.data

import com.sunit.myapplication.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("v3/b/6323a7995c146d63ca9d124f")
    suspend fun getDenyList(): Response<ApiResponse>
}

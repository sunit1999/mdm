package com.sunit.myapplication.ui

import com.sunit.myapplication.data.Api
import com.sunit.myapplication.data.model.ApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WelcomeRepository(
    private val api: Api,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getDenyList(): ApiResponse {
        return withContext(dispatcher) {
            val response = api.getDenyList()
            return@withContext (if (response.isSuccessful) {
                println(response.body())
                response.body() ?: throw Exception("Null response")
            } else {
                throw Exception(response.errorBody().toString())
            })
        }
    }
}
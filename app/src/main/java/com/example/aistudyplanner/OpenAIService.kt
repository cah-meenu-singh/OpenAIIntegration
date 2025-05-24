package com.example.aistudyplanner
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers("Authorization: Bearer key")
    @POST("v1/chat/completions")
    fun getStudyPlan(@Body request: OpenAIRequest): Call<OpenAIResponse>
}
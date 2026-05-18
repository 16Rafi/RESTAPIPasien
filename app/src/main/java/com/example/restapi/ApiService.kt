package com.example.restapi

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body body: Map<String, String>): Call<LoginResponse>

    @GET("pasien")
    fun getPatients(@Header("Authorization") token: String): Call<PatientResponse>
}

package com.example.restapi

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body body: Map<String, String>): Call<LoginResponse>

    @GET("pasien")
    fun getPatients(@Header("Authorization") token: String): Call<PatientResponse>

    @POST("pasien")
    fun createPatient(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Call<BaseResponse>

    @POST("pasien/{id}")
    fun updatePatient(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Call<BaseResponse>

    @DELETE("pasien/{id}")
    fun deletePatient(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<BaseResponse>
}

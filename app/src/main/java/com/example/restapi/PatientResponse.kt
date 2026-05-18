package com.example.restapi

import com.google.gson.annotations.SerializedName

data class PatientResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Patient>
)

data class Patient(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String,
    @SerializedName("jenis_kelamin") val jenisKelamin: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("no_telepon") val noTelepon: String
)

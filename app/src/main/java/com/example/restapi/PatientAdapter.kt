package com.example.restapi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restapi.databinding.ItemPatientBinding

class PatientAdapter(private val patients: List<Patient>) :
    RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    class PatientViewHolder(val binding: ItemPatientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        with(holder.binding) {
            tvPatientName.text = patient.nama
            tvBirthDate.text = "Tanggal Lahir: ${patient.tanggalLahir}"
            tvGender.text = "Jenis Kelamin: ${patient.jenisKelamin}"
            tvAddress.text = "Alamat: ${patient.alamat}"
            tvPhone.text = "No. Telepon: ${patient.noTelepon}"
        }
    }

    override fun getItemCount(): Int = patients.size
}

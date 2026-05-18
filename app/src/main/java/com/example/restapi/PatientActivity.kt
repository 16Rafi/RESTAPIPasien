package com.example.restapi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restapi.databinding.ActivityPatientBinding
import com.example.restapi.databinding.DialogPatientBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        val userName = sessionManager.fetchUserName()
        binding.tvWelcome.text = "Selamat Datang, $userName"

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        binding.fabAdd.setOnClickListener {
            showPatientDialog(null)
        }

        setupRecyclerView()
        fetchPatientData()
    }

    private fun logoutUser() {
        sessionManager.clearSession()
        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupRecyclerView() {
        adapter = PatientAdapter(
            emptyList(),
            onEditClick = { patient -> showPatientDialog(patient) },
            onDeleteClick = { patient -> confirmDelete(patient) }
        )
        binding.rvPatients.layoutManager = LinearLayoutManager(this)
        binding.rvPatients.adapter = adapter
    }

    private fun fetchPatientData() {
        showLoading(true)
        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        RetrofitClient.instance.getPatients(token).enqueue(object : Callback<PatientResponse> {
            override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val patients = response.body()?.data ?: emptyList()
                    adapter.updateData(patients)
                } else {
                    Toast.makeText(this@PatientActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@PatientActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPatientDialog(patient: Patient?) {
        val dialogBinding = DialogPatientBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this).setView(dialogBinding.root)
        val dialog = builder.create()

        if (patient != null) {
            dialogBinding.tvTitle.text = "Edit Pasien"
            dialogBinding.etName.setText(patient.nama)
            dialogBinding.etBirthDate.setText(patient.tanggalLahir)
            dialogBinding.etGender.setText(patient.jenisKelamin)
            dialogBinding.etAddress.setText(patient.alamat)
            dialogBinding.etPhone.setText(patient.noTelepon)
        }

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etName.text.toString().trim()
            val birthDate = dialogBinding.etBirthDate.text.toString().trim()
            val gender = dialogBinding.etGender.text.toString().trim()
            val address = dialogBinding.etAddress.text.toString().trim()
            val phone = dialogBinding.etPhone.text.toString().trim()

            if (name.isEmpty() || birthDate.isEmpty() || gender.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "nama" to name,
                "tanggal_lahir" to birthDate,
                "jenis_kelamin" to gender,
                "alamat" to address,
                "no_telepon" to phone
            )

            if (patient == null) {
                createPatient(body, dialog)
            } else {
                updatePatient(patient.id, body, dialog)
            }
        }

        dialog.show()
    }

    private fun createPatient(body: Map<String, String>, dialog: AlertDialog) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        RetrofitClient.instance.createPatient(token, body).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    dialog.dismiss()
                    Toast.makeText(this@PatientActivity, "Pasien berhasil ditambah", Toast.LENGTH_SHORT).show()
                    fetchPatientData()
                } else {
                    Toast.makeText(this@PatientActivity, "Gagal menambah pasien", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(this@PatientActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePatient(id: Int, body: Map<String, String>, dialog: AlertDialog) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        RetrofitClient.instance.updatePatient(id, token, body).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    dialog.dismiss()
                    Toast.makeText(this@PatientActivity, "Pasien berhasil diupdate", Toast.LENGTH_SHORT).show()
                    fetchPatientData()
                } else {
                    Toast.makeText(this@PatientActivity, "Gagal update pasien", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(this@PatientActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmDelete(patient: Patient) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pasien")
            .setMessage("Apakah Anda yakin ingin menghapus ${patient.nama}?")
            .setPositiveButton("Hapus") { _, _ -> deletePatient(patient.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deletePatient(id: Int) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        RetrofitClient.instance.deletePatient(id, token).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PatientActivity, "Pasien berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchPatientData()
                } else {
                    Toast.makeText(this@PatientActivity, "Gagal menghapus pasien", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(this@PatientActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

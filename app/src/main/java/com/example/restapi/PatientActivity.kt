package com.example.restapi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restapi.databinding.ActivityPatientBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientBinding
    private lateinit var sessionManager: SessionManager

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
        binding.rvPatients.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchPatientData() {
        showLoading(true)
        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        RetrofitClient.instance.getPatients(token).enqueue(object : Callback<PatientResponse> {
            override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val patients = response.body()?.data ?: emptyList()
                    binding.rvPatients.adapter = PatientAdapter(patients)
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

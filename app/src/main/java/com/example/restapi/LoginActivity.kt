package com.example.restapi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.restapi.databinding.ActivityLoginBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.fetchAuthToken() != null) {
            moveToPatientActivity()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        showLoading(true)
        val loginRequest = mapOf(
            "email" to email,
            "password" to password
        )

        RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val loginData = response.body()?.data
                    if (loginData != null) {
                        sessionManager.saveAuthToken(loginData.token)
                        sessionManager.saveUserName(loginData.user.name)
                        moveToPatientActivity()
                    }
                } else {
                    // Ambil pesan error dari body atau errorBody
                    val errorMsg = try {
                        val errorObj = response.errorBody()?.string()?.let { JSONObject(it) }
                        errorObj?.optString("message") ?: "Login Gagal"
                    } catch (e: Exception) {
                        response.body()?.message ?: "Login Gagal"
                    }
                    Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun moveToPatientActivity() {
        startActivity(Intent(this, PatientActivity::class.java))
        finish()
    }
}

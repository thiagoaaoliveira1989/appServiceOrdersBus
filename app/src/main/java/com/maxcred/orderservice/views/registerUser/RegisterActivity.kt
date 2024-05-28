package com.maxcred.orderservice.views.registerUser

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maxcred.orderservice.R
import com.maxcred.orderservice.data.db.AppDatabase
import com.maxcred.orderservice.repository.DatabaseDataSource
import com.maxcred.orderservice.views.login.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var repository: DatabaseDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_form)

        // Inicializando o banco de dados e o repositório
        val database = AppDatabase.getInstance(this)
        repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

        val btnRegister: Button = findViewById(R.id.btnSubmitRegister)
        btnRegister.setOnClickListener {
            val usernameEditText = findViewById<EditText>(R.id.edtRegisterName)
            val username = usernameEditText.text.toString()

            val emailEditText = findViewById<EditText>(R.id.edtRegisterEmail)
            val email = emailEditText.text.toString()

            val passwordEditText = findViewById<EditText>(R.id.edtRegisterPassword)
            val password = passwordEditText.text.toString()

            val confirmPasswordEditText = findViewById<EditText>(R.id.edtRegisterConfirmPassword)
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "As senhas não são iguais", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val isEmailExists = repository.isEmailExists(email)
                    if (isEmailExists) {
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, "Já existe um usuário cadastrado com esse email!", Toast.LENGTH_SHORT).show()
                            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                        }
                    } else {
                        val result = repository.insertRegister(username, email, password)
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, "Usuário cadastrado", Toast.LENGTH_SHORT).show()

                            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val btnReturn: Button = findViewById(R.id.btnSubmitReturn)
        btnReturn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

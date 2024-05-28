    package com.maxcred.orderservice.views.login

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.inputmethod.InputMethodManager
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.lifecycle.lifecycleScope
    import com.maxcred.orderservice.R
    import com.maxcred.orderservice.views.registerUser.RegisterActivity
    import com.maxcred.orderservice.data.db.AppDatabase
    import com.maxcred.orderservice.repository.DatabaseDataSource
    import com.maxcred.orderservice.views.dashboard.DashboardActivity
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext

    class MainActivity : AppCompatActivity() {

        private lateinit var repository: DatabaseDataSource

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            val database = AppDatabase.getInstance(this)
            repository = DatabaseDataSource(database.registerDAO, database.busDAO, database.serviceOrderDAO, database.partDAO)

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val btnLogin: Button = findViewById(R.id.btnLogin)
            btnLogin.setOnClickListener {
                val emailText = findViewById<EditText>(R.id.loginEmail)
                val email = emailText.text.toString()

                val passwordText = findViewById<EditText>(R.id.loginPassword)
                val password = passwordText.text.toString()

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val usersLiveData = repository.getAllRegister()

                            withContext(Dispatchers.Main) {
                                usersLiveData.observe(this@MainActivity) { users ->
                                    users?.let { userList ->
                                        var isUserAuthenticated = false

                                        // Verifica se há algum usuário com as credenciais fornecidas
                                        for (user in userList) {
                                            if (email == user.email && password == user.password) {
                                                isUserAuthenticated = true
                                                break
                                            }
                                        }

                                        // Exibe o Toast com base no resultado da autenticação
                                        if (isUserAuthenticated) {
                                            Toast.makeText(this@MainActivity, "Login realizado com Sucesso", Toast.LENGTH_SHORT).show()

                                            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                                            val intent = Intent(
                                                this@MainActivity,
                                                DashboardActivity::class.java
                                            )
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this@MainActivity, "Usuario ou Senha incorretos", Toast.LENGTH_SHORT).show()
                                            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("LoginActivity", "Erro ao Realizar Login, Tente novamente", e)
                        }
                    }

                }
            }

            // Configura o botão de registro
            val btnRegister: Button = findViewById(R.id.btnRegister)
            btnRegister.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

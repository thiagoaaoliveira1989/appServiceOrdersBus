package com.maxcred.orderservice.views.dashboard
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maxcred.orderservice.R
import com.maxcred.orderservice.views.part.ListPartsActivity
import com.maxcred.orderservice.views.bus.ListBusActivity
import com.maxcred.orderservice.views.bus.RegisterBusActivity
import com.maxcred.orderservice.views.part.RegisterPartActivity
import com.maxcred.orderservice.views.serviceOrder.ListServiceOrderActivity
import com.maxcred.orderservice.views.serviceOrder.RegisterServiceOrderActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)



        // Botao de Cadastrar Onibus
        val btnCadastrarBus: Button = findViewById(R.id.btnCadastrarOnibus)
        btnCadastrarBus.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val intent = Intent(this@DashboardActivity, RegisterBusActivity::class.java)
                startActivity(intent)
            }
        }

        // Botao para listar onibus cadastrados
        val btnListarBus: Button = findViewById(R.id.btnListarOnibus)
        btnListarBus.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val intent = Intent(this@DashboardActivity, ListBusActivity::class.java)
                startActivity(intent)
            }
        }


        // Botao Cadastrar Peças
        val btnCadastrarParts: Button = findViewById(R.id.btnCadastrarParts)
        btnCadastrarParts.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val intent = Intent(this@DashboardActivity, RegisterPartActivity::class.java)
                startActivity(intent)
            }
        }

        // Botao Listar todas as Peças
        val btnListarParts: Button = findViewById(R.id.btnListarPeças)
        btnListarParts.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val intent = Intent(this@DashboardActivity, ListPartsActivity::class.java)
                startActivity(intent)
            }
        }



        // Botao Cadastrar Ordem de serviço
        val btnCadastrarOrdem: Button = findViewById(R.id.btnCadastrarOrdemServico)
        btnCadastrarOrdem.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val intent = Intent(this@DashboardActivity, RegisterServiceOrderActivity::class.java)
                startActivity(intent)
            }
        }

        // Botao Listar Ordem de serviço
        val btnListarOrdem: Button = findViewById(R.id.btnListarOrdemSerico)
        btnListarOrdem.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val intent = Intent(this@DashboardActivity, ListServiceOrderActivity::class.java)
                startActivity(intent)
            }
        }

    }


}

package com.maxcred.orderservice.utils

import android.content.Context
import android.util.Base64
import com.maxcred.orderservice.R
import java.io.InputStream
import java.text.NumberFormat
import java.util.*

class OrderServiceReport(
    val orderNumber: Long,
    private val veiculo: String,
    private val km: String,
    private val placa: String,
    private val numberCar: String,
    private val dataAbertura: String,
    private val dataFechamento: String,
    private val descricaoServico: String,
    private val pecasUtilizadas: List<OrderServiceReport.Peca>,
    private val mecanico: String,
    private val encarregado: String
) {

    data class Peca(
        val quantidade: Int,
        val codigo: String,
        val descricao: String,
        val custo: String?,
        val custoTotal: String?
    )

    private fun convertDrawableToBase64(context: Context, drawableId: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(drawableId)
        val bytes = inputStream.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun formatCurrency(value: Double?): String {
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return format.format(value ?: 0.0)
    }

    private fun parseDouble(value: String?): Double? {
        return value?.toDoubleOrNull()
    }

    fun generateHtml(context: Context): String {
        val logoBase64 = convertDrawableToBase64(context, R.drawable.logo)
        var totalOrderCost = 0.0

        val pecasHtml = if (pecasUtilizadas.isNotEmpty()) {
            pecasUtilizadas.joinToString(separator = "") { peca ->
                totalOrderCost += parseDouble(peca.custoTotal) ?: 0.0
                """
                <tr>
                    <td>${peca.quantidade}</td>
                    <td>${peca.codigo}</td>
                    <td>${peca.descricao}</td>
                    <td>${formatCurrency(parseDouble(peca.custo))}</td>
                    <td>${formatCurrency(parseDouble(peca.custoTotal))}</td>
                </tr>
                """
            }
        } else {
            // Caso não haja peças utilizadas, preencha a tabela com 6 linhas vazias
            """
            <tr><td></td><td></td><td></td><td></td><td></td></tr>
            <tr><td></td><td></td><td></td><td></td><td></td></tr>
            <tr><td></td><td></td><td></td><td></td><td></td></tr>
            <tr><td></td><td></td><td></td><td></td><td></td></tr>
            <tr><td></td><td></td><td></td><td></td><td></td></tr>
            <tr><td></td><td></td><td></td><td></td><td></td></tr>
            """
        }

        val descricaoServicoHtml = """
            <div class="description">
                <ul>
                    ${descricaoServico.split("\n").joinToString(separator = "") { "<li>$it</li>" }}
                </ul>
            </div>
        """

        val totalOrderCostHtml = if (totalOrderCost > 0) {
            """
            <tr>
                <td colspan="4" style="text-align: right;"><strong>Total da Ordem:</strong></td>
                <td>${formatCurrency(totalOrderCost)}</td>
            </tr>
            """
        } else {
            ""
        }

        return  """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>ORDEM DE SERVIÇO - OURO NEGRO</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
            }
            .container {
                margin-bottom: 20px;
            }
            .header {
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 20px;
            }
            .header img {
                max-height: 80px;
                margin-right: 40px;
            }
            .header h1 {
                font-size: 24px;
                margin: 0;
            }
            .info-table, .parts-table, .signatures-table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 20px;
            }
            .info-table th, .info-table td, .parts-table th, .parts-table td, .signatures-table th, .signatures-table td {
                border: 1px solid #000;
                padding: 8px;
                text-align: center;
            }
            .info-table th {
                background-color: #f2f2f2;
            }
            .parts-table th {
                background-color: #e0e0e0;
            }
            .signatures-table th {
                background-color: #f2f2f2;
            }
            .section-title {
                text-align: center;
                font-size: 18px;
                font-weight: bold;
                border-top: 1px solid #000;
                border-left: 1px solid #000;
                border-right: 1px solid #000;
                border-bottom: none;
                padding: 8px;
            }
            .description {
                border: 1px solid #000;
                padding: 8px;
                min-height: 100px;
            }
            .description ul {
                list-style-type: circle;
                padding-left: 20px;
            }
        </style>
    </head>
    <body>
    <div class="header">
        <img src="data:image/jpeg;base64,$logoBase64" alt="Logo"/>
        <h1>Ordem de Serviço Nº ${orderNumber}</h1>
    </div>
    <table class="info-table">
        <tr>
            <th>Veículo</th>
            <th>KM</th>
            <th>Placa</th>
            <th>Nº da Ordem</th>
            <th>Data de Abertura</th>
            <th>Data de Fechamento</th>
        </tr>
        <tr>
            <td>$veiculo</td>
            <td>$km</td>
            <td>$placa</td>
            <td>$numberCar</td>
            <td>$dataAbertura</td>
            <td>$dataFechamento</td>
        </tr>
    </table>
    <div class="container">
        <div class="section-title">Descrição do Serviço</div>
        $descricaoServicoHtml
    </div>
    <div class="container">
        <div class="section-title">Peças Utilizadas</div>
        <table class="parts-table">
            <tr>
                <th>Quantidade</th>
                <th>Código</th>
                <th>Descrição</th>
                <th>Custo</th>
                <th>Custo Total</th>
            </tr>
            $pecasHtml
            $totalOrderCostHtml
        </table>
    </div>
    <table class="signatures-table">
        <tr>
            <th>Mecânico</th>
            <th>Encarregado</th>
        </tr>
        <tr>
            <td>$mecanico</td>
            <td>$encarregado</td>
        </tr>
    </table>
    </body>
    </html>
    """
    }
}

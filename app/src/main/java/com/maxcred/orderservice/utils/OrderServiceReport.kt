package com.maxcred.orderservice.utils

class OrderServiceReport(
    private val veiculo: String,
    private val km: String,
    private val placa: String,
    val numeroOrdem: String,
    private val dataAbertura: String,
    private val dataFechamento: String,
    private val descricaoServico: String,
    private val pecasUtilizadas: List<OrderServiceReport.Peca>,
    private val mecanico: String,
    private val supervisor: String
) {

    data class Peca(
        val quantidade: Int,
        val codigo: String,
        val descricao: String,
        val custo: Double,
        val custoTotal: Double
    )

    fun generateHtml(): String {
        val pecasHtml = pecasUtilizadas.joinToString(separator = "") { peca ->
            """
            <tr>
                <td>${peca.quantidade}</td>
                <td>${peca.codigo}</td>
                <td>${peca.descricao}</td>
                <td>${peca.custo}</td>
                <td>${peca.custoTotal}</td>
            </tr>
            """
        }

        return """
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
                .header {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: 20px;
                }
                .header img {
                    max-height: 60px; /* Ajuste este valor conforme necessário */
                    margin-right: 20px;
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
                    text-align: left;
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
                    font-size: 18px;
                    font-weight: bold;
                    margin-top: 20px;
                }
                .description {
                    border: 1px solid #000;
                    padding: 10px;
                    margin-bottom: 20px;
                }
            </style>
        </head>
        <body>
        <div class="header">
            <img src="drawable/logo.jpg" alt="Logo"/>
            <h1>Ordem de Serviço</h1>
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
                <td>$numeroOrdem</td>
                <td>$dataAbertura</td>
                <td>$dataFechamento</td>
            </tr>
        </table>
        <div class="section-title">Descrição do Serviço</div>
        <div class="description">
            $descricaoServico
        </div>
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
        </table>
        <table class="signatures-table">
            <tr>
                <th>Mecânico</th>
                <th>Supervisor</th>
            </tr>
            <tr>
                <td>$mecanico</td>
                <td>$supervisor</td>
            </tr>
        </table>
        </body>
        </html>
        """
    }
}

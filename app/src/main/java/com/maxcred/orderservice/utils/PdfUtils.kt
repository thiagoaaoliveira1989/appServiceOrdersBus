package com.maxcred.orderservice.utils

import android.content.Context
import android.os.Environment
import com.itextpdf.html2pdf.HtmlConverter
import java.io.File
import java.io.FileOutputStream

object PdfUtils {
    fun generatePdf(context: Context, orderServiceReport: OrderServiceReport) {
        val htmlContent = orderServiceReport.generateHtml()

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            println("Storage não está montado.")
            return
        }

        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        if (!outputDir.exists()) {
            println("Diretório de saída não existe. Criando...")
            outputDir.mkdirs()
        }

        // Nome do arquivo incluindo o número da ordem de serviço
        val outputFileName = "Ordem_de_Serviço_${orderServiceReport.numeroOrdem}.pdf"
        val outputFile = File(outputDir, outputFileName)

        try {
            FileOutputStream(outputFile).use { outputStream ->
                println("Convertendo HTML para PDF...")
                HtmlConverter.convertToPdf(htmlContent, outputStream)
                println("PDF gerado com sucesso em: ${outputFile.absolutePath}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Erro ao gerar o PDF: ${e.message}")
        }
    }
}

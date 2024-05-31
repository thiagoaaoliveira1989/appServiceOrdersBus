package com.maxcred.orderservice.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.itextpdf.html2pdf.HtmlConverter
import java.io.File
import java.io.FileOutputStream

object PdfUtils {
    fun generatePdf(context: Context, orderServiceReport: OrderServiceReport): String? {
        val htmlContent = orderServiceReport.generateHtml(context) // Passando o contexto aqui

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            println("Storage não está montado.")
            return null
        }

        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        if (!outputDir.exists()) {
            println("Diretório de saída não existe. Criando...")
            outputDir.mkdirs()
        }

        val outputFileName = "Ordem_de_Serviço_${orderServiceReport.orderNumber}.pdf"
        val outputFile = File(outputDir, outputFileName)

        return try {
            FileOutputStream(outputFile).use { outputStream ->
                println("Convertendo HTML para PDF...")
                HtmlConverter.convertToPdf(htmlContent, outputStream)
                println("PDF gerado com sucesso em: ${outputFile.absolutePath}")
            }
            openPdf(context, outputFile.absolutePath)
            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            println("Erro ao gerar o PDF: ${e.message}")
            null
        }
    }

    private fun openPdf(context: Context, pdfPath: String) {
        val pdfFile = File(pdfPath)
        val pdfUri = FileProvider.getUriForFile(
            context,
            "${context.applicationContext.packageName}.provider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(intent, "Abrir PDF")
        context.startActivity(shareIntent)
    }
}

package com.example.unnamulaibilling.util

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.unnamulaibilling.data.model.Invoice
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfGenerator {
    fun generateInvoicePdf(context: Context, invoice: Invoice): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        drawInvoice(canvas, invoice)

        document.finishPage(page)

        val file = File(context.cacheDir, "Invoice_${invoice.invoiceDetails.invoiceNumber}.pdf")
        try {
            document.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
            document.close()
            return null
        }
        document.close()
        return file
    }

    fun writePdfToUri(context: Context, pdfFile: File, destinationUri: Uri): Boolean {
        return try {
            context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                pdfFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun savePdfToDownloads(context: Context, invoice: Invoice): Uri? {
        val pdfFile = generateInvoicePdf(context, invoice) ?: return null
        val fileName = "Invoice_${invoice.invoiceDetails.invoiceNumber}.pdf"

        return try {
            val savedUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return null
                resolver.openOutputStream(uri)?.use { outputStream ->
                    pdfFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                uri
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val destFile = File(downloadsDir, fileName)
                pdfFile.copyTo(destFile, overwrite = true)
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", destFile)
            }
            if (savedUri != null) {
                openPdf(context, savedUri)
            }
            savedUri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun openPdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "PDF saved successfully. No PDF viewer app found to open it.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun drawInvoice(canvas: Canvas, invoice: Invoice) {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 10f

        val margin = 20f
        val width = 555f // 595 - 40
        val height = 802f // 842 - 40
        
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRect(margin, margin, margin + width, margin + height, paint)
        
        paint.style = Paint.Style.FILL
        
        // Header
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val headerText = invoice.documentType
        val headerWidth = paint.measureText(headerText)
        canvas.drawText(headerText, margin + (width - headerWidth) / 2, margin + 20, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        if (invoice.seller.gstin != null) {
            canvas.drawText("GSTIN: ${invoice.seller.gstin}", margin + 10, margin + 20, paint)
        }
        if (invoice.seller.panNo != null) {
            val panWidth = paint.measureText("PAN NO: ${invoice.seller.panNo}")
            canvas.drawText("PAN NO: ${invoice.seller.panNo}", margin + width - panWidth - 10, margin + 20, paint)
        }
        
        var y = margin + 35
        paint.style = Paint.Style.STROKE
        canvas.drawLine(margin, y, margin + width, y, paint)
        
        // Split for Seller and Invoice Details
        val midX = margin + width / 2
        canvas.drawLine(midX, y, midX, y + 90, paint)

        // Seller Details
        paint.style = Paint.Style.FILL
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        y += 15
        canvas.drawText(invoice.seller.name, margin + 10, y, paint)
        
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        y += 15
        if (invoice.seller.description != null) {
            invoice.seller.description.split("\n").forEach {
                canvas.drawText(it, margin + 10, y, paint)
                y += 12
            }
        }
        invoice.seller.address.split("\n").forEach {
            canvas.drawText(it, margin + 10, y, paint)
            y += 12
        }
        if (invoice.seller.phone != null) {
            canvas.drawText("Phone: ${invoice.seller.phone}", margin + 10, y, paint)
        }
        
        // Invoice Details
        var rightY = margin + 50
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Invoice No: ${invoice.invoiceDetails.invoiceNumber}", midX + 10, rightY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        rightY += 15
        canvas.drawText("Date: ${invoice.invoiceDetails.date}", midX + 10, rightY, paint)
        rightY += 15
        if (invoice.invoiceDetails.vehicleNo != null) {
            canvas.drawText("Vehicle No: ${invoice.invoiceDetails.vehicleNo}", midX + 10, rightY, paint)
        }
        rightY += 15
        if (invoice.invoiceDetails.vehicleType != null) {
            canvas.drawText("Type: ${invoice.invoiceDetails.vehicleType}", midX + 10, rightY, paint)
        }
        
        y = maxOf(y, rightY) + 15
        paint.style = Paint.Style.STROKE
        canvas.drawLine(margin, y, margin + width, y, paint)

        // Buyer Details
        paint.style = Paint.Style.FILL
        y += 15
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Billed To:", margin + 10, y, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        y += 15
        canvas.drawText(invoice.buyer.name, margin + 10, y, paint)
        y += 12
        canvas.drawText(invoice.buyer.address, margin + 10, y, paint)
        y += 12
        if (invoice.buyer.gstin != null) canvas.drawText("GSTIN: ${invoice.buyer.gstin}", margin + 10, y, paint)
        if (invoice.buyer.stateCode != null) {
            val scWidth = paint.measureText("State Code: ${invoice.buyer.stateCode}")
            canvas.drawText("State Code: ${invoice.buyer.stateCode}", margin + width - scWidth - 10, y, paint)
        }
        
        y += 15
        paint.style = Paint.Style.STROKE
        canvas.drawLine(margin, y, margin + width, y, paint)
        
        // Draw Table Header
        paint.style = Paint.Style.FILL
        y += 15
        val cols = listOf(
            margin to "S.No",
            margin + 40 to "Description & Material",
            margin + 280 to "KGS/UNIT",
            margin + 380 to "RATE-₹",
            margin + 480 to "AMOUNT"
        )
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        cols.forEach { (x, text) ->
            canvas.drawText(text, x + 5, y, paint)
        }
        y += 10
        paint.style = Paint.Style.STROKE
        canvas.drawLine(margin, y, margin + width, y, paint)
        
        // Draw Items
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        y += 15
        invoice.items.forEach { item ->
            canvas.drawText(item.sNo.toString(), cols[0].first + 5, y, paint)
            canvas.drawText(item.descriptionAndMaterial, cols[1].first + 5, y, paint)
            canvas.drawText("${item.quantity} ${item.unit}", cols[2].first + 5, y, paint)
            canvas.drawText(String.format("%.2f", item.rate), cols[3].first + 5, y, paint)
            canvas.drawText(String.format("%.2f", item.amount), cols[4].first + 5, y, paint)
            y += 15
        }
        
        y += 10
        paint.style = Paint.Style.STROKE
        canvas.drawLine(margin, y, margin + width, y, paint)
        
        // Draw Summary
        paint.style = Paint.Style.FILL
        y += 15
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Sub Total", cols[3].first + 5, y, paint)
        canvas.drawText(String.format("%.2f", invoice.taxSummary.subTotal), cols[4].first + 5, y, paint)
        
        y += 15
        invoice.taxSummary.sgst?.let {
            canvas.drawText("SGST (${it.ratePercent}%)", cols[3].first + 5, y, paint)
            canvas.drawText(String.format("%.2f", it.amount), cols[4].first + 5, y, paint)
            y += 15
        }
        invoice.taxSummary.cgst?.let {
            canvas.drawText("CGST (${it.ratePercent}%)", cols[3].first + 5, y, paint)
            canvas.drawText(String.format("%.2f", it.amount), cols[4].first + 5, y, paint)
            y += 15
        }
        invoice.taxSummary.igst?.let {
            canvas.drawText("IGST (${it.ratePercent}%)", cols[3].first + 5, y, paint)
            canvas.drawText(String.format("%.2f", it.amount), cols[4].first + 5, y, paint)
            y += 15
        }
        
        y += 5
        paint.style = Paint.Style.STROKE
        canvas.drawLine(cols[3].first, y, margin + width, y, paint)
        y += 15
        paint.style = Paint.Style.FILL
        canvas.drawText("Grand Total", cols[3].first + 5, y, paint)
        canvas.drawText(String.format("%.2f", invoice.taxSummary.grandTotal), cols[4].first + 5, y, paint)
        
        // Amount in words
        y += 20
        canvas.drawText("Amount in words: ${invoice.taxSummary.amountInWords}", margin + 10, y, paint)
        
        // Footer (Bank Details & Terms & Signature)
        val footerY = margin + height - 120
        paint.style = Paint.Style.STROKE
        canvas.drawLine(margin, footerY, margin + width, footerY, paint)
        
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Bank Details:", margin + 10, footerY + 15, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        invoice.bankDetails?.let { bank ->
            canvas.drawText("Bank: ${bank.bankName}", margin + 10, footerY + 30, paint)
            canvas.drawText("A/C No: ${bank.accountNumber}", margin + 10, footerY + 45, paint)
            canvas.drawText("Branch: ${bank.branch}", margin + 10, footerY + 60, paint)
            canvas.drawText("IFSC: ${bank.ifscCode}", margin + 10, footerY + 75, paint)
        }

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Terms & Declarations:", margin + 200, footerY + 15, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        var termY = footerY + 30
        invoice.termsAndDeclarations?.forEach {
            canvas.drawText(it, margin + 200, termY, paint)
            termY += 15
        }
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("For ${invoice.seller.name}", midX + 50, footerY + 15, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Authorized Signatory", midX + 50, footerY + 100, paint)
    }
}

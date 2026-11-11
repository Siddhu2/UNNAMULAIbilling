package com.example.unnamulaibilling.util

object NumberToWordsConverter {
    private val units = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )
    
    private val tens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    fun convert(number: Long): String {
        if (number == 0L) return "Zero Rupees Only"
        
        var n = number
        val sb = StringBuilder()
        
        val crores = (n / 10000000).toInt()
        if (crores > 0) {
            sb.append(convertLessThanOneThousand(crores)).append(" Crore ")
            n %= 10000000
        }
        
        val lakhs = (n / 100000).toInt()
        if (lakhs > 0) {
            sb.append(convertLessThanOneThousand(lakhs)).append(" Lakh ")
            n %= 100000
        }
        
        val thousands = (n / 1000).toInt()
        if (thousands > 0) {
            sb.append(convertLessThanOneThousand(thousands)).append(" Thousand ")
            n %= 1000
        }
        
        val hundreds = (n / 100).toInt()
        if (hundreds > 0) {
            sb.append(convertLessThanOneThousand(hundreds)).append(" Hundred ")
            n %= 100
        }
        
        if (n > 0) {
            if (sb.isNotEmpty()) {
                sb.append("and ")
            }
            sb.append(convertLessThanOneThousand(n.toInt()))
        }
        
        return sb.toString().trim() + " Rupees Only"
    }

    private fun convertLessThanOneThousand(number: Int): String {
        if (number == 0) return ""
        
        if (number < 20) {
            return units[number]
        }
        
        if (number < 100) {
            return (tens[number / 10] + " " + units[number % 10]).trim()
        }
        
        return (units[number / 100] + " Hundred " + convertLessThanOneThousand(number % 100)).trim()
    }
}

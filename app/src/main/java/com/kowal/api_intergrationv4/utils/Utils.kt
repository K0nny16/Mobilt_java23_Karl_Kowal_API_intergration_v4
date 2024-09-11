package com.kowal.api_intergrationv4.utils

class Utils {
    companion object{
        fun String.capitalizeFirstLetter(): String {
            return this.substring(0,1).uppercase()+this.substring(1)
        }
    }
}
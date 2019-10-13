package com.example.keykeeper.domain

import java.lang.StringBuilder
import kotlin.random.Random

class Random {
    companion object{
        fun getRandomPlainText(len:Int):String{
            val sb = StringBuilder()
            for (i in 1..len){
                if (Random.nextInt(3) == 0){
                    sb.append((65..90).random().toChar())
                }else {
                    sb.append((97..122).random().toChar())
                }
            }
            return if (sb.isEmpty()){
                getRandomPlainText(6)
            }else{
                sb.toString()
            }
        }
        fun getRandomNumberText(len:Int):String{
            val sb = StringBuilder()
            for (i in 1..len){
                sb.append(Random.nextInt(10))
            }
            return if (sb.isEmpty()){
                getRandomNumberText(6)
            }else{
                sb.toString()
            }
        }
    }
}
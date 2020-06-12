package kr.goldenmine.downloader

import java.io.File

fun downloadAll() {
    val file = File("links.txt")
    file.useLines { lines->
        lines.forEach {
            val split = it.split("/")
            if(split[0].startsWith("http")) {

            }
        }
    }
}
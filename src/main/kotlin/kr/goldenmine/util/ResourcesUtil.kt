package kr.goldenmine.util

import java.io.File




fun getResource(fileName: String): File? {
    val classLoader = ClassLoader.getSystemClassLoader()

    val url = classLoader.getResource(fileName)

    return if(url != null) File(url.file) else null
}
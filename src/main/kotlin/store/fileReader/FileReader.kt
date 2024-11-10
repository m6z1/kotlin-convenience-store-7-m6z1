package store.fileReader

import java.io.File

class FileReader(private val filePath: String) {

    fun readFile(): List<String> {
        return File(filePath).useLines { it.toList() }
    }
}
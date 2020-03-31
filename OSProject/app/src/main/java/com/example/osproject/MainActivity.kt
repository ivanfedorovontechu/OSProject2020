package com.example.osproject

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class Graph<T> {
    val adjacencyMap: HashMap<T, HashSet<T>> = HashMap()
    fun addEdge(sourceVertex: T, destinationVertex: T) {
        // Add edge to source vertex / node.
        adjacencyMap
            .getOrPut(sourceVertex) { HashSet() }
            .add(destinationVertex)
        adjacencyMap
            .getOrPut(destinationVertex) { HashSet() }
            .add(sourceVertex)
    }

    override fun toString(): String = StringBuffer().apply {
        for (key in adjacencyMap.keys) {
            append("$key -> ")
            append(adjacencyMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()
}

fun <T> depthFirstTraversal(graph: Graph<T>, startNode: T): String {
    val visitedMap = mutableMapOf<T, Boolean>().apply {
        graph.adjacencyMap.keys.forEach { node -> put(node, false) }
    }
    val stack: Deque<T> = ArrayDeque()
    stack.push(startNode)
    val traversalList = mutableListOf<T>()
    while (stack.isNotEmpty()) {
        val currentNode = stack.pop()
        if (!visitedMap[currentNode]!!) {
            traversalList.add(currentNode)
            visitedMap[currentNode] = true
            graph.adjacencyMap[currentNode]?.forEach { node ->
                stack.push(node)
            }
        }
    }
    return traversalList.joinToString()
}

fun writeFile(context: Context) {
    val filename = "writeTest"
    val fileContents = "Hello world!"
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        for (i in 1..1000) {
            it.write(fileContents.toByteArray())
        }
        it.close()
    }
}

fun readFile(context: Context){
    context.assets.open("readTest").use { stream ->
        val text = stream.bufferedReader().use {
            it.readText()
        }
        stream.close()
    }
}

fun encryptFile(applicationContext: Context){
    val path = applicationContext.filesDir.absolutePath
    val myFile = File(path, "data.txt")
    if (myFile.exists()) myFile.delete()
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    var encryptedFile = EncryptedFile.Builder(
        File(path, "data.txt"),
        applicationContext,
        masterKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
    encryptedFile.openFileOutput().bufferedWriter().use {
        it.write("AES-ENCRYPTION-")
        it.close()
    }
}

fun decryptFile(applicationContext: Context){
    val path = applicationContext.filesDir.absolutePath
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    val encryptedFile = EncryptedFile.Builder(
        File(path, "data.txt"),
        applicationContext,
        masterKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
    val encryptedInputStream: FileInputStream = encryptedFile.openFileInput()
    var content = encryptedInputStream.readBytes().toString(Charset.defaultCharset())
    encryptedInputStream.close()
    println(content)
}



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val graph = Graph<Int>()
        graph.addEdge(1, 2)
        graph.addEdge(2, 3)
        graph.addEdge(3, 4)
        graph.addEdge(5, 6)
        graph.addEdge(4, 7)
        graph.addEdge(7, 8)
        graph.addEdge(8, 9)
        graph.addEdge(7, 10)
        graph.addEdge(10, 11)
        graph.addEdge(3, 12)
        graph.addEdge(2, 13)
        graph.addEdge(13, 14)
        graph.addEdge(13, 20)
        graph.addEdge(15, 16)
        graph.addEdge(16, 17)
        graph.addEdge(17, 18)
        graph.addEdge(16, 19)
        graph.addEdge(1, 21)
        graph.addEdge(21, 22)
        graph.addEdge(22, 23)
        graph.addEdge(23, 24)
        graph.addEdge(23, 25)
        graph.addEdge(25, 26)
        graph.addEdge(25, 27)
        graph.addEdge(27, 28)
        graph.addEdge(27, 29)
        graph.addEdge(21, 30)
        graph.addEdge(30, 31)
        graph.addEdge(30, 32)
        graph.addEdge(32, 33)
        graph.addEdge(32, 34)
        graph.addEdge(34, 35)
        graph.addEdge(34, 36)
        val button: Button = findViewById<Button>(R.id.recalculateButton)
        button.setOnClickListener {
            var DFSExecutionTime = measureNanoTime {
                val DFSResults = depthFirstTraversal(graph, 1)
                println(DFSResults)
            }
            val textViewDFSResults = findViewById<TextView>(R.id.DFSResults)
            textViewDFSResults.text = DFSExecutionTime.toString()

            var readExecutionTime = measureTimeMillis {
                readFile(this.applicationContext)
            }
            val textViewReadResults = findViewById<TextView>(R.id.ReadResults)
            textViewReadResults.text = readExecutionTime.toString()

            var writeExecutionTime = measureTimeMillis {
                writeFile(this.applicationContext)
            }
            val textViewWriteResults = findViewById<TextView>(R.id.WriteResults)
            textViewWriteResults.text = writeExecutionTime.toString()

            var encryptExecutionTime = measureTimeMillis {
                encryptFile(this.applicationContext)
            }
            val textViewEncryptResults = findViewById<TextView>(R.id.encryptionResults)
            textViewEncryptResults.text = encryptExecutionTime.toString()

            var decryptExecutionTime = measureTimeMillis {
                decryptFile(this.applicationContext)
            }
            val textViewDecryptResults = findViewById<TextView>(R.id.decryptionResults)
            textViewDecryptResults.text = decryptExecutionTime.toString()
        }
    }
}

package com.app.test

import java.io.File
import java.io.FileWriter
import javax.xml.parsers.DocumentBuilderFactory


open class Run {
    companion object {
        fun create() {
            val file = File("app/src/main/AndroidManifest.xml")
            println(file.absoluteFile)
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val document = db.parse(file)
            val `package` = document.documentElement.getAttribute("package")
            println(`package`)
            val application = document.getElementsByTagName("application")
            val activities = application.item(0).ownerDocument.getElementsByTagName("activity")
            val importArray = mutableListOf<String>()
            println(activities.length)
            for (i in 0 until activities.length) {
                val namedItem = activities.item(i).attributes.getNamedItem("android:name")
                val import = `package` + namedItem.nodeValue
                importArray.add(import)
                println(import)
            }

            val builder = StringBuilder("package $`package`")
            builder.append("\r\r")
            for (imp in importArray) {
                builder.append("import $imp").append("\r")
            }
            builder.append("\r")
            builder.append("object Factory {")
            builder.append("\r")

            builder.append("\tval map = hashMapOf<String, Class<*>>()")
            builder.append("\r\r")
            builder.append("\tinit {")
            builder.append("\r")
            for (imp in importArray) {
                val index = imp.lastIndexOf(".")
                val clazz = imp.substring(index + 1)
                var upIndex = 0
                clazz.forEachIndexed { index, c ->
                    if (c.isUpperCase()) {
                        upIndex = index
                    }
                }
                val key = "/${clazz.substring(0, 1).lowercase()}" +
                        clazz.substring(1, upIndex) +
                        "/${clazz.substring(upIndex).lowercase()}"
                builder.append("\t\tmap[\"$key\"] = $clazz::class.java")
                builder.append("\r")
            }
            builder.append("\t}")

            builder.append("\r")
            builder.append("}")
            val content = builder.toString()
            println(content)

            val parent = file.parent!! + "\\java\\" + `package`.replace(".", "\\")
            val newFile = File(parent, "Factory.kt")
            if (!newFile.exists()) {
                newFile.createNewFile()
            }
            try {
                val writer = FileWriter(newFile)
//        val bw = BufferedWriter(writer)
                writer.write(content)
//        bw.close()
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.fillInStackTrace()
            }
            println(newFile.absoluteFile)
        }
    }

    var a: A? = null

    class A {
        var b: B? = null
    }

    class B {
        fun print() {
            print("b")
        }
    }
}

fun main() {
    Run.create()
}
package com.example.silentmapapp

import android.content.Context
import android.content.Context.MODE_PRIVATE
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FileManager{

    companion object {

        fun loadFromFile(context: Context, filename: String): Any{

            val fis = context.applicationContext.openFileInput(filename)
            val ois = ObjectInputStream(fis)

            return ois.readObject()
        }

        fun saveToFile(data: Any, context: Context, filename: String){

            val fos =context.applicationContext.openFileOutput(filename, MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)

            oos.writeObject(data)
            oos.close()
        }
    }
}
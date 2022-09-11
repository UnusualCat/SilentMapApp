package Helper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FileManager{

    companion object {

        private const val filename = "listaGeofence.data"

        fun loadFromFile(context: Context): Any {

            val fis = context.applicationContext.openFileInput(filename)
            val ois = ObjectInputStream(fis)

            val output = ois.readObject()
            ois.close()
            return output
        }

        fun saveToFile(data: Any, context: Context){

            val fos =context.applicationContext.openFileOutput(filename, MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)

            oos.writeObject(data)
            oos.close()
        }
    }
}
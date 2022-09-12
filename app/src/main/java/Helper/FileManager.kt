package Helper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FileManager{

    companion object {

        fun loadFromFile(context: Context): Any {

            val fis = context.applicationContext.openFileInput(Resources.GEOFENCE_FILE_NAME)
            val ois = ObjectInputStream(fis)

            val output = ois.readObject()
            ois.close()
            return output
        }

        fun saveToFile(data: Any, context: Context){

            val fos =context.applicationContext.openFileOutput(Resources.GEOFENCE_FILE_NAME, MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)

            oos.writeObject(data)
            oos.close()
        }
    }
}
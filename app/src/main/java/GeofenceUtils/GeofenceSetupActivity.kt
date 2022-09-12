package GeofenceUtils

import Helper.Resources
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.silentmapapp.R
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.gms.maps.model.LatLng
import java.io.File


class GeofenceSetupActivity : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofence_setup)
        val latLng = intent.extras?.getParcelable<LatLng>("latLng")!!
        val onConfirmButton: Button = findViewById(R.id.confirmButton)
        val onCancel: Button = findViewById(R.id.onCancel)
        val raggioBar: SeekBar = findViewById(R.id.raggioSeekBar)
        val valoreRaggio: TextView = findViewById(R.id.valoreRaggio)
        val coloreImmagine: ImageButton = findViewById(R.id.imageButton)
        val switch: Switch = findViewById(R.id.switchMode)
        val nomeGeoFence: EditText = findViewById(R.id.nomeGeofence)
        val COLORE_DEFAULT_RAGGIO = -65536
        var coloreRaggio = COLORE_DEFAULT_RAGGIO
        onConfirmButton.isEnabled=false

        nomeGeoFence.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(p0: Editable?) {

                val text = p0.toString()
                onConfirmButton.isEnabled=true

                if(text.length<4){

                    nomeGeoFence.error = "Il nome del Geofence deve essere di almeno 4 caratteri"
                    onConfirmButton.isEnabled=false

                }

                if(File(Resources.Filepath , Resources.GEOFENCE_FILE_NAME).exists()){

                    for(setting in Resources.listaGeofence){
                        if(text == setting.geofenceID){

                            nomeGeoFence.error = "Il nome del Geofence deve essere univoco"
                            onConfirmButton.isEnabled=false
                        }
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence? , p1: Int , p2: Int , p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        // ROSSO: Colore di default dei cerchi
        coloreImmagine.setColorFilter(COLORE_DEFAULT_RAGGIO)
        coloreImmagine.setOnClickListener {

            ColorPickerDialogBuilder
                .with(this)
                .setTitle("Scegli il colore: ")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                }
                .setPositiveButton("Conferma") { _, coloreSelezionato, _ ->

                    coloreImmagine.setColorFilter(coloreSelezionato)
                    coloreRaggio = coloreSelezionato
                }
                .setNegativeButton("Esci"){_, _ ->

                }
                .build()
                .show()
        }

        switch.setOnClickListener {

            if(switch.isChecked) switch.text = "Silenzioso"
            else switch.text = "Suoneria"
        }
        raggioBar.setOnSeekBarChangeListener(object :
        
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(raggioBar: SeekBar, progresso: Int, fromUser: Boolean) {
                valoreRaggio.text = progresso.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        onCancel.setOnClickListener {
            finish()
        }

        onConfirmButton.setOnClickListener {

            val resultIntent = Intent()
            resultIntent.putExtra("raggio", valoreRaggio.text.toString())
            resultIntent.putExtra("colore", coloreRaggio)
            resultIntent.putExtra("silenzioso", switch.isChecked)
            resultIntent.putExtra("nome", nomeGeoFence.text.toString())
            resultIntent.putExtra("latLng",latLng)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
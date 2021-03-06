package es.upm.miw.ficheros;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class FicherosActivity extends AppCompatActivity {

    String NOMBRE_FICHERO;
    String RUTA_FICHERO;         /** SD card **/
    EditText lineaTexto;
    Button botonAniadir;
    TextView contenidoFichero;
    boolean SDactivo;
    ActionBar actionbar;

    @Override
    protected void onStart() {
        super.onStart();
        mostrarContenido(contenidoFichero);
        this.SDactivo = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("opcionesCheckBox", true);
        this.NOMBRE_FICHERO = PreferenceManager.getDefaultSharedPreferences(this).getString("opcionesNombre", "mi_fichero_miw.txt");
        RUTA_FICHERO = getExternalFilesDir(null) + "/" + NOMBRE_FICHERO;
        actionbar = getActionBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros);

        lineaTexto       = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir     = (Button)   findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);
        /** SD card **/
        // RUTA_FICHERO = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + NOMBRE_FICHERO;
    }

    /**
     * Al pulsar el botón añadir -> añadir al fichero.
     * Después de añadir -> mostrarContenido()
     *
     * @param v Botón añadir
     */
    public void accionAniadir(View v){
        if(SDactivo) {
            accionAniadirSD(v);
        }else{
            accionAniadirLocal(v);
        }
    }

    public void accionAniadirSD(View v) {
        /** Comprobar estado SD card **/
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {  /** SD card **/
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
                fos.write(lineaTexto.getText().toString().getBytes());
                fos.write('\n');
                fos.close();
                lineaTexto.setText("");
                mostrarContenido(contenidoFichero);
                Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
                Log.d("Guardado en SD", Boolean.valueOf(this.SDactivo).toString());
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void accionAniadirLocal(View v) {
        try {
            FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_APPEND);
            fos.write(lineaTexto.getText().toString().getBytes());
            fos.write('\n');
            fos.close();
            lineaTexto.setText("");
            mostrarContenido(contenidoFichero);
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mostrarContenido(View textviewContenidoFichero){
        if(SDactivo){
            mostrarContenidoSD(textviewContenidoFichero);
        }else{
            mostrarContenidoLocal(textviewContenidoFichero);
        }
    }

    /**
     * Se pulsa sobre el textview -> mostrar contenido del fichero
     * Si está vacío -> mostrar un Toast
     *
     * @param textviewContenidoFichero TextView contenido del fichero
     */
    public void mostrarContenidoSD(View textviewContenidoFichero) {
        boolean hayContenido = false;
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();
        contenidoFichero.setText("");
        try {
            if (fichero.exists() &&         /** SD card **/
                    estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                // BufferedReader fin =
                //        new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
                BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarContenidoLocal(View textviewContenidoFichero) {
        boolean hayContenido = false;
        contenidoFichero.setText("");
        try {
                 BufferedReader fin =
                        new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
                //BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");

        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * Añade el menú con la opcion de vaciar el fichero
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //menu.add(Menu.NONE, 1, Menu.NONE, R.string.opcionVaciar)
        //        .setIcon(android.R.drawable.ic_menu_delete); // sólo visible android < 3.0

        // Inflador del menú: añade elementos a la action bar
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accionVaciar:
                borrarContenido();
                break;
            case R.id.accionAjustes:
                mostrarAjustes();
                break;
            case R.id.menu_destino:
                openFilesDestino(this);
                break;
        }

        return true;
    }

    public void openFilesDestino(Context context){
        File file = context.getExternalFilesDir(null);
        Log.i("url", file.toString());
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "*/*");
        try {
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void borrarContenido(){
        if(SDactivo){
            borrarContenidoSD();
        }else {
            borrarContenidoLocal();
        }
    }
    /**
     * Vaciar el contenido del fichero, la línea de edición y actualizar
     *
     */
    public void borrarContenidoSD() {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {  // Vaciar el fichero
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) { /** SD card **/
                // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void borrarContenidoLocal() {
        try {  // Vaciar el fichero
                FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                //FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);

        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mostrarAjustes(){
        Intent ajustes = new Intent(this, SettingsActivity.class);
        startActivity(ajustes);
    }

}
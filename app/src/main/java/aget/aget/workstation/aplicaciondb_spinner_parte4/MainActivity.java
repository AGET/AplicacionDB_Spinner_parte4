package aget.aget.workstation.aplicaciondb_spinner_parte4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class MainActivity extends ActionBarActivity {


    ListView lstv;
    String namespace = "http://tempuri.org/";
    String url = "http://10.0.2.2/WSAgenda/Service1.asmx";


    SQLHelper sqlhelper;
    SQLiteDatabase db;
    /*HttpTransportSE transporte;
    SoapObject resques;
    SoapSerializationEnvelope sobre;
    SoapPrimitive resultado;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstv = (ListView) findViewById(R.id.lista);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {

                String valor = (String) lstv.getItemAtPosition(posicion);
                StringTokenizer st = new StringTokenizer(valor, "-");
                String clave = st.nextToken();
                String nombre = st.nextToken();
                String telefono = st.nextToken();
                String email = st.nextToken();
                String pais = st.nextToken();
                lanzarAlerta(clave, nombre, telefono, email, pais);
            }
        });
        actualiza();
    }



    public void lanzarAlerta(final String cve, final String nom, final String tel, final String email, final String pais){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Que deseas hacer con el contacto?");
        dialog.setMessage(nom);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                borrar(cve);
            }
        });
        dialog.setNegativeButton("Modificar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Insertar.class);
                intent.putExtra("id", cve);
                intent.putExtra("nom", nom);
                intent.putExtra("tel", tel);
                intent.putExtra("mail", email);
                intent.putExtra("pais", pais);
                intent.putExtra("boton", "Modificar");
                startActivity(intent);
            }
        });
        dialog.show();
    }

    protected void borrar(String id){
        sqlhelper = new SQLHelper(this);
        db = sqlhelper.getWritableDatabase();
        db.execSQL("delete from contacto where _id ='" + id + "'");
        db.close();
        actualiza();
    }

    protected void actualiza(){
        sqlhelper = new SQLHelper (this);
        db = sqlhelper.getWritableDatabase();

        Cursor c = db.rawQuery(
                "select "
                        + "c._id, "
                        + "c.nombre, "
                        + "c.telefono, "
                        + "c.correo, "
                        + "p.nombre "
                        + " from contacto c "
                        + " inner join pais p "
                        + " on c.pais=p.id",null);

//		Cursor c = db.rawQuery("select c.id, c.nombre, c.telefono, c.correo, c.pais from contacto c",null);

        if (c.moveToFirst()){
            ArrayList<String> arreglo =
                    new ArrayList<String>(c.getCount());
            do{
                String id = c.getString(0);
                String nom = c.getString(1);
                String tel = c.getString(2);
                String mail = c.getString(3);
                String pais = c.getString(4);
                arreglo.add(id+"-"+nom+"-"+tel+"-"+mail+"-"+pais);
            }while(c.moveToNext());
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_list_item_1,
                            arreglo);
            lstv.setAdapter(adapter);
        }else{
            ArrayList<String> arreglo = new ArrayList<String>(1);
            arreglo.add("Sin Datos");
            ArrayAdapter<String> adapter =

                    new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_list_item_1,
                            arreglo
                    );
            lstv.setAdapter(adapter);
        } // if

        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            actualiza();
        }
    }
}

package co.com.k4soft.parqueaderouco.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Tarifa;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;
import co.com.k4soft.parqueaderouco.utilities.ActionBarUtil;

public class EditarTarifaActivity extends AppCompatActivity {

    @BindView(R.id.txtEditarNombre)
    public EditText txtEditarNombre;

    @BindView(R.id.txtEditarTarifa)
    public EditText txtEditarTarifa;

    private ActionBarUtil actionBarUtil;
    private DataBaseHelper database;

    private Tarifa tarifaEncontrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        ButterKnife.bind(this);
        initComponents();
        buscarTarifaExistente();
    }

    private void initComponents() {
        actionBarUtil = new ActionBarUtil(this);
        actionBarUtil.setToolBar(getString(R.string.editar_registro));
        database = DataBaseHelper.getDBMainThread(this);
    }

    private void buscarTarifaExistente() {
        String idTarifa = getIntent().getStringExtra("id");
        tarifaEncontrada = database.getTarifaDAO().getByIdTarifa(Integer.valueOf(idTarifa));
        txtEditarNombre.setText(tarifaEncontrada.getNombre());
        txtEditarTarifa.setText(String.valueOf(tarifaEncontrada.getPrecio()));
    }

    public void editarTarifa(View view) {
        String tarifaNombre = txtEditarNombre.getText().toString();
        Double tarifaValor = toDouble(txtEditarTarifa.getText().toString());
        Integer idTarifa = tarifaEncontrada.getIdTarifa();

        if (validarInformacion(tarifaNombre, tarifaValor)) {
            Tarifa tarifaEditada = new Tarifa();
            tarifaEditada.setIdTarifa(idTarifa);
            tarifaEditada.setNombre(tarifaNombre);
            tarifaEditada.setPrecio(tarifaValor);

            new EditarTarifa().execute(tarifaEditada);
            finish();

        }
    }

    private boolean validarInformacion(String tarifaNombre, Double tarifaValor) {
        boolean esValido = true;

        if ("".equals(tarifaNombre)) {
            txtEditarNombre.setError(getString(R.string.requerido));
            esValido = false;
        }

        if (tarifaValor == 0) {
            txtEditarTarifa.setError(getString(R.string.requerido));
            esValido = false;
        }

        return esValido;
    }

    private Double toDouble(String valor) {
        return "".equals(valor) ? 0 : Double.parseDouble(valor);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private class EditarTarifa extends AsyncTask<Tarifa, Void, Void> {

        @Override
        protected Void doInBackground(Tarifa... tarifas) {
            DataBaseHelper.getSimpleDB(getApplicationContext()).getTarifaDAO().update(tarifas[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), getString(R.string.edited), Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
    }
}

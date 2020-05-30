package co.com.k4soft.parqueaderouco.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.entidades.Tarifa;
import co.com.k4soft.parqueaderouco.negocio.CalculadoraTiempo;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;
import co.com.k4soft.parqueaderouco.utilities.ActionBarUtil;
import co.com.k4soft.parqueaderouco.utilities.DateUtil;

public class MovimientoActivity extends AppCompatActivity {

    @BindView(R.id.txtPlaca)
    public EditText txtPlaca;
    @BindView(R.id.layoutDatos)
    public ConstraintLayout layoutDatos;
    @BindView(R.id.txtCantidadHoras)
    public TextView txtCantidadHoras;
    @BindView(R.id.tipoTarifaSpinner)
    public Spinner tipoTarifaSpinner;
    @BindView(R.id.btnIngreso)
    public Button btnIngreso;
    @BindView(R.id.btnSalida)
    public Button btnSalida;
    @BindView(R.id.txtTotal)
    public TextView txtTotal;

    private ActionBarUtil actionBarUtil;
    private DataBaseHelper db;
    private List<Tarifa> tarifas;


    private Movimiento movimiento;
    private CalculadoraTiempo calculadoraTiempo;
    private Tarifa tarifa;
    private String[] arrayTarifas;
    private String valorTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento);
        ButterKnife.bind(this);

        initComponents();
        ocultarComponents();
        cargarDatosSpinner();
        spinnerOnItemSelected();

    }

    private void initComponents(){
        db = DataBaseHelper.getDBMainThread(this);
        calculadoraTiempo = new CalculadoraTiempo();
        actionBarUtil = new ActionBarUtil(this);
        actionBarUtil.setToolBar(getString(R.string.tarifas));
    }

    private void ocultarComponents() {
        tipoTarifaSpinner.setVisibility(View.GONE);
        btnIngreso.setVisibility(View.GONE);
        btnSalida.setVisibility(View.GONE);
        layoutDatos.setVisibility(View.GONE);

    }

    private void cargarDatosSpinner() {
        tarifas = db.getTarifaDAO().listar();
        if(tarifas.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.sin_tarifas,Toast.LENGTH_SHORT).show();
            finish();
        }else{
            arrayTarifas = new String[tarifas.size()];
            for (int i = 0; i<tarifas.size(); i++){
                arrayTarifas[i] = tarifas.get(i).getNombre();
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayTarifas);
                tipoTarifaSpinner.setAdapter(arrayAdapter);
            }
        }
    }

    private void spinnerOnItemSelected() {
        tipoTarifaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tarifa = tarifas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void buscarPlaca(View view) throws ParseException {
        ocultarComponents();
        movimiento =  db.getMovimientoDAO().findByPlaca(txtPlaca.getText().toString().toUpperCase());
        if(movimiento == null){
            mostrarComponentesIngreso();
        }else {
            mostrarComponentesSalida();
            calcularPrecioParqueo();
        }
    }

    private void mostrarComponentesIngreso() {
        tipoTarifaSpinner.setVisibility(View.VISIBLE);
        btnIngreso.setVisibility(View.VISIBLE);
    }

    private void mostrarComponentesSalida() {
        btnSalida.setVisibility(View.VISIBLE);
        layoutDatos.setVisibility(View.VISIBLE);
    }

    private void calcularPrecioParqueo() throws ParseException {

        String fechaIngreso = movimiento.getFechaEntrada();
        String fechaSalida = DateUtil.convertirDateToString(new Date());
        movimiento.setFechaSalida(fechaSalida);

        int totalHoras = calculadoraTiempo.calcularTiempoParqueado(fechaIngreso, fechaSalida);
        txtCantidadHoras.setText(Integer.toString(totalHoras) + " Hora(s)");
        Tarifa tarifaExistente = db.getTarifaDAO().getByIdTarifa(movimiento.getIdTarifa());
        valorTotal = String.valueOf(tarifaExistente.getPrecio()* totalHoras);
        txtTotal.setText(valorTotal);
    }


    public void registrarIngresoAlParqueadero(View view) throws ParseException {
        if(tarifa == null){
            Toast.makeText(getApplicationContext(), R.string.seleccionar_tarifa, Toast.LENGTH_SHORT).show();
        }else if(movimiento == null){
            movimiento = new Movimiento();
            movimiento.setPlaca(txtPlaca.getText().toString().toUpperCase());
            movimiento.setIdTarifa(tarifa.getIdTarifa());
            movimiento.setFechaEntrada(DateUtil.convertirDateToString(new Date()));

            new PersistenciaMovimiento().execute(movimiento);
            movimiento = null;
            ocultarComponents();
        }
    }

    public void registrarSalidaDelParqueadero(View view) throws ParseException {
        Movimiento movimiento = db.getMovimientoDAO().findByPlaca(txtPlaca.getText().toString().toUpperCase());
            movimiento.setFechaSalida(DateUtil.convertirDateToString(new Date()));
            movimiento.setValorTotal(valorTotal);
            movimiento.setFinalizaMovimiento(true);

        new PersistenciaMovimiento().execute(movimiento);
        valorTotal = "";
        ocultarComponents();
    }


    private class PersistenciaMovimiento extends AsyncTask<Movimiento, Void, Void> {

        @Override
        protected Void doInBackground(Movimiento... movimientos) {
            DataBaseHelper.getSimpleDB(getApplicationContext()).getMovimientoDAO().insert(movimientos[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), R.string.transaccion_exitosa, Toast.LENGTH_SHORT).show();
        }
    }


}

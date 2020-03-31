package co.com.k4soft.parqueaderouco.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.utilities.ActionBarUtil;

public class MainActivity extends AppCompatActivity {

    private ActionBarUtil actionBarUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {
        actionBarUtil = new ActionBarUtil(this);
        actionBarUtil.setToolBar(getString(R.string.menu_principal));
    }


    public void goToTarifaActivity(View view) {
       Intent intent = new Intent(this,TarifaActivity.class);
       startActivity(intent);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}

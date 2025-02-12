package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText etMonto, etMasa, etVolumen, etLongitud, etAlmacenamiento, etTiempo, etTransferencia;
    private Spinner spinnerMonedaEntrada, spinnerMonedaSalida, spinnerMasaEntrada, spinnerMasaSalida,
            spinnerVolumenEntrada, spinnerVolumenSalida, spinnerLongitudEntrada, spinnerLongitudSalida,
            spinnerAlmacenamientoEntrada, spinnerAlmacenamientoSalida, spinnerTiempoEntrada, spinnerTiempoSalida,
            spinnerTransferenciaEntrada, spinnerTransferenciaSalida;
    private Button btnConvertirMoneda, btnConvertirMasa, btnConvertirVolumen, btnConvertirLongitud,
            btnConvertirAlmacenamiento, btnConvertirTiempo, btnConvertirTransferencia;
    private TextView tvResultadoMoneda, tvResultadoMasa, tvResultadoVolumen, tvResultadoLongitud,
            tvResultadoAlmacenamiento, tvResultadoTiempo, tvResultadoTransferencia;

    private HashMap<String, Double> tasasDeCambio;
    private HashMap<String, Double> tasasDeMasa;
    private HashMap<String, Double> tasasDeVolumen;
    private HashMap<String, Double> tasasDeLongitud;
    private HashMap<String, Double> tasasDeAlmacenamiento;
    private HashMap<String, Double> tasasDeTiempo;
    private HashMap<String, Double> tasasDeTransferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();
        configurarTabs();
        configurarSpinnersYTasas();
        configurarListeners();
    }

    private void inicializarComponentes() {
        etMonto = findViewById(R.id.etMonto);
        spinnerMonedaEntrada = findViewById(R.id.spinnerMonedaEntrada);
        spinnerMonedaSalida = findViewById(R.id.spinnerMonedaSalida);
        btnConvertirMoneda = findViewById(R.id.btnConvertirMoneda);
        tvResultadoMoneda = findViewById(R.id.tvResultadoMoneda);

        etMasa = findViewById(R.id.etMasa);
        spinnerMasaEntrada = findViewById(R.id.spinnerMasaEntrada);
        spinnerMasaSalida = findViewById(R.id.spinnerMasaSalida);
        btnConvertirMasa = findViewById(R.id.btnConvertirMasa);
        tvResultadoMasa = findViewById(R.id.tvResultadoMasa);

        etVolumen = findViewById(R.id.etVolumen);
        spinnerVolumenEntrada = findViewById(R.id.spinnerVolumenEntrada);
        spinnerVolumenSalida = findViewById(R.id.spinnerVolumenSalida);
        btnConvertirVolumen = findViewById(R.id.btnConvertirVolumen);
        tvResultadoVolumen = findViewById(R.id.tvResultadoVolumen);

        etLongitud = findViewById(R.id.etLongitud);
        spinnerLongitudEntrada = findViewById(R.id.spinnerLongitudEntrada);
        spinnerLongitudSalida = findViewById(R.id.spinnerLongitudSalida);
        btnConvertirLongitud = findViewById(R.id.btnConvertirLongitud);
        tvResultadoLongitud = findViewById(R.id.tvResultadoLongitud);

        etAlmacenamiento = findViewById(R.id.etAlmacenamiento);
        spinnerAlmacenamientoEntrada = findViewById(R.id.spinnerAlmacenamientoEntrada);
        spinnerAlmacenamientoSalida = findViewById(R.id.spinnerAlmacenamientoSalida);
        btnConvertirAlmacenamiento = findViewById(R.id.btnConvertirAlmacenamiento);
        tvResultadoAlmacenamiento = findViewById(R.id.tvResultadoAlmacenamiento);

        etTiempo = findViewById(R.id.etTiempo);
        spinnerTiempoEntrada = findViewById(R.id.spinnerTiempoEntrada);
        spinnerTiempoSalida = findViewById(R.id.spinnerTiempoSalida);
        btnConvertirTiempo = findViewById(R.id.btnConvertirTiempo);
        tvResultadoTiempo = findViewById(R.id.tvResultadoTiempo);

        etTransferencia = findViewById(R.id.etTransferencia);
        spinnerTransferenciaEntrada = findViewById(R.id.spinnerTransferenciaEntrada);
        spinnerTransferenciaSalida = findViewById(R.id.spinnerTransferenciaSalida);
        btnConvertirTransferencia = findViewById(R.id.btnConvertirTransferencia);
        tvResultadoTransferencia = findViewById(R.id.tvResultadoTransferencia);
    }

    private void configurarTabs() {
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("Moneda").setContent(R.id.tab1).setIndicator("Moneda"));
        tabHost.addTab(tabHost.newTabSpec("Masa").setContent(R.id.tab2).setIndicator("Masa"));
        tabHost.addTab(tabHost.newTabSpec("Volumen").setContent(R.id.tab3).setIndicator("Volumen"));
        tabHost.addTab(tabHost.newTabSpec("Longitud").setContent(R.id.tab4).setIndicator("Longitud"));
        tabHost.addTab(tabHost.newTabSpec("Almacenamiento").setContent(R.id.tab5).setIndicator("Almacenamiento"));
        tabHost.addTab(tabHost.newTabSpec("Tiempo").setContent(R.id.tab6).setIndicator("Tiempo"));
        tabHost.addTab(tabHost.newTabSpec("Transferencia").setContent(R.id.tab7).setIndicator("Transferencia"));
    }

    private void configurarSpinnersYTasas() {
        String[] monedas = {
                "Dolar", "Euros", "Yen Japones", "Peso Mexicano",
                "Colon Costarrisence", "Quetzal Guatemalteco", "Lempira Hondureño", "Cordoba Nicaraguense", "Colon Salvadoreño",
                "Peso Argentino", "Sol Peruano", "Bolivares Venezolano"
        };
        ArrayAdapter<String> adapterMonedas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monedas);
        spinnerMonedaEntrada.setAdapter(adapterMonedas);
        spinnerMonedaSalida.setAdapter(adapterMonedas);

        String[] unidadesMasa = {"Kilogramo", "Gramo", "Libra", "Onza"};
        ArrayAdapter<String> adapterMasa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidadesMasa);
        spinnerMasaEntrada.setAdapter(adapterMasa);
        spinnerMasaSalida.setAdapter(adapterMasa);

        String[] unidadesVolumen = {"Litro", "Mililitro", "Galón", "Pulgada cúbica"};
        ArrayAdapter<String> adapterVolumen = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidadesVolumen);
        spinnerVolumenEntrada.setAdapter(adapterVolumen);
        spinnerVolumenSalida.setAdapter(adapterVolumen);

        String[] unidadesLongitud = {"Metro", "Centímetro", "Pulgada", "Pie"};
        ArrayAdapter<String> adapterLongitud = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidadesLongitud);
        spinnerLongitudEntrada.setAdapter(adapterLongitud);
        spinnerLongitudSalida.setAdapter(adapterLongitud);

        String[] unidadesAlmacenamiento = {"Byte", "Kilobyte", "Megabyte", "Gigabyte"};
        ArrayAdapter<String> adapterAlmacenamiento = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidadesAlmacenamiento);
        spinnerAlmacenamientoEntrada.setAdapter(adapterAlmacenamiento);
        spinnerAlmacenamientoSalida.setAdapter(adapterAlmacenamiento);

        String[] unidadesTiempo = {"Segundo", "Minuto", "Hora", "Día"};
        ArrayAdapter<String> adapterTiempo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidadesTiempo);
        spinnerTiempoEntrada.setAdapter(adapterTiempo);
        spinnerTiempoSalida.setAdapter(adapterTiempo);

        String[] unidadesTransferencia = {"Bit", "Kilobit", "Megabit", "Gigabit"};
        ArrayAdapter<String> adapterTransferencia = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidadesTransferencia);
        spinnerTransferenciaEntrada.setAdapter(adapterTransferencia);
        spinnerTransferenciaSalida.setAdapter(adapterTransferencia);

        tasasDeCambio = new HashMap<>();
        tasasDeCambio.put("Dolar", 1.0);
        tasasDeCambio.put("Euros", 0.85);
        tasasDeCambio.put("Yen Japones", 110.0);
        tasasDeCambio.put("Peso Mexicano", 20.0);
        tasasDeCambio.put("Colon Costarrisence", 620.0);
        tasasDeCambio.put("Quetzal Guatemalteco", 7.75);
        tasasDeCambio.put("Lempira Hondureño", 24.0);
        tasasDeCambio.put("Cordoba Nicaraguense", 35.0);
        tasasDeCambio.put("Colon Salvadoreño", 8.75);
        tasasDeCambio.put("Peso Argentino", 95.0);
        tasasDeCambio.put("Sol Peruano", 3.8);
        tasasDeCambio.put("Bolivares Venezolanos", 4.5);

        tasasDeMasa = new HashMap<>();
        tasasDeMasa.put("Kilogramo", 1.0);
        tasasDeMasa.put("Gramo", 1000.0);
        tasasDeMasa.put("Libra", 2.20462);
        tasasDeMasa.put("Onza", 35.274);

        tasasDeVolumen = new HashMap<>();
        tasasDeVolumen.put("Litro", 1.0);
        tasasDeVolumen.put("Mililitro", 1000.0);
        tasasDeVolumen.put("Galón", 0.264172);
        tasasDeVolumen.put("Pulgada cúbica", 61.0237);

        tasasDeLongitud = new HashMap<>();
        tasasDeLongitud.put("Metro", 1.0);
        tasasDeLongitud.put("Centímetro", 100.0);
        tasasDeLongitud.put("Pulgada", 39.3701);
        tasasDeLongitud.put("Pie", 3.28084);

        tasasDeAlmacenamiento = new HashMap<>();
        tasasDeAlmacenamiento.put("Byte", 1.0);
        tasasDeAlmacenamiento.put("Kilobyte", 0.001);
        tasasDeAlmacenamiento.put("Megabyte", 0.000001);
        tasasDeAlmacenamiento.put("Gigabyte", 0.000000001);

        tasasDeTiempo = new HashMap<>();
        tasasDeTiempo.put("Segundo", 1.0);
        tasasDeTiempo.put("Minuto", 0.0166667);
        tasasDeTiempo.put("Hora", 0.000277778);
        tasasDeTiempo.put("Día", 0.0000115741);

        tasasDeTransferencia = new HashMap<>();
        tasasDeTransferencia.put("Bit", 1.0);
        tasasDeTransferencia.put("Kilobit", 0.001);
        tasasDeTransferencia.put("Megabit", 0.000001);
        tasasDeTransferencia.put("Gigabit", 0.000000001);
    }

    private void configurarListeners() {
        btnConvertirMoneda.setOnClickListener(v -> convertirMoneda());
        btnConvertirMasa.setOnClickListener(v -> convertirMasa());
        btnConvertirVolumen.setOnClickListener(v -> convertirVolumen());
        btnConvertirLongitud.setOnClickListener(v -> convertirLongitud());
        btnConvertirAlmacenamiento.setOnClickListener(v -> convertirAlmacenamiento());
        btnConvertirTiempo.setOnClickListener(v -> convertirTiempo());
        btnConvertirTransferencia.setOnClickListener(v -> convertirTransferencia());
    }

    private void convertirMoneda() {
        String monedaEntrada = spinnerMonedaEntrada.getSelectedItem().toString();
        String monedaSalida = spinnerMonedaSalida.getSelectedItem().toString();

        try {
            double monto = Double.parseDouble(etMonto.getText().toString());
            double tasaEntrada = tasasDeCambio.get(monedaEntrada);
            double tasaSalida = tasasDeCambio.get(monedaSalida);

            double resultado = monto * (tasaSalida / tasaEntrada);
            tvResultadoMoneda.setText("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            tvResultadoMoneda.setText("Ingrese un monto válido.");
        }
    }

    private void convertirMasa() {
        String masaEntrada = spinnerMasaEntrada.getSelectedItem().toString();
        String masaSalida = spinnerMasaSalida.getSelectedItem().toString();

        try {
            double masa = Double.parseDouble(etMasa.getText().toString());
            double tasaEntrada = tasasDeMasa.get(masaEntrada);
            double tasaSalida = tasasDeMasa.get(masaSalida);

            double resultado = masa * (tasaSalida / tasaEntrada);
            tvResultadoMasa.setText("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            tvResultadoMasa.setText("Ingrese una masa válida.");
        }
    }

    private void convertirVolumen() {
        String volumenEntrada = spinnerVolumenEntrada.getSelectedItem().toString();
        String volumenSalida = spinnerVolumenSalida.getSelectedItem().toString();

        try {
            double volumen = Double.parseDouble(etVolumen.getText().toString());
            double tasaEntrada = tasasDeVolumen.get(volumenEntrada);
            double tasaSalida = tasasDeVolumen.get(volumenSalida);

            double resultado = volumen * (tasaSalida / tasaEntrada);
            tvResultadoVolumen.setText("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            tvResultadoVolumen.setText("Ingrese un volumen válido.");
        }
    }

    private void convertirLongitud() {
        String longitudEntrada = spinnerLongitudEntrada.getSelectedItem().toString();
        String longitudSalida = spinnerLongitudSalida.getSelectedItem().toString();

        try {
            double longitud = Double.parseDouble(etLongitud.getText().toString());
            double tasaEntrada = tasasDeLongitud.get(longitudEntrada);
            double tasaSalida = tasasDeLongitud.get(longitudSalida);

            double resultado = longitud * (tasaSalida / tasaEntrada);
            tvResultadoLongitud.setText("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            tvResultadoLongitud.setText("Ingrese una longitud válida.");
        }
    }

    private void convertirAlmacenamiento() {
        String almacenamientoEntrada = spinnerAlmacenamientoEntrada.getSelectedItem().toString();
        String almacenamientoSalida = spinnerAlmacenamientoSalida.getSelectedItem().toString();

        try {
            double almacenamiento = Double.parseDouble(etAlmacenamiento.getText().toString());
            double tasaEntrada = tasasDeAlmacenamiento.get(almacenamientoEntrada);
            double tasaSalida = tasasDeAlmacenamiento.get(almacenamientoSalida);

            double resultado = almacenamiento * (tasaSalida / tasaEntrada);
            tvResultadoAlmacenamiento.setText("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            tvResultadoAlmacenamiento.setText("Ingrese un valor de almacenamiento válido.");
        }
    }

    private void convertirTiempo() {
        String tiempoEntrada = spinnerTiempoEntrada.getSelectedItem().toString();
        String tiempoSalida = spinnerTiempoSalida.getSelectedItem().toString();

        try {
            double tiempo = Double.parseDouble(etTiempo.getText().toString());
            double tasaEntrada = tasasDeTiempo.get(tiempoEntrada);
            double tasaSalida = tasasDeTiempo.get(tiempoSalida);

            double resultado = tiempo * (tasaSalida / tasaEntrada);
            tvResultadoTiempo.setText("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            tvResultadoTiempo.setText("Ingrese un tiempo válido.");
        }
    }

    private void convertirTransferencia() {
        String transferenciaEntrada = spinnerTransferenciaEntrada.getSelectedItem().toString();
        String transferenciaSalida = spinnerTransferenciaSalida.getSelectedItem().toString();

        try {
            double transferencia = Double.parseDouble(etTransferencia.getText().toString());
            double tasaEntrada = tasasDeTransferencia.get(transferenciaEntrada);
            double tasaSalida = tasasDeTransferencia.get(transferenciaSalida);

            double resultado = transferencia * (tasaSalida / tasaEntrada);
            tvResultadoTransferencia.setText("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            tvResultadoTransferencia.setText("Ingrese un valor de transferencia válido.");
        }
    }
}
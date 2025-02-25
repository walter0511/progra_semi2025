package com.example.miprimeraaplicacion;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    private EditText edtConsumo, edtValorArea;
    private TextView txtResultadoAgua, txtResultadoArea;
    private Spinner spinnerEntrada, spinnerSalida;

    private final String[] unidades = {
            "Milímetro cuadrado (mm²)", "Centímetro cuadrado (cm²)", "Decímetro cuadrado (dm²)",
            "Metro cuadrado (m²)", "Decámetro cuadrado (dam²)", "Hectárea (ha)",
            "Kilómetro cuadrado (km²)", "Pulgada cuadrada (in²)", "Pie cuadrado (ft²)", "Yarda cuadrada (yd²)"
    };

    private final double[] factores = {
            1, 100, 10_000, 1_000_000, 100_000_000, 10_000_000_000L,
            1_000_000_000_000L, 645.16, 92_903, 836_127
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("Pago de Agua")
                .setContent(R.id.tabPagoAgua));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("Conversor de Área")
                .setContent(R.id.tabConversorArea));

        edtConsumo = findViewById(R.id.edtConsumo);
        txtResultadoAgua = findViewById(R.id.txtResultadoAgua);
        Button btnCalcularAgua = findViewById(R.id.btnCalcularAgua);
        btnCalcularAgua.setOnClickListener(v -> calcularPagoAgua());

        edtValorArea = findViewById(R.id.edtValorArea);
        spinnerEntrada = findViewById(R.id.spinnerEntrada);
        spinnerSalida = findViewById(R.id.spinnerSalida);
        txtResultadoArea = findViewById(R.id.txtResultadoArea);
        Button btnConvertirArea = findViewById(R.id.btnConvertirArea);
        btnConvertirArea.setOnClickListener(v -> convertirArea());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidades);
        spinnerEntrada.setAdapter(adapter);
        spinnerSalida.setAdapter(adapter);
    }

    private void calcularPagoAgua() {
        String input = edtConsumo.getText().toString();
        if (input.isEmpty()) {
            txtResultadoAgua.setText("Ingrese un valor válido.");
            return;
        }

        double consumo = Double.parseDouble(input);
        double tarifaBase = 5.0;
        double costoPorMetroCubico = 0.75;
        double total = tarifaBase + (consumo * costoPorMetroCubico);

        txtResultadoAgua.setText("Total a pagar: $" + String.format("%.2f", total));
    }

    private void convertirArea() {
        String input = edtValorArea.getText().toString();
        if (input.isEmpty()) {
            txtResultadoArea.setText("Ingrese un valor válido.");
            return;
        }

        double valor = Double.parseDouble(input);
        int posEntrada = spinnerEntrada.getSelectedItemPosition();
        int posSalida = spinnerSalida.getSelectedItemPosition();

        double valorBase = valor / factores[posEntrada];  // Convertir a m²
        double resultado = valorBase * factores[posSalida];  // Convertir a la unidad de salida

        txtResultadoArea.setText("Resultado: " + String.format("%.6f", resultado));
    }
}
package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText  num1, num2;
    private TextView resultado;
    private RadioGroup radioGroupOperaciones;
    private Button btnCalcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vincular vistas
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        resultado = findViewById(R.id.resultado);
        radioGroupOperaciones = findViewById(R.id.radioGroupOperaciones);
        btnCalcular = findViewById(R.id.btnCalcular);


        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularResultado();
            }
        });
    }

    private void calcularResultado() {

        String strNum1 = num1.getText().toString();
        String strNum2 = num2.getText().toString();

        if (strNum1.isEmpty() || strNum2.isEmpty()) {
            resultado.setText("Ingresa ambos números");
            return;
        }

        double n1 = Double.parseDouble(strNum1);
        double n2 = Double.parseDouble(strNum2);
        double result = 0;


        int selectedId = radioGroupOperaciones.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);

        if (radioButton == null) {
            resultado.setText("Selecciona una operación");
            return;
        }

        String operacion = radioButton.getText().toString();


        switch (operacion) {
            case "Suma":
                result = n1 + n2;
                break;
            case "Resta":
                result = n1 - n2;
                break;
            case "Multiplicación":
                result = n1 * n2;
                break;
            case "División":
                if (n2 != 0) {
                    result = n1 / n2;
                } else {
                    resultado.setText("Error: División por cero");
                    return;
                }
                break;
            case "Exponente":
                result = Math.pow(n1, n2);
                break;
            case "Porcentaje":
                result = (n1 * n2) / 100;
                break;
            case "Raíz":
                if (n1 >= 0) {
                    result = Math.pow(n1, 1 / n2);
                } else {
                    resultado.setText("Error: Raíz de número negativo");
                    return;
                }
                break;
            case "Factorial":
                if (n1 >= 0 && n1 == (int) n1) {
                    result = factorial((int) n1);
                } else {
                    resultado.setText("Error: Factorial de número no entero");
                    return;
                }
                break;
            default:
                resultado.setText("Operación no válida");
                return;
        }


        resultado.setText("Respuesta: " + result);
    }


    private double factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
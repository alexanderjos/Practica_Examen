package com.example.practica;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.practica.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private List<Button> botones; // Lista de botones
    private List<Integer> listaValores; // Lista de valores de los botones
    private int totalClicks = 0; // Contador total de clics
    private int primerosClick = -1; // Índice del primer botón clicado
    private int segundoClick = -1; // Índice del segundo botón clicado
    private int numeroDeCoincidencias = 0; // Contador de coincidencias

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String nombre = SecondFragmentArgs.fromBundle(getArguments()).getNombreArg();
        String dificultad = SecondFragmentArgs.fromBundle(getArguments()).getDificultadArg();
        binding.txtBienvenida.setText("Hola " + nombre);
        int dificultadInt = "Facil".equalsIgnoreCase(dificultad) ? 0 : 1; // Ignora mayúsculas
        crearBotones(dificultadInt);
        binding.btnVolver.setOnClickListener(view1 -> {
            SecondFragmentDirections.ActionSecondFragmentToFirstFragment action =
                    SecondFragmentDirections.actionSecondFragmentToFirstFragment(nombre, totalClicks, dificultad);
            NavHostFragment.findNavController(SecondFragment.this).navigate(action);
        });
    }

    private void crearBotones(int dificultad) {
        // Elimina todas las vistas anteriores del grid donde se colocarán los botones
        binding.gridMemorias.removeAllViews();

        // Define las dimensiones de la cuadrícula dependiendo de la dificultad
        int columnas = (dificultad == 0) ? 3 : 4; // Si la dificultad es 0, se crean 3 columnas; si no, 4
        int filas = (dificultad == 0) ? 4 : 5; // Si la dificultad es 0, se crean 4 filas; si no, 5
        int maxValor = (dificultad == 0) ? 6 : 10; // Se usan 6 valores para fácil, 10 para difícil

        // Genera un conjunto único de números para el juego
        Set<Integer> valores = new HashSet<>();
        while (valores.size() < (columnas * filas) / 2) {
            // Añade números aleatorios al conjunto hasta que haya suficientes
            valores.add((int) (Math.random() * maxValor) + 1);
        }

        // Crea una lista con los valores generados
        listaValores = new ArrayList<>(valores);
        listaValores.addAll(listaValores); // Duplica los valores para hacer pares
        Collections.shuffle(listaValores); // Mezcla los valores para que estén en posiciones aleatorias

        // Inicializa la lista de botones
        botones = new ArrayList<>();

        // Genera los botones y los añade al grid
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                // Crea un nuevo botón
                Button button = new Button(requireContext());
                button.setText(""); // Deja el texto vacío al principio

                // Define el layout para los botones dentro del grid
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1f); // Distribuye el alto en la fila
                params.columnSpec = GridLayout.spec(j, 1f); // Distribuye el ancho en la columna
                params.width = 0; // Ancho proporcional
                params.height = 0; // Altura proporcional
                params.setMargins(8, 8, 8, 8); // Márgenes alrededor del botón
                button.setLayoutParams(params);
                button.setTextSize(30); // Tamaño del texto

                // Índice único para identificar cada botón
                final int index = i * columnas + j;

                // Define el comportamiento del botón cuando se le hace clic
                button.setOnClickListener(v -> manejarClick(index, button));

                // Añade el botón a la lista y lo añade al grid
                botones.add(button);
                binding.gridMemorias.addView(button);
            }
        }
    }

    private void manejarClick(int index, Button button) {
        // Verifica si el botón ya está desactivado
        if (!button.isEnabled()) return;

        // Incrementa el contador total de clics
        totalClicks++;

        // Muestra el número correspondiente al índice en el botón
        button.setText(String.valueOf(listaValores.get(index)));
        button.setEnabled(false); // Desactiva el botón después del clic

        // Si es el primer clic
        if (primerosClick == -1) {
            primerosClick = index;
        } else if (segundoClick == -1 && index != primerosClick) {
            // Si es el segundo clic (y no es el mismo botón del primero)
            segundoClick = index;
            verificarCoincidencia(); // Comprueba si los dos clics forman una coincidencia
        }
    }

    private void verificarCoincidencia() {
        // Si los valores de los dos clics coinciden
        if (listaValores.get(primerosClick).equals(listaValores.get(segundoClick))) {
            numeroDeCoincidencias++; // Incrementa el número de coincidencias
            Toast.makeText(getActivity(), "¡Coincidencia!", Toast.LENGTH_SHORT).show();
        } else {
            // Si no coinciden, muestra un mensaje y oculta los botones después de un segundo
            Toast.makeText(getActivity(), "No coinciden. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
            new android.os.Handler().postDelayed(this::ocultarBotones, 1000);
        }

        // Después de un segundo, reinicia los valores de los clics
        new android.os.Handler().postDelayed(() -> {
            primerosClick = -1;
            segundoClick = -1;
            Toast.makeText(getActivity(), "Total de clics: " + totalClicks, Toast.LENGTH_SHORT).show();
        }, 1000);
    }

    private void ocultarBotones() {
        // Si el primer botón fue seleccionado
        if (primerosClick != -1) {
            botones.get(primerosClick).setText(""); // Oculta el valor del primer botón
            botones.get(primerosClick).setEnabled(true); // Reactiva el botón
        }
        // Si el segundo botón fue seleccionado
        if (segundoClick != -1) {
            botones.get(segundoClick).setText(""); // Oculta el valor del segundo botón
            botones.get(segundoClick).setEnabled(true); // Reactiva el botón
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

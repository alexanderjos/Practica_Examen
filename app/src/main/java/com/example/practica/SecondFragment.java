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
        binding.btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecondFragmentDirections.ActionSecondFragmentToFirstFragment action =
                        SecondFragmentDirections.actionSecondFragmentToFirstFragment(nombre,totalClicks,dificultad);
                NavHostFragment.findNavController(SecondFragment.this).navigate(action);
            }
        });
    }

    private void crearBotones(int dificultad) {
        binding.gridMemorias.removeAllViews();

        int columnas = (dificultad == 0) ? 3 : 4; // 3 columnas
        int filas = (dificultad == 0) ? 4 : 5; // 4 filas
        int maxValor = (dificultad == 0) ? 6 : 10; // 6 para fácil, 10 para difícil

        Set<Integer> valores = new HashSet<>();
        while (valores.size() < (columnas * filas) / 2) {
            valores.add((int) (Math.random() * maxValor) + 1);
        }
        listaValores = new ArrayList<>(valores);
        listaValores.addAll(listaValores); // Duplicamos los valores para el juego
        Collections.shuffle(listaValores); // Mezclamos los valores

        botones = new ArrayList<>(); // Inicializa la lista de botones

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Button button = new Button(requireContext());
                button.setText(""); // Texto vacío inicialmente
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i); // Fila
                params.columnSpec = GridLayout.spec(j); // Columna
                button.setLayoutParams(params);

                final int index = i * columnas + j; // Índice único para cada botón

                button.setOnClickListener(v -> manejarClick(index, button));
                botones.add(button); // Agrega el botón a la lista
                binding.gridMemorias.addView(button);
            }
        }
    }

    private void manejarClick(int index, Button button) {
        // Verificar si el botón ya está desactivado
        if (!button.isEnabled()) return;

        // Incrementar el contador total de clics
        totalClicks++;

        // Muestra el número en el botón
        button.setText(String.valueOf(listaValores.get(index)));
        button.setEnabled(false); // Desactiva el botón

        if (primerosClick == -1) {
            // Primer clic
            primerosClick = index;
        } else if (segundoClick == -1 && index != primerosClick) {
            // Segundo clic
            segundoClick = index;
            verificarCoincidencia();
        }
    }

    private void verificarCoincidencia() {
        // Si los números coinciden
        if (listaValores.get(primerosClick).equals(listaValores.get(segundoClick))) {
            numeroDeCoincidencias++;
            Toast.makeText(getActivity(), "¡Coincidencia!", Toast.LENGTH_SHORT).show();
        } else {
            // Si no coinciden
            Toast.makeText(getActivity(), "No coinciden. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
            // Temporizador para ocultar los números nuevamente
            new android.os.Handler().postDelayed(this::ocultarBotones, 1000);
        }

        // Reinicia el registro de clics después de un segundo
        new android.os.Handler().postDelayed(() -> {
            primerosClick = -1;
            segundoClick = -1;
            Toast.makeText(getActivity(), "Total de clics: " + totalClicks, Toast.LENGTH_SHORT).show();
        }, 1000);
    }

    private void ocultarBotones() {
        // Voltear y reactivar botones si no coinciden
        if (primerosClick != -1) {
            botones.get(primerosClick).setText(""); // Oculta el primer botón
            botones.get(primerosClick).setEnabled(true); // Vuelve a activar el botón
        }
        if (segundoClick != -1) {
            botones.get(segundoClick).setText(""); // Oculta el segundo botón
            botones.get(segundoClick).setEnabled(true); // Vuelve a activar el botón
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

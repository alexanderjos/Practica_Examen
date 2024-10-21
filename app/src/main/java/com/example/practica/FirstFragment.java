package com.example.practica;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.practica.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strNombre = binding.edtNombre.getText().toString();
                int select = binding.rgDificultad.getCheckedRadioButtonId();
                RadioButton selectRadio = binding.getRoot().findViewById(select);
                String textoSelecionado = selectRadio.getText().toString();
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(strNombre,textoSelecionado);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        binding.btnResultrados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreJ = FirstFragmentArgs.fromBundle(getArguments()).getNombreP1();
                int tags = FirstFragmentArgs.fromBundle(getArguments()).getTagsP1();
                String dificultad = FirstFragmentArgs.fromBundle(getArguments()).getDificultad();
                FirstFragmentDirections.ActionFirstFragmentToTercerFragment action1 =
                        FirstFragmentDirections.actionFirstFragmentToTercerFragment(nombreJ,tags,dificultad);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action1);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
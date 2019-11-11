package com.example.bledemo.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bledemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharacteristicDetailFragment extends Fragment {

    String charUUID;
    String servUUID;
    OnCharacteristicSelectedInterface caller;

    public CharacteristicDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_characteristic_detail, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        charUUID = getArguments().getString("charUUID");
        servUUID = getArguments().getString("servUUID");
        TextView uuidTextView = getView().findViewById(R.id.uuid_text_view);
        uuidTextView.setText(charUUID);
        caller = (OnCharacteristicSelectedInterface) getContext();

        TextView valueTextview = getView().findViewById(R.id.value_text_view);
        valueTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edittext = new EditText(getActivity());
                alert.setMessage("The value must be in Hex format");
                alert.setTitle("Set Value");

                alert.setView(edittext);

                alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = edittext.getText().toString();
                        caller.onValueSet(value, servUUID, charUUID);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

    }
}

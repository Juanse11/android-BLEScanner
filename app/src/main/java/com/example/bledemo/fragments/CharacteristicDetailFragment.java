package com.example.bledemo.fragments;


import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bledemo.R;
import com.example.bledemo.ble.BLEManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharacteristicDetailFragment extends Fragment {

    BluetoothGattCharacteristic characteristic;
    OnCharacteristicSelectedInterface caller;
    ArrayList<String> listOfDescriptors = new ArrayList<>();

    ArrayAdapter<String> adapter;
    public CharacteristicDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_characteristic_detail, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listOfDescriptors);
        characteristic= getArguments().<BluetoothGattCharacteristic>getParcelable("characteristic");

        caller = (OnCharacteristicSelectedInterface) getContext();
        caller.getCharacteristic(characteristic);
        ProgressBar progressBar = getView().findViewById(R.id.indeterminateCharacteristicBar);
        progressBar.setVisibility(View.VISIBLE);
        TextView statusTextView = getView().findViewById(R.id.characteristic_status_text_view);
        statusTextView.setVisibility(View.VISIBLE);

        TextView uuidTextView = getView().findViewById(R.id.uuid_text_view);
        uuidTextView.setText(characteristic.getUuid().toString());

        TextView propertiesTextView = getView().findViewById(R.id.properties_text_view);
        propertiesTextView.setVisibility(View.GONE);

        final TextView valueTextview = getView().findViewById(R.id.value_text_view);
        valueTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edittext = new EditText(getActivity());
                edittext.setPadding(20,20,20,20);
                alert.setMessage("The value must be in Hex format");
                alert.setTitle("Set Value");
                alert.setView(edittext);
                alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = edittext.getText().toString();
                        valueTextview.setText(value);
                        caller.onValueSet(value, characteristic);
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

    public void updateValue(String value){
        final TextView valueTextview = getView().findViewById(R.id.value_text_view);
        valueTextview.setText(value);
    }

    public void readValue(String value, BluetoothGattCharacteristic c){
        boolean isReadable = BLEManager.isCharacteristicReadable(c);
        boolean isWritable = BLEManager.isCharacteristicWriteable(c);
        boolean isNotifiable = BLEManager.isCharacteristicNotifiable(c);

        TextView textView1 = getView().findViewById(R.id.read_text_view);
        textView1.setVisibility(isReadable ? View.VISIBLE : View.GONE);
        TextView textView2 = getView().findViewById(R.id.write_text_view);
        textView2.setVisibility(isWritable ? View.VISIBLE : View.GONE);
        TextView textView3 = getView().findViewById(R.id.notify_text_view);
        textView3.setVisibility(isNotifiable ? View.VISIBLE : View.GONE);

        final TextView valueTextview = getView().findViewById(R.id.value_text_view);
        valueTextview.setText(value);

        ProgressBar progressBar = getView().findViewById(R.id.indeterminateCharacteristicBar);
        progressBar.setVisibility(View.GONE);
        TextView statusTextView = getView().findViewById(R.id.characteristic_status_text_view);
        statusTextView.setVisibility(View.GONE);
        for (BluetoothGattDescriptor d: c.getDescriptors()) {
            listOfDescriptors.add(d.getUuid().toString());
        }
        ListView listView = getView().findViewById(R.id.descriptors_list_view);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}

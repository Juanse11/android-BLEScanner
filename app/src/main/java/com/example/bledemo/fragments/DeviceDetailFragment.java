package com.example.bledemo.fragments;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bledemo.MainActivity;
import com.example.bledemo.R;
import com.example.bledemo.adapters.ExpandableListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceDetailFragment extends Fragment {
    private ExpandableListView expandableListView;

    private ExpandableListViewAdapter expandableListViewAdapter;

    private List<BluetoothGattService> listDataGroup;
    private String address;
    private String deviceName;
    private HashMap<BluetoothGattService, List<BluetoothGattCharacteristic>> listDataChild;

    public DeviceDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initObjects();
        address = getArguments().getString("deviceAddress");
        ((MainActivity) getActivity()).connectToGatServer(address);
        deviceName = getArguments().getString("deviceName");
    }

    /**
     * method to initialize the views
     */
    private void initViews() {
        ProgressBar progressBar = getView().findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.VISIBLE);
        address = getArguments().getString("deviceAddress");
        deviceName = getArguments().getString("deviceName");
        expandableListView = getView().findViewById(R.id.expandableListView);
        TextView deviceNameTextView = getView().findViewById(R.id.device_name_text_view);
        deviceNameTextView.setText(deviceName);
        TextView deviceAddressTextView = getView().findViewById(R.id.device_address_text_view);
        deviceAddressTextView.setText(address);

    }

    /**
     * method to initialize the listeners
     */
    private void initListeners() {

        // ExpandableListView on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        // ExpandableListView Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        // ExpandableListView Group collapsed listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

    }

    /**
     * method to initialize the objects
     */
    private void initObjects() {

        listDataGroup = new ArrayList<>();

        listDataChild = new HashMap<>();

        expandableListViewAdapter = new ExpandableListViewAdapter(getContext(), listDataGroup, listDataChild);

        expandableListView.setAdapter(expandableListViewAdapter);

    }


    public void initListData(List<BluetoothGattService> services ) {
        listDataGroup.addAll(services);
        for (BluetoothGattService s: services ) {
            listDataChild.put(s, s.getCharacteristics());
        }
        expandableListViewAdapter.notifyDataSetChanged();
        ProgressBar progressBar = getView().findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.GONE);
    }
}

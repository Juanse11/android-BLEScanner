package com.example.bledemo.fragments;


import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

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

    private List<String> listDataGroup;

    private HashMap<String, List<BluetoothGattCharacteristic>> listDataChild;

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

        initListData();
    }

    /**
     * method to initialize the views
     */
    private void initViews() {

        expandableListView = getView().findViewById(R.id.expandableListView);

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


    private void initListData() {


        listDataGroup.add("Test Service");
        listDataGroup.add("Test Service 1");
        listDataGroup.add("Test Service 2" );
        listDataGroup.add("Test Service 3");

        String[] array;

        expandableListViewAdapter.notifyDataSetChanged();
    }
}

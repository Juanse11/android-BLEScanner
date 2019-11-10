package com.example.bledemo.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bledemo.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {

    ArrayList<String> listOfLogs = new ArrayList<>();
    ArrayAdapter<String> adapter;

    public LogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listOfLogs);
    }

    public void newLogEntry(String log) {
        Date currentTime = new Date();
        listOfLogs.add(currentTime.toString() + " --- " + log);
        if (getView() != null) {
            updateList();
        }
    }

    public void updateList() {
        ListView listView = getView().findViewById(R.id.log_list_id);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}

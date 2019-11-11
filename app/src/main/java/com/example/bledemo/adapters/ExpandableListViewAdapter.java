package com.example.bledemo.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bledemo.R;
import com.example.bledemo.fragments.OnCharacteristicSelectedInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private final OnCharacteristicSelectedInterface caller;
    private Context context;
    private CharacteristicListAdapter characteristicListAdapter;
    // group titles
    private List<BluetoothGattService> listDataGroup;

    // child data
    private HashMap<BluetoothGattService, List<BluetoothGattCharacteristic>> listDataChild;

    public ExpandableListViewAdapter(Context context, List<BluetoothGattService> listDataGroup,
                                     HashMap<BluetoothGattService, List<BluetoothGattCharacteristic>> listChildData, OnCharacteristicSelectedInterface caller) {
        this.context = context;
        this.listDataGroup = listDataGroup;
        this.listDataChild = listChildData;
        this.caller = caller;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final BluetoothGattCharacteristic child = (BluetoothGattCharacteristic) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.service_list_item, null);
        }

        TextView textViewChild = convertView
                .findViewById(R.id.characteristic_list_item_text_view);
        textViewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caller.onCharacteristicSelected(child);
            }
        });
        textViewChild.setTypeface(null, Typeface.BOLD);
        textViewChild.setText(child.getUuid().toString());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.listDataChild.get(this.listDataGroup.get(groupPosition)) != null) {
            return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                    .size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataGroup.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataGroup.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        BluetoothGattService headerTitle = (BluetoothGattService) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.services_list_group, null);
        }

        TextView textViewGroup = convertView
                .findViewById(R.id.textViewGroup);
        textViewGroup.setTypeface(null, Typeface.BOLD);
        textViewGroup.setText(headerTitle.getUuid().toString());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
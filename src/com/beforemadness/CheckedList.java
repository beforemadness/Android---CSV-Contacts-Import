package com.beforemadness;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public class CheckedList extends ArrayAdapter<Hashtable<String, String>> {

	private Context context;
	private ArrayList<Hashtable<String, String>> items;

	public CheckedList(Context context,ArrayList<Hashtable<String, String>> data, int resource) {
		super(context, resource,data);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.items = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {

			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.check_box_list, null);
		}
		Hashtable<String, String> o = items.get(position);
		if (o != null) {
			CheckBox cBox = (CheckBox) v.findViewById(R.id.bcheck);
			cBox.setText(o.get("firstname"));
			//cBox.setChecked(true);
		}
		return v;
	}

}

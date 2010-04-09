package com.beforemadness;

import java.util.ArrayList;
import java.util.Hashtable;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.CheckedTextView;

public class CheckedList extends ArrayAdapter<Hashtable<String, String>> {

	private Context context;
	private ArrayList<Hashtable<String, String>> items;

	public CheckedList(Context context,ArrayList<Hashtable<String, String>> data, int resource) {
		super(context, resource,data);
		// TODO Auto-generated constructor stub
		this.context = (CSVContactsImport)context;
		this.items = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Hashtable<String, String> o = items.get(position);
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1 = vi.inflate(R.layout.check_box_list, null);
		CheckedTextView ctv =(CheckedTextView)row1.findViewById(R.id.text1);
		String firstname = o.get("firstname") ; 
		if ( firstname != null) {
			ctv.setTag(new Integer(position));
			ctv.setText(o.get("firstname"));
		} else {
			items.remove(position);
			remove(items.get(position));
		}
		return(row1); 
	}
}

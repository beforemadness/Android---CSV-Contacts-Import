package com.beforemadness;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;





import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
		
/*		View v = convertView;
		if (v == null) {

			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.check_box_list, null);
		}
		Hashtable<String, String> o = items.get(position);
		if (o != null) {
			CheckBox cBox = (CheckBox) v.findViewById(R.id.bcheck);
			cBox.setTag(new Integer(position));
			cBox.setText(o.get("firstname"));
			//cBox.setChecked(true);
		}
		return v;*/
		Hashtable<String, String> o = items.get(position);
		//if (row==null) {
			/*LayoutInflater inflater= (LayoutInflater)((CSVContactsImport) context).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=inflater.inflate(R.layout.check_box_list, null); 
			wrapper=new ViewWrapper(row);  
		    row.setTag(wrapper);  
		    cb=wrapper.getCheckBox();  
			*/
		//}  else {  
		   // wrapper=(ViewWrapper)row.getTag();  
		    //cb=wrapper.getCheckBox();  
		//}  
		
		//cb.setTag(new Integer(position));  
		//cb.setText(o.get("firstname"));
		
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1 = vi.inflate(R.layout.check_box_list, null);
		CheckedTextView ctv =(CheckedTextView)row1.findViewById(R.id.text1);
		ctv.setTag(new Integer(position));
		ctv.setText(o.get("firstname"));
		return(row1); 
	}
}

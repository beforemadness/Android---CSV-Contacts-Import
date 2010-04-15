package com.beforemadness;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SimpleCursorAdapter;

public class CheckedCursor extends SimpleCursorAdapter {
	
    private Cursor c;
    private Context context;

	public CheckedCursor(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		this.c = c;
		this.context = context;
	}
	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		
//		Hashtable<String, String> o = items.get(position);
//		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View row1 = vi.inflate(R.layout.check_box_list, null);
//		CheckedTextView ctv =(CheckedTextView)row1.findViewById(R.id.text1);
//		String firstname = o.get("firstname") ; 
//		if ( firstname != null) {
//			ctv.setTag(new Integer(position));
//			ctv.setText(o.get("firstname"));
//		} else {
//			items.remove(position);
//			remove(items.get(position));
//		}
//		return(row1); 
//	}

}

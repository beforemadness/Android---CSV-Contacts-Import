package com.beforemadness;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.widget.SimpleCursorAdapter;

public class MainScreen extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listContacts();
	}
	
	public void listContacts() {
		
		String[] projection = new String[] {
				People.NAME,
				People.Phones._COUNT,
				People.PRIMARY_EMAIL_ID,
				People.NUMBER
		};
		
		Uri contacts = People.CONTENT_URI;
		Cursor managedCursor = managedQuery(contacts,projection,null,null,People.NAME + "ASC");
		 String[] displayFields = new String[] {People.NAME, People.NUMBER};
		int[] displayViews = new int[] { android.R.id.text1, android.R.id.text2 };

		setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, managedCursor,displayFields, displayViews));
		
	}

}

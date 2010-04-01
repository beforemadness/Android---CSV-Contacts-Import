package com.beforemadness;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts.People;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

public class CSVContactsImport extends ListActivity {
	/** Called when the activity is first created. */
	private final int DIALOG_FILE_SELECT = 1;
	private final int DIALOG_PLEASE_WAIT = 2;
	private final int DIALOG_OK = 3;
	private ArrayList<Hashtable<String, String>> mContactsList = new ArrayList<Hashtable<String, String>>();
	final Handler mHandler = new Handler();
	private FileHelper mFileHelper = new FileHelper();
	private boolean mChecked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		showDialog(DIALOG_FILE_SELECT);
	}

	// Overriding Methods
	public void onCreate1(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mFileHelper = new FileHelper();
		showDialog(DIALOG_FILE_SELECT);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_FILE_SELECT:
			CharSequence[] files = mFileHelper.getAllFiles();
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("pick the file");
			builder.setCancelable(true);
			builder.setSingleChoiceItems(files, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							dismissDialog(DIALOG_FILE_SELECT);
							showDialog(DIALOG_PLEASE_WAIT);
							// Toast.makeText(getApplicationContext(),
							// items[item], Toast.LENGTH_SHORT).show();
							mFileHelper.setSelectedFileIndex(item);
							getFromCSVThread();
						}

					});
			return builder.create();

		case DIALOG_PLEASE_WAIT:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("Please wait ... ");
			dialog.setCancelable(false);
			return dialog;

		case DIALOG_OK:
			Builder builder1 = new AlertDialog.Builder(this);
			builder1.setMessage("Contacts Have been imported.").setCancelable(
					false).setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CSVContactsImport.this.finish();
						}
					});
			return builder1.create();

		default:
			break;
		}

		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_import:
			showDialog(DIALOG_PLEASE_WAIT);
			startImportOfContacts();
			break;
		case R.id.menu_select:
			selectAll();
			break;

		default:
			break;
		}
		return false;

	}

	private void selectAll() {
		// TODO Auto-generated method stub
		if(!mChecked) {
			mChecked = true;
		} else {
			mChecked = false;
		}
    	ListView lst = getListView();
  
    	for (int i = 0; i < lst.getChildCount(); i++) {
			CheckBox cbox = (CheckBox) ((View)lst.getChildAt(i)).findViewById(R.id.bcheck);
			cbox.setChecked(mChecked);
		}
		
	}

	private void startImportOfContacts() {
		Thread t = new Thread() {
			public void run() {
				importContacts();
				mHandler.post(mUpdateImport);				
			}
		};
		
		t.start();
	}
	
	final Runnable mUpdateImport = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateSuccess();
		}

		private void updateSuccess() {
			// TODO Auto-generated method stub
			showDialog(DIALOG_OK);
		}
	};	
	
	private void importContacts() {
		// TODO Auto-generated method stub
    	ListView lst = getListView();
    	
    	for (int i = 0; i < lst.getChildCount(); i++) {
			CheckBox cbox = (CheckBox) ((View)lst.getChildAt(i)).findViewById(R.id.bcheck);
			if (cbox.isChecked()) {
				//Add record to the Contacts DB
				ContentValues values = new ContentValues(); 
				Hashtable<String, String> contacts = mContactsList.get(i);
				String firstname = contacts.remove("firstname").trim();
				String lastname = "";
				String email = "";
				String address = "";
				if (contacts.containsKey("lastname"))
					lastname = contacts.remove("lastname").trim();
				if (contacts.containsKey("email"))
					email = contacts.remove("email");
				String other = contacts.remove("unknown");
				if (contacts.containsKey("address"))
					address = contacts.remove("address");
				values.put(People.NAME, firstname+" "+lastname);
				values.put(People.PRIMARY_EMAIL_ID, email);
				Uri uri = getContentResolver().insert(People.CONTENT_URI, values);
				
				Uri phoneUri = Uri.withAppendedPath(uri, People.Phones.CONTENT_DIRECTORY);
				values.clear();
				
				for(String val: contacts.values()) {
					values.put(People.Phones.TYPE, People.Phones.TYPE_MOBILE);
					values.put(People.Phones.NUMBER, val);
				}
				getContentResolver().insert(phoneUri, values);
				
			}
		}		
	}
	

	final Runnable mUpdateResults = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateUI();
		}

	};

	private void getFromCSVThread() {
		// TODO Auto-generated method stub
		showDialog(DIALOG_PLEASE_WAIT);
		Thread t = new Thread() {
			public void run() {

				mContactsList = mFileHelper.processCsvFile();
				mHandler.post(mUpdateResults);

			}
		};
		t.start();
	}

	private void updateUI() {
		setListAdapter(new CheckedList(this, mContactsList,	R.layout.check_box_list));

		
		dismissDialog(DIALOG_PLEASE_WAIT);
	}

}
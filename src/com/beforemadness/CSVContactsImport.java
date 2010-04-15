package com.beforemadness;

import java.util.ArrayList;
import java.util.Hashtable;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts.People;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CSVContactsImport extends ListActivity {
	
	/** Called when the activity is first created. */
	private final int DIALOG_FILE_SELECT = 1;
	private final int DIALOG_PLEASE_WAIT = 2;
	private final int DIALOG_OK = 3;
	private final int DIALOG_RESULT = 4;
	private final int DIALOG_EXPORTED = 5;
	final Handler mHandler = new Handler();
	private FileHelper mFileHelper = new FileHelper(this);
	private boolean mChecked = false;
	private int mImported = 0;
	private int mExported = 0;
	private int mError = 0;
	private MenuItem mSelectedMenuItem = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//showDialog(DIALOG_FILE_SELECT);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		
	}

	@Override
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
							setmenuItem();
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
		
		case DIALOG_RESULT:
			CharSequence k = mImported+" Contacts Imported."+mError+" Errors";
			Builder builder11 = new AlertDialog.Builder(this);
			builder11.setMessage(k).setCancelable(
					false).setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CSVContactsImport.this.finish();
						}
					});
			return builder11.create();	
			
		case DIALOG_EXPORTED:
			CharSequence k1 = mExported +" Contacts Exported."+" The contacts are saved in the SDCARD.";
			Builder builder111 = new AlertDialog.Builder(this);
			builder111.setMessage(k1).setCancelable(
					false).setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CSVContactsImport.this.finish();
						}
					});
			return builder111.create();	
			
		default:
			break;
		}

		return null;
	}
	
	public void setmenuItem() {
		mSelectedMenuItem.setTitle(R.string.add_contacts);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_select:
			selectAll();
			break;
		
		case R.id.menu_import:
			if (mSelectedMenuItem != null && mSelectedMenuItem.getTitle() != this.getString(R.string.add_contacts)) {
				mSelectedMenuItem.setTitle(this.getString(R.string.menu_export));
			}			
			mSelectedMenuItem = item;
			if (item.getTitle().equals(this.getString(R.string.menu_import))) {
				showDialog(DIALOG_FILE_SELECT);
			} else {
				showDialog(DIALOG_PLEASE_WAIT);
				startImportOfContacts();
				item.setTitle(this.getString(R.string.menu_import));
			}
			
			break;
			
		case R.id.menu_export:
			if (mSelectedMenuItem != null && mSelectedMenuItem.getTitle() != this.getString(R.string.export_out)) {
				mSelectedMenuItem.setTitle(this.getString(R.string.menu_import));
			}
			mSelectedMenuItem = item;
			
			if (item.getTitle().equals(this.getString(R.string.menu_export))) {
				listContacts();
				item.setTitle(this.getString(R.string.export_out));
			} else {
				showDialog(DIALOG_PLEASE_WAIT);
				startExportThread();
			}
			
			break;
			
		default:
			break;
		}
		return false;

	}
	
	private void startExportThread() {
		// TODO Auto-generated method stub
		Thread t = new Thread() {
			public void run() {
				exportContacts();
				mHandler.post(mUpdateExport);				
			}

		};
		
		t.start();
		
	}

	
	
	/**
	 * Check/Uncheck All the contacts.
	 */
	private void selectAll() {
		// TODO Auto-generated method stub
		if(!mChecked) {
			mChecked = true;
		} else {
			mChecked = false;
		}
    	ListView lst = getListView();
    	
    	for (int i = 0; i < lst.getCount(); i++) {
    		lst.setItemChecked(i, mChecked);
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

	final Runnable mUpdateExport = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateSuccess();
		}

		private void updateSuccess() {
			dismissDialog(DIALOG_PLEASE_WAIT);
			showDialog(DIALOG_EXPORTED);
		}
	};
	
	
	final Runnable mUpdateImport = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateSuccess();
		}

		private void updateSuccess() {
			dismissDialog(DIALOG_PLEASE_WAIT);
			showDialog(DIALOG_RESULT);
		}
	};
	
	/**
	 * Update the UI with the currentlist of contacts
	 */
	private void updateUI() {
		CheckedList adapter = new CheckedList(this, mFileHelper.getContactsList(),	android.R.layout.simple_list_item_multiple_choice);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		dismissDialog(DIALOG_PLEASE_WAIT);
	}	

	private void exportContacts() {
		// TODO Auto-generated method stub
		SparseBooleanArray list = getListView().getCheckedItemPositions();
		ListAdapter v = getListAdapter();
		for (int i = 0; i < list.size(); i++) {
			CursorWrapper wrapper = (CursorWrapper) v.getItem(i);
			
			String line = "";			
			String name = wrapper.getString(wrapper.getColumnIndex(People.NAME)).trim();
			String number = wrapper.getString(wrapper.getColumnIndex(People.NUMBER)).trim();
			String email = wrapper.getString(wrapper.getColumnIndex(People.PRIMARY_EMAIL_ID)).trim();
			
			line = "\""+name+"\""+","+"\""+number+"\""+","+"\""+email+"\"";
			if (line.endsWith(",")) {
				line = line.substring(0, (line.length()-1));
			}
			
			mFileHelper.writeToFile(line);
			mExported++;
		}
		
		mFileHelper.closeWrite();
		
	}

		
	private void importContacts() {
		ArrayList<Hashtable<String, String>> contactList = mFileHelper.getContactsList();
    	SparseBooleanArray list = getListView().getCheckedItemPositions();
    	for (int i = 0; i < list.size(); i++) {
				try {
					ContentValues values = new ContentValues(); 
					Hashtable<String, String> contacts = contactList.get(list.keyAt(i));
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
					mImported++;
				} catch (Exception e) {
					//Toast toast = Toast.makeText(getApplicationContext(), "Encountered a bad record ... ", Toast.LENGTH_LONG);
					//toast.show();
					mError++;
					  
				}
		}		
	}
	

	final Runnable mUpdateResults = new Runnable() {
		@Override
		public void run() {
			updateUI();
		}

	};

	private void getFromCSVThread() {
		showDialog(DIALOG_PLEASE_WAIT);
		Thread t = new Thread() {
			public void run() {

				mFileHelper.processCsvFile();
				mHandler.post(mUpdateResults);

			}
		};
		t.start();
	}
	
	public void listContacts() {
		
		String[] projection = new String[] {
				People._ID,
				People.NAME,
				People.PRIMARY_EMAIL_ID,
				People.Phones.NUMBER
		};
		
		Uri contacts = People.CONTENT_URI;
		Cursor managedCursor = managedQuery(contacts,projection,null,null,People.NAME + " ASC");
		 String[] displayFields = new String[] {People.NAME, People.NUMBER};
		int[] displayViews = new int[] { android.R.id.text1, android.R.id.text2 };

		setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, managedCursor,displayFields, displayViews));
		
	}	
	


}
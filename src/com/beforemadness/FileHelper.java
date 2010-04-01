package com.beforemadness;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

public class FileHelper {

	private File mFiles[];
	private int mSelectedFile;

	private ArrayList<Hashtable<String, String>> mContactsList = new ArrayList<Hashtable<String, String>>();
	final Handler mHandler = new Handler();

	/**
	 * Class to work with the file system and tokenize the file.
	 */
	public FileHelper() {
		mFiles = Environment.getExternalStorageDirectory().listFiles(
				new Filter());
	}

	/**
	 * Returns the CSV files in the phone
	 * 
	 * @return
	 */
	public CharSequence[] getAllFiles() {
		
		CharSequence[] filesString = new CharSequence[mFiles.length];
		for (int i = 0; i < mFiles.length; i++) {
			filesString[i] = mFiles[i].getName();
		}

		return filesString;
	}

	/**
	 * Process a CSV file to tokenize and return an arraylist of tokeized
	 * contacts
	 * 
	 * @param fileIndex
	 * @return ArrayList of Contacts.
	 */
	protected ArrayList<Hashtable<String, String>> processCsvFile() {
		try {
			FileInputStream stream = new FileInputStream(
					mFiles[getSelectedFileIndex()]);
			DataInputStream dis = new DataInputStream(new BufferedInputStream(
					stream));
			String record;
			while ((record = dis.readLine()) != null) {
				mContactsList.add(tokenize(record));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(CSVContactsImport.getApplicationContext(),
			// "exception on:" + e.toString(), Toast.LENGTH_LONG).show();
		}

		return mContactsList;
	}

	/**
	 * Tokenize each line, find if its a phone number, a email address or a
	 * name.
	 * 
	 * @param csvLine
	 * @return A Hashtable of Contacts info.
	 */
	private Hashtable<String, String> tokenize(String csvLine) {

		// StringTokenizer token = new StringTokenizer(csvLine, ",");
		String[] token = csvLine.split(",");
		Hashtable<String, String> contact = new Hashtable<String, String>();
		int numberCount = 0;
		int nameCount = 0;
		if (token.length > 0) {
			for (int i = 0; i < token.length; i++) {
				// Check if the first record is all alphabet then assume its a
				// name
				boolean found = false;
				String item = token[i].trim();
				item = item.replaceAll("\"", "");
				if (!item.equals("")) {
					try {
						Double.parseDouble(item);
						// If no exception then this has to be a phone number
						numberCount++;
						contact.put("number" + numberCount, item);
						found = true;
					} catch (Exception e) {

					}

					// match for email address
					if (item.matches(".+@.+\\.[a-z]+") && !found) {
						// valid email address
						contact.put("email", item);
						// match for other string types
					} else if (!found) {
						// item is a string which can be first name, last name
						// or address
						if (!contact.containsKey("firstname")) {
							contact.put("firstname", item);
						} else if (!contact.containsKey("lastname")) {
							contact.put("lastname", item);
						} else if (!contact.containsKey("address")) {
							contact.put("address", item);
						} else {
							contact.put("unknown", item);
						}
					}
				}

			}

		}

		return contact;
	}

	public void setSelectedFileIndex(int mSelectedFile) {
		this.mSelectedFile = mSelectedFile;
	}

	public int getSelectedFileIndex() {
		return mSelectedFile;
	}

}

/**
 * Class to create filtering of csv files.
 * 
 * @author shyam
 * 
 */
class Filter implements FileFilter {
	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		return file.getName().endsWith("csv");
	}

}
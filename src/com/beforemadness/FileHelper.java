package com.beforemadness;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

public class FileHelper {

	private File mFiles[];
	private int mSelectedFile;
	private ArrayList<Hashtable<String, String>> mContactsList = null;
	private int mReadErrors = 0;
	private String mOutFile = "android_contacts_export.csv";
	private PrintWriter mOutFileHandle = null;
	private Context	mContext;

	public int getReadErrors() {
		return mReadErrors;
	}
	
	public int addError() {
		return mReadErrors++;
	}

	final Handler mHandler = new Handler();

	/**
	 * Class to work with the file system and tokenize the file.
	 */
	public FileHelper(Context context) {
		mFiles = Environment.getExternalStorageDirectory().listFiles(
				new Filter());
		
		this.mContext = context;
		
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
	
	public void writeToFile(String line) {

		if (mOutFileHandle == null) {
			try {
				    File root = Environment.getExternalStorageDirectory();
				    if (root.canWrite()){
				        File gpxfile = new File(root, mOutFile);
				        FileWriter gpxwriter = new FileWriter(gpxfile);
				         mOutFileHandle = new PrintWriter(gpxwriter);
				    }

				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mOutFileHandle.println(line);
		
		
	}
	
	public void closeWrite() {
		mOutFileHandle.flush();
		mOutFileHandle.close();
		mOutFileHandle = null;
	}
	

	/**
	 * Process a CSV file to tokenize and return an arraylist of tokeized
	 * contacts
	 * 
	 * @param fileIndex
	 * @return ArrayList of Contacts.
	 */
	protected void processCsvFile() {
		mContactsList = new ArrayList<Hashtable<String, String>>();
		try {
			FileInputStream stream = new FileInputStream(
					mFiles[getSelectedFileIndex()]);
			DataInputStream dis = new DataInputStream(new BufferedInputStream(
					stream));
			String record;
			while ((record = dis.readLine()) != null) {
				Hashtable<String, String> token = tokenize(record);
				if (token.containsKey("firstname")) {
					mContactsList.add(token);
				} else {
					mReadErrors++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(CSVContactsImport.getApplicationContext(),
			// "exception on:" + e.toString(), Toast.LENGTH_LONG).show();
			mReadErrors++;
		}

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
	
	public ArrayList<Hashtable<String, String>> getContactsList() {
		return mContactsList;
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
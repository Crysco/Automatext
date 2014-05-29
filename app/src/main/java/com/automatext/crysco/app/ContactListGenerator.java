package com.automatext.crysco.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.lang.Integer;import java.lang.String;import java.util.ArrayList;

/**
 * Created by Crys on 5/12/14.
 */
public class ContactListGenerator {

    private Activity activity;
    private static ArrayList<Contact> contacts;

    private static ContactListGenerator instance = null;

    public static ContactListGenerator getInstance() {
        if(instance == null)
            instance = new ContactListGenerator();

        return instance;
    }

    protected ContactListGenerator() {
    }

    public void initializeContacts(Activity a) {
        activity = a;
        generateContacts();
    }

    private void generateContacts() {

        contacts = new ArrayList<Contact>();
        contacts.add(new Contact("New Contact", ""));

        Cursor cursor = activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur != null && pCur.moveToNext()) {
                        Contact contact = new Contact();
                        contact.setName(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        contact.setNumber(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        contacts.add(contact);
                        break;
                    }
                    pCur.close();
                }
            } while (cursor.moveToNext()) ;
        }
    }

    public static ArrayList<Contact> getContacts() {
        return contacts;
    }

    public class Contact {
        private String name;
        private String number;

        public Contact() {
            name = "";
            number = "";
        }

        public Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }
}

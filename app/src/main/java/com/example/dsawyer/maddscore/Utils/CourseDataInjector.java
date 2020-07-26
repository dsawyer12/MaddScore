package com.example.dsawyer.maddscore.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dsawyer.maddscore.Objects.Course;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseDataInjector {
    private static final String TAG = "TAG";

    Context context;
    DatabaseReference ref;

    BufferedReader titleReader, addressReader, holeReader;

    ArrayList<Course> courses;

    public interface ThreadCompleteListener {
        void notifyOfThreadComplete(int code);
    }
    ThreadCompleteListener tListener, aListener, hListener;

    public CourseDataInjector(Context context) {
        this.context = context;
        courses = new ArrayList<>();

        tListener = new ThreadCompleteListener() {
            @Override
            public void notifyOfThreadComplete(int code) {
                if (code == 1){
                    Log.d(TAG, "Title Thread complete");
                    try {
                        initAddresses();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Log.d(TAG, "Title Thread exception");
            }
        };

        aListener = new ThreadCompleteListener() {
            @Override
            public void notifyOfThreadComplete(int code) {
                if (code == 1){
                    Log.d(TAG, "Address Thread complete");
                    try {
                        initHoles();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Log.d(TAG, "Address Thread exception");
            }
        };

        hListener = new ThreadCompleteListener() {
            @Override
            public void notifyOfThreadComplete(int code) {
                if (code == 1){
                    Log.d(TAG, "Hole Thread complete");
                    initLocations();
                }
                else
                    Log.d(TAG, "Hole Thread exception");
            }
        };
    }

    public void initTitles() throws IOException {
        Log.d(TAG, "initTitles: called");
        titleReader = new BufferedReader(new InputStreamReader(context.getAssets().open("titles.txt")));
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String title;
                    Course course;
                    int count = 0;
                    while ((title = titleReader.readLine()) != null) {
                        course = new Course(title);
                        courses.add(course);
                        Log.d(TAG, String.valueOf(count));
                        count++;
                    }
                    titleReader.close();
                    tListener.notifyOfThreadComplete(1);
                }
                catch (IOException e){
                    tListener.notifyOfThreadComplete(0);
                    Log.d(TAG, e.getMessage());
                }

            }
        });
        tThread.start();
    }

    public void initAddresses() throws IOException {
        Log.d(TAG, "initAddresses: called");
        addressReader = new BufferedReader(new InputStreamReader(context.getAssets().open("addresses.txt")));
        Thread aThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String address;
                    int count = 0;
                    while ((address = addressReader.readLine()) != null) {
                        courses.get(count).setAddress(address);
                        Log.d(TAG, String.valueOf(count));
                        count++;
                    }
                    addressReader.close();
                    aListener.notifyOfThreadComplete(1);
                }
                catch (IOException e){
                    aListener.notifyOfThreadComplete(0);
                    Log.d(TAG, e.getMessage());
                }

            }
        });
        aThread.start();
    }

    public void initHoles() throws IOException {
        Log.d(TAG, "initHoles: called");
        holeReader = new BufferedReader(new InputStreamReader(context.getAssets().open("holes.txt")));
        Thread hThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String holes;
                    int count = 0;
                    while ((holes = holeReader.readLine()) != null) {
                        courses.get(count).setNumHoles(Integer.valueOf(holes.trim()));
                        Log.d(TAG, String.valueOf(count));
                        count++;
                    }
                    holeReader.close();
                    hListener.notifyOfThreadComplete(1);
                }
                catch (IOException e){
                    hListener.notifyOfThreadComplete(0);
                    Log.d(TAG, e.getMessage());
                }

            }
        });
        hThread.start();
    }

    public void initLocations() {
        Log.d(TAG, "initLocations: called");
        int count = 0;
        LatLng latLng;
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getAddress() != null) {
                latLng = getLocationFromAddress(courses.get(i).getAddress());
                if (latLng != null){
                    courses.get(i).setLatitude(latLng.latitude);
                    courses.get(i).setLongitude(latLng.longitude);
                    Log.d(TAG, String.valueOf(count));
                    count++;
                }
                else{
                    courses.get(i).setLatitude(null);
                    courses.get(i).setLongitude(null);
                    Log.d(TAG, String.valueOf(count));
                    count++;
                }
            }
            else {
                Log.d(TAG, "course: " + courses.get(i).getName() + " has a NULL address");
            }
        }
        injectDatabase();
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        }
        catch (Exception ex) {

            ex.printStackTrace();
        }

        return latLng;
    }

    public void injectDatabase() {

        ref = FirebaseDatabase.getInstance().getReference();
        String courseId;

        for (int i = 0; i < courses.size(); i++){
            courseId = ref.child("courses").push().getKey();
            courses.get(i).setCourseId(courseId);
            ref.child("courses").child(courseId).setValue(courses.get(i));
        }

    }

















//    public void injectData() {
//        String courseId;
//        int line = 1;
//
//        for (int i = 0; i < courses.size(); i++){
//            courseId = ref.child("courses").push().getKey();
//            courses.get(i).setCourseId(courseId);
//            ref.child("courses").child(courseId).setValue(courses.get(i));
//            Log.d(TAG, line + courses.get(i).getName());
//        }
//
//    }
//
//    public void displayContent(){
//        for (int i = 0; i < courses.size(); i++){
//            Log.d(TAG, courses.get(i).getName()+"\n");
//            Log.d(TAG, courses.get(i).getAddress()+"\n");
//            Log.d(TAG, courses.get(i).getNumHoles()+"\n");
//            Log.d(TAG, courses.get(i).getLatitude()+"\n");
//            Log.d(TAG, courses.get(i).getLongitude()+"\n");
//        }
//    }
//
//    public void initData(){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    int line = 1;
//                    courses = new ArrayList<>();
//                    reader = new BufferedReader(new InputStreamReader(context.getAssets().open("temp")));
//                    BufferedWriter writer = new BufferedWriter(
//                            new FileWriter(String.valueOf(context.getAssets().open("output")), true));
//
//                    latLngs = new HashMap<>();
//                    titles = new HashMap<>();
//                    addresses = new HashMap<>();
//                    holes = new HashMap<>();
//
//                    int titleLine = 0, addressLine = 0, holeLine = 0, latlng = 0;
//
//                    while((address = reader.getLine()) != null){
//                        if (address.contains(TITLE)){
//                            newTitle = address.replace(TITLE, "");
//                            titles.put(titleLine, newTitle);
//                            titleLine++;
//                            Course course = new Course(newTitle);
//                            courses.add(course);
//                            try {
//                                writer.append(course.toString());
//                            }
//                            catch(IOException e){
//                                Log.d(TAG, e.getMessage());
//                            }
//                            Log.d(TAG, String.valueOf(line));
//                            line++;
//                        }
////                        if (address.contains(ADDRESS)){
////                            newAddress = address.replace(ADDRESS, "");
////                            addresses.put(addressLine, newAddress);
////                            ll = getLocationFromAddress(context, newAddress);
////                            latLngs.put(addressLine, ll);
////                            addressLine++;
////                            latlng++;
////                        }
////                        if (address.contains(HOLES)){
////                            newHole = address.replace(HOLES, "");
////                            holes.put(holeLine, Integer.valueOf(newHole));
////                            holeLine++;
////                        }
//
//                    }
//                    reader.close();
//                    writer.close();
//
////                    int line2 = 1;
////                    for (Integer key : titles.keySet()){
////                        courses.add(new Course(titles.get(key), key));
////                        Log.d(TAG, "COURSE " + line2 + " : " + titles.get(key));
////                        line2++;
////                    }
////
////                    int line3 = 1;
////                    for (Integer key : addresses.keySet()){
////                        for (int i = 0; i < courses.size(); i++){
////                            if (courses.get(i).getLine() == key){
////                                courses.get(i).setAddress(addresses.get(key));
////                                Log.d(TAG, "ADDRESS " + line3 + " : " + addresses.get(key));
////                                line3++;
////                            }
////                        }
////                    }
////
////                    int line4 = 1;
////                    for (Integer key : latLngs.keySet()){
////                        for (int i = 0; i < courses.size(); i++){
////                            if (courses.get(i).getLine() == key){
////                                courses.get(i).setLatitude(latLngs.get(key).latitude);
////                                courses.get(i).setLongitude(latLngs.get(key).longitude);
////                                Log.d(TAG, "LatLong " + line4 + " : " + latLngs.get(key));
////                            }
////                        }
////                    }
////
////                    int line5 = 1;
////                    for (Integer key : holes.keySet()){
////                        for (int i = 0; i < courses.size(); i++){
////                            if (courses.get(i).getLine() == key){
////                                courses.get(i).setNumHoles(holes.get(key));
////                                Log.d(TAG, "HOLES " + line5 + " : " + holes.get(key));
////                            }
////                        }
////                    }
//
////                    listener.notifyOfThreadComplete(1);
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                    listener.notifyOfThreadComplete(0);
//                }
//            }
//        });
//        thread.start();
//    }
//
//    public LatLng getLocationFromAddress(Context context, String strAddress) {
//
//        Geocoder coder = new Geocoder(context);
//        List<Address> address;
//        LatLng p1 = null;
//
//        try {
//            address = coder.getFromLocationName(strAddress, 1);
//            if (address == null) {
//                return null;
//            }
//            Address location = address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
//
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//        }
//
//        return p1;
//    }
//

}

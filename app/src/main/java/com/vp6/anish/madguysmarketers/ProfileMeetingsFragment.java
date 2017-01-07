package com.vp6.anish.madguysmarketers;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileMeetingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileMeetingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileMeetingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<String> lng;
    ArrayList<String> lat;
    ArrayList<String> description;
    ArrayList<ArrayList> imageurl;
    ArrayList<ArrayList>meetings;
    ArrayList<String>creator;
    ArrayList<String>created;
    ArrayList<String>id;
    RecyclerView recyclerView;
    MeetingAdapter meetingAdapter;
    public  Context context;
    private static final int ACCESS_FINE_LOCATION = 2;
    public ProfileMeetingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileMeetingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileMeetingsFragment newInstance(String param1, String param2) {
        ProfileMeetingsFragment fragment = new ProfileMeetingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_meetings, container, false);
        description = new ArrayList<>();
        id = new ArrayList<>();
        created= new ArrayList<>();
        creator= new ArrayList<>();
        meetings = new ArrayList<>();
        imageurl= new ArrayList<>();
        lat= new ArrayList<>();
        lng= new ArrayList<>();

        if(mListener != null){

            meetings= mListener.getMeetings();
            if(meetings != null){
                if(meetings.size() > 0) {
                    description = meetings.get(0);
                    imageurl = meetings.get(1);
                    lat = meetings.get(2);
                    lng = meetings.get(3);
                    creator = meetings.get(4);
                    created = meetings.get(5);
                    id = meetings.get(6);

                }

            }
        }
        meetingAdapter = new MeetingAdapter(this,description,lat,lng,imageurl,id,creator,created);
        recyclerView = (RecyclerView)view.findViewById(R.id.profile_meetings);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(meetingAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        return  view;
    }

    @Override
    public void onResume(){
        super.onResume();
        description = new ArrayList<>();
        id = new ArrayList<>();
        created= new ArrayList<>();
        creator= new ArrayList<>();
        meetings = new ArrayList<>();
        imageurl= new ArrayList<>();
        lat= new ArrayList<>();
        lng= new ArrayList<>();

        if(mListener != null){

            meetings= mListener.getMeetings();
            if(meetings != null){
                if(meetings.size() > 0) {
                    description = meetings.get(0);
                    imageurl = meetings.get(1);
                    lat = meetings.get(2);
                    lng = meetings.get(3);
                    created = meetings.get(5);
                    creator = meetings.get(4);
                    id = meetings.get(6);

                }

            }
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.getMeetings();
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        ArrayList<ArrayList> getMeetings();
    }


    public void showmeetinglocation(String Lat, String Lng) {
        if(!Lat.equals("") && !Lng.equals("")){
            if (Build.VERSION.SDK_INT >= 23) {
                getPermissionToAccessFineLocation();
            }
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (gps) {
                    Intent intent = new Intent(context, LocationDisplayActivity.class);
                    intent.putExtra("lat", Lat);
                    intent.putExtra("lng", Lng);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Turn on the GPS first", Toast.LENGTH_LONG).show();
                }
            }}
        else
        {

            Toast.makeText(context, "Location was not marked at time of meeting.", Toast.LENGTH_LONG).show();

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToAccessFineLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
                Toast.makeText(context, "Accessing Location is required to open and work with Maps. You can always turn it off/on in Settings > Application > MadGuys Marketers", Toast.LENGTH_LONG).show();

            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION);
        }

    }


    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request

        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Access Location Permission granted", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(context, "Access Location permission denied. It is required to get your exact location", Toast.LENGTH_SHORT).show();
            }
        }


    }


}

package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
 * {@link AllListingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllListingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;
    public ArrayList<ArrayList> mData;
    public ArrayList<String> mData_name;
    public ArrayList<String> mData_type;
    public ArrayList<String> mData_id;
    public ArrayList<String> mData_phone_number;
    public ArrayList<String> mData_status;
    public ArrayList<String> mData_address;
    public ArrayList<String> mData_lat;
    public ArrayList<String> mData_lng;
    public ArrayList<String> mData_imageurl;
    private FloatingActionButton fab_map_all;

    public OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    ListingAdapter listingAdapter;
    Context mcontext;

    public AllListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllListingFragment newInstance(String param1, String param2) {
        AllListingFragment fragment = new AllListingFragment();
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

        mcontext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mData = new ArrayList<>();
        mData_name = new ArrayList<>();
        mData_type = new ArrayList<>();
        mData_id = new ArrayList<>();
        mData_imageurl = new ArrayList<>();
        mData_lat = new ArrayList<>();
        mData_lng = new ArrayList<>();
        mData_address = new ArrayList<>();
        mData_phone_number = new ArrayList<>();
        mData_status = new ArrayList<>();

        View v =  inflater.inflate(R.layout.fragment_all_listing, container, false);
        if(mListener != null) {
            mData = mListener.alldatalist();
            if (mData != null) {
                if (mData.size() > 0) {
                    mData_name = mData.get(0);
                    mData_type = mData.get(1);
                    mData_id = mData.get(2);
                    mData_imageurl = mData.get(3);
                    mData_status = mData.get(4);
                    mData_address = mData.get(5);
                    mData_phone_number = mData.get(6);
                    mData_lat = mData.get(7);
                    mData_lng = mData.get(8);

                }
            }
        }
        fab_map_all = (FloatingActionButton)v.findViewById(R.id.fab_map_all);
        fab_map_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3  = new Intent(mcontext, LocationDisplayActivity.class);
                intent3.putStringArrayListExtra("lat_list", mData_lat);
                intent3.putStringArrayListExtra("lng_list", mData_lng);
                intent3.putStringArrayListExtra("status_list", mData_status);
                intent3.putStringArrayListExtra("type_list", mData_type);
                intent3.putStringArrayListExtra("name_list", mData_name);
                intent3.putStringArrayListExtra("address_list", mData_address);
                intent3.putStringArrayListExtra("id_list", mData_id);

                startActivity(intent3);
            }
        });
        recyclerView = (RecyclerView) v.findViewById(R.id.listView2);
        listingAdapter = new ListingAdapter(mcontext,mData_imageurl,mData_name,mData_type,mData_id,mData_phone_number,mData_status,mData_address, AllListingFragment.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( mcontext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listingAdapter);

        return v;
    }

    public void click(String id, String type){
        if(type.equals("cafe")) {
            Intent intent = new Intent(mcontext, CafeDisplayActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else if(type.equals("coaching")){
            Intent intent = new Intent(mcontext, CoachingDisplayActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);

        }
        else
        {
            Toast.makeText(mcontext,"failed, try later", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
          //  mListener.alldatalist();
        }
    }

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
    @Override
    public void onResume() {
        super.onResume();
        mData = new ArrayList<>();
        mData_name = new ArrayList<>();
        mData_type = new ArrayList<>();
        mData_id = new ArrayList<>();
        mData_imageurl = new ArrayList<>();
        mData_lat = new ArrayList<>();
        mData_lng = new ArrayList<>();
        mData_address = new ArrayList<>();
        mData_phone_number = new ArrayList<>();
        mData_status = new ArrayList<>();

        if(mListener != null){
            mData = mListener.alldatalist();
            if(mData != null){
                if(mData.size() > 0) {
                    mData_name = mData.get(0);
                    mData_type = mData.get(1);
                    mData_id = mData.get(2);
                    mData_imageurl = mData.get(3);
                    mData_status = mData.get(4);
                    mData_address = mData.get(5);
                    mData_phone_number = mData.get(6);
                    mData_lat = mData.get(7);
                    mData_lng = mData.get(8);
                }
            }
        }
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
//        void onFragmentInteraction(Uri uri);
        ArrayList<ArrayList> alldatalist();
    }
}

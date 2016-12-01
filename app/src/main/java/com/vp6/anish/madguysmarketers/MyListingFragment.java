package com.vp6.anish.madguysmarketers;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyListingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyListingFragment extends Fragment implements  View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private TextView fab1text, fab2text;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public ArrayList<ArrayList> mData;
    public ArrayList<String> mData_name;
    public ArrayList<String> mData_type;
    public ArrayList<String> mData_id;
    public ArrayList<String> mData_imageurl;
    public OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    ListingAdapter listingAdapter;
    Context mcontext;

    public MyListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListingFragment newInstance(String param1, String param2) {
        MyListingFragment fragment = new MyListingFragment();
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
        mData_name = new ArrayList<>();
        mData_type = new ArrayList<>();
        mData_id = new ArrayList<>();
        mData_imageurl = new ArrayList<>();
        mData = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_my_listing, container, false);
        if(mListener != null){

                mData = mListener.getData();
            if(mData != null){
            if(mData.size() > 0) {
                mData_name = mData.get(0);
                mData_type = mData.get(1);
                mData_id = mData.get(2);
                mData_imageurl = mData.get(3);
            }

            }
        }
        recyclerView = (RecyclerView) v.findViewById(R.id.listView1);
        listingAdapter = new ListingAdapter(mcontext,mData_imageurl,mData_name,mData_type,mData_id, MyListingFragment.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mcontext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listingAdapter);


        //////////floating button

        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab1 = (FloatingActionButton)v.findViewById(R.id.fab1);
        fab1text =(TextView) v.findViewById(R.id.fab1text);
        fab2 = (FloatingActionButton)v.findViewById(R.id.fab2);
        fab2text =(TextView)v.findViewById(R.id.fab2text);
        fab_open = AnimationUtils.loadAnimation(mcontext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mcontext,R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(mcontext,R.anim.record_forward);
        rotate_backward = AnimationUtils.loadAnimation(mcontext,R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

       //// getPermissionToReadCallLogs();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
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


    @Override
    public void onResume() {
        super.onResume();
        mData = new ArrayList<>();
        mData_name = new ArrayList<>();
        mData_type = new ArrayList<>();
        mData_id = new ArrayList<>();
        mData_imageurl = new ArrayList<>();
        if(mListener != null){
            mData = mListener.getData();
            if(mData != null){
            if(mData.size() > 0) {
                mData_name = mData.get(0);
                mData_type = mData.get(1);
                mData_id = mData.get(2);
                mData_imageurl = mData.get(3);
            }
            }
        }
    }

    public void click(String id, String type){
        if(type.equals("cafe")){
        Intent intent = new Intent(mcontext,CafeDisplayActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);}
        else if (type.equals("coaching"))
        {
            Intent intent = new Intent(mcontext,CoachingDisplayActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(mcontext,"failed, try later", Toast.LENGTH_SHORT).show();
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1text.startAnimation(fab_close);
            fab2text.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
          //  Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1text.startAnimation(fab_open);
            fab2text.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);

            isFabOpen = true;
        //    Log.d("Raj","open");

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:
                Intent intent1 = new Intent(mcontext,AddCoachingActivity.class);
                startActivity(intent1);
              break;
            case R.id.fab2:
                Intent intent = new Intent(mcontext,AddCafeActivity.class);
                startActivity(intent);

                break;
        }
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //void onFragmentInteraction(Uri uri);
        ArrayList<ArrayList> getData();
    }
}

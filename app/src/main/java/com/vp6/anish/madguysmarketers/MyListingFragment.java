package com.vp6.anish.madguysmarketers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
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
public class MyListingFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab_map;
    private TextView fab1text, fab2text, fab_map_text;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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
    public ArrayList<ListItem> allData;
    private FloatingActionButton fab_sort_all;

    public OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    ListingAdapter listingAdapter;
    Context mcontext;
    String searched="";
    String typeofsort;
    String typeoforder;



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
        mData_lat = new ArrayList<>();
        mData_lng = new ArrayList<>();
        mData_address = new ArrayList<>();
        mData_phone_number = new ArrayList<>();
        mData_status = new ArrayList<>();
        allData = new ArrayList<>();
        mData = new ArrayList<>();

        View v = inflater.inflate(R.layout.fragment_my_listing, container, false);
        if (mListener != null) {

            mData = mListener.getData();
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
        recyclerView = (RecyclerView) v.findViewById(R.id.listView1);

        //////////floating button

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) v.findViewById(R.id.fab1);
        fab1text = (TextView) v.findViewById(R.id.fab1text);
        fab2 = (FloatingActionButton) v.findViewById(R.id.fab2);
        fab2text = (TextView) v.findViewById(R.id.fab2text);
        fab_map = (FloatingActionButton) v.findViewById(R.id.fab_map);
        fab_map_text = (TextView) v.findViewById(R.id.fab_map_text);

        fab_open = AnimationUtils.loadAnimation(mcontext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mcontext, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(mcontext, R.anim.record_forward);
        rotate_backward = AnimationUtils.loadAnimation(mcontext, R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab_map.setOnClickListener(this);

        //// getPermissionToReadCallLogs();

        fab_sort_all = (FloatingActionButton)v.findViewById(R.id.fab_sort_my);
        fab_sort_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose sorting type");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mcontext, android.R.layout.select_dialog_item);
                arrayAdapter.add("Name");
                arrayAdapter.add("Type");
                arrayAdapter.add("Status");

                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        typeofsort = arrayAdapter.getItem(i);
                        final int j = type(typeofsort);
                        dialogInterface.dismiss();
                        dialogInterface.cancel();

                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Choose order");

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mcontext, android.R.layout.select_dialog_item);
                        arrayAdapter.add("Ascending");
                        arrayAdapter.add("Descending");

                        builder1.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                typeoforder = arrayAdapter.getItem(i);
                                int k = type(typeoforder);
                                if( j == 201){
                                    if(k == 301){
                                        listingAdapter.sortlistnameasc();
                                        filter_results(searched);
                                    }else if( k == 302){
                                        listingAdapter.sortlistnamedes();
                                        filter_results(searched);
                                    }else{
                                        dialogInterface.dismiss();
                                        dialogInterface.cancel();
                                    }
                                }else if(j == 202){
                                    if(k == 301){
                                        listingAdapter.sortlisttypeasc();
                                        filter_results(searched);
                                    }else if( k == 302){
                                        listingAdapter.sortlisttypedes();
                                        filter_results(searched);
                                    }else{
                                        dialogInterface.dismiss();
                                        dialogInterface.cancel();
                                    }
                                }else if(j == 203){
                                    if(k == 301){
                                        listingAdapter.sortliststatusasc();
                                        filter_results(searched);
                                    }else if( k == 302){
                                        listingAdapter.sortliststatusdes();
                                        filter_results(searched);
                                    }else{
                                        dialogInterface.dismiss();
                                        dialogInterface.cancel();
                                    }
                                }else{
                                    dialogInterface.dismiss();
                                    dialogInterface.cancel();
                                }
                                dialogInterface.dismiss();
                                dialogInterface.cancel();
                            }
                        });
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder1.show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

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

    public void filter_results(String Query){
        searched = Query;
        listingAdapter.getFilter().filter(Query);
    }

    public int type(String str) {
        switch (str) {
            case "Name":
                return 201;
            case "Type":
                return 202;
            case "Status":
                return 203;
            case "Ascending":
                return 301;
            case "Descending":
                return 302;
            default:
                return 0;

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mData_name = new ArrayList<>();
        mData_type = new ArrayList<>();
        mData_id = new ArrayList<>();
        mData_imageurl = new ArrayList<>();
        mData_lat = new ArrayList<>();
        mData_lng = new ArrayList<>();
        mData_address = new ArrayList<>();
        mData_phone_number = new ArrayList<>();
        mData_status = new ArrayList<>();
        mData = new ArrayList<>();
        if (mListener != null) {
            mData = mListener.getData();
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
                    for (int i = 0; i < mData_id.size(); i++) {
                        ListItem item = new ListItem();
                        item.setData(mData_name.get(i), mData_type.get(i), mData_id.get(i), mData_imageurl.get(i), mData_lat.get(i), mData_lng.get(i), mData_address.get(i), mData_phone_number.get(i), mData_status.get(i));
                        allData.add(item);
                    }
                }

            }
        }

        //listingAdapter = new ListingAdapter(mcontext, mData_imageurl, mData_name, mData_type, mData_id, mData_phone_number, mData_status, mData_address, MyListingFragment.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mcontext);
        recyclerView.setLayoutManager(mLayoutManager);
        Log.i("MyListing ", "On Resume");
        listingAdapter = new ListingAdapter(mcontext, MyListingFragment.this, allData,recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listingAdapter);
    }

    public void click(String id, String type) {
        if (type.equals("cafe")) {
            Intent intent = new Intent(mcontext, CafeDisplayActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (type.equals("coaching")) {
            Intent intent = new Intent(mcontext, CoachingDisplayActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else {
            Toast.makeText(mcontext, "failed, try later", Toast.LENGTH_SHORT).show();
        }
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab_map.startAnimation(fab_close);
            fab1text.startAnimation(fab_close);
            fab2text.startAnimation(fab_close);
            fab_map_text.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab_map.setClickable(false);
            isFabOpen = false;
            //  Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab_map.startAnimation(fab_open);
            fab1text.startAnimation(fab_open);
            fab2text.startAnimation(fab_open);
            fab_map_text.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab_map.setClickable(true);
            isFabOpen = true;
            //    Log.d("Raj","open");

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:
                Intent intent1 = new Intent(mcontext, AddCoachingActivity.class);
                startActivity(intent1);
                break;
            case R.id.fab2:
                Intent intent = new Intent(mcontext, AddCafeActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_map:
                Intent intent3 = new Intent(mcontext, LocationDisplayActivity.class);
                intent3.putStringArrayListExtra("lat_list", mData_lat);
                intent3.putStringArrayListExtra("lng_list", mData_lng);
                intent3.putStringArrayListExtra("status_list", mData_status);
                intent3.putStringArrayListExtra("type_list", mData_type);
                intent3.putStringArrayListExtra("name_list", mData_name);
                intent3.putStringArrayListExtra("address_list", mData_address);
                intent3.putStringArrayListExtra("id_list", mData_id);
                startActivity(intent3);

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

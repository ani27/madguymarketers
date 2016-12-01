package com.vp6.anish.madguysmarketers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileCallLogsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileCallLogsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileCallLogsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    ArrayList<String> number;
    ArrayList<String> time;
    ArrayList<String> duration;
    ArrayList<String> type;
    ArrayList<ArrayList>callLogs;
    RecyclerView recyclerView;
    CallLogsAdapter callLogsAdapter;
    Context context;
    public ProfileCallLogsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileCallLogsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileCallLogsFragment newInstance(String param1, String param2) {
        ProfileCallLogsFragment fragment = new ProfileCallLogsFragment();
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
        number = new ArrayList<>();
        type = new ArrayList<>();
        duration= new ArrayList<>();
        time= new ArrayList<>();
        callLogs = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_profile_call_logs, container, false);
        if(mListener != null){

            callLogs = mListener.getCallLogs();
            if(callLogs != null){
                if(callLogs.size() > 0) {
                    number = callLogs.get(0);
                    type = callLogs.get(3);
                    time= callLogs.get(1);
                    duration = callLogs.get(2);
                }

            }
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.profile_calllogs);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        callLogsAdapter = new CallLogsAdapter(number, time, duration, type, recyclerView);
        recyclerView.setAdapter(callLogsAdapter);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        number = new ArrayList<>();
        type = new ArrayList<>();
        duration= new ArrayList<>();
        time= new ArrayList<>();
        callLogs = new ArrayList<>();

        if(mListener != null){

            callLogs = mListener.getCallLogs();
            if(callLogs != null){
                if(callLogs.size() > 0) {
                    number = callLogs.get(0);
                    type = callLogs.get(3);
                    time= callLogs.get(1);
                    duration = callLogs.get(2);
                }

            }
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.getCallLogs();
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
        ArrayList<ArrayList>getCallLogs();
    }
}

package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HREmployeeRequests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HREmployeeRequests extends Fragment {

    private RecyclerView recyclerView;
    private RequestRecyclerViewAdapter recyclerviewAdapter;
    private List<RequestItem> requestItemList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HREmployeeRequests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HREmployeeRequests.
     */
    // TODO: Rename and change types and number of parameters
    public static HREmployeeRequests newInstance(String param1, String param2) {
        HREmployeeRequests fragment = new HREmployeeRequests();
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
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_r_employee_requests, container, false);


        //recyclerView = view.findViewById(R.id.recent_activity_recyclerview);
        recyclerView = view.findViewById(R.id.request_form);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize your list of NotificationItems
        requestItemList = new ArrayList<>();
        // Add sample data, replace this with your actual data
        requestItemList.add(new RequestItem("12/15/2023", R.drawable.user, "Dela Cruz, Juan", "Employee","Leave Form"));
        requestItemList.add(new RequestItem("12/17/2023", R.drawable.user, "Pedro Batumbakal", "Employee","Pass Slip"));


        recyclerviewAdapter = new RequestRecyclerViewAdapter(requestItemList, getActivity());
        recyclerView.setAdapter(recyclerviewAdapter);

        return view;
    }
}
package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Request Forms")
                    .whereEqualTo("request_status", "Pending")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            requestItemList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String date = document.getString("transaction_date");
                                String request_type = document.getString("requestType");
                                String status = document.getString("request_status");
                                String employee_firstname = document.getString("first_name");
                                String employee_lastname = document.getString("last_name");
                                String position = document.getString("user_level");

                                String fullName = employee_firstname + " " + employee_lastname;

                                requestItemList.add(new RequestItem(date,R.drawable.user,fullName,position, request_type));
                            }
                            recyclerView = view.findViewById(R.id.request_form);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            recyclerviewAdapter = new RequestRecyclerViewAdapter(requestItemList, getActivity());
                            recyclerView.setAdapter(recyclerviewAdapter);

                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        }

        return view;
    }
}
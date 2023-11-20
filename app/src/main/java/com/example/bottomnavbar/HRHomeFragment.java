package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HRHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HRHomeFragment extends Fragment {

    private TextView usernameText;
    private TextView total_applicantsText;
    private TextView total_employeesText;
    private RecyclerView recyclerView;
    private RecyclerviewAdapter recyclerviewAdapter;
    private List<EmployeeItem> employeeItemList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HRHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HRHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HRHomeFragment newInstance(String param1, String param2) {
        HRHomeFragment fragment = new HRHomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_h_r_home, container, false);

        usernameText = view.findViewById(R.id.HRUsernameTxt);
        total_applicantsText = view.findViewById(R.id.applicant_number);
        total_employeesText = view.findViewById(R.id.Employeenumber);


        // Assuming user is authenticated and UID is available
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Log.d("UID", "User UID: " + uid);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //Counter for number of applicants
            db.collection("Applicant Information")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {


                            int numberOfApplicants = task.getResult().size();

                            total_applicantsText.setText(String.valueOf(numberOfApplicants));

                            Log.d("ApplicantCount", "Number of applicants: " + numberOfApplicants);
                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
            //Counter for number of employees
            db.collection("Employee Information")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int numberOfEmployees = task.getResult().size();
                            total_employeesText.setText(String.valueOf(numberOfEmployees));
                            Log.d("ApplicantCount", "Number of applicants: " + numberOfEmployees);
                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    });


            // Use a query to find the document based on the user ID
            db.collection("User Account")
                    .whereEqualTo("userID", uid) // Assuming there's a field "UserID" in your documents
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            showToast("Document ID: " + documentId);
                            Log.d("DID", "Document DID: " + documentId);
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Retrieve the username from the nested field "Account_Information"
                            Map<String, Object> accountInfo = (Map<String, Object>) documentSnapshot.get("Account_Information");
                            if (accountInfo != null) {
                                String username = (String) accountInfo.get("Username");


                                usernameText.setText(username);
                                Log.d("Username", "Retrieved username: " + username);


                                showToast("Username loaded successfully");
                            } else {

                                showToast("No username found in Account Information");
                            }
                        } else {

                            showToast("No document found for the user ID: " + uid);
                        }
                    })
                    .addOnFailureListener(e -> {

                        showToast("Failed to load username. Please try again.");
                    });
        } else {
            // Handle case where user is not authenticated
            showToast("User not authenticated");
        }



        Button seeAllButton = view.findViewById(R.id.AllBtn);
        seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the HRHistory fragment
                replaceFragment(new HREmployeeRequests());
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize your list of NotificationItems
        employeeItemList = new ArrayList<>();
        // Add sample data, replace this with your actual data
        employeeItemList.add(new EmployeeItem("12/15/2023", R.drawable.user, "Dela Cruz, Juan", "Employee", "Applied for Sick Leave", "To be Approved"));
        // Add more items as needed
        employeeItemList.add(new EmployeeItem("11/12/2023", R.drawable.user, "Pedro Batumbakal", "Employee", "Applied for Vacation Leave", "Already Approved"));

        recyclerviewAdapter = new RecyclerviewAdapter(employeeItemList, getActivity());
        recyclerView.setAdapter(recyclerviewAdapter);

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.hr_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);  // Optional: Add to back stack if needed
        fragmentTransaction.commit();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
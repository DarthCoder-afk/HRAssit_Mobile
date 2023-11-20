package com.example.bottomnavbar;

import static android.content.ContentValues.TAG;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HomeFragment extends Fragment {
    private TextView usernameText;
    private TextView total_absentText;
    private TextView total_pendingText;
    private TextView total_leaveText;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        usernameText = view.findViewById(R.id.UsernameTxt);
        total_absentText = view.findViewById(R.id.absentnumber);
        total_pendingText = view.findViewById(R.id.pendingnumber);
        total_leaveText = view.findViewById(R.id.remainingleavenumber);

        // Replace this with the actual way you retrieve the user ID from the User Account collection
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user != null ? user.getUid() : null;

        if (userID != null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference requestFormsCollection = db.collection("Request Forms");


            Query query = requestFormsCollection.whereEqualTo("user_id", userID);

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {

                int totalPendingForms = 0;
                // Initialize your list of NotificationItems
                employeeItemList = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Assuming your document structure, adjust this accordingly
                    String date = document.getString("transaction_date");
                    String employee_firstname = document.getString("first_name");
                    String employee_lastname = document.getString("last_name");
                    String request_type = document.getString("requestType");
                    String position = document.getString("user_level");
                    String status = document.getString("request_status");

                    String fullName = employee_firstname + " " + employee_lastname;

                    String purpose_text = String.format("You submitted a %s", request_type);

                    employeeItemList.add(new EmployeeItem(date, R.drawable.user, fullName, position,purpose_text,status ));;

                    if ("Pending".equals(status)) {
                        totalPendingForms++;
                    }

                    usernameText.setText(employee_firstname);
                    total_pendingText.setText(String.valueOf(totalPendingForms));

                }

                // Set up RecyclerView
                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                recyclerviewAdapter = new RecyclerviewAdapter(employeeItemList, getActivity());
                recyclerView.setAdapter(recyclerviewAdapter);
            }).addOnFailureListener(e -> {
                // Handle failure, e.g., show an error message
                Log.e(TAG, "Error getting documents: ", e);
            });
        }

        Button seeAllButton = view.findViewById(R.id.AllBtn);
        seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the RecentActivityFragment
                replaceFragment(new HistoryFragment());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);  // Optional: Add to back stack if needed
        fragmentTransaction.commit();
    }

}
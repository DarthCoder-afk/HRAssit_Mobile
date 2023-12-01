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
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    private String fullName;

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

        // Initialize views
        usernameText = view.findViewById(R.id.UsernameTxt);
        total_absentText = view.findViewById(R.id.absentnumber);
        total_pendingText = view.findViewById(R.id.pendingnumber);
        total_leaveText = view.findViewById(R.id.remainingleavenumber);

        // Replace this with the actual way you retrieve the user ID from the User Account collection
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user != null ? user.getUid() : null;

        if (userID != null) {
            // Initialize Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Query to get request forms
            CollectionReference requestFormsCollection = db.collection("Request Information");
            Query query = requestFormsCollection.whereEqualTo("employeeDocID", userID);

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                int totalPendingForms = 0;
                employeeItemList = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Extract data from the document
                    Timestamp timestamp = document.getTimestamp("createdAt");
                    Date date = timestamp != null ? timestamp.toDate() : null;
                    String dateString = date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date) : "";

                    String request_type = document.getString("RequestType");
                    String status = document.getString("RequestStatus");

                    // Assuming you have a nested "Request_Details" field, adjust accordingly
                    Map<String, Object> requestDetails = (Map<String, Object>) document.get("Request_Details");
                    String leaveType = requestDetails != null ? (String) requestDetails.get("LeaveType") : "";

                    String purpose_text = String.format("You submitted a %s - %s", request_type, leaveType);

                    employeeItemList.add(new EmployeeItem(dateString, R.drawable.user, fullName, "Employee", purpose_text, status));

                    if ("Pending".equals(status)) {
                        totalPendingForms++;
                    }
                }

                // Set up RecyclerView
                recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                recyclerviewAdapter = new RecyclerviewAdapter(employeeItemList, getActivity());
                recyclerView.setAdapter(recyclerviewAdapter);

                // Update total pending text
                total_pendingText.setText(String.valueOf(totalPendingForms));
            }).addOnFailureListener(e -> {
                // Handle failure, e.g., show an error message
                Log.e(TAG, "Error getting documents: ", e);
            });

            // Query to get user information from "User Account" collection
            db.collection("User Account")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Log.d(TAG,"User id"+userID);

                            // Retrieve the document ID for "Employee Information"
                            String documentID = (String) documentSnapshot.get("documentID");
                            Log.d(TAG,"User acc docid"+documentID);

                            // Query to get employee information from "Employee Information" collection
                            db.collection("Employee Information")
                                    .whereEqualTo("accountID", documentID)
                                    .get()
                                    .addOnSuccessListener(employeeDocuments -> {
                                        if (!employeeDocuments.isEmpty()) {
                                            DocumentSnapshot employeeDocument = employeeDocuments.getDocuments().get(0);

                                            String documentID2 = (String) employeeDocument.get("documentID");
                                            Log.d(TAG,"Emplyee doc id"+documentID2);

                                            // Retrieve data from the "Employee Information" document
                                            Map<String, Object> personalInfo = (Map<String, Object>) employeeDocument.get("Personal_Information");
                                            if (personalInfo != null) {
                                                String firstName = (String) personalInfo.get("FirstName");
                                                String surname = (String) personalInfo.get("SurName");
                                                String userLevel = (String) personalInfo.get("UserLevel");

                                                // Use retrieved data as needed
                                                fullName = firstName + " " + surname;
                                                usernameText.setText(fullName);

                                                showToast("Employee information loaded successfully");
                                            } else {
                                                showToast("No personal information found in Employee Information");
                                            }
                                        } else {
                                            showToast("Employee Information document does not exist");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast("Failed to load employee information. Please try again.");
                                    });

                            db.collection("Employee Information")
                                    .whereEqualTo("accountID", documentID)
                                    .get()
                                    .addOnSuccessListener(employeeDocuments -> {
                                        if (!employeeDocuments.isEmpty()) {
                                            DocumentSnapshot employeeDocument = employeeDocuments.getDocuments().get(0);

                                            String documentID2 = (String) employeeDocument.get("documentID");
                                            Log.d(TAG, "Emplyee doc id" + documentID2);

                                            db.collection("201File Information")
                                                    .whereEqualTo("employeeDocID", documentID2)
                                                    .get()
                                                    .addOnSuccessListener(employeeDocuments2 -> {
                                                        for (QueryDocumentSnapshot employeeDocument2 : employeeDocuments2) {
                                                            // Retrieve data from each "201File Information" document
                                                            Map<String, Object> appointmentDetails = (Map<String, Object>) employeeDocument2.get("Appointment_Details");
                                                            if (appointmentDetails != null) {
                                                                String startingDate = (String) appointmentDetails.get("DateofSigning");
                                                                Log.d(TAG, "Starting Date: " + startingDate);
                                                                showToast("Appointment Details" + startingDate);
                                                                updateLeaveCredits(startingDate);
                                                            } else {
                                                                showToast("No appointment details found in 201File Information");
                                                            }
                                                        }
                                                    });
                                        }
                                            });

                        } else {
                            showToast("No document found for the user ID: " + userID);
                        }
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to load user information. Please try again.");
                    });

            Button seeAllButton = view.findViewById(R.id.AllBtn);
            seeAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Replace the current fragment with the RecentActivityFragment
                    replaceFragment(new HistoryFragment());
                }
            });

        }

        // ... (rest of your code)

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);  // Optional: Add to back stack if needed
        fragmentTransaction.commit();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLeaveCredits(String startDate) {
        // Get the current date
        Date currentDate = new Date();

        // Format the current date in "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateStr = dateFormat.format(currentDate);

        // Call the calculateLeaveCredits function
        double leaveCredits = calculateLeaveCredits(startDate, currentDateStr);

        // Update the leavecreditstxt
        total_leaveText.setText(String.format(Locale.getDefault(), "%.2f", leaveCredits));
    }

    // Function to calculate leave credits
    private double calculateLeaveCredits(String start_date, String end_date) {
        // Conversion tables
        Map<Integer, Double> monthConversion = new HashMap<>();
        monthConversion.put(1, 1.25);
        monthConversion.put(2, 2.5);
        monthConversion.put(3, 3.75);
        monthConversion.put(4, 5.0);
        monthConversion.put(5, 6.25);
        monthConversion.put(6, 7.5);
        monthConversion.put(7, 8.75);
        monthConversion.put(8, 10.0);
        monthConversion.put(9, 11.25);
        monthConversion.put(10, 12.5);
        monthConversion.put(11, 13.75);
        monthConversion.put(12, 15.0);

        Map<Integer, Double> dayConversion = new HashMap<>();
        for (int i = 1; i <= 30; i++) {
            dayConversion.put(i, i / 30.0);
        }

        // Parse start and end dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate, endDate;
        try {
            startDate = dateFormat.parse(start_date);
            endDate = dateFormat.parse(end_date);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }

        // Calculate the number of months and days worked
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        int monthsWorked = (int) diffInDays / 30;
        int daysInLastMonth = (int) diffInDays % 30;

        // Calculate leave credits
        double leaveCredits = monthsWorked * monthConversion.get(startDate.getMonth() + 1);
        leaveCredits += monthConversion.get(startDate.getMonth() + 1) * (daysInLastMonth / 30.0);

        return leaveCredits * 2.0;  // Multiply by 2 for both vacation and sick leave
    }

}
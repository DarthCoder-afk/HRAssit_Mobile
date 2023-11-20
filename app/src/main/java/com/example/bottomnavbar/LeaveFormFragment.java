package com.example.bottomnavbar;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaveFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaveFormFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String leaveType;
    private String startDate;
    private String endDate;
    private String headOfficer;
    private String reason;


    private Spinner spinnerLeaveType;
    private TextInputEditText editTextHeadOfficer;
    private TextInputEditText editTextReason;
    private Button startdate;
    private Button enddate;

    private String userid;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String[] LEAVE_TYPES = {"Sick Leave", "Vacation Leave", "Bereavement", "Other"};

    public LeaveFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaveFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaveFormFragment newInstance(String param1, String param2) {
        LeaveFormFragment fragment = new LeaveFormFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_form, container, false);

        spinnerLeaveType = view.findViewById(R.id.spinnerLeaveType);
        editTextHeadOfficer = view.findViewById(R.id.editTextHeadOfficer);
        editTextReason = view.findViewById(R.id.editTextReason);
        startdate = view.findViewById(R.id.buttonStartDateLeave);
        enddate = view.findViewById(R.id.buttonEndDateLeave);

        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner leaveTypeSpinner = view.findViewById(R.id.spinnerLeaveType);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, LEAVE_TYPES);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        leaveTypeSpinner.setAdapter(adapter);

        Button startDateButton = view.findViewById(R.id.buttonStartDateLeave);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(startDateButton);
            }
        });

        Button endDateButton = view.findViewById(R.id.buttonEndDateLeave);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(endDateButton);
            }
        });

        Button submitButton = view.findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLeaveFormToFirestore();
            }
        });


    }

    private void showDatePickerDialog(final Button dateButton) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Handle the selected date, for example:
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dateButton.setText(selectedDate);
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void saveLeaveFormToFirestore() {
        // Get values from UI elements
        String requestType = "Leave Form";
        String request_status = "Pending";
        String transaction_date = getCurrentDateTime();
        String leaveType = spinnerLeaveType.getSelectedItem().toString();
        String startDate = startdate.getText().toString();
        String endDate = enddate.getText().toString(); // Get the actual end date value
        String headOfficer = editTextHeadOfficer.getText().toString();
        String reason = editTextReason.getText().toString();

        // Validate input
        if (isEmpty(leaveType) || isEmpty(startDate) || isEmpty(endDate) || isEmpty(headOfficer) || isEmpty(reason)) {
            showToast("Please fill in all fields");
            return;
        }

        // Create a LeaveFormData object
        LeaveFormData leaveFormData = new LeaveFormData();
        leaveFormData.setRequestType(requestType);
        leaveFormData.setLeaveType(leaveType);
        leaveFormData.setStartDate(startDate);
        leaveFormData.setEndDate(endDate);
        leaveFormData.setHeadOfficer(headOfficer);
        leaveFormData.setReason(reason);
        leaveFormData.setRequest_status(request_status);
        leaveFormData.setTransaction_date(transaction_date);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            Log.d("UserID", "User ID: " + userID);

            leaveFormData.setUser_id(userID);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference requestFormsCollection = db.collection("Request Forms");

            requestFormsCollection.add(leaveFormData)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Leave form submitted successfully");
                        clearFormFields();
                        replaceFragment(new HistoryFragment());
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to submit leave form. Please try again.");
                    });
        } else {
            showToast("User not authenticated");
        }
    }

    private boolean isEmpty(String value) {
        return value.trim().isEmpty();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }


    private void clearFormFields() {
        // Clear the input fields
        startdate.setText("Start Date");
        enddate.setText("End Date");
        editTextHeadOfficer.setText("");
        editTextReason.setText("");

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);  // Optional: Add to back stack if needed
        fragmentTransaction.commit();
    }

}
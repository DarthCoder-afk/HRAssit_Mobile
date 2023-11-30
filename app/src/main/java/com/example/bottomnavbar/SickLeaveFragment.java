package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SickLeaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SickLeaveFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText fileDetailsTextView;
    private Spinner spinnerLeaveType;

    private TextInputEditText specifyillness1;

    private TextInputEditText specifyillness2;

    private Button startdate;
    private Button enddate;

    private String userid;

    private Uri fileUri;

    private static final String[] LEAVE_TYPES = {"Sick Leave","Vacation Leave","Mandatory Leave", "Paternity Leave",
            "Special Privilege Leave","Solo Parent Leave","Study Leave","10-Day VAWC Leave","Rehabilitation Privilege",
            "Special Leave Benefits for Women","Special Emergency (Calamity) Leave","Adoption Leave","Other"};

    private static final int PICK_FILE_REQUEST_CODE = 1;

    public SickLeaveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SickLeaveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SickLeaveFragment newInstance(String param1, String param2) {
        SickLeaveFragment fragment = new SickLeaveFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sick_leave, container, false);

        spinnerLeaveType = view.findViewById(R.id.spinnerLeaveType);
        startdate = view.findViewById(R.id.buttonStartDateLeave);
        enddate = view.findViewById(R.id.buttonEndDateLeave);
        fileDetailsTextView = view.findViewById(R.id.file_details);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner leaveTypeSpinner = view.findViewById(R.id.spinnerLeaveType);
        Button uploadImageButton = view.findViewById(R.id.uploadimage);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, LEAVE_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        leaveTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLeaveType = LEAVE_TYPES[position];


               if (selectedLeaveType.equals("Vacation Leave")) {

                    VacationLeaveFragment vacationLeaveFragment = new VacationLeaveFragment();
                    replaceFragment(vacationLeaveFragment);
                } else if (selectedLeaveType.equals("Study Leave")) {

                    StudyLeaveFragment studyLeaveFragment = new StudyLeaveFragment();
                    replaceFragment(studyLeaveFragment);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
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
        final String[] purpose = {spinnerLeaveType.getSelectedItem().toString()};
        String startDate = startdate.getText().toString();
        String endDate = enddate.getText().toString();

        final String[] leave_details = {""};

        // Validate input
        if (isEmpty(purpose[0]) || isEmpty(startDate) || isEmpty(endDate)) {
            showToast("Please fill in all fields");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            Log.d("UserID", "User ID: " + userID);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a StorageReference for the file
            String fileName = getFileName(fileUri);
            StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("uploads/" + fileName);

            // Upload the file to Firebase Storage
            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {

                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String fileUrl = uri.toString();

                            // Query the User Account collection to get user details
                            db.collection("User Account")
                                    .whereEqualTo("userID", userID)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            // The query contains at least one document
                                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                            String user_level = documentSnapshot.getString("UserLevel");

                                            RadioGroup radioGroup = getView().findViewById(R.id.radioGroup3);
                                            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                                            String status;

                                            if (selectedRadioButtonId == R.id.inhospitalbutton) {
                                                // If "In Hospital" radio button is selected, get value from inhospitalspecify
                                                leave_details[0] = ((TextInputEditText) getView().findViewById(R.id.inhospitalspecify)).getText().toString();
                                                status = "In Hospital";
                                            } else if (selectedRadioButtonId == R.id.outpatientbutton) {
                                                // If "Out Patient" radio button is selected, get value from outpatientspecify
                                                leave_details[0] = ((TextInputEditText) getView().findViewById(R.id.outpatientspecify)).getText().toString();
                                                status = "Out Patient";
                                            } else {
                                                // Handle the case where no radio button is selected
                                                showToast("Please select a status");
                                                return;
                                            }

                                            // Validate input
                                            if (isEmpty(purpose[0]) || isEmpty(leave_details[0])) {
                                                showToast("Please fill in all fields");
                                                return;
                                            }

                                            // Create a RequestFormData object
                                            LeaveRequestFormData leaveRequestFormData = new LeaveRequestFormData();
                                            leaveRequestFormData.setRequestType(requestType);
                                            leaveRequestFormData.setStartDate(startDate);
                                            leaveRequestFormData.setEndDate(endDate);
                                            leaveRequestFormData.setPurpose(purpose[0]);
                                            leaveRequestFormData.setRequest_status(request_status);
                                            leaveRequestFormData.setTransaction_date(transaction_date);
                                            leaveRequestFormData.setUser_id(userID);
                                            leaveRequestFormData.setUser_level(user_level);
                                            leaveRequestFormData.setFileUrl(fileUrl);
                                            leaveRequestFormData.setDetails_of_leave(status);
                                            leaveRequestFormData.setSpecified(leave_details[0]);

                                            // Add the request form to the Request Information collection
                                            CollectionReference requestInformationCollection = db.collection("Request Information");
                                            requestInformationCollection.add(leaveRequestFormData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        showToast("Leave form submitted successfully");
                                                        clearFormFields();
                                                        replaceFragment(new HomeFragment());
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        showToast("Failed to submit leave form. Please try again.");
                                                    });
                                        } else {
                                            showToast("User details not found");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast("Error retrieving user details");
                                        Log.e("Firestore", "Error retrieving user details", e);
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to upload file. Please try again.");
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


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);  // Optional: Add to back stack if needed
        fragmentTransaction.commit();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();

            String fileName = getFileName(fileUri);
            if (fileDetailsTextView != null) {
                fileDetailsTextView.setText(fileName);
            } else {
                showToast("Error: Unable to display file name.");
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

}
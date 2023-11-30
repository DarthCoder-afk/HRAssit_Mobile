package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocatorForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocatorForm extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText editTextPurpose;
    private Button startdate;
    private Button enddate;

    private TextInputEditText fileDetailsTextView;

    private StorageReference storageReference;

    private Uri fileUri;

    private static final int PICK_FILE_REQUEST_CODE = 1;

    public LocatorForm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocatorForm.
     */
    // TODO: Rename and change types and number of parameters
    public static LocatorForm newInstance(String param1, String param2) {
        LocatorForm fragment = new LocatorForm();
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
        View view = inflater.inflate(R.layout.fragment_locator_form, container, false);

        editTextPurpose = view.findViewById(R.id.editTextPurpose);
        startdate = view.findViewById(R.id.buttonStartDateLocator);
        enddate = view.findViewById(R.id.buttonEndDateLocator);
        fileDetailsTextView = view.findViewById(R.id.file_details);

        storageReference = FirebaseStorage.getInstance().getReference();

        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button startDateButton = view.findViewById(R.id.buttonStartDateLocator);
        Button uploadImageButton = view.findViewById(R.id.uploadimagebtn);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(startDateButton);
            }
        });

        Button endDateButton = view.findViewById(R.id.buttonEndDateLocator);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(endDateButton);
            }
        });

        Button submitButton = view.findViewById(R.id.buttonSubmitlocator);
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

    private void showTimePickerDialog(final Button timeButton) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireActivity(),
                (view, selectedHour, selectedMinute) -> {
                    // Handle the selected time, for example:
                    String selectedTime = formatTime(selectedHour, selectedMinute);
                    timeButton.setText(selectedTime);
                },
                hour,
                minute,
                false // set false for 12-hour format
        );
        timePickerDialog.show();
    }

    private String formatTime(int hourOfDay, int minute) {
        String timeSuffix = (hourOfDay < 12) ? "AM" : "PM";
        int hourFormat = (hourOfDay > 12) ? (hourOfDay - 12) : hourOfDay;
        return String.format(Locale.getDefault(), "%02d:%02d %s", hourFormat, minute, timeSuffix);
    }

    private void saveLeaveFormToFirestore() {
        // Get values from UI elements
        String requestType = "Locator Form";
        String request_status = "Pending";
        String transaction_date = getCurrentDateTime();
        String purpose = editTextPurpose.getText().toString();
        String startDate = startdate.getText().toString();
        String endDate = enddate.getText().toString(); // Get the actual end date value


        // Validate input
        if (isEmpty(purpose) || isEmpty(startDate) || isEmpty(endDate)) {
            showToast("Please fill in all fields");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            Log.d("UserID", "User ID: " + userID);

            // Query the User Account collection to get user details
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String fileName = getFileName(fileUri);
            StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("uploads/" + fileName);


            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {

                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String fileUrl = uri.toString();

                            RadioGroup radioGroup = getView().findViewById(R.id.radioGroup);
                            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                            String status;

                            if (selectedRadioButtonId == R.id.officialbutton) {
                                status = "Official";
                            } else if (selectedRadioButtonId == R.id.personalbutton) {
                                status = "Personal";
                            } else {
                                // Handle the case where no radio button is selected
                                showToast("Please select a status");
                                return;
                            }

                            // Query the User Account collection to get user details
                            db.collection("User Account")
                                    .whereEqualTo("userID", userID)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            // The query contains at least one document
                                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                            String user_level = documentSnapshot.getString("UserLevel");

                                            // Create a RequestFormData object
                                            LocatorFormData locatorFormData = new LocatorFormData();
                                            locatorFormData.setRequestType(requestType);
                                            locatorFormData.setStartDate(startDate);
                                            locatorFormData.setDeparture_time(endDate);
                                            locatorFormData.setPurpose(purpose);
                                            locatorFormData.setRequest_status(request_status);
                                            locatorFormData.setTransaction_date(transaction_date);
                                            locatorFormData.setUser_id(userID);
                                            locatorFormData.setUser_level(user_level);
                                            locatorFormData.setFileUrl(fileUrl);
                                            locatorFormData.setDetails(status);

                                            // Add the request form to the Request Information collection
                                            CollectionReference requestInformationCollection = db.collection("Request Information");
                                            requestInformationCollection.add(locatorFormData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        showToast("Locator form submitted successfully");
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

        editTextPurpose.setText("");
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
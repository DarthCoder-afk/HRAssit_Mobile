package com.example.bottomnavbar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView usernameText;
    private TextView addressText;
    private TextView phoneText;
    private TextView birthText;

    private TextView emailText;
    private ImageView ImgUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        usernameText = view.findViewById(R.id.employee_name);
        phoneText = view.findViewById(R.id.employee_phonenum);
        addressText = view.findViewById(R.id.employee_address);
        birthText = view.findViewById(R.id.employee_birthday);
        emailText = view.findViewById(R.id.employee_email);
        ImgUser = view.findViewById(R.id.employeeprofile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Log.d("UID", "User UID: " + uid);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Use a query to find the document based on the user ID
            db.collection("User Account")
                    .whereEqualTo("userID", uid) // Assuming there's a field "UserID" in your documents
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Log.d("DID", "Document DID: " + documentId);
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String documentID = (String) documentSnapshot.get("documentID");

                            db.collection("Employee Information")
                                    .whereEqualTo("accountID", documentID)
                                    .get()
                                    .addOnSuccessListener(employeeDocuments -> {
                                        for (QueryDocumentSnapshot employeeDocument : employeeDocuments) {


                                            String profileUrl = (String) employeeDocument.get("ProfilePictureURL");

                                            // Load and display the image using Glide
                                            Glide.with(this)
                                                    .load(profileUrl)
                                                    .placeholder(R.drawable.employee) // Placeholder image while loading
                                                    .transform(new CircleCrop())
                                                    .into(ImgUser);

                                            Map<String, Object> personalInfo = (Map<String, Object>) employeeDocument.get("Personal_Information");
                                            if (personalInfo != null) {
                                                String firstName = (String) personalInfo.get("FirstName");
                                                String surname = (String) personalInfo.get("SurName");
                                                String phonenum = (String) personalInfo.get("MobileNumber");
                                                String birthdate = (String) personalInfo.get("Birthdate");
                                                String province = (String) personalInfo.get("Province");
                                                String municipality = (String) personalInfo.get("Municipality");
                                                String email = (String) personalInfo.get("Email");



                                                // Use retrieved data as needed
                                                String fullName = firstName + " " + surname;
                                                String address = municipality + "," + province;
                                                usernameText.setText(fullName);
                                                phoneText.setText(phonenum);
                                                birthText.setText(birthdate);
                                                addressText.setText(address);
                                                emailText.setText(email);


                                                showToast("Employee information loaded successfully");
                                            } else {
                                                showToast("No personal information found in Employee Information");
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast("Failed to load employee information. Please try again.");
                                    });
                        } else {
                            showToast("No document found for the user ID: " + uid);
                        }
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to load user information. Please try again.");
                    });



        } else {
            // Handle case where user is not authenticated
            showToast("User not authenticated");
        }

        return view;
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
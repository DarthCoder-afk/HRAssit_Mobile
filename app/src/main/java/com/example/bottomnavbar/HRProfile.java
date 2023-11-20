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

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HRProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HRProfile extends Fragment {
    private TextView usernameText;
    private TextView addressText;
    private TextView phoneText;
    private TextView birthText;

    private TextView emailText;
    private ImageView hrImgUser;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HRProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HRProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static HRProfile newInstance(String param1, String param2) {
        HRProfile fragment = new HRProfile();
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


        View view = inflater.inflate(R.layout.fragment_h_r_profile, container, false);
        usernameText = view.findViewById(R.id.admin_name);
        phoneText = view.findViewById(R.id.admin_phonenum);
        addressText = view.findViewById(R.id.admin_address);
        birthText = view.findViewById(R.id.admin_birthday);
        emailText = view.findViewById(R.id.admin_email);
        hrImgUser = view.findViewById(R.id.adminprofile);

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

                            String email = documentSnapshot.getString("Email");

                            emailText.setText(email);

                            Map<String, Object> profile = (Map<String, Object>) documentSnapshot.get("Account_Information");
                            if (profile == null) {
                                throw new AssertionError();
                            }
                            String profileUrl = (String) profile.get("ProfilePictureURL");

                            // Load and display the image using Glide
                            Glide.with(this)
                                    .load(profileUrl)
                                    .placeholder(R.drawable.employee) // Placeholder image while loading
                                    .transform(new CircleCrop())
                                    .into(hrImgUser);


                            // Retrieve the username from the nested field "Account_Information"
                            Map<String, Object> accountInfo = (Map<String, Object>) documentSnapshot.get("Personal_Information");
                            if (accountInfo != null) {

                                String first_name = (String) accountInfo.get("FirstName");
                                String last_name = (String) accountInfo.get("LastName");
                                String address = (String) accountInfo.get("Address");
                                String birthdate = (String) accountInfo.get("Birthdate");
                                String phone_num = (String) accountInfo.get("Phone");


                                String fullName = first_name + " " + last_name;


                                usernameText.setText(fullName);
                                addressText.setText(address);
                                phoneText.setText(phone_num);
                                birthText.setText(birthdate);


                                showToast("Profile loaded successfully");
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

        return view;
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
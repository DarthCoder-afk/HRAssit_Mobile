package com.example.bottomnavbar;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter recyclerviewAdapter;
    private List<HistoryItem> historyItemList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user != null ? user.getUid() : null;
        if (userID != null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference requestFormsCollection = db.collection("Request Information");

            Query query = requestFormsCollection.whereEqualTo("employeeDocID", userID);

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {

                // Initialize your list of NotificationItems
                historyItemList = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Timestamp timestamp = document.getTimestamp("createdAt");
                    Date date = timestamp != null ? timestamp.toDate() : null;
                    String dateString = date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date) : "";
                    Log.d(TAG, "Document ID: " + document.getId());
                    // Extract data from the document

                    String request_type = document.getString("RequestType");
                    //String position = document.getString("request_Details.UserLevel");
                    String status = document.getString("RequestStatus");



                    // Assuming you have a nested "Request_Details" field, adjust accordingly
                    Map<String, Object> requestDetails = (Map<String, Object>) document.get("Request_Details");
                    String leaveType = requestDetails != null ? (String) requestDetails.get("LeaveType") : "";

                    String purpose_text = String.format("You submitted a %s - %s", request_type, leaveType);
                    historyItemList.add(new HistoryItem(dateString, purpose_text));;

                }

                // Set up RecyclerView
                recyclerView = view.findViewById(R.id.history);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                recyclerviewAdapter = new HistoryAdapter(historyItemList, getActivity());
                recyclerView.setAdapter(recyclerviewAdapter);
            }).addOnFailureListener(e -> {
                // Handle failure, e.g., show an error message
                Log.e(TAG, "Error getting documents: ", e);
            });
        }
        return view;
}
}


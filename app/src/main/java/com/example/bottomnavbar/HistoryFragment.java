package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;  // Add this import
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "HistoryFragment";

    private RecyclerView recyclerView;
    private HistoryAdapter recyclerviewAdapter;
    private List<HistoryItem> historyItemList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user != null ? user.getUid() : null;

        if (userID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference requestFormsCollection = db.collection("Request Information");

            Query query = requestFormsCollection.whereEqualTo("employeeDocID", userID);

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                historyItemList = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Timestamp timestamp = document.getTimestamp("createdAt");
                    Date date = timestamp != null ? timestamp.toDate() : null;
                    String dateString = date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date) : "";
                    Log.d(TAG, "Document ID: " + document.getId());

                    String request_type = document.getString("RequestType");
                    String status = document.getString("RequestStatus");

                    Map<String, Object> requestDetails = (Map<String, Object>) document.get("Request_Details");
                    String leaveType = requestDetails != null ? (String) requestDetails.get("LeaveType") : "";

                    String purpose_text = String.format("You submitted a %s - %s", request_type, leaveType);
                    historyItemList.add(new HistoryItem(dateString, purpose_text, document.getId())); // Pass document ID

                }

                recyclerView = view.findViewById(R.id.history);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                recyclerviewAdapter = new HistoryAdapter(historyItemList, getActivity());
                recyclerView.setAdapter(recyclerviewAdapter);

                recyclerviewAdapter.setOnItemClickListener(HistoryFragment.this);

            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting documents: ", e);
            });
        }
        return view;
    }

    @Override
    public void onItemClick(int position) {
        HistoryItem clickedItem = historyItemList.get(position);
        showDetailsDialog(clickedItem, position);
    }

    private void showDetailsDialog(HistoryItem historyItem, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Details")
                .setMessage("Date: " + historyItem.getDate() + "\nContent: " + historyItem.getContent())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Remove", (dialog, which) -> {
                    // Remove the item from the RecyclerView and Firebase
                    removeItem(position);
                })
                .show();
    }

    private void removeItem(int position) {
        // Get the document ID of the corresponding item in Firebase
        String documentId = getDocumentId(position);

        // Remove the item from the RecyclerView
        historyItemList.remove(position);
        recyclerviewAdapter.notifyItemRemoved(position);

        // Remove the item from the Firebase collection
        if (documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference requestFormsCollection = db.collection("Request Information");

            requestFormsCollection.document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error deleting document", e));
        }
    }

    private String getDocumentId(int position) {
        // Get the document ID of the corresponding item in Firebase
        if (position >= 0 && position < historyItemList.size()) {
            return historyItemList.get(position).getDocumentId(); // Replace with the actual method to get the document ID
        }
        return null;
    }
}

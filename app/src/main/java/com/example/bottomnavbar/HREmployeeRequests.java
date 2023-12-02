package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HREmployeeRequests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HREmployeeRequests extends Fragment implements OnItemClickListener {

    private static final String TAG = "HREmployeeRequests";

    private RecyclerView recyclerView;
    private RequestRecyclerViewAdapter recyclerviewAdapter;
    private List<RequestItem> requestItemList;

    private String fullName;

    public HREmployeeRequests() {
        // Required empty public constructor
    }

    public static HREmployeeRequests newInstance() {
        return new HREmployeeRequests();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_r_employee_requests, container, false);

        recyclerView = view.findViewById(R.id.request_form);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Request Information")
                    .whereEqualTo("RequestStatus", "Pending")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            requestItemList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String employeeDocID = document.getString("employeeDocID");
                                Timestamp timestamp = document.getTimestamp("createdAt");
                                Date date = timestamp != null ? timestamp.toDate() : null;
                                String dateString = date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date) : "";

                                String request_type = document.getString("RequestType");

                                requestItemList.add(new RequestItem(dateString, R.drawable.user, fullName, "Employee", request_type, ""));

                                db.collection("Employee Information")
                                        .whereEqualTo("documentID", employeeDocID)
                                        .get()
                                        .addOnSuccessListener(employeeDocuments -> {
                                            if (!employeeDocuments.isEmpty()) {
                                                DocumentSnapshot employeeDocument = employeeDocuments.getDocuments().get(0);

                                                Map<String, Object> personalInfo = (Map<String, Object>) employeeDocument.get("Personal_Information");
                                                if (personalInfo != null) {
                                                    String firstName = (String) personalInfo.get("FirstName");
                                                    String surname = (String) personalInfo.get("SurName");
                                                    fullName = firstName + " " + surname;

                                                    showToast("Employee information loaded successfully");

                                                    // Initialize and set up the adapter after data is loaded
                                                    recyclerviewAdapter = new RequestRecyclerViewAdapter(requestItemList, getActivity());
                                                    recyclerView.setAdapter(recyclerviewAdapter);
                                                    recyclerviewAdapter.setOnItemClickListener(HREmployeeRequests.this);

                                                    // Notify the adapter that the data has changed
                                                    recyclerviewAdapter.notifyDataSetChanged();
                                                } else {
                                                    showToast("No personal information found in Employee Information");
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
        return view;
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        RequestItem requestItem = requestItemList.get(position);
        showDetailsDialog(requestItem, position);
    }

    private void showDetailsDialog(RequestItem requestItem, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Details")
                .setMessage("Date: " + requestItem.getRequest_date() + "\nContent: " + requestItem.getRequestType())
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
        requestItemList.remove(position);
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
        if (position >= 0 && position < requestItemList.size()) {
            // Replace with the actual method to get the document ID
            return ""; // Adjust according to your implementation
        }
        return null;
    }
}

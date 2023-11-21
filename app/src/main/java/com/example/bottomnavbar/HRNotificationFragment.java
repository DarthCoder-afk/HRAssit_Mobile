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
 * Use the {@link HRNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HRNotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter recyclerviewAdapter;
    private List<NotifItem> notifItemList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HRNotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HRNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HRNotificationFragment newInstance(String param1, String param2) {
        HRNotificationFragment fragment = new HRNotificationFragment();
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
        View view = inflater.inflate(R.layout.fragment_h_r_notification, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Request Forms")
                    .whereEqualTo("request_status", "Pending")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            notifItemList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date = document.getString("transaction_date");
                                String request_type = document.getString("requestType");
                                String status = document.getString("request_status");

                                String pending_text = String.format("An employee submitted a %s", request_type);

                                notifItemList.add(new NotifItem(date, pending_text));
                            }
                            recyclerView = view.findViewById(R.id.hrnotif);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            recyclerviewAdapter = new NotificationAdapter(notifItemList, getActivity());
                            recyclerView.setAdapter(recyclerviewAdapter);

                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        }


        return view;
    }
}
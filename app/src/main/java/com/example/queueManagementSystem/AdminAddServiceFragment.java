package com.example.queueManagementSystem;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminAddServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAddServiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminAddServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminAddServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminAddServiceFragment newInstance(String param1, String param2) {
        AdminAddServiceFragment fragment = new AdminAddServiceFragment();
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

    View view;
    DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_add_service_fragment, container, false);

        // do stuff here
        db = new DatabaseHelper(getActivity());
        Button btnAddService = (Button) view.findViewById(R.id.btnAddService);
        EditText etServiceName = view.findViewById(R.id.etServiceName);
        EditText etNumOfCounters = view.findViewById(R.id.etNumOfCounters);
        Resources res = getResources();


        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceName = etServiceName.getText().toString().trim();
                Integer numOfCounters =  Integer.parseInt(etNumOfCounters.getText().toString().trim());

                //tvServiceDetails.setText(text);

                //add service to db
                long val = db.addService(serviceName, numOfCounters);

                if (val > 0) {
                    Toast.makeText(getActivity(), "Successfully added service", Toast.LENGTH_SHORT).show();
                    //
                    androidx.fragment.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        // close current fragment
                        fm.popBackStack();
                        // refresh current activity
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                }
            }
        });


        return view;
    }
}
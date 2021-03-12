package com.example.queueManagementSystem;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminServiceSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminServiceSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminServiceSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminServiceSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminServiceSettingsFragment newInstance(String param1, String param2) {
        AdminServiceSettingsFragment fragment = new AdminServiceSettingsFragment();
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
    Intent currentIntent;
    int serviceId, serviceRunning;
    String serviceName;
    DatabaseHelper db;
    TextView tvServiceTitle;
    EditText etServiceName;
    Button btnSaveChanges, btnStopService, btnStartService, btnDeleteService;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.admin_service_settings_fragment, container, false);

        // init components
        db = new DatabaseHelper(getActivity());
        currentIntent = getActivity().getIntent();
        serviceId = currentIntent.getIntExtra("serviceId", -1);
        tvServiceTitle = view.findViewById(R.id.tvServiceSettingTitle);
        etServiceName = view.findViewById(R.id.etServiceName);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnStopService = view.findViewById(R.id.btnStopService);
        btnStartService = view.findViewById(R.id.btnStartService);
        btnDeleteService = view.findViewById(R.id.btnDeleteService);

        //disable save button before any changes are made
        disableSaveButton();

        // get service name
        Cursor cursor = db.getService(serviceId);

        if (cursor.moveToFirst()) {
            serviceName = cursor.getString(1);
            serviceRunning = cursor.getInt(2);
        }

        // set page title to reflect service name
        tvServiceTitle.setText("Service: " + serviceName);

        //set form
        etServiceName.setText(serviceName);

       etServiceName.addTextChangedListener(etServiceNameWatcher);

        if (serviceRunning == 0) {
            btnStartService.setVisibility(View.VISIBLE);
            btnStopService.setVisibility(View.INVISIBLE);
        } else {
            btnStopService.setVisibility(View.VISIBLE);
            btnStartService.setVisibility(View.INVISIBLE);
        }

        // save form changes
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newServiceName = etServiceName.getText().toString().trim();
                long res = db.setServiceName(serviceId, newServiceName);

                if (res > 0) {
                    Toast.makeText(getActivity(), "Successfully updated service", Toast.LENGTH_SHORT).show();

                    //reset page
                    tvServiceTitle.setText("Service: " + newServiceName);
                    etServiceName.setText(newServiceName);
                    disableSaveButton();
                }
            }
        });

        // handle stop service request
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long res = db.setServiceRunning(serviceId, 0);

                if (res > 0) {
                    refreshActivity("update", serviceName);
                }
            }
        });

        // handle start service request
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long res = db.setServiceRunning(serviceId, 1);

                if (res > 0) {
                    refreshActivity("update", serviceName);
                }
            }
        });

        // handle stop button request
        btnDeleteService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long res = db.deleteService(serviceId);

                if (res > 0) {
                    refreshActivity("delete", serviceName);
                }
            }
        });

        return view;
    }

    private TextWatcher etServiceNameWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void afterTextChanged(Editable s) {
            // detect if new service name is equal to current service name
            String newServiceName = (String) s.toString();
            if (newServiceName.equals(serviceName)) {
                if (btnSaveChanges.isEnabled()) {
                    disableSaveButton();
                }
            } else {
                if (!btnSaveChanges.isEnabled()) {
                    enableSaveButton();
                }
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void disableSaveButton() {
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_400)));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void enableSaveButton() {
        btnSaveChanges.setEnabled(true);
        btnSaveChanges.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magenta)));
    }

    public void refreshActivity(String mode, String serviceName) {
        String message = "";
        if (mode == "update") {
            message = "Successfully updated service " + serviceName;
        } else if (mode == "delete") {
            message = "Successfully deleted service " + serviceName;
        }
        androidx.fragment.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            // close current fragment
            fm.popBackStack();
            // refresh current activity
            getActivity().finish();
            startActivity(getActivity().getIntent());
        }
    }
}
package com.example.queueManagementSystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
 * Use the {@link AdminProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminProfileFragment newInstance(String param1, String param2) {
        AdminProfileFragment fragment = new AdminProfileFragment();
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
    Button btnLogout, btnSaveChanges;
    TextView tvUsername;
    Intent currentIntent;
    EditText etCustOldPassword;
    EditText etCustNewPassword;
    Admin admin;
    boolean passwordMatch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_profile_fragment, container, false);

        //init components
        db = new DatabaseHelper(getActivity());
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnSaveChanges = (Button) view.findViewById(R.id.btnSaveChanges);
        etCustOldPassword = (EditText) view.findViewById(R.id.etCustOldPassword);
        etCustNewPassword = (EditText) view.findViewById(R.id.etCustNewPassword);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        currentIntent = getActivity().getIntent();
        admin = (Admin) currentIntent.getSerializableExtra("adminObject");

        // set customer profile username
        tvUsername.setText(admin.getUsername());

        //disable save changes button by default
        disableSaveButton();

        // detect input in both password fields
        etCustOldPassword.addTextChangedListener(oldPasswordWatcher);
        etCustNewPassword.addTextChangedListener(newPasswordWatcher);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity currentActivity = getActivity();
                Intent intent = new Intent(currentActivity, LoginActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        });


        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = etCustNewPassword.getText().toString().trim();
                long res = db.setUserPassword(admin.getId(), newPassword);
                admin.setPassword(newPassword);

                if (res > 0) {
                    Toast.makeText(getActivity(), "Successfully updated password", Toast.LENGTH_SHORT).show();

                    //reset form
                    etCustOldPassword.setText("");
                    etCustNewPassword.setText("");
                    disableSaveButton();
                }

                db.close();
            }
        });

        return view;
    }

    private TextWatcher oldPasswordWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
//            Toast.makeText(getActivity(), s.toString(), Toast.LENGTH_SHORT).show();
            String oldPassword = (String) s.toString();
            // detect if old password matches user's current password
            if (oldPassword.equals(admin.getPassword())) {
                passwordMatch = true;

            } else {
                passwordMatch = false;
                if (btnSaveChanges.isEnabled()) {
                    disableSaveButton();
                }
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };


    private TextWatcher newPasswordWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
//            Toast.makeText(getActivity(), s.toString(), Toast.LENGTH_SHORT).show();
            String newPassword = (String) s.toString();
            // enable save changes button only if new password length > 0 and old password matches user's current password
            if (newPassword.length() > 0 && passwordMatch) {
                //re-enable the save changes button
                enableSaveButton();
            } else {
                disableSaveButton();
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };

    public void disableSaveButton() {
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setBackgroundColor(getResources().getColor(R.color.grey_400));
    }

    public void enableSaveButton() {
        btnSaveChanges.setEnabled(true);
        btnSaveChanges.setBackgroundColor(getResources().getColor(R.color.magenta));
    }
}
package com.archive.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.model.Users;
import com.archive.app.view.activity.LoginActivity;


public class ProfileFragment extends Fragment {

    private TextView tvWelcomeUser;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvWelcomeUser = view.findViewById(R.id.tv_welcome_user);
        btnLogout = view.findViewById(R.id.btn_logout);

        displayUserInfo();

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void displayUserInfo() {
        Users currentUser = MyApplication.getCurrentUser();
        if (currentUser != null && currentUser.getFullName() != null) {
            tvWelcomeUser.setText(getString(R.string.welcome_user, currentUser.getFullName()));
        } else if (currentUser != null && currentUser.getUsername() != null) {
            tvWelcomeUser.setText(getString(R.string.welcome_user, currentUser.getUsername())); // Fallback to username
        }
        else {
            tvWelcomeUser.setText(R.string.unknown_user);
        }
    }

    private void logout() {
        MyApplication.logout(); // Clear user data
        // Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish(); // Finish MainActivity
        }
    }
}

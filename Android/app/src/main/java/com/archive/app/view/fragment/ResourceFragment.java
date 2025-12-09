package com.archive.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.view.activity.ResourceDetailActivity;
import com.archive.app.view.adapter.ResourceAdapter;
import com.archive.app.viewmodel.ResourceViewModel;
import com.google.gson.Gson;

public class ResourceFragment extends Fragment {

    private ResourceViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ResourceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resource, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResourceViewModel.class);

        recyclerView = view.findViewById(R.id.recycler_view_resources);
        progressBar = view.findViewById(R.id.progress_bar_resource);

        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ResourceAdapter();
        adapter.setOnItemClickListener(resource -> {
            Intent intent = new Intent(getContext(), ResourceDetailActivity.class);
            intent.putExtra("resource_json", new Gson().toJson(resource));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        // 观察资源列表和分类 Map，只有当两者都就绪时才刷新 UI
        // 这里简化处理，直接在 Adapter 里设置
        viewModel.getResources().observe(getViewLifecycleOwner(), resources -> {
            if (resources != null) {
                adapter.setData(resources, viewModel.getCategoryMap().getValue());
            }
        });

        // 如果分类后加载，也刷新一下
        viewModel.getCategoryMap().observe(getViewLifecycleOwner(), map -> {
            if (viewModel.getResources().getValue() != null) {
                adapter.setData(viewModel.getResources().getValue(), map);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
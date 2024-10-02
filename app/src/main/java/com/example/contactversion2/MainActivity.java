package com.example.contactversion2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.contactversion2.databinding.ActivityMainBinding;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private ContactDAO contactDAO;
    private AppDatabase appDatabase;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    contactList = contactDAO.getAll().stream()
                            .sorted(Comparator.comparing(Contact::getNameLower))
                            .collect(Collectors.toList());
                    contactAdapter.setData(contactList);
                    latch.countDown();
                }
            });
            try {
                latch.await();
                contactAdapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        appDatabase = AppDatabase.getInstance(this);
        contactDAO = appDatabase.contactDAO();

        binding.rvContactList.setLayoutManager(new LinearLayoutManager(this));
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                contactList = contactDAO.getAll().stream()
                        .sorted(Comparator.comparing(Contact::getNameLower))
                        .collect(Collectors.toList());
                contactAdapter = new ContactAdapter(contactList);
                binding.rvContactList.setAdapter(contactAdapter);
                contactAdapter.notifyDataSetChanged();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivityIfNeeded(intent, 1);
            }
        });

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llContent.setVisibility(View.GONE);
                binding.llSearch.setVisibility(View.VISIBLE);
                if (binding.evSearch.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.evSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.evSearch.getWindowToken(), 0);
                binding.llSearch.setVisibility(View.GONE);
                binding.llContent.setVisibility(View.VISIBLE);
                binding.evSearch.setText("");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        contactList = contactDAO.getAll().stream()
                                .sorted(Comparator.comparing(Contact::getNameLower))
                                .collect(Collectors.toList());
                        contactAdapter.setData(contactList);
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                    contactAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }
        });

        binding.evSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = binding.evSearch.getText().toString();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        contactList = contactDAO.searchContactsByName(searchText)
                                .stream().sorted(Comparator.comparing(Contact::getNameLower))
                                .collect(Collectors.toList());
                        contactAdapter.setData(contactList);
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                    contactAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    ///// tao da o day
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
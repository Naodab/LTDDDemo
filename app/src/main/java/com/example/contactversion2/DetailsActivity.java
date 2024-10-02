package com.example.contactversion2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contactversion2.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private AppDatabase appDatabase;
    private ContactDAO contactDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        appDatabase = AppDatabase.getInstance(this);
        contactDAO = appDatabase.contactDAO();

        Intent intent = getIntent();
        DetailsActivity _this = this;
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String name = binding.etName.getText().toString();
                        String phone = binding.etPhone.getText().toString();
                        String email = binding.etEmail.getText().toString();
                        boolean isExistsByName = contactDAO.isExistsName(name);
                        boolean isExistsByPhone = contactDAO.isExistsPhone(phone);
                        if (isExistsByPhone) {
                            runOnUiThread(() -> Toast.makeText(_this, "Phone has been existed!", Toast.LENGTH_SHORT).show());
                        } else if (isExistsByName) {
                            runOnUiThread(() -> Toast.makeText(_this, "Name has been existed!", Toast.LENGTH_SHORT).show());
                        } else {
                            Contact contact = new Contact(name, phone, email);
                            contactDAO.insert(contact);
                            setResult(RESULT_OK, null);
                            finish();
                        }
                    }
                });
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
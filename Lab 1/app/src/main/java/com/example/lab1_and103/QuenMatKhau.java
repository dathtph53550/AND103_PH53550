package com.example.lab1_and103;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class QuenMatKhau extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText txtEmail;
    Button btnGui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quen_mat_khau);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtEmail = findViewById(R.id.txtEmail);
        btnGui = findViewById(R.id.btnGui);

        mAuth = FirebaseAuth.getInstance();

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                if(email.isEmpty()){
                    txtEmail.setError("Vui lòng nhập email");
                    return;
                }

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(QuenMatKhau.this, "Đã gửi email thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(QuenMatKhau.this, "Đã gửi email thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
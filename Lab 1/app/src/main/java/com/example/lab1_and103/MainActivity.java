package com.example.lab1_and103;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1_and103.Adapter.UserAdapter;
import com.example.lab1_and103.DTO.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    private FirebaseAuth mAuth;
    RecyclerView rycUser;
    FirebaseFirestore db;
    ArrayList<User> listUser;
    Button btnThem;
    UserAdapter adapter;

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
        rycUser = findViewById(R.id.rycUser);
        btnThem = findViewById(R.id.btnThem);
        listUser = new ArrayList<>();
        listUser.add(new User("1","NV001","Hoang Tien Dat","2005"));
        listUser.add(new User("2","NV002","Ngo Kha Ba","1999"));
        listUser.add(new User("3","NV003","Sa Ngo Tinh","2001"));

        adapter = new UserAdapter(this,listUser);
        rycUser.setAdapter(adapter);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

//        ghiDuLieu();
        docDuLieu();

//        Button btnLogOut = findViewById(R.id.btnLogOut);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        btnLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, DangNhap.class));
//            }
//        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogThem();
            }
        });
    }

    private void ghiDuLieu(){
        CollectionReference user = db.collection("user");
        Map<String,Object> userList = new HashMap<>();
        userList.put("ma", "NV001");
        userList.put("ten","Hoang Tien Dat");
        userList.put("namSinh","2005");

        user.document("NV1").set(userList);

        Map<String,Object> userList2 = new HashMap<>();
        userList.put("ma", "NV002");
        userList.put("ten","Ngo Kha Ba");
        userList.put("namSinh","1999");

        user.document("NV2").set(userList);

        Map<String,Object> userList3 = new HashMap<>();
        userList.put("ma", "NV003");
        userList.put("ten","Sa Ngo Tinh");
        userList.put("namSinh","2001");

        user.document("NV3").set(userList);
    }

    private void docDuLieu(){
        listUser.clear();
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("zzzz", "onComplete: " + document.getId() + " => " + document.getData());
                                String ma = document.getString("maUser");
                                String ten = document.getString("tenUser");
                                String namSinh = document.getString("namSinh");

                                User nguoiDung = new User(document.getId(),ma, ten, namSinh);
                                listUser.add(nguoiDung);
                            }
                            UpdateListView();
                        }
                        else{
                            Log.d("zzzz", "onComplete: ", task.getException());
                        }
                    }
                });
    }

    void UpdateListView(){
        Log.d("zz", "onCreate: listbook = " + listUser.size());
        adapter.notifyDataSetChanged();
    }

    void dialogThem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = ((Activity)this).getLayoutInflater().inflate(R.layout.dialog_them,null);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtMa = v.findViewById(R.id.edtMa);
        EditText edtTen = v.findViewById(R.id.edtTen);
        EditText edtNamSinh = v.findViewById(R.id.edtNamSinh);
        Button btnThem = v.findViewById(R.id.btnThem);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMa.getText().toString();
                String ten = edtTen.getText().toString();
                String namSinh = edtNamSinh.getText().toString();

                if(ma.isEmpty() || ten.isEmpty() || namSinh.isEmpty()){
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("user")
                        .whereEqualTo("maUser", ma)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        User userThem = new User(task.getResult().size() + 1 + "",ma, ten, namSinh);
                                        db.collection("user")
                                                .add(userThem)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d("zzzz", "onSuccess: Thêm nhân viên thành công");
                                                        docDuLieu();
                                                        dialog.dismiss();
                                                        Log.d("bbb", "onSuccess: " + task.getResult().size());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("zzzz", "onFailure: Lỗi thêm nhân viên");
                                                        e.printStackTrace();
                                                    }
                                                });
                                    } else {
                                        // Nếu có dữ liệu trùng, thông báo cho người dùng
                                        Toast.makeText(MainActivity.this, "Mã NV đã tồn tại!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("zzzz", "onComplete: Lỗi khi kiểm tra mã NV", task.getException());
                                }
                            }
                        });
            }
        });
    }

}
package com.example.lab1_and103.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1_and103.DTO.User;
import com.example.lab1_and103.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    Context context;
    ArrayList<User> listUser;

    public UserAdapter(Context context, ArrayList<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        return new UserHolder(inflater.inflate(R.layout.layout_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User us = listUser.get(position);
        holder.txtMaUser.setText("Ma: " + us.getMaUser());
        holder.txtTenUser.setText("Ten: " + us.getTenUser());
        holder.txtNamSinh.setText("Nam Sinh: " + us.getNamSinh());
        Log.d("dk", "onBindViewHolder: ");
        Log.d("hhhhh", "onBindViewHolder: " + us.getMaUser() + " |" + us.getTenUser() + "|" + us.getNamSinh());

        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEdit(us);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDelete(us);
            }
        });
    }

    void dialogEdit(User us){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtMa = view.findViewById(R.id.edtMa);
        EditText edtTen = view.findViewById(R.id.edtTen);
        EditText edtNamSinh = view.findViewById(R.id.edtNamSinh);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        edtMa.setText(us.getMaUser());
        edtTen.setText(us.getTenUser());
        edtNamSinh.setText(us.getNamSinh());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMa.getText().toString();
                String ten = edtTen.getText().toString();
                String namSinh = edtNamSinh.getText().toString();

                if (ma.isEmpty() || ten.isEmpty() || namSinh.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                kiemTraTrung(ma, us.getDocumentId(), ten, namSinh, dialog);
            }
        });


    }

    private void updateUser(String documentId, String ma, String ten, String namSinh) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> updatedUser = new HashMap<>();
        updatedUser.put("maUser", ma);
        updatedUser.put("tenUser", ten);
        updatedUser.put("namSinh", namSinh);

        db.collection("user").document(documentId)
                .update(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Cập nhật thành công");

                    for (User u : listUser) {
                        if (u.getDocumentId().equals(documentId)) {
                            u.setMaUser(ma);
                            u.setTenUser(ten);
                            u.setNamSinh(namSinh);
                            break;
                        }
                    }
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Lỗi cập nhật", e));
    }

    private void kiemTraTrung(String ma, String idHienTai, String ten, String namSinh, AlertDialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .whereEqualTo("maUser", ma)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            boolean isDuplicate = true;
                            for (DocumentSnapshot document : task.getResult()) {
                                if (!document.getId().equals(idHienTai)) {
                                    isDuplicate = false;
                                    break;
                                }
                            }

                            if (isDuplicate) {
                                updateUser(idHienTai, ma, ten, namSinh);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Mã nhân viên đã tồn tại!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            updateUser(idHienTai, ma, ten, namSinh);
                            dialog.dismiss();
                        }
                    } else {
                        Log.e("Firebase", "Lỗi kiểm tra trùng mã", task.getException());
                    }
                });
    }

    private void dialogDelete(User us) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteUser(us.getDocumentId());
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.create().show();
    }

    private void deleteUser(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Xóa người dùng thành công");
                    for (int i = 0; i < listUser.size(); i++) {
                        if (listUser.get(i).getDocumentId().equals(documentId)) {
                            Toast.makeText(context, "Xoá thành công !! " + listUser.get(i).getMaUser(), Toast.LENGTH_SHORT).show();
                            listUser.remove(i);
                            break;
                        }
                    }
                    notifyDataSetChanged();  // Cập nhật lại RecyclerView
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Lỗi xóa người dùng", e);
                    Toast.makeText(context, "Xóa thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        TextView txtMaUser,txtTenUser,txtNamSinh;
        ImageView ivEdit,ivDelete;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            txtMaUser = itemView.findViewById(R.id.txtMaUser);
            txtTenUser = itemView.findViewById(R.id.txtTenUser);
            txtNamSinh = itemView.findViewById(R.id.txtNamSinh);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}

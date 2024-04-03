package com.example.cafe;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafe.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class dang_ky extends AppCompatActivity {

    Button bt_dk;
    TextInputEditText edt_name, edt_pass,edt_email,edt_pass2;
    ImageView img_back;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        anhxa();
        nutBack();
        nutDK();
        nutTVDN();




    }

    private void nutBack() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dang_ky.this,dang_nhap.class));
            }
        });
    }

    private void nutTVDN() {
    }

    private void nutDK() {
        bt_dk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = edt_name.getText().toString().trim();
                final String pass = edt_pass.getText().toString().trim();
                final String pass2 = edt_pass2.getText().toString().trim();
                final String email = edt_email.getText().toString().trim();

                // Kiểm tra xem các trường đã được điền đầy đủ chưa
                if (name.isEmpty() || pass.isEmpty() || pass2.isEmpty() || email.isEmpty()) {
                    Toast.makeText(dang_ky.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra định dạng email
                if (!isValidEmail(email)) {
                    Toast.makeText(dang_ky.this, "Email không hợp lệ, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem mật khẩu nhập lại có trùng với mật khẩu ban đầu không
                if (!pass.equals(pass2)) {
                    Toast.makeText(dang_ky.this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Truy vấn Firebase Firestore để kiểm tra xem đã có tài khoản nào có tên trùng lặp hay chưa
                db.collection("tk")
                        .document(name)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Tài khoản đã tồn tại
                                    Toast.makeText(dang_ky.this, "Tên người dùng đã tồn tại, vui lòng chọn tên khác", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Tài khoản chưa tồn tại, tiến hành đăng ký
                                    User tk = new User(name, pass, email);

                                    Map<String, Object> dt = new HashMap<>();
                                    dt.put("name", name);
                                    dt.put("email", email);
                                    dt.put("pass", pass);

                                    db.collection("tk")
                                            .document(name)
                                            .set(dt)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(dang_ky.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(dang_ky.this, dang_nhap.class));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(dang_ky.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(dang_ky.this, "Lỗi khi kiểm tra tên người dùng", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    private void anhxa() {
        //ánh xạ
        img_back = findViewById(R.id.imgback);
        bt_dk = findViewById(R.id.bttDangKy);
        edt_name = findViewById(R.id.dk_name);
        edt_pass = findViewById(R.id.dk_pass);
        edt_email = findViewById(R.id.dk_email);
        edt_pass2 = findViewById(R.id.dk_pass_2);
    }


    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

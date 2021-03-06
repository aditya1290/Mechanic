package com.example.mechanic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mechanic.dialogBox.CustomDialogBox;
import com.example.mechanic.model.Manager;
import com.example.mechanic.model.Mechanic;
import com.github.aakira.expandablelayout.ExpandableWeightLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import xyz.hasnat.sweettoast.SweetToast;

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;
    EditText registerName, registerEmail, registerPassword, registerPhone, registerEmpId, registerDepartment, registerDesignation;
    TextView attachFile;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference;
    FirebaseFunctions firebaseFunctions;
    ImageView image,image1;

    byte[] documentImage = null;
    UploadTask uploadTask;
    String imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        image = findViewById(R.id.image);
        image1 = findViewById(R.id.image1);

        registerButton = findViewById(R.id.registerButton1);
        registerName = findViewById(R.id.editTextName);
        registerPhone = findViewById(R.id.editTextPhone);
        registerEmail = findViewById(R.id.editTextEmail);
        registerPassword = findViewById(R.id.editTextPassword);
        registerEmpId = findViewById(R.id.editTextName1);
        registerDepartment = findViewById(R.id.editTextEmail1);
        registerDesignation = findViewById(R.id.editTextPassword1);
        attachFile = findViewById(R.id.attach_file);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();

        final ExpandableWeightLayout expandableLayout = (ExpandableWeightLayout) findViewById(R.id.expandableLayout);
        final ExpandableWeightLayout expandableLayout1 = (ExpandableWeightLayout) findViewById(R.id.expandableLayout1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!expandableLayout.isExpanded())
                {
                    image.animate().rotationBy(180).setDuration(200).start();
                    expandableLayout.toggle();
                    if(expandableLayout1.isExpanded()) {
                        expandableLayout1.collapse();
                        image1.animate().rotationBy(180).start();
                    }
                }
            }
        },1000);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.animate().rotationBy(180).setDuration(200).start();
                expandableLayout.toggle();
                if(expandableLayout1.isExpanded()) {
                    expandableLayout1.collapse();
                    image1.animate().rotationBy(180).start();
                }
            }
        });

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image1.animate().rotationBy(180).start();
                expandableLayout1.toggle();
                if(expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                    image.animate().rotationBy(180).setDuration(200).start();
                }
            }
        });

        attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 12);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomDialogBox dialogBox = new CustomDialogBox(RegisterActivity.this);
                dialogBox.show();

                final String userName = registerName.getText().toString();
                final String phone = registerPhone.getText().toString();
                final String email = registerEmail.getText().toString();
                final String password = registerPassword.getText().toString();
                final String empId = registerEmpId.getText().toString();
                final String department = registerDepartment.getText().toString();
                final String designation = registerDesignation.getText().toString();
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference("/idProof/"+registerEmpId.getText().toString());
                uploadTask = storageReference.putBytes(documentImage);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageLink = uri.toString();
                                Mechanic mechanic = new Mechanic(userName, email, null, null, 0, 0, 0, null, null,null,null,null,phone, password,empId,department,designation,imageLink);

                                userReference = firebaseDatabase.getReference("UnverifiedAccounts");
                                userReference.child("Mechanic").child(empId).setValue(mechanic);
                                dialogBox.dismiss();
                                SweetToast.success(RegisterActivity.this, "wait for verification");


                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogBox.dismiss();
                    }
                });

//                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                    if(task.isSuccessful())
//                    {
//                        user = auth.getCurrentUser();
//                        userReference = firebaseDatabase.getReference("Users");
//
//                        HashMap<String,String> data = new HashMap<>();
//
//                        data.put("claim","mechanic");
//                        data.put("email",user.getEmail());
//
//                        firebaseFunctions.getHttpsCallable("setCustomClaim")
//                            .call(data)
//                            .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
//                                @Override
//                                public void onSuccess(HttpsCallableResult httpsCallableResult) {
//
//                                    user = auth.getCurrentUser();
//                                    HashMap<String,String> hashMap = (HashMap<String, String>) httpsCallableResult.getData();
//                                    if(hashMap.get("status").equals("Successful"))
//                                    {
//                                        Mechanic mechanic = new Mechanic();
//                                        mechanic.setEmail(email);
//                                        mechanic.setUserName(userName);
//                                        mechanic.setUid(user.getUid());
//
//                                        userReference.child("Mechanic").child(user.getUid()).setValue(mechanic);
//                                        startActivity(new Intent(getApplicationContext(), BottomNavigationActivity.class));
//                                        finish();
//                                    }
//                                    else
//                                    {
//                                        user.delete();
//                                        Toast.makeText(RegisterActivity.this, "Some Error Occured \n Please try again", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                    }
//                    else
//                    {
//                        Toast.makeText(RegisterActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
//                    }
//                    }
//                });
            }
        });
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        changeStatusBarColor();
        finish();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.i("PRofile", "helo1");

                //dialogBox.show();

                Uri imageUri = data.getData();
                try {
                    ContentResolver cr = getBaseContext().getContentResolver();
                    InputStream inputStream = cr.openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    documentImage = baos.toByteArray();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        if (resultCode == 0) {
            Log.d("RESULT****", "CANCELLED");
        }
    }


}

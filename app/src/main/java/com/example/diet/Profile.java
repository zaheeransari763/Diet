package com.example.diet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends AppCompatActivity {
    CircleImageView profilePic;
    private TextView userFullname,  userEmail, userContact,userBmi;
    private DatabaseReference SettingUserRef;
    private FirebaseAuth mAuth;
    String currentUserId;

    Uri imageUri;

    private ProgressDialog loadingBar;
    StorageReference mImageStorage ;
    private StorageTask uploadTask;
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        profilePic = (CircleImageView) findViewById(R.id.userprofile);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        currentUserId = mAuth.getCurrentUser().getUid();
        SettingUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        userFullname = (TextView) findViewById(R.id.name);
        userEmail = (TextView) findViewById(R.id.email);
        userContact = (TextView) findViewById(R.id.phone);
        userBmi = (TextView) findViewById(R.id.bmiprofile);


        //DATA RETRIEVAL OF SELF PROFILE
        SettingUserRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userfullname = dataSnapshot.child("Fullname").getValue().toString();
                    String useremail = dataSnapshot.child("Email").getValue().toString();
                    String usercontact = dataSnapshot.child("Contact").getValue().toString();
                    String bmi=dataSnapshot.child("BMI").getValue().toString();

                    userFullname.setText(userfullname);
                    userContact.setText(usercontact);
                    userEmail.setText(useremail);
                    userBmi.setText(bmi);

                    final String image = dataSnapshot.child("image").getValue().toString();
                    if(!image.equals("default"))
                    {
                        Picasso.with(Profile.this).load(image).placeholder(R.drawable.profileimg).into(profilePic);
                        Picasso.with(Profile.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileimg).into(profilePic, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {

                            }

                            @Override
                            public void onError()
                            {
                                Picasso.with(Profile.this).load(image).placeholder(R.drawable.profileimg).into(profilePic);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && uploadTask!= null && uploadTask.isInProgress() && data != null && data.getData() != null)
            {
                Toast.makeText(this, "Upload In Progress", Toast.LENGTH_SHORT).show();
            }
            else
            {
                UloadImage();
            }
        }
    }

    private void UloadImage() {
        loadingBar = new ProgressDialog(Profile.this);
        loadingBar.setTitle("Uploading Image...");
        loadingBar.setMessage("Please wait while we upload and process the image.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if(imageUri != null)
        {
            String current_user_id = mAuth.getUid();
            final StorageReference fileReference = mImageStorage.child("profile_images").child(current_user_id + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        String current_uid = mAuth.getUid();
                        SettingUserRef = FirebaseDatabase.getInstance().getReference("Users").child(current_uid);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", mUri);
                        SettingUserRef.updateChildren(map);
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(Profile.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }

}

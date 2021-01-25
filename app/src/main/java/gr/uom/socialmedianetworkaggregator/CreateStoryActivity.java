package gr.uom.socialmedianetworkaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.ShareBuilder;
import com.facebook.share.model.ShareStoryContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class CreateStoryActivity extends AppCompatActivity {

    private static final String TAG ="Thanos" ;
    private Button uploadImageButton;
    private ImageView uploadImageView;
    private CheckBox facebookCheckBox;
    private CheckBox instagramCheckBox;
    private EditText descriptionText;
    private Button createStoryButton;

    private Uri imageUri;
    private Uri downloadUrl;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        uploadImageView =findViewById(R.id.uploadStoryImageView);

        facebookCheckBox=findViewById(R.id.facebookStoryCheckBox);
        instagramCheckBox=findViewById(R.id.instagramStoryCheckBox);

        createStoryButton =findViewById(R.id.uploadStoryButton);
        uploadImageButton = findViewById(R.id.uploadStoryImageButton);


        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);

            }
        });


        createStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Create Post button clicked!");

                if((uploadImageView.getDrawable()!=null)
                        &&(facebookCheckBox.isChecked()||instagramCheckBox.isChecked())){

                    if(facebookCheckBox.isChecked()){
                        // Define photo or video asset URI

                        String appId = "200755704911190"; // This is your application's FB ID

// Instantiate implicit intent with ADD_TO_STORY action
                        Intent intent = new Intent("com.facebook.stories.ADD_TO_STORY");
                        intent.setDataAndType(imageUri, "image/jpeg");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", appId);

// Instantiate activity and verify it will resolve implicit intent
                        startActivityForResult(intent, 0);

                    }
                    if(instagramCheckBox.isChecked()){

                        String sourceApplication = "gr.uom.socialmedianetworkaggregator";

// Instantiate implicit intent with ADD_TO_STORY action and background asset
                        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
                        intent.putExtra("source_application",sourceApplication);

                        intent.setDataAndType(imageUri, "image/jpeg");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivityForResult(intent, 0);

                    }

                }else{
                    Log.d(TAG,"Create Post button something is missing");
                    Toast.makeText(CreateStoryActivity.this,"Something is missing",Toast.LENGTH_SHORT);
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//                StorageReference userReference = storageReference.child()
                imageUri = data.getData();

                StorageReference userReference = storageReference.child("images/stories/"+imageUri.getLastPathSegment());
                UploadTask uploadTask;
                uploadTask=userReference.putFile(imageUri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d(TAG, "Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(@NonNull UploadTask.TaskSnapshot snapshot) {
                        Log.d(TAG, "Upload is paused");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Upload to firebase failed "+e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        userReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl=uri;


                            }
                        });
                    }
                });


                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                uploadImageView.setImageBitmap(selectedImage);
                uploadImageView.setTag(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            Log.d(TAG, "result code:"+resultCode);
        }
    }
}
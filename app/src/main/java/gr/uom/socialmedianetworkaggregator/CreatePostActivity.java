package gr.uom.socialmedianetworkaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.Share;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareMedia;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
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

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG ="Thanos" ;
    private Button uploadImageButton;
    private ImageView uploadImageView;
    private CheckBox twitterCheckBox;
    private CheckBox facebookCheckBox;
    private CheckBox instagramCheckBox;
    private EditText descriptionText;
    private Button createPostButton;

    private Uri downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        uploadImageView =findViewById(R.id.uploadImageView);

        twitterCheckBox=findViewById(R.id.twitterCheckBox);
        facebookCheckBox=findViewById(R.id.facebookCheckBox);
        instagramCheckBox=findViewById(R.id.instagramCheckBox);

        descriptionText=findViewById(R.id.descriptionEditText);

        createPostButton=findViewById(R.id.uploadPostButton);
        uploadImageButton = findViewById(R.id.uploadAnImageButton);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);

            }
        });

        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Create Post button clicked!");

                if((descriptionText.getText()!=null || uploadImageView.getDrawable()!=null)
                        &&(twitterCheckBox.isChecked()||facebookCheckBox.isChecked()||instagramCheckBox.isChecked())){

                    if(twitterCheckBox.isChecked()){
                        Log.d(TAG, "twitterCheckBox is checked");
                        try {
                            if(uploadImageView.getDrawable()==null)
                                AppUser.getTwitterUser().createPost(descriptionText.getText().toString(), "");
                        } catch (UnsupportedEncodingException e) {
                            Log.d(TAG,e.toString());
                        } catch (GeneralSecurityException e) {
                            Log.d(TAG,e.toString());
                        }
                    }
                    if(facebookCheckBox.isChecked()){
                        if(uploadImageView.getDrawable()==null) {
                            AppUser.getFbInstaUSer().createFbPost(descriptionText.getText().toString(), null);
                        }else if(descriptionText.getText()==null){
                            AppUser.getFbInstaUSer().createFbPost(null,downloadUrl.toString());
                        }else AppUser.getFbInstaUSer().createFbPost(descriptionText.getText().toString(),downloadUrl.toString());
                    }
                    if(instagramCheckBox.isChecked()){
                        AppUser.getFbInstaUSer().createInstaPost(descriptionText.getText().toString(), uploadImageView.getTag().toString());
                    }

                }else{
                    Log.d(TAG,"Create Post button something is missing");
                    Toast.makeText(CreatePostActivity.this,"Something is missing",Toast.LENGTH_SHORT);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//                StorageReference userReference = storageReference.child()
                final Uri imageUri = data.getData();

                StorageReference userReference = storageReference.child("images/"+imageUri.getLastPathSegment());
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
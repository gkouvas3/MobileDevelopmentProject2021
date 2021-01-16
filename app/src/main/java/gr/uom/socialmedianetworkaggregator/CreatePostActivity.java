package gr.uom.socialmedianetworkaggregator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
                        AppUser.getFbInstaUSer().createFbPost(descriptionText.getText().toString(), uploadImageView.getTag().toString());
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
                final Uri imageUri = data.getData();
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
        }
    }
}
package com.example.paras.methopedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.paras.methopedia.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class upload extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 1;

    private static final String server_url = "http://192.168.1.1/SavePicture";


    ImageView imageToUpload, downloadedImage;
    Button bUploadImage, bDownloadImage;
    EditText uploadImageName, downloadImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);

        bUploadImage = (Button) findViewById(R.id.bUploadImage);

        uploadImageName = (EditText) findViewById(R.id.etUploadName);
        downloadImageName = (EditText) findViewById(R.id.etDownloadName);

        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.bUploadImage:
                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
                InsertCV(image,uploadImageName.getText().toString() );
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }


    private void InsertCV(Bitmap image, final String name) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(upload.this, response, Toast.LENGTH_LONG).show();

                        TextView textView1 = (TextView) findViewById(R.id.textView1);

                        textView1.setText(response);

                        //textView1.setText(response);

                        //Intent intent3 = new Intent();
                        //intent3.putExtra("TextView", response);
                        //intent3.setClass(upload.this, download.class);
                        //startActivity(intent3);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("error",error.networkResponse.toString());
                Toast.makeText(upload.this, "allbad", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key1", encodedImage);
                params.put("key2", name);
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
}

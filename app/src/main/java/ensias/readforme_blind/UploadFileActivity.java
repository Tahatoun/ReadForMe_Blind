package ensias.readforme_blind;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import ensias.readforme_blind.dao.Auth;
import ensias.readforme_blind.dao.SharedData;
import ensias.readforme_blind.dao.Util;
import ensias.readforme_blind.databinding.ActivityMainBinding;
import ensias.readforme_blind.databinding.ActivityUploadFileBinding;
import ensias.readforme_blind.model.Blind;
import es.voghdev.pdfviewpager.library.util.FileUtil;
import retrofit2.Call;

public class UploadFileActivity extends AppCompatActivity {
    private  ActivityUploadFileBinding binding;
    private  UploadFileService uploadFileService;
    private  boolean serviceBound=false;
    private  Call<Blind> profileCall;
    private  String currentTime;
    private  Blind blind;
    private  Activity activity;
    private Intent serviceIntent;
    private  Auth authManager;
    private  Uri uploadFileUri;
    private  String path;
    private  File uploadFile=null;
    private String FilePathStr=null;
    private static final int REQUEST_PERMISSION = 200;
    private boolean permissionAccepted = false;
    private String [] permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
                permissionAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionAccepted ) finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBindingUtil.setContentView(this,R.layout.activity_upload_file);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_file);
        activity = this;
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
        currentTime=System.currentTimeMillis()+"";

        uploadFile = new File(getFilesDir().getAbsolutePath()+"/i@"+currentTime+".pdf");

        if (!uploadFile.exists()) {
            try {
                uploadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        authManager= Auth.getInstance(getSharedPreferences("token_prefs", MODE_PRIVATE));
        blind = SharedData.getBlind(this);
        if(blind == null) blind = new Blind();
        if(authManager.getToken() == null){
            startActivity(new Intent(UploadFileActivity.this, LoginActivity.class));
            finish();
        }

        binding.importFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_pdf_file)), 1);

            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceIntent = new Intent( UploadFileActivity.this , UploadFileService.class);
                serviceIntent.putExtra("file", uploadFile);
                startService(serviceIntent);
                bindService(serviceIntent, uploadFileServiceConnection, Context.BIND_AUTO_CREATE);
                finish();
            }
        });


        
    }

    /*========================================================= SERVICE ================================*/
    private ServiceConnection uploadFileServiceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UploadFileService.UploadFileServiceBinder binder = (UploadFileService.UploadFileServiceBinder)service;
            //get service
            uploadFileService= binder.getService();

            if (uploadFileService!=null){
                serviceBound=true;
                uploadFileService.setUploadedFile(FilePathStr);

                uploadFileService.setName(binding.editTextTitle.getText().toString());
                uploadFileService.setDescription(binding.editTextDescription.getText().toString());

                uploadFileService.setNOTIFICATION_ID(Integer.parseInt(currentTime.substring(currentTime.length()-9)));
                Intent intent=new Intent(UploadFileService.START_UPLOAD);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (uploadFileServiceConnection != null) {
            try {
                unbindService(uploadFileServiceConnection);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() !=null) {
            uploadFileUri = data.getData();

            String uriString = uploadFileUri.toString();

            uploadFile = new File(uriString);
            path = uploadFile.getAbsolutePath();
            try {
                FilePathStr= Util.getPath(activity,uploadFileUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String displayName = null;
            String [] filePath={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.DISPLAY_NAME};

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = activity.getContentResolver().query(uploadFileUri, filePath, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                         displayName = cursor.getString(cursor.getColumnIndex(filePath[1]));
                    }
                } finally {
                    cursor.close();
                }

            } else if (uriString.startsWith("file://")) {
                displayName = uploadFile.getName();
            }

            if (displayName!=null){
                binding.fileName.setText(displayName);
            }else{
                binding.fileName.setText(R.string.no_file);
            }
        }

    }
}

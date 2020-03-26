package ensias.readforme_blind;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import ensias.readforme_blind.dao.Auth;
import ensias.readforme_blind.dao.SharedData;
import ensias.readforme_blind.dao.Util;
import ensias.readforme_blind.databinding.ActivityMainBinding;
import ensias.readforme_blind.model.Blind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityMainBinding binding;
    Call<Blind> profileCall;
    Blind blind;
	Blind blind2;
    Activity activity;
    Auth authManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activity = this;
        authManager= Auth.getInstance(getSharedPreferences("token_prefs", MODE_PRIVATE));
        blind = SharedData.getBlind(this);
        if(blind == null) blind = new Blind();
        if(authManager.getToken() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        profileCall = Util.getBlindService(this).getAuthBlind("files");
        profileCall.enqueue(new Callback<Blind>() {
            @Override
            public void onResponse(Call<Blind> call, Response<Blind> response) {
                if(response.isSuccessful()){
                    try {
                        blind.setId(response.body().getId());
                        blind.setLastName(response.body().getLastName());
                        blind.setFirstName(response.body().getFirstName());
                        blind.setEmail(response.body().getEmail());
                        blind.setBirthdate(response.body().getBirthdate());
                        SharedData.saveBlindInSharedPref(blind.toString(), MainActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Application Error "+response.code(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Application Error "+response.code(), Toast.LENGTH_SHORT).show();
                    authManager.deleteToken();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Blind> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                t.printStackTrace();
                System.out.println(call.toString());
            }
        });
        binding.myFiles.setOnClickListener(this);
        binding.newFile.setOnClickListener(this);
        binding.allFiles.setOnClickListener(this);
        binding.myProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FilenameFilter audioFilter = new FilenameFilter() {
            File f;
            public boolean accept(File dir, String name) {
                if(name.endsWith(".mp3") || name.endsWith(".MP3")) {
                    return true;
                }
                f = new File(dir.getAbsolutePath()+"/"+name);
                return f.isDirectory();
            }
        };
        Intent intent = null;
        switch (v.getId()) {
            case R.id.myProfile:  intent = new Intent(MainActivity.this,LoginActivity.class); break;
            case R.id.newFile:  intent = new Intent(MainActivity.this,UploadFileActivity.class); break;
            case R.id.allFiles: intent = new Intent(MainActivity.this, FilesActivity.class);  break;
            case R.id.myFiles:  intent = new Intent(MainActivity.this,MyFilesActivity.class); break;

        }
        if(intent != null){
            intent.putExtra("Blind",blind);
            startActivityForResult(intent, 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra("currentBlind")) {
                    Blind currentBlind = (Blind) data.getExtras().getSerializable("currentBlind");

                }
            }

        }
    }
}

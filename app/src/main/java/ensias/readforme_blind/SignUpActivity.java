package ensias.readforme_blind;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.dd.processbutton.iml.ActionProcessButton;

import ensias.readforme_blind.dao.Auth;
import ensias.readforme_blind.dao.RetrofitBuilder;
import ensias.readforme_blind.dao.SharedData;
import ensias.readforme_blind.dao.UserInterface;
import ensias.readforme_blind.dao.Util;
import ensias.readforme_blind.databinding.ActivitySignUpBinding;
import ensias.readforme_blind.model.AccessToken;
import ensias.readforme_blind.model.Blind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    AwesomeValidation validator;
    Call<AccessToken> tokenService;
    UserInterface retrofitService;
    Auth authToken;
    Blind blind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this,R.layout.activity_sign_up);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);


        retrofitService= RetrofitBuilder.createService(UserInterface.class);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        authToken = Auth.getInstance(getSharedPreferences("token_prefs", MODE_PRIVATE));
        Rules();
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final ActionProcessButton btnUpload = (ActionProcessButton) findViewById(R.id.buttonSignup);
        btnUpload.setProgress(0);
        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   binding.editTextUsername.setError(null);
                binding.editTextEmail.setError(null);
                binding.editTextPassword.setError(null);
                validator.clear();
                binding.notifyChange();
                if(validator.validate()) {
                    btnUpload.setProgress(50);
                    binding.executePendingBindings();
                    //    Toast.makeText(Signup.this, binding.editTextUsername.getText().toString(), Toast.LENGTH_SHORT).show();
                    //    String username=binding.editTextUsername.getText().toString();
                    //     String firstName=binding.editTextFirstName.getText().toString();
                    //      String lastName=binding.editTextLastName.getText().toString();
                    String email=binding.editTextEmail.getText().toString();
                    String password=binding.editTextPassword.getText().toString();
                    String firstName=binding.editTextFirstName.getText().toString();
                    String lastName=binding.editTextLastName.getText().toString();
                    String code=binding.editTextCode.getText().toString();
                    //      int gender= getGender();

                    //tokenService=retrofitService.signUp(firstName, lastName,username,email,password,gender );
                    //tokenService=retrofitService.signupEmail(email,password);
                    tokenService=retrofitService.signUp(email,password,firstName,lastName,code);

                    tokenService.enqueue(new Callback<AccessToken>() {
                        @Override
                        public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                            if (response.isSuccessful()) {
                                try {
                                    authToken.saveToken(response.body());
                                    Call<Blind>  profileCall = Util.getBlindService(getApplicationContext()).getAuthBlind("");
                                    profileCall.enqueue(new Callback<Blind>() {
                                        @Override
                                        public void onResponse(Call<Blind> call, Response<Blind> response) {
                                            if(response.isSuccessful()){
                                                try {
                                                    btnUpload.setProgress(100);
                                                    blind = new Blind();
                                                    blind.setId(response.body().getId());
                                                    blind.setEmail(response.body().getEmail());
                                                    SharedData.saveBlindInSharedPref(blind.toString(),  SignUpActivity.this);
                                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                    finish();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(SignUpActivity.this, "Application Error "+ response.code(), Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Blind> call, Throwable t) {
                                            Toast.makeText(SignUpActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                                            t.printStackTrace();
                                            System.out.println(call.toString());
                                        }
                                    });

                                }catch (Exception e){
                                    btnUpload.setProgress(-1);
                                    e.printStackTrace();
                                    Toast.makeText(SignUpActivity.this, "Application Error", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                btnUpload.setProgress(-1);
                                Toast.makeText(SignUpActivity.this, "Network Error " + response.code(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(Signup.this, call.request().toString(), Toast.LENGTH_SHORT).show();
                                if (response.code()==422){
                                    Toast.makeText(SignUpActivity.this, "Email taken", Toast.LENGTH_LONG).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<AccessToken> call, Throwable t) {
                            btnUpload.setProgress(-10);
                            Toast.makeText(SignUpActivity.this, "Network Error "+call.request().url(), Toast.LENGTH_LONG).show();
                            Toast.makeText(SignUpActivity.this, call.request().toString(), Toast.LENGTH_SHORT).show();
                            Log.e("err",t.getLocalizedMessage(),t);
                        }
                    });
                }
            }
        });

        /*if(authToken.getToken().getAccessToken() != null){
            startActivity(new Intent(Signup.this,myProfile.class));
            finish();
        }*/
    }

    public void Rules(){
        //    validator.addValidation(this, binding.editTextUsername.getId(), RegexTemplate.NOT_EMPTY, R.string.username_error);
        validator.addValidation(this, binding.editTextEmail.getId(), Patterns.EMAIL_ADDRESS, R.string.email_error);
        validator.addValidation(this, binding.editTextPassword.getId(), "[a-zA-Z0-9]{6,}", R.string.password_error);
        //  validator.addValidation(this, binding.editTextFirstName.getId(), RegexTemplate.NOT_EMPTY, R.string.fname_uncorrect);
        //   validator.addValidation(this, binding.editTextLastName.getId(), RegexTemplate.NOT_EMPTY, R.string.lname_incorrect);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }

    public int getGender() {
  /*      if(binding.radioButtonNot.isChecked()) return 0;
        if(binding.radioButtonMale.isChecked()) return 1;
        if(binding.radioButtonFemale.isChecked()) return 2;*/
        return 0;
    }
}

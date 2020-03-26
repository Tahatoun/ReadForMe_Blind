package ensias.readforme_blind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

import ensias.readforme_blind.dao.Auth;
import ensias.readforme_blind.dao.SharedData;
import ensias.readforme_blind.dao.Util;
import ensias.readforme_blind.databinding.ActivityFilesBinding;
import ensias.readforme_blind.databinding.ActivityMyFilesBinding;
import ensias.readforme_blind.listAdapter.FileAdapter;
import ensias.readforme_blind.model.Blind;
import ensias.readforme_blind.model.File;
import ensias.readforme_blind.model.Home;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFilesActivity extends AppCompatActivity {
    ActivityMyFilesBinding binding;
    Call<Home> profileCall;
    Call<Blind> profileCall2;
    ArrayList<File> posts;
    Home home;
	Home home2;
    Blind blind;
    FileAdapter adapter;
    Activity activity;
    Auth authManager;
    public View ftView;
    boolean loadingMore=false;
    boolean search=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this,R.layout.activity_my_files);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_files);
        home=new Home();
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.footer_view, null);

        posts=new ArrayList<>();
        activity = this;
        authManager=Auth.getInstance(getSharedPreferences("token_prefs", MODE_PRIVATE));
        blind = (Blind) getIntent().getSerializableExtra("blind");
        if(blind == null){
            blind = SharedData.getBlind(this);
            if(blind == null) blind = new Blind();
        }
        if(blind.getFirstName() == null) blind.setFirstName("  ");
        if(blind.getLastName() == null) blind.setLastName("  ");
        if(authManager.getToken() == null){
            startActivity(new Intent(MyFilesActivity.this, LoginActivity.class));
            finish();
        }

        adapter = new FileAdapter(activity,R.layout.file_item, blind,posts);
        binding.listViewPosts.setAdapter(adapter);
        profileCall2 = Util.getBlindService(this).getAuthBlind("");

        profileCall2.enqueue(new Callback<Blind>() {
            @Override
            public void onResponse(Call<Blind> call, Response<Blind> response) {
                if(response.isSuccessful()){
                    try {
                        blind.setId(response.body().getId());
                        blind.setLastName(response.body().getLastName());
                        blind.setFirstName(response.body().getFirstName());
                        blind.setEmail(response.body().getEmail());
                        blind.setBirthdate(response.body().getBirthdate());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MyFilesActivity.this, "Application Error "+response.code(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    authManager.deleteToken();
                    startActivity(new Intent(MyFilesActivity.this, LoginActivity.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Blind> call, Throwable t) {
                Toast.makeText(MyFilesActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                t.printStackTrace();
                System.out.println(call.toString());
            }
        });

        binding.listViewPosts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //what is the bottom item that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;

                //is the bottom item file visible & not loading more ? Load more !

                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    //if the last page equal to current page ? no more data else load more
                    if (!(search)) {
                        if (home.getCurrent_page() == home.getLast_page()) {
                            Toast.makeText(MyFilesActivity.this, "No More Files ", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingMore = true;
                            binding.listViewPosts.addFooterView(ftView);
                            homeContent(home.getNextPage());
                        }
                    } else {

                    }
                }
            }
        });

        binding.listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                Intent intent= new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("file", posts.get(i));
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_bar, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);


        SearchView searchView = (SearchView)searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                binding.listViewPosts.addFooterView(ftView);
                if (TextUtils.isEmpty(s)){
                    adapter.filter("");
                    binding.listViewPosts.clearTextFilter();
                }
                else {
                    adapter.filter(s);
                }
                binding.listViewPosts.removeFooterView(ftView);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                search=true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                search=false;
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()) {

            case R.id.newFileItem: intent=new Intent(MyFilesActivity.this,UploadFileActivity.class); break;
            case R.id.allFilesItem: intent=new Intent(MyFilesActivity.this,FilesActivity.class); break;
            case R.id.myFilesItem: intent=new Intent(MyFilesActivity.this,MyFilesActivity.class); break;
            case R.id.myProfileItem: intent=new Intent(MyFilesActivity.this,MyFilesActivity.class); break;
            case R.id.logoutItem:
                authManager.deleteToken();
                intent=new Intent(MyFilesActivity.this,LoginActivity.class);
                break;

            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        if (intent!=null){
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    protected void homeContent(int pageIndex){
        profileCall = Util.getBlindService(this).getBlindFiles(pageIndex);
        profileCall.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(Call<Home> call, Response<Home> response) {
                if(response.isSuccessful()){
                    try {
                        posts.addAll(posts.size(),response.body().getData());
                        home=response.body();
                        adapter.notifyDataSetChanged();
                        loadingMore=false;
                        binding.listViewPosts.removeFooterView(ftView);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MyFilesActivity.this, "Application Error "+response.code(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.v("posts home",response.code()+" "+response.toString());
                }
            }
            @Override
            public void onFailure(Call<Home> call, Throwable t) {
                Toast.makeText(MyFilesActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                t.printStackTrace();
                System.out.println(call.toString());
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}


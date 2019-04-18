package ensias.readforme_blind;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ensias.readforme_blind.dao.Auth;
import ensias.readforme_blind.dao.SharedData;
import ensias.readforme_blind.dao.Util;
import ensias.readforme_blind.listAdapter.TrackAdapter;
import ensias.readforme_blind.model.Blind;
import ensias.readforme_blind.model.File;
import ensias.readforme_blind.model.Track;

import ensias.readforme_blind.databinding.ActivityPlayerBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity implements  SeekBar.OnSeekBarChangeListener {
    ActivityPlayerBinding binding;

    private  MediaPlayerService musicSrv;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private Intent playIntent;
    Auth authManager;
    private Track currentTrack;
    private boolean isRepeat = false;
    private static   boolean musicBound = false;
    private File  file;
    private Call<File> profileCall2;
    private int currentFileIndex;
    private TrackAdapter adapter;
    private Blind blind;
    private Activity activity;
    private ArrayList<Track> playList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.seekBarSong.setOnSeekBarChangeListener(this);
        activity=this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(MediaPlayerService.PREV_ACTION);
        filter.addAction(MediaPlayerService.PLAY_ACTION);
        filter.addAction(MediaPlayerService.NEXT_ACTION);
        filter.addAction(MediaPlayerService.UPDATE_SECONDARY_SEEKBAR);
        filter.addAction(MediaPlayerService.TIME_UPDATE);
        filter.addAction(MediaPlayerService.START_UPDATE);

        registerReceiver(receiver,filter);
        binding.seekBarSong.setOnSeekBarChangeListener(this);

        authManager=Auth.getInstance(getSharedPreferences("token_prefs", MODE_PRIVATE));
        blind = (Blind) getIntent().getSerializableExtra("blind");
        if(blind == null){
            blind = SharedData.getBlind(this);
            if(blind == null) blind = new Blind();
        }
        if(blind.getFirstName() == null) blind.setFirstName("  ");
        if(blind.getLastName() == null) blind.setLastName("  ");
        if(authManager.getToken() == null){
            startActivity(new Intent(PlayerActivity.this, LoginActivity.class));
            finish();
        }

        playIntent=null;
        file=new File();

        file =(File) getIntent().getExtras().get("file");


        Toast.makeText(activity, "id "+file.getId(), Toast.LENGTH_SHORT).show();
        adapter = new TrackAdapter(activity,R.layout.track_item,playList);
        profileCall2 = Util.getFileService(this).getFileWithIncludes(file.getId(),"tracks");

        binding.listViewTrack.setAdapter( adapter);

        // By default play first song
        //playSong(0);


        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        binding.imageViewPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                playPause();

            }
        });

        binding.imageViewNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                Next();

            }
        });
        binding.imageViewPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Previous();

            }
        });

        binding.imageViewRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    binding.imageViewRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    //isShuffle = false;
                    binding.imageViewRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                    binding.imageViewShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
            }
        });

        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        binding.imageViewShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(musicSrv.isShuffle()){
                    musicSrv.setShuffle();
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    binding.imageViewShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }else{
                    // make repeat to true
                    musicSrv.setShuffle();
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    binding.imageViewShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                    binding.imageViewRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                }
            }
        });

    }

    private  void playPause(){
        if(musicSrv.isPlaying()){
            if(musicSrv!=null){
                musicSrv.pause();
                // Changing button image to play button
                binding.imageViewPlay.setImageResource(R.drawable.ic_play_arrow);
            }
        }else{
            // Resume song
            if(musicSrv!=null){
                musicSrv.start();
                // Changing button image to pause button
                binding.imageViewPlay.setImageResource(R.drawable.ic_pause_black_24dp);
            }
        }
    }

    private void Next(){
        if (musicSrv!=null&& musicBound)
            musicSrv.playNext();
    }

    private void Previous(){
        if (musicSrv!=null&&musicBound)
            musicSrv.playPrev();
    }

    @Override
    public void onBackPressed() {
        if(musicSrv != null){
            if(musicSrv.isPlaying()){
                musicSrv.stop();
            }
            // musicSrv.release();
            musicSrv.removeHandler();
        }
        if (musicConnection != null) {
            try{
                unbindService(musicConnection);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        finish();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            switch (intent.getAction()){
                case MediaPlayerService.PLAY_ACTION:
                    if(musicSrv.isPlaying()){
                        binding.imageViewPlay.setImageResource(R.drawable.ic_play_arrow);

                    }else{
                        binding.imageViewPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                    }
                    break;
                case MediaPlayerService.UPDATE_SECONDARY_SEEKBAR:
                    int percent=intent.getExtras().getInt("percent");
                    binding.seekBarSong.setSecondaryProgress(percent);
                    break;
                case MediaPlayerService.TIME_UPDATE:

                    long totalDuration = musicSrv.getDuration();
                    long currentDuration = musicSrv.getCurrentPosition();
                    binding.textViewDuration.setText(""+ Util.milliSecondsToTimer(totalDuration));
                    // Displaying time completed playing
                    binding.textViewCurrentDuration.setText(""+Util.milliSecondsToTimer(currentDuration));
                    // Updating progress bar
                    int progress = (int)(Util.getProgressPercentage(currentDuration, totalDuration));
                    binding.seekBarSong.setProgress(progress);
                    break;
                case MediaPlayerService.START_UPDATE:
                    currentTrack=musicSrv.getCurrentTrack();
                    binding.setTrack(currentTrack);
                    binding.imageViewPlay.setImageResource(R.drawable.ic_pause_black_24dp);

                    break;
            }

        }
    };

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            int currentTime;
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder)service;
            //get service

            musicSrv = binder.getService();
            //pass list
            if(getIntent().getAction()=="notification"){
                musicSrv.removeHandler();
                //playList= (ArrayList<Track>) getIntent().getExtras().get("playlist");
                currentSongIndex=getIntent().getExtras().getInt("currentSong");
                currentTime=getIntent().getExtras().getInt("currentTime");
                musicSrv.setList(playList);
                musicSrv.setSong(currentSongIndex);
                musicSrv.setCurrentTime(currentTime);
               // musicSrv.playSong();
                musicBound = true;

            }else{
                if(musicSrv!=null) {
                    musicSrv.setList(playList);
                    musicSrv.setSong(currentSongIndex);
                    musicSrv.setCurrentTime(0);
                    //musicSrv.playSong();
                    musicBound = true;
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MediaPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }else{
            currentTrack=musicSrv.getCurrentTrack();
            musicSrv.updateProgressBar();
            binding.setTrack(currentTrack);
            musicBound=true;
            if (musicSrv.isPlaying()){
                binding.imageViewPlay.setImageResource(R.drawable.ic_play_arrow);
            }else {
                binding.imageViewPlay.setImageResource(R.drawable.ic_pause_black_24dp);

            }
        }
        profileCall2.enqueue(new Callback<File>() {
            @Override
            public void onResponse(Call<File> call, Response<File> response) {
                if(response.isSuccessful()){
                    try {
                        file.setId(response.body().getId());
                        file.setName(response.body().getName());
                        file.setTracks(response.body().getTracks());
                        playList.addAll(0,file.getTracks());
                        adapter.notifyDataSetChanged();
                        if (playList.size()>0) {
                            Toast.makeText(activity, "size "+playList.size(), Toast.LENGTH_SHORT).show();
                            currentTrack = playList.get(0);
                            binding.setTrack(currentTrack);

                            musicSrv.playSong();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PlayerActivity.this, "Application Error ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    authManager.deleteToken();
                    startActivity(new Intent(PlayerActivity.this, LoginActivity.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<File> call, Throwable t) {
                Toast.makeText(PlayerActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                t.printStackTrace();
                System.out.println(call.toString());
            }
        });
    }


    /**
     * Receiving song index from playlist view
     * and play the song
     * */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }
*/
    }

    /*
    public void  playSong(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
           // mp.setDataSource(this,Uri.parse(playList.get(songIndex).getUrlTrack()));
            mp.setDataSource("http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3");
            mp.setOnPreparedListener(this);
            mp.prepareAsync();
            //mp.start();
            // Displaying Song title
            binding.setTrack(playList.get(songIndex));

            // Changing Button Image to pause image
            binding.imageViewPlay.setImageResource(R.drawable.ic_pause_black_24dp);
            Toast.makeText(getApplicationContext(),Uri.parse(playList.get(songIndex).getUrlTrack()).toString(),Toast.LENGTH_LONG).show();
            // set Progress bar values
            binding.seekBarSong.setProgress(0);
            binding.seekBarSong.setMax(100);

            // Updating progress bar
            //updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

/**
 * Update timer on seekbar
 * */



    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        musicSrv.removeHandler();
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        musicSrv.removeHandler();
        int totalDuration = musicSrv.getDuration();
        int currentPosition = Util.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        musicSrv.seekTo(currentPosition);

        // update timer progress again
        musicSrv.updateProgressBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(musicSrv!=null){
            //musicSrv.removeHandler();
            //  unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (musicConnection!=null){
            try {
                unbindService(musicConnection);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (receiver!=null){
            try {
                unregisterReceiver(receiver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
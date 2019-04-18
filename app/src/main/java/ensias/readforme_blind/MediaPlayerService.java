package ensias.readforme_blind;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import ensias.readforme_blind.model.Track;

/**
 * Created by Idriss on 19/07/2017.
 */

public class MediaPlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener{

    public static final String PLAY_ACTION="play_action";
    public static final String NEXT_ACTION="next_action";
    public static final String PREV_ACTION="prev_action";
    public static final String CLOSE_ACTION="close_action";
    public static final String UPDATE_SECONDARY_SEEKBAR="update_secondary_seekbar";
    public static final String START_UPDATE="start_update";
    public static final String TIME_UPDATE="time_update";
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private  Intent notification_intent;
    private static final int notification_id = 1564846546;
    private RemoteViews remoteViews;
    private Context context;
    private int currentTime;
    private MediaPlayer player;
    private ArrayList<Track> songs;
    private int currentSongIndex ;
    private Track currentTrack;
    private final IBinder musicBind = new MusicBinder();
    private boolean shuffle=false;
    private boolean repeat=false;
    private boolean prepared=false;
    private Random rand;
    private Handler mHandler = new Handler();

    public void onCreate(){

        //create the service
        super.onCreate();
        context = this;
      /*  notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        remoteViews = new RemoteViews(getPackageName(),R.layout.player_notification);*/
        currentSongIndex =0;
        rand=new Random();
        player = new MediaPlayer();
        Log.i("service-bind", "created successfully!");
        initMusicPlayer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CLOSE_ACTION);
        filter.addAction(PLAY_ACTION);
        filter.addAction(NEXT_ACTION);
        filter.addAction(PREV_ACTION);

        registerReceiver(receiver,filter);
    }

    public void initMusicPlayer(){
        //set player properties
        //player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnBufferingUpdateListener(this);
    }
    public void stop(){
        player.stop();
    }
    public void release(){
        player.release();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        //    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            switch (intent.getAction()){
                case PLAY_ACTION:
                    if (player!=null){
                        if (player.isPlaying()){
                            player.pause();
                            removeHandler();
                            remoteViews.setImageViewResource(R.id.imageViewPlayNot,android.R.drawable.ic_media_play);
                        }else{
                            remoteViews.setImageViewResource(R.id.imageViewPlayNot,android.R.drawable.ic_media_pause);
                            updateProgressBar();
                            player.start();
                        }
                    }else{
                       // manager.cancel(intent.getExtras().getInt("id"));
                    }
                    builder.setCustomBigContentView(remoteViews);
                    notificationManager.notify(notification_id,builder.build());
                    break;
                case CLOSE_ACTION:
                    //manager.cancel(intent.getExtras().getInt("id"));
                    break;
                case MediaPlayerService.PREV_ACTION:
                   playPrev();
                    break;
                case MediaPlayerService.NEXT_ACTION:
                    playNext();
                    break;
            }

        }
    };

    //pass song list
    public void setList(ArrayList<Track> theSongs){
        songs=theSongs;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Intent intent=new Intent(UPDATE_SECONDARY_SEEKBAR);
        intent.putExtra("percent",i);
        sendBroadcast(intent);

    }



    //binder
    public class MusicBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        /*player.stop();
        player.release();*/
        return false;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return  START_STICKY;
    }

    //play a song
    public void playSong(){
        removeHandler();
        prepared=false;
        try {
            initMusicPlayer();
            currentTrack=songs.get(currentSongIndex);
            System.out.println(currentTrack);
            if (player!=null&&player.isPlaying())
                player.stop();
            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            System.out.println(currentTrack.getUrlTrack());
            player.setDataSource(this,Uri.parse(currentTrack.getUrlTrack()));
            //Toast.makeText(context, currentTrack.getUrlTrack(), Toast.LENGTH_SHORT).show();
            player.setOnPreparedListener(this);
            player.prepareAsync();
            Intent intent=new Intent(START_UPDATE);
            sendBroadcast(intent);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            Toast.makeText(context, "Can't get the song from the server", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //set the song
    public void setSong(int songIndex){
        currentSongIndex =songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if(player.getCurrentPosition()>0){
            removeHandler();
            mp.reset();
            playNext();
            currentTime=0;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();

        //notification
        prepared=true;
        if(mp.isPlaying()) {
            mp.seekTo(currentTime);
            updateProgressBar();
          //  notification();
        }
    }

    public void notification(){
        remoteViews.setImageViewResource(R.id.imageViewCoverNot,R.mipmap.ic_cover);
        remoteViews.setImageViewResource(R.id.imageViewCloseNot,R.drawable.ic_close);
        remoteViews.setImageViewResource(R.id.imageViewBackNot, android.R.drawable.ic_media_previous);
        remoteViews.setImageViewResource(R.id.imageViewForwNot,android.R.drawable.ic_media_next);
        remoteViews.setImageViewResource(R.id.imageViewPlayNot,android.R.drawable.ic_media_pause);
        remoteViews.setTextViewText(R.id.textViewTitle,currentTrack.getName());
        //remoteViews.setTextViewText(R.id.textViewArtist,currentTrack.getName());
        //notification_id = (int) System.currentTimeMillis();

        Intent button_intent = new Intent(CLOSE_ACTION);
        button_intent.putExtra("id",notification_id);
        PendingIntent button_pending_event = PendingIntent.getBroadcast(context,notification_id,
                button_intent,0);
        remoteViews.setOnClickPendingIntent(R.id.imageViewCloseNot,button_pending_event);

        button_intent = new Intent(PLAY_ACTION);
        button_intent.putExtra("id",notification_id);
        button_pending_event = PendingIntent.getBroadcast(context,notification_id,
                button_intent,0);
        remoteViews.setOnClickPendingIntent(R.id.imageViewPlayNot,button_pending_event);

        button_intent = new Intent(PREV_ACTION);
        button_intent.putExtra("id",notification_id);
        button_pending_event = PendingIntent.getBroadcast(context,notification_id,
                button_intent,0);
        remoteViews.setOnClickPendingIntent(R.id.imageViewBackNot,button_pending_event);

        button_intent = new Intent(NEXT_ACTION);
        button_intent.putExtra("id",notification_id);
        button_pending_event = PendingIntent.getBroadcast(context,notification_id,
                button_intent,0);
        remoteViews.setOnClickPendingIntent(R.id.imageViewForwNot,button_pending_event);

        notification_intent = new Intent(context,PlayerActivity.class);
        notification_intent.setAction("notification");
        notification_intent.putExtra("playlist",songs);
        notification_intent.removeExtra("currentSong");
        notification_intent.putExtra("currentSong",currentSongIndex);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notification_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.mipmap.ic_logo)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notification_id,builder.build());
    }
    //playback methods


    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(Track currentTrack) {
        this.currentTrack = currentTrack;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pause(){
        removeHandler();
        player.pause();
    }

    public void seekTo(int posn){
        player.seekTo(posn);
    }

    public void start(){
        updateProgressBar();
        player.start();
    }

    //skip to previous track
    public void playPrev(){
        currentTime=0;
        currentSongIndex--;
        if(currentSongIndex <0) currentSongIndex =songs.size()-1;
        playSong();
    }

    public void pausePlay(){
        if (player.isPlaying()){
            player.pause();
        }else {
            player.start();
        }
    }

    //skip to next
    public void playNext(){
        currentTime=0;
        if(shuffle){
            int newSong = currentSongIndex;
            while(newSong== currentSongIndex){
                newSong=rand.nextInt(songs.size());
            }
            currentSongIndex =newSong;
        }
        else{
            currentSongIndex++;
            if(currentSongIndex >=songs.size()) currentSongIndex =0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }


    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Intent intent = new Intent(TIME_UPDATE);
            sendBroadcast(intent);
          /*  notification_intent = new Intent(context,Player.class);
            notification_intent.setAction("notification");
            notification_intent.putExtra("playlist",songs);
            notification_intent.removeExtra("currentSong");
            notification_intent.putExtra("currentSong",currentSongIndex);
            notification_intent.removeExtra("currentTime");
            notification_intent.putExtra("currentTime",player.getCurrentPosition());
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notification_intent,PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setSmallIcon(R.mipmap.ic_logo)
                    .setAutoCancel(true)
                    .setCustomBigContentView(remoteViews)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(notification_id,builder.build());*/
            mHandler.postDelayed(this, 100);
        }
    };

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public void removeHandler(){
        if (mUpdateTimeTask!=null)
            mHandler.removeCallbacks(mUpdateTimeTask);
    }

}

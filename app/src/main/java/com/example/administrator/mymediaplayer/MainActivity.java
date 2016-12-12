package com.example.administrator.mymediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private Display currdis;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer player;
    private int vwidth,vheight;
    private Timer timer;
    private ImageButton rew;
    private ImageButton pause;
    private ImageButton start;
    private ImageButton ff;
    private TextView play_time;
    private TextView all_time;
    private TextView title;
    private SeekBar seekbar;

    public class Mytask extends TimerTask{
        public void run(){
            Message mess=new Message();
            mess.what=1;
            handler.sendMessage(mess);
        }
    }
    private final Handler handler=new Handler() {
        public void handleMessage(Message mess){
            switch (mess.what){
                case 1:
                    Time progress=new Time(player.getCurrentPosition());
                    Time alltime=new Time(player.getDuration());
                    String timeStr=progress.toString();
                    String timeStr2=alltime.toString();
                    play_time.setText(timeStr.substring(timeStr.indexOf(":")+1));
                    all_time.setText(timeStr2.substring(timeStr.indexOf(":")+1));
                    int provalue=0;
                    if(player.getDuration()>0){
                        provalue=seekbar.getMax()*player.getCurrentPosition()/player.getDuration();
                    }
                    seekbar.setProgress(provalue);
                    break;
            }
            super.handleMessage(mess);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent in=getIntent();
        Uri u=in.getData();
        String mpath="";
        if(u!=null){
            mpath=u.getPath();
        }else{
            Bundle localb=getIntent().getExtras();
            if(localb!=null){
                String tpath=localb.getString("path");
                if(tpath!=null&&!"".equals(tpath)){
                    mpath=tpath;
                }
            }
        }
        title=(TextView) findViewById(R.id.title);
        surfaceView=(SurfaceView) findViewById(R.id.surv);
        rew=(ImageButton) findViewById(R.id.rew);
        pause=(ImageButton) findViewById(R.id.pause);
        start=(ImageButton) findViewById(R.id.start);
        ff=(ImageButton) findViewById(R.id.ff);
        play_time=(TextView) findViewById(R.id.playtime);
        all_time=(TextView) findViewById(R.id.alltime);
        seekbar=(SeekBar) findViewById(R.id.seekbar);
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                player.setDisplay(surfaceHolder);
                player.prepareAsync();
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        player=new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(timer!=null){
                    timer.cancel();
                    timer=null;
                }
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vwidth=player.getVideoWidth();
                vheight=player.getVideoHeight();
                if(vwidth>currdis.getWidth()||vheight>currdis.getHeight()) {
                    float wra=(float) vwidth/currdis.getWidth();
                    float hra=(float) vheight/currdis.getHeight();
                    float ra=Math.max(wra,hra);
                    vwidth=(int)Math.ceil(vwidth/ra);
                    vheight=(int)Math.ceil(vheight/ra);
                    surfaceView.setLayoutParams(new LinearLayout.LayoutParams(vwidth, vheight));
                    player.start();
                }else{
                    player.start();
                }
                if(timer!=null){
                    timer.cancel();
                    timer=null;
                }
                timer=new Timer();
                timer.schedule(new Mytask(),50,500);
            }
        });
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            if(!mpath.equals("")){
                title.setText(mpath.substring(mpath.lastIndexOf("/")+1));
                player.setDataSource(mpath);
            }else{
                AssetFileDescriptor afd=this.getResources().openRawResourceFd(R.raw.exodus);
                player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getDeclaredLength());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                player.pause();
                if(timer!=null){
                    timer.cancel();
                    timer=null;
                }
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                player.start();
                if(timer!=null){
                    timer.cancel();
                    timer=null;
                }
                timer=new Timer();
                timer.schedule(new Mytask(),50,500);
            }
        });
        rew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()){
                    int curr=player.getCurrentPosition();
                    if(curr-10000>0){
                        player.seekTo(curr-10000);
                    }
                }
            }
        });
        ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()){
                    int curr=player.getCurrentPosition();
                    if(curr+10000<player.getDuration()){
                        player.seekTo(curr+10000);
                    }
                }
            }
        });
        currdis=this.getWindowManager().getDefaultDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,"文件夹");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1){
            Intent in=new Intent(this,Main2Activity.class);
            startActivity(in);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player!=null){
            player.stop();
        }
    }
}

package app.com.CATE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.com.CATE.adapters.CommentAdapter;
import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.models.CommentModel;
import app.com.CATE.models.YoutubeCommentModel;
import app.com.CATE.models.YoutubeDataModel;
import app.com.CATE.requests.CommentInsertRequest;
import app.com.CATE.requests.CommentRequest;
import app.com.youtubeapiv3.R;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static String GOOGLE_YOUTUBE_API = "AIzaSyBH8szUCt1ctKQabVeQuvWgowaKxHVjn8E";
    private YoutubeDataModel youtubeDataModel = null;
    TextView textViewName,countLike,countDisLike;
    ImageView imageButtonLike,imageButtonDisLike;

    public static final String VIDEO_ID = "c2UNv38V6y4";
    private YouTubePlayerView mYoutubePlayerView = null;
    private YouTubePlayer mYoutubePlayer = null;
    private ArrayList<YoutubeCommentModel> mListData = new ArrayList<>();
    private RecyclerView mList_videos = null;
    ListView listview;
    public static int video_index;
    int size, u_v_status,likes,dislikes;
    public static String userName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        youtubeDataModel = getIntent().getParcelableExtra(YoutubeDataModel.class.toString());
        Log.e("", youtubeDataModel.getDescription());
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        video_index = intent.getIntExtra("video_index", 0);
        u_v_status = intent.getIntExtra("u_v_status",0);
        likes = intent.getIntExtra("likes",0);
        dislikes = intent.getIntExtra("dislikes",0);

        mYoutubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API, this);

        textViewName = (TextView) findViewById(R.id.textViewName);

        TextView ss=findViewById(R.id.textViewDate);
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd HH:mm");
        Date time = new Date();

        String time1 = format1.format(time);
        ss.setText(time1);
        textViewName.setText(youtubeDataModel.getTitle());

        mList_videos = (RecyclerView) findViewById(R.id.mList_videos);
        listview = (ListView) findViewById(R.id.commentList);

        imageButtonLike=findViewById(R.id.imageButtonLike);
        imageButtonDisLike=findViewById(R.id.imageButtonDisLike);

        if(u_v_status==1){
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected);
            imageButtonLike.setTag(R.drawable.ic_thumb_up_selected);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down);
        }else if(u_v_status==2){
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up);
            imageButtonLike.setTag(R.drawable.ic_thumb_up);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected);
        }else{
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up);
            imageButtonLike.setTag(R.drawable.ic_thumb_up);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down);
        }

        countLike=findViewById(R.id.countLike);
        countDisLike=findViewById(R.id.countDisLike);

        countLike.setText(String.valueOf(likes));
        countDisLike.setText(String.valueOf(dislikes));
        imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_selected)) {     //좋아요 취소
                    update_likes(5);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up);
                    imageButtonLike.setTag(R.drawable.ic_thumb_up);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) - 1));
                }
                else if(imageButtonLike.getTag().equals(R.drawable.ic_thumb_up) &&imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_selected)){
                    update_likes(3);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString())+1));
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())-1));
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_selected);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down);
                }
                else{
                    update_likes(1);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected);      //좋아요 누르기
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_selected);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString())+1));
                }
            }
        });
        imageButtonDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_selected) ){    //싫어요 취소
                    update_likes(6);
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())-1));
                }
                else if(imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down) &&imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_selected)){
                    update_likes(4);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) - 1));
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())+1));
                    imageButtonLike.setTag(R.drawable.ic_thumb_up);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected);
                }
                else{             //i가 1일때 싫어요 클릭이 안된 상태
                    update_likes(2);
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected);      //싫어요 누르기
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())+1));
                }
            }
        });


        if (!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        final EditText descText = (EditText) findViewById(R.id.descText);
        Button insertButton = (Button) findViewById(R.id.insertButton);

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response.startsWith("ï»¿")) {
                        response = response.substring(3);
                    }
                    ArrayList<CommentModel> cListData = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject commentObject = jsonArray.getJSONObject(i);
                        String author = commentObject.getString("author");
                        String _index = commentObject.getString("_index");
                        String desc = commentObject.getString("desc");
                        String writetime = commentObject.getString("writetime");
                        String commentLike = commentObject.getString("commentLike");
                        String commentDisLike = commentObject.getString("commentDisLike");
                        String status = commentObject.getString("status");

                        CommentModel commentModel = new CommentModel(author,_index, desc,writetime,commentLike,commentDisLike,status);
                        cListData.add(commentModel);
                    }
                    if(cListData.isEmpty()) size = 0;
                    else size = cListData.size();

                    CommentAdapter adapter = new CommentAdapter(cListData);
                    listview.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        final CommentRequest commentRequest = new CommentRequest(video_index, userName,responseListener);
        RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);
        queue.add(commentRequest);

        insertButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String author = "test";
                String desc = descText.getText().toString();

                final Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.startsWith("ï»¿")) {
                                response = response.substring(3);
                            }
                            ArrayList<CommentModel> cListData = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject commentObject = jsonArray.getJSONObject(i);
                                String author = commentObject.getString("author");
                                String _index = commentObject.getString("_index");
                                String desc = commentObject.getString("desc");
                                String writetime = commentObject.getString("writetime");
                                String commentLike = commentObject.getString("commentLike");
                                String commentDisLike = commentObject.getString("commentDisLike");
                                String status = commentObject.getString("status");

                                CommentModel commentModel = new CommentModel(author,_index, desc,writetime,commentLike,commentDisLike,status);
                                cListData.add(commentModel);
                            }
                            if(cListData.isEmpty()) size = 0;
                            else size = cListData.size();

                            CommentAdapter adapter = new CommentAdapter(cListData);
                            listview.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                CommentInsertRequest commentInsertRequest = new CommentInsertRequest(video_index, size+1, userName, desc,userName, responseListener1);
                RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);
                queue.add(commentInsertRequest);

                descText.setText(null);
            }
        });
    }

    public void update_likes(final int target){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        Call<JsonObject> call=retrofitService.updatelikes(userName,String.valueOf(video_index),String.valueOf(target));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Toast.makeText(DetailsActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Err", t.getMessage());
            }
        });
    }
    public void back_btn_pressed(View view) {
        finish();
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if (!wasRestored) {
            youTubePlayer.cueVideo(youtubeDataModel.getVideo_id());
        }
        mYoutubePlayer = youTubePlayer;
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public void share_btn_pressed(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String link = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
        // this is the text that will be shared
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, youtubeDataModel.getTitle()
                + "Share");

        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "share"));
    }

    public void downloadVideo(View view) {
        //get the download URL
        String youtubeLink = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
        YouTubeUriExtractor ytEx = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoID, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    int itag = 22;
                    //This is the download URL
                    String downloadURL = ytFiles.get(itag).getUrl();
                    Log.e("download URL :", downloadURL);

                    //now download it like a file
                    new RequestDownloadVideoStream().execute(downloadURL, videoTitle);


                }

            }
        };

        ytEx.execute(youtubeLink);
    }

    private ProgressDialog pDialog;


    private class RequestDownloadVideoStream extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailsActivity.this);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;
            URL u = null;
            int len1 = 0;
            int temp_progress = 0;
            int progress = 0;
            try {
                u = new URL(params[0]);
                is = u.openStream();
                URLConnection huc = (URLConnection) u.openConnection();
                huc.connect();
                int size = huc.getContentLength();

                if (huc != null) {
                    String file_name = params[1] + ".mp4";
                    String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YoutubeVideos";
                    File f = new File(storagePath);
                    if (!f.exists()) {
                        f.mkdir();
                    }

                    FileOutputStream fos = new FileOutputStream(f+"/"+file_name);
                    byte[] buffer = new byte[1024];
                    int total = 0;
                    if (is != null) {
                        while ((len1 = is.read(buffer)) != -1) {
                            total += len1;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            progress = (int) ((total * 100) / size);
                            if(progress >= 0) {
                                temp_progress = progress;
                                publishProgress("" + progress);
                            }else
                                publishProgress("" + temp_progress+1);

                            fos.write(buffer, 0, len1);
                        }
                    }

                    if (fos != null) {
                        publishProgress("" + 100);
                        fos.close();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }





    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }
}

package app.com.CATE.fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.CATE.MainActivity;
import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.adapters.CategoryAdapter;
import app.com.CATE.models.CategoryModel;
import app.com.CATE.models.YoutubeDataModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends ListFragment {

    private CategoryAdapter adapter = null;
    private List<CategoryModel> categoryList;
    private MainActivity mainActivity;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Adapter 생성 및 Adapter 지정.
        adapter = new CategoryAdapter();
        mainActivity = (MainActivity) getActivity();
        categoryList = new ArrayList<CategoryModel>();

        All_category();


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void All_category() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.all_category(mainActivity.strName).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                try {
                    JsonArray jsonArray = jsonObject.get("response").getAsJsonArray();
                    JsonArray numberArray = jsonObject.get("category_number").getAsJsonArray();

                    int count = 0;

                    String cateId, cateName, cateDetail, cateKey;
                    Boolean cateState;
                    while (count < jsonArray.size()) {
                        JsonObject object = jsonArray.get(count).getAsJsonObject();

                        if (numberArray.get(count).getAsJsonObject().get("category_number").getAsString().equals("1")) cateState = true;
                        else cateState = false;

                        cateId = object.get("id").getAsString();
                        cateName = object.get("name").getAsString();
                        cateDetail = object.get("detail").getAsString();
                        cateKey = object.get("key").getAsString();

                        if (cateState) {
                            mSelectedItems.put(count, true);
                        }
                        CategoryModel CategoryModel = new CategoryModel(cateId, cateName, cateDetail, cateKey, cateState);
                        categoryList.add(CategoryModel);
                        adapter.addItem(cateId, cateName, cateDetail, cateKey, cateState);
                        count++;
                    }

                    setListAdapter(adapter);
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    //아이템 클릭 이벤트
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // get TextView's Text.
        CategoryModel item = (CategoryModel) l.getItemAtPosition(position);
        new PcOfSeat().execute(item.getDetail());

        if (mSelectedItems.get(position, false)) {
            mSelectedItems.put(position, false);
            v.setBackgroundColor(Color.TRANSPARENT);

            Map map = new HashMap();
            map.put("index", position + "");
            map.put("value", 0 + "");
            map.put("user_name", mainActivity.strName);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RetrofitService.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitService retrofitService = retrofit.create(RetrofitService.class);

            retrofitService.postCategory(map).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });

        } else {
            mSelectedItems.put(position, true);
            v.setBackgroundColor(Color.LTGRAY);

            Map map = new HashMap();
            map.put("index", position + "");
            map.put("value", 1 + "");
            map.put("user_name", mainActivity.strName);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RetrofitService.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitService retrofitService = retrofit.create(RetrofitService.class);

            retrofitService.postCategory(map).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });

        }

        mainActivity.PLAYLIST_ID = item.getKey();

        // TODO : use item data.
    }

    public void addItem(String icon, String title, String desc, String channel, Boolean state) {
        adapter.addItem(icon, title, desc, channel, state);
    }


    public class PcOfSeat extends AsyncTask<String, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://ghkdua1829.dothome.co.kr/fow/fow_getVideo.php";
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String tag = (String) params[0];

                URL url = new URL(target);//URL 객체 생성

                String data = URLEncoder.encode("tag", "UTF-8") + "=" + URLEncoder.encode(tag, "UTF-8");
                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());

                wr.write(data);
                wr.flush();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();


                //한줄씩 읽어서 stringBuilder에 저장함
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            //    Toast.makeText(mainActivity, result, Toast.LENGTH_SHORT).show();
            mainActivity.channel = result;

            try {
                ArrayList<YoutubeDataModel> listData = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;

                while (count < jsonArray.length()) {

                    JSONObject object = jsonArray.getJSONObject(count);

                    YoutubeDataModel youtubeObject = new YoutubeDataModel();
                    String thumbnail = "";
                    String video_id = "";
                    String cateName, video_kind, cateDetail;
                    int video_index;

                    cateName = object.getString("title");
                    video_kind = object.getString("kind");
                    cateDetail = object.getString("url");
                    video_index = Integer.parseInt(object.getString("id"));

                    if (video_kind.equals("YOUTUBE")) {
                        video_id = cateDetail.substring(cateDetail.indexOf("=") + 1);
                        thumbnail = "https://i.ytimg.com/vi/" + video_id + "/hqdefault.jpg";
                    }
                    if (video_kind.equals("TWITCH")) {
                        String[] split = cateDetail.split("/");
                        video_id = split[4];
                        thumbnail = "https://static-cdn.jtvnw.net/jtv_user_pictures/twitch-profile_image-8a8c5be2e3b64a9a-300x300.png";
                    }

                    youtubeObject.setVideo_index(video_index);
                    youtubeObject.setTitle(cateName);
                    youtubeObject.setThumbnail(thumbnail);
                    youtubeObject.setVideo_id(video_id);
                    youtubeObject.setVideo_kind(video_kind);

                    count++;
                    listData.add(youtubeObject);
                }
                mainActivity.listData = listData;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
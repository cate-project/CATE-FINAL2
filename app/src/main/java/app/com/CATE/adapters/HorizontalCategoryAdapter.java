package app.com.CATE.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.com.CATE.interfaces.OnArrayClickListner;
import app.com.youtubeapiv3.R;

public class HorizontalCategoryAdapter extends RecyclerView.Adapter<HorizontalCategoryAdapter.ViewHolder> {
    private ArrayList<String> itemList;
    private Context context;
    private OnArrayClickListner onArrayClickListner;

    public HorizontalCategoryAdapter(Context context, ArrayList<String> itemList, OnArrayClickListner onArrayClickListner) {
        this.context = context;
        this.itemList = itemList;
        this.onArrayClickListner = onArrayClickListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // context 와 parent.getContext() 는 같다.
        View view = LayoutInflater.from(context)
                .inflate(R.layout.horizontal_category_list_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String item = itemList.get(position);

        holder.textview.setText(item);
//        holder.textview.setTag(item);
        holder.bind(itemList.get(position), onArrayClickListner);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textview;

        public ViewHolder(final View itemView) {
            super(itemView);

            textview = (TextView) itemView.findViewById(R.id.category_textView);
        }


        public void bind(final String string, final OnArrayClickListner onArrayClickListner){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onArrayClickListner.onArrayClick(string, view);
                }
            });
        }
    }
}

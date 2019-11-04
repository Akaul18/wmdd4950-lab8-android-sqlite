package edu.wmdd.sqlite_example;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private static final String TAG = "RecyclerAdapter";
    List<String> operators = new ArrayList<>();
    List<String> urls = new ArrayList<>();

    public RecyclerAdapter(List<String> operators, List<String> urls) {
        this.operators = operators;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       holder.textView.setText(operators.get(position));

//        Log.d(TAG, "url: "+url.get(0));

        holder.textView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("url", urls.get(position));
            v.getContext().startActivity(intent);
//            Log.d(TAG, "onBindViewHolder: tapped");
        });
    }

    @Override
    public int getItemCount() {
        return operators.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}

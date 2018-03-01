package com.sendi.slides;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SlidesRecyclerView mRecyclerView;
    private SlidesAdapter mSlidesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView=findViewById(R.id.recyclerView);
        mSlidesAdapter=new SlidesAdapter(this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mSlidesAdapter);

        mSlidesAdapter.setDeleteListener(new SlidesAdapter.OnDeleteListener() {
            @Override
            public void onDelete(int position, View view) {
                Log.i("TAG", "onDelete: "+position);
                mSlidesAdapter.deleteItem(position);
                mRecyclerView.removeView(view);
            }
        });

    }
}

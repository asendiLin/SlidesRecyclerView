package com.sendi.slides;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

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
        mRecyclerView.setAdapter(mSlidesAdapter);

        mRecyclerView.setDeleteListener(new SlidesRecyclerView.OnDeleteListener() {
            @Override
            public void onDelete(int position) {
                Toast.makeText(MainActivity.this, "click the item:"+position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

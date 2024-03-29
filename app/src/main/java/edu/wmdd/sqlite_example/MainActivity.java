package edu.wmdd.sqlite_example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();
    private RentalDBHelper helper = null;
    private SQLiteDatabase db = null;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database, potentially creating it
        helper = new RentalDBHelper(this);
        db = helper.getReadableDatabase();

        // Only populate the db if it is empty
        Cursor c = db.rawQuery("SELECT count(*) FROM issues", null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    // We have to init the data in a separate thread because of networking
                    helper.initData();

                    // We are now ready to initialize the view on the UI thread
                    runOnUiThread(() -> {
                        initView();
                    });
                }
            };
            t.start();
        } else {
            // We are already inside the UI thread
            initView();
        }
        c.close();
    }

    private void initView() {
        Spinner tv = findViewById(R.id.spinnerTextView);
        ArrayList<String> areas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT(area) FROM issues", null);
        while (cursor.moveToNext()) {
            String area = cursor.getString(0);
            areas.add(area);
        }
        cursor.close();
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, areas);

        tv.setAdapter(areaAdapter);
        tv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedArea = ((TextView) view).getText().toString();
                Cursor cursor1 = db.rawQuery("SELECT operator,businessURL FROM issues WHERE area = ?", new String[]{selectedArea});
                ArrayList<String> operators = new ArrayList<>();
                ArrayList<String> urls = new ArrayList<>();
                while (cursor1.moveToNext()) {
                    String operator = cursor1.getString(0);
                    String url = cursor1.getString(1);
                    operators.add(operator);
                    urls.add(url);
                    Log.d(TAG, operator+url);
                }

                ArrayList<String> url = new ArrayList<>();

                for(int i=0; i<urls.size(); i++){
                    url.add("https"+urls.get(i).split("http")[1]);
                }

                RecyclerView rv = findViewById(R.id.rv);
                recyclerAdapter = new RecyclerAdapter(operators, url);
                rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rv.setAdapter(recyclerAdapter);
                rv.addItemDecoration (new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}

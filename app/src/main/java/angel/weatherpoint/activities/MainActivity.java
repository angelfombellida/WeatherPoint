package angel.weatherpoint.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import angel.weatherpoint.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container, new MapFragment())
//                .commit();
    }
}

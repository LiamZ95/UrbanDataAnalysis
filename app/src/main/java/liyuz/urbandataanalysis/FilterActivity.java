package liyuz.urbandataanalysis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {
    private Capability selectedCap;
    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<String> classifier = new ArrayList<>();
    private static String[] colors = {"Red","Blue","Green","Gray","Purple"};
    private static String[] level = {"1","2","3","4","5","6"};

    private Button areaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Intent intent = getIntent();
        selectedCap = (Capability) intent.getSerializableExtra("SelectedCapabilityForFilter");



    }


}

package com.mvcoder.tbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mvcoder.tbs.views.OfficeFileViewr;

import java.io.FileNotFoundException;

public class DisplayFileActivity extends AppCompatActivity {

    private OfficeFileViewr fileViewr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file);
        initView();
        getAgrment();
    }

    private void getAgrment() {
        String filePath = getIntent().getStringExtra("filePath");
        if(filePath != null){
            try {
                fileViewr.displayFile(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        fileViewr = findViewById(R.id.office_viewer);
    }
}

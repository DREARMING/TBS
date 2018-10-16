package com.mvcoder.tbsdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mvcoder.tbs.DisplayFileActivity;
import com.mvcoder.tbs.TBSBrowserActivity;
import com.mvcoder.tbsdemo.utils.OfficeUtils;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CODE = 10;
    private File cacheDir;
    private Button btDoc;
    private Button btXls;
    private Button btXlsx;
    private Button btPPtx;
    private Button btDocX;
    private Button btTxt;
    private Button btPDF;
    private Button btWeb;

    private boolean hasPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        } else {
            hasPermission = true;
        }
    }

    private void initView() {
        btDoc = findViewById(R.id.bt_doc);
        btDocX = findViewById(R.id.bt_docx);
        btXls = findViewById(R.id.bt_xls);
        btXlsx = findViewById(R.id.bt_xlsx);
        btPPtx = findViewById(R.id.bt_pptx);
        btTxt = findViewById(R.id.bt_tx);
        btPDF = findViewById(R.id.bt_pdf);
        btWeb = findViewById(R.id.bt_browser);
        btDoc.setOnClickListener(this);
        btDocX.setOnClickListener(this);
        btXls.setOnClickListener(this);
        btXlsx.setOnClickListener(this);
        btPPtx.setOnClickListener(this);
        btTxt.setOnClickListener(this);
        btPDF.setOnClickListener(this);
        btWeb.setOnClickListener(this);
    }

    private void startBrowserActivity() {
        Intent intent = new Intent(this, TBSBrowserActivity.class);
        intent.putExtra(TBSBrowserActivity.KEY_URL, "http://www.baidu.com");
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_browser) {
            startBrowserActivity();
            return;
        }
        if (!hasPermission) {
            LogUtils.d("没有sd卡读写权限");
            return;
        }
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File showFile = null;
        String fileName = null;
        switch (view.getId()) {
            case R.id.bt_doc:
                fileName = "1.doc";
                break;
            case R.id.bt_docx:
                fileName = "1.docx";
                break;
            case R.id.bt_xls:
                fileName = "1.xls";
                break;
            case R.id.bt_xlsx:
                fileName = "1.xlsx";
                break;
            case R.id.bt_pptx:
                fileName = "1.pptx";
                break;
            case R.id.bt_tx:
                fileName = "1.txt";
                break;
            case R.id.bt_pdf:
                fileName = "1.pdf";
                break;
        }
        showFile = new File(fileDir, fileName);

        if (TBSApplication.hasX5Core) {
            if (showFile != null && showFile.exists()) {
                Intent intent = new Intent(this, DisplayFileActivity.class);
                intent.putExtra("filePath", showFile.getAbsolutePath());
                startActivity(intent);
            } else {
                ToastUtils.showShort("file不存在");
            }
        } else {
            ToastUtils.showShort("不支持X5内核");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri uri = null;
            if (Build.VERSION_CODES.N > Build.VERSION.SDK_INT) {
                uri = Uri.fromFile(showFile);
            } else {
                uri = FileProvider.getUriForFile(this, "com.mvcoder.tbsdemo.fileprovider", showFile);
                if (uri != null) {
                    LogUtils.e("uri : " + uri.toString());
                } else {
                    LogUtils.e("uri == null");
                }
            }
            intent.setDataAndType(uri, OfficeUtils.getMineTypeBySuffix(fileName.substring(2)));
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list != null && list.size() > 0) {
                startActivity(intent);
            } else {
                LogUtils.e("no  resovle activity");
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults != null && grantResults.length > 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

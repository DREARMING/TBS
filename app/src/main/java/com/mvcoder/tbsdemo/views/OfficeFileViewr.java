package com.mvcoder.tbsdemo.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
import java.io.FileNotFoundException;

public class OfficeFileViewr extends FrameLayout implements TbsReaderView.ReaderCallback {

    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_TEMP_PATH = "tempPath";

    private final String tempFilePath = "TBSReaderTemp";
    private File tempFileDir;

    private final String TAG = OfficeFileViewr.class.getSimpleName();

    private TbsReaderView tbsReaderView;

    private File showFile;

    public OfficeFileViewr(@NonNull Context context) {
        this(context, null);
    }

    public OfficeFileViewr(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfficeFileViewr(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void displayFile(@NonNull String filePath) throws FileNotFoundException,IllegalAccessException {
        showFile = new File(filePath);
        if(showFile != null && showFile.exists()){
            displayFile(showFile);
            return;
        }
        throw new FileNotFoundException("Filepath : " + filePath + " 该文件不存在");
    }

    public void displayFile(@NonNull File file) throws FileNotFoundException, IllegalAccessException {
        if(!file.exists()) throw new FileNotFoundException("Filepath : " + file.getAbsolutePath().toString() + " 该文件不存在");
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.e(TAG, "没有文件读写权限");
            throw new IllegalAccessException("没有外部存储读取权限");
        }
        File fileDir = getContext().getExternalFilesDir(null);
        tempFileDir = new File(fileDir, "TbsTemp");
        if(!tempFileDir.exists()){
            boolean b = tempFileDir.mkdir();
            if(!b){
                Log.e(TAG,"创建TbsTemp目录失败");
                return;
            }
        }
        Bundle localBundle = new Bundle();
        localBundle.putString(KEY_FILE_PATH, file.toString());
        localBundle.putString(KEY_TEMP_PATH, fileDir.getAbsolutePath());

        boolean flag = tbsReaderView.preOpen(getFileType(file.toString()),false);
        if(flag){
            tbsReaderView.openFile(localBundle);
        }else{
            Log.e(TAG,"无法加载该文档：" + file.toString());
        }

    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d(TAG, "paramString---->null");
            return str;
        }
        Log.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d(TAG, "i <= -1");
            return str;
        }


        str = paramString.substring(i + 1);
        Log.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }

    private void initView() {
        tbsReaderView = new TbsReaderView(getContext(), this);
        addView(tbsReaderView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        tbsReaderView.onSizeChanged(w,h);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        tbsReaderView.onStop();
    }
}

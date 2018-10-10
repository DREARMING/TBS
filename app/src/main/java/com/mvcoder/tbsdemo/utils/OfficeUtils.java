package com.mvcoder.tbsdemo.utils;

import java.util.HashMap;

public class OfficeUtils {

    public static HashMap<String, String> OFFICE_SUFFIX = new HashMap<>(8);

    static {
        String docMineType = "application/msword";
        String pptMineType = "application/vnd.ms-powerpoint";
        String xlsMineType = "application/vnd.ms-excel";
        String pdfMineType = "application/pdf";
        String txtMineType = "text/plain";
        OFFICE_SUFFIX.put("doc", docMineType);
        OFFICE_SUFFIX.put("docx", docMineType);
        OFFICE_SUFFIX.put("xls", xlsMineType);
        OFFICE_SUFFIX.put("xlsx", xlsMineType);
        OFFICE_SUFFIX.put("ppt", pptMineType);
        OFFICE_SUFFIX.put("pptx", pptMineType);
        OFFICE_SUFFIX.put("pdf", pdfMineType);
        OFFICE_SUFFIX.put("txt", txtMineType);
    }

    public static String getMineTypeBySuffix(String suffix) {
        String mineType = OFFICE_SUFFIX.get(suffix);
        return mineType;
    }
}

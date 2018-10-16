package com.mvcoder.tbs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;

/**
 * 需要权限：网络、获取网络状态、外部存储的权限和手机状态读取权限<br/>
 * 再使用该库时，需要先初始化x5内核，如果内核初始化失败，请不要用该库
 */
public class TBSBrowserActivity extends AppCompatActivity {

    private final String TAG = TBSBrowserActivity.class.getSimpleName();
    private String WEB_URL = null;
    public static final String KEY_URL = "key_url";

    private FrameLayout webviewContainer;
    private FrameLayout loadLayout;
    private RelativeLayout errorPageLayout;
    private Button btReload;

    private WebView webView;
    private boolean needClearHistroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        filterX5Spread();
        setContentView(R.layout.activity_tbsbrowser);
        getArgument(getIntent());
        initView();
        loadUrl();
    }



    private void loadUrl() {
        if (webView != null) {
            webView.loadUrl(WEB_URL);
        }
    }

    private void initView() {
        webviewContainer = findViewById(R.id.browser_fl_webview_container);
        loadLayout = findViewById(R.id.load_layout);
        errorPageLayout = findViewById(R.id.errorPage);
        btReload = findViewById(R.id.reloadBt);
        btReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClicked();
            }
        });

        webView = new WebView(this);
        webViewSetting(webView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webviewContainer.addView(webView, params);
    }

    private void webViewSetting(WebView mWebView) {
        if (webView != null) {
            WebSettings settings = webView.getSettings();
            settings.setDomStorageEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setUseWideViewPort(true);//关键点
            //settings.setSupportMultipleWindows(true);
            settings.setAllowFileAccess(true);
            settings.setLoadsImagesAutomatically(true);
            settings.setAllowContentAccess(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setSupportZoom(true); // 支持缩放
            settings.setDisplayZoomControls(false);
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染优先级
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            //settings.setUseWideViewPort(true);//显示正常的视图
            settings.setAppCacheEnabled(true);
            settings.setDatabaseEnabled(true);

            //当访问https网页时，允许加载http图片，默认不能混合加载
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                // settings.setMixedContentMode(com.tencent.smtt.sdk.WebSettings.);
            }


            webView.setWebViewClient(new WebViewClient() {

                /*  @Override
                  public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                      //用当前webview打开网页
                      //view.loadUrl(URL);
                      return false;
                  }
  */
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (TextUtils.isEmpty(url)) return false;
                    if (url.startsWith("http") || url.startsWith("https")) {
                        return false;
                    } else {
                        try {
                            if (url.startsWith("weixin://") || url.startsWith("alipays://") || url.startsWith("mailto://")
                                    || url.startsWith("tel://") || url.startsWith("baidu") || url.startsWith("dianping://")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                                return true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //return true就不会发生私有协议错误的事情，让webview继续下一步加载
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    //隐藏 ProgressBar,开放网络图片加载
                    //view.getSettings().setBlockNetworkImage(false);
                    super.onPageFinished(view, url);
                }

                //清除历史记录
                @Override
                public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                    super.doUpdateVisitedHistory(view, url, isReload);
                    if (needClearHistroy) {
                        needClearHistroy = false;
                        view.clearHistory();
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    //显示progressBar,堵塞网络图片加载，加快页面打开速度。
                    loadLayout.setVisibility(View.VISIBLE);
                    //view.getSettings().setBlockNetworkImage(true);
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    webView.stopLoading();
                    loadLayout.setVisibility(View.GONE);
                    errorPageLayout.setVisibility(View.VISIBLE);

                    //ToastUtils.showShort("加载URL失败："+failingUrl);
                    //webView.loadUrl(ERROR_PAGE);
                }
            });

            mWebView.setWebChromeClient(new WebChromeClient() {

               /* @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(view.getContext());
                    builder.content(message)
                            .positiveText("确定")
                            .canceledOnTouchOutside(true)
                            .show();
                    result.confirm();
                    return*//* super.onJsAlert(view, url, message, result);*//* true;
                }

                @Override
                public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(view.getContext());
                    builder.content(message)
                            .positiveText("确定")
                            .negativeText("取消")
                            .canceledOnTouchOutside(false)
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.POSITIVE) {
                                        result.confirm();
                                    } else if (which == DialogAction.NEGATIVE) {
                                        result.cancel();
                                    }
                                }
                            }).show();
                    return true;
                }*/

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    if (!BuildConfig.DEBUG) return true;
                    if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.DEBUG) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("console:\nfrom: ");
                        builder.append(consoleMessage.sourceId());
                        builder.append("\n");
                        builder.append("message: ");
                        builder.append(consoleMessage.message());
                        Log.d(TAG, builder.toString());
                    }
                    return super.onConsoleMessage(consoleMessage);
                }

                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 2);
                    return true;
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        loadLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (title.contains("404") || title.contains("not available") || title.contains("not found")) {
                        webView.stopLoading();
                        errorPageLayout.setVisibility(View.VISIBLE);
                        //mWebView.loadUrl(ERROR_PAGE);
                    }
                }
            });
        }
    }

    private void getArgument(Intent intent) {
        String url = intent.getStringExtra(KEY_URL);
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url must not be null");
        }
        WEB_URL = url;
    }

    public void onViewClicked() {
        needClearHistroy = true;
        loadLayout.setVisibility(View.VISIBLE);
        webView.loadUrl(WEB_URL);
        errorPageLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
        }
        webviewContainer.removeAllViews();
    }

    /**
     * 拦截x5的推广信息
     */
    private void filterX5Spread() {
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ArrayList<View> outView = new ArrayList<View>();
                getWindow().getDecorView().findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT);
                int size = outView.size();
                if (outView != null && outView.size() > 0) {
                    outView.get(0).setVisibility(View.GONE);
                }
            }
        });
    }
}
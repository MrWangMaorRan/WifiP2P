package leavesc.hello.filetransfer;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class AgreementActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        initView();
        initData();
    }

    private void initData() {
        webView.loadUrl("file:///android_asset/UserProtoclHtml.html");//这里写的是assets文件夹下html文件的名称，需要带上后面的后缀名，前面的路径是安卓系统自己规定的android_asset就是表示的在assets文件夹下的意思。
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕
        webView.getSettings().setLoadWithOverviewMode(true);//自适应屏幕
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);//扩大比例的缩放
        webView.getSettings().setBuiltInZoomControls(true);//设置是否出现缩放工具
        WebSettings settings = webView.getSettings();
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.weview);
    }
}
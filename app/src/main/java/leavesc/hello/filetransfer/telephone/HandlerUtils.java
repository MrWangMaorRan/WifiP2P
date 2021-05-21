package leavesc.hello.filetransfer.telephone;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class HandlerUtils {

    /**
     * 获取主线程的handler
     *
     * @return
     */
    public static Handler getMainHandler() {
        return new Handler(Looper.getMainLooper());
    }

    /**
     * 主线程赋值
     *
     * @param textView
     * @param text
     */
    public static void setText(TextView textView, String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (textView != null && text != null) {
                    textView.setText(text);
                }
            }
        });
    }

    /**
     * 主线程改字体颜色
     *
     * @param textView
     * @param textColor
     */
    public static void setTextColor(TextView textView, int textColor) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (textView != null) {
                    textView.setTextColor(textColor);
                }
            }
        });
    }

    /**
     * 主线程改字体颜色
     *
     * @param imageView
     * @param img
     */
    public static void setImg(ImageView imageView, Drawable img) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (imageView != null) {
                    imageView.setImageDrawable(img);
                }
            }
        });
    }

    /**
     * 主线程赋值
     *
     * @param editText
     * @param text
     */
    public static void setText(EditText editText, String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (editText != null && text != null) {
                    editText.setText(text);
                }
            }
        });
    }

    /**
     * 主线程改字体颜色
     *
     * @param view
     * @param visible
     */
    public static void setVisible(View view, int visible) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.setVisibility(visible);
                }
            }
        });
    }
}

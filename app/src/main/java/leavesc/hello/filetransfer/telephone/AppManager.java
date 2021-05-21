package leavesc.hello.filetransfer.telephone;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 * @author xin.xie
 * 
 */
public class AppManager {

	private static Stack<Activity> activityStack;

	private AppManager() {

	}

	/**
	 * 添加Activity到堆栈
	 * 
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 去除已经释放的Activity
	 * 
	 * @param activity
	 */
	public static void removeActivity(Activity activity) {
		if (activityStack != null && activityStack.size() > 0) {
			// 判断该activity是否存在
			if (activityStack.contains(activity)) {
				activityStack.remove(activity);
			}
		}
	}

	/**
	 * 释放栈顶指定说明的Activity
	 * 
	 * @param topCount
	 */
	public static void finishActivity(int topCount) {
		if (topCount > 0 && topCount < activityStack.size()) {
			while (topCount > 0) {
				finishLastActivity();
				topCount--;
			}
		}
	}

	/** 结束当前Activity（堆栈中最后一个压入的） */
	public static void finishLastActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 * 
	 * @param activity
	 */
	public static void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定的Activity
	 * 
	 * @param context
	 */
	public static void finishActivity(Context context) {
		finishActivity((Activity) context);
	}

	/**
	 * 结束指定类名的Activity
	 * 
	 * @param cls
	 */
	public static void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/** 结束所有Activity */
	public static void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (activityStack.get(i) != null) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	public static void finishAllActivityExceptMain() {
		for (int i = 1; i < activityStack.size(); i++) {
			if (activityStack.get(i) != null) {
				activityStack.get(i).finish();
			}
		}
		for (int i = 1; i < activityStack.size(); i++) {
			if (activityStack.get(i) != null) {
				activityStack.remove(i);
			}
		}
	}

	/**
	 * 退出应用程序
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public static void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.restartPackage(context.getPackageName());
//			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 * 
	 * @return
	 */
	public static Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 获取指定Activity
	 * 
	 * @return
	 */
	public static Activity getAppointActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

}

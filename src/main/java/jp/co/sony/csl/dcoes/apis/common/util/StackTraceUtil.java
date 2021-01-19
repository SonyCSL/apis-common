package jp.co.sony.csl.dcoes.apis.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a trivial stack trace-related tool.
 * @author OES Project
  * スタックトレースまわりのどうってことないツール.
 * @author OES Project
 */
public class StackTraceUtil {

	private StackTraceUtil() { }

	/**
	 * Gets the last element in stack trace of current thread.
	 * @return last stacktraceelement object in stack trace of current thread
	 * 現在のスレッドのスタックトレースの最後の要素を取得する.
	 * @return 現在のスレッドのスタックトレースの最後の stacktraceelement オブジェクト
	 */
	public static StackTraceElement lastStackTrace() {
		return lastStackTrace(null);
	}
	/**
	 * Gets the last element in stack trace of current thread.
	 * Classes to ignore can be specified by {@code classes}.
	 * The ignore function is used, for example, when calling in a utility object and you do not wish to include processing within the object.
	 * @param classes array to classes to ignore
	 * @return last stacktraceelement object in stack trace of current thread.
	 *         Excludes processing in classes specified by {@code classes}.
	 * 現在のスレッドのスタックトレースの最後の要素を取得する.
	 * {@code classes} でスルーするクラスを指定できる.
	 * スルー機能は例えばユーティリティオブジェクト中で呼び出す際にそのオブジェクト内の処理を含めたくない場合など.
	 * @param classes スルーするクラスの配列
	 * @return 現在のスレッドのスタックトレースの最後の stacktraceelement オブジェクト.
	 *         ただし {@code classes} で指定したクラス内の処理は除く.
	 */
	public static StackTraceElement lastStackTrace(Class<?>[] classes) {
		Map<String, Object> classNames = new HashMap<>();
		classNames.put(Thread.class.getName(), Boolean.TRUE);
		classNames.put(StackTraceUtil.class.getName(), Boolean.TRUE);
		if (classes != null) {
			for (Class<?> aClass : classes) {
				classNames.put(aClass.getName(), Boolean.TRUE);
			}
		}
		for (StackTraceElement aSte : Thread.currentThread().getStackTrace()) {
			// Ignores classes to be ignored
			// スルーするクラスをスルーするー
			if (classNames.containsKey(aSte.getClassName())) continue;
			return aSte;
		}
		return null;
	}

	/**
	 * Gets stack trace of current thread.
	 * @return stack trace of current thread
	 * 現在のスレッドのスタックトレースを取得する.
	 * @return 現在のスレッドのスタックトレース
	 */
	public static StackTraceElement[] stackTrace() {
		return stackTrace(null);
	}
	/**
	 * Gets stack trace of current thread.
	 * Classes to ignore can be specified by {@code classes}.
	 * The ignore function is used, for example, when calling in a utility object and you do not wish to include processing within the object.
	 * @param classes array to classes to ignore
	 * @return stack trace of current thread.
	 *         Excludes processing in classes specified by {@code classes}.
	 * 現在のスレッドのスタックトレースを取得する.
	 * {@code classes} でスルーするクラスを指定できる.
	 * スルー機能は例えばユーティリティオブジェクト中で呼び出す際にそのオブジェクト内の処理を含めたくない場合など.
	 * @param classes スルーするクラスの配列
	 * @return 現在のスレッドのスタックトレース.
	 *         ただし {@code classes} で指定したクラス内の処理は除く.
	 */
	public static StackTraceElement[] stackTrace(Class<?>[] classes) {
		Map<String, Object> classNames = new HashMap<>();
		classNames.put(Thread.class.getName(), Boolean.TRUE);
		classNames.put(StackTraceUtil.class.getName(), Boolean.TRUE);
		if (classes != null) {
			for (Class<?> aClass : classes) {
				classNames.put(aClass.getName(), Boolean.TRUE);
			}
		}
		List<StackTraceElement> result = new ArrayList<>();
		for (StackTraceElement aSte : Thread.currentThread().getStackTrace()) {
			if (classNames.containsKey(aSte.getClassName())) continue;
			result.add(aSte);
		}
		return result.toArray(new StackTraceElement[result.size()]);
	}
}

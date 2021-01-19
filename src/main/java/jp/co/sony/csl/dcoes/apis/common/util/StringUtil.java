package jp.co.sony.csl.dcoes.apis.common.util;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This is a trivial {@link String} related tool.
 * @author OES Project
 * {@link String} まわりのどうってことないツール.
 * @author OES Project
 */
public class StringUtil {
	private static final Logger log = LoggerFactory.getLogger(StringUtil.class);

	/**
	 * String to be replaced by path of temporary file location (e.g. /tmp for UNIX systems) in {@link #fixFilePath(String)}.
	 * The value is {@value}.
	 * {@link #fixFilePath(String)} で一時ファイル置き場のパス ( UNIX 系なら /tmp など ) に置換される文字列.
	 * 値は {@value}.
	 */
	public static final String TMPDIR = "{tmpdir}";

	private StringUtil() { }

	/**
	 * {@code null} if {@code null} or empty string; otherwise, gets original string.
	 * @param value string to be processed
	 * @return {@code null} if {@code null} or empty string; otherwise, original string
	 * {@code null} または空文字列なら {@code null}, そうでなければ元の文字列を取得する.
	 * @param value 処理対象文字列
	 * @return {@code null} または空文字列なら {@code null}, そうでなければ元の文字列
	 */
	public static String nullIfEmpty(String value) {
		return (value == null || value.isEmpty()) ? null : value;
	}

	/** 
	 * Gets path string using prescribed rule for path strings.
	 * The current rule is just to replace {@link #TMPDIR} with path of temporary file location (e.g. /tmp for UNIX systems).
	 * The temporary file directory is acquired by {@code System.getProperty("java.io.tmpdir", ".") ;}.
	 * In short, the default is {@code .}.
	 * @param value path string
	 * @return path string after replacement using prescribed rule
	 * パス文字列を規定のルールで置換した文字列を取得する.
	 * 現在のルールは {@link #TMPDIR} を一時ファイル置き場のパス ( UNIX 系なら /tmp など ) に置換するだけ.
	 * 一時ファイル置き場は {@code System.getProperty("java.io.tmpdir", ".");} で取得する.
	 * つまりデフォルトは {@code .}.
	 * @param value パス文字列
	 * @return 規定のルールで置換したパス文字列
	 */
	public static String fixFilePath(String value) {
		String result = value.replace('/', File.separatorChar);
		if (result.contains(TMPDIR)) {
			String tmpdir = System.getProperty("java.io.tmpdir", ".");
			if (tmpdir.endsWith(File.separator)) tmpdir = tmpdir.substring(0, tmpdir.length() - 1);
			result = result.replace(TMPDIR, tmpdir);
		}
		if (log.isInfoEnabled()) log.info("fixFilePath : [" + value + "] -> [" + result + ']');
		return result;
	}

	/**
	 * Gets URL encoded string in {@code UTF-8}.
	 * Returns original string if {@link URLEncoder#encode(String, String)} fails.
	 * @param value string to be encoded
	 * @return encoded string
	 * {@code UTF-8} で URL エンコードした文字列を取得する.
	 * {@link URLEncoder#encode(String, String)} が失敗したら元の文字列を返す.
	 * @param value エンコード対象文字列
	 * @return エンコード済み文字列
	 */
	public static String urlEncode(String value) {
		if (value != null && !value.isEmpty()) {
			try {
				return URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			}
		}
		return value;
	}

}

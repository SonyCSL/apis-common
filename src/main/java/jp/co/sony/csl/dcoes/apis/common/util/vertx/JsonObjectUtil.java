package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.sony.csl.dcoes.apis.common.util.DateTimeUtil;

/**
 * This tool enables access and easy use of {@link JsonObject} ( and {@link JsonArray} ).
 * @author OES Project
 * {@link JsonObject} ( と {@link JsonArray} ) をお手軽に利用するアクセスツール.
 * @author OES Project
 */
public class JsonObjectUtil {
	private static final Logger log = LoggerFactory.getLogger(JsonObjectUtil.class);

	private JsonObjectUtil() { }

	/**
	 * Restores {@link JsonObject} from string.
	 * @param value JsonObject serialized string
	 * @param completionHandler the completion handler
	 * 文字列から {@link JsonObject} を復元する.
	 * @param value JsonObject をシリアライズした文字列
	 * @param completionHandler the completion handler
	 */
	public static void toJsonObject(String value, Handler<AsyncResult<JsonObject>> completionHandler) {
		JsonObject result;
		try {
			result = new JsonObject(value);
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn(e);
			completionHandler.handle(Future.failedFuture(e));
			return;
		}
		completionHandler.handle(Future.succeededFuture(result));
	}
	/**
	 * Restores {@link JsonObject} from {@link Buffer}.
	 * @param value {@link Buffer} which gets the JsonObject serialized string
	 * @param completionHandler the completion handler
	 * {@link Buffer} から {@link JsonObject} を復元する.
	 * @param value JsonObject をシリアライズした文字列を取得する {@link Buffer}
	 * @param completionHandler the completion handler
	 */
	public static void toJsonObject(Buffer value, Handler<AsyncResult<JsonObject>> completionHandler) {
		JsonObject result;
		try {
			result = new JsonObject(value);
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn(e);
			completionHandler.handle(Future.failedFuture(e));
			return;
		}
		completionHandler.handle(Future.succeededFuture(result));
	}

	/**
	 * Restores {@link JsonArray} from string.
	 * @param value JsonArray serialized string
	 * @param completionHandler the completion handler
	 * 文字列から {@link JsonArray} を復元する.
	 * @param value JsonArray をシリアライズした文字列
	 * @param completionHandler the completion handler
	 */
	public static void toJsonArray(String value, Handler<AsyncResult<JsonArray>> completionHandler) {
		JsonArray result;
		try {
			result = new JsonArray(value);
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn(e);
			completionHandler.handle(Future.failedFuture(e));
			return;
		}
		completionHandler.handle(Future.succeededFuture(result));
	}
	/**
	 * Restores {@link JsonArray} from {@link Buffer}.
	 * @param value {@link Buffer} which gets JsonArray serialized string
	 * @param completionHandler the completion handler
	 * {@link Buffer} から {@link JsonArray} を復元する.
	 * @param value JsonArray をシリアライズした文字列を取得する {@link Buffer}
	 * @param completionHandler the completion handler
	 */
	public static void toJsonArray(Buffer value, Handler<AsyncResult<JsonArray>> completionHandler) {
		JsonArray result;
		try {
			result = new JsonArray(value);
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn(e);
			completionHandler.handle(Future.failedFuture(e));
			return;
		}
		completionHandler.handle(Future.succeededFuture(result));
	}

	////

	/**
	 * This is last resort to pass the {@link String} type default value to each API.
	 * Does not pass other {@link String} type arguments because many APIs accept variable-length {@link String} type argument as the path for the key.
	 * @author OES Project
	 * 各種 API に {@link String} 型のデフォルト値を渡すための苦肉の策.
	 * 多くの API がキーのパスとして {@link String} 型の可変長引数を受け取る仕様であるため {@link String} 型の他の引数を渡せない.
	 * @author OES Project
	 */
	public static class DefaultString {
		public final String value;
		public DefaultString(String value) {
			this.value = value;
		}
	}
	/**
	 * Sets {@link String} type default value for expressing {@code null} string.
	 * {@code null} 文字列を表す {@link String} 型デフォルト値.
	 */
	public static final DefaultString NULL_DEFAULT_STRING = new DefaultString(null);
	/**
	 * Sets {@link String} type default value for expressing empty string.
	 * 空文字列を表す {@link String} 型デフォルト値.
	 */
	public static final DefaultString EMPTY_DEFAULT_STRING = new DefaultString("");
	/**
	 * Generates default value object.
	 * @param value string to express
	 * @return default value object expressing string to be expressed
	 * デフォルト値オブジェクトの生成.
	 * @param value 表したい文字列
	 * @return 表したい文字列を表現するデフォルト値オブジェクト
	 */
	public static DefaultString defaultString(String value) {
		if (null == value) return NULL_DEFAULT_STRING;
		if (0 == value.length()) return EMPTY_DEFAULT_STRING;
		return new DefaultString(value);
	}

	////

	/**
	 * Traverses {@code keys} hierarchy from {@code jsonObject} and returns the {@link JsonObject} before the last one.
	 * Returns {@code null} if value cannot be acquired while traversing the hierarchy.
	 * Also returns {@code null}if value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param keys keys for digging into hierarchy
	 * @return {@code JsonObject} acquired by traversing the hierarchy to the level before the end of {@code keys}, or {@code null}
	 * {@code jsonObject} から {@code keys} 階層を辿って最後の一つ前の {@link JsonObject} を返す.
	 * 階層を辿る途中で値が取得できない場合は {@code null} を返す.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys 階層を掘るキー
	 * @return {@code keys} の最後から一つ前まで階層を辿って取得した {@code JsonObject} または {@code null}
	 */
	private static JsonObject minusOneJsonObject_(JsonObject jsonObject, String... keys) {
		for (int i = 0; jsonObject != null && i < keys.length - 1; i++) {
			Object subJsonObject = jsonObject.getValue(keys[i]);
			jsonObject = (subJsonObject instanceof JsonObject) ? (JsonObject) subJsonObject : null;
		}
		return jsonObject;
	}
	/**
	 * Returns the last element of {@code keys}.
	 * @param keys keys for digging into hierarchy
	 * @return last element of {@code keys}
	 * {@code keys} の最後の要素を返す.
	 * @param keys 階層を掘るキー
	 * @return {@code keys} の最後の要素
	 */
	private static String lastKey_(String... keys) {
		return keys[keys.length - 1];
	}

	/**
	 * Traverses {@code keys} hierarchy from {@link JsonObject} and returns value.
	 * Returns {@code null} if value cannot be acquired while traversing the hierarchy.
	 * Also returns {@code null}if value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link JsonObject} から {@code keys} 階層を辿って値を取得する.
	 * 階層を辿る途中で値が取得できない場合は {@code null} を返す.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した値または {@code null}
	 */
	public static Object getValue(JsonObject jsonObject, String... keys) {
		return getValue(jsonObject, null, keys);
	}
	/**
	 * Traverses {@code keys} hierarchy from {@link JsonObject} and returns value.
	 * Returns {@code null} if value cannot be acquired while traversing the hierarchy.
	 * Also returns {@code null}if value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param def default value
	 * @param keys key hierarchy
	 * @return value acquired by traversing {@code keys} hierarchy. {@code def} if it cannot be acquired
	 * {@link JsonObject} から {@code keys} 階層を辿って値を取得する.
	 * 階層を辿る途中で値が取得できない場合は {@code null} を返す.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した値. 取得できない場合は {@code def}
	 */
	public static Object getValue(JsonObject jsonObject, Object def, String... keys) {
		Object result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			result = minusOneJsonObject.getValue(lastKey_(keys));
		}
		return (result != null) ? result : def;
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link Float}.
	 * Returns {@code null} if unable to convert to {@link Float}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link Float} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link Float} で取得する.
	 * {@link Float} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Float} 値または {@code null}
	 */
	public static Float getFloat(JsonObject jsonObject, String... keys) {
		return getFloat(jsonObject, null, keys);
	}
	/**
	 * Same as {@link #getValue(JsonObject, Object, String...)} but gets results in {@link Float}.
	 * Returns ${@code def} if unable to convert to {@link Float}.
	 * @param jsonObject jsonobject object
	 * @param def default value
	 * @param keys key hierarchy
	 * @return {@link Float} value acquired by traversing {@code keys} hierarchy, or {@code def}
	 * {@link #getValue(JsonObject, Object, String...)} と同じだが結果を {@link Float} で取得する.
	 * {@link Float} に変換できない場合も ${@code def} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Float} 値または {@code def}
	 */
	public static Float getFloat(JsonObject jsonObject, Float def, String... keys) {
		Float result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = minusOneJsonObject.getFloat(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getFloat(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return (result != null) ? result : def;
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link Integer}.
	 * Returns {@code null} if unable to convert to {@link Integer}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link Integer} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link Integer} で取得する.
	 * {@link Integer} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Integer} 値または {@code null}
	 */
	public static Integer getInteger(JsonObject jsonObject, String... keys) {
		return getInteger(jsonObject, null, keys);
	}
	/**
	 * Same as {@link #getValue(JsonObject, Object, String...)} but gets results in {@link Integer}.
	 * Returns ${@code def} if unable to convert to {@link Integer}.
	 * @param jsonObject jsonobject object
	 * @param def default value
	 * @param keys key hierarchy
	 * @return {@link Integer} value acquired by traversing {@code keys} hierarchy, or {@code def} 
	 * {@link #getValue(JsonObject, Object, String...)} と同じだが結果を {@link Integer} で取得する.
	 * {@link Integer} に変換できない場合も ${@code def} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Integer} 値または {@code def}
	 */
	public static Integer getInteger(JsonObject jsonObject, Integer def, String... keys) {
		Integer result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = minusOneJsonObject.getInteger(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getInteger(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return (result != null) ? result : def;
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link Long}.
	 * Returns {@code null} if unable to convert to {@link Long}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link Long} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link Long} で取得する.
	 * {@link Long} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Long} 値または {@code null}
	 */
	public static Long getLong(JsonObject jsonObject, String... keys) {
		return getLong(jsonObject, null, keys);
	}
	/**
	 * Same as {@link #getValue(JsonObject, Object, String...)} but gets results in {@link Long}.
	 * Returns ${@code def} if unable to convert to {@link Long}.
	 * @param jsonObject jsonobject object
	 * @param def default value
	 * @param keys key hierarchy
	 * @return {@link Long} value acquired by traversing {@code keys} hierarchy, or {@code def}
	 * {@link #getValue(JsonObject, Object, String...)} と同じだが結果を {@link Long} で取得する.
	 * {@link Long} に変換できない場合も ${@code def} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Long} 値または {@code def}
	 */
	public static Long getLong(JsonObject jsonObject, Long def, String... keys) {
		Long result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = minusOneJsonObject.getLong(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getLong(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return (result != null) ? result : def;
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link String}.
	 * Returns {@code null} if unable to convert to {@link String}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link String} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link String} で取得する.
	 * {@link String} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link String} 値または {@code null}
	 */
	public static String getString(JsonObject jsonObject, String... keys) {
		return getString(jsonObject, NULL_DEFAULT_STRING, keys);
	}
	/**
	 * Same as {@link #getValue(JsonObject, Object, String...)} but gets results in {@link String}.
	 * Returns ${@code def} if unable to convert to {@link String}.
	 * @param jsonObject jsonobject object
	 * @param def default value. Passes using {@link DefaultString} object
	 * @param keys key hierarchy
	 * @return {@link String} value acquired by traversing {@code keys} hierarchy, or {@code def}
	 * {@link #getValue(JsonObject, Object, String...)} と同じだが結果を {@link String} で取得する.
	 * {@link String} に変換できない場合も ${@code def} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param def デフォルト値. {@link DefaultString} オブジェクトで渡す
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link String} 値または {@code def}
	 */
	public static String getString(JsonObject jsonObject, DefaultString def, String... keys) {
		String result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = minusOneJsonObject.getString(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getString(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return (result != null) ? result : ((def != null) ? def.value : null);
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link Boolean}.
	 * Returns {@code null} if unable to convert to {@link Boolean} .
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link Boolean} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link Boolean} で取得する.
	 * {@link Boolean} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Boolean} 値または {@code null}

	 */
	public static Boolean getBoolean(JsonObject jsonObject, String... keys) {
		return getBoolean(jsonObject, null, keys);
	}
	/**
	 * Same as {@link #getValue(JsonObject, Object, String...)} but gets results in {@link Boolean}.
	 * Returns ${@code def} if unable to convert to {@link Boolean}.
	 * @param jsonObject jsonobject object
	 * @param def default value
	 * @param keys key hierarchy
	 * @return {@link Boolean} value acquired by traversing {@code keys} hierarchy, or {@code def}
	 * {@link #getValue(JsonObject, Object, String...)} と同じだが結果を {@link Boolean} で取得する.
	 * {@link Boolean} に変換できない場合も ${@code def} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Boolean} 値または {@code def}
	 */
	public static Boolean getBoolean(JsonObject jsonObject, Boolean def, String... keys) {
		Boolean result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = minusOneJsonObject.getBoolean(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getBoolean(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return (result != null) ? result : def;
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link LocalDateTime}.
	 * Returns {@code null} if unable to convert to {@link LocalDateTime}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link LocalDateTime} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link LocalDateTime} で取得する.
	 * {@link LocalDateTime} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link LocalDateTime} 値または {@code null}
	 */
	public static LocalDateTime getLocalDateTime(JsonObject jsonObject, String... keys) {
		return getLocalDateTime(jsonObject, NULL_DEFAULT_STRING, keys);
	}
	/**
	 * Same as {@link #getValue(JsonObject, Object, String...)} but gets results in {@link LocalDateTime}.
	 * Returns {@link LocalDateTime} expressing ${@code def} even if unable to convert to {@link LocalDateTime}.
	 * @param jsonObject jsonobject object
	 * @param def default value. Passed using {@link DefaultString} object
	 * @param keys key hierarchy
	 * @return {@link LocalDateTime} value acquired by traversing {@code keys} hierarchy or {@link LocalDateTime} expressing {@code def}
	 * {@link #getValue(JsonObject, Object, String...)} と同じだが結果を {@link LocalDateTime} で取得する.
	 * {@link LocalDateTime} に変換できない場合も ${@code def} が表す {@link LocalDateTime} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param def デフォルト値. {@link DefaultString} オブジェクトで渡す
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link LocalDateTime} 値または {@code def} が表す {@link LocalDateTime}
	 */
	public static LocalDateTime getLocalDateTime(JsonObject jsonObject, DefaultString def, String... keys) {
		LocalDateTime result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = DateTimeUtil.toLocalDateTime(minusOneJsonObject.getString(lastKey_(keys)));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getLocalDateTime(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return (result != null) ? result : ((def != null) ? DateTimeUtil.toLocalDateTime(def.value) : null);
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link JsonArray}.
	 * Returns {@code null} if unable to convert to {@link JsonArray}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link JsonArray} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link JsonArray} で取得する.
	 * {@link JsonArray} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link JsonArray} 値または {@code null}
	 */
	public static JsonArray getJsonArray(JsonObject jsonObject, String... keys) {
		JsonArray result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = minusOneJsonObject.getJsonArray(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getJsonArray(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return result;
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link List} of {@link Float}.
	 * First, gets {@link JsonArray}, and then converts to {@link List} of {@link Float}.
	 * Returns {@code null} if unable to get {@link JsonArray}.
	 * For each element of {@link JsonArray}, throws away value that cannot be converted to {@link Float}. 
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link List} of {@link Float} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link Float} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link Float} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link Float} に変換できない値は捨てる.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Float} の {@link List} 値または {@code null}
	 */
	public static List<Float> getFloatList(JsonObject jsonObject, String... keys) {
		JsonArray jsonArray = getJsonArray(jsonObject, keys);
		if (jsonArray != null) {
			List<Float> result = new ArrayList<Float>(jsonArray.size());
			for (int i = 0; i < jsonArray.size(); i++) {
				try {
					result.add(jsonArray.getFloat(i));
				} catch (Exception e) {
					log.error("JsonObjectUtil.getFloatList(); keys : " + Arrays.toString(keys) + ", value : " + jsonArray.getValue(i), e);
				}
			}
			return result;
		}
		return null;
	}
	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link List} of {@link Integer}.
	 * First, gets {@link JsonArray}, and then converts to {@link List} of {@link Integer}.
	 * Returns {@code null} if unable to get {@link JsonArray}.
	 * For each element of {@link JsonArray}, throws away value that cannot be converted to {@link Integer}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link List} of {@link Integer} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link Integer} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link Integer} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link Integer} に変換できない値は捨てる.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Integer} の {@link List} 値または {@code null}
	 */
	public static List<Integer> getIntegerList(JsonObject jsonObject, String... keys) {
		JsonArray jsonArray = getJsonArray(jsonObject, keys);
		if (jsonArray != null) {
			List<Integer> result = new ArrayList<Integer>(jsonArray.size());
			for (int i = 0; i < jsonArray.size(); i++) {
				try {
					result.add(jsonArray.getInteger(i));
				} catch (Exception e) {
					log.error("JsonObjectUtil.getIntegerList(); keys : " + Arrays.toString(keys) + ", value : " + jsonArray.getValue(i), e);
				}
			}
			return result;
		}
		return null;
	}
	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link List} of {@link String}.
	 * First, gets {@link JsonArray}, and then converts to {@link List} of {@link String}.
	 * Returns {@code null} if unable to get {@link JsonArray}.
	 * For each element of {@link JsonArray}, throws away value that cannot be converted to {@link String}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link List} of {@link String} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link String} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link String} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link String} に変換できない値は捨てる.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link String} の {@link List} 値または {@code null}
	 */
	public static List<String> getStringList(JsonObject jsonObject, String... keys) {
		JsonArray jsonArray = getJsonArray(jsonObject, keys);
		if (jsonArray != null) {
			List<String> result = new ArrayList<String>(jsonArray.size());
			for (int i = 0; i < jsonArray.size(); i++) {
				try {
					result.add(jsonArray.getString(i));
				} catch (Exception e) {
					log.error("JsonObjectUtil.getStringList(); keys : " + Arrays.toString(keys) + ", value : " + jsonArray.getValue(i), e);
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * Same as {@link #getValue(JsonObject, String...)} but gets results in {@link JsonObject}.
	 * Returns {@code null} if unable to convert to {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return {@link JsonObject} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(JsonObject, String...)} と同じだが結果を {@link JsonObject} で取得する.
	 * {@link JsonObject} に変換できない場合も {@code null} を返す.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link JsonObject} 値または {@code null}
	 */
	public static JsonObject getJsonObject(JsonObject jsonObject, String... keys) {
		JsonObject result = null;
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject != null) {
			try {
				result = minusOneJsonObject.getJsonObject(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.getJsonObject(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
		}
		return result;
	}

	////

	/**
	 * Removes value traversed in {@code keys} hierarchy from {@link JsonObject}.
	 * Ends process if value cannot be acquired while traversing the hierarchy.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return removed object or {@code null}
	 * {@link JsonObject} から {@code keys} 階層を辿った値を削除する.
	 * 階層を辿る途中で値が取得できなければそこで処理を終了する.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return 削除したオブジェクトまたは {@code null}
	 */
	public static Object remove(JsonObject jsonObject, String... keys) {
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		return (minusOneJsonObject != null) ? minusOneJsonObject.remove(lastKey_(keys)) : null;
	}
	/**
	 * Same as {@link #remove(JsonObject, String...)} but removes only when the value is {@link JsonArray}.
	 * Ends process if value cannot be acquired while traversing the hierarchy.
	 * Does not remove when value is not {@link JsonArray}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return removed {@link JsonArray} object or {@code null}
	 * {@link #remove(JsonObject, String...)} と同じだが値が {@link JsonArray} である場合のみ削除する.
	 * 階層を辿る途中で値が取得できなければそこで処理を終了する.
	 * 値が {@link JsonArray} でない場合は削除しない.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return 削除した {@link JsonArray} オブジェクトまたは {@code null}
	 */
	public static JsonArray removeJsonArray(JsonObject jsonObject, String... keys) {
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject == null) return null;
		Object obj = minusOneJsonObject.getValue(lastKey_(keys));
		return (obj instanceof JsonArray) ? (JsonArray) minusOneJsonObject.remove(lastKey_(keys)) : null;
	}
	/**
	 * Same as {@link #remove(JsonObject, String...)} but removes only when the value is {@link JsonObject}.
	 * Ends process if value cannot be acquired while traversing the hierarchy.
	 * Does not remove when value is not {@link JsonArray}.
	 * @param jsonObject jsonobject object
	 * @param keys key hierarchy
	 * @return removed {@link JsonObject} object or {@code null}
	 * {@link #remove(JsonObject, String...)} と同じだが値が {@link JsonObject} である場合のみ削除する.
	 * 階層を辿る途中で値が取得できなければそこで処理を終了する.
	 * 値が {@link JsonArray} でない場合は削除しない.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys キーの階層
	 * @return 削除した {@link JsonObject} オブジェクトまたは {@code null}
	 */
	public static JsonObject removeJsonObject(JsonObject jsonObject, String... keys) {
		JsonObject minusOneJsonObject = minusOneJsonObject_(jsonObject, keys);
		if (minusOneJsonObject == null) return null;
		Object obj = minusOneJsonObject.getValue(lastKey_(keys));
		return (obj instanceof JsonObject) ? (JsonObject) minusOneJsonObject.remove(lastKey_(keys)) : null;
	}

	/**
	 * Traverses {@code keys} hierarchy from {@code jsonObject} and returns the {@link JsonObject} before the last one.
	 * Creates new {@link JsonObject} if value cannot be acquired while traversing the hierarchy.
	 * Also creates new {@link JsonObject} and replaces when value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param keys keys for digging into hierarchy
	 * @return {@code JsonObject} created or acquired by traversing the hierarchy to the level before the end of {@code keys}
	 * {@code jsonObject} から {@code keys} 階層を辿って最後の一つ前の {@link JsonObject} を返す.
	 * 階層を辿る途中で値が取得できない場合は新しく {@link JsonObject} を作る.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も新しく {@link JsonObject} を作り差し替える.
	 * @param jsonObject jsonobject オブジェクト
	 * @param keys 階層を掘るキー
	 * @return {@code keys} の最後から一つ前まで階層を辿って取得もしくは作成した {@code JsonObject}
	 */
	private static JsonObject minusOneJsonObjectCreateIfNot_(JsonObject jsonObject, String... keys) {
		for (int i = 0; jsonObject != null && i < keys.length - 1; i++) {
			Object subJsonObject = jsonObject.getValue(keys[i]);
			if (!(subJsonObject instanceof JsonObject)) {
				subJsonObject = new JsonObject();
				jsonObject.put(keys[i], subJsonObject);
			}
			jsonObject = (JsonObject) subJsonObject;
		}
		return jsonObject;
	}

	/**
	 * Digs into {@code keys} hierarchy and places value in {@link JsonObject}.
	 * Creates new {@link JsonObject} if value cannot be acquired while traversing the hierarchy.
	 * Also creates new {@link JsonObject} and replaces when value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param value value to be placed
	 * @param keys key hierarchy
	 * {@link JsonObject} に {@code keys} 階層を掘って値を配置する.
	 * 階層を辿る途中で値が取得できない場合は新しく {@link JsonObject} を作る.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も新しく {@link JsonObject} を作り差し替える.
	 * @param jsonObject jsonobject オブジェクト
	 * @param value 配置する値
	 * @param keys キーの階層
	 */
	public static void put(JsonObject jsonObject, Object value, String... keys) {
		JsonObject minusOneJsonObject = minusOneJsonObjectCreateIfNot_(jsonObject, keys);
		minusOneJsonObject.put(lastKey_(keys), value);
	}

	/**
	 * Digs into {@code keys} hierarchy and places value in {@link JsonObject}.
	 * Carries out deep merge using {@link JsonObject#mergeIn(JsonObject, boolean)} if {@link JsonObject} already exists.
	 * Creates {@link JsonObject} if value cannot be acquired while traversing the hierarchy.
	 * Also creates new {@link JsonObject} and replaces when value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param value jsonobject object to be placed
	 * @param keys key hierarchy
	 * {@link JsonObject} に {@code keys} 階層を掘って値を配置する.
	 * 既に {@link JsonObject} が存在する場合は {@link JsonObject#mergeIn(JsonObject, boolean)} で深くマージする.
	 * 階層を辿る途中で値が取得できない場合は新しく {@link JsonObject} を作る.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も新しく {@link JsonObject} を作り差し替える.
	 * @param jsonObject jsonobject オブジェクト
	 * @param value 配置する jsonobject オブジェクト
	 * @param keys キーの階層
	 */
	public static void mergeIn(JsonObject jsonObject, JsonObject value, String... keys) {
		if (value != null) {
			JsonObject minusOneJsonObject = minusOneJsonObjectCreateIfNot_(jsonObject, keys);
			JsonObject lastJsonObject = null;
			try {
				lastJsonObject = minusOneJsonObject.getJsonObject(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.mergeIn(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
			if (lastJsonObject == null) {
				minusOneJsonObject.put(lastKey_(keys), value);
			} else {
				lastJsonObject.mergeIn(value, true);
			}
		} else {
			if (log.isWarnEnabled()) log.warn("JsonObjectUtil#mergeIn(); value is null; keys : " + Arrays.toString(keys));
		}
	}

	/**
	 * Digs into {@code keys} hierarchy and places value in {@link JsonObject}.
	 * Adds element if {@link JsonArray} already exists.
	 * If value does not exist, creates new {@link JsonArray} and adds element.
	 * Creates {@link JsonObject} if value cannot be acquired while traversing the hierarchy.
	 * Also creates new {@link JsonObject} and replaces if value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param jsonObject jsonobject object
	 * @param value value to be added as element of {@link JsonArray}
	 * @param keys key hierarchy
	 * {@link JsonObject} に {@code keys} 階層を掘って値を配置する.
	 * 既に {@link JsonArray} が存在する場合は要素を追加する.
	 * 値が存在しない場合は新しく {@link JsonArray} を作り要素を追加する.
	 * 階層を辿る途中で値が取得できない場合は新しく {@link JsonObject} を作る.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も新しく {@link JsonObject} を作り差し替える.
	 * @param jsonObject jsonobject オブジェクト
	 * @param value {@link JsonArray} の要素として追加する値
	 * @param keys キーの階層
	 */
	public static void add(JsonObject jsonObject, Object value, String... keys) {
		if (value != null) {
			JsonObject minusOneJsonObject = minusOneJsonObjectCreateIfNot_(jsonObject, keys);
			JsonArray jsonArray = null;
			try {
				jsonArray = minusOneJsonObject.getJsonArray(lastKey_(keys));
			} catch (Exception e) {
				log.error("JsonObjectUtil.add(); keys : " + Arrays.toString(keys) + ", value : " + minusOneJsonObject.getValue(lastKey_(keys)), e);
			}
			if (jsonArray == null) {
				jsonArray = new JsonArray();
				minusOneJsonObject.put(lastKey_(keys), jsonArray);
			}
			jsonArray.add(value);
		} else {
			if (log.isWarnEnabled()) log.warn("JsonObjectUtil#add(); value is null; keys : " + Arrays.toString(keys));
		}
	}

}

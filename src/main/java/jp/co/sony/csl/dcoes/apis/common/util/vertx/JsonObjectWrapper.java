package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.JsonObjectUtil.DefaultString;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implements wrapper that uses {@link JsonObjectUtil} to access inner {@link JsonObject}.
 * The wrapper is more convenient than plain {@link JsonObject} in the following way.
 * - Can carry out multi-level access by arranging keys 
 * @author OES Project
 * {@link JsonObjectUtil} を使って内部の {@link JsonObject} にアクセスする wrapper.
 * 素の {@link JsonObject} と比べて以下の点で便利.
 * - キーを並べて多階層アクセスできる
 * @author OES Project
 */
public class JsonObjectWrapper {
	private static final Logger log = LoggerFactory.getLogger(JsonObjectWrapper.class);

	private JsonObject jsonObject_;

	/**
	 * Creates empty instance.
	 * 空のインスタンスを作成する.
	 */
	public JsonObjectWrapper() { }
	/**
	 * Creates instance having specified {@link JsonObject}.
	 * @param value jsonobject object
	 * 指定した {@link JsonObject} を持つインスタンスを作成する.
	 * @param value jsonobject オブジェクト
	 */
	public JsonObjectWrapper(JsonObject value) {
		setJsonObject(value);
	}

	/**
	 * Replaces inner {@link JsonObject}.
	 * @param value jsonobject object
	 * @return a reference to this, so the API can be used fluently
	 * 内部の {@link JsonObject} を置き換える.
	 * @param value jsonobject オブジェクト
	 * @return a reference to this, so the API can be used fluently
	 */
	public JsonObjectWrapper setJsonObject(JsonObject value) {
		jsonObject_ = value;
		return this;
	}
	/**
	 * Gets inner {@link JsonObject}.
	 * @return jsonobject object
	 * 内部の {@link JsonObject} を取得する.
	 * @return jsonobject オブジェクト
	 */
	public JsonObject jsonObject() {
		return jsonObject_;
	}
	/**
	 * Determines whether or not {@link JsonObject} is {@code null}.
	 * Empty {@link JsonObject} is {@code false}.
	 * @return true if boolean null.
	 *         false if not null, even if empty.
	 * 内部の {@link JsonObject} が {@code null} か否か.
	 * 空の {@link JsonObject} は {@code false}.
	 * @return boolean null なら true.
	 *         null でなければ空っぽでも false.
	 */
	public boolean isNull() {
		return jsonObject_ == null;
	}
	/**
	 * Determines whether or not inner {@link JsonObject} is {@code null} or empty {@link JsonObject}.
	 * @return true if boolean null or empty {@link JsonObject}
	 * 内部の {@link JsonObject} が {@code null} もしくは空の {@link JsonObject} か否か.
	 * @return boolean null または空っぽの {@link JsonObject} なら true
	 */
	public boolean isEmpty() {
		return (jsonObject_ == null || jsonObject_.isEmpty());
	}
	/**
	 *  Empties content.
	 * @return a reference to this, so the API can be used fluently
	 *  内容を空にする.
	 */
	public JsonObjectWrapper reset() {
		jsonObject_ = null;
		return this;
	}

	/**
	 * Gets value using {@code key}.
	 * @param key key
	 * @return value acquired with {@code key}.
	 * {@code key} で値を取得する.
	 * @param key キー
	 * @return {@code key} で取得した値.
	 */
	public Object getValue(String key) {
		return getValue(null, key);
	}
	/**
	 * Gets value using {@code key}.
	 * Returns {@code def} if result is null.
	 * @param def default value
	 * @param key key
	 * @return value acquired with {@code key}, or {@code def}
	 * {@code key} で値を取得する.
	 * 結果が null の場合は {@code def} を返す.
	 * @param def デフォルト値
	 * @param key キー
	 * @return {@code key} で取得した値または {@code def}
	 */
	public Object getValue(Object def, String key) {
		return JsonObjectUtil.getValue(jsonObject_, def, key);
	}
	/**
	 * Gets value by traversing hierachy using {@code keys}.
	 * @param keys key hierachy
	 * @return value acquired by traversing {@code keys} hierarchy.
	 * {@code keys} で階層を辿って値を取得する.
	 * @param keys key hierachy
	 * @return {@code keys} 階層を辿って取得した値.
	 */
	public Object getValue(String... keys) {
		return getValue(null, keys);
	}
	/**
	 * Gets value by traversing hierachy using {@code keys}.
	 * Returns {@code def} if result is null.
	 * @param def default value
	 * @param keys key hierachy
	 * @return value acquired by traversing {@code keys} hierarchy or {@code def}
	 * {@code keys} で階層を辿って値を取得する.
	 * 結果が null の場合は {@code def} を返す.
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した値または {@code def}
	 */
	public Object getValue(Object def, String... keys) {
		return JsonObjectUtil.getValue(jsonObject_, def, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link Float}.
	 * Returns {@code null} if unable to convert to {@link Float}.
	 * @param key key
	 * @return {@link Float} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link Float}.
	 * {@link Float} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link Float} 値または {@code null}
	 */
	public Float getFloat(String key) {
		return getFloat(null, key);
	}
	/**
	 * Same as {@link #getValue(Object, String)} but gets results in {@link Float}.
	 * Returns {@code def} if unable to convert to {@link Float}.
	 * @param def default value
	 * @param key key
	 * @return {@link Float} value acquired with {@code key}, or {@code def}
	 * {@link #getValue(Object, String)} と同じだが結果を {@link Float} で取得する.
	 * {@link Float} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param key キー
	 * @return {@code key} で取得した {@link Float} 値または {@code def}
	 */
	public Float getFloat(Float def, String key) {
		return JsonObjectUtil.getFloat(jsonObject_, def, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link Float}.
	 * Returns {@code null} if unable to convert to {@link Float}.
	 * @param keys key hierachy
	 * @return {@link Float} value acquired by by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link Float}.
	 * {@link Float} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Float} 値または {@code null}
	 */
	public Float getFloat(String... keys) {
		return getFloat(null, keys);
	}
	/**
	 * Same as {@link #getValue(Object, String...)} but gets results in {@link Float}.
	 * Returns {@code def} if unable to convert to {@link Float}.
	 * @param def default value
	 * @param keys key hierachy
	 * @return {@link Float} value acquired by traversing {@code keys} hierarchy, or {@code def}
	 * {@link #getValue(Object, String...)} と同じだが結果を {@link Float} で取得する.
	 * {@link Float} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Float} 値または {@code def}
	 */
	public Float getFloat(Float def, String... keys) {
		return JsonObjectUtil.getFloat(jsonObject_, def, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link Integer}.
	 * Returns {@code null} if unable to convert to {@link Integer}.
	 * @param key key
	 * @return {@link Integer} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link Integer} で取得する.
	 * {@link Integer} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link Integer} 値または {@code null}
	 */
	public Integer getInteger(String key) {
		return getInteger(null, key);
	}
	/**
	 * Same as {@link #getValue(Object, String)} but gets results in {@link Integer}.
	 * Returns {@code def} if unable to convert to {@link Integer}.
	 * @param def default value
	 * @param key key
	 * @return {@link Integer} value acquired with {@code key}, or {@code def}
	 * {@link #getValue(Object, String)} と同じだが結果を {@link Integer} で取得する.
	 * {@link Integer} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param key キー
	 * @return {@code key} で取得した {@link Integer} 値または {@code def}
	 */
	public Integer getInteger(Integer def, String key) {
		return JsonObjectUtil.getInteger(jsonObject_, def, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link Integer}.
	 * Returns {@code null} if unable to convert to {@link Integer}.
	 * @param keys key hierachy
	 * @return {@link Integer} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link Integer} で取得する.
	 * {@link Integer} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Integer} 値または {@code null}
	 */
	public Integer getInteger(String... keys) {
		return getInteger(null, keys);
	}
	/**
	 * Same as {@link #getValue(Object, String...)} but gets results in {@link Integer}.
	 * Returns {@code def} if unable to convert to {@link Integer}.
	 * @param def default value
	 * @param keys key hierachy
	 * @return {@link Integer} value acquired by traversing {@code keys} hierarchy, or {@code def}
	 * {@link #getValue(Object, String...)} と同じだが結果を {@link Integer} で取得する.
	 * {@link Integer} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Integer} 値または {@code def}
	 */
	public Integer getInteger(Integer def, String... keys) {
		return JsonObjectUtil.getInteger(jsonObject_, def, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link Long}.
	 * Returns {@code null} if unable to convert to {@link Long}.
	 * @param key key
	 * @return {@link Long} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link Long} で取得する.
	 * {@link Long} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link Long} 値または {@code null}
	 */
	public Long getLong(String key) {
		return getLong(null, key);
	}
	/**
	 * Same as {@link #getValue(Object, String)} but gets results in {@link Long}.
	 * Returns {@code def} if unable to convert to {@link Long}.
	 * @param def default value
	 * @param key key
	 * @return {@link Long} value acquired with {@code key}, or {@code def}
	 * {@link #getValue(Object, String)} と同じだが結果を {@link Long} で取得する.
	 * {@link Long} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param key キー
	 * @return {@code key} で取得した {@link Long} 値または {@code def}
	 */
	public Long getLong(Long def, String key) {
		return JsonObjectUtil.getLong(jsonObject_, def, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link Long}.
	 * Returns {@code null} if unable to convert to {@link Long}.
	 * @param keys key hierachy
	 * @return {@link Long} value acquired by traversing {@code keys} hierarchy}, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link Long} で取得する.
	 * {@link Long} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Long} 値または {@code null}
	 */
	public Long getLong(String... keys) {
		return getLong(null, keys);
	}
	/**
	 * Same as {@link #getValue(Object, String...)} but gets results in {@link Long}.
	 * Returns {@code def} if unable to convert to {@link Long}.
	 * @param def default value
	 * @param keys key hierachy
	 * @return {@link Long} value acquired by traversing {@code keys} hierarchy, or {@code def}
	 * {@link #getValue(Object, String...)} と同じだが結果を {@link Long} で取得する.
	 * {@link Long} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Long} 値または {@code def}
	 */
	public Long getLong(Long def, String... keys) {
		return JsonObjectUtil.getLong(jsonObject_, def, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link String}.
	 * Returns {@code null} if unable to convert to {@link String}.
	 * @param key key
	 * @return {@link String} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link String} で取得する.
	 * {@link String} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link String} 値または {@code null}
	 */
	public String getString(String key) {
		return getString(JsonObjectUtil.NULL_DEFAULT_STRING, key);
	}
	/**
	 * Same as {@link #getValue(Object, String)} but gets results in {@link String}.
	 * Returns {@link String} expressing {@code def} if unable to convert to {@link String}.
	 * @param def default value. Passes using {@link DefaultString} object
	 * @param key key
	 * @return {@code key} で取得した {@link String} value acquired with {@code key}, or {@link String} expressing {@code def}
	 * {@link #getValue(Object, String)} と同じだが結果を {@link String} で取得する.
	 * {@link String} に変換できない場合も {@code def} が表す {@link String} を返す.
	 * @param def デフォルト値. {@link DefaultString} オブジェクトで渡す
	 * @param key キー
	 * @return {@code key} で取得した {@link String} 値または {@code def} が表す {@link String}
	 */
	public String getString(JsonObjectUtil.DefaultString def, String key) {
		return JsonObjectUtil.getString(jsonObject_, def, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link String}.
	 * Returns {@code null} if unable to convert to {@link String}.
	 * @param keys key hierachy
	 * @return {@link String} value acquired by traversing {@code keys} hierarchy}, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link String} で取得する.
	 * {@link String} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link String} 値または {@code null}
	 */
	public String getString(String... keys) {
		return getString(JsonObjectUtil.NULL_DEFAULT_STRING, keys);
	}
	/**
	 * Same as {@link #getValue(Object, String...)} but gets results in {@link String}.
	 * Returns {@link String} expressing {@code def} if unable to convert to {@link String} 
	 * @param def default value. Passes using {@link DefaultString} object
	 * @param keys key hierachy
	 * @return {@link String} value acquired by traversing {@code keys} hierarchy}, or {@link String} expressing {@code def}
	 * {@link #getValue(Object, String...)} と同じだが結果を {@link String} で取得する.
	 * {@link String} に変換できない場合も {@code def} が表す {@link String} を返す.
	 * @param def デフォルト値. {@link DefaultString} オブジェクトで渡す
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link String} 値または {@code def} が表す {@link String}
	 */
	public String getString(JsonObjectUtil.DefaultString def, String... keys) {
		return JsonObjectUtil.getString(jsonObject_, def, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link Boolean}.
	 * Returns {@code null} if unable to convert to {@link Boolean}.
	 * @param key key
	 * @return {@link Boolean} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link Boolean} で取得する.
	 * {@link Boolean} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link Boolean} 値または {@code null}
	 */
	public Boolean getBoolean(String key) {
		return getBoolean(null, key);
	}
	/**
	 * Same as {@link #getValue(Object, String)} but gets results in {@link Boolean}.
	 * Returns {@code def} if unable to convert to {@link Boolean}.
	 * @param def default value
	 * @param key key
	 * @return {@link Boolean} value acquired with {@code key}, or {@code def}
	 * {@link #getValue(Object, String)} と同じだが結果を {@link Boolean} で取得する.
	 * {@link Boolean} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param key キー
	 * @return {@code key} で取得した {@link Boolean} 値または {@code def}
	 */
	public Boolean getBoolean(Boolean def, String key) {
		return JsonObjectUtil.getBoolean(jsonObject_, def, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link Boolean}.
	 * Returns {@code null} if unable to convert to {@link Boolean}.
	 * @param keys key hierachy
	 * @return {@link Boolean} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link Boolean} で取得する.
	 * {@link Boolean} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Boolean} 値または {@code null}
	 */
	public Boolean getBoolean(String... keys) {
		return getBoolean(null, keys);
	}
	/**
	 * Same as {@link #getValue(Object, String...)} but gets results in {@link Boolean}.
	 * Returns {@code def} if unable to convert to {@link Boolean} .
	 * @param def default value
	 * @param keys key hierachy
	 * @return by traversing {@code keys} hierarchy {@link Boolean} value acquired with {@code key}, or {@code def}
	 * {@link #getValue(Object, String...)} と同じだが結果を {@link Boolean} で取得する.
	 * {@link Boolean} に変換できない場合も {@code def} を返す.
	 * @param def デフォルト値
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Boolean} 値または {@code def}
	 */
	public Boolean getBoolean(Boolean def, String... keys) {
		return JsonObjectUtil.getBoolean(jsonObject_, def, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link LocalDateTime}.
	 * Returns {@code null} if unable to convert to {@link LocalDateTime}.
	 * @param key key
	 * @return {@link LocalDateTime} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link LocalDateTime} で取得する.
	 * {@link LocalDateTime} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link LocalDateTime} 値または {@code null}
	 */
	public LocalDateTime getLocalDateTime(String key) {
		return getLocalDateTime(JsonObjectUtil.NULL_DEFAULT_STRING, key);
	}
	/**
	 * Same as {@link #getValue(Object, String)} but gets results in {@link LocalDateTime}.
	 * Returns {@link LocalDateTime} expressing {@code def} even if unable to convert to {@link LocalDateTime}.
	 * @param def default value. Passes using {@link DefaultString} object
	 * @param key key
	 * @return {@link LocalDateTime} value acquired with {@code key}, or {{@link LocalDateTime} expressing @code def}
	 * {@link #getValue(Object, String)} と同じだが結果を {@link LocalDateTime} で取得する.
	 * {@link LocalDateTime} に変換できない場合も {@code def} が表す {@link LocalDateTime} を返す.
	 * @param def デフォルト値. {@link DefaultString} オブジェクトで渡す
	 * @param key キー
	 * @return {@code key} で取得した {@link LocalDateTime} 値または {@code def} が表す {@link LocalDateTime}
	 */
	public LocalDateTime getLocalDateTime(JsonObjectUtil.DefaultString def, String key) {
		return JsonObjectUtil.getLocalDateTime(jsonObject_, def, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link LocalDateTime}.
	 * Returns {@code null} if unable to convert to {@link LocalDateTime}.
	 * @param keys key hierachy
	 * @return {@link LocalDateTime} value acquired by traversing {@code keys} hierarchy}, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link LocalDateTime} で取得する.
	 * {@link LocalDateTime} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link LocalDateTime} 値または {@code null}
	 */
	public LocalDateTime getLocalDateTime(String... keys) {
		return getLocalDateTime(JsonObjectUtil.NULL_DEFAULT_STRING, keys);
	}
	/**
	 * Same as {@link #getValue(Object, String...)} but gets results in {@link LocalDateTime}.
	 * Returns {@link LocalDateTime} expressing {@code def} even if unable to convert to {@link LocalDateTime}.
	 * @param def default value. Passes using {@link DefaultString} object
	 * @param keys key hierachy
	 * @return {@link LocalDateTime} value acquired by traversing {@code keys} hierarchy, or {@link LocalDateTime} expressing {@code def}
	 * {@link #getValue(Object, String...)} と同じだが結果を {@link LocalDateTime} で取得する.
	 * {@link LocalDateTime} に変換できない場合も {@code def} が表す {@link LocalDateTime} を返す.
	 * @param def デフォルト値. {@link DefaultString} オブジェクトで渡す
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link LocalDateTime} 値または {@code def} が表す {@link LocalDateTime}
	 */
	public LocalDateTime getLocalDateTime(JsonObjectUtil.DefaultString def, String... keys) {
		return JsonObjectUtil.getLocalDateTime(jsonObject_, def, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link JsonArray}.
	 * Returns {@code null} if unable to convert to {@link JsonArray}.
	 * @param key key
	 * @return {@link JsonArray} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link JsonArray} で取得する.
	 * {@link JsonArray} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link JsonArray} 値または {@code null}
	 */
	public JsonArray getJsonArray(String key) {
		return JsonObjectUtil.getJsonArray(jsonObject_, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link JsonArray}.
	 * Returns {@code null} if unable to convert to {@link JsonArray}.
	 * @param keys key hierachy
	 * @return {@link JsonArray} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link JsonArray} で取得する.
	 * {@link JsonArray} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link JsonArray} 値または {@code null}
	 */
	public JsonArray getJsonArray(String... keys) {
		return JsonObjectUtil.getJsonArray(jsonObject_, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link List} of {@link Float}.
	 * First, gets {@link JsonArray}, then converts to {@link List} of {@link Float}.
	 * Returns {@code null} if unable to acquire {@link JsonArray}.
	 * For each element in {@link JsonArray}, throws away value that cannot be converted to {@link Float}.
	 * @param key key
	 * @return {@link List} of {@link Float} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link Float} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link Float} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link Float} に変換できない値は捨てる.
	 * @param key キー
	 * @return {@code key} で取得した {@link Float} の {@link List} 値または {@code null}
	 */
	public List<Float> getFloatList(String key) {
		return JsonObjectUtil.getFloatList(jsonObject_, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link List} of {@link Float}.
	 * First, gets {@link JsonArray}, then converts to {@link List} of {@link Float}.
	 * Returns {@code null} if unable to acquire {@link JsonArray}.
	 * For each element in {@link JsonArray}, throws away value that cannot be converted to {@link Float}
	 * @param keys key hierachy
	 * @return {@link List} of {@link Float} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link Float} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link Float} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link Float} に変換できない値は捨てる.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Float} の {@link List} 値または {@code null}
	 */
	public List<Float> getFloatList(String... keys) {
		return JsonObjectUtil.getFloatList(jsonObject_, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link List} of {@link Integer}.
	 * First, gets {@link JsonArray}, then converts to {@link List} of {@link Integer}.
	 * Returns {@code null} if unable to acquire {@link JsonArray}.
	 * For each element in {@link JsonArray}, throws away value that cannot be converted to {@link Integer}.
	 * @param key key
	 * @return {@link List} of {@link Integer} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link Integer} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link Integer} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link Integer} に変換できない値は捨てる.
	 * @param key キー
	 * @return {@code key} で取得した {@link Integer} の {@link List} 値または {@code null}
	 */
	public List<Integer> getIntegerList(String key) {
		return JsonObjectUtil.getIntegerList(jsonObject_, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link List} of {@link Integer}.
	 * First, gets {@link JsonArray}, then converts to {@link List} of {@link Integer}.
	 * Returns {@code null} if unable to acquire {@link JsonArray}.
	 * For each element in {@link JsonArray}, throws away value that cannot be converted to {@link Integer}.
	 * @param keys key hierachy
	 * @return {@link List} of {@link Integer} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link Integer} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link Integer} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link Integer} に変換できない値は捨てる.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link Integer} の {@link List} 値または {@code null}
	 */
	public List<Integer> getIntegerList(String... keys) {
		return JsonObjectUtil.getIntegerList(jsonObject_, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link List} of {@link String}.
	 * First, gets {@link JsonArray}then converts to {@link List} of {@link String}.
	 * Returns {@code null} if unable to acquire {@link JsonArray}.
	 * For each element in {@link JsonArray}, throws away value that cannot be converted to {@link String}.
	 * @param key key
	 * @return {@link List} of {@link String} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link String} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link String} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link String} に変換できない値は捨てる.
	 * @param key キー
	 * @return {@code key} で取得した {@link String} の {@link List} 値または {@code null}
	 */
	public List<String> getStringList(String key) {
		return JsonObjectUtil.getStringList(jsonObject_, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link List} of {@link String}.
	 * First, gets {@link JsonArray}then converts to {@link List} of {@link String}.
	 * Returns {@code null} if unable to acquire {@link JsonArray}.
	 * For each element in {@link JsonArray}, throws away value that cannot be converted to {@link String}.
	 * @param keys key hierachy
	 * @return by traversing {@code keys} hierarchy {@link List} of {@link String} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link String} の {@link List} で取得する.
	 * まず {@link JsonArray} を取得し {@link String} の {@link List} に変換する.
	 * {@link JsonArray} が取得できない場合は {@code null} を返す.
	 * {@link JsonArray} の各要素のうち {@link String} に変換できない値は捨てる.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link String} の {@link List} 値または {@code null}
	 */
	public List<String> getStringList(String... keys) {
		return JsonObjectUtil.getStringList(jsonObject_, keys);
	}

	/**
	 * Same as {@link #getValue(String)} but gets results in {@link JsonObject}.
	 * Returns {@code null} if unable to convert to {@link JsonObject}.
	 * @param key key
	 * @return {@link JsonObject} value acquired with {@code key}, or {@code null}
	 * {@link #getValue(String)} と同じだが結果を {@link JsonObject} で取得する.
	 * {@link JsonObject} に変換できない場合も {@code null} を返す.
	 * @param key キー
	 * @return {@code key} で取得した {@link JsonObject} 値または {@code null}
	 */
	public JsonObject getJsonObject(String key) {
		return JsonObjectUtil.getJsonObject(jsonObject_, key);
	}
	/**
	 * Same as {@link #getValue(String...)} but gets results in {@link JsonObject}.
	 * Returns {@code null} if unable to convert to {@link JsonObject}.
	 * @param keys key hierachy
	 * @return {@link JsonObject} value acquired by traversing {@code keys} hierarchy, or {@code null}
	 * {@link #getValue(String...)} と同じだが結果を {@link JsonObject} で取得する.
	 * {@link JsonObject} に変換できない場合も {@code null} を返す.
	 * @param keys キーの階層
	 * @return {@code keys} 階層を辿って取得した {@link JsonObject} 値または {@code null}
	 */
	public JsonObject getJsonObject(String... keys) {
		return JsonObjectUtil.getJsonObject(jsonObject_, keys);
	}

	////

	/**
	 * Removes value traversed in {@code keys} hierarchy.
	 * Ends process if value cannot be acquired while traversing the hierarchy.
	 * @param keys key hierachy
	 * @return removed object or {@code null}
	 * {@code keys} 階層を辿った値を削除する.
	 * 階層を辿る途中で値が取得できなければそこで処理を終了する.
	 * 削除が成功したら削除したオブジェクトを返す.
	 * @param keys キーの階層
	 * @return 削除したオブジェクトまたは {@code null}
	 */
	public synchronized Object remove(String... keys) {
		if (jsonObject_ != null) {
			JsonObject newJsonObject = jsonObject_.copy();
			Object result = JsonObjectUtil.remove(newJsonObject, keys);
			jsonObject_ = newJsonObject;
			return result;
		} else {
			return null;
		}
	}
	/**
	 * Same as {@link #remove(String...)} but removes only when the value is {@link JsonArray}.
	 * Ends process if value cannot be acquired while traversing the hierarchy.
	 * Does not remove when value is not {@link JsonArray}.
	 * Returns removed object if removal is successful.
	 * @param keys key hierachy
	 * @return removed {@link JsonArray} object or {@code null}
	 * {@link #remove(String...)} と同じだが値が {@link JsonArray} である場合のみ削除する.
	 * 階層を辿る途中で値が取得できなければそこで処理を終了する.
	 * 値が {@link JsonArray} でない場合は削除しない.
	 * 削除が成功したら削除したオブジェクトを返す.
	 * @param keys キーの階層
	 * @return 削除した {@link JsonArray} オブジェクトまたは {@code null}
	 */
	public synchronized JsonArray removeJsonArray(String... keys) {
		if (jsonObject_ != null) {
			JsonObject newJsonObject = jsonObject_.copy();
			JsonArray result = JsonObjectUtil.removeJsonArray(newJsonObject, keys);
			jsonObject_ = newJsonObject;
			return result;
		} else {
			return null;
		}
	}
	/**
	 * Same as {@link #remove(String...)} but removes only when the value is {@link JsonObject}.
	 * Ends process if value cannot be acquired while traversing the hierarchy.
	 * Does not remove when value is not {@link JsonArray}.
	 * Returns removed object if removal is successful.
	 * @param keys key hierachy
	 * @return removed {@link JsonObject} object or {@code null}
	 * {@link #remove(String...)} と同じだが値が {@link JsonObject} である場合のみ削除する.
	 * 階層を辿る途中で値が取得できなければそこで処理を終了する.
	 * 値が {@link JsonObject} でない場合は削除しない.
	 * 削除が成功したら削除したオブジェクトを返す.
	 * @param keys キーの階層
	 * @return 削除した {@link JsonObject} オブジェクトまたは {@code null}
	 */
	public synchronized JsonObject removeJsonObject(String... keys) {
		if (jsonObject_ != null) {
			JsonObject newJsonObject = jsonObject_.copy();
			JsonObject result = JsonObjectUtil.removeJsonObject(newJsonObject, keys);
			jsonObject_ = newJsonObject;
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Digs into {@code keys} hierarchy and places value.
	 * Creates new {@link JsonObject} if value cannot be acquired while traversing the hierarchy.
	 * Also creates new {@link JsonObject} and replaces if value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param value to be placed
	 * @param keys key hierachy
	 * {@code keys} 階層を掘って値を配置する.
	 * 階層を辿る途中で値が取得できない場合は新しく {@link JsonObject} を作る.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も新しく {@link JsonObject} を作り差し替える.
	 * @param value 配置する値
	 * @param keys キーの階層
	 */
	public synchronized void put(Object value, String... keys) {
		JsonObject newJsonObject = (jsonObject_ != null) ? jsonObject_.copy() : new JsonObject();
		JsonObjectUtil.put(newJsonObject, value, keys);
		jsonObject_ = newJsonObject;
	}
	/**
	 * Digs into {@code keys} hierarchy and places value.
	 * Carries out merge using {@link JsonObject#mergeIn(JsonObject)} if {@link JsonObject} already exists.
	 * Creates {@link JsonObject} if value cannot be acquired while traversing the hierarchy.
	 * Also creates new {@link JsonObject} and replaces when value acquired while traversing the hierarchy is not {@link JsonObject}.
	 * @param value jsonobject object to be placed
	 * @param keys key hierachy
	 * {@code keys} 階層を掘って値を配置する.
	 * 既に {@link JsonObject} が存在する場合は {@link JsonObject#mergeIn(JsonObject)} でマージする.
	 * 階層を辿る途中で値が取得できない場合は新しく {@link JsonObject} を作る.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も新しく {@link JsonObject} を作り差し替える.
	 * @param value 配置する jsonobject オブジェクト
	 * @param keys キーの階層
	 */
	public synchronized void mergeIn(JsonObject value, String... keys) {
		if (value != null) {
			JsonObject newJsonObject = (jsonObject_ != null) ? jsonObject_.copy() : new JsonObject();
			JsonObjectUtil.mergeIn(newJsonObject, value, keys);
			jsonObject_ = newJsonObject;
		} else {
			if (log.isWarnEnabled()) log.warn("JsonObjectWrapper.mergeIn(); value is null; keys : " + Arrays.toString(keys));
		}
	}

	/**
	 * Digs into {@code keys} hierarchy and places value.
	 * Adds element if {@link JsonArray} already exists.
	 * If value does not exist, creates new {@link JsonArray} and adds element.
	 * Creates {@link JsonObject} if value cannot be acquired while traversing the hierarchy.
	 * Also creates new {@link JsonObject} and replaces if value acquired while traversing the hierarchy is not {@link JsonObject}
	 * @param value value to be added as element of {@link JsonArray}
	 * @param keys key hierachy
	 * {@code keys} 階層を掘って値を配置する.
	 * 既に {@link JsonArray} が存在する場合は要素を追加する.
	 * 値が存在しない場合は新しく {@link JsonArray} を作り要素を追加する.
	 * 階層を辿る途中で値が取得できない場合は新しく {@link JsonObject} を作る.
	 * 階層を辿る途中で取得した値が {@link JsonObject} ではない場合も新しく {@link JsonObject} を作り差し替える.
	 * @param value {@link JsonArray} の要素として追加する値
	 * @param keys キーの階層
	 */
	public synchronized void add(Object value, String... keys) {
		if (value != null) {
			JsonObject newJsonObject = (jsonObject_ != null) ? jsonObject_.copy() : new JsonObject();
			JsonObjectUtil.add(newJsonObject, value, keys);
			jsonObject_ = newJsonObject;
		} else {
			if (log.isWarnEnabled()) log.warn("JsonObjectWrapper.add(); value is null; keys : " + Arrays.toString(keys));
		}
	}

}

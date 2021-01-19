package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.impl.FileResolver;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.PemKeyCertOptions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jp.co.sony.csl.dcoes.apis.common.util.EncryptionUtil;

/**
 * This is the common startup class for APIS programs.
 * It carries out tricks around encryption. 
 * It is specified by maven-shade-plugin's {@literal <Main-Verticle>} in pom.xml.
 * APIS プログラム共通の起動クラス.
 * 暗号化まわりの細工をするため.
 * pom.xml の maven-shade-plugin の {@literal <Main-Class>} で指定してある.
 * @author OES Project
 */
public class ApisLauncher extends Launcher {
	private static final Logger log = LoggerFactory.getLogger(ApisLauncher.class);

	/**
	 * Sets suffix to key of entry encrypted in CONFIG.
	 * Sets suffix to encrypted file name.
	 * The value is {@value}.
	 * CONFIG 中の暗号化されたエントリのキー接尾辞.
	 * 暗号化されたファイルのファイル名接尾辞.
	 * 値は {@value}.
	 */
	private static final String SUFFIX_TO_DECRYPT = ".encrypted";

	/**
	 * Keeps {@link Path} to delete decrypted files later.
	 * 復号した暗号化ファイルを後で削除するため {@link Path} を保持しておく.
	 */
	private List<Path> decryptedPaths_ = new ArrayList<>();

	/**
	 * Follows {@link Launcher#main(String[])} pattern.
	 * @param args the user command line arguments.
	 * {@link Launcher#main(String[])} を踏襲.
	 * @param args the user command line arguments.
	 */
	public static void main(String[] args) {
		new ApisLauncher().dispatch(args);
	}
	/**
	 * Follows {@link Launcher#executeCommand(String, String...)} pattern.
	 * @param cmd  the command
	 * @param args the arguments
	 * {@link Launcher#executeCommand(String, String...)} を踏襲.
	 * @param cmd  the command
	 * @param args the arguments
	 */
	public static void executeCommand(String cmd, String... args) {
		new ApisLauncher().execute(cmd, args);
	}

	////

	/**
	 * Called after reading CONFIG files.
	 * Decrypts encrypted strings in CONFIG.
	 * Decrypts encrypted files
	 * @param config {@inheritDoc}
	 * CONFIG ファイル読み込み後に呼び出される.
	 * CONFIG 中の暗号化された文字列を復号する.
	 * 暗号化された各種ファイルを復号する.
	 * @param config {@inheritDoc}
	 */
	@Override public void afterConfigParsed(JsonObject config) {
		VertxConfig.config.setJsonObject(config);
		initVertxCacheDirBase_();
		initEncryption_();
		decryptConfig_();
		decryptFiles_();
	}
	/**
	 * Called before starting the vertx instance.
	 * Configures SSL for EventBus message communication.
	 * @param options {@inheritDoc}
	 * vertx インスタンスの起動前に呼び出される.
	 * EventBus メッセージ通信の SSL 化を設定する.
	 * @param options {@inheritDoc}
	 */
	@Override public void beforeStartingVertx(VertxOptions options) {
		doSecureCluster_(options);
	}
	/**
	 * Called after starting vertx instance.
	 * Deletes decrypted files.
	 * @param vertx {@inheritDoc}
	 * vertx インスタンスの起動後に呼び出される.
	 * 復号した暗号化ファイルを削除する.
	 * @param vertx {@inheritDoc}
	 */
	@Override public void afterStartingVertx(Vertx vertx) {
		deleteDecryptedFiles_();
	}
	/**
	 * Called before stopping vertx instance.
	 * Deletes decrypted files.
	 * @param vertx {@inheritDoc}
	 * vertx インスタンス停止前に呼ばれる.
	 * 復号した暗号化ファイルを削除する.
	 * @param vertx {@inheritDoc}
	 */
	@Override public void beforeStoppingVertx(Vertx vertx) {
		deleteDecryptedFiles_();
	}
	/**
	 * Called after stopping vertx instance.
	 * Deletes decrytped files.
	 * vertx インスタンス停止後に呼ばれる.
	 * 復号した暗号化ファイルを削除する.
	 */
	@Override public void afterStoppingVertx() {
		deleteDecryptedFiles_();
	}

	////

	/**
	 * Changes the location and name of vertx-cache directory.
	 * The default behavior of Vert.x is {@code /tmp/vertx-cache}.
	 * {IllegalStateException: Failed to create cache dir} occurs when multiple apis-main for running the gateway unit are started in different accounts.
	 * To avoid this, path is included in the account string.
	 * vertx-cache ディレクトリの場所と名前を変更する.
	 * Vert.x のデフォルト動作は {@code /tmp/vertx-cache} である.
	 * ゲイトウェイユニット運用のため複数の apis-main を異なるアカウントで起動すると {@code java.lang.IllegalStateException: Failed to create cache dir} が起きる.
	 * これを避けるためパスにアカウント文字列を含めるようにした.
	 */
	private void initVertxCacheDirBase_() {
		String tmpdir = System.getProperty("java.io.tmpdir", ".");
		String name = System.getProperty("user.name", "unknown");
		String base = tmpdir + File.separator + "vertx-cache." + name;
		if (log.isDebugEnabled()) log.debug(FileResolver.CACHE_DIR_BASE_PROP_NAME + " : " + base);
		System.setProperty(FileResolver.CACHE_DIR_BASE_PROP_NAME, base);
	}

	/**
	 * Carries out {initialization of {@link EncryptionUtil#initialize(io.vertx.core.Handler) EncryptionUtil}.
	 * @throws RuntimeException initialization failure
	 * {@link EncryptionUtil#initialize(io.vertx.core.Handler) EncryptionUtil を初期化}する.
	 * @throws RuntimeException 初期化失敗
	 */
	private void initEncryption_() {
		EncryptionUtil.initialize(r -> {
			if (r.succeeded()) {
			} else {
				throw new RuntimeException("encryption initialization failed", r.cause());
			}
		});
	}

	/**
	 * Decrypts encrypted strings in CONFIG ( {@link JsonObject} ) entries.
	 * For the encryption marker, key ends with suffix {@value #SUFFIX_TO_DECRYPT}.
	 * After decryption, registers using key without suffix.
	 * Processes recursively if value is {@link JsonArray} or {@link JsonObject}.
	 * CONFIG ( {@link JsonObject} ) のエントリのうち暗号化された文字列を復号する.
	 * 暗号化の目印はキーが接尾辞 {@value #SUFFIX_TO_DECRYPT} で終わっていること.
	 * 復号したのち接尾辞を除いたキーで登録する.
	 * 値が {@link JsonArray} や {@link JsonObject} の場合は再帰的に処理する.
	 */
	private void decryptConfig_() {
		JsonObject src = VertxConfig.config.jsonObject();
		if (src != null) {
			JsonObject result = decrypt_(src, false);
			VertxConfig.config.setJsonObject(result);
		}
	}
	/**
	 * Decrypts encrypted strings by recursively traversing {@link JsonObject}.
	 * @param obj jsonobject object to be decrypted
	 * @param needDecrypt decryption flag.
	 *                    This is needed because child elements of entry with encryption marker {@value #SUFFIX_TO_DECRYPT} need to be decrypted recursively.
	 * @return decrypted jsonobject object
	 * {@link JsonObject} を再帰的に辿って暗号化された文字列を復号する.
	 * @param obj 復号対象 jsonobject オブジェクト
	 * @param needDecrypt 復号フラグ.
	 *                    暗号化の目印 {@value #SUFFIX_TO_DECRYPT} が着いたエントリの子孫要素は再帰的に復号する必要があるため.
	 * @return 復号済みの jsonobject オブジェクト
	 */
	private JsonObject decrypt_(JsonObject obj, boolean needDecrypt) {
		JsonObject result = new JsonObject();
		obj.forEach(kv -> {
			String k = kv.getKey();
			boolean needDecrypt_ = needDecrypt;
			if (k.endsWith(SUFFIX_TO_DECRYPT)) {
				needDecrypt_ = true;
				k = k.substring(0, k.length() - SUFFIX_TO_DECRYPT.length());
			}
			result.put(k, decrypt_(kv.getValue(), needDecrypt_));
		});
		return result;
	}
	/**
	 * Decrypts encrypted strings by recursively traversing {@link JsonArray}.
	 * @param ary jsonarray object to be decrypted
	 * @param needDecrypt decryption flag.
	 *                    This is needed because child elements of entry with encryption marker {@value #SUFFIX_TO_DECRYPT} need to be decrypted recursively.
	 * @return decrypted jsonarray object
	 * {@link JsonArray} を再帰的に辿って暗号化された文字列を復号する.
	 * @param ary 復号対象 jsonarray オブジェクト
	 * @param needDecrypt 復号フラグ.
	 *                    暗号化の目印 {@value #SUFFIX_TO_DECRYPT} が着いたエントリの子孫要素は再帰的に復号する必要があるため.
	 * @return 復号済みの jsonarray オブジェクト
	 */
	private JsonArray decrypt_(JsonArray ary, boolean needDecrypt) {
		JsonArray result = new JsonArray();
		ary.forEach(v -> {
			result.add(decrypt_(v, needDecrypt));
		});
		return result;
	}
	/**
	 * Decrypts encrypted strings by recursively traversing {@link JsonArray}.
	 * @param v object to be decrypted.
	 *          If string, decrypts as needed in accordance with {@code needDecrypt}.
	 *          If {@link JsonObject}, calls {@link #decrypt_(JsonObject, boolean)}.
	 *          If {@link JsonArray}, calls {@link #decrypt_(JsonArray, boolean)}.
	 * @param needDecrypt decryption flag.
	 *                    This is needed because child elements of entry with encryption marker {@value #SUFFIX_TO_DECRYPT} need to be decrypted recursively.
	 * @return Decrypted object
	 * {@link JsonArray} を再帰的に辿って暗号化された文字列を復号する.
	 * @param v 復号対象オブジェクト.
	 *          文字列の場合は {@code needDecrypt} に従って必要に応じて復号する.
	 *          {@link JsonObject} の場合は {@link #decrypt_(JsonObject, boolean)} を呼ぶ.
	 *          {@link JsonArray} の場合は {@link #decrypt_(JsonArray, boolean)} を呼ぶ.
	 * @param needDecrypt 復号フラグ.
	 *                    暗号化の目印 {@value #SUFFIX_TO_DECRYPT} が着いたエントリの子孫要素は再帰的に復号する必要があるため.
	 * @return 復号済みのオブジェクト
	 */
	private Object decrypt_(Object v, boolean needDecrypt) {
		if (v instanceof String && needDecrypt) {
			if (log.isDebugEnabled()) log.debug("decrypting string : " + v);
			try {
				return EncryptionUtil.decrypt((String) v);
			} catch (Exception e) {
				log.error(e);
				throw new RuntimeException("string decryption failed : " + v, e);
			}
		} else if (v instanceof JsonObject) {
			return decrypt_((JsonObject) v, needDecrypt);
		} else if (v instanceof JsonArray) {
			return decrypt_((JsonArray) v, needDecrypt);
		} else {
			return v;
		}
	}

	/**
	 * Decrypts encrypted files that exist in the current directory.
	 * For the encryption marker, the file name ends in {@value #SUFFIX_TO_DECRYPT}.
	 * After decryption, registers using the file name without suffix.
	 * Keeps decrypted files in {@link #decryptedPaths_} to delete later.　
	 * @throws RuntimeException decryption failure
	 * カレントディレクトリに存在する暗号化されたファイルを復号する.
	 * 暗号化の目印はファイル名が {@value #SUFFIX_TO_DECRYPT} で終わっていること.
	 * 復号したのち接尾辞を除いたファイル名で登録する.
	 * 復号したファイルは後ほど削除するため {@link #decryptedPaths_} に保持しておく.　
	 * @throws RuntimeException 復号失敗
	 */
	private void decryptFiles_() {
		try (Stream<Path> paths = Files.list(Paths.get(""))) {
			paths.filter(path -> !Files.isDirectory(path) && path.getFileName() != null && path.getFileName().toString().endsWith(SUFFIX_TO_DECRYPT)).forEach(path -> {
				String encryptedFilename = path.getFileName().toString();
				String decryptedFilename = encryptedFilename.substring(0, encryptedFilename.length() - SUFFIX_TO_DECRYPT.length());
				if (log.isDebugEnabled()) log.debug("decrypting file : " + encryptedFilename);
				try {
					List<String> lines = Files.readAllLines(path);
					String encrypted = String.join("", lines);
					String decrypted = EncryptionUtil.decrypt(encrypted);
					Path decryptedPath = Paths.get("", decryptedFilename);
					Files.write(decryptedPath, decrypted.getBytes(StandardCharsets.UTF_8));
					if (log.isDebugEnabled()) log.debug("file decrypted : " + decryptedFilename);
					decryptedPaths_.add(decryptedPath);
				} catch (Exception e) {
					log.error(e);
					throw new RuntimeException("file decryption failed : " + encryptedFilename, e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("file decryption failed", e);
		}
	}
	/**
	 * Deletes decrypted files.
	 * Refers to {@link #decryptedPaths_} for files to be deleted.
	 * @throws RuntimeException deletion failure
	 * 復号した暗号化ファイルを削除する.
	 * 削除対象ファイルは {@link #decryptedPaths_} を参照する.
	 * @throws RuntimeException 削除失敗
	 */
	private void deleteDecryptedFiles_() {
		for (Path decryptedPath : decryptedPaths_) {
			if (log.isDebugEnabled()) log.debug("deleting file : " + decryptedPath);
			try {
				boolean deleted = Files.deleteIfExists(decryptedPath);
				if (deleted) {
					if (log.isDebugEnabled()) log.debug("file deleted : " + decryptedPath);
				} else {
					if (log.isDebugEnabled()) log.debug("no such file : " + decryptedPath);
				}
			} catch (Exception e) {
				log.error(e);
				throw new RuntimeException("file deletion failed : " + decryptedPath, e);
			}
		}
	}

	/**
	 * Configures SSL for EventBus message communication.
	 * Does nothing if {@link VertxConfig#securityEnabled()} is {@code false}.
	 * @param options the configured Vert.x options. Modify them to customize the Vert.x instance.
	 * EventBus メッセージ通信の SSL 化を設定する.
	 * {@link VertxConfig#securityEnabled()} が {@code false} なら何もしない.
	 * @param options the configured Vert.x options. Modify them to customize the Vert.x instance.
	 */
	private void doSecureCluster_(VertxOptions options) {
		if (VertxConfig.securityEnabled()) {
			if (log.isInfoEnabled()) log.info("EventBus will be secured");
			String keyFilePath = VertxConfig.securityPemKeyFile();
			String certFilePath = VertxConfig.securityPemCertFile();
			if (log.isDebugEnabled()) log.debug("pem key file : " + keyFilePath);
			if (log.isDebugEnabled()) log.debug("pem cert file : " + certFilePath);
			options.getEventBusOptions().setSsl(true).setPemKeyCertOptions(new PemKeyCertOptions().addKeyPath(keyFilePath).addCertPath(certFilePath));
		}
	}

}

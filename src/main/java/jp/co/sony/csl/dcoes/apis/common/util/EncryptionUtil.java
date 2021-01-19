package jp.co.sony.csl.dcoes.apis.common.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jp.co.sony.csl.dcoes.apis.common.util.vertx.AbstractStarter;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.VertxConfig;

/**
 * Tool for encryption processing.
 * @author OES Project
 * 暗号化処理ツール.
 * @author OES Project
 */
public class EncryptionUtil {
	private static final Logger log = LoggerFactory.getLogger(EncryptionUtil.class);

	/**
	 * Encryption algorithm.
	 * The value is {@value}.
	 * 暗号化アルゴリズム.
	 * 値は {@value}.
	 */
	private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
	/**
	 * Key length.
	 * The value is {@value}.
	 * 鍵の長さ.
	 * 値は {@value}.
	 */
	private static final int CIPHER_KEY_SIZE = 128;
	/**
	 * Secret key algorithm.
	 * The value is {@value}.
	 * 秘密鍵アルゴリズム.
	 * 値は {@value}.
	 */
	private static final String CIPHER_KEY_ALGORITHM = "AES";
	/**
	 * Hashing algorithm used when generating key and IV from seed.
	 * The value is {@value}.
	 * seed から鍵と IV を生成する際に使用するハッシュアルゴリズム.
	 * 値は {@value}.
	 */
	private static final String HASH_ALGORITHM = "MD5";

	private static int CIPHER_BLOCK_SIZE;
	private static Key DEFAULT_KEY;
	private static AlgorithmParameterSpec DEFAULT_ALGORITHM_PARAMETER_SPEC;

	private EncryptionUtil() { }

	/**
	 * Initializes.
	 * The default seed string is communityId-clusterId-APIS_VERSION.
	 * @param completionHandler the completion handler
	 * 初期化.
	 * デフォルトの seed 文字列は communityId-clusterId-APIS_VERSION.
	 * @param completionHandler the completion handler
	 */
	public static void initialize(Handler<AsyncResult<Void>> completionHandler) {
		try {
			String defaultSeed = VertxConfig.communityId() + '-' + VertxConfig.clusterId() + '-' + AbstractStarter.APIS_VERSION;
			Cipher cipher = generateCipher();
			CIPHER_BLOCK_SIZE = cipher.getBlockSize();
			DEFAULT_KEY = generateKey(defaultSeed);
			DEFAULT_ALGORITHM_PARAMETER_SPEC = generateIv(defaultSeed);
		} catch (Exception e) {
			log.error(e);
			completionHandler.handle(Future.failedFuture(e));
			return;
		}
		if (log.isInfoEnabled()) log.info("initialized");
		completionHandler.handle(Future.succeededFuture());
	}

	////

	/**
	 * Gets encryption object.
	 * Generates {@link Cipher} algorithm specified by {@link #CIPHER_TRANSFORMATION}.
	 * @return cipher object
	 * @throws GeneralSecurityException {@link Cipher#getInstance(String)}
	 * 暗号化オブジェクトを取得する.
	 * {@link #CIPHER_TRANSFORMATION} で指定されたアルゴリズムの {@link Cipher} を生成する.
	 * @return cipher オブジェクト
	 * @throws GeneralSecurityException {@link Cipher#getInstance(String)}
	 */
	public static Cipher generateCipher() throws GeneralSecurityException {
		return Cipher.getInstance(CIPHER_TRANSFORMATION);
	}
	/**
	 * Gets secret key used in encryption.
	 * Generates {@link Key} of {@link #CIPHER_KEY_SIZE} length from {@code seed}.
	 * @param seed seed
	 * @return key object
	 * @throws GeneralSecurityException {@link MessageDigest#getInstance(String)},
	 *                                  {@link SecretKeySpec#SecretKeySpec(byte[], String)}
	 * 暗号化に使用する秘密鍵を取得する.
	 * {@code seed} から長さ {@link #CIPHER_KEY_SIZE} の {@link Key} を生成する.
	 * {@code seed} を {@link #HASH_ALGORITHM} でハッシュして使用する.
	 * @param seed 種
	 * @return key オブジェクト
	 * @throws GeneralSecurityException {@link MessageDigest#getInstance(String)},
	 *                                  {@link SecretKeySpec#SecretKeySpec(byte[], String)}
	 */
	public static Key generateKey(String seed) throws GeneralSecurityException {
		MessageDigest hash = MessageDigest.getInstance(HASH_ALGORITHM);
		byte[] md = hash.digest(seed.getBytes(StandardCharsets.UTF_8));
		byte[] bytes = new byte[CIPHER_KEY_SIZE / 8];
		System.arraycopy(md, 0, bytes, 0, bytes.length);
		return new SecretKeySpec(bytes, CIPHER_KEY_ALGORITHM);
	}
	/**
	 * Gets IV used in encryption.
	 * Generates {@link AlgorithmParameterSpec} of {@link #CIPHER_KEY_SIZE} length from {@code seed}.
	 * Uses {@code seed} hashed twice by {@link #HASH_ALGORITHM}.
	 * @param seed seed
	 * @return algorithmparameterspec object
	 * @throws GeneralSecurityException {@link MessageDigest#getInstance(String)},
	 *                                  {@link IvParameterSpec#IvParameterSpec(byte[])}
	 * 暗号化に使用する IV を取得する.
	 * {@code seed} から長さ {@link #CIPHER_KEY_SIZE} の {@link AlgorithmParameterSpec} を生成する.
	 * {@code seed} を {@link #HASH_ALGORITHM} で二度ハッシュして使用する.
	 * @param seed 種
	 * @return algorithmparameterspec オブジェクト
	 * @throws GeneralSecurityException {@link MessageDigest#getInstance(String)},
	 *                                  {@link IvParameterSpec#IvParameterSpec(byte[])}
	 */
	public static AlgorithmParameterSpec generateIv(String seed) throws GeneralSecurityException {
		MessageDigest hash = MessageDigest.getInstance(HASH_ALGORITHM);
		byte[] md = hash.digest(hash.digest(seed.getBytes(StandardCharsets.UTF_8)));
		byte[] bytes = new byte[CIPHER_BLOCK_SIZE];
		System.arraycopy(md, 0, bytes, 0, CIPHER_BLOCK_SIZE);
		return new IvParameterSpec(bytes);
	}

	////

	/**
	 * Encrypts string.
	 * Uses defaults for both encryption algorithm and seed.
	 * @param value string to be encrypted
	 * @return encrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 文字列を暗号化する.
	 * 暗号化アルゴリズム、seed 共にデフォルトを用いる.
	 * @param value 暗号化する文字列
	 * @return 暗号化した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String encrypt(String value) throws GeneralSecurityException {
		return encrypt(value, null, null);
 	}
	/**
	 * Encrypts string.
	 * Uses default for encryption algorithm.
	 * @param value string to be encrypted
	 * @param seed seed used in encryption
	 * @return encrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 文字列を暗号化する.
	 * 暗号化アルゴリズムはデフォルトを用いる.
	 * @param value 暗号化する文字列
	 * @param seed 暗号化に使用する seed
	 * @return 暗号化した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String encrypt(String value, String seed) throws GeneralSecurityException {
		return encrypt(value, null, seed);
 	}
	/**
	 * Encrypts string.
	 * Uses default for seed used in encryption.
	 * @param value string to be encrypted
	 * @param cipher encryption object 
	 * @return encrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 文字列を暗号化する.
	 * 暗号化に使用する seed はデフォルトを用いる.
	 * @param value 暗号化する文字列
	 * @param cipher 暗号化オブジェクト
	 * @return 暗号化した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String encrypt(String value, Cipher cipher) throws GeneralSecurityException {
		return encrypt(value, cipher, null);
 	}
	/**
	 * Encrypts string.
	 * @param value string to be encrypted
	 * @param cipher encryption object 
	 * @param seed seed used in encryption
	 * @return encrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 文字列を暗号化する.
	 * @param value 暗号化する文字列
	 * @param cipher 暗号化オブジェクト
	 * @param seed 暗号化に使用する seed
	 * @return 暗号化した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String encrypt(String value, Cipher cipher, String seed) throws GeneralSecurityException {
		cipher = (cipher != null) ? cipher : generateCipher();
		Key key = (seed != null) ? generateKey(seed) : DEFAULT_KEY;
		AlgorithmParameterSpec algorithmParameterSpec = (seed != null) ? generateIv(seed) : DEFAULT_ALGORITHM_PARAMETER_SPEC;
		cipher.init(Cipher.ENCRYPT_MODE, key, algorithmParameterSpec);
		byte[] bytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
		String result = Base64.getEncoder().encodeToString(bytes);
//		if (log.isDebugEnabled()) log.debug("encrypted ; " + value + " -> " + result);
		return result;
	}

	/**
	 * Decrypts encrypted string.
	 * Uses defaults for both encryption algorithm and seed.
	 * @param value encrypted string
	 * @return decrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 暗号化された文字列を復号する.
	 * 暗号化アルゴリズム、seed 共にデフォルトを用いる.
	 * @param value 暗号化された文字列
	 * @return 復号した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String decrypt(String value) throws GeneralSecurityException {
		return decrypt(value, null, null);
	}
	/**
	 * Decrypts encrypted string.
	 * Uses default for encryption algorithm.
	 * @param value encrypted string
	 * @param seed seed used for decryption
	 * @return decrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 暗号化された文字列を復号する.
	 * 暗号化アルゴリズムはデフォルトを用いる.
	 * @param value 暗号化された文字列
	 * @param seed 復号に使用する seed
	 * @return 復号した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String decrypt(String value, String seed) throws GeneralSecurityException {
		return decrypt(value, null, seed);
 	}
	/**
	 * Decrypts encrypted string.
	 * Uses default for seed used in encryption.
	 * @param value encrypted string
	 * @param cipher encryption object 
	 * @return decrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 暗号化された文字列を復号する.
	 * 暗号化に使用する seed はデフォルトを用いる.
	 * @param value 暗号化された文字列
	 * @param cipher 暗号化オブジェクト
	 * @return 復号した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String decrypt(String value, Cipher cipher) throws GeneralSecurityException {
		return decrypt(value, cipher, null);
 	}
	/**
	 * Encrypts string.
	 * @param value encrypted string
	 * @param cipher encryption object 
	 * @param seed seed used for decryption
	 * @return decrypted string
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 * 文字列を暗号化する.
	 * @param value 暗号化された文字列
	 * @param cipher 暗号化オブジェクト
	 * @param seed 復号に使用する seed
	 * @return 復号した文字列
	 * @throws GeneralSecurityException {@link #generateCipher()},
	 *                                  {@link #generateKey(String)},
	 *                                  {@link #generateIv(String)},
	 *                                  {@link Cipher#init(int, Key, AlgorithmParameterSpec)},
	 *                                  {@link Cipher#doFinal(byte[])}
	 */
	public static String decrypt(String value, Cipher cipher, String seed) throws GeneralSecurityException {
		cipher = (cipher != null) ? cipher : generateCipher();
		Key key = (seed != null) ? generateKey(seed) : DEFAULT_KEY;
		AlgorithmParameterSpec algorithmParameterSpec = (seed != null) ? generateIv(seed) : DEFAULT_ALGORITHM_PARAMETER_SPEC;
		cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameterSpec);
		byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(value));
		String result = new String(bytes, StandardCharsets.UTF_8);
//		if (log.isDebugEnabled()) log.debug("decrypted ; " + value + " -> " + result);
		return result;
	}

}

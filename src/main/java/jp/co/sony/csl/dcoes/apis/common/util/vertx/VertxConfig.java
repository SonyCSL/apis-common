package jp.co.sony.csl.dcoes.apis.common.util.vertx;

/**
 * This tool enables access to CONFIG common to APIS programs.
 * This tool is only partially supported.
 * @author OES Project
 * APIS プログラム共通の CONFIG アクセスツール.
 * 一部しかサポートしていない.
 * @author OES Project
 */
public class VertxConfig {

	private VertxConfig() { }

	/**
	 * Creates {@link JsonObjectWrapper} which holds CONFIG.
	 * CONFIG を保持する {@link JsonObjectWrapper} オブジェクト.
	 */
	public static final JsonObjectWrapper config = new JsonObjectWrapper();

	/**
	 * Gets program identification string from CONFIG.
	 * {@code CONFIG.programId}.
	 * @return program identification string
	 * CONFIG からプログラム識別文字列を取得.
	 * {@code CONFIG.programId}.
	 * @return プログラム識別文字列
	 */
	public static String programId() {
		return config.getString("programId");
	}
	/**
	 * Gets community identification string from CONFIG.
	 * {@code CONFIG.communityId}.
	 * @return community identification string
	 * CONFIG からコミュニティ識別文字列を取得.
	 * {@code CONFIG.communityId}.
	 * @return コミュニティ識別文字列
	 */
	public static String communityId() {
		return config.getString("communityId");
	}
	/**
	 * Gets cluster identification string from CONFIG.
	 * {@code CONFIG.clusterId}.
	 * @return cluster identification string
	 * CONFIG からクラスタ識別文字列を取得.
	 * {@code CONFIG.clusterId}.
	 * @return クラスタ識別文字列
	 */
	public static String clusterId() {
		return config.getString("clusterId");
	}
	/**
	 * Reads the enable flag for setting SSL encryption of EventBus message communication and Cluster Wide Map encryption from CONFIG.
	 * {@code CONFIG.security.enabled}.
	 * @return enable flag
	 * CONFIG から EventBus メッセージ通信の SSL 化および Cluster Wide Map の暗号化設定の有効フラグを読み込む
	 * {@code CONFIG.security.enabled}.
	 * @return 有効フラグ
	 */
	public static boolean securityEnabled() {
		return config.getBoolean(Boolean.FALSE, "security", "enabled");
	}
	/**
	 * Reads path of secret key file for setting SSL encryption of EventBus message communication and Cluster Wide Map encryption from CONFIG.
	 * {@code CONFIG.security.pemKeyFile}.
	 * @return path of secret key file
	 * CONFIG から EventBus メッセージ通信の SSL 化および Cluster Wide Map の暗号化設定の秘密鍵ファイルのパスを読み込む
	 * {@code CONFIG.security.pemKeyFile}.
	 * @return 秘密鍵ファイルのパス
	 */
	public static String securityPemKeyFile() {
		return config.getString("security", "pemKeyFile");
	}
	/**
	 * Reads path of certificate file for setting SSL encryption of EventBus message communication and Cluster Wide Map encryption from CONFIG.
	 * {@code CONFIG.security.pemCertFile}.
	 * @return path of certificate file
	 * CONFIG から EventBus メッセージ通信の SSL 化および Cluster Wide Map の暗号化設定の証明書ファイルのパスを読み込む
	 * {@code CONFIG.security.pemCertFile}.
	 * @return 証明書ファイルのパス
	 */
	public static String securityPemCertFile() {
		return config.getString("security", "pemCertFile");
	}

}

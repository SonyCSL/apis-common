package jp.co.sony.csl.dcoes.apis.common;

/**
 * Carries out unified management of {@link io.vertx.core.eventbus.EventBus} addresses.
 * @author OES Project
 * {@link io.vertx.core.eventbus.EventBus} アドレスの一元管理.
 * @author OES Project
 */
public class ServiceAddress {

	private ServiceAddress() { }

	/** Address for use in technique to ensure that units in the cluster do not have the same ID.
	 * Scope : Global
	 * Process : Technique to ensure that units in the cluster do not have the same ID.
	 * 　　   Returns unit ID of own unit if message body is empty.
	 * 　　   If there is message body and it matches own {@link io.vertx.core.AbstractVerticle#deploymentID()}, then ignores (because it is own self).
	 * 　　   If there is message body and it does not matches own {@link io.vertx.core.AbstractVerticle#deploymentID()}, then FATAL error (it means there is another unit with same ID).
	 * Message body : Sender {@link jp.co.sony.csl.dcoes.apis.main.app.Helo} Verticle's {@link io.vertx.core.AbstractVerticle#deploymentID()} [{@link String}]
	 * Message header : None
	 * Response : ID [{@link String}] of own unit if message body is empty.
	 * 　　　　　   In all other cases, returns nothing.
	 * @param unitId unit ID
	 * @return address string
	 * クラスタ内に同一 ID を持つユニットが重複しないための仕組みで使用するアドレス.
	 * 範囲 : グローバル
	 * 処理 : クラスタ内に同一 ID を持つユニットが重複しないための仕組み.
	 * 　　   メッセージボディが空なら自ユニットのユニット ID を返す.
	 * 　　   メッセージボディがあり自分の {@link io.vertx.core.AbstractVerticle#deploymentID()} と一致したらスルー ( 自分なので ).
	 * 　　   メッセージボディがあり自分の {@link io.vertx.core.AbstractVerticle#deploymentID()} と一致しなかったら FATAL エラー ( 他に同じ ID のユニットがいるということなので ).
	 * メッセージボディ : 送信元 {@link jp.co.sony.csl.dcoes.apis.main.app.Helo} Verticle の {@link io.vertx.core.AbstractVerticle#deploymentID()} [{@link String}]
	 * メッセージヘッダ : なし
	 * レスポンス : メッセージボディが空なら自ユニットの ID [{@link String}].
	 * 　　　　　   それ以外は返さない.
	 * @param unitId ユニット ID
	 * @return アドレス文字列
	 */
	public static String helo(String unitId) {
		return "apis." + unitId + ".helo";
	}
	/**
	 * Address to get own unit's POLICY.
	 * Scope : Local
	 * Process : Gets own unit's POLICY.
	 * 　　   Returns cache content, which is periodically refreshed.
	 * Message body : None
	 * Message header : None
	 * Response : POLICY information [{@link io.vertx.core.json.JsonObject JsonObject}]
	 * @return address string
	 * 自ユニットの POLICY を取得するアドレス.
	 * 範囲 : ローカル
	 * 処理 : 自ユニットの POLICY を取得する.
	 * 　　   定期的にリフレッシュされているキャッシュ内容を返す.
	 * メッセージボディ : なし
	 * メッセージヘッダ : なし
	 * レスポンス : POLICY 情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
	 * @return アドレス文字列
	 */
	public static String policy() {
		return "apis.policy";
	}
	/**
	 * Address to set/get global Power Sharing mode.
	 * Scope : Global
	 * Process : Sets/gets global Power Sharing mode.
	 * 　　   Value is one of the following.
	 * 　　   - "autonomous"
	 * 　　   - "heteronomous"
	 * 　　   - "stop"
	 * 　　   - "manual"
	 * Message body :
	 * 　　　　　　　　   For set : Global Power Sharing mode [{@link String}] to be set
	 * 　　　　　　　　   For get : None
	 * Message header : {@code "command"}
	 * 　　　　　　　　   - {@code "set"} : Updates global Power Sharing mode
	 * 　　　　　　　　   - {@code "get"} : Gets global Power Sharing mode
	 * Response :
	 * 　　　　　   For set : ID [{@link String}] of own unit
	 * 　　　　　   For get : Current Power Sharing mode [{@link String}]
	 * 　　　　　   Fail if error occurs.
	 * @return address string
	 * グローバル融通モードを set/get するアドレス.
	 * 範囲 : グローバル
	 * 処理 : グローバル融通モードを set/get する.
	 * 　　   値は以下のいずれか.
	 * 　　   - "autonomous"
	 * 　　   - "heteronomous"
	 * 　　   - "stop"
	 * 　　   - "manual"
	 * メッセージボディ :
	 * 　　　　　　　　   set の場合 : 設定するグローバル融通モード [{@link String}]
	 * 　　　　　　　　   get の場合 : なし
	 * メッセージヘッダ : {@code "command"}
	 * 　　　　　　　　   - {@code "set"} : グローバル融通モードを変更する
	 * 　　　　　　　　   - {@code "get"} : グローバル融通モードを取得する
	 * レスポンス :
	 * 　　　　　   set の場合 : 自ユニットの ID [{@link String}]
	 * 　　　　　   get の場合 : 現在のグローバル融通モード [{@link String}]
	 * 　　　　　   エラーが起きたら fail.
	 * @return アドレス文字列
	 */
	public static String operationMode() {
		return "apis.operationMode";
	}
	/**
	 * Address to pub/sub errors.
	 * Address : {@link ServiceAddress#error()}
	 * Scope : Global
	 * Process : Receives error.
	 * 　　   - GridMaster : Keeps only global errors.
	 * 　　   - User : Keeps only local errors of own units.
	 * Message body : Error information [{@link io.vertx.core.json.JsonObject JsonObject}]
	 * Message header : None
	 * Response : None
	 * @return address string
	 * エラーを pub/sub するアドレス.
	 * アドレス : {@link ServiceAddress#error()}
	 * 範囲 : グローバル
	 * 処理 : エラーを受信する.
	 * 　　   - GridMaster : グローバルエラーのみ保持する.
	 * 　　   - User : 自ユニットのローカルエラーのみ保持する.
	 * メッセージボディ : エラー情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
	 * メッセージヘッダ : なし
	 * レスポンス : なし
	 * @return アドレス文字列
	 */
	public static String error() {
		return "apis.error";
	}
	/**
	 * Address to reset own unit.
	 * Scope : Local
	 * Process : Resets own unit.
	 * Message body : None
	 * Message header : None
	 * Response : Own unit ID [{@link String}].
	 * 　　　　　   Fail if error occurs.
	 * @return address string
	 * 自ユニットのリセットのためのアドレス.
	 * 範囲 : ローカル
	 * 処理 : 自ユニットをリセットする.
	 * メッセージボディ : なし
	 * メッセージヘッダ : なし
	 * レスポンス : 自ユニットの ID [{@link String}].
	 * 　　　　　   エラーが起きたら fail.
	 * @return アドレス文字列
	 */
	public static String resetLocal() {
		return "apis.reset.local";
	}
	/**
	 * Address to reset all.
	 * Scope : Global
	 * Process : Resets all units and all programs participating in cluster.
	 * Message body : None
	 * Message header : None
	 * Response : Own unit ID [{@link String}].
	 * 　　　　　   Fail if error occurs.
	 * @return address string
	 * 全体リセットのためのアドレス.
	 * 範囲 : グローバル
	 * 処理 : 全ユニットおよびクラスタに参加する全プログラムをリセットする.
	 * メッセージボディ : なし
	 * メッセージヘッダ : なし
	 * レスポンス : 自ユニットの ID [{@link String}].
	 * 　　　　　   エラーが起きたら fail.
	 * @return アドレス文字列
	 */
	public static String resetAll() {
		return "apis.reset.all";
	}
	/**
	 * Address to shut down own unit.
	 * Scope : Local
	 * Process : Executes shutdown.
	 * 　　   The actual process is {@link jp.co.sony.csl.dcoes.apis.common.util.vertx.AbstractStarter#shutdown_()}.
	 * Message body : None
	 * Message header : None
	 * Response : {@code "ok"}
	 * @return address string
	 * 自ユニットのシャットダウンのためのアドレス.
	 * 範囲 : ローカル
	 * 処理 : シャットダウンする.
	 * 　　   実際の処理は {@link jp.co.sony.csl.dcoes.apis.common.util.vertx.AbstractStarter#shutdown_()}.
	 * メッセージボディ : なし
	 * メッセージヘッダ : なし
	 * レスポンス : {@code "ok"}
	 * @return アドレス文字列
	 */
	public static String shutdownLocal() {
		return "apis.shutdown.local";
	}
	/**
	 * Address to shut down all.
	 * Scope : Global
	 * Process : Executes shutdown.
	 * 　　   The actual process is {@link jp.co.sony.csl.dcoes.apis.common.util.vertx.AbstractStarter#shutdown_()}.
	 * Message body : None
	 * Message header : None
	 * Response : {@code "ok"}
	 * @return address string
	 * 全体シャットダウンのためのアドレス.
	 * 範囲 : グローバル
	 * 処理 : シャットダウンする.
	 * 　　   実際の処理は {@link jp.co.sony.csl.dcoes.apis.common.util.vertx.AbstractStarter#shutdown_()}.
	 * メッセージボディ : なし
	 * メッセージヘッダ : なし
	 * レスポンス : {@code "ok"}
	 * @return アドレス文字列
	 */
	public static String shutdownAll() {
		return "apis.shutdown.all";
	}
	/**
	 * Address to shut down single unit with specified ID.
	 * Scope : Global
	 * Process : Executes shutdown.
	 * 　　   The actual process calls {@link ServiceAddress#shutdownLocal()}.
	 * Message body : None
	 * Message header : None
	 * Response : {@code "ok"}
	 * 　　　　　   Fail if error occurs.
	 * @param unitId ID of target unit
	 * @return address string
	 * ID を指定した単体シャットダウンのためのアドレス.
	 * 範囲 : グローバル
	 * 処理 : シャットダウンする.
	 * 　　   実際の処理は {@link ServiceAddress#shutdownLocal()} を呼び出す.
	 * メッセージボディ : なし
	 * メッセージヘッダ : なし
	 * レスポンス : {@code "ok"}
	 * 　　　　　   エラーが起きたら fail.
	 * @param unitId 対象ユニットの ID
	 * @return アドレス文字列	 
	 */
	public static String shutdown(String unitId) {
		return "apis." + unitId + ".shutdown";
	}
	/**
	 * Address to update UDP multicast log output level.
	 * Scope : Global
	 * Process : Updates UDP multicast log output level
	 * Message body : Log level [{@link String}]
	 * 　　　　　　　　   Returns original state if not specified
	 * Message header : None
	 * Response : {@code "ok"} if successful
	 * 　　　　　   Fail if error occurs.
	 * @return address string
	 * UDP マルチキャストログ出力のレベルを変更するためのアドレス.
	 * 範囲 : グローバル
	 * 処理 : UDP マルチキャストログ出力のレベルを変更する
	 * メッセージボディ : ログレベル [{@link String}]
	 * 　　　　　　　　   指定がなければ初期状態に戻す
	 * メッセージヘッダ : なし
	 * レスポンス : 成功したら {@code "ok"}
	 * 　　　　　   エラーが起きたら fail.
	 * @return アドレス文字列
	 */
	public static String multicastLogHandlerLevel() {
		return "apis.multicastLogHandlerLevel";
	}
	/**
	 * Carries out unified management of {@link io.vertx.core.eventbus.EventBus} addresses used by Controller service.
	 * @author OES Project
	 * Controller サービスで使用する {@link io.vertx.core.eventbus.EventBus} アドレスの一元管理.
	 * @author OES Project
	 */
	public static class Controller {
		/**
		 * Address to get unit data of own unit.
		 * Scope : Local
		 * Process : Gets unit data of own unit.
		 * 　　   Returns fresh data by inquiring device on the spot, not cached data.
		 * Message body : None
		 * Message header : None
		 * Response : Unit data [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 自ユニットのユニットデータを取得するためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : 自ユニットのユニットデータを取得する.
		 * 　　   キャッシュではなくその場でデバイスに問い合せフレッシュなデータを返す.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : ユニットデータ [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String urgentUnitData() {
			return "apis.Controller.data.urgent";
		}
		/**
		 * Address to get unit data of own unit.
		 * Address : {@link ServiceAddress.Controller#unitData()}
		 * Scope : Local
		 * Process : Gets unit data of own unit.
		 * 　　   If urgent is specified in header, returns fresh data by inquiring device on the spot, not cached data.
		 * 　　   If not, returns cached data, which is refreshed periodically.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "urgent"} : urgent flag
		 * 　　　　　　　　     - {@code "true"} : returns fresh data by inquiring device on the spot
		 * 　　　　　　　　     - {@code "false"} : returns cached data, which is refreshed periodically
		 * Response : Unit data [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 自ユニットのユニットデータを取得するためのアドレス.
		 * アドレス : {@link ServiceAddress.Controller#unitData()}
		 * 範囲 : ローカル
		 * 処理 : 自ユニットのユニットデータを取得する.
		 * 　　   ヘッダに urgent 指定があればキャッシュではなくその場でデバイスに問い合せフレッシュなデータを返す.
		 * 　　   そうでなければ定期的にリフレッシュしてあるキャッシュデータを返す.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "urgent"} : 緊急フラグ
		 * 　　　　　　　　     - {@code "true"} : その場でデバイスに問合せフレッシュなデータを返す
		 * 　　　　　　　　     - {@code "false"} : 定期的にリフレッシュしてあるキャッシュデータを返す
		 * レスポンス : ユニットデータ [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String unitData() {
			return "apis.Controller.data";
		}
		/**
		 * Address to get unit data specified by ID.
		 * Scope : Global
		 * Process : Gets unit data of unit specified by ID.
		 * 　　   If urgent is specified in header, returns fresh data by inquiring device on the spot, not cached data.
		 * 　　   If not, returns cached data, which is refreshed periodically.
		 * 　　   GridMasterUnitId must be specified in header and must match GridMaster interlock value.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "urgent"} : urgent flag
		 * 　　　　　　　　     - {@code "true"} : returns fresh data by inquiring device on the spot
		 * 　　　　　　　　     - {@code "false"} : returns cached data, which is refreshed periodically
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster unit ID
		 * Response : Unit data [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定したユニットデータ取得のためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : ID で指定したユニットのユニットデータを取得する.
		 * 　　   ヘッダに urgent 指定があればキャッシュではなくその場でデバイスに問い合せフレッシュなデータを返す.
		 * 　　   そうでなければ定期的にリフレッシュしてあるキャッシュデータを返す.
		 * 　　   ヘッダに gridMasterUnitId 指定が必要であり GridMaster インタロック値と一致する必要がある.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "urgent"} : 緊急フラグ
		 * 　　　　　　　　     - {@code "true"} : その場でデバイスに問合せフレッシュなデータを返す
		 * 　　　　　　　　     - {@code "false"} : 定期的にリフレッシュしてあるキャッシュデータを返す
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster ユニット ID
		 * レスポンス : ユニットデータ [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String unitData(String unitId) {
			return "apis." + unitId + ".Controller.data";
		}
		/**
		 * Address to collect unit data of all units by GridMaster.
		 * Scope : Global
		 * Process : Sends unit data of own unit to specified address.
		 * 　　   Used by GridMaster's data collection process.
		 * 　　   If urgent is specified in header, returns fresh data by inquiring device on the spot, not cached data.
		 * 　　   If not, returns cached data, which is refreshed periodically.
		 * 　　   GridMasterUnitId must be specified in header and must match GridMaster interlock value.
		 * 　　   replyAddress, which sends back data, must be specified in the header.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "urgent"} : urgent flag
		 * 　　　　　　　　     - {@code "true"} : returns fresh data by inquiring device on the spot
		 * 　　　　　　　　     - {@code "false"} : returns cached data, which is refreshed periodically
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster unit ID
		 * 　　　　　　　　   - {@code "replyAddress"} : address for sending back data
		 * Response : None
		 * @return address string
		 * GridMaster が全ユニットのユニットデータを収集するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 指定されたアドレスに対し自ユニットのユニットデータを送る.
		 * 　　   GridMaster のデータ収集処理で使用する.
		 * 　　   ヘッダに urgent 指定があればキャッシュではなくその場でデバイスに問い合せフレッシュなデータを返す.
		 * 　　   そうでなければ定期的にリフレッシュしてあるキャッシュデータを返す.
		 * 　　   ヘッダに gridMasterUnitId 指定が必要であり GridMaster インタロック値と一致する必要がある.
		 * 　　   ヘッダにデータを送り返すアドレス replyAddress 指定が必要である.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "urgent"} : 緊急フラグ
		 * 　　　　　　　　     - {@code "true"} : その場でデバイスに問合せフレッシュなデータを返す
		 * 　　　　　　　　     - {@code "false"} : 定期的にリフレッシュしてあるキャッシュデータを返す
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster ユニット ID
		 * 　　　　　　　　   - {@code "replyAddress"} : データを送り返すアドレス
		 * レスポンス : なし
		 * @return アドレス文字列
		 */
		public static String unitDatas() {
			return "apis.Controller.data.all";
		}
		/**
		 * Address to get device control status of own unit.
		 * Scope : Local
		 * Process : Gets device control status of own unit.
		 * 　　   Returns fresh data by inquiring device on the spot, not cached data.
		 * Message body : None
		 * Message header : None
		 * Response : Device control status [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 自ユニットのデバイス制御状態を取得するためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : 自ユニットのデバイス制御状態を取得する.
		 * 　　   キャッシュではなくその場でデバイスに問い合せフレッシュなデータを返す.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : デバイス制御状態 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String urgentUnitDeviceStatus() {
			return "apis.Controller.device.status.urgent";
		}
		/**
		 * Address to get device control status of own unit.
		 * Scope : Local
		 * Process : Gets device control status of own unit.
		 * 　　   If urgent is specified in header, returns fresh data by inquiring device on the spot, not cached data.
		 * 　　   If not, returns cached data, which is refreshed periodically.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "urgent"} : urgent flag
		 * 　　　　　　　　     - {@code "true"} : returns fresh data by inquiring device on the spot
		 * 　　　　　　　　     - {@code "false"} : returns cached data, which is refreshed periodically
		 * Response : Device control status [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 自ユニットのデバイス制御状態を取得するためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : 自ユニットのデバイス制御状態を取得する.
		 * 　　   ヘッダに urgent 指定があればキャッシュではなくその場でデバイスに問い合せフレッシュなデータを返す.
		 * 　　   そうでなければ定期的にリフレッシュしてあるキャッシュデータを返す.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "urgent"} : 緊急フラグ
		 * 　　　　　　　　     - {@code "true"} : その場でデバイスに問合せフレッシュなデータを返す
		 * 　　　　　　　　     - {@code "false"} : 定期的にリフレッシュしてあるキャッシュデータを返す
		 * レスポンス : デバイス制御状態 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String unitDeviceStatus() {
			return "apis.Controller.device.status";
		}
		/**
		 * Address to get control status of device specified by ID.
		 * Scope : Global
		 * Process : Gets control status of device specified by ID.
		 * 　　   If urgent is specified in header, returns fresh data by inquiring device on the spot, not cached data.
		 * 　　   If not, returns cached data, which is refreshed periodically.
		 * 　　   GridMasterUnitId must be specified in header and must match GridMaster interlock value.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "urgent"} : urgent flag
		 * 　　　　　　　　     - {@code "true"} : returns fresh data by inquiring device on the spot
		 * 　　　　　　　　     - {@code "false"} : returns cached data, which is refreshed periodically
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster unit ID
		 * Response : Device control status [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定したデバイス制御状態の取得のためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : ID で指定したユニットのデバイス制御状態を取得する.
		 * 　　   ヘッダに urgent 指定があればキャッシュではなくその場でデバイスに問い合せフレッシュなデータを返す.
		 * 　　   そうでなければ定期的にリフレッシュしてあるキャッシュデータを返す.
		 * 　　   ヘッダに gridMasterUnitId 指定が必要であり GridMaster インタロック値と一致する必要がある.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "urgent"} : 緊急フラグ
		 * 　　　　　　　　     - {@code "true"} : その場でデバイスに問合せフレッシュなデータを返す
		 * 　　　　　　　　     - {@code "false"} : 定期的にリフレッシュしてあるキャッシュデータを返す
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster ユニット ID
		 * レスポンス : デバイス制御状態 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String unitDeviceStatus(String unitId) {
			return "apis." + unitId + ".Controller.device.status";
		}
		/**
		 * Address to control unit specified by ID.
		 * Address : {@link ServiceAddress.Controller#deviceControlling(String)}
		 * Scope : Global
		 * Process : Controls unit specified by ID.
		 * 　　   Sends content of control in message body.
		 * 　　   GridMasterUnitId must be specified in header and must match GridMaster interlock value.
		 * 　　   Specific process is implemented in child class.
		 * Message body : command and parameters [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header :
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster unit ID
		 * Response : Device control status [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定したユニット制御のためのアドレス.
		 * アドレス : {@link ServiceAddress.Controller#deviceControlling(String)}
		 * 範囲 : グローバル
		 * 処理 : ID で指定したユニットのデバイスを制御する.
		 * 　　   制御の内容はメッセージボディで送る.
		 * 　　   ヘッダに gridMasterUnitId 指定が必要であり GridMaster インタロック値と一致する必要がある.
		 * 　　   具体的な処理は子クラスで実装する.
		 * メッセージボディ : コマンドおよびパラメタ [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "gridMasterUnitId"} : GridMaster ユニット ID
		 * レスポンス : デバイス制御状態 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String deviceControlling(String unitId) {
			return "apis." + unitId + ".Controller.control.device";
		}
		/**
		 * Address to stop own unit's device.
		 * Scope : Local
		 * Process : Stops own unit's device.
		 * Message body : None
		 * Message header : None
		 * Response : Device control status [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string

		 * 自ユニットのデバイスを停止するためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : 自ユニットのデバイスを停止する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : デバイス制御状態 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String stopLocal() {
			return "apis.Controller.stop.local";
		}
		/**
		 * Address for emergency stop.
		 * Scope : Global
		 * Process : Emergency stops devices of all units.
		 * 　　   GridMaster interlock is not necessary for emergency process.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "excludeVoltageReference"} : voltage reference exclusion flag
		 * 　　　　　　　　     - {@code "true"} : own unitが電圧リファレンスならスルーする
		 * 　　　　　　　　     - {@code "false"} : own unitが電圧リファレンスでも停止命令を送信する
		 * Response : Device control status [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 緊急停止のためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 全ユニットのデバイスを緊急停止する.
		 * 　　   緊急処理のため GridMaster インタロックは不要.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "excludeVoltageReference"} : 電圧リファレンス除外フラグ
		 * 　　　　　　　　     - {@code "true"} : 自ユニットが電圧リファレンスならスルーする
		 * 　　　　　　　　     - {@code "false"} : 自ユニットが電圧リファレンスでも停止命令を送信する
		 * レスポンス : デバイス制御状態 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String scram() {
			return "apis.Controller.scram";
		}
		/**
		 * Address to carry out battery capacity management functions (confirms whether or not new Power Sharing is possible)
		 * Scope : Local
		 * Process : Confirms battery capacity and current Power Sharing status and determines whether or not new Power Sharing is possible.
		 * 　　   If one battery is shared by multiple apis-main, such as for Gateway operation, coordinates management of battery capacity with other apis-main. 
		 * 　　   At present, all apis-main that coordinate management must be on the same computer (because file system is used for coordinating management).
		 * Message body : direction of Power Sharing [{@link String}]
		 * 　　　　　　　　   - {@code "DISCHARGE"} : discharge
		 * 　　　　　　　　   - {@code "CHARGE"} : charge
		 * Message header : None
		 * Response : {@link Boolean#TRUE} if possible or there are no battery capacity management functions
		 * 　　　　　   {@link Boolean#FALSE} if not possible
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * バッテリ容量管理機能 ( 新しい融通が可能か否かの確認 ) のためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : バッテリ容量と現在の融通状態を確認し新しい融通が可能か否か判定する.
		 * 　　   Gateway 運用など一つのバッテリを複数の apis-main で共有している場合に他の apis-main とバッテリ電流容量を協調管理する.
		 * 　　   現状は協調管理する apis-main が全て同一コンピュータ上にある必要がある ( ファイルシステムを用いて協調管理するため ).
		 * メッセージボディ : 融通方向 [{@link String}]
		 * 　　　　　　　　   - {@code "DISCHARGE"} : 送電
		 * 　　　　　　　　   - {@code "CHARGE"} : 受電
		 * メッセージヘッダ : なし
		 * レスポンス : 可またはバッテリ容量管理機能が無効なら {@link Boolean#TRUE}
		 * 　　　　　   不可なら {@link Boolean#FALSE}
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String batteryCapacityTesting() {
			return "apis.Controller.batteryCapacity.test";
		}
		/**
		 * Address for battery capacity management functions ( 融通枠の確保と解放 )
		 * Scope : Local
		 * Process : 融通枠を確保/解放する.
		 * 　　   If one battery is shared by multiple apis-main, such as for Gateway operation, coordinates management of battery capacity with other apis-main.
		 * 　　   At present, all apis-main that coordinate management must be on the same computer (because file system is used for coordinating management).
		 * Message body : Power Sharing information [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header : {@code "command"}
		 * 　　　　　　　　   - {@code "acquire"} : Secures Power Sharing frame
		 * 　　　　　　　　   - {@code "release"} : Releases Power Sharing frame
		 * Response : {@link Boolean#TRUE} if successful or there are no battery capacity management functions
		 * 　　　　　   {@link Boolean#FALSE} if fail
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * バッテリ容量管理機能 ( 融通枠の確保と解放 ) のためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : 融通枠を確保/解放する.
		 * 　　   Gateway 運用など一つのバッテリを複数の apis-main で共有している場合に他の apis-main とバッテリ電流容量を協調管理する.
		 * 　　   現状は協調管理する apis-main が全て同一コンピュータ上にある必要がある ( ファイルシステムを用いて協調管理するため ).
		 * メッセージボディ : 融通情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ : {@code "command"}
		 * 　　　　　　　　   - {@code "acquire"} : 融通枠を確保する
		 * 　　　　　　　　   - {@code "release"} : 融通枠を解放する
		 * レスポンス : 成功またはバッテリ容量管理機能が無効なら {@link Boolean#TRUE}
		 * 　　　　　   失敗なら {@link Boolean#FALSE}
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String batteryCapacityManaging() {
			return "apis.Controller.batteryCapacity.manage";
		}
	}
	/**
	 * Carries out unified management of {@link io.vertx.core.eventbus.EventBus} addresses used by User service.
	 * @author OES Project
	 * User サービスで使用する {@link io.vertx.core.eventbus.EventBus} アドレスの一元管理.
	 * @author OES Project
	 */
	public static class User {
		/**
		 * Address to get own unit's SCENARIO.
		 * Specifies datetime in APIS standard format ( {@code uuuu/MM/dd-HH:mm:ss} ).
		 * Scope : Local
		 * Process : Gets own unit's SCENARIO.
		 * 　　   Returns settings at specified time from periodically refreshed cache.
		 * Message body : datetime [{@link String}]
		 * 　　　　　　　　   APIS standard format ( uuuu/MM/dd-HH:mm:ss )
		 * Message header : None
		 * Response : SCENARIO subset [{@link io.vertx.core.json.JsonObject JsonObject}] at specified time
		 * 　　　　　   Fail if not found.
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 自ユニットの SCENARIO を取得するアドレス.
		 * APIS 標準フォーマット ( {@code uuuu/MM/dd-HH:mm:ss} ) で日時を指定する.
		 * 範囲 : ローカル
		 * 処理 : 自ユニットの SCENARIO を取得する.
		 * 　　   定期的にリフレッシュされているキャッシュから指定した時刻における設定を返す.
		 * メッセージボディ : 日時 [{@link String}]
		 * 　　　　　　　　   APIS 標準フォーマット ( uuuu/MM/dd-HH:mm:ss )
		 * メッセージヘッダ : なし
		 * レスポンス : 指定した時刻における SCENARIO サブセット [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　   見つからない場合は fail.
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String scenario() {
			return "apis.User.scenario";
		}
		/**
		 * Address to set/get local Power Sharing mode specified by ID.
		 * Scope : Global
		 * Process : Sets/gets own unit's local Power Sharing mode.
		 * 　　   Value is one of the following.
		 * 　　   - {@code null}
		 * 　　   - "heteronomous"
		 * 　　   - "stop"
		 * Message body :
		 * 　　　　　　　　   For set : local Power Sharing mode to be set [{@link String}]
		 * 　　　　　　　　   For get : None
		 * Message header : {@code "command"}
		 * 　　　　　　　　   - {@code "set"} : Updates local Power Sharing mode
		 * 　　　　　　　　   - {@code "get"} : Gets local Power Sharing mode
		 * Response :
		 * 　　　　　   For set : ID [{@link String}] of own unit
		 * 　　　　　   For get : Current local Power Sharing mode [{@link String}]
		 * 　　　　　   Fail if error occurs.
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定したローカル融通モードを set/get するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 自ユニットのローカル融通モードを set/get する.
		 * 　　   値は以下のいずれか.
		 * 　　   - {@code null}
		 * 　　   - "heteronomous"
		 * 　　   - "stop"
		 * メッセージボディ :
		 * 　　　　　　　　   set の場合 : 設定するローカル融通モード [{@link String}]
		 * 　　　　　　　　   get の場合 : なし
		 * メッセージヘッダ : {@code "command"}
		 * 　　　　　　　　   - {@code "set"} : ローカル融通モードを変更する
		 * 　　　　　　　　   - {@code "get"} : ローカル融通モードを取得する
		 * レスポンス :
		 * 　　　　　   set の場合 : 自ユニットの ID [{@link String}]
		 * 　　　　　   get の場合 : 現在のローカル融通モード [{@link String}]
		 * 　　　　　   エラーが起きたら fail.
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String operationMode(String unitId) {
			return "apis." + unitId + ".operationMode";
		}
		/**
		 * Address to process request from another unit.
		 * This is the name used to relay by Mediator service.
		 * Scope : Local
		 * Process : Processes request from another unit.
		 * Message body : Request [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header : None
		 * Response : Information about acceptance of request [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　   {@code null} if not accepted
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 他ユニットからのリクエストを処理するためのアドレス.
		 * Mediator サービスが中継するためこんな名前.
		 * 範囲 : ローカル
		 * 処理 : 他ユニットからのリクエストを処理する.
		 * メッセージボディ : リクエスト [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ : なし
		 * レスポンス : リクエストに対するアクセプト情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　   アクセプトしなかった場合は {@code null}
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String mediatorRequest() {
			return "apis.User.mediatorRequest";
		}
		/**
		 * Address to process group of accepts for request issued by own unit.
		 * This is the name used to relay by Mediator service.
		 * Scope : Local
		 * Process : Processes group of accepts for request issued by own unit.
		 * Message body : Request and group of accepts [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　　　　   - {@code "request"} : Request information [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　　　　   - {@code "accepts"} : List of accept information [{@link io.vertx.core.json.JsonArray JsonArray}]
		 * Message header : None
		 * Response : Accept information about selected result [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　   {@code null} if accepts are not selected
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 自ユニットが発したリクエストに対するアクセプト群を処理するためのアドレス.
		 * Mediator サービスが中継するためこんな名前.
		 * 範囲 : ローカル
		 * 処理 : 自ユニットが発したリクエストに対するアクセプト群を処理する.
		 * メッセージボディ : リクエストおよびアクセプト群 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　　　　   - {@code "request"} : リクエスト情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　　　　   - {@code "accepts"} : アクセプト情報のリスト [{@link io.vertx.core.json.JsonArray JsonArray}]
		 * メッセージヘッダ : なし
		 * レスポンス : 選択結果のアクセプト情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　   アクセプトが選ばれなかった場合は {@code null}
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String mediatorAccepts() {
			return "apis.User.mediatorAccepts";
		}
		/**
		 * Address to confirm existence of errors in unit specified by ID.
		 * Address : {@link ServiceAddress.User#errorTesting(String)}
		 * Scope : Global
		 * Process : Confirms existence of local errors.
		 * Message body : None
		 * Message header : None
		 * Response : Existence [{@link Boolean}] of local errors
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定したユニットのエラー有無を確認するためのアドレス.
		 * アドレス : {@link ServiceAddress.User#errorTesting(String)}
		 * 範囲 : グローバル
		 * 処理 : ローカルエラーの有無を確認する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : ローカルエラーの有無 [{@link Boolean}]
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String errorTesting(String unitId) {
			return "apis." + unitId + ".User.error.test";
		}
	}
	/**
	 * Carries out unified management of {@link io.vertx.core.eventbus.EventBus} addresses used by Mediator service.
	 * @author OES Project
	 * Mediator サービスで使用する {@link io.vertx.core.eventbus.EventBus} アドレスの一元管理.
	 * @author OES Project
	 */
	public static class Mediator {
		/**
		 * Address to process requests issued by User service.
		 * Scope : Local
		 * Process : Receives request from own unit and executes Power Sharing deal.
		 * Message body : request information [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header : None
		 * Response : deploymentID [{@link String}]
		 * @return address string
		 * User サービスが発したリクエストを処理するためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : 自ユニットからリクエストを受け取り融通交渉を実行する.
		 * メッセージボディ : リクエスト情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ : なし
		 * レスポンス : deploymentID [{@link String}]
		 * @return アドレス文字列
		 */
		public static String internalRequest() {
			return "apis.Mediator.internalRequest";
		}
		/**
		 * Address to process request from another unit.
		 * Scope : Global
		 * Process : Receives request request from another unit and sends accept to address.
		 * 　　   Does not send if not accepted.
		 * 　　   Accept creation is relayed to User service.
		 * Message body : request information [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header :
		 * 　　　　　　　　   - {@code "replyAddress"} : address to reply acceptance to
		 * Response : None
		 * @return address string
		 * 他ユニットからのリクエストを処理するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 他ユニットからリクエストを受け取り指定されたアドレスに対しアクセプトを送る.
		 * 　　   アクセプトしない場合は送らない.
		 * 　　   アクセプト作成は User サービスへ中継する.
		 * メッセージボディ : リクエスト情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "replyAddress"} : アクセプトを送り返すアドレス
		 * レスポンス : なし
		 * @return アドレス文字列
		 */
		public static String externalRequest() {
			return "apis.Mediator.externalRequest";
		}
		/**
		 * Address to get list of Power Sharing information
		 * Scope : Global
		 * Process : Gets list of Power Sharing information.
		 * Message body : None
		 * Message header : None
		 * Response : List of Power Sharing information [{@link io.vertx.core.json.JsonArray JsonArray}] managed in shared memory.
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 融通情報リストを取得するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 融通情報リストを取得する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : 共有メモリに管理されている融通情報リスト [{@link io.vertx.core.json.JsonArray JsonArray}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String deals() {
			return "apis.Mediator.deals";
		}
		/**
		 * Address to register materialized Power Sharing.
		 * Scope : Global
		 * Process : Registers Power Sharing information.Registers 
		 * Message body : Power Sharing information [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header : None
		 * Response : Created Power Sharing information [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if failed.
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 成立した融通を登録するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 融通情報を登録する.
		 * メッセージボディ : 融通情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ : なし
		 * レスポンス : 作成された融通情報 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   失敗したら fail.
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String dealCreation() {
			return "apis.Mediator.deal.create";
		}
		/**
		 * Address to delete Power Sharing
		 * Scope : Local
		 * Process : Deletes Power Sharing information.
		 * Message body : Power Sharing ID [{@link String}]
		 * Message header : None
		 * Response : Deleted Power Sharing informationPower Sharing information [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 融通を削除するためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : 融通情報を削除する.
		 * メッセージボディ : 融通 ID [{@link String}]
		 * メッセージヘッダ : なし
		 * レスポンス : 削除された融通情報 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String dealDisposition() {
			return "apis.Mediator.deal.dispose";
		}
		/**
		 * Address to request stopping Power Sharing
		 * Scope : Global
		 * Process : Sends request to stop Power Sharing.
		 * Message body : Stop request [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　　　　   - {@code "dealId"} : Power Sharing ID
		 * 　　　　　　　　   - {@code "reasons"} : List of reasons [{@link io.vertx.core.json.JsonArray JsonArray}]
		 * Message header : None
		 * Response : Power Sharing ID [{@link String}]
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 融通停止要求のためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 融通停止要求を送信する.
		 * メッセージボディ : 停止要求 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * 　　　　　　　　   - {@code "dealId"} : 融通 ID
		 * 　　　　　　　　   - {@code "reasons"} : 理由リスト [{@link io.vertx.core.json.JsonArray JsonArray}]
		 * メッセージヘッダ : なし
		 * レスポンス : 融通 ID [{@link String}]
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String dealNeedToStop() {
			return "apis.Mediator.deal.needToStop";
		}
		/**
		 * Address to register Power Sharing
		 * Scope : Global.
		 * Process : Registers Power Sharing.
		 * 　　   - apis-main : Saves to file system if own unit participates in received Power Sharing.
		 * 　　                 ignores if own unit does not participate in received Power Sharing.
		 * 　　   - apis-ccc : Reports Power Sharing information to Service Center.
		 * Message body : Power Sharing information [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header : None
		 * Response :
		 * 　　　　　   - apis-main : Own unit ID [{@link String}] if own unit participates in received Power Sharing.
		 * 　　　　　                 {@code "N/A"} if own unit does not participate in received Power Sharing.
		 * 　　　　　   - apis-ccc : {@code "ok"} if reporting function is enabled.
		 * 　　　　　                {@code "N/A"} if reporting function is not enabled.
		 * 　　　　　   - Fail if error occurs.
		 * @return address string
		 * 融通を記録するためのアドレス.
		 * 範囲 : グローバル.
		 * 処理 : 融通を記録する.
		 * 　　   - apis-main : 受け取った融通に自ユニットが参加していたらファイルシステムに保存する.
		 * 　　                 受け取った融通に自ユニットが参加していなければスルー.
		 * 　　   - apis-ccc : Service Center に対し融通情報を通知する.
		 * メッセージボディ : 融通情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ : なし
		 * レスポンス :
		 * 　　　　　   - apis-main : 受け取った融通に自ユニットが参加していたら自ユニットの ID [{@link String}].
		 * 　　　　　                 受け取った融通に自ユニットが参加していなかったら {@code "N/A"}.
		 * 　　　　　   - apis-ccc : 通知機能が有効なら {@code "ok"}.
		 * 　　　　　                通知機能が無効なら {@code "N/A"}.
		 * 　　　　　   - エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String dealLogging() {
			return "apis.Mediator.deal.log";
		}
		/**
		 * Address for GridMaster interlock function
		 * Scope : Local
		 * Process : Secures/releases GridMaster interlock.
		 * Message body : GridMaster unit ID [{@link String}]
		 * Message header :
		 * 　　　　　　　　   - {@code "command"}
		 * 　　　　　　　　     - {@code "acquire"} : Secures interlock
		 * 　　　　　　　　     - {@code "release"} : Releases interlock
		 * Response : Own unit ID [{@link String}]
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * GridMaster インタロック機能のためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : GridMaster インタロックを確保/解放する.
		 * メッセージボディ : GridMaster ユニット ID [{@link String}]
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "command"}
		 * 　　　　　　　　     - {@code "acquire"} : インタロックを確保する
		 * 　　　　　　　　     - {@code "release"} : インタロックを解放する
		 * レスポンス : 自ユニットの ID [{@link String}]
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String gridMasterInterlocking() {
			return "apis.Mediator.interlock.gridMaster";
		}
		/**
		 * Address for interlock function of Power Sharing specified by ID.
		 * Scope : Global
		 * Process : Secures/releases Power Sharing interlock.
		 * Message body : Power Sharing information [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * Message header :
		 * 　　　　　　　　   - {@code "command"}
		 * 　　　　　　　　     - {@code "acquire"} : Secures interlock
		 * 　　　　　　　　     - {@code "release"} : Releases interlock
		 * Response : Own unit ID [{@link String}]
		 * 　　　　　   Fail if error occurs.
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定した融通インタロック機能のためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 融通インタロックを確保/解放する.
		 * メッセージボディ : 融通情報 [{@link io.vertx.core.json.JsonObject JsonObject}]
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "command"}
		 * 　　　　　　　　     - {@code "acquire"} : インタロックを確保する
		 * 　　　　　　　　     - {@code "release"} : インタロックを解放する
		 * レスポンス : 自ユニットの ID [{@link String}]
		 * 　　　　　   エラーが起きたら fail.
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String dealInterlocking(String unitId) {
			return "apis." + unitId + ".Mediator.interlock.deal";
		}
		/**
		 * Address to establish GridMaster in appropriate unit.
		 * Scope : Local
		 * Process : Maintains existence of GridMaster.
		 * 　　   Confirms existence.
		 * 　　   Confirms place of existence.
		 * 　　   Moves to appropriate unit.
		 * Message body : None
		 * Message header : None
		 * Response : ID [{@link String}] of unit in which GridMaster exists.
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * 適切なユニットに GridMaster を立てておく機能のためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : GridMaster の存在メンテナンス.
		 * 　　   存在の確認.
		 * 　　   存在場所の確認.
		 * 　　   適切なユニットへの移動.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : GridMaster が存在すユニットの ID [{@link String}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String gridMasterEnsuring() {
			return "apis.Mediator.gridMaster.ensure";
		}
		/**
		 * Address to launch GridMaster specified by ID.
		 * Scope : Global
		 * Process : Starts GridMaster.
		 * Message body : None
		 * Message header : None
		 * Response : Own unit ID [{@link String}].
		 * 　　　　　   Fail if error occurs.
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定した GridMaster 起動のためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : GridMaster を起動する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : 自ユニットの ID [{@link String}].
		 * 　　　　　   エラーが起きたら fail.
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String gridMasterActivation(String unitId) {
			return "apis." + unitId + ".Mediator.gridMaster.activate";
		}
		/**
		 * Address to stop GridMaster specified by ID.
		 * Scope : Global
		 * Process : Stops GridMaster.
		 * Message body : None
		 * Message header : None
		 * Response : Own unit ID [{@link String}].
		 * 　　　　　   Fail if error occurs.
		 * @param unitId ID of target unit
		 * @return address string
		 * ID を指定した GridMaster 停止のためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : GridMaster を停止する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : 自ユニットの ID [{@link String}].
		 * 　　　　　   エラーが起きたら fail.
		 * @param unitId 対象ユニットの ID
		 * @return アドレス文字列
		 */
		public static String gridMasterDeactivation(String unitId) {
			return "apis." + unitId + ".Mediator.gridMaster.deactivate";
		}
	}
	/**
	 * Carries out unified management of {@link io.vertx.core.eventbus.EventBus} addresses used by GridMaster service.
	 * @author OES Project
	 * GridMaster サービスで使用する {@link io.vertx.core.eventbus.EventBus} アドレスの一元管理.
	 * @author OES Project
	 */
	public static class GridMaster {
		/**
		 * Address for use in technique to ensure that there are no duplicate Gridmasters in the cluster.
		 * Scope : Global
		 * Process : Technique to ensure that there are no duplicate Gridmasters.
		 * 　　   Returns unit ID of own unit if message body is empty ( because it is inquiry of GM unit ID).
		 * 　　   If there is message body and it matches own {@link io.vertx.core.AbstractVerticle#deploymentID()}, then ignores (because it is own self).
		 * 　　   Global error if there is message body and it does not match own {@link io.vertx.core.AbstractVerticle#deploymentID()} ( because it means another GM exists).
		 * Message body : Sender Verticle の {@link io.vertx.core.AbstractVerticle#deploymentID()} [{@link String}]
		 * Message header : None
		 * Response : ID [{@link String}] of own unit if message body is empty.
		 * 　　　　　   In all other cases, returns nothing.
		 * @return address string
		 * クラスタ内に GridMaster が重複しないための仕組みで使用するアドレス.
		 * 範囲 : グローバル
		 * 処理 : GridMaster が重複しないための仕組み.
		 * 　　   メッセージボディが空なら自ユニットのユニット ID を返す ( GM ユニット ID の問合せなので ).
		 * 　　   メッセージボディがあり自分の {@link io.vertx.core.AbstractVerticle#deploymentID()} と一致したらスルー ( 自分なので ).
		 * 　　   メッセージボディがあり自分の {@link io.vertx.core.AbstractVerticle#deploymentID()} と一致しなかったらグローバルエラー ( 他に GM がいるということなので ).
		 * メッセージボディ : 送信元 Verticle の {@link io.vertx.core.AbstractVerticle#deploymentID()} [{@link String}]
		 * メッセージヘッダ : なし
		 * レスポンス : メッセージボディが空なら自ユニットの ID [{@link String}].
		 * 　　　　　   それ以外は返さない.
		 * @return アドレス文字列
		 */
		public static String helo() {
			return "apis.GridMaster.helo";
		}
		/**
		 * Address to get all units' unit data, which GridMaster periodically refreshes and caches.
		 * Scope : Global
		 * Process : gets all units' unit data.
		 * 　　   Returns cache values periodically refreshed by GridMaster.
		 * Message body : None
		 * Message header : None
		 * Response : all units' unit data [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * GridMaster が定期的にリフレッシュしてキャッシュしている全ユニットのユニットデータを取得するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 全ユニットのユニットデータを取得する.
		 * 　　   GridMaster が定期的にリフレッシュしているキャッシュ値を返す.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : 全ユニットのユニットデータ [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String unitDatas() {
			return "apis.GridMaster.data.all";
		}
		/**
		 * Address to immediately collect all units' unit data by GridMaster
		 * Address : {@link ServiceAddress.GridMaster#urgentUnitDatas()}
		 * Scope : Local
		 * Process : Gets all units' unit data.
		 * 　　   Inquires all units and returns collected result instead of values periodically refreshed and cached by GridMaster.
		 * 　　   Returns cache without collecting data if most recent data collection is newer than time stamp sent in message body.
		 *      Cached data that is periodically refreshed in each unit is gathered because urgent is not specified in the header at the time of collection.
		 * Message body : Timestamp [{@link Number}] for which collection is unnecessary if collection time is newer
		 * Message header : None
		 * Response : all units' unit data [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * GridMaster が全ユニットのユニットデータを今すぐ収集するためのアドレス.
		 * アドレス : {@link ServiceAddress.GridMaster#urgentUnitDatas()}
		 * 範囲 : ローカル
		 * 処理 : 全ユニットのユニットデータを取得する.
		 * 　　   GridMaster が定期的にリフレッシュしているキャッシュ値ではなく全ユニットに問合せ収集した結果を返す.
		 * 　　   メッセージボディでタイムスタンプが送られその値より直近のデータ収集が新しい場合はデータ収集を実行せずキャッシュを返す.
		 * 　　   収集時ヘッダに urgent 指定をしないため各ユニットで定期的にリフレッシュしてあるキャッシュデータが集まる.
		 * メッセージボディ : これより新しければ収集不要タイムスタンプ [{@link Number}]
		 * メッセージヘッダ : なし
		 * レスポンス : 全ユニットのユニットデータ [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String urgentUnitDatas() {
			return "apis.GridMaster.data.all.urgent";
		}
		/**
		 * Address to get list of ID of all units from GridMaster.
		 * Scope : Global
		 * Process : Gets list of ID of all units.
		 * 　　   Gets list of ID from POLICY.
		 * Message body : None
		 * Message header : None
		 * Response : List of ID of all units [{@link io.vertx.core.json.JsonArray JsonArray}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * GridMaster から全ユニットの ID リストを取得するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : 全ユニットの ID リストを取得する.
		 * 　　   ID リストは POLICY から取得する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : 全ユニットの ID リスト [{@link io.vertx.core.json.JsonArray JsonArray}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String unitIds() {
			return "apis.GridMaster.unitId.all";
		}
		/**
		 * Address to confirm existence of global errors.
		 * Scope : Global
		 * Process : Confirms existence of global errors.
		 * Message body : None
		 * Message header : None
		 * Response : existence of global error [{@link Boolean}]
		 * @return address string
		 * グローバルエラーの有無を確認するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : グローバルエラーの有無を確認する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : グローバルエラーの有無 [{@link Boolean}]
		 * @return アドレス文字列
		 */
		public static String errorTesting() {
			return "apis.GridMaster.error.test";
		}
		/**
		 * Address to stop GridMaster.
		 * Scope : Local
		 * Process : Stops GridMaster.
		 * Message body : None
		 * Message header : None
		 * Response : Own unit ID [{@link String}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * GridMaster を停止するためのアドレス.
		 * 範囲 : ローカル
		 * 処理 : GridMaster を停止する.
		 * メッセージボディ : なし
		 * メッセージヘッダ : なし
		 * レスポンス : 自ユニットの ID [{@link String}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String undeploymentLocal() {
			return "apis.GridMaster.undeploy.local";
		}
	}

	/**
	 * Carries out unified management of {@link io.vertx.core.eventbus.EventBus} addresses used by apis-ccc's ControlCenterClient service.
	 * @author OES Project
	 * apis-ccc の ControlCenterClient サービスで使用する {@link io.vertx.core.eventbus.EventBus} アドレスの一元管理.
	 * @author OES Project
	 */
	public static class ControlCenterClient {
		/**
		 * Address to get SCENARIO from Service Center.
		 * Scope : Global
		 * Process : Gets SCENARIO from Service Center.
		 * 　　   account, password, unitId are required.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "account"} : Account
		 * 　　　　　　　　   - {@code "password"} : Password
		 * 　　　　　　　　   - {@code "unitId"} : Unit ID
		 * Response : Acquired SCENARIO information [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * Service Center から SCENARIO を取得するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : Service Center から SCENARIO を取得する.
		 * 　　   account, password, unitId が必要.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "account"} : アカウント
		 * 　　　　　　　　   - {@code "password"} : パスワード
		 * 　　　　　　　　   - {@code "unitId"} : ユニット ID
		 * レスポンス : 取得した SCENARIO 情報 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String scenario() {
			return "apis.ControlCenterClient.scenario";
		}
		/**
		 * Address to get POLICY from Service Center.
		 * Scope : Global
		 * Process : Gets POLICY from Service Center.
		 * 　　   account, password, unitId are required.
		 * Message body : None
		 * Message header :
		 * 　　　　　　　　   - {@code "account"} : Account
		 * 　　　　　　　　   - {@code "password"} : Password
		 * 　　　　　　　　   - {@code "unitId"} : Unit ID
		 * Response : Acquired POLICY policy [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   Fail if error occurs.
		 * @return address string
		 * Service Center から POLICY を取得するためのアドレス.
		 * 範囲 : グローバル
		 * 処理 : Service Center から POLICY を取得する.
		 * 　　   account, password, unitId が必要.
		 * メッセージボディ : なし
		 * メッセージヘッダ :
		 * 　　　　　　　　   - {@code "account"} : アカウント
		 * 　　　　　　　　   - {@code "password"} : パスワード
		 * 　　　　　　　　   - {@code "unitId"} : ユニット ID
		 * レスポンス : 取得した POLICY 情報 [{@link io.vertx.core.json.JsonObject JsonObject}].
		 * 　　　　　   エラーが起きたら fail.
		 * @return アドレス文字列
		 */
		public static String policy() {
			return "apis.ControlCenterClient.policy";
		}
	}

}

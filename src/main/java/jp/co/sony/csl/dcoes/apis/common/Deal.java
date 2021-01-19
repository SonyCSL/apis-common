package jp.co.sony.csl.dcoes.apis.common;

import java.util.UUID;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import jp.co.sony.csl.dcoes.apis.common.util.vertx.JsonObjectUtil;

/**
 * This tool references attributes from {@link JsonObject} holding Power Sharing information.
 * Only processes generating Power Sharing ID carry out updates. Other processes only reference Power Sharings.
 * Updated mainly by {@code jp.co.sony.csl.dcoes.apis.main.app.mediator.util.DealUtil}.
 * 融通情報を保持する {@link JsonObject} から属性を参照するツール.
 * 融通 ID を生成する処理のみ更新する以外は参照のみ.
 * 更新は主に {@code jp.co.sony.csl.dcoes.apis.main.app.mediator.util.DealUtil} で行う.
 * @author OES Project
 */
public class Deal {
	private static final Logger log = LoggerFactory.getLogger(Deal.class);

	/**
	 * This datetime string indicates that there is no datetime information.
	 * The value is {@value}.
	 * 日時情報が無いことを示す日時文字列.
	 * 値は {@value}.
	 */
	public static final String NULL_DATE_TIME_VALUE = "--";

	private Deal() { }

	/**
	 * Indicates direction of the Power Sharing.
	 * At present, this is used only by {@code jp.co.sony.csl.dcoes.apis.main.app.controller.BatteryCapacityManagement}.
	 * @author OES Project
	 * 融通の方向を示す.
	 * いまのところ {@code jp.co.sony.csl.dcoes.apis.main.app.controller.BatteryCapacityManagement} で使われているだけ.
	 * @author OES Project
	 */
	public enum Direction {
		/**
		 * Discharge
		 * 送電
		 */
		DISCHARGE,
		/**
		 * Charge
		 * 受電
		 */
		CHARGE,
	}
	/**
	 * Gets {@link Direction} object from string indicating Power Sharing direction.
	 * @param value string indicating direction
	 * @return direction object indicating direction of Power Sharing
	 * 融通の方向を示す文字列から {@link Direction} オブジェクトを取得する.
	 * @param value 方向を示す文字列
	 * @return 方向を示す direction オブジェクト
	 */
	public static Direction direction(String value) {
		try {
			return Direction.valueOf(value.toUpperCase());
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	/**
	 * Resets Power Sharing ID.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return generated ID value 	
	 * 融通 ID を生成しセット.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 生成した ID 値
	 */
	public static String generateDealId(JsonObject deal) {
		String dealId = UUID.randomUUID().toString();
		deal.put("dealId", dealId);
		return dealId;
	}

	/**
	 * Gets Power Sharing ID.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return generated ID value
	 * 融通 ID を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return ID 値
	 */
	public static String dealId(JsonObject deal) {
		return deal.getString("dealId");
	}

	/**
	 * Gets Power Sharing type.
	 * Indicates direction of Power Sharing requested by Power Sharing request creation side.
	 * {@code "charge"} or {@code "discharge"}.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return string indicating Power Sharing type
	 * 融通の種類を取得.
	 * 融通リクエスト作成側が要求した融通方向.
	 * {@code "charge"} または {@code "discharge"}.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通の種類を示す文字列
	 */
	public static String type(JsonObject deal) {
		return deal.getString("type");
	}

	/**
	 * Gets ID of requesting unit.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return ID of the requesting unit
	 * リクエストユニットの ID を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return リクエストユニットの ID
	 */
	public static String requestUnitId(JsonObject deal) {
		return deal.getString("requestUnitId");
	}
	/**
	 * Gets ID of accepting unit.
	 * @param deal jsonobject object of target Power Sharing information      
	 * @return ID of accepting unit
	 * アクセプトユニットの ID を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return アクセプトユニットの ID
	 */
	public static String acceptUnitId(JsonObject deal) {
		return deal.getString("acceptUnitId");
	}

	/**
	 * Gets ID of discharging unit.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return ID of discharging unit
	 * 送電ユニットの ID を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 送電ユニットの ID
	 */
	public static String dischargeUnitId(JsonObject deal) {
		return deal.getString("dischargeUnitId");
	}
	/**
	 * Gets ID of charging unit.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return ID of charging unit
	 * 受電ユニットの ID を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 受電ユニットの ID
	 */
	public static String chargeUnitId(JsonObject deal) {
		return deal.getString("chargeUnitId");
	}

	/**
	 * Gets estimated power of Power Sharing.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return estimated power of Power Sharing
	 * 予定融通電力を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 予定融通電力
	 */
	public static Float dealAmountWh(JsonObject deal) {
		return deal.getFloat("dealAmountWh");
	}

	/**
	 * Gets grid voltage that is most efficient for unit on discharging side. 
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return grid voltage that is most efficient for unit on discharging side
	 * 送電側ユニットが最も効率が良くなるグリッド電圧を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 送電側ユニットが最も効率が良くなるグリッド電圧
	 */
	public static Float dischargeUnitEfficientGridVoltageV(JsonObject deal) {
		return deal.getFloat("dischargeUnitEfficientGridVoltageV");
	}
	/**
	 * Gets grid voltage that is most efficient for unit on charging side. 
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return grid voltage that is most efficient for unit on charging side
	 * 受電側ユニットが最も効率が良くなるグリッド電圧を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 受電側ユニットが最も効率が良くなるグリッド電圧
	 */
	public static Float chargeUnitEfficientGridVoltageV(JsonObject deal) {
		return deal.getFloat("chargeUnitEfficientGridVoltageV");
	}

	/**
	 * Gets grid current during Power Sharing.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return expected grid current during Power Sharing
	 * 融通時のグリッド電流を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通時の予定グリッド電流
	 */
	public static Float dealGridCurrentA(JsonObject deal) {
		return deal.getFloat("dealGridCurrentA");
	}
	/**
	 * Gets target value of current compensation.
	 * This is the grid current of the voltage reference unit before the Power Sharing in question starts.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return target value of current compensation
	 * 電流コンペンセイションのターゲット値を取得.
	 * 当該融通起動前における電圧リファレンスユニットのグリッド電流.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 電流コンペンセイションのターゲット値
	 */
	public static Float compensationTargetVoltageReferenceGridCurrentA(JsonObject deal) {
		return deal.getFloat("compensationTargetVoltageReferenceGridCurrentA");
	}
	/**
	 * Gets grid current value on discharging unit side after current compensation ends. 
	 * This is the grid current of voltage reference unit before the Power Sharing in question begins.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return grid current value of discharging unit side after current compensation ends
	 * 電流コンペンセイション終了後の送電ユニット側グリッド電流値を取得.
	 * 当該融通起動前における電圧リファレンスユニットのグリッド電流.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 電流コンペンセイション終了後の送電ユニット側グリッド電流値
	 */
	public static Float dischargeUnitCompensatedGridCurrentA(JsonObject deal) {
		return deal.getFloat("dischargeUnitCompensatedGridCurrentA");
	}
	/**
	 * Gets grid current value of charging unit side after current compensation ends.
	 * This is the grid current of the voltage reference unit before the Power Sharing in question begins.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return grid current value of charging unit side after current compensation ends
	 * 電流コンペンセイション終了後の受電ユニット側グリッド電流値を取得.
	 * 当該融通起動前における電圧リファレンスユニットのグリッド電流.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 電流コンペンセイション終了後の受電ユニット側グリッド電流値
	 */
	public static Float chargeUnitCompensatedGridCurrentA(JsonObject deal) {
		return deal.getFloat("chargeUnitCompensatedGridCurrentA");
	}

	/**
	 * Gets datetime of registration by Power Sharing.
	 * Requests → Accepts → Forms → "Registers".
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime of registration of Power Sharing
	 * 融通が登録された日時を取得.
	 * リクエスト → アクセプト → 成立 → "登録".
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通が登録された日時を表す文字列
	 */
	public static String createDateTime(JsonObject deal) {
		return deal.getString("createDateTime");
	}
	/**
	 * Gets datetime of start of Power Sharing.
	 * The timing is when startup control is applied to the unit on voltage reference side.
	 * Or, the timing is when the unit is already in operation and applies additional control for the Power Sharing in question.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime of start of Power Sharing
	 * 融通が起動された日時を取得.
	 * 電圧リファレンス側ユニットを起動制御したタイミング.
	 * もしくはすでに稼働中で当該融通のため追加制御したタイミング.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通が起動された日時を表す文字列
	 */
	public static String activateDateTime(JsonObject deal) {
		return deal.getString("activateDateTime");
	}
	/**
	 * Gets datetime of the end of voltage ramp-up.
	 * Has only the Power Sharing that first starts from the condition where there are no Power Sharings.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime of end of voltage ramp-up
	 * 電圧ランプアップが完了した日時を取得.
	 * 融通が無い状態から最初に起動した融通のみ持つ.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 電圧ランプアップが完了した日時を表す文字列
	 */
	public static String rampUpDateTime(JsonObject deal) {
		return deal.getString("rampUpDateTime");
	}
	/**
	 * Gets datetime of end of startup by units on both sides.
	 * The timing is when startup control is applied to the unit on non-voltage reference side.
	 * Or, the timing is when the unit is already in operation and applies additional control for the Power Sharing in question.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime of end of startup by units on both sides
	 * 両側ユニットが起動完了した日時を取得.
	 * 電圧リファレンス側ではない側のユニットを起動制御したタイミング.
	 * もしくはすでに稼働中で当該融通のため追加制御したタイミング.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 両側ユニットが起動完了した日時を表す文字列
	 */
	public static String warmUpDateTime(JsonObject deal) {
		return deal.getString("warmUpDateTime");
	}
	/**
	 * Gets datetime when Power Sharing begins.
	 * The timing is when current compensation ends.
	 * Calculation of the total power of Power Sharing begins here.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime when Power Sharings begin
	 * 融通開始日時を取得.
	 * 電流コンペンセイションが終了したタイミング.
	 * 融通電力の積算計算はここから始まる.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通開始日時を表す文字列
	 */
	public static String startDateTime(JsonObject deal) {
		return deal.getString("startDateTime");
	}
	/**
	 * Gets datetime of end of Power Sharing.
	 * The timing is when unit on non-voltage reference side is stopped.
	 * Or, the timing is when additional control for remaining Power Sharings is applied.
	 * Calculation of the total power of Power Sharing is carried out to this point.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime of end of Power Sharing
	 * 融通終了日時を取得.
	 * 電圧リファレンス側ではない側のユニットを停止制御したタイミング.
	 * もしくは残りの融通のため追加制御したタイミング.
	 * 融通電力の積算計算はここまで.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通終了日時を表す文字列
	 */
	public static String stopDateTime(JsonObject deal) {
		return deal.getString("stopDateTime");
	}
	/**
	 * Gets datetime when units on both sides have stopped completely.
	 * The timing is when stop control is applied to the unit on voltage reference side.
	 * Or, the timing is when additional control for remaining Power Sharings is applied.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime when units on both sides have stopped completely
	 * 両側ユニットが停止完了した日時を取得.
	 * 電圧リファレンス側ユニットを停止制御したタイミング.
	 * もしくは残りの融通のため追加制御したタイミング.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 両側ユニットが停止完了した日時を表す文字列
	 */
	public static String deactivateDateTime(JsonObject deal) {
		return deal.getString("deactivateDateTime");
	}
	/**
	 * Gets datetime when Power Sharing ends abnormally.
	 * Power Sharing does not have this datetime when it ends normally.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return string representation of datetime when Power Sharing ends abnormally 
	 * 融通を異常終了した日時を取得.
	 * 正常に終了した融通は持たない.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通を異常終了した日時を表す文字列
	 */
	public static String abortDateTime(JsonObject deal) {
		return deal.getString("abortDateTime");
	}

	/**
	 * Gets information that reset Power Sharing. 
	 * Power Sharing that is not reset does not have this information.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return jsonarray object representation of information resetting Power Sharing.
	 *         The elements are {@link JsonObject} with the following attributes.
	 *         - dateTime : string representation of datetime of reset
	 *         - reason : reason for reset
	 * 融通をリセットした情報を取得.
	 * リセットが発生しなかった融通は持たない.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通をリセットした情報を表す jsonarray オブジェクト.
	 *         要素は以下の属性を持つ {@link JsonObject}.
	 *         - dateTime : リセット日時を表す文字列
	 *         - reason : 理由
	 */
	public static JsonArray resets(JsonObject deal) {
		return JsonObjectUtil.getJsonArray(deal, "reset");
	}
	/**
	 * Gets number of occurrences of resets.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return number of occurrences of resets
	 * リセット回数を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return リセット回数
	 */
	public static int numberOfResets(JsonObject deal) {
		JsonArray resets = resets(deal);
		return (resets != null) ? resets.size() : 0;
	}

	/**
	 * Gets information of abnormal termination of Power Sharing.
	 * Power Sharing that does not end abnormally does not have this information.
	 * Abnormal termination of Power Sharing itself occurs only once. However, it is possible that error processing may take place multiple times, so the information is held in {@link JsonArray}.
	 * The following information about abnormal termination is held.
	 * - abortDateTime : string representation of datetime of abnormal termination
	 * - abortReason : reason
	 * @param deal jsonobject object of target Power Sharing information
	 * @return jsonarray object representation of information about abnormal termination of Power Sharing.
	 *         The elements are {@link JsonObject} with the following attributes.
	 *         - dateTime : string representation of datetime of abnormal termination
	 *         - reason : reason for abnormal termination
	 * 融通を異常終了した情報を取得.
	 * 異常終了しなかった融通は持たない.
	 * 融通そのものの異常終了は一度しか起きないがエラー処理の都合上複数回処理する可能性があるため {@link JsonArray} で保持する.
	 * これとは別に以下の異常終了情報を持つ.
	 * - abortDateTime : 異常終了日時を表す文字列
	 * - abortReason : 理由
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通を異常終了した情報を表す jsonarray オブジェクト.
	 *         要素は以下の属性を持つ {@link JsonObject}.
	 *         - dateTime : string representation of datetime of abnormal termination
	 *         - reason : 理由
	 */
	public static JsonArray aborts(JsonObject deal) {
		return JsonObjectUtil.getJsonArray(deal, "abort");
	}
	/**
	 * Gets number of occurrences of abnormal terminations.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return number of occurrences of abnormal terminations
	 * 異常終了回数を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 異常終了回数
	 */
	public static int numberOfAborts(JsonObject deal) {
		JsonArray aborts = aborts(deal);
		return (aborts != null) ? aborts.size() : 0;
	}

	/**
	 * Gets reason when there is request to stop Power Sharing due to error that occurred in unit participating in Power Sharing.
	 	 * @param deal jsonobject object of target Power Sharing information
	 * @return jsonarray object of reasons for requesting stopping Power Sharing
	 * 融通参加ユニットで起きたエラーにより融通停止要求があった場合の理由を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通停止要求の理由の jsonarray オブジェクト
	 */
	public static JsonArray needToStopReasons(JsonObject deal) {
		return JsonObjectUtil.getJsonArray(deal, "needToStopReasons");
	}

	/**
	 * Determines if the unit specified by {@code unitId} is the discharging side of the Power Sharing in question.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param unitId unit ID being inquired.
	 *        {@code null} permitted.
	 * @return {@code true} if discharging unit.
	 *         {@code false} if not discharging unit.
	 *         {@code false} if {@code unitId} is {@code null}.
	 * {@code unitId} で指定するユニットが当該融通の送電側か.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param unitId 問い合わせるユニット ID.
	 *        {@code null} 可.
	 * @return 送電ユニットなら {@code true}.
	 *         送電ユニットでなければ {@code false}.
	 *         {@code unitId} が {@code null} なら {@code false}.
	 */
	public static boolean isDischargeUnit(JsonObject deal, String unitId) {
		return (unitId != null && unitId.equals(dischargeUnitId(deal)));
	}
	/**
	 * Determines if the unit specified by {@code unitId} is the charging side of the Power Sharing in question.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param unitId unit ID being inquired.
	 *        {@code null} permitted.
	 * @return {@code true} if charging unit.
	 *         {@code false} if not charging unit.
	 *         {@code false} if {@code unitId} is {@code null}.
	 * {@code unitId} で指定するユニットが当該融通の受電側か.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param unitId 問い合わせるユニット ID.
	 *        {@code null} 可.
	 * @return 受電ユニットなら {@code true}.
	 *         受電ユニットでなければ {@code false}.
	 *         {@code unitId} が {@code null} なら {@code false}.
	 */
	public static boolean isChargeUnit(JsonObject deal, String unitId) {
		return (unitId != null && unitId.equals(chargeUnitId(deal)));
	}
	/**
	 * Determines if unit specified by {@code unitId} is participating in the Power Sharing in question.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param unitId unit ID being inquired.
	 *        {@code null} permitted.
	 * @return {@code true} if the unit is discharing unit or charging unit.
	 *         {@code false} if either is not the case.
	 *         {@code false} if {@code unitId} is {@code null}.
	 * {@code unitId} で指定するユニットが当該融通に参加しているか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param unitId 問い合わせるユニット ID.
	 *        {@code null} 可.
	 * @return 送電ユニットまたは受電ユニットなら {@code true}.
	 *         いずれでもなければ {@code false}.
	 *         {@code unitId} が {@code null} なら {@code false}.
	 */
	public static boolean isInvolved(JsonObject deal, String unitId) {
		return (unitId != null && (unitId.equals(dischargeUnitId(deal)) || unitId.equals(chargeUnitId(deal))));
	}
	/**
	 * Determines whether the unit specified by {@code unitId} is discharging or charging.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param unitId unit ID being inquired.
	 *        {@code null} permitted.
	 * @return {@link Direction#DISCHARGE} if discharging unit.
	 *         {@link Direction#CHARGE} if charging unit.
	 *         {@code null} if neither.
	 *         {@code null} if {@code unitId} is {@code null}.
	 * {@code unitId} で指定するユニットが送電受電どちら側か.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param unitId 問い合わせるユニット ID.
	 *        {@code null} 可.
	 * @return 送電ユニットなら {@link Direction#DISCHARGE}.
	 *         受電ユニットなら {@link Direction#CHARGE}.
	 *         いずれでもなければ {@code null}.
	 *         {@code unitId} が {@code null} なら {@code null}.
	 */
	public static Direction direction(JsonObject deal, String unitId) {
		return (isDischargeUnit(deal, unitId)) ? Direction.DISCHARGE : (isChargeUnit(deal, unitId)) ? Direction.CHARGE : null;
	}

	/**
	 * Gets unit ID of voltage reference side.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param masterSidePolicy POLICY information specifying voltage reference side.
	 *                         Basic settings are given in POLICY.gridMaster.voltageReferenceSide.
	 *                         Depending on settings of high-capacity units, the voltage reference side may be dynamically reversed during Power Sharing process.
	 * @return unit ID on discharging side if {@code masterSidePolicy} is {@code "dischargeUnit"}.
	 *         If not, unit ID on charging side.
	 * 電圧リファレンス側のユニット ID を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param masterSidePolicy 電圧リファレンス側を指定する POLICY 情報.
	 *                         基本は POLICY.gridMaster.voltageReferenceSide.
	 *                         大容量ユニットの設定によっては融通処理中に動的に反転することもある.
	 * @return {@code masterSidePolicy} が {@code "dischargeUnit"} なら送電側ユニット ID.
	 *         そうでなければ受電側ユニット ID.
	 */
	public static String masterSideUnitId(JsonObject deal, String masterSidePolicy) {
		return ("dischargeUnit".equals(masterSidePolicy)) ? dischargeUnitId(deal) : chargeUnitId(deal);
	}
	/**
	 * Gets unit ID on non-voltage reference side.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param masterSidePolicy POLICY information specifying voltage reference side.
	 *                         Basic settings are given in POLICY.gridMaster.voltageReferenceSide.
	 *                         Depending on settings of high-capacity units, the voltage reference side may be dynamically reversed during Power Sharing process.
	 * @return Unit ID on charging side if {@code masterSidePolicy} is {@code "dischargeUnit"}.
	 *         If not, unit ID on discharging side.
	 * 電圧リファレンス側でない側のユニット ID を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param masterSidePolicy 電圧リファレンス側を指定する POLICY 情報.
	 *                         基本は POLICY.gridMaster.voltageReferenceSide.
	 *                         大容量ユニットの設定によっては融通処理中に動的に反転することもある.
	 * @return {@code masterSidePolicy} が {@code "dischargeUnit"} なら受電側ユニット ID.
	 *         そうでなければ送電側ユニット ID.
	 */
	public static String slaveSideUnitId(JsonObject deal, String masterSidePolicy) {
		return ("dischargeUnit".equals(masterSidePolicy)) ? chargeUnitId(deal) : dischargeUnitId(deal);
	}

	/**
	 * Determines if unit specified by {@code unitId} is unit on voltage reference side.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param unitId unit ID being inquired.
	 *        {@code null} permitted.
	 * @param masterSidePolicy POLICY information specifying voltage reference side.
	 *                         Basic settings are given in POLICY.gridMaster.voltageReferenceSide.
	 *                         Depending on settings of high-capacity units, the voltage reference side may be dynamically reversed during Power Sharing process.
	 * @return {@code true} if voltage reference side.
	 *         If not, {@code false}.
	 *         {@code false} if {@code unitId} is {@code null}.
	 * {@code unitId} で指定するユニットが電圧リファレンス側のユニットか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param unitId 問い合わせるユニット ID.
	 *        {@code null} 可.
	 * @param masterSidePolicy 電圧リファレンス側を指定する POLICY 情報.
	 *                         基本は POLICY.gridMaster.voltageReferenceSide.
	 *                         大容量ユニットの設定によっては融通処理中に動的に反転することもある.
	 * @return 電圧リファレンス側なら {@code true}.
	 *         そうでなければ {@code false}.
	 *         {@code unitId} が {@code null} なら {@code false}.
	 */
	public static boolean isMasterSideUnit(JsonObject deal, String unitId, String masterSidePolicy) {
		return (unitId != null && unitId.equals(masterSideUnitId(deal, masterSidePolicy)));
	}
	/**
	 * unit specified by {@code unitId}がnon-voltage reference sideのユニットか.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param unitId unit ID being inquired.
	 *        {@code null} permitted.
	 * @param masterSidePolicy POLICY information specifying voltage reference side.
	 *                         Basic settings are given in POLICY.gridMaster.voltageReferenceSide.
	 *                         Depending on settings of high-capacity units, the voltage reference side may be dynamically reversed during Power Sharing process.
	 * @return {@code true} if non-voltage reference side.
	 *         If not, {@code false}.
	 *         {@code false} if {@code unitId} is {@code null}.
	 * {@code unitId} で指定するユニットが電圧リファレンス側でない側のユニットか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param unitId 問い合わせるユニット ID.
	 *        {@code null} 可.
	 * @param masterSidePolicy 電圧リファレンス側を指定する POLICY 情報.
	 *                         基本は POLICY.gridMaster.voltageReferenceSide.
	 *                         大容量ユニットの設定によっては融通処理中に動的に反転することもある.
	 * @return 電圧リファレンス側でない側なら {@code true}.
	 *         そうでなければ {@code false}.
	 *         {@code unitId} が {@code null} なら {@code false}.
	 */
	public static boolean isSlaveSideUnit(JsonObject deal, String unitId, String masterSidePolicy) {
		return (unitId != null && unitId.equals(slaveSideUnitId(deal, masterSidePolicy)));
	}

	/**
	 * Gets grid current after current compensation of value unit specified by {@code unitId} ends.
	 * @param deal jsonobject object of target Power Sharing information
	 * @param unitId unit ID being inquired.
	 *        {@code null} permitted.
	 * @return grid current after current compensation of value unit specified by {@code unitId} ends.
	 *         {@code null} if {@code unitId} is not participating.
	 *         {@code null} if {@code unitId} is {@code null}.
	 * {@code unitId} で指定するユニットの電流コンペンセイション終了後のグリッド電流値を取得.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @param unitId 問い合わせるユニット ID.
	 *        {@code null} 可.
	 * @return {@code unitId} で指定するユニットの電流コンペンセイション終了後のグリッド電流値.
	 *         {@code unitId} が参加していなければ {@code null}.
	 *         {@code unitId} が {@code null} なら {@code null}.
	 */
	public static Float compensatedGridCurrentA(JsonObject deal, String unitId) {
		if (isDischargeUnit(deal, unitId)) return dischargeUnitCompensatedGridCurrentA(deal);
		if (isChargeUnit(deal, unitId)) return chargeUnitCompensatedGridCurrentA(deal);
		return null;
	}

	/**
	 * Determines if the Power Sharing in question has completed startup.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if startup is completed.
	 * 当該融通が起動済みか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 起動済みなら {@code true}.
	 */
	public static boolean isActivated(JsonObject deal) {
		return (activateDateTime(deal) != null);
	}
	/**
	 * Determines if voltage ramp-up of the Power Sharing in question has completed.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if voltage ramp-up has completed.
	 * 当該融通が電圧ランプアップ済みか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 電圧ランプアップ済みなら {@code true}.
	 */
	public static boolean isRampedUp(JsonObject deal) {
		return (rampUpDateTime(deal) != null);
	}
	/**
	 * Determines if units on both sides of the Power Sharing in question have completed startup.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if units on both sides have completed startup.
	 * 当該融通が両側ユニット起動済みか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 両側ユニット起動済みなら {@code true}.
	 */
	public static boolean isWarmedUp(JsonObject deal) {
		return (warmUpDateTime(deal) != null);
	}
	/**
	 * Determines if the Power Sharing in question has started.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if the Power Sharing has started.
	 * 当該融通が融通開始済みか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通開始済みなら {@code true}.
	 */
	public static boolean isStarted(JsonObject deal) {
		return (startDateTime(deal) != null);
	}
	/**
	 * Determines if the Power Sharing in question has ended.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if the interchange has ended.
	 * 当該融通が融通終了済みか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通終了済みなら {@code true}.
	 */
	public static boolean isStopped(JsonObject deal) {
		return (stopDateTime(deal) != null);
	}
	/**
	 * Determines if units on both sides of the Power Sharing in question have stopped completely.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if units on both sides of the Power Sharing have stopped completely.
	 * 当該融通が両側ユニット停止済みか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 両側ユニット停止済みなら {@code true}.
	 */
	public static boolean isDeactivated(JsonObject deal) {
		return (deactivateDateTime(deal) != null);
	}
	/**
	 * Determines if the Power Sharing in question has completed abnormal termination.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if the abnormal termination has completed.
	 * 当該融通が異常終了済みか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 異常終了済みなら {@code true}.
	 */
	public static boolean isAborted(JsonObject deal) {
		return (abortDateTime(deal) != null);
	}

	/**
	 * Determines if the unit on voltage reference side of the Power Sharing in question is in operation.
	 * The Power Sharing has finished starting up; the units on both sides have not yet stopped completely.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if the unit on voltage reference side is in operation.
	 * 当該融通の電圧リファレンス側ユニットが稼働中か.
	 * 起動済みかつ両側ユニット停止済みではない.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 電圧リファレンス側ユニットが稼働中なら {@code true}.
	 */
	public static boolean masterSideUnitMustBeActive(JsonObject deal) {
		return (isActivated(deal) && !isDeactivated(deal));
	}
	/**
	 * Determines if the unit on the non-voltage reference side of the Power Sharing in question is in operation.
	 * Units on both sides have completed starting; the Power Sharing has not yet been completed.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if the unit on the non-voltage reference side is in operation.
	 * 当該融通の電圧リファレンス側でない側ユニットが稼働中か.
	 * 両側ユニット起動済みかつ融通終了済みではない.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 電圧リファレンス側でない側ユニットが稼働中なら {@code true}.
	 */
	public static boolean slaveSideUnitMustBeActive(JsonObject deal) {
		return (isWarmedUp(deal) && !isStopped(deal));
	}

	/**
	 * Determines if units on both sides of the Power Sharing in question are in operation.
	 * Assumed to be the same as {@link #slaveSideUnitMustBeActive(JsonObject) unit on non-voltage reference side of the Power Sharing in question is in operation}.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if units on both sides are in operation.
	 * 当該融通の両側ユニットが稼働中か.
	 * {@link #slaveSideUnitMustBeActive(JsonObject) 当該融通の電圧リファレンス側でない側ユニットが稼働中か} と同じとする.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 両側ユニットが稼働中なら {@code true}.
	 */
	public static boolean bothSideUnitsMustBeActive(JsonObject deal) {
		return slaveSideUnitMustBeActive(deal);
	}
	/**
	 * Determines if the units on both sides of the Power Sharing in question are inactive.
	 * Assumed to be reversal of {@link #masterSideUnitMustBeActive(JsonObject) is unit on voltage reference side of the Power Sharing in question in operation}.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if the units on both sides are stopped.
	 * 当該融通の両側ユニットが停止中か.
	 * {@link #masterSideUnitMustBeActive(JsonObject) 当該融通の電圧リファレンス側ユニットが稼働中か} の反転とする.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 両側ユニットが停止中なら {@code true}.
	 */
	public static boolean bothSideUnitsMustBeInactive(JsonObject deal) {
		return !masterSideUnitMustBeActive(deal);
	}
	/**
	 * Determines if the control state of the Power Interhchange in question is in transitional state.
	 * Assumed to be condition that is neither {@link #bothSideUnitsMustBeActive(JsonObject) units on both sides of the Power Sharing in question are in operation}, nor {@link #bothSideUnitsMustBeInactive(JsonObject) units on both sides of the Power Sharing in question are stopped}.
	 * @param deal jsonobject object of target Power Sharing information    
	 * @return {@code true} if units on both sides are in the transitional period. 
	 * 当該融通の制御状態が過渡期か.
	 * {@link #bothSideUnitsMustBeActive(JsonObject) 当該融通の両側ユニットが稼働中} ではなく {@link #bothSideUnitsMustBeInactive(JsonObject) 当該融通の両側ユニットが停止中} でもない状態とする.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 両側ユニットが過渡期なら {@code true}.
	 */
	public static boolean isTransitionalState(JsonObject deal) {
		return (!bothSideUnitsMustBeActive(deal) && !bothSideUnitsMustBeInactive(deal));
	}

	/**
	 * Determines if there is request to stop the Power Sharing due to error that occurred in unit participating in the Power Sharing.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return {@code true} if there is a request to stop the Power Sharing.
	 * 融通参加ユニットで起きたエラーにより融通停止要求があるか.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 融通停止要求があるなら {@code true}.
	 */
	public static boolean isNeedToStop(JsonObject deal) {
		return (needToStopReasons(deal) != null);
	}

	/**
	 * Determines if the Power Sharing in question is Master Deal.
	 * Master Deal is Power Sharing (one of them) that includes the voltage reference unit. It is a critical element for controllng Power Sharings.
	 * → First, the Power Sharing that begins first is made to be the Master Deal. 
	 * → The voltage reference is set on voltageReferenceSide of Master Deal.
	 * → After the Master Deal ends, the next Master Deal is determined from the remaining Power Sharings.
	 * → The voltage reference moves together with the Master Deal.
	 * @param deal jsonobject object of target Power Sharing information
	 * @return {@code true} if Master Deal.
	 * 当該融通が Master Deal であるか否か.
	 * Master Deal とはvoltage reference unitが含まれる融通 ( のうちの一つ ) であり融通制御に重要な要素.
	 * → まず最初は一番に始めた融通を Master Deal とする.
	 * → 電圧リファレンスは Master Deal の voltageReferenceSide に立てる.
	 * → Master Deal が終わるときには残る融通の中から次の Master Deal を決める.
	 * → 電圧リファレンスは Master Deal につられて移動する.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return Master Deal なら {@code true}.
	 */
	public static boolean isMaster(JsonObject deal) {
		return deal.getBoolean("isMaster", Boolean.FALSE);
	}

	/**
	 * Determines if the Power Sharing in question is a Power Sharing that should be registered.
	 * Of the Power Sharings that are formed, there are those that do follow the starting conditions. They are not executed and are discarded.
	 * It is not necessary to include those Power Sharings when reporting to Service Center or saving to the file system.
	 * Only Power Sharings to which startup control has been applied at least once should be recorded.	 
	 * @param deal jsonobject object of target Power Sharing information
	 * @return {@code true} if the Power Sharing should be recorded.
	 * 当該融通が記録しておくべき融通であるか否か.
	 * 成立した融通の中には開始条件にそぐわず実行されずに捨てられるものもある.
	 * Service Center への通知やファイルシステムへの保存の際にはそれらの融通を含める必要はないと考える.
	 * 一度でも起動制御された融通のみ記録しておくべきとする.
	 * @param deal 対象の融通情報 jsonobject オブジェクト
	 * @return 記録するべき融通なら {@code true}.
	 */
	public static boolean isSaveworthy(JsonObject deal) {
		if (activateDateTime(deal) != null && !NULL_DATE_TIME_VALUE.equals(activateDateTime(deal))) return true;
		if (deal.getValue("reset") != null) return true;
		return false;
	}

}

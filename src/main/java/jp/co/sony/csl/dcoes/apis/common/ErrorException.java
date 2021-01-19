package jp.co.sony.csl.dcoes.apis.common;

/**
 * Exceptions to handle errors occurring in the tools of APIS program.
 * Flow of program becomes incomprehensible if an error occurs in tools.
 * Treats exceptions as dedicated exceptions and returns them to be handled by the caller.
 * Used from 
 * {@code jp.co.sony.csl.dcoes.apis.main.util.ErrorExceptionUtil#log(Error.Category, Error.Extent, Error.Level, String)}
 * and
 * {@code jp.co.sony.csl.dcoes.apis.main.util.ErrorExceptionUtil#logAndFail(Error.Category, Error.Extent, Error.Level, Throwable, Handler)}
 * specifically.
 * @author OES Project
 * APIS プログラムのツール中で発生するエラーを扱うための例外.
 * ツールでエラーを発しても流れがわからなくなる.
 * 専用の例外にして戻し呼び出し元で処理する.
 * 具体的には
 * {@code jp.co.sony.csl.dcoes.apis.main.util.ErrorExceptionUtil#log(Error.Category, Error.Extent, Error.Level, String)}
 * や
 * {@code jp.co.sony.csl.dcoes.apis.main.util.ErrorExceptionUtil#logAndFail(Error.Category, Error.Extent, Error.Level, Throwable, Handler)}
 * から使われる.
 * @author OES Project
 */
// This may be FailureException ...
// こっちは FailureException かな ...
public class ErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	public final String unitId;
	public final Error.Category category;
	public final Error.Extent extent;
	public final Error.Level level;

	/**
	 * Creates instance.
	 * @param unitId ID of unit that generated error	 
	 * @param category error category object
	 * @param extent error extent object
	 * @param level error level object
	 * @param message error message
	 * インスタンス作成.
	 * @param unitId エラー生成ユニット ID
	 * @param category エラーの category オブジェクト
	 * @param extent エラーの extent オブジェクト
	 * @param level エラーの level オブジェクト
	 * @param message エラーメッセージ
	 */
	private ErrorException(String unitId, Error.Category category, Error.Extent extent, Error.Level level, String message) {
		super(message, null, true, false);
		this.unitId = unitId;
		this.category = category;
		this.extent = extent;
		this.level = level;
	}

	/**
	 * Creates instance.
	 * @param unitId ID of unit that generated error	 
	 * @param category error category object
	 * @param extent error extent object
	 * @param level error level object
	 * @param message error message
	 * @return errorexception object
	 * インスタンス作成.
	 * @param unitId エラー生成ユニット ID
	 * @param category エラーの category オブジェクト
	 * @param extent エラーの extent オブジェクト
	 * @param level エラーの level オブジェクト
	 * @param message エラーメッセージ
	 * @return errorexception オブジェクト
	 */
	public static ErrorException create(String unitId, Error.Category category, Error.Extent extent, Error.Level level, String message) {
		return new ErrorException(unitId, category, extent, level, message);
	}

}

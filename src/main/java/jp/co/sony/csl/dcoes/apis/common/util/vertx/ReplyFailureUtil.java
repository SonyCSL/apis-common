package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;

/**
 * This tool determines the type of EventBus send failure.
 * @author OES Project
 * EventBus の send 失敗の種類を判定するツール.
 * @author OES Project
 */
public class ReplyFailureUtil {

	private ReplyFailureUtil() { }

	/**
	 * Gets the type of failure if EventBus send fails.
	 * @param reply asyncresult object of send result
	 * @return failure type.
	 *         {@code null} if not failure.
	 *         {@code null} if {@link AsyncResult#cause()} is not {@link ReplyException}.
	 * EventBus の send 失敗の場合にその失敗の種類を取得する.
	 * @param reply send 結果の asyncresult オブジェクト
	 * @return 失敗の種類.
	 *         失敗でない場合は {@code null}.
	 *         {@link AsyncResult#cause()} が {@link ReplyException} ではない場合も {@code null}.
	 */
	public static ReplyFailure replyFailure(AsyncResult<?> reply) {
		if (reply != null && reply.failed()) {
			return replyFailure(reply.cause());
		}
		return null;
	}
	/**
	 * Gets the failure type if exception is EventBus send failure. 
	 * @param cause exception
	 * @return failure type.
	 *         {@code null} if {@code cause} is not {@link ReplyException}.
	 * 例外が EventBus の send 失敗の場合にその失敗の種類を取得する.
	 * @param cause 例外
	 * @return 失敗の種類.
	 *         {@code cause} が {@link ReplyException} ではない場合は {@code null}.
	 */
	public static ReplyFailure replyFailure(Throwable cause) {
		if (cause instanceof ReplyException) {
			return ((ReplyException) cause).failureType();
		}
		return null;
	}

	/**
	 * Determines whether or not the reason for EventBus send failure is EventBus timeout.
	 * @param reply asyncresult object of send result 
	 * @return true if timeout
	 * EventBus の send 失敗の理由が EventBus タイムアウトか否か.
	 * @param reply send 結果の asyncresult オブジェクト
	 * @return タイムアウトなら true
	 */
	public static boolean isTimeout(AsyncResult<?> reply) {
		return ReplyFailure.TIMEOUT.equals(replyFailure(reply));
	}
	/**
	 * Determines whether or not the reason for EventBus send failure is absence of recipient.
	 * @param reply asyncresult object of send result 
	 * @return true if recipient is absent
	 * EventBus の send 失敗の理由が受信者不在か否か.
	 * @param reply send 結果の asyncresult オブジェクト
	 * @return 受信者不在なら true
	 */
	public static boolean isNoHandlers(AsyncResult<?> reply) {
		return ReplyFailure.NO_HANDLERS.equals(replyFailure(reply));
	}
	/**
	 * Determines whether or not the reason for EventBus send failure is receiving side error.
	 * @param reply asyncresult object of send result 
	 * @return true if receiving side error
	 * EventBus の send 失敗の理由が受信側エラーか否か.
	 * @param reply send 結果の asyncresult オブジェクト
	 * @return 受信側エラーなら true
	 */
	public static boolean isRecipientFailure(AsyncResult<?> reply) {
		return ReplyFailure.RECIPIENT_FAILURE.equals(replyFailure(reply));
	}

	/**
	 * If exception is EventBus send failure, determines whether or not the reason for send failure is timeout.
	 * @param cause exception
	 * @return true if timeout
	 * 例外が EventBus の send 失敗の場合にその失敗の理由がタイムアウトか否か.
	 * @param cause 例外
	 * @return タイムアウトなら true
	 */
	public static boolean isTimeout(Throwable cause) {
		return ReplyFailure.TIMEOUT.equals(replyFailure(cause));
	}
	/**
	 * If exception is EventBus send failure, determines whether or not the reason for send failure is absence of recipient. 
	 * @param cause exception
	 * @return true if recipient is absent
	 * 例外が EventBus の send 失敗の場合にその失敗の理由が受信者不在か否か.
	 * @param cause 例外
	 * @return 受信者不在なら true
	 */
	public static boolean isNoHandlers(Throwable cause) {
		return ReplyFailure.NO_HANDLERS.equals(replyFailure(cause));
	}
	/**
	 * If exception is EventBus send failure, determines whether or not the reason for send failure is receiving side error. 
	 * @param cause exception
	 * @return true if receiving side error
	 * 例外が EventBus の send 失敗の場合にその失敗の理由が受信側エラーか否か.
	 * @param cause 例外
	 * @return 受信側エラーなら true
	 */
	public static boolean isRecipientFailure(Throwable cause) {
		return ReplyFailure.RECIPIENT_FAILURE.equals(replyFailure(cause));
	}

}

package jp.co.sony.csl.dcoes.apis.common.util.vertx;

import io.vertx.core.logging.VertxLoggerFormatter;

import java.util.logging.LogRecord;

/**
 * Sets dedicated log format for APIS.
 * The program identification string is output.
 * Parsed by {@link jp.co.sony.csl.dcoes.apis.tools.log.util.ApisVertxLogParser}.
 * @author OES Project
 * APIS 専用のログフォーマット.
 * プログラム識別文字列が出力される.
 * {@link jp.co.sony.csl.dcoes.apis.tools.log.util.ApisVertxLogParser} でパースされる.
 * @author OES Project
 */
public class ApisLoggerFormatter extends VertxLoggerFormatter {

	private String PROGRAM_ID_ = null;

	/**
	 * Adds {@link #programId() program identification string} to parent class {@link VertxLoggerFormatter#format(LogRecord) result} and outputs.
	 * @param record {@inheritDoc}
	 * @return {@inheritDoc}
	 * 親クラスの {@link VertxLoggerFormatter#format(LogRecord) 結果} に {@link #programId() プログラム識別文字列} を追加して出力する.
	 * @param record {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override public String format(final LogRecord record) {
		String result = super.format(record);
		return "[[[" + programId() + "]]] " + result;
	}

	/**
	 * Returns program identification string, which is output to a log.
	 * Returns emptry string if {@code null}.
	 * @return program identification string
	 * ログに出力するプログラム識別文字列を返す.
	 * {@code null} なら空文字列を返す.
	 * @return プログラム識別文字列
	 */
	protected String programId() {
		if (PROGRAM_ID_ == null) PROGRAM_ID_ = VertxConfig.programId();
		return (PROGRAM_ID_ != null) ? PROGRAM_ID_ : "";
	}

}

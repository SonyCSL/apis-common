package jp.co.sony.csl.dcoes.apis.common.util;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Log handler.
 * Subclass in name only to output to two groups of files ( *.log and *.err ).
 * (Wish if there's another way to implement this).
 * @author OES Project
 * ログのハンドラ.
 * ファイルへの出力を 2 系統にする ( *.log および *.err ) ための名ばかりのサブクラス.
 * ( 他に実現方法があるならそっちが良い ).
 * @author OES Project
 */
public class AnotherFileHandler extends FileHandler {

	/**
	 * Creates instance.
	 * @throws SecurityException {@inheritDoc}
	 * @throws IOException {@inheritDoc}
	 * インスタンス作成.
	 * @throws SecurityException {@inheritDoc}
	 * @throws IOException {@inheritDoc}
	 */
	public AnotherFileHandler() throws SecurityException, IOException {
		super();
	}

}

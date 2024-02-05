package ccw.serviceinnovation.node.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * 静默关闭工具类。
 *
 * @author 陈翔
 */
public class Closeables {

  public static void closeQuietly(Closeable closeable) {
    if (closeable == null) {
      return;
    }
    try {
      closeable.close();
    } catch (IOException ignored) {
      ignored.printStackTrace();
    }
  }
}

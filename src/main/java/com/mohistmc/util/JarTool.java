package com.mohistmc.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Author Mgazul
 * @create 2019/10/12 20:26
 */
public class JarTool {

    public static String getJarPath() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    public static File getJarDir() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getParentFile();
    }

	public static String getJarDirStr() {
		File file = getFile();
		if (file == null) {
			return null;
		}
		return getFile().getParent();
	}

    public static String getJarName() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getName();
    }

    private static File getFile() {
        String path = JarTool.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return new File(path);
        }
		return new File(path);
    }

    public static void inputStreamFile(InputStream inputStream, String targetFilePath) {
        File file = new File(targetFilePath);
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            os.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

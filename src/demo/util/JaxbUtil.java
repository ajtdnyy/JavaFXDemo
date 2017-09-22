/*
 * Copyright 2017 开发辅助.
 *  个人博客 http://www.vbox.top/
 */
package demo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author 开发辅助
 */
public class JaxbUtil {

    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String DIR = System.getProperty("user.dir") + "/conf";
    public static final String SETTING_FILE = DIR + "/setting.xml";
    public static final String SETTING_HTML = DIR + "/setting.html";
    public static final String BOOKMARK_FILE = DIR + "/bookmark.xml";

    /**
     * JavaBean转换成xml 默认编码UTF-8
     *
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        return convertToXml(obj, CHARSET_UTF8);
    }

    /**
     * 将对象转成xml并存储到xml
     *
     * @param ss
     * @param file
     */
    public static void saveToXML(Object ss, String file) {
        try {
            String content = convertToXml(ss, CHARSET_UTF8);
            saveFile(content, file);
        } catch (Exception ex) {
            Logger.getLogger(JaxbUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 保存文件
     *
     * @param content 文件内容
     * @param file 文件路径
     */
    public static void saveFile(String content, String file) {
        try {
            new File(DIR).mkdirs();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(content);
                fw.flush();
            }
        } catch (Exception ex) {
            Logger.getLogger(JaxbUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 从输入流读取文件内容
     *
     * @param in
     * @return
     */
    public static String readFile(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String tmp = reader.readLine();
            while (tmp != null) {
                sb.append(tmp);
                tmp = reader.readLine();
            }
            return sb.toString();
        } catch (IOException ex) {
            Logger.getLogger(JaxbUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * 从文件中读取内容
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String tmp = reader.readLine();
            while (tmp != null) {
                sb.append(tmp);
                tmp = reader.readLine();
            }
            return sb.toString();
        } catch (IOException ex) {
            Logger.getLogger(JaxbUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * JavaBean转换成xml
     *
     * @param obj
     * @param encoding
     * @return
     */
    public static String convertToXml(Object obj, String encoding) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            Logger.getLogger(JaxbUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    /**
     * file转换成JavaBean
     *
     * @param <T>
     * @param file
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String file, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new File(file));
        } catch (Exception e) {
            Logger.getLogger(JaxbUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return t;
    }
}

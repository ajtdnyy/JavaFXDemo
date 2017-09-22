/*
 * Copyright 2017 lancw.
 *  个人博客 http://www.vbox.top/
 */
package demo.model;

import java.net.URLDecoder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author lancw
 * @since 2017-9-21 22:59:33
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "demo.model.Setting")
@XmlRootElement(name = "Setting")
public class Setting {

    private String home;
    private String initType;
    private String initURLS;
    private String searchEngines;
    private Boolean showBookmark = false;

    public Setting() {
    }

    public void init(String data) {
        String[] tmp = data.split("&");
        try {
            for (String s : tmp) {
                String[] temp = s.split("=");
                if (temp.length == 2) {
                    switch (temp[0]) {
                        case "home":
                            home = URLDecoder.decode(temp[1], "UTF8");
                            break;
                        case "initType":
                            initType = temp[1];
                            break;
                        case "initURLS":
                            initURLS = URLDecoder.decode(temp[1], "UTF8");
                            break;
                        case "searchEngines":
                            searchEngines = URLDecoder.decode(temp[1], "UTF8");
                            break;
                        case "showBookmark":
                            showBookmark = Boolean.valueOf(temp[1]);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String tmp = "home=" + home + "&initType=" + initType + "&initURLS=" + initURLS;
        tmp += "&searchEngines=" + searchEngines + "&showBookmark=" + showBookmark;
        return tmp;
    }

    public String[] getURLS() {
        return initURLS == null ? null : initURLS.split(",");
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getInitType() {
        return initType;
    }

    public void setInitType(String initType) {
        this.initType = initType;
    }

    public String getInitURLS() {
        return initURLS;
    }

    public void setInitURLS(String initURLS) {
        this.initURLS = initURLS;
    }

    public String getSearchEngines() {
        switch (searchEngines) {
            case "baidu":
                return "https://www.baidu.com/s?wd=";
            case "google":
                return "https://www.google.com/search?q=";
            case "bing":
                return "http://cn.bing.com/search?q=";
            case "yahoo":
                return "https://sg.search.yahoo.com/search?p=";
            case "sogou":
                return "https://www.sogou.com/web?query=";
            case "so":
                return "https://www.so.com/s?q=";
        }
        return searchEngines;
    }

    public void setSearchEngines(String searchEngines) {
        this.searchEngines = searchEngines;
    }

    public Boolean getShowBookmark() {
        return showBookmark;
    }

    public void setShowBookmark(Boolean showBookmark) {
        this.showBookmark = showBookmark;
    }
}

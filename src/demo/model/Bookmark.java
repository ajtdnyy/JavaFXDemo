/*
 * Copyright 2017 开发辅助.
 *  个人博客 http://www.vbox.top/
 */
package demo.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author 开发辅助
 * @since 2017-9-20 23:38:56
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "demo.model.Bookmark")
@XmlRootElement(name = "Bookmark")
public class Bookmark {

    private List<BookmarkModel> bookmarks = new ArrayList<>();

    public List<BookmarkModel> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<BookmarkModel> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public void addBookmark(String name, String url) {
        bookmarks.add(new BookmarkModel(name, url));
    }

    public static class BookmarkModel {

        String name;
        String url;

        public BookmarkModel() {
        }

        public BookmarkModel(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

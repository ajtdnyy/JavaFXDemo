/*
 * Copyright 2017 开发辅助.
 *  个人博客 http://www.vbox.top/
 */
package demo.controller;

import demo.model.Bookmark;
import demo.model.Setting;
import demo.node.MyTab;
import demo.util.JaxbUtil;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author 开发辅助
 */
public class DemoController implements Initializable {

    @FXML
    private TabPane tabPane;
    @FXML
    private Button addTabBtn;
    @FXML
    private TextField bookmarkName;
    @FXML
    private TextField bookmarkAddress;
    @FXML
    private AnchorPane bookmarkPane;
    @FXML
    private CheckMenuItem bookmarkMenu;
    @FXML
    private HBox bookmarkBox;
    public static Setting userSettiing;
    private static ObservableList<Node> bookmarks;
    private final Double tabHeaderWidth = 200D;

    @FXML
    private void closeAction(ActionEvent event) {//函数名必需与fxml中的一致
        System.exit(0);
    }

    @FXML
    private void showSetting() {
        MyTab mt = new MyTab("", this);
        if (userSettiing == null) {
            mt.load("file:///" + JaxbUtil.SETTING_HTML.replaceAll("\\\\", "/"));
        } else {
            System.out.println(userSettiing.toString());
            mt.load("file:///" + JaxbUtil.SETTING_HTML.replaceAll("\\\\", "/") + "?" + userSettiing.toString());
        }
        mt.setType(MyTab.Type.SETTING);
        mt.hideAddressBox();
        tabPane.getTabs().add(mt);
        tabPane.getSelectionModel().select(mt);
    }

    /**
     * 显示书签栏
     *
     * @param event
     */
    @FXML
    private void showBookmark(ActionEvent event) {//函数名必需与fxml中的一致
        tabPane.getTabs().forEach((t) -> {
            MyTab mt = (MyTab) t;
            mt.changeBookmarkBarVisible(bookmarkMenu.isSelected());
        });
        bookmarkBox.setVisible(bookmarkMenu.isSelected());
    }

    /**
     * 保存书签
     */
    @FXML
    private void saveBookmark() {
        addBookmark(bookmarkName.getText(), bookmarkAddress.getText());
        hideBookmark();
        Bookmark bm = new Bookmark();
        bookmarks.forEach((t) -> {
            Label l = (Label) t;
            bm.addBookmark(l.getText(), l.getUserData().toString());
        });
        JaxbUtil.saveToXML(bm, JaxbUtil.BOOKMARK_FILE);//将书签存储到文件
    }

    /**
     * 隐藏书签管理窗口
     */
    @FXML
    public void hideBookmark() {
        bookmarkPane.setVisible(false);
    }

    /**
     * 显示书签管理窗口
     *
     * @param name
     * @param url
     * @param userData 编辑传需要修改的节点
     */
    public void showBookmarkPane(String name, String url, Object userData) {
        Double width = bookmarkPane.getScene().getWidth();
        AnchorPane.setLeftAnchor(bookmarkPane, (width - 344d) / 2);
        bookmarkAddress.setText(url);
        bookmarkName.setText(name);
        bookmarkPane.setVisible(true);
        bookmarkPane.setUserData(userData);
    }

    /**
     * 添加书签
     *
     * @param name
     * @param url
     */
    public void addBookmark(String name, String url) {
        Object data = bookmarkPane.getUserData();
        String tooltip = "右键点击修改\n左键单击新标签页打开书签\nCTRL+左键单击当前页打开书签\n" + name + "\n" + url;
        if (data != null) {//修改书签
            Label node = (Label) data;
            node.setText(name);
            node.setUserData(url);
            node.setTooltip(new Tooltip(tooltip));
        } else {//添加书签
            Label node = new Label(name);
            node.setMaxWidth(100);
            node.setUserData(url);
            node.setTooltip(new Tooltip(tooltip));
            node.setCursor(Cursor.HAND);
            node.setOnMouseClicked((event) -> {//书签点击事件处理
                String ul = node.getUserData().toString();
                if (MouseButton.SECONDARY.equals(event.getButton())) {
                    showBookmarkPane(node.getText(), ul, node);
                } else if (event.isControlDown()) {//CTRL+单击事件
                    MyTab mt = (MyTab) tabPane.getSelectionModel().getSelectedItem();
                    mt.load(ul);
                } else {
                    MyTab mt = new MyTab(ul, this);
                    tabPane.getTabs().add(mt);
                    tabPane.getSelectionModel().select(mt);
                }
            });
            bookmarks.add(node);
        }
    }

    /**
     * 移除书签
     *
     * @param node
     */
    public static void removeBookmark(Node node) {
        bookmarks.add(node);
    }

    public void initSetting(boolean openURLS) {
        if (userSettiing == null) {
            return;
        }
        MyTab.HOME_URL = userSettiing.getHome();
        MyTab.SEARCH = userSettiing.getSearchEngines();
        bookmarkMenu.setSelected(userSettiing.getShowBookmark());
        if (userSettiing.getShowBookmark()) {
            MyTab.currentWebViewTop = 54D;
        } else {
            MyTab.currentWebViewTop = 28D;
        }
        showBookmark(null);
        if (openURLS) {
            if ("newtab".equals(userSettiing.getInitType())) {
                tabPane.getTabs().add(new MyTab("", this));
            } else {
                for (String s1 : userSettiing.getURLS()) {
                    tabPane.getTabs().add(new MyTab(s1, this));
                }
            }
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File setting = new File(JaxbUtil.SETTING_HTML);
        if (!setting.exists()) {
            JaxbUtil.saveFile(JaxbUtil.readFile(getClass().getResourceAsStream("/resources/html/default.html")), JaxbUtil.SETTING_HTML);
        }
        userSettiing = JaxbUtil.converyToJavaBean(JaxbUtil.SETTING_FILE, Setting.class);
        if (userSettiing != null) {
            initSetting(true);
        } else {
            tabPane.getTabs().add(new MyTab(this));
        }
        bookmarks = bookmarkBox.getChildren();
        Bookmark bm = JaxbUtil.converyToJavaBean(JaxbUtil.BOOKMARK_FILE, Bookmark.class);
        if (bm != null) {
            bm.getBookmarks().forEach((t) -> {
                addBookmark(t.getName(), t.getUrl());
            });
        }
        bookmarkPane.setVisible(false);
        addTabBtn.setOnAction((event) -> {
            MyTab mt = new MyTab("", this);
            tabPane.getTabs().add(mt);
            tabPane.getSelectionModel().select(mt);
        });
        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> c) -> {
            int size = c.getList().size();
            if (size == 0) {
                Platform.exit();
            }
            Double width = tabPane.getScene().getWidth();
            Double left = tabHeaderWidth * size + (size - 1) * 12D + 20D;
            if (left - 80D > width) {
                left = width - 80D;
                tabPane.setTabMinWidth(5D);
            } else if (left < width - tabHeaderWidth) {
                tabPane.setTabMinWidth(tabHeaderWidth);
            }
            AnchorPane.setLeftAnchor(addTabBtn, left);
        });
        tabPane.setTabMaxWidth(tabHeaderWidth);
    }

}

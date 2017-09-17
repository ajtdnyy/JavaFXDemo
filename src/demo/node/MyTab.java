package demo.node;

import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

/*
 * Copyright 2017 lancw.
 *  个人博客 http://www.vbox.top/
 * @version 1.0
 * @author ajtdnyy
 * @since 2017-9-16 14:05:19
 */
public class MyTab extends Tab {

    private static final String BLANK = "_blank";
    private static final String TARGET = "target";
    private static final String CLICK = "click";
    private static final String DEFAULT_TEXT = "新标签";
    private static final String SEARCH = "https://www.baidu.com/s?wd=";
    public static String HOME_URL = "http://www.vbox.top";
    private static final String PROTOCOL = "^(http|ftp|https):\\/\\/\\S*";
    private static final String URL_REG_EXP = "((http|ftp|https):\\/\\/)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
    private final WebView webview;
    private final AnchorPane anchorPane;
    private final HBox addressBox;
    private final HBox bookmarkBox;
    private final WebHistory history;
    private final TextField input;
    private static Double currentWebViewTop = 28D;
    private static ObservableList<Node> bookmarks;

    public MyTab() {
        this(HOME_URL);
    }

    public MyTab(String loc) {
        addressBox = new HBox();
        bookmarkBox = new HBox();
        webview = new WebView();
        input = new TextField();
        //设置各组件在AnchorPane中的位置
        AnchorPane.setLeftAnchor(webview, 0D);
        AnchorPane.setRightAnchor(webview, 0D);
        AnchorPane.setBottomAnchor(webview, 0D);
        AnchorPane.setTopAnchor(addressBox, 0D);
        AnchorPane.setTopAnchor(bookmarkBox, 28D);
        AnchorPane.setTopAnchor(webview, currentWebViewTop);
        //为各个组件添加子组件及设置初始参数和事件监听
        initHBox(addressBox);
        initChild(addressBox);
        initHBox(bookmarkBox);
        initWebViewEvent();
        addressBox.getChildren().add(getTextField());

        input.setText(loc);//设置初始化地址
        analysisAddress();//分析地址
        //将三个组件添加到AnchorPane中，注意顺序webview放在bookmarkBox之前，因为隐藏书签拦就是用webview盖住bookmarkBox实现
        anchorPane = new AnchorPane(addressBox, webview, bookmarkBox);
        bookmarks = bookmarkBox.getChildren();
        history = webview.getEngine().getHistory();
        setContent(anchorPane);
        setText(DEFAULT_TEXT);
        setClosable(true);
    }

    public static void addBookmark(Node node) {
        bookmarks.add(node);
    }

    public static void removeBookmark(Node node) {
        bookmarks.add(node);
    }

    public void changeBookmarkBarVisible() {
        double top = AnchorPane.getTopAnchor(webview);
        currentWebViewTop = top == 28D ? 54D : 28D;
        AnchorPane.setTopAnchor(webview, currentWebViewTop);
    }

    private TextField getTextField() {
        input.setPromptText("输入网址或搜索内容");
        input.setPrefHeight(25D);
        input.setPrefWidth(600D);
        input.setOnKeyReleased((event) -> {
            if (KeyCode.ENTER.equals(event.getCode())) {//回车加载页面
                analysisAddress();
            }
        });
        input.setOnMouseClicked((event) -> {
            if (input.getText().length() == input.getCaretPosition()) {
                input.selectAll();
            }
        });
        return input;
    }

    private void analysisAddress() {
        String text = input.getText();
        Pattern p = Pattern.compile(URL_REG_EXP);
        if (p.matcher(text).matches()) {
            if (!Pattern.compile(PROTOCOL).matcher(text).matches()) {
                text = "http://" + text;
            }
            webview.getEngine().load(text);
        } else {
            webview.getEngine().load(SEARCH + text);
        }
    }

    private void initWebViewEvent() {
        webview.getEngine().locationProperty().addListener((ObservableValue<? extends String> ov, final String oldLoc, final String loc) -> {
            input.setText(loc);
        });
        webview.getEngine().titleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                setText(newValue);
            } else {
                setText(DEFAULT_TEXT);
            }
        });
        webview.getEngine().documentProperty().addListener((observable, ov, document) -> {
            if (document != null) {
                NodeList nodeList = document.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    org.w3c.dom.Node node = nodeList.item(i);
                    org.w3c.dom.Node targetNode = node.getAttributes().getNamedItem(TARGET);
                    if (targetNode != null) {
                        String target = targetNode.getTextContent();
                        if (BLANK.equals(target)) {//页面中target="_blank"的在新标签中打开
                            EventTarget eventTarget = (EventTarget) node;
                            eventTarget.addEventListener(CLICK, (Event evt) -> {
                                HTMLAnchorElement anchorElement = (HTMLAnchorElement) evt.getCurrentTarget();
                                String href = anchorElement.getHref();
                                MyTab mt = new MyTab(href);
                                getTabPane().getTabs().add(mt);
                                getTabPane().getSelectionModel().select(mt);
                                evt.preventDefault();
                            }, false);
                        }
                    }
                }
            }
        });
    }

    private void initChild(HBox hb) {
        hb.getChildren().add(getButton(Style.BACK));
        hb.getChildren().add(getButton(Style.FORWARD));
        hb.getChildren().add(getButton(Style.REFRESH));
        hb.getChildren().add(getButton(Style.HOME));
    }

    private Button getButton(Style style) {
        Button btn = new Button("");
        btn.setPrefWidth(25D);
        btn.getStyleClass().add("btn");
        btn.getStyleClass().add(style.style);
        btn.setOnAction((event) -> {
            Integer current = history.currentIndexProperty().getValue();
            switch (style) {
                case BACK:
                    if (current > 0) {
                        history.go(-1);
                    }
                    break;
                case FORWARD:
                    if (current < history.getEntries().size() - 1) {
                        history.go(1);
                    }
                    break;
                case REFRESH:
                    webview.getEngine().reload();
                    break;
                case HOME:
                    webview.getEngine().load(HOME_URL);
                    break;
            }
        });
        return btn;
    }

    private void initHBox(HBox hb) {
        hb.setSpacing(3D);
        hb.setPrefHeight(25D);
        hb.setPadding(new Insets(3D));
        AnchorPane.setLeftAnchor(hb, 0D);
        AnchorPane.setRightAnchor(hb, 0D);
    }

    private static enum Style {
        BACK("left_point"),
        FORWARD("right_point"),
        REFRESH("refresh_btn"),
        HOME("home_btn");
        final String style;

        private Style(String style) {
            this.style = style;
        }
    }

}

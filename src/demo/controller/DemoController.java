/*
 * Copyright 2017 lancw.
 *  个人博客 http://www.vbox.top/
 */
package demo.controller;

import demo.node.MyTab;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author 46135
 */
public class DemoController implements Initializable {

    @FXML
    private TabPane tabPane;

    @FXML
    private void closeAction(ActionEvent event) {//函数名必需与fxml中的一致
        System.exit(0);
    }

    @FXML
    private void showBookmark(ActionEvent event) {//函数名必需与fxml中的一致
        tabPane.getTabs().forEach((t) -> {
            MyTab mt = (MyTab) t;
            mt.changeBookmarkBarVisible();
        });
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabPane.getTabs().add(new MyTab());
        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> c) -> {
            if (c.getList().isEmpty()) {
                System.exit(0);
            }
        });
    }

}

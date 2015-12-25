/*
 *
 * Copyright 2015 magiclen.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.magiclen.playingcardfx.pages;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.magiclen.playingcardfx.playingcard.PlayingCard;

/**
 * 主頁面。
 *
 * @author Magic Len
 */
public class Main extends BorderPane {

    // -----類別常數-----
    private static final int GAP = 5;
    private static final int MIN_SIZE = 30;
    private static final int MAX_SIZE = 250;
    private static final int SCROLL_SIZE_DELTA = 5;

    // -----類別變數-----
    // -----物件變數-----
    private final Insets insets;
    private final ListView lvCards;
    private final BorderPane center;
    private final ScrollPane scrollPane;
    private final FlowPane cardPane;
    private final ObservableList<Node> cardList;
    private final FlowPane controlPane;
    private final ScrollBar sbSize;
    private final CheckBox cbBack;
    private final StackPane[] stackPanes = new StackPane[53];
    private final PlayingCard[] cards = new PlayingCard[53];
    private final ArrayList<String> v = new ArrayList<>();

    // -----建構子-----
    public Main() {
	insets = new Insets(GAP, GAP, GAP, GAP);

	// 初始化StackPane
	for (int i = 0; i < 53; ++i) {
	    stackPanes[i] = new StackPane();
	}

	// 初始化撲克牌
	cards[0] = new PlayingCard();
	for (int i = 0; i < 53; ++i) {
	    if (i > 0) {
		cards[i] = new PlayingCard((i - 1) / 13 + 1, (i - 1) % 13 + 1);
	    }
	    v.add(cards[i].toString());
	    FlowPane.setMargin(stackPanes[i], insets);
	    stackPanes[i].getChildren().add(cards[i]);
	    stackPanes[i].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
	    final PlayingCard card = cards[i];
	    card.setOnMouseClicked(e -> {
		card.setSelected(!card.isSelected());
		card.resetCanvas();
	    }
	    );
	}

	// GUI元件
	lvCards = new ListView(FXCollections.observableArrayList(v));
	lvCards.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
	lvCards.setPrefWidth(100);
	lvCards.getSelectionModel().select(0);
	
	cardPane = new FlowPane();
	cardPane.setAlignment(Pos.CENTER);
	cardList = cardPane.getChildren();
	cardList.add(stackPanes[0]);
	
	cbBack = new CheckBox("顯示背面");
	
	sbSize = new ScrollBar();
	sbSize.setOrientation(Orientation.HORIZONTAL);
	sbSize.setPrefSize(300, 25);
	sbSize.setMin(MIN_SIZE);
	sbSize.setMax(MAX_SIZE);
	sbSize.setValue(100);
	sbSize.setBlockIncrement(15);
	
	FlowPane.setMargin(cbBack, insets);
	FlowPane.setMargin(sbSize, insets);
	controlPane = new FlowPane();
	controlPane.setOrientation(Orientation.HORIZONTAL);
	controlPane.setAlignment(Pos.CENTER);
	final ObservableList<Node> children = controlPane.getChildren();
	children.addAll(cbBack, sbSize);
	
	scrollPane = new ScrollPane();
	scrollPane.setContent(cardPane);
	scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	scrollPane.setFitToHeight(true);
	scrollPane.setFitToWidth(true);
	scrollPane.setOnScroll(e -> {
	    final double value = sbSize.getValue();
	    double newValue = value + SCROLL_SIZE_DELTA * ((e.getDeltaY() > 0) ? -1 : 1);
	    if (newValue > MAX_SIZE) {
		newValue = MAX_SIZE;
	    } else if (newValue < MIN_SIZE) {
		newValue = MIN_SIZE;
	    }
	    sbSize.setValue(newValue);
	});
	
	BorderPane.setMargin(scrollPane, insets);
	BorderPane.setMargin(controlPane, insets);
	center = new BorderPane();
	center.setCenter(scrollPane);
	center.setBottom(controlPane);
	
	BorderPane.setMargin(lvCards, insets);
	BorderPane.setMargin(center, insets);
	setLeft(lvCards);
	setCenter(center);

	// 事件
	lvCards.getSelectionModel().selectedIndexProperty().addListener((e) -> {
	    final int index = lvCards.getSelectionModel().getSelectedIndex();
	    final StackPane stackPane = stackPanes[index];
	    final PlayingCard card = cards[index];
	    card.setBack(cbBack.isSelected());
	    card.setSize(sbSize.getValue() / 100);
	    card.setSelected(false);
	    card.resetCanvas();
	    cardList.clear();
	    cardList.add(stackPane);
	});
	
	cbBack.setOnAction(e -> {
	    final int index = lvCards.getSelectionModel().getSelectedIndex();
	    final PlayingCard card = cards[index];
	    card.setBack(cbBack.isSelected());
	    card.resetCanvas();
	});
	
	sbSize.valueProperty().addListener(e -> {
	    final double size = sbSize.getValue() / 100;
	    final int index = lvCards.getSelectionModel().getSelectedIndex();
	    final PlayingCard card = cards[index];
	    card.setSize(size);
	    card.resetCanvas();
	});
    }
}

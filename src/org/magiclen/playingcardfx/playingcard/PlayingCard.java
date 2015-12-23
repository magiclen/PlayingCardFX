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
package org.magiclen.playingcardfx.playingcard;

import java.time.LocalTime;
import javafx.beans.InvalidationListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;

/**
 * <p>
 * 撲克牌圖形化類別。
 * </p>
 * <p>
 * 繼承Region，可輕易地被加入至Parent容器中。
 * </p>
 * <p>
 * 實作Comparable介面，使得牌可以被比較大小，令其可被用於排序法。
 * </p>
 *
 * @author Magic Len
 */
public class PlayingCard extends Region implements Comparable<PlayingCard> {

    // -----類別列舉-----
    /**
     * <p>
     * 提供撲克牌的花色類型。
     * </p>
     * <p>
     * 有鬼牌、黑桃、紅心、梅花、方塊。
     * </p>
     */
    public static enum Suit {

	GHOST, SPADE, HEART, CLUB, DIAMOND
    }

    // -----類別常數-----
    /**
     * 預設的撲克牌大小比例(大小為長x寬，單位：像素)，以297*421為基準。
     */
    private static final double SIZE_RATE = 1.0; //預設的撲克牌大小(長x寬，單位：像素)，以297*421為基準。
    /**
     * 牌的花色圖形文字。
     */
    private static final String[] SUITSIMG = {"♨", "♠", "♥", "♣", "♦"}; //牌的花色圖形文字。
    /**
     * 牌的花色文字。
     */
    private static final String[] SUITS = {"鬼牌", "黑桃", "紅心", "梅花", "方塊"};
    /**
     * 牌值文字。
     */
    private static final String[] FACES = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    /**
     * 字型。
     */
    private static final String FONT_FAMILY = "Noto Sans CJK TC Regular";

    // -----類別方法-----
    /**
     * <p>
     * 取得目前的時間。
     * </p>
     * <p>
     * 用於發生例外時，在命令列中顯示出現問題的時間。
     * </p>
     *
     * @return 傳回時間文字。
     */
    private static String getExceptionTime() {
	final LocalTime currentTime = LocalTime.now();
	StringBuilder sb = new StringBuilder().append('['); //建立字串暫存物件。
	int hour = currentTime.getHour(), min = currentTime.getMinute(), sec = currentTime.getSecond(); //取得目前的時、分、秒。

	//以下將時、分、秒以MM:HH:SS的格式加入暫存物件中。
	if (hour < 10) {
	    sb.append(0);
	}
	sb.append(hour).append(':');
	if (min < 10) {
	    sb.append(0);
	}
	sb.append(min).append(':');
	if (sec < 10) {
	    sb.append(0);
	}
	sb.append(min).append(':').append("]");

	return sb.toString(); //傳回暫存的字串。
    }

    // -----物件常數-----
    /**
     * 儲存牌的畫布。
     */
    private final Canvas canvas = new Canvas();
    /**
     * 儲存畫布內容。
     */
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    /**
     * 儲存重新繪製畫布的監聽者。
     */
    private final InvalidationListener refreshCanvasListener = e -> resetCanvas();

    // -----物件變數-----
    /**
     * 儲存牌的花色。
     */
    private Suit suit;
    /**
     * 儲存牌值。
     */
    private int face;
    /**
     * 儲存撲克牌圖形尺寸的倍率。
     */
    private double sizeRate;
    /**
     * 儲存牌是否被選取。
     */
    private boolean select = false;
    /**
     * 儲存牌是否顯示背面。
     */
    private boolean back = false;

    // -----建構子-----
    /**
     * 建構子，若建構物件時沒有傳入任何參數，則自動以鬼牌處裡。
     */
    public PlayingCard() {
	this(Suit.GHOST, 1);
    }

    /**
     * 建構子，設定撲克牌的花色與牌值。
     *
     * @param suit 傳入撲克牌的花色
     * @param face 傳入撲克牌的值
     */
    public PlayingCard(final Suit suit, final int face) {
	initial(suit, face);
    }

    /**
     * 建構子，設定撲克牌的花色與牌值。
     *
     * @param ordinal 傳入撲克牌的花色
     * @param face 傳入撲克牌的值
     */
    public PlayingCard(final int ordinal, final int face) {
	Suit s = Suit.GHOST;
	switch (ordinal) {
	    case 1:
		s = Suit.SPADE;
		break;
	    case 2:
		s = Suit.HEART;
		break;
	    case 3:
		s = Suit.CLUB;
		break;
	    case 4:
		s = Suit.DIAMOND;
		break;
	    default:
		System.out.println(getExceptionTime() + "Creating PlayingCard warning! Please check the wrong suit value: " + ordinal);
	}
	initial(s, face);
    }

    // -----物件方法-----
    /**
     * 建構方法，傳入撲克牌的花色與牌值。
     *
     * @param suit 傳入撲克牌的花色
     * @param face 傳入撲克牌的值
     */
    private void initial(Suit suit, int face) {
	setCard(suit, face);
	setSize(SIZE_RATE);
	canvas.widthProperty().bind(this.widthProperty());
	canvas.heightProperty().bind(this.heightProperty());

	canvas.widthProperty().addListener(refreshCanvasListener);
	canvas.heightProperty().addListener(refreshCanvasListener);

	getChildren().add(canvas); //將畫紙加入牌中(貼到牌上)
    }

    /**
     * 判斷牌是否顯示背面。
     *
     * @return 傳回牌是否顯示背面
     */
    public boolean isBack() {
	return back;
    }

    /**
     * 設定牌是否顯示背面。
     *
     * @param back 傳入牌是否顯示背面
     */
    public void setBack(final boolean back) {
	this.back = back;
    }

    /**
     * 判斷牌是否被選取。
     *
     * @return 傳回牌是否被選取
     */
    public boolean isSelected() {
	return select;
    }

    /**
     * 設定牌是否被選取。
     *
     * @param select 傳入牌是否被選取
     */
    public void setSelected(final boolean select) {
	this.select = select;
    }

    /**
     * 設定牌的尺寸比例。
     *
     * @param sizeRate 傳入尺寸比例
     */
    public void setSize(final double sizeRate) {
	this.sizeRate = sizeRate;
	setPrefSize(297 * sizeRate, 421 * sizeRate); //將基準長寬乘上比例後，設定牌的尺寸大小
    }

    /**
     * 判斷牌是否為鬼牌。
     *
     * @return 傳回牌是否為鬼牌
     */
    public boolean isGhost() {
	return suit == Suit.GHOST;
    }

    /**
     * 判斷牌是否為黑色。
     *
     * @return 傳回牌是否為黑色
     */
    public boolean isBlack() {
	return suit == Suit.SPADE || suit == Suit.CLUB; //黑桃和梅花是黑色的
    }

    /**
     * 判斷牌是否為紅色。
     *
     * @return 傳回牌是否為紅色
     */
    public boolean isRed() {
	return suit == Suit.HEART || suit == Suit.DIAMOND; //紅心和方塊是紅色的
    }

    /**
     * 判斷牌是否為人像。
     *
     * @return 傳回牌是否為人像
     */
    public boolean isMan() {
	return face >= 11; //當牌值>=11(J、Q、K)時，就是人像
    }

    /**
     * 判斷牌值是否為數字。
     *
     * @return 傳回牌值是否為數字
     */
    public boolean isNumber() {
	return face >= 2 && face <= 10; //當牌值介於2和10之間時，就是數字。因為A、J、Q、K是文字
    }

    /**
     * 傳回牌所代表的字串，可格式化。
     *
     * @param format 傳入字串的格式化方式
     * @return 傳回牌所代表的字串
     */
    public String toFormatString(final String format) {
	try {
	    return String.format(format, getSuitString(), getFaceString());
	} catch (Exception e) {
	    try {
		return String.format(format, getSuitString());
	    } catch (final Exception err) {
		System.out.println(getExceptionTime() + "Format string failed! Caused by the wrong format text.");
	    }
	}
	return null;
    }

    /**
     * 傳回牌所代表的字串，預設以"%s%s"進行格式化。
     *
     * @return 傳回牌所代表的字串
     */
    @Override
    public String toString() {
	if (!isGhost()) {
	    return toFormatString("%s%s");
	} else {
	    return toFormatString("%s");
	}
    }

    /**
     * 傳回牌的花色圖形文字。
     *
     * @return 傳回牌的花色圖形文字
     */
    public String getSuitStringImg() {
	return SUITSIMG[suit.ordinal()];
    }

    /**
     * 傳回牌的花色文字。
     *
     * @return 傳回牌的花色文字
     */
    public String getSuitString() {
	return SUITS[suit.ordinal()];
    }

    /**
     * 傳回牌值的文字。
     *
     * @return 傳回牌值的文字
     */
    public String getFaceString() {
	return FACES[face - 1];
    }

    /**
     * 傳回牌的花色。
     *
     * @return 傳回牌的花色
     */
    public Suit getSuit() {
	return suit;
    }

    /**
     * 傳回牌值。
     *
     * @return 傳回牌值
     */
    public int getFace() {
	return face;
    }

    /**
     * 設定牌的花色與牌值。
     *
     * @param suit 傳入牌的花色
     * @param face 傳入牌值
     */
    public void setCard(final Suit suit, final int face) {
	setSuit(suit);
	setFace(face);
    }

    /**
     * 設定牌的花色。
     *
     * @param suit 傳入牌的花色
     */
    private void setSuit(final Suit suit) {
	this.suit = suit;
    }

    /**
     * 設定牌值，若牌值不正確，則產生錯誤訊息。
     *
     * @param face 傳入牌值
     */
    private void setFace(final int face) {
	try {
	    if (face > 13 || face < 1) { //若牌值超出範圍。
		throw new Exception();
	    }
	    this.face = face;
	} catch (final Exception e) {
	    System.out.println(getExceptionTime() + "Creating PlayingCard failed! Caused by the wrong face value : " + face);
	}
    }

    /**
     * 取得牌的畫布。
     *
     * @return 傳回牌的畫布
     */
    public Canvas getCanvas() {
	return canvas;
    }

    /**
     * 重繪畫布。
     */
    public void resetCanvas() {
	final double width = canvas.getWidth();  //取得寬度
	final double height = canvas.getHeight();  //取得高度
	double fontsize = 48 * sizeRate; //設定字體大小

	//畫背景
	gc.setFill(isSelected() ? Color.BLACK : Color.WHITE);
	gc.fillRect(0, 0, width, height);

	if (isBack()) {
	    fontsize /= 1.5;
	    double imgsize = fontsize * 0.9; //計算花樣估計大小
	    boolean align = true;
	    final int w = (int) Math.ceil(imgsize * 1.2);
	    final int h = (int) Math.ceil(imgsize * 0.6);
	    gc.setFill(isSelected() ? Color.color(0.2, 1, 1) : Color.color(0.75, 0, 0));
	    gc.setFont(Font.font(FONT_FAMILY, fontsize));
	    for (int i = -h; i <= height + (2 * h); i += h) {
		if (align) {
		    for (int j = 0; j <= width; j += w) {
			gc.fillText("♦", j, i);
		    }
		} else {
		    for (int j = -(w / 2); j <= width; j += w) {
			gc.fillText("♦", j, i);
		    }
		}
		align = !align;
	    }
	} else {
	    // 畫中心線
//	    gc.strokeLine(0, height / 2, width, height / 2);
//	    gc.strokeLine(width / 2, 0, width / 2, height);
	    if (isGhost()) { // 如果是鬼牌
		final double fontSizeG = fontsize * 4;
		final double halfFontSizeG = fontSizeG / 2;
		gc.setFont(Font.font(FONT_FAMILY, fontSizeG));
		gc.setFill(isSelected() ? Color.color(0, 0.7, 1) : Color.ORANGERED);
		gc.fillText(getSuitStringImg(), 149 * sizeRate - halfFontSizeG, 175 * sizeRate + halfFontSizeG);
	    } else { //如果不是鬼牌
		gc.setFill(isRed() ? isSelected() ? Color.color(0.2, 1, 1) : Color.color(0.75, 0, 0) : isSelected() ? Color.WHITE : Color.BLACK);
		final double fontsize_c; //花樣文字大小
		// ---繪製撲克牌---
		double a = 19 * sizeRate, b = fontsize, c = 9 * sizeRate, d = fontsize * 2;
		//設定花樣文字大小
		if (getFace() == 1) {
		    fontsize_c = fontsize * 3.0;
		} else if (getFace() == 2) {
		    fontsize_c = fontsize * 1.8;
		} else if (getFace() <= 10) {
		    fontsize_c = fontsize * 1.7;
		} else {
		    fontsize_c = fontsize * 1.1;
		}
		final double imgsize = fontsize_c * 0.5; //計算半花樣估計大小
		final String suitStringImg = getSuitStringImg();
		gc.setFont(Font.font(FONT_FAMILY, fontsize_c));
		switch (getFace()) {
		    case 1:
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 187 * sizeRate + imgsize);
			break;
		    case 2:
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 80 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize - width, 80 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 3:
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 4:
			a = (int) Math.ceil(16 * sizeRate);

			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 5:
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 6:
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 7:
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 135 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 8:
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 70 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 135 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize - width, 135 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 70 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 9:
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 50 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 50 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 150 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 150 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 200 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 50 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 50 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 150 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 150 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 10:
			a = sizeRate;

			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 50 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 50 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize, 150 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize, 150 * sizeRate + imgsize);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize, 100 * sizeRate + imgsize);
			inverseGraphics();
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 50 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 50 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 149 * sizeRate - imgsize - width, 100 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 205 * sizeRate - imgsize - width, 150 * sizeRate + imgsize - height);
			gc.fillText(suitStringImg, 93 * sizeRate - imgsize - width, 150 * sizeRate + imgsize - height);
			inverseGraphics();
			break;
		    case 11:
			gc.fillText("　　●", 42 * sizeRate - imgsize, 175 * sizeRate - imgsize);
			gc.fillText("　＜█＞", 42 * sizeRate - imgsize, 252 * sizeRate - imgsize);
			gc.fillText("　／　＼", 42 * sizeRate - imgsize, 329 * sizeRate - imgsize);
			break;
		    case 12:
			a = 15 * sizeRate;

			gc.fillText("　●／", 95 * sizeRate - imgsize, 175 * sizeRate - imgsize);
			gc.fillText("＜█", 95 * sizeRate - imgsize, 252 * sizeRate - imgsize);
			gc.fillText("／　＼", 95 * sizeRate - imgsize, 329 * sizeRate - imgsize);
			break;
		    case 13:
			a = 16 * sizeRate;

			gc.fillText("＼●", 95 * sizeRate - imgsize, 175 * sizeRate - imgsize);
			gc.fillText("　█＞", 95 * sizeRate - imgsize, 252 * sizeRate - imgsize);
			gc.fillText("／　）", 95 * sizeRate - imgsize, 329 * sizeRate - imgsize);
			break;
		}
		final String faceString = getFaceString();
		gc.setFont(Font.font(FONT_FAMILY, fontsize));
		gc.fillText(faceString, a, b);
		gc.fillText(suitStringImg, c, d);
		inverseGraphics();
		gc.fillText(faceString, a - width, b - height);
		gc.fillText(suitStringImg, c - width, d - height);
		inverseGraphics();
	    }
	}

    }

    /**
     * 旋轉畫布。
     */
    private void inverseGraphics() {
	final Affine at = gc.getTransform().clone(); //取得畫布的仿射轉換物件
	at.appendRotation(180); //旋轉180度
	gc.setTransform(at); //將設定過後的仿射轉換物件傳回給畫布
    }

    /**
     * <p>
     * 判斷兩物件是否在邏輯上相同。
     * </p>
     * <p>
     * 原則上當牌的花色和數值相同時，會判定為一樣的物件。
     * </p>
     *
     * @param o 傳入物件
     * @return 傳回兩物件是否在邏輯上相同
     */
    @Override
    public boolean equals(final Object o) {
	if (this == o) {
	    return true;
	}
	if (o instanceof PlayingCard) {
	    final PlayingCard card = (PlayingCard) o;
	    return getSuit() == card.getSuit() && getFace() == card.getFace();
	} else {
	    return false;
	}
    }

    /**
     * 卡片雜湊值。
     *
     * @return 傳回卡片雜湊值
     */
    @Override
    public int hashCode() {
	return getSuit().ordinal() * 100 + getFace();
    }

    /**
     * 比較兩張牌的大小。
     *
     * @param card 傳入一張撲克牌
     * @return 傳回兩張牌值的差距，若這張牌加權牌值大於目標牌加權牌值，傳回值為正；相同，傳回零；小於，傳回負數
     */
    @Override
    public int compareTo(final PlayingCard card) {
	//加權牌值計算公式 = 花色值*100 + 原牌值。
	int this_score = this.getSuit().ordinal() * 100 + this.getFace(); //取得這張牌加權之後的牌值
	int score = card.getSuit().ordinal() * 100 + card.getFace(); //取得目標牌加權之後的牌值
	return this_score - score; //若這張牌加權牌值大於目標牌加權牌值，傳回值為正；相同，傳回零；小於，傳回負數
    }
}

package com.project.waragainstbots;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class GameController {

    @FXML
    private Button card10Button;

    @FXML
    private Button card1Button;

    @FXML
    private Button card2Button;

    @FXML
    private Button card3Button;

    @FXML
    private Button card4Button;

    @FXML
    private Button card5Button;

    @FXML
    private Button card6Button;

    @FXML
    private Button card7Button;

    @FXML
    private Button card8Button;

    @FXML
    private Button card9Button;

    @FXML
    private Button cardCenterAButton;

    @FXML
    private Button cardCenterBButton;

    @FXML
    private Button cardPlayer2Button;

    @FXML
    private Label globalMessageLabel;

    @FXML
    private Label messageLabelPlayer2;

    @FXML
    private ImageView packCard;

    @FXML
    private Label resultsLabel;

    @FXML
    private Button skipButton;

    @FXML
    private Button startGameButton;
    private static int level;
    private static Button[] arrayButtons;
    private static final List<String> pack = new ArrayList<>();
    private static String[] myCards = new String[10];
    private static final List<String> cardsPlayer2 = new ArrayList<>();
    private static int botValue;
    private static int movesCounter = 0;
    private static boolean draw = false;
    private static int drawSelection;
    private static int botsKilledPlayer1 = 0;
    private static int botsKilledPlayer2 = 0;
    private static int pointsPlayer1 = 0;
    private static int pointsPlayer2 = 0;
    private static int delayTime = 0;
    private static boolean botWeapon = false;
    private static boolean botArmor = false;
    private static final HashMap<Integer, Pair<Integer, Integer>> hm2 = new HashMap<>();
    private static final List<Button> cardsToMove = new ArrayList<>();
    private static ActionEvent currentEvent;
    private static final List<String> lastCards = new ArrayList<>();

    public void setL(int l) {
        level = l;
    }

    public void setArrayButtons() {
        arrayButtons = new Button[10];
        arrayButtons[0] = card1Button;
        arrayButtons[1] = card2Button;
        arrayButtons[2] = card3Button;
        arrayButtons[3] = card4Button;
        arrayButtons[4] = card5Button;
        arrayButtons[5] = card6Button;
        arrayButtons[6] = card7Button;
        arrayButtons[7] = card8Button;
        arrayButtons[8] = card9Button;
        arrayButtons[9] = card10Button;
        setDisableButtons();
    }

    public void setEnableButtons() {
        for (Button b : arrayButtons) {
            b.setDisable(false);
        }
    }

    public void setDisableButtons() {
        for (Button b : arrayButtons) {
            b.setDisable(true);
        }
    }

    public void resetCards() {
        String[] suits = {
                "C", "B", "K", "P"
        };
        String[] rank = {
                "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "10", "11", "12", "13"
        };
        int n = 54;
        for (String s : rank) {
            for (String suit : suits) {
                pack.add(s + suit);
            }
        }
        pack.add("14R");
        pack.add("14B");
        for (int i = 0; i < n; i++) {
            int r = i + (int) (Math.random() * (n - i));
            String tmp = pack.get(r);
            pack.set(r, pack.get(i));
            pack.set(i, tmp);
        }
        System.out.println(pack.size());
    }

    @FXML
    void startGame(ActionEvent event) throws FileNotFoundException {
        cardsDistribution();
    }

    private void cardsDistribution() throws FileNotFoundException {

        resetCards();
        startGameButton.setDisable(true);
        startGameButton.setOpacity(0);

        hm2.put(1, new Pair<>(-200, 600));
        hm2.put(2, new Pair<>(-181, -296));

        for (int i = 1; i <= 2; i++) {
            for (int j = 0; j < 10; j++) {
                Timeline anim = new Timeline(
                        new KeyFrame(Duration.seconds(0.3), new KeyValue(packCard.translateXProperty(), hm2.get(i).getKey()), new KeyValue(packCard.translateYProperty(), hm2.get(i).getValue()))
                );
                anim.setDelay(Duration.millis(300 * j + 3000 * (i - 1)));
                anim.play();
                Timeline animBack = new Timeline(
                        new KeyFrame(Duration.seconds(0.3), new KeyValue(packCard.translateXProperty(), 0), new KeyValue(packCard.translateYProperty(), 0))
                );
                animBack.setDelay(Duration.millis(300 * (j + 1) + 3000 * (i - 1)));
                animBack.play();
                if (i == 1) {
                    myCards[j] = pack.get(0);
                    setBackgroundOnCard(pack.get(0), arrayButtons[j]);
                    Timeline animCard = new Timeline(
                            new KeyFrame(Duration.seconds(0.3), new KeyValue(arrayButtons[j].translateYProperty(), -300))
                    );
                    animCard.setDelay(Duration.millis(300 * (j + 1)));
                    animCard.play();
                } else cardsPlayer2.add(pack.get(0));
                pack.remove(0);
            }
        }
        System.out.println("Size of pack: " + pack.size());

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000L * 2);
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
            }
            Platform.runLater(() -> {
                try {
                    draw();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        thread.start();
    }

    private void draw() throws FileNotFoundException {
        System.out.println("Drawselection in draw():" + drawSelection);
        if (drawSelection != 0 && drawSelection != 1) {
            game(new ActionEvent());
        } else {
            System.out.println("Player 1: " + Arrays.toString(myCards));
            System.out.println("Player 2: " + cardsPlayer2);
            if (areSimilarForHand()) {
                globalMessageLabel.setOpacity(1);
                globalMessageLabel.setText("У гравця 1 одномастні карти");//Усім додаються бали

                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted.");
                    }
                    Platform.runLater(() -> {
                        try {
                            globalMessageLabel.setText("Новий раунд");
                            resetHands();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                t.start();
            } else if (areSimilar()) {
                globalMessageLabel.setOpacity(1);
                globalMessageLabel.setText("У гравця 2 одномастні карти");//Усім додаються бали
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted.");
                    }
                    Platform.runLater(() -> {
                        try {
                            globalMessageLabel.setText("Новий раунд");
                            resetHands();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                t.start();
            } else {
                setEnableButtons();

                globalMessageLabel.setOpacity(1);
                if (!draw)
                    globalMessageLabel.setText("Жеребкування: виберіть карту");
                else {
                    for (int i = 0; i < myCards.length; i++) {
                        if (valueOfCard(myCards[i]) == 14)
                            arrayButtons[i].setDisable(true);
                    }
                    globalMessageLabel.setText("Виберіть зброю та броню для бота");
                }
            }
        }
    }

    @FXML
    void moveCard(ActionEvent event) throws FileNotFoundException {
        delayTime = 0;
        Button b = (Button) event.getSource();
        Random rand = new Random();
        int y = (int) b.getLayoutY();

        if (!draw) {

            Timeline animCard1;
            if ((b.getId().charAt(4) == '1' && b.getId().charAt(5) != '0') || b.getId().charAt(4) == '3' || b.getId().charAt(4) == '5' || b.getId().charAt(4) == '7' || b.getId().charAt(4) == '9') {
                animCard1 = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(b.layoutYProperty(), y - 90))
                );
                animCard1.play();
                Timeline animBack = new Timeline(
                        new KeyFrame(Duration.seconds(0), new KeyValue(b.layoutYProperty(), y - 90)),
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(b.layoutYProperty(), y))
                );
                animBack.setDelay(Duration.millis(3000));
                animBack.play();
            } else {
                animCard1 = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(b.layoutYProperty(), y - 30))
                );
                animCard1.play();
                Timeline animBack = new Timeline(
                        new KeyFrame(Duration.seconds(0), new KeyValue(b.layoutYProperty(), y - 30)),
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(b.layoutYProperty(), y))
                );
                animBack.setDelay(Duration.millis(3000));
                animBack.play();
            }
            int max1, max2;
            int idChoice = Integer.parseInt(b.getId().charAt(4) + "");
            if (idChoice == 1)
                if (b.getId().charAt(5) == '0')
                    idChoice = 10;
            String temp = myCards[--idChoice];
            max1 = valueOfCard(temp);

            if (level == 2 || level == 3) {
                max2 = maxCard();
            } else {
                max2 = valueOfCard(cardsPlayer2.get(7));
            }
            String cardName2 = null;
            for (String s : cardsPlayer2) {
                if (Integer.toString(max2).length() == 1) {
                    if (s.contains(Integer.toString(max2)) && s.length() == 2) {
                        cardName2 = s;
                        break;
                    }
                } else {
                    if (s.contains(Integer.toString(max2))) {
                        cardName2 = s;
                        break;
                    }
                }
            }
            setBackgroundOnCard(cardName2, cardPlayer2Button);
            int x2 = (int) cardPlayer2Button.getLayoutX();
            Timeline animCard2 = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(cardPlayer2Button.layoutXProperty(), x2 - 248))
            );
            animCard2.play();
            Timeline animBack2 = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(cardPlayer2Button.layoutXProperty(), x2 - 248)),
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(cardPlayer2Button.layoutXProperty(), x2))
            );
            animBack2.setDelay(Duration.millis(3000));
            animBack2.play();

            drawSelection = max1 > max2 ? 1 : (max1 == max2 ? rand.nextInt(1, 3) : 2);


            System.out.println("DRAWSELECTION " + drawSelection);
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted.");
                }
                Platform.runLater(() -> globalMessageLabel.setText("Гру починає гравець " + drawSelection));
            });
            thread.start();
            setDisableButtons();
            draw = true;
            Thread thread1 = new Thread(() -> {
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted.");
                }
                Platform.runLater(() -> {
                    if (drawSelection != 1) {
                        try {
                            game(event);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        setEnableButtons();
                        globalMessageLabel.setOpacity(1);
                        globalMessageLabel.setText("Виберіть зброю та броню для бота");
                        for (int i = 0; i < myCards.length; i++) {
                            if (valueOfCard(myCards[i]) == 14) {
                                arrayButtons[i].setDisable(true);
                            }
                        }
                    }
                });
            });
            thread1.start();
        } else {
            game(event);
        }
    }

    @FXML
    void skipTurn(ActionEvent event) throws FileNotFoundException {
        skipButton.setOpacity(0);
        skipButton.setDisable(true);
        movesCounter++;
        delayTime = 0;
        if (2 - movesCounter == 1) {
            Timeline anim = new Timeline(
                    new KeyFrame(Duration.millis(1), new KeyValue(globalMessageLabel.textProperty(), "Гравець 2 переміг!"))
            );
            anim.play();
            Thread t1 = new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted.");
                }
                Platform.runLater(() -> {
                    try {
                        pointsPlayer2 += botsKilledPlayer2;
                        globalMessageLabel.setText("Новий раунд");
                        drawSelection = 1;
                        resetHands();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            t1.start();
        } else {
            drawSelection = 2;
            game(event);
        }
    }

    private void game(ActionEvent event) throws FileNotFoundException {
        currentEvent = event;
        System.out.println("Size of pack: "+pack.size());
        System.out.println("Player 1 " + Arrays.toString(myCards));
        System.out.println("Player 2 " + cardsPlayer2);
        if (drawSelection == 1) {
            delayTime = 0;
            if (!botWeapon || !botArmor) {
                movesCounter = 0;
                Button b = (Button) event.getSource();
                cardsToMove.add(b);
                int idChoice = Integer.parseInt(b.getId().charAt(4) + "");
                if (idChoice == 1)
                    if (b.getId().charAt(5) == '0')
                        idChoice = 10;
                idChoice--;
                if (suitOfCard(myCards[idChoice]) == 'B') {


                    int curX = (int) arrayButtons[idChoice].getLayoutX();
                    int curY = (int) arrayButtons[idChoice].getLayoutY();
                    int x = (int) cardCenterAButton.getLayoutX();
                    int y = (int) cardCenterAButton.getLayoutY();
                    int finalIdChoice = idChoice;
                    Thread t = new Thread(() -> Platform.runLater(() -> {

                        setDisableButtons();
                        Timeline anim = new Timeline(
                                new KeyFrame(Duration.millis(1500), new KeyValue(arrayButtons[finalIdChoice].translateXProperty(), x - curX), new KeyValue(arrayButtons[finalIdChoice].translateYProperty(), y - curY))

                        );
                        anim.play();
                        Timeline animBack = new Timeline(
                                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.opacityProperty(), 1), new KeyValue(cardCenterAButton.backgroundProperty(), arrayButtons[finalIdChoice].getBackground())),
                                new KeyFrame(Duration.millis(1), new KeyValue(arrayButtons[finalIdChoice].translateYProperty(), 0), new KeyValue(arrayButtons[finalIdChoice].translateXProperty(), 0))
                        );
                        animBack.setDelay(Duration.millis(1500));
                        animBack.play();
                    }));
                    t.start();

                    myCards[finalIdChoice] = "";
                    botWeapon = true;

                    Thread t1 = new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted.");
                        }
                        Platform.runLater(() -> {
                            if (!botArmor) {
                                for (int i = 0; i < myCards.length; i++) {
                                    if (suitOfCard(myCards[i]) == 'R' && valueOfCard(myCards[i]) != 14) {
                                        for (Button button : arrayButtons) {
                                            Timeline tAnim;
                                            if (i + 1 == Integer.parseInt(button.getId().charAt(4) + "")) {
                                                tAnim = new Timeline(
                                                        new KeyFrame(Duration.millis(1), new KeyValue(button.disableProperty(), false))
                                                );
                                                tAnim.play();
                                                break;
                                            } else if (i + 1 == 10) {
                                                tAnim = new Timeline(
                                                        new KeyFrame(Duration.millis(1), new KeyValue(card10Button.disableProperty(), false))
                                                );
                                                tAnim.play();
                                            }


                                        }
                                    }
                                }
                            }
                        });
                    });
                    t1.start();
                }
                if (suitOfCard(myCards[idChoice]) == 'R') {

                    botValue = valueOfCard(myCards[idChoice]);
                    myCards[idChoice] = "";
                    botArmor = true;

                    int curX = (int) arrayButtons[idChoice].getLayoutX();
                    int curY = (int) arrayButtons[idChoice].getLayoutY();
                    int x = (int) cardCenterBButton.getLayoutX();
                    int y = (int) cardCenterBButton.getLayoutY();


                    int finalIdChoice = idChoice;
                    Thread t = new Thread(() -> Platform.runLater(() -> {
                        setDisableButtons();
                        Timeline anim = new Timeline(
                                new KeyFrame(Duration.millis(1500), new KeyValue(arrayButtons[finalIdChoice].translateXProperty(), x - curX), new KeyValue(arrayButtons[finalIdChoice].translateYProperty(), y - curY))

                        );
                        anim.play();
                        Timeline animBack = new Timeline(
                                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.opacityProperty(), 1), new KeyValue(cardCenterBButton.backgroundProperty(), arrayButtons[finalIdChoice].getBackground())),
                                new KeyFrame(Duration.millis(1), new KeyValue(arrayButtons[finalIdChoice].translateYProperty(), 0), new KeyValue(arrayButtons[finalIdChoice].translateXProperty(), 0))
                        );
                        animBack.setDelay(Duration.millis(1500));
                        animBack.play();

                    }));
                    t.start();
                    Thread t1 = new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted.");
                        }
                        Platform.runLater(() -> {
                            if (!botWeapon) {
                                for (int i = 0; i < myCards.length; i++) {
                                    if (suitOfCard(myCards[i]) == 'B' && valueOfCard(myCards[i]) != 14) {
                                        for (Button button : arrayButtons) {
                                            Timeline tAnim;
                                            if (i + 1 == Integer.parseInt(button.getId().charAt(4) + "")) {
                                                tAnim = new Timeline(
                                                        new KeyFrame(Duration.millis(1), new KeyValue(button.disableProperty(), false))
                                                );
                                                tAnim.play();
                                                break;
                                            } else if (i + 1 == 10) {
                                                tAnim = new Timeline(
                                                        new KeyFrame(Duration.millis(1), new KeyValue(card10Button.disableProperty(), false))
                                                );
                                                tAnim.play();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    });
                    t1.start();

                }

                if (botWeapon && botArmor) {
                    delayTime += 1500 + 500;
                    drawSelection = 2;
                }
            } else {
                //мій хід
                globalMessageLabel.setOpacity(0);
                Button b = (Button) event.getSource();
                int idChoice = Integer.parseInt(b.getId().charAt(4) + "");
                if (idChoice == 1)
                    if (b.getId().charAt(5) == '0')
                        idChoice = 10;
                idChoice--;
                lastCards.add(myCards[idChoice]);
                myCards[idChoice] = "";
                cardsToMove.add(b);
                int curX = (int) b.getLayoutX();
                int curY = (int) b.getLayoutY();
                int x = (int) cardCenterBButton.getLayoutX();
                int y = (int) cardCenterBButton.getLayoutY();

                setDisableButtons();
                Timeline anim = new Timeline(
                        new KeyFrame(Duration.millis(1500), new KeyValue(b.translateXProperty(), x - curX), new KeyValue(b.translateYProperty(), y - curY))

                );
                anim.play();
                Timeline animBack = new Timeline(
                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.opacityProperty(), 1), new KeyValue(cardCenterBButton.backgroundProperty(), b.getBackground())),
                        new KeyFrame(Duration.millis(1), new KeyValue(b.translateYProperty(), 0), new KeyValue(b.translateXProperty(), 0))
                );
                animBack.setDelay(Duration.millis(1500));
                animBack.play();

                Timeline pileAnim = new Timeline(
                        new KeyFrame(Duration.millis(2000), new KeyValue(cardCenterAButton.translateYProperty(), 1000), new KeyValue(cardCenterAButton.translateXProperty(), -1000)),
                        new KeyFrame(Duration.millis(2000), new KeyValue(cardCenterBButton.translateYProperty(), 1000), new KeyValue(cardCenterBButton.translateXProperty(), -1000))
                );
                //pileAnim.setDelay(Duration.millis(delayTime+4000));
                pileAnim.setDelay(Duration.millis(3000));
                pileAnim.play();

                Timeline cardsBack = new Timeline(
                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.opacityProperty(), 0), new KeyValue(cardCenterBButton.backgroundProperty(), null)),
                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.translateYProperty(), 0), new KeyValue(cardCenterBButton.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.opacityProperty(), 0), new KeyValue(cardCenterAButton.backgroundProperty(), null)),
                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.translateYProperty(), 0), new KeyValue(cardCenterAButton.translateXProperty(), 0))
                );
                cardsBack.setDelay(Duration.millis(5000));
                cardsBack.play();

                botArmor = false;
                botValue = 0;
                botWeapon = false;
                botsKilledPlayer1++;
                System.out.println("BotsKilled - 1:" + botsKilledPlayer1);
                Thread tA = new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted.");
                    }
                    Platform.runLater(() -> {
                        int cards1;
                        try {
                            cards1 = takeCardsInHand();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        int cards2 = takeCards();

                        int tempDelayTime = 0;


                        tempDelayTime = takeCardsInHandAnimation(cards1, tempDelayTime);
                        tempDelayTime = takeCardsAnimation(cards2, tempDelayTime);
                        int finalTempDelayTime = tempDelayTime;
                        if (areSimilarForHand()) {

                            Thread tGM = new Thread(() -> {
                                try {
                                    Thread.sleep(finalTempDelayTime);
                                } catch (InterruptedException e) {
                                    System.out.println("Interrupted.");
                                }
                                Platform.runLater(() -> {
                                    pointsPlayer1 += botsKilledPlayer1;
                                    pointsPlayer2 += botsKilledPlayer2;
                                    globalMessageLabel.setOpacity(1);
                                    globalMessageLabel.setText("У гравця 1 одномастні карти");//Усім додаються бали
                                });
                            });
                            tGM.start();

                            Thread tNR = new Thread(() -> {
                                try {
                                    Thread.sleep(2000 + finalTempDelayTime);
                                } catch (InterruptedException e) {
                                    System.out.println("Interrupted.");
                                }
                                Platform.runLater(() -> {
                                    try {
                                        globalMessageLabel.setText("Новий раунд");
                                        resetHands();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            });
                            tNR.start();
                        } else if (areSimilar()) {

                            Thread tGM = new Thread(() -> {
                                try {
                                    Thread.sleep(finalTempDelayTime);
                                } catch (InterruptedException e) {
                                    System.out.println("Interrupted.");
                                }
                                Platform.runLater(() -> {
                                    pointsPlayer1 += botsKilledPlayer1;
                                    pointsPlayer2 += botsKilledPlayer2;
                                    globalMessageLabel.setOpacity(1);
                                    globalMessageLabel.setText("У гравця 2 одномастні карти");//Усім додаються бали
                                });
                            });
                            tGM.start();

                            Thread tNR = new Thread(() -> {
                                try {
                                    Thread.sleep(2000 + finalTempDelayTime);
                                } catch (InterruptedException e) {
                                    System.out.println("Interrupted.");
                                }
                                Platform.runLater(() -> {
                                    try {
                                        globalMessageLabel.setText("Новий раунд");
                                        resetHands();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            });
                            tNR.start();
                        } else {
                            Thread t = new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    System.out.println("Interrupted.");
                                }
                                Platform.runLater(() -> {
                                    setEnableButtons();
                                    globalMessageLabel.setOpacity(1);
                                    globalMessageLabel.setText("Виберіть зброю та броню для бота");
                                    for (int i = 0; i < myCards.length; i++) {
                                        if (valueOfCard(myCards[i]) == 14) {
                                            arrayButtons[i].setDisable(true);
                                        }
                                    }
                                });
                            });
                            t.start();
                        }
                    });
                });
                tA.start();

            }

        }
        if (drawSelection == 2) {
            if (areSimilar()) {
                Thread a = new Thread(() -> {
                    try {
                        Thread.sleep(delayTime);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted.");
                    }
                    Platform.runLater(() -> {
                        pointsPlayer1 += botsKilledPlayer1;
                        pointsPlayer2 += botsKilledPlayer2;
                        globalMessageLabel.setOpacity(1);
                        globalMessageLabel.setText("У гравця 2 одномастні карти");//Усім додаються бали
                    });
                });
                a.start();

                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(delayTime + 2000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted.");
                    }
                    Platform.runLater(() -> {
                        try {
                            //delayTime+=2000;
                            globalMessageLabel.setText("Новий раунд");
                            resetHands();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                t.start();
            } else {
                //створення бота
                if (!botWeapon || !botArmor) {
                    movesCounter = 0;
                    Random rand = new Random();
                    String cardB, cardR;
                    List<String> al = new ArrayList<>(List.copyOf(cardsPlayer2));
                    al.removeIf(s -> valueOfCard(s) == 14);

                    if(level == 3){
                        cardB = minCardWithColor(al, 'B');
                        al.removeIf(s -> suitOfCard(s) == 'B');
                        cardR = minimax(al);
                    }
                    else if (level == 2) {
                        cardB = minCardWithColor(al, 'B');
                        al.removeIf(s -> suitOfCard(s) == 'B');
                        al.sort((o1, o2) -> valueOfCard(o1) - valueOfCard(o2));
                        if (botsKilledPlayer2 >= 2)
                            cardR = maxCardWithColor(al, 'R');
                        else {
                            cardR = al.size() >= 2 ? al.get(al.size() - 2) : maxCardWithColor(al, 'R');
                        }
                    } else {
                        cardB = maxCardWithColor(al, 'B');
                        al.removeIf(s -> suitOfCard(s) == 'B');
                        cardR = al.get(rand.nextInt(0, al.size()));
                    }
                    cardsPlayer2.remove(cardB);
                    cardsPlayer2.remove(cardR);
                    String finalCardB = cardB;
                    int curX = (int) cardPlayer2Button.getLayoutX();
                    int curY = (int) cardPlayer2Button.getLayoutY();
                    int xB = (int) cardCenterAButton.getLayoutX();
                    int yB = (int) cardCenterAButton.getLayoutY();
                    int xR = (int) cardCenterBButton.getLayoutX();
                    int yR = (int) cardCenterBButton.getLayoutY();
                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep(delayTime);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted.");
                        }
                        Platform.runLater(() -> {
                            try {
                                setBackgroundOnCard(finalCardB, cardPlayer2Button);
                                globalMessageLabel.setOpacity(1);
                                globalMessageLabel.setText("Створення бота");
                                Timeline animB = new Timeline(
                                        new KeyFrame(Duration.millis(1500), new KeyValue(cardPlayer2Button.translateXProperty(), xB - curX), new KeyValue(cardPlayer2Button.translateYProperty(), yB - curY))
                                );
                                animB.play();

                                Timeline animBackB = new Timeline(
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.opacityProperty(), 1)),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.backgroundProperty(), cardPlayer2Button.getBackground())),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardPlayer2Button.translateYProperty(), 0), new KeyValue(cardPlayer2Button.translateXProperty(), 0))
                                );
                                animBackB.setDelay(Duration.millis(1500));
                                animBackB.play();
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
                    t.start();

                    String finalCardR = cardR;
                    Thread t1 = new Thread(() -> {
                        try {
                            Thread.sleep(delayTime + 1600);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted.");
                        }
                        Platform.runLater(() -> {
                            try {
                                setBackgroundOnCard(finalCardR, cardPlayer2Button);
                                Timeline animR = new Timeline(
                                        new KeyFrame(Duration.millis(1500), new KeyValue(cardPlayer2Button.translateXProperty(), xR - curX), new KeyValue(cardPlayer2Button.translateYProperty(), yR - curY))
                                );
                                animR.play();

                                Timeline animBackR = new Timeline(
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.opacityProperty(), 1)),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.backgroundProperty(), cardPlayer2Button.getBackground())),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardPlayer2Button.translateYProperty(), 0), new KeyValue(cardPlayer2Button.translateXProperty(), 0))
                                );
                                animBackR.setDelay(Duration.millis(1500));
                                animBackR.play();
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
                    t1.start();

                    botArmor = true;
                    botWeapon = true;
                    botValue = valueOfCard(cardR);
                    Thread f2 = new Thread(() -> {
                        try {
                            Thread.sleep(delayTime + 3100);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted.");
                        }
                        Platform.runLater(() -> {
                            globalMessageLabel.setText("Виконайте хід");
                            drawSelection = 1;
                            int count1 = 0;
                            for (int i = 0; i < myCards.length; i++) {
                                if (botValue == 13) {
                                    if (valueOfCard(myCards[i]) == 14) {
                                        count1++;
                                        for (Button button : arrayButtons) {
                                            Timeline tAnim;
                                            if (i + 1 == Integer.parseInt(button.getId().charAt(4) + "")) {
                                                tAnim = new Timeline(
                                                        new KeyFrame(Duration.millis(1), new KeyValue(button.disableProperty(), false))
                                                );
                                                tAnim.play();
                                                break;
                                            } else if (i + 1 == 10) {
                                                tAnim = new Timeline(
                                                        new KeyFrame(Duration.millis(1), new KeyValue(card10Button.disableProperty(), false))
                                                );
                                                tAnim.play();
                                            }
                                        }
                                    }
                                } else if ((suitOfCard(myCards[i]) == 'B' && valueOfCard(myCards[i]) >= botValue) || valueOfCard(myCards[i]) == 14) {
                                    count1++;
                                    for (Button button : arrayButtons) {
                                        Timeline tAnim;
                                        if (i + 1 == Integer.parseInt(button.getId().charAt(4) + "")) {
                                            tAnim = new Timeline(
                                                    new KeyFrame(Duration.millis(1), new KeyValue(button.disableProperty(), false))
                                            );
                                            tAnim.play();
                                            break;
                                        } else if (i + 1 == 10) {
                                            tAnim = new Timeline(
                                                    new KeyFrame(Duration.millis(1), new KeyValue(card10Button.disableProperty(), false))
                                            );
                                            tAnim.play();
                                        }
                                    }
                                }
                            }
                            if (count1 == 0) {
                                skipButton.setDisable(false);
                                skipButton.setOpacity(1);
                            }

                        });
                    });
                    f2.start();

                }
                //хід гравця
                else {
                    ArrayList<String> res = new ArrayList<>();
                    List<String> al = new ArrayList<>(List.copyOf(cardsPlayer2));
                    String card = "";
                    String temp;

                    while (valueOfCard(temp = maxCardWithColor(al, 'B')) >= botValue) {
                        if (botValue == 13)
                            break;
                        res.add(temp);
                        al.remove(temp);
                    }
                    boolean checker = false;
                    if (valueOfCard(temp = maxCardWithColor(al, 'R')) == 14 || valueOfCard(temp = maxCardWithColor(al, 'B')) == 14) {
                        al.remove(temp);
                        res.add(temp);
                        checker = true;
                    }
                    System.out.println("Weapons: " + res);

                    if (!res.isEmpty()) {
                        if (level == 2 || level == 3) {
                            card = minCardWithColor(res, 'B');
                        } else {
                            if (checker)
                                card = res.get(res.size() - 1);
                            else
                                card = maxCardWithColor(res, 'B');
                        }
                        if (card.isEmpty())
                            card = res.get(0);
                    }
                    String answer;
                    System.out.println(card);
                    if (!card.isEmpty()) {

                        answer = "Ходжу";

                        String finalCard = card;
                        String finalAnswer = answer;
                        Thread t1 = new Thread(() -> {
                            try {
                                Thread.sleep(delayTime);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted.");
                            }
                            Platform.runLater(() -> {
                                try {
                                    System.out.println("Ходжу");
                                    globalMessageLabel.setOpacity(0);
                                    Timeline animCard2 = new Timeline(
                                            new KeyFrame(Duration.millis(1000), new KeyValue(messageLabelPlayer2.opacityProperty(), 1), new KeyValue(messageLabelPlayer2.textProperty(), finalAnswer))

                                    );
                                    animCard2.play();
                                    Timeline animBack2 = new Timeline(
                                            new KeyFrame(Duration.millis(1), new KeyValue(messageLabelPlayer2.opacityProperty(), 1)),
                                            new KeyFrame(Duration.millis(1000), new KeyValue(messageLabelPlayer2.opacityProperty(), 0), new KeyValue(messageLabelPlayer2.textProperty(), ""))
                                    );
                                    animBack2.setDelay(Duration.millis(2000));
                                    animBack2.play();
                                    cardsPlayer2.remove(finalCard);
                                    setBackgroundOnCard(finalCard, cardPlayer2Button);
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                        t1.start();
                        botsKilledPlayer2++;
                        System.out.println("BotsKilled - 2:" + botsKilledPlayer2);

                        int curX = (int) cardPlayer2Button.getLayoutX();
                        int curY = (int) cardPlayer2Button.getLayoutY();
                        int x = (int) cardCenterBButton.getLayoutX();
                        int y = (int) cardCenterBButton.getLayoutY();
                        Thread t2 = new Thread(() -> {
                            try {
                                Thread.sleep(delayTime + 3000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted.");
                            }
                            Platform.runLater(() -> {
                                System.out.println("Ходжу2");
                                Timeline anim = new Timeline(
                                        new KeyFrame(Duration.millis(1500), new KeyValue(cardPlayer2Button.translateXProperty(), x - curX), new KeyValue(cardPlayer2Button.translateYProperty(), y - curY))

                                );
                                anim.play();
                                Timeline animBack = new Timeline(
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.backgroundProperty(), cardPlayer2Button.getBackground())),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardPlayer2Button.translateYProperty(), 0), new KeyValue(cardPlayer2Button.translateXProperty(), 0))
                                );
                                animBack.setDelay(Duration.millis(1500));
                                animBack.play();

                                Timeline pileAnim = new Timeline(
                                        new KeyFrame(Duration.millis(2000), new KeyValue(cardCenterAButton.translateYProperty(), 1000), new KeyValue(cardCenterAButton.translateXProperty(), -1000)),
                                        new KeyFrame(Duration.millis(2000), new KeyValue(cardCenterBButton.translateYProperty(), 1000), new KeyValue(cardCenterBButton.translateXProperty(), -1000))
                                );
                                pileAnim.setDelay(Duration.millis(3000));
                                pileAnim.play();

                                Timeline cardsBack = new Timeline(
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.opacityProperty(), 0), new KeyValue(cardCenterBButton.backgroundProperty(), null)),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.translateYProperty(), 0), new KeyValue(cardCenterBButton.translateXProperty(), 0)),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.opacityProperty(), 0), new KeyValue(cardCenterAButton.backgroundProperty(), null)),
                                        new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.translateYProperty(), 0), new KeyValue(cardCenterAButton.translateXProperty(), 0))
                                );
                                cardsBack.setDelay(Duration.millis(5000));
                                cardsBack.play();


                                botValue = 0;
                                botWeapon = false;
                                botArmor = false;
                                int cards2 = takeCards();
                                int cards1;
                                try {
                                    cards1 = takeCardsInHand();
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                int tempDelayTime = 5000;

                                tempDelayTime = takeCardsAnimation(cards2, tempDelayTime);
                                tempDelayTime = takeCardsInHandAnimation(cards1, tempDelayTime);
                                int finalTempDelayTime = tempDelayTime;
                                if (areSimilarForHand()) {

                                    Thread tGM = new Thread(() -> {
                                        try {
                                            Thread.sleep(finalTempDelayTime);
                                        } catch (InterruptedException e) {
                                            System.out.println("Interrupted.");
                                        }
                                        Platform.runLater(() -> {
                                            pointsPlayer1 += botsKilledPlayer1;
                                            pointsPlayer2 += botsKilledPlayer2;
                                            globalMessageLabel.setOpacity(1);
                                            globalMessageLabel.setText("У гравця 1 одномастні карти");//Усім додаються бали
                                        });
                                    });
                                    tGM.start();

                                    Thread tNR = new Thread(() -> {
                                        try {
                                            Thread.sleep(2000 + finalTempDelayTime);
                                        } catch (InterruptedException e) {
                                            System.out.println("Interrupted.");
                                        }
                                        Platform.runLater(() -> {
                                            try {
                                                globalMessageLabel.setText("Новий раунд");
                                                resetHands();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                    });
                                    tNR.start();
                                } else if (areSimilar()) {

                                    Thread tGM = new Thread(() -> {
                                        try {
                                            Thread.sleep(finalTempDelayTime);
                                        } catch (InterruptedException e) {
                                            System.out.println("Interrupted.");
                                        }
                                        Platform.runLater(() -> {
                                            pointsPlayer1 += botsKilledPlayer1;
                                            pointsPlayer2 += botsKilledPlayer2;
                                            globalMessageLabel.setOpacity(1);
                                            globalMessageLabel.setText("У гравця 2 одномастні карти");//Усім додаються бали
                                        });
                                    });
                                    tGM.start();

                                    Thread tNR = new Thread(() -> {
                                        try {
                                            Thread.sleep(2000 + finalTempDelayTime);
                                        } catch (InterruptedException e) {
                                            System.out.println("Interrupted.");
                                        }
                                        Platform.runLater(() -> {
                                            try {
                                                globalMessageLabel.setText("Новий раунд");
                                                resetHands();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                    });
                                    tNR.start();
                                } else {
                                    Thread t3 = new Thread(() -> {
                                        try {
                                            Thread.sleep(5900);
                                        } catch (InterruptedException e) {
                                            System.out.println("Interrupted.");
                                        }
                                        Platform.runLater(() -> {
                                            System.out.println("Бота буду ставить");
                                            try {
                                                delayTime = 0;
                                                game(event);
                                            } catch (FileNotFoundException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                    });
                                    t3.start();
                                }
                            });
                        });
                        t2.start();
                    } else {
                        movesCounter++;
                        answer = "Пропускаю";
                        String finalAnswer = answer;
                        Thread t = new Thread(() -> {
                            try {
                                Thread.sleep(delayTime);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted.");
                            }
                            Platform.runLater(() -> {
                                globalMessageLabel.setOpacity(0);
                                Timeline animCard2 = new Timeline(
                                        new KeyFrame(Duration.millis(1000), new KeyValue(messageLabelPlayer2.opacityProperty(), 1), new KeyValue(messageLabelPlayer2.textProperty(), finalAnswer))

                                );
                                animCard2.play();
                                Timeline animBack2 = new Timeline(
                                        new KeyFrame(Duration.millis(1), new KeyValue(messageLabelPlayer2.opacityProperty(), 1)),
                                        new KeyFrame(Duration.millis(1000), new KeyValue(messageLabelPlayer2.opacityProperty(), 0), new KeyValue(messageLabelPlayer2.textProperty(), ""))
                                );
                                animBack2.setDelay(Duration.millis(2000));
                                animBack2.play();
                            });
                        });
                        t.start();


                        Timeline anim = new Timeline(
                                new KeyFrame(Duration.millis(1), new KeyValue(globalMessageLabel.opacityProperty(), 1)),
                                new KeyFrame(Duration.millis(1), new KeyValue(globalMessageLabel.textProperty(), "Гравець 1 переміг!"))
                        );
                        anim.setDelay(Duration.millis(delayTime + 3000));
                        anim.play();


                        Thread t1 = new Thread(() -> {
                            try {
                                Thread.sleep(delayTime + 5000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted.");
                            }
                            Platform.runLater(() -> {
                                try {
                                    pointsPlayer1 += botsKilledPlayer1;
                                    globalMessageLabel.setText("Новий раунд");
                                    drawSelection = 2;
                                    setDisableButtons();
                                    resetHands();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                        t1.start();


                    }
                }
            }
        }
    }

    private String minimax(List<String> armors) {
        int counterBig = 0, counterSmall = 0;
        Random rand = new Random();
        List<String> weapons = new ArrayList<>(List.copyOf(cardsPlayer2));
        weapons.removeIf(s -> suitOfCard(s) == 'R');
        armors.sort((o1, o2) -> valueOfCard(o1) - valueOfCard(o2));
        for (int i = 0; i < armors.size(); i++) {
            if (valueOfCard(armors.get(i)) >= 10)
                counterBig++;
            else counterSmall++;
        }
        if (pointsPlayer2 < pointsPlayer1 || botsKilledPlayer2 + pointsPlayer2 >= 10 || botsKilledPlayer2 >= 2) {
            if(botsKilledPlayer2>0)
                return maxCardWithColor(armors, 'R');
            else return minCardWithColor(armors, 'R');
        } else {
            if (counterBig > 2) {
                if (botsKilledPlayer2 > 0) {
                    if (valueOfCard(maxCardWithColor(weapons, 'B')) < 10 || weapons.size() <= 3 || valueOfCard(maxCardWithColor(lastCards, 'B')) >= 10)
                        return maxCardWithColor(armors, 'R');
                    else return armors.get(armors.size() - 2);
                } else {
                    if (rand.nextInt(0, 2) == 1) // блеф
                        return armors.get(armors.size() - 2);
                    else return armors.get(armors.size() - 3);
                }
            } else {
                if ((counterSmall > 3 && counterBig > 0 && botsKilledPlayer2 > 0) || weapons.size() > armors.size()) //6-4, 7-3, 8-2, 9-1
                    return maxCardWithColor(armors, 'R');
                else return minCardWithColor(armors, 'R');
            }
        }
    }

    private void win(int number, ActionEvent event) throws IOException {
        pointsPlayer1 = 0;
        pointsPlayer2 = 0;
        draw = false;
        drawSelection = 0;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/waragainstbots/win-view.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();

        WinController wc = loader.getController();
        wc.setWinner(number);
        stage.show();
    }

    private boolean areSimilarForHand() {
        boolean red = true;
        boolean black = true;
        for (String s : myCards) {
            if (!s.isEmpty() && valueOfCard(s) != 14) {
                if (suitOfCard(s) == 'B')
                    black = false;
                else red = false;
            }
        }
        return !(!red && !black);
    }

    private boolean areSimilar() {
        boolean red = true;
        boolean black = true;
        for (String card : cardsPlayer2) {
            if (valueOfCard(card) != 14) {
                if (suitOfCard(card) == 'B')
                    black = false;
                else red = false;
            }
        }
        return !(!red && !black);
    }

    private void animResults() {
        String res = "Результати:\nГравець 1 - " + pointsPlayer1 + "\nГравець 2 - " + pointsPlayer2;

        Timeline anim = new Timeline(
                new KeyFrame(Duration.millis(1), new KeyValue(resultsLabel.textProperty(), res)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(resultsLabel.opacityProperty(), 1))
        );
        anim.play();
        Timeline animBack = new Timeline(
                new KeyFrame(Duration.millis(1), new KeyValue(resultsLabel.textProperty(), null)),
                new KeyFrame(Duration.millis(1), new KeyValue(resultsLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(resultsLabel.opacityProperty(), 0))
        );
        animBack.setDelay(Duration.millis(2000));
        animBack.play();
    }

    private void resetHands() throws FileNotFoundException {
        animResults();

        int x = (int) packCard.getLayoutX();
        int y = (int) packCard.getLayoutY();
        for (Button b : arrayButtons) {
            if (b.getBackground() != null) {
                int curX = (int) b.getLayoutX();
                int curY = (int) b.getLayoutY();
                Timeline anim = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), new KeyValue(b.translateXProperty(), x - curX), new KeyValue(b.translateYProperty(), y - curY))
                );
                anim.play();
                Timeline animBack = new Timeline(
                        new KeyFrame(Duration.millis(1), new KeyValue(b.translateYProperty(), 0), new KeyValue(b.translateXProperty(), 0))
                );
                animBack.setDelay(Duration.millis(500));
                animBack.play();

                try {
                    setBackgroundOnCard("background-card", cardPlayer2Button);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }


        int xA = (int) cardCenterAButton.getLayoutX();
        int yA = (int) cardCenterAButton.getLayoutY();
        Timeline centerCardAAnim = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cardCenterAButton.translateXProperty(), x - xA), new KeyValue(cardCenterAButton.translateYProperty(), y - yA))
        );
        centerCardAAnim.play();

        Timeline centerCardAAnimBack = new Timeline(
                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.translateYProperty(), 0), new KeyValue(cardCenterAButton.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.backgroundProperty(), null)),
                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterAButton.opacityProperty(), 0))
        );
        centerCardAAnimBack.setDelay(Duration.millis(500));
        centerCardAAnimBack.play();

        int xB = (int) cardCenterBButton.getLayoutX();
        int yB = (int) cardCenterBButton.getLayoutY();
        Timeline centerCardBAnim = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cardCenterBButton.translateXProperty(), x - xB), new KeyValue(cardCenterBButton.translateYProperty(), y - yB))
        );
        centerCardBAnim.play();

        Timeline centerCardBAnimBack = new Timeline(
                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.translateYProperty(), 0), new KeyValue(cardCenterBButton.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.backgroundProperty(), null)),
                new KeyFrame(Duration.millis(1), new KeyValue(cardCenterBButton.opacityProperty(), 0))
        );
        centerCardBAnimBack.setDelay(Duration.millis(500));
        centerCardBAnimBack.play();


        int x2 = (int) cardPlayer2Button.getLayoutX();
        int y2 = (int) cardPlayer2Button.getLayoutY();
        Timeline anim = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(cardPlayer2Button.translateXProperty(), x - x2), new KeyValue(cardPlayer2Button.translateYProperty(), y - y2))
        );
        anim.play();
        Timeline animBack = new Timeline(
                new KeyFrame(Duration.millis(1), new KeyValue(cardPlayer2Button.translateYProperty(), 0), new KeyValue(cardPlayer2Button.translateXProperty(), 0))
        );
        animBack.setDelay(Duration.millis(500));
        animBack.play();


        movesCounter = 0;
        pack.clear();
        cardsPlayer2.clear();
        cardsToMove.clear();
        myCards = new String[10];
        botValue = 0;
        botWeapon = false;
        botArmor = false;
        botsKilledPlayer1 = 0;
        botsKilledPlayer2 = 0;

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
            }
            Platform.runLater(() -> {
                try {
                    if (!isWinner())
                        cardsDistribution();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        t2.start();
    }

    private int takeCardsAnimation(int countCards, int delayTime) {
        for (int i = 0; i < countCards; i++) {
            Timeline tempAnim;
            tempAnim = new Timeline(
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(packCard.translateXProperty(), hm2.get(2).getKey()), new KeyValue(packCard.translateYProperty(), hm2.get(2).getValue()))
            );
            tempAnim.setDelay(Duration.millis(delayTime));
            tempAnim.play();
            delayTime += 300;

            Timeline tempAnimBack = new Timeline(
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(packCard.translateXProperty(), 0), new KeyValue(packCard.translateYProperty(), 0))
            );
            tempAnimBack.setDelay(Duration.millis(delayTime));
            tempAnimBack.play();
        }
        return delayTime;
    }

    private int takeCardsInHandAnimation(int countCards, int delayTime) {
        for (int i = 0; i < countCards; i++) {
            Timeline tempAnim;
            tempAnim = new Timeline(
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(packCard.translateXProperty(), hm2.get(1).getKey()), new KeyValue(packCard.translateYProperty(), hm2.get(1).getValue()))
            );
            tempAnim.setDelay(Duration.millis(delayTime));
            tempAnim.play();
            delayTime += 300;

            Timeline tempAnimBack = new Timeline(
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(packCard.translateXProperty(), 0), new KeyValue(packCard.translateYProperty(), 0))
            );
            tempAnimBack.setDelay(Duration.millis(delayTime));
            tempAnimBack.play();


        }
        if (countCards == 1 && cardsToMove.size() == 2) {
            int a;
            int b;
            if (cardsToMove.get(0).getId().charAt(4) == 1) {
                if (cardsToMove.get(0).getId().charAt(5) == 0)
                    a = 10;
                else a = 1;
            } else a = Integer.parseInt(cardsToMove.get(0).getId().charAt(4) + "");

            if (cardsToMove.get(1).getId().charAt(4) == 1) {
                if (cardsToMove.get(1).getId().charAt(5) == 0)
                    b = 10;
                else b = 1;
            } else b = Integer.parseInt(cardsToMove.get(1).getId().charAt(4) + "");
            if (a > b)
                cardsToMove.remove(0);
            else cardsToMove.remove(1);
        }
        for (int i = 0; i < cardsToMove.size(); i++) {
            if (i <= countCards - 1) {
                Timeline animCard = new Timeline(
                        new KeyFrame(Duration.millis(1), new KeyValue(cardsToMove.get(i).translateYProperty(), 0)),
                        new KeyFrame(Duration.seconds(0.3), new KeyValue(cardsToMove.get(i).translateYProperty(), -300))
                );
                animCard.setDelay(Duration.millis(delayTime));
                animCard.play();
            }
        }
        cardsToMove.clear();
        return delayTime;
    }

    private int takeCardsInHand() throws FileNotFoundException {
        int count = 0;
        if (pack.size() > 0) {
            for (int i = 0; i < myCards.length; i++) {
                if (myCards[i].isEmpty() && pack.size() > 0) {
                    myCards[i] = pack.get(0);
                    setBackgroundOnCard(myCards[i], arrayButtons[i]);
                    count++;
                    pack.remove(0);
                }
            }
        }
        return count;
    }

    private int takeCards() {
        int count = 0;
        if (pack.size() > 0 && cardsPlayer2.size() < 10) {
            while (cardsPlayer2.size() < 10) {
                if (pack.size() == 0)
                    break;
                count++;
                cardsPlayer2.add(pack.get(0));
                pack.remove(0);
            }
        }
        return count;
    }

    private boolean isWinner() throws IOException {
        if (pointsPlayer1 >= 10) {
            if(pointsPlayer1>pointsPlayer2)
                win(1, currentEvent);
            else
                win(2, currentEvent);
            return true;
        } else {
            if (pointsPlayer2 >= 10) {
                win(2, currentEvent);
                return true;
            }
        }
        return false;
    }

    private void setBackgroundOnCard(String imageName, Button b) throws FileNotFoundException {
        String fileName = "src/main/resources/com/project/waragainstbots/images/" + imageName + ".png";
        FileInputStream inp = new FileInputStream(fileName);
        Image im = new Image(inp);
        BackgroundImage bi = new BackgroundImage(im,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background bg = new Background(bi);
        b.setBackground(bg);
    }

    private int valueOfCard(String card) {
        if (card.isEmpty())
            return 0;
        StringBuilder c = new StringBuilder(card);
        c.deleteCharAt(card.length() - 1);
        return Integer.parseInt(c.toString());
    }

    private char suitOfCard(String card) {
        if (card.isEmpty())
            return '0';
        StringBuilder sb = new StringBuilder(card);
        char res = card.charAt(card.length() - 1);
        sb.deleteCharAt(card.length() - 1);
        if (Integer.parseInt(sb.toString()) == 14)
            return res;
        else
            return card.charAt(card.length() - 1) == 'C' || card.charAt(card.length() - 1) == 'B' ? 'R' : 'B';
    }

    private int maxCard() {
        int max = 1;
        for (String card : cardsPlayer2) {
            StringBuilder temp = new StringBuilder(card);
            temp.deleteCharAt(card.length() - 1);
            if (max < Integer.parseInt(temp.toString()))
                max = Integer.parseInt(temp.toString());
        }
        return max;
    }

    private String maxCardWithColor(List<String> cardsPlayer, char color) {
        int max = 0;
        String name = "";
        for (String card : cardsPlayer) {
            StringBuilder temp = new StringBuilder(card);
            char suit = temp.charAt(card.length() - 1);
            if (suitOfCard(temp.toString()) == color) {
                temp.deleteCharAt(card.length() - 1);
                if (max < Integer.parseInt(temp.toString())) {
                    max = Integer.parseInt(temp.toString());
                    name = Integer.toString(max) + suit;
                }
            }
        }
        return name;
    }

    private String minCardWithColor(List<String> cardsPlayer, char color) {
        int min = 15;
        String name = "";
        for (String card : cardsPlayer) {
            StringBuilder temp = new StringBuilder(card);
            char suit = temp.charAt(card.length() - 1);
            if (suitOfCard(temp.toString()) == color) {
                temp.deleteCharAt(card.length() - 1);
                if (min > Integer.parseInt(temp.toString())) {
                    min = Integer.parseInt(temp.toString());
                    name = Integer.toString(min) + suit;
                }
            }
        }
        return name;
    }

    @FXML
    void initialize() {
        setArrayButtons();
    }

}

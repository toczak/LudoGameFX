package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Field;
import model.LastField;
import model.Pawn;
import model.packet.ClientPacket;
import model.packet.ServerPacket;
import model.player.Actions;
import model.player.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class MainWindowController {

    private Player player = new Player();
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private ClientPacket clientPacket;
    private ServerPacket serverPacket;
    private Socket serverSocket;

    Random random = new Random();
    int color;
    ArrayList<Pawn> pawns;
    ArrayList<Circle> circleList;
    ArrayList<Field> fields;
    ArrayList<LastField> lastFields;
    Pawn actualPawn;
    int minPosition, maxPosition;
    boolean isPause;


    public void setPawns(ArrayList<Pawn> pawns) {
        this.pawns = pawns;
    }


    @FXML
    private Pane boardImage;

    @FXML
    private Pane boardPane;

    @FXML
    private ToggleButton startButton;

    @FXML
    private Button buttonDice;

    @FXML
    private Button buttonPause;

    @FXML
    private Label labelNick;

    @FXML
    private Label labelPlayer;

    @FXML
    private Label labelColor;

    @FXML
    private Label labelMove;

    @FXML
    private Label labelDice;


    @FXML
    private void initialize() {

        Platform.runLater(() -> {
            try {
                boardImage.setStyle("-fx-border-color: black;");
                Image image = new Image("plansza.jpg");
                boardImage.getChildren().add(new ImageView(image));
                serverSocket = new Socket("localhost", 5054);
                clientPacket = new ClientPacket(player);
                objectOutputStream = new ObjectOutputStream(serverSocket.getOutputStream());
                objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
                labelNick.setText(player.getNickname());
                clientPacket.getChatText().setNickname(player.getNickname());
                clientPacket.getPlayer().setLastAction(Actions.CONNECTED);
                objectOutputStream.writeObject(clientPacket);
                listenFromServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        initUI();
    }

    @FXML
    private void handleStartButton(MouseEvent event) throws IOException {
        objectOutputStream.reset();

        if (startButton.isSelected()) {
            startButton.setSelected(true);
            clientPacket.getPlayer().setReady(true);
        } else if (!startButton.isSelected()) {
            startButton.setSelected(false);
            clientPacket.getPlayer().setReady(false);
        }
        clientPacket.getPlayer().setLastAction(Actions.READY);
        objectOutputStream.writeObject(clientPacket);
    }

    private void initUI() {
        initFields();
        buttonDice.setDisable(true);
        labelNick.setVisible(true);
        labelMove.setVisible(false);
        startButton.setSelected(false);
        buttonPause.setDisable(true);
    }

    private void initFields() {
        fields = new ArrayList<>();
        fields.add(new Field(251, 590));//niebieski
        fields.add(new Field(251, 535));
        fields.add(new Field(251, 480));
        fields.add(new Field(251, 425));
        fields.add(new Field(251, 370));
        fields.add(new Field(195, 370));
        fields.add(new Field(140, 370));
        fields.add(new Field(85, 370));
        fields.add(new Field(30, 370));
        fields.add(new Field(30, 315));
        fields.add(new Field(30, 260)); //żółty
        fields.add(new Field(85, 260));
        fields.add(new Field(140, 260));
        fields.add(new Field(195, 260));
        fields.add(new Field(251, 260));
        fields.add(new Field(251, 200));
        fields.add(new Field(251, 145));
        fields.add(new Field(251, 90));
        fields.add(new Field(251, 35));
        fields.add(new Field(305, 35));
        fields.add(new Field(360, 30)); //ZIELONY
        fields.add(new Field(360, 90));
        fields.add(new Field(360, 150));
        fields.add(new Field(360, 200));
        fields.add(new Field(360, 255));
        fields.add(new Field(420, 255));
        fields.add(new Field(475, 255));
        fields.add(new Field(531, 255));
        fields.add(new Field(585, 255));
        fields.add(new Field(585, 310));
        fields.add(new Field(585, 365)); //CZERWONY
        fields.add(new Field(531, 365));
        fields.add(new Field(475, 365));
        fields.add(new Field(420, 365));
        fields.add(new Field(360, 365));
        fields.add(new Field(360, 425));
        fields.add(new Field(360, 485));
        fields.add(new Field(360, 535));
        fields.add(new Field(360, 590));
        fields.add(new Field(305, 590));

        lastFields = new ArrayList<>();
        lastFields.add(new LastField(305, 370, 1));
        lastFields.add(new LastField(305, 420, 1));
        lastFields.add(new LastField(305, 475, 1));
        lastFields.add(new LastField(305, 535, 1));

        lastFields.add(new LastField(250, 310, 2));
        lastFields.add(new LastField(195, 310, 2));
        lastFields.add(new LastField(140, 310, 2));
        lastFields.add(new LastField(85, 310, 2));

        lastFields.add(new LastField(305, 255, 3));
        lastFields.add(new LastField(305, 200, 3));
        lastFields.add(new LastField(305, 145, 3));
        lastFields.add(new LastField(305, 90, 3));

        lastFields.add(new LastField(360, 310, 4));
        lastFields.add(new LastField(418, 310, 4));
        lastFields.add(new LastField(475, 310, 4));
        lastFields.add(new LastField(530, 310, 4));

    }

    private void listenFromServer() {
        new Thread(() -> {
            try {
                while (true) {
                    serverPacket = (ServerPacket) objectInputStream.readObject();
                    switch (serverPacket.getAction()) {
                        case (Actions.CHAT):
                            Platform.runLater(() -> {System.out.println(serverPacket.getChatText() + "\n");
                                labelPlayer.setText(serverPacket.getChatText().toString());});
                            break;
                        case (Actions.SET_COLOR):
                            Platform.runLater(() -> setPosition(serverPacket.getValue()));
                            player.setColor(serverPacket.getValue());
                            break;
                        case (Actions.GAME_STARTED):
                            Platform.runLater(this::getInformationAboutStart);
                            break;
                        case (Actions.BEGIN_GAME):
                            startButton.setVisible(false);
                            buttonPause.setDisable(false);
                            Platform.runLater(() -> labelColor.setText(player.getColor()));
                            break;
                        case (Actions.GET_BOARD):
                            Platform.runLater(() -> {
                                setPawns(serverPacket.getPawns());
                                setDefaultPositions();
                                drawPawns();
                            });
                            break;
                        case (Actions.START_ROUND):
                            if (isPause) {
                                endRound();
                                break;
                            }
                            labelMove.setVisible(true);
                            buttonDice.setDisable(false);
                            player.setDrawing(true);
                            break;
                        case (Actions.END_OF_ROUND):
                            labelMove.setVisible(false);
                            player.setDrawing(false);
                            break;
                        case (Actions.UPDATE_BOARD):
                            Platform.runLater(() -> {
                                setPawns(serverPacket.getPawns());
                                getAvailablePawn();
                                drawPawns();
                            });
                            break;
                    }
                }
            } catch (SocketException e) {
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void endRound() throws IOException {
        buttonDice.setDisable(true);
        objectOutputStream.reset();
        clientPacket.getPlayer().setLastAction(Actions.THROW);
        clientPacket.setPawns(pawns);
        objectOutputStream.writeObject(clientPacket);
    }

    private void getAvailablePawn() {
        for (Pawn pawn : pawns) {
            if (pawn.getColor() == color && pawn.isAvailable()) {
                actualPawn = pawn;
            }
        }
    }

    private void setDefaultPositions() {

        for (Pawn pawn : pawns) {
            if (pawn.getColor() == color) {
                actualPawn = pawn;
            }
            if (pawn.getColor() == 1) pawn.setPosition(0);
            if (pawn.getColor() == 2) pawn.setPosition(10);
            if (pawn.getColor() == 3) pawn.setPosition(20);
            if (pawn.getColor() == 4) pawn.setPosition(30);

        }
    }

    private void setPosition(int value) {
        color = value;
        if (value == 1) {
            maxPosition = 39;
            minPosition = 0;
        }
        if (value == 2) {
            maxPosition = 9;
            minPosition = 10;
        }
        if (value == 3) {
            maxPosition = 19;
            minPosition = 20;
        }
        if (value == 4) {
            maxPosition = 29;
            minPosition = 30;
        }
    }

    private void getInformationAboutStart() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja!");
        alert.setHeaderText(null);
        alert.setContentText("Gra rozpocznie się za 10 sekund! Naciśnij START, jeśli tego nie zrobiłeś.");

        alert.show();
    }

    public Color getObjectColor(int id) {
        switch (id) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GOLD;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.RED;
        }
        return Color.BLACK;
    }

    private void drawPawns() {
        boardPane.getChildren().removeAll(boardPane);
        boardPane.getChildren().clear();
        circleList = new ArrayList<>();
        for (Pawn pawn : pawns) {
            Circle circle = new Circle(pawn.getX(), pawn.getY(), 20, getObjectColor(pawn.getColor()));
            circleList.add(circle);
        }
        boardPane.getChildren().addAll(circleList);
    }

    //
    public Player getPlayer() {
        return player;
    }


    public void throwDice(ActionEvent actionEvent) throws IOException {
        int dice = random.nextInt(6) + 1;
        labelDice.setText("Wypadło: " + dice);
        if (actualPawn.getPosition() >= 0 && actualPawn.getPosition() <= maxPosition) {
            if (actualPawn.getPosition() + dice > maxPosition) {
                for (LastField field : lastFields) {
                    if (actualPawn.getColor() == field.getColor() && field.isAvailable()) {
                        actualPawn.setX(field.getX());
                        actualPawn.setY(field.getY());
                        field.setAvailable(false);
                        break;
                    }
                }
                actualPawn.setAvailable(false);
                boolean isEnd = true;
                for (Pawn pawn : pawns) {
                    if (pawn.isAvailable() && pawn.getColor() == color) {
                        actualPawn = pawn;
                        isEnd = false;
                        break;
                    }
                }
                drawPawns();
                if (isEnd) {
                    //Jeśli koniec gry
                    isPause = true;
                    buttonPause.setDisable(true);
                    buttonDice.setDisable(true);
                }

            } else {
                actualPawn.setPosition(actualPawn.getPosition() + dice);
                actualPawn.setX(fields.get(actualPawn.getPosition()).getX());
                actualPawn.setY(fields.get(actualPawn.getPosition()).getY());
                drawPawns();
            }

        } else {
            if (actualPawn.getPosition() + dice > 39) {
                actualPawn.setPosition((actualPawn.getPosition() + dice) - 39);
            } else actualPawn.setPosition(actualPawn.getPosition() + dice);
            actualPawn.setX(fields.get(actualPawn.getPosition()).getX());
            actualPawn.setY(fields.get(actualPawn.getPosition()).getY());
            drawPawns();

        }
        if (dice != 6) {
            endRound();
        }
    }

    public void clickPause(ActionEvent actionEvent) {
        if (!isPause) {
            isPause = true;
            buttonPause.setText("Wyłącz pauzę");
            buttonDice.setDisable(true);
        } else {
            isPause = false;
            buttonPause.setText("PAUZA");
            buttonDice.setDisable(false);

        }
    }
}

/**
 * Video Player java faylda
 * @author Nizomov Asadbek
 * JAVA_FX
 */

import java.io.File;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;

class malumotlar_modeli {

    SimpleStringProperty yoli;
    SimpleStringProperty vaqti;

    public malumotlar_modeli(String yoli, String vaqti) {
        this.yoli = new SimpleStringProperty(yoli);
        this.vaqti = new SimpleStringProperty(vaqti);
    }

    public String getYoli() {
        return yoli.get();
    }

    public void setYoli(String yoli) {
        this.yoli.set(yoli);
    }

    public String getVaqti() {
        return vaqti.get();
    }

    public void setVaqti(String vaqti) {
        this.vaqti.set(vaqti);
    }

}

public class VideoPlayer extends Application {

    enum xolat {
        STOP, PAUSE, REC, PLAY;
        private xolat status;

        public void setStatus(xolat s) {
            status = s;
        }

        public xolat getStatus() {
            return status;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public String _login = "";
    public String _parol = "";
    private Media media;
    private MediaPlayer mp;
    private MediaView mv = new MediaView(mp);
    private String path = "D:" + File.separator;
    private FileChooser openDialog = new FileChooser();
    private FileChooser.ExtensionFilter filterMovie = new FileChooser.ExtensionFilter("Video media",
            "*.mp4", "*.vob", "*.flv");
    private FileChooser.ExtensionFilter filterMusic = new FileChooser.ExtensionFilter("Ovozli media", "*.mp3",
            "*.wav");
    private Slider volume = new Slider(0, 1, 1);
    private Slider timeSlider = new Slider();
    private GridPane setka = new GridPane();
    private Button stopPlayButton = new Button();
    private Image playImg = new Image(getClass().getResourceAsStream("playbutton.png"));
    private Image pauseImg = new Image(getClass().getResourceAsStream("pausebutton.png"));
    private Image openImg = new Image(VideoPlayer.class.getResourceAsStream("open.png"));
    private Image muteImg = new Image(getClass().getResourceAsStream("mute.png"));
    private Button open = new Button("Ochish", new ImageView(openImg));
    private Button mute = new Button("", new ImageView(muteImg));
    private File f;
    private Duration d;
    private double sekund = 0;
    private Button MediadanChiqish = new Button("null");
    private boolean maxvalue = true;
    private Button rate0 = new Button("\u2022");
    private Button rate05 = new Button("<<<<");
    private Button rate075 = new Button("<<");
    private Button rate15 = new Button(">>");
    private Button rate2 = new Button(">>>>");
    private double[] rates = {};
    private int rateCount = 2;
    private MenuBar bar = new MenuBar();
    private Menu fayl = new Menu("Fayl");
    private Menu haqida = new Menu("Haqida");
    private Menu yordam = new Menu("Yordam");
    private Menu sozlamalar = new Menu("Sozlamalar");
    private Menu ovozli_malumotlar = new Menu("Media");
    private int StopPlayQueue = 1;
    private Pane gr = new Pane(setka);
    private double currentBalans = 0;
    private File MOfayl = new File("C:\\Windows\\malumotlarbazasi.mpdll");
    public Statement stmt = null;
    public ResultSet rs = null;
    private List<String> yoli = new ArrayList<String>();
    private List<String> vaqti = new ArrayList<String>();
    public Stage primaryStage;
    private boolean is_clicked_stop_button = false;

    public void tozala() {
        yoli = new ArrayList<>();
        vaqti = new ArrayList<>();
    }

    public void read_values_from_base() throws SQLException {
        while (rs.next()) {
            yoli.add(rs.getString("yoli"));
            vaqti.add(rs.getString("vaqti"));
        }
    }

    private void scaleXY(Node n, double x, double y) {
        n.setScaleX(x);
        n.setScaleY(y);
    }

    private void Disable(boolean x) {
        volume.setDisable(x);
        timeSlider.setDisable(x);
        stopPlayButton.setDisable(x);
        mute.setDisable(x);
    }

    private void boshla() {
//        buttonEffect();
        Disable(true);
        openDialog.setTitle("Media fayllar");
        openDialog.setInitialDirectory(new File(path));
        openDialog.getExtensionFilters().addAll(filterMovie, filterMusic);
    }

    public void kirishOynasi() {
        Stage sahna = new Stage();

        Button btn = new Button("OK");
        Hyperlink havola = new Hyperlink("Registratsiya");
        havola.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _login = JOptionPane.showInputDialog(null, "Loginni kiriting");
                _parol = JOptionPane.showInputDialog(null, "Parolni kiriting");

                File f = new File("C:\\Windows\\bkrmivro.prop");

                try {
                    FileWriter fw = new FileWriter(f);
                    BufferedWriter buffer = new BufferedWriter(fw);
                    buffer.write(_login);
                    buffer.newLine();
                    buffer.write(_parol);
                    buffer.flush();
                    buffer.close();
                    primaryStage.setOpacity(1);
                    havola.setVisible(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                btn.setDisable(false);
                sahna.close();
                kirishOynasi();
            }

        });

        GridPane setka = new GridPane();
        setka.setHgap(10);
        setka.setVgap(5);

        TextField login = new TextField();
        PasswordField parol = new PasswordField();

        btn.setStyle("-fx-background-color: lightblue; -fx-background-radius: 0");

        File f = new File("C:\\Windows\\bkrmivro.prop");
        havola.setVisible(!f.exists());

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (f.exists()) {
                    try {
                        FileReader fr = new FileReader(f);
                        BufferedReader buffer = new BufferedReader(fr);
                        _login = buffer.readLine();
                        _parol = buffer.readLine();
                        buffer.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(VideoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (login.getText().equals(_login) && parol.getText().equals(_parol)) {
                        JOptionPane.showMessageDialog(null, "Admin panelga xush kelibsiz");
                        sahna.close();
                        AdminOynasi();
                    } else {
                        JOptionPane.showMessageDialog(null, "Login yoki parol noto'g'ri",
                                "Xatolik 101", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    try {
                        f.createNewFile();
                        JOptionPane.showMessageDialog(null, "Regitratsiyadan o'ting");
                        primaryStage.setOpacity(0);
                        havola.setFocusTraversable(true);
                        btn.setDisable(true);
                    } catch (IOException ex) {
                        Logger.getLogger(VideoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        setka.setPadding(new Insets(10));
        setka.setAlignment(Pos.CENTER);
        setka.add(new Label("Login"), 0, 0);
        setka.add(new Label("Parol"), 0, 1);
        setka.add(login, 1, 0);
        setka.add(parol, 1, 1);
        setka.add(btn, 2, 2);
        setka.add(havola, 1, 3, 2, 1);

        Scene scene = new Scene(setka);
        sahna.setTitle("Kirish");
        sahna.setScene(scene);
        sahna.show();
    }

    public void AdminOynasi() {
        ListView lv = new ListView(FXCollections.observableArrayList(yoli));
        Label xabar = new Label("Tanlanmagan");
        VBox vb = new VBox(10, lv, xabar);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);

        lv.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                try {
                    xabar.setText(vaqti.get(newValue.intValue()));
                } catch (IndexOutOfBoundsException e) {
                    xabar.setText("Error 101");
                }
            }
        });

        Scene admin_oynasi_show = new Scene(vb);
        Stage sahna = new Stage();
//        sahna.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                tozala();
//            }
//        });
        sahna.setScene(admin_oynasi_show);
        sahna.setTitle("Admin bo'lim");
        sahna.setResizable(false);
        sahna.show();
    }

    @Override
    public void start(Stage asosiy_sahna) {
        boshla();
        primaryStage = asosiy_sahna;
        volume.setBlockIncrement(0.05);
        volume.setMajorTickUnit(0.5);
        volume.setShowTickMarks(true);
        volume.setMinorTickCount(4);
        setka.setPadding(new Insets(0, 0, 5, 0));
        setka.setAlignment(Pos.TOP_CENTER);
        setka.setHgap(10);
        setka.setVgap(10);
        stopPlayButton.setGraphic(new ImageView(pauseImg));
        open.setGraphicTextGap(5);

        //Hodisalar:
        stopPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (StopPlayQueue == 1) {
                    mp.pause();
                    stopPlayButton.setGraphic(new ImageView(playImg));
                    StopPlayQueue = 2;
                } else if (StopPlayQueue == 2) {
                    mp.play();
                    stopPlayButton.setGraphic(new ImageView(pauseImg));
                    StopPlayQueue = 1;
                }
            }
        });
        rate05.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    mp.setRate(0.5);
                }

            }
        });
        rate075.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    mp.setRate(.75);
                }
            }
        });
        rate0.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    mp.setRate(1);
                }
            }
        });
        rate15.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    mp.setRate(1.5);
                }
            }
        });
        rate2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    mp.setRate(2);
                }
            }
        });

        mute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp.isMute()) {
                    mp.setMute(false);
                } else {
                    mp.setMute(true);
                }
            }
        });

        MediadanChiqish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                media = null;
                mp = new MediaPlayer(media);
                mv.setMediaPlayer(null);
            }
        });

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    mp.stop();
                }
                media = null;
                mp = null;

                mv.setMediaPlayer(null);

                f = openDialog.showOpenDialog(null);

                path = f.toURI().toString();
                if (path != null) {
                    media = new Media(path);
                    mp = new MediaPlayer(media);
                    mv.setMediaPlayer(mp);
                    mp.setAutoPlay(true);
                    timeSlider.setMin(0);
                    timeSlider.setMax(100);
                    timeSlider.setValue(0);
                    Disable(false);
                }

                for (int i = 0; i < yoli.size(); i++) {

                    if (f.toString().equals(yoli.get(i))) {
                        return;
                    }
                }

                yoli.add(f.toString());

            }
        });

        Timeline tl = new Timeline();
        tl.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    timeSlider.setValue((mp.getCurrentTime().toSeconds() * 100) / mp.getCycleDuration().toSeconds());
                }
            }
        });
        timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tl.pause();
            }
        });

        timeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tl.play();
                mp.seek(new Duration((timeSlider.getValue() * mp.getCycleDuration().toSeconds()) * 10));
            }
        });

        tl.getKeyFrames().add(kf);
        tl.play();

        volume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (mp != null) {
                    mp.setVolume(newValue.doubleValue());
                }
            }
        });

        //Menular:
        mv.setStyle("-fx-background-color: linear-gradient(to right bottom, #C02425, #F0CB35)");
        mv.setSmooth(true);
        Stage st = new Stage();
        WebEngine engine;
        WebView wv = new WebView();
        engine = wv.getEngine();
        engine.load("https://t.me/EngineerOfJava");
        Group ildiz = new Group(wv);
        Scene epizod = new Scene(ildiz);
        st.setTitle("Yordam ko'rsatish markazi");
        st.setScene(epizod);

        MenuItem stop = new MenuItem("Stop");
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mp != null) {
                    mp.stop();
                    StopPlayQueue = 2;
                    stopPlayButton.setGraphic(new ImageView(playImg));
                }
            }
        });

        MenuItem balans = new MenuItem("Balans");

        balans.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                balancemp();
            }
        });

        sozlamalar.getItems().addAll(balans, stop);

        MenuItem support = new MenuItem("Yordam ko'rsatish markazi");
        support.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                st.show();
            }
        });

        MenuItem admin_panel = new MenuItem("Admin panel");
        admin_panel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                kirishOynasi();
            }
        });

        yordam.getItems().addAll(support, new SeparatorMenuItem(), admin_panel);

        MenuItem DasturchiHaqida = new MenuItem("Dasturchi haqida");
        DasturchiHaqida.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showmessage("Dasturchi: Nizomov Asadbek");
            }
        });

        MenuItem dasturHaqida = new MenuItem("Dastur haqida");
        dasturHaqida.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showmessage("Media player dasturining 2.1.5 versiyasi");
            }
        });

        haqida.getItems().addAll(DasturchiHaqida, dasturHaqida);

        MenuItem ochish = new MenuItem("Ochish");
        MenuItem chiqish = new MenuItem("Chiqish");
        ochish.setOnAction(open.getOnAction());
        chiqish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        fayl.getItems().addAll(ochish, new SeparatorMenuItem(), chiqish);

        MenuItem karaoke = new MenuItem("Karaoke");
        MenuItem skachat = new MenuItem("Online media");
        ovozli_malumotlar.getItems().addAll(karaoke, skachat);

        bar.getMenus().addAll(fayl, haqida, yordam, sozlamalar);

        HBox rateHB = new HBox(5, rate05, rate075, rate0, rate15, rate2);
        HBox panels = new HBox(5, open, stopPlayButton, volume, mute);
        rateHB.setAlignment(Pos.CENTER_LEFT);
        rateHB.setPadding(new Insets(1));
        mv.setStyle("-fx-background-color: black");
        mv.setFitHeight(310);
        mv.setFitWidth(555);
        rate2.setLayoutX(-80);
        setka.add(bar, 0, 0);
        setka.add(mv, 0, 1, 7, 7);
        setka.add(panels, 0, 8);
        setka.add(rateHB, 0, 12);
        setka.add(new Separator(), 0, 11, 8, 1);
        setka.add(new Separator(), 0, 9, 7, 1);
        setka.add(timeSlider, 0, 10, 7, 1);

        gr.setPadding(new Insets(2));

        Scene scene = new Scene(gr, 555, 468);
        primaryStage.setFullScreenExitHint("Chiqish uchun ESC ni bosing");
        primaryStage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        double widthmv = mv.getFitWidth();
        double heightmv = mv.getFitHeight();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if (e.getCode().equals(e.getCode().L)) {
                    primaryStage.setFullScreen(true);
                    mv.setFitWidth(primaryStage.getWidth() + 80);
                    mv.setFitHeight(primaryStage.getHeight() + 33);
                }
                if (e.getCode().equals(e.getCode().ESCAPE)) {
                    primaryStage.setFullScreen(false);
                    mv.setFitWidth(widthmv);
                    mv.setFitHeight(heightmv);
                }
//                System.out.print(e.getText());
            }
        });

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                primaryStage.setTitle(newValue.doubleValue() + "x" + scene.getHeight());
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                primaryStage.setTitle(scene.getWidth() + "x" + newValue.doubleValue());
            }
        });

        scene.getStylesheets().add("Stil.css");
        primaryStage.setTitle("Video player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    protected void balancemp() {
        Stage st = new Stage();
        Button standartbalance = new Button("Standart balans");
        standartbalance.setEffect(new DropShadow(10, 4, 4, Color.GREY));

        standartbalance.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                standartbalance.setEffect(null);
            }
        });

        standartbalance.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                standartbalance.setEffect(new DropShadow());
            }
        });

        standartbalance.setStyle("-fx-background-color: lightblue");
        Slider slider = new Slider(-1, 1, currentBalans);
        standartbalance.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mp.setBalance(0);
                currentBalans = 0;
                slider.setValue(0);
            }
        });
        st.setTitle("Balans");
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (mp != null) {
                    mp.setBalance(newValue.doubleValue());
                    currentBalans = newValue.doubleValue();
                }
            }
        });

        VBox vb = new VBox(10, standartbalance, slider);
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setPadding(new Insets(5));

        Scene epizod = new Scene(vb, 120, 60);
        st.setScene(epizod);
        st.show();
    }

    public void showmessage(String text) {
        Stage msgSahna = new Stage(StageStyle.DECORATED);
        msgSahna.setTitle("Xabar!");
        Label xabar = LabelBuilder.create().translateX(60).font(new Font("Verdana", 18))
                .effect(new DropShadow())
                .text(text)
                .wrapText(true)
                .build();
        Button ok = ButtonBuilder.create().text("OK").translateX(180).translateY(30)
                .onAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        msgSahna.close();
                    }
                })
                .build();
        VBox vb = new VBox(15, xabar, ok);

        vb.setPadding(new Insets(5));
        Scene msgscene = new Scene(vb, 360, 120);
        msgSahna.setScene(msgscene);

        msgSahna.show();
    }

    public void show_karaoke_stage() {

    }
    public void show_download_media_stage(){

    }
}

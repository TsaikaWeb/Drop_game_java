package com.Tsaika;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameWindow extends JFrame {


    private static  GameWindow game_window;
    private static long last_frame_time;
    private static Image background;
    private static Image gameOver;
    private static Image drop;
    private static Image restart;
    private static float drop_left= 200;
    private static float drop_top = 200;

    private static float drop_v=200;
    private static int score;
    private static boolean end;
    private static float drop_width = 105;
    private static float drop_height = 110;
    private static boolean pause = false;
    private static float drop_speed_save;

    private static double mousecordX = 250;
    private static double mousecordY = 250;
    private static int direction = -1;

    private static Entry nameEntry;
    private static Database db;
    private static boolean isRecorded = false;
    private static boolean drawRecords = false;
    private static ArrayList<String> recordList = new ArrayList<String>();


    public static void main(String[] args) throws IOException {
        db = new Database ("String url = \"jdbc:mysql://localhost/droptsaika?useLegacyDatetimeCode=false&serverTimezone=Europe/Helsinki\";", "root","");
        db.init();

        background = ImageIO.read(GameWindow.class.getResourceAsStream("background.jpg"));
        gameOver = ImageIO.read(GameWindow.class.getResourceAsStream("gameOver.png"));
        drop = ImageIO.read(GameWindow.class.getResourceAsStream("drop.png"))
                .getScaledInstance((int) drop_width,(int) drop_height, Image.SCALE_DEFAULT);;
        restart = ImageIO.read(GameWindow.class.getResourceAsStream("reset.png"))
                .getScaledInstance(200,130, Image.SCALE_DEFAULT);;
        game_window = new GameWindow();//Создание нового окна
        game_window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//Закрытие программы
        game_window.setLocation(200, 100);//расположение мыши
        game_window.setSize(906, 478);//размер окна
        pause= false;
        last_frame_time = System.nanoTime();
        GameField game_field = new GameField();
        onDirection();
        game_field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3){
                    if(pause) {
                        pause = false;
                        drop_v = drop_speed_save;

                        try {
                            Robot r = new Robot();
                            r.mouseMove((int)mousecordX,(int)mousecordY);
                        }
                        catch (AWTException ee){

                        }
                    }
                    else{
                        drop_speed_save= drop_v;
                        drop_v= 0;

                        mousecordX = MouseInfo.getPointerInfo().getLocation().getX();
                        mousecordY = MouseInfo.getPointerInfo().getLocation().getY();

                        pause = true;
                    }

                }

                if (pause) return;



                int x = e.getX();
                int y = e.getY();



                float drop_right = drop_left + drop.getWidth(null);
                float drop_bottom = drop_top + drop.getHeight(null);
                boolean is_drop = x >= drop_left && x <= drop_right && y >=drop_top && y <= drop_bottom;
                if(is_drop) {
                    if(drop_height > 30 && drop_width > 50) {
                        drop_width = drop_width -1;
                        drop_height = drop_height -2;
                        try{
                            dropResize();
                        }
                        catch (IOException ioe){

                        }

                    }



                    drop_top = -150;
                    drop_left = (int) (Math.random() * (game_field.getWidth() - drop.getWidth(null)));
                    drop_v = drop_v + 20;
                    score++;
                    onDirection();
                    game_window.setTitle("Score: "+ score);
                }
                if (end){
                    boolean isRestart = x>=250 && x <=250 + restart.getWidth(null)
                            && y >= 250 && y <=250 + restart.getHeight(null);
                    if (isRestart){
                        end = false;
                        score = 0;
                        game_window.setTitle("Score"+ score);
                        drop_top=-150;
                        drop_left = (int) (Math.random() * (game_field.getWidth() - drop.getWidth(null)));
                        drop_v=200;
                        drop_width= 100;
                        drop_height= 152;
                        isRecorded = false;
                        drawRecords = true;

                    }

                }
            }
        });
        nameEntry = new Entry();
        game_window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                nameEntry.keyPress(e);
                if (nameEntry.isActive && !isRecorded){
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        db.addRecord(nameEntry.text, score);
                        isRecorded = true;

                    }

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });{


        }

        game_window.add(game_field);
        game_window.setResizable(false);
        game_window.setVisible(true);



    }
    private static void dropResize() throws IOException {
        drop = ImageIO.read(GameWindow.class.getResourceAsStream("drop.png"))
                .getScaledInstance((int) drop_width,(int) drop_height, Image.SCALE_DEFAULT);;
    }

    private static int  onDirection(){
        int rand = (int)(Math.random()*2+1);
        if (rand == 2) direction = 1;
        else direction = -1;
        System.out.println(direction);

        return direction;
    }

    private static void onRepaint(Graphics g){
        long current_time = System.nanoTime();
        float delta_time = (current_time - last_frame_time)*0.000000001f;
        last_frame_time = current_time;
        drop_top = drop_top +drop_v * delta_time ;
        g.drawImage(background, 0, 0, null);
        g.drawImage(drop, (int)drop_left, (int)drop_top,null);
        drop_left = drop_left +(direction*drop_v) * delta_time;
       /* drop_top = drop_top + drop_v  * delta_time;*/


        if (drop_top > game_window.getHeight()) {
            g.drawImage(gameOver, 280, 120, null);
            g.drawImage(restart, 320, 280, null);
            end = true;
        }
            //g.drawImage(gameOver, 280, 120, null);
        if(drop_left <=0.0 ||drop_left + drop_width > game_window.getWidth()){
            if(direction == -1) direction = 1;
            else direction = -1;

        }
        if (drawRecords){
        }

        nameEntry.isActive = end;
        nameEntry.update(g);
    }
    private static class GameField extends JPanel{
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            onRepaint(g);
            repaint();
        }
    }
   /* String url = "jdbc:mysql://localhost/gamedrop?useLegacyDatetimeCode=false&serverTimezone=Europe/Helsinki";*/
}

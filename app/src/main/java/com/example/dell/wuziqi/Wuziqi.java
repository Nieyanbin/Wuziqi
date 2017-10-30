package com.example.dell.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/10/28.
 * 作者 聂雁宾
 */

public class Wuziqi extends View{
    private int mPanelWidth;
    private float mLineHeight;
   private int MAX_LINE=12;
    private int MAX_COUNT_IN_LINE=5;
   private Paint mpaint=new Paint();
   private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
   private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //白棋先手，或者是轮到白棋了
    private boolean mIsWhite=true;
  private List<Point> mWhiteArray=new ArrayList<>();
    private List<Point> mBlackArray=new ArrayList<>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;



    public Wuziqi(Context context) {
        this(context,null);
    }

    public Wuziqi(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Wuziqi(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0x44ff0000);
        //初始化
        init();

    }
    private void init() {
        mpaint.setColor(0x88000000);
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setStyle(Paint.Style.STROKE);
        //获取黑白棋
        mWhitePiece= BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece= BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
    }

    //测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        //高
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int witch = Math.min(widthSize, heightSize);

        if(widthMode==MeasureSpec.UNSPECIFIED){
               witch = heightSize;
        }else if(heightMode==MeasureSpec.UNSPECIFIED){
            witch = widthSize;
        }
        setMeasuredDimension(witch,witch);
    }
//对关于尺寸的成员变量进行赋值
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
        int PieceWidth = (int) (mLineHeight*ratioPieceOfLineHeight);
        //白棋
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece,PieceWidth,PieceWidth,false);
        //黑棋
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,PieceWidth,PieceWidth,false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mIsGameOver){
      return false;
        }

        int action = event.getAction();
        if(action==MotionEvent.ACTION_DOWN){
            int x= (int) event.getX();
            int y= (int) event.getY();
            Point p=getValidPoint(x,y);

            if(mWhiteArray.contains(p)||mBlackArray.contains(p)){
           return false;
            }

            if(mIsWhite){
                mWhiteArray.add(p);
            }else{
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite=!mIsWhite;
        }
        return true;

    }

    private Point getValidPoint(int x, int y) {

        return  new Point((int) (x/mLineHeight) ,(int)(y/mLineHeight));
    }

    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        //绘制棋子
        drawPiece(canvas);
        //五子连的逻辑判断
        checkGameOver();
    }

    private void checkGameOver() {
        //白棋赢
     boolean whiteWin= checkFiveInLine(mWhiteArray);
//黑棋赢
        boolean blackWin= checkFiveInLine(mBlackArray);
        //判断有一个赢就游戏Over
        if(whiteWin||blackWin){
            mIsGameOver=true;
            mIsWhiteWinner = whiteWin;
            String text=mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
            start();
        }
    }
public void start(){
    mWhiteArray.clear();
    mBlackArray.clear();
    mIsGameOver=false;
    mIsWhiteWinner=false;
    invalidate();
}
    private boolean checkFiveInLine(List<Point> mWhiteArray) {

        for (Point p:mWhiteArray) {
           int x=p.x;
            int y=p.y;
           boolean win= checkHorizontal(x,y,mWhiteArray);
        if(win){return true;}
             win= checkVertical(x,y,mWhiteArray);
            if(win){return true;}
             win= checkRight(x,y,mWhiteArray);
            if(win){return true;}
             win= checkLeft(x,y,mWhiteArray);
            if(win){return true;}

        }
        return false;
    }
//判断棋子是不是横向的连续的五个
    private boolean checkHorizontal(int x, int y, List<Point> mWhiteArray) {
       int count=1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
              if(mWhiteArray.contains(new Point(x-i,y))){
                count++;
            }else{
                  break;
              }
        }
        if(count==MAX_COUNT_IN_LINE){
     return  true;
        }
        //右
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if(mWhiteArray.contains(new Point(x+i,y))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return  true;
        }
        return false;
    }

    //判断棋子是不是纵向的连续的五个
    private boolean checkVertical(int x, int y, List<Point> mWhiteArray) {
        int count=1;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if(mWhiteArray.contains(new Point(x,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return  true;
        }
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if(mWhiteArray.contains(new Point(x,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return  true;
        }
        return false;
    }

    //判断棋子是不是左斜的连续的五个
    private boolean checkLeft(int x, int y, List<Point> mWhiteArray) {
        int count=1;
        //左上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if(mWhiteArray.contains(new Point(x-i,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return  true;
        }
        //左下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if(mWhiteArray.contains(new Point(x+i,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return  true;
        }
        return false;
    }

    //判断棋子是不是右斜的连续的五个
    private boolean checkRight(int x, int y, List<Point> mWhiteArray) {
        int count=1;
        //右上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if(mWhiteArray.contains(new Point(x-i,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return  true;
        }
        //右下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if(mWhiteArray.contains(new Point(x+i,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return  true;
        }
        return false;
    }

    private void drawPiece(Canvas canvas) {
        //绘制白棋
        for (int i = 0, n= mWhiteArray.size();i < n; i++) {
         Point whitePoint =mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);

        }
        //绘制黑棋
        for (int i = 0, n= mBlackArray.size();i < n; i++) {
            Point blackPoint =mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);

        }
    }

    private void drawBoard(Canvas canvas) {
        int w=mPanelWidth;
        float lineHeight=mLineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            //设置起始的X坐标
            int startX= (int) (lineHeight/2);
            //设置结束的X坐标
            int endX= (int) (w-lineHeight/2);
            int y= (int) ((0.5+i)*lineHeight);
            //画横向
            canvas.drawLine(startX,y,endX,y,mpaint);
            //画纵向
            canvas.drawLine(y,startX,y,endX,mpaint);
        }
    }
}

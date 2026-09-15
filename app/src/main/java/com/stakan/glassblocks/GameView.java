package com.stakan.glassblocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class GameView extends View {
    private static final int COLS=10, ROWS=20;
    private static final int[][][] PIECES={
        {{0,1},{1,1},{2,1},{3,1}}, {{0,0},{0,1},{1,1},{2,1}},
        {{2,0},{0,1},{1,1},{2,1}}, {{1,0},{2,0},{1,1},{2,1}},
        {{1,0},{2,0},{0,1},{1,1}}, {{1,0},{0,1},{1,1},{2,1}},
        {{0,0},{1,0},{1,1},{2,1}}
    };
    private final int[] colors={0xff00f5ff,0xff3478ff,0xffff8a00,0xffffe600,0xff39ff88,0xffc83cff,0xffff3d81};
    private final int[][] board=new int[ROWS][COLS];
    private final Paint p=new Paint(3); private final Random random=new Random();
    private int type, nextType=-1, rotation, px, py, score, best, level=1, lines, pieceGeneration;
    private int touchGeneration=-1;
    private boolean gameOver, dragged; private long lastDrop; private float downX,downY,lastMoveX;
    private final SharedPreferences prefs;

    public GameView(Context c){ super(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null); prefs=c.getSharedPreferences("game",0); best=prefs.getInt("best",0); spawn(); }

    private void spawn(){ if(nextType<0)nextType=random.nextInt(PIECES.length);type=nextType;nextType=random.nextInt(PIECES.length);pieceGeneration++;rotation=0;px=3;py=-1;if(collides(px,py,rotation)){gameOver=true;if(score>best){best=score;prefs.edit().putInt("best",best).apply();}} }
    private Point cell(int[] base,int r){int x=base[0],y=base[1];for(int i=0;i<r;i++){int n=x;x=3-y;y=n;}return new Point(x,y);}
    private boolean collides(int x,int y,int r){ for(int[] b:PIECES[type]){Point q=cell(b,r);int bx=x+q.x,by=y+q.y;if(bx<0||bx>=COLS||by>=ROWS||(by>=0&&board[by][bx]!=0))return true;}return false; }
    private void lock(){ for(int[] b:PIECES[type]){Point q=cell(b,rotation);int x=px+q.x,y=py+q.y;if(y>=0)board[y][x]=type+1;} clearLines();spawn(); }
    private void clearLines(){int cleared=0;for(int y=ROWS-1;y>=0;y--){boolean full=true;for(int x=0;x<COLS;x++)if(board[y][x]==0){full=false;break;}if(full){cleared++;for(int yy=y;yy>0;yy--)System.arraycopy(board[yy-1],0,board[yy],0,COLS);board[0]=new int[COLS];y++;}}if(cleared>0){int[] points={0,100,300,500,800};score+=points[cleared]*level;lines+=cleared;level=1+lines/10;} }
    private void drop(){if(!collides(px,py+1,rotation))py++;else lock();}
    private void hardDrop(){while(!collides(px,py+1,rotation)){py++;score+=2;}lock();}
    private void reset(){for(int y=0;y<ROWS;y++)board[y]=new int[COLS];score=lines=0;level=1;gameOver=false;spawn();}

    @Override protected void onDraw(Canvas c){super.onDraw(c);long now=SystemClock.uptimeMillis();if(!gameOver&&now-lastDrop>Math.max(110,700-level*45)){drop();lastDrop=now;}drawGame(c);postInvalidateDelayed(16);}
    private void text(Canvas c,String s,float size,float x,float y,int align){p.setTypeface(Typeface.create("sans",Typeface.BOLD));p.setTextSize(size);p.setTextAlign(align==0?Paint.Align.LEFT:align==2?Paint.Align.RIGHT:Paint.Align.CENTER);p.setColor(0xffe9fbff);p.setShadowLayer(12,0,0,0xff00d9ff);c.drawText(s,x,y,p);p.clearShadowLayer();}
    private void drawGame(Canvas c){float top=getHeight()*.12f, available=getHeight()*.76f;float cell=Math.min(getWidth()*.78f/COLS,available/ROWS);float left=(getWidth()-cell*COLS)/2;
        text(c,"GLASS BLOCKS",getWidth()*.075f,getWidth()/2f,getHeight()*.075f,1);text(c,"SCORE  "+score,getWidth()*.036f,left,top-16,0);text(c,"BEST  "+best,getWidth()*.036f,left+cell*COLS,top-16,2);
        p.setStyle(Paint.Style.FILL);p.setColor(0xaa090d25);p.setShadowLayer(20,0,0,0xff006eff);c.drawRoundRect(left-7,top-7,left+cell*COLS+7,top+cell*ROWS+7,14,14,p);p.clearShadowLayer();
        for(int y=0;y<ROWS;y++)for(int x=0;x<COLS;x++){if(board[y][x]!=0)block(c,left+x*cell,top+y*cell,cell,colors[board[y][x]-1]);else{p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);p.setColor(0x3328b8ff);c.drawRect(left+x*cell,top+y*cell,left+(x+1)*cell,top+(y+1)*cell,p);}}
        if(!gameOver)for(int[] b:PIECES[type]){Point q=cell(b,rotation);if(py+q.y>=0)block(c,left+(px+q.x)*cell,top+(py+q.y)*cell,cell,colors[type]);}
        drawNext(c,left+cell*COLS-4,top+8,cell*.46f);
        text(c,"LEVEL "+level+"   •   LINES "+lines,getWidth()*.035f,getWidth()/2f,top+cell*ROWS+42,1);text(c,"TAP = ROTATE  ↻    •    SWIPE = MOVE / DROP",getWidth()*.026f,getWidth()/2f,top+cell*ROWS+76,1);
        if(gameOver){p.setColor(0xdd050816);p.setStyle(Paint.Style.FILL);c.drawRoundRect(left+18,top+cell*7,left+cell*COLS-18,top+cell*13,24,24,p);text(c,"GAME OVER",getWidth()*.065f,getWidth()/2f,top+cell*9.2f,1);text(c,"TAP TO RESTART",getWidth()*.033f,getWidth()/2f,top+cell*11.1f,1);}}
    private void block(Canvas c,float x,float y,float s,int color){p.setStyle(Paint.Style.FILL);p.setColor(color);p.setShadowLayer(s*.35f,0,0,color);c.drawRoundRect(x+3,y+3,x+s-3,y+s-3,s*.16f,s*.16f,p);p.clearShadowLayer();p.setShader(new LinearGradient(x,y,x+s,y+s,0x99ffffff,0x11000000,Shader.TileMode.CLAMP));c.drawRoundRect(x+5,y+5,x+s-5,y+s-5,s*.13f,s*.13f,p);p.setShader(null);}
    private void drawNext(Canvas c,float right,float top,float s){float w=s*4.7f,h=s*3.8f,left=right-w;p.setStyle(Paint.Style.FILL);p.setColor(0xcc050816);c.drawRoundRect(left,top,right,top+h,12,12,p);text(c,"NEXT",s*.72f,(left+right)/2,top+s*.86f,1);for(int[] b:PIECES[nextType]){float x=left+s*.45f+b[0]*s,y=top+s*1.05f+b[1]*s;block(c,x,y,s,colors[nextType]);}}
    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()==MotionEvent.ACTION_DOWN){downX=lastMoveX=e.getX();downY=e.getY();dragged=false;touchGeneration=pieceGeneration;return true;}
        if(e.getAction()==MotionEvent.ACTION_MOVE&&!gameOver){
            if(touchGeneration!=pieceGeneration)return true;
            float step=Math.max(24,getWidth()/14f),dx=e.getX()-lastMoveX;
            while(Math.abs(dx)>=step){int direction=dx>0?1:-1,nx=px+direction;if(!collides(nx,py,rotation))px=nx;lastMoveX+=direction*step;dx=e.getX()-lastMoveX;dragged=true;}
            if(e.getY()-downY>70){drop();downY=e.getY();dragged=true;}
            invalidate();return true;
        }
        if(e.getAction()==MotionEvent.ACTION_UP){
            if(gameOver){reset();invalidate();return true;}
            if(touchGeneration!=pieceGeneration){touchGeneration=-1;return true;}
            float dy=e.getY()-downY;
            if(!dragged&&Math.abs(e.getX()-downX)<45&&Math.abs(dy)<45){int nr=(rotation+1)%4;if(!collides(px,py,nr))rotation=nr;}
            else if(dy>110)hardDrop();
            touchGeneration=-1;invalidate();return true;
        }
        return true;
    }
}

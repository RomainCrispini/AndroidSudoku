package com.romain.app11sudoku;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;


public class GameView extends View implements GestureDetector.OnGestureListener {

    // Ajout de l'attribut de type Paint : un stylo qui va dessiner la grille
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GestureDetector gestureDetector;

    private GameBoard gameBoard = GameBoard.getGameBoard( GameLevel.MEDIUM );

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;
    private float buttonWidth;
    private float buttonRadius;
    private float buttonMargin;

    private Bitmap eraserBitmap;
    private Bitmap pencilBitmap;
    private Bitmap littlePencilBitmap;

    public GameView(Context context) {
        super(context);
        this.init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        this.init();
    }

    private void init(){
        // Activation du gestureDetector
        gestureDetector = new GestureDetector( getContext(), this );

    }

    // --- Events handlers ---

    // Override from View
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    // Override from OnGestureDectector
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) { // e (param) est l'objet d'événement
        RectF rectF;

        // --- Check grid cell click ---
        if ( e.getY() < gridWidth ) {
            int cellX = (int)( e.getX() / cellWidth );
            int cellY = (int)( e.getY() / cellWidth );

            gameBoard.currentCellX = cellX;
            gameBoard.currentCellY = cellY;
            postInvalidate();
            return true;
        }

        float buttonLeft = buttonMargin;
        float buttonTop = 9 * cellWidth + gridSeparatorSize / 2;

        if ( gameBoard.currentCellX != -1 && gameBoard.currentCellY != -1 ) {

            // --- Check number buttons ---
            for (int i = 1; i <= 9; i++) {
                rectF = new RectF(buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonWidth);
                if (rectF.contains(e.getX(), e.getY())) { // e renvoit la position en x et la position en y de l'endroit où on a tapé
                    gameBoard.pushValue(i);
                    postInvalidate(); // Force une réactualisation graphique
                    return true;
                }

                if (i != 6) {
                    buttonLeft += buttonWidth + buttonMargin;
                } else {
                    buttonLeft = buttonMargin;
                    buttonTop += buttonWidth + buttonMargin;
                }
            }

            // --- eraser button ---
            rectF = new RectF(buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonWidth);
            if (rectF.contains(e.getX(), e.getY())) {
                gameBoard.clearCell();
                postInvalidate();
                return true;
            }
            buttonLeft += buttonWidth + buttonMargin;
        }

        // --- pencil button ---
        rectF = new RectF( buttonLeft, buttonTop, buttonLeft+buttonWidth, buttonTop+buttonWidth );
        if ( rectF.contains( e.getX(), e.getY() ) ) {
            gameBoard.bigNumber = ! gameBoard.bigNumber;
            postInvalidate();
            return true;
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    // Comme l'orientation est bloquée en mode portrait, pas de changement de size de la grille
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged( w, h, oldw, oldh );

        // Les tailles sont en pourcentage, la taille des séparations de cellule et des cellules sont
        // exprimées en pourcentage pour s'adapter aux différentes tailles d'écran
        gridSeparatorSize = (w / 9f) / 20f;

        gridWidth = w;                                  // Size of the grid (it's a square)
        cellWidth = gridWidth / 9f;                     // Size of a cell (it's a square too)
        buttonWidth = w / 7f;                           // Size of a button
        buttonRadius = buttonWidth / 10f;               // Size of the rounded corner for a button
        buttonMargin = (w - 6*buttonWidth) / 7f;        // Margin between two buttons


        // We resize for this screen the two images
        eraserBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eraser);
        eraserBitmap = Bitmap.createScaledBitmap(eraserBitmap,
                (int) (buttonWidth*0.8f), (int) (buttonWidth*0.8f), false);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pencil);
        pencilBitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (buttonWidth*0.8f), (int) (buttonWidth*0.8), false);
        littlePencilBitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (buttonWidth/3), (int) (buttonWidth/3), false);


    }

    // Méthode qui sera invoquée à chaque fois que l'on devra dessiner
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setTextAlign( Paint.Align.CENTER );
        // paint.setTextSize( cellWidth * 0.7f ); // Taille de police adaptée à la résolution du mobile

        for( int y = 0; y < 9; y++ ) {
            for( int x = 0; x < 9; x++ ) {
                int backgroundColor = Color.WHITE;

                // Highlight the current row, current column and the current block
                // A value can be appeared only one time into all highlighted cells.
                if ( gameBoard.currentCellX != -1 && gameBoard.currentCellY != -1 ) {
                    if ( (x / 3 == gameBoard.currentCellX / 3 && y / 3 == gameBoard.currentCellY / 3) ||
                            (x == gameBoard.currentCellX && y != gameBoard.currentCellY) ||
                            (x != gameBoard.currentCellX && y == gameBoard.currentCellY)  ) {
                        backgroundColor = 0xFF_FF_F0_F0;
                    }
                }

                // Check if cell is initially proposed: in this case, the background is grey
                if ( gameBoard.cells[y][x].isInitial ) {
                    if ( backgroundColor == 0xFF_FF_F0_F0 ) {
                        backgroundColor = 0xFF_F4_F0_F0;
                    } else {
                        backgroundColor = 0xFF_F0_F0_F0;
                    }
                }

                // Change the color for the currently selected value
                if ( gameBoard.getSelectedValue() > 0 &&
                        gameBoard.cells[y][x].assumedValue == gameBoard.getSelectedValue() ) {
                    backgroundColor = 0xFF_C7_DA_F8;
                }

                // Display errors (conflicts) in red color: an error appear if a value is present
                // at least two times in the same line, column or block.
                if ( gameBoard.cells[y][x].assumedValue > 0 ) {
                    for( int tx=0; tx<9; tx++ ) {
                        if ( tx != x &&
                                gameBoard.cells[y][tx].assumedValue == gameBoard.cells[y][x].assumedValue ) {
                            backgroundColor = 0xFF_FF_00_00;
                            break;
                        }
                    }
                    if ( backgroundColor != 0xFF_FF_00_00 ) {
                        for (int ty = 0; ty < 9; ty++) {
                            if ( ty != y &&
                                    gameBoard.cells[ty][x].assumedValue == gameBoard.cells[y][x].assumedValue ) {
                                backgroundColor = 0xFF_FF_00_00;
                                break;
                            }
                        }
                    }
                    if ( backgroundColor != 0xFF_FF_00_00 ) {
                        int bx = x / 3;
                        int by = y / 3;
                        for (int dy = 0; dy < 3; dy++) {
                            for (int dx = 0; dx < 3; dx++) {
                                int tx = bx * 3 + dx;
                                int ty = by * 3 + dy;
                                if ( tx != x && ty != y &&
                                        gameBoard.cells[ty][tx].assumedValue == gameBoard.cells[y][x].assumedValue ) {
                                    backgroundColor = 0xFF_FF_00_00;
                                    break;
                                }
                            }
                        }
                    }
                }


                // Draw the background for the current cell
                paint.setColor( backgroundColor );
                canvas.drawRect(x * cellWidth,
                        y * cellWidth ,
                        (x+1) * cellWidth,
                        (y+1) * cellWidth,
                        paint);

                if (gameBoard.cells[y][x].assumedValue != 0) {

                    // Draw the assumed value for the cell.
                    paint.setColor(0xFF000000);
                    paint.setTextSize( cellWidth*0.7f );
                    canvas.drawText("" + gameBoard.cells[y][x].assumedValue,
                            x * cellWidth + cellWidth / 2,
                            y * cellWidth + cellWidth * 0.75f, paint);

                } else {

                    // Draw each mark if exists
                    paint.setTextSize( cellWidth*0.33f );
                    paint.setColor( 0xFFA0A0A0 );
                    if ( gameBoard.cells[y][x].marks[0] ) {
                        paint.setColor(gameBoard.getSelectedValue()==1 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("1",
                                x * cellWidth + cellWidth * 0.2f,
                                y * cellWidth + cellWidth * 0.3f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[1] ) {
                        paint.setColor(gameBoard.getSelectedValue()==2 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("2",
                                x * cellWidth + cellWidth * 0.5f,
                                y * cellWidth + cellWidth * 0.3f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[2] ) {
                        paint.setColor(gameBoard.getSelectedValue()==3 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("3",
                                x * cellWidth + cellWidth * 0.8f,
                                y * cellWidth + cellWidth * 0.3f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[3] ) {
                        paint.setColor(gameBoard.getSelectedValue()==4 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("4",
                                x * cellWidth + cellWidth * 0.2f,
                                y * cellWidth + cellWidth * 0.6f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[4] ) {
                        paint.setColor(gameBoard.getSelectedValue()==5 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("5",
                                x * cellWidth + cellWidth * 0.5f,
                                y * cellWidth + cellWidth * 0.6f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[5] ) {
                        paint.setColor(gameBoard.getSelectedValue()==6 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("6",
                                x * cellWidth + cellWidth * 0.8f,
                                y * cellWidth + cellWidth * 0.6f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[6] ) {
                        paint.setColor(gameBoard.getSelectedValue()==7 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("7",
                                x * cellWidth + cellWidth * 0.2f,
                                y * cellWidth + cellWidth * 0.9f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[7] ) {
                        paint.setColor(gameBoard.getSelectedValue()==8 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("8",
                                x * cellWidth + cellWidth * 0.5f,
                                y * cellWidth + cellWidth * 0.9f, paint);
                    }
                    if ( gameBoard.cells[y][x].marks[8] ) {
                        paint.setColor(gameBoard.getSelectedValue()==9 ? 0xFF4084EF : 0xFFA0A0A0);
                        canvas.drawText("9",
                                x * cellWidth + cellWidth * 0.8f,
                                y * cellWidth + cellWidth * 0.9f, paint);
                    }
                }
            }
        }

        // --- Draw the grid lines ---
        paint.setColor( Color.GRAY);
        paint.setStrokeWidth( gridSeparatorSize/2 );
        for( int i = 0; i <= 9; i++ ) {
            canvas.drawLine( i*cellWidth, 0, i*cellWidth, cellWidth*9, paint );
            canvas.drawLine( 0,i*cellWidth, cellWidth*9, i*cellWidth, paint );
        }
        paint.setColor( Color.BLACK );
        paint.setStrokeWidth( gridSeparatorSize );
        for( int i = 0; i <= 3; i++ ) {
            canvas.drawLine( i*(cellWidth*3), 0, i*(cellWidth*3), cellWidth*9, paint );
            canvas.drawLine( 0,i*(cellWidth*3), cellWidth*9, i*(cellWidth*3), paint );
        }

        // --- Draw border for the current selected cell ---
        if ( gameBoard.currentCellX != -1 && gameBoard.currentCellY != -1 ) {
            paint.setColor( 0xFF_30_3F_9F );
            paint.setStrokeWidth( gridSeparatorSize * 1.5f );
            paint.setStyle( Paint.Style.STROKE );
            canvas.drawRect( gameBoard.currentCellX * cellWidth,
                    gameBoard.currentCellY * cellWidth,
                    (gameBoard.currentCellX+1) * cellWidth,
                    (gameBoard.currentCellY+1) * cellWidth,
                    paint);
            paint.setStyle( Paint.Style.FILL_AND_STROKE );
            paint.setStrokeWidth( 1 );
        }

        // --- Buttons bar ---

        float buttonsTop = 9*cellWidth + gridSeparatorSize/2;

        paint.setColor(0xFFC7DAF8);
        canvas.drawRect(0, buttonsTop, gridWidth, getHeight(), paint);

        float buttonLeft = buttonMargin;
        float buttonTop = buttonsTop + buttonMargin;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(buttonWidth * 0.7f); // La taille du chiffre dans le bouton est proportionnel
        // à la taille de la cellule (70%)

        for (int i = 1; i <= 9; i++) {
            paint.setColor( 0xFFFFFFFF ); // Du blanc
            // Attention aux new !!! Mais ici, on n'est pas trop gourmand
            // Il existe une autre version de drawRoundRect, mais elle necessite
            // que vous modifiez la version minimale supportee pour Android :-(
            RectF rectF = new RectF(buttonLeft, buttonTop,
                    buttonLeft + buttonWidth, buttonTop + buttonWidth);
            canvas.drawRoundRect(rectF, buttonRadius, buttonRadius, paint); // Rectangle avec bords ronds

            paint.setColor( 0xFF000000 );
            canvas.drawText("" + i, rectF.centerX(), rectF.top + rectF.height() * 0.75f, paint);

            if (i != 6) {
                buttonLeft += buttonWidth + buttonMargin;
            } else {
                buttonLeft = buttonMargin;
                buttonTop += buttonWidth + buttonMargin;
            }
        }

        int imageWidth = (int) (buttonWidth * 0.8f);
        int imageMargin = (int) (buttonWidth * 0.1f);

        // --- eraser ---
        paint.setColor(0xFFFFFFFF);
        RectF rectF = new RectF( buttonLeft, buttonTop,
                buttonLeft + buttonWidth, buttonTop + buttonWidth );
        canvas.drawRoundRect( rectF, buttonRadius, buttonRadius, paint );
        canvas.drawBitmap( eraserBitmap,
                buttonLeft + imageMargin, buttonTop + imageMargin, paint );
        buttonLeft += buttonWidth + buttonMargin;

        // --- pencil ---
        paint.setColor(0xFFFFFFFF);
        rectF = new RectF( buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonWidth );
        canvas.drawRoundRect( rectF, buttonRadius, buttonRadius, paint );
        Bitmap bitmap = gameBoard.bigNumber ? pencilBitmap : littlePencilBitmap;
        canvas.drawBitmap( bitmap, buttonLeft + imageMargin, buttonTop + imageMargin, paint );


    }



}

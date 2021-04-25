package com.endgame.endgame;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.endgame.chess.Game;
import com.endgame.chess.GameFactory;
import com.endgame.chess.Piece.AbstractPiece;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener, PropertyChangeListener {
    GameFactory gameFactory = GameFactory.getInstance();
    Game game;
    String scenarioName = "KQvK";

    ArrayList<TextView> textViews = new ArrayList<>();
    ArrayList<FrameLayout> frameLayouts = new ArrayList<>();
    boolean boardIsFrozen = false;

    // timer stuff
    private Chronometer mChessTimer;
    private boolean mTimerRunning;
    private long lastPause;

    DatabaseHelper scoreboardDB;

    // this is effectively update() from the observer pattern
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String eventName = evt.getPropertyName();
        if (eventName.equals("board")) {
            loadGameToUI();
        } else if (eventName.equals("gameOver")) {
            showEndGame();
        }
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //start a game
        startGame();
        loadGameToUI();

        //init database
        scoreboardDB = new DatabaseHelper(this);

        //setup timer (here for now)
        mChessTimer = findViewById(R.id.chessTimer);
        Button mButtonScoreboard = findViewById(R.id.scoreboardButton);
        mButtonScoreboard.setOnClickListener(v -> openScoreboardActivity());

        Button mResetButton = findViewById(R.id.resetButton);
        mResetButton.setOnClickListener(v -> {
            resetTimer();
            startGame();
            loadGameToUI();
        });

        //Build scenario menu
        Button mButtonScenarios = findViewById(R.id.scenariosButton);
        mButtonScenarios.setOnClickListener(v -> openScenariosActivity());
    }

    private void startGame() {
        boardIsFrozen = false;
        game = gameFactory.initGame(scenarioName);
        game.shuffleBoard();
        game.attachObserver(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (boardIsFrozen) {
                return true;
            }
            AbstractPiece piece;
            int[] rowCol = coordinatesToRowCol(extractCoordinates(v.getTag().toString()));
            int row = rowCol[0];
            int col = rowCol[1];
            piece = game.getBoard()[row][col];
            // ignore touch if not our piece
            if (piece.getColor() != game.activePlayer.getColor()) return false;
            highlightSquares(piece.getPossibleSquares());
            // create a new ClipData.Item from the ImageView object's tag
            ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
            // create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. this will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
            // instantiate the drag shadow builder
            View.DragShadowBuilder dragShadow = new BigDragShadowBuilder(v);
            // start the drag
            v.startDragAndDrop(data, dragShadow, v, 0);
            return true;
        }
        return true;
    }

    // this is the method that the system calls when it dispatches a drag event to the listener
    @Override
    public boolean onDrag(View v, DragEvent event) {
        // define a variable to store the action type for the incoming event
        int action = event.getAction();
        // handle each of the expected events
        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // returns true to indicate that the View can accept the dragged data
                    return true;
                }
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                // apply tint to the target square
                v.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                // invalidate the view to force a redraw in the new tint
                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                // clear square tint
                v.getBackground().clearColorFilter();
                // invalidate the view to force a redraw in the new tint
                v.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                clearHighlightedSquares();
                // turn off any color tints
                v.getBackground().clearColorFilter();
                // invalidate the view to force a redraw
                v.invalidate();
                View vw = (View) event.getLocalState();
                FrameLayout container = (FrameLayout) v;
                ViewGroup owner = (ViewGroup) vw.getParent();
                String fromCoord = extractCoordinates(vw.getTag().toString());
                String toCoord = extractCoordinates(getResources().getResourceEntryName(container.getId()));
                if (fromCoord.equals(toCoord)) return false;

                owner.removeView(vw); // remove the dragged view
                container.removeAllViews(); //'captures' any existing pieces

                moveGamePiece(fromCoord, toCoord);

                // set tag to new position of piece
                vw.setTag("piece_" + toCoord);
                container.addView(vw); // add the dragged view

                vw.setVisibility(View.VISIBLE); // finally set Visibility to VISIBLE

                if (!mTimerRunning) startTimer();
                game.nextTurn();
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                clearHighlightedSquares();
                // turn off any color tinting
                v.getBackground().clearColorFilter();
                // invalidate the view to force a redraw
                v.invalidate();
                return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void loadGameToUI() {
        runOnUiThread(() -> {
            // remove existing pieces
            for (FrameLayout container : frameLayouts) {
                container.removeAllViews(); //'captures' any existing pieces
            }
            textViews.clear();

            AbstractPiece[][] board = game.getBoard();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    AbstractPiece piece = board[row][col];
                    if (piece.isEmptyPiece()) {
                        continue;
                    }
                    String pieceText = "";
                    switch (board[row][col].getType()) {
                        case BISHOP:
                            pieceText = "n";
                            break;
                        case KING:
                            pieceText = "l";
                            break;
                        case KNIGHT:
                            pieceText = "j";
                            break;
                        case PAWN:
                            pieceText = "o";
                            break;
                        case QUEEN:
                            pieceText = "w";
                            break;
                        case ROOK:
                            pieceText = "t";
                            break;
                    }

                    // build ui chess piece
                    TextView textView = new OutlineTextView(findViewById(R.id.chessBoard).getContext());
                    textView.setText(pieceText);
                    textView.setGravity(1); // 1 == centered
                    textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    Typeface face = getResources().getFont(R.font.cheq_tt);

                    // set color
                    int pieceWhite = ContextCompat.getColor(textView.getContext(), R.color.pieceColor_White);
                    int pieceBlack = ContextCompat.getColor(textView.getContext(), R.color.pieceColor_Black);
                    int pieceOutlineWhite = ContextCompat.getColor(textView.getContext(), R.color.pieceOutline_White);
                    int pieceOutlineBlack = ContextCompat.getColor(textView.getContext(), R.color.pieceOutline_Black);
                    if (piece.getColor() == Game.Color.WHITE) {
                        textView.setTextColor(pieceWhite);
                        textView.setShadowLayer(0, 0, 0, pieceOutlineWhite);
                    } else {
                        textView.setTextColor(pieceBlack);
                        textView.setShadowLayer(0, 0, 0, pieceOutlineBlack);
                    }

                    textView.setTypeface(face);
                    textView.setOnTouchListener(this);
                    textView.setVisibility(View.VISIBLE);

                    // add to board
                    String squareID = "square_" + rowColToCoordinates(row, col);

                    // set tag
                    textView.setTag(squareID.replace("square_", "piece_"));

                    int resID = getResources().getIdentifier(squareID, "id", getPackageName());
                    FrameLayout fl = findViewById(resID);
                    fl.addView(textView);
                    textViews.add(textView);
                    frameLayouts.add(fl);
                }
            }
        });
    }

    public void showEndGame() {
        pauseTimer(); // stop timer
        boardIsFrozen = true; // freeze board

        String messageText = "";
        String messageTitle = "";
        if (game.winner == game.nobody) {
            messageTitle = "Scenario Failed";
            messageText = "Stalemate!";
        } else if (game.winner == game.human) {
            messageTitle = "Scenario Complete";
            messageText = "Checkmate!";
        } else {
            messageTitle = "Scenario Failed";
            messageText = "You got checkmated!";
        }

        // add message to board
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(messageTitle)
                .setMessage(messageText)
                .setNegativeButton(android.R.string.ok, null)
                .show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        if (textView != null) textView.setTextSize(30);

        //todo add timer and move counter to scorescreen
        scoreboardDB.insertData(game.scenarioName, SystemClock.elapsedRealtime() - mChessTimer.getBase(), game.human.getMoveCount());

        //todo add buttons to load scenarios and retry
    }

    public void highlightSquares(ArrayList<AbstractPiece> highlightSquares) {
        String squareID;
        FrameLayout fl;
        int resID;

        for (AbstractPiece highlightSquare : highlightSquares) {
            int row = highlightSquare.getRow();
            int col = highlightSquare.getCol();
            squareID = "square_" + rowColToCoordinates(row, col);
            resID = getResources().getIdentifier(squareID, "id", getPackageName());
            fl = findViewById(resID);
            fl.setOnDragListener(this); // allow drag to
            if ((row + col) % 2 == 0) {
                fl.setBackgroundResource(R.drawable.highlight_white);
            } else {
                fl.setBackgroundResource(R.drawable.highlight_black);
            }
            fl.invalidate();
        }
    }

    public void clearHighlightedSquares() {
        ArrayList<View> views;
        views = getViewsByTag((ViewGroup) findViewById(R.id.chessBoard), "square_black");
        for (View view : views) {
            view.setBackgroundResource(R.color.chessBoard_Black);
            view.setOnDragListener(null); // disallow drag to
        }
        views = getViewsByTag((ViewGroup) findViewById(R.id.chessBoard), "square_white");
        for (View view : views) {
            view.setBackgroundResource(R.color.chessBoard_White);
            view.setOnDragListener(null); // disallow drag to
        }
    }

    public void moveGamePiece(String fromCoord, String toCoord) {
        int from_row = coordinatesToRowCol(fromCoord)[0];
        int from_col = coordinatesToRowCol(fromCoord)[1];
        int to_row = coordinatesToRowCol(toCoord)[0];
        int to_col = coordinatesToRowCol(toCoord)[1];

        if (from_row != to_row || from_col != to_col) {
            game.movePiece(from_row, from_col, to_row, to_col);
        }

    }

    public String rowColToCoordinates(int row, int col) {
        // example: rowColToCoordinates(7,7) -> "h1"
        String coord = "";
        coord += String.valueOf((char) (col + 1 + 96));
        coord += Integer.toString(7 - row + 1);
        return coord;
    }

    public int[] coordinatesToRowCol(String coord) {
        // example: coordinatesToRowCol("h1") -> [7,7]
        int[] rowCol = new int[2];
        rowCol[0] = 7 - (Integer.parseInt(coord.substring(1)) - 1);
        rowCol[1] = Integer.valueOf(coord.charAt(0)) - 96 - 1;
        return rowCol;
    }

    public String extractCoordinates(String text) {
        if (text.startsWith("piece_")) {
            text = text.substring(6);
        }
        if (text.startsWith("square_")) {
            text = text.substring(7);
        }
        return text;
    }

    private void startTimer() {
        if (lastPause != 0) {
            mChessTimer.setBase(mChessTimer.getBase() + SystemClock.elapsedRealtime() - lastPause);
        } else {
            mChessTimer.setBase(SystemClock.elapsedRealtime());
        }

        mChessTimer.start();
        mTimerRunning = true;
    }

    private long pauseTimer() {
        lastPause = SystemClock.elapsedRealtime();
        mChessTimer.stop();
        mTimerRunning = false;
        return lastPause;
    }

    private void resetTimer() {
        mChessTimer.stop();
        mTimerRunning = false;
        mChessTimer.setBase(SystemClock.elapsedRealtime());
        lastPause = 0;
    }

    public void openScoreboardActivity() {
        //query db for scoreboard results
        Cursor scoreboardData = scoreboardDB.getScoreboardData();
        ArrayList<String> scores = new ArrayList<>();

        if (scoreboardData.getCount() == 0) {
            //show error message
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("DBError");
            builder.setMessage("No data found");
            builder.show();
        } else {
            while (scoreboardData.moveToNext()) {
                scores.add(scoreboardData.getString(0));

                //convert milliseconds to readable format
                long millis = scoreboardData.getLong(2);
                @SuppressLint("DefaultLocale")
                String time = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                scores.add(time);
                scores.add(String.valueOf(scoreboardData.getInt(1)));
            }
        }

        //setup the new activity, passing in queried data as a string
        Intent intent = new Intent(this, ScoreboardActivity.class);
        intent.putExtra("ScoreboardData", scores);
        startActivity(intent);
    }

    public void openScenariosActivity() {
        Intent intent = new Intent(this, ScenariosActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2 && resultCode == 2) {
            scenarioName = data.getStringExtra("MESSAGE");
            resetTimer();
            startGame();
            loadGameToUI();
        }
    }

}


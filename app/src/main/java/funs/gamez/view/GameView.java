package funs.gamez.view;


import android.content.Context;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import funs.gamez.controller.GameController;
import funs.gamez.minos.R;
import funs.gamez.model.Game;
import funs.gamez.view.render.GameRenderer;

// 自定义游戏示图
public class GameView extends View {

    private Game game;
    private GameController gameController;
	private final GameRenderer gameRenderer;

	private boolean inBackground = false;
	
	public GameView(Context context, Game game) {
		super(context);
		this.game = game;
		this.gameRenderer = new GameRenderer(context, game);
		
		setLayoutParams(new FrameLayout.LayoutParams(game.getMetrics()
                .getWidth(), game.getMetrics().getHeight(), Gravity.CENTER));
		
		setBackgroundColor(context.getResources().getColor(R.color.game_background));
	
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    gameRenderer.render(canvas);
	    
	    if (inBackground) {
	        gameRenderer.renderOverlay(canvas);
	    }
	    
	}

	
	public void setInBackground(boolean inBackground) {
	    this.inBackground = inBackground;
	    this.invalidate();
	}
	
	public boolean isInBackground() {
	    return inBackground;
	}

	public void setGameController(GameController gameConroller) {
	    this.gameController = gameConroller;
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (gameController != null) {
            gameController.handleDrag(this, ev);
        }
        return true;
    }
 	
}

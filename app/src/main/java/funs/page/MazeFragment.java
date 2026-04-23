package funs.page;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import funs.common.tools.CLogger;
import funs.gamez.controller.GameController;
import funs.gamez.minos.R;
import funs.gamez.model.Game;
import funs.gamez.model.GameState;
import funs.gamez.model.SettingsFormData;
import funs.gamez.model.factory.GameFactory;
import funs.gamez.model.factory.GameMetrics;
import funs.gamez.view.DisplayUtils;
import funs.gamez.view.GameView;
import funs.gamez.view.SettingsWindow;

/**
 * A simple {@link Fragment} subclass.🏆
 * create an instance of this fragment.
 */
public class MazeFragment extends Fragment {
    private static final String TAG = "MazeFragment";
    private static final String STATE_KEY_GAME = "game";
    private static final String STATE_KEY_GAME_SETTINGS = "gameSettings";
    private static final String STATE_KEY_SCREEN_ORIENTATION = "screenOrientation";

    private static final String SCORE_FORMAT = " \uD83C\uDFC6  %d ";
    private int score = 0;
    private int scoreStep = 10;

    private FrameLayout gameContainer = null;
    private GameView gameView = null;
    private SettingsWindow settingsWindow = null;

    private GameMetrics gameMetrics = null;

    private GameController gameController = null;

    private Animation destroyMazeAnimation = null;
    private Animation createMazeAnimation = null;

    /* --- 实例状态 --- */
    private Game game;
    private SettingsFormData gameSettings;
    private int screenOrientation;

    private TextView scoreTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // App 首次启动
        if (savedInstanceState == null) {

            // 确定并锁定当前屏幕方向
            screenOrientation = DisplayUtils.getCurrentScreenOrientation(getContext());
            requireActivity().setRequestedOrientation(screenOrientation);

            gameSettings = new SettingsFormData();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }
        CLogger.i(TAG, "onCreate");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        CLogger.i(TAG, "onSaveInstanceState");
        outState.putParcelable(STATE_KEY_GAME, game);
        outState.putParcelable(STATE_KEY_GAME_SETTINGS, gameSettings);
        outState.putInt(STATE_KEY_SCREEN_ORIENTATION, screenOrientation);
        super.onSaveInstanceState(outState);
    }

    private void onRestoreInstanceState(Bundle savedState) {
//        super.onRestoreInstanceState(savedState);
        if (savedState == null) {
            return;
        }
        CLogger.i(TAG, "onRestoreInstanceState");

        screenOrientation = savedState.getInt(STATE_KEY_SCREEN_ORIENTATION);
        if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            // 确定并锁定当前屏幕方向
            screenOrientation = DisplayUtils.getCurrentScreenOrientation(getContext());
            requireActivity().setRequestedOrientation(screenOrientation);
        }

        gameSettings = savedState.getParcelable(STATE_KEY_GAME_SETTINGS);

        game = savedState.getParcelable(STATE_KEY_GAME);
        if (game != null) {
            Runnable task = () -> {
                CLogger.i(TAG, "Creating game metrics & view...");
                // 重新创建 game metrics...
                gameMetrics = new GameMetrics(gameContainer, gameSettings);
                // ...设定游戏...
                game.setMetrics(gameMetrics);
                // ...并创建游戏视图
                createGameView();
            };
            runOnUiThread(task);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maze, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameContainer = view.findViewById(R.id.gameContainer);
        initViews(view);
        CLogger.i(TAG, "onViewCreated");
        // 如果这是应用程序的首次启动或没有游戏被保留...
        if (game == null) {
            // ...请求创建新游戏
            createGame();
        }
    }

    private void initViews(View view) {
        View settingsBtn = view.findViewById(R.id.settingBtn);
        View nextBtn = view.findViewById(R.id.nextBtn);
        scoreTv = view.findViewById(R.id.scoreTv);

        settingsBtn.setOnClickListener(v -> showSettingsWindow());
        nextBtn.setOnClickListener(v -> {
            if (!onNextGame()) goNextGame();
        });

        scoreTv.setText(String.format(SCORE_FORMAT, score));
    }

    // 钩子函数
    protected boolean onNextGame() {
        return false;
    }

    protected void goNextGame() {
        if (isVisible()) createGame();
    }

    @Override
    public void onStop() {
        super.onStop();
        CLogger.i(TAG, "onStop");
        // 关闭设置弹窗...
        if (settingsWindow != null) {
            settingsWindow.dismiss();
            settingsWindow = null;
        }
    }


    // 游戏难度设置
    private void showSettingsWindow() {
        CLogger.i(TAG, "showSettingsWindow");
        // 如果尚未可见...
        if (settingsWindow == null) {
            // 暂停游戏并保留旧的游戏状态
            final GameState oldGameState = game.getState();
            gameController.setGameState(GameState.PAUSED);
            // 创建新的设置弹窗
            View content = getLayoutInflater().inflate(R.layout.settings_window, null);
            settingsWindow = new SettingsWindow(content, gameSettings);
            // 确定按钮
            Button okButton = content.findViewById(R.id.okButton);
            okButton.setOnClickListener(v -> {
                // 确保确实存在一个设置窗口实例
                if (settingsWindow != null) {
                    // 关闭设置窗口
                    settingsWindow.dismiss();
                    settingsWindow = null;
//                        updateMenu();
                    // 恢复游戏
                    gameView.setInBackground(false);
                    if (game.getState() == GameState.READY) {
                        gameController.setGameState(GameState.PLAYING);
                    } else {
                        gameController.setGameState(oldGameState);
                    }
                }
            });
            // 游戏视图
            gameView.setInBackground(true);
            // 显示设置弹窗
            settingsWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);

            settingsWindow.setOnGameSettingsChangedListener(settings -> onGameSettingsChanged());
        }
    }

    /* --- 游戏操控 ------------------------------------------- */

    private void createGame() {
        CLogger.i(TAG, "createGame");

        // 放弃旧游戏（如果有的话）
        if (gameController != null) {
            gameController.setGameState(GameState.PAUSED);
        }
        game = null;

        // 当前屏幕方向
        int currentScreenOrientation = DisplayUtils
                .getCurrentScreenOrientation(getContext());

        // 如果设置窗口尚未可见...
        if (settingsWindow == null) {
            // 锁定当前屏幕方向
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            // 确定并锁定当前屏幕方向
            screenOrientation = DisplayUtils.getCurrentScreenOrientation(getContext());
            requireActivity().setRequestedOrientation(screenOrientation);
        }

        // 如果屏幕方向没有改变...
        if (screenOrientation == currentScreenOrientation) {
            Runnable task = () -> {
                CLogger.i(TAG, "Creating game...");
                // 确定游戏参数指标
                gameMetrics = new GameMetrics(gameContainer, gameSettings);
                // 创建游戏
                game = GameFactory.createGame(gameMetrics);
                // 创建游戏示图
                createGameView();
                gameController = new GameController(getContext(), game, gameView);
                gameView.setGameController(gameController);
                if (settingsWindow == null) {
                    gameController.setGameState(GameState.PLAYING);
                } else {
                    gameView.setInBackground(true);
                }

                // todo
                gameController.setOnGameStateChangedListener(new GameController.OnGameStateChangedListener() {
                    @Override
                    public void onGameStarted() {
                    }

                    @Override
                    public void onGameFinished() {
                        if (!onNextGame() && null != gameContainer) {
                            postDelayed(() -> goNextGame(), 1200);
                        }

                        score += Math.max(scoreStep, 10);
                        scoreTv.setText(String.format(SCORE_FORMAT, score));
                        CLogger.i(TAG, "onGameFinished, scoreStep: " + scoreStep);
                    }
                });
            };
            runOnUiThread(task);

        } else {
            // 游戏将在 Activity重新创建期间创建
        }

    }

    private void createGameView() {
        CLogger.i(TAG, "createGameView");
        // 如果有旧的游戏视图
        if (gameView != null) {
            final ViewGroup gc = gameContainer;
            final View gv = gameView;
            if (createMazeAnimation != null && !createMazeAnimation.hasEnded()) {
                CLogger.d(TAG, "stopping createMazeAnimation");
                createMazeAnimation.cancel();
            }
            destroyMazeAnimation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.destroy_maze);
            CLogger.d(TAG, "starting destroyMazeAnimation");
            destroyMazeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Runnable task = () -> {
                        CLogger.d(TAG, "destroyMazeAnimation finished, removing old game view");
                        gc.removeView(gv);
                    };
                    runOnUiThread(task);
                }
            });
            gameView.startAnimation(destroyMazeAnimation);
        }
        // 创建新的游戏视图
        gameView = new GameView(getContext(), game);
        // gameView.setVisibility(View.GONE);
        gameContainer.addView(gameView, 0);

        CLogger.d(TAG, "starting createMazeAnimation");
        createMazeAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.create_maze);
        gameView.startAnimation(createMazeAnimation);
    }

    /* --- 游戏设置处理 --- */
    private void onGameSettingsChanged() {
        CLogger.i(TAG, "onGameSettingsChanged");
        // 仅在游戏的参数指标发生变化时才创建新游戏
        GameMetrics newGameMetrics = new GameMetrics(gameContainer,
                gameSettings);
        if (!newGameMetrics.equals(gameMetrics)) {
            createGame();
            int standard = null != gameSettings ? (int) (100 * gameSettings.getMazeSize()) : 10;
            if (standard > 19) {
                scoreStep = 10 * (standard / 10);
            } else scoreStep = 10;

            CLogger.i(TAG, "onGameSettingsChanged, scoreStep: " + scoreStep);
        }
    }

    protected int getScoreStep() {
        return scoreStep;
    }

    protected void setScoreStep(int scoreStep) {
        this.scoreStep = scoreStep;
    }

    protected void runOnUiThread(Runnable task) {
        if (null != gameContainer) gameContainer.post(task);
    }

    protected void postDelayed(Runnable task, long delayMillis) {
        if (null != gameContainer) gameContainer.postDelayed(task, delayMillis);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gameController != null) {
            gameController.release();
        }
    }

    public String getSimpleName() {
        return TAG;
    }
}
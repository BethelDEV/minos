package funs.gamez.view;

import android.graphics.Point;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import funs.common.tools.CLogger;
import funs.gamez.minos.R;
import funs.gamez.model.SettingsFormData;

// 游戏设置 弹窗
public class SettingsWindow extends PopupWindow implements OnSeekBarChangeListener {

    private final String TAG = this.getClass().getSimpleName();
    
	private final SettingsFormData gameSettings;
	
	private final SeekBar mazeSizeSeekBar;

	public SettingsWindow(View content, SettingsFormData gameSettings) {
		super(content);
		CLogger.i(TAG, "ctor");
		
		this.gameSettings = gameSettings;
		
		Point displaySize = DisplayUtils.getDisplaySize(content.getContext());
		
		content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		setWidth(Math.min(displaySize.x, displaySize.y));
		setHeight(content.getMeasuredHeight());

		mazeSizeSeekBar = (SeekBar)content.findViewById(R.id.mazeSize);
		mazeSizeSeekBar.setOnSeekBarChangeListener(this);
		mazeSizeSeekBar.setProgress((int)(100 * gameSettings.getMazeSize()));
		
	}


	/* --- OnSeekBarChangeListener implementation --- */
	
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        CLogger.d(TAG, "onProgressChanged");
        onGameSettingsChanged();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        CLogger.d(TAG, "onStartTrackingTouch");
        onGameSettingsChanged();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        CLogger.d(TAG, "onStopTrackingTouch");
        onGameSettingsChanged();
    }

    /* --- Intent handling ----------------------------------------- */
    
    private void onGameSettingsChanged() {
        CLogger.i(TAG, "onGameSettingsChanged");
        
        gameSettings.setMazeSize((float)mazeSizeSeekBar.getProgress()/100f);

        if (mListener != null) {
            mListener.onChanged(gameSettings);
        }
    }

    private onGameSettingsChangedListener mListener;
    public interface onGameSettingsChangedListener{
        void onChanged(SettingsFormData settings);
    }

    public void setOnGameSettingsChangedListener(onGameSettingsChangedListener mListener) {
        this.mListener = mListener;
    }
}

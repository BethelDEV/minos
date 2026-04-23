
package funs.page;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import funs.gamez.minos.R;

//  You need to use a Theme.AppCompat theme (or descendant) with this activity.
public class MazeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze);
    }
}
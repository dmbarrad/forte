package hunterirving.forte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Intent.ACTION_CALL_BUTTON;
import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_APP_MAPS;
import static android.content.Intent.CATEGORY_APP_MESSAGING;
import static android.content.Intent.CATEGORY_DEFAULT;
import static android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA;

public class FullscreenActivity extends AppCompatActivity {
    //DISPLAY_NAME, INTENT_TYPE, INTENT_CATEGORY, PACKAGE_NAME
    //each appPair must include either <INTENT_TYPE and INTENT_CATEGORY> or <PACKAGE_NAME>
    final String[][] appPairs = {
                    {"ATLAS", ACTION_MAIN, CATEGORY_APP_MAPS, null},
                    {"CAMERA", INTENT_ACTION_STILL_IMAGE_CAMERA, CATEGORY_DEFAULT, null},
                    {"TELEPHONE", ACTION_CALL_BUTTON, CATEGORY_DEFAULT, null},
                    {"TELEGRAPH°", ACTION_MAIN, CATEGORY_APP_MESSAGING, null}/*,
                    {"PHONOGRAPH", null, null, "com.spotify.music"}*/
            };

    int chunkSize = 85;
    float maxPos = chunkSize * appPairs.length;
    float pos = 0; //0-(maxPos-1) (position within UI bounds)
    int index = 0; //0-(appPairs.length-1) (index of selected element in appPairs)
    float lastKnownY = 0;
    float yDelta = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        final FrameLayout Frame = findViewById(R.id.Frame);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Initialize UI
        final TextView UI_container = findViewById(R.id.UI_container);
        final TextView selected_item = findViewById(R.id.selected_item);
        UI_container.setText(get_UI_String(appPairs, index));
        selected_item.setText(appPairs[index][0]);

        //listen for touch events
        Frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastKnownY = event.getY();
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float newY = event.getY();
                    yDelta = lastKnownY - newY;
                    lastKnownY = newY;

                    //Add newest movement delta to position
                    float unclampedPos = pos + yDelta;

                    //sanity check (clamp pos within UI bounds)
                    pos = clamp(unclampedPos);

                    //determine index of item that needs to be selected
                    int prevIndex = index;
                    index = (int) (pos / chunkSize);

                    //determine if UI needs to be updated
                    if (index != prevIndex) {
                        UI_container.setText(get_UI_String(appPairs, index));
                        selected_item.setText(appPairs[index][0]);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(25, 55));
                        } else {
                            vibrator.vibrate(55);
                        }
                    }
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    launchSelected(appPairs, index);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        pos = 0; //0-(maxPos-1) (position within UI bounds)
        index = 0; //0-(appPairs.length-1) (index of selected element in appPairs)
        lastKnownY = 0;
        yDelta = 0;

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //Reinitialize UI
        final TextView UI_container = findViewById(R.id.UI_container);
        final TextView selected_item = findViewById(R.id.selected_item);
        UI_container.setText(get_UI_String(appPairs, index));
        selected_item.setText(appPairs[index][0]);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final TextView UI_container = findViewById(R.id.UI_container);
        final TextView selected_item = findViewById(R.id.selected_item);

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (index > 0) {
                    index--;
                }
                //update pos (in case user returns to touch navigation)
                pos = clamp((index * chunkSize) + (chunkSize/2));

                UI_container.setText(get_UI_String(appPairs, index));
                selected_item.setText(appPairs[index][0]);
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (index < appPairs.length - 1) {
                    index++;
                }
                //update pos (in case user returns to touch navigation)
                pos = clamp((index * chunkSize) + (chunkSize/2));

                UI_container.setText(get_UI_String(appPairs, index));
                selected_item.setText(appPairs[index][0]);
                return true;

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                launchSelected(appPairs, index);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void launchSelected(String appPairs[][], int index) {
        Intent launchIntent;

        if (appPairs[index][3] == null) {
            launchIntent = new Intent(appPairs[index][1]);
            launchIntent.addCategory(appPairs[index][2]);
        }
        //handle explicitly defined packages (eg: spotify)
        else {
            launchIntent = getPackageManager().getLaunchIntentForPackage(appPairs[index][3]);
        }
        try {
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);
        } catch (Exception e) {
            CharSequence text = appPairs[index][0] + " UNAVAILABLE";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }

    private String get_UI_String(String appPairs[][], int index) {
        String UI_backgroundString = "";
        for (int i = 0; i < (appPairs.length) - index; i++) {
            UI_backgroundString += "\n";
        }
        for (int i = 0; i < appPairs.length; i++) {
            UI_backgroundString += appPairs[i][0] + "\n";
        }
        for (int i = 0; i < index; i++) {
            UI_backgroundString += "\n";
        }
        return UI_backgroundString;
    }

    private float clamp(float unclampedPos) {
        if (unclampedPos < 0) {
            pos = 0;
        } else if (unclampedPos >= maxPos) {
            pos = maxPos - 1;
        } else {
            pos = unclampedPos;
        }
        return pos;
    }
}
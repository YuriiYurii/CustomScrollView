package globallogic.yuriitsap.com.customscrollview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private HorizontalScrollView mHorizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_scroller);
        for (int i = 0; i < 100; i++) {
            Button button = new Button(MainActivity.this);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast(v);
                }
            });
            button.setText("Button N" + i);
            mHorizontalScrollView.addView(button);
        }
    }

    public void showToast(View view) {
        Toast.makeText(MainActivity.this,
                "View left " + view.getLeft() + " right " + view.getRight(),
                Toast.LENGTH_SHORT).
                show();
    }
}

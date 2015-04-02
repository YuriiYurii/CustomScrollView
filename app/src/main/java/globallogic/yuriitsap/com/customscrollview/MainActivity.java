package globallogic.yuriitsap.com.customscrollview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showToast(View view) {
        Toast.makeText(MainActivity.this, "Your answer is correct!",
                Toast.LENGTH_SHORT).
                show();
    }
}

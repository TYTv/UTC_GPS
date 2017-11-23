package felix.com.utc_gps;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GPS(this) {
            @Override
            public void onResult(PACKAGE result) {
                ;
            }

            @Override
            public void onResultFinish(PACKAGE result) {

                StringBuilder sb = new StringBuilder();

                sb.append("[Date]\n");
                sb.append(result.RMC[RMC.UT_date.ordinal()]);
                sb.append("\n\n");

                sb.append("[Time]\n");
                sb.append(result.RMC[RMC.UTC_of_position_fix.ordinal()]);
                sb.append("\n\n");

                sb.append("[Offset]\n");
                sb.append(result.RMC_offsetTime() + "s");
                sb.append("\n\n");

                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText(sb);

            }
        };

    }

}

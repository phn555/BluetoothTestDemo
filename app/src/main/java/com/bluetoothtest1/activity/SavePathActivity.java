package com.bluetoothtest1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/9/19.
 */
public class SavePathActivity extends Activity {
    private EditText path ;
    private Button button ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        setContentView(R.layout.save_path);

        path = (EditText) findViewById(R.id.save_path);
        button = (Button) findViewById(R.id.save_path_bnt) ;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pathString = path.getText().toString() ;
                if(pathString!= null){
                    Intent intent = new Intent();
                    intent.putExtra("data_return" ,pathString) ;
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
}

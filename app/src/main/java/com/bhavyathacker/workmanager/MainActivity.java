package com.bhavyathacker.workmanager;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String KEY_TASK_DESC = "key_task_desc";
    OneTimeWorkRequest workRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data data = new Data.Builder()
                .putString(KEY_TASK_DESC, "Hey I am sending the work data")
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.METERED)
                .build();
        workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WorkManager.getInstance().enqueue(workRequest);
                scheduleWork();

            }
        });
        final TextView textView = (TextView) findViewById(R.id.textView);
        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workInfo) {
                Log.d(TAG, "onChanged: ");
                if (workInfo != null) {
                    Log.d(TAG, "onChanged: workInfo != null");
                    if (workInfo.getState().isFinished()) {
                        Log.d(TAG, "onChanged: Finished");
                        Data data = workInfo.getOutputData();
                        String output = data.getString(MyWorker.KEY_TASK_OUTPUT);
                        textView.append(output + "\n");
                    }
                    String status = workInfo.getState().name();
                    textView.append(status + "\n");
                    Log.d(TAG, "onChanged: status: " + workInfo.getState().name());
//                    scheduleWork();
                }


            }
        });
    }

    /*private void cancelWork() {
        Log.d(TAG, "cancelWork: ");
        WorkManager.getInstance().cancelWorkById(workRequest.getId());
        scheduleWork();
    }*/

    private void scheduleWork() {
        Log.d(TAG, "scheduleWork: ");
        WorkManager.getInstance().enqueue(workRequest);
    }


}

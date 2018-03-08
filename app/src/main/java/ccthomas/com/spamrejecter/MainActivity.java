package ccthomas.com.spamrejecter;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ccthomas.com.spamrejecter.file.FileManagement;
import ccthomas.com.spamrejecter.object.IncomingCallReceiver;
import ccthomas.com.spamrejecter.object.Subset;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String phoneDirectoryFileName = "phone_directory.csv";
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;
    int typeSelected = Subset.FULL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask for Permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }

        FileManagement.setPhoneDirectory(this, phoneDirectoryFileName);

        setReceivers();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateSubsetList();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void setReceivers() {
        try {

            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            IncomingCallReceiver mReceiver = new IncomingCallReceiver();
            registerReceiver(mReceiver, filter);

        } catch (Exception e) { }
    }

    private void updateSubsetList() {
        FileManagement.savePhoneDirectory(this, phoneDirectoryFileName);
        TableLayout table = (TableLayout) findViewById(R.id.subset_table);
        table.removeAllViews();

        ArrayList<Subset> subsets = IncomingCallReceiver.getSubsets();
        System.out.println("Debug: " + subsets.size());
        for (int id = 0; id < subsets.size(); id++) {

            // Text View
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView textView = new TextView(this);
            textView.setText(subsets.get(id).getNumbers() + " " + subsets.get(id).getTypeAsString() + " " +subsets.get(id).getExceptionAsString());
            row.addView(textView);

            // Edit
            Button button = new Button(this);
            button.setId(id);
            button.setText("Edit");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), SubsetEditorActivity.class);
                    intent.putExtra("subsetID", v.getId());
                    startActivity(intent);
                }
            });
            row.addView(button);

            // Delete
            button = new Button(this);
            button.setId(1000 + id);
            button.setText("Delete");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    IncomingCallReceiver.removeSubsetWithID(v.getId() - 1000);
                    updateSubsetList();
                }
            });
            row.addView(button);

            // Add Row to Table
            table.addView(row);
        }


    }


    public void addNewNumberSubsetClicked(View view) {
        // startActivity(new Intent(MainActivity.this, SubsetEditorActivity.class));
        Intent intent = new Intent(getBaseContext(), SubsetEditorActivity.class);
        intent.putExtra("subsetID", -1);
        startActivity(intent);
    }

    //=============================================================================================
    // AdapterView.OnItemSelectedListener Methods
    //=============================================================================================

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        typeSelected = Subset.convertTypeStringToInt(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}

package ccthomas.com.spamrejecter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import ccthomas.com.spamrejecter.object.IncomingCallReceiver;
import ccthomas.com.spamrejecter.object.Subset;

/**
 * Created by CCThomasMac on 3/7/18.
 */

public class SubsetEditorActivity extends AppCompatActivity {
    private int selectedCheckBoxID = -1;
    private int subsetID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subset_editor);

        //int subsetID = Integer.parseInt(getIntent().getExtras().getString("subsetID"));
        Intent mIntent = getIntent();
        subsetID = mIntent.getIntExtra("subsetID", 0);

        if (subsetID != -1) {
            Subset subset = IncomingCallReceiver.getSubsetAtIndex(subsetID);

            // Set Number
            EditText editText = (EditText) findViewById(R.id.subset_numbers);
            editText.setText(subset.getNumbers());

            // Set Filter Type
            int filterType = subset.getType();
            switch (filterType) {
                case Subset.FULL:
                    setCheckBoxTrue(R.id.checkbox_full);
                    break;
                case Subset.PREFIX:
                    setCheckBoxTrue(R.id.checkbox_prefix);
                    break;
                case Subset.SUBSTRING:
                    setCheckBoxTrue(R.id.checkbox_substring);
                    break;
                case Subset.SUFFIX:
                    setCheckBoxTrue(R.id.checkbox_suffix);
                    break;
            }


            // Set Exception
            boolean exception = subset.getException();
            if (exception) {  // Natural State for Switch is False, so no need to set it.
                Switch switchWidget = (Switch) findViewById(R.id.switch_exception);
                switchWidget.setChecked(exception);
            }
        }

    }


    public void checkBoxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_full:
                if (checked) {
                    unCheckPrevious();
                    selectedCheckBoxID = R.id.checkbox_full;
                }
                break;
            case R.id.checkbox_prefix:
                if (checked) {
                    unCheckPrevious();
                    selectedCheckBoxID = R.id.checkbox_prefix;
                }
                break;
            case R.id.checkbox_substring:
                if (checked) {
                    unCheckPrevious();
                    selectedCheckBoxID = R.id.checkbox_substring;
                }
                break;
            case R.id.checkbox_suffix:
                if (checked) {
                    unCheckPrevious();
                    selectedCheckBoxID = R.id.checkbox_suffix;
                }
                break;
            default:
                selectedCheckBoxID = -1;
        }
    }

    private void setCheckBoxTrue(int id) {
        selectedCheckBoxID = id;
        CheckBox checkBox = (CheckBox) findViewById(selectedCheckBoxID);
        checkBox.setChecked(true);
    }

    private void unCheckPrevious() {
        if (selectedCheckBoxID != -1) {
            CheckBox checkBox = (CheckBox) findViewById(selectedCheckBoxID);
            checkBox.setChecked(false);
        }
    }

    public void saveButtonClicked(View view) {

        Switch switchObject = (Switch) findViewById(R.id.switch_exception);
        boolean exceptionIsChecked = switchObject.isChecked();

        EditText editText = (EditText) findViewById(R.id.subset_numbers);
        String numbers = String.valueOf(editText.getText());

        if (!numbers.equals("") && numbers.length() <= 12) {

            switch (selectedCheckBoxID) {
                case R.id.checkbox_full:
                    if (numbers.length() == 12 && numbers.substring(0, 1).equals("+") && numbers.substring(1).matches("[0-9]+")) {
                        createNewSubset(numbers, Subset.FULL, exceptionIsChecked);
                    }
                    else {
                        TextView textView = (TextView) findViewById(R.id.text_view_subset_of_numbers);
                        textView.setTextColor(Color.RED);
                    }
                    break;
                case R.id.checkbox_prefix:
                    if ((numbers.substring(0, 1).equals("+") && numbers.substring(1).matches("[0-9]+")) || numbers.matches("[0-9]+")) {
                        createNewSubset(numbers, Subset.PREFIX, exceptionIsChecked);
                    }
                    else {
                        TextView textView = (TextView) findViewById(R.id.text_view_subset_of_numbers);
                        textView.setTextColor(Color.RED);
                    }
                    break;
                case R.id.checkbox_substring:
                    if ((numbers.substring(0, 1).equals("+") && numbers.substring(1).matches("[0-9]+")) || numbers.matches("[0-9]+")) {
                        createNewSubset(numbers, Subset.SUBSTRING, exceptionIsChecked);
                    }
                    else {
                        TextView textView = (TextView) findViewById(R.id.text_view_subset_of_numbers);
                        textView.setTextColor(Color.RED);
                    }
                    break;
                case R.id.checkbox_suffix:
                    if ((numbers.substring(0, 1).equals("+") && numbers.substring(1).matches("[0-9]+")) || numbers.matches("[0-9]+")) {
                        createNewSubset(numbers, Subset.SUFFIX, exceptionIsChecked);
                    }
                    else {
                        TextView textView = (TextView) findViewById(R.id.text_view_subset_of_numbers);
                        textView.setTextColor(Color.RED);
                    }
                    break;
                default:
                    TextView textView = (TextView) findViewById(R.id.text_view_filter_type);
                    textView.setTextColor(Color.RED);
            }
        }
        else {
            TextView textView = (TextView) findViewById(R.id.text_view_subset_of_numbers);
            textView.setTextColor(Color.RED);
        }
    }

    public void cancelButtonClicked(View view) {
        finish();
    }

    private void createNewSubset(String numbers, int filterType, boolean exceptionIsChecked) {
        Subset subset = new Subset(numbers, filterType, exceptionIsChecked);
        if (subsetID == -1) IncomingCallReceiver.addSubset(subset);
        else IncomingCallReceiver.replaceSubsetAtIndex(subsetID, subset);
        finish();
    }
}

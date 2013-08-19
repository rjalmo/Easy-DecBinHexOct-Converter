package com.github.rjalmo.decbinhexoctconverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Ryan Almodovar (rjalmo)
 */
public class MainActivity extends Activity {
    
    public static final String DEC_VALUE = "com.github.rjalmo.decbinhexoctconverter.DEC_VALUE";
    public static final String BIN_VALUE = "com.github.rjalmo.decbinhexoctconverter.BIN_VALUE";
    public static final String HEX_VALUE = "com.github.rjalmo.decbinhexoctconverter.HEX_VALUE";
    public static final String OCT_VALUE = "com.github.rjalmo.decbinhexoctconverter.OCT_VALUE";
    
    private List<TextView> mOutputs;
    private List<Button> mButtons;
    private HashMap<ConvertType, List<Boolean>> mHashEnabledStates;
    
    private TextView mDecTVOutput, mBinTVOutput, mHexTVOutput, mOctTVOutput, mInputRef;
    private String mDecValue, mBinValue, mHexValue, mOctValue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDecValue = savedInstanceState.getString(DEC_VALUE);
            mBinValue = savedInstanceState.getString(BIN_VALUE);
            mHexValue = savedInstanceState.getString(HEX_VALUE);
            mOctValue = savedInstanceState.getString(OCT_VALUE);
        }
        
        setContentView(R.layout.activity_main);
        
        // init lists and hashmap
        mButtons = new ArrayList<Button>();
        mOutputs = new ArrayList<TextView>();
        mHashEnabledStates = new HashMap<ConvertType, List<Boolean>>();
        
        // init text views and set tags
        mDecTVOutput = (TextView) findViewById(R.id.output_dec);
        mDecTVOutput.setTag(ConvertType.DEC);
        mBinTVOutput = (TextView) findViewById(R.id.output_bin);
        mBinTVOutput.setTag(ConvertType.BIN);
        mHexTVOutput = (TextView) findViewById(R.id.output_hex);
        mHexTVOutput.setTag(ConvertType.HEX);
        mOctTVOutput = (TextView) findViewById(R.id.output_oct);
        mOctTVOutput.setTag(ConvertType.OCT);
        Collections.addAll(mOutputs, mDecTVOutput, mBinTVOutput, mHexTVOutput, mOctTVOutput);
        
        // add buttons to array, create hashmap for enabled button states
        addButtonsToList();
        createAndFillHash();
        
        // set decimal as starting input type
        switchInputType(ConvertType.DEC);
    }
    
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString(DEC_VALUE, mDecValue);
        saveInstanceState.putString(BIN_VALUE, mBinValue);
        saveInstanceState.putString(HEX_VALUE, mHexValue);
        saveInstanceState.putString(OCT_VALUE, mOctValue);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // keep portrait mode locked
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    /**
     * Switch the input option of the program based on the selectedKey
     * 
     * @param selectedKey the ConvertType tag associated with the textview outputs
     */
    private void switchInputType(ConvertType selectedKey) {
        
        // Set buttons enabled/disabled based on key
        List<Boolean> states = mHashEnabledStates.get(selectedKey);
        for (int i=0; i<mButtons.size(); ++i)
            mButtons.get(i).setEnabled(states.get(i));
        
        // Set input reference to the selected textview, change background colors
        mInputRef.setBackgroundResource(R.color.LightGray);
        for (TextView t : mOutputs) {
            if (t.getTag() == selectedKey) {
                mInputRef = t;
                mInputRef.setBackgroundResource(R.color.white);
                break;
            }
        }
    }
    
    /**
     * Respond to a TextView click
     */
    public void onTextClick(View v) {
        ConvertType key = (ConvertType) v.getTag();
        // don't switch if key same as current input
        if (mInputRef.getTag() != key)
            switchInputType(key);
    }
    
    /**
     * Respons to a button click
     */
    public void processInput(View v) {
        int id = v.getId();
        String startingText = mInputRef.getText().toString();
        
        switch (id) {
        case R.id.button_clear:
            // clear the textviews
            for (TextView t : mOutputs)
                t.setText("0");
            break;
        case R.id.button_del:
            // Delete the last character entered of current input
            int len = startingText.length();
            mInputRef.setText((len > 1) ? startingText.substring(0, len-1) : "0");
            convertAndUpdateTextOutputs();
            break;
        default:
            if (mDecTVOutput.length() >= 18) {
                Toast.makeText(getApplicationContext(), "Number too large", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Button b = (Button) findViewById(id);
            String btext = b.getText().toString();
            
            if (startingText.equals("0"))
                mInputRef.setText(btext);
            else
                mInputRef.append(btext);
            
            convertAndUpdateTextOutputs();
            break;
        }
    }
    
    private void convertAndUpdateTextOutputs() {
        // Remove all whitespace characters from the input
        String input = mInputRef.getText().toString().replaceAll("\\s", "");
        ConvertType from = (ConvertType) mInputRef.getTag();
        
        // Convert the input, set the result for each output textview
        for (TextView outputTV : mOutputs) {
            switch ((ConvertType) outputTV.getTag()) {
            case BIN:
                mBinValue = RadixConverter.toBin(input, from);
                outputTV.setText(insertSpacing(mBinValue, 4));
                break;
            case DEC:
                mDecValue = RadixConverter.toDec(input, from);
                outputTV.setText(insertSpacing(mDecValue, 3));
                break;
            case HEX:
                mHexValue = RadixConverter.toHex(input, from).toUpperCase(Locale.ENGLISH);
                outputTV.setText(insertSpacing(mHexValue, 3));
                break;
            case OCT:
                mOctValue = RadixConverter.toOct(input, from);
                outputTV.setText(insertSpacing(mOctValue, 3));
                break;
            default:
                break;
            }
        }
        
    }
    
    /**
     * Insert whitespaces in the given text for readability.
     * 
     * @param start the starting text
     * @param step number of characters to step to add a whitespace
     * @return the string with inserted spaces
     */
    private String insertSpacing(String start, int step) {
        StringBuilder sb = new StringBuilder(start);
        for (int i = sb.length() - step; i >= 0; i -= step) {
            sb.insert(i,' ');
        }
        return sb.toString();
    }
    
    private void addButtonsToList() {
        Collections.addAll(mButtons
                , (Button) findViewById(R.id.button_0)
                , (Button) findViewById(R.id.button_1)
                , (Button) findViewById(R.id.button_2)
                , (Button) findViewById(R.id.button_3)
                , (Button) findViewById(R.id.button_4)
                , (Button) findViewById(R.id.button_5)
                , (Button) findViewById(R.id.button_6)
                , (Button) findViewById(R.id.button_7)
                , (Button) findViewById(R.id.button_8)
                , (Button) findViewById(R.id.button_9)
                , (Button) findViewById(R.id.button_A)
                , (Button) findViewById(R.id.button_B)
                , (Button) findViewById(R.id.button_C)
                , (Button) findViewById(R.id.button_D)
                , (Button) findViewById(R.id.button_E)
                , (Button) findViewById(R.id.button_F));
        Collections.unmodifiableList(mButtons);
    }
    
    /**
     * HashMap: key is the ConvertType; values are boolean arrays corresponding to the
     * button-enabled states of the selected ConvertType.
     * e.g. Binary:  0 and 1 true; 2 to 15 false
     *      Decimal: 0 to 9 true; 10 to 15 false
     *      Octal:   0 to 7 true;  
     *      Hex:     0 to 15 (all) true
     *      
     */
    private void createAndFillHash() {
        if (mButtons == null) return;
        
        int size = mButtons.size();
        List<Boolean> toggleVals = new ArrayList<Boolean>(size);
        
        // Hexadecimal entry; all enabled states are true
        while (toggleVals.size() < size)
            toggleVals.add(true);
        mHashEnabledStates.put(ConvertType.HEX, new ArrayList<Boolean>(toggleVals));
        
        // Decimal entry; 0-9 true
        for (int i=15; i>=10; --i)
            toggleVals.set(i, false);
        mHashEnabledStates.put(ConvertType.DEC, new ArrayList<Boolean>(toggleVals));
        
        // Octal entry; set 0-7 to true
        toggleVals.set(9, false);
        toggleVals.set(8, false);
        mHashEnabledStates.put(ConvertType.OCT, new ArrayList<Boolean>(toggleVals));
        
        // Binary entry; set 0-1 to true
        for (int i=7; i>=2; --i)
            toggleVals.set(i, false);
        mHashEnabledStates.put(ConvertType.BIN, toggleVals);        
    }
    
}

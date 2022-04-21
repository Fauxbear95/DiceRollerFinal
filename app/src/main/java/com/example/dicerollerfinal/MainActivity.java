package com.example.dicerollerfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
{
    ListView historyDisplay;
    Spinner spinner;
    TextView rollDisplay;
    EditText customDiceET, numberOfRolls;
    Button roll, add, historyClear;
    ArrayList<String> spinnerList;
    ArrayList<String> originalHistory;
    ArrayList<String> copyHistory;
    ArrayAdapter<String> historyAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sets defaults for your preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //text view
        rollDisplay = findViewById(R.id.rollDisplay);

        //edit text views
        customDiceET = findViewById(R.id.customDiceEditText);
        numberOfRolls = findViewById(R.id.numberOfRolls);

        //buttons
        add = findViewById(R.id.Add);
        roll = findViewById(R.id.Roll);
        historyClear = findViewById(R.id.historyClear);
        historyDisplay = findViewById(R.id.historyListView);

        //instantiating arraylists to be used for my history display
        originalHistory = new ArrayList<>();
        copyHistory = new ArrayList<>();

        //setting up my adapter
        historyAdapter = new ArrayAdapter<>(this, R.layout.history, R.id.historyEntry, copyHistory);
        historyDisplay.setAdapter(historyAdapter);

        /**
         * Sets an a long click listener on the items in my listview, which will remove them.
         */
        historyDisplay.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String historyView = (String)adapterView.getItemAtPosition(i);

                originalHistory.remove(historyView);
                copyHistory.remove(historyView);


                UpdateScreen();
                return true;
            }
        });

        //prepared a list for my spinner with its base values
        spinnerList = new ArrayList<>();
        spinnerList.add("Dice List");
        spinnerList.add("D4");
        spinnerList.add("D6");
        spinnerList.add("D8");
        spinnerList.add("D10");
        spinnerList.add("D12");
        spinnerList.add("D20");
        spinnerList.add("D100");

        spinner = findViewById(R.id.Spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        add.setOnClickListener(this);
        roll.setOnClickListener(this);
        historyClear.setOnClickListener(this);
    }

    protected void onStart()
    {
        super.onStart();
        boolean stopper = true;
        int historyIndex = 0;
        while(stopper)
        {
            final String history = sharedPreferences.getString(String.valueOf(historyIndex), "");

            if(history.equals(""))
            {
                stopper = false;
            }
            else
            {
                originalHistory.add(history);
            }
            ++historyIndex;
        }
        int diceIndex = 8;

        stopper = true;
        while(stopper)
        {
            final String diceValues = sharedPreferences.getString((String.valueOf(diceIndex) + "a"), "");

            if(diceValues.equals(""))
            {
                stopper = false;
            }
            else
            {
                spinnerList.add(diceValues);
            }
            ++diceIndex;
        }
        UpdateScreen();
    }

    protected void onStop()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean willSaveHistory = sharedPreferences.getBoolean("save_values_preferences", false);

        if (willSaveHistory)
        {
            for (int historyIndex = 0; historyIndex < originalHistory.size(); ++ historyIndex)
            {
                editor.putString(String.valueOf(historyIndex), originalHistory.get(historyIndex));
            }

            if (spinnerList.size() > 8)
            {
                for (int diceIndex = 8; diceIndex < spinnerList.size(); ++diceIndex)
                {
                    editor.putString(String.valueOf(diceIndex) + "a", spinnerList.get(diceIndex));
                }
            }

        }
        else
        {
            editor.clear();
            editor.putBoolean("save_values_preferences", false);
        }
        editor.apply();
        super.onStop();
    }

    protected void onRestart()
    {
        super.onRestart();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean willSaveHistory = sharedPreferences.getBoolean("save_values_preferences", false);
        if (willSaveHistory)
        {
            for (int historyIndex = 0; historyIndex < originalHistory.size(); ++historyIndex)
            {
                editor.putString(String.valueOf(historyIndex), originalHistory.get(historyIndex));
            }
            for (int diceValue = 8; diceValue < spinnerList.size(); ++diceValue)
            {
                editor.putString(String.valueOf(diceValue) + "a", spinnerList.get(diceValue));
            }
        }
        else
        {
            editor.clear();
            editor.putBoolean("save_values_preferences", false);
        }
        editor.apply();
    }



    /** This creates the menu for the buttons to be added*/
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    /** This is my method to read from the activity bar when a button is clicked*/
    public boolean onOptionsItemSelected(MenuItem item){
        long id = item.getItemId();
        if (id == R.id.about_menu)
        {
            Toast.makeText(this,
                    "Developer: Daniel Faubert \nStudent Num: A00124623\n",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.settings_menu)
        {
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.Add)
        {
            String input = "D" + customDiceET.getText();
            customDiceET.setText(null);
            spinnerList.add(input);
        }
        else if(view.getId() == R.id.Roll && !spinner.getSelectedItem().toString().equals("Dice List \u25BC"))
        {
            String text = spinner.getSelectedItem().toString();
            int maxValue = Integer.parseInt(text.replace("D", ""));
            int rollCount = Integer.parseInt(String.valueOf(numberOfRolls.getText()));
            int totalReturn = 0;
            for (int i = 0; i < rollCount; ++i)
            {
                int randomVal = (int)(Math.random() * maxValue) + 1;
                totalReturn += randomVal;
            }

            String displayText = rollCount + text + " = " + totalReturn;

            originalHistory.add(displayText);
            copyHistory.add(displayText);
            rollDisplay.setText(displayText);
            UpdateScreen();
        }
        else if (view.getId() == R.id.historyClear)
        {
            originalHistory.clear();
            copyHistory.clear();
            UpdateScreen();
        }
    }

    public void UpdateScreen()
    {
        historyAdapter.clear();
        historyAdapter.addAll(originalHistory);
    }

}
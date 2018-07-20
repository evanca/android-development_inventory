package com.example.ivanna.inventory;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.action_settings);

    }

    public void deleteEverything(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_all_items_msg));
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // Here we delete ALL the data using ContentResolver().delete
                int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);

                if (rowsDeleted == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.delete_all_items_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.delete_all_items_successful),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.setTitle(getString(R.string.warning));
        builder.setIcon(R.drawable.delete);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public static class InventoryPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        EditTextPreference editTextPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            editTextPreference = (EditTextPreference) findPreference(getString(R.string.settings_currency_key_custom));

            // If pre-defined currency is already selected, enable EditTextPreference:
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String currency = sharedPrefs.getString(
                    getString(R.string.settings_currency_key),
                    getString(R.string.settings_currency_EUR));
            if (currency.equals(getString(R.string.settings_currency_custom_title))) {
                editTextPreference.setEnabled(true);
            }

            final ListPreference listPreference = (ListPreference) findPreference(getString(R.string.settings_currency_key));

            // Custom OnPreferenceChangeListener to check if user wants to use a pre-defined or a custom currency:
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {

                    int index = listPreference.findIndexOfValue(value.toString());

                    if (index != -1) {
                        // Check if a "Custom" option is selected
                        if (listPreference.getEntries()[index].equals(getString(R.string.settings_currency_custom_title))) {
                            // User prefers to use a custom currency, so enable EditTextPreference
                            editTextPreference.setEnabled(true);
                        } else {
                            // Pre-defined currency is selected, so disable EditTextPreference
                            editTextPreference.setText("");
                            editTextPreference.setSummary("");
                            editTextPreference.setEnabled(false);
                        }
                    }
                    return true;
                }
            });

            Preference maxQuantityPref = findPreference(getString(R.string.settings_max_quantity_key));
            bindPreferenceSummaryToValue(maxQuantityPref);

            Preference customCurrency = findPreference(getString(R.string.settings_currency_key_custom));
            bindPreferenceSummaryToValue(customCurrency);

            Preference stepPref = findPreference(getString(R.string.settings_step_key));
            bindPreferenceSummaryToValue(stepPref);

            Preference creditsPref = findPreference(getString(R.string.settings_credits_key));
            creditsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.credits_summary));
                    builder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    builder.setTitle(getString(R.string.settings_credits_no_symbol));
                    builder.setIcon(R.drawable.information_outline);

                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return true;
                }
            });
        }

        // Called when a Preference has been changed by the user. This is called before the state of
        // the Preference is about to be updated and before the state is persisted.
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }

        // Binds a preference's summary to its value. More specifically, when the preference's value
        // is changed, its summary (line of text below the preference title) is updated to reflect
        // the value. The summary is also immediately updated upon calling this method. The exact
        // display format is dependent on the type of preference.
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);

        }

    }

}


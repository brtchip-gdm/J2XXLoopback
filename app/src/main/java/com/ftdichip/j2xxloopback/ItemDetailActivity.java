package com.ftdichip.j2xxloopback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    int devTest = 0;
    public static D2xxManager ftD2xx = null;
    FT_Device ftDevice = null;
    //Context ctx = this.getBaseContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        try {
            ftD2xx = D2xxManager.getInstance(this.getBaseContext());
        } catch (D2xxException ex) {
            ex.printStackTrace();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast myToast;
                FT_Device ftDevice = null;
                Context ctx = view.getContext();
                byte[] myData = new byte[96];

                myData = "UuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUuUu".getBytes();

                //myToast = Toast.makeText(ctx, String.format("%d Sending string to device %d", ftDevice, devTest), Toast.LENGTH_LONG);
                //myToast.show();

                Log.i("Loopback testing ",
                        "Device number = " + devTest);

                myToast = Toast.makeText(ctx, "using " + ftDevice, Toast.LENGTH_SHORT);
                myToast.show();
                //ftDevice.write(myData, 96, true);
                myData = "cleared".getBytes();
                myToast = Toast.makeText(ctx, "Written to " + devTest, Toast.LENGTH_SHORT);
                myToast.show();
                //ftDevice.read(myData, 96, 1000);
                myToast = Toast.makeText(ctx, "Read from " + myData.toString(), Toast.LENGTH_LONG);
                myToast.show();

                Log.i("Loopback testing ",
                        "Device number = " + myData.toString());
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
            devTest = Integer.parseInt(getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            Toast myToast;

            //Snackbar.make(getContentView, String.format("Loopback on USB device %d", devTest), Snackbar.LENGTH_LONG)
              //      .setAction("Action", null).show();

            //devCount = ftD2xx.createDeviceInfoList(ctx);
            Log.i("Loopback testing ",
                    "Device number = " + devTest);

            Context ctx = this.getBaseContext();
            try {
                ftDevice = ftD2xx.openByIndex(ctx, devTest);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }

            if (ftDevice != null)
            {
                myToast = Toast.makeText(ctx, "Opened device " + devTest, Toast.LENGTH_SHORT);
                myToast.show();
                Log.i("Loopback testing ",
                        "Opened device " + devTest);
                ftDevice.resetDevice();

                byte[] myData = new byte[96];
                myData = "Hello".getBytes();

                ftDevice.write(myData, myData.length, true);

                myData = "empty".getBytes();
                ftDevice.read(myData, myData.length, 1000);

                myToast = Toast.makeText(ctx, "Loopback " + myData.toString(), Toast.LENGTH_SHORT);
                myToast.show();
                Log.i("Loopback testing ",
                        "Data " + myData.toString());
            } else {
                myToast = Toast.makeText(ctx, "Could not open device " + devTest, Toast.LENGTH_SHORT);
                myToast.show();
                Log.i("Loopback testing ",
                        "Failed to open device " + devTest);
            }
        }
    }
    @Override
    protected void onDestroy() {
        if (ftDevice != null) {
            ftDevice.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

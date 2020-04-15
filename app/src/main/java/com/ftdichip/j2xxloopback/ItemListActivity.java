package com.ftdichip.j2xxloopback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.ftdichip.j2xxloopback.classDeviceInfo.classDeviceInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import static com.ftdichip.j2xxloopback.classDeviceInfo.classDeviceInfo.ITEMS;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static D2xxManager ftD2xx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        try {
            ftD2xx = D2xxManager.getInstance(this);
        } catch (D2xxManager.D2xxException ex) {
            ex.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        final View recyclerView1 = findViewById(R.id.recyclerView1);
        assert recyclerView1 != null;
        setupRecyclerView((RecyclerView) recyclerView1, classDeviceInfo.classDeviceInfoItem.SOURCE);
        final View recyclerView2 = findViewById(R.id.recyclerView2);
        assert recyclerView2 != null;
        setupRecyclerView((RecyclerView) recyclerView2, classDeviceInfo.classDeviceInfoItem.DEST);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FT_Device ftDevice = null;
                Context ctx = view.getContext();
                Toast myToast;
                int devCount = 0;

                classDeviceInfo.clearItems();

                Snackbar.make(view, "Scanning USB for devices", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                devCount = ftD2xx.createDeviceInfoList(ctx);
                Log.i("Scanning ",
                        "Device number = " + Integer.toString(devCount));

                if (devCount > 0) {
                    myToast = Toast.makeText(ctx, String.format("%d devices!", devCount), Toast.LENGTH_SHORT);

                    D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
                    ftD2xx.getDeviceInfoList(devCount, deviceList);
                    for (int dev = 0; dev < devCount; dev++) {
                        try {
                            // Try and open each device as we find it.
                            ftDevice = ftD2xx.openByIndex(ctx, dev);
                            ftDevice.resetDevice();
                            ftDevice.close();
                            classDeviceInfo.addItem(deviceList[dev]);
                        } catch (RuntimeException ex) {
                            ex.printStackTrace();
                        }
                    }

                } else {
                    D2xxManager.FtDeviceInfoListNode dummy = new D2xxManager.FtDeviceInfoListNode();
                    dummy.description = "dummy device";
                    dummy.serialNumber = "FTserial";
                    dummy.id = 0;
                    classDeviceInfo.addItem(dummy);

                    myToast = Toast.makeText(ctx, String.format("There are no devices!"), Toast.LENGTH_SHORT);
                }
                myToast.show();
                ((RecyclerView) recyclerView1).getAdapter().notifyDataSetChanged();
                ((RecyclerView) recyclerView2).getAdapter().notifyDataSetChanged();
            }
        });

        FloatingActionButton fabAddDummy = findViewById(R.id.fabAddDummy);
        fabAddDummy.setOnClickListener( new View.OnClickListener()
                                        {

                                            @Override
                                            public void onClick(View view) {
                                                Log.i("fab","fabAddDummy onClick");
                                            }
                                        }


        );
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, int function) {
        SimpleItemRecyclerViewAdapter rcv = new SimpleItemRecyclerViewAdapter(this, ITEMS);
        recyclerView.setAdapter(rcv);
        rcv.setFunction(function);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<classDeviceInfo.classDeviceInfoItem> mValues;
        private CheckBox lastChecked = null;
        private int lastCheckedPos = 0;
        private int function = classDeviceInfo.classDeviceInfoItem.SOURCE;

        public void setFunction(int function)
        {
            this.function = function;
        }

        /*private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classDeviceInfo.classDeviceInfoItem item = (classDeviceInfo.classDeviceInfoItem) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                context.startActivity(intent);
            }
        };*/
        /*private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public void onClick(View view) {
                classDeviceInfo.classDeviceInfoItem item = (classDeviceInfo.classDeviceInfoItem) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                context.startActivity(intent);
            }
        };*/

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<classDeviceInfo.classDeviceInfoItem> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).node.description);
            holder.mCheckView.setTag(new Integer(position));
            holder.mCheckView.setChecked(mValues.get(position).isSelected(function));

            holder.itemView.setTag(mValues.get(position));
            //holder.itemView.setOnClickListener(mOnClickListener);

            //for default check in first item
            if (position == 0 && mValues.get(0).isSelected(function) && holder.mCheckView.isChecked()) {
                lastChecked = holder.mCheckView;
                lastCheckedPos = 0;
            }

            holder.mCheckView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    CheckBox cb = (CheckBox)v;
                    int clickedPos = ((Integer)cb.getTag()).intValue();

                    if(cb.isChecked())
                    {
                        if(lastChecked != null)
                        {
                            lastChecked.setChecked(false);
                            mValues.get(lastCheckedPos).setSelected(function, false);
                        }

                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                    }
                    else
                        lastChecked = null;
                    mValues.get(clickedPos).setSelected(function, cb.isChecked());
                }
            });
            holder.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    classDeviceInfo.classDeviceInfoItem item = (classDeviceInfo.classDeviceInfoItem) view.getTag();
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            });
            holder.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    classDeviceInfo.classDeviceInfoItem item = (classDeviceInfo.classDeviceInfoItem) view.getTag();
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final CheckBox mCheckView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mCheckView = (CheckBox) view.findViewById(R.id.checkbox);
            }
        }
    }
}

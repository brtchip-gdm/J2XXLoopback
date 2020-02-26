package com.ftdichip.j2xxloopback.classDeviceInfo;

import com.ftdi.j2xx.D2xxManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class classDeviceInfo {
    /**
     * An array of sample (classDeviceInfo) items.
     */
    public static final List<classDeviceInfoItem> ITEMS = new ArrayList<classDeviceInfoItem>();

    public static final Map<String, classDeviceInfoItem> ITEM_MAP = new HashMap<String, classDeviceInfoItem>();

    private static void addItem(classDeviceInfoItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void addItem(D2xxManager.FtDeviceInfoListNode node) {
        String count = String.valueOf(ITEMS.size());
        classDeviceInfoItem item = new classDeviceInfoItem(count, node);
        ITEMS.add(item);
        ITEM_MAP.put(count, item);
    }

    public static void clearItems() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static String makeDetails(int position) {
        D2xxManager.FtDeviceInfoListNode node = ITEMS.get(position).node;
        return node.toString();
    }

    /**
     * A classDeviceInfo item representing a piece of content.
     */
    public static class classDeviceInfoItem {
        public static final int SOURCE  = 426;
        public static final int DEST  = 427;
        public final String id;
        public final D2xxManager.FtDeviceInfoListNode node;
        public boolean selectedSrc;
        public boolean selectedDest;

        public classDeviceInfoItem(String id, D2xxManager.FtDeviceInfoListNode node) {
            this.id = id;
            this.node = node;
            this.selectedSrc = false;
            this.selectedDest = false;
        }

        public boolean isSelected(int either) {
            if (either == SOURCE) {
                return selectedSrc;
            }
            return selectedDest;
        }
        public void setSelected(int either, boolean select) {
            if (either == SOURCE) {
                this.selectedSrc = select;
            } else {
                this.selectedDest = select;
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Details about Item: ").append(id);
            builder.append("\nDescription: ").append(node.description);
            builder.append("\nSerial Number: ").append(node.serialNumber);
            builder.append("\nType: ");
            if (D2xxManager.FT_DEVICE_232B == node.type) builder.append("FT232B");
            if (D2xxManager.FT_DEVICE_8U232AM == node.type) builder.append("FT232AM");
            if (D2xxManager.FT_DEVICE_2232 == node.type) builder.append("FT2232");
            if (D2xxManager.FT_DEVICE_232R == node.type) builder.append("FT232R");
            if (D2xxManager.FT_DEVICE_245R == node.type) builder.append("FT245R");
            if (D2xxManager.FT_DEVICE_2232H == node.type) builder.append("FT2232H");
            if (D2xxManager.FT_DEVICE_4232H == node.type) builder.append("FT4232H");
            if (D2xxManager.FT_DEVICE_232H == node.type) builder.append("FT232H");
            if (D2xxManager.FT_DEVICE_X_SERIES == node.type) builder.append("FT-X");
            if (D2xxManager.FT_DEVICE_4222_0 == node.type) builder.append("FT4222 Rev A");
            if (D2xxManager.FT_DEVICE_4222_1_2 == node.type) builder.append("FT4222 Rev B or C");
            if (D2xxManager.FT_DEVICE_4222_3 == node.type) builder.append("FT4222");
            builder.append("\nBCD: ").append(String.format("0x%04x", node.bcdDevice));

            return builder.toString();
        }
    }
}

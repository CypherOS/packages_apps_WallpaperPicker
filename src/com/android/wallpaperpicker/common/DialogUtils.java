package com.android.wallpaperpicker.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.android.wallpaperpicker.R;

/**
 * Utility class used to show dialogs for things like picking which wallpaper to set.
 */
public class DialogUtils {
    /**
     * Calls cropTask.execute(), once the user has selected which wallpaper to set. On pre-N
     * devices, the prompt is not displayed since there is no API to set the lockscreen wallpaper.
     *
     * TODO: Don't use CropAndSetWallpaperTask on N+, because the new API will handle cropping instead.
     */
    public static void executeCropTaskAfterPrompt(
            Context context, final AsyncTask<Integer, ?, ?> cropTask,
            DialogInterface.OnCancelListener onCancelListener) {
				
        final Icon[] icons = {
            new Icon(R.drawable.ic_home),
            new Icon(R.drawable.ic_lockscreen),
            new Icon(R.drawable.ic_both),
        };
				
		ListAdapter adapter = new ArrayAdapter<Integer>(context,
                android.R.layout.select_dialog_item,
                android.R.id.text1, icons) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String[] items = context.getResources().getStringArray(
                        R.array.which_wallpaper_options);
                float padding = context.getResources().getDimensionPixelSize(
                        R.dimen.wallpaper_text_padding);
                float leftPadding = context.getResources().getDimensionPixelSize(
                        R.dimen.wallpaper_text_padding_left);
                float textSize = context.getResources().getDimensionPixelSize(
                        R.dimen.wallpaper_text_size);

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setText(items[position]);
                text.setTextSize(textSize);
                text.setCompoundDrawablesWithIntrinsicBounds(icons.get(position), 0, 0, 0);
                text.setPadding((int) leftPadding, 0, 0, 0);
                text.setCompoundDrawablePadding((int) padding);

                return view;
            }
        };
        if (Utilities.isAtLeastN()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.wallpaper_instructions)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemIndex) {
                        int whichWallpaper;
                        if (selectedItemIndex == 0) {
                            whichWallpaper = WallpaperManagerCompat.FLAG_SET_SYSTEM;
                        } else if (selectedItemIndex == 1) {
                            whichWallpaper = WallpaperManagerCompat.FLAG_SET_LOCK;
                        } else {
                            whichWallpaper = WallpaperManagerCompat.FLAG_SET_SYSTEM
                                    | WallpaperManagerCompat.FLAG_SET_LOCK;
                        }
                        cropTask.execute(whichWallpaper);
                    }
                })
                .setOnCancelListener(onCancelListener)
                .show();
        } else {
            cropTask.execute(WallpaperManagerCompat.FLAG_SET_SYSTEM);
        }
    }
	
	public static class Icon{
        public final int icon;
        public Icon(Integer icon) {
            this.icon = icon;
        }
    }
}

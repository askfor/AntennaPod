package de.danoeh.antennapod.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.PodcastApp;
import de.danoeh.antennapod.R;

/** Let's the user choose a directory on the storage device. */
public class DirectoryChooserActivity extends SherlockActivity {
	private static final String TAG = "DirectoryChooserActivity";

	private Button butConfirm;
	private Button butCancel;
	private ImageButton butNavUp;
	private TextView txtvSelectedFolder;
	private ListView listDirectories;

	private ArrayAdapter<String> listDirectoriesAdapter;
	private ArrayList<String> filenames;
	private File selectedDir;
	private File[] filesInDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(PodcastApp.getThemeResourceId());
		super.onCreate(savedInstanceState);

		setContentView(R.layout.directory_chooser);
		butConfirm = (Button) findViewById(R.id.butConfirm);
		butCancel = (Button) findViewById(R.id.butCancel);
		butNavUp = (ImageButton) findViewById(R.id.butNavUp);
		txtvSelectedFolder = (TextView) findViewById(R.id.txtvSelectedFolder);
		listDirectories = (ListView) findViewById(R.id.directory_list);

		butConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		butCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listDirectories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				if (AppConfig.DEBUG)
					Log.d(TAG, "Selected index: " + position);
				if (filesInDir != null && position >= 0
						&& position < filesInDir.length) {
					changeDirectory(filesInDir[position]);
				}
			}
		});

		butNavUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				File parent = null;
				if (selectedDir != null
						&& (parent = selectedDir.getParentFile()) != null) {
					changeDirectory(parent);
				}
			}
		});

		filenames = new ArrayList<String>();
		listDirectoriesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, filenames);
		listDirectories.setAdapter(listDirectoriesAdapter);
		changeDirectory(Environment.getExternalStorageDirectory());
	}

	private void changeDirectory(File dir) {
		if (dir != null && dir.isDirectory()) {
			File[] contents = dir.listFiles();
			if (contents != null) {
				int numDirectories = 0;
				for (File f : contents) {
					if (f.isDirectory()) {
						numDirectories++;
					}
				}
				filesInDir = new File[numDirectories];
				filenames.clear();
				for (int i = 0, counter = 0; i < numDirectories; counter++) {
					if (contents[counter].isDirectory()) {
						filesInDir[i] = contents[counter];
						filenames.add(contents[counter].getName());
						i++;
					}
				}
				Arrays.sort(filesInDir);
				Collections.sort(filenames);
				selectedDir = dir;
				txtvSelectedFolder.setText(dir.getAbsolutePath());
				listDirectoriesAdapter.notifyDataSetChanged();
				if (AppConfig.DEBUG)
					Log.d(TAG, "Changed directory to " + dir.getAbsolutePath());
			} else {
				if (AppConfig.DEBUG) Log.d(TAG, "Could not change folder: contents of dir were null");
			}
		} else {
			if (dir == null) {
				if (AppConfig.DEBUG)
					Log.d(TAG, "Could not change folder: dir was null");
			} else {
				if (AppConfig.DEBUG)
					Log.d(TAG, "Could not change folder: dir is no directory");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.directory_chooser, menu);
		return true;
	}
}
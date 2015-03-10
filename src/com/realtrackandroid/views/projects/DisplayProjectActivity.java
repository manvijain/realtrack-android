package com.realtrackandroid.views.projects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.GlossaryDialog;
import com.realtrackandroid.views.help.HelpDialog;

/*
 * Presents an activity that displays details of an existing project
 * Also lets you edit the project (EditProjectActivity) or delete the project (right from this java file)
 * by choosing buttons in the ActionBar
 * Pressing the back key will exit the activity
 */
public class DisplayProjectActivity extends SherlockFragmentActivity {
  private int id;

  private Project p;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_displayproject);

    // read in the ID of the project that this activity must display details of
    id = getIntent().getExtras().getInt("projectid");
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    p = pDao.getProjectWithId(id);
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(p.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    Date d = new Date(p.getStartDate());
    TextView startDate = (TextView) findViewById(R.id.startDate);
    startDate.setText(parser.format(d));
    d = new Date(p.getEndDate());
    TextView endDate = (TextView) findViewById(R.id.endDate);
    endDate.setText(parser.format(d));
    TextView notes = (TextView) findViewById(R.id.notes);
    if (p.getNotes().length() > 0)
      notes.setText("Notes:\n" + p.getNotes());
    TextView load = (TextView) findViewById(R.id.load);
    if (p.getLoad().length() > 0)
      load.setText("Load:\n" + p.getLoad());
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.displayprojectmenu, menu);

    getSupportActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }

  /**
   * ***********************************************************************************************
   * ******************* transition to view for adding new project when the add icon in the action
   * bar is clicked
   * *********************************************************************************
   * *********************************
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
	if (itemId == android.R.id.home) {
		// provide a back button on the actionbar
        finish();
	} else if (itemId == R.id.action_deleteproject) {
		// warn the user first!
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this project? This CANNOT be undone.")
                .setCancelable(false).setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
                    pDao.deleteProject(DisplayProjectActivity.this.id);
                    finish();
                  }
                }).show();
	} else if (itemId == R.id.action_editproject) {
		Intent i = new Intent(DisplayProjectActivity.this, EditProjectActivity.class);
		i.putExtra("projectid", id);
		startActivity(i);
		overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
	} else if (itemId == R.id.action_help) {
		HelpDialog helpDialog = new HelpDialog();
		helpDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		helpDialog.show(getSupportFragmentManager(), "helpdialog");
	} else if (itemId == R.id.action_framework) {
		FrameworkInfoDialog frameworkInfoDialog = new FrameworkInfoDialog();
		frameworkInfoDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		frameworkInfoDialog.show(getSupportFragmentManager(), "frameworkinfodialog");
	} else if (itemId == R.id.action_glossary) {
		GlossaryDialog glossaryDialog = new GlossaryDialog();
		glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		glossaryDialog.show(getSupportFragmentManager(), "glossarydialog");
	} else {
		return super.onOptionsItemSelected(item);
	}

    return true;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}
package com.realtrackandroid.backend.projects;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.realtrackandroid.backend.GlobalDatabaseHelper;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.projects.Project;

public class ProjectDAO {
  private GlobalDatabaseHelper opener;

  private SQLiteDatabase readDatabase;

  private SQLiteDatabase writeDatabase;

  private Context context;

  public ProjectDAO(Context context) {
    this.context = context;
    this.opener = GlobalDatabaseHelper.getInstance(context);
    this.readDatabase = opener.getReadableDatabase();
    this.writeDatabase = opener.getWritableDatabase();
    closeDB();
  }

  private void openDB() {
    if (!readDatabase.isOpen()) {
      readDatabase = opener.getReadableDatabase();
    }
    if (!writeDatabase.isOpen()) {
      writeDatabase = opener.getWritableDatabase();
    }
  }

  private void closeDB() {
    if (readDatabase.isOpen()) {
      readDatabase.close();
    }
    if (writeDatabase.isOpen()) {
      writeDatabase.close();
    }
  }

  public ArrayList<Project> getAllProjects() {
    openDB();
    ArrayList<Project> output = null;
    String[] columnsToRead = new String[6];
    columnsToRead[0] = Project.COLUMN_TITLE;
    columnsToRead[1] = Project.COLUMN_STARTDATE;
    columnsToRead[2] = Project.COLUMN_ENDDATE;
    columnsToRead[3] = Project.COLUMN_NOTES;
    columnsToRead[4] = Project.COLUMN_ID;
    columnsToRead[5] = Project.COLUMN_LOAD;
    Cursor returnData = readDatabase.query(Project.PROJECT_TABLE, columnsToRead, null, null, null,
            null, null,null);
    output = extractProjects(returnData);
    closeDB();
    return output;
  }

  public Project getProjectWithId(int id) {
    openDB();
    String[] columnsToRead = new String[6];
    columnsToRead[0] = Project.COLUMN_TITLE;
    columnsToRead[1] = Project.COLUMN_STARTDATE;
    columnsToRead[2] = Project.COLUMN_ENDDATE;
    columnsToRead[3] = Project.COLUMN_NOTES;
    columnsToRead[4] = Project.COLUMN_ID;
    columnsToRead[5] = Project.COLUMN_LOAD;
    String whereClause = Project.COLUMN_ID + '=' + id;
    Cursor returnData = readDatabase.query(Project.PROJECT_TABLE, columnsToRead, whereClause, null,
            null, null, null,null);
    returnData.moveToFirst();
    Project p = new Project();
    p.setTitle(returnData.getString(0));
    p.setStartDate(returnData.getLong(1));
    p.setEndDate(returnData.getLong(2));
    p.setNotes(returnData.getString(3));
    p.setId(Integer.parseInt(returnData.getString(4)));
    p.setLoad(returnData.getString(5));
    closeDB();
    // Return the constructed Project
    return p;
  }

  private ArrayList<Project> extractProjects(Cursor returnData) {
    // The output ArrayList is initialized
    ArrayList<Project> output = new ArrayList<Project>();
    // Move the counter to the first item in the return data
    returnData.moveToFirst();
    int count = 0;
    // While there are still values in the return data
    while (!returnData.isAfterLast()) {
      // Add the new Project to the ArrayList
      Project p = new Project();
      p.setTitle(returnData.getString(0));
      p.setStartDate(returnData.getLong(1));
      p.setEndDate(returnData.getLong(2));
      p.setNotes(returnData.getString(3));
      p.setId(Integer.parseInt(returnData.getString(4)));
      p.setLoad(returnData.getString(5));
      output.add(count, p);
      // Advance the Cursor
      returnData.moveToNext();
      // Advance the counter
      count++;
    }
    // Return the ArrayList
    return output;
  }

  public void addProject(Project project) {
    openDB();
    ContentValues newValue = new ContentValues(4);
    newValue.put(Project.COLUMN_TITLE, project.getTitle());
    newValue.put(Project.COLUMN_STARTDATE, project.getStartDate());
    newValue.put(Project.COLUMN_ENDDATE, project.getEndDate());
    newValue.put(Project.COLUMN_NOTES, project.getNotes());
    newValue.put(Project.COLUMN_LOAD, project.getLoad());
    // Insert the item into the database
    writeDatabase.insert(Project.PROJECT_TABLE, null, newValue);
    closeDB();
  }

  public void updateProject(Project project) {
    openDB();
    ContentValues newValue = new ContentValues(4);
    newValue.put(Project.COLUMN_TITLE, project.getTitle());
    newValue.put(Project.COLUMN_STARTDATE, project.getStartDate());
    newValue.put(Project.COLUMN_ENDDATE, project.getEndDate());
    newValue.put(Project.COLUMN_NOTES, project.getNotes());
    newValue.put(Project.COLUMN_LOAD, project.getLoad());
    String whereClause = Project.COLUMN_ID + '=' + project.getId();
    // Update the item into the database
    writeDatabase.update(Project.PROJECT_TABLE, newValue, whereClause, null);
    closeDB();
  }

  public int deleteProject(int id) {
    deleteActivitiesForProjectId(id);
    openDB();
    String whereClause = Project.COLUMN_ID + '=' + id;
    // Return the total number of rows removed
    int numItemsDeleted = writeDatabase.delete(Project.PROJECT_TABLE, whereClause, null);
    closeDB();
    return numItemsDeleted;
  }

  private void deleteActivitiesForProjectId(int projectId) {
    ActivitiesDAO aDao = new ActivitiesDAO(context);
    ArrayList<Activities> activities_data = aDao.getAllActivitiesForProjectId(projectId);
    for (Activities a : activities_data) {
      aDao.deleteActivities(a.getId());
    }
  }
}

package com.shahzaib.moneybox.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shahzaib.moneybox.Add_Goal;
import com.shahzaib.moneybox.DataUtils.Goal;
import com.shahzaib.moneybox.database.DbContract;
import com.shahzaib.moneybox.DepositMoney;
import com.shahzaib.moneybox.GoalOverview;
import com.shahzaib.moneybox.R;

import java.io.File;

import alarm_utils.AlarmService;
import de.hdodenhof.circleimageview.CircleImageView;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;
    private int alarmID;
    String itemID;

    public GoalsAdapter(Context context) {
        this.context = context;
        this.alarmID = 0;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_list_single_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (cursor == null) return;
        // getting the data from the database and save into Goal object
        cursor.moveToPosition(position);


        Goal goal = new Goal(context);
        goal.setTitle(cursor.getString(cursor.getColumnIndex(DbContract.GOALS.COLUMN_TITLE)));
        goal.setTargetAmount(cursor.getLong(cursor.getColumnIndex(DbContract.GOALS.COLUMN_TARGET_AMOUNT)));
        goal.setTargetDateInMillis(cursor.getLong(cursor.getColumnIndex(DbContract.GOALS.COLUMN_TARGET_DATE)));
        goal.setPictureName(cursor.getString(cursor.getColumnIndex(DbContract.GOALS.COLUMN_PICTURE_NAME)));
        goal.setSavingFrequency(cursor.getString(cursor.getColumnIndex(DbContract.GOALS.COLUMN_SAVING_FREQUENCY)));
        goal.setReminder(cursor.getLong(cursor.getColumnIndex(DbContract.GOALS.COLUMN_REMINDER)));
        goal.setDepositedAmount(cursor.getLong(cursor.getColumnIndex(DbContract.GOALS.COLUMN_DEPOSITED_AMOUNT)));
        // setting tag will help us, when user click on goal for detailed overview
        holder.itemView.setTag(cursor.getString(cursor.getColumnIndex(DbContract.GOALS._ID)));
        alarmID = (int) cursor.getLong(cursor.getColumnIndex(DbContract.GOALS.COLUMN_ALARM_ID));
        itemID = cursor.getString(cursor.getColumnIndex(DbContract.GOALS._ID));

        //*************** binding data to the ui
        holder.titleTV.setText(goal.getTitle());
        holder.targetAmountTV.setText(Goal.separateNumberWithComma(goal.getTargetAmount()));
        holder.remainingDaysTV.setText(goal.getRemainingDays());
        holder.remainingAmountTV.setText(Goal.separateNumberWithComma(goal.getRemainingAmount()));
        holder.percentage_DepositedAmount_TV.setText(goal.getPercentageCompleted_With_DepositedAmount());
        holder.progressBar.setProgress(goal.getPercentageCompleted());
        if (goal.getPictureName() == null) {


            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .centerInside()
                    .override(250, 250))
                    .load(R.drawable.default_goal_image)
                    .into(holder.goalImageIV);

        }
        else
        {
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                    .centerInside()
                    .override(250, 250))
                    .load(goal.getPictureFileAddress()).into(holder.goalImageIV);
        }


        if (goal.getPercentageCompleted() < 100) {
            holder.ic_menu.setVisibility(View.VISIBLE);
            holder.ic_deposit_money.setVisibility(View.VISIBLE);
            holder.ic_delete.setVisibility(View.GONE);

        } else {
            holder.ic_menu.setVisibility(View.GONE);
            holder.ic_deposit_money.setVisibility(View.GONE);
            holder.ic_delete.setVisibility(View.VISIBLE);
        }


        //******************************************* On Click Listeners
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GoalOverview.class);
                intent.putExtra(GoalOverview.INTENT_KEY_ITEM_ID, (String) holder.itemView.getTag());
                context.startActivity(intent);
            }
        });
        holder.ic_deposit_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DepositMoney.class);
                intent.putExtra(DepositMoney.KEY_INTENT_ITEM_ID, (String) holder.itemView.getTag());
                context.startActivity(intent);
            }
        });
        holder.ic_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.ic_menu, position, (String) holder.itemView.getTag());
            }
        });

        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the picture name To delete the picture from the internal storage
                deletePictureFromInternalStorage(itemID);

                // delete the item
//                deleteReminder(itemID,alarmID); // reminder will not exist because, when item is completed they reminder is destroyed
                int itemDeleted = context.getContentResolver().delete(DbContract.GOALS.CONTENT_URI.buildUpon().appendPath(itemID).build(), null, null);
                Toast.makeText(context, "Goal Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public int getItemCount() {
        if (cursor != null) return cursor.getCount();
        else {
            Log.i("123456", "Cursor is Null");
            return 0;
        }
    }


    //****************** Helper methods
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    private void showPopupMenu(final View view, final int position, final String itemID) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_menu_item:
                        Intent editGoalIntent = new Intent(context, Add_Goal.class);
                        editGoalIntent.putExtra(Add_Goal.KEY_INTENT_ITEM_ID, itemID);
                        context.startActivity(editGoalIntent);
                        break;

                    case R.id.delete_menu_item:
                        new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage("Delete item ?")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletePictureFromInternalStorage(itemID);
                                        // delete the item
                                        deleteReminder(itemID, alarmID);
                                        cancelNotification(itemID); // if notification is active then cancel the notification
                                        int itemDeleted = context.getContentResolver().delete(DbContract.GOALS.CONTENT_URI.buildUpon().appendPath(itemID).build(), null, null);
                                        Toast.makeText(context, "Goal Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        break;
                }
                return true;
            }

        });
        popup.show();
    }

    private void deleteReminder(String itemID, int alarmID) {
        if (alarmID == 0) return;
        Intent alarmIntent = new Intent(context, AlarmService.class);
        alarmIntent.putExtra(AlarmService.KEY_ALARM_ID, alarmID);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                alarmID,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        ContentValues values = new ContentValues();
        values.put(DbContract.GOALS.COLUMN_REMINDER, 0);
        values.put(DbContract.GOALS.COLUMN_ALARM_ID, 0);
        context.getContentResolver().update(DbContract.GOALS.CONTENT_URI.buildUpon().appendPath(itemID).build(), values, null, null);
        SHOW_LOG("Reminder Deleted from the database ");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmPendingIntent);
        SHOW_LOG("Alarm Also Canceled");

    }

    private void SHOW_LOG(String message) {
        Log.i("123456", message);
    }

    private void cancelNotification(String itemID) {
        String selection = DbContract.NOTIFICATION_IDs._ID+"="+itemID;
        Cursor cursor = context.getContentResolver().query(DbContract.NOTIFICATION_IDs.CONTENT_URI,null,selection,null,null);
        if(cursor!=null && cursor.moveToFirst())
        {
            int notificationId = (int) cursor.getLong(cursor.getColumnIndex(DbContract.NOTIFICATION_IDs.COLUMN_NotificationID));
            if(notificationId != 0)
            {
                AlarmService.cancelNotification(context,notificationId);
            }
        }

    }
    private void deletePictureFromInternalStorage(String itemID) {
        SHOW_LOG("&&&&&&&&&&&&&******************** ItemID: "+itemID);
        Cursor cursor = context.getContentResolver().query(DbContract.GOALS.CONTENT_URI.buildUpon().appendPath(itemID).build(),
                null,null,null,null);
        if(cursor!=null) {
            cursor.moveToFirst();
            String goalPictureName = cursor.getString(cursor.getColumnIndex(DbContract.GOALS.COLUMN_PICTURE_NAME));
            if(goalPictureName!=null)
            {
                File folder = context.getFilesDir();
                File file = new File(folder, goalPictureName);
                boolean deleted = file.delete();
                SHOW_LOG(goalPictureName+" Deleted from the internal storage: "+deleted);
            }
        }
        else
        {
            SHOW_LOG("Cursor is Null for deleting image");
        }


    }








    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV, percentage_DepositedAmount_TV, targetAmountTV, remainingDaysTV, remainingAmountTV;
        CircleImageView goalImageIV;
        ProgressBar progressBar;
        ImageButton ic_menu, ic_deposit_money, ic_delete;

        ViewHolder(View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.titleTV);
            percentage_DepositedAmount_TV = itemView.findViewById(R.id.percentage_DepositedAmount_TV);
            targetAmountTV = itemView.findViewById(R.id.targetDateTV);
            remainingDaysTV = itemView.findViewById(R.id.remainingDaysTV);
            remainingAmountTV = itemView.findViewById(R.id.remainingAmountTV);
            goalImageIV = itemView.findViewById(R.id.goalImageIV);
            progressBar = itemView.findViewById(R.id.progressBar);
            ic_menu = itemView.findViewById(R.id.ic_menu);
            ic_deposit_money = itemView.findViewById(R.id.ic_deposit_money);
            ic_delete = itemView.findViewById(R.id.ic_delete);

        }
    }
}

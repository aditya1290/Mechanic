package com.example.mechanic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.mechanic.adapters.RequestCompletedAdapter;
import com.example.mechanic.fragments.RequestCompletedFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("ClassFirebaseMessaging", "Refreshed token: " + token);

        FirebaseMessaging.getInstance().subscribeToTopic("mechanic")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful()) {
                                Log.d("firebaseInstance","Can't register to mechanic");
                            }

                            Log.d("firebaseInstance", "Registered to mechanic");
                        }
                    });

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            Log.d("firebaseInstance","Can't register to all");
                        }

                        Log.d("firebaseInstance", "Registered to all");
                    }
                });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        Log.d("received message", String.valueOf(remoteMessage));
        String type;
        type = data.get("type").toString();
        if(type.equals("broadcast")){

            String subject,message;
            subject = data.get("subject").toString();
            message = data.get("message").toString();

            saveInDB(subject,message);

            Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 101, intent, 0);
            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();

                channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
                nm.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), "222")
                            .setContentTitle(subject)
                            .setAutoCancel(true)
                            //                        .setLargeIcon(((BitmapDrawable)getDrawable(R.drawable.lmis_logo)).getBitmap())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setSummaryText("Broadcast Message")
                                    .setBigContentTitle(subject)
                                    .bigText(message))

                            //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.electro))
                            .setContentText(message)
                            .setColor(Color.BLUE)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi);

            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            nm.notify(101, builder.build());
        }
        else if(type.equals("generateComplaintMessage")){
            String serviceType,description,instruction;
            serviceType = data.get("serviceType").toString();
            description = data.get("description").toString();
            instruction = data.get("instruction").toString();

            Intent intent = new Intent(getApplicationContext(), PendingComplaintsActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 101, intent, 0);

            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();

                channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
                nm.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), "222")
                            .setContentTitle(serviceType)
                            .setAutoCancel(true)
                            //                        .setLargeIcon(((BitmapDrawable)getDrawable(R.drawable.lmis_logo)).getBitmap())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setSummaryText("New Complaint Message")
                                    .setBigContentTitle(serviceType)
                                    .bigText(description))

                            //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.electro))
                            .setContentText(instruction)
                            .setColor(Color.BLUE)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi);

            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            nm.notify(101, builder.build());
        }
        else if(type.equals("requestApproved")){
            String rating,description;
            rating = data.get("rating").toString();
            description = data.get("description").toString();

            Intent intent = new Intent(getApplicationContext(), Requests.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 101, intent, 0);

            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();

                channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
                nm.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), "222")
                            .setContentTitle(description)
                            .setAutoCancel(true)
                            //.setLargeIcon(((BitmapDrawable)getDrawable(R.drawable.lmis_logo)).getBitmap())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setSummaryText("Request Approved")
                                    .setBigContentTitle(description)
                                    .bigText("Your rating is "+rating))

                            //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.electro))
                            //.setContentText(description)
                            //.setColor(Color.BLUE)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi);

            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            nm.notify(101, builder.build());
        }
        else if(type.equals("requestDeclined")){
            String description;
            description = data.get("description").toString();

            Intent intent = new Intent(getApplicationContext(), PendingComplaintsActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 101, intent, 0);

            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();

                channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
                nm.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), "222")
                            .setContentTitle(description)
                            .setAutoCancel(true)
                            //.setLargeIcon(((BitmapDrawable)getDrawable(R.drawable.lmis_logo)).getBitmap())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setSummaryText("Request Approved")
                                    //.setBigContentTitle(description)
                                    .bigText(description))

                            //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.electro))
                            //.setContentText(description)
                            //.setColor(Color.BLUE)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi);

            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            nm.notify(101, builder.build());
        }
    }
    public void saveInDB(String subject, String message)
    {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseHelper db = new DatabaseHelper(this, user.getUid());
        String type="1";
        Log.i("NCheck ", "Yaha aa gya");
        boolean isInserted = db.insertData(type, subject, message,user.getUid());

        Log.i("NCheck", "dekh hua kya");

    }
}

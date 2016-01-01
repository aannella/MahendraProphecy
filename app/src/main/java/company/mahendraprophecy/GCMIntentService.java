package company.mahendraprophecy;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import java.util.Random;

import br.com.goncalves.pugnotification.notification.PugNotification;
import company.mahendraprophecy.BOOKS.BOOK_DETAILS;
import company.mahendraprophecy.OTHERS.SPLASH;
import company.mahendraprophecy.WEBVIEW_FILES.OFFERS;

public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     */
    @Override
    protected void onRegistered(Context context, String registrationId)
    {
        Log.d(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        //Log.d("NAME", MainActivity.name);
        // ServerUtilities.register(context, MainActivity.name, MainActivity.email, registrationId);
    }

    /**
     * Method called on device un registred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
        Log.d(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        // ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     */
    @Override
    protected void onMessage(Context context, Intent intent)
    {
        Log.d(TAG, "Received message");
        String message = intent.getExtras().getString("message");

        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on receiving a deleted message
     */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.d(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
      /*  int icon = R.drawable.app_icon_final;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = null;

        if (message.startsWith("1")) {
            message = message.substring(1, message.length());
            notificationIntent = new Intent(context, SPLASH.class);
        }

        if (message.startsWith("2")) {
            message = message.substring(1, message.length());
            int count = 0;
            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == '|') {
                    count = i;
                    break;
                }
            }
            String id = message.substring(0, count);
            message = message.substring(count + 1, message.length());
            notificationIntent = new Intent(context, BOOK_DETAILS.class);
            notificationIntent.putExtra("id", id);
        }

        if (message.startsWith("3")) {
            message = message.substring(1, message.length());
            notificationIntent = new Intent(context, OFFERS.class);
        }


        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);
*/

        Random random = new Random();
        int m=random.nextInt();
        while(m<1)
        {
            m=random.nextInt();
        }

        Intent notificationIntent = null;
        if (message.startsWith("1")) {
            message = message.substring(1, message.length());
            notificationIntent = new Intent(context, SPLASH.class);

            PendingIntent intent = PendingIntent.getActivity(context, m, notificationIntent, 0);
            PugNotification.with(context)
                    .load()
                    .sound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .button(R.drawable.notify_open, "Open Application", intent)
                    .title(R.string.app_name)
                    .message(message)
                    .bigTextStyle(message)
                    .identifier(m)
                    .smallIcon(R.drawable.m_final)
                    .largeIcon(R.drawable.app_icon_final)
                    .simple()
                    .build();
        }

        if (message.startsWith("2")) {
            message = message.substring(1, message.length());
            int count = 0;
            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == '|') {
                    count = i;
                    break;
                }
            }
            String id = message.substring(0, count);
            message = message.substring(count + 1, message.length());
            notificationIntent = new Intent(context, BOOK_DETAILS.class);
            notificationIntent.putExtra("id", id);
            PendingIntent intent = PendingIntent.getActivity(context, m, notificationIntent, Intent.FILL_IN_CATEGORIES);
            PugNotification.with(context)
                    .load()
                    .button(R.drawable.notify_book, "View Book", intent)
                    .sound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .title(R.string.app_name)
                    .message(message)
                    .identifier(m)
                    .bigTextStyle(message)
                    .smallIcon(R.drawable.m_final)
                    .largeIcon(R.drawable.app_icon_final)
                    .simple()
                    .build();
        }

        if (message.startsWith("3")) {
            message = message.substring(1, message.length());
            notificationIntent = new Intent(context, OFFERS.class);

            PendingIntent intent = PendingIntent.getActivity(context, m, notificationIntent, 0);
            PugNotification.with(context)
                    .load()
                    .sound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .button(R.drawable.notify_offer, "View Offer", intent)
                    .title(R.string.app_name)
                    .message(message)
                    .identifier(m)
                    .bigTextStyle(message)
                    .smallIcon(R.drawable.m_final)
                    .largeIcon(R.drawable.app_icon_final)
                    .simple()
                    .build();
        }
    }



    // Google project id
    static final String SENDER_ID = "276760959039";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "apna_gcm";
    static final String DISPLAY_MESSAGE_ACTION = "technovators.mahendraprophecy.DISPLAY_MESSAGE";
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

}

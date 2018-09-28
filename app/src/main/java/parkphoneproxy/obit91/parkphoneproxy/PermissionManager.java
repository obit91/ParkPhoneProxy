package parkphoneproxy.obit91.parkphoneproxy;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class PermissionManager {

    public enum PERMISSIONS {
        REQUEST_PERMISSION_MAKE_CALL(1),
        REQUEST_READ_PHONE_STATE(2),
        REQUEST_PROCESS_OUTGOING_CALLS(3);

        private int value;

        PERMISSIONS(int value) {
            this.value = value;
        }

        public int getValue() { return value; }
    }

    private Activity activity;
    private PermissionResponseHandler permissionResponseHandlers;

    public <T extends AppCompatActivity & PermissionResponseHandler> PermissionManager(T activity) {
        this.activity = activity;
        this.permissionResponseHandlers = activity;
    }

    private static final String TAG = "PERMISSION_MANAGER";

    /**
     * Shows an explanation to the user, why are we asking for permissions?
     * @param title title
     * @param message message
     * @param permission which permission we're requesting
     * @param permissionRequestCode the unique permission request code.
     */
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    /**
     * Initiates a permission request popup.
     * @param permissionName name of the permission we desire.
     * @param permissionRequestCode unique permission code.
     */
    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permissionName}, permissionRequestCode);
    }

    /**
     * Requests permissions for requirements.
     * @param permissionToCheck which permission is requested.
     */
    public void showStatePermissions(PERMISSIONS permissionToCheck) {
        int permissionCheck;

        String permissionRequest = null;
        switch (permissionToCheck) {
            case REQUEST_PERMISSION_MAKE_CALL:
                permissionRequest = Manifest.permission.CALL_PHONE;
                break;
            case REQUEST_READ_PHONE_STATE:
                permissionRequest = Manifest.permission.READ_PHONE_STATE;
                break;
            case REQUEST_PROCESS_OUTGOING_CALLS:
                permissionRequest = Manifest.permission.PROCESS_OUTGOING_CALLS;
                break;
        }

        permissionCheck = ContextCompat.checkSelfPermission(activity, permissionRequest);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequest)) {
                showExplanation("Permission Needed", "This page can not be used without granting the permission.", permissionRequest, permissionToCheck.value);
            } else {
                requestPermission(permissionRequest, permissionToCheck.value);
            }
        } else {
            permissionResponseHandlers.permissionGranted(permissionToCheck.value);
        }
    }
}

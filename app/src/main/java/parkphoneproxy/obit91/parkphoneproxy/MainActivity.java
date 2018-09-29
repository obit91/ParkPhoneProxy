package parkphoneproxy.obit91.parkphoneproxy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PermissionResponseHandler, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static MainActivity mainActivity;

    PermissionResponseHandler permissionResponseHandler;

    Button callButton;
    Button editButton;
    Button saveButton;
    EditText textPhoneNum = null;

    CallReceiver callReceiver = null;

    private static void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMainActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setPermissionResponseHandler(this);

        final PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.showStatePermissions(PermissionManager.PERMISSIONS.REQUEST_READ_PHONE_STATE);
        permissionManager.showStatePermissions(PermissionManager.PERMISSIONS.REQUEST_PROCESS_OUTGOING_CALLS);
        permissionManager.showStatePermissions(PermissionManager.PERMISSIONS.REQUEST_PERMISSION_MAKE_CALL);

        callButton = findViewById(R.id.main_call_button);
        editButton = findViewById(R.id.main_edit_button);
        saveButton = findViewById(R.id.main_confirm_button);
        textPhoneNum = findViewById(R.id.main_phone_num);
        textPhoneNum.setClickable(false);

        callButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        enableEdit(true);
    }

    /**
     * Dials a phone number :)
     * @param number phone number to call.
     */
    private void dialNumber(String number) {
        String phoneNumber = String.format("tel: %s", number);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public void permissionGranted(int permissionCode) {
        int count = 0;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            count++;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED) {
            count++;
        }

        if (count == 2 && callReceiver == null) {
            callReceiver = new CallReceiver();
        }
    }

    @Override
    public void permissionDenied(int permissionCode) {

    }

    @Override
    public void setPermissionResponseHandler(PermissionResponseHandler permissionResponseHandler) {
        this.permissionResponseHandler = permissionResponseHandler;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            permissionResponseHandler.permissionGranted(requestCode);
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            permissionResponseHandler.permissionDenied(requestCode);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.main_call_button):
                initiateCall();
                break;
            case(R.id.main_edit_button):
                enableEdit(true);
                break;
            case(R.id.main_confirm_button):
                enableEdit(false);
                break;
        }
    }

    /**
     * Performs a call to the phone number inserted.
     */
    public void initiateCall() {
        if (textPhoneNum.isClickable()) {
            Toast.makeText(mainActivity, "A phone number must be set before performing a call.", Toast.LENGTH_SHORT).show();
            return;
        }
        dialNumber(textPhoneNum.getText().toString());
    }

    /**
     * Enables editing text phone number.
     * @param editable a boolean indicating if the phone number will be edited.
     */
    private void enableEdit(boolean editable) {

        if (editable) {
            callButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            callButton.setEnabled(false);
            editButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            editButton.setEnabled(false);
            saveButton.getBackground().setColorFilter(null);
            saveButton.setEnabled(true);
            textPhoneNum.setInputType(InputType.TYPE_CLASS_PHONE);
        } else {
            callButton.getBackground().setColorFilter(null);
            callButton.setEnabled(true);
            editButton.getBackground().setColorFilter(null);
            editButton.setEnabled(true);
            saveButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            saveButton.setEnabled(false);
            textPhoneNum.setInputType(InputType.TYPE_NULL);
        }
        textPhoneNum.setEnabled(editable);
    }
}

package parkphoneproxy.obit91.parkphoneproxy;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.Date;

public class CallReceiver extends PhoneCallReceiver {

    MainActivity delegate = MainActivity.getMainActivity();

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, String.format("Incoming call from: %s", number), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Outgoing started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Incoming ended", Toast.LENGTH_SHORT).show();
        delegate.dialNumber(number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Outgoing ended", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Missed call", Toast.LENGTH_SHORT).show();
        delegate.dialNumber(number);
    }
}

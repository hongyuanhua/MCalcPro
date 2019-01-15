package com.example.david.mcalcpro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.roumani.i2c.MPro;

public class MCalcPro_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener
{
    private TextToSpeech tts;
    SensorManager sm;
    MPro mp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcalcpro_layout);
        this.tts = new TextToSpeech(this, this);
        sm=(SensorManager)this.getSystemService(Context.SENSOR_SERVICE);

        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mp = new MPro();
    }

    public void onInit(int status)
    {
        this.tts.setLanguage(Locale.US);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax*ax + ay*ay + az*az);
        if (a > 20)
        {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1)
    {

    }

    public void buttonClicked(View v)
    {
        try{
            mp.setPrinciple(((EditText)findViewById(R.id.pBox)).getText().toString());
            mp.setAmortization(((EditText)findViewById(R.id.aBox)).getText().toString());
            mp.setInterest(((EditText)findViewById(R.id.iBox)).getText().toString());
            String s = "Monthly Payment = " + mp.computePayment("%,.2f");
            s += "\n\n";
            s += "By making this payments monthly for " + ((EditText)findViewById(R.id.aBox)).getText().toString() +
                    " years, the mortgage will be paid in full. But if you terminate the mortgage on its nth anniversary, " +
                    "the balance still owing depends on n as shown below:";
            s += "\n\n\n";
            for (int i=0;i<6;i++)
            {
                s += String.format("%8d",i)+mp.outstandingAfter(i,"%,16.0f");
                s += "\n\n";
            }
            for (int n=10; n<21;n+=5)
            {
                s += String.format("%8d",n)+mp.outstandingAfter(n,"%,16.0f");
                s += "\n\n";
            }

            ((TextView)findViewById(R.id.output)).setText(s);
            tts.speak(s, TextToSpeech.QUEUE_FLUSH,null);
        }
        catch (Exception e)
        {
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }
    }

}

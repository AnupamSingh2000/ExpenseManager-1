package com.example.expensetracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener {
    private PieChart pieChart;
    EditText expense, amount;
    TextView date,txtlocation;
    Button insert, update, delete, view,createpdf,getlocation;
    DBHelper DB;
    LocationManager locationManager;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtlocation=findViewById(R.id.location);
        getlocation=findViewById(R.id.getlocation);
        expense = findViewById(R.id.etexpense);
        amount = findViewById(R.id.etamount);
        date =findViewById(R.id.etdate);
        insert = findViewById(R.id.btnadd);
        update = findViewById(R.id.btnUpdate);
        delete = findViewById(R.id.btnDelete);
        createpdf=findViewById(R.id.createpdf);
        view = findViewById(R.id.btnshow);

        DB = new DBHelper(this);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setIcon(R.mipmap.expensetrackericon2);
        actionBar.setTitle(" ExpenseTracker");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }


        DatePickerDialog.OnDateSetListener datedialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        createpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Cursor res = DB.getdata();
                if(res.getCount()==0){
                    Toast.makeText(MainActivity.this, "No Expenses Exists", Toast.LENGTH_SHORT).show();
                    return;
                }



                PdfDocument document = new PdfDocument();
                // crate a page description
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
                // start a page
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);

                int y=50;
                while(res.moveToNext()) {
                    canvas.drawText("1.Name:", 30, y, paint);
                    canvas.drawText(res.getString(0), 100, y, paint);

                    y=y+20;
                    canvas.drawText("2.Amount:", 30, y, paint);
                    canvas.drawText(res.getString(1), 100, y, paint);

                    y=y+20;
                    canvas.drawText("3.Date:", 30, y, paint);
                    canvas.drawText(res.getString(2), 100, y, paint);

                    y=y+20;
                    canvas.drawText("4.Location:", 30, y, paint);
                    canvas.drawText(res.getString(3), 100, y, paint);

                    y=y+40;
                }

                document.finishPage(page);

                File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(),"/E-Bill.pdf");

                try{
                    document.writeTo(new FileOutputStream(file));
                    Toast.makeText(MainActivity.this,"Generated",Toast.LENGTH_SHORT).show();
                }catch(IOException e){
                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                // close the document
                document.close();
            }
        });

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationfun();
            }


        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, datedialog, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expensetxt = expense.getText().toString();
                String amounttxt = amount.getText().toString();
                String datetxt = date.getText().toString();
                String locationtxt=txtlocation.getText().toString();
                Boolean checkinsertdata = DB.insertuserdata(expensetxt, amounttxt, datetxt,locationtxt);
                if(checkinsertdata==true)
                    Toast.makeText(MainActivity.this, "New Expense Inserted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "New Expense Not Inserted", Toast.LENGTH_SHORT).show();
            }        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expensetxt = expense.getText().toString();
                String amounttxt = amount.getText().toString();
                String datetxt = date.getText().toString();
                String locationtxt=txtlocation.getText().toString();
                Boolean checkupdatedata = DB.updateuserdata(expensetxt, amounttxt, datetxt,locationtxt);
                if(checkupdatedata==true)
                    Toast.makeText(MainActivity.this, "Expense Updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "New Expense Not Updated", Toast.LENGTH_SHORT).show();
            }        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expensetxt = expense.getText().toString();
                Boolean checkudeletedata = DB.deletedata(expensetxt);
                if(checkudeletedata==true)
                    Toast.makeText(MainActivity.this, "Expense Deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Expense Not Deleted", Toast.LENGTH_SHORT).show();
            }        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.getdata();
                if(res.getCount()==0){
                    Toast.makeText(MainActivity.this, "No Expenses Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("Expensetype :"+res.getString(0)+"\n");
                    buffer.append("Amount :"+res.getString(1)+"\n");
                    buffer.append("Date:"+res.getString(2)+"\n");
                    buffer.append("Location:"+res.getString(3)+"\n\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Expenses List");
                builder.setMessage(buffer.toString());
                builder.show();
            }        });
    }

    @SuppressLint("MissingPermission")
    private void getLocationfun() {
        try {
            locationManager=(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MainActivity.this);
        }catch (Exception e)
        {

        }

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this,"("+location.getLatitude()+","+location.getLongitude()+")",Toast.LENGTH_SHORT).show();

        try{
            Geocoder geocoder=new Geocoder(MainActivity.this,Locale.getDefault());
            List<Address>addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address=addresses.get(0).getAddressLine(0);
            txtlocation.setText(address);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}

package com.hswie.educaremobile.carer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import com.hswie.educaremobile.R;
import com.hswie.educaremobile.api.dao.ResidentRDH;
import com.hswie.educaremobile.helper.FileHelper;
import com.hswie.educaremobile.helper.JsonHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.EasyImage;

public class AddResidentActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    private static final String TAG = "AddResidentActivity";

    private Calendar myCalendar = Calendar.getInstance();
    private EditText dateOfAdoption, birthDate, getPhoto, firstName, lastName, address, city, image;
    private Switch switchImage;
    private Button addResidentButton;

    private File file;

    private int currentEditDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resident);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();


    }

    private void initView() {
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        image = (EditText) findViewById(R.id.getPhoto);
        dateOfAdoption = (EditText) findViewById(R.id.date_of_adoption);
        birthDate = (EditText) findViewById(R.id.birth_date);
        getPhoto = (EditText) findViewById(R.id.getPhoto);

        switchImage = (Switch) findViewById(R.id.imageSwitch);
        switchImage.setTextOn(getText(R.string.switch_camera));
        switchImage.setTextOff(getText(R.string.switch_gallery));

        dateOfAdoption.setOnClickListener(setDateListener);
        birthDate.setOnClickListener(setDateListener);

        getPhoto.setOnClickListener(getPhotoListener);

        addResidentButton = (Button) findViewById(R.id.add_resident);
        addResidentButton.setOnClickListener(addResident);


    }


    private View.OnClickListener addResident= new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(file !=null) {

                ArrayList<String> params = new ArrayList<>();

                params.add(firstName.getText().toString());
                params.add(lastName.getText().toString());
                params.add(dateOfAdoption.getText().toString());
                params.add(birthDate.getText().toString());
                params.add(address.getText().toString());
                params.add(city.getText().toString());
                params.add(image.getText().toString());

                for (String param:params) {

                    Log.d(TAG, param);
                }


                new UploadPhotoToServer(AddResidentActivity.this).execute(file, params);
            }

        }
    };


    private View.OnClickListener getPhotoListener= new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (switchImage.isChecked())
            EasyImage.openCamera(AddResidentActivity.this);
            else
            EasyImage.openGalleryPicker(AddResidentActivity.this);
        }
    };


    private View.OnClickListener setDateListener= new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            new DatePickerDialog(AddResidentActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            currentEditDate = v.getId();
        }
    };



    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        if (currentEditDate == dateOfAdoption.getId())
        dateOfAdoption.setText(sdf.format(myCalendar.getTime()));

        if (currentEditDate == birthDate.getId())
            birthDate.setText(sdf.format(myCalendar.getTime()));
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                onPhotoReturned(imageFile.getName());
                file = imageFile;
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource) {

            }
        });
    }

    private void onPhotoReturned(String name) {

        getPhoto.setText(JsonHelper.HOSTNAME_RESIDENTIMAGE + name);

    }

    private class UploadPhotoToServer extends AsyncTask<Object, Void, Void>{

        private ProgressDialog dialog;

        public UploadPhotoToServer(AddResidentActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Upload Photo...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Object... params) {

           FileHelper fileHelper = new FileHelper();
            fileHelper.uploadFile((File) params[0]);
            ResidentRDH residentRDH = new ResidentRDH();


            residentRDH.addResident((ArrayList<String>)params[1]);
            return null;
        }


    }





}

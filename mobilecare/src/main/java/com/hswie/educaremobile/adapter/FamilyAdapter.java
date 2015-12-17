package com.hswie.educaremobile.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hswie.educaremobile.R;
import com.hswie.educaremobile.api.pojo.Family;
import com.hswie.educaremobile.helper.FamilyModel;
import com.hswie.educaremobile.helper.ResidentsModel;

import java.util.ArrayList;

/**
 * Residents ListView's adapter.
 */
public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.ViewHolder> {

    private static final String TAG = "FamilyAdapter";
    private boolean filterItems = false;
    private ArrayList<Family> filteredItems;
    private Context context;

    public FamilyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.family_list_item, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Family family;

        if (filterItems)
            family = filteredItems.get(position);
        else
            family = FamilyModel.get().getResidentFamily(ResidentsModel.get().getCurrentResident().getID()).get(position);


        viewHolder.fullnameView.setText(family.getFullName());


        viewHolder.callView.setBackgroundResource(R.drawable.ic_phone_black_48dp);
        viewHolder.callView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "clickCall");
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + family.getPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);


            }
        });

        viewHolder.callLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "clickCall");
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + family.getPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        try{

            return FamilyModel.get().getResidentFamily(ResidentsModel.get().getCurrentResident().getID()).size();
        } catch (NullPointerException e){

            return 0;
        }
    }


    public void resetItems(){
        filterItems = false;
        filteredItems = ResidentsModel.get().getCurrentResident().getFamilies();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullnameView;

        public Button callView;
        public LinearLayout callLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            fullnameView = (TextView) itemLayoutView.findViewById(R.id.text_fullname);
            callView = (Button) itemLayoutView.findViewById(R.id.button_call);
            callLayout = (LinearLayout) itemLayoutView.findViewById(R.id.layout_call);
        }
    }
}
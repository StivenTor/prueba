package intisolutions.com.co.pruebamerqueo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import intisolutions.com.co.pruebamerqueo.Model.Company;
import intisolutions.com.co.pruebamerqueo.Model.RestClient;
import intisolutions.com.co.pruebamerqueo.R;
import io.realm.RealmBaseAdapter;

/**
 * Created by STIVEN on 14/03/2018.
 */

public class AdapterExample extends RecyclerView.Adapter<AdapterExample.AdapterExampleViewHolder> {

    Context context;
    private List<Company> companyList = null;
    View view;
    LayoutInflater layoutInflater;

    public AdapterExample(Context context, List<Company> companies) {
        this.context = context;
        this.companyList = companies;
    }

    @Override
    public AdapterExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_row, parent, false);
        return new AdapterExampleViewHolder(v);
    }


    public class AdapterExampleViewHolder extends RecyclerView.ViewHolder  {

        private TextView tvName;
        private TextView tvId;
        private ImageView imageView;


        public AdapterExampleViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgView);
            tvName = (TextView) itemView.findViewById(R.id.nameTv);
            tvId = (TextView) itemView.findViewById(R.id.idTv);
        }
    }

    @Override
    public void onBindViewHolder(AdapterExampleViewHolder holder, int position) {
        Company companyItem = companyList.get(position);
        holder.tvName.setText(companyItem.getName());
        holder.tvId.setText(String.valueOf(companyItem.getId()));
        Glide.with(context)
                .load(companyList.get(position).getImgUrl())
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }
}
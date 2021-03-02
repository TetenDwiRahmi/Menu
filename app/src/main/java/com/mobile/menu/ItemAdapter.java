package com.mobile.menu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter <ItemAdapter.ItemViewRecHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_item, null);
        return new ItemViewRecHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewRecHolder holder, final int position) {
        final Item item = itemList.get(position);
        holder.txtNamaItem.setText(item.getNama_item());
        holder.txtNamaKategori.setText(item.getNama_kategori());
        holder.txtHargaItem.setText("Rp. "+ String.valueOf(item.getHarga_item()));
        holder.imageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,UpdateActivity.class);
                i.putExtra("id_item", item.getId_item());
                i.putExtra("id_kat",item.getId_kategori());//untuk spinner
                context.startActivity(i);
            }
        });
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusdata(item.getId_item());
                itemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, itemList.size());
                notifyDataSetChanged();
            }
        });
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("id_item", item.getId_item());
                context.startActivity(i);
            }
        });

        //untuk mengambil gambar di folder upload kemudian ditampilkan
        Picasso.get().load("http://192.168.100.142/ServerMenu/upload/"+item.getFoto_item())
                .resize(172,172).into(holder.image);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void hapusdata(int id_item) {
        String HAPUS_PESANAN= "http://192.168.100.142/ServerMenu/delete_item.php";
        AndroidNetworking.post(HAPUS_PESANAN)
                .addBodyParameter("id_item", id_item + "")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sukses", "code : "+response);
                            if (response.getString("status").equalsIgnoreCase("1")){
                                Toast.makeText(context, "Berhasil Menghapus Pesanan", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror","code : "+anError);
                        Toast.makeText(context, "Gagal Menghapus Pesanan", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class ItemViewRecHolder extends RecyclerView.ViewHolder {
        TextView txtNamaItem, txtHargaItem, txtNamaKategori;
        ImageView image, imagesetUpdate, imageUpdate, imageDelete;
        RecyclerView recyclerViewMain;
        CardView cardItem;

        public ItemViewRecHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            txtNamaItem = itemView.findViewById(R.id.txtNamaItem);
            txtHargaItem = itemView.findViewById(R.id.txtHargaItem);
            txtNamaKategori = itemView.findViewById(R.id.txtNamaKategori);
            image = itemView.findViewById(R.id.image);
            imagesetUpdate = itemView.findViewById(R.id.imageSetUpdate); //untuk menampilkan foto yg diklik
            imageUpdate = itemView.findViewById(R.id.imageUpdate);
            imageDelete = itemView.findViewById(R.id.imageDelete);
            recyclerViewMain = itemView.findViewById(R.id.recyclerViewMain);
            cardItem = itemView.findViewById(R.id.cardItem);
        }
    }
}

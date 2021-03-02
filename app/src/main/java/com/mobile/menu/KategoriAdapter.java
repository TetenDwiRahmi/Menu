package com.mobile.menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter <KategoriAdapter.ItemViewRecHolder> {
    private Context context;
    private List<Kategori> kategoriList;


    public KategoriAdapter(Context context, List<Kategori> kategoriList) {
        this.context = context;
        this.kategoriList = kategoriList;
    }

    @NonNull
    @Override
    public ItemViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_kategori, null);
        return new KategoriAdapter.ItemViewRecHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewRecHolder holder, int position) {
        final Kategori kategori = kategoriList.get(position);
        holder.txtKategoriGrid.setText(kategori.getNama_kategori());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,KategoriActivity.class);
                i.putExtra("id_kategori", kategori.getId_kategori());
                context.startActivity(i);
            }
        });
        Picasso.get().load("http://192.168.100.142/ServerMenu/upload/"+kategori.getFoto_kategori())
                .resize(172,172).into(holder.imageGrid);
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    class ItemViewRecHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtKategoriGrid, txtNamaKategori;
        ImageView imageGrid;

        public ItemViewRecHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            cardView = itemView.findViewById(R.id.cardView);
            imageGrid = itemView.findViewById(R.id.imageGrid);
            txtKategoriGrid = itemView.findViewById(R.id.txtKategoriGrid);
            txtNamaKategori =itemView.findViewById(R.id.txtNamaKategori);
        }
    }
}
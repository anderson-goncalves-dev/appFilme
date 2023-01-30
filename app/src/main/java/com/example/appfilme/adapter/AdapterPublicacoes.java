package com.example.appfilme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfilme.R;
import com.example.appfilme.model.Publicacao;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPublicacoes extends RecyclerView.Adapter<AdapterPublicacoes.MyViewHolder> {

    private List<Publicacao> publicacoes;
    private Context context;

    public AdapterPublicacoes(List<Publicacao> publicacoes, Context context) {
        this.publicacoes = publicacoes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_publicacao, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Publicacao publicacao = publicacoes.get(position);
        holder.titulo.setText(publicacao.getTitulo());

        List<String> urlFotos = publicacao.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return publicacoes.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView titulo;
        ImageView foto;
        public MyViewHolder(View itemView){
            super (itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            foto = itemView.findViewById(R.id.imageAnuncio);
        }
    }
}

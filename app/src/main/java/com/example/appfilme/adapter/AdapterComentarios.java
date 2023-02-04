package com.example.appfilme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfilme.R;
import com.example.appfilme.model.Comentario;

import java.util.List;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.MyViewHolder> {

    private List<Comentario> listaComentarios;
    private Context context;

    public AdapterComentarios(List<Comentario> listaComentarios, Context context) {
        this.listaComentarios = listaComentarios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentario, parent, false);
        return new AdapterComentarios.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        holder.nomeUsuario.setText(comentario.getNomeUsuario());
        holder.comentario.setText(comentario.getComentario());
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nomeUsuario, comentario;

        public MyViewHolder(View itemView){
            super(itemView);
            nomeUsuario = itemView.findViewById(R.id.textNomeComentario);
            comentario = itemView.findViewById(R.id.textComentario);
        }
    }
}

package com.example.appfilme.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appfilme.R;
import com.example.appfilme.model.Publicacao;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetalhesFilmeActivity extends AppCompatActivity {


    private TextView titulo;
    private TextView categoria;
    private TextView avaliacao;
    private Publicacao publicacaoSelecionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_filme);
        inicializarComponentes();
        publicacaoSelecionada = (Publicacao) getIntent().getSerializableExtra("publicacaoSelecionada");

        if(publicacaoSelecionada != null){

            titulo.setText(publicacaoSelecionada.getTitulo());
            avaliacao.setText(publicacaoSelecionada.getDescricao());
            categoria.setText(publicacaoSelecionada.getCategoria());




        }
    }
    private void inicializarComponentes(){

        titulo = findViewById(R.id.textTituloDetalhe);
        categoria = findViewById(R.id.textCategoriaDetalhe);
        avaliacao = findViewById(R.id.textAvaliacaoDetalhe);

    }
}
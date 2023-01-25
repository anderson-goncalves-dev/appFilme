package com.example.appfilme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfilme.R;
import com.example.appfilme.adapter.AdapterAnuncios;
import com.example.appfilme.databinding.ActivityMeusAnunciosBinding;
import com.example.appfilme.helper.ConfiguracaoFirebase;
import com.example.appfilme.helper.RecyclerItemClickListener;
import com.example.appfilme.model.Publicacao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {
    private RecyclerView recyclerAnuncios;
    private List<Publicacao> publicacoes = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference publicacoesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //Configurações iniciais
        publicacoesRef = ConfiguracaoFirebase.getFirebase().child("minhas_publicacoes")
                        .child(ConfiguracaoFirebase.getIdUsuario());

        inicializarComponentes();
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurar RecyclerView
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);

        adapterAnuncios = new AdapterAnuncios(publicacoes, this);

        recyclerAnuncios.setAdapter(adapterAnuncios);

        recuperarPublicacoes();
        recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Publicacao publicacaoSelecionado = publicacoes.get(position);
                                publicacaoSelecionado.remover();
                                adapterAnuncios.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );
    }
    private void recuperarPublicacoes(){
        LoadingAlert loadingAlert = new LoadingAlert(MeusAnunciosActivity.this);
        loadingAlert.startAlertDialog();
        publicacoesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                publicacoes.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    publicacoes.add(ds.getValue(Publicacao.class));
                }
                Collections.reverse(publicacoes);
                adapterAnuncios.notifyDataSetChanged();
                loadingAlert.closeAlertDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void inicializarComponentes(){
        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
    }



}
package com.example.appfilme.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.appfilme.R;
import com.example.appfilme.adapter.AdapterAnuncios;
import com.example.appfilme.helper.ConfiguracaoFirebase;
import com.example.appfilme.model.Publicacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnunciosActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private RecyclerView recyclerPublicacoesPublicas;
    private Button buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Publicacao> listaPublicacoes = new ArrayList<>();
    private DatabaseReference publicacoesPublicasRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();



        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        publicacoesPublicasRef = ConfiguracaoFirebase.getFirebase().child("publicacoes");
        //Configurar RecyclerView
        recyclerPublicacoesPublicas.setLayoutManager(new LinearLayoutManager(this));
        recyclerPublicacoesPublicas.setHasFixedSize(true);

        adapterAnuncios = new AdapterAnuncios(listaPublicacoes, this);

        recyclerPublicacoesPublicas.setAdapter(adapterAnuncios);
        recuperarPublicacoesPublicas();
        //autenticacao.signOut();
    }
    public void recuperarPublicacoesPublicas(){
        LoadingAlert loadingAlert = new LoadingAlert(AnunciosActivity.this);
        loadingAlert.startAlertDialog();
        listaPublicacoes.clear();
        publicacoesPublicasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot categorias: dataSnapshot.getChildren()){
                    for(DataSnapshot publicacoes: categorias.getChildren()){
                        Publicacao publicacao = publicacoes.getValue(Publicacao.class);
                        listaPublicacoes.add(publicacao);

                    }
                }
                Collections.reverse(listaPublicacoes);
                adapterAnuncios.notifyDataSetChanged();
                loadingAlert.closeAlertDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if( autenticacao.getCurrentUser()==null){//usuario deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }else{//usuario logado
            menu.setGroupVisible(R.id.group_logado,true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(),CadastroActivity.class));
                break;
            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;


        }

        return super.onOptionsItemSelected(item);
    }
    public void inicializarComponentes(){
        recyclerPublicacoesPublicas = findViewById(R.id.recyclerPublicacoesPublicas);
    }
}
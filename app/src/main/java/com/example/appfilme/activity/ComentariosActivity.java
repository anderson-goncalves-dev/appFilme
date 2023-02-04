package com.example.appfilme.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appfilme.R;
import com.example.appfilme.adapter.AdapterComentarios;
import com.example.appfilme.helper.ConfiguracaoFirebase;
import com.example.appfilme.helper.UsuarioFirebase;
import com.example.appfilme.model.Comentario;
import com.example.appfilme.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {
    private EditText editComentario;
    private String idPostagem;
    private RecyclerView recyclerComentarios;
    private AdapterComentarios adapterComentarios;
    private List<Comentario> listaComentarios = new ArrayList<>();
    private Usuario usuario;
    private DatabaseReference firebaseRef;
    private DatabaseReference comentariosRef;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        //inicializa componentes
        editComentario = findViewById(R.id.editComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);
        //configurações iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        //Configuração recyclerview
        adapterComentarios = new AdapterComentarios(listaComentarios, getApplicationContext());
        recyclerComentarios.setHasFixedSize(true);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter(adapterComentarios);
        //Recupera id da postagem
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }
    }
    private void recuperarComentarios(){
        comentariosRef = firebaseRef.child("Comentarios")
                .child(idPostagem);
        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaComentarios.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    listaComentarios.add(ds.getValue(Comentario.class));

                }
                adapterComentarios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener(valueEventListenerComentarios);
    }

    public void salvarComentario(View view){
        String textoComentario = editComentario.getText().toString();
        if (textoComentario != null && !textoComentario.equals("")){
            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getId());
            comentario.setNomeUsuario(usuario.getNome());
            comentario.setComentario(textoComentario);
            if (comentario.salvar()){
                Toast.makeText(this,"Comentário salvo com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"Insira um comentario antes de salvar", Toast.LENGTH_SHORT).show();
        }
        //Limpar comentario
        editComentario.setText("");
    }
}